/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser;

import java.util.concurrent.Callable;
import org.openide.util.Exceptions;
import ovation.IEntityBase;

/**
 *
 * @author huecotanks
 */
public class EntityWrapper {
    
    private IEntityBase entity;
    private Class type;
    private String displayName;
    private Callable<IEntityBase> retrieveEntity;
    
    public EntityWrapper(IEntityBase e)
    {
        entity = e;
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
        if (entity == null)
        {
            try {
                return retrieveEntity.call();
            } catch (Exception ex) {
                return null;
            }
        }
        return entity;
    }
    
    public String getDisplayName() {return displayName;}
    public Class getType() { return type;}
}
