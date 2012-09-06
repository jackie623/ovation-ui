/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.editor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.swing.JPanel;
import org.jfree.data.xy.DefaultXYDataset;
import ovation.NumericData;
import ovation.Response;
import ovation.URLResponse;
import us.physion.ovation.interfaces.IEntityWrapper;

/**
 *
 * @author huecotanks
 */
public interface ResponseWrapper {
    ResponseGroupWrapper createGroup();
}
