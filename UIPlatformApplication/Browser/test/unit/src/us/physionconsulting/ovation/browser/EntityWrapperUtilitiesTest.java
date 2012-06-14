/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physionconsulting.ovation.browser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.junit.*;
import static org.junit.Assert.*;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;
import ovation.*;

/**
 *
 * @author jackie
 */
public class EntityWrapperUtilitiesTest {
    
    public EntityWrapperUtilitiesTest() {
    }
    
    ExplorerManager em;
    Map<String, Node> treeMap;
    DataContext ctx;
    

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() throws UserAuthenticationException {
        ctx = Ovation.connect("/Users/jackie/test-db.boot", "TestUser", "password");
        treeMap = new HashMap<String, Node>();
        em = new ExplorerManager();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of expandNodesFromQuery method, of class EntityWrapperUtilities.
     */
    @Test
    public void testExpandNodesFromQuery() {
        System.out.println("expandNodesFromQuery");
        Map<String, Node> treeMap = null;
        Iterator<IEntityBase> itr = null;
        BeanTreeView btv = null;
        ExplorerManager mgr = null;
        EntityWrapperUtilities.expandNodesFromQuery(treeMap, itr, btv, mgr);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParentInTree method, of class EntityWrapperUtilities.
     */
    @Test
    public void testGetParentInTree() {
        System.out.println("getParentInTree");
        IEntityBase e = null;
        Map<String, Node> treeMap = null;
        String path = "";
        String expResult = "";
        String result = EntityWrapperUtilities.getParentInTree(e, treeMap, path);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
