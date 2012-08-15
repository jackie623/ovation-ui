/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.dbconnection;

import java.io.File;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import org.junit.*;
import static org.junit.Assert.*;
import ovation.IAuthenticatedDataStoreCoordinator;
import ovation.LogLevel;
import ovation.Ovation;
import ovation.database.DatabaseManager;
import ovation.test.TestManager;
import us.physion.ovation.interfaces.OvationTestCase;

/**
 *
 * @author huecotanks
 */
public class DatabaseConnectionProviderTest extends OvationTestCase{
    
    static TestManager mgr = new DBConnectionTestManager();
    public DatabaseConnectionProviderTest() {
        setTestManager(mgr); //this is because there are static and non-static methods that need to use the test manager
    }
    
    @BeforeClass
    public static void setUpClass()
    {
        OvationTestCase.setUpClass(mgr, 3);
    }
    
    @Before
    public void setUp() {
        super.setUp();
    }

    
    @Test
    public void testShouldRunUpdaterReturnsFalseIfUserCancels(){
        DBConnectionDialog d = new DBConnectionDialog();
        ShouldRunUpdaterDialog shouldRun = new ShouldRunUpdaterDialog();
        shouldRun.cancel();
        assertFalse(d.shouldRunUpdater(1, 2, false, shouldRun));
    }
    
    @Test
    public void testShouldRunUpdaterReturnsTrueIfUserPressesOK(){
        DBConnectionDialog d = new DBConnectionDialog();
        ShouldRunUpdaterDialog shouldRun = new ShouldRunUpdaterDialog();
        assertTrue(d.shouldRunUpdater(1, 2, false, shouldRun));
    }
     
    @Test
    public void testShouldRunUpdaterReturnsFalseIfDatabaseVersionAndAPIVersionMatch(){
        DBConnectionDialog d = new DBConnectionDialog();
        ShouldRunUpdaterDialog shouldRun = new ShouldRunUpdaterDialog();
        assertFalse(d.shouldRunUpdater(1, 1, false, shouldRun));
    }
    
    @Test
    public void testShouldRunUpdaterReturnsTrueIfDatabaseVersionIsLessThanSchemaVersion(){
        DBConnectionDialog d = new DBConnectionDialog();
        ShouldRunUpdaterDialog shouldRun = new ShouldRunUpdaterDialog();
        assertTrue(d.shouldRunUpdater(1, 3, false, shouldRun));
    }
    
    @Test
    public void testShouldRunUpdaterReturnsFalseIfDatabaseVersionIsGreaterThanSchemaVersionButAlertsUserOfThis(){
        DBConnectionDialog d = new DBConnectionDialog();
        ShouldRunUpdaterDialog shouldRun = new ShouldRunUpdaterDialog();
        assertFalse(d.shouldRunUpdater(2, 1, false, shouldRun)); 
    }
    
    @Test
    public void testRunUpdaterReturnsFalseIfCancelled()
    {   
        DBConnectionDialog d = new DBConnectionDialog();
        UpdaterInProgressDialog running = new UpdaterInProgressDialog();
        running.cancelled = true;
        assertFalse(d.runUpdater(running, false));
    }
    
    @Test 
    public void testRunUpdaterReturnsTrueIfNotCancelled()
    {
        DBConnectionDialog d = new DBConnectionDialog();
        UpdaterInProgressDialog running = new UpdaterInProgressDialog();
        assertTrue(d.runUpdater(running, false));
    }
    
    @Test
    public void testPluginDependanciesAreHandledCorrectly()
    {
        
    }
}
