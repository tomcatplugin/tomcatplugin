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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariable;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.google.common.base.Joiner;

import net.sf.eclipse.tomcat.editors.TomcatDirectoryFieldEditor;
import net.sf.eclipse.tomcat.editors.TomcatFileFieldEditor;


public class TomcatPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, TomcatPluginResources {


    private RadioGroupFieldEditor version;
    private DirectoryFieldEditor home;
    private TomcatDirectoryFieldEditor contextsDir;
    private TomcatFileFieldEditor configFile;
    private RadioGroupFieldEditor configMode;
    private String selectedVersion;
    private String oldVersion;

    public TomcatPreferencePage() {
        super();
        setPreferenceStore(TomcatLauncherPlugin.getDefault().getPreferenceStore());
    }

    @Override
    protected Control createContents(Composite parent) {
        final Composite composite = parent;

        oldVersion = TomcatLauncherPlugin.getDefault().getTomcatVersion();
        version = new RadioGroupFieldEditor(
                                            TomcatLauncherPlugin.TOMCAT_PREF_VERSION_KEY,
                                            PREF_PAGE_CHOOSEVERSION_LABEL,
                                            1,
                                            new String[][] {
                                                            {PREF_PAGE_VERSION3_LABEL, TomcatLauncherPlugin.TOMCAT_VERSION3},
                                                            {PREF_PAGE_VERSION4_LABEL, TomcatLauncherPlugin.TOMCAT_VERSION4},
                                                            {PREF_PAGE_VERSION4_1_LABEL, TomcatLauncherPlugin.TOMCAT_VERSION41},
                                                            {PREF_PAGE_VERSION5_LABEL, TomcatLauncherPlugin.TOMCAT_VERSION5},
                                                            {PREF_PAGE_VERSION6_LABEL, TomcatLauncherPlugin.TOMCAT_VERSION6},
                                                            {PREF_PAGE_VERSION7_LABEL, TomcatLauncherPlugin.TOMCAT_VERSION7 },
                                                            {PREF_PAGE_VERSION8_LABEL, TomcatLauncherPlugin.TOMCAT_VERSION8 },
                                                            {PREF_PAGE_VERSION9_LABEL, TomcatLauncherPlugin.TOMCAT_VERSION9 }
                                            },
                                            composite,
                                            true);


        Group homeGroup = new Group(composite,SWT.NONE);
        home = new VariableAwareDirectoryFieldEditor(
                                                     TomcatLauncherPlugin.TOMCAT_PREF_HOME_KEY,
                                                     PREF_PAGE_HOME_LABEL,
                                                     homeGroup);


        IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();
        IStringVariable[] variables = variableManager.getVariables();
        Collection<String> variablesToDisplay = new ArrayList<String>();
        for (IStringVariable variable : variables) {
            String variableName = variable.getName();
            // use a heuristic to pick interesting variables
            if (variableName.contains("_loc") || variableName.contains("_path") || variableName.contains("_location")) {
                variablesToDisplay.add(variableName + " - " + variable.getDescription());
            }
        }

        Group listOfVariablesGroup = new Group(composite, SWT.NONE);
        listOfVariablesGroup.setText("Variables to use for 'Tomcat Home'");
        Label listLabel = new Label(listOfVariablesGroup, SWT.NULL);
        listLabel.setText(Joiner.on("\n").join(variablesToDisplay));
        listLabel.setToolTipText("List of available variables (Usage: ${VARIABLE_NAME})");

        Group modeGroup = new Group(composite,SWT.NONE);
        modeGroup.setLayout(new GridLayout(1, false));

        Composite configGroup = new Composite(modeGroup, SWT.NULL);
        configMode = new RadioGroupFieldEditor(
                                               TomcatLauncherPlugin.TOMCAT_PREF_CONFMODE_KEY,
                                               PREF_PAGE_CHOOSECONFMODE_LABEL,
                                               1,
                                               new String[][] {
                                                               {PREF_PAGE_SERVERXML_LABEL, TomcatLauncherPlugin.SERVERXML_MODE},
                                                               {PREF_PAGE_CONTEXTFILES_LABEL, TomcatLauncherPlugin.CONTEXTFILES_MODE},
                                               },
                                               configGroup,
                                               false);

        new Label(composite, SWT.NULL); //blank

        final Composite configLocationGroup = new Composite(modeGroup, SWT.NULL);
        configFile = new TomcatFileFieldEditor(
                                               TomcatLauncherPlugin.TOMCAT_PREF_CONFIGFILE_KEY,
                                               PREF_PAGE_CONFIGFILE_LABEL,
                                               configLocationGroup);

        contextsDir = new TomcatDirectoryFieldEditor(
                                                     TomcatLauncherPlugin.TOMCAT_PREF_CONTEXTSDIR_KEY,
                                                     PREF_PAGE_CONTEXTSDIR_LABEL,
                                                     configLocationGroup);

        home.setPropertyChangeListener(new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if(event.getProperty().equals(FieldEditor.VALUE)) {
                    computeConfigFile();
                    computeContextsDir();
                }
            }
        });

        new Label(composite, SWT.NULL); //blank

        initLayoutAndData(homeGroup, 3);
        initLayoutAndData(modeGroup, 1);
        initLayoutAndData(listOfVariablesGroup, 1);
        initLayoutAndData(configLocationGroup, 3);

        this.initField(version);
        version.setPropertyChangeListener(new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if(event.getProperty().equals(FieldEditor.VALUE)) {
                    String value = (String)event.getNewValue();
                    versionChanged(value);
                }
            }
        });

        this.initField(home);
        this.initField(configMode);
        modeChanged(configLocationGroup, getPreferenceStore().getString(TomcatLauncherPlugin.TOMCAT_PREF_CONFMODE_KEY));

        configMode.setPropertyChangeListener(new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if(event.getProperty().equals(FieldEditor.VALUE)) {
                    String value = (String)event.getNewValue();
                    modeChanged(configLocationGroup, value);
                }
            }
        });

        this.initField(configFile);
        if(configFile.getStringValue().length() == 0) {
            computeConfigFile();
        }
        this.initField(contextsDir);
        if(contextsDir.getStringValue().length() == 0) {
            computeContextsDir();
        }

        return parent;
    }

    public void init(IWorkbench workbench) {
        // empty implementation
    }


    @Override
    public boolean performOk() {
        version.store();
        home.store();
        configFile.store();
        configMode.store();
        contextsDir.store();
        TomcatLauncherPlugin.getDefault().initTomcatClasspathVariable();
        TomcatLauncherPlugin.getDefault().savePluginPreferences();

        if(!oldVersion.equals(TomcatLauncherPlugin.getDefault().getTomcatVersion())) {
            this.updateTomcatProjectsBuildPath();
        }
        return true;
    }

    private void updateTomcatProjectsBuildPath() {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject[] projects = root.getProjects();

        try {
            for (IProject project : projects) {
                if(project.hasNature(TomcatLauncherPlugin.NATURE_ID)) {
                    TomcatProject.create(project).addTomcatJarToProjectClasspath();
                }
            }
        } catch (CoreException e) {
            // ignore update if there is an exception
        }

    }

    private void initField(FieldEditor field) {
        field.setPreferenceStore(getPreferenceStore());
        field.setPreferencePage(this);
        field.load();
    }

    private void computeConfigFile() {
        configFile.setStringValue(home.getStringValue() + File.separator + "conf" + File.separator + "server.xml");
    }

    private void computeContextsDir() {
        if(selectedVersion == null) {
            selectedVersion = TomcatLauncherPlugin.getDefault().getPreferenceStore().getString(TomcatLauncherPlugin.TOMCAT_PREF_VERSION_KEY);
        }

        if(selectedVersion.equals(TomcatLauncherPlugin.TOMCAT_VERSION3) ||
                        selectedVersion.equals(TomcatLauncherPlugin.TOMCAT_VERSION4) ||
                        selectedVersion.equals(TomcatLauncherPlugin.TOMCAT_VERSION41)) {
            contextsDir.setStringValue(home.getStringValue() + File.separator + "webapps");
        } else {
            String contextDirName = home.getStringValue() + File.separator + "conf" + File.separator + "Catalina" + File.separator + "localhost";
            checkOrCreateDefaultContextDir();
            contextsDir.setStringValue(contextDirName);
        }
    }

    /**
     * Since Tomcat 6, conf/Catalina/host does not exist after installation
     */
    private void checkOrCreateDefaultContextDir() {
        String confDirName = home.getStringValue() + File.separator + "conf";
        File confDir = new File(confDirName);
        if(confDir.exists()) {
            String catalinaDirName = confDirName + File.separator + "Catalina";
            File catalinaDir = new File(catalinaDirName);
            catalinaDir.mkdir();
            if(catalinaDir.exists()) {
                String localhostDirName = catalinaDirName + File.separator + "localhost";
                File localhostDir = new File(localhostDirName);
                localhostDir.mkdir();
            }
        }
    }

    private void modeChanged(final Composite configLocationGroup, String value) {
        if(value.equals(TomcatLauncherPlugin.SERVERXML_MODE)) {
            contextsDir.setEnabled(false, configLocationGroup);
            configFile.setEnabled(true, configLocationGroup);
        } else {
            contextsDir.setEnabled(true, configLocationGroup);
            configFile.setEnabled(false, configLocationGroup);
        }

        // Refresh error message
        configFile.valueChanged();
        contextsDir.valueChanged();
    }

    private void versionChanged(String value) {
        selectedVersion = value;
        computeContextsDir();
    }

    private void initLayoutAndData(Composite aGroup, int numColumns) {
        GridLayout gl = new GridLayout(numColumns, false);
        aGroup.setLayout(gl);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 1;
        gd.widthHint = 400;
        aGroup.setLayoutData(gd);
    }

}

