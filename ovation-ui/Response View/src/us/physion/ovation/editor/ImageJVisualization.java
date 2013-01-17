/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import imagej.ImageJ;
import imagej.display.DisplayService;
import java.awt.Component;
import java.io.File;
import javax.swing.JPanel;
import net.imglib2.img.ImgPlus;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import org.openide.util.lookup.ServiceProvider;
import ovation.OvationException;
import ovation.Response;
import ovation.URLResponse;
import imagej.data.Dataset;


/**
 *
 * @author huecotanks
 */
public class ImageJVisualization implements Visualization{

    private final ImageJ ijContext;
    private final Response response;
    ImageJVisualization(Response r)
    {
        this.ijContext = ImageJ.createContext();
        this.response = r;
    }
    
    @Override
    public Component generatePanel() {
        assert (this.response instanceof URLResponse);

        try {
            final ImageJ context = ImageJ.createContext();

//                // load the dataset
//                final IOService ioService = context.getService(IOService.class);
//                final Dataset dataset = ioService.loadDataset();

//                // display the dataset
//                final DisplayService displayService =
//                        context.getService(DisplayService.class);
//                displayService.createDisplay(file.getName(), dataset);

            final ImgPlus ip = ImgOpener.open(((URLResponse) this.response).getURLString());
            final Dataset dataset = new Dataset(context, ip);
            // display the dataset
            final DisplayService displayService = context.getService(DisplayService.class);
            displayService.createDisplay(((URLResponse) this.response).getURLString(), dataset);
            displayService.getActiveDisplay().display(ip);
        } catch (ImgIOException ex) {
            throw new OvationException("Unable to open image " + ex.getMessage());
        }

        return new JPanel();
    }

    @Override
    public boolean shouldAdd(Response r) {
        return false;
    }

    @Override
    public void add(Response r) {
        throw new UnsupportedOperationException("Not supported for this image visualization.");
    }
    
}
