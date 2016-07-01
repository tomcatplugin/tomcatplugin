/* The MIT License
 * (c) Copyright Martin Kahr, Sysdeo SA 2001-2002
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

	private TomcatProjectGeneralPropertyPage generalPropertyPage;
	private TomcatProjectWebclasspathPropertyPage webClassPathPropertyPage;
	private TomcatProjectWARPropertyPage warPropertyPage;

	@Override
    protected Control createContents(Composite parent) {
	    TabFolder folder = new TabFolder(parent, SWT.NONE);

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

	@Override
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

	public void init(IWorkbench workbench) {
	    // empty default implementation
	}

	/* helper methods */
	protected IJavaProject getJavaProject() throws CoreException {
		IProject project = (this.getElement().getAdapter(IProject.class));
		return (IJavaProject) (project.getNature(JavaCore.NATURE_ID));
	}
	protected TomcatProject getTomcatProject() throws CoreException {
		return TomcatProject.create(getJavaProject());
	}

}
