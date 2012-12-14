/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser.insertion;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import org.openide.WizardDescriptor;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import ovation.*;
import us.physion.ovation.browser.moveme.*;
import us.physion.ovation.interfaces.IEntityWrapper;

@ServiceProviders(value={
    @ServiceProvider(service=SourceInsertable.class),
    @ServiceProvider(service=EpochGroupInsertable.class),
    @ServiceProvider(service=ExperimentInsertable.class),
    @ServiceProvider(service=RootInsertable.class)
})
/**
 *
 * @author huecotanks
 */
public class InsertSource extends InsertEntity implements SourceInsertable, EpochGroupInsertable, ExperimentInsertable, RootInsertable
{
    public InsertSource() {
        putValue(NAME, "Insert Source...");
    }

    public List<WizardDescriptor.Panel<WizardDescriptor>> getPanels()
    {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new InsertAnalysisRecordWizardPanel1());
        panels.add(new InsertAnalysisRecordWizardPanel2());
        panels.add(new InsertAnalysisRecordWizardPanel3()); 
        return panels;
    }

    @Override
    public void wizardFinished(WizardDescriptor wiz, IAuthenticatedDataStoreCoordinator dsc, IEntityWrapper parent)
    {
        ((Source)parent.getEntity()).insertSource(((String)wiz.getProperty("Source.label")));
    }
}
