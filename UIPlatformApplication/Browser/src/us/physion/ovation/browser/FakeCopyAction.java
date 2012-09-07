/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.explorer.ExplorerManager;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import us.physion.ovation.interfaces.IEntityWrapper;


public final class FakeCopyAction implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        Lookup.Result global = Utilities.actionsGlobalContext().lookupResult(IEntityWrapper.class);
        Collection<? extends IEntityWrapper> entities = global.allInstances();
        String selection = "";
        if (entities.size() == 1)
        {
            selection += entities.iterator().next().getURI();
        }
        else{
            for (IEntityWrapper ew : entities)
            {
                selection += ew.getURI() + "\n"; 
            }
        }
        System.out.println("Selection: "+ selection);
        StringSelection data = new StringSelection(selection);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(data, data);
    }
}
