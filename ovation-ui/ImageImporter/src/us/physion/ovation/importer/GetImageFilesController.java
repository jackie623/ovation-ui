/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.importer;

import java.awt.Component;
import java.io.File;
import java.util.List;
import java.util.Set;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import us.physion.ovation.interfaces.BasicWizardPanel;

/**
 *
 * @author huecotanks
 */
public class GetImageFilesController extends BasicWizardPanel{

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new GetImageFilesPanel(changeSupport);
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
        GetImageFilesPanel c = (GetImageFilesPanel)component;
        if (c != null)
        {
            List<File> files = c.getFiles();
            return files.size() != 0;
        }
        return false;
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        GetImageFilesPanel c = (GetImageFilesPanel)component;
        wiz.putProperty("epoch.files", c.getFiles());
        // use wiz.putProperty to remember current panel state
    }
}
