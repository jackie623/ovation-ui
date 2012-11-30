/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import java.util.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import ovation.DataContext;
import ovation.IAuthenticatedDataStoreCoordinator;
import ovation.IEntityBase;
import ovation.User;
import us.physion.ovation.interfaces.ConnectionProvider;
import us.physion.ovation.interfaces.EventQueueUtilities;
import us.physion.ovation.interfaces.IEntityWrapper;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//us.physion.ovation.detailviews//PropertiesView//EN",
autostore = false)
@TopComponent.Description(preferredID = "PropertiesViewTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE", 
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "properties", openAtStartup = true)
@ActionID(category = "Window", id = "us.physion.ovation.detailviews.PropertiesViewTopComponent")
@ActionReference(path = "Menu/Window" /*
 * , position = 333
 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_PropertiesViewAction",
preferredID = "PropertiesViewTopComponent")
@Messages({
    "CTL_PropertiesViewAction=Properties",
    "CTL_PropertiesViewTopComponent=Properties",
    "HINT_PropertiesViewTopComponent=Displays the properties of the selected entites"
})
public final class PropertiesViewTopComponent extends TopComponent {

    Lookup.Result global;
    private Collection<? extends IEntityWrapper> entities;
    private LookupListener listener = new LookupListener() {

        @Override
        public void resultChanged(LookupEvent le) {

            //TODO: we should have some other Interface for things that can update the tags view
            //then we could get rid of the Library dependancy on the Explorer API
            if (TopComponent.getRegistry().getActivated() instanceof ExplorerManager.Provider)
            {
                update();
            }
        }

    };
    
    public void update()
    {
        EventQueueUtilities.runOffEDT(new Runnable() {

            public void run() {
                update(global.allInstances());
            }
        });
    }
    
    public ScrollableTableTree getTableTree()
    {
        return (ScrollableTableTree)jScrollPane1;
    }        
            
    
    public void update(final Collection<? extends IEntityWrapper> entities)
    {
        setEntities(entities, null);
        
        if (entities.size() > 1) {
            EventQueueUtilities.runOnEDT(new Runnable() {

                @Override
                public void run() {

                    setName(Bundle.CTL_PropertiesViewTopComponent() + " - " + entities.size() + " entities");
                }
            });
        } else {
            EventQueueUtilities.runOnEDT(new Runnable() {

                @Override
                public void run() {

                    setName(Bundle.CTL_PropertiesViewTopComponent());
                }
            });
        }
    }
    
    protected void setEntities(Collection<? extends IEntityWrapper> entities, IAuthenticatedDataStoreCoordinator dsc)
    {
        DataContext c;
        if (dsc == null) {
            c = Lookup.getDefault().lookup(ConnectionProvider.class).getConnection().getContext();
        }else{
            c = dsc.getContext();
        }

        ArrayList<TableTreeKey> properties = new ArrayList<TableTreeKey>();
        Set<String> uris = new HashSet<String>();
        Set<IEntityBase> entitybases = new HashSet();
        Set<String> owners = new HashSet();
        for (IEntityWrapper w : entities) {
            IEntityBase e = w.getEntity();
            entitybases.add(e);
            uris.add(e.getURIString());
            owners.add(e.getOwner().getUuid());
        }

        String currentUserUUID = c.currentAuthenticatedUser().getUuid();
        Iterator<User> users = c.getUsersIterator();
        boolean containsCurrentUser = false;//current user's property table should always exist, even if there are no properties
        while (users.hasNext()) {
            User u = users.next();
            Map<String, Object> userProps = new HashMap();
            for (IEntityBase e : entitybases) {
                userProps.putAll(e.getUserProperties(u));
            }
            if (!userProps.isEmpty()) {
                String uuid = u.getUuid();
                UserPropertySet propertySet;
                if (currentUserUUID.equals(uuid)) {
                    containsCurrentUser = true;
                    propertySet = new UserPropertySet(u, owners.contains(uuid), true, userProps, uris);
                } else {
                    propertySet = new UserPropertySet(u, owners.contains(uuid), false, userProps, uris);
                }
                properties.add(propertySet);
            }
        }
        if (!containsCurrentUser) {
            User current = c.currentAuthenticatedUser();
            properties.add(new UserPropertySet(current, owners.contains(current.getUuid()), true, new HashMap<String, Object>(), uris));
        }
        
        Collections.sort(properties);
        
        ((ScrollableTableTree) jScrollPane1).setKeys(properties);
        
        this.entities = entities;
    }
    
    public PropertiesViewTopComponent() {
        initComponents();
        setName(Bundle.CTL_PropertiesViewTopComponent());
        setToolTipText(Bundle.HINT_PropertiesViewTopComponent());
        
        global = Utilities.actionsGlobalContext().lookupResult(IEntityWrapper.class);
        global.addLookupListener(listener);
    }
    
    public Collection<? extends IEntityWrapper> getEntities()
    {
        return entities;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new ScrollableTableTree();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
