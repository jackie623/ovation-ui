/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;
import org.openide.util.Lookup;
import ovation.DataContext;
import ovation.IAuthenticatedDataStoreCoordinator;
import ovation.IEntityBase;
import us.physion.ovation.interfaces.ConnectionProvider;
import us.physion.ovation.interfaces.EventQueueUtilities;

/**
 *
 * @author huecotanks
 */
class PropertyTableModelListener implements TableModelListener{

    JTree tree;
    Set<String> uris;
    IAuthenticatedDataStoreCoordinator dsc;
    TableNode node;
    public PropertyTableModelListener(Set<String> uriSet, JTree tree, TableNode node, IAuthenticatedDataStoreCoordinator dsc) {
        this.dsc = dsc;
        uris = uriSet;
        this.tree = tree;
        this.node = node;
    }

    @Override
    public void tableChanged(TableModelEvent tme) {
        DefaultTableModel t = (DefaultTableModel)tme.getSource();
        int firstRow = tme.getFirstRow();
        int lastRow = tme.getLastRow();
        
        if (tme.getType() == TableModelEvent.UPDATE || tme.getType() == TableModelEvent.INSERT)
        {
            Map<String, Object> newProperties = new HashMap<String, Object>();
            
            for (int i = firstRow; i <= lastRow; i++) {
                String key = (String) t.getValueAt(i, 0);
                if (key == null || key.isEmpty())
                    continue;
                Object value = t.getValueAt(i, 1);
                newProperties.put(key, value);
                
            }
            final Map<String, Object> props = newProperties;
            EventQueueUtilities.runOffEDT(new Runnable() {

                @Override
                public void run() {
                    
                    DataContext c = dsc.getContext();
                    for (String key: props.keySet())
                    {
                        for (String uri : uris) {
                            IEntityBase eb = c.objectWithURI(uri);
                            parseAndAdd(eb, key, props.get(key));
                        }
                    }
                    node.resetProperties(dsc);
                }
            });
        }
    }

    void deleteProperty(final String key, DefaultTableModel model, int rowToRemove) {
        EventQueueUtilities.runOffEDT(new Runnable() {

            @Override
            public void run() {
                for (String uri : uris) {
                    DataContext c = dsc.getContext();
                    IEntityBase eb = c.objectWithURI(uri);
                    eb.removeProperty(key);
                }
            }
        });
        model.removeRow(rowToRemove);
    }

    void parseAndAdd(IEntityBase eb, String key, Object value)
    {
        if (value instanceof String) {
            String s = (String) value;
            try {
                int v = Integer.parseInt(s);
                eb.addProperty(key, v);
                return;
            } catch (NumberFormatException e) {
            }
            try {
                long v = Long.parseLong(s);
                eb.addProperty(key, v);
                return;
            } catch (NumberFormatException e) {
            }
            try {
                double v = Double.parseDouble(s);
                eb.addProperty(key, v);
                return;
            } catch (NumberFormatException e) {
            }
            try {
                double v = Double.parseDouble(s);
                eb.addProperty(key, v);
                return;
            } catch (NumberFormatException e) {
            }
            if (s.toLowerCase().equals("true"))
            {
                eb.addProperty(key, true);
                return;
            }
            if (s.toLowerCase().equals("false"))
            {
                eb.addProperty(key, false);
                return;
            }
           
            //byte array
            //Timestamp
            //NumericData
            //EntityBase
        }
        eb.addProperty(key, value);
    }
}
