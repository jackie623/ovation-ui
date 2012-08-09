/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import java.io.File;
import java.net.InetAddress;
import java.util.*;
import junit.framework.TestCase;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.joda.time.DateTime;
import org.junit.*;
import static org.junit.Assert.*;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;
import ovation.IAuthenticatedDataStoreCoordinator;
import ovation.*;
import ovation.database.DatabaseManager;
import us.physion.ovation.interfaces.ConnectionListener;
import us.physion.ovation.interfaces.ConnectionProvider;
import us.physion.ovation.interfaces.IEntityWrapper;
import us.physion.ovation.interfaces.TestEntityWrapper;

/**
 *
 * @author huecotanks
 */
public class ResponseViewTopComponentTest{
    
   IAuthenticatedDataStoreCoordinator dsc;
   Experiment experiment = null;
   Epoch epoch = null;
    static ResponseViewTestManager tm = new ResponseViewTestManager();

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

            int jobFdId = 2;
            if (System.getProperty("JOB_FDID") != null) {
                jobFdId = Integer.parseInt(System.getProperty("JOB_FDID"));
            }
            
            db.createLocalDatabase(tm.getConnectionFile(), lockServer, nodeFdId + jobFdId);
        }
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
        DateTime UNUSED_END = new DateTime(1);
        String UNUSED_LABEL = "label";
        
        DataContext c = dsc.getContext();
        Project project = c.insertProject(UNUSED_NAME, UNUSED_PURPOSE, UNUSED_START);
        experiment = project.insertExperiment(UNUSED_PURPOSE, UNUSED_START);

        Source p = c.insertSource("experiment source");
        Source source = p.insertSource("source");

        EpochGroup group = experiment.insertEpochGroup(source, UNUSED_LABEL, UNUSED_START);

        epoch = group.insertEpoch(UNUSED_START, UNUSED_END, "protocol", new HashMap<String, Object>());

    }


    @AfterClass
    public static void tearDownClass() throws Exception {
        DatabaseManager db = new DatabaseManager();
        db.deleteLocalDatabase(tm.getConnectionFile());
    }
    
  
    @After
    public void tearDown() {
        tm.tearDownDatabase();
    }

    public ResponseViewTopComponentTest() {
    }

    @Test
    public void testGraphsSelectedEntity() {
        
        ResponseViewTopComponent t = new ResponseViewTopComponent();
        //assertNotNull(Lookup.getDefault().lookup(ConnectionProvider.class));
        Collection entities = new HashSet();
        ExternalDevice dev = experiment.externalDevice("device-name", "manufacturer");
        double[] d = new double[10000];
        for (int i=0; i< d.length; ++i)
        {
            d[i] = i;
        }
        NumericData data = new NumericData(d);
        String units = "units";
        String dimensionLabel = "dimension label";
        double samplingRate = 3;
        String samplingRateUnits = "Hz";
        String dataUTI = Response.NUMERIC_DATA_UTI;
        Response r = epoch.insertResponse(dev, new HashMap(), data, units, dimensionLabel, samplingRate, samplingRateUnits, dataUTI);
        entities.add(new TestEntityWrapper(dsc, r));
        
        Collection<ChartWrapper> chartWrappers= t.updateEntitySelection(entities);
        
        System.out.println(chartWrappers.size());
        
        assertTrue(chartWrappers.size() == entities.size());
        
        for (ChartWrapper p : chartWrappers)
        {
            XYDataset ds = p.getDataset();
            Comparable key = ds.getSeriesKey(0);
            assertEquals(key, dev.getName());
            for (int i=0; i<d.length; ++i)
            {
                assertTrue(d[i] == ds.getYValue(0, i));
                assertTrue(i/samplingRate == ds.getXValue(0, i));
            }
            
            assertEquals(p.getXAxis(), ResponseWrapper.convertSamplingRateUnitsToGraphUnits(r.getSamplingUnits()[0]));
            assertEquals(p.getYAxis(), r.getUnits());
        }
    }
    
    @Test
    public void testCreateChartFromChartWrapper() 
    {
        ExternalDevice dev = experiment.externalDevice("device-name", "manufacturer");
        double[] d = new double[10000];
        for (int i=0; i< d.length; ++i)
        {
            d[i] = i;
        }
        NumericData data = new NumericData(d);
        String units = "units";
        String dimensionLabel = "dimension label";
        double samplingRate = 3;
        String samplingRateUnits = "Hz";
        String dataUTI = Response.NUMERIC_DATA_UTI;
        Response r = epoch.insertResponse(dev, new HashMap(), data, units, dimensionLabel, samplingRate, samplingRateUnits, dataUTI);
        
        ResponseWrapper rw = ResponseWrapper.createIfPlottable(r);
        DefaultXYDataset ds = new DefaultXYDataset();
        ResponseViewTopComponent.addXYDataset( ds, rw, dev.getName());
        
        ChartWrapper cw = new ChartWrapper(ds, rw.xUnits(), rw.yUnits());
        cw.setTitle(dev.getName());
        ChartPanel p = cw.generateChartPanel();
        XYPlot plot = p.getChart().getXYPlot();
        Comparable key = ds.getSeriesKey(0);
        assertEquals(key, dev.getName());
        for (int i = 0; i < d.length; ++i) {
            assertTrue(d[i] == ds.getYValue(0, i));
            assertTrue(i / samplingRate == ds.getXValue(0, i));
        }

        assertEquals(plot.getDomainAxis().getLabel(), ResponseWrapper.convertSamplingRateUnitsToGraphUnits(r.getSamplingUnits()[0]));
        assertEquals(plot.getRangeAxis().getLabel(), r.getUnits());
    }
    
    @Test
    public void testGraphsMultipleSelectedEntitiesWithSharedUnits()
    {
    
    }
    
    @Test
    public void testGraphsMultipleSelectedEntitiesWithoutSharedUnits()
    {
    
    }
}
