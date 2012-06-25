/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physionconsulting.ovation.browser;

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
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
        ctx = Ovation.connect("/Users/huecotanks/test-ui/test-ui.connection", "TestUser", "password");
        treeMap = new HashMap<String, Node>();
        em = new ExplorerManager();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCreateNodeForNodeThatAlreadyExists()
    {
        
    }
    
    @Test
    public void testQuerySetsProjectViewRootNodeAppropriately()
    {
        ExplorerManager em = new ExplorerManager();
        em.setRootContext(new AbstractNode(new QueryChildren(true)));
        Iterator<IEntityBase> itr = ctx.query(Experiment.class, "true");
        
        Set s = new HashSet<ExplorerManager>();
        s.add(em);
        EntityWrapperUtilities.createNodesFromQuery(s, itr);
        
        Node[] projects = em.getRootContext().getChildren().getNodes(true);
        Set<String> projectSet = new HashSet<String>();
        for (Project p : ctx.getProjects())
        {
            projectSet.add(p.getURIString());
        }
        
        for (Node n : projects)
        {
            EntityWrapper ew = n.getLookup().lookup(EntityWrapper.class);
            assertTrue(projectSet.contains(ew.getURI()));
            projectSet.remove(ew.getURI());
        }
        assertTrue(projectSet.isEmpty());
    }
    
    @Test
    public void testQuerySetsSourceViewRootNodeAppropriately()
    {
        ExplorerManager em = new ExplorerManager();
        em.setRootContext(new AbstractNode(new QueryChildren(false)));
        Iterator<IEntityBase> itr = ctx.query(Experiment.class, "true");
        
        Set mgrSet = new HashSet<ExplorerManager>();
        mgrSet.add(em);
        EntityWrapperUtilities.createNodesFromQuery(mgrSet, itr);
        
        Node[] sources = em.getRootContext().getChildren().getNodes(true);
        Set<String> sourcesSet = new HashSet<String>();
        
        for (Source s : ctx.getSources())
        {
            if (s.getParent() == null)
            {
                sourcesSet.add(s.getURIString());
            }
        }
        for (Node n : sources)
        {
            EntityWrapper ew = n.getLookup().lookup(EntityWrapper.class);
            assertTrue(sourcesSet.contains(ew.getURI()));
            sourcesSet.remove(ew.getURI());
        }
        assertTrue(sourcesSet.isEmpty());
    }
    
    @Test
    public void testQuerySetsExperimentNodesAppropriatelyInSourceView()
    {
        ExplorerManager em = new ExplorerManager();
        em.setRootContext(new AbstractNode(new QueryChildren(false)));
        Iterator<IEntityBase> itr = ctx.query(Experiment.class, "true");
        
        Set mgrSet = new HashSet<ExplorerManager>();
        mgrSet.add(em);
        EntityWrapperUtilities.createNodesFromQuery(mgrSet, itr);
        while(itr.hasNext())
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        ArrayList<Node> sourceChildren = new ArrayList();
        for (Node n : em.getRootContext().getChildren().getNodes(true))
        {
            Node[] nodes = n.getChildren().getNodes(true);
            for (Node child : nodes)
            {
                sourceChildren.add(child);
            }
        }
        Set<String> entitySet = new HashSet<String>();
        
        for (Source s : ctx.getSources())
        {
            if (s.getParent() == null)
            {
                for (Experiment e : s.getExperiments()) {
                    entitySet.add(e.getURIString());
                }
            }
        }
        for (Node n : sourceChildren)
        {
            EntityWrapper ew = n.getLookup().lookup(EntityWrapper.class);
            if (ew.getType().isAssignableFrom(Experiment.class))
            {
                assertTrue(entitySet.contains(ew.getURI()));
                entitySet.remove(ew.getURI());
            }
        }
        assertTrue(entitySet.isEmpty());
    }
    
    @Test
    public void testQuerySetsExperimentNodesAppropriatelyInProjectView()
    {
        ExplorerManager em = new ExplorerManager();
        em.setRootContext(new AbstractNode(new QueryChildren(true)));
        Iterator<IEntityBase> itr = ctx.query(Experiment.class, "true");
        
        Set mgrSet = new HashSet<ExplorerManager>();
        mgrSet.add(em);
        EntityWrapperUtilities.createNodesFromQuery(mgrSet, itr);
        
        ArrayList<Node> projectChildren = new ArrayList();
        for (Node n : em.getRootContext().getChildren().getNodes(true))
        {
            for (Node child : n.getChildren().getNodes(true))
            {
                projectChildren.add(child);
            }
        }
        Set<String> entitySet = new HashSet<String>();
        for (Project p : ctx.getProjects())
        {
            for (Experiment e : p.getExperiments())
            {
                entitySet.add(e.getURIString());
            }
        }
        for (Node n : projectChildren)
        {
            EntityWrapper ew = n.getLookup().lookup(EntityWrapper.class);
            if (ew.getType().isAssignableFrom(Experiment.class))
            {
                assertTrue(entitySet.contains(ew.getURI()));
                entitySet.remove(ew.getURI());
            }
            else
            {
                fail("Project node's child was something other than an Experment");
            }
        }
        assertTrue(entitySet.isEmpty());
    }
    
    @Test
    public void testQuerySetsAnalysisRecordNodesAppropriatelyInProjectView()
    {
        ExplorerManager em = new ExplorerManager();
        em.setRootContext(new AbstractNode(new QueryChildren(true)));
        Iterator<IEntityBase> itr = ctx.query(AnalysisRecord.class, "true");

        Set mgrSet = new HashSet<ExplorerManager>();
        mgrSet.add(em);
        EntityWrapperUtilities.createNodesFromQuery(mgrSet, itr);

        ArrayList<Node> analysisRecordNodes = new ArrayList();
        for (Node n : em.getRootContext().getChildren().getNodes(true)) {
            for (Node child : n.getChildren().getNodes(true)) {
                for (Node grandChild : child.getChildren().getNodes(true)) {
                    analysisRecordNodes.add(grandChild);
                }
            }
        }
        Set<String> entitySet = new HashSet<String>();
        Iterator<User> userItr = ctx.getUsersIterator();
        while (userItr.hasNext())
        {
            User user = userItr.next();
            for (Project p : ctx.getProjects()) {
                for (AnalysisRecord e : p.getAnalysisRecords(user.getUsername())) {
                    entitySet.add(e.getURIString());
                }
            }
        }
        
        for (Node n : analysisRecordNodes) {
            EntityWrapper ew = n.getLookup().lookup(EntityWrapper.class);
            if (ew.getType().isAssignableFrom(Experiment.class)) {
                assertTrue(entitySet.contains(ew.getURI()));
                entitySet.remove(ew.getURI());
            }
            else
            {
                fail("Project node's child was something other than an Experment");
            }
        }
        assertTrue(entitySet.isEmpty());
    }
   
    //Manual test
    @Test
    public void testPerformanceOnManyChildrenNodes()
    {
        
    }
    
    @Test
    public void testQueryListenerCancelsIteration()
    {
        
    }
    
}
