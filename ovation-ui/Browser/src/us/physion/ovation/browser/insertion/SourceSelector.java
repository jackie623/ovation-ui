/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser.insertion;

import com.physion.ebuilder.ExpressionBuilder;
import com.physion.ebuilder.expression.ExpressionTree;
import java.awt.Color;
import java.awt.Component;
import java.util.Collection;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;
import ovation.IAuthenticatedDataStoreCoordinator;
import ovation.Source;
import us.physion.ovation.browser.BrowserUtilities;
import us.physion.ovation.browser.EntityWrapper;
import us.physion.ovation.browser.ResetQueryAction;
import us.physion.ovation.interfaces.ConnectionProvider;
import us.physion.ovation.interfaces.ExpressionTreeProvider;
import us.physion.ovation.interfaces.IEntityWrapper;


/**
 *
 * @author huecotanks
 */
public class SourceSelector extends javax.swing.JPanel {

    ChangeSupport cs;
    private IAuthenticatedDataStoreCoordinator dsc;
    private void resetSources() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Sources");
        for (Source s : dsc.getContext().getSources())
        {
            if (s.getParent() == null)
            {
                root.add(new DefaultMutableTreeNode(new EntityWrapper(s)));
            }
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)root.getFirstChild();
        while ((node =node.getNextNode()) != null) 
        {
           Source s = ((Source)((IEntityWrapper)node.getUserObject()).getEntity());
           Source[] children = s.getChildren();
           for (Source child: children)
           {
               node.add(new DefaultMutableTreeNode(new EntityWrapper(child)));
           }
        }
        ((DefaultTreeModel)sourcesTree.getModel()).setRoot(root);

    }

    private static class SourcesCellRenderer implements TreeCellRenderer{

        public SourcesCellRenderer() {
        }

        @Override
        public Component getTreeCellRendererComponent(JTree jtree, 
        Object o, 
        boolean selected, 
        boolean expanded, 
        boolean leaf, 
        int row, 
        boolean hasFocus) {
            JLabel l;
            Object value = ((DefaultMutableTreeNode) o).getUserObject();
            if (value instanceof String)
            {
                l = new JLabel((String)value);
            }else{
                l = new JLabel(((Source)((IEntityWrapper)value).getEntity()).getLabel());
            }
            
            if (selected)
            {
                l.setForeground(Color.GRAY);
                System.out.println("Set the background color");
            }
            return l;
        }
    }

    private IEntityWrapper selected;
    /**
     * Creates new form SourceSelector
     */
    public SourceSelector(ChangeSupport changeSupport, IEntityWrapper source) {
        initComponents();
        this.cs = changeSupport;
        //save Browser regisetered ems
        
        //init Browser = ExplorerUtils.createLookup(em, getActionMap());
        //sourcesTree.setUI(new PropertiesTreeUI(this));
        //addComponentListener(new RepaintOnResize(tree));
        sourcesTree.setCellRenderer(new SourcesCellRenderer());
        sourcesTree.setEditable(true);
        //sourcesTree.setRootVisible(false);
        sourcesTree.setShowsRootHandles(true);
        sourcesTree.setEditable(false);
        if (source != null)
        {
            selected = source;
            cs.fireChange();
        }
        sourcesTree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent tse) {
                TreePath path = tse.getPath();
                DefaultMutableTreeNode n = (DefaultMutableTreeNode)path.getLastPathComponent();
                if (n.getUserObject() instanceof IEntityWrapper)
                {
                    selected = (IEntityWrapper)n.getUserObject();
                    System.out.println("Selected");
                    cs.fireChange();
                }else{
                    selected = null;
                    System.out.println("Not selected");
                    cs.fireChange();
                }
            }
        });
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Sources");
        dsc = Lookup.getDefault().lookup(ConnectionProvider.class).getConnection();
        resetSources();
        
    }

    public IEntityWrapper getSource()
    {
        return selected;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        runQueryButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        sourcesTree = new javax.swing.JTree();

        runQueryButton.setText(org.openide.util.NbBundle.getMessage(SourceSelector.class, "SourceSelector.runQueryButton.text")); // NOI18N
        runQueryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runQueryButtonActionPerformed(evt);
            }
        });

        resetButton.setText(org.openide.util.NbBundle.getMessage(SourceSelector.class, "SourceSelector.resetButton.text")); // NOI18N
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(sourcesTree);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 409, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(layout.createSequentialGroup()
                .add(resetButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(runQueryButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(resetButton)
                    .add(runQueryButton)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        resetSources();
    }//GEN-LAST:event_resetButtonActionPerformed

    private void runQueryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runQueryButtonActionPerformed
        final ExpressionTreeProvider etp = Lookup.getDefault().lookup(ExpressionTreeProvider.class);
        ExpressionTree et = etp.getExpressionTree();
        
        final ExpressionTree result = ExpressionBuilder.editExpression(et).expressionTree;
        //run query, and reset
    }//GEN-LAST:event_runQueryButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton runQueryButton;
    private javax.swing.JTree sourcesTree;
    // End of variables declaration//GEN-END:variables

}
