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

/*
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
