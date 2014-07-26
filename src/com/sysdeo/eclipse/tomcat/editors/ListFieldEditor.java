package com.sysdeo.eclipse.tomcat.editors;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import com.sysdeo.eclipse.tomcat.TomcatPluginResources;

public class ListFieldEditor extends ListEditor implements TomcatPluginResources {

	/**
	 * The list widget; <code>null</code> if none (before creation or after
	 * disposal).
	 */
	protected List list;

	/**
	 * The button box containing the Add, Remove, Up, and Down buttons;
	 * <code>null</code> if none (before creation or after disposal).
	 */
	protected Composite buttonBox;

	protected Button addButton;

	protected Button removeButton;

	protected Button updateButton;

	protected Button upButton;

	protected Button downButton;

	/**
	 * The selection listener.
	 */
	protected SelectionListener selectionListener;

	/**
	 * Creates a new list field editor
	 */
	public ListFieldEditor() {
	}

	/**
	 * Creates a list field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public ListFieldEditor(String name, String labelText, Composite parent) {
		init(name, labelText);
		createControl(parent);
	}

	/**
	 * Notifies that the Add button has been pressed.
	 */
	protected void addPressed() {
		setPresentsDefaultValue(false);
		String input = getNewInputObject();

		if (input != null) {
			int index = list.getSelectionIndex();
			if (index >= 0)
				list.add(input, index + 1);
			else
				list.add(input, 0);
			selectionChanged();
		}
	}

	/**
	 * Notifies that the Add button has been pressed.
	 */
	protected void updatePressed() {
		String input = getNewInputObject();

		if (input != null) {
			int index = list.getSelectionIndex();
			if (index >= 0)
				list.setItem(index, input);
			selectionChanged();
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void adjustForNumColumns(int numColumns) {
		Control control = getLabelControl();
		((GridData) control.getLayoutData()).horizontalSpan = numColumns;
		((GridData) list.getLayoutData()).horizontalSpan = numColumns - 1;
	}

	/**
	 * Creates the Add, Remove, Up, and Down button in the given button box.
	 * 
	 * @param buttonBox
	 *            the box for the buttons
	 */
	protected void createButtons(Composite buttonBox) {
		addButton = createPushButton(buttonBox, PREF_PAGE_ADDBUTTON_LABEL); //$NON-NLS-1$
		removeButton = createPushButton(buttonBox, PREF_PAGE_REMOVEBUTTON_LABEL); //$NON-NLS-1$
		updateButton = createPushButton(buttonBox, PREF_PAGE_UPDATEBUTTON_LABEL); //$NON-NLS-1$
		upButton = createPushButton(buttonBox, PREF_PAGE_UPBUTTON_LABEL); //$NON-NLS-1$
		downButton = createPushButton(buttonBox, PREF_PAGE_DOWNBUTTON_LABEL); //$NON-NLS-1$
	}

	/**
	 * Helper method to create a push button.
	 * 
	 * @param parent
	 *            the parent control
	 * @param key
	 *            the resource name used to supply the button's label text
	 */
	protected Button createPushButton(Composite parent, String label) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = convertVerticalDLUsToPixels(button, IDialogConstants.BUTTON_HEIGHT);
		int widthHint = convertHorizontalDLUsToPixels(button, IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		//		data.horizontalSpan = hs; use to have multiple buttons on the same
		// row
		button.setLayoutData(data);
		button.addSelectionListener(getSelectionListener());
		return button;
	}

	/**
	 * Creates a selection listener.
	 */
	public void createSelectionListener() {
		selectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Widget widget = event.widget;
				if (widget == addButton) {
					addPressed();
				} else if (widget == removeButton) {
					removePressed();
				} else if (widget == updateButton) {
					updatePressed();
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

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		Control control = getLabelControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns;
		control.setLayoutData(gd);

		list = getListControl(parent);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 80;
		gd.widthHint = 300;
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalSpan = numColumns - 1;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		list.setLayoutData(gd);

		buttonBox = getButtonBoxControl(parent);
		gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		buttonBox.setLayoutData(gd);
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoad() {
		if (list != null) {
			String s = getPreferenceStore().getString(getPreferenceName());
			String[] array = parseString(s);
			for (int i = 0; i < array.length; i++) {
				list.add(array[i]);
			}
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoadDefault() {
		if (list != null) {
			list.removeAll();
			String s = getPreferenceStore().getDefaultString(getPreferenceName());
			String[] array = parseString(s);
			for (int i = 0; i < array.length; i++) {
				list.add(array[i]);
			}
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doStore() {
		String s = createList(list.getItems());
		if (s != null)
			getPreferenceStore().setValue(getPreferenceName(), s);
	}

	/**
	 * Notifies that the Down button has been pressed.
	 */
	protected void downPressed() {
		swap(false);
	}

	/**
	 * Returns this field editor's button box containing the Add, Remove, Up,
	 * and Down button.
	 * 
	 * @param parent
	 *            the parent control
	 * @return the button box
	 */
	public Composite getButtonBoxControl(Composite parent) {
		if (buttonBox == null) {
			buttonBox = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			layout.numColumns = 1; // change value to have multiple buttons on
								   // the same row
			layout.marginWidth = 0;
			layout.marginHeight = 0;
			layout.verticalSpacing = 0;
			buttonBox.setLayout(layout);
			createButtons(buttonBox);
			buttonBox.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
					addButton = null;
					removeButton = null;
					upButton = null;
					downButton = null;
					buttonBox = null;
				}
			});

		} else {
			checkParent(buttonBox, parent);
		}

		selectionChanged();
		return buttonBox;
	}

	/**
	 * Returns this field editor's list control.
	 * 
	 * @param parent
	 *            the parent control
	 * @return the list control
	 */
	public List getListControl(Composite parent) {
		if (list == null) {
			list = new List(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
			list.addSelectionListener(getSelectionListener());
			list.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
					list = null;
				}
			});
		} else {
			checkParent(list, parent);
		}
		return list;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public int getNumberOfControls() {
		return 2;
	}

	/**
	 * Returns this field editor's selection listener. The listener is created
	 * if nessessary.
	 * 
	 * @return the selection listener
	 */
	protected SelectionListener getSelectionListener() {
		if (selectionListener == null)
			createSelectionListener();
		return selectionListener;
	}

	/**
	 * Returns this field editor's shell.
	 * <p>
	 * This method is internal to the framework; subclassers should not call
	 * this method.
	 * </p>
	 * 
	 * @return the shell
	 */
	protected Shell getShell() {
		if (addButton == null)
			return null;
		return addButton.getShell();
	}

	/**
	 * Notifies that the Remove button has been pressed.
	 */
	protected void removePressed() {
		setPresentsDefaultValue(false);
		int[] indices = list.getSelectionIndices();
		if (indices.length > 0) {
			list.remove(indices);
			selectionChanged();
		}
	}

	/**
	 * Notifies that the list selection has changed.
	 */
	protected void selectionChanged() {

		int index = list.getSelectionIndex();
		int size = list.getItemCount();

		removeButton.setEnabled(index >= 0);
		upButton.setEnabled(size > 1 && index > 0);
		downButton.setEnabled(size > 1 && index >= 0 && index < size - 1);
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public void setFocus() {
		if (list != null) {
			list.setFocus();
		}
	}

	/**
	 * Moves the currently selected item up or down.
	 * 
	 * @param up
	 *            <code>true</code> if the item should move up, and
	 *            <code>false</code> if it should move down
	 */
	protected void swap(boolean up) {
		setPresentsDefaultValue(false);
		int index = list.getSelectionIndex();
		int target = up ? index - 1 : index + 1;

		if (index >= 0) {
			String[] selection = list.getSelection();
			if (selection.length == 1) {
				list.remove(index);
				list.add(selection[0], target);
				list.setSelection(target);
			}
		}
		selectionChanged();
	}

	/**
	 * Notifies that the Up button has been pressed.
	 */
	protected void upPressed() {
		swap(true);
	}

	/**
	 * Combines the given list of items into a single string. This method is the
	 * converse of <code>parseString</code>.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 * 
	 * @param items
	 *            the list of items
	 * @return the combined string
	 * @see #parseString
	 */

	protected String createList(String[] items) {
		StringBuffer path = new StringBuffer("");

		for (int i = 0; i < items.length; i++) {
			path.append(URLEncoder.encode(items[i]));
			path.append(TomcatPluginResources.PREF_PAGE_LIST_SEPARATOR);
		}
		return path.toString();
	}

	/**
	 * Creates and returns a new item for the list.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 * 
	 * @return a new item
	 */
	protected String getNewInputObject() {

		String defaultValue = "";
		if (list.getSelection().length != 0) {
			defaultValue = list.getSelection()[0];
		}

		InputDialog dialog = new InputDialog(getShell(), "New Tomcat JVM paramater", "Enter a JVM parameter", defaultValue, null);
		String param = null;
		int dialogCode = dialog.open();
		if (dialogCode == InputDialog.OK) {
			param = dialog.getValue();
			if (param != null) {
				param = param.trim();
				if (param.length() == 0)
					return null;
			}
		}
		return param;
	}

	/**
	 * Splits the given string into a list of strings. This method is the
	 * converse of <code>createList</code>.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 * 
	 * @param stringList
	 *            the string
	 * @return an array of <code>String</code>
	 * @see #createList
	 */
	protected String[] parseString(String stringList) {
		StringTokenizer st = new StringTokenizer(stringList, TomcatPluginResources.PREF_PAGE_LIST_SEPARATOR); //$NON-NLS-1$
		ArrayList v = new ArrayList();
		while (st.hasMoreTokens()) {
			v.add(URLDecoder.decode(st.nextToken()));
		}
		return (String[]) v.toArray(new String[v.size()]);
	}

}