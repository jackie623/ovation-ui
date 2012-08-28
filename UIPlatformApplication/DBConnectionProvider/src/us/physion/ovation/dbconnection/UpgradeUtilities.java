/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.dbconnection;

import com.objy.db.DatabaseNotFoundException;
import com.objy.db.DatabaseOpenException;
import com.objy.db.app.Session;
import java.util.*;
import org.openide.util.Lookup;
import ovation.*;
import us.physion.ovation.interfaces.IUpdateUI;
import us.physion.ovation.interfaces.IUpgradeDB;
import us.physion.ovation.interfaces.UpdateInfo;

/**
 *
 * @author huecotanks
 */
public class UpgradeUtilities {
   
    
    public static ConnectionHandler connect(DBConnectionDialog connectionDialog)
    {
        if (connectionDialog.isCancelled())
        {
            return null;
        }
        
        String username = connectionDialog.getUsername();
        String password = connectionDialog.getPassword();
        String connectionFile = connectionDialog.getConnectionFile();

        Session s = Session.getCurrent();
        if (s != null) {
            s.terminate();
        }
        ConnectionHandler cd = getConnectionHandler(connectionFile, username, password);
        if (cd.errorOccurred())
        {
            return cd;
        }
        if (cd.upgradeInProgress()) {
            DBIsUpgradingDialog d = new DBIsUpgradingDialog();
            d.showDialog();
            if (d.forceUpgrade()) {
                DataStoreCoordinator dsc = null;
                try {
                    dsc = DataStoreCoordinator.coordinatorWithConnectionFile(connectionFile);
                    dsc.removeUpgradeLock("OVATION_UPGRADE_FLAG");
                } catch (DatabaseOpenException ex1) {
                    cd.setError(ex1.getLocalizedMessage());
                    return cd;
                } catch (DatabaseNotFoundException ex1) {
                    cd.setError(ex1.getLocalizedMessage());
                    return cd;

                }finally{
                    dsc.close();
                }
                return connect(connectionDialog);
            }
            else{
                cd.setCancelled(true);
                return cd;
            }
        }
        if (cd.upgradeRequired()) {
            cd.checkShouldRunUpgrader();
            if (cd.errorOccurred()) {
                return cd;
            }
            if (!cd.shouldRunUpgrade()) {
                cd.setCancelled(true);
                return cd;
            }

            int databaseVersion = cd.getDatabaseVersion();
            int apiVersion = cd.getAPIVersion();

            UpdaterInProgressDialog uiUpdater = new UpdaterInProgressDialog();
            UpgradeTool tool = getUpgradeTool(connectionFile, username, password, uiUpdater, databaseVersion, apiVersion);

            cd.runUpdater(tool, uiUpdater, true);
            if (cd.errorOccurred()) {
                return cd;
            }

            return connect(connectionDialog);
        }
        
        return cd;
    }
    
    public static UpgradeTool getUpgradeTool(String connectionFile, String username, String password, UpdaterInProgressDialog uiUpgrader, int databaseVersion, int apiVersion)
    {
        Collection<? extends UpdateInfo> updates = Lookup.getDefault().lookupAll(UpdateInfo.class);
        List<UpdateInfo> versions = new LinkedList<UpdateInfo>();
        for (UpdateInfo u : updates) {
            int updateVersion = u.getSchemaVersion();
            if (updateVersion > databaseVersion && updateVersion <= apiVersion) {
                versions.add(u);
            }
        }
        Collections.sort(versions, new UpdateComparator());
        UpgradeTool tool = new UpgradeTool(versions, connectionFile, username, password, uiUpgrader);
        uiUpgrader.setUpgradeTool(tool);
        return tool;
    }
    
    public static ConnectionHandler getConnectionHandler(String connectionFile, String username, String password)
    {
        ConnectionHandler connectionHandler = new ConnectionHandler(connectionFile, username, password);
        DataContext c;
        try {
            c = DataStoreCoordinator.coordinatorWithConnectionFile(connectionFile).getContext();
            c.authenticateUser(username, password);
            connectionHandler.setDataStoreCoordinator(c.getAuthenticatedDataStoreCoordinator());
            connectionHandler.setConnectionAcquired(true);
        } catch (SchemaVersionException ex2) {
            connectionHandler.setDatabaseVersion(ex2.getDatabaseSchemaNumber());
            connectionHandler.setAPIVersion(ex2.getAPISchemaNumber());
            connectionHandler.setUpgradeRequired(true);
            return connectionHandler;
            
        } catch (DatabaseIsUpgradingException ex)
        {
            connectionHandler.setUpgradeInProgress(true);
            return connectionHandler;
        } catch (Exception e)
        {
            connectionHandler.setError(e.getLocalizedMessage());
            return connectionHandler;
        }
        
        return connectionHandler;
    }

   /* public DataContext getContextFromConnectionFile(DBConnectionDialog connectionDialog, String connectionFile, String username, String password) {
        DataContext c = null;
        try {
            c = DataStoreCoordinator.coordinatorWithConnectionFile(connectionFile).getContext();
        } catch (SchemaVersionException ex2) {
            int databaseVersion = ex2.getDatabaseSchemaNumber();
            int apiVersion = ex2.getAPISchemaNumber();
            boolean success = shouldRunUpgrader(databaseVersion, apiVersion); //ask the user if they want to run the upgrader
            if (success) {
                Collection<? extends UpdateInfo> updates = Lookup.getDefault().lookupAll(UpdateInfo.class);
                List<UpdateInfo> versions = new LinkedList<UpdateInfo>();
                for (UpdateInfo u : updates) {
                    int updateVersion = u.getSchemaVersion();
                    if (updateVersion > databaseVersion && updateVersion <= apiVersion) {
                        versions.add(u);
                    }
                }
                Collections.sort(versions, new UpdateComparator());
                UpdaterInProgressDialog uiUpdater = new UpdaterInProgressDialog();
                UpgradeTool tool = new UpgradeTool(versions, connectionFile, username, password, uiUpdater);
                uiUpdater.setUpgradeTool(tool);
                try {
                    success = runUpdater(tool, uiUpdater, true);
                } catch (Exception e) {
                    connectionDialog.cancelled = true;
                    error(connectionDialog, e);
                    return c;
                }
            }
            if (success) {
                try {
                    c = DataStoreCoordinator.coordinatorWithConnectionFile(connectionFile).getContext();
                } catch (DatabaseOpenException ex) {
                    error(connectionDialog, ex);
                    return c;
                } catch (DatabaseNotFoundException ex) {
                    error(connectionDialog, ex);
                    return c;
                }
            } else {
                connectionDialog.cancelled = true;
                return c;
            }
        } catch (DatabaseIsUpgradingException ex) {
            DBIsUpgradingDialog d = new DBIsUpgradingDialog();
            d.showDialog();
            if (d.forceUpgrade()) {
                try {
                    DataStoreCoordinator.coordinatorWithConnectionFile(connectionFile).removeUpgradeLock("OVATION_UPGRADE_FLAG");
                } catch (DatabaseOpenException ex1) {
                    error(connectionDialog, ex);
                    return c;
                } catch (DatabaseNotFoundException ex1) {
                    error(connectionDialog, ex);
                    return c;
                }
                return getContextFromConnectionFile(connectionDialog, connectionFile, username, password);
            }

        } catch (Exception ex) {
            error(connectionDialog, ex);
            return c;
        }
        return c;
    }

    

    private void error(DBConnectionDialog d, Exception e) {
        d.showErrors(e);
    }*/

    static class UpdateComparator implements Comparator<UpdateInfo> {

        @Override
        public int compare(UpdateInfo t, UpdateInfo t1) {
            return t.getSchemaVersion() - t1.getSchemaVersion();
        }
    }
}
