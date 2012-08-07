/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import ovation.IAuthenticatedDataStoreCoordinator;
import ovation.IEntityBase;
import us.physion.ovation.interfaces.IEntityWrapper;
import us.physion.ovation.interfaces.EntityWrapper;

/**
 *
 * @author huecotanks
 */
public class TestEntityWrapper implements IEntityWrapper{

    IAuthenticatedDataStoreCoordinator dsc; 
    String uri;
    String displayName;
    Class type;
    public TestEntityWrapper(IAuthenticatedDataStoreCoordinator dsc, IEntityBase e)
    {
        this.dsc = dsc;
        dsc.getContext();
        uri = e.getURIString();
        type = e.getClass();
        displayName = EntityWrapper.inferDisplayName(e);
    }
    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public IEntityBase getEntity() {
        return dsc.getContext().objectWithURI(uri);
    }

    @Override
    public Class getType() {
        return type;
    }

    @Override
    public String getURI() {
        return uri;
    }
    
}
