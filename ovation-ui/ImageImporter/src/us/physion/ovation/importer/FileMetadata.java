/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.importer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.ByteOrder;
import java.util.*;
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
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import ovation.NumericDataFormat;
import ovation.NumericDataType;
import ovation.OvationException;

/**
 *
 * @author huecotanks
 */
public class FileMetadata {

    File file;
    MetadataRetrieve retrieve;
    DateTime start;
    DateTime end;
    Map<String, Object> epochProperties;
    List<Map<String, Object>> instruments;
    List<Map<String, Object>> responses;

    FileMetadata(File f) {
        file = f;
        ServiceFactory factory = null;
        OMEXMLService service = null;
        IMetadata meta = null;
        try {
            //LoggerFactory l;
            //LoggerFactory.getLogger(FileMetadata.class);
            //ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();
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
        IFormatReader r = new ImageReader(); //maybe checkbox for prairie images?

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
        Hashtable original;
        try {
            original = service.getOriginalMetadata(service.createOMEXMLMetadata());
        } catch (ServiceException ex) {
            throw new OvationException("Unable to read original metadata. " + ex.getMessage());
        }
        retrieve = service.asRetrieve(r.getMetadataStore());

        parseRetrieve(retrieve, original);
    }

    public File getFile() {
        return file;
    }

    public DateTime getStart() {
        if (start != null) {
            return start;
        }

        Date min = null;
        for (int i = 0; i < retrieve.getImageCount(); i++) {
            Date newDate = retrieve.getImageAcquisitionDate(i).asDate();
            if (newDate != null) {
                if (min == null) {
                    min = newDate;
                }
                if (min.after(newDate)) {
                    min = newDate;
                }
            }
        }
        return new DateTime(min);
    }

    public DateTime getEnd() {
        if (end != null) {
            return end;
        }

        Date max = null;
        for (int i = 0; i < retrieve.getImageCount(); i++) {
            Date newDate = retrieve.getImageAcquisitionDate(i).asDate();
            if (newDate != null) {
                if (max == null) {
                    max = newDate;
                }
                if (newDate.after(max)) {
                    max = newDate;
                }
            }
        }
        return new DateTime(max);
    }

    public void setStart(DateTime s) {
        start = s;
    }

    public void setEnd(DateTime s) {
        end = s;
    }

    public Map<String, Object> getEpochProperties() {
        return epochProperties;
    }
    
    public List<Map<String, Object>> getDevices() {
        return instruments;
    }

    public List<Map<String, Object>> getResponses() {
        return responses;
    }
    
    private void parseRetrieve(MetadataRetrieve retrieve, Hashtable original) {
        instruments = getInstrumentData();

        epochProperties = new HashMap<String, Object>();
        if (original != null) {
            for (Object key : original.keySet()) {
                epochProperties.put("original." + key, original.get(key));
            }
        }
        addMetadataProperties(epochProperties, retrieve);

        responses = new ArrayList<Map<String, Object>>();

        for (int j = 0; j < retrieve.getImageCount(); j++) {
            Map<String, Object> responseStruct = new HashMap<String, Object>();
            put("name", "response" + j, responseStruct);
            put("properties", getResponseProperties(j), responseStruct);

            //TODO: get instrument ref and look it up that way
            //NOTE: I'm not using microbeamManipulation right now
            put("device.name", retrieve.getInstrumentID(0), responseStruct);
            //put("device.manufacturer", retrieve.getMicroscopeManufacturer(0), responseStruct);

            put("device.parameters", getDeviceParameters(j), responseStruct);

            try {
                put("url", getFile().toURI().toURL().toExternalForm(), responseStruct);
            } catch (MalformedURLException ex) {
                throw new OvationException("Unable to get url for image file. " + ex.getMessage());
            }
            long[] shape = new long[]{retrieve.getPixelsSizeX(j).getValue().longValue(),
                retrieve.getPixelsSizeY(j).getValue().longValue(),
                retrieve.getPixelsSizeZ(j).getValue().longValue(),
                retrieve.getPixelsSizeC(j).getValue().longValue(),
                retrieve.getPixelsSizeT(j).getValue().longValue()};
            put("shape", shape, responseStruct);
            //data type doesn't actually matter, since this is not a NumericData object
            ByteOrder b;
            if (retrieve.getPixelsBinDataBigEndian(j, 0)) {
                b = ByteOrder.BIG_ENDIAN;
            } else {
                b = ByteOrder.LITTLE_ENDIAN;
            }
            put("dataType", new NumericDataType(NumericDataFormat.SignedFixedPointDataType, (short) 4, b), responseStruct);
            put("units", "pixels", responseStruct);

            put("dimensionLabels", new String[]{"X", "Y", "Z", "Channels", "Time"}, responseStruct);
            double timeIncrement = retrieve.getPixelsTimeIncrement(j).doubleValue();
            put("samplingRates", new double[]{
                        shape[0] == 0 ? 0 : retrieve.getPixelsPhysicalSizeX(j).getValue() / shape[0],
                        shape[1] == 0 ? 0 : retrieve.getPixelsPhysicalSizeY(j).getValue() / shape[1],
                        shape[2] == 0 ? 0 : retrieve.getPixelsPhysicalSizeZ(j).getValue() / shape[2],
                        1,
                        timeIncrement == 0 ? 0 : (1 / timeIncrement)}, responseStruct);
            put("samplingRateUnits", new String[]{"microns",
                        "microns",
                        "frames",
                        "channels",
                        "Hz"}, responseStruct);//TODO not sure about Hz
            put("uti", "public.bioFormats", responseStruct);//TODO: fix - get file type?

            //TODO: planes (represent images in time)
            //TODO: plates -- start and end time information?

            responses.add(responseStruct);
        }
    }

    private Map<String, Object> getDeviceParameters(int imageNum) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        put("imageName", retrieve.getImageName(imageNum), parameters);
        put("imageDescription", retrieve.getImageDescription(imageNum), parameters);
        put("imageID", retrieve.getImageID(imageNum), parameters);
        
        try{ put("imagingEnvironment.airPressure", retrieve.getImagingEnvironmentAirPressure(imageNum), parameters);
        } catch (NullPointerException e){}
        try{put("imagingEnvironment.CO2Percent", retrieve.getImagingEnvironmentCO2Percent(imageNum), parameters);
        } catch (NullPointerException e){}
        try{put("imagingEnvironment.humidity", retrieve.getImagingEnvironmentHumidity(imageNum), parameters);
        } catch (NullPointerException e){}
        try{put("imagingEnvironment.temperature", retrieve.getImagingEnvironmentTemperature(imageNum), parameters);
        } catch (NullPointerException e){}
        try{
            put("stageLabel.name", retrieve.getStageLabelName(imageNum), parameters);
            put("stageLabel.x", retrieve.getStageLabelX(imageNum), parameters);
            put("stageLabel.y", retrieve.getStageLabelY(imageNum), parameters);
            put("stageLabel.z", retrieve.getStageLabelZ(imageNum), parameters);
        } catch (NullPointerException e){}
        
        try {
            String obj = "objective";
            put(obj + ".settingsID", retrieve.getObjectiveSettingsID(imageNum), parameters);
            put(obj + ".settingsMedium", retrieve.getObjectiveSettingsMedium(imageNum), parameters);
            put(obj + ".settingsRefractiveIndex", retrieve.getObjectiveSettingsRefractiveIndex(imageNum), parameters);
            put(obj + ".settingsCorrectionCollar", retrieve.getObjectiveSettingsCorrectionCollar(imageNum), parameters);
        } catch (NullPointerException e) {}
        
        for (int i = 0; i < retrieve.getChannelCount(imageNum); i++) {
            String channelID = "channel_" + retrieve.getChannelID(imageNum, i);
            put(channelID + ".acqisitionMode", retrieve.getChannelAcquisitionMode(imageNum, i), parameters);
            put(channelID + ".color", retrieve.getChannelColor(imageNum, i), parameters);
            put(channelID + ".emissionWavelength", retrieve.getChannelEmissionWavelength(imageNum, i), parameters);
            put(channelID + ".excitationWavelength", retrieve.getChannelExcitationWavelength(imageNum, i), parameters);
            put(channelID + ".illuminationType", retrieve.getChannelIlluminationType(imageNum, i), parameters);
            put(channelID + ".fluor", retrieve.getChannelFluor(imageNum, i), parameters);
            put(channelID + ".ID", retrieve.getChannelID(imageNum, i), parameters);
            
            try{
                put(channelID + ".lightSourceSettingsID", retrieve.getChannelLightSourceSettingsID(imageNum, i), parameters);
                put(channelID + ".lightSourceSettingsAttenuation", retrieve.getChannelLightSourceSettingsAttenuation(imageNum, i), parameters);
                put(channelID + ".lightSourceSettingsWavelength", retrieve.getChannelLightSourceSettingsWavelength(imageNum, i), parameters);
            } catch (NullPointerException e){}
            
            put(channelID + ".name", retrieve.getChannelName(imageNum, i), parameters);
            put(channelID + ".NDFilter", retrieve.getChannelNDFilter(imageNum, i), parameters);
            put(channelID + ".pinholeSize", retrieve.getChannelPinholeSize(imageNum, i), parameters);
            put(channelID + ".pockelCellSetting", retrieve.getChannelPockelCellSetting(imageNum, i), parameters);
            put(channelID + ".samplesPerPixel", retrieve.getChannelSamplesPerPixel(imageNum, i), parameters);

            try {
                String channelDetector = channelID + ".detector";
                put(channelDetector + ".settingsBinning", retrieve.getDetectorSettingsBinning(imageNum, i), parameters);//TODO: do they really mean channel number?
                put(channelDetector + ".settingsGain", retrieve.getDetectorSettingsGain(imageNum, i), parameters);//TODO: do they really mean channel number?
                put(channelDetector + ".settingsID", retrieve.getDetectorSettingsID(imageNum, i), parameters);//TODO: do they really mean channel number?
                put(channelDetector + ".settingsOffset", retrieve.getDetectorSettingsOffset(imageNum, i), parameters);//TODO: do they really mean channel number?
                put(channelDetector + ".settingsReadOutRate", retrieve.getDetectorSettingsReadOutRate(imageNum, i), parameters);//TODO: do they really mean channel number?
                put(channelDetector + ".settingsVoltage", retrieve.getDetectorSettingsVoltage(imageNum, i), parameters);//TODO: do they really mean channel number?
            } catch (NullPointerException e) {}
        }

        for (int i = 0; i < retrieve.getMicrobeamManipulationCount(imageNum); i++) {
            put("microbeamManipulation" + i + ".ID", retrieve.getMicrobeamManipulationID(imageNum, i), parameters);
            put("microbeamManipulation" + i + ".description", retrieve.getMicrobeamManipulationDescription(imageNum, i), parameters);
            put("microbeamManipulation" + i + ".type", retrieve.getMicrobeamManipulationType(imageNum, i), parameters);

            //for each light source ?
            //put("microbeamManipulation" + i+"."+ "lightSourceSettingsAttenuation", retrieve.getMicrobeamManipulationLightSourceSettingsAttenuation(imageNumber, i), parameters);
            //put("microbeamManipulation" + i+"."+ "lightSourceSettingsID", retrieve.getMicrobeamManipulationLightSourceSettingsID(imageNumber, i), parameters);
            //put("microbeamManipulation" + i+"."+ "lightSourceSettingnWavelength", retrieve.getMicrobeamManipulationLightSourceSettingsWavelength(imageNumber, i), parameters);

        }

        for (int k = 0; k < retrieve.getLightSourceCount(imageNum); k++) {
            String type = retrieve.getLightSourceType(imageNum, k).toLowerCase();
            
            if (type.equals("arc"))
            {
                String arcName = "arc_" + retrieve.getArcID(imageNum, k);
                put(arcName + ".power", retrieve.getArcPower(imageNum, k), parameters);
            }
            if (type.equals("filament"))
            {
                String filamentName = "filament_" + retrieve.getFilamentID(imageNum, k);
                put(filamentName + ".power", retrieve.getFilamentPower(imageNum, k), parameters);
            }
            
            if (type.equals("laser"))
            {
                String laserName = ".laser_" + retrieve.getLaserID(imageNum, k);
                put(laserName + ".frequencyMultiplication", retrieve.getLaserFrequencyMultiplication(imageNum, k), parameters);
                put(laserName + ".medium", retrieve.getLaserLaserMedium(imageNum, k), parameters);
                put(laserName + ".pockelCell", retrieve.getLaserPockelCell(imageNum, k), parameters);
                put(laserName + ".power", retrieve.getLaserPower(imageNum, k), parameters);
                put(laserName + ".pump", retrieve.getLaserPump(imageNum, k), parameters);
                put(laserName + ".repetitionRate", retrieve.getLaserRepetitionRate(imageNum, k), parameters);
                put(laserName + ".tuneable", retrieve.getLaserTuneable(imageNum, k), parameters);
                put(laserName + ".wavelength", retrieve.getLaserWavelength(imageNum, k), parameters);
            }
            
            if(type.equals("lightEmittingDiodeName"))
            {
                String lightEmittingDiodeName = "lightEmittingDiodeName_" + retrieve.getLightEmittingDiodeID(imageNum, k);
                put(lightEmittingDiodeName + ".power", retrieve.getLightEmittingDiodePower(imageNum, k), parameters);
            }
        }

        for (int k = 0; k < retrieve.getObjectiveCount(imageNum); k++) {
            String objName = "objective_" + retrieve.getObjectiveID(imageNum, k);
            put(objName + ".calibratedMagnification", retrieve.getObjectiveCalibratedMagnification(imageNum, k), parameters);
            put(objName + ".correction", retrieve.getObjectiveCorrection(imageNum, k), parameters);
            put(objName + ".immersion", retrieve.getObjectiveImmersion(imageNum, k), parameters);
            put(objName + ".iris", retrieve.getObjectiveIris(imageNum, k), parameters);
            put(objName + ".lensNA", retrieve.getObjectiveLensNA(imageNum, k), parameters);
            put(objName + ".nominalMagnification", retrieve.getObjectiveNominalMagnification(imageNum, k), parameters);
            put(objName + ".workingDistance", retrieve.getObjectiveWorkingDistance(imageNum, k), parameters);
        }

        for (int k = 0; k < retrieve.getFilterCount(imageNum); k++) {
            String filterName = "filter_" + retrieve.getFilterID(imageNum, k);
            put(filterName + ".transmittanceRangeCutIn", retrieve.getTransmittanceRangeCutIn(imageNum, k), parameters);
            put(filterName + ".transmittanceRangeCutInTolerance", retrieve.getTransmittanceRangeCutInTolerance(imageNum, k), parameters);
            put(filterName + ".transmittanceRangeCutOut", retrieve.getTransmittanceRangeCutOut(imageNum, k), parameters);
            put(filterName + ".transmittanceRangeCutOutTolerance", retrieve.getTransmittanceRangeCutOutTolerance(imageNum, k), parameters);
            put(filterName + ".transmittanceRangeTransmittance", retrieve.getTransmittanceRangeTransmittance(imageNum, k), parameters);

            put(filterName + ".wheel", retrieve.getFilterFilterWheel(imageNum, k), parameters);
        }

        for (int k = 0; k < retrieve.getDetectorCount(imageNum); k++) {
            String filterName = "detector_" + retrieve.getDetectorID(imageNum, k);
            put(filterName + ".amplificationGain", retrieve.getDetectorAmplificationGain(imageNum, k), parameters);
            put(filterName + ".gain", retrieve.getDetectorGain(imageNum, k), parameters);
            put(filterName + ".offset", retrieve.getDetectorOffset(imageNum, k), parameters);
            put(filterName + ".voltage", retrieve.getDetectorVoltage(imageNum, k), parameters);
            put(filterName + ".zoom", retrieve.getDetectorZoom(imageNum, k), parameters);
        }

        return parameters;
    }

