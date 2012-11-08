/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import java.util.Map;

/**
 *
 * @author huecotanks
 */
public class OVTreeTable {
    
    private Map<String, Map<String, Object>> data;
    
    OVTreeTable()
    {
        //create a tree 
    }
            
    public void setData(Map<String, Map<String, Object>> d)
    {
        data = d;
        //set data on tree
    }
}
