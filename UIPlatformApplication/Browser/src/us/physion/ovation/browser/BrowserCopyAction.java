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
import org.openide.util.Lookup;
import us.physion.ovation.interfaces.IEntityWrapper;

/**
 *
 * @author huecotanks
 */
public class BrowserCopyAction extends CopyAction{
    @Override
    public void actionPerformed(ActionEvent e)
    {
        Collection<? extends IEntityWrapper> entities = Lookup.getDefault().lookupAll(IEntityWrapper.class);
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
