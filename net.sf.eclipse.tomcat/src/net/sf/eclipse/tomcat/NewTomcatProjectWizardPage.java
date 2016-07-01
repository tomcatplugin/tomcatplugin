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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewTomcatProjectWizardPage extends WizardPage implements TomcatPluginResources {

	private Button updateXmlCheck;
	private Text webpathText;
	private Text rootDirText;

	// See TomcatProjectCreationWizard.getNextPage
	private boolean displayedOnce = false;

	private static final int TEXT_FIELD_WIDTH = 200;

	/**
	 * Creates a new project creation wizard page.
	 *
	 * @param pageName the name of this page
	 */
	public NewTomcatProjectWizardPage(String pageName) {
		super(pageName);
		setPageComplete(true);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);

		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		createWebpathGroup(composite);
		createUpdateXmlGroup(composite);

		new Label(composite, SWT.NULL);
		createRootDirGroup(composite);

		setErrorMessage(null);
		setMessage(null);
		setControl(composite);
	}


	public void createWebpathGroup(Composite parent) {
		Composite webpathGroup = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		webpathGroup.setLayout(layout);
		webpathGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// location label
		Label webpathLabel = new Label(webpathGroup,SWT.NONE);
		webpathLabel.setText(WIZARD_PROJECT_WEBPATH_LABEL);
		webpathLabel.setEnabled(true);

		// project location entry field
		webpathText = new Text(webpathGroup, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = TEXT_FIELD_WIDTH;
		webpathText.setLayoutData(data);
		webpathText.setText(""); // see TomcatProjectCreationWizard.nextPage
		webpathText.setEnabled(true);
	}


	public void createUpdateXmlGroup(Composite parent) {
		Composite updateXmlGroup = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		updateXmlGroup.setLayout(layout);
		updateXmlGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// project location entry field
		updateXmlCheck = new Button(updateXmlGroup, SWT.CHECK | SWT.LEFT);
		updateXmlCheck.setText(WIZARD_PROJECT_UPDATEXML_LABEL);
		updateXmlCheck.setEnabled(true);
		updateXmlCheck.setSelection(true);
	}

	public void createRootDirGroup(Composite parent) {
		Composite rootDirGroup = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		rootDirGroup.setLayout(layout);
		rootDirGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// location label
		Label rootDirLabel = new Label(rootDirGroup,SWT.NONE);
		rootDirLabel.setText(WIZARD_PROJECT_ROOTDIR_LABEL);
		rootDirLabel.setEnabled(true);

		// project location entry field
		rootDirText = new Text(rootDirGroup, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = TEXT_FIELD_WIDTH;
		rootDirText.setLayoutData(data);
		rootDirText.setText("/"); // see TomcatProjectCreationWizard.nextPage
		rootDirText.setEnabled(true);
	}

	public String getWebpath() {
		return webpathText.getText();
	}

	public String getRootDir() {
		return rootDirText.getText();
	}

	public boolean getUpdateXml() {
		return updateXmlCheck.getSelection();
	}

	public void setWebpath(String path) {
		webpathText.setText(path);
	}

	@Override
    public boolean canFlipToNextPage() {
		displayedOnce = true;
		return super.canFlipToNextPage();
	}


	/**
	 * Gets the wasDisplayedOnce.
	 *
	 * @return Returns a boolean
	 */
	public boolean wasDisplayedOnce() {
		return displayedOnce;
	}

}
