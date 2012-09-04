/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import javax.swing.JPanel;

/**
 *
 * @author huecotanks
 */
public class ResponsePanel {
    JPanel panel;
    ResponsePanel(JPanel p)
    {
        panel = p;
    }
    
    public JPanel getPanel()
    {
        return panel;
    }
}
