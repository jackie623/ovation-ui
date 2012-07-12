/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.dbconnection;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
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

    private IAuthenticatedDataStoreCoordinator dsc = null;
    private ArrayList<ConnectionListener> connectionListeners;
    
    private boolean waitingForDSC = false;
    
    public DBConnectionProvider(){
        
        connectionListeners = new ArrayList<ConnectionListener>();
    };

    @Override
    public synchronized IAuthenticatedDataStoreCoordinator getConnection() {
        
        synchronized(this)
        {
            if (waitingForDSC || dsc != null) {
                return dsc;
            }
            setWaitingFlag(true);
        }
        
        final ConnectionListener[] listeners = connectionListeners.toArray(new ConnectionListener[0]);
        
        Runnable r = new Runnable() {

            public void run() {

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
                    setDsc(dialog.getDataStoreCoordinator());
                    setWaitingFlag(false);

                    for (PropertyChangeListener l : listeners) {
                        dialog.addPropertyChangeListener(l);
                    }
                    dialog.firePropertyChange("ovation.connectionChanged", 0, 1);
                }
            }
        };
        
        if (EventQueue.isDispatchThread())
        {
            r.run();
        }
        else{
            //try {
                EventQueue.invokeLater(r);
            /*} catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }*/
        }
        
        return dsc;
    }
    
    private synchronized void setDsc(IAuthenticatedDataStoreCoordinator the_dsc)
    {
        dsc = the_dsc;
    }
    
    private synchronized void setWaitingFlag(boolean b)
    {
        waitingForDSC = b;
    }
            

    @Override
    public  void addConnectionListener(ConnectionListener cl) {
        connectionListeners.add(cl);
    }

    @Override
    public void removeConnectionListener(ConnectionListener cl) {
        connectionListeners.remove(cl);
    }

}