package net.sf.eclipse.tomcat;

import java.io.File;
import java.util.ArrayList;

/**
 * Bootstrap specifics for Tomcat 7
 */
public class Tomcat7Bootstrap extends Tomcat6Bootstrap {

    @Override
    public String[] getClasspath() {
        ArrayList<String> classpath = new ArrayList<String>();
        classpath.add(getTomcatDir() + File.separator + "bin" + File.separator + "bootstrap.jar");
        classpath.add(getTomcatDir() + File.separator + "bin" + File.separator + "tomcat-juli.jar");
        // Add tools.jar JDK file to classpath
        String toolsJarLocation = VMLauncherUtility.getVMInstall().getInstallLocation() + File.separator + "lib" + File.separator + "tools.jar";
        if(new File(toolsJarLocation).exists()) {
            classpath.add(toolsJarLocation);
        }
        return (classpath.toArray(new String[0]));
    }

    @Override
    public String getLabel() {
        return "Tomcat 7.x";
    }
}
