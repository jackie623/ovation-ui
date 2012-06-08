/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physionconsulting.ovation.browser;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import org.openide.util.Lookup;
import ovation.*;
import us.physion.ovation.interfaces.ConnectionProvider;

/**
 *
 * @author huecotanks
 */
public class EntityWrapper {
    
    private String uri;
    private Class type;
    private String displayName;
    private Callable<IEntityBase> retrieveEntity;
    private String projectPath;
    
    public EntityWrapper(IEntityBase e)
    {
        uri = e.getURIString();
        type = e.getClass();
        displayName = e.toString();
    }
    
    public EntityWrapper(String name, Class clazz, Callable<IEntityBase> toCall)
    {
        type = clazz;
        displayName = name;
        retrieveEntity = toCall;
    }
    
    public IEntityBase getEntity(){
        IAuthenticatedDataStoreCoordinator dsc = Lookup.getDefault().lookup(ConnectionProvider.class).getConnection();
        DataContext c = dsc.getContext();
        if (uri == null)
        {
            try {
                return retrieveEntity.call();
            } catch (Exception ex) {
                return null;
            }
        }
        return c.objectWithURI(uri);
    }
    
    public String getDisplayName() {return displayName;}
    public Class getType() { return type;}

    /*public Set<EntityWrapper> getParents()
    {
        HashSet s = new HashSet();
        if (type.isAssignableFrom(Project.class))
        {
            s.add(this);
        }else if (type.isAssignableFrom(Experiment.class))
        {
            Experiment entity = (Experiment)this.getEntity();
            for (IEntityBase e : entity.getProjects())
            {
                s.add(new EntityWrapper(e));
            }
        }
        else if (type.isAssignableFrom(EpochGroup.class))
        {
            EpochGroup entity = (EpochGroup)this.getEntity();
            for (IEntityBase e : entity.getExperiment().getProjects())
            {
                s.add(new EntityWrapper(e));
            }
        }
        else if (type.isAssignableFrom(Epoch.class))
        {
            EpochGroup entity = (EpochGroup)this.getEntity();
            for (IEntityBase e : entity.getExperiment().getProjects())
            {
                s.add(new EntityWrapper(e));
            }
        }
    }*/
}
