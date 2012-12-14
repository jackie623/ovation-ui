/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser.insertion;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;

public class InsertProjectWizardPanel1 extends BasicWizardPanel{

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public InsertProjectVisualPanel1 getComponent() {
        if (component == null) {
            component = new InsertProjectVisualPanel1(changeSupport);
        }
        return (InsertProjectVisualPanel1)component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        InsertProjectVisualPanel1 c = (InsertProjectVisualPanel1)component;
        if (c != null)
        {
           return (c.getProjectName() != null && !c.getProjectName().isEmpty() 
                   && c.getProjectPurpose() != null && !c.getProjectPurpose().isEmpty() );
                   //&& component.getStart() != null);
        }
        return false;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }
    
    public String getName()
    {
        return "Project Data";
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        InsertProjectVisualPanel1 c = (InsertProjectVisualPanel1)component;
        wiz.putProperty("project.name", c.getProjectName());
        wiz.putProperty("project.purpose", c.getProjectPurpose());
        wiz.putProperty("project.start", c.getStart());
        wiz.putProperty("project.end", c.getEnd());
        // use wiz.putProperty to remember current panel state
    }
}
