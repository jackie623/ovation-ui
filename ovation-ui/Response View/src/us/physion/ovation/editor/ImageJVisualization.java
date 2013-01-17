/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.io.Opener;
import imagej.ImageJ;
import imagej.display.DisplayService;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JPanel;
import net.imglib2.img.ImgPlus;
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

    JPanel panel;
    ImageJVisualization(String url)
    {
        url = url.substring("file:".length());
        // open a file with ImageJ
        try {
            final ImagePlus imp = new Opener().openImage(url);
            panel = new BufferedImagePanel(imp.getBufferedImage());
            /*ImageCanvas ic = new ImageCanvas(imp);
            panel = new JPanel();
            panel.add(ic);
            */
        } catch (Exception e) {
            /*try {
                ImgPlus ip = ImgOpener.open(url);
                // display the dataset
                DisplayService displayService = new ImageJ().getService(DisplayService.class);
                displayService.getActiveDisplay().display(ip);
            } catch (Exception ex){
                System.out.println(ex);
            }*/
        }
    }
    
    @Override
    public Component generatePanel() {
        return panel;
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

