/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.Collection;
import org.openide.actions.CopyAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import us.physion.ovation.interfaces.IEntityWrapper;

/**
 *
 * @author huecotanks
 */
@ActionID(category = "Edit",
id = "us.physion.ovation.browser.FakeCopyAction")
@ActionRegistration(displayName = "#CTL_FakeCopyAction")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1200),
    @ActionReference(path = "Shortcuts", name = "M-C")
})
@NbBundle.Messages("CTL_FakeCopyAction=Copy Entity")
public class BrowserCopyAction extends CopyAction{
    @Override
    public void actionPerformed(ActionEvent e)
    {
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