    protected void addMetadataProperties(Map<String, Object> properties, MetadataRetrieve retrieve) {
        for (int i = 0; i < retrieve.getDatasetCount(); i++) {
            put("dataset" + i + ".description", retrieve.getDatasetDescription(i), properties);
            put("dataset" + i + ".ID", retrieve.getDatasetID(i), properties);
            put("dataset" + i + ".name", retrieve.getDatasetName(i), properties);
            put("dataset" + i + ".imageRef", retrieve.getDatasetImageRefCount(i), properties);
        }

        for (int i = 0; i < retrieve.getExperimenterCount(); i++) {
            String name = retrieve.getExperimenterFirstName(i) + retrieve.getExperimenterLastName(i);
            put("experimenter" + i + ".username", retrieve.getExperimenterUserName(i), properties);
            put("experimenter" + i + ".ID", retrieve.getExperimenterID(i), properties);
            put("experimenter" + i + ".name", name, properties);
            put("experimenter" + i + ".email", retrieve.getExperimenterEmail(i), properties);
            put("experimenter" + i + ".institution", retrieve.getExperimenterInstitution(i), properties);
        }
    }

    private List<Map<String, Object>> getInstrumentData() {
        List<Map<String, Object>> instrumentStructs = new ArrayList<Map<String, Object>>();

        for (int j = 0; j < retrieve.getInstrumentCount(); j++) {
            Map<String, Object> instrumentStruct = new HashMap<String, Object>();

            put("ID", retrieve.getInstrumentID(j), instrumentStruct);

            if (isMicroscope(retrieve, j)) {
                put("microscopeLotNumber", retrieve.getMicroscopeLotNumber(j), instrumentStruct);
                put("microscopeManufacturer", retrieve.getMicroscopeManufacturer(j), instrumentStruct);
                put("microscopeModel", retrieve.getMicroscopeModel(j), instrumentStruct);
                put("microscopeSerialNumber", retrieve.getMicroscopeSerialNumber(j), instrumentStruct);
                put("microscopeType", retrieve.getMicroscopeType(j), instrumentStruct);
            }

            for (int k = 0; k < retrieve.getLightSourceCount(j); k++) {

                String type = retrieve.getLightSourceType(j, k).toLowerCase();
                put("lightSource" + k + ".type", type, instrumentStruct);

                if (type.equals("arc")) {
                    String arcName = "arc_" + retrieve.getArcID(j, k);
                    put(arcName + ".ID", retrieve.getArcID(j, k), instrumentStruct);
                    put(arcName + ".lotNumber", retrieve.getArcLotNumber(j, k), instrumentStruct);
                    put(arcName + ".manufacturer", retrieve.getArcManufacturer(j, k), instrumentStruct);
                    put(arcName + ".model", retrieve.getArcModel(j, k), instrumentStruct);
                    put(arcName + ".serialNumber", retrieve.getArcSerialNumber(j, k), instrumentStruct);
                    put(arcName + ".type", retrieve.getArcType(j, k), instrumentStruct);
                }

                if (type.equals("filament")) {
                    String filamentName = "filament_" + retrieve.getFilamentID(j, k);
                    put(filamentName + ".ID", retrieve.getFilamentID(j, k), instrumentStruct);
                    put(filamentName + ".lotNumber", retrieve.getFilamentLotNumber(j, k), instrumentStruct);
                    put(filamentName + ".manufacturer", retrieve.getFilamentManufacturer(j, k), instrumentStruct);
                    put(filamentName + ".model", retrieve.getFilamentModel(j, k), instrumentStruct);
                    put(filamentName + ".serialNumber", retrieve.getFilamentSerialNumber(j, k), instrumentStruct);
                    put(filamentName + ".type", retrieve.getFilamentType(j, k), instrumentStruct);
                }

                if (type.equals("laser")) {
                    String laserName = "laser_" + retrieve.getLaserID(j, k);
                    put(laserName + ".ID", retrieve.getLaserID(j, k), instrumentStruct);
                    put(laserName + ".medium", retrieve.getLaserLaserMedium(j, k), instrumentStruct);
                    put(laserName + ".lotNumber", retrieve.getLaserLotNumber(j, k), instrumentStruct);
                    put(laserName + ".manufacturer", retrieve.getLaserManufacturer(j, k), instrumentStruct);
                    put(laserName + ".model", retrieve.getLaserModel(j, k), instrumentStruct);
                    put(laserName + ".serialNumber", retrieve.getLaserSerialNumber(j, k), instrumentStruct);
                    put(laserName + ".tuneable", retrieve.getLaserTuneable(j, k), instrumentStruct);
                    put(laserName + ".type", retrieve.getLaserType(j, k), instrumentStruct);
                }

                if (type.equals("lightEmittingDiode")) {
                    String lightEmittingDiodeName = "lightEmittingDiodeName_" + retrieve.getLightEmittingDiodeID(j, k);
                    put(lightEmittingDiodeName + ".ID", retrieve.getLightEmittingDiodeID(j, k), instrumentStruct);
                    put(lightEmittingDiodeName + ".lotNumber", retrieve.getLightEmittingDiodeLotNumber(j, k), instrumentStruct);
                    put(lightEmittingDiodeName + ".manufacturer", retrieve.getLightEmittingDiodeManufacturer(j, k), instrumentStruct);
                    put(lightEmittingDiodeName + ".model", retrieve.getLightEmittingDiodeModel(j, k), instrumentStruct);
                    put(lightEmittingDiodeName + ".serialNumber", retrieve.getLightEmittingDiodeSerialNumber(j, k), instrumentStruct);
                }
            }

            for (int k = 0; k < retrieve.getObjectiveCount(j); k++) {
                String objName = "objective_" + retrieve.getObjectiveID(j, k);
                put(objName + ".ID", retrieve.getObjectiveID(j, k), instrumentStruct);
                put(objName + ".lotNumber", retrieve.getObjectiveLotNumber(j, k), instrumentStruct);
                put(objName + ".manufacturer", retrieve.getObjectiveManufacturer(j, k), instrumentStruct);
                put(objName + ".model", retrieve.getObjectiveModel(j, k), instrumentStruct);
                put(objName + ".serialNumber", retrieve.getObjectiveSerialNumber(j, k), instrumentStruct);
            }

            for (int k = 0; k < retrieve.getFilterCount(j); k++) {
                String filterName = "filter_" + retrieve.getFilterID(j, k);
                put(filterName + ".wheel", retrieve.getFilterFilterWheel(j, k), instrumentStruct);
                put(filterName + ".ID", retrieve.getFilterID(j, k), instrumentStruct);
                put(filterName + ".lotNumber", retrieve.getFilterLotNumber(j, k), instrumentStruct);
                put(filterName + ".model", retrieve.getFilterModel(j, k), instrumentStruct);
                put(filterName + ".serialNumber", retrieve.getFilterSerialNumber(j, k), instrumentStruct);
                put(filterName + ".manufacturer", retrieve.getFilterManufacturer(j, k), instrumentStruct);
                put(filterName + ".type", retrieve.getFilterType(j, k), instrumentStruct);
            }

            for (int k = 0; k < retrieve.getDetectorCount(j); k++) {
                String filterName = "detector_" + retrieve.getDetectorID(j, k);
                put(filterName + ".ID", retrieve.getDetectorID(j, k), instrumentStruct);
                put(filterName + ".lotNumber", retrieve.getDetectorLotNumber(j, k), instrumentStruct);
                put(filterName + ".model", retrieve.getDetectorModel(j, k), instrumentStruct);
                put(filterName + ".manufacturer", retrieve.getDetectorManufacturer(j, k), instrumentStruct);
                put(filterName + ".serialNumber", retrieve.getDetectorSerialNumber(j, k), instrumentStruct);
                put(filterName + ".type", retrieve.getDetectorType(j, k), instrumentStruct);
            }

            for (int k = 0; k < retrieve.getDichroicCount(j); k++) {
                String filterName = "dichroic_" + retrieve.getDichroicID(j, k);
                put(filterName + ".ID", retrieve.getDichroicID(j, k), instrumentStruct);
                put(filterName + ".lotNumber", retrieve.getDichroicLotNumber(j, k), instrumentStruct);
                put(filterName + ".manufacturer", retrieve.getDichroicManufacturer(j, k), instrumentStruct);
                put(filterName + ".model", retrieve.getDichroicModel(j, k), instrumentStruct);
                put(filterName + ".serialNumber", retrieve.getDichroicSerialNumber(j, k), instrumentStruct);
            }

            instrumentStructs.add(instrumentStruct);
        }
        return instrumentStructs;
    }

    private void put(String name, Object value, Map<String, Object> map) {
        if (value != null) {
            //TODO: handle Enums, PositiveIntegers, etc
            map.put(name, value);
        }
    }

    protected Map<String, Object> getResponseProperties(int imageNumber) {
        Map<String, Object> properties = new HashMap<String, Object>();
        put("imageName", retrieve.getImageName(imageNumber), properties);
        put("imageDescription", retrieve.getImageDescription(imageNumber), properties);
        put("imageID", retrieve.getImageID(imageNumber), properties);

        return properties;
    }

    private boolean isMicroscope(MetadataRetrieve retrieve, int j) {
        try {
            retrieve.getMicroscopeLotNumber(j);
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }
}