/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser;

import java.awt.Component;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.actions.CopyAction;
import org.openide.explorer.propertysheet.DefaultPropertyModel;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.NewType;
import org.openide.util.datatransfer.PasteType;
import ovation.*;
import us.physion.ovation.interfaces.*;

/**
 *
 * @author huecotanks
 */
public class EntityNode extends AbstractNode implements ResettableNode{

    private Action[] actionList;
    private IEntityWrapper parent;
    private static Map<Class, Class> insertableMap = createMap();
        
    public EntityNode(Children c, Lookup l, IEntityWrapper parent) {
        super (c, l);
        this.parent = parent;
    }
  
   public EntityNode(Children c, IEntityWrapper parent)
   {
       super(c);
       this.parent = parent;
   }
   @Override
   public void resetNode()
   {
       Children c = getChildren();
       if (c == null || this.isLeaf())
           return;
       if (c instanceof EntityChildren)
       {
           ((EntityChildren)c).initKeys();
       }
   }
   
   protected void setActionList(Action[] actions)
   {
       actionList = actions;
   }
   
   @Override
    public Action[] getActions(boolean popup) {
       if (actionList == null)
       {
           if (parent == null)// root node
           {
               Collection<? extends RootInsertable> insertables = Lookup.getDefault().lookupAll(RootInsertable.class);
               List<RootInsertable> l = new ArrayList(insertables);
               Collections.sort(l);
               actionList = l.toArray(new RootInsertable[l.size()]);
           }
           else{
               Class entityClass = parent.getType();
               Class insertableClass = insertableMap.get(entityClass);
               if (insertableClass == null)
               {
                   actionList = new Action[0];
               } else {
                   Collection insertables = Lookup.getDefault().lookupAll(insertableClass);
                   List<? extends Comparable> l = new ArrayList(insertables);
                   Collections.sort(l);
                   actionList = l.toArray(new EntityInsertable[l.size()]);
               }
           }
       }
        return actionList;
    }

    private static Map<Class, Class> createMap() {
        Map<Class, Class> insertables = new HashMap<Class, Class>();
        insertables.put(Project.class, ProjectInsertable.class);
        insertables.put(Source.class, SourceInsertable.class);
        insertables.put(Experiment.class, ExperimentInsertable.class);
        insertables.put(EpochGroup.class, EpochGroupInsertable.class);
        insertables.put(Epoch.class, EpochInsertable.class);
        insertables.put(Response.class, ResponseInsertable.class);
        insertables.put(Stimulus.class, StimulusInsertable.class);
        insertables.put(DerivedResponse.class, DerivedResponseInsertable.class);
        return insertables;
    }
   
   /*@Override
   public Sheet createSheet()
   {
       Sheet sheet = Sheet.createDefault();
       IEntityWrapper obj = getLookup().lookup(IEntityWrapper.class);

       IAuthenticatedDataStoreCoordinator dsc = Lookup.getDefault().lookup(ConnectionProvider.class).getConnection();
       DataContext c = dsc.getContext();
       IEntityBase e = obj.getEntity();
       
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
    }*/
    //TODO: figure out how to make this work
    /*
    @Override
    public boolean canCopy() {
        return true;
    }
    
     @Override
    public Transferable clipboardCopy() throws IOException {
        Transferable deflt = super.clipboardCopy();
        ExTransferable added = ExTransferable.create(deflt);
        added.put(new ExTransferable.Single(DataFlavor.stringFlavor) {
            @Override
            protected String getData() {
                Lookup.Result global = Utilities.actionsGlobalContext().lookupResult(IEntityWrapper.class);
                Collection<? extends IEntityWrapper> entities = global.allInstances();
                String selection = "";
                if (entities.size() == 1) {
                    selection += entities.iterator().next().getURI();
                } else {
                    for (IEntityWrapper ew : entities) {
                        selection += ew.getURI() + "\n";
                    }
                }
                System.out.println("Selection: " + selection);
                return selection;
            }
        });
        return added;
    }*/
}
