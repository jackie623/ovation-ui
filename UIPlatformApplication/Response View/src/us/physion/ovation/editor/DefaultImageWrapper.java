/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openide.util.Exceptions;
import ovation.Response;
import ovation.URLResponse;

/**
 *
 * @author huecotanks
 */
public class DefaultImageWrapper implements ResponseWrapper, ResponseGroupWrapper{

    String name;
    BufferedImage img;
    DefaultImageWrapper(Response r)
    {
        
        InputStream in = null;
        try {
            if (r instanceof URLResponse)
                in = r.getDataStream();
            else{
                in = new ByteArrayInputStream(r.getDataBytes());
            }
            
            img = ImageIO.read(in);
            this.name = r.getExternalDevice().getName();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            throw new RuntimeException(ex.getLocalizedMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                    throw new RuntimeException(ex.getLocalizedMessage());
                }
            }
        }
    }

    @Override
    public Component generatePanel() {
        JLabel l = new JLabel(new ImageIcon(img));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        //l.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        ImagePanel p = new ImagePanel(name, l);
        return p;
    }
    
    @Override
    public ResponseGroupWrapper createGroup() {
        return this;
    }


    @Override
    public boolean shouldAdd(ResponseWrapper r) {
        return false;
    }

    @Override
    public void add(ResponseWrapper r) {
        throw new UnsupportedOperationException("Images are currently implemented one per panel");
    }
    
}

class BufferedImagePanel extends JPanel
{
    BufferedImage img;
    BufferedImagePanel(BufferedImage buf)
    {
        img = buf;
    }
    
    @Override
    public void paint(Graphics g)
    {
        g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), this);
    }
}
