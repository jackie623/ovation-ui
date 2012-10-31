/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author huecotanks
 */
public class NonEditableTable extends javax.swing.JPanel implements TablePanel {

    private JTable table;
    private PropertiesTreeUI treeUI;
    /**
     * Creates new form NonEditableTable
     */
    public NonEditableTable(JTable table, PropertiesTreeUI treeUI) {
        initComponents();
        jScrollPane1.getViewport().add(table, null);
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        this.table = table;
        this.treeUI = treeUI;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public JTable getTable() {
        return table;
    }

    @Override
    public JPanel getPanel() {
        return this;
    }
    
    @Override
    public Dimension getPreferredSize(){  
        int height; 
        if (table.getHeight() ==0)
        {
            height = (int)super.getPreferredSize().getHeight();
        }
        else
        {
            height = (table.getRowCount()+1)*table.getRowHeight() + 5;
        }
        Dimension actual = new Dimension(treeUI.getCellWidth(), height);
        return actual;  
    }
}