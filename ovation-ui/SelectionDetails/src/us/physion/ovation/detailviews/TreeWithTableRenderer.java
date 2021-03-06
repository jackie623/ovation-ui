/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

/**
 *
 * @author jackie
 */
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.*;
import org.openide.util.Lookup;
import ovation.*;
import us.physion.ovation.interfaces.ConnectionProvider;
import us.physion.ovation.interfaces.EventQueueUtilities;
import us.physion.ovation.interfaces.IEntityWrapper;

public class TreeWithTableRenderer extends JScrollPane {

    private ExpandableJTree tree;
    private Map<String, DefaultMutableTreeNode> userNodes;

    JTree getTree() {
        return tree;
    }

    public void setKeys(final ArrayList<? extends TableTreeKey> keys) {
        
        EventQueueUtilities.runOnEDT(new Runnable() {

            @Override
            public void run() {
                final DefaultMutableTreeNode root = new DefaultMutableTreeNode("");
                Set<DefaultMutableTreeNode> nodesToExpand = new HashSet<DefaultMutableTreeNode>();

                for (TableTreeKey tableInfo : keys) {
                    boolean shouldExpand = shouldExpand(tableInfo);
                    DefaultMutableTreeNode userNode = new DefaultMutableTreeNode(tableInfo.getDisplayName());
                    userNodes.put(tableInfo.getID(), userNode);

                    if (shouldExpand) {
                        nodesToExpand.add(userNode);
                    }
                    TableNode n = new TableNode(tableInfo);
                    userNode.add(n);

                    root.add(userNode);
                }
                // clear any selection first -- this prevents a null pointer exception
                // if you click on a different entity while editing this one.
                tree.setSelectionPath(null);

                //((DefaultMutableTreeNode)((DefaultTreeModel) tree.getModel()).getRoot()).removeAllChildren();
                ((DefaultTreeModel) tree.getModel()).setRoot(root);
                for (DefaultMutableTreeNode node : nodesToExpand) {
                    tree.expand(node);
                }
            }
        });
    }

    public TreeWithTableRenderer() {
        super();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("");
        tree = new ExpandableJTree(root);
        TableInTreeCellRenderer r = new TableInTreeCellRenderer();
        tree.setUI(new PropertiesTreeUI(this.getViewport()));
        addComponentListener(new RepaintOnResize(tree));
        tree.setCellRenderer(r);
        tree.setRowHeight(0);
        tree.setEditable(true);
        tree.setCellEditor(r);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                int x = (int) e.getPoint().getX();
                int y = (int) e.getPoint().getY();
                TreePath path = tree.getPathForLocation(x, y);
                if (path == null) {
                    tree.setCursor(Cursor.getDefaultCursor());
                    return;
                }
                TreePath parent = path.getParentPath();
                if (parent == null) {
                    tree.setCursor(Cursor.getDefaultCursor());
                    return;
                }
                if (parent.getParentPath() == null) {
                    //if path is a user node
                    tree.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    return;
                }

                tree.setCursor(Cursor.getDefaultCursor());
            }
        });
        this.setViewportView(tree);

        userNodes = new HashMap<String, DefaultMutableTreeNode>();
    }

    public Dimension getPreferredSize() {
        return getParent().getPreferredSize();
    }

    private boolean shouldExpand(TableTreeKey tableInfo) {
        if (userNodes.containsKey(tableInfo.getID())) {
            DefaultMutableTreeNode n = userNodes.get(tableInfo.getID());
            return (!tree.isCollapsed(new TreePath(n.getPath())));
        } 
        return tableInfo.isExpandedByDefault();
    }

    class TableInTreeCellRenderer extends AbstractCellEditor implements TreeCellEditor, TreeCellRenderer {

        boolean isCurrentUser;
        Map<String, TablePanel> tableLookup;

        public TableInTreeCellRenderer() {
            super();
            tableLookup = new HashMap<String, TablePanel>();
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            final Object o = ((DefaultMutableTreeNode) value).getUserObject();
            if (o instanceof String) {
                //Component r = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
                UserPropertyLabel l = new UserPropertyLabel((String) o);
                return l;
            }

            // otherwise we expect it to be UserPropertySet object
            //table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            if (value instanceof TableNode)
            {
                TableModelListener l = null;
                if (o instanceof UserPropertySet && ((UserPropertySet)o).isEditable())
                    l = new PropertyTableModelListener(((UserPropertySet)o).uris, tree, (TableNode)value, Lookup.getDefault().lookup(ConnectionProvider.class).getConnection());
                
                return PanelFactory.getPanel(TreeWithTableRenderer.this, this, ((TableNode)value), l);
            }
            
            return null;
        }

        TableModel getTableModel(TableTreeKey s) {
            String id = s.getID();
            if (tableLookup.containsKey(id)) {
                return tableLookup.get(id).getTable().getModel();
            }
            return null;
        }

        @Override
        public Object getCellEditorValue() {
            return null;//currentPanel
        }

        @Override
        public boolean isCellEditable(final EventObject event) {

            Object node;
            if (event instanceof MouseEvent) {
                TreePath p = tree.getPathForLocation(((MouseEvent) event).getX(), ((MouseEvent) event).getY());
                node = p.getLastPathComponent();
            } else {
                node = tree.getLastSelectedPathComponent();
            }
            if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;

                Object p = treeNode.getUserObject();
                if (p instanceof TableTreeKey) {
                    return ((TableTreeKey) p).isEditable();
                }
            }
            return false;
        }

        @Override
        public Component getTreeCellEditorComponent(
                final JTree tree,
                final Object value,
                final boolean isSelected,
                final boolean expanded,
                final boolean leaf,
                final int row) {
            Object o = ((DefaultMutableTreeNode) value).getUserObject();
            if (o instanceof UserPropertySet) {
                JPanel panel = tableLookup.get(((UserPropertySet) o).getID()).getPanel();
                return panel;
            } else {
                Component editor = getTreeCellRendererComponent(tree,
                        value, true, expanded, leaf, row, true);
                return editor;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                //new TreeWithTableRenderer().setVisible(true);
            }
        });
    }

    public int getCellWidth() {
        return ((PropertiesTreeUI) tree.getUI()).getCellWidth();
    }
}