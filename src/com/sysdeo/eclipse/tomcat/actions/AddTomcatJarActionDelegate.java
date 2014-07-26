package com.sysdeo.eclipse.tomcat.actions;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */
 

import com.sysdeo.eclipse.tomcat.TomcatProject;

public class AddTomcatJarActionDelegate extends TomcatProjectAbstractActionDelegate {
	
	public void doActionOn(TomcatProject prj) throws Exception {
		prj.addTomcatJarToProjectClasspath();
	}
	
}

