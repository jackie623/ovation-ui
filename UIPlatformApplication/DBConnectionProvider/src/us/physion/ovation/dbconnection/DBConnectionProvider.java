/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.dbconnection;

import java.lang.reflect.InvocationTargetException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.util.lookup.ServiceProvider;
import ovation.IAuthenticatedDataStoreCoordinator;
import us.physion.ovation.interfaces.ConnectionProvider;

@ServiceProvider(service=ConnectionProvider.class)
/**
 *
 * @author huecotanks
 */
public class DBConnectionProvider implements ConnectionProvider{

    private IAuthenticatedDataStoreCoordinator dsc;
    
    public DBConnectionProvider(){};

    @Override
    public IAuthenticatedDataStoreCoordinator getConnection() {
        if (dsc == null)
        {
            try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException ex) {
                java.util.logging.Logger.getLogger(DBConnectionDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                java.util.logging.Logger.getLogger(DBConnectionDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                java.util.logging.Logger.getLogger(DBConnectionDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(DBConnectionDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            
            try {
                java.awt.EventQueue.invokeAndWait(new Runnable() {

                    public void run() {
                        DBConnectionDialog dialog = new DBConnectionDialog(new javax.swing.JFrame());

                        dialog.setVisible(true);
                        
                        if (!dialog.isCancelled())
                        {
                            DBConnectionProvider.this.dsc = dialog.getDataStoreCoordinator();
                        }
                    }
                });
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return dsc;
    }
    
}