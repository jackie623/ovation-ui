/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser.insertion;

import javax.swing.JPanel;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.HelpCtx;
import ovation.Source;
import us.physion.ovation.interfaces.IEntityWrapper;

/**
 *
 * @author huecotanks
 */
class InsertSourceWizardPanel1 extends BasicWizardPanel
{
    @Override
    public JPanel getComponent() {
        if (component == null) {
            component = new InsertSourceVisualPanel1(changeSupport);
        }
        return component;
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
        String label = ((InsertSourceVisualPanel1)component).getLabel();
        return (label != null && !label.isEmpty());
    }


    @Override
    public void storeSettings(WizardDescriptor wiz) {
        String label = ((InsertSourceVisualPanel1)component).getLabel();
        wiz.putProperty("source.label", label);
    }
}
