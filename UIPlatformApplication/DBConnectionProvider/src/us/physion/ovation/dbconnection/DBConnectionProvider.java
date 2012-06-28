/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.dbconnection;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.util.lookup.ServiceProvider;
import ovation.IAuthenticatedDataStoreCoordinator;
import us.physion.ovation.interfaces.ConnectionListener;
import us.physion.ovation.interfaces.ConnectionProvider;

@ServiceProvider(service=ConnectionProvider.class)
/**
 *
 * @author huecotanks
 */
public class DBConnectionProvider implements ConnectionProvider{

    private IAuthenticatedDataStoreCoordinator dsc;
    private ArrayList<ConnectionListener> connectionListeners;
    
    public DBConnectionProvider(){
        
        connectionListeners = new ArrayList<ConnectionListener>();
    };

    @Override
    public synchronized IAuthenticatedDataStoreCoordinator getConnection() {
        if (dsc != null)
        {
            return dsc;
        }
        final ConnectionListener[] listeners = connectionListeners.toArray(new ConnectionListener[0]);
        
        Runnable r = new Runnable() {

            public void run() {
                /*if (DBConnectionProvider.this.dsc != null) {
                    return;
                }*/

                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    /*
                     * for (javax.swing.UIManager.LookAndFeelInfo info :
                     * javax.swing.UIManager.getInstalledLookAndFeels()) { if
                     * ("Nimbus".equals(info.getName())) {
                     * javax.swing.UIManager.setLookAndFeel(info.getClassName());
                     * break; }
                }
                     */

                } catch (ClassNotFoundException ex) {
                    java.util.logging.Logger.getLogger(DBConnectionDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    java.util.logging.Logger.getLogger(DBConnectionDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    java.util.logging.Logger.getLogger(DBConnectionDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                    java.util.logging.Logger.getLogger(DBConnectionDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
                DBConnectionDialog dialog = new DBConnectionDialog(new javax.swing.JFrame());
                
                dialog.setLocationRelativeTo(null);
                dialog.pack();
                dialog.setVisible(true);

                if (!dialog.isCancelled()) {
                    DBConnectionProvider.this.dsc = dialog.getDataStoreCoordinator();

                    for (PropertyChangeListener l : listeners) {
                        dialog.addPropertyChangeListener(l);
                    }
                    dialog.firePropertyChange("ovation.connectionChanged", 0, 1);
                }
            }
        };
        r.run(); //TODO: Clean up. Looks like we don't need to create a runnable
        
        //new Thread (r).start ();
        /*try {
            if (SwingUtilities.isEventDispatchThread()) {
                r.run();
            } else {
                SwingUtilities.invokeAndWait(r);
            }
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }*/
        return dsc;
    }

    @Override
    public synchronized void addConnectionListener(ConnectionListener cl) {
        connectionListeners.add(cl);
    }

    @Override
    public synchronized void removeConnectionListener(ConnectionListener cl) {
        connectionListeners.remove(cl);
    }

}