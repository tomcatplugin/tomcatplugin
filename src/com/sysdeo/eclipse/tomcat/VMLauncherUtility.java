package com.sysdeo.eclipse.tomcat;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputer;
import org.eclipse.debug.core.sourcelookup.containers.DefaultSourceContainer;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.classpath.ClasspathEntry;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jdt.internal.launching.JavaSourceLookupDirector;
import org.eclipse.jdt.internal.launching.RuntimeClasspathEntry;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.jdt.launching.sourcelookup.containers.ClasspathContainerSourceContainer;
import org.eclipse.jdt.launching.sourcelookup.containers.ClasspathVariableSourceContainer;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaProjectSourceContainer;
import org.eclipse.jdt.launching.sourcelookup.containers.PackageFragmentRootSourceContainer;

import com.sysdeo.eclipse.tomcat.editors.ProjectListElement;

/**
 * Utility class for launching a JVM in Eclipse and registering it to debugger
 * 
 * It might exist better way to implements those operations,
 * or they might already exist in other form JDT
 */

public class VMLauncherUtility {

	static public ILaunch ILAUNCH = null;
	
	static public IVMInstall getVMInstall() {
		IVMInstallType[] vmTypes = JavaRuntime.getVMInstallTypes();
		for (int i = 0; i < vmTypes.length; i++) {
			IVMInstall[] vms = vmTypes[i].getVMInstalls();
			for (int j = 0; j < vms.length; j++) {
				if (vms[j].getId().equals(TomcatLauncherPlugin.getDefault().getTomcatJRE())) {
					return vms[j];
				}
			}
		}
		return JavaRuntime.getDefaultVMInstall();
	}


	static public void runVM(String label, String classToLaunch, String[] classpath, String[] bootClasspath, String vmArgs, String prgArgs, boolean debug, boolean showInDebugger, boolean saveConfig)
		throws CoreException {

//		IVMInstall vmInstall = getVMInstall();
		String mode = "";
		if (debug)
			mode = ILaunchManager.DEBUG_MODE;
		else
			mode = ILaunchManager.RUN_MODE;

//		IVMRunner vmRunner = vmInstall.getVMRunner(mode);
		ILaunchConfigurationWorkingCopy config = createConfig(label, classToLaunch, classpath, bootClasspath, vmArgs, prgArgs, debug, showInDebugger, saveConfig);
		ILAUNCH = config.launch(mode, null);

//		ISourceLocator sourceLocator = getSourceLocator(config, false);
//		
//		//		Launch launch = createLaunch(label, classToLaunch, classpath, bootClasspath, vmArgs, prgArgs, sourceLocator, debug, showInDebugger, false);
//		Launch launch = new Launch(config, mode, sourceLocator);
//
//		
//		if (vmRunner != null) {
//			VMRunnerConfiguration vmConfig = new VMRunnerConfiguration(classToLaunch, classpath);
//			ExecutionArguments executionArguments = new ExecutionArguments(vmArgs, prgArgs);
//			vmConfig.setVMArguments(executionArguments.getVMArgumentsArray());
//			vmConfig.setProgramArguments(executionArguments.getProgramArgumentsArray());
//
//			if (bootClasspath.length == 0) {
//				vmConfig.setBootClassPath(null); // use default bootclasspath	
//			} else {
//				vmConfig.setBootClassPath(bootClasspath);
//			}
//
//			vmRunner.run(vmConfig, launch, null);
//		}
//
//		// Show in debugger
//		if (showInDebugger) {
//			DebugPlugin.getDefault().getLaunchManager().addLaunch(launch);
//		}

	}

