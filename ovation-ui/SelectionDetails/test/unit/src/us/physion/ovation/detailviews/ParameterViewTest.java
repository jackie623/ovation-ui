/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.joda.time.DateTime;
import org.junit.*;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;
import ovation.*;
import ovation.test.TestManager;
import us.physion.ovation.interfaces.*;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

@ServiceProvider(service = Lookup.Provider.class)
/**
 *
 * @author huecotanks
 */
public class ParameterViewTest extends OvationTestCase implements Lookup.Provider, ConnectionProvider{
        
    private Lookup l;
    InstanceContent ic;
    private TestEntityWrapper e1;
    private TestEntityWrapper e2;
    
    static TestManager mgr = new SelectionViewTestManager();
    public ParameterViewTest() {
	setTestManager(mgr); //this is because there are static and non-static methods that need to use the test manager
        ic = new InstanceContent();
        l = new AbstractLookup(ic);
    }
    
    @BeforeClass
    public static void setUpClass()
    {
        OvationTestCase.setUpDatabase(mgr, 5);
    }
    
    @Before
    public void setUp() throws UserAuthenticationException {
        dsc = setUpTest();
        Ovation.enableLogging(LogLevel.DEBUG);

        String UNUSED_NAME = "name";
        String UNUSED_PURPOSE = "purpose";
        DateTime UNUSED_START = new DateTime(0);
        byte[] data = {1, 2, 3, 4, 5};
        String uti = "unknown-uti";
        
        DataContext c = dsc.getContext();
        User newUser = c.addUser("newUser", "password");
        Project p = c.insertProject(UNUSED_NAME, UNUSED_PURPOSE, UNUSED_START);
        Source s = c.insertSource("source");
        HashMap params = new HashMap();
        params.put("color", "yellow");
        params.put("size", 10.5);
        e1 = new TestEntityWrapper(dsc, p.insertExperiment("purpose", UNUSED_START).insertEpochGroup(s, "label", UNUSED_START).insertEpoch(UNUSED_START, UNUSED_START, "protocolID", params));
        params = new HashMap();
        params.put("id", 4);
        params.put("birthday", "6/23/1988");
        e2 = new TestEntityWrapper(dsc, p.insertExperiment("purpose", UNUSED_START).insertEpochGroup(s, "label", UNUSED_START).insertEpoch(UNUSED_START, UNUSED_START, "protocolID", params));

        ic.add(this);

        Lookup.getDefault().lookup(ConnectionProvider.class);
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
    public void testGetsProperTreeNodeStructure() throws InterruptedException {
        EventQueueUtilities.runOnEDT(new Runnable() {

            @Override
            public void run() {
                Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();

                entitySet.add(e1);
                entitySet.add(e2);
                ParametersTopComponent t = new ParametersTopComponent();
                assertTrue(t.getEntities() == null || t.getEntities().isEmpty());
                t.setEntities(entitySet);

                JTree tree = t.getTableTree().getTree();
                DefaultMutableTreeNode n = (DefaultMutableTreeNode) ((DefaultTreeModel) tree.getModel()).getRoot();
                assertEquals(n.getChildCount(), 1);

                
                DefaultMutableTreeNode currentUserNode = (DefaultMutableTreeNode) n.getChildAt(0);

                assertTrue(((DefaultMutableTreeNode) currentUserNode.getChildAt(0)) instanceof TableNode);
                assertEquals(currentUserNode.getChildCount(), 1);
            }
        });

    }
    
    @Test
    public void testGetsPropertiesAppropriatelyForEachUser()
    {
        /*Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();
       
        entitySet.add(project);
        entitySet.add(source);
        PropertiesViewTopComponent tc = new PropertiesViewTopComponent();
        tc.setEntities(entitySet, dsc);
        
        DataContext c = dsc.getContext();
        ScrollableTableTree t = tc.getTableTree();
        
        //user1 properties
        Set<TestProperty> props = getProperties(t, user1.getURI());
        Set<TestProperty> databaseProps = getAggregateUserProperties(((User)user1.getEntity()), entitySet);
        assertSetsEqual(props, databaseProps);
        
        //user2 properties
        props = getProperties(t, user1.getURI());
        databaseProps = getAggregateUserProperties(((User)user1.getEntity()), entitySet);
        assertSetsEqual(props, databaseProps);*/
        
    }

    @Override
    public Lookup getLookup() {
        return l;
    }

    @Override
    public IAuthenticatedDataStoreCoordinator getConnection() {
        return dsc;
    }

    @Override
    public void addConnectionListener(ConnectionListener cl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeConnectionListener(ConnectionListener cl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

        static Set<TestProperty> getAggregateUserProperties(User u, Set<IEntityWrapper> entities) {
        
        Set<TestProperty> databaseProps = new HashSet<TestProperty>();
        for (IEntityWrapper ew : entities) {
            Map<String, Object> props = ew.getEntity().getUserProperties(u);
            for (String key : props.keySet())
            {
                databaseProps.add(new TestProperty(key, props.get(key)));
            }
        }
        return databaseProps;
    }

    void assertSetsEqual(Set s1, Set s2) {
        assertEquals(s1.size(), s2.size());
        for (Object t1 : s1)
        {
            for (Object t2 : s2)
            {
                if (t1.equals(t2))
                {
                    s2.remove(t2);
                    break;
                }
            }    
                
        }
        assertTrue(s2.isEmpty());
        //assertTrue(s1.containsAll(s2));
    }
    
    Set<TestProperty> getPropertiesByKey(String key, Set<TestProperty> props)
    {
        Set<TestProperty> result = new HashSet<TestProperty>();
        for (TestProperty p : props)
        {
            if (p.getKey().equals(key))
            {
                result.add(p);
            }
        }
        return result;
    }
    
    private Set<TestProperty> getProperties(ScrollableTableTree t, String userURI) {
        Set<TestProperty> properties = new HashSet<TestProperty>();
        TableTreeKey k = t.getTableKey(userURI);
        if (k == null)
        {
            return properties;
        }    
         
        Object[][] data = k.getData();
        //DefaultTableModel m = ((DefaultTableModel) ((TableInTreeCellRenderer) t.getTree().getCellRenderer()).getTableModel(k));
        for (int i  = 0; i < data.length; ++i)
        {
            properties.add(new TestProperty((String) data[i][0], data[i][1]));
        }
        return properties;
    }

    @Override
    public void resetConnection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
