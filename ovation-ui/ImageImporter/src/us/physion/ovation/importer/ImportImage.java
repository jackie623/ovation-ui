/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.importer;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import us.physion.ovation.interfaces.*;
import loci.common.DateTools;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.in.PrairieReader;
import loci.formats.FormatReader;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.meta.IMetadata;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;
/**
 *
 * @author huecotanks
 */
public class ImportImage extends InsertEntity implements ProjectInsertable
{
    @Override
    public int getPosition() {
        return 101;
    }
    
    @Override
    public List<Panel<WizardDescriptor>> getPanels(IEntityWrapper iew) {
        ServiceFactory factory = null;
        IMetadata meta = null;
        try {
            factory = new ServiceFactory();

            OMEXMLService service = factory.getInstance(OMEXMLService.class);
            try {
                meta = service.createOMEXMLMetadata();
            } catch (ServiceException ex) {
                Logger.getLogger(ImportImage.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (DependencyException ex) {
            Logger.getLogger(ImportImage.class.getName()).log(Level.SEVERE, null, ex);
        }

        // create format reader
        IFormatReader reader = new ImageReader();
        reader.setMetadataStore(meta);
        try {
            // initialize file
            reader.setId("file://Users.huecotanks/Downloads/test-dicom.dcm");
        } catch (FormatException ex) {
            Logger.getLogger(ImportImage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ImportImage.class.getName()).log(Level.SEVERE, null, ex);
        }

        reader.getGlobalMetadata();
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void wizardFinished(WizardDescriptor wd, ovation.IAuthenticatedDataStoreCoordinator dsc, IEntityWrapper iew) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
