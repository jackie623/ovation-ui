/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import javax.swing.SwingUtilities;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import ovation.*;
import us.physion.ovation.interfaces.ConnectionProvider;
import us.physion.ovation.interfaces.IEntityWrapper;

/**
 *
 * @author huecotanks
 */
public class EntityChildren extends Children.Keys<IEntityWrapper> {

    IEntityWrapper parent;
    boolean projectView;
    IAuthenticatedDataStoreCoordinator dsc;

    EntityChildren(IEntityWrapper e, boolean pView, IAuthenticatedDataStoreCoordinator theDSC) {
        parent = e;
        projectView = pView;
        dsc = theDSC;
        if (e instanceof PerUserEntityWrapper)
        {
            setKeys(((PerUserEntityWrapper)e).getChildren());
        }else{
            initKeys();
        }
    }

    private Callable<Children> getChildrenCallable(final IEntityWrapper key)
    {
        return new Callable<Children>() {

            @Override
            public Children call() throws Exception {
                return new EntityChildren(key, projectView, dsc);
            }
        };
    }

    @Override
    protected Node[] createNodes(final IEntityWrapper key) {

        return new Node[]{EntityWrapperUtilities.createNode(key, Children.createLazy(getChildrenCallable(key)))};
    }

   
    protected void updateWithKeys(final List<IEntityWrapper> list)
    {
        if (EventQueue.isDispatchThread())
        {
            setKeys(list);
            addNotify();
            refresh();
        }
        else{
           // try {
                SwingUtilities.invokeLater(new Runnable(){

                    @Override
                    public void run() {
                        setKeys(list);
                        addNotify();
                        refresh();
                    }
                    
                });
            /*} catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }*/
        }
    }
    
    protected void initKeys()
    {
        if (EventQueue.isDispatchThread())
        {
            BrowserUtilities.submit(new Runnable(){

                @Override
                public void run() {
                    createKeys();
                }
            });
        }
        else{
            createKeys();
        }
    }
    
    protected void createKeys() {
        
        if (dsc == null)
            dsc = Lookup.getDefault().lookup(ConnectionProvider.class).getConnection();
        if (dsc == null) {
            return;
        }
        DataContext c = dsc.getContext();
        
        if (parent == null) {
            List<IEntityWrapper> list = new LinkedList<IEntityWrapper>();
            //case root node: add entityWrapper for each project
            if (projectView) {
                for (Project p : c.getProjects()) {
                    p.getURIString();
                    EntityWrapper x = new EntityWrapper(p);
                    list.add(new EntityWrapper(p));
                }
            } else {
                Iterator<Source> itr = c.query(Source.class, "isNull(parent)");
                while (itr.hasNext()) {
                    Source s = itr.next();
                    s.getURIString();
                    EntityWrapper x = new EntityWrapper(s);
                    list.add(new EntityWrapper(s));
                }
            }

            updateWithKeys(list);

        } else {
            updateWithKeys(createKeysForEntity(c, parent));
        }
    }

    protected List<IEntityWrapper> createKeysForEntity(DataContext c, IEntityWrapper ew) {

        DataContext context = dsc.getContext();
        List<IEntityWrapper> list = new LinkedList<IEntityWrapper>();
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
                int count = 0;
                for (Epoch e : entity.getEpochsIterable()) {
                    
                    list.add(new EntityWrapper(e));
                    /*if (count%10 == 0)
                    {
                        updateWithKeys(list);
                    }
                    count++;*/
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
