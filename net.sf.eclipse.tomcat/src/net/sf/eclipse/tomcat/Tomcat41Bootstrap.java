package net.sf.eclipse.tomcat;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


/**
 * Bootstrap specifics for Tomcat 4.1
 *
 * See %TOMCAT4_HOME%/bin/catalina.bat
 */
public class Tomcat41Bootstrap extends Tomcat4Bootstrap {


    /*
     * @see TomcatBootstrap#getVmArgs()
     */
    @Override
    public String[] getVmArgs() {
        ArrayList<String> vmArgs = new ArrayList<String>();
        vmArgs.add("-Dcatalina.home=\"" + getTomcatDir() + "\"");

        //		This is not needed in Tomcat 4.1.24, does it come from a previous version
        // 		Does not work with Tomcat 4.1.29
        //		String binDir = getTomcatDir() + File.separator + "bin";
        //		vmArgs.add("-Djava.endorsed.dirs=\"" + binDir + File.pathSeparator + commonEndorsedDir + "\"");

        String commonEndorsedDir = getTomcatDir() + File.separator + "common" + File.separator + "endorsed";
        vmArgs.add("-Djava.endorsed.dirs=\"" + commonEndorsedDir + "\"");


        String catalinaBase = getTomcatBase();
        if(catalinaBase.length() == 0) {
            catalinaBase = getTomcatDir();
        }

        vmArgs.add("-Dcatalina.base=\"" + catalinaBase + "\"");
        vmArgs.add("-Djava.io.tmpdir=\"" + catalinaBase + File.separator + "temp\"");

        if(TomcatLauncherPlugin.getDefault().isSecurityManagerEnabled()) {
            vmArgs.add("-Djava.security.manager");
            String securityPolicyFile = catalinaBase + File.separator + "conf" + File.separator + "catalina.policy";
            vmArgs.add("-Djava.security.policy=\"" + securityPolicyFile + "\"");
        }

        return (vmArgs.toArray(new String[0]));
    }


    @Override
    public IPath getJasperJarPath() {
        return new Path("common").append("lib").append("jasper-runtime.jar");
    }


    /**
     * @see TomcatBootstrap#getLabel()
     */
    @Override
    public String getLabel() {
        return "Tomcat 4.1.x";
    }
}

