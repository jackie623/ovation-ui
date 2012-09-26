/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import javax.imageio.ImageIO;
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
          
        }else {
            String lowercaseUTI = uti.toLowerCase();
            for (String name : ImageIO.getReaderFormatNames())
            {
                if (lowercaseUTI.contains(name.toLowerCase()))
                {
                    DefaultImageWrapper d = new DefaultImageWrapper(r);
                    return d;
                }
            }
        }
        return null;
    }
}
