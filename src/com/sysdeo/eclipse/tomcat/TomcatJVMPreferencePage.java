package com.sysdeo.eclipse.tomcat;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */

import java.util.ArrayList;

import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.sysdeo.eclipse.tomcat.editors.ClasspathFieldEditor;
import com.sysdeo.eclipse.tomcat.editors.ComboFieldEditor;
import com.sysdeo.eclipse.tomcat.editors.ListFieldEditor;

public class TomcatJVMPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, TomcatPluginResources {

	static final private int FIELD_WIDTH = 50;
	private ComboFieldEditor jvmChoice;
	private ListFieldEditor jvmParamaters;
	private ClasspathFieldEditor jvmClasspath;
	private ClasspathFieldEditor jvmBootClasspath;
	private BooleanFieldEditor debugModeEditor;

	public TomcatJVMPreferencePage() {
		super();
		setPreferenceStore(TomcatLauncherPlugin.getDefault().getPreferenceStore());
		//		setDescription("");
	}

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		final ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
		Composite composite = new Composite(scrolledComposite, SWT.NULL);
		scrolledComposite.setContent(composite);
		composite.setLayout(new GridLayout(2, false));

		// Collect all JREs
		ArrayList allVMs = new ArrayList();
		IVMInstallType[] vmTypes = JavaRuntime.getVMInstallTypes();
		for (int i = 0; i < vmTypes.length; i++) {
			IVMInstall[] vms = vmTypes[i].getVMInstalls();
			for (int j = 0; j < vms.length; j++) {
				allVMs.add(vms[j]);
			}
		}

		String[][] namesAndValues = new String[allVMs.size()][2];
		for (int i = 0; i < allVMs.size(); i++) {
			namesAndValues[i][0] = ((IVMInstall) allVMs.get(i)).getName();
			namesAndValues[i][1] = ((IVMInstall) allVMs.get(i)).getId();
		}

		jvmChoice = new ComboFieldEditor(TomcatLauncherPlugin.TOMCAT_PREF_JRE_KEY, PREF_PAGE_JRE_LABEL, namesAndValues, composite);

		debugModeEditor = new BooleanFieldEditor(TomcatLauncherPlugin.TOMCAT_PREF_DEBUGMODE_KEY, PREF_PAGE_DEBUGMODE_LABEL, composite);
		this.initField(debugModeEditor);

		new Label(composite, SWT.NULL);
		Composite group = new Composite(composite, SWT.NULL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		group.setLayoutData(gd);
		group.setLayout(new GridLayout(2, false));
		Button btAddLaunch = new Button(group, SWT.PUSH);
		btAddLaunch.setText(PREF_PAGE_CREATE_LAUNCH_LABEL);
		btAddLaunch.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					TomcatLauncherPlugin.getDefault().getTomcatBootstrap().addLaunch();
				} catch (Exception ex) {
					TomcatLauncherPlugin.log("Failed to create launch configuration/n");
					TomcatLauncherPlugin.log(ex);
				}
			}
		});
		Button btLog = new Button(group, SWT.PUSH);
		btLog.setText(PREF_PAGE_DUMP_CONFIG_LABEL);
		btLog.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					TomcatLauncherPlugin.getDefault().getTomcatBootstrap().logConfig();
				} catch (Exception ex) {
					TomcatLauncherPlugin.log("Failed to create launch configuration/n");
					TomcatLauncherPlugin.log(ex);
				}
			}
		});


		jvmParamaters = new ListFieldEditor(TomcatLauncherPlugin.TOMCAT_PREF_JVM_PARAMETERS_KEY, PREF_PAGE_PARAMETERS_LABEL, composite);
		jvmClasspath = new ClasspathFieldEditor(TomcatLauncherPlugin.TOMCAT_PREF_JVM_CLASSPATH_KEY, PREF_PAGE_CLASSPATH_LABEL, composite);
		jvmBootClasspath = new ClasspathFieldEditor(TomcatLauncherPlugin.TOMCAT_PREF_JVM_BOOTCLASSPATH_KEY, PREF_PAGE_BOOTCLASSPATH_LABEL, composite);

		this.initField(jvmChoice);
		this.initField(jvmParamaters);
		this.initField(jvmClasspath);
		this.initField(jvmBootClasspath);

		composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		return scrolledComposite;
	}

	/*
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	public boolean performOk() {
		jvmChoice.store();
		jvmBootClasspath.store();
		jvmClasspath.store();
		jvmParamaters.store();
		debugModeEditor.store();

		TomcatLauncherPlugin.getDefault().savePluginPreferences();
		return true;
	}

	private void initField(FieldEditor field) {
		field.setPreferenceStore(getPreferenceStore());
		field.setPreferencePage(this);
		field.load();
	}
	

}
