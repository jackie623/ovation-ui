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
    Set<String> uris;
    private Map<String, DefaultMutableTreeNode> userNodes;
    
    JTree getTree()
    {
        return tree;
    }

    public void setEntities(Collection<? extends IEntityWrapper> entities, DataContext c) {
        if (c == null)
        {
            c = Lookup.getDefault().lookup(ConnectionProvider.class).getConnection().getContext();
        }
        
        ArrayList<UserPropertySet> properties = new ArrayList<UserPropertySet>();
        uris.clear();
        Set<IEntityBase> entitybases = new HashSet();
        Set<String> owners = new HashSet();
        for (IEntityWrapper w : entities) {
                IEntityBase e = w.getEntity();
                entitybases.add(e);
                uris.add(e.getURIString());
                owners.add(e.getOwner().getUuid());
        }
        Set<DefaultMutableTreeNode> nodesToExpand = new HashSet();
        Iterator<User> users = c.query(User.class, "true");//c.getUsersIterator();
        while (users.hasNext()) {
            User u = users.next();
            Map<String, Object> userProps = new HashMap();
            for (IEntityBase e : entitybases) {
                userProps.putAll(e.getUserProperties(u));
            }
            if (!userProps.isEmpty()) {
                String uuid = u.getUuid();
                UserPropertySet propertySet = new UserPropertySet(u, owners.contains(uuid), c.currentAuthenticatedUser().getUuid().equals(uuid), userProps, uris);
                properties.add(propertySet);
            }
          }
        
        final ArrayList<UserPropertySet> propertySets = properties;
        EventQueueUtilities.runOnEDT(new Runnable(){

            @Override
            public void run() {
                final DefaultMutableTreeNode root = new DefaultMutableTreeNode("");
                Set<DefaultMutableTreeNode> nodesToExpand = new HashSet<DefaultMutableTreeNode>();
                
                Collections.sort(propertySets);
                for (UserPropertySet propertySet : propertySets)
                {
                    boolean shouldExpand = shouldExpand(propertySet);
                    DefaultMutableTreeNode userNode = getNode(propertySet);

                    if (shouldExpand) {
                        nodesToExpand.add(userNode);
                    }
                    TableNode n = new TableNode(propertySet);
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
        tree.setUI(new PropertiesTreeUI(this));
        addComponentListener(new RepaintOnResize(tree));
        tree.setCellRenderer(r);
        tree.setRowHeight(0);
        tree.setEditable(true);
        tree.setCellEditor(r);
        uris = new HashSet<String>();
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
                if (parent == null)
                {
                    tree.setCursor(Cursor.getDefaultCursor());
                    return;
                }
                if (parent.getParentPath() == null)
                {
                    //if path is a user node
                    tree.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    return;
                }
                
                tree.setCursor(Cursor.getDefaultCursor());
            }
        });
        getViewport().add(tree, null);
        userNodes = new HashMap<String, DefaultMutableTreeNode>();
    }
    
    private boolean shouldExpand(UserPropertySet propertySet)
    {
        if (userNodes.containsKey(propertySet.getURI()))
        {
            DefaultMutableTreeNode n = userNodes.get(propertySet.getURI());
            return (!tree.isCollapsed(new TreePath(n.getPath())));
        }
        return false;
    }

    private DefaultMutableTreeNode getNode(UserPropertySet propertySet) {
        if (userNodes.containsKey(propertySet.getURI()))
        {
            DefaultMutableTreeNode n = userNodes.get(propertySet.getURI());
            n.removeAllChildren();
            n.removeFromParent();
            return n;
        }
        
        DefaultMutableTreeNode n = new DefaultMutableTreeNode(propertySet.getDisplayName());
        userNodes.put(propertySet.getURI(), n);
        return n;
    }

    class TableInTreeCellRenderer extends AbstractCellEditor implements TreeCellEditor, TreeCellRenderer {

        boolean trueFalse = true;
        
        boolean isCurrentUser;
        Map<String, TablePanel> tableLookup;
        
        public TableInTreeCellRenderer()
        {
            super();
            tableLookup = new HashMap<String, TablePanel>();
                    
            //this.addCellEditorListener(new PropertyTableModelListener(new HashSet<String>()));
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
            if (o instanceof UserPropertySet)
            {   
                JPanel panel = getPanelFromPropertySet((UserPropertySet)o, (TableNode)value, Lookup.getDefault().lookup(ConnectionProvider.class).getConnection());
                return panel;
            }
            return null;
        }
        
       JPanel getPanelFromPropertySet(UserPropertySet p, TableNode node, IAuthenticatedDataStoreCoordinator dsc)
       {
           String user = p.getURI();
                //lookup tables
                TablePanel panel;
                if (tableLookup.containsKey(user))
                {
                    panel = tableLookup.get(user);
                }else{
                    JTable table = new JTable();
                    table.setGridColor(new Color(211, 211, 211, 180));
                    table.setBorder(new CompoundBorder(new EmptyBorder(new Insets(1,4,1,4)), table.getBorder()));
                    
                    table.setBorder(BorderFactory.createEmptyBorder());
                    table.getTableHeader().setBackground(Color.WHITE);
                    //table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
                    //table.setRowSorter(new TableRowSorter());
                    //table.getTableHeader().setVisible(false);
                    //table.getTableHeader().setPreferredSize(new Dimension(-1, 0));
                    if (p.isCurrentUser())
                    {
                        panel = new EditableTable(table, ((PropertiesTreeUI)tree.getUI()));
                    }else{
                        panel = new NonEditableTable(table, ((PropertiesTreeUI)tree.getUI()));
                        
                    }
                   
                    tableLookup.put(user, panel);
                }

                Map<String, Object> props = p.getProperties();
                Object[][] dataVector = new Object[props.size()][2];
                int i=0;
                for (Map.Entry<String, Object> entry : props.entrySet()) {
                    dataVector[i][0] = entry.getKey();
                    dataVector[i][1] = entry.getValue();
                    i++;
                }
                String[] columnNames = {"Name", "Value"};
                DefaultTableModel tableModel = new DefaultTableModel(columnNames, props.size());
                tableModel.setDataVector(dataVector, columnNames);

                JTable table = panel.getTable();
                table.setModel(tableModel);
                //((TableRowSorter)table.getRowSorter()).setModel(tableModel);
                table.setPreferredScrollableViewportSize(panel.getPanel().getPreferredSize());
                
                if (p.isCurrentUser()) {
                    tableModel.addTableModelListener(new PropertyTableModelListener(uris, tree, node, dsc));
                }
                
                return panel.getPanel();
       }
       
       TableModel getTableModel(UserPropertySet s)
       {
           String userURI = s.getURI();
           if (tableLookup.containsKey(userURI))
           {
               return tableLookup.get(userURI).getTable().getModel();
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
            if (event instanceof MouseEvent)
            {
                TreePath p = tree.getPathForLocation(((MouseEvent)event).getX(), ((MouseEvent)event).getY());
                node = p.getLastPathComponent();
            }
            else{
                node = tree.getLastSelectedPathComponent();
            }
            if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
                
                Object p = treeNode.getUserObject();
                if (p instanceof UserPropertySet)
                {
                    return ((UserPropertySet)p).isCurrentUser();
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
            if (o instanceof UserPropertySet)
            {
                JPanel panel = tableLookup.get(((UserPropertySet)o).getURI()).getPanel();
                return panel;
            }
            else{
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
}