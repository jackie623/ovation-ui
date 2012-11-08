/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import java.util.*;
import javax.swing.table.DefaultTableModel;
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
import ovation.*;
import us.physion.ovation.interfaces.EventQueueUtilities;
import us.physion.ovation.interfaces.IEntityWrapper;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//us.physion.ovation.detailviews//Parameters//EN",
autostore = false)
@TopComponent.Description(preferredID = "ParametersTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE", 
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "leftSlidingSide", openAtStartup = true)
@ActionID(category = "Window", id = "us.physion.ovation.detailviews.ParametersTopComponent")
@ActionReference(path = "Menu/Window" /*
 * , position = 333
 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_ParametersAction",
preferredID = "ParametersTopComponent")
@Messages({
    "CTL_ParametersAction=Parameters",
    "CTL_ParametersTopComponent=Parameters Window",
    "HINT_ParametersTopComponent=This is a Parameters window"
})
public final class ParametersTopComponent extends TopComponent {

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
    
    public void update(final Collection<? extends IEntityWrapper> entities)
    {
        this.entities = entities;

        Map<String, Map<String, Object>> tables = new HashMap<String, Map<String, Object>>();
        for (IEntityWrapper ew : entities)
        {
            IEntityBase eb = ew.getEntity();
            if (eb instanceof Epoch)
            {
                String paramName = "Protocol Parameters";
                Map<String, Object> params = tables.get(paramName);
                if (params == null)
                {
                    params = ((Epoch)eb).getProtocolParameters();
                }else{
                    params.putAll(((Epoch)eb).getProtocolParameters());
                }
                tables.put(paramName, params);
            }
            if (eb instanceof Stimulus)
            {
                String paramName = "Stimulus Parameters";
                Map<String, Object> params = tables.get(paramName);
                if (params == null)
                {
                    params = ((Stimulus)eb).getStimulusParameters();
                }else{
                    params.putAll(((Stimulus)eb).getStimulusParameters());
                }
                tables.put(paramName, params);
            }
            if (eb instanceof IIOBase)
            {
                String paramName = "Device Parameters";
                Map<String, Object> params = tables.get(paramName);
                if (params == null)
                {
                    params = ((IIOBase)eb).getDeviceParameters();
                }else{
                    params.putAll(((IIOBase)eb).getDeviceParameters());
                }
                tables.put(paramName, params);
            }
            if (eb instanceof AnalysisRecord)
            {
                String paramName = "Analysis Parameters";
                Map<String, Object> params = tables.get(paramName);
                if (params == null)
                {
                    params = ((AnalysisRecord)eb).getAnalysisParameters();
                }else{
                    params.putAll(((AnalysisRecord)eb).getAnalysisParameters());
                }
                tables.put(paramName, params);
            }
        }
        ArrayList<TableTreeKey> tableKeys = new ArrayList<TableTreeKey>();
        for (String key: tables.keySet())
        {
            tableKeys.add(new ParameterSet(key, tables.get(key)));
        }
        ((TreeWithTableRenderer)jScrollPane2).setKeys(tableKeys);
        
        /*ArrayList<String> keys = new ArrayList();
        ArrayList<Object> values = new ArrayList();
        for (String tableName: tables.keySet())
        {
            Map<String, Object> m = tables.get(tableName);
            for (String key : m.keySet())
            {
                keys.add(key);
                values.add(m.get(key));
            }
        }
        Object[][] v = new Object[keys.size()][2];
        for (int i =0; i< keys.size(); i++)
        {
            v[i][0] = keys.get(i);
            v[i][1] = values.get(i);
        }*/
        //jTable1.setModel(new DefaultTableModel(v, new String[]{"Name", "Parameter"}));
    }
    
    
    private DefaultTableModel tableModel;
    public ParametersTopComponent() {
        tableModel = new DefaultTableModel( new Object[0][0], new String[]{ "Name", "Parameter" });
        initComponents();
        setName(Bundle.CTL_ParametersTopComponent());
        setToolTipText(Bundle.HINT_ParametersTopComponent());
        
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

        jScrollPane2 = new TreeWithTableRenderer();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 678, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 711, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
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
