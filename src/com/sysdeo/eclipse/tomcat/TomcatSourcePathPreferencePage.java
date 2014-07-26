package com.sysdeo.eclipse.tomcat;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.sysdeo.eclipse.tomcat.editors.ProjectListEditor;


public class TomcatSourcePathPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, TomcatPluginResources {

	private ProjectListEditor projectListEditor;
	private BooleanFieldEditor automaticEditor;
		
	public TomcatSourcePathPreferencePage() {
		super();
		setPreferenceStore(TomcatLauncherPlugin.getDefault().getPreferenceStore());
	}

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());

		// Group securityGroup = new Group(composite,SWT.NONE);
		automaticEditor = new BooleanFieldEditor(
			TomcatLauncherPlugin.TOMCAT_PREF_COMPUTESOURCEPATH_KEY,
			PREF_PAGE_COMPUTESOURCEPATH_LABEL,
			composite);
		this.initField(automaticEditor);

		final Group projectListGroup = new Group(composite, SWT.NULL);
		projectListGroup.setLayout(new GridLayout());
		projectListEditor = new ProjectListEditor();
		projectListEditor.setLabel(PREF_PAGE_PROJECTINSOURCEPATH_LABEL);
		final Control projectList = projectListEditor.getControl(projectListGroup);
		projectListGroup.setLayoutData(new GridData(GridData.FILL_BOTH));		
		projectList.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		projectListEditor.setCheckedElements(TomcatLauncherPlugin.getDefault().readProjectsInSourcePathFromPref());		 

		projectListEditor.setEnabled(!automaticEditor.getBooleanValue());
		//projectListEditor.setEnabled(false);
		automaticEditor.setPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				projectListEditor.setEnabled(!automaticEditor.getBooleanValue());
			}
		});
		
		return composite;
	}

	/*
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}


	public boolean performOk() {
		automaticEditor.store();
		TomcatLauncherPlugin.getDefault().setProjectsInSourcePath(projectListEditor.getCheckedElements());
//		TomcatLauncherPlugin.getDefault().savePluginPreferences();
		return true;	
	}

	private void initField(FieldEditor field) {
		field.setPreferenceStore(getPreferenceStore());
		field.setPreferencePage(this);
		field.load();		
	}	
}