	static public void log(String label, String classToLaunch, String[] classpath, String[] bootClasspath, String vmArgs, String prgArgs, boolean debug, boolean showInDebugger) {
		StringBuffer trace = new StringBuffer("\n-------- Sysdeo Tomcat Launcher settings --------");
		trace.append("\n-> Label : " + label);
		trace.append("\n-> ClassToLaunch : " + classToLaunch);
		trace.append("\n-> Classpath : ");
		for (int i = 0; i < classpath.length; i++) {
			trace.append(" | " + classpath[i] + " | ");
		}
		trace.append("\n-> BootClasspath : ");
		for (int i = 0; i < bootClasspath.length; i++) {
			trace.append(" | " + bootClasspath[i] + " | ");
		}
		trace.append("\n-> Vmargs : " + vmArgs);
		trace.append("\n-> PrgArgs : " + prgArgs);
		trace.append("\n-> Debug : " + debug);
		trace.append("\n-> Source lookup : \n");
		TomcatLauncherPlugin.log(trace.toString());		

		try {
			ILaunchConfigurationWorkingCopy config = createConfig(label, classToLaunch, classpath, bootClasspath, vmArgs, prgArgs, debug, showInDebugger, false);
			getSourceLocator(config, true);
		} catch (CoreException e) {
			TomcatLauncherPlugin.log("getSourceLocator failed");
		}
		
		TomcatLauncherPlugin.log("\n-------- Sysdeo Tomcat Launcher settings--------");
	}


	static public ILaunchConfigurationWorkingCopy createConfig(String label, String classToLaunch, String[] classpath, String[] bootClasspath, String vmArgs, String prgArgs, boolean debug, boolean showInDebugger, boolean saveConfig) throws CoreException {
		IVMInstall vmInstall = getVMInstall();

		ILaunchConfigurationType launchType = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType("org.eclipse.jdt.launching.localJavaApplication");
		ILaunchConfigurationWorkingCopy config = launchType.newInstance(null, label);
		config.setAttribute(IDebugUIConstants.ATTR_PRIVATE, !saveConfig);
		config.setAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_ID, "org.eclipse.jdt.launching.sourceLocator.JavaSourceLookupDirector");
		
