/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import ovation.Response;

/**
 *
 * @author huecotanks
 */
public class ResponseWrapperFactory {
    public static ResponseWrapper create(Response r)
    {
        String uti = r.getUTI();
        if (uti.equals(Response.NUMERIC_DATA_UTI) && r.getShape().length == 1)
        {
            ChartWrapper cw = new ChartWrapper(r);
            return cw;
            
        } else if (uti.equals("org.nema.dicom"))
        {
            DicomWrapper d = new DicomWrapper(r);
            return d;
          
        }else{
            return null;
        }
    }
}
