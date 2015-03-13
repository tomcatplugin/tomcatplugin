/**
 *
 */
package net.sf.eclipse.tomcat;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;

/**
 * DirectoryFieldEditor which considers workspace variables like workspace_loc
 *
 * @author keunecke
 */
public class VariableAwareDirectoryFieldEditor extends DirectoryFieldEditor {

    /**
     * Initial path for the Browse dialog.
     */
    private File filterPath = null;

    protected VariableAwareDirectoryFieldEditor() {
        //nop
    }

    /**
     * Creates a variable aware directory field editor.
     *
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    public VariableAwareDirectoryFieldEditor(String name, String labelText, Composite parent) {
        init(name, labelText);
        setErrorMessage(JFaceResources.getString("DirectoryFieldEditor.errorMessage"));//$NON-NLS-1$
        setChangeButtonText(JFaceResources.getString("openBrowse"));//$NON-NLS-1$
        setValidateStrategy(VALIDATE_ON_FOCUS_LOST);
        createControl(parent);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.DirectoryFieldEditor#doCheckState()
     */
    @Override
    protected boolean doCheckState() {
        String fileName = replaceVariables(getTextControl().getText());
        fileName = fileName.trim();
        if (fileName.length() == 0 && isEmptyStringAllowed()) {
            return true;
        }
        File file = new File(fileName);
        return file.isDirectory();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.DirectoryFieldEditor#changePressed()
     */
    @Override
    protected String changePressed() {
        File f = new File(getTextControl().getText());
        if (!f.exists()) {
            f = null;
        }
        File d = getDirectory(f);
        if (d == null) {
            return null;
        }

        return d.getAbsolutePath();
    }

    /**
     * Sets the initial path for the Browse dialog.
     * @param path initial path for the Browse dialog
     * @since 3.6
     */
    @Override
    public void setFilterPath(File path) {
        filterPath = path;
    }

    /**
     * Helper that opens the directory chooser dialog.
     * @param startingDirectory The directory the dialog will open in.
     * @return File File or <code>null</code>.
     *
     */
    private File getDirectory(File startingDirectory) {

        DirectoryDialog fileDialog = new DirectoryDialog(getShell(), SWT.OPEN | SWT.SHEET);
        if (startingDirectory != null) {
            fileDialog.setFilterPath(startingDirectory.getPath());
        } else if (filterPath != null) {
            fileDialog.setFilterPath(filterPath.getPath());
        }
        String dir = fileDialog.open();
        if (dir != null) {
            dir = dir.trim();
            if (dir.length() > 0) {
                return new File(dir);
            }
        }

        return null;
    }

    private String replaceVariables(String toReplace) {
        try {
            IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();
            String replaced = variableManager.performStringSubstitution(toReplace);
            return replaced;
        } catch (CoreException e) {
            e.printStackTrace();
            return toReplace;
        }
    }

}
