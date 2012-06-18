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
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import ovation.*;

/**
 *
 * @author huecotanks
 */
public class EntityWrapperUtilities {

    private static String SEPARATOR = ";";

    protected static Set<Node> nodesFromQuery(Map<String, Node> treeMap, Iterator<IEntityBase> itr, ExplorerManager mgr) {
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

    protected static void expandNodes(Set<Node> nodes, BeanTreeView btv, ExplorerManager mgr) {
        //Delete me
        /*for (Node o : mgr.getRootContext().getChildren().getNodes(true)) {
            for (Node p : o.getChildren().getNodes(true)) {
                p.getChildren().getNodes(true);
                for (Node l : p.getChildren().getNodes(true)) {
                    for (Node m : l.getChildren().getNodes(true)) {
                        for (Node q : m.getChildren().getNodes(true)) {
                            for (Node r : q.getChildren().getNodes(true)) {
                                r.getChildren().getNodes(true);
                            }
                        }
                    }
                }
            }
        }*/
        
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
}
