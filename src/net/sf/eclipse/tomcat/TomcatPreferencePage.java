package net.sf.eclipse.tomcat;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */

import java.io.File;

import net.sf.eclipse.tomcat.editors.TomcatDirectoryFieldEditor;
import net.sf.eclipse.tomcat.editors.TomcatFileFieldEditor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class TomcatPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, TomcatPluginResources {

	private BooleanFieldEditor securityEditor;

	static final private int FIELD_WIDTH = 50;
	private RadioGroupFieldEditor version;
	private DirectoryFieldEditor home;
	private TomcatDirectoryFieldEditor contextsDir;	
	private TomcatFileFieldEditor configFile;
	private RadioGroupFieldEditor configMode;
	private ModifyListener fModifyListener;
	private String selectedVersion;
	private String oldVersion;
		
	public TomcatPreferencePage() {
		super();
		setPreferenceStore(TomcatLauncherPlugin.getDefault().getPreferenceStore());
	}

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		final Composite composite = parent;
//		final Composite composite = new Composite(parent, SWT.NONE);
//		composite.setLayout(new GridLayout(2, false));

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
					{PREF_PAGE_VERSION7_LABEL, TomcatLauncherPlugin.TOMCAT_VERSION7}
				},
				composite,
				true);

		
		Group homeGroup = new Group(composite,SWT.NONE);
		home = new DirectoryFieldEditor(
				TomcatLauncherPlugin.TOMCAT_PREF_HOME_KEY,
				PREF_PAGE_HOME_LABEL,
				homeGroup);

		new Label(composite, SWT.NULL); //blank

		Group modeGroup = new Group(composite,SWT.NONE);
		modeGroup.setLayout(new GridLayout(1, false));
		
		Composite configGroup = new Composite(modeGroup, SWT.NULL);
//		getPreferenceStore().setDefault(TomcatLauncherPlugin.TOMCAT_PREF_CONFMODE_KEY, TomcatLauncherPlugin.CONTEXTFILES_MODE);		
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
		initLayoutAndData(configLocationGroup, 3);
		
		this.initField(version);
		version.setPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if(event.getProperty().equals(FieldEditor.VALUE)) {
					String value = (String)event.getNewValue();
					versionChanged(composite, value);		
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

	/*
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}


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
			for (int i = 0; i < projects.length; i++) {
				if(projects[i].hasNature(TomcatLauncherPlugin.NATURE_ID)) {
					TomcatProject.create(projects[i]).addTomcatJarToProjectClasspath();
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
	
	private void versionChanged(final Composite composite, String value) {
		selectedVersion = value;
		computeContextsDir();
	}
		
	private void initLayoutAndData(Composite aGroup, int spanH, int spanV, int numColumns) {
		GridLayout gl = new GridLayout(numColumns, false);
		aGroup.setLayout(gl);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = spanH;
		gd.verticalSpan = spanV;
		aGroup.setLayoutData(gd);
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

