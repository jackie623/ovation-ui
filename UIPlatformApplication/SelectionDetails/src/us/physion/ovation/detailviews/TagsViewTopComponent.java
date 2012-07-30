/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import java.awt.event.ActionEvent;
import java.util.*;
import java.util.concurrent.FutureTask;
import javax.swing.AbstractListModel;
import javax.swing.DefaultComboBoxModel;
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
import ovation.IEntityBase;
import ovation.ITaggableEntityBase;
import ovation.KeywordTag;
import us.physion.ovation.interfaces.ConnectionProvider;
import us.physion.ovation.interfaces.IEntityWrapper;

/**
 * Top component which displays something.
 */
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
    "CTL_TagsViewAction=TagsView",
    "CTL_TagsViewTopComponent=Keyword Tags",
    "HINT_TagsViewTopComponent=This is a Keyword Tags window"
})
public final class TagsViewTopComponent extends TopComponent {

    private DefaultComboBoxModel tagComboModel = new DefaultComboBoxModel(new String[] {});
    Lookup.Result global;
    private Collection<? extends IEntityWrapper> entities;
    private StringListModel listModel = new StringListModel();
    private LookupListener listener = new LookupListener() {

        @Override
        public void resultChanged(LookupEvent le) {

            //TODO: we should have some other Interface for things that can update the tags view
            //then we could get rid of the Library dependancy on the Explorer API
            if (TopComponent.getRegistry().getActivated() instanceof ExplorerManager.Provider)
            {
                updateListModel();
            }
        }

    };

    private class StringListModel extends AbstractListModel
    {
        List<String> tags = new LinkedList<String>();

        @Override
        public int getSize() {
            return tags.size();
        }

        @Override
        public Object getElementAt(int i) {
            if (i < tags.size())
                return tags.get(i);
            return null;
        }

        protected void setTags(List<String> newTags)
        {
            int length = Math.max(tags.size(), newTags.size());
            tags = newTags;
            this.fireContentsChanged(this, 0, length);
        }

        protected void addTag(String tag)
        {
            tags.add(tag);
            this.fireContentsChanged(this, tags.size(), tags.size());
        }
    };



    protected void updateListModel()
    {
        entities = global.allInstances();
        if (entities.isEmpty())
            {
                System.out.println("Nothing selected");
            }
            else{
                for (IEntityWrapper e : entities)
                {
                    System.out.println(e.getType().toString() + " is selected");
                }
            }
        ConnectionProvider cp = Lookup.getDefault().lookup(ConnectionProvider.class);
        cp.getConnection().getContext(); //getContext
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
                if (entity instanceof ovation.Experiment)
                {
                    tags.add("Experiment");
                }
                if (entity instanceof ovation.EpochGroup)
                {
                    tags.add("EpochGroup");
                }
                if (entity instanceof ovation.Project)
                {
                    tags.add("Project");
                }
                if (entity instanceof ovation.Epoch)
                {
                    tags.add("Epoch");
                }
            }
        }

        listModel.setTags(tags);

    }
    public TagsViewTopComponent() {
        initComponents();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();

        addTagComboBox.setEditable(true);
        addTagComboBox.setModel(tagComboModel);
        addTagComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTagComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(TagsViewTopComponent.class, "TagsViewTopComponent.jLabel1.text")); // NOI18N

        jList1.setModel(listModel);
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(addTagComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 689, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addTagComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addTagComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTagComboBoxActionPerformed

        System.out.println("Combo box action performed");
        if (evt.getActionCommand().equals("comboBoxEdited"))
        {
            System.out.println("Combo  box edited");
            //add tag
            ConnectionProvider cp = Lookup.getDefault().lookup(ConnectionProvider.class);
            cp.getConnection().getContext(); //getContext
            String tag = addTagComboBox.getSelectedItem().toString();
            for (IEntityWrapper e : entities)
            {

                IEntityBase ie = e.getEntity();
                if (ie instanceof ITaggableEntityBase)
                {
                    ((ITaggableEntityBase)ie).addTag(tag);
                }
            }
            listModel.addTag(tag);
            tagComboModel.removeAllElements();
            addTagComboBox.setSelectedItem("");
            addTagComboBox.setSelectedItem(null);
        }
    }//GEN-LAST:event_addTagComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox addTagComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList jList1;
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
