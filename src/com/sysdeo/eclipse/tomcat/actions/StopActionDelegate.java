package com.sysdeo.eclipse.tomcat.actions;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.sysdeo.eclipse.tomcat.TomcatLauncherPlugin;

public class StopActionDelegate implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;

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
		if(TomcatLauncherPlugin.checkTomcatSettingsAndWarn()) {
			//TomcatLauncherPlugin.log(TomcatLauncherPlugin.getResourceString("msg.stop"));
			try {	
				TomcatLauncherPlugin.getDefault().getTomcatBootstrap().stop();
			} catch (Exception ex) {
				String msg = TomcatLauncherPlugin.getResourceString("msg.stop.failed");
				TomcatLauncherPlugin.log(msg + "/n");
				TomcatLauncherPlugin.log(ex);	
			}
		}
	}

	/*
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}

