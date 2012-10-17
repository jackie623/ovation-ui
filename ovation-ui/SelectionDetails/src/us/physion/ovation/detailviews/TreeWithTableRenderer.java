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
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;
import org.openide.util.Lookup;
import ovation.DataContext;
import ovation.IEntityBase;
import ovation.LogLevel;
import ovation.Ovation;
import ovation.User;
import us.physion.ovation.interfaces.ConnectionProvider;
import us.physion.ovation.interfaces.IEntityWrapper;

public class TreeWithTableRenderer extends JScrollPane {

    private static final long serialVersionUID = 1L;
    private JTree tree;
    Set<String> uris;

    public void setEntities(Collection<? extends IEntityWrapper> entities) {
        DataContext c = Lookup.getDefault().lookup(ConnectionProvider.class).getConnection().getContext();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("");

        uris.clear();
        Set<IEntityBase> entitybases = new HashSet();
        Set<String> owners = new HashSet();
        for (IEntityWrapper w : entities) {
                IEntityBase e = w.getEntity();
                entitybases.add(e);
                uris.add(e.getURIString());
                owners.add(e.getOwner().getUuid());
        }
        Iterator<User> users = c.getUsersIterator();
        while (users.hasNext()) {
            User u = users.next();
            Map<String, Object> userProps = new HashMap();
            for (IEntityBase e : entitybases) {
                userProps.putAll(e.getUserProperties(u));
            }
            if (!userProps.isEmpty()) {
                String uuid = u.getUuid();
                UserPropertySet propertySet = new UserPropertySet(u.getUsername(), uuid, owners.contains(uuid), uuid.equals(c.currentAuthenticatedUser().getUuid()), userProps, this);
                DefaultMutableTreeNode userNode = new DefaultMutableTreeNode(propertySet.getDisplayName());
                userNode.add(new DefaultMutableTreeNode(propertySet));

                root.add(userNode);
            }
        }

        ((DefaultTreeModel) tree.getModel()).setRoot(root);
    }

    public TreeWithTableRenderer() {
        super();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("");
        tree = new JTree(root);
        tree.setCellRenderer(new TableInTreeCellRenderer());
        tree.setRowHeight(0);
        tree.setEditable(true);
        tree.setCellEditor(new LeafRenderer());
        
        uris = new HashSet<String>();
        //tree.setRootVisible(false);
        getViewport().add(tree, null);
        
    }

    class TableInTreeCellRenderer implements TreeCellRenderer {

        boolean isCurrentUser;
        
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            final Object o = ((DefaultMutableTreeNode) value).getUserObject();
            if (o instanceof String) {
                return new JLabel((String) o);
            }

                // otherwise we expect it to be UserPropertySet object
                //table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            if (o instanceof UserPropertySet)
            {

                String[] columnNames = {"property", "value"};
                DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
               

                Map<String, Object> tableMap = ((UserPropertySet) o).getProperties();
                for (Map.Entry<String, Object> entry : tableMap.entrySet()) {
                    tableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
                }
                JTable table = new JTable();

                table.setModel(tableModel);
                table.setPreferredScrollableViewportSize(table.getPreferredSize());

                //panel.setSize(new Dimension(500, 500));
                //table.setSize(panel.getSize());
                
                if (((UserPropertySet) o).isCurrentUser()) {
                    //add buttons
                    //register listener
                    tableModel.addTableModelListener(new PropertyTableModelListener(Lookup.getDefault().lookup(ConnectionProvider.class).getConnection(), uris));
                    return new EditableTable(table);
                }
                JPanel panel = new JPanel(new BorderLayout());
                JScrollPane scrollPane = new JScrollPane(table);
                panel.add(scrollPane);
                return panel;

            }
            return null;
        }
    }

    class LeafRenderer extends AbstractCellEditor implements
            TreeCellEditor {

        TableInTreeCellRenderer renderer = new TableInTreeCellRenderer();

        @Override
        public Object getCellEditorValue() {//default value, not used
            return 5;
        }

        @Override
        public boolean isCellEditable(final EventObject event) {
            Object node = tree.getLastSelectedPathComponent();
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
            Component editor = renderer.getTreeCellRendererComponent(tree,
                    value, true, expanded, leaf, row, true);
            return editor;
        }

        public void itemStateChanged(ItemEvent itemEvent) {
            if (stopCellEditing()) {
                System.out.println("Stopped editing cell");
                fireEditingStopped();
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