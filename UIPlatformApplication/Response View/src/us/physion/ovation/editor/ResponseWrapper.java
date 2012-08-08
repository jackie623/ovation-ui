/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import ovation.NumericData;
import ovation.Response;
import us.physion.ovation.interfaces.IEntityWrapper;

/**
 *
 * @author huecotanks
 */
public class ResponseWrapper {
    String xunits;
    String yunits;
    double samplingRate;
    NumericData data;
    
    protected ResponseWrapper() {}
    
    
    static ResponseWrapper createIfPlottable(Response r)
    {
        if (!r.getUTI().equals(Response.NUMERIC_DATA_UTI) || r.getShape().length != 1)
        {
            return null;
        }
    
        ResponseWrapper rw = new ResponseWrapper();
        rw.data = r.getData();
        rw.samplingRate = r.getSamplingRates()[0];
        rw.yunits = r.getUnits();
        rw.xunits = convertSamplingRateUnitsToGraphUnits(r.getSamplingUnits()[0]);
        return rw;
    }
    static ResponseWrapper createIfPlottable(IEntityWrapper ew)
    {
        return createIfPlottable((Response)ew.getEntity());
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
    
    protected static String convertSamplingRateUnitsToGraphUnits(String samplingRateUnits){
       if (samplingRateUnits.toLowerCase().contains("hz"))
       {
           String prefix = samplingRateUnits.substring(0, samplingRateUnits.toLowerCase().indexOf("hz"));
           return "Time (in " + prefix + "Seconds)";
       }
       else return ("1 / " + samplingRateUnits);
    }
}
