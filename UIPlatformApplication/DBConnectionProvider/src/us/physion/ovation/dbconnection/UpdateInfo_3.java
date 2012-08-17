/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.dbconnection;

import java.util.LinkedList;
import java.util.List;
import us.physion.ovation.interfaces.UpdateInfo;
import us.physion.ovation.interfaces.UpdateStep;

/**
 *
 * @author huecotanks
 */
public class UpdateInfo_3 implements UpdateInfo{

    //This opject represents the upgrade to schema version 3
    //It contains:
    //*moving all users to a single, named container
    //*moving projects and sources into two separate, named containers
    //*adding uti to DerivedResponse (maybe adding imageAnnotations to derivedResponses)
    //*upgrading the schema accordingly
   
    @Override
    public String getSpecificationVersion()
    {
        return "1.3.101";
    }
    
    @Override
    public int getSchemaVersion()
    {
        return 3;
    }
    
    @Override
    public List<UpdateStep> getUpdateSteps() {
        List<UpdateStep> steps = new LinkedList<UpdateStep>();
        steps.add(new UpdateJarStep("~/Ovation/ovation-ui/UIPlatformApplication/Update_3.jar"));//for development, this is a hardcoded path, in production and tests, this should be on s3
        //steps.add(new UpdateSchemaStep("schema step"));
        //write the proper database schema number, too
        return steps;
    }
}
