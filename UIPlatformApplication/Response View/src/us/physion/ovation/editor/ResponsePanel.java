/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import javax.swing.JComponent;

/**
 *
 * @author huecotanks
 */
public class ResponsePanel {
    JComponent panel;
    ResponsePanel(JComponent p)
    {
        panel = p;
    }
    
    public JComponent getPanel()
    {
        return panel;
    }
}