		ISourceLookupDirector locator = (ISourceLookupDirector) getSourceLocator(config, false);
		config.setAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_MEMENTO, locator.getMemento());
		
		ArrayList classpathMementos = new ArrayList();
		for (int i = 0; i < classpath.length; i++) {
			IRuntimeClasspathEntry cpEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(classpath[i]));
			cpEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			classpathMementos.add(cpEntry.getMemento());
		}
		
		if (bootClasspath.length == 0) {
			IPath path = new Path(JavaRuntime.JRE_CONTAINER);
			// Not needed
//			if (!fJREBlock.isDefaultJRE()) {
//				IVMInstall vm = fJREBlock.getJRE();
//				if (vm != null) {
//					path = path.append(vm.getVMInstallType().getId());
//					path = path.append(vm.getName());
//				}
//			}

			try {
				IClasspathEntry cpEntry = JavaCore.newContainerEntry(path);
				// from org.eclipse.jdt.internal.debug.ui.actions.AddLibraryAction run()
				IRuntimeClasspathEntry rcpEntry = JavaRuntime.newRuntimeContainerClasspathEntry(cpEntry.getPath(), IRuntimeClasspathEntry.STANDARD_CLASSES);
				classpathMementos.add(rcpEntry.getMemento());
			} catch (CoreException ex) {
				TomcatLauncherPlugin.log(ex);
			}

	

// old			
//			LibraryLocation[] librairies = vmInstall.getVMInstallType().getDefaultLibraryLocations(vmInstall.getInstallLocation());
//			for (int i = 0; i < librairies.length; i++) {
//				IRuntimeClasspathEntry cpEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(librairies[i].getSystemLibraryPath());
//				cpEntry.setClasspathProperty(IRuntimeClasspathEntry.BOOTSTRAP_CLASSES);
//				classpathMementos.add(cpEntry.getMemento());
//			}
		} else {
			for (int i = 0; i < bootClasspath.length; i++) {
				IRuntimeClasspathEntry cpEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(bootClasspath[i]));
				cpEntry.setClasspathProperty(IRuntimeClasspathEntry.BOOTSTRAP_CLASSES);
				classpathMementos.add(cpEntry.getMemento());
			}
		}

		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE, vmInstall.getVMInstallType().getId());
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME, vmInstall.getName());
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classpathMementos);
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, prgArgs);
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArgs);
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, classToLaunch);

		
		String catalinaBase = TomcatLauncherPlugin.getDefault().getTomcatBase();
		if(catalinaBase.length() == 0) {
			catalinaBase = TomcatLauncherPlugin.getDefault().getTomcatDir();
		}
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, catalinaBase);

		if(saveConfig) {
			getSourceLocator(config, false);
			config.doSave();
		} 

		return config;
	}

	private static ISourceLocator getSourceLocator(ILaunchConfiguration configuration, boolean trace) throws CoreException {
		ArrayList tempList = new ArrayList();
		StringBuffer traceBuffer = new StringBuffer();

		traceBuffer.append("Projects in source path :\n");
		List projects = TomcatLauncherPlugin.getDefault().getProjectsInSourcePath();
		for (Iterator iter = projects.iterator(); iter.hasNext();) {
			IProject project = ((ProjectListElement) iter.next()).getProject();
			traceBuffer.append("Project " + project.getName());
			if ((project.isOpen()) && project.hasNature(JavaCore.NATURE_ID)) {
				tempList.add(project.getNature(JavaCore.NATURE_ID));
				traceBuffer.append(" added to tempList\n");
			}
		}

		ISourceLookupDirector sourceLocator = null;
		
		sourceLocator = new JavaSourceLookupDirector();
		ISourcePathComputer computer = DebugPlugin.getDefault().getLaunchManager().getSourcePathComputer("org.eclipse.jdt.launching.sourceLookup.javaSourcePathComputer");
		sourceLocator.setSourcePathComputer(computer); //$NON-NLS-1$
		
		ArrayList sourceContainers = new ArrayList();

		if (!tempList.isEmpty()) {
			IJavaProject[] javaProjects = (IJavaProject[]) tempList.toArray(new IJavaProject[1]);
				//		sourceLocator = new JavaSourceLocator(javaProjects, true);

			
			// Eclipse stops looking for source if it finds a jar containing the source code
			// despite this jar as no attached source (the user will have to use 'Attach source' button).
			// So we have to enforce that sources in project are searched before jar files,
			// To do so we add source containers in this orders :
			// - First project source containers.
			// - second packageFragmentRoot container (jar files in projects build path will be added to source path)
			// - third DefaultSourceContainer (jar files added to classpath will be added to source path)

	
			// First add all projects source containers
			for (int i = 0; i < javaProjects.length; i++) {
				IJavaProject project = javaProjects[i];
				traceBuffer.append("  -> Add JavaProjectSourceContainer for " + project.getProject().getName() + "\n");
				sourceContainers.add(new JavaProjectSourceContainer(project));
			}

			// Adding packageFragmentRoot source containers, so classes in jar files associated to a project will be seen 
			HashSet external = new HashSet();

			for (int i = 0; i < javaProjects.length; i++) {
				IJavaProject project = javaProjects[i];
				traceBuffer.append("  -> Compute SourceContainers for " + project.getProject().getName() + " :\n");

				IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();
				for (int ri = 0; ri < roots.length; ri++) {
					IPackageFragmentRoot root = roots[ri];					
					if (root.isExternal()) {
						IPath location = root.getPath();
						if (external.contains(location)) {
							continue;
						}
						external.add(location);
					}
					sourceContainers.add(new PackageFragmentRootSourceContainer(root));
					traceBuffer.append("     RootSourceContainer created for : " + root.getPath().toPortableString() + "\n");
				}
			}			
		}
		
		// Last add DefaultSourceContainer, classes in jar files added to classpath will be visible
		sourceContainers.add(new DefaultSourceContainer());

		sourceLocator.setSourceContainers((ISourceContainer[])sourceContainers.toArray(new ISourceContainer[sourceContainers.size()]));
		sourceLocator.initializeParticipants();
			

		if(trace) TomcatLauncherPlugin.log(traceBuffer.toString());	
		return sourceLocator;
	}
	
}
