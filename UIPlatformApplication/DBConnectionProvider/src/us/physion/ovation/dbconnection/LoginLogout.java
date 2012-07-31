/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.dbconnection;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter.Toolbar;
import us.physion.ovation.interfaces.ConnectionProvider;

@ActionID(category = "Profile",
id = "us.physion.ovation.dbconnection.LoginLogout")
@ActionRegistration(iconBase = "us/physion/ovation/dbconnection/loginimage.png",
displayName = "#CTL_LoginLogout")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1300),
    @ActionReference(path = "Shortcuts", name = "M-L")
})
@Messages("CTL_LoginLogout=Login/Logout")
public final class LoginLogout extends AbstractAction implements ActionListener{

    public void actionPerformed(ActionEvent e) {
        ConnectionProvider cp = Lookup.getDefault().lookup(ConnectionProvider.class);
        if (cp == null)
        {
            cp = new DatabaseConnectionProvider();
            cp.getConnection();
        }else {
            cp.resetConnection();
        }
    }

    /*public Component getToolbarPresenter() {
        new DatabaseConnectionProvider();
        return new JPanel();
    }*/
}
