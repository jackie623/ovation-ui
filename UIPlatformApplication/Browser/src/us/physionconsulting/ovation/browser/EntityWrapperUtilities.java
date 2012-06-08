/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physionconsulting.ovation.browser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ovation.Epoch;
import ovation.EpochGroup;
import ovation.Experiment;
import ovation.Project;

/**
 *
 * @author huecotanks
 */
public class EntityWrapperUtilities {
    
    /*protected static List<EntityWrapper> getRootProject(EntityWrapper ew)
    {
        Class entityClass = ew.getType();
        if (entityClass.isAssignableFrom(Project.class)) {
            Project entity = (Project) ew.getEntity();
            for (Experiment e : entity.getExperiments()) {
                list.add(new EntityWrapper(e));
            }
            return true;
        } else if (entityClass.isAssignableFrom(Experiment.class)) {
            Experiment entity = (Experiment) ew.getEntity();

            for (EpochGroup eg : entity.getEpochGroups()) {
                list.add(new EntityWrapper(eg));
            }
            return true;
        } else if (entityClass.isAssignableFrom(EpochGroup.class)) {
            EpochGroup entity = (EpochGroup) ew.getEntity();

            for (EpochGroup eg : entity.getChildren()) {
                list.add(new EntityWrapper(eg));
            }
            for (Epoch e : entity.getEpochs()) {
                list.add(new EntityWrapper(e));
            }
            return true;
        }
    }*/
    
    /*protected static Set<EntityWrapper> getParent(EntityWrapper ew, List<EntityWrapper> list)
    {
        Class entityClass = ew.getType();
        Set s = new HashSet();
        if (entityClass.isAssignableFrom(Project.class)) {
            s.add(ew);
            return s;
        } else if (entityClass.isAssignableFrom(Experiment.class)) {
           
            return new HashSet();
        } else if (entityClass.isAssignableFrom(EpochGroup.class)) {
            EpochGroup entity = (EpochGroup) ew.getEntity();

            for (EpochGroup eg : entity.getChildren()) {
                list.add(new EntityWrapper(eg));
            }
            for (Epoch e : entity.getEpochs()) {
                list.add(new EntityWrapper(e));
            }
        }
    }*/
    
}
