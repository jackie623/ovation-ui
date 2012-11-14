/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.tree.DefaultMutableTreeNode;
import org.openide.util.Lookup;
import ovation.DataContext;
import ovation.IAuthenticatedDataStoreCoordinator;
import ovation.IEntityBase;
import ovation.User;
import us.physion.ovation.interfaces.ConnectionProvider;

/**
 *
 * @author huecotanks
 */
public class TableNode extends DefaultMutableTreeNode{
    TablePanel tp;
    TableNode(TableTreeKey p)
    {
        super(p);
    }
    
    public void resetProperties(IAuthenticatedDataStoreCoordinator dsc)
    {
        //regrab properties from the database
        TableTreeKey p = (TableTreeKey)getUserObject();
        p.refresh(dsc);
    }
    
    public void resetProperties()
    {
        //regrab properties from the database
        TableTreeKey p = (TableTreeKey)getUserObject();
        p.refresh(Lookup.getDefault().lookup(ConnectionProvider.class).getConnection());
    }
    
    public void setPanel(TablePanel t)
    {
        tp = t;
    }
    
    public TablePanel getPanel()
    {
        return tp;
    }
    
    public int getHeight()
    {
        if (tp == null)
            return -1;
        return tp.getPanel().getPreferredSize().height;
    }
    
    public int getViewportHeight()
    {
        if (tp == null)
            return -1;
        return tp.getTable().getPreferredScrollableViewportSize().height;
    }
}
