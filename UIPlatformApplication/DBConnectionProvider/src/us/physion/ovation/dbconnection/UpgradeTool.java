/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.dbconnection;

import java.io.*;
import java.rmi.AlreadyBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import ovation.Ovation;
import ovation.OvationException;

import us.physion.ovation.interfaces.*;

/**
 *
 * @author huecotanks
 */
public class UpgradeTool implements IUpgradeDB{

    private IUpdateProgress pu;

    List<UpdateStep> steps;
    String connectionFile;
    String username;
    String password;
    IUpdateUI uiUpdater = null;
    
    public UpgradeTool(List<UpdateStep> updateSteps, String connectionFile, String username, String password)
    {
        steps = updateSteps;
        this.connectionFile = connectionFile;
        this.username = username;
        this.password = password;
    }
    
    public UpgradeTool(List<UpdateStep> updateSteps, String connectionFile, String username, String password, IUpdateUI ui)
    {
        this(updateSteps, connectionFile, username, password);
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

    public void start()
    {
        start(connectionFile, username, password);
    }
    public void start(String connectionFile, String username, String password) {
        //TODO: security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        try{
            pu = new ProgressUpdater(connectionFile, username, password, uiUpdater);
            UnicastRemoteObject.exportObject(pu, 10002);
            Registry r = LocateRegistry.createRegistry(10002);
            r.bind("ProgressUpdater", pu);

        }catch (RemoteException e)
        {
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
        int totalSteps = steps.size();
        int stepNumber = 1;
        for (UpdateStep step : steps)
        {
            if (step instanceof UpdateJarStep)
            {
                File file = new File(step.getStepDescriptor());//grab from jar if resource, download from s3 is another possibility
                try{
                    ProcessBuilder pb = new ProcessBuilder("java",
                            "-Djava.security.policy=security.txt",
                            "-cp",
                            System.getProperty("java.class.path") + ":" + file.getAbsolutePath(),
                            "-jar",
                            file.getAbsolutePath(),
                            connectionFile,
                            username,
                            password);

                    startProcess(pb);

                } catch(Exception e)
                {
                    throw new RuntimeException("Could not run jar '" + file + "'. " + e.getMessage());
                }
            }else if (step instanceof UpdateSchemaStep)
            {
                File file = new File(step.getStepDescriptor());//grab from jar if resource, download from s3 is another possibility
                try{
                    ProcessBuilder pb = new ProcessBuilder(new File(objyBin, "ooschemaupgrade").getPath(),
                            "-infile",
                            file.getAbsolutePath(),
                            connectionFile);

                    startProcess(pb);

                } catch(Exception e)
                {
                    throw new RuntimeException("Could not upgrade schema using file '" + file + "'. " + e.getMessage());
                }
            }
            update(-1, "Completed upgrade step " + stepNumber++ + " of " + totalSteps);
        }
        System.out.println("Done");
        update(100, "Done");
    }
    
    private boolean startProcess(ProcessBuilder pb) throws IOException, InterruptedException {
        Process p = pb.start();
        
        InputStreamHandler inputHandler = new InputStreamHandler(new InputStreamReader(p.getInputStream()));
        InputStreamHandler errHandler = new InputStreamHandler(new InputStreamReader(p.getErrorStream()));

        inputHandler.start();
        errHandler.start();

        try {
            int err = p.waitFor();
            if (err != 0) {
                System.out.println("Error: " + errHandler.getCaptureBuffer());
                Ovation.getLogger().error("Unable to complete command: " + errHandler.getCaptureBuffer());
                return false;
            }
        } catch (InterruptedException e) {
            throw new OvationException("Unable to complete command: " + e.getLocalizedMessage());
        }
        return true;
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
    static class InputStreamHandler extends Thread
    {

        private StringBuffer captureBuffer;
        private InputStreamReader stream;

        public InputStreamReader getStream() {
            return stream;
        }

        public StringBuffer getCaptureBuffer() {
            return captureBuffer;
        }


        InputStreamHandler(InputStreamReader inputStream) {
            stream = inputStream;
            captureBuffer = new StringBuffer();
        }

        public void run() {
            try {
                int nextChar;
                while((nextChar = getStream().read()) >= 0) {
                    getCaptureBuffer().append((char)nextChar);
                }
            }
            catch(IOException e) {
                Ovation.getLogger().error(e.getLocalizedMessage());
            }
        }
    }
}
