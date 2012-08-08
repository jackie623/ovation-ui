/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser;

import java.awt.Component;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.explorer.propertysheet.DefaultPropertyModel;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.NewType;
import org.openide.util.datatransfer.PasteType;
import ovation.*;
import us.physion.ovation.interfaces.ConnectionProvider;
import us.physion.ovation.interfaces.IEntityWrapper;

/**
 *
 * @author huecotanks
 */
public class EntityNode extends AbstractNode{

    private Action[] actionList;

        
    public EntityNode(Children c, Lookup l) {
        super (c, l);
        actionList = new Action[] {new OpenAction()};
    }
  
   public EntityNode(Children c)
   {
       super(c);
       actionList = new Action[0];
   }
   
   @Override
   public Sheet createSheet()
   {
       Sheet sheet = Sheet.createDefault();
       IEntityWrapper obj = getLookup().lookup(IEntityWrapper.class);
       IEntityBase e = obj.getEntity();

       IAuthenticatedDataStoreCoordinator dsc = Lookup.getDefault().lookup(ConnectionProvider.class).getConnection();
       DataContext c = dsc.getContext();
       
       Sheet.Set myProperties = createPropertySetForUser(obj, e, c.currentAuthenticatedUser());
       
       if (e.getOwner().getUuid().equals(c.currentAuthenticatedUser().getUuid()))
       {
           myProperties.setDisplayName("My Properties (Owner)");
           sheet.put(myProperties);
       }
       else{
           myProperties.setDisplayName("My Properties");
           sheet.put(myProperties);

           Sheet.Set ownerProperties = createPropertySetForUser(obj, e, e.getOwner());
           
           ownerProperties.setDisplayName("Owner Properties (" + e.getOwner().getUsername() + ")");
           sheet.put(ownerProperties);
       }
       
       Iterator<User> userItr = c.getUsersIterator();
       while (userItr.hasNext())
       {
           User u = userItr.next();
           
           Sheet.Set userProperties = createPropertySetForUser(obj, e, u);
           userProperties.setDisplayName(u.getUsername() + "'s Properties");
           sheet.put(userProperties);
       }
       
       return sheet;
   }
   
   protected Sheet.Set createPropertySetForUser(IEntityWrapper obj, IEntityBase e, User u)
   {
       Sheet.Set properties = Sheet.createPropertiesSet();
       properties.setName(u.getUsername() + "'s Properties");
       Map<String, Object> props = e.getUserProperties(u);
       for (String propKey :props.keySet())
       {
           Property entityProp = new EntityProperty(obj, propKey, props.get(propKey), e.canWrite());
           entityProp.setName(propKey);
           properties.put(entityProp);
       }
       return properties;
   }
   
    @Override
    public Action[] getActions(boolean popup) {
        return actionList;
    }
    
    
    private class OpenAction extends AbstractAction{
        
        public OpenAction() {
            putValue(NAME, "Open");
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            
        }

    }
}
