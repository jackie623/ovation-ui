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

    public void setEntities(Collection<? extends IEntityWrapper> entities)
    {
        DataContext c = Lookup.getDefault().lookup(ConnectionProvider.class).getConnection().getContext();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Properties");
       
        Iterator<User> users = c.query(User.class, "true");//getUsersIterator();
        while (users.hasNext()) {
            User u = users.next();
            Map<String, Object> userProps = new HashMap();
            for (IEntityWrapper w : entities) {
                IEntityBase e = w.getEntity();
                userProps.putAll(e.getUserProperties(u));
            }
            if (!userProps.isEmpty()) {
                DefaultMutableTreeNode userNode = new DefaultMutableTreeNode(u);
                userNode.add(new DefaultMutableTreeNode(userProps));
                root.add(userNode);
            }
        }

        ((DefaultTreeModel)tree.getModel()).setRoot(root);
    }
    
    
    public TreeWithTableRenderer() {
        super();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Properties");
        tree = new JTree(root);
        tree.setCellRenderer(new TableInTreeCellRenderer());
        tree.setRowHeight(0);
        getViewport().add(tree, null);
    }
    class TableInTreeCellRenderer implements TreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) 
        {
            final Object o = ((DefaultMutableTreeNode) value).getUserObject();
            if (o instanceof String)
            {
                return new JLabel((String) o);
            }
            if (o instanceof User)
            {
                return new JLabel(((User)o).getUsername());
            }

            // otherwise we expect it to be a Map<String, Object>
            JTable table = new JTable();

            String[] columnNames = {"property", "value"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            Map<String,Object> tableMap = (Map)o;
            for (Map.Entry<String, Object> entry : tableMap.entrySet()) {
                tableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
            }

            table.setModel(tableModel);
            table.setPreferredScrollableViewportSize(table.getPreferredSize());

            JPanel panel = new JPanel(new BorderLayout());
            JScrollPane scrollPane = new JScrollPane(table);
            panel.add(scrollPane);
            return panel;
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