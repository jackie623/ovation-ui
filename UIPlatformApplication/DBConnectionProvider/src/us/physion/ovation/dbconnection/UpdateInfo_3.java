/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.dbconnection;

import java.util.LinkedList;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;
import us.physion.ovation.interfaces.ConnectionProvider;
import us.physion.ovation.interfaces.UpdateInfo;
import us.physion.ovation.interfaces.UpdateStep;

@ServiceProvider(service = UpdateInfo.class)
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
        //steps.add(new UpdateSchemaStep("/Users/huecotanks/Ovation/production/schemas/r16/ovation.schema"));
        //steps.add(new UpdateJarStep("https://a/Users/huecotanks/Ovation/ovation-ui/UIPlatformApplication/Update_3.jar"));//for development, this is a hardcoded path, in production and tests, this should be on s3
        steps.add(new UpdateSchemaStep("https://s3.amazonaws.com/com.physionconsulting.ovation.updates/1.4/ovation.schema"));
        steps.add(new UpdateJarStep("https://s3.amazonaws.com/com.physionconsulting.ovation.updates/1.4/Update_3.jar"));
        return steps;
    }
}
