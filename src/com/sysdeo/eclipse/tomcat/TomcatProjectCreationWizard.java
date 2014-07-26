package com.sysdeo.eclipse.tomcat;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPage;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

public class TomcatProjectCreationWizard extends NewElementWizard implements IExecutableExtension, TomcatPluginResources {

	public static final String NEW_PROJECT_WIZARD_ID = "org.eclipse.jdt.ui.wizards.NewProjectCreationWizard"; //$NON-NLS-1$

	private NewTomcatProjectWizardPage fTomcatPage;
	private NewJavaProjectWizardPage fJavaPage;
	private WizardNewProjectCreationPage fMainPage;
	private IConfigurationElement fConfigElement;

	public TomcatProjectCreationWizard() {
		super();
		TomcatLauncherPlugin.checkTomcatSettingsAndWarn();
		ImageDescriptor banner = this.getBannerImg();
		if (banner != null)
			setDefaultPageImageDescriptor(banner);
		setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
		setWindowTitle(WIZARD_PROJECT_TITLE);
	}

	public boolean canFinish() {
		return TomcatLauncherPlugin.isTomcatConfigured();

	}

	private ImageDescriptor getBannerImg() {
		try {
			URL prefix = new URL(TomcatLauncherPlugin.getDefault().getDescriptor().getInstallURL(), "icons/");
			return ImageDescriptor.createFromURL(new URL(prefix, "newjprj_wiz.gif"));
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/*
	 * @see Wizard#addPages
	 */
	public void addPages() {
		super.addPages();
		fMainPage = new WizardNewProjectCreationPage("Page 1");
		fMainPage.setTitle(WIZARD_PROJECT_MAINPAGE_TITLE);
		fMainPage.setDescription(WIZARD_PROJECT_MAINPAGE_DESCRIPTION);
		addPage(fMainPage);

		fTomcatPage = new NewTomcatProjectWizardPage(this, "NewTomcatProjectPage");
		fTomcatPage.setTitle(WIZARD_PROJECT_TOMCATPAGE_TITLE);
		fTomcatPage.setDescription(WIZARD_PROJECT_TOMCATPAGE_DESCRIPTION);
		addPage(fTomcatPage);

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		fJavaPage = new NewJavaProjectWizardPage(root, fMainPage);
		//		addPage(fJavaPage);

	}

	/*
	 * @see Wizard#performFinish
	 */
	public boolean performFinish() {
		IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation(fJavaPage.getRunnable());
		try {
			getContainer().run(false, true, op);
			TomcatProject.addTomcatNature(fJavaPage.getNewJavaProject());
			TomcatProject tomcatPrj = TomcatProject.create(fJavaPage.getNewJavaProject());
			tomcatPrj.setWebPath(fTomcatPage.getWebpath());
			tomcatPrj.setUpdateXml(fTomcatPage.getUpdateXml());
			tomcatPrj.setRootDir(fTomcatPage.getRootDir());
			tomcatPrj.saveProperties();
			tomcatPrj.fullConfiguration();
		} catch (InvocationTargetException e) {
			String title = NewWizardMessages.JavaProjectWizard_op_error_title; //$NON-NLS-1$
			String message = NewWizardMessages.JavaProjectWizard_op_error_create_message; //$NON-NLS-1$
			ExceptionHandler.handle(e, getShell(), title, message);
			return false;
		} catch (InterruptedException e) {
			return false;
		} catch (CoreException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

		BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
		selectAndReveal(fJavaPage.getNewJavaProject().getProject());
		return true;
	}

	/*
	 * Stores the configuration element for the wizard.  The config element will be used
	 * in <code>performFinish</code> to set the result perspective.
	 */
	public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
		fConfigElement = cfig;
	}

	/*
	 * @see IWizard#getNextPage(IWizardPage)
	 */
	public IWizardPage getNextPage(IWizardPage page) {
		// initialize Tomcat Wizard page webpath field
		// Default value is / + projectName
		if (page instanceof WizardNewProjectCreationPage) {
			if (!fTomcatPage.wasDisplayedOnce()) {
				fTomcatPage.setWebpath("/" + fMainPage.getProjectName());
			}
		}

		return super.getNextPage(page);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		// for Eclipse 3.0 compatibility
	}

	/**
	 * Getters for subclasses
	 */
	protected IConfigurationElement getFConfigElement() {
		return fConfigElement;
	}

	protected NewJavaProjectWizardPage getFJavaPage() {
		return fJavaPage;
	}

	protected WizardNewProjectCreationPage getFMainPage() {
		return fMainPage;
	}

	protected NewTomcatProjectWizardPage getFTomcatPage() {
		return fTomcatPage;
	}

	public IJavaElement getCreatedElement() {
		// TODO Auto-generated method stub
		return null;
	}

}