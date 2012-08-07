/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import java.awt.Font;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.ui.RectangleInsets;

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
    
    ChartPanel generateChartPanel()
    {
        JFreeChart chart = ChartFactory.createXYLineChart(getTitle(), getXAxis(), getYAxis(), getDataset(), PlotOrientation.VERTICAL, true, true, true);
        ChartPanel p = new ChartPanel(chart);

        chart.setTitle(convertTitle(getTitle()));
        chart.setPadding(new RectangleInsets(20, 20, 20, 20));
        XYPlot plot = chart.getXYPlot();
        plot.getDomainAxis().setLabelFont(new Font("timesnewroman", Font.LAYOUT_LEFT_TO_RIGHT, 15));
        plot.getRangeAxis().setLabelFont(new Font("timesnewroman", Font.LAYOUT_LEFT_TO_RIGHT, 15));
        return p;
    }
    
    private TextTitle convertTitle(String s)
    {
        return new TextTitle(s, new Font("timesnewroman", Font.BOLD, 20));
    }

}