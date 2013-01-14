/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.importer;

import java.awt.Component;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.openide.util.ChangeSupport;
import us.physion.ovation.interfaces.DatePickerUtilities;
import us.physion.ovation.interfaces.DateTimePicker;

/**
 *
 * @author huecotanks
 */
public class GetImageFilesPanel extends javax.swing.JPanel {

    private static class DateTimePickerPanel {

        private DateTimePicker picker;
        private JScrollPane pane;
        public DateTimePickerPanel(DateTimePicker p) {
            picker = p;
            pane = new JScrollPane();
            pane.setViewportView(picker);
        }
        
        public DateTimePicker getPicker()
        {
            return picker;
        }
        public JScrollPane getPane()
        {
            return pane;
        }
    }

    private FileTableModel fileTableModel;
    class FileTableModel extends DefaultTableModel 
    {
        ArrayList<FileMetadata> files = new ArrayList<FileMetadata>();
        ArrayList<DateTimePickerPanel> startPickers = new ArrayList<DateTimePickerPanel>();
        ArrayList<DateTimePickerPanel> endPickers = new ArrayList<DateTimePickerPanel>();
        ArrayList<JComboBox> timezoneComboBoxes = new ArrayList<JComboBox>();
        public int getSize() {
            return files.size();
        }

        public int getRowCount()
        {
            if (files == null)
                return 0;
            return files.size();
        }
        
        public int getColumnCount()
        {
            return 4;
        }
        
        public String getColumnName(int i)
        {
            if (i == 0)
            {
                return "File";
            }
            if (i == 1)
            {
                return "Start";
            }
            if (i == 2)
            {
                return "End";
            }
            if (i == 3)
            {
                return "Timezone";
            }
            return "";
        }
        
        public Object getValueAt(int row, int column) {
            FileMetadata meta = files.get(row);
            if (column == 0)
                return new JLabel(meta.getFile().getName());
            if (column == 1)
            {
                DateTimePickerPanel p = getPicker(row, column);
                p.getPicker().setDate(meta.getStart());
                return p.getPane();
            }
            if (column == 2)
            {
                DateTimePickerPanel p = getPicker(row, column);
                p.getPicker().setDate(meta.getEnd());
                return p.getPane();
            }
            if (column == 3)
            {
                JComboBox b = timezoneComboBoxes.get(row);
                b.getModel().setSelectedItem(Calendar.getInstance().getTimeZone());
                return b;
            }
            else
                return null;
        }
       
        public void add(FileMetadata s)
        {
            files.add(s);
            startPickers.add(new DateTimePickerPanel(DatePickerUtilities.createDateTimePicker()));
            endPickers.add(new DateTimePickerPanel(DatePickerUtilities.createDateTimePicker()));
            timezoneComboBoxes.add(new JComboBox(new DefaultComboBoxModel(){}));
            fireTableRowsInserted(files.size()-1, files.size() -1);
            fireTableDataChanged();
        }
        
        public void remove(int i)
        {
            files.remove(i);
            startPickers.remove(i);
            endPickers.remove(i);
            timezoneComboBoxes.remove(i);
            fireTableRowsDeleted(files.size(), files.size());
        }
        public List<FileMetadata> getFiles()
        {
            return files;
        }

        private DateTimePickerPanel getPicker(int row, int column) {
            if (column == 1)
            {
                if (row < startPickers.size())
                    return startPickers.get(row);
                
            }else if (column == 2)
            {
                if (row < endPickers.size())
                    return endPickers.get(row);
            }
            return null;
        }
    }
    ChangeSupport cs;
    /**
     * Creates new form GetImageFilesPanel
     */
    public GetImageFilesPanel(ChangeSupport cs) {
        this.cs = cs;
        fileTableModel = new FileTableModel();
        initComponents();
        jTable1.setRowHeight(40);
        Enumeration e = jTable1.getColumnModel().getColumns();
        int count = 0;
        while (e.hasMoreElements())
        {
            TableColumn col = (TableColumn) e.nextElement();

            col.setCellRenderer(new TableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int row, int column) {
                    FileTableModel model = (FileTableModel) jtable.getModel();
                    return (Component) model.getValueAt(row, column);
                }
            });
            
            if (count == 1 || count == 2)
                col.setPreferredWidth(200);
            if (count == 3)
                col.setPreferredWidth(180);
            count++;
            
            //col.setCellEditor(new DefaultCellEditor());
        }
    }

    public List<FileMetadata> getFiles()
    {
        return fileTableModel.getFiles();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addImagesButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        addImagesButton.setText(org.openide.util.NbBundle.getMessage(GetImageFilesPanel.class, "GetImageFilesPanel.addImagesButton.text")); // NOI18N
        addImagesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addImagesButtonActionPerformed(evt);
            }
        });

        jTable1.setModel(fileTableModel
        );
        jScrollPane2.setViewportView(jTable1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(addImagesButton)
                .addContainerGap())
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 769, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(addImagesButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addImagesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addImagesButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new ImageFileFilter();
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(new JPanel());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file != null)
            {
                fileTableModel.add(new FileMetadata(file));
            }
            for (File f : chooser.getSelectedFiles())
            {
                fileTableModel.add(new FileMetadata(f));
            }
        }
    }//GEN-LAST:event_addImagesButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addImagesButton;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
