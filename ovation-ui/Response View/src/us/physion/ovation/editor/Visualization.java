/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import java.awt.Component;
import ovation.Response;

/**
 *
 * @author huecotanks
 */
public interface Visualization {
    public Component generatePanel();
    
    public boolean shouldAdd(Response r);
    
    public void add(Response r);
}
