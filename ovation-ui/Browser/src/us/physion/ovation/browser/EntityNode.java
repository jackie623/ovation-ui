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
import us.physion.ovation.browser.moveme.*;
import us.physion.ovation.interfaces.ConnectionProvider;
import us.physion.ovation.interfaces.IEntityWrapper;

/**
 *
 * @author huecotanks
 */
public class EntityNode extends AbstractNode{

    private Action[] actionList;
    private IEntityWrapper parent;
        
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
               if (entityClass.isAssignableFrom(Project.class))
               {
                   Collection<? extends ProjectInsertable> insertables = Lookup.getDefault().lookupAll(ProjectInsertable.class);
                   List<ProjectInsertable> l = new ArrayList(insertables);
                   Collections.sort(l);
                   actionList = l.toArray(new ProjectInsertable[l.size()]);
                           
               }else if (entityClass.isAssignableFrom(Source.class))
               {
                   Collection<? extends SourceInsertable> insertables = Lookup.getDefault().lookupAll(SourceInsertable.class);
                   List<SourceInsertable> l = new ArrayList(insertables);
                   Collections.sort(l);
                   actionList = l.toArray(new ProjectInsertable[l.size()]);
               }else if (entityClass.isAssignableFrom(Experiment.class))
               {
                   Collection<? extends ExperimentInsertable> insertables = Lookup.getDefault().lookupAll(ExperimentInsertable.class);
                   List<ExperimentInsertable> l = new ArrayList(insertables);
                   Collections.sort(l);
                   actionList = l.toArray(new ExperimentInsertable[l.size()]);
               }else if (entityClass.isAssignableFrom(EpochGroup.class))
               {
                   Collection<? extends EpochGroupInsertable> insertables = Lookup.getDefault().lookupAll(EpochGroupInsertable.class);
                   List<EpochGroupInsertable> l = new ArrayList(insertables);
                   Collections.sort(l);
                   actionList = l.toArray(new EpochGroupInsertable[l.size()]);
               }else if (entityClass.isAssignableFrom(Epoch.class))
               {
                   Collection<? extends EpochInsertable> insertables = Lookup.getDefault().lookupAll(EpochInsertable.class);
                   List<EpochInsertable> l = new ArrayList(insertables);
                   Collections.sort(l);
                   actionList = l.toArray(new EpochInsertable[l.size()]);
               }else if (entityClass.isAssignableFrom(Response.class))
               {
                   Collection<? extends ResponseInsertable> insertables = Lookup.getDefault().lookupAll(ResponseInsertable.class);
                   List<ResponseInsertable> l = new ArrayList(insertables);
                   Collections.sort(l);
                   actionList = l.toArray(new ResponseInsertable[l.size()]);
               }else if (entityClass.isAssignableFrom(DerivedResponse.class))
               {
                   Collection<? extends DerivedResponseInsertable> insertables = Lookup.getDefault().lookupAll(DerivedResponseInsertable.class);
                   List<DerivedResponseInsertable> l = new ArrayList(insertables);
                   Collections.sort(l);
                   actionList = l.toArray(new DerivedResponseInsertable[l.size()]);
               }else if (entityClass.isAssignableFrom(AnalysisRecord.class))
               {
                   Collection<? extends AnalysisRecordInsertable> insertables = Lookup.getDefault().lookupAll(AnalysisRecordInsertable.class);
                   List<AnalysisRecordInsertable> l = new ArrayList(insertables);
                   Collections.sort(l);
                   actionList = l.toArray(new AnalysisRecordInsertable[l.size()]);
               }  
               else{
                   actionList = new Action[0];
               }
           }
       }
        return actionList;
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
    
    private class InsertAction extends AbstractAction{
        
        public InsertAction() {
            putValue(NAME, "Insert");
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            //get selected 
            Lookup.Result global = Utilities.actionsGlobalContext().lookupResult(IEntityWrapper.class);
            Collection<? extends IEntityWrapper> entities = global.allInstances();
            String selection = "";
            if (entities.size() == 1) {
                String uri = entities.iterator().next().getURI();
                System.out.println("Insert uri " + uri);
            }
        }

    }
}
