/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.jfree.chart.ChartPanel;

/**
 *
 * @author huecotanks
 */
public class ResponseCellRenderer implements TableCellRenderer{
     public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component panel = (Component) value;
        
        if (table.getRowCount() != 0 && table.getColumnCount() != 0);
        {
            panel.setSize(new Dimension(table.getWidth() / table.getColumnCount(), table.getHeight() / table.getRowCount()));
        }
        return panel;
    }
}
