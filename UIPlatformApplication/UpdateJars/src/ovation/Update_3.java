/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ovation;

import us.physion.ovation.interfaces.IUpdateProgress;
import ovation.Updater;
import java.rmi.RemoteException;
import java.lang.InterruptedException;
import ovation.Updater;
/**
 *
 * @author huecotanks
 */

public class Update_3 extends Updater{

    @Override
    public boolean runUpdate(DataContext c, IUpdateProgress pu)
    {
        //moves all the users into one container
        //
        c.getProjects();
	try{
	    pu.update(20, "Running part 1");
	    Thread.sleep(2000);
	    pu.update(40, "Running part 2");
	    Thread.sleep(3000);
	    pu.update(60, "Running part 4");
	    Thread.sleep(3000);
	    pu.update(80, "Running part 5");
	    Thread.sleep(3000);
	    pu.update(100, "Done running test");
	} catch (RemoteException e){
	    throw new OvationException(e.getMessage());
	} catch (InterruptedException e){
	    throw new OvationException(e.getMessage());
	}
        System.out.println("Running");
        return true;
    }

    public static void main(String[] args)
    {
	Update_3 update = new Update_3();
	update.runMain(args);
    }
}
