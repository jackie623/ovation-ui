/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physionconsulting.ovation.browser;

import java.beans.PropertyVetoException;
import java.util.*;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import ovation.*;

/**
 *
 * @author huecotanks
 */
public class EntityWrapperUtilities {

    private static String SEPARATOR = ";";
    
    protected static void createNodesFromQuery(ExplorerManager mgr, Iterator<IEntityBase> itr)
    {
        HashSet<Node> selectedNodes = new HashSet();
        Map<String, Node> treeMap = new HashMap<String, Node>();
        while (itr.hasNext()) {
            IEntityBase e = itr.next();
            String key = e.getURIString();
            String pathToExistingAncestor = getParentInTree(e, treeMap, e.getURIString());
            String[] uris = pathToExistingAncestor.split(SEPARATOR);
         
            Node parentInTree = treeMap.get(uris[0]);
            if (parentInTree == null) {
                parentInTree = mgr.getRootContext();
            } else {
                uris = Arrays.copyOfRange(uris, 1, uris.length);
            }

            for (String uri : uris) {
                //ArrayList l = parentInTree.getChildren().Keys.getKeys();
                Node n = EntityWrapperUtilities.createNode(new EntityWrapper(e), treeMap, Children.LEAF);
                parentInTree.getChildren().add(new Node[]{n});
                Children ch = parentInTree.getChildren();
                parentInTree = n;
            }
        }
    }
            
    protected static Set<Node> existingNodesFromQuery(Map<String, Node> treeMap, Iterator<IEntityBase> itr, ExplorerManager mgr) {
        HashSet<Node> selectedNodes = new HashSet();
        while (itr.hasNext()) {
            IEntityBase e = itr.next();
            String key = e.getURIString();
            Node n;
            if (!treeMap.containsKey(key)) {
                String pathToOpenParent = getParentInTree(e, treeMap, e.getURIString());
                String[] uris = pathToOpenParent.split(SEPARATOR);

                Node parentInTree = treeMap.get(uris[0]);
                if (parentInTree == null) {
                    parentInTree = mgr.getRootContext();
                } else {
                    uris = Arrays.copyOfRange(uris, 1, uris.length);
                }

                for (String uri : uris) {
                    Node[] nodes = parentInTree.getChildren().getNodes(true);
                    for (Node node : nodes) {
                        EntityWrapper ew = (EntityWrapper) node.getLookup().lookup(EntityWrapper.class);
                        if (ew.getURI().equals(uri)) {
                            parentInTree = node;
                            break;
                        }
                    }
                }
                selectedNodes.add(parentInTree);
            } else {
                n = treeMap.get(key);
                selectedNodes.add(n);
            }
        }
        return selectedNodes;
    }

    //expand each path from root to the given set of nodes in the tree view
    protected static void expandNodes(Set<Node> nodes, BeanTreeView btv, ExplorerManager mgr) {
        
        //btv = Utilities.actionsGlobalContext().lookup(BeanTreeView.class); 
        Set<Node> toExpand = new HashSet<Node>();
        for (Node n : nodes) {
            ArrayList<Node> ancestors = new ArrayList();
            Node parent = n.getParentNode();
            while (parent != null && !toExpand.contains(parent)) {
                ancestors.add(parent);
                toExpand.add(parent);
                parent = parent.getParentNode();
            }
            Node[] ancestorNodes = ancestors.toArray(new Node[0]);
            for (int i = ancestorNodes.length - 1; i >= 0; --i) {
                    btv.expandNode(ancestorNodes[i]);
            }
        }
        try {
            mgr.setSelectedNodes((Node[]) (nodes.toArray(new Node[0])));
        } catch (PropertyVetoException ex) {
        } 
    }

    protected static TreePath createTreePath(Node currentNode, List<Node> ancestors) {
        ancestors.add(currentNode);
        if (currentNode.getParentNode() == null) {
            return new TreePath(ancestors);
        }
        return createTreePath(currentNode.getParentNode(), ancestors);
    }

    protected static String getParentInTree(IEntityBase e, Map<String, Node> treeMap, String path) {
        IEntityBase parent = getParent(e);
        if (parent == null) {
            return path;
        }
        String uri = parent.getURIString();
        if (treeMap.containsKey(uri)) {
            return uri + SEPARATOR + path;
        } else {
            return getParentInTree(parent, treeMap, uri + SEPARATOR + path);
        }
    }

    private static IEntityBase getParent(IEntityBase entity) {
        Class type = entity.getClass();
        if (type.isAssignableFrom(Source.class)) {
            return ((Source) entity).getParent();
        } else if (type.isAssignableFrom(Experiment.class)) {
            return ((Experiment) entity).getProjects()[0];
        } else if (type.isAssignableFrom(EpochGroup.class)) {
            EpochGroup parent = ((EpochGroup) entity).getParent();
            if (parent == null) {
                return ((EpochGroup) entity).getExperiment();
            }
            return parent;
        } else if (type.isAssignableFrom(Epoch.class)) {
            return ((Epoch) entity).getEpochGroup();
        } else if (type.isAssignableFrom(Response.class)) {
            return ((Response) entity).getEpoch();
        } else if (type.isAssignableFrom(Stimulus.class)) {
            return ((Stimulus) entity).getEpoch();
        } else if (type.isAssignableFrom(DerivedResponse.class)) {
            return ((DerivedResponse) entity).getEpoch();
        }
        return null;
    }
    
    protected static Node createNode(EntityWrapper key, Map<String, Node> treeMap, Children c)
    {
        AbstractNode n = new AbstractNode(c, Lookups.singleton(key));
        n.setDisplayName(key.getDisplayName());
        setIconForType(n, key.getType());
        if (key.getURI() != null) {
            treeMap.put(key.getURI(), n);
        }
        return n;
    }
    
    protected static void setIconForType(AbstractNode n, Class entityClass) {
        if (entityClass.isAssignableFrom(Experiment.class)) {
            n.setIconBaseWithExtension("");

        } else if (entityClass.isAssignableFrom(EpochGroup.class)) {
            n.setIconBaseWithExtension("");
        }
    }
}
