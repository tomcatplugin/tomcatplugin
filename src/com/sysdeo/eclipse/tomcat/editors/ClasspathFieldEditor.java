package com.sysdeo.eclipse.tomcat.editors;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */

/**
 *	Use in TomcatPreferencePage
 * 	This class is based on PathEditor
 *  There is a button to add files on the list,
 */

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Widget;

import com.sysdeo.eclipse.tomcat.TomcatPluginResources;

/**
 * A field editor to edit directory paths.
 */
public class ClasspathFieldEditor extends ListFieldEditor implements TomcatPluginResources {

	protected Button addJarZipButton;
	protected Button addDirButton;

	/**
	 * The last path, or <code>null</code> if none.
	 */
	private String lastPath;

	/**
	 * Creates a new path field editor 
	 */
	protected ClasspathFieldEditor() {
	}


	public ClasspathFieldEditor(String name, String labelText, Composite parent) {
		init(name, labelText);
		createControl(parent);
	}

	protected String[] getNewJarZip() {
		FileDialog dialog = new FileDialog(addJarZipButton.getShell(), SWT.MULTI);
		if (lastPath != null) {
			if (new File(lastPath).exists())
				dialog.setFilterPath(lastPath);
		}
		String file = dialog.open();
	
		if (dialog.getFileNames().length != 0) {
			lastPath = dialog.getFilterPath();
			String[] result = dialog.getFileNames();
			for (int i = 0; i < result.length; i++) {
				result[i] = lastPath + File.separator + result[i];
			}
			return result;
		} else {
			return new String[0];
		}		
	}

	protected String getNewDir() {
		DirectoryDialog dialog = new DirectoryDialog(addDirButton.getShell());
		if (lastPath != null) {
			if (new File(lastPath).exists())
				dialog.setFilterPath(lastPath);
		}
		String dir = dialog.open();
		if (dir != null) {
			dir = dir.trim();
			if (dir.length() == 0)
				return null;
			lastPath = dir;
		}
		return dir;
	}

	protected void createButtons(Composite buttonBox) {
		addJarZipButton = createPushButton(buttonBox, PREF_PAGE_ADDJARZIPBUTTON_LABEL);//$NON-NLS-1$
		addDirButton = createPushButton(buttonBox, PREF_PAGE_ADDDIRBUTTON_LABEL);//$NON-NLS-1$
		removeButton = createPushButton(buttonBox, PREF_PAGE_REMOVEBUTTON_LABEL);//$NON-NLS-1$
		upButton = createPushButton(buttonBox, PREF_PAGE_UPBUTTON_LABEL);//$NON-NLS-1$
		downButton = createPushButton(buttonBox, PREF_PAGE_DOWNBUTTON_LABEL);//$NON-NLS-1$
	}

	/**
	 * Creates a selection listener.
	 */
	public void createSelectionListener() {
		selectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Widget widget = event.widget;
				if (widget == addJarZipButton) {
					addJarZipPressed();
				} else
					if (widget == addDirButton) {
						addDirPressed();
					} else
						if (widget == removeButton) {
							removePressed();
						} else
							if (widget == upButton) {
								upPressed();
							} else
								if (widget == downButton) {
									downPressed();
								} else
									if (widget == list) {
										selectionChanged();
									}
			}
		};
	}

	protected void addJarZipPressed() {
		setPresentsDefaultValue(false);
		String[] input = getNewJarZip();
	
		for(int i=0; i<input.length; i++) {
			if (input != null) {
				int index = list.getSelectionIndex();
				if (index >= 0)
					list.add(input[i], index + 1);
				else
					list.add(input[i], 0);
				selectionChanged();
			}
		}
	}
	
	
	protected void addDirPressed() {
		setPresentsDefaultValue(false);
		String input = getNewDir();
	
		if (input != null) {
			int index = list.getSelectionIndex();
			if (index >= 0)
				list.add(input, index + 1);
			else
				list.add(input, 0);
			selectionChanged();
		}
	}


}