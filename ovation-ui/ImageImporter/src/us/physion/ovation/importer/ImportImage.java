/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.importer;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import org.joda.time.DateTime;
import org.openide.util.lookup.ServiceProvider;
import ovation.*;

@ServiceProvider(service = RootInsertable.class)
/**
 *
 * @author huecotanks
 */
public class ImportImage extends InsertEntity implements RootInsertable
{
    public ImportImage()
    {
        putValue(NAME, "Import Image...");
    }
    
    @Override
    public int getPosition() {
        return 101;
    }
    
    @Override
    public List<Panel<WizardDescriptor>> getPanels(IEntityWrapper iew) {
        List<Panel<WizardDescriptor>> panels = new ArrayList<Panel<WizardDescriptor>>();
        panels.add(new GetImageFilesController());
        //panels.add(new EpochDetailPanel());
        //panels.add(new ResponseDetailPanel());
        return panels;
    }

    @Override
    public void wizardFinished(WizardDescriptor wd, ovation.IAuthenticatedDataStoreCoordinator dsc, IEntityWrapper iew) {
        EpochGroup eg = ((EpochGroup)iew.getEntity());
        Experiment exp = eg.getExperiment();
        int i =0;
        for (;;)
        {
            String device = "device" + String.valueOf(i++);
            if (wd.getProperty(device + ".name") != null)
            {   
                //name, manufacturer, properties
                ExternalDevice dev = exp.externalDevice((String)wd.getProperty(device + ".name"), (String)wd.getProperty("device.manufacturer"));
                Map<String, Object> properties = (Map<String, Object>)wd.getProperty(device + ".properties");
                for (String key : properties.keySet())
                {
                    dev.addProperty(key, properties.get(key));
                }
            }else{
                break;
            }
        }
        
        i =0;
        for (;;)
        {
            String protocolID = (String)wd.getProperty("epoch.protocolID");
            Map<String, Object> protocolParameters = (Map<String, Object>) wd.getProperty("epoch.protocolParameters");
            String deviceManufacturer = (String)wd.getProperty("device.manufacturer");    
            
            String epochName = "epoch" + String.valueOf(i++);
            if (wd.getProperty(epochName + ".start") != null)
            {
                DateTime start = (DateTime)wd.getProperty(epochName + ".start");
                DateTime end = (DateTime)wd.getProperty(epochName + ".end");
                Map<String, Object> epochProperties = (Map<String, Object>) wd.getProperty(epochName + ".properties");
                
                Epoch e = eg.insertEpoch(start, end, protocolID, protocolParameters);
                
                String responseName = epochName + ".response";
                String deviceName = (String)wd.getProperty(responseName + ".device.name");
                Map<String, Object> deviceParameters = (Map<String, Object>)wd.getProperty(responseName + ".device.parameters");
                String url = (String)wd.getProperty(responseName + ".url");
                long[] shape = (long[])wd.getProperty(responseName + ".shape");
                NumericDataType type = (NumericDataType)wd.getProperty(responseName + ".dataType");
                String units = (String)wd.getProperty(responseName + ".units");
                String[] dimensionLabels = (String[])wd.getProperty(responseName + ".dimensionLabels");
                double[] samplingRates = (double[])wd.getProperty(responseName + ".samplingRates");
                String[] samplingRateUnits = (String[])wd.getProperty(responseName + ".samplingRateUnits");
                String uti = (String)wd.getProperty(responseName + ".uti");

                
                Response r = e.insertURLResponse(exp.externalDevice(deviceName, deviceManufacturer),
                        deviceParameters,
                        url,
                        shape,
                        type,
                        units,
                        dimensionLabels,
                        samplingRates,
                        samplingRateUnits,
                        uti);
            }
            else{
                break;
            }
        }
    }
}
