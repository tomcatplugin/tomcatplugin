package com.sysdeo.eclipse.tomcat.actions;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */
 

import com.sysdeo.eclipse.tomcat.TomcatLauncherPlugin;
import com.sysdeo.eclipse.tomcat.TomcatProject;

public class ExportToWarActionDelegate extends TomcatProjectAbstractActionDelegate {
	
	public void doActionOn(TomcatProject prj) throws Exception {
		if(!prj.getWarLocation().equals("")) {
			prj.exportToWar();
		} else {
			throw new TomcatActionException(TomcatLauncherPlugin.getResourceString("msg.action.exportWAR.failed"));	
		}
	}			

}

