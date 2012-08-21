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
public class UpdateSchemaStep implements UpdateStep{

    String schemafile;
    String version;
    
    UpdateSchemaStep(String schema, String version)
    {
        schemafile = schema;
        this.version = version;
    }
    @Override
    public String getStepDescriptor() {
        return schemafile;
    }
    
    public String getSchemaVersion()
    {
        return version;
    }
    
}
