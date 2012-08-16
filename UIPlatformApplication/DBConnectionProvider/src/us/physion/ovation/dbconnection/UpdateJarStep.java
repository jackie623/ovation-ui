/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.dbconnection;

import us.physion.ovation.interfaces.UpdateStep;

/**
 *
 * @author huecotanks
 */
public class UpdateJarStep implements UpdateStep{

    String jarfile;
    
    UpdateJarStep(String jar)
    {
        jarfile = jar;
    }
    @Override
    public String getStepDescriptor() {
        return jarfile;
    }
    
}
