/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author huecotanks
 */
class ChartWrapper
{
    DefaultXYDataset _ds;
    String _xAxis;
    String _yAxis;
    String _title;
    
    ChartWrapper(DefaultXYDataset ds, String xAxis, String yAxis)
    {
        _ds = ds;
        _xAxis = xAxis;
        _yAxis = yAxis;
    }
    DefaultXYDataset getDataset(){ return _ds;}
    String getXAxis() { return _xAxis;}
    String getYAxis() { return _yAxis;}
    void setTitle(String s) {_title = s;}
    String getTitle() {return _title;}
}