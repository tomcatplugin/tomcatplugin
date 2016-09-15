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
package net.sf.eclipse.tomcat.editors;

/**
 *	Use in TomcatPreferencePage
 * 	This class is based on PathEditor
 *  There is a button to add files on the list,
 */

import java.io.File;

import net.sf.eclipse.tomcat.TomcatPluginResources;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Widget;

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
		dialog.open();
	
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

	@Override
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
	@Override
	public void createSelectionListener() {
		selectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Widget widget = event.widget;
				if (widget == addJarZipButton) {
					addJarZipPressed();
				} else if (widget == addDirButton) {
					addDirPressed();
				} else if (widget == removeButton) {
					removePressed();
				} else if (widget == upButton) {
					upPressed();
				} else if (widget == downButton) {
					downPressed();
				} else if (widget == list) {
					selectionChanged();
				}
			}
		};
	}

	protected void addJarZipPressed() {
		setPresentsDefaultValue(false);
		String[] input = getNewJarZip();
	
		for(int i=0; i<input.length; i++) {
			int index = list.getSelectionIndex();
			if (index >= 0)
				list.add(input[i], index + 1);
			else
				list.add(input[i], 0);
			selectionChanged();
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