/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import java.io.File;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;
import org.joda.time.DateTime;
import org.junit.*;
import ovation.IAuthenticatedDataStoreCoordinator;
import ovation.database.DatabaseManager;
import static org.junit.Assert.*;
import ovation.DataContext;
import ovation.ITaggableEntityBase;
import ovation.Project;
import us.physion.ovation.interfaces.TestEntityWrapper;

/**
 *
 * @author huecotanks
 */
public class TagsViewTopComponentTest {
    private TestEntityWrapper project;
    private TestEntityWrapper project2;
    
    public TagsViewTopComponentTest() {
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
    }

    @After
    public void tearDown() {
        tm.tearDownDatabase();
    }
    
    @Test
    public void testUpdateListModelSetsSelectedTagsProperly()
    {
        TagsViewTopComponent t = new TagsViewTopComponent();
        Set entitySet = new HashSet();
        entitySet.add(project);
        entitySet.add(project2);
        
        //both projects are selected
        t.updateListModel(entitySet);
        Set<String> tags = new HashSet();
        for (String tag : ((ITaggableEntityBase)project.getEntity()).getTags())
        {
            tags.add(tag);
        }
        for (String tag : ((ITaggableEntityBase)project2.getEntity()).getTags())
        {
            tags.add(tag);
        }
        assertEquals(t.getTagList().size(), tags.size());
        
        for (String s : t.getTagList())
        {
            assertTrue(tags.contains(s));
        }
        
        //a single project is selected
        entitySet = new HashSet();
        entitySet.add(project);
        t.updateListModel(entitySet);
        tags = new HashSet();
        for (String tag : ((ITaggableEntityBase)project.getEntity()).getTags())
        {
            tags.add(tag);
        }
        assertEquals(t.getTagList().size(), tags.size());
        
        for (String s : t.getTagList())
        {
            assertTrue(tags.contains(s));
        }
        
    }
    
     @Test
    public void testAddTagToSelectedEntities()
    {
        TagsViewTopComponent t = new TagsViewTopComponent();
        Set entitySet = new HashSet();
        entitySet.add(project);
        entitySet.add(project2);
        
        t.updateListModel(entitySet);
        String newTag = "aaaa new tag";//should be at the beginning of the list
        t.addTag(entitySet, newTag);
        
        //adds new tag to both entites
        assertTrue(t.getTagList().contains(newTag));
        assertEquals(((ITaggableEntityBase)project2.getEntity()).getTags()[0], newTag);
        assertEquals(((ITaggableEntityBase)project.getEntity()).getTags()[0], newTag);
        
        entitySet = new HashSet();
        entitySet.add(project2);
        
        Project p2 = (Project)project2.getEntity();
        String oldTag = p2.getTags()[0];
        int tagNumber = p2.getTags().length;
        
        t.updateListModel(entitySet);

        assertEquals(tagNumber, t.getTagList().size());
        t.addTag(entitySet, oldTag);
        
        assertEquals(tagNumber, t.getTagList().size());
    }
     
}
