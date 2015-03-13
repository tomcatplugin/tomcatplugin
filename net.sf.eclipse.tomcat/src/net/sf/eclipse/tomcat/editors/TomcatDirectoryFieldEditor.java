/*
 * Created on 1 juil. 2004
 *
 * To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.eclipse.tomcat.editors;

import net.sf.eclipse.tomcat.VariableAwareDirectoryFieldEditor;

import org.eclipse.swt.widgets.Composite;

/**
 * @author Bruno
 *
 * To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TomcatDirectoryFieldEditor extends VariableAwareDirectoryFieldEditor {

    boolean enabledField = true;

    public TomcatDirectoryFieldEditor(String name, String labelText, Composite parent) {
        super(name, labelText, parent);
    }

    @Override
    protected boolean doCheckState() {
        if(enabledField) {
            return super.doCheckState();
        }
        return true;
    }


    @Override
    public void setEnabled(boolean enabled, Composite parent) {
        this.enabledField = enabled;
        super.setEnabled(enabled, parent);
    }


    @Override
    public void valueChanged() {
        super.valueChanged();
    }

}
