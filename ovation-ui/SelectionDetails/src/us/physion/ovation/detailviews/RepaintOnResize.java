/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 *
 * @author huecotanks
 */
class RepaintOnResize implements ComponentListener {
    JTree tree;
    RepaintOnResize(JTree c)
    {
        super();
        tree = c;
    }

    @Override
    public void componentResized(ComponentEvent ce) {
        if (tree.isEditing())
            return;
        DefaultMutableTreeNode root = ((DefaultMutableTreeNode)tree.getModel().getRoot());
        DefaultMutableTreeNode leaf = root.getFirstLeaf();
        for (int i =0; i< root.getLeafCount(); i++)
        {
            ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(leaf);
            leaf = leaf.getNextLeaf();
        }
    }

    @Override
    public void componentMoved(ComponentEvent ce) {}

    @Override
    public void componentShown(ComponentEvent ce) {}

    @Override
    public void componentHidden(ComponentEvent ce) {}
    
}
