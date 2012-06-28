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

/**
 *
 * @author huecotanks
 */
public class QueryChildren extends Children.Keys<EntityWrapper> {

    Set<EntityWrapper> keys = new HashSet<EntityWrapper>();
    Set<String> keyURIs = new HashSet<String>();
    private boolean projectView;
    private HashMap<String, QueryChildren> childrenMap = new HashMap<String, QueryChildren>();
    private HashMap<String, Set<Stack<EntityWrapper>>> pathMap = new HashMap<String, Set<Stack<EntityWrapper>>>();

    protected QueryChildren(boolean pView) {
        projectView = pView;
    }

    protected QueryChildren(Set<Stack<EntityWrapper>> paths, boolean pView) {
        this(pView);

        if (paths == null) {
            return;
        }
        for (Stack<EntityWrapper> path : paths) {
            addPath(path);
        }
    }
  
    @Override
    protected Node[] createNodes(EntityWrapper key) {
        QueryChildren children;
        
        children = new QueryChildren(pathMap.get(key.getURI()), projectView);
        childrenMap.put(key.getURI(), children);

        return new Node[]{EntityWrapperUtilities.createNode(key, children, key.isUnique())};
    }

    @Override
    protected void addNotify() {
        setKeys(keys);
    }

    @Override
    protected void removeNotify() {
        setKeys(Collections.EMPTY_SET);
    }

    protected boolean shouldAdd(EntityWrapper e) {
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

    protected void addPath(Stack<EntityWrapper> path) {
        if (path == null || path.isEmpty()) {
            return;
        }
        EntityWrapper e = path.pop();

        if (shouldAdd(e)) {
            if (!keyURIs.contains(e.getURI())) {
                keyURIs.add(e.getURI());
                keys.add(e);
                pathMap.put(e.getURI(), new HashSet<Stack<EntityWrapper>>());
                addNotify();
                refresh();//in case the node is already created
            }
            Set<Stack<EntityWrapper>> paths = pathMap.get(e.getURI());
            paths.add(path);
            QueryChildren children = childrenMap.get(e.getURI());
            if (children != null) {
                children.addPath(path);
            }
        }
    }
}
