/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physionconsulting.ovation.browser;

import java.util.List;
import ovation.IEntityBase;
import ovation.User;

/**
 *
 * @author huecotanks
 */
public class PerUserEntityWrapper extends EntityWrapper{
    
    List<EntityWrapper> children;
    public PerUserEntityWrapper(String username, List<EntityWrapper> children)
    {
        super(username, User.class);
        this.children = children;
    }
    protected PerUserEntityWrapper(String username)
    {
        super(username, User.class);
    }
    
    @Override
    public Boolean isUnique()
    {
        return true;
    }

   
    public List<EntityWrapper> getChildren()
    {
        return children;
    }
}
