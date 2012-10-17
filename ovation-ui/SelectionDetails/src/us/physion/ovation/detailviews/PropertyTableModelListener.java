/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import java.util.Set;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import ovation.DataContext;
import ovation.IAuthenticatedDataStoreCoordinator;
import ovation.IEntityBase;

/**
 *
 * @author huecotanks
 */
class PropertyTableModelListener implements TableModelListener {

    Set<String> uris;
    IAuthenticatedDataStoreCoordinator dsc;
    public PropertyTableModelListener(IAuthenticatedDataStoreCoordinator dsc, Set<String> uriSet) {
        this.dsc = dsc;
        uris = uriSet;
    }

    @Override
    public void tableChanged(TableModelEvent tme) {
        DefaultTableModel t = (DefaultTableModel)tme.getSource();
        int firstRow = tme.getFirstRow();
        int lastRow = tme.getLastRow();
        
        for (int i = firstRow; i<=lastRow; i++)
        {
            String key = (String)t.getValueAt(i, 0);
            Object value = t.getValueAt(i, 1);
            for (String uri : uris)
            {
                DataContext c = dsc.getContext();
                IEntityBase eb = c.objectWithURI(uri);
                eb.addProperty(key, value);
            }
        }
    }
    
}
