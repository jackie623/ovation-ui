/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.browser;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import ovation.test.TestManager;

/**
 *
 * @author huecotanks
 */
public class BrowserTestManager extends TestManager {

    @Override
    public String getLicenseText() {
        return "crS9RjS6wJgmZkJZ1WRbdEtIIwynAVmqFwrooGgsM7ytyR+wCD3xpjJEENey+b0GVVEgib++HAKh94LuvLQXQ2lL2UCUo75xJwVLL3wmd21WbumQqKzZk9p6fkHCVoiSxgon+2RaGA75ckKNmUVTeIBn+QkalKCg9p1P7FbWqH3diXlAOKND2mwjI8V4unq7aaKEUuCgdU9V/BjFBkoytG8FzyBCNn+cBUNTByYy7RxYxH37xECZJ6/hG/vP4QjKpks9cu3yQL9QjXBQIizrzini0eQj62j+QzCSf0oQg8KdIeZHuU+ZSZZ1pUHLYiOiQWaOL9cVPxqMzh5Q/Zvu6Q==";
    }

    @Override
    public String getLicenseInstitution() {
        return "Institution";
    }

    @Override
    public String getLicenseGroup() {
        return "Lab";
    }

    @Override
    public String getConnectionFile() {
        System.out.println("Getting connection file");
        String pwd = "";/*AccessController.doPrivileged(new PrivilegedAction<String>() {

            public String run() {
                String pwd = System.getProperty("WORKSPACE");
                return pwd;
            }
        });*/
        System.out.println(System.getProperty("java.security.debug"));
        System.out.println(System.getProperty("java.security.policy"));
        System.out.println("Got working dir");
        String path = pwd + "data" + File.separator
                + "browser" + File.separator
                + "browser-test.connection";
        System.out.println("Got connection file");

        return path;
    }

    @Override
    public String getFirstUserName() {
        return "TestUser";
    }

    @Override
    public String getFirstUserPassword() {
        return "password";
    }
}
