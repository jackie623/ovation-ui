/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.interfaces;

import ovation.IEntityBase;

/**
 *
 * @author huecotanks
 */
public interface IEntityWrapper {

    String getDisplayName();

    IEntityBase getEntity();

    Class getType();

    String getURI();
    
}
