/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import java.io.File;
import java.net.InetAddress;
import java.util.HashMap;
import org.junit.*;
import static org.junit.Assert.*;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import ovation.IAuthenticatedDataStoreCoordinator;
import ovation.database.DatabaseManager;

/**
 *
 * @author huecotanks
 */
public class ResourceViewTopComponentTest {
    
    public ResourceViewTopComponentTest() {
    }

    static SelectionViewTestManager tm = new SelectionViewTestManager();
    IAuthenticatedDataStoreCoordinator dsc;
    @BeforeClass
    public static void setUpClass() throws Exception {

        File f = new File(tm.getConnectionFile());
        if (!f.exists()) {
            DatabaseManager db = new DatabaseManager();
            String lockServer = System.getProperty("OVATION_LOCK_SERVER");
            if(lockServer == null) {
                lockServer = InetAddress.getLocalHost().getHostName();
            }
            
            int nodeFdId = 0;
            if (System.getProperty("NODE_FDID") != null) {
                nodeFdId = Integer.parseInt(System.getProperty("NODE_FDID"));
            }

            int jobFdId = 4;
            if (System.getProperty("JOB_FDID") != null) {
                jobFdId = Integer.parseInt(System.getProperty("JOB_FDID"));
            }
            
            db.createLocalDatabase(tm.getConnectionFile(), lockServer, nodeFdId + jobFdId);
        }

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        DatabaseManager db = new DatabaseManager();
        db.deleteLocalDatabase(tm.getConnectionFile());
    }

    @Before
    public void setUp() {
        try {
            dsc = tm.setupDatabase();
        } catch (Exception e) {
            tearDown();
            fail(e.getMessage());
        }

    }

    @After
    public void tearDown() {
        tm.tearDownDatabase();
    }
}
