/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

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
    TableNode(UserPropertySet p)
    {
        super(p);
    }
    
    public UserPropertySet getUserProperties()
    {
        return (UserPropertySet)getUserObject();
    }
    
    public void resetProperties(IAuthenticatedDataStoreCoordinator dsc)
    {
        //regrab properties from the database
        UserPropertySet p = (UserPropertySet)getUserObject();
        p.refresh(dsc);
    }
}
