/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.importer;

import java.awt.Component;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.ByteOrder;
import java.util.*;
import loci.formats.meta.MetadataRetrieve;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import ovation.NumericDataFormat;
import ovation.NumericDataType;
import ovation.OvationException;
import us.physion.ovation.interfaces.BasicWizardPanel;

/**
 *
 * @author huecotanks
 */
public class GetImageFilesController extends BasicWizardPanel{

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new GetImageFilesPanel(changeSupport);
        }
        return component;
    }
    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        GetImageFilesPanel c = (GetImageFilesPanel)component;
        if (c != null)
        {
            List<FileMetadata> files = c.getFiles();
            return files.size() != 0;
        }
        return false;
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        GetImageFilesPanel c = (GetImageFilesPanel)component;
        List<FileMetadata> files = c.getFiles();
        
        //set the user-approved start and end times
        int count =0;
        for (FileMetadata f : files)
        {
            f.setStart(c.getStart(count));
            f.setEnd(c.getEnd(count++));
        }
        Collections.sort(files, new FileMetadataComparator());
        
        /*setInstrumentData(files, wiz);
        
        for (int i=0; i< files.size(); i++)
        {
            FileMetadata data = files.get(i);
            MetadataRetrieve retrieve = data.getMetadata();
            String epochName = "epoch" + i;
            wiz.putProperty(epochName + ".start", data.getStart());
            wiz.putProperty(epochName + ".end", data.getEnd());
            
            Map<String, Object> properties = new HashMap<String, Object>();
            Hashtable original = data.getOriginalMetadata();
            for (Object key : original.keySet())
            {
                properties.put("original." + key, original.get(key));
            }
            addMetadataProperties(properties, retrieve);
            wiz.putProperty(epochName + ".properties", properties);
            
            
            for (int j=0; j < retrieve.getImageCount(); j++)
            {
                String responseName = epochName + ".response" + j;
                setResponseProperties(responseName, retrieve, j, wiz);
                
                //TODO: get instrument ref and look it up that way
                //NOTE: I'm not using microbeamManipulation right now
                wiz.putProperty(responseName + ".device.name", retrieve.getInstrumentID(0));
                wiz.putProperty(responseName + ".device.manufacturer", retrieve.getMicroscopeManufacturer((0)));
                
                addDeviceParameters(responseName, retrieve, j, wiz);
                
                try {
                    wiz.putProperty(responseName + ".url", data.getFile().toURI().toURL().toExternalForm());
                } catch (MalformedURLException ex) {
                    throw new OvationException("Unable to get url for image file. " + ex.getMessage());
                }
                long[] shape = new long[]{retrieve.getPixelsSizeX(j).getValue().longValue(), 
                    retrieve.getPixelsSizeY(j).getValue().longValue(), 
                    retrieve.getPixelsSizeZ(j).getValue().longValue(), 
                    retrieve.getPixelsSizeC(j).getValue().longValue(), 
                    retrieve.getPixelsSizeT(j).getValue().longValue()};
                wiz.putProperty(responseName + ".shape", shape);
                //data type doesn't actually matter, since this is not a NumericData object
                ByteOrder b; 
                if (retrieve.getPixelsBinDataBigEndian(j, 0))
                {
                    b = ByteOrder.BIG_ENDIAN;
                }else
                {
                    b = ByteOrder.LITTLE_ENDIAN;
                }
                wiz.putProperty(responseName + ".dataType",  new NumericDataType(NumericDataFormat.SignedFixedPointDataType, (short)4, b));
                wiz.putProperty(responseName + ".units", "pixels");
                //TODO: add time!
                wiz.putProperty(responseName + ".dimensionLabels", new String[] {"X", "Y", "Z", "Channels", "Time"});
                double timeIncrement = retrieve.getPixelsTimeIncrement(j).doubleValue();
                wiz.putProperty(responseName + ".samplingRates", new double[] {
                    shape[0] == 0? 0:retrieve.getPixelsPhysicalSizeX(j).getValue()/shape[0],
                    shape[1] == 0? 0:retrieve.getPixelsPhysicalSizeY(j).getValue()/shape[1],
                    shape[2] == 0? 0:retrieve.getPixelsPhysicalSizeZ(j).getValue()/shape[2],
                    1,
                    timeIncrement ==0 ? 0:(1/timeIncrement)});
                wiz.putProperty(responseName + ".samplingRateUnits", new String[] {"microns", 
                    "microns", 
                    "frames", 
                    "channels",
                    "Hz"});//TODO not sure about seconds
                wiz.putProperty(responseName + ".uti", "public.bioFormats");//TODO: fix - get file type?
                
                //TODO: planes (represent images in time)
                //TODO: plates -- start and end time information?
            }
            
        }
        wiz.putProperty("epoch.files", files);
        // use wiz.putProperty to remember current panel state
    }

    private void addMetadataProperties(Map<String, Object> properties, MetadataRetrieve retrieve) {
        for (int i =0; i< retrieve.getDatasetCount(); i++)
        {
            put("dataset" + i + ".description", retrieve.getDatasetDescription(i), properties);
            put("dataset" + i + ".ID", retrieve.getDatasetID(i), properties);
            put("dataset" + i + ".name", retrieve.getDatasetName(i), properties);
            put("dataset" + i + ".imageRef", retrieve.getDatasetImageRefCount(i), properties);
        }
        
        for (int i =0; i< retrieve.getExperimenterCount(); i++)
        {
            String name = retrieve.getExperimenterFirstName(i) + retrieve.getExperimenterLastName(i); 
            put("experimenter" + i + ".username", retrieve.getExperimenterUserName(i), properties);
            put("experimenter" + i + ".ID", retrieve.getExperimenterID(i), properties);
            put("experimenter" + i + ".name", name, properties);
            put("experimenter" + i + ".email", retrieve.getExperimenterEmail(i), properties);
            put("experimenter" + i + ".institution", retrieve.getExperimenterInstitution(i), properties);
        }
    }

    private void setInstrumentData(List<FileMetadata> files, WizardDescriptor wiz) {
        for (int i=0; i< files.size(); i++)
        {
            FileMetadata data = files.get(i);
            MetadataRetrieve retrieve = data.getMetadata();
            for (int j =0; j< retrieve.getInstrumentCount(); j++)
            {
                String deviceName = "device_" + retrieve.getInstrumentID(j);
                put(deviceName + ".ID", retrieve.getInstrumentID(j), wiz);
                put(deviceName + ".microscopeLotNumber", retrieve.getMicroscopeLotNumber(j), wiz);
                put(deviceName + ".microscopeManufacturer", retrieve.getMicroscopeManufacturer(j), wiz);
                put(deviceName + ".microscopeModel", retrieve.getMicroscopeModel(j), wiz);
                put(deviceName + ".microscopeSerialNumber", retrieve.getMicroscopeSerialNumber(j), wiz);
                put(deviceName + ".microscopeType", retrieve.getMicroscopeType(j), wiz);

                
                for (int k =0; k< retrieve.getLightSourceCount(j); k++)
                {
                    String arcName = deviceName + ".arc_" + retrieve.getArcID(j, k);
                    put(arcName + ".ID", retrieve.getArcID(j, k), wiz);
                    put(arcName + ".lotNumber", retrieve.getArcLotNumber(j, k), wiz);
                    put(arcName + ".manufacturer", retrieve.getArcManufacturer(j, k), wiz);
                    put(arcName + ".model", retrieve.getArcModel(j, k), wiz);
                    put(arcName + ".serialNumber", retrieve.getArcSerialNumber(j, k), wiz);
                    put(arcName + ".type", retrieve.getArcType(j, k), wiz);
                    
                    String filamentName = deviceName + ".filament_" + retrieve.getFilamentID(j, k);
                    put(filamentName + ".ID", retrieve.getFilamentID(j, k), wiz);
                    put(filamentName + ".lotNumber", retrieve.getFilamentLotNumber(j, k), wiz);
                    put(filamentName + ".manufacturer", retrieve.getFilamentManufacturer(j, k), wiz);
                    put(filamentName + ".model", retrieve.getFilamentModel(j, k), wiz);
                    put(filamentName + ".serialNumber", retrieve.getFilamentSerialNumber(j, k), wiz);
                    put(filamentName + ".type", retrieve.getFilamentType(j, k), wiz);
                    
                    String laserName = deviceName + ".laser_" + retrieve.getLaserID(j, k);
                    put(laserName + ".ID", retrieve.getLaserID(j, k), wiz);
                    put(laserName + ".medium", retrieve.getLaserLaserMedium(j, k), wiz);
                    put(laserName + ".lotNumber", retrieve.getLaserLotNumber(j, k), wiz);
                    put(laserName + ".manufacturer", retrieve.getLaserManufacturer(j, k), wiz);
                    put(laserName + ".model", retrieve.getLaserModel(j, k), wiz);
                    put(laserName + ".serialNumber", retrieve.getLaserSerialNumber(j, k), wiz);
                    put(laserName + ".tuneable", retrieve.getLaserTuneable(j, k), wiz);
                    put(laserName + ".type", retrieve.getLaserType(j, k), wiz);
                    
                    String lightEmittingDiodeName = deviceName + ".lightEmittingDiodeName_" + retrieve.getLightEmittingDiodeID(j, k);
                    put(lightEmittingDiodeName + ".ID", retrieve.getLightEmittingDiodeID(j, k), wiz);
                    put(lightEmittingDiodeName + ".lotNumber", retrieve.getLightEmittingDiodeLotNumber(j, k), wiz);
                    put(lightEmittingDiodeName + ".manufacturer", retrieve.getLightEmittingDiodeManufacturer(j, k), wiz);
                    put(lightEmittingDiodeName + ".model", retrieve.getLightEmittingDiodeModel(j, k), wiz);
                    put(lightEmittingDiodeName + ".serialNumber", retrieve.getLightEmittingDiodeSerialNumber(j, k), wiz);
                    
                    put(deviceName + ".lightSourceType", retrieve.getLightSourceType(j, k), wiz);
                }
                
                for (int k =0; k< retrieve.getObjectiveCount(j); k++)
                {
                    String objName = deviceName + ".objective_" + retrieve.getObjectiveID(j, k);
                    put(objName + ".ID", retrieve.getObjectiveID(j, k), wiz);
                    put(objName + ".lotNumber", retrieve.getObjectiveLotNumber(j, k), wiz);
                    put(objName + ".manufacturer", retrieve.getObjectiveManufacturer(j, k), wiz);
                    put(objName + ".model", retrieve.getObjectiveModel(j, k), wiz);
                    put(objName + ".serialNumber", retrieve.getObjectiveSerialNumber(j, k), wiz);
                }
                
                for (int k =0; k< retrieve.getFilterCount(j); k++)
                {
                    String filterName = deviceName + ".filter_" + retrieve.getFilterID(j, k);
                    put(filterName + ".wheel", retrieve.getFilterFilterWheel(j, k), wiz);
                    put(filterName + ".ID", retrieve.getFilterID(j, k), wiz);
                    put(filterName + ".lotNumber", retrieve.getFilterLotNumber(j, k), wiz);
                    put(filterName + ".model", retrieve.getFilterModel(j, k), wiz);
                    put(filterName + ".serialNumber", retrieve.getFilterSerialNumber(j, k), wiz);
                    put(filterName + ".manufacturer", retrieve.getFilterManufacturer(j, k), wiz);
                    put(filterName + ".type", retrieve.getFilterType(j, k), wiz);
                }
                
                for (int k =0; k< retrieve.getDetectorCount(j); k++)
                {
                    String filterName = deviceName + ".detector_" + retrieve.getDetectorID(j, k);
                    put(filterName + ".ID", retrieve.getDetectorID(j, k), wiz);
                    put(filterName + ".lotNumber", retrieve.getDetectorLotNumber(j, k), wiz);
                    put(filterName + ".model", retrieve.getDetectorModel(j, k), wiz);
                    put(filterName + ".manufacturer", retrieve.getDetectorManufacturer(j, k), wiz);
                    put(filterName + ".serialNumber", retrieve.getDetectorSerialNumber(j, k), wiz);
                    put(filterName + ".type", retrieve.getDetectorType(j, k), wiz);
                }
                
                for (int k =0; k< retrieve.getDichroicCount(j); k++)
                {
                    String filterName = deviceName + ".dichroic_" + retrieve.getDichroicID(j, k);
                    put(filterName + ".ID", retrieve.getDichroicID(j, k), wiz);
                    put(filterName + ".lotNumber", retrieve.getDichroicLotNumber(j, k), wiz);
                    put(filterName + ".manufacturer", retrieve.getDichroicManufacturer(j, k), wiz);
                    put(filterName + ".model", retrieve.getDichroicModel(j, k), wiz);
                    put(filterName + ".serialNumber", retrieve.getDichroicSerialNumber(j, k), wiz);
                }
            }
        }
    }

    private void put(String name, Object value, WizardDescriptor wiz) {
        if (value != null)
        {
            //TODO: handle Enums, PositiveIntegers, etc
            wiz.putProperty(name, value);
        }
    }
    
    private void put(String name, Object value, Map<String, Object> map) {
        if (value != null)
        {
            //TODO: handle Enums, PositiveIntegers, etc
            map.put(name, value);
        }
    }

    private void addDeviceParameters(String responseName, MetadataRetrieve retrieve, int imageNum, WizardDescriptor wiz) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        put("imageName", retrieve.getImageName(imageNum), parameters);
        put("imageDescription", retrieve.getImageDescription(imageNum), parameters);
        put("imageID", retrieve.getImageID(imageNum), parameters);
        put("imagingEnvironment.airPressure", retrieve.getImagingEnvironmentAirPressure(imageNum), parameters);
        put("imagingEnvironment.CO2Percent", retrieve.getImagingEnvironmentCO2Percent(imageNum), parameters);
        put("imagingEnvironment.humidity", retrieve.getImagingEnvironmentHumidity(imageNum), parameters);
        put("imagingEnvironment.temperature", retrieve.getImagingEnvironmentTemperature(imageNum), parameters);
        put("stageLabel.name", retrieve.getStageLabelName(imageNum), parameters);
        put("stageLabel.x", retrieve.getStageLabelX(imageNum), parameters);
        put("stageLabel.y", retrieve.getStageLabelY(imageNum), parameters);
        put("stageLabel.z", retrieve.getStageLabelZ(imageNum), parameters);
        
        String obj = "objective";
        put(obj + ".settingsID", retrieve.getObjectiveSettingsID(imageNum), wiz);
        put(obj + ".settingsMedium", retrieve.getObjectiveSettingsMedium(imageNum), wiz);
        put(obj + ".settingsRefractiveIndex", retrieve.getObjectiveSettingsRefractiveIndex(imageNum), wiz);
        put(obj + ".settingsCorrectionCollar", retrieve.getObjectiveSettingsCorrectionCollar(imageNum), wiz);
        
        for (int i=0; i< retrieve.getChannelCount(imageNum); i++)
        {
            String channelID = "channel_" + retrieve.getChannelID(imageNum, i);
            put(channelID + ".acqisitionMode", retrieve.getChannelAcquisitionMode(imageNum, i), parameters);
            put(channelID + ".color", retrieve.getChannelColor(imageNum, i), parameters);
            put(channelID + ".emissionWavelength", retrieve.getChannelEmissionWavelength(imageNum, i), parameters);
            put(channelID + ".excitationWavelength", retrieve.getChannelExcitationWavelength(imageNum, i), parameters);
            put(channelID + ".illuminationType", retrieve.getChannelIlluminationType(imageNum, i), parameters);
            put(channelID + ".fluor", retrieve.getChannelFluor(imageNum, i), parameters);
            put(channelID + ".ID", retrieve.getChannelID(imageNum, i), parameters);
            put(channelID + ".lightSourceSettingsID", retrieve.getChannelLightSourceSettingsID(imageNum, i), parameters);
            put(channelID + ".lightSourceSettingsAttenuation", retrieve.getChannelLightSourceSettingsAttenuation(imageNum, i), parameters);
            put(channelID + ".lightSourceSettingsWavelength", retrieve.getChannelLightSourceSettingsWavelength(imageNum, i), parameters);
            put(channelID + ".name", retrieve.getChannelName(imageNum, i), parameters);
            put(channelID + ".NDFilter", retrieve.getChannelNDFilter(imageNum, i), parameters);
            put(channelID + ".pinholeSize", retrieve.getChannelPinholeSize(imageNum, i), parameters);
            put(channelID + ".pockelCellSetting", retrieve.getChannelPockelCellSetting(imageNum, i), parameters);
            put(channelID + ".samplesPerPixel", retrieve.getChannelSamplesPerPixel(imageNum, i), parameters);
            
            String channelDetector = channelID + ".detector";
            put(channelDetector + ".settingsBinning", retrieve.getDetectorSettingsBinning(imageNum, i), wiz);//TODO: do they really mean channel number?
            put(channelDetector + ".settingsGain", retrieve.getDetectorSettingsGain(imageNum, i), wiz);//TODO: do they really mean channel number?
            put(channelDetector + ".settingsID", retrieve.getDetectorSettingsID(imageNum, i), wiz);//TODO: do they really mean channel number?
            put(channelDetector + ".settingsOffset", retrieve.getDetectorSettingsOffset(imageNum, i), wiz);//TODO: do they really mean channel number?
            put(channelDetector + ".settingsReadOutRate", retrieve.getDetectorSettingsReadOutRate(imageNum, i), wiz);//TODO: do they really mean channel number?
            put(channelDetector + ".settingsVoltage", retrieve.getDetectorSettingsVoltage(imageNum, i), wiz);//TODO: do they really mean channel number?
            
        }
        
        for (int i = 0; i< retrieve.getMicrobeamManipulationCount(imageNum); i++)
        {
            put("microbeamManipulation" + i+ ".ID", retrieve.getMicrobeamManipulationID(imageNum, i), parameters);
            put("microbeamManipulation" + i+ ".description", retrieve.getMicrobeamManipulationDescription(imageNum, i), parameters);
            put("microbeamManipulation" + i+ ".type", retrieve.getMicrobeamManipulationType(imageNum, i), parameters);
            
            //for each light source ?
            //put("microbeamManipulation" + i+"."+ "lightSourceSettingsAttenuation", retrieve.getMicrobeamManipulationLightSourceSettingsAttenuation(imageNumber, i), parameters);
            //put("microbeamManipulation" + i+"."+ "lightSourceSettingsID", retrieve.getMicrobeamManipulationLightSourceSettingsID(imageNumber, i), parameters);
            //put("microbeamManipulation" + i+"."+ "lightSourceSettingnWavelength", retrieve.getMicrobeamManipulationLightSourceSettingsWavelength(imageNumber, i), parameters);
            
        }

        for (int k = 0; k < retrieve.getLightSourceCount(imageNum); k++) {
            String arcName = "arc_" + retrieve.getArcID(imageNum, k);
            put(arcName + ".power", retrieve.getArcPower(imageNum, k), wiz);

            String filamentName = "filament_" + retrieve.getFilamentID(imageNum, k);
            put(filamentName + ".power", retrieve.getFilamentPower(imageNum, k), wiz);

            String laserName = ".laser_" + retrieve.getLaserID(imageNum, k);
            put(laserName + ".frequencyMultiplication", retrieve.getLaserFrequencyMultiplication(imageNum, k), wiz);
            put(laserName + ".medium", retrieve.getLaserLaserMedium(imageNum, k), wiz);
            put(laserName + ".pockelCell", retrieve.getLaserPockelCell(imageNum, k), wiz);
            put(laserName + ".power", retrieve.getLaserPower(imageNum, k), wiz);
            put(laserName + ".pump", retrieve.getLaserPump(imageNum, k), wiz);
            put(laserName + ".repetitionRate", retrieve.getLaserRepetitionRate(imageNum, k), wiz);
            put(laserName + ".tuneable", retrieve.getLaserTuneable(imageNum, k), wiz);
            put(laserName + ".wavelength", retrieve.getLaserWavelength(imageNum, k), wiz);

            String lightEmittingDiodeName = "lightEmittingDiodeName_" + retrieve.getLightEmittingDiodeID(imageNum, k);
            put(lightEmittingDiodeName + ".power", retrieve.getLightEmittingDiodePower(imageNum, k), wiz);

            put("lightSourceType", retrieve.getLightSourceType(imageNum, k), wiz);
        }

        for (int k = 0; k < retrieve.getObjectiveCount(imageNum); k++) {
            String objName = "objective_" + retrieve.getObjectiveID(imageNum, k);
            put(objName + ".calibratedMagnification", retrieve.getObjectiveCalibratedMagnification(imageNum, k), wiz);
            put(objName + ".correction", retrieve.getObjectiveCorrection(imageNum, k), wiz);
            put(objName + ".immersion", retrieve.getObjectiveImmersion(imageNum, k), wiz);
            put(objName + ".iris", retrieve.getObjectiveIris(imageNum, k), wiz);
            put(objName + ".lensNA", retrieve.getObjectiveLensNA(imageNum, k), wiz);
            put(objName + ".nominalMagnification", retrieve.getObjectiveNominalMagnification(imageNum, k), wiz);
            put(objName + ".workingDistance", retrieve.getObjectiveWorkingDistance(imageNum, k), wiz);
        }

        for (int k = 0; k < retrieve.getFilterCount(imageNum); k++) {
            String filterName = "filter_" + retrieve.getFilterID(imageNum, k);
            put(filterName + ".transmittanceRangeCutIn", retrieve.getTransmittanceRangeCutIn(imageNum, k), wiz);
            put(filterName + ".transmittanceRangeCutInTolerance", retrieve.getTransmittanceRangeCutInTolerance(imageNum, k), wiz);
            put(filterName + ".transmittanceRangeCutOut", retrieve.getTransmittanceRangeCutOut(imageNum, k), wiz);
            put(filterName + ".transmittanceRangeCutOutTolerance", retrieve.getTransmittanceRangeCutOutTolerance(imageNum, k), wiz);
            put(filterName + ".transmittanceRangeTransmittance", retrieve.getTransmittanceRangeTransmittance(imageNum, k), wiz);

            put(filterName + ".wheel", retrieve.getFilterFilterWheel(imageNum, k), wiz);
        }

        for (int k = 0; k < retrieve.getDetectorCount(imageNum); k++) {
            String filterName = "detector_" + retrieve.getDetectorID(imageNum, k);
            put(filterName + ".amplificationGain", retrieve.getDetectorAmplificationGain(imageNum, k), wiz);
            put(filterName + ".gain", retrieve.getDetectorGain(imageNum, k), wiz);
            put(filterName + ".offset", retrieve.getDetectorOffset(imageNum, k), wiz);
            put(filterName + ".voltage", retrieve.getDetectorVoltage(imageNum, k), wiz);
            put(filterName + ".zoom", retrieve.getDetectorZoom(imageNum, k), wiz);
        }

        wiz.putProperty(responseName + ".deviceParameters", parameters);
    }

    private void setResponseProperties(String responseName, MetadataRetrieve retrieve, int imageNumber, WizardDescriptor wiz) {
        Map<String, Object> properties = new HashMap<String, Object>();
        put("imageName", retrieve.getImageName(imageNumber), properties);
        put("imageDescription", retrieve.getImageDescription(imageNumber), properties);
        put("imageID", retrieve.getImageID(imageNumber), properties);
        
        wiz.putProperty(responseName + ".properties", properties);*/
    }
}
