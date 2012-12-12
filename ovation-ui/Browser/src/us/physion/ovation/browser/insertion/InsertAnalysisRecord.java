/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser.insertion;

import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.AbstractAction;
import org.openide.util.Utilities;
import org.openide.util.lookup.ServiceProvider;
import us.physion.ovation.browser.moveme.ProjectInsertable;
import us.physion.ovation.interfaces.IEntityWrapper;

@ServiceProvider(service=ProjectInsertable.class)
/**
 *
 * @author huecotanks
 */
public class InsertAnalysisRecord extends AbstractAction implements ProjectInsertable{

    public InsertAnalysisRecord() {}

    @Override
    public void actionPerformed(ActionEvent ae) {
        Collection<? extends IEntityWrapper> entities = Utilities.actionsGlobalContext().lookupResult(IEntityWrapper.class).allInstances();
        if (entities.size() == 1)
        {
            IEntityWrapper entity = entities.iterator().next();
            
        }
    }

    @Override
    public int compareTo(Object t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getPosition() {
        return 300;
    }
}