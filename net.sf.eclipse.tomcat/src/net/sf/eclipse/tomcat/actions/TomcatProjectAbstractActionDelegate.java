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
package net.sf.eclipse.tomcat.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import net.sf.eclipse.tomcat.TomcatLauncherPlugin;
import net.sf.eclipse.tomcat.TomcatProject;

abstract public class TomcatProjectAbstractActionDelegate implements IWorkbenchWindowActionDelegate {
	private String msg;

	public void dispose() {
	    // empty default implementation
	}

	public void init(IWorkbenchWindow window) {
        // empty default implementation
	}

	public void run(IAction action) {
		setMsgToSuccess();
		try {
			TomcatProject prj = this.getCurrentSelection();
			if(prj != null) {
				this.doActionOn(prj);
			}
		} catch (TomcatActionException ex) {
			setMsgToFail(ex.getMessage(), false);
		} catch (Exception ex) {
			TomcatLauncherPlugin.log(ex);
			setMsgToFail(ex.getMessage(), true);
		}

		if(showMessageBox()) {
			Shell shell= TomcatLauncherPlugin.getShell();
			MessageDialog.openInformation(shell,"Tomcat", msg);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
        // empty default implementation
	}

	protected TomcatProject getCurrentSelection() {
		IWorkbenchWindow window= JavaPlugin.getActiveWorkbenchWindow();
		TomcatProject result = null;
		if (window != null) {
			ISelection selection= window.getSelectionService().getSelection();
			if (selection instanceof IStructuredSelection) {
				Object project = ((IStructuredSelection)selection).getFirstElement();
				if(project instanceof IProject) {
                    result = TomcatProject.create((IProject)project);
                }
				if(project instanceof IJavaProject) {
                    result = TomcatProject.create((IJavaProject)project);
                }
			}
		}
		return result;
	}

	abstract public void doActionOn(TomcatProject prj) throws Exception;

	public boolean showMessageBox() {
		return true;
	};

	/**
	 * Sets the msg.
	 *
	 * @param msg The msg to set
	 */
	private void setMsgToFail(String detail, boolean seelog) {
		this.msg = TomcatLauncherPlugin.getResourceString("msg.action.failed");
		this.msg += "\n" + detail;
		if(seelog) {
			this.msg += TomcatLauncherPlugin.getResourceString("msg.action.seelog");
		}
	}

	/**
	 * Sets the msg.
	 *
	 * @param msg The msg to set
	 */
	private void setMsgToSuccess() {
		this.msg = TomcatLauncherPlugin.getResourceString("msg.action.succeeded");
	}
}

