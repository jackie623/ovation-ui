/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import java.util.*;
import javax.swing.JTree;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import org.joda.time.DateTime;
import org.junit.*;
import static org.junit.Assert.*;
import ovation.*;
import ovation.test.TestManager;
import us.physion.ovation.detailviews.TreeWithTableRenderer.TableInTreeCellRenderer;
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
    private TestEntityWrapper user1;
    private TestEntityWrapper user2;
    private Set<String> userURIs;
    
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
        Ovation.enableLogging(LogLevel.DEBUG);

        String UNUSED_NAME = "name";
        String UNUSED_PURPOSE = "purpose";
        DateTime UNUSED_START = new DateTime(0);
        byte[] data = {1, 2, 3, 4, 5};
        String uti = "unknown-uti";
        
        DataContext c = dsc.getContext();
        User newUser = c.addUser("newUser", "password");
        project = new TestEntityWrapper(dsc, c.insertProject(UNUSED_NAME, UNUSED_PURPOSE, UNUSED_START));
        source = new TestEntityWrapper(dsc, c.insertSource("source"));
        Project p = (Project)project.getEntity();
        p.addProperty("color", "yellow");
        p.addProperty("size", 10.5);
        Source s = (Source)source.getEntity();
        s.addProperty("id", 4);
        s.addProperty("birthday", "6/23/1988");
        
        user1 = new TestEntityWrapper(dsc, c.currentAuthenticatedUser());
        user2 = new TestEntityWrapper(dsc, newUser);
        userURIs = new HashSet();
        userURIs.add(user1.getURI());
        userURIs.add(user2.getURI());
        
        c.authenticateUser("newUser", "password");
        p.addProperty("color", "chartreuse");
        p.addProperty("interesting", true);
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
    public void testGetsProperTreeNodeStructure()
    {
        Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();
        
        entitySet.add(project);
        entitySet.add(source);
        PropertiesViewTopComponent t = new PropertiesViewTopComponent();
        assertTrue( t.getEntities() == null ||t.getEntities().isEmpty());
        t.update(entitySet, dsc.getContext());
        
        JTree tree = t.getTreeRenderer().getTree();
        DefaultMutableTreeNode n = (DefaultMutableTreeNode)((DefaultTreeModel)tree.getModel()).getRoot();
        assertEquals(n.getChildCount(), 2);

        DefaultMutableTreeNode currentUserNode = (DefaultMutableTreeNode)n.getChildAt(0);
        DefaultMutableTreeNode otherUserNode = (DefaultMutableTreeNode)n.getChildAt(1);
        
        assertTrue(((DefaultMutableTreeNode)currentUserNode.getChildAt(0)) instanceof TableNode);
        assertEquals(currentUserNode.getChildCount(), 1);
        assertTrue(((DefaultMutableTreeNode)otherUserNode.getChildAt(0)) instanceof TableNode);
        assertEquals(otherUserNode.getChildCount(), 1);
    }
    
    @Test
    public void testGetsPropertiesAppropriatelyForEachUser()
    {
        Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();
       
        entitySet.add(project);
        entitySet.add(source);
        PropertiesViewTopComponent tc = new PropertiesViewTopComponent();
        tc.update(entitySet, dsc.getContext());
        
        DataContext c = dsc.getContext();
        TableTree t = new TableTree(tc.getTreeRenderer(), userURIs);
        
        //user1 properties
        Map<String, Object> props = t.getProperties(user1.getURI());
        Map<String, Object> databaseProps = getAggregateUserProperties(((User)user1.getEntity()), entitySet);
        assertMapsEqual(props, databaseProps);
        
        //user2 properties
        props = t.getProperties(user1.getURI());
        databaseProps = getAggregateUserProperties(((User)user1.getEntity()), entitySet);
        assertMapsEqual(props, databaseProps);
        
    }
    
    @Test
    public void testSetsPropertiesByDifferentUsers()
    {
        Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();
       
        entitySet.add(project);
        entitySet.add(source);
        PropertiesViewTopComponent tc = new PropertiesViewTopComponent();
        tc.update(entitySet, dsc.getContext());
        
        DataContext c = dsc.getContext();
        TableTree t = new TableTree(tc.getTreeRenderer(), userURIs);
        
        
        String userURI = c.currentAuthenticatedUser().getURIString();
        Map<String, Object> props = t.getProperties(userURI);
        String key = props.keySet().iterator().next();
        
        String newValue = "something else";
        t.editProperty(userURI, key, newValue);        
        Map<String, Object> databaseProps = getAggregateUserProperties(c.currentAuthenticatedUser(), entitySet);
        assertEquals(t.getProperties(userURI).get(key), newValue);
        assertEquals(databaseProps.get(key), newValue);
    }  
    
    @Test
    public void testCantEditOtherUsersProperty()
    {
        Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();
        
        entitySet.add(project);
        entitySet.add(source);
        PropertiesViewTopComponent tc = new PropertiesViewTopComponent();
        tc.update(entitySet, dsc.getContext());
        
        DataContext c = dsc.getContext();
        TableTree t = new TableTree(tc.getTreeRenderer(), userURIs);
        
        String userURI = user2.getURI();
        Map<String, Object> props = t.getProperties(userURI);
        String key = props.keySet().iterator().next();
        
        //this should fail
        String newValue = "something else";
        t.editProperty(userURI, key, newValue);        
        Map<String, Object> databaseProps = getAggregateUserProperties(c.currentAuthenticatedUser(), entitySet);
        assertEquals(t.getProperties(userURI).get(key), newValue);
        assertEquals(databaseProps.get(key), newValue);
    }
   
    @Test
    public void testAddPropertyToEntity()
    {
        Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();
        
        entitySet.add(project);
        entitySet.add(source);
        PropertiesViewTopComponent tc = new PropertiesViewTopComponent();
        assertTrue( tc.getEntities().isEmpty());
        tc.update(entitySet, dsc.getContext());
        
        DataContext c = dsc.getContext();
        TableTree t = new TableTree(tc.getTreeRenderer(), userURIs);
        
        String userURI = user1.getURI();
        Map<String, Object> props = t.getProperties(userURI);
        String key = "a brand new key";
        assertFalse(props.containsKey(key));
        
        String newValue = "something else";
        t.addProperty(userURI, key, newValue);        
        Map<String, Object> databaseProps = getAggregateUserProperties(c.currentAuthenticatedUser(), entitySet);
        assertEquals(t.getProperties(userURI).get(key), newValue);
        assertEquals(databaseProps.get(key), newValue);
    } 
    
    @Test
    public void testAddPropertiesToMultipleEntities()
    {
        Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();
        
        entitySet.add(project);
        entitySet.add(source);
        PropertiesViewTopComponent tc = new PropertiesViewTopComponent();
        assertTrue( tc.getEntities().isEmpty());
        tc.update(entitySet, dsc.getContext());
        
        DataContext c = dsc.getContext();
        TableTree t = new TableTree(tc.getTreeRenderer(), userURIs);
        
        String userURI = user1.getURI();
        Map<String, Object> props = t.getProperties(userURI);
        String key = "a brand new key";
        assertFalse(props.containsKey(key));
                
        String newValue = "something else";
        t.addProperty(userURI, key, newValue);        
        
        assertEquals(project.getEntity().getMyProperties().get(key), newValue);
        assertEquals(source.getEntity().getMyProperties().get(key), newValue);
    } 
    
    @Test
    public void testAddPropertyToMultipleEntitiesEvenWhenOneEntityAlreadyHasProperty()
    {
         
        Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();
        
        entitySet.add(project);
        entitySet.add(source);
        PropertiesViewTopComponent tc = new PropertiesViewTopComponent();
        assertTrue( tc.getEntities().isEmpty());
        tc.update(entitySet, dsc.getContext());
        
        DataContext c = dsc.getContext();
        TableTree t = new TableTree(tc.getTreeRenderer(), userURIs);
        
        String userURI = user1.getURI();
        Map<String, Object> props = t.getProperties(userURI);
        String key = "a brand new key";
        assertFalse(props.containsKey(key));
        
        project.getEntity().addProperty(key, 25.7); //now project contains a property with key, but source does not
        
        String newValue = "something else";
        t.addProperty(userURI, key, newValue);        
        
        assertEquals(project.getEntity().getMyProperties().get(key), newValue);
        assertEquals(source.getEntity().getMyProperties().get(key), newValue);

    } 
    
    @Test
    public void testRemovePropertyFromEntity()
    {
        Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();
       
        entitySet.add(project);
        entitySet.add(source);
        PropertiesViewTopComponent tc = new PropertiesViewTopComponent();
        tc.update(entitySet, dsc.getContext());
        
        DataContext c = dsc.getContext();
        TableTree t = new TableTree(tc.getTreeRenderer(), userURIs);
        
        String userURI = c.currentAuthenticatedUser().getURIString();
        Map<String, Object> props = t.getProperties(userURI);
        String key = props.keySet().iterator().next();
        
        t.removeProperty(userURI, key);
        
        Map<String, Object> databaseProps = getAggregateUserProperties(c.currentAuthenticatedUser(), entitySet);
        assertFalse(t.getProperties(userURI).containsKey(key));
        assertFalse(databaseProps.containsKey(key));
    } 
    
    @Test
    public void testRemovePropertiesFromMultipleEntities()
    {
        Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();
       
        entitySet.add(project);
        entitySet.add(source);
        PropertiesViewTopComponent tc = new PropertiesViewTopComponent();
        tc.update(entitySet, dsc.getContext());
        
        DataContext c = dsc.getContext();
        TableTree t = new TableTree(tc.getTreeRenderer(), userURIs);
        
        String userURI = c.currentAuthenticatedUser().getURIString();
        Map<String, Object> props = t.getProperties(userURI);
        String key = "a brand new key";
        assertFalse(props.containsKey(key));
        
        project.getEntity().addProperty(key, 27.8);
        source.getEntity().addProperty(key, 27.8);
        
        t.removeProperty(userURI, key);
        
        Map<String, Object> databaseProps = getAggregateUserProperties(c.currentAuthenticatedUser(), entitySet);
        assertFalse(project.getEntity().getMyProperties().containsKey(key));
        assertFalse(source.getEntity().getMyProperties().containsKey(key));
    } 
    
    @Test
    public void testRemovePropertyFromMutlipleEntitesIfPropertyDidNotExistOnOneEntity()
    {
        Set<IEntityWrapper> entitySet = new HashSet<IEntityWrapper>();
       
        entitySet.add(project);
        entitySet.add(source);
        PropertiesViewTopComponent tc = new PropertiesViewTopComponent();
        tc.update(entitySet, dsc.getContext());
        
        DataContext c = dsc.getContext();
        TableTree t = new TableTree(tc.getTreeRenderer(), userURIs);
        
        String userURI = c.currentAuthenticatedUser().getURIString();
        Map<String, Object> props = t.getProperties(userURI);
        String key = "a brand new key";
        assertFalse(props.containsKey(key));
        
        project.getEntity().addProperty(key, 27.8);
        
        t.removeProperty(userURI, key);
        
        Map<String, Object> databaseProps = getAggregateUserProperties(c.currentAuthenticatedUser(), entitySet);
        assertFalse(project.getEntity().getMyProperties().containsKey(key));
        assertFalse(source.getEntity().getMyProperties().containsKey(key));
    } 
    
    @Test
    public void testRemoveOnlySelectedKeyValuePairIfKeyExistsOnAnotherObject()
    {
        assertFalse(true);
    }

    static Map<String, Object> getAggregateUserProperties(User u, Set<IEntityWrapper> entities)
    {
        Map<String, Object> databaseProps = new HashMap<String, Object>();
        for (IEntityWrapper ew : entities)
        {
            databaseProps.putAll(ew.getEntity().getUserProperties(u));
        }
        return databaseProps;
    }
    
    void assertMapsEqual(Map<String, Object> m1, Map<String, Object> m2)
    {
        assertEquals(m1.size(), m2.size());
        for (String key : m1.keySet())
        {
            assertEquals(m2.get(key), m1.get(key));
        }
    }
    
    class TableTree{
        TreeWithTableRenderer renderer;
        TableTree(TreeWithTableRenderer t, Set<String> userURIs)
        {
            renderer = t;
            int i =1;
            for (String userURI :userURIs)
            {
                TableNode node = ((TableNode)((DefaultMutableTreeNode)getUserNode(userURI)).getChildAt(0));
                ((TableInTreeCellRenderer)renderer.getTree().getCellRenderer()).getPanelFromPropertySet(getUserPropertySet(userURI), node, dsc);
            }
        }
        
        DefaultMutableTreeNode getUserNode(String userURI)
        {
            JTree tree = renderer.getTree();
            DefaultMutableTreeNode n = (DefaultMutableTreeNode)((DefaultTreeModel)tree.getModel()).getRoot();

            if (userURI == null)
            {
                return n;
            }
            for (int i =0; i< n.getChildCount(); i++)
            {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)n.getChildAt(i);
                UserPropertySet s = ((UserPropertySet)((DefaultMutableTreeNode)node.getChildAt(0)).getUserObject());
                if (s.getURI().equals(userURI))
                {
                    return node;
                }
            }
            return null;
        }
        
        UserPropertySet getUserPropertySet(String userURI)
        {
            JTree tree = renderer.getTree();
            DefaultMutableTreeNode n = (DefaultMutableTreeNode)((DefaultTreeModel)tree.getModel()).getRoot();

            if (userURI == null)
            {
                return null;
            }
            for (int i =0; i< n.getChildCount(); i++)
            {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)n.getChildAt(i);
                UserPropertySet s = ((UserPropertySet)((DefaultMutableTreeNode)node.getChildAt(0)).getUserObject());
                if (s.getURI().equals(userURI))
                {
                    return s;
                }
            }
            return null;
        }
        
        //These methods are used by the tests
        public Map<String, Object> getProperties(String userURI)
        {
            JTree tree = renderer.getTree();
            DefaultMutableTreeNode n = (DefaultMutableTreeNode)((DefaultTreeModel)tree.getModel()).getRoot();

            for (int i =0; i< n.getChildCount(); i++)
            {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)n.getChildAt(i);
                UserPropertySet s = ((UserPropertySet)((DefaultMutableTreeNode)node.getChildAt(0)).getUserObject());
                if (s.getURI().equals(userURI))
                {
                    return s.getProperties();
                }
            }
            return null;
        }
        
        public void editProperty(String userURI, String key, Object value)
        {
            JTree tree = renderer.getTree();
            UserPropertySet s = getUserPropertySet(userURI);
            DefaultTableModel m = ((DefaultTableModel)((TableInTreeCellRenderer)tree.getCellRenderer()).getTableModel(s));
            int firstRow = -1;
            for (int i =0; i< m.getRowCount(); i++)
            {
                if (m.getValueAt(i, 0).equals(key))
                {
                    firstRow = i;
                    m.setValueAt(value, i, 1);
                }
            }
            if (firstRow < 0)
            {
                throw new RuntimeException("Property to edit doesn't exist, call 'addProperties' instead");
            }
            
            boolean noListener = true;
            for (TableModelListener l : m.getListeners(TableModelListener.class))
            {
                if (l instanceof PropertyTableModelListener)
                {
                    noListener = false;
                    
                    TableModelEvent t = new TableModelEvent(m, firstRow, firstRow, 1, TableModelEvent.UPDATE);
                    l.tableChanged(t);
                    break;
                }
            }
            if (noListener)
            {
                throw new RuntimeException("No listener available for the TableModel");
            }
            
        }
        
        public void addProperty(String userURI, String key, Object value)
        {
            JTree tree = renderer.getTree();
            UserPropertySet s = getUserPropertySet(userURI);
            DefaultTableModel m = ((DefaultTableModel)((TableInTreeCellRenderer)tree.getCellRenderer()).getTableModel(s));
            
            boolean noListener = true;
            for (TableModelListener l : m.getListeners(TableModelListener.class))
            {
                if (l instanceof PropertyTableModelListener)
                {
                    noListener = false;

                    TableModelEvent t0 = new TableModelEvent(m, m.getRowCount(), m.getRowCount(), 0, TableModelEvent.INSERT);
                    TableModelEvent t1 = new TableModelEvent(m, m.getRowCount(), m.getRowCount(), 1, TableModelEvent.INSERT);

                    TableModelEvent t2 = new TableModelEvent(m, m.getRowCount(), m.getRowCount(), 1, TableModelEvent.UPDATE);
                    TableModelEvent t3 = new TableModelEvent(m, m.getRowCount(), m.getRowCount(), 0, TableModelEvent.UPDATE);
                    
                    l.tableChanged(t0);
                    l.tableChanged(t1);
                    l.tableChanged(t2);
                    l.tableChanged(t3);
                    break;
                }
            }
            if (noListener)
            {
                throw new RuntimeException("No listener available for the TableModel");
            }
        }
        
        public void removeProperty(String userURI, String key)
        {
            JTree tree = renderer.getTree();
            UserPropertySet s = getUserPropertySet(userURI);
            DefaultTableModel m = ((DefaultTableModel)((TableInTreeCellRenderer)tree.getCellRenderer()).getTableModel(s));
            
            int row = -1;
            for (int i =0; i< m.getRowCount(); i++)
            {
                if (m.getValueAt(i, 0).equals(key))
                {
                    row = i;
                }
            }
            
            boolean noListener = true;
            for (TableModelListener l : m.getListeners(TableModelListener.class))
            {
                if (l instanceof PropertyTableModelListener)
                {
                    noListener = false;

                    ((PropertyTableModelListener)l).deleteProperty(key, m, row);
                    TableModelEvent t0 = new TableModelEvent(m, m.getRowCount(), m.getRowCount(), 0, TableModelEvent.INSERT);
                    TableModelEvent t1 = new TableModelEvent(m, m.getRowCount(), m.getRowCount(), 1, TableModelEvent.INSERT);

                    TableModelEvent t2 = new TableModelEvent(m, m.getRowCount(), m.getRowCount(), 1, TableModelEvent.UPDATE);
                    TableModelEvent t3 = new TableModelEvent(m, m.getRowCount(), m.getRowCount(), 0, TableModelEvent.UPDATE);
                    
                    l.tableChanged(t0);
                    l.tableChanged(t1);
                    l.tableChanged(t2);
                    l.tableChanged(t3);
                    break;
                }
            }
            if (noListener)
            {
                throw new RuntimeException("No listener available for the TableModel");
            }
        }
    }
}
