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

import us.physion.ovation.interfaces.*;

/**
 *
 * @author huecotanks
 */
public class UpgradeTool implements IUpgradeDB, IUpdateUI{

    private IUpdateProgress pu;

    public static void runUpgrade (String[] args) throws Exception {
        UpgradeTool ut = new UpgradeTool();

        Properties p = new Properties(System.getProperties());
        System.setProperty("java.security.policy", "security.txt");

        if (args.length == 3)
        {
            ut.start(args[0], args[1], args[2]);
        }
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

    public void start(String connectionFile, String username, String password, List<UpdateStep> steps) {
        //TODO: security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        try{
            pu = new ProgressUpdater(connectionFile, username, password, this);
            UnicastRemoteObject.exportObject(pu, 10001);
            Registry r = LocateRegistry.createRegistry(10001);
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
        }
    }
    
    private void startProcess(ProcessBuilder pb) throws IOException, InterruptedException {
        Process p = pb.start();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(p.getInputStream()));
        BufferedReader err = new BufferedReader(
                new InputStreamReader(p.getErrorStream()));
        p.waitFor();

        String line;

        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }

        String errorMessage = "";
        while ((line = err.readLine()) != null) {
            errorMessage += line;
        }

        if (!errorMessage.equals("")) {
            throw new RuntimeException(errorMessage);
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

    @Override
    public void update(int i, String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
