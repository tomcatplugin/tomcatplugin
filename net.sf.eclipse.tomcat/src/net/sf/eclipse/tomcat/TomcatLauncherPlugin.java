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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import net.sf.eclipse.tomcat.editors.ProjectListElement;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The main plugin class to be used in the desktop.
 */
public class TomcatLauncherPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "net.sf.eclipse.tomcat" ;
    public static final String NATURE_ID = PLUGIN_ID + ".tomcatnature" ;

    static final String TOMCAT_PREF_HOME_KEY = "tomcatDir";
    static final String TOMCAT_PREF_BASE_KEY = "tomcatBase";
    static final String TOMCAT_PREF_CONFIGFILE_KEY = "tomcatConfigFile";
    static final String TOMCAT_PREF_VERSION_KEY = "tomcatVersion";
    static final String TOMCAT_PREF_JRE_KEY = "tomcatJRE";
    static final String TOMCAT_PREF_JVM_PARAMETERS_KEY = "jvmParameters";
    static final String TOMCAT_PREF_JVM_CLASSPATH_KEY = "jvmClasspath";
    static final String TOMCAT_PREF_JVM_BOOTCLASSPATH_KEY = "jvmBootClasspath";
    static final String TOMCAT_PREF_PROJECTSINCP_KEY = "projectsInCp";
    static final String TOMCAT_PREF_PROJECTSINSOURCEPATH_KEY = "projectsInSourcePath";
    static final String TOMCAT_PREF_COMPUTESOURCEPATH_KEY = "computeSourcePath";
    static final String TOMCAT_PREF_DEBUGMODE_KEY = "tomcatDebugMode";
    static final String TOMCAT_PREF_TARGETPERSPECTIVE = "targetPerspective";
    static final String TOMCAT_PREF_SECURITYMANAGER = "enabledSecurityManager";
    static final String TOMCAT_PREF_MANAGER_URL = "managerUrl";
    static final String TOMCAT_PREF_MANAGER_USER = "managerUser";
    static final String TOMCAT_PREF_MANAGER_PASSWORD = "managerPassword";
    static final String TOMCAT_VERSION3 = "tomcatV3";
    static final String TOMCAT_VERSION4 = "tomcatV4";
    static final String TOMCAT_VERSION41 = "tomcatV41";
    static final String TOMCAT_VERSION5 = "tomcatV5";
    static final String TOMCAT_VERSION6 = "tomcatV6";
    static final String TOMCAT_VERSION7 = "tomcatV7";
    static final String TOMCAT_VERSION8 = "tomcatV8";
    static final String TOMCAT_VERSION9 = "tomcatV9";

    static final String TOMCAT_PREF_CONFMODE_KEY = "configMode";
    static final String SERVERXML_MODE = "serverFile";
    static final String CONTEXTFILES_MODE = "contextFiles";
    static final String TOMCAT_PREF_CONTEXTSDIR_KEY = "contextsDir";

    private static final String TOMCAT_HOME_CLASSPATH_VARIABLE = "TOMCAT_HOME";


    //The shared instance.
    private static TomcatLauncherPlugin plugin;
    //Resource bundle.
    private ResourceBundle resourceBundle;

    /**
     * The constructor.
     */
    public TomcatLauncherPlugin() {
        super();
        plugin = this;
        try {
            resourceBundle= PropertyResourceBundle.getBundle("resources");
        } catch (MissingResourceException x) {
            resourceBundle = null;
        }

        this.getWorkspace().addResourceChangeListener(new TomcatProjectChangeListener(), IResourceChangeEvent.PRE_DELETE);
    }

    /**
     * Remove TOMCAT_HOME variable from Tomcat projects build path
     * (Eclipse 3 will not compile Tomcat projects without this fix)
     */
    private void fixTomcatHomeBug() {
        if(this.getPreferenceStore().getString("fixTomcatHomeBug").equals("")) {
            this.getPreferenceStore().setValue("fixTomcatHomeBug", "fixed");
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IProject[] projects = root.getProjects();

            try {
                for (IProject project : projects) {
                    if(project.hasNature(NATURE_ID)) {
                        List cp = new ArrayList(projects.length - 1);
                        IJavaProject javaProject = JavaCore.create(project);
                        IClasspathEntry[] classpath = javaProject.getRawClasspath();
                        cp.addAll(Arrays.asList(classpath));
                        for (IClasspathEntry element : classpath) {
                            if(element.getEntryKind() == IClasspathEntry.CPE_VARIABLE) {
                                if(element.getPath().equals(TomcatLauncherPlugin.getDefault().getTomcatIPath())) {
                                    cp.remove(element);
                                }
                            }
                        }
                        javaProject.setRawClasspath((IClasspathEntry[])cp.toArray(new IClasspathEntry[cp.size()]), null);
                    }
                }
            } catch (Exception e) {
                log(e);
            }
        }
    }

    /**
     * Returns the shared instance.
     */
    public static TomcatLauncherPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns the workspace instance.
     */
    public static IWorkspace getWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }

    /**
     * Returns the active shell for this plugin.
     */
    public static Shell getShell() {
        return getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
    }

    /**
     * Returns the string from the plugin's resource bundle,
     * or 'key' if not found.
     */
    public static String getResourceString(String key) {
        ResourceBundle bundle= TomcatLauncherPlugin.getDefault().getResourceBundle();
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Returns the plugin's resource bundle,
     */
    public ResourceBundle getResourceBundle() {
        try {
            resourceBundle= PropertyResourceBundle.getBundle("resources");
        } catch (MissingResourceException x) {
            resourceBundle = null;
        }
        return resourceBundle;
    }


    public String getTomcatDir() {
        return substituteVariablesForPreference(TOMCAT_PREF_HOME_KEY);
    }

    public String getTomcatBase() {
        return substituteVariablesForPreference(TOMCAT_PREF_BASE_KEY);
    }

    public String getConfigFile() {
        return substituteVariablesForPreference(TOMCAT_PREF_CONFIGFILE_KEY);
    }

    public String getConfigMode() {
        return substituteVariablesForPreference(TOMCAT_PREF_CONFMODE_KEY);
    }

    public String getContextsDir() {
        return substituteVariablesForPreference(TOMCAT_PREF_CONTEXTSDIR_KEY);
    }

    private String substituteVariablesForPreference(String preferenceKey) {
        IPreferenceStore pref =	TomcatLauncherPlugin.getDefault().getPreferenceStore();
        String preferenceRaw = pref.getString(preferenceKey);
        try {
            IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();
            String preferenceResolved = variableManager.performStringSubstitution(preferenceRaw);
            return preferenceResolved;
        } catch (CoreException e) {
            e.printStackTrace();
            return preferenceRaw;
        }
    }

    public String getTomcatVersion() {
        IPreferenceStore pref =	TomcatLauncherPlugin.getDefault().getPreferenceStore();
        String result = pref.getString(TOMCAT_PREF_VERSION_KEY);
        if (result.equals("")) {
            result = TOMCAT_VERSION4;
        }

        return result;
    }

    public String getTomcatJRE() {
        IPreferenceStore pref =	TomcatLauncherPlugin.getDefault().getPreferenceStore();
        String result = pref.getString(TOMCAT_PREF_JRE_KEY);
        if (result.equals("")) {
            result = JavaRuntime.getDefaultVMInstall().getId();
        }

        return result;
    }

    public boolean isDebugMode() {
        IPreferenceStore pref =	TomcatLauncherPlugin.getDefault().getPreferenceStore();
        return !pref.getBoolean(TOMCAT_PREF_DEBUGMODE_KEY);
    }

    public String getTargetPerspective() {
        IPreferenceStore pref =	TomcatLauncherPlugin.getDefault().getPreferenceStore();
        return pref.getString(TOMCAT_PREF_TARGETPERSPECTIVE);
    }

    public boolean isSecurityManagerEnabled() {
        IPreferenceStore pref =	TomcatLauncherPlugin.getDefault().getPreferenceStore();
        return pref.getBoolean(TOMCAT_PREF_SECURITYMANAGER);
    }

    public String getJvmParamaters() {
        IPreferenceStore pref =	TomcatLauncherPlugin.getDefault().getPreferenceStore();
        return pref.getString(TOMCAT_PREF_JVM_PARAMETERS_KEY);
    }

    public String getJvmClasspath() {
        IPreferenceStore pref =	TomcatLauncherPlugin.getDefault().getPreferenceStore();
        return pref.getString(TOMCAT_PREF_JVM_CLASSPATH_KEY);
    }

    public String getJvmBootClasspath() {
        return substituteVariablesForPreference(TOMCAT_PREF_JVM_BOOTCLASSPATH_KEY);
    }

    public TomcatBootstrap getTomcatBootstrap() {
        TomcatBootstrap tomcatBootstrap = null;

        if(getTomcatVersion().equals(TOMCAT_VERSION3)) {
            tomcatBootstrap = new Tomcat3Bootstrap("Tomcat 3.x");
        }
        if(getTomcatVersion().equals(TOMCAT_VERSION4)) {
            tomcatBootstrap = new Tomcat4Bootstrap("Tomcat 4.x");
        }
        if(getTomcatVersion().equals(TOMCAT_VERSION41)) {
            tomcatBootstrap = new Tomcat41Bootstrap("Tomcat 4.1.x");
        }
        if(getTomcatVersion().equals(TOMCAT_VERSION5)) {
            tomcatBootstrap = new Tomcat5Bootstrap("Tomcat 5.x");
        }
        if(getTomcatVersion().equals(TOMCAT_VERSION6)) {
            tomcatBootstrap = new Tomcat6Bootstrap("Tomcat 6.x");
        }
        if(getTomcatVersion().equals(TOMCAT_VERSION7)) {
            tomcatBootstrap = new Tomcat7Bootstrap("Tomcat 7.x");
        }
        if (getTomcatVersion().equals(TOMCAT_VERSION8)) {
            tomcatBootstrap = new Tomcat7Bootstrap("Tomcat 8.x");
        }
        if (getTomcatVersion().equals(TOMCAT_VERSION9)) {
        	tomcatBootstrap = new Tomcat7Bootstrap("Tomcat 9.x");
        }

        return tomcatBootstrap;
    }


    public String getManagerAppUrl() {
        IPreferenceStore pref =	TomcatLauncherPlugin.getDefault().getPreferenceStore();
        return pref.getString(TOMCAT_PREF_MANAGER_URL);
    }

    public String getManagerAppUser() {
        IPreferenceStore pref =	TomcatLauncherPlugin.getDefault().getPreferenceStore();
        return pref.getString(TOMCAT_PREF_MANAGER_USER);
    }

    public String getManagerAppPassword() {
        IPreferenceStore pref =	TomcatLauncherPlugin.getDefault().getPreferenceStore();
        return pref.getString(TOMCAT_PREF_MANAGER_PASSWORD);
    }

    static public void log(String msg) {
        ILog log = TomcatLauncherPlugin.getDefault().getLog();
        Status status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, msg + "\n", null);
        log.log(status);
    }

    static public void log(Exception ex) {
        ILog log = TomcatLauncherPlugin.getDefault().getLog();
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        String msg = stringWriter.getBuffer().toString();

        Status status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, msg, null);
        log.log(status);
    }


    public IPath getTomcatIPath() {
        IPath tomcatPath = getTomcatClasspathVariable();
        if(tomcatPath == null) {
            return new Path(TomcatLauncherPlugin.getDefault().getTomcatDir());
        } else {
            return new Path(TOMCAT_HOME_CLASSPATH_VARIABLE);
        }
    }

    private IPath getTomcatClasspathVariable() {
        IPath tomcatPath = JavaCore.getClasspathVariable(TOMCAT_HOME_CLASSPATH_VARIABLE);
        if(tomcatPath == null) {
            this.initTomcatClasspathVariable();
            tomcatPath = JavaCore.getClasspathVariable(TOMCAT_HOME_CLASSPATH_VARIABLE);
        }
        return tomcatPath;
    }

    public void initTomcatClasspathVariable() {
        try {
            JavaCore.setClasspathVariable(
                                          TOMCAT_HOME_CLASSPATH_VARIABLE,
                                          new Path(TomcatLauncherPlugin.getDefault().getTomcatDir()),
                                          null);
        } catch (JavaModelException e) {
            log(e);
        }
    }



    public void setProjectsInCP(List projectsInCP) {
        this.saveProjectsToPreferenceStore(projectsInCP, TOMCAT_PREF_PROJECTSINCP_KEY);
    }

    public List getProjectsInCP() {
        return this.readProjectsFromPreferenceStore(TOMCAT_PREF_PROJECTSINCP_KEY);
    }

    public void setProjectsInSourcePath(List projectsInCP) {
        this.saveProjectsToPreferenceStore(projectsInCP, TOMCAT_PREF_PROJECTSINSOURCEPATH_KEY);
    }

    public List getProjectsInSourcePath() {
        IPreferenceStore pref =	TomcatLauncherPlugin.getDefault().getPreferenceStore();
        boolean automaticallyComputed =  pref.getBoolean(TOMCAT_PREF_COMPUTESOURCEPATH_KEY);

        if(automaticallyComputed) {
            return computeProjectsInSourcePath();
        } else {
            return readProjectsInSourcePathFromPref();
        }
    }

    public List readProjectsInSourcePathFromPref() {
        IPreferenceStore pref =	TomcatLauncherPlugin.getDefault().getPreferenceStore();
        if(!(pref.contains(TOMCAT_PREF_PROJECTSINSOURCEPATH_KEY))) {
            // Compute source path for a new workspace
            pref.setValue(TOMCAT_PREF_COMPUTESOURCEPATH_KEY, true);
            return computeProjectsInSourcePath();
        } else {
            return TomcatLauncherPlugin.readProjectsFromPreferenceStore(TOMCAT_PREF_PROJECTSINSOURCEPATH_KEY);
        }
    }

    private List computeProjectsInSourcePath() {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject[] allProjects = root.getProjects();

        // Since version 3.2 final, default source path contains all opened Java projects
        // Previously we add Tomcat projects and their required Java projects to source path
        // For beginner this should make thing easier.

        ArrayList tempList = new ArrayList(allProjects.length);

        ArrayList alreadyAdded = new ArrayList();

        for (IProject allProject : allProjects) {
            IProject project = allProject;
            try {
                if((project.isOpen()) && project.hasNature(JavaCore.NATURE_ID)) {
                	IJavaProject javaProject = JavaCore.create(project);
                    if(!alreadyAdded.contains(project))
                    {
                        tempList.add(new ProjectListElement(javaProject.getProject()));
                        alreadyAdded.add(project);
                    }

                }
            } catch (CoreException e) {
                TomcatLauncherPlugin.log(e);
            }
        }
        return tempList;

    }

    static void saveProjectsToPreferenceStore(List projectList, String keyInPreferenceStore) {
        IPreferenceStore pref =	TomcatLauncherPlugin.getDefault().getPreferenceStore();
        StringBuffer buf = new StringBuffer();
        Iterator it = projectList.iterator();
        while(it.hasNext()) {
            ProjectListElement each = (ProjectListElement)it.next();
            buf.append(each.getID());
            buf.append(';');
        }
        pref.setValue(keyInPreferenceStore, buf.toString());
    }

    static List<IProject> readProjectsFromPreferenceStore(String keyInPreferenceStore) {
        IPreferenceStore pref =	TomcatLauncherPlugin.getDefault().getPreferenceStore();
        List<String> projectsIdList = Arrays.asList(StringUtil.cutString(pref.getString(keyInPreferenceStore), TomcatPluginResources.PREF_PAGE_LIST_SEPARATOR));
        return ProjectListElement.stringsToProjectsList(projectsIdList);

    }

    static public boolean checkTomcatSettingsAndWarn() {
        if(!isTomcatConfigured()) {
            String msg = TomcatLauncherPlugin.getResourceString("msg.noconfiguration");
            MessageDialog.openWarning(TomcatLauncherPlugin.getShell(),"Tomcat", msg);
            return false;
        }
        return true;
    }

    static public boolean isTomcatConfigured() {
        return !(TomcatLauncherPlugin.getDefault().getTomcatDir().equals(""));
    }


    @Override
    public void startup() throws CoreException {
        super.startup();
        this.fixTomcatHomeBug();
    }

}

