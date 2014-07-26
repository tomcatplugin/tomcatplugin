package com.sysdeo.eclipse.tomcat.editors;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.CheckedListDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.ide.IDE;

import com.sysdeo.eclipse.tomcat.TomcatLauncherPlugin;
import com.sysdeo.eclipse.tomcat.TomcatPluginResources;

public class ProjectListEditor implements TomcatPluginResources {
			
	private CheckedListDialogField fProjectsList;
	private String[] fExcludedNatures;
	
	public ProjectListEditor() {
		this(new String[0]);
	}
	
	public ProjectListEditor(String[] excludedNatures) {
		this.fExcludedNatures = excludedNatures;
		String[] buttonLabels= new String[] {
			PREF_PAGE_SELECTALL_LABEL, 
			PREF_PAGE_UNSELECTALL_LABEL
		};
		
		fProjectsList= new CheckedListDialogField(null, buttonLabels, new MyLabelProvider());
		fProjectsList.setCheckAllButtonIndex(0);
		fProjectsList.setUncheckAllButtonIndex(1);
		updateProjectsList();		
//		fProjectsList.setViewerSorter(new CPListElementSorter());
	}
	
	public void setEnabled(boolean enabled) {
		fProjectsList.setEnabled(enabled);
	}
	
	public void init(IJavaProject jproject) {
		updateProjectsList();
	}
	
	public void setLabel(String label) {
		fProjectsList.setLabelText(label);			
	}
	
	private void updateProjectsList() {
		try {
			IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
			IProject[] projects = root.getProjects();
			
			List projectsList = new ArrayList(projects.length);
		
			for (int i= 0; i < projects.length; i++) {
				IProject proj = projects[i];
				if(projects[i].isOpen()) {
					boolean accept = true;
					for (int j = 0; j < fExcludedNatures.length; j++) {
						if(proj.hasNature(fExcludedNatures[j])) accept = false;
					}
					if(accept) {
						projectsList.add(new ProjectListElement(proj));
					} 
				}
			}	
			
			
			// Remove Tomcat project for preference Store (for compatibility between tomcat plugin versions V2.x and V3)
			List oldProjectsInCP = TomcatLauncherPlugin.getDefault().getProjectsInCP();
			List newProjectsInCP = new ArrayList();
			for (Iterator iter = oldProjectsInCP.iterator(); iter.hasNext();) {
				ProjectListElement element = (ProjectListElement) iter.next();
				boolean accept = true;
				for (int j = 0; j < fExcludedNatures.length; j++) {
					if(element.getProject().hasNature(fExcludedNatures[j])) accept = false;
				}
				if(accept) {
					newProjectsInCP.add(element);
				} 
			}			
			
			/* Quick hack :
			 * Using reflection for compatability with Eclipse 2.1 and 3.0	M9				
			 *	
			 * Old code :
			 * 		fProjectsList.setElements(projectsList);
			 *		fProjectsList.setCheckedElements(TomcatLauncherPlugin.getDefault().getProjectsInCP());
			 */
			this.invokeForCompatibility("setElements", projectsList);
			this.invokeForCompatibility("setCheckedElements", newProjectsInCP);
			 	
		} catch (Exception e) {
			/* Old code :
			 * 		fProjectsList.setElements(new ArrayList(5));
			 */
			this.invokeForCompatibility("setElements", new ArrayList(5));
		}

	}		
		
	// -------- UI creation ---------
		
	public Control getControl(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);

//		fProjectsList.doFillIntoGrid(composite,3);			
		LayoutUtil.doDefaultLayout(composite, new DialogField[] { fProjectsList }, true, 0, 0);
				
		return composite;
	}
		
	public List getCheckedElements() {
		return (List)fProjectsList.getCheckedElements();
	}
	
	public void setCheckedElements(List projects) {
		/* Old code :
		 * 		fProjectsList.setCheckedElements(projects);
		 */
		this.invokeForCompatibility("setCheckedElements", projects);
	}	  
	
	private class MyLabelProvider extends LabelProvider {
		
		/*
		 * @see ILabelProvider#getImage(Object)
		 */
		public Image getImage(Object element) {
			IWorkbench workbench= JavaPlugin.getDefault().getWorkbench();
			return workbench.getSharedImages().getImage(IDE.SharedImages.IMG_OBJ_PROJECT);
		}

		/*
		 * @see ILabelProvider#getText(Object)
		 */
		public String getText(Object element) {
			return super.getText(element);
		}

	}

	/* Quick hack :
	 * Using reflection for compatability with Eclipse 2.1 and 3.0	M9
	 */			
	
	private void invokeForCompatibility(String methodName, List projects) {
		Class clazz = fProjectsList.getClass();
		Class[] collectionParameter = {Collection.class};
		try {
			Method method = clazz.getMethod(methodName, collectionParameter);
			Object[] args = {projects};
			method.invoke(fProjectsList, args);
		} catch (Exception e) {
			Class[] listParameter = {List.class};
			try {
				Method method = clazz.getMethod(methodName, listParameter);
				Object[] args = {projects};
				method.invoke(fProjectsList, args);				
			} catch (Exception ex) {
				TomcatLauncherPlugin.log(ex);
			}
		}
			
	}

}
