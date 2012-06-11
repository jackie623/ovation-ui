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
        displayName = inferDisplayName(e);
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
    public String getURI()
    {
        return uri;
    }
    public String getDisplayName() {return displayName;}
    public Class getType() { return type;}

    /*public EntityWrapper getParent()
    {
        if (type.isAssignableFrom(Source.class)){
            Source s= (Source)this.getEntity();
            if (s.getParent() == null)
            {
                return null;
            }
            return new EntityWrapper(s.getParent());
        }
        else if (type.isAssignableFrom(Project.class))
        {
            return null;
        }else if (type.isAssignableFrom(Experiment.class))
        {
            Experiment entity = (Experiment)this.getEntity();
            return new EntityWrapper(entity.getProjects()[0]);
        }
        else if (type.isAssignableFrom(EpochGroup.class))
        {
            EpochGroup entity = (EpochGroup)this.getEntity();
            EpochGroup parent = entity.getParent();
            if (parent == null)
            {
                return new EntityWrapper(entity.getExperiment());
            }
            return new EntityWrapper(parent);
        }
        else if (type.isAssignableFrom(Epoch.class))
        {
            Epoch entity = (Epoch)this.getEntity();
            return new EntityWrapper(entity.getEpochGroup());
        }
        else if (type.isAssignableFrom(Response.class))
        {
            Response entity = (Response)this.getEntity();
            return new EntityWrapper(entity.getEpoch());
        }
        else if (type.isAssignableFrom(Stimulus.class))
        {
            Stimulus entity = (Stimulus)this.getEntity();
            return new EntityWrapper(entity.getEpoch());
        }
        else if (type.isAssignableFrom(DerivedResponse.class))
        {
            DerivedResponse entity = (DerivedResponse)this.getEntity();
            return new EntityWrapper(entity.getEpoch());
        }
        return null;
    }*/

    private String inferDisplayName(IEntityBase e) {
        
        if (type.isAssignableFrom(Source.class))
        {
            return ((Source)e).getLabel();
        }
        else if (type.isAssignableFrom(Project.class))
        {
            return ((Project)e).getName();
        }else if (type.isAssignableFrom(Experiment.class))
        {
            return ((Experiment)e).getStartTime().toString("MM/dd/yyyy-hh:mm:ss");
        }
        else if (type.isAssignableFrom(EpochGroup.class))
        {
            return ((EpochGroup)e).getLabel();
        }
        else if (type.isAssignableFrom(Epoch.class))
        {
            return ((Epoch)e).getProtocolID();
        }
        else if (type.isAssignableFrom(Response.class))
        {
            return ((Response)e).getExternalDevice().getName();
        }
        else if (type.isAssignableFrom(Stimulus.class))
        {
            return ((Stimulus)e).getExternalDevice().getName();
        }
        else if (type.isAssignableFrom(DerivedResponse.class))
        {
            return ((DerivedResponse)e).getName();
        }
        return "<no name>";
    }
}
