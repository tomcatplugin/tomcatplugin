package com.sysdeo.eclipse.tomcat;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */
 
import java.io.File;
import java.io.FilenameFilter;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


/**
 * See %TOMCAT3_HOME%/bin/tomcat.bat
 */	
public class Tomcat3Bootstrap extends TomcatBootstrap {
	
	static private String DEBUG_VIEW_LABEL = "Tomcat 3.3";

	/*
	 * @see TomcatBootstrap#getClasspath()
	 */
	public String[] getClasspath() {
		String[] classpath = new String[1];
		
		String toolsJarLocation = VMLauncherUtility.getVMInstall().getInstallLocation() + File.separator + "lib" + File.separator + "tools.jar";
		classpath[0] = toolsJarLocation;

		File libDir = new File(getTomcatDir() + File.separator + "lib");
		classpath = this.addJarsOfDirectory(classpath, libDir);			

		File containerDir = new File(getTomcatDir() + File.separator + "lib" + File.separator + "container");
		classpath = this.addJarsOfDirectory(classpath, containerDir);		

		File commonDir = new File(getTomcatDir() + File.separator + "lib" + File.separator + "common");
		classpath = this.addJarsOfDirectory(classpath, commonDir);		

		File appsDir = new File(getTomcatDir() + File.separator + "lib" + File.separator + "apps");
		classpath = this.addJarsOfDirectory(classpath, appsDir);		
				
		return classpath;
	}

	/*
	 * @see TomcatBootstrap#getMainClass()
	 */
	public String getMainClass() {
		return "org.apache.tomcat.startup.Main";
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
		String[] prgArgs = null;
		if(command.equals(getStartCommand())) {
			prgArgs = new String[3];			
			prgArgs[0] = command;	
			prgArgs[1] = "-config";
			prgArgs[2] = TomcatLauncherPlugin.getDefault().getConfigFile();				
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
		String[] vmArgs = new String[1];
		vmArgs[0] = "-Dtomcat.home=" + getTomcatDir();
				
		return vmArgs;
	}

	/*
	 * Add all jar files of directory dir to previous array
	 */
	protected String[] addJarsOfDirectory(String[] previous, File dir) {
		if((dir != null) && (dir.isDirectory())) {
			// Filter for .jar files
			FilenameFilter filter = new FilenameFilter() { 
				public boolean accept(File dir, String filename) {
					return filename.endsWith(".jar");
				}
			};
		
			String[] jars = null;
			
			File[] files = dir.listFiles(filter);
			jars = new String[files.length];
			for(int i=0; i<files.length; i++)
				jars[i] = files[i].getAbsolutePath();
			
			return StringUtil.concat(previous, jars);
		} else {
			return previous;	
		}
	}
	
	/*
	 * @see TomcatBootstrap#getXMLTagAfterContextDefinition()
	 */
	public String getXMLTagAfterContextDefinition() {
		return "</ContextManager>";
	}


	public IPath getServletJarPath() {
		return new Path("lib").append("common").append("servlet.jar");
	}
	
	public IPath getJasperJarPath() {	
		return new Path("lib").append("common").append("jasper-runtime.jar");
	}
	
	/**
	 * @see TomcatBootstrap#getLabel()
	 */
	public String getLabel() {
		return DEBUG_VIEW_LABEL;
	}	
	
	public String getContextWorkDir(String workFolder) {
		StringBuffer workDir = new StringBuffer("workDir=");
		workDir.append('"');
		workDir.append(workFolder);
		workDir.append('"');		
		return workDir.toString();
	}		
	
	/*
	 * No JSP jar for Tomcat 3, JSP classes are in servlet jar 
	 */
	public IPath getJSPJarPath() {
		return null;
	}

}

