/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physionconsulting.ovation.browser;

import java.util.*;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;
import ovation.*;

/**
 *
 * @author huecotanks
 */
public class EntityWrapperUtilities {
    private static String SEPARATOR = ";";
    
    protected static void expandNodesFromQuery(Map<String, Node> treeMap, Iterator<IEntityBase> itr, BeanTreeView btv, ExplorerManager mgr)
    {
        while (itr.hasNext())
        {
            IEntityBase e = itr.next();
            String key = e.getURIString();
            Node n;
            if (!treeMap.containsKey(key))
            {
                String path = getParentInTree(e, treeMap, "");
                String[] uris = path.split(SEPARATOR);
                
                Node parentInTree = treeMap.get(uris[0]);
                if (parentInTree == null)
                {
                    parentInTree = mgr.getRootContext();
                }
                else
                {
                    uris = Arrays.copyOfRange(uris, 1, uris.length);
                }
                
                for (String uri : uris)
                {
                    btv.expandNode(parentInTree);
                    Node[] nodes = parentInTree.getChildren().getNodes();
                    for (Node node : nodes) {
                        EntityWrapper ew = (EntityWrapper) node.getLookup().lookup(EntityWrapper.class);
                        if (ew.getURI().equals(uri)) {
                            parentInTree = node;
                            break;
                        }
                    }
                }
            } else {
                n = treeMap.get(key);
                btv.expandNode(n);
            }
        }
    }
    
    protected static String getParentInTree(IEntityBase e, Map<String, Node> treeMap, String path)
    {
        IEntityBase parent = getParent(e);
        if (parent == null)
        {
            return path;
        }
        String uri = parent.getURIString();
        if (treeMap.containsKey(uri)) {
            return uri + SEPARATOR + path;
        }else {
            return getParentInTree(parent, treeMap, uri + SEPARATOR + path);
        }
    }
    
    private static IEntityBase getParent(IEntityBase entity)
    {
        Class type = entity.getClass();
        if (type.isAssignableFrom(Source.class)){
            return ((Source)entity).getParent();
        }
        else if (type.isAssignableFrom(Experiment.class))
        {
            return ((Experiment)entity).getProjects()[0];
        }
        else if (type.isAssignableFrom(EpochGroup.class))
        {
            EpochGroup parent = ((EpochGroup)entity).getParent();
            if (parent == null)
            {
                return ((EpochGroup)entity).getExperiment();
            }
            return parent;
        }
        else if (type.isAssignableFrom(Epoch.class))
        {
            return((Epoch)entity).getEpochGroup();
        }
        else if (type.isAssignableFrom(Response.class))
        {
            return((Response)entity).getEpoch();
        }
        else if (type.isAssignableFrom(Stimulus.class))
        {
            return((Stimulus)entity).getEpoch();
        }
        else if (type.isAssignableFrom(DerivedResponse.class))
        {
            return((DerivedResponse)entity).getEpoch();
        }
        return null;
    }
}
