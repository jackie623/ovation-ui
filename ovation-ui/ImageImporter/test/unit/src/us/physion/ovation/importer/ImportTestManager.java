/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.importer;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import ovation.test.TestManager;

/**
 *
 * @author huecotanks
 */
public class ImportTestManager extends TestManager{

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
        String pwd = AccessController.doPrivileged(new PrivilegedAction<String>() {

            public String run() {
                String pwd = System.getProperty("WORKSPACE");
                return pwd;
            }
        });
        if (pwd == null)
        {
            pwd = "/Users/jackie/Ovation/ovation-ui/";
        }
        String path = pwd + "data" + File.separator
                + "import" + File.separator
                + "import-test.connection";

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
