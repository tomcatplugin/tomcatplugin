package com.sysdeo.eclipse.tomcat;

/*
 * (c) Copyright Martin Kahr, Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * provides a tab control with property pages for the 
 * settings of a tomcat project.
 * 
 * @version 	1.0
 * @author		Martin Kahr
 */
public class TomcatProjectPropertyPage extends PropertyPage implements IWorkbenchPreferencePage, TomcatPluginResources {

	private TabFolder folder;
	private TomcatProjectGeneralPropertyPage generalPropertyPage;
	private TomcatProjectWebclasspathPropertyPage webClassPathPropertyPage;
	private TomcatProjectWARPropertyPage warPropertyPage;

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		folder = new TabFolder(parent, SWT.NONE);

//		folder.setLayout(new TabFolderLayout());
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));

		// general property page
		generalPropertyPage = new TomcatProjectGeneralPropertyPage(this);
		// add to tab
		TabItem generalTab = new TabItem(folder, SWT.NONE);
		generalTab.setText(TomcatPluginResources.PROPERTIES_PAGE_PROJECT_GENERAL_TAB_LABEL);
		generalTab.setControl(generalPropertyPage.createContents(folder));

		// webclasspath property page
		webClassPathPropertyPage = new TomcatProjectWebclasspathPropertyPage(this);
		// add to tab
		TabItem webClassTab = new TabItem(folder, SWT.NONE);
		webClassTab.setText(TomcatPluginResources.PROPERTIES_PAGE_PROJECT_DEVLOADER_TAB_LABEL);
		webClassTab.setControl(webClassPathPropertyPage.getControl(folder));

		// war property page
		warPropertyPage = new TomcatProjectWARPropertyPage(this);
		// add to tab
		TabItem warTab = new TabItem(folder, SWT.NONE);
		warTab.setText(TomcatPluginResources.PROPERTIES_PAGE_PROJECT_WAR_TAB_LABEL);
		warTab.setControl(warPropertyPage.createContents(folder));

		return folder;
	}

	/**
	 * @see IPreferencePage#performOk()
	 */
	public boolean performOk() {
		// delegate to property pages
		if (generalPropertyPage.performOk()) {
			// check if it's a tomcat project any more 
			if (generalPropertyPage.isTomcatProjectChecked()) {
				if (webClassPathPropertyPage.performOk()) {
					try {
						this.getTomcatProject().updateContext();
					} catch (Exception ex) {
						TomcatLauncherPlugin.log(ex);
					}
				}
				warPropertyPage.performOk();
			}
		}
		return true;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	/* helper methods */
	protected IJavaProject getJavaProject() throws CoreException {
		IProject project = (IProject) (this.getElement().getAdapter(IProject.class));
		return (IJavaProject) (project.getNature(JavaCore.NATURE_ID));
	}
	protected TomcatProject getTomcatProject() throws CoreException {
		return TomcatProject.create(getJavaProject());
	}

}
