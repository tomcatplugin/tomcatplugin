package com.sysdeo.eclipse.tomcat;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class TomcatProjectChangeListener implements IResourceChangeListener, TomcatPluginResources {

	/*
	 * @see IResourceChangeListener#resourceChanged(IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		if( event.getResource() instanceof IProject) {
			final TomcatProject project = TomcatProject.create((IProject)event.getResource());
			if(project != null) {

				Display.getDefault().syncExec(
					new Runnable() {
						public void run() {
							IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

							String[] labels = {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL};
							MessageDialog dialog = new MessageDialog(
								window.getShell(),
								WIZARD_PROJECT_REMOVE_TITLE,
								null,
								WIZARD_PROJECT_REMOVE_DESCRIPTION,
								MessageDialog.QUESTION,
								labels,
								1);
								
							if (dialog.open() == MessageDialog.OK ) {
								try {
									project.removeContext();
								} catch (Exception ex) {
									TomcatLauncherPlugin.log(ex.getMessage());	
								}		
							}
						}
					}
				);

	
			}		
		}
	}

}
