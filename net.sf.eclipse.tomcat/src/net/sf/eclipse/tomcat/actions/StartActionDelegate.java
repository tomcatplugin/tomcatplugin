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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import net.sf.eclipse.tomcat.TomcatLauncherPlugin;

public class StartActionDelegate implements IWorkbenchWindowActionDelegate {

	public void dispose() {
		// empty default implementation
	}

	public void init(IWorkbenchWindow window) {
        // empty default implementation
	}

	public void run(IAction action) {
		if(TomcatLauncherPlugin.checkTomcatSettingsAndWarn()) {
			//TomcatLauncherPlugin.log(TomcatLauncherPlugin.getResourceString("msg.start"));
			try {
				TomcatLauncherPlugin.getDefault().getTomcatBootstrap().start();
			} catch (Exception ex) {
				String msg = TomcatLauncherPlugin.getResourceString("msg.start.failed");
				TomcatLauncherPlugin.log(msg + "/n");
				TomcatLauncherPlugin.log(ex);
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// empty default implementation
	}

}

