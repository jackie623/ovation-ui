package us.physion.ovation.interfaces;

import ovation.IAuthenticatedDataStoreCoordinator;
import ovation.DataStoreCoordinator;
import ovation.UserAuthenticationException;
import ovation.OvationException;
import ovation.DataContext;
import com.objy.db.DatabaseOpenException;
import com.objy.db.DatabaseNotFoundException;
import ovation.Ovation;
import java.rmi.Naming;

public class Updater {
    public static boolean runUpdate(IAuthenticatedDataStoreCoordinator dsc, IUpdateProgress pu)
    {
	throw new UnsupportedOperationException("Subclasses of the Updater should override the runUpdate method");
    }
    
    public static void main(String[] args)
    {
	if (args.length !=3) 
	{
	    System.out.println("Usage: java -jar <connectionfile> <username> <password>");
	    return;
	}

	DataStoreCoordinator c = null;
        try {
            c = DataStoreCoordinator.coordinatorWithConnectionFile(args[0]);
	    c.getContext().authenticateUser(args[1], args[2]);
        }catch (DatabaseOpenException e){
	    Ovation.getLogger().debug(e.getMessage());
	    throw new OvationException(e.getMessage(), e);
	} catch (DatabaseNotFoundException e){
	    Ovation.getLogger().debug(e.getMessage());
	    throw new OvationException(e.getMessage(), e);
	}
	catch (UserAuthenticationException e) {
	    Ovation.getLogger().debug(e.getMessage());
            throw new OvationException(e.getMessage(), e);
        }

        IUpdateProgress pu = null;
	try {                                                                                                                                                                                                                                                                                  
	     pu = (IUpdateProgress) Naming.lookup("rmi://localhost:10001/ProgressUpdater");                                                                                                                                                                                                     
	} catch (Exception e) {                                                                                                                                                                                                                                                                
	     pu = null;                                                                                                                                                                                                                                                                         
	}

	runUpdate(c.getContext().getAuthenticatedDataStoreCoordinator(), pu);
    }
    
}