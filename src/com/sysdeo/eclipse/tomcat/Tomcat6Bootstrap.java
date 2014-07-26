package com.sysdeo.eclipse.tomcat;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;


/**
 * See %TOMCAT5_HOME%/bin/catalina.bat
 */	
public class Tomcat6Bootstrap extends TomcatBootstrap {

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
			prgArgs[0] = "-config";
			prgArgs[1] = "\"" + TomcatLauncherPlugin.getDefault().getConfigFile() + "\"";	
			prgArgs[2] = command;	
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
		ArrayList vmArgs = new ArrayList();
		vmArgs.add("-Dcatalina.home=\"" + getTomcatDir() + "\"");
				
		String endorsedDir = getTomcatDir() + File.separator + "endorsed";
		vmArgs.add("-Djava.endorsed.dirs=\"" + endorsedDir + "\"");
		
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
		
		return ((String[])vmArgs.toArray(new String[0])); 			
	}


	/*
	 * @see TomcatBootstrap#getXMLTagAfterContextDefinition()
	 */
	public String getXMLTagAfterContextDefinition() {
		return "</Host>";
	}
		
	public IPath getJasperJarPath() {	
		return new Path("lib").append("jasper.jar");
	}

	public IPath getServletJarPath() {	
		return new Path("lib").append("servlet-api.jar");
	}	

	public IPath getJSPJarPath() {
		return new Path("lib").append("jsp-api.jar");
	}	
	
	public IPath getElJarPath() {
		return new Path("lib").append("el-api.jar");
	}	

	public IPath getAnnotationsJarPath() {
		return new Path("lib").append("annotations-api.jar");
	}
	
	public Collection getTomcatJars() {
		IPath tomcatHomePath = TomcatLauncherPlugin.getDefault().getTomcatIPath();
		ArrayList jars = (ArrayList) super.getTomcatJars();
		jars.add(JavaCore.newVariableEntry(tomcatHomePath.append(this.getElJarPath()), null, null));
		jars.add(JavaCore.newVariableEntry(tomcatHomePath.append(this.getAnnotationsJarPath()), null, null));
		return jars;
	}

	/**
	 * @see TomcatBootstrap#getLabel()
	 */
	public String getLabel() {
		return "Tomcat 6.x";
	}

	public String getContextWorkDir(String workFolder) {
		StringBuffer workDir = new StringBuffer("workDir=");
		workDir.append('"');
		workDir.append(workFolder);
		workDir.append('"');		
		return workDir.toString();
	}

}

