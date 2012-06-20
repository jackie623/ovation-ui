/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physionconsulting.ovation.browser;

import java.util.*;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import ovation.Project;
import ovation.Source;

/**
 *
 * @author huecotanks
 */
public class QueryChildren extends Children.Keys<EntityWrapper> {

    Set<EntityWrapper> keys = new HashSet<EntityWrapper>();
    Set<Stack<EntityWrapper>> paths = new HashSet();
    Set<String> keyURIs = new HashSet<String>();
    private boolean projectView;
    
    protected QueryChildren(boolean pView) 
    {
        projectView = pView;
    }
    
    protected QueryChildren(Set<Stack<EntityWrapper>> paths, boolean pView)
    {
        this(pView);
        
        if (paths == null)
            return;
        for (Stack<EntityWrapper> path : paths)
        {
            addPath(path);
        }
    }
    
    @Override
    protected Node[] createNodes(EntityWrapper key) {
        return new Node[]{ EntityWrapperUtilities.createNode(key, new QueryChildren(paths, projectView))};
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
    
    protected boolean shouldAdd(EntityWrapper e)
    {
        if (projectView)
        {
            if (e.getType().isAssignableFrom(Source.class))
            {
                return false;
            }
        }else {
            if (e.getType().isAssignableFrom(Project.class))
            {
                return false;
            }
        }
        if (keyURIs.contains(e.getURI()))
        {
            return false;
        }
        return true;
    }
    
    protected void addPath(Stack<EntityWrapper> path)
    {
        if (path ==null || path.isEmpty())
        {
            return;
        }
        EntityWrapper e = path.pop();
        
        if (shouldAdd(e)) 
        {
            keyURIs.add(e.getURI());
            keys.add(e);
            addNotify();
            paths.add(path);
        }
    }
    
}
