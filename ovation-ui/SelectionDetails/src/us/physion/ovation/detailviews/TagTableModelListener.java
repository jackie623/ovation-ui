/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openide.util.Lookup;
import ovation.DataContext;
import ovation.IAuthenticatedDataStoreCoordinator;
import ovation.IEntityBase;
import ovation.ITaggableEntityBase;
import us.physion.ovation.interfaces.ConnectionProvider;
import us.physion.ovation.interfaces.EventQueueUtilities;

/**
 *
 * @author huecotanks
 */
class TagTableModelListener implements EditableTableModelListener {

    Set<String> uris;
    ResizableTree tree;
    IAuthenticatedDataStoreCoordinator dsc;
    TableNode node;
    public TagTableModelListener(Set<String> uriSet, ResizableTree expandableJTree, TableNode n, IAuthenticatedDataStoreCoordinator connection) {
        this.dsc = connection;
        uris = uriSet;
        this.tree = expandableJTree;
        this.node = n;
    }

    @Override
    public void tableChanged(TableModelEvent tme) {
        DefaultTableModel t = (DefaultTableModel)tme.getSource();
        final int firstRow = tme.getFirstRow();
        final int lastRow = tme.getLastRow();
                         
        if (tme.getType() == TableModelEvent.INSERT)
        {
            tree.resizeNode(node);

        } else if (tme.getType() == TableModelEvent.UPDATE)
        {
            Set<String> tags = new HashSet();
            for (int i = 0; i < t.getRowCount(); i++) {
                String tag = (String) t.getValueAt(i, 0);
                tags.add(tag);
            }

            final Set<String> newTags = tags;
            //add new tags
            EventQueueUtilities.runOffEDT(new Runnable() {

                @Override
                public void run() {
                    List<String> originalTags = ((TagsSet) node.getUserObject()).getTags();
                    List<String> toRemove = new ArrayList<String>();
                    DataContext c = dsc.getContext();
                    
                    for (String original : originalTags) {
                        if (!newTags.contains(original)) {
                            toRemove.add(original);
                        }else{
                            newTags.remove(original);
                        }
                    }
                    for (String uri : uris) {
                        IEntityBase eb = c.objectWithURI(uri);
                        if (eb instanceof ITaggableEntityBase) {
                            for (String tag : newTags) {
                                if (tag != null && !tag.isEmpty())
                                    ((ITaggableEntityBase) eb).addTag(tag);
                            }
                            for (String tag : toRemove) {
                                if (tag != null && !tag.isEmpty())
                                    ((ITaggableEntityBase) eb).removeTag(tag);
                            }
                        }
                    }
                    node.reset(dsc);
                }
            });
        }
    }

    public void deleteRows(final DefaultTableModel model, int[] rowsToRemove) {
        
        Arrays.sort(rowsToRemove);
        final int[] rows = rowsToRemove;
        EventQueueUtilities.runOffEDT(new Runnable() {

            @Override
            public void run() {
                DataContext c = dsc.getContext();
                for (int i = rows.length - 1; i >= 0; i--) {
                    String tag = (String) model.getValueAt(rows[i], 0);

                    for (String uri : uris) {
                        IEntityBase eb = c.objectWithURI(uri);
                        if (eb instanceof ITaggableEntityBase)
                        {
                            ((ITaggableEntityBase)eb).removeTag(tag);
                        }
                    }
                }
                node.reset(dsc);
                //remove rows and resize
                EventQueueUtilities.runOnEDT(new Runnable() {

                    @Override
                    public void run() {
                        for (int i = rows.length - 1; i >= 0; i--) {
                            model.removeRow(rows[i]);
                        }
                        EditableTable p = (EditableTable)node.getPanel();
                        JScrollPane sp = p.getScrollPane();
                        if (sp != null)
                            sp.setSize(sp.getPreferredSize());
                        p.setSize(p.getPreferredSize());
                        tree.resizeNode(node);//this resizes the tree cell that contains the editable table that just deleted a row
                    }
                });
            }
        });
    }
}
