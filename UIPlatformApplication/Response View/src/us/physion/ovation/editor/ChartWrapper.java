/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import java.io.InputStream;
import org.jfree.data.xy.DefaultXYDataset;
import ovation.NumericData;
import ovation.Response;
import ovation.URLResponse;

/**
 *
 * @author huecotanks
 */
public class ChartWrapper implements ResponseWrapper{
    NumericData data;
    InputStream dataStream;
    String name;
    double samplingRate;
    String yunits;
    String xunits;
    
    public ChartWrapper(Response r)
    {
        if (r instanceof URLResponse)
        {
            dataStream = ((URLResponse) r).getDataStream();
        }else{
            data = r.getData();
        }
        name = r.getExternalDevice().getName();
        samplingRate = r.getSamplingRates()[0];
        yunits = r.getUnits();
        xunits = convertSamplingRateUnitsToGraphUnits(r.getSamplingUnits()[0]);
        
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
    public ResponseGroupWrapper createGroup() {
        ChartGroupWrapper g = new ChartGroupWrapper(new DefaultXYDataset(), xunits, yunits);
        g.setTitle(getName());
        g.addXYDataset(this);
        return g;
    }
    
    public String getXUnits()
    {
        return xunits;
    }
    
    public String getYUnits()
    {
        return yunits;
    }
    
    public double getSamplingRate()
    {
       return samplingRate; 
    }
    
    public String getName()
    {
        return name;
    }
    
    public NumericData getNumericData()
    {
        return data;
    }
    
    public InputStream getDataStream()
    {
        return dataStream;
    }
}
