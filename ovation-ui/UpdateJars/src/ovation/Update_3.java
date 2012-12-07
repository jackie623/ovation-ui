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
import java.net.URI;
import java.util.Set;
import java.util.HashSet;
/**
 *
 * @author huecotanks
 */

public class Update_3 extends Updater{

    IUpdateProgress pu;
    DataContext c;

    @Override
    public boolean runUpdate(DataContext c, IUpdateProgress pu)
    {
	this.pu = pu;
	this.c = c;
	System.setProperty("no.parallel.scan", "true");
	if (pu != null)
	{ 
	    update(-1, "Migrating DerivedResponses");
	}

	updateDerivedResponses();
	moveUsers();
	moveSources();

        return true;
    }

    public void moveSources()
    {
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
    }
    public void moveUsers()
    {
	Set<User> users = new HashSet<User>();
	String containerID = null;
	c.beginTransaction();
	try{
	    containerID = c.DBUtilities().getUsersAndGroupsContainer().getOid().getString();
	    Iterator<User> userItr = c.DBUtilities().getUsersAndGroupsDB().scan("ovation.User", "true");//c.getUsersIterator();
	    if (!userItr.hasNext())
	    {
		throw new OvationException("No users in the database!");
	    }
	    update(-1, "Finding Users");
	    while (userItr.hasNext())
		users.add(userItr.next());
	    
	} finally
	{
	    c.commitTransaction();
	}

	int i =0;
	for (User u: users)
	{
	    Set<URI> set = new HashSet<URI>();

	    update(-1, "Finding objects owned by user " + ++i);
	    Iterator<IEntityBase> itr = c.query(EntityBase.class, "owner.uuid == '" + u.getUuid() + "'");
	    while (itr.hasNext())
	    {
		set.add(itr.next().getURI());
	    }
	    if (set.isEmpty())
	    {
		update(-1, "Warning: no objects found for current user");
	    }

	    c.beginWriteTransaction();
	    System.out.println("About to move user");
	    try{
		if (!u.getContainer().getOid().getString().equals(containerID))
		{
		    u.move(c.DBUtilities().getUsersAndGroupsContainer());
		}
		c.commitTransaction();
	    }
	    catch (Exception e)
	    {
		update(-1, "Couldn't move users. " + e.getMessage());
		c.abortTransaction();
		throw new OvationException("Error while updating: "  + e.getMessage());
	    }
	    System.out.println("User moved successfully!");

	    // for each object in the database, transfer ownership to the new user
	    for (URI uri: set)
	    {
		update(-1, "Getting new object from URI : " + uri + " --------------------");
		IEntityBase e = c.objectWithURI(uri);
		update(-1, "Transferring ownership");
		try{
		    e.transferOwnership(u);
		    update(-1, "Transferred ownership");
		} catch (UserAccessException ex){
		    update(-1, "Warning: unable to transfer ownership of object");
		    //pass
		}
		c.beginTransaction();
		String userOid = u.getOidString();
		update(-1, "Got oid string");
		try{
		    if (!e.getOwner().getOidString().equals(userOid))
		    {
			update(-1, "User " + userOid + "' was moved, but ownership was not properly transferred");
			throw new OvationException("User '" + userOid + "' was moved, but ownership was not properly transfered");
		    }
		}finally
		{
		    c.commitTransaction();
		}
	    }  
	}
	
	update(-1, "Verifying");
	c.beginTransaction();
	try{
	    for (User u : users){
		if (!u.getContainer().getOid().getString().equals(containerID))
		{
		    throw new OvationException("Upgrade failed: Unable to move user to proper container");
		}
	    }
	} finally{
	    c.commitTransaction();
	}
    }

    public void updateDerivedResponses()
    {
	Iterator<DerivedResponse> derivedResponses = c.query(DerivedResponse.class, "true");
	if (!derivedResponses.hasNext())
	{
	    update(-1, "Warning: No DerivedResponses in the database");
	}

	Set<URI> uris = new HashSet();
	int count =1;
	while (derivedResponses.hasNext())
	    {
		update (-1, "Updating derived response " + count++);

		DerivedResponse a = derivedResponses.next();
		Epoch e = a.getEpoch();
		uris.add(e.getURI());
      		e.resetDerivedResponsesMap();//deletes the ooMap containing DerivedResponse names
		c.beginWriteTransaction();
		try{
		    a.setUTI(Response.NUMERIC_DATA_UTI);
		    c.commitTransaction();
		} catch(Exception ex)
		    {
			c.abortTransaction();
			throw new OvationException("Error while updating: "  + ex.getMessage());
		    }

		if (a.getUTI() == null)
		    throw new OvationException("Error while updating: UTI was not set properly");
	    }
	count = 1;
        for (URI uri : uris)
	{
	    Epoch e = (Epoch)c.objectWithURI(uri);
	    update(-1, "Updating epoch maps "  + count++);
	    e.repopulateDerivedResponseMap(); //calls getDerivedResponsesooMap
	}
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
