/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import java.util.*;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreeNode;
import junit.framework.TestCase;
import org.joda.time.DateTime;
import org.junit.*;
import org.openide.util.Exceptions;
import ovation.*;
import ovation.test.TestManager;
import us.physion.ovation.interfaces.OvationTestCase;
import us.physion.ovation.interfaces.TestEntityWrapper;

/**
 *
 * @author jackie
 */
public class TagTableListenerTest extends OvationTestCase{
    private TestEntityWrapper project;
    private TestEntityWrapper project2;
    private MockResizableTree mockTree;
    
    static TestManager mgr = new SelectionViewTestManager();
    
    public TagTableListenerTest() {
        setTestManager(mgr); 
    }
    
    @BeforeClass
    public static void setUpClass()
    {
        OvationTestCase.setUpDatabase(mgr, 6);
    }
    
    @Before
    public void setUp() {
        dsc = setUpTest();

        String UNUSED_NAME = "name";
        String UNUSED_PURPOSE = "purpose";
        DateTime UNUSED_START = new DateTime(0);
        
        DataContext c = dsc.getContext();
        Project p = c.insertProject(UNUSED_NAME, UNUSED_PURPOSE, UNUSED_START);
        project = new TestEntityWrapper(dsc, p);
        p.addTag("tag1");
        p.addTag("tag2");
        
        Project p2 = c.insertProject(UNUSED_NAME, UNUSED_PURPOSE, UNUSED_START);
        project2 = project = new TestEntityWrapper(dsc, p);
        p2.addTag("tag1");
        p2.addTag("another tag");
        
        mockTree = new MockResizableTree();
        
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
    public void testTableChanged()
    {   
        Set<String> uris = new HashSet();
        uris.add(project.getURI());
        uris.add(project2.getURI());
        
        DataContext c = dsc.getContext();
        
        List<String> oldTags = new ArrayList<String>();
        
        TagsSet key = new TagsSet(c.currentAuthenticatedUser(), true, true, oldTags, uris);
        TreeNode node = new TableNode(key);
        //TagTableModelListener l = new TagTableModelListener(uris, mockTree, node, dsc);
        //l.addBlankRow();
    }
    
    
    @Test
    public void testAddNewBlankRow()
    {
    }
    
    @Test
    public void testAddNewTag()
    {
    }
    
    @Test
    public void testUpdatesDatabaseAndTableModelWithRowDeletion()
    {
        Set<String> uris = new HashSet();
        uris.add(project.getURI());
        uris.add(project2.getURI());
        
        String newTag1 = "something";
        String newTag2 = "something2";
        
        int i=0;
        for (String uri : uris)
        {
            ITaggableEntityBase eb = (ITaggableEntityBase)dsc.getContext().objectWithURI(uri);
            eb.addTag(newTag1);
            assertContainsTag(newTag1, eb, true);

            if (i++%2 == 0){
                eb.addTag(newTag2);//only some uris have the second tag
                assertContainsTag(newTag2, eb, true);
            }
        }
        
        TestTreeKey key = new TestTreeKey(dsc, uris);

        TagTableModelListener listener = new TagTableModelListener(uris, mockTree, new TableNode(key), dsc);
  
        final DefaultTableModel m = new DefaultTableModel();
        m.setDataVector(new Object[][]{new Object[]{newTag1}, new Object[]{newTag2}}, new Object[]{"Value"});
      
        TestCase.assertEquals(m.getRowCount(), 2);
        TestCase.assertEquals(m.getValueAt(0, 0), newTag1);
        TestCase.assertEquals(m.getValueAt(1, 0), newTag2);
        
        JTable table = new JTable();
        table.setModel(m);
        m.addTableModelListener(listener);
        EditableTable t = new EditableTable(table, null);
        
        t.deleteRows(new int[] {0, 1});
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        for (String uri : uris)
        {
            ITaggableEntityBase eb = (ITaggableEntityBase)dsc.getContext().objectWithURI(uri);
            assertContainsTag(newTag1, eb, false);
            assertContainsTag(newTag2, eb, false);
        }
        
        int rowCount = m.getRowCount();
        TestCase.assertEquals(rowCount, 0);
    }

    private void assertContainsTag(String t, ITaggableEntityBase eb, boolean b) {
        boolean contains = false;
        for (String tag : eb.getMyTags())
        {
            if (t.equals(tag))
            {
                contains = true;
            }
        }
        TestCase.assertEquals(contains, b);
    }
    
    private class TestTreeKey extends TagsSet {

        public TestTreeKey(IAuthenticatedDataStoreCoordinator dsc, Set<String> uris) {
            super(dsc.getContext().currentAuthenticatedUser(), true, true , new ArrayList<String>(), uris);
        }
        
        @Override
        public TableModelListener createTableModelListener(ScrollableTableTree t, TableNode n) {
            if (isEditable()) {
                return new TagTableModelListener(uris, (ExpandableJTree) t.getTree(), n, dsc);
            }
            return null;
        }
    }
}
