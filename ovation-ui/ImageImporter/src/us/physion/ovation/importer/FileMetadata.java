/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.importer;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.in.PrairieReader;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.services.OMEXMLService;
import ome.xml.model.primitives.Timestamp;
import org.joda.time.DateTime;
import org.openide.util.Exceptions;
import ovation.OvationException;

/**
 *
 * @author huecotanks
 */
public class FileMetadata {
    File file;
    MetadataRetrieve retrieve;
    FileMetadata(File f)
    {
        file = f;
        ServiceFactory factory = null;
        OMEXMLService service = null;
        IMetadata meta = null;
        try {
            factory = new ServiceFactory();

            service = factory.getInstance(OMEXMLService.class);
            try {
                meta = service.createOMEXMLMetadata();
            } catch (ServiceException ex) {
                Logger.getLogger(ImportImage.class.getName()).log(Level.SEVERE, null, ex);
                throw new OvationException("Unable to create metadata. " + ex.getMessage());
            }
        } catch (DependencyException ex) {
            Logger.getLogger(ImportImage.class.getName()).log(Level.SEVERE, null, ex);
            throw new OvationException("Unable to create metadata. " + ex.getMessage());
        }
        IFormatReader r;
        if (file.getName().endsWith(".lsm"))
            r = new PrairieReader();
        /*else if (file.getName().endsWith(".zeiss"))
            r = new ZeissReader();*/
        else
            r = new ImageReader();
        
        r.setMetadataStore(meta);
        try {
            r.setId(file.getAbsolutePath());
        } catch (FormatException ex) {
            Exceptions.printStackTrace(ex);
            throw new OvationException("Unable to parse file. " + ex.getMessage());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            throw new OvationException("Unable to read file. " + ex.getMessage());
        }
        retrieve = service.asRetrieve(r.getMetadataStore());
    }
    
    public File getFile()
    {
        return file;
    }
    public MetadataRetrieve getMetadata()
    {
        return retrieve;
    }
    
    public DateTime getStart()
    {
        Date min = null;
        for (int i =0; i<retrieve.getImageCount(); i++)
        {
            Date newDate = retrieve.getImageAcquisitionDate(i).asDate();
            if (newDate != null)
            {
                if (min != null)
                {
                    min = newDate;
                }
                if (min.after(newDate))
                {
                    min = newDate;
                }
            }
        }
        return new DateTime(min);
    }
    
    public DateTime getEnd()
    {
        Date max = null;
        for (int i =0; i<retrieve.getImageCount(); i++)
        {
            Date newDate = retrieve.getImageAcquisitionDate(i).asDate();
            if (newDate != null)
            {
                if (max != null)
                {
                    max = newDate;
                }
                if (newDate.after(max))
                {
                    max = newDate;
                }
            }
        }
        return new DateTime(max);
    }
}
