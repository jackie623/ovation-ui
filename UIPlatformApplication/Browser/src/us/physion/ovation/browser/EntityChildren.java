/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.awt.EventQueue;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import ovation.*;
import us.physion.ovation.interfaces.ConnectionProvider;

/**
 *
 * @author huecotanks
 */
public class EntityChildren extends Children.Keys<EntityWrapper> {

    EntityWrapper parent;
    boolean projectView;
    IAuthenticatedDataStoreCoordinator dsc;
    ExecutorService executor;

    EntityChildren(EntityWrapper e, boolean pView, IAuthenticatedDataStoreCoordinator theDSC, ExecutorService es) {
        parent = e;
        projectView = pView;
        dsc = theDSC;
        executor = es;
        if (e instanceof PerUserEntityWrapper)
        {
            setKeys(((PerUserEntityWrapper)e).getChildren());
        }else{
            setKeys(createKeys());
        }
    }

    private Callable<Children> getChildrenCallable(final EntityWrapper key)
    {
        return new Callable<Children>() {

            @Override
            public Children call() throws Exception {
                //return new EntityChildren(key, projectView, dsc, executor);
                
                //if in the event queue thread, run in another thread
                if (EventQueue.isDispatchThread()) {
                    Callable<Children> toCall = new Callable<Children>() {

                        @Override
                        public EntityChildren call() {
                            return new EntityChildren(key, projectView, dsc, executor);
                        }
                    };
                    String type;
                    if (parent == null)
                    {
                        type = "Root";
                    }
                    else{
                        type = parent.getType().getName();
                    }
                    System.out.println("Getting children for (" + type + ") in another thread");
                    Future<Children> t = BrowserUtilities.submit(toCall);
                    long time = Calendar.getInstance().getTimeInMillis();
                    Children c = t.get();
                    System.out.println("Getting children entity took: " + (Calendar.getInstance().getTimeInMillis() - time));    
                    return c;
                } else {
                    return new EntityChildren(key, projectView, dsc, executor);
                }
            }
        };
    }

    @Override
    protected Node[] createNodes(final EntityWrapper key) {

        return new Node[]{EntityWrapperUtilities.createNode(key, Children.createLazy(getChildrenCallable(key)), key.isUnique())};
    }

    protected List<EntityWrapper> createKeys() {
        if (dsc == null)
            dsc = Lookup.getDefault().lookup(ConnectionProvider.class).getConnection();
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

        DataContext context = dsc.getContext();
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
                while (userItr.hasNext()) {
                    User user = userItr.next();
                    String username = user.getUsername();
                    Iterator<AnalysisRecord> itr = entity.getAnalysisRecordIterable(user).iterator();
                    if (itr.hasNext()) {
                        List<EntityWrapper> l = new LinkedList();
                        while (itr.hasNext()) {
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
            
            context.beginTransaction();
            try {
                for (Epoch e : entity.getEpochsIterable()) {
                    list.add(new EntityWrapper(e));
                }
            } finally{
                context.commitTransaction();
            }
            return list;
        } else if (entityClass.isAssignableFrom(Epoch.class)) {
            context.beginTransaction();
            try {
                Epoch entity = (Epoch) ew.getEntity();
                for (Stimulus s : entity.getStimulusIterable()) {
                    list.add(new EntityWrapper(s));
                }
                for (Response r : entity.getResponseIterable()) {
                    list.add(new EntityWrapper(r));
                }

                String currentUser = c.currentAuthenticatedUser().getUsername();

                Iterator<User> userItr = c.getUsersIterator();
                while (userItr.hasNext()) {
                    User user = userItr.next();
                    String username = user.getUsername();
                    Iterator<DerivedResponse> itr = entity.getDerivedResponseIterable(user).iterator();
                    if (itr.hasNext()) {
                        List<EntityWrapper> l = new LinkedList();
                        while (itr.hasNext()) {
                            l.add(new EntityWrapper(itr.next()));
                        }
                        list.add(new PerUserEntityWrapper(username, user.getURIString(), l));
                    }
                }
            } finally {
                context.commitTransaction();
            }
        }
        return list;
    }
}
