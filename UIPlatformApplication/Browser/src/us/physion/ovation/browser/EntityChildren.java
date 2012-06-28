/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import ovation.*;
import us.physion.ovation.interfaces.ConnectionProvider;

/**
 *
 * @author huecotanks
 */
public class EntityChildren extends Children.Keys<EntityWrapper>{

    EntityWrapper parent;
    boolean projectView;
    
    EntityChildren(EntityWrapper e, boolean pView)
    {
        parent = e;
        projectView = pView;
        setKeys(createKeys());
    }
    
    private EntityChildren(List<EntityWrapper> children, boolean pView)
    {
        projectView = pView;
        setKeys(children);
    }
    
    @Override
    protected Node[] createNodes(EntityWrapper key) {
        
        EntityChildren children;
        //right now, all PerUserEntityWrappers' children are childless. If this changes, the logic here should change
        if (key instanceof PerUserEntityWrapper)
        {
            children = new EntityChildren(((PerUserEntityWrapper)key).getChildren(), projectView);
        }else{
            children = new EntityChildren(key, projectView);
        }
        return new Node[] {EntityWrapperUtilities.createNode(key, children, key.isUnique())};
    }
    
    protected List<EntityWrapper> createKeys() {
        IAuthenticatedDataStoreCoordinator dsc = Lookup.getDefault().lookup(ConnectionProvider.class).getConnection();
        if (dsc == null) {
            return new LinkedList();
        }
        DataContext c = dsc.getContext();
        if (dsc == null) {
            return new LinkedList();
        }
        if (parent == null) {
            List<EntityWrapper> list = new LinkedList<EntityWrapper>();
            //case root node: add entityWrapper for each project
            if (projectView) {
                for (Project p : c.getProjects()) {
                    list.add(new EntityWrapper(p));
                }
            } else {
                Iterator<Source> itr = c.query(Source.class, "isNull(parent)");
                while (itr.hasNext()) {
                    Source s = itr.next();
                    list.add(new EntityWrapper(s));
                }
            }

            return list;

        } else {
            return createKeysForEntity(c, parent);
        }
    }

    protected List<EntityWrapper> createKeysForEntity(DataContext c, EntityWrapper ew) {

        List<EntityWrapper> list = new LinkedList<EntityWrapper>();
        Class entityClass = ew.getType();
        if (projectView) {
            if (entityClass.isAssignableFrom(Project.class)) {
                Project entity = (Project) ew.getEntity();
                for (Experiment e : entity.getExperiments()) {
                    list.add(new EntityWrapper(e));
                }
                String currentUser = c.currentAuthenticatedUser().getUsername();
               
                Iterator<User> userItr = c.getUsersIterator();
                while (userItr.hasNext())
                {
                    User user = userItr.next();
                    String username = user.getUsername();
                    Iterator<AnalysisRecord> itr = entity.getAnalysisRecordIterable(user).iterator();
                    if (itr.hasNext())
                    {
                        List<EntityWrapper> l = new LinkedList();
                        while(itr.hasNext())
                        {
                            l.add(new EntityWrapper(itr.next()));
                        }
                        list.add(new PerUserEntityWrapper(username, user.getURIString(), l));
                    }
                }
             
                return list;
            } 
        } else {
            if (entityClass.isAssignableFrom(Source.class)) {
                Source entity = (Source) ew.getEntity();
                for (Source e : entity.getChildren()) {
                    list.add(new EntityWrapper(e));
                }
                for (Experiment e : entity.getExperiments()) {
                    list.add(new EntityWrapper(e));
                }
                return list;
            }
        }
        if (entityClass.isAssignableFrom(Experiment.class)) {
            Experiment entity = (Experiment) ew.getEntity();

            for (EpochGroup eg : entity.getEpochGroups()) {
                list.add(new EntityWrapper(eg));
            }
            return list;
        } else if (entityClass.isAssignableFrom(EpochGroup.class)) {
            EpochGroup entity = (EpochGroup) ew.getEntity();

            for (EpochGroup eg : entity.getChildren()) {
                list.add(new EntityWrapper(eg));
            }
            for (Epoch e : entity.getEpochs()) {
                list.add(new EntityWrapper(e));
            }
            return list;
        }
        return list;
    }
}
