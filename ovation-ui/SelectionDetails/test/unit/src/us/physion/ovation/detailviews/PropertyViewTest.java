/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.joda.time.DateTime;
import org.junit.*;
import static org.junit.Assert.*;
import ovation.*;
import ovation.test.TestManager;
import us.physion.ovation.interfaces.IEntityWrapper;
import us.physion.ovation.interfaces.OvationTestCase;
import us.physion.ovation.interfaces.TestEntityWrapper;

/**
 *
 * @author huecotanks
 */
public class PropertyViewTest extends OvationTestCase{
    
    private TestEntityWrapper project;
    private TestEntityWrapper source;
    
    static TestManager mgr = new SelectionViewTestManager();
    public PropertyViewTest() {
	setTestManager(mgr); //this is because there are static and non-static methods that need to use the test manager
    }
    
    @BeforeClass
    public static void setUpClass()
    {
        OvationTestCase.setUpDatabase(mgr, 5);
    }
    
    @Before
    public void setUp() throws UserAuthenticationException {
        dsc = setUpTest();

        String UNUSED_NAME = "name";
        String UNUSED_PURPOSE = "purpose";
        DateTime UNUSED_START = new DateTime(0);
        byte[] data = {1, 2, 3, 4, 5};
        String uti = "unknown-uti";
        
        DataContext c = dsc.getContext();
        c.addUser("newUser", "password");
        project = new TestEntityWrapper(dsc, c.insertProject(UNUSED_NAME, UNUSED_PURPOSE, UNUSED_START));
        source = new TestEntityWrapper(dsc, c.insertSource("source"));
        Project p = (Project)project.getEntity();
        p.addProperty("color", "yellow");
        p.addProperty("size", 10.5);
        Source s = (Source)source.getEntity();
        s.addProperty("id", 4);
        s.addProperty("birthday", "6/23/1988");
        
        c.authenticateUser("newUser", "password");
        p.addProperty("color", "chartreuse");
        p.addProperty("interesting", true);
       
        
        Ovation.enableLogging(LogLevel.DEBUG);
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
    public void testGetsPropertiesByDifferentUsers()
    {
        Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();
        
        entitySet.add(project);
        entitySet.add(source);
        PropertiesViewTopComponent t = new PropertiesViewTopComponent();
        assertTrue( t.getEntities().isEmpty());
        t.update(entitySet);
    }
    
    @Test
    public void testSetsPropertiesByDifferentUsers()
    {
        Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();
        
        entitySet.add(project);
        entitySet.add(source);
        PropertiesViewTopComponent t = new PropertiesViewTopComponent();
        assertTrue( t.getEntities().isEmpty());
        t.update(entitySet);
    }  
    
    @Test
    public void testCantEditOtherUsersProperty()
    {
        Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();
        
        entitySet.add(project);
        entitySet.add(source);
        PropertiesViewTopComponent t = new PropertiesViewTopComponent();
        assertTrue( t.getEntities().isEmpty());
        t.update(entitySet);
    }
    
    @Test
    public void testCurrentUserNodeIsFirst()
    {
        Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();
        
        entitySet.add(project);
        entitySet.add(source);
        PropertiesViewTopComponent t = new PropertiesViewTopComponent();
        assertTrue( t.getEntities().isEmpty());
        t.update(entitySet);
    } 
    
    @Test
    public void testAddPropertyToEntity()
    {
        Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();
        
        entitySet.add(project);
        entitySet.add(source);
        PropertiesViewTopComponent t = new PropertiesViewTopComponent();
        assertTrue( t.getEntities().isEmpty());
        t.update(entitySet);
    } 
    
    @Test
    public void testAddPropertiesToMultipleEntities()
    {
        Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();
        
        entitySet.add(project);
        entitySet.add(source);
        PropertiesViewTopComponent t = new PropertiesViewTopComponent();
        assertTrue( t.getEntities().isEmpty());
        t.update(entitySet);
    } 
    
    @Test
    public void testAddPropertyToMultipleEntitiesEvenWhenOneEntityAlreadyHasProperty()
    {
        Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();
        
        entitySet.add(project);
        entitySet.add(source);
        PropertiesViewTopComponent t = new PropertiesViewTopComponent();
        assertTrue( t.getEntities().isEmpty());
        t.update(entitySet);
    } 
    
    @Test
    public void testRemovePropertyFromEntity()
    {
        Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();
        
        entitySet.add(project);
        entitySet.add(source);
        PropertiesViewTopComponent t = new PropertiesViewTopComponent();
        assertTrue( t.getEntities().isEmpty());
        t.update(entitySet);
    } 
    
    @Test
    public void testRemovePropertiesFromMultipleEntities()
    {
        Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();
        
        entitySet.add(project);
        entitySet.add(source);
        PropertiesViewTopComponent t = new PropertiesViewTopComponent();
        assertTrue( t.getEntities().isEmpty());
        t.update(entitySet);
    } 
    
    @Test
    public void testRemovePropertyFromMutlipleEntitesPropertyDidNotExistOnOneEntity()
    {
        Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();
        
        entitySet.add(project);
        entitySet.add(source);
        PropertiesViewTopComponent t = new PropertiesViewTopComponent();
        assertTrue( t.getEntities().isEmpty());
        t.update(entitySet);
    } 
    
    
}
