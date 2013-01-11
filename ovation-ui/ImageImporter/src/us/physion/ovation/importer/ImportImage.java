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
import loci.formats.meta.MetadataRetrieve;
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
        return null;
    }

    @Override
    public void wizardFinished(WizardDescriptor wd, ovation.IAuthenticatedDataStoreCoordinator dsc, IEntityWrapper iew) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
