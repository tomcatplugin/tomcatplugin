package com.sysdeo.eclipse.tomcat.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

public class ProjectListElement {

	IProject project;

	public ProjectListElement(IProject project) {
		super();
		this.project = project;	
	}

	public String toString() {
		return getID(project);	
	}
	
	public String getID() {
		return getID(project);	
	}
	
	static protected String getID(IProject project) {
		return project.getName();
	}
	
	static public List stringsToProjectsList(List projectIdList) {
		IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();
 		
		List selectedProjects = new ArrayList();
		for(int i=0; i<projects.length; i++) {
			if(projectIdList.contains(getID(projects[i]))) {
				selectedProjects.add(new ProjectListElement(projects[i]));	
			}
		}

		return selectedProjects;
	}
	
	/*
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if(obj instanceof ProjectListElement)
			return this.getID().equals(((ProjectListElement)obj).getID());

		return false;
	}

	/*
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return this.getID().hashCode();
	}

	/**
	 * Gets the project.
	 * @return Returns a IProject
	 */
	public IProject getProject() {
		return project;
	}


}
