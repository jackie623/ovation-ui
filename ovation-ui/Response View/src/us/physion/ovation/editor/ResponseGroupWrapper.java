/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import java.awt.Component;

/**
 *
 * @author huecotanks
 */
public interface ResponseGroupWrapper {
    public Component generatePanel();
    
    public boolean shouldAdd(ResponseWrapper r);
    
    public void add(ResponseWrapper r);
}
