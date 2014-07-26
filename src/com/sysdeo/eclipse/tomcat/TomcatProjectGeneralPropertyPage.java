package com.sysdeo.eclipse.tomcat;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class TomcatProjectGeneralPropertyPage implements TomcatPluginResources {

	private Button isTomcatProjectCheck;
	private Button updateXmlCheck;
	private Button reloadableCheck;
	private Button redirectLoggerCheck;
	private Text webpathText;
	private Text rootDirText;
	private Text extraInfoText;
	private TomcatProjectPropertyPage page;
	
	private static final int TEXT_FIELD_WIDTH = 200;
	
	public TomcatProjectGeneralPropertyPage(TomcatProjectPropertyPage page) {
		this.page = page;
	}
	
	
	/**
	 * returns a control which consists of the ui elements of this page
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
	
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createIsTomcatProjectGroup(composite);
		Group group = new Group(composite, SWT.NONE);
		group.setLayout(new GridLayout());
		createWebpathGroup(group);
		createExtraInformationGroup(group);
		Label lab = new Label(group, SWT.NULL); //blank
		createRootDirGroup(group);

		return composite;
	}

	public void createIsTomcatProjectGroup(Composite parent) {
		Composite isTomcatProjectGroup = new Composite(parent,SWT.NONE);
		isTomcatProjectGroup.setLayout(new GridLayout(3, false));
		isTomcatProjectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// project location entry field
		isTomcatProjectCheck = new Button(isTomcatProjectGroup, SWT.CHECK | SWT.LEFT);
		isTomcatProjectCheck.setText(PROPERTIES_PAGE_PROJECT_ISTOMCATPROJECT_LABEL);
		isTomcatProjectCheck.setEnabled(true);

		try {		
			isTomcatProjectCheck.setSelection(page.getJavaProject().getProject().hasNature(TomcatLauncherPlugin.NATURE_ID));
		} catch (CoreException ex) {
			TomcatLauncherPlugin.log(ex.getMessage());	
		}
	}

	public void createWebpathGroup(Composite parent) {
		Composite webpathGroup = new Composite(parent,SWT.NONE);
		webpathGroup.setLayout(new GridLayout(3, false));
		webpathGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// location label
		Label webpathLabel = new Label(webpathGroup,SWT.NONE);
		webpathLabel.setText(WIZARD_PROJECT_WEBPATH_LABEL);
		webpathLabel.setEnabled(true);

		// project location entry field
		webpathText = new Text(webpathGroup, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = TEXT_FIELD_WIDTH;
//		data.horizontalSpan = 2;
		webpathText.setLayoutData(data);		
		webpathText.setText(this.getWebPath()); 
		webpathText.setEnabled(true);
						
		// project location entry field
		updateXmlCheck = new Button(webpathGroup, SWT.CHECK | SWT.LEFT);
		updateXmlCheck.setText(WIZARD_PROJECT_UPDATEXML_LABEL);
		data = new GridData();
		data.horizontalSpan = 3;
		updateXmlCheck.setLayoutData(data);	
		updateXmlCheck.setEnabled(true);
		updateXmlCheck.setSelection(this.getUpdateXml());
		
		// reloadable attribute			
		reloadableCheck = new Button(webpathGroup,SWT.CHECK | SWT.LEFT);		
		reloadableCheck.setText(WIZARD_PROJECT_RELOADABLE_LABEL);
		data = new GridData();
		data.horizontalSpan = 3;		
		reloadableCheck.setLayoutData(data);
		reloadableCheck.setEnabled(true);
		reloadableCheck.setSelection(this.getReloadable());				

		// reloadable attribute			
		redirectLoggerCheck = new Button(webpathGroup,SWT.CHECK | SWT.LEFT);		
		redirectLoggerCheck.setText(WIZARD_PROJECT_REDIRECTLOGGER_LABEL);
		data = new GridData();
		data.horizontalSpan = 3;		
		redirectLoggerCheck.setLayoutData(data);
		redirectLoggerCheck.setEnabled(true);
		redirectLoggerCheck.setSelection(this.getRedirectLogger());
	}
	

	public void createRootDirGroup(Composite parent) {
		Composite rootDirGroup = new Composite(parent,SWT.NONE);
		rootDirGroup.setLayout(new GridLayout(2, false));
		rootDirGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// location label
		Label rootDirLabel = new Label(rootDirGroup,SWT.NONE);
		rootDirLabel.setText(WIZARD_PROJECT_ROOTDIR_LABEL);
		rootDirLabel.setEnabled(true);

		// project location entry field
		rootDirText = new Text(rootDirGroup, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 30;
//		data.horizontalSpan = 3;
		rootDirText.setLayoutData(data);
		rootDirText.setText(this.getRootDir());
		rootDirText.setEnabled(true);
	}

	public void createExtraInformationGroup(Composite parent) {
		Composite contextGroup = new Composite(parent,SWT.NONE);
		contextGroup.setLayout(new GridLayout(1, false));
		contextGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Extra information label
		Label rootDirLabel = new Label(contextGroup,SWT.NONE);
		rootDirLabel.setText(PROPERTIES_PAGE_PROJECT_EXTRAINFO_LABEL);
		rootDirLabel.setEnabled(true);

		// Extra information field
		extraInfoText = new Text(contextGroup, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.widthHint = 500;
		data.heightHint = 100;
//		data.horizontalSpan = 3;
		extraInfoText.setLayoutData(data);
		extraInfoText.setText(this.getExtraInfo());
		extraInfoText.setEnabled(true);
	}
		

			
	protected String getWebPath() {
		String result = "";
		try {
			TomcatProject prj = page.getTomcatProject();
			if(prj != null)
				result = prj.getWebPath();
		} catch (CoreException ex) {
			// result = "";
		}
		return result;
	}
	
	
	protected String getRootDir() {
		String result = "/";
		try {
			TomcatProject prj = page.getTomcatProject();
			if(prj != null)
				result = prj.getRootDir();
		} catch (CoreException ex) {
			// result = "";
		}
		return result;
	}
	
	protected String getExtraInfo() {
		String result = "";
		try {
			TomcatProject prj = page.getTomcatProject();
			if(prj != null)
				result = prj.getExtraInfo();
		} catch (CoreException ex) {
			// result = "";
		}
		return result;
	}	

	protected boolean getUpdateXml() {
		boolean result = true;
		try {
			TomcatProject prj = page.getTomcatProject();
			if(prj != null)
				result = prj.getUpdateXml();
		} catch (CoreException ex) {
			// result = false;
		}
		return result;
	}


	protected boolean getReloadable(){
		boolean result = true;
		try{
			TomcatProject prj = page.getTomcatProject();
			if(prj != null)
				result = prj.getReloadable();
		}catch(CoreException ex){
		}
		return result;
	}

	protected boolean getRedirectLogger(){
		boolean result = false;
		try{
			TomcatProject prj = page.getTomcatProject();
			if(prj != null)
				result = prj.getRedirectLogger();
		}catch(CoreException ex){
		}
		return result;
	}
				
	/**
	 * performes the ok action for this property page
	 */
	public boolean performOk() {
		try {
			if(isTomcatProjectCheck.getSelection()) {		
				TomcatProject.addTomcatNature(page.getJavaProject());
				TomcatProject prj = page.getTomcatProject();
				prj.updateWebPath(webpathText.getText());			
				prj.setUpdateXml(updateXmlCheck.getSelection());
				prj.setReloadable(reloadableCheck.getSelection());				
				prj.setRedirectLogger(redirectLoggerCheck.getSelection());
				prj.setExtraInfo(extraInfoText.getText());
				prj.setRootDir(rootDirText.getText());
				prj.saveProperties();
			} else {
				page.getTomcatProject().removeContext();
				TomcatProject.removeTomcatNature(page.getJavaProject());
			}
		} catch (Exception ex) {
			TomcatLauncherPlugin.log(ex.getMessage());	
		}
		
		return true;
	}
	

	
	public boolean isTomcatProjectChecked() {
		return isTomcatProjectCheck.getSelection();
	}
	

}
