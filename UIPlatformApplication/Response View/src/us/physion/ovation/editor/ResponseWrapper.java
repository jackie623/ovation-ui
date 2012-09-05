/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import javax.swing.JPanel;
import org.jfree.data.xy.DefaultXYDataset;
import ovation.NumericData;
import ovation.Response;
import us.physion.ovation.interfaces.IEntityWrapper;

/**
 *
 * @author huecotanks
 */
public class ResponseWrapper implements ResponseGroupWrapper{
    String xunits;
    String yunits;
    double samplingRate;
    NumericData data;
    boolean isPlottable;
    ChartWrapper cw;
    
    protected ResponseWrapper() {}
    
    
    static ResponseWrapper createIfDisplayable(Response r, String name)
    {
        ResponseWrapper rw = new ResponseWrapper();
        
        if (r.getUTI().equals(Response.NUMERIC_DATA_UTI) && r.getShape().length == 1)
        {
            rw.isPlottable = true;
        } else if (isImageType(r.getUTI()))
        {
            rw.isPlottable = false;
        } else{
            return null;
        }
        
        rw.data = r.getData();
        rw.samplingRate = r.getSamplingRates()[0];
        rw.yunits = r.getUnits();
        rw.xunits = convertSamplingRateUnitsToGraphUnits(r.getSamplingUnits()[0]);
        
        if (rw.isPlottable())
        {
            rw.cw = new ChartWrapper(new DefaultXYDataset(), rw.xunits, rw.yunits);
            rw.cw.setTitle(name);
        }            
        return rw;
    }
    static ResponseWrapper createIfDisplayable(IEntityWrapper ew, String name)
    {
        return createIfDisplayable((Response)ew.getEntity(), name);
    }

    static boolean isImageType(String uti)
    {
        return true;//TODO: fix this
    }
    protected NumericData getData()
    {
        return data;
    }
    protected double getSamplingRate()
    {
        return samplingRate;
    }
    
    protected String xUnits()
    {
        return xunits;
    }
    protected String yUnits()
    {
        return yunits;
    }
    protected boolean isPlottable()
    {
        return isPlottable;
    }
    protected static String convertSamplingRateUnitsToGraphUnits(String samplingRateUnits){
       if (samplingRateUnits.toLowerCase().contains("hz"))
       {
           String prefix = samplingRateUnits.substring(0, samplingRateUnits.toLowerCase().indexOf("hz"));
           return "Time (in " + prefix + "Seconds)";
       }
       else return ("1 / " + samplingRateUnits);
    }

    @Override
    public JPanel generatePanel() {
        if (cw != null)
        {
            return cw.generateChartPanel();
        }
        else{
            return null;
        }
    }
}
