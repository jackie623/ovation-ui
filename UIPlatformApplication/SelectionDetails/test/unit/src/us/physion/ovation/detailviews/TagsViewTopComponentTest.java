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
import org.openide.util.Exceptions;
import ovation.*;
import ovation.test.TestManager;
import us.physion.ovation.interfaces.TestEntityWrapper;
import us.physion.ovation.interfaces.OvationTestCase;

/**
 *
 * @author huecotanks
 */
public class TagsViewTopComponentTest extends OvationTestCase{
    private TestEntityWrapper project;
    private TestEntityWrapper project2;
    
    static TestManager mgr = new SelectionViewTestManager();
    public TagsViewTopComponentTest() {
        setTestManager(mgr); //this is because there are static and non-static methods that need to use the test manager
    }
    
    @BeforeClass
    public static void setUpClass()
    {
        OvationTestCase.setUpClass(mgr, 6);
    }
    
    @Before
    public void setUp() {
        super.setUp();

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
