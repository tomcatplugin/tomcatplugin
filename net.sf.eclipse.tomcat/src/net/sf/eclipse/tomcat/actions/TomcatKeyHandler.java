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

import net.sf.eclipse.tomcat.TomcatLauncherPlugin;
import net.sf.eclipse.tomcat.VMLauncherUtility;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;

public class TomcatKeyHandler extends AbstractHandler {

	private static final String START_STOP_CMD_ID = "pl.szpinda.plugin.tomcat.commands.tomcatStartStop";

	public Object execute(ExecutionEvent arg) throws ExecutionException {
		boolean restart = !arg.getCommand().getId().equals(START_STOP_CMD_ID);
		boolean start = false;
		try {
			if(restart){
				if(VMLauncherUtility.ILAUNCH == null || VMLauncherUtility.ILAUNCH.isTerminated())
				{
					TomcatLauncherPlugin.getDefault().getTomcatBootstrap().start();
				}else{
					VMLauncherUtility.ILAUNCH.terminate();
					TomcatLauncherPlugin.getDefault().getTomcatBootstrap().start();
				}
			}else{
				if(VMLauncherUtility.ILAUNCH == null || VMLauncherUtility.ILAUNCH.isTerminated())
				{
					start = true;
					TomcatLauncherPlugin.getDefault().getTomcatBootstrap().start();
				}else{
					VMLauncherUtility.ILAUNCH.terminate();
				}
			}
		} catch (CoreException ex) {
			String msg = null;
			if(restart){
				msg = TomcatLauncherPlugin.getResourceString("msg.restart.failed");
			}else{
				if(start){
					msg = TomcatLauncherPlugin.getResourceString("msg.start.failed");
				}else{
					msg = TomcatLauncherPlugin.getResourceString("msg.stop.failed");					
				}
			}
			TomcatLauncherPlugin.log(msg + "/n");
			TomcatLauncherPlugin.log(ex);
		}
		return null;
	}

}
