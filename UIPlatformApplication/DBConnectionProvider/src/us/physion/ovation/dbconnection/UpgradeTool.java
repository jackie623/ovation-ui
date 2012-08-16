/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.dbconnection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.rmi.AlreadyBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import us.physion.ovation.interfaces.IUpgradeDB;
import us.physion.ovation.interfaces.IUpdateProgress;
import us.physion.ovation.interfaces.IUpdateUI;
import us.physion.ovation.interfaces.ProgressUpdater;

/**
 *
 * @author huecotanks
 */
public class UpgradeTool implements IUpgradeDB, IUpdateUI{

    private IUpdateProgress pu;
    private static boolean production;

    public static void runUpgrade (String[] args) throws Exception {
        UpgradeTool ut = new UpgradeTool();

        Properties p = new Properties(System.getProperties());
        production = !p.contains("EXECUTION_STEPS_DIR");

        System.setProperty("java.security.policy", getExecDir() + "/security.txt");
        System.out.println(System.getProperty("java.security.policy"));

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

    public void start(String connectionFile, String username, String password) {
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
        ArrayList<File> filesToExecute = getFilesToExecute();

        boolean requiresPlugins = false;
        File execDir = getExecDir();
        File pluginDir = new File(execDir, platform);
        if(pluginDir.exists() && !pluginDir.getAbsolutePath().equals(execDir.getAbsolutePath()))
        {
            requiresPlugins = true;
        }

        String objyLib = objyBin + "/../lib";
        for (File file : filesToExecute)
        {
            if (file.getName().contains(".schema")){
                try{
                    ProcessBuilder pb = new ProcessBuilder(new File(objyBin, "ooschemaupgrade").getPath(),
                            "-infile",
                            file.getAbsolutePath(),
                            connectionFile);
                    if (requiresPlugins)
                    {
                        updateEnvironment(pluginDir, objyLib, pb);

                    }

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

                    if (!errorMessage.equals(""))
                    {
                        throw new RuntimeException(errorMessage);
                    }

                } catch(Exception e)
                {
                    throw new RuntimeException("Could not upgrade schema using file '" + file + "'. " + e.getMessage());
                }

            }
            if (file.getName().endsWith(".jar")){

                try{
                    ProcessBuilder pb = new ProcessBuilder("java",
                            "-Djava.security.policy=" + getExecDir() + "/security.txt",
                            "-cp",
                            System.getProperty("java.class.path") + ":" + file.getAbsolutePath(),
                            "-jar",
                            file.getAbsolutePath(),
                            connectionFile,
                            username,
                            password);

                    if (requiresPlugins)
                    {
                        updateEnvironment(pluginDir, objyLib, pb);
                    }
                    Process p = pb.start();
                    System.out.println("Started");
                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                    String line;

                    while (true) {
                        try {
                            int exitCode = p.exitValue();
                            break;
                        } catch (IllegalThreadStateException e) {
                            while ((line = in.readLine()) != null) {
                                System.out.println(line);
                            }
                        }
                    }


                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }

                    String errorMessage = "";
                    while ((line = err.readLine()) != null) {
                        errorMessage += line;
                    }

                    if (!errorMessage.equals(""))
                    {
                        throw new RuntimeException(errorMessage);
                    }
                    System.out.println("Done");

                } catch(Exception e)
                {
                    throw new RuntimeException("Could not run jar '" + file + "'. " + e.getMessage());
                }
            }
        }
    }

    public static File getExecDir() {
        if ( production )//in production
        {
            //OMG super lame
            return new File(new File(UpgradeTool.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent());
        }

        return new File(System.getProperty("EXECUTION_STEPS_DIR"));

    }

    public ArrayList<File> getFilesToExecute() {
        Scanner s = null;
        File execDir = getExecDir();
        File executionSteps = null;
        File jar_dir = null;
        File schema_dir = null;

        if ( production )
        {
            jar_dir = execDir;
            schema_dir = execDir;
        }else{// for testing in intellij

            jar_dir = new File(execDir, "../artifacts");
            schema_dir = new File(execDir, "../../../schemas/current");
        }

        executionSteps = new File(execDir, "execution_steps.txt");

        try{
            s = new Scanner(executionSteps);
        }
        catch(NullPointerException e)
        {
            throw new RuntimeException("File not found: execution_steps.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }

        ArrayList<File> filesToExecute = new ArrayList<File>();

        while (s.hasNext())
        {
            String filename = s.next().replaceAll("\\s+$", "").replaceAll(",", "");
            File f = null;
            if (filename.endsWith(".jar"))
                f = new File(jar_dir, filename);
            else if(filename.contains(".schema"))
                f = new File(schema_dir, filename);
            else
                continue;

            if (! f.exists())
            {
                throw new RuntimeException("File not found: " + f.getAbsolutePath());
            }

            filesToExecute.add(f);

        }
        if (filesToExecute.isEmpty())
        {
            throw new RuntimeException("Nothing to execute");
        }
        return filesToExecute;
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
