/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ovation;

import us.physion.ovation.interfaces.IUpdateProgress;
import us.physion.ovation.interfaces.Updater;

/**
 *
 * @author huecotanks
 */

public class Update_3 extends Updater{
    public static boolean runUpdate(IAuthenticatedDataStoreCoordinator dsc, IUpdateProgress pu)
    {
        //moves all the users into one container
        //
        DataContext c  = dsc.getContext();
        System.out.println("Running");
        return true;
    }
    
}
