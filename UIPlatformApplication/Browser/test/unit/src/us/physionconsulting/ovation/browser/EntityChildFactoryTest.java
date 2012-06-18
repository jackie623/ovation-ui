/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physionconsulting.ovation.browser;

import java.util.HashMap;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;
import org.openide.nodes.Node;

/**
 *
 * @author huecotanks
 */
public class EntityChildFactoryTest {
    
    public EntityChildFactoryTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of createKeys method, of class EntityChildFactory.
     */
    @Test
    public void testCreateKeys() {
        System.out.println("createKeys");
        List<EntityWrapper> list = null;
        EntityChildFactory instance= null; //= new EntityFactory(null, new HashMap<String, Node>());
        boolean expResult = false;
        boolean result = instance.createKeys(list);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createKeysForEntity method, of class EntityChildFactory.
     */
    @Test
    public void testCreateKeysForEntity() {
        System.out.println("createKeysForEntity");
        EntityWrapper ew = null;
        List<EntityWrapper> list = null;
        EntityChildFactory instance = null;
        boolean expResult = false;
        boolean result = instance.createKeysForEntity(ew, list);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createNodeForKey method, of class EntityChildFactory.
     */
    @Test
    public void testCreateNodeForKey() {
        System.out.println("createNodeForKey");
        EntityWrapper key = null;
        EntityChildFactory instance = null;
        Node expResult = null;
        Node result = instance.createNodeForKey(key);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
