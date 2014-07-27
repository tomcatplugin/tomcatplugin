package net.sf.eclipse.tomcat.actions;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */
 

import net.sf.eclipse.tomcat.TomcatLauncherPlugin;
import net.sf.eclipse.tomcat.TomcatProject;

public class RemoveTomcatContextActionDelegate extends TomcatProjectAbstractActionDelegate {
	
	public void doActionOn(TomcatProject prj) throws Exception{
		if(prj.getUpdateXml()) {
			prj.removeContext();
		} else {
			throw new TomcatActionException(TomcatLauncherPlugin.getResourceString("msg.action.updateServerXML.failed"));	
		}
	}
	
}

