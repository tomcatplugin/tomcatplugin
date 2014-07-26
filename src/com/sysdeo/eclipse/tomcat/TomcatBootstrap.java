package com.sysdeo.eclipse.tomcat;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputer;
import org.eclipse.debug.core.sourcelookup.containers.DefaultSourceContainer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.launching.JavaSourceLookupDirector;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaProjectSourceContainer;

import com.sysdeo.eclipse.tomcat.editors.ProjectListElement;

/**
 * Start and stop Tomcat
 * Subclasses contains all information specific to a Tomcat Version
 */

public abstract class TomcatBootstrap {

	private static final String WEBAPP_CLASSPATH_FILENAME = ".#webclasspath";
	private static final int RUN = 1;
	private static final int LOG = 2;	
	private static final int ADD_LAUNCH = 3;
	
	public abstract String[] getClasspath();
	public abstract String[] getVmArgs();
	public abstract String[] getPrgArgs(String command);
	public abstract String getStartCommand();
	public abstract String getStopCommand();
	public abstract String getMainClass();

	abstract public String getLabel();

	abstract public String getContextWorkDir(String workFolder);

	abstract public IPath getServletJarPath();
	abstract public IPath getJasperJarPath();
	abstract public IPath getJSPJarPath();

	public Collection getTomcatJars() {
		IPath tomcatHomePath = TomcatLauncherPlugin.getDefault().getTomcatIPath();
		ArrayList jars = new ArrayList();
		
		if(this.getServletJarPath() != null) {
			jars.add(JavaCore.newVariableEntry(tomcatHomePath.append(this.getServletJarPath()), null, null));
		}

		if(this.getJasperJarPath() != null)	{		
			jars.add(JavaCore.newVariableEntry(tomcatHomePath.append(this.getJasperJarPath()), null, null));
		}
				
		if(this.getJSPJarPath() != null)	{		
			jars.add(JavaCore.newVariableEntry(tomcatHomePath.append(this.getJSPJarPath()), null, null));
		}
		
		return jars;
	}
	
	/**
	 * Return the tag that will be used to find where context definition should be added in server.xml
	 */
	public abstract String getXMLTagAfterContextDefinition();

	/**
	 * See %TOMCAT_HOME%/bin/startup.bat
	 */
	public void start() throws CoreException {
		this.runTomcatBootsrap(getStartCommand(), true, RUN, false);
	}
	/**
	 * See %TOMCAT_HOME%/bin/shutdown.bat
	 */
	public void stop() throws CoreException {
		this.runTomcatBootsrap(getStopCommand(), false, RUN, false);
	}

	/**
	 * Simply stop and start
	 */
	public void restart() throws CoreException {
		this.stop();

		// Hack, need more testings
		try {
			Thread.sleep(5000);
		} catch (InterruptedException ex) {
		}

		this.start();
	}

	/**
	 * Write tomcat launch configuration to .metadata/.log
	 */
	public void logConfig() throws CoreException {
		this.runTomcatBootsrap(getStartCommand(), true, LOG, false);
	}
	
	/**
	 * Create an Eclipse launch configuration
	 */
	public void addLaunch() throws CoreException {
		this.runTomcatBootsrap(getStartCommand(), true, ADD_LAUNCH, true);
	}
	
	/**
	 * Launch a new JVM running Tomcat Main class
	 * Set classpath, bootclasspath and environment variable
	 */
	private void runTomcatBootsrap(String tomcatBootOption, boolean showInDebugger, int action, boolean saveConfig) throws CoreException {
		String[] prgArgs = this.getPrgArgs(tomcatBootOption);

		IProject[] projects = TomcatLauncherPlugin.getWorkspace().getRoot().getProjects();

		for (int i = 0; i < projects.length; i++) {
			if (!projects[i].isOpen())
				continue;
			TomcatProject tomcatProject = (TomcatProject) projects[i].getNature(TomcatLauncherPlugin.NATURE_ID);
			if (tomcatProject != null) {
				ArrayList al = new ArrayList();
				ArrayList visitedProjects = new ArrayList(); /*IMC*/ 
				IJavaProject javaProject = (IJavaProject) projects[i].getNature(JavaCore.NATURE_ID);
				WebClassPathEntries entries = tomcatProject.getWebClassPathEntries();
				if (entries != null) {
					getClassPathEntries(javaProject, al, entries.getList(), visitedProjects);

					IFile file = null;
				 	if(tomcatProject.getRootDirFolder() == null) {
				 		file = projects[i].getFile(new Path(WEBAPP_CLASSPATH_FILENAME));
				 	} else {	 		
				 		file = tomcatProject.getRootDirFolder().getFile(new Path(WEBAPP_CLASSPATH_FILENAME));
				 	}
					
					File cpFile = file.getLocation().makeAbsolute().toFile();
					if (cpFile.exists()) {
						cpFile.delete();
					}
					try {
						if (cpFile.createNewFile()) {
							PrintWriter pw = new PrintWriter(new FileOutputStream(cpFile));

							for (int j = 0; j < al.size(); j++) {
								pw.println(al.get(j));
							}
							pw.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		String[] classpath = new String[0];
		classpath = addPreferenceJvmToClasspath(classpath);
		classpath = addPreferenceProjectListToClasspath(classpath);
		classpath = StringUtil.concatUniq(classpath, this.getClasspath());

		String[] vmArgs = this.getVmArgs();
		vmArgs = addPreferenceParameters(vmArgs);

		String[] bootClasspath = addPreferenceJvmToBootClasspath(new String[0]);

		StringBuffer programArguments = new StringBuffer();
		for (int i = 0; i < prgArgs.length; i++) {
			programArguments.append(" " + prgArgs[i]);
		}

		StringBuffer jvmArguments = new StringBuffer();
		for (int i = 0; i < vmArgs.length; i++) {
			jvmArguments.append(" " + vmArgs[i]);
		}

		if(action == RUN) {
			VMLauncherUtility.runVM(getLabel(), getMainClass(), classpath, bootClasspath, jvmArguments.toString(), programArguments.toString(), isDebugMode(), showInDebugger, saveConfig);
		}
		if(action == LOG) {
			VMLauncherUtility.log(getLabel(), getMainClass(), classpath, bootClasspath, jvmArguments.toString(), programArguments.toString(), isDebugMode(), showInDebugger);
		}
		if(action == ADD_LAUNCH) {
			VMLauncherUtility.createConfig(getLabel(), getMainClass(), classpath, bootClasspath, jvmArguments.toString(), programArguments.toString(), isDebugMode(), showInDebugger, true);
		}		

	}

	private void add(ArrayList data, IPath entry) {
		if (entry.isAbsolute() == false)
			entry = entry.makeAbsolute();
		String tmp = entry.toFile().toString();
		if (!data.contains(tmp)) {
			data.add(tmp);
		}
	}

	private void add(ArrayList data, IResource con) {
		if (con == null)
			return;
		add(data, con.getLocation());
	}

	private void getClassPathEntries(IJavaProject prj, ArrayList data, List selectedPaths, ArrayList visitedProjects) {
		IClasspathEntry[] entries = null;

		IPath outputPath = null;
		try {
			outputPath = prj.getOutputLocation();
			if (selectedPaths.contains(outputPath.toFile().toString().replace('\\', '/'))) {
				add(data, prj.getProject().getWorkspace().getRoot().findMember(outputPath));
			}
			entries = prj.getRawClasspath();
		} catch (JavaModelException e) {
			TomcatLauncherPlugin.log(e);
		}
		
		if (entries != null) {
			getClassPathEntries(entries, prj, data, selectedPaths, visitedProjects, outputPath);
		}
	}
	
	private void getClassPathEntries(IClasspathEntry[] entries, IJavaProject prj, ArrayList data, List selectedPaths, ArrayList visitedProjects, IPath outputPath) {		
		for (int i = 0; i < entries.length; i++) {
			IClasspathEntry entry = entries[i];
			IPath path = entry.getPath();
			if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				path = entry.getOutputLocation();
				if(path == null) continue;
			}			
			if (entry.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
				String prjName = entry.getPath().lastSegment();
				if(!visitedProjects.contains(prjName)) {
					visitedProjects.add(prjName);
					getClassPathEntries(prj.getJavaModel().getJavaProject(prjName), data, selectedPaths, visitedProjects);
				}
				continue;
			} else if (!selectedPaths.contains(path.toFile().toString().replace('\\', '/'))) {
				if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER &&
					!entry.getPath().toString().equals("org.eclipse.jdt.launching.JRE_CONTAINER")) {

					// entires in container are only processed individually
					// if container itself is not selected
					
					IClasspathContainer container;
					try {
						container = JavaCore.getClasspathContainer(path, prj);
					} catch (JavaModelException e1) {
						TomcatLauncherPlugin.log(e1);
						container = null;
					}
					
					if (container != null) {
						getClassPathEntries(
							container.getClasspathEntries(), 
							prj, data, 
							selectedPaths, visitedProjects,
							outputPath);
					}
				}
				continue;
			}

			IClasspathEntry[] tmpEntry = null;
			if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
				try {
					tmpEntry = JavaCore.getClasspathContainer(path, prj).getClasspathEntries();
				} catch (JavaModelException e1) {
					TomcatLauncherPlugin.log(e1);
					continue;
				}
			} else {
				tmpEntry = new IClasspathEntry[1];
				tmpEntry[0] = JavaCore.getResolvedClasspathEntry(entry);
			}

			for (int j = 0; j < tmpEntry.length; j++) {
				if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
					IResource res = prj.getProject().getWorkspace().getRoot().findMember(tmpEntry[j].getPath());
					if (res != null)
						add(data, res);
					else
						add(data, tmpEntry[j].getPath());
				} else if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					IPath srcPath = entry.getOutputLocation();
					if(srcPath != null && !srcPath.equals(outputPath))
					{
						add(data, prj.getProject().getWorkspace().getRoot().findMember(srcPath));
					}
				} else {
					TomcatLauncherPlugin.log(">>> " + tmpEntry[j]);
					if(tmpEntry[j].getPath() != null)
						add(data, tmpEntry[j].getPath());
				} 			
			}
		}
	}
	private boolean isDebugMode() {
		return TomcatLauncherPlugin.getDefault().isDebugMode();
	}

