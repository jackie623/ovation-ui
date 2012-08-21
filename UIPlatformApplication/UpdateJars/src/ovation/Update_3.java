/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ovation;

import us.physion.ovation.interfaces.IUpdateProgress;
import java.rmi.RemoteException;
import java.lang.InterruptedException;
import ovation.*;
import com.objy.db.app.*;
import java.util.Iterator;
/**
 *
 * @author huecotanks
 */

public class Update_3 extends Updater{

    @Override
    public boolean runUpdate(DataContext c, IUpdateProgress pu)
    {
	if (pu != null)
	{ 
	    try {
		pu.update(-1, "Migrating DerivedResponses");
	    } catch (RemoteException e) {
		//pass                                                                                                                                                                                                                                                                       
	    }
	}

	Iterator<DerivedResponse> derivedResponses = c.query(DerivedResponse.class, "true");
	while (derivedResponses.hasNext())
	    {
		
		DerivedResponse a = derivedResponses.next();

		c.beginTransaction();
		try{
		    a.setUTI(Response.NUMERIC_DATA_UTI);
		    c.commitTransaction();
		} catch(Exception e)
		    {
			c.abortTransaction();
			throw new OvationException("Error while updating: "  + e.getMessage());
		    }

		if (a.getUTI() == null)
		    throw new OvationException("Error while updating: UTI was not set properly");
	    }

        //moves all the users into one container
        //
	Iterator<User> users = c.getUsersIterator();
	int i = 0;
	
	while (users.hasNext()){

	    if (pu != null){
		try{
		    pu.update(i++, "Moving Users");
		} catch (RemoteException e)
		    {
			//pass
		    }
	    }

	    c.beginTransaction();
	    User u = users.next();
	    
	    String containerID = c.DBUtilities().getUsersAndGroupsContainer().getOid().getString();
	    try{
		if (!u.getContainer().getOid().getString().equals(containerID))
		{
		    u.move(c.DBUtilities().getUsersAndGroupsContainer());
		}
		c.commitTransaction();
	    }
	    catch (Exception e)
	    {
		c.abortTransaction();
		throw new OvationException("Error while updating: "  + e.getMessage());
	    }
	    
	}
	
	if (pu != null)
	{

	    try{
		pu.update(-1, "Moving Sources");
	    } catch (RemoteException e)
		{
		    //pass
		}
	}

	Iterator<Source> sources = c.query(c.DBUtilities().getSourcesDB(), Source.class, "true");
	while (sources.hasNext()){

	    c.beginTransaction();
	    Source u = sources.next();

	    String containerID = c.DBUtilities().getSourcesContainer().getOid().getString();
	    try{
		if (!u.getContainer().getOid().getString().equals(containerID))
		{
		    u.move(c.DBUtilities().getSourcesContainer());
		}
		c.commitTransaction();
	    }
	    catch (Exception e)
	    {
		c.abortTransaction();
		throw new OvationException("Error while updating: "  + e.getMessage());
	    }
	    
	}
	
        return true;
    }

    public static void main(String[] args)
    {
	Update_3 update = new Update_3();

	try{
	    update.runMain(args);
	} catch (Exception e)
	{
	    System.err.println(e.getMessage());
	    
	    System.exit(1);
	}
	System.exit(0);
    }
}
