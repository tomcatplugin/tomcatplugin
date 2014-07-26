package com.sysdeo.eclipse.tomcat;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */
 

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

/**
 * Utility class for JDT
 * It might exist better way to implements those operations,
 * or they might already exist in JDT
 */

public class JDTUtil {

	/**
	 * Adds a nature to a project
	 * (From BuildPathsBlock class)
	 */
	
	public static void addNatureToProject(IProject project, String natureId) throws CoreException {
		IProject proj = project.getProject(); // Needed if project is a IJavaProject
		IProjectDescription description = proj.getDescription();
		String[] prevNatures= description.getNatureIds();

		int natureIndex = -1;
		for (int i=0; i<prevNatures.length; i++) {
			if(prevNatures[i].equals(natureId)) {
				natureIndex	= i;
				i = prevNatures.length;
			}
		}

		// Add nature only if it is not already there
		if(natureIndex == -1) { 		
			String[] newNatures= new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length]= natureId;
			description.setNatureIds(newNatures);
			proj.setDescription(description, null);
		}
	}


	/**
	 * Remove a Nature to a Project
	 */
	
	public static void removeNatureToProject(IProject project, String natureId) throws CoreException {
		IProject proj = project.getProject(); // Needed if project is a IJavaProject
		IProjectDescription description = proj.getDescription();
		String[] prevNatures= description.getNatureIds();

		int natureIndex = -1;
		for (int i=0; i<prevNatures.length; i++) {
			if(prevNatures[i].equals(natureId)) {
				natureIndex	= i;
				i = prevNatures.length;
			}
		}

		// Remove nature only if it exists...
		if(natureIndex != -1) { 				
			String[] newNatures= new String[prevNatures.length - 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, natureIndex);
			System.arraycopy(prevNatures, natureIndex+1, newNatures, natureIndex, prevNatures.length - (natureIndex+1));
			description.setNatureIds(newNatures);
			proj.setDescription(description, null);
		}
	}

}
