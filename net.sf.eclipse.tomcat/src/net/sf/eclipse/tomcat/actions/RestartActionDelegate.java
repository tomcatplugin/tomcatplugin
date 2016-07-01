package net.sf.eclipse.tomcat.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */


import net.sf.eclipse.tomcat.TomcatLauncherPlugin;

public class RestartActionDelegate implements IWorkbenchWindowActionDelegate {

	/*
	 * @see IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
		// empty default implementation
	}

	/*
	 * @see IWorkbenchWindowActionDelegate#init(IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
        // empty default implementation
	}

	/*
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		if(TomcatLauncherPlugin.checkTomcatSettingsAndWarn()) {
			//TomcatLauncherPlugin.log(TomcatLauncherPlugin.getResourceString("msg.restart"));
			try {
				TomcatLauncherPlugin.getDefault().getTomcatBootstrap().restart();
			} catch (Exception ex) {
				String msg = TomcatLauncherPlugin.getResourceString("msg.restart.failed");
				TomcatLauncherPlugin.log(msg + "/n");
				TomcatLauncherPlugin.log(ex);
			}
		}
	}

	/*
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		// empty default implementation
	}

}

