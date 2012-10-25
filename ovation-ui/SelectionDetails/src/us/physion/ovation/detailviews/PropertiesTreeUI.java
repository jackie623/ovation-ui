/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreePath;

/**
 *
 * @author huecotanks
 */
public class PropertiesTreeUI extends BasicTreeUI{
    
    JComponent window;
    PropertiesTreeUI(JComponent window)
    {
        this.window = window;
    }

    @Override
    protected AbstractLayoutCache.NodeDimensions createNodeDimensions() {
        return new NodeDimensionsHandler() {

            @Override
            public Rectangle getNodeDimensions(
                    Object value, int row, int depth, boolean expanded,
                    Rectangle size) {
                Rectangle dimensions = super.getNodeDimensions(value, row,
                        depth, expanded, size);
                dimensions.width =
                        window.getWidth() - getRowX(row, depth);
                //dimensions.height = window.getHeight();
                return dimensions;
            }
        };
    }
    
    @Override
    protected int getRowX(int row, int depth)
    {
        if (depth ==2)
            return 2;
        
        return super.getRowX(row, depth);
    }

    @Override
    protected void paintHorizontalLine(Graphics g, JComponent c,
            int y, int left, int right) {
        // do nothing.
    }

    @Override
    protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds,
            Insets insets, TreePath path) {
        // do nothing.
    }
    
    class RepaintOnResize implements ComponentListener
    {

        BasicTreeUI panelToRepaint;
        
        @Override
        public void componentResized(ComponentEvent ce) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void componentMoved(ComponentEvent ce) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void componentShown(ComponentEvent ce) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void componentHidden(ComponentEvent ce) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
}
