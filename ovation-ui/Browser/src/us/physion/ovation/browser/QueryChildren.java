/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser;

import java.util.*;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import ovation.Project;
import ovation.Source;

import us.physion.ovation.interfaces.IEntityWrapper;


/**
 *
 * @author huecotanks
 */
public class QueryChildren extends Children.Keys<IEntityWrapper> {

    Set<IEntityWrapper> keys = new HashSet<IEntityWrapper>();
    Set<String> keyURIs = new HashSet<String>();
    private boolean projectView;
    private HashMap<String, Children> childrenMap = new HashMap<String, Children>();
    private HashMap<String, Set<Stack<IEntityWrapper>>> pathMap = new HashMap<String, Set<Stack<IEntityWrapper>>>();

    protected QueryChildren(boolean pView) {
        projectView = pView;
    }

    protected QueryChildren(Set<Stack<IEntityWrapper>> paths, boolean pView) {
        this(pView);

        if (paths == null) {
            return;
        }
        for (Stack<IEntityWrapper> path : paths) {
            addPath(path);
        }
    }
  
    @Override
    protected Node[] createNodes(IEntityWrapper key) {
        Children children = childrenMap.get(key.getURI());
        if (children == null)
        {
            children = new QueryChildren(pathMap.get(key.getURI()), projectView);
            childrenMap.put(key.getURI(), children);
        } else if (children instanceof QueryChildren)
        {
            for (Stack<IEntityWrapper> path : pathMap.get(key.getURI()))
                ((QueryChildren)children).addPath(path);
        } 
        return new Node[]{EntityWrapperUtilities.createNode(key, children)};
    }

    @Override
    protected void addNotify() {
        setKeys(keys);
    }

    @Override
    protected void removeNotify() {
        setKeys(Collections.EMPTY_SET);
    }

    protected boolean shouldAdd(IEntityWrapper e) {
        if (projectView) {
            if (e.getType().isAssignableFrom(Source.class)) {
                return false;
            }
        } else {
            if (e.getType().isAssignableFrom(Project.class)) {
                return false;
            }
        }
        return true;
    }

    protected void addPath(Stack<IEntityWrapper> path) {
        if (path == null || path.isEmpty()) {
            return;
        }
        IEntityWrapper e = path.pop();

        if (shouldAdd(e)) {
            if (!keyURIs.contains(e.getURI())) {
                keyURIs.add(e.getURI());
                keys.add(e);
                pathMap.put(e.getURI(), new HashSet<Stack<IEntityWrapper>>());
                addNotify();
                refresh();//in case the node is already created
            }
            Set<Stack<IEntityWrapper>> paths = pathMap.get(e.getURI());
            paths.add(path);
            Children children = childrenMap.get(e.getURI());
            if (children != null)
            {
                if (children instanceof QueryChildren) 
                    ((QueryChildren)children).addPath(path);
            }else{
                childrenMap.put(e.getURI(), new EntityChildren((EntityWrapper)e, projectView, null));
            }
        }
    }
}
