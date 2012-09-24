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

    IUpdateProgress pu;
    @Override
    public boolean runUpdate(DataContext c, IUpdateProgress pu)
    {
	this.pu = pu;
	System.setProperty("no.parallel.scan", "true");
	if (pu != null)
	{ 
	    update(-1, "Migrating DerivedResponses");
	}

	Iterator<DerivedResponse> derivedResponses = c.query(DerivedResponse.class, "true");
	if (!derivedResponses.hasNext())
	{
	    update(-1, "Warning: No DerivedResponses in the database");
	}
	
	int count =1;
	while (derivedResponses.hasNext())
	    {
		update ((count++)%99, "Updating derived response " + count);

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
	c.beginTransaction();
	try{
	    Iterator<User> users = c.DBUtilities().getUsersAndGroupsDB().scan("ovation.User", "true");//c.getUsersIterator();
	    int i = 0;
	if (!users.hasNext())
	{
	    throw new OvationException("No Users in the database!");
	}
	
	while (users.hasNext()){
	    
	    update(i++, "Moving Users");

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
	c.commitTransaction();
	} catch (Exception e)
	{
	    c.abortTransaction();
	    throw new OvationException(e.getMessage(), e);
	}

	c.beginTransaction();
	try{
	    Iterator<User> users = c.DBUtilities().getUsersAndGroupsDB().scan("ovation.User", "true");//c.getUsersIterator();                                                                                                                                                                    
	    String containerID = c.DBUtilities().getUsersAndGroupsContainer().getOid().getString();
	    while (users.hasNext()){
		User u = users.next();
		if (!u.getContainer().getOid().getString().equals(containerID))
		{
		    throw new OvationException("Update failed: Unable to move user to proper container");
		}
	    }
	} finally{
	    c.commitTransaction();
	}
	
	
	update(-1, "Moving Sources");

	Iterator<Source> sources = c.query(c.DBUtilities().getSourcesDB(), Source.class, "true");
	if (!sources.hasNext())
	    {
		throw new OvationException("Update failed: No sources in the database!");
	    }
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
	    
	    c.beginTransaction();
	    try{
		if (!u.getContainer().getOid().getString().equals(containerID))
		    {
			throw new OvationException("Update failed: Unable to move source to proper container");
		    }
	    } finally{
		c.commitTransaction();
	    }

	}
	
        return true;
    }


    public void update(int i, String message)
    {
        if (pu != null)
	{

	    try{
		pu.update(i, message);
	    } catch (RemoteException e)
		{
		    //pass
		}
	}
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
