package com.sysdeo.eclipse.tomcat;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


/**
 * See %TOMCAT4_HOME%/bin/catalina.bat
 */	
public class Tomcat4Bootstrap extends TomcatBootstrap {
		
	/*
	 * @see TomcatBootstrap#getClasspath()
	 */
	public String[] getClasspath() {
		ArrayList classpath = new ArrayList();
		classpath.add(getTomcatDir() + File.separator + "bin" + File.separator + "bootstrap.jar");
		// Add tools.jar JDK file to classpath
		String toolsJarLocation = VMLauncherUtility.getVMInstall().getInstallLocation() + File.separator + "lib" + File.separator + "tools.jar";
		if(new File(toolsJarLocation).exists()) {
			classpath.add(toolsJarLocation);
		}
		return ((String[])classpath.toArray(new String[0]));
	}

	/*
	 * @see TomcatBootstrap#getMainClass()
	 */
	public String getMainClass() {
		return "org.apache.catalina.startup.Bootstrap";
	}

	/*
	 * @see TomcatBootstrap#getStartCommand()
	 */
	public String getStartCommand() {
		return "start";
	}

	/*
	 * @see TomcatBootstrap#getStopCommand()
	 */
	public String getStopCommand() {
		return "stop";
	}

	public String[] getPrgArgs(String command) {
		String[] prgArgs;
		if (TomcatLauncherPlugin.getDefault().getConfigMode().equals(TomcatLauncherPlugin.SERVERXML_MODE)) {
			prgArgs = new String[3];
			prgArgs[0] = command;				
			prgArgs[1] = "-config";
			prgArgs[2] = "\"" + TomcatLauncherPlugin.getDefault().getConfigFile() + "\"";	
		} else {
			prgArgs = new String[1];
			prgArgs[0] = command;				
		
		}
		return prgArgs;
	}

	/*
	 * @see TomcatBootstrap#getVmArgs()
	 */
	public String[] getVmArgs() {
		String[] vmArgs = new String[3];
		vmArgs[0] = "-Dcatalina.home=\"" + getTomcatDir() + "\"";

		String binDir = getTomcatDir() + File.separator + "bin";
		String commonLibDir = getTomcatDir() + File.separator + "common" + File.separator + "lib";
		vmArgs[1] = "-Djava.endorsed.dirs=\"" + binDir + File.pathSeparator + commonLibDir + "\"";
				
		if (getTomcatBase().length() != 0) {
			vmArgs[2] = "-Dcatalina.base=\"" + getTomcatBase() + "\"";
		} else {
			vmArgs[2] = "-Dcatalina.base=\"" + getTomcatDir() + "\"";
		}
		
		return vmArgs;
	}

	/*
	 * @see TomcatBootstrap#getXMLTagAfterContextDefinition()
	 */
	public String getXMLTagAfterContextDefinition() {
		return "</Host>";
	}

	public IPath getServletJarPath() {
		return new Path("common").append("lib").append("servlet.jar");
	}
	
	public IPath getJasperJarPath() {	
		return new Path("lib").append("jasper-runtime.jar");
	}

	/*
	 * No JSP jar for Tomcat 4, JSP classes are in servlet jar 
	 */
	public IPath getJSPJarPath() {
		return null;
	}	
	
	/**
	 * @see TomcatBootstrap#getLabel()
	 */
	public String getLabel() {
		return "Tomcat 4.0.x";
	}
	

	public String getContextWorkDir(String workFolder) {
		StringBuffer workDir = new StringBuffer("workDir=");
		workDir.append('"');
		workDir.append(workFolder);

		workDir.append(File.separator);
		workDir.append("org");
		workDir.append(File.separator);
		workDir.append("apache");
		workDir.append(File.separator);
		workDir.append("jsp");
		
		workDir.append('"');		
		return workDir.toString();
	}	
}

