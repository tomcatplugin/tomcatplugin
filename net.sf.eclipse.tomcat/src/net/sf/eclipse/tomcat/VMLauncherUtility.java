/* The MIT License
 * (c) Copyright Sysdeo SA 2001-2002
 * (c) Copyright Eclipse Tomcat Plugin 2014-2016
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or 
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.sf.eclipse.tomcat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputer;
import org.eclipse.debug.core.sourcelookup.containers.DefaultSourceContainer;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.launching.JavaSourceLookupDirector;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaProjectSourceContainer;
import org.eclipse.jdt.launching.sourcelookup.containers.PackageFragmentRootSourceContainer;

import net.sf.eclipse.tomcat.editors.ProjectListElement;

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

	/** @return {@code true} if java version is 1.8 or less */
	static public boolean isJavaVersion18OrLess() {
		Integer javaMajorVersion = getJavaMajorVersion();
		return javaMajorVersion != null && javaMajorVersion.intValue() == 1;
	}

	/** @return {@code true} if java version is 9 or greater */
	static public boolean isJavaVersion9OrGreater() {
		Integer javaMajorVersion = getJavaMajorVersion();
		return javaMajorVersion != null && javaMajorVersion.intValue() >= 9;
	}

	static private final Pattern JAVA_MAJOR_VERSION_PATTERN = Pattern.compile("([1-9][0-9]*).*"); 
	static private Integer getJavaMajorVersion() {
		IVMInstall vmInstall = VMLauncherUtility.getVMInstall();
		if (vmInstall instanceof IVMInstall2) {
			IVMInstall2 vmInstall2 = (IVMInstall2)vmInstall;
			String javaVersion = vmInstall2.getJavaVersion();
			if (javaVersion != null) {
				Matcher matcher = JAVA_MAJOR_VERSION_PATTERN.matcher(javaVersion);
				if (matcher.matches()) {
					return Integer.valueOf(matcher.group(1));
				}
			}
		}
		return null;
	}

	/**
	 * Start Tomcat.
	 * @param label something like "Tomcat 8.x"
	 * @param classToLaunch Tomcat's Bootstrap class
	 * @param classpath What will become Tomcat's system class path
	 * @param bootClasspath What will become Tomcat's bootstrap class path
	 * @param vmArgs
	 * @param prgArgs
	 * @param debug
	 * @param showInDebugger
	 * @param saveConfig
	 * @throws CoreException
	 * 
	 * @see "https://tomcat.apache.org/tomcat-8.0-doc/class-loader-howto.html"
	 * @see "https://help.eclipse.org/luna/index.jsp?topic=%2Forg.eclipse.jdt.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fjdt%2Flaunching%2FJavaRuntime.html"
	 */
	static public void runVM(String label, String classToLaunch, String[] classpath, String[] bootClasspath, String vmArgs, String prgArgs, boolean debug, boolean showInDebugger, boolean saveConfig)
		throws CoreException {

		String mode = "";
		if (debug)
			mode = ILaunchManager.DEBUG_MODE;
		else
			mode = ILaunchManager.RUN_MODE;

		ILaunchConfigurationWorkingCopy config = createConfig(label, classToLaunch, classpath, bootClasspath, vmArgs, prgArgs, debug, showInDebugger, saveConfig);
		ILAUNCH = config.launch(mode, null);
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
			getSourceLocator(true);
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
		
		ISourceLookupDirector locator = (ISourceLookupDirector) getSourceLocator(false);
		config.setAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_MEMENTO, locator.getMemento());
		
		ArrayList classpathMementos = new ArrayList();
		for (int i = 0; i < classpath.length; i++) {
			IRuntimeClasspathEntry cpEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(classpath[i]));
			cpEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			classpathMementos.add(cpEntry.getMemento());
		}
		
		if (bootClasspath.length == 0) {
			IPath path = new Path(JavaRuntime.JRE_CONTAINER);

			try {
				IClasspathEntry cpEntry = JavaCore.newContainerEntry(path);
				// from org.eclipse.jdt.internal.debug.ui.actions.AddLibraryAction run()
				IRuntimeClasspathEntry rcpEntry = JavaRuntime.newRuntimeContainerClasspathEntry(cpEntry.getPath(), IRuntimeClasspathEntry.STANDARD_CLASSES);
				classpathMementos.add(rcpEntry.getMemento());
			} catch (CoreException ex) {
				TomcatLauncherPlugin.log(ex);
			}

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
			getSourceLocator(false);
			config.doSave();
		} 

		return config;
	}

	private static ISourceLocator getSourceLocator(boolean trace) throws CoreException {
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
