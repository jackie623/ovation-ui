/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartPanel;

class ChartTableModel extends DefaultTableModel {
  List<ChartPanel> data;

  public ChartTableModel(List<ChartPanel> data) {
    this.data = data;
  }
  
  public void setCharts(List<ChartPanel> charts)
  {
      data = charts;
  }

  public Class<?> getColumnClass(int columnIndex) { return ChartPanel.class; }
  public int getColumnCount() { return 1; }
  public String getColumnName(int columnIndex) { return ""; }
  public int getRowCount() { return (data == null) ? 0 : data.size(); }
  public Object getValueAt(int rowIndex, int columnIndex) { return data.get(rowIndex); }
  public boolean isCellEditable(int rowIndex, int columnIndex) { return true; }
}
