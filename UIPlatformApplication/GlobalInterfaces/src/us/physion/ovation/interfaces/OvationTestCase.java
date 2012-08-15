/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.interfaces;

import java.io.File;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;
import org.joda.time.DateTime;
import org.junit.*;
import ovation.IAuthenticatedDataStoreCoordinator;
import ovation.database.DatabaseManager;
import static org.junit.Assert.*;
import ovation.*;
import ovation.test.TestManager;
/**
 *
 * @author huecotanks
 */
public class OvationTestCase {

    public OvationTestCase() { }
    private TestManager tm;
    public IAuthenticatedDataStoreCoordinator dsc;

    public void setTestManager(TestManager mgr)
    {
	tm = mgr;
    }

    
	public static void setUpClass(TestManager tm, int defaultID) {

        File f = new File(tm.getConnectionFile());
        if (!f.exists()) {
            String lockServer = System.getProperty("OVATION_LOCK_SERVER");
            if(lockServer == null) {
		try{
		    lockServer = InetAddress.getLocalHost().getHostName();
            	} catch (java.net.UnknownHostException e){
		    lockServer = "127.0.0.1";
		}
	    }
            
            int nodeFdId = 0;
            if (System.getProperty("NODE_FDID") != null) {
                nodeFdId = Integer.parseInt(System.getProperty("NODE_FDID"));
            }

            int jobFdId = defaultID;
            if (System.getProperty("JOB_FDID") != null) {
                jobFdId = Integer.parseInt(System.getProperty("JOB_FDID"));
            }
            
            DatabaseManager.createLocalDatabase(tm.getConnectionFile(), lockServer, nodeFdId + jobFdId);
        }

    }

	public static void tearDownClass(TestManager tm) throws Exception {
        DatabaseManager.deleteLocalDatabase(tm.getConnectionFile());
    }

    @Before
	public void setUp() {
        try {
            dsc = tm.setupDatabase();
        } catch (Exception e) {
            tearDown();
            fail(e.getMessage());
        }

        Ovation.enableLogging(LogLevel.DEBUG);
    }

    @After
	public void tearDown() {
        tm.tearDownDatabase();
    }
    
}
