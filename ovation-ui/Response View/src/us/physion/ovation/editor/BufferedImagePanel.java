/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author huecotanks
 */
public class BufferedImagePanel extends JPanel
{
    BufferedImage img;

    BufferedImagePanel(BufferedImage img) {
        this.img = img;
    }
    
    @Override
    public void paint(Graphics g)
    {
        double height = img.getHeight();
        double width = img.getWidth();
        if (this.getHeight() < height)
        {
            height = this.getHeight();
            width = height/img.getHeight()*img.getWidth();
        }
        if (this.getWidth() < width)
        {
            width = this.getWidth();
            height = width/img.getWidth()*img.getHeight();
        }
        int startX = (int)((this.getWidth() - width)/2);
        int startY = (int)((this.getHeight() - height)/2);
        g.drawImage(img, startX, Math.min(10, startY), (int)width, (int)height, this);
    }
}
