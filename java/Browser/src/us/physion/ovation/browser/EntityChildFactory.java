/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser;

import java.util.List;
import java.util.concurrent.Callable;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;
import ovation.*;

/**
 *
 * @author huecotanks
 */
public class EntityChildFactory extends ChildFactory<EntityWrapper>{

    private EntityWrapper ew;
    public EntityChildFactory(EntityWrapper node)
    {
        ew = node;
    }
    @Override
    protected boolean createKeys(List<EntityWrapper> list) {
        Callable<IEntityBase> toCall = new Callable<IEntityBase>(){

            @Override
            public IEntityBase call() throws Exception {
                return null;
            }
        };
        for (int i=0; i<5; i++)
        {
            list.add(new EntityWrapper("Node " + i, IEntityBase.class, toCall));
        }
        return true;
    }
    
    protected boolean createKeys2(List<EntityWrapper> list) {
        IEntityBase entity = ew.getEntity();
        if (entity instanceof Project)
        {
            for (Experiment e: ((Project)entity).getExperiments())
            {
                list.add(new EntityWrapper(e));
            }
            return true;
        }
        return true;
    }
    
    @Override
    protected Node createNodeForKey(EntityWrapper key) {
        if (key == null)
        {
            //root node
            
            
        }
        
        Node n = new AbstractNode(Children.create(new EntityChildFactory(key), true), Lookups.singleton(key));
        n.setDisplayName(key.getDisplayName());
        return n;
    }
}
