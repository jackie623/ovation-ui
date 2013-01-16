/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import java.awt.Component;
import java.io.File;
import javax.swing.JPanel;
import net.imglib2.io.ImgOpener;
import org.openide.util.lookup.ServiceProvider;
import ovation.Response;


/**
 *
 * @author huecotanks
 */
public class ImageJVisualization implements Visualization{

    ImageJVisualization(String s)
    {
    }
    
    @Override
    public Component generatePanel() {
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
