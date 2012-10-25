/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author huecotanks
 */
public class ExpandableJTree extends JTree{
    
    public ExpandableJTree(DefaultMutableTreeNode root)
    {
        super(root);
    }
    public void expand(DefaultMutableTreeNode node)
    {
        setExpandedState(new TreePath(node.getPath()), true);
    }
}
