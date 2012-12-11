/**
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import java.awt.event.ActionEvent;
import java.util.*;
import java.util.concurrent.FutureTask;
import javax.swing.AbstractListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListSelectionEvent;
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
import org.openide.windows.WindowManager;
import ovation.*;
import us.physion.ovation.interfaces.ConnectionProvider;
import us.physion.ovation.interfaces.IEntityWrapper;


@ConvertAsProperties(dtd = "-//us.physion.ovation.detailviews//TagsView//EN",
autostore = false)
@TopComponent.Description(preferredID = "TagsViewTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE",
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "properties", openAtStartup = true)
@ActionID(category = "Window", id = "us.physion.ovation.detailviews.TagsViewTopComponent")
@ActionReference(path = "Menu/Window" /*
 * , position = 333
 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_TagsViewAction",
preferredID = "TagsViewTopComponent")
@Messages({
    "CTL_TagsViewAction=Keyword Tags",
    "CTL_TagsViewTopComponent=Keyword Tags",
    "HINT_TagsViewTopComponent=This is a Keyword Tags window"
})
public final class TagsViewTopComponent extends TopComponent {

    private DefaultComboBoxModel tagComboModel = new DefaultComboBoxModel(new String[] {});
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

    /*protected void addTag(Collection<? extends IEntityWrapper> entities, String tag) {
        for (IEntityWrapper e : entities) {

            IEntityBase ie = e.getEntity();
            if (ie instanceof ITaggableEntityBase) {
                ((ITaggableEntityBase) ie).addTag(tag);
            }
        }
        tagComboModel.removeAllElements();
        addTagComboBox.setSelectedItem("");
        addTagComboBox.setSelectedItem(null);
    }*/

    protected void update()
    {
        entities = global.allInstances();
        ConnectionProvider cp = Lookup.getDefault().lookup(ConnectionProvider.class);
        cp.getConnection().getContext(); //getContext
        update(entities, Lookup.getDefault().lookup(ConnectionProvider.class).getConnection());
    }

    protected List<TableTreeKey> update(Collection<? extends IEntityWrapper> entities, IAuthenticatedDataStoreCoordinator dsc)
    {
        /*if (entities.isEmpty()) {
            listModel.setTags(new LinkedList<String>());
            return null;
        }

        List<String> tags = new LinkedList<String>();
        for (IEntityWrapper e: entities)
        {
            IEntityBase entity = e.getEntity();
            if (entity != null && entity instanceof ITaggableEntityBase)
            {
                for (KeywordTag t : ((ITaggableEntityBase)entity).getTagSet())
                {
                    tags.add(t.getTag());
                }
            }
        }

        listModel.setTags(tags);
        return null;
       */
        DataContext c;
        if (dsc == null) {
            c = Lookup.getDefault().lookup(ConnectionProvider.class).getConnection().getContext();
        }else{
            c = dsc.getContext();
        }

        ArrayList<TableTreeKey> tags = new ArrayList<TableTreeKey>();
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
            List<String> taglist = new ArrayList<String>();
            for (IEntityBase e : entitybases) {
                if (e instanceof ITaggableEntityBase)
                {
                    for (KeywordTag t : ((ITaggableEntityBase)e).getTagSet())//TODO: Make this faster
                    {
                        if (t.getOwner().getUuid().equals(u.getUuid()))
                        {
                            taglist.add(t.getTag());
                        }
                    }
                }
            }
            if (!taglist.isEmpty()) {
                String uuid = u.getUuid();
                TagsSet propertySet;
                if (currentUserUUID.equals(uuid)) {
                    containsCurrentUser = true;
                    propertySet = new TagsSet(u, owners.contains(uuid), true, taglist, uris);
                } else {
                    propertySet = new TagsSet(u, owners.contains(uuid), false, taglist, uris);
                }
                tags.add(propertySet);
            }
        }
        if (!containsCurrentUser) {
            User current = c.currentAuthenticatedUser();
            tags.add(new TagsSet(current, owners.contains(current.getUuid()), true, new ArrayList<String>(), uris));
        }
        
        Collections.sort(tags);
        
        ((ScrollableTableTree) tagTree).setKeys(tags);
        
        this.entities = entities;
        return tags;
    }
    public TagsViewTopComponent() {
        initComponents();
        this.add(tagTree);
        setName(Bundle.CTL_TagsViewTopComponent());
        setToolTipText(Bundle.HINT_TagsViewTopComponent());
        global = Utilities.actionsGlobalContext().lookupResult(IEntityWrapper.class);
        global.addLookupListener(listener);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addTagComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        tagTree = new ScrollableTableTree();

        addTagComboBox.setEditable(true);
        addTagComboBox.setModel(tagComboModel);
        addTagComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTagComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(TagsViewTopComponent.class, "TagsViewTopComponent.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addTagComboBox, 0, 555, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tagTree, javax.swing.GroupLayout.PREFERRED_SIZE, 543, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addTagComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tagTree, javax.swing.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addTagComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTagComboBoxActionPerformed

        /*if (evt.getActionCommand().equals("comboBoxEdited"))
        {
            //add tag
            ConnectionProvider cp = Lookup.getDefault().lookup(ConnectionProvider.class);
            cp.getConnection().getContext(); //getContext
            String tag = addTagComboBox.getSelectedItem().toString();
            
            addTag(entities, tag);
        }*/
    }//GEN-LAST:event_addTagComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox addTagComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane tagTree;
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
