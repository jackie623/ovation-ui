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

public class TreeWithTableRenderer2 extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTree tree;

    public TreeWithTableRenderer2() {
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Properties");
        
        Map<String, String> props = new HashMap();
        props.put("foo", "bar");
        props.put("baz", "bing");
        
        DefaultMutableTreeNode nodeToAdd = new DefaultMutableTreeNode("node 1");
        nodeToAdd.add(new DefaultMutableTreeNode(props));
        root.add(nodeToAdd);
        
        props = new HashMap();
        props.put("foo", "another");
        props.put("baz", "thing");
        
        nodeToAdd = new DefaultMutableTreeNode("node 2");
        nodeToAdd.add(new DefaultMutableTreeNode(props));
        root.add(nodeToAdd);
        
        tree = new JTree(root);
        tree.setCellRenderer(new MyTableInTreeCellRenderer());
        tree.setRowHeight(0);
        JScrollPane jsp = new JScrollPane(tree);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(jsp, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    class MyTableInTreeCellRenderer implements TreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) 
        {
            final Object o = ((DefaultMutableTreeNode) value).getUserObject();
            if (o instanceof String)
            {
                return new JLabel((String) o);
            }

            // otherwise we expect it to be a Map<String, String>
            JTable table = new JTable();
            //JScrollPane scrollPane = new JScrollPane(table);
            //add(scrollPane);

            String[] columnNames = {"property", "value"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            Map<String,String> tableMap = (Map)o;
            for (Map.Entry<String, String> entry : tableMap.entrySet()) {
                tableModel.addRow(new String[]{entry.getKey(), entry.getValue()});
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
                new TreeWithTableRenderer2().setVisible(true);
            }
        });
    }
}