package com.sysdeo.eclipse.tomcat;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class TomcatManagerAppPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage, TomcatPluginResources {

	private String DEFAULT_MANAGER_URL = "http://localhost:8080/manager";
	private StringFieldEditor urlEditor;
	private StringFieldEditor userEditor;
	private StringFieldEditor pwdEditor;	

	public TomcatManagerAppPreferencePage() {
		super(GRID);
		setPreferenceStore(
			TomcatLauncherPlugin.getDefault().getPreferenceStore());
	}

	protected void createFieldEditors() {
		Group box = new Group(this.getFieldEditorParent(), SWT.NULL);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		box.setLayoutData(gd);
		box.setLayout(new GridLayout(1, true));
		Label details = new Label(box, SWT.WRAP);
		details.setText(PREF_PAGE_MANAGER_BANNER);

		new Label(this.getFieldEditorParent(), SWT.NULL);
		new Label(this.getFieldEditorParent(), SWT.NULL);
		
		urlEditor = new StringFieldEditor(
				TomcatLauncherPlugin.TOMCAT_PREF_MANAGER_URL,
				PREF_PAGE_MANAGER_URL,
				getFieldEditorParent());
		this.addField(urlEditor);
		
		new Label(this.getFieldEditorParent(), SWT.NULL);
		new Label(this.getFieldEditorParent(), SWT.NULL);

		userEditor = new StringFieldEditor(
				TomcatLauncherPlugin.TOMCAT_PREF_MANAGER_USER,
				PREF_PAGE_MANAGER_USER,
				getFieldEditorParent());
		this.addField(userEditor);

		pwdEditor = new StringFieldEditor(
				TomcatLauncherPlugin.TOMCAT_PREF_MANAGER_PASSWORD,
				PREF_PAGE_MANAGER_PASSWORD,
				getFieldEditorParent());
		this.addField(pwdEditor);

		new Label(this.getFieldEditorParent(), SWT.NULL);
		Button addUserBt = new Button(getFieldEditorParent(), SWT.PUSH);
		addUserBt.setText(PREF_PAGE_MANAGER_ADDUSER);
		addUserBt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			 	addUserToTomcatUsers(); 	
			}
		});		

	}

	/*
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		if (!(getPreferenceStore()
			.contains(TomcatLauncherPlugin.TOMCAT_PREF_MANAGER_URL))) {
			getPreferenceStore().setValue(
				TomcatLauncherPlugin.TOMCAT_PREF_MANAGER_URL,
				DEFAULT_MANAGER_URL);
		}
	}

	public void addUserToTomcatUsers() {
		try {
			String endTag = "</tomcat-users>";
			String tomcatDir = TomcatLauncherPlugin.getDefault().getTomcatDir();
			// TODO: should use Tomcat base ?
			File tomcatUsersFile = new File(tomcatDir + File.separator + "conf" + File.separator + "tomcat-users.xml");
			String tomcatUsersContent = FileUtil.readTextFile(tomcatUsersFile);
			String before = tomcatUsersContent.substring(0, tomcatUsersContent.indexOf(endTag));
			String username = userEditor.getStringValue();
			String manager = pwdEditor.getStringValue();
			String userTag = "  <user username=\"" + username + "\" password=\"" + manager + "\" roles=\"manager\"/>\n";
			FileUtil.toTextFile(tomcatUsersFile, before + userTag + endTag);	
		} catch (IOException e) {
			TomcatLauncherPlugin.log(e);
		}
	}
}
