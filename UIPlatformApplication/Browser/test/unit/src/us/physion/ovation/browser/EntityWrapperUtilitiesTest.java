/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser;

import us.physion.ovation.browser.QueryChildren;
import us.physion.ovation.browser.EntityWrapperUtilities;
import java.io.File;
import java.net.InetAddress;
import java.util.*;
import junit.framework.TestCase;
import org.apache.log4j.Level;
import org.junit.*;
import static org.junit.Assert.*;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import ovation.*;
import ovation.database.DatabaseManager;
import us.physion.ovation.interfaces.IEntityWrapper;

/**
 *
 * @author jackie
 */
public class EntityWrapperUtilitiesTest {

    public EntityWrapperUtilitiesTest() {
        /*
         * String s = System.getProperty("OVATION_TEST");
         * System.setProperty("OVATION_TEST", "true"); s =
         * System.getProperty("OVATION_TEST"); String s2 = s.toString();
         */
    }
    ExplorerManager em;
    Map<String, Node> treeMap;
    IAuthenticatedDataStoreCoordinator dsc;
    static BrowserTestManager tm = new BrowserTestManager();

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

            int jobFdId = 3;
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

        treeMap = new HashMap<String, Node>();
        em = new ExplorerManager();
    }

    @After
    public void tearDown() {
        tm.tearDownDatabase();
    }

    @Test
    public void testCreateNodeForNodeThatAlreadyExists() {
        //TODO
    }

    @Test
    public void testQuerySetsProjectViewRootNodeAppropriately() {
        em = new ExplorerManager();
        em.setRootContext(new AbstractNode(new QueryChildren(true)));
        Iterator<IEntityBase> itr = dsc.getContext().query(Experiment.class, "true");

        Set s = new HashSet<ExplorerManager>();
        s.add(em);
        EntityWrapperUtilities.createNodesFromQuery(s, itr);

        Node[] projects = em.getRootContext().getChildren().getNodes(true);
        Set<String> projectSet = new HashSet<String>();
        for (Project p : dsc.getContext().getProjects()) {
            projectSet.add(p.getURIString());
        }

        for (Node n : projects) {
            IEntityWrapper ew = n.getLookup().lookup(IEntityWrapper.class);
            assertTrue(projectSet.contains(ew.getURI()));
            projectSet.remove(ew.getURI());
        }
        assertTrue(projectSet.isEmpty());
    }

    @Test
    public void testQuerySetsSourceViewRootNodeAppropriately() {
        ExplorerManager em = new ExplorerManager();
        em.setRootContext(new AbstractNode(new QueryChildren(false)));
        Iterator<IEntityBase> itr = dsc.getContext().query(Experiment.class, "true");

        Set mgrSet = new HashSet<ExplorerManager>();
        mgrSet.add(em);
        EntityWrapperUtilities.createNodesFromQuery(mgrSet, itr);

        Node[] sources = em.getRootContext().getChildren().getNodes(true);
        Set<String> sourcesSet = new HashSet<String>();

        for (Source s : dsc.getContext().getSources()) {
            if (s.getParent() == null) {
                sourcesSet.add(s.getURIString());
            }
        }
        for (Node n : sources) {
            IEntityWrapper ew = n.getLookup().lookup(IEntityWrapper.class);
            assertTrue(sourcesSet.contains(ew.getURI()));
            sourcesSet.remove(ew.getURI());
        }
        assertTrue(sourcesSet.isEmpty());
    }

    @Test
    public void testQuerySetsExperimentNodesAppropriatelyInSourceView() {
        ExplorerManager em = new ExplorerManager();
        em.setRootContext(new AbstractNode(new QueryChildren(false)));
        Iterator<IEntityBase> itr = dsc.getContext().query(Experiment.class, "true");

        Set mgrSet = new HashSet<ExplorerManager>();
        mgrSet.add(em);
        EntityWrapperUtilities.createNodesFromQuery(mgrSet, itr);
        while (itr.hasNext()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        ArrayList<Node> sourceChildren = new ArrayList();
        for (Node n : em.getRootContext().getChildren().getNodes(true)) {
            Node[] nodes = n.getChildren().getNodes(true);
            for (Node child : nodes) {
                sourceChildren.add(child);
            }
        }
        Set<String> entitySet = new HashSet<String>();

        for (Source s : dsc.getContext().getSources()) {
            if (s.getParent() == null) {
                for (Experiment e : s.getExperiments()) {
                    entitySet.add(e.getURIString());
                }
            }
        }
        for (Node n : sourceChildren) {
            IEntityWrapper ew = n.getLookup().lookup(IEntityWrapper.class);
            if (ew.getType().isAssignableFrom(Experiment.class)) {
                assertTrue(entitySet.contains(ew.getURI()));
                entitySet.remove(ew.getURI());
            }
        }
        assertTrue(entitySet.isEmpty());
    }

    @Test
    public void testQuerySetsExperimentNodesAppropriatelyInProjectView() {
        ExplorerManager em = new ExplorerManager();
        em.setRootContext(new AbstractNode(new QueryChildren(true)));
        Iterator<IEntityBase> itr = dsc.getContext().query(Experiment.class, "true");

        Set mgrSet = new HashSet<ExplorerManager>();
        mgrSet.add(em);
        EntityWrapperUtilities.createNodesFromQuery(mgrSet, itr);

        ArrayList<Node> projectChildren = new ArrayList();
        for (Node n : em.getRootContext().getChildren().getNodes(true)) {
            for (Node child : n.getChildren().getNodes(true)) {
                projectChildren.add(child);
            }
        }
        Set<String> entitySet = new HashSet<String>();
        for (Project p : dsc.getContext().getProjects()) {
            for (Experiment e : p.getExperiments()) {
                entitySet.add(e.getURIString());
            }
        }
        for (Node n : projectChildren) {
            IEntityWrapper ew = n.getLookup().lookup(IEntityWrapper.class);
            if (ew.getType().isAssignableFrom(Experiment.class)) {
                assertTrue(entitySet.contains(ew.getURI()));
                entitySet.remove(ew.getURI());
            } else {
                fail("Project node's child was something other than an Experment");
            }
        }
        assertTrue(entitySet.isEmpty());
    }

    @Test
    public void testQuerySetsAnalysisRecordNodesAppropriatelyInProjectView() {
        ExplorerManager em = new ExplorerManager();
        em.setRootContext(new AbstractNode(new QueryChildren(true)));
        Iterator<IEntityBase> itr = dsc.getContext().query(AnalysisRecord.class, "true");

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
        Iterator<User> userItr = dsc.getContext().getUsersIterator();
        while (userItr.hasNext()) {
            User user = userItr.next();
            for (Project p : dsc.getContext().getProjects()) {
                for (AnalysisRecord e : p.getAnalysisRecords(user.getUsername())) {
                    entitySet.add(e.getURIString());
                }
            }
        }

        for (Node n : analysisRecordNodes) {
            IEntityWrapper ew = n.getLookup().lookup(IEntityWrapper.class);
            if (ew.getType().isAssignableFrom(Experiment.class)) {
                assertTrue(entitySet.contains(ew.getURI()));
                entitySet.remove(ew.getURI());
            } else {
                fail("Project node's child was something other than an Experment");
            }
        }
        assertTrue(entitySet.isEmpty());
    }

    //Manual test
    @Test
    public void testPerformanceOnManyChildrenNodes() {
    }

    @Test
    public void testQueryListenerCancelsIteration() {
    }
}
