/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser;

import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.Node.Property;

/**
 *
 * @author huecotanks
 */
public class EntityProperty extends Property{

    boolean writable;
    Object value;
    String key;
    
    EntityWrapper entity;
    
    EntityProperty(EntityWrapper e, String key, Object value, boolean canWrite)
    {
        super(value.getClass());
        entity = e;
        this.key = key;
        this.value = value;
        writable = canWrite;
    }
    
    @Override
    public boolean canRead() {
        return true;
    }

    @Override
    public Object getValue() throws IllegalAccessException, InvocationTargetException {
        return value;
    }

    @Override
    public boolean canWrite() {
        return writable;
    }

    @Override
    public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        entity.getEntity().addProperty(key, t);
        value = t;
    }
    
}
