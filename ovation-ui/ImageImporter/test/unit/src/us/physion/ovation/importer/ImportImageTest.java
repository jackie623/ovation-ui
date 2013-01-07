/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.importer;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import org.junit.*;
import static org.junit.Assert.*;
import ovation.LogLevel;
import ovation.Ovation;
import ovation.test.TestManager;
import us.physion.ovation.interfaces.OvationTestCase;

/**
 *
 * @author huecotanks
 */
public class ImportImageTest extends OvationTestCase{
    static TestManager mgr = new ImportTestManager();
    public ImportImageTest() {
        setTestManager(mgr); //this is because there are static and non-static methods that need to use the test manager
    }
    
    @BeforeClass
    public static void setUpClass()
    {
        Ovation.enableLogging(LogLevel.ALL);
        AccessController.doPrivileged(new PrivilegedAction<Boolean>() {

            public Boolean run() {
                OvationTestCase.setUpDatabase(mgr, 2);
                return true;
            }
        });
        
    }
    
    @Before
    public void setUp() {
        dsc = setUpTest();
    }
    
    @After
    public void tearDown()
    {
        tearDownTest();
    }
    
    @AfterClass
    public static void tearDownClass() throws Exception {
        OvationTestCase.tearDownDatabase(mgr);
    }

    @Test
    public void testSomeMethod() {
        ImportImage img = new ImportImage();
        img.getPanels(null);
    }
}