	protected String getTomcatDir() {
		return TomcatLauncherPlugin.getDefault().getTomcatDir();
	}

	protected String getTomcatBase() {
		return TomcatLauncherPlugin.getDefault().getTomcatBase();
	}

	private String[] addPreferenceProjectListToClasspath(String[] previouscp) {
		List projectsList = TomcatLauncherPlugin.getDefault().getProjectsInCP();
		String[] result = previouscp;
		Iterator it = projectsList.iterator();
		while (it.hasNext()) {
			try {
				ProjectListElement ple = (ProjectListElement) it.next();
				IJavaProject jproject = JavaCore.create(ple.getProject());
				result = this.addProjectToClasspath(result, jproject);
			} catch (Exception e) {
				// nothing will be added to classpath
			}
		}

		return result;

	}

	private String[] addProjectToClasspath(String[] previouscp, IJavaProject project) throws CoreException {
		//		IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
		//		IJavaProject project = JavaCore.getJavaCore().create(root.getProject(projectName));
		if ((project != null) && (project.exists() && project.isOpen())) {
			String[] projectcp = JavaRuntime.computeDefaultRuntimeClassPath(project);
			return StringUtil.concatUniq(projectcp, previouscp);
		} else {
			return previouscp;
		}
	}

	private String[] addPreferenceParameters(String[] previous) {
		String[] prefParams = StringUtil.cutString(TomcatLauncherPlugin.getDefault().getJvmParamaters(), TomcatPluginResources.PREF_PAGE_LIST_SEPARATOR);
		return StringUtil.concat(previous, prefParams);
	}

	private String[] addPreferenceJvmToClasspath(String[] previous) {
		String[] prefClasspath = StringUtil.cutString(TomcatLauncherPlugin.getDefault().getJvmClasspath(), TomcatPluginResources.PREF_PAGE_LIST_SEPARATOR);
		return StringUtil.concatUniq(previous, prefClasspath);
	}

	private String[] addPreferenceJvmToBootClasspath(String[] previous) {
		String[] prefBootClasspath = StringUtil.cutString(TomcatLauncherPlugin.getDefault().getJvmBootClasspath(), TomcatPluginResources.PREF_PAGE_LIST_SEPARATOR);
		return StringUtil.concatUniq(previous, prefBootClasspath);
	}

}
