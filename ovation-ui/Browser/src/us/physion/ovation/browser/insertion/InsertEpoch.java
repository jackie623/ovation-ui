/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser.insertion;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.util.lookup.ServiceProvider;
import us.physion.ovation.browser.moveme.EpochGroupInsertable;
import us.physion.ovation.browser.moveme.ProjectInsertable;
import us.physion.ovation.interfaces.IEntityWrapper;

@ServiceProvider(service=EpochGroupInsertable.class)
/**
 *
 * @author huecotanks
 */
public class InsertEpoch extends InsertEntity implements EpochGroupInsertable {

    public InsertEpoch() {
        putValue(NAME, "Insert Epoch");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}