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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
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
import org.osgi.framework.Bundle;

public class TomcatProjectCreationWizard extends NewElementWizard implements IExecutableExtension, TomcatPluginResources {

    private NewTomcatProjectWizardPage fTomcatPage;
    private NewJavaProjectWizardPage fJavaPage;
    private WizardNewProjectCreationPage fMainPage;
    private IConfigurationElement fConfigElement;

    public TomcatProjectCreationWizard() {
        super();
        TomcatLauncherPlugin.checkTomcatSettingsAndWarn();
        ImageDescriptor banner = this.getBannerImg();
        if (banner != null) {
            setDefaultPageImageDescriptor(banner);
        }
        setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
        setWindowTitle(WIZARD_PROJECT_TITLE);
    }

    @Override
    public boolean canFinish() {
        return TomcatLauncherPlugin.isTomcatConfigured();

    }

    private ImageDescriptor getBannerImg() {
        try {
			Bundle bundle = Platform.getBundle(TomcatLauncherPlugin.PLUGIN_ID);
			URL icons = FileLocator.find(bundle, new Path("icons/"), new HashMap<String, String>());
            return ImageDescriptor.createFromURL(new URL(icons, "newjprj_wiz.gif"));
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public void addPages() {
        super.addPages();
        fMainPage = new WizardNewProjectCreationPage("Page 1");
        fMainPage.setTitle(WIZARD_PROJECT_MAINPAGE_TITLE);
        fMainPage.setDescription(WIZARD_PROJECT_MAINPAGE_DESCRIPTION);
        addPage(fMainPage);

        fTomcatPage = new NewTomcatProjectWizardPage("NewTomcatProjectPage");
        fTomcatPage.setTitle(WIZARD_PROJECT_TOMCATPAGE_TITLE);
        fTomcatPage.setDescription(WIZARD_PROJECT_TOMCATPAGE_DESCRIPTION);
        addPage(fTomcatPage);

        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        fJavaPage = new NewJavaProjectWizardPage(root, fMainPage);

    }

    @Override
    public boolean performFinish() {
        IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation(fJavaPage.getRunnable());
        try {
            getContainer().run(false, true, op);
            TomcatProject.addTomcatNature(fJavaPage.getNewJavaProject());
            TomcatProject tomcatPrj = TomcatProject.create(fJavaPage.getNewJavaProject());
            tomcatPrj.setWebPath(fTomcatPage.getWebpath());
            tomcatPrj.setUpdateXml(fTomcatPage.getUpdateXml());
            tomcatPrj.setRootDir(fTomcatPage.getRootDir());
            tomcatPrj.setWorkDir("work");
            tomcatPrj.saveProperties();
            tomcatPrj.fullConfiguration();
        } catch (InvocationTargetException e) {
            String title = NewWizardMessages.JavaProjectWizard_op_error_title;
            String message = NewWizardMessages.JavaProjectWizard_op_error_create_message;
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

    /**
     * Stores the configuration element for the wizard.  The config element will be used
     * in <code>performFinish</code> to set the result perspective.
     */
    public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
        fConfigElement = cfig;
    }

    @Override
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

    @Override
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

    @Override
    public IJavaElement getCreatedElement() {
        return null;
    }

}