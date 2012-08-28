/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.dbconnection;

import com.objy.db.app.Session;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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

@ServiceProvider(service = ConnectionProvider.class)
/**
 *
 * @author huecotanks
 */
public class DatabaseConnectionProvider implements ConnectionProvider {

    private IAuthenticatedDataStoreCoordinator dsc = null;
    private Set<ConnectionListener> connectionListeners;
    private boolean waitingForDSC = false;

    public DatabaseConnectionProvider() {

        connectionListeners = Collections.synchronizedSet(new HashSet());
    }

    public synchronized void resetConnection()
    {
        dsc = null;
        getConnection();
    }

    @Override
    public IAuthenticatedDataStoreCoordinator getConnection() {

        synchronized (this) {
            if (waitingForDSC || dsc != null) {
                return dsc;
            }
            setWaitingFlag(true);
        }

        final ConnectionListener[] listeners = connectionListeners.toArray(new ConnectionListener[0]);

        Runnable r = new Runnable() {

            public void run() {

                /*
                 * try {
                 * UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                 * /* for (javax.swing.UIManager.LookAndFeelInfo info :
                 * javax.swing.UIManager.getInstalledLookAndFeels()) { if
                 * ("Nimbus".equals(info.getName())) {
                 * javax.swing.UIManager.setLookAndFeel(info.getClassName());
                 * break; } }
                 */

                /*
                 * } catch (ClassNotFoundException ex) {
                 * java.util.logging.Logger.getLogger(DBConnectionDialog.class.getName()).log(java.util.logging.Level.SEVERE,
                 * null, ex); } catch (InstantiationException ex) {
                 * java.util.logging.Logger.getLogger(DBConnectionDialog.class.getName()).log(java.util.logging.Level.SEVERE,
                 * null, ex); } catch (IllegalAccessException ex) {
                 * java.util.logging.Logger.getLogger(DBConnectionDialog.class.getName()).log(java.util.logging.Level.SEVERE,
                 * null, ex); } catch
                 * (javax.swing.UnsupportedLookAndFeelException ex) {
                 * java.util.logging.Logger.getLogger(DBConnectionDialog.class.getName()).log(java.util.logging.Level.SEVERE,
                 * null, ex);
                }
                 */

                try {
                    DBConnectionDialog dialog = new DBConnectionDialog();
                    dialog.showDialog();

                    ConnectionHandler h = UpgradeUtilities.connect(dialog);
                    while (!h.isCancelled() )
                    {
                        if (h.connectionAcquired())
                        {
                            setDsc(h.getDataStoreCoordinator());
                            setWaitingFlag(false);

                            final DBConnectionDialog d = dialog;
                            runOnEDT(new Runnable() {

                                @Override
                                public void run() {
                                    for (PropertyChangeListener l : listeners) {
                                        d.addPropertyChangeListener(l);
                                    }
                                    d.firePropertyChange("ovation.connectionChanged", 0, 1);
                                }
                            });
                            break;

                        }else if(h.errorOccurred())
                        {
                            dialog.showErrors(new RuntimeException(h.getError()));
                            dialog.showDialog();
                        }
                        
                        h = UpgradeUtilities.connect(dialog);
                        
                    } 
                    if (h.isCancelled())
                    {
                        Session s = Session.getCurrent();
                        if (s != null)
                        {
                           s.terminate();
                        }
                    }
                    
                   /* if (!dialog.isCancelled()) {
                            setDsc(dialog.getDataStoreCoordinator());
                            setWaitingFlag(false);

                            for (PropertyChangeListener l : listeners) {
                                dialog.addPropertyChangeListener(l);
                            }
                    }*/
                    
                } finally {
                    setWaitingFlag(false);
                }
            }
        };

        runOffEDT(r);

        return dsc;
    }

    private synchronized void setDsc(IAuthenticatedDataStoreCoordinator the_dsc) {
        dsc = the_dsc;
    }

    private synchronized void setWaitingFlag(boolean b) {
        waitingForDSC = b;
    }

    @Override
    public void addConnectionListener(ConnectionListener cl) {
        connectionListeners.add(cl);
    }

    @Override
    public void removeConnectionListener(ConnectionListener cl) {
        connectionListeners.remove(cl);
    }

    public static void runOnEDT(Runnable r) {
        if (EventQueue.isDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }
    
    public static void runAndWaitOnEDT(Runnable r) throws InterruptedException {
        if (EventQueue.isDispatchThread()) {
            r.run();
        } else {
            try{
                SwingUtilities.invokeAndWait(r);
            } catch (InvocationTargetException e)
            {
                e.printStackTrace(); //TODO: handle this better
            }
        }
    }
    
    public static void runOffEDT(Runnable r) {
        if (EventQueue.isDispatchThread()) {
            Thread t = new Thread(r);//TODO: executor service
            t.start();
        } else {
            r.run();
        }
    }
}