/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.dbconnection;

import ovation.DatabaseIsUpgradingException;
import ovation.IAuthenticatedDataStoreCoordinator;
import us.physion.ovation.interfaces.IUpgradeDB;

/**
 * Handles the logic surrounding getting a database connection
 * @author huecotanks
 */
public class ConnectionHandler {
    public ConnectionHandler(String connectionFile, String username, String password)
    {
        this.connectionFile = connectionFile;
        this.username = username;
        this.password = password;
        
    }
    private String connectionFile;
    private String username;
    private String password;
    private boolean upgradeInProgress = false;
    private boolean upgradeRequired = false;
    private boolean connectionAcquired = false;
    private boolean cancelled = false;
    private String errorMessage;
    
    private IAuthenticatedDataStoreCoordinator dsc;
    
    private boolean shouldRun = false;
    
    private int dbVersion = -1;
    private int apiVersion = -2;
    
    public void setShouldRun(boolean b)
    {
        shouldRun = b;
    }
    
    public int getDatabaseVersion()
    {
        return dbVersion;
    }
    
    public int getAPIVersion()
    {
        return apiVersion;
    }
    
    public boolean upgradeInProgress()
    {
        return upgradeInProgress;
    }
    
    public boolean upgradeRequired()
    {
        return upgradeRequired;
    }
    
    public boolean connectionAcquired()
    {
        return connectionAcquired;
    }
    
    public boolean errorOccurred()
    {
        if (errorMessage == null)
            return false;
        return !errorMessage.isEmpty();
    }
    
    public void setError(String message)
    {
        errorMessage = message;
    }
    
    public void setUpgradeInProgress(boolean u)
    {
        upgradeInProgress = u;
    }
    
    public void setUpgradeRequired(boolean u)
    {
        upgradeRequired = u;
    }
    
    public void setConnectionAcquired(boolean u)
    {
        connectionAcquired = u;
    }
    
    public boolean isCancelled()
    {
        return cancelled;
    }
    
    public void setCancelled(boolean b)
    {
        cancelled = b;
    }
    
    public void setDatabaseVersion(int dbVersion)
    {
        this.dbVersion = dbVersion;
    }
   
    public void setAPIVersion(int apiVersion)
    {
        this.apiVersion = apiVersion;
    }
    
    public String getError()
    {
        return errorMessage;
    }
    
    public void checkShouldRunUpgrader() {
        checkShouldRunUpgrader(dbVersion, apiVersion, true, null);
    }
    //dependency injection, for testing

    public void checkShouldRunUpgrader(int databaseVersion, int apiVersion, boolean showDialogs, ShouldRunUpdaterDialog shouldRun) {
        if (databaseVersion < 0 || apiVersion < 0) {
            setError("Invalid database schema version (" + databaseVersion + ") or api schema version (" + apiVersion + ")");
            return;
        }

        if (databaseVersion > apiVersion) {
            if (showDialogs) {
                InstallLatestVersionDialog installVersionDialog = new InstallLatestVersionDialog();
                installVersionDialog.showDialog();
            }
        } else if (databaseVersion < apiVersion) {
            if (shouldRun == null) {
                shouldRun = new ShouldRunUpdaterDialog();
            }
            if (showDialogs) {
                shouldRun.showDialog();
            }
            setShouldRun(!shouldRun.isCancelled());
            return;
        }
        setShouldRun(false);
    }

    protected boolean runUpdater(final IUpgradeDB tool, UpdaterInProgressDialog inProgress, boolean showDialogs) {
        try {
            if (showDialogs) {
                inProgress.showDialog();
            }
            try {
                tool.start();
            } catch (DatabaseIsUpgradingException e) {
                inProgress.cancel();
                throw new RuntimeException(e);
                //TODO: pop up a dialog here

            } catch (Exception e) {
                inProgress.cancel();
                throw new RuntimeException(e);
            }
            if (inProgress.isCancelled()) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            setError(e.getLocalizedMessage());
            return false;
        }
    }
    
    public void setDataStoreCoordinator(IAuthenticatedDataStoreCoordinator dsc)
    {
        this.dsc = dsc;
    }
    
    public IAuthenticatedDataStoreCoordinator getDataStoreCoordinator()
    {
        return dsc;
    }
    public boolean shouldRunUpgrade()
    {
        return shouldRun;
    }
}
