package com.sysdeo.eclipse.tomcat.actions;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */
 

import com.sysdeo.eclipse.tomcat.TomcatLauncherPlugin;
import com.sysdeo.eclipse.tomcat.TomcatProject;

public class UpdateTomcatContextActionDelegate extends TomcatProjectAbstractActionDelegate {
	
	public void doActionOn(TomcatProject prj) throws Exception {
		if(prj.getUpdateXml()) {
			prj.updateContext();
		} else {
			throw new TomcatActionException(TomcatLauncherPlugin.getResourceString("msg.action.updateServerXML.failed"));	
		}
	}
}

