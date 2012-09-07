/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Edit",
id = "us.physion.ovation.browser.ResetQueryAction")
@ActionRegistration(iconBase = "us/physion/ovation/browser/reset-query.png",
displayName = "#CTL_ResetQueryAction")
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 1200),
    @ActionReference(path = "Toolbars/Find", position = 150),
    @ActionReference(path = "Shortcuts", name = "SM-R")
})
@Messages("CTL_ResetQueryAction=Reset Query")
public final class ResetQueryAction implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        BrowserUtilities.resetView();
    }
}
