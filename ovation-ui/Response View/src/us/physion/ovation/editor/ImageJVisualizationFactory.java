/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import imagej.ImageJ;
import imagej.display.DisplayService;
import java.io.File;
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
        return new ImageJVisualization(r);
    }

    @Override
    public int getPreferenceForDataContainer(Response r) {
        if (r.getUTI().toLowerCase().contains("tif"))
        {
            return 110;
        }
        return -1;
    }
    
}
