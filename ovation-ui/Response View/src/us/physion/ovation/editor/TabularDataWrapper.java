/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import com.pixelmed.dicom.DicomInputStream;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import ovation.Response;
import ovation.URLResponse;

public class TabularDataWrapper implements Visualization {

    String[] columnNames;
    String[][] tabularData;

    TabularDataWrapper(Response r) {
        
        InputStream in;
        if (r instanceof URLResponse) {
            in = r.getDataStream();
        } else {
            in = new ByteArrayInputStream(r.getDataBytes());
        }
        
        /*
        DicomInputStream in = null;
        try {
            if (r instanceof URLResponse)
                in = new DicomInputStream(r.getDataStream());
            else{
                in = new DicomInputStream(new ByteArrayInputStream(r.getDataBytes()));
            }
            src = new SourceImage(in);
            this.name = r.getExternalDevice().getName();
        } catch (DicomException ex) {
            Exceptions.printStackTrace(ex);
            throw new RuntimeException(ex.getLocalizedMessage());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            throw new RuntimeException(ex.getLocalizedMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                    throw new RuntimeException(ex.getLocalizedMessage());
                }
            }
        }*/
    }

    void parseCSVData(InputStream in) {
        /*
        FileInputStream s = new FileInputStream(in);
        while (in.read) {
        String line = acq_scan.nextLine();
        if (line.charAt(0) == '#') {
            continue;
        }
        String[] split = line.split(",");

        itemName = split[0];
        quantity = Integer.parseInt(split[1]);
        cost = Double.parseDouble(split[2]);
        price = Double.parseDouble(split[3]);


        while(invscan.hasNext()) {
            String line2 = invscan.nextLine();
            if (line2.charAt(0) == '#') {
                continue;
            }
            String[] split2 = line2.split(",");

            itemNameInv = split2[0];
            quantityInv = Integer.parseInt(split2[1]);
            costInv = Double.parseDouble(split2[2]);
            priceInv = Double.parseDouble(split2[3]);


            if(itemName == itemNameInv) {
                //update quantity

            }
        }
        //add new entry into csv file

     }*/
    }

    @Override
    public Component generatePanel() {
	JScrollPane pane = new JScrollPane();
	pane.add(new JTable(tabularData, columnNames));
	return pane;
    }

    @Override
    public boolean shouldAdd(Response r) {
        return false;
    }

    @Override
    public void add(Response r) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
