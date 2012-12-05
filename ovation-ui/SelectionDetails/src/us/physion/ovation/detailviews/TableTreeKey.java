/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import javax.swing.event.TableModelListener;
import ovation.IAuthenticatedDataStoreCoordinator;

/**
 *
 * @author huecotanks
 */
public interface TableTreeKey extends Comparable
{
    public void refresh(IAuthenticatedDataStoreCoordinator dsc);//regrab info from the database
    public String getDisplayName();
    public Object[][] getData();//maybe this is already sorted?
    public String getID();// for comparison
    public boolean isEditable();
    public boolean isExpandedByDefault();
    public TableModelListener createTableModelListener(ScrollableTableTree t, TableNode n);
}
