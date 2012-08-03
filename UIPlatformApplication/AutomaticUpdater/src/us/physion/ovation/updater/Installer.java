/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.updater;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.swing.SwingUtilities;
import org.netbeans.api.autoupdate.*;
import org.netbeans.api.autoupdate.OperationContainer.OperationInfo;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable () {
            public void run () {
              RequestProcessor.getDefault ().post (doCheck, 10000);
            }
        });
    }
    
    private static Runnable doCheck = new Runnable () {
        public void run() {
            if (SwingUtilities.isEventDispatchThread ()) {
                RequestProcessor.getDefault ().post (doCheck);
                return ;
            }
            /*List<UpdateUnitProvider> providers = UpdateUnitProviderFactory.getDefault().getUpdateUnitProviders(false);
            for (UpdateUnitProvider prov : providers)
            {
                System.out.println("Provider: '" + prov.getProviderURL().toExternalForm() + "' is enabled: " + prov.isEnabled());
                List<UpdateUnit> units = prov.getUpdateUnits();
                
                for (UpdateUnit u: units)
                {
                    if (u.getCodeName().startsWith("us.physion"))
                    {
                        if (u.getInstalled() != null)
                            System.out.println(u.getInstalled().getSpecificationVersion());
                        
                        
                        List<UpdateElement> elements = u.getAvailableUpdates();
                        if (elements.isEmpty())
                        {
                            System.out.println("No updates for " + u.getCodeName());
                        }
                    }
                }
            }*/
            
            UpdateManager.getDefault ().getUpdateUnits (); //this makes the status bar alert users to the new updates
        }
    };
    
}
