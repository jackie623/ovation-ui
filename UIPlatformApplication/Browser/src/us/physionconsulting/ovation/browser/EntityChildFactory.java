/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physionconsulting.ovation.browser;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
    private boolean projectView;

    EntityChildFactory(EntityWrapper node, boolean pView) {
        ew = node;
        projectView = pView;
    }

    @Override
    protected boolean createKeys(List<EntityWrapper> list) {
        IAuthenticatedDataStoreCoordinator dsc = Lookup.getDefault().lookup(ConnectionProvider.class).getConnection();
        if (dsc == null) {
            return true;
        }
        DataContext c = dsc.getContext();
        if (dsc == null) {
            return true;
        }
        if (ew == null) {
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

            return true;

        } else {
            return createKeysForEntity(ew, list);
        }
    }

    protected boolean createKeysForEntity(EntityWrapper ew, List<EntityWrapper> list) {

        Class entityClass = ew.getType();
        if (projectView) {
            if (entityClass.isAssignableFrom(Project.class)) {
                Project entity = (Project) ew.getEntity();
                for (Experiment e : entity.getExperiments()) {
                    list.add(new EntityWrapper(e));
                }
                return true;
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
                return true;
            }
        }
        if (entityClass.isAssignableFrom(Experiment.class)) {
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
        return true;
    }

    @Override
    protected Node createNodeForKey(EntityWrapper key) {

        return EntityWrapperUtilities.createNode(key, Children.create(new EntityChildFactory(key, projectView), true));
    }
}
