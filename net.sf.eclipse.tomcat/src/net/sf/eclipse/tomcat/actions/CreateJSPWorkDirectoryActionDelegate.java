package net.sf.eclipse.tomcat.actions;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */
 

import net.sf.eclipse.tomcat.TomcatLauncherPlugin;
import net.sf.eclipse.tomcat.TomcatProject;

import org.eclipse.core.runtime.CoreException;

public class CreateJSPWorkDirectoryActionDelegate extends TomcatProjectAbstractActionDelegate {
	
	public void doActionOn(TomcatProject prj)  throws Exception {
		prj.createWorkFolder();		
		try {
			prj.setWorkAsSourceFolder();
		} catch (CoreException ex) {
			// exception if work already set as source folder	
		}

		if(prj.getUpdateXml()) {
			prj.updateContext();
		} else {
			throw new TomcatActionException(TomcatLauncherPlugin.getResourceString("msg.action.updateServerXML.failed"));	
		}
		
	}

}

