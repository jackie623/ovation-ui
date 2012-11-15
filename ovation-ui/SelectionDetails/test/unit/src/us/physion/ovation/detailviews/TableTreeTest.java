/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import java.util.HashSet;
import java.util.Set;
import javax.swing.JTree;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import org.joda.time.DateTime;
import org.junit.*;
import static org.junit.Assert.*;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;
import ovation.*;
import ovation.test.TestManager;
import us.physion.ovation.interfaces.ConnectionListener;
import us.physion.ovation.interfaces.ConnectionProvider;
import us.physion.ovation.interfaces.OvationTestCase;
import us.physion.ovation.interfaces.TestEntityWrapper;

@ServiceProvider(service = Lookup.Provider.class)

/**
 *
 * @author huecotanks
 */
public class TableTreeTest extends OvationTestCase implements Lookup.Provider, ConnectionProvider{

    private Lookup l;
    InstanceContent ic;
    private TestEntityWrapper project;
    private TestEntityWrapper source;
    private TestEntityWrapper user1;
    private TestEntityWrapper user2;
    private Set<String> userURIs;
    
    private Set<String> uris;
    private JTree mockTree;

    static TestManager mgr = new SelectionViewTestManager();
    public TableTreeTest() {
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
        
        ic.add(this);
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
    public void testUpdatesDatabaseWithRowAddition()
    {
        TableTreeKey k = new TestTreeKey("");
        PropertyTableModelListener l = new PropertyTableModelListener(uris, mockTree, new TableNode(k));
  
        DefaultTableModel m = new DefaultTableModel();
        int row = Math.max(m.getRowCount() -1, 0);
        TableModelEvent t = new TableModelEvent(m, row, row, 0, TableModelEvent.INSERT);

        l.tableChanged(t);
        
        //assertTrue(mockTree.nodeWasChanged());
        
        
    }
    
    @Test
    public void testUpdatesDatabaseWithModifiedKeyValue()
    {
        
    }
    
    @Test
    public void testUpdatesDatabaseWithRowDeletion()
    {
        
    }
    
    @Test
    public void testSomething()
    {
        
    }

    @Override
    public Lookup getLookup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IAuthenticatedDataStoreCoordinator getConnection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addConnectionListener(ConnectionListener cl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeConnectionListener(ConnectionListener cl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void resetConnection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private static class TestTreeKey implements TableTreeKey {

        String name;
        public TestTreeKey(String name) {
            this.name = name;
        }

        @Override
        public void refresh(IAuthenticatedDataStoreCoordinator dsc) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getDisplayName() {
            return name;
        }

        @Override
        public Object[][] getData() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getID() {
            return name;
        }

        @Override
        public boolean isEditable() {
            return true;
        }

        @Override
        public boolean isExpandedByDefault() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int compareTo(Object t) {
            if (t instanceof TestTreeKey)
                return getID().compareTo(((TestTreeKey) t).getID());
            throw new RuntimeException("Object is not of type " + this.getClass());
        }
    }
}
