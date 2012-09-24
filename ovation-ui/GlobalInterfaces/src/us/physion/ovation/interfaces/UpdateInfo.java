package us.physion.ovation.interfaces;
import java.util.List;

public interface UpdateInfo
{
    public int getSchemaVersion();
    public String getSpecificationVersion();
    public List<UpdateStep> getUpdateSteps(); 
}