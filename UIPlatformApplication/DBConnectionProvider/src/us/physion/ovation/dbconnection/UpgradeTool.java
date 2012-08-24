/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.dbconnection;

import com.objy.db.DatabaseNotFoundException;
import com.objy.db.DatabaseOpenException;
import java.io.*;
import java.lang.reflect.Method;
import java.rmi.AlreadyBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import org.openide.util.Exceptions;
import ovation.DataStoreCoordinator;
import ovation.DatabaseIsUpgradingException;
import ovation.Ovation;
import ovation.OvationException;
import ovation.util.CommandUtilities;
import us.physion.ovation.dbconnection.UpdateJarStep;
import us.physion.ovation.dbconnection.UpdateSchemaStep;

import us.physion.ovation.interfaces.*;

/**
 *
 * @author huecotanks
 */
public class UpgradeTool implements IUpgradeDB {

    private IUpdateProgress pu;
    List<UpdateInfo> updates;
    String connectionFile;
    String username;
    String password;
    IUpdateUI uiUpdater = null;
    boolean forceUpdate = false;

    public UpgradeTool(List<UpdateInfo> updates, String connectionFile, String username, String password) {
        this.updates = updates;
        this.connectionFile = connectionFile;
        this.username = username;
        this.password = password;
    }

    public UpgradeTool(List<UpdateInfo> updates, String connectionFile, String username, String password, IUpdateUI ui) {
        this(updates, connectionFile, username, password);
        uiUpdater = ui;
    }

    public static boolean isWindows() {

        String os = System.getProperty("os.name").toLowerCase();
        // windows
        return (os.contains("win"));

    }

    public static boolean isMac() {

        String os = System.getProperty("os.name").toLowerCase();
        // Mac
        return (os.contains("mac"));

    }

    public static boolean isLinux() {

        String os = System.getProperty("os.name").toLowerCase();
        // linux
        return (os.contains("linux"));

    }

    public void start() {
        DataStoreCoordinator dsc = null;
        try {
            dsc = DataStoreCoordinator.coordinatorWithConnectionFile(connectionFile, "UPGRADE");
        } catch (DatabaseOpenException ex) {
            throw new OvationException(ex.getMessage(), ex);
        } catch (DatabaseNotFoundException ex) {
            throw new OvationException(ex.getMessage(), ex);
        }
        
        if (dsc.getStringDatabasePreference("OVATION_UPGRADE_CONNECTION_LIST").split(":").length > 1)
        {
            dsc.removeFromConnectionList(dsc.getContext(), "OVATION_UPGRADE_CONNECTION_LIST");
            throw new DatabaseIsUpgradingException();
        }
        while (dsc.getConnectedHosts().size() != 0)
        {
            update(-1, "Waiting for " + dsc.getConnectedHosts().size() + " other users to disconnect...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                throw new OvationException(ex.getMessage());
            }
            
            if (forceUpdate)
            {
                break;
            }
        }
        start(connectionFile, username, password);
    }
    
    public void forceUpdate()
    {
        forceUpdate = true;
    }

    public void start(String connectionFile, String username, String password) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        try {
            pu = new ProgressUpdater(connectionFile, username, password, uiUpdater);
            UnicastRemoteObject.exportObject(pu, 10002);
            Registry r = LocateRegistry.createRegistry(10002);
            r.bind("ProgressUpdater", pu);

        } catch (RemoteException e) {
            throw new RuntimeException(e.getMessage());
        } catch (AlreadyBoundException e) {
            throw new RuntimeException(e.getMessage());
        }

        String platform = "";
        File objyBin = null;
        if (isWindows()) {
            objyBin = new File("C:\\Program Files\\Physion\\Ovation\\Objectivity\\bin"); //TODO: check this path
            platform = "Windows";
        } else if (isLinux()) {
            objyBin = new File("/usr/object/linux86_64/bin/");
            platform = "Linux";
        } else if (isMac()) {
            objyBin = new File("/opt/object/mac86_64/bin/");
            platform = "Darwin";
        }

        System.out.println("Running on platform " + platform);
        update(-1, "Initializing");
        int totalUpdates = updates.size();
        try {
            for (UpdateInfo update : updates) {
                List<UpdateStep> steps = update.getUpdateSteps();
                for (UpdateStep step : steps) {

                    if (step instanceof UpdateJarStep) {

                        File file = new File(step.getStepDescriptor());//grab from jar if resource, download from s3 is another possibility
                        try {
                            ProcessBuilder pb = new ProcessBuilder("java",
                                    "-Djava.security.policy=security.txt",
                                    "-cp",
                                    System.getProperty("java.class.path") + ":" + file.getAbsolutePath(),
                                    "-jar",
                                    file.getAbsolutePath(),
                                    connectionFile,
                                    username,
                                    password);

                            CommandUtilities.runCommand(pb);

                        } catch (Exception e) {
                            throw new RuntimeException("Could not run jar '" + file + "'. " + e.getMessage());
                        }
                    } else if (step instanceof UpdateSchemaStep) {
                        File file = new File(step.getStepDescriptor());//grab from jar if resource, download from s3 is another possibility
                        try {
                            ProcessBuilder pb = new ProcessBuilder(new File(objyBin, "ooschemaupgrade").getPath(),
                                    "-infile",
                                    file.getAbsolutePath(),
                                    connectionFile);

                            CommandUtilities.runCommand(pb);

                        } catch (Exception e) {
                            throw new RuntimeException("Could not upgrade schema using file '" + file + "'. " + e.getMessage());
                        }
                    }
                }
                System.out.println("Done with update");

                try {
                    DataStoreCoordinator dsc = DataStoreCoordinator.coordinatorWithConnectionFile(connectionFile, "UPGRADE");
                    dsc.getContext();
                    dsc.setDatabasePreference("OVATION_SCHEMA_VERSION", String.valueOf(update.getSchemaVersion()));
                    dsc.close();
                } catch (DatabaseOpenException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (DatabaseNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            try {
                DataStoreCoordinator dsc = DataStoreCoordinator.coordinatorWithConnectionFile(connectionFile, "UPGRADE");
                dsc.upgradeFinished();
                dsc.close();
            } catch (DatabaseOpenException ex) {
                Exceptions.printStackTrace(ex);
            } catch (DatabaseNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }

            update(100, "Done");
            
            
        } catch (Exception e) {
            if (uiUpdater != null) {
                uiUpdater.cancel();
            }
            throw new OvationException(e.getMessage(), e);
        }
    }
  
    private void updateEnvironment(File pluginDir, String objyLib, ProcessBuilder pb) {
        Map env = pb.environment();
        env.put("OO_PLUGIN_SPEC_DIR", pluginDir.getAbsolutePath());
        env.put("DYLD_LIBRARY_PATH", pluginDir.getAbsolutePath() + ":" + objyLib+ ":$DYLD_LIBRARY_PATH");
        env.put("LD_LIBRARY_PATH", pluginDir.getAbsolutePath() + ":" + objyLib+ ":$LD_LIBRARY_PATH");

        if(isWindows()) {
            env.put("PATH", env.get("PATH") + ";" + pluginDir.getAbsolutePath() + ";" + objyLib);
        }
    }

    private void update(int percent, String text)
    {
        if (uiUpdater == null)
            return;
        
        uiUpdater.update(percent, text);
    }
}
