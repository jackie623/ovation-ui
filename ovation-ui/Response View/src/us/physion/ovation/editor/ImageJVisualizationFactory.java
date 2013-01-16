/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import imagej.ImageJ;
import imagej.display.DisplayService;
import net.imglib2.img.ImgPlus;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;
import ovation.OvationException;
import ovation.Response;
import ovation.URLResponse;

@ServiceProvider(service = VisualizationFactory.class)
/**
 *
 * @author huecotanks
 */
public class ImageJVisualizationFactory implements VisualizationFactory{

    @Override
    public Visualization createVisualization(Response r) {
        /*if (r instanceof URLResponse)
        {
            try {
                ImgPlus ip = ImgOpener.open(((URLResponse)r).getURLString());
		// display the dataset
                DisplayService displayService = new ImageJ().getService(DisplayService.class);
                displayService.getActiveDisplay().display(ip);
                return new ImageJVisualization(((URLResponse)r).getURLString());
            } catch (ImgIOException ex) {
                throw new OvationException("Unable to open image " + ex.getMessage());
            }
        }*/
        
        return new ImageJVisualization(null);
    }

    @Override
    public int getPreferenceForDataContainer(Response r) {
        if (r.getUTI().toLowerCase().contains("tif"))
        {
            return 180;
        }
        return -1;
    }
    
}
