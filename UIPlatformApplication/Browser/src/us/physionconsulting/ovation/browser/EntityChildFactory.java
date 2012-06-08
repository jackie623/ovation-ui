/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physionconsulting.ovation.browser;

import java.util.List;
import java.util.concurrent.Callable;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import ovation.*;
import us.physion.ovation.interfaces.ConnectionProvider;

/**
 *
 * @author huecotanks
 */
public class EntityChildFactory extends ChildFactory<EntityWrapper> {

    private EntityWrapper ew;

    EntityChildFactory(EntityWrapper node) {
        ew = node;
    }

    protected boolean createKeys2(List<EntityWrapper> list) {

        Callable<IEntityBase> toCall = new Callable<IEntityBase>() {

            @Override
            public IEntityBase call() throws Exception {
                return null;
            }
        };
        for (int i = 0; i < 5; i++) {
            list.add(new EntityWrapper("Node " + i, IEntityBase.class, toCall));
        }
        return true;
    }

    @Override
    protected boolean createKeys(List<EntityWrapper> list) {
        IAuthenticatedDataStoreCoordinator dsc = Lookup.getDefault().lookup(ConnectionProvider.class).getConnection();
        DataContext c = dsc.getContext();
        if (dsc == null) {
            return true;
        }
        if (ew == null) {
            //case root node: add entityWrapper for each project
            for (Project p : c.getProjects())
            {
                list.add(new EntityWrapper(p));
            }
            
            for (Source p : c.getSources())
            {
                list.add(new EntityWrapper(p));
            }
            return true;
            
        } else {
            return createKeysForEntity(ew, list);
        }
    }

    protected boolean createKeysForEntity(EntityWrapper ew, List<EntityWrapper> list) {
        
        Class entityClass = ew.getType();
        if (entityClass.isAssignableFrom(Project.class)) {
            Project entity = (Project) ew.getEntity();
            for (Experiment e : entity.getExperiments()) {
                list.add(new EntityWrapper(e));
            }
            return true;
        } else if (entityClass.isAssignableFrom(Experiment.class)) {
            Experiment entity = (Experiment) ew.getEntity();

            for (EpochGroup eg : entity.getEpochGroups()) {
                list.add(new EntityWrapper(eg));
            }
            return true;
        } else if (entityClass.isAssignableFrom(EpochGroup.class)) {
            EpochGroup entity = (EpochGroup) ew.getEntity();

            for (EpochGroup eg : entity.getChildren()) {
                list.add(new EntityWrapper(eg));
            }
            for (Epoch e : entity.getEpochs()) {
                list.add(new EntityWrapper(e));
            }
            return true;
        }
        return false;
    }
    
    /*protected Node getNodeForKey(EntityWrapper key)
    {
        //find project node, then walk down the tree to key
        Class entityClass = ew.getType();
        if (entityClass.isAssignableFrom(Project.class)) {
            Project entity = (Project) ew.getEntity();
            for (Experiment e : entity.getExperiments()) {
                list.add(new EntityWrapper(e));
            }
            return true;
        } 
    }*/

    @Override
    protected Node createNodeForKey(EntityWrapper key) {
      
        Node n = new AbstractNode(Children.create(new EntityChildFactory(key), true), Lookups.singleton(key));
        n.setDisplayName(key.getDisplayName());
        return n;
    }
}
