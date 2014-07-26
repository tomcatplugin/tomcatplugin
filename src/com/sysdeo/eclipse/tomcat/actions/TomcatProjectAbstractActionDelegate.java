package com.sysdeo.eclipse.tomcat.actions;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */
 

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

import com.sysdeo.eclipse.tomcat.TomcatLauncherPlugin;
import com.sysdeo.eclipse.tomcat.TomcatProject;

abstract public class TomcatProjectAbstractActionDelegate implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	private String msg;

	/*
	 * @see IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
	}

	/*
	 * @see IWorkbenchWindowActionDelegate#init(IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	/*
	 * @see IActionDelegate#run(IAction)
	 */
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

	/*
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {

	}

	protected TomcatProject getCurrentSelection() {
		IWorkbenchWindow window= JavaPlugin.getActiveWorkbenchWindow();
		TomcatProject result = null;
		if (window != null) {
			ISelection selection= window.getSelectionService().getSelection();
			if (selection instanceof IStructuredSelection) {
				Object project = ((IStructuredSelection)selection).getFirstElement();
				if(project instanceof IProject)
					result = TomcatProject.create((IProject)project);
				if(project instanceof IJavaProject)
					result = TomcatProject.create((IJavaProject)project);
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
	 * @param msg The msg to set
	 */
	private void setMsgToSuccess() {
		this.msg = TomcatLauncherPlugin.getResourceString("msg.action.succeeded");
	}	
}

