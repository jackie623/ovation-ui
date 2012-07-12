/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.dbconnection;

import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;
import us.physion.ovation.interfaces.ConnectionProvider;

public class Installer extends ModuleInstall {

    private ConnectionProvider dbc;
    @Override
    public synchronized void restored() {
        
        if (dbc == null)
            dbc = new DBConnectionProvider();
    }
}
