/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser.insertion;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.util.*;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.openide.util.ChangeSupport;

public final class InsertEpochGroupVisualPanel2 extends JPanel{

    private ChangeSupport cs;
    private String label;
    private DateTime start;
    private DateTime end;
    private DateTimePicker startPicker;
    private DateTimePicker endPicker;
    private String[] availableIDs; 
    /**
     * Creates new form InsertEpochGroupVisualPanel2
     */
    public InsertEpochGroupVisualPanel2(ChangeSupport cs) {
        initComponents();

        this.cs = cs;
        label = "";
        startPicker = DatePickerUtilities.createDateTimePicker();
        startPicker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if ("date".equals(propertyChangeEvent.getPropertyName())) {
                    startDateTimeChanged();
                }
            }
        });
        endPicker = DatePickerUtilities.createDateTimePicker();
        endPicker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if ("date".equals(propertyChangeEvent.getPropertyName())) {
                    endDateTimeChanged();
                }
            }
        });
        
        jComboBox1.setSelectedItem(DatePickerUtilities.getID(startPicker));
        jComboBox2.setSelectedItem(DatePickerUtilities.getID(endPicker));
        startTimePane.setViewportView(startPicker);
        endTimePane.setViewportView(endPicker);
        start = null;
        end = null;
    }

    protected void startDateTimeChanged() {
        start = new DateTime(startPicker.getDate(), DateTimeZone.forID(((String)jComboBox1.getSelectedItem())));
        setStart(start);
    }
    protected void endDateTimeChanged() {
        end = new DateTime(endPicker.getDate(), DateTimeZone.forID(((String)jComboBox2.getSelectedItem())));
        setEnd(end);
    }
    @Override
    public String getName() {
        return "Insert Epoch Group";
    }

    String getLabel() {
        return label;
    }

    DateTime getStart() {
        return start;
    }

    DateTime getEnd() {
        return end;
    }
    
    protected void setLabel(String l)
    {
        boolean fireChange = true;
        if (label.isEmpty() == l.isEmpty())
            fireChange = false;
        label = l;
        
        if (fireChange)
            cs.fireChange();
    }
    protected void setStart(DateTime t)
    {
        start = t;
        cs.fireChange();
    }
    protected void setEnd(DateTime t)
    {
        end = t;
        cs.fireChange();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        labelTextField = new javax.swing.JTextField();
        startTimePane = new javax.swing.JScrollPane();
        endTimePane = new javax.swing.JScrollPane();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(InsertEpochGroupVisualPanel2.class, "InsertEpochGroupVisualPanel2.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(InsertEpochGroupVisualPanel2.class, "InsertEpochGroupVisualPanel2.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(InsertEpochGroupVisualPanel2.class, "InsertEpochGroupVisualPanel2.jLabel3.text")); // NOI18N

        labelTextField.setText(org.openide.util.NbBundle.getMessage(InsertEpochGroupVisualPanel2.class, "InsertEpochGroupVisualPanel2.labelTextField.text")); // NOI18N
        labelTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                labelTextFieldActionPerformed(evt);
            }
        });
        labelTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                labelTextFieldKeyReleased(evt);
            }
        });

        startTimePane.setBackground(new java.awt.Color(204, 204, 204));
        startTimePane.setBorder(null);
        startTimePane.setPreferredSize(new java.awt.Dimension(200, 30));

        endTimePane.setBackground(new java.awt.Color(204, 204, 204));
        endTimePane.setBorder(null);
        endTimePane.setPreferredSize(new java.awt.Dimension(200, 30));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(DatePickerUtilities.getTimeZoneIDs()));
        jComboBox1.setMaximumSize(new java.awt.Dimension(300, 32767));
        jComboBox1.setPreferredSize(new java.awt.Dimension(180, 30));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(DatePickerUtilities.getTimeZoneIDs()));
        jComboBox2.setPreferredSize(new java.awt.Dimension(150, 30));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(startTimePane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(endTimePane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox1, 0, 0, Short.MAX_VALUE)
                            .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelTextField)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(jLabel2)
                                .addGap(15, 15, 15))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jComboBox1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(endTimePane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(startTimePane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(141, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void labelTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_labelTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_labelTextFieldActionPerformed

    private void labelTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_labelTextFieldKeyReleased
        setLabel(labelTextField.getText());
    }//GEN-LAST:event_labelTextFieldKeyReleased

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        setStart(new DateTime(startPicker.getDate(),  DateTimeZone.forID(((String)jComboBox1.getSelectedItem()))));
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        end = new DateTime(endPicker.getDate(),  DateTimeZone.forID(((String)jComboBox2.getSelectedItem())));
        setEnd(end);
    }//GEN-LAST:event_jComboBox2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane endTimePane;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField labelTextField;
    private javax.swing.JScrollPane startTimePane;
    // End of variables declaration//GEN-END:variables
}