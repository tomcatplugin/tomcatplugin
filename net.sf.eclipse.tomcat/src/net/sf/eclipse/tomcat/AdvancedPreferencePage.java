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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import net.sf.eclipse.tomcat.editors.ProjectListEditor;

/**
 *
 * @author keunecke
 */
public class AdvancedPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, TomcatPluginResources {

    private BooleanFieldEditor securityEditor;
    private DirectoryFieldEditor base;
    private ProjectListEditor projectListEditor;

    public AdvancedPreferencePage() {
        super();
        setPreferenceStore(TomcatLauncherPlugin.getDefault().getPreferenceStore());
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout(1, false));


        Group homeGroup = new Group(composite,SWT.NONE);

        base = new DirectoryFieldEditor(
                                        TomcatLauncherPlugin.TOMCAT_PREF_BASE_KEY,
                                        PREF_PAGE_BASE_LABEL,
                                        homeGroup);

        initLayoutAndData(homeGroup, 3);

        Group securityGroup = new Group(composite,SWT.NONE);
        securityEditor = new BooleanFieldEditor(
                                                TomcatLauncherPlugin.TOMCAT_PREF_SECURITYMANAGER,
                                                PREF_PAGE_SECURITYMANAGER_LABEL,
                                                securityGroup);
        this.initField(securityEditor);
        initLayoutAndData(securityGroup, 1);

        Group projectListGroup = new Group(composite,SWT.NONE);
        String[] excludedProjectsNature = {TomcatLauncherPlugin.NATURE_ID};
        projectListEditor = new ProjectListEditor(excludedProjectsNature);
        projectListEditor.setLabel(PREF_PAGE_PROJECTINCP_LABEL);
        Control projectList = projectListEditor.getControl(projectListGroup);
        GridData gd2 = new GridData();
        gd2.horizontalAlignment = GridData.FILL;
        projectList.setLayoutData(gd2);
        initLayoutAndData(projectListGroup, 1);

        this.initField(base);

        new Label(composite, SWT.NULL); //blank

        return composite;
    }

    public void init(IWorkbench workbench) {
        // empty default implementation
    }


    @Override
    public boolean performOk() {
        base.store();
        securityEditor.store();
        TomcatLauncherPlugin.getDefault().setProjectsInCP(projectListEditor.getCheckedElements());
        TomcatLauncherPlugin.getDefault().savePluginPreferences();
        return true;
    }

    private void initField(FieldEditor field) {
        field.setPreferenceStore(getPreferenceStore());
        field.setPreferencePage(this);
        field.load();
    }

    private void initLayoutAndData(Group aGroup, int numColumns) {
        GridLayout gl = new GridLayout(numColumns, false);
        aGroup.setLayout(gl);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 1;
        gd.widthHint = 400;
        aGroup.setLayoutData(gd);
    }
}

