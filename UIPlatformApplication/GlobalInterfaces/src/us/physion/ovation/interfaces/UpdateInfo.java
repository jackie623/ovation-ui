package us.physion.ovation.interfaces;
import java.util.List;

public interface UpdateInfo
{
    public int getStartingSpecificationNumber();

    public List<UpdateStep> getUpdateSteps(); 
}