/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physionconsulting.ovation.browser;

import java.util.*;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author huecotanks
 */
public class QueryChildren extends Children.Keys<EntityWrapper> {

    Set<EntityWrapper> keys = new HashSet<EntityWrapper>();
    Set<Stack<EntityWrapper>> paths = new HashSet();
    Set<String> keyURIs = new HashSet<String>();
    
    protected QueryChildren() {}
    
    protected QueryChildren(Set<Stack<EntityWrapper>> paths)
    {
        if (paths == null)
            return;
        for (Stack<EntityWrapper> path : paths)
        {
            addPath(path);
        }
    }
    
    @Override
    protected Node[] createNodes(EntityWrapper key) {
        return new Node[]{ EntityWrapperUtilities.createNode(key, new QueryChildren(paths))};
    }
    
    @Override
    protected void addNotify()
    {
        setKeys(keys);
    }
    
    @Override
    protected void removeNotify()
    {
        setKeys(Collections.EMPTY_SET);
    }
    
    protected void addKey(EntityWrapper key)
    {
        keys.add(key);
    }
    
    protected void addKeys(Collection<EntityWrapper> toAdd)
    {
        keys.addAll(toAdd);
    }
    
    protected void addPath(Stack<EntityWrapper> path)
    {
        if (path ==null || path.isEmpty())
        {
            return;
        }
        EntityWrapper e = path.pop();
        if (keyURIs.contains(e.getURI()))
        {
            return;
        }
        keyURIs.add(e.getURI());
        keys.add(e);
        addNotify();
        paths.add(path);
    }
    
}
