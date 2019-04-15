/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.ui.preferences;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import se.gu.featuredashboard.parsing.ProjectParser;

/**
 * This is the class of preferences for specifying excluded files and folders
 * from searching for annotated files in the projects.
 *
 */
public class MainPreferences extends PreferencePage implements IWorkbenchPreferencePage {

	List lstOutputs;
	List lstExtensions;

	Button btnRemoveExtension;
	Button btnAddExtension;
	Button btnRemoveOutput;
	Button btnAddOutput;

	private static final java.util.List<String> DEFAULT_EXCLUDED_EXTENSIONS = ProjectParser.DEFAULT_EXCLUDED_ANNOTATED_FILES_EXTENSIONS;
	private static final java.util.List<String> DEFAULT_EXCLUDED_FOLDERS = ProjectParser.DEFAULT_EXCLUDED_FOLDERS_OF_ANNOTATED_FILES;

	@Override
	public void init(IWorkbench workbench) {
		Preferences preferences = ConfigurationScope.INSTANCE.getNode("se.gu.featuredashboard.ui.mainPreferences.page");
		Preferences sub1 = preferences.node("node1");

		if (!sub1.get("initialized", "default").equals("yes")) {
			sub1.put("initialized", "yes");

			for (int i = 0; i < DEFAULT_EXCLUDED_EXTENSIONS.size(); i++) {
				sub1.put("Extension" + i, DEFAULT_EXCLUDED_EXTENSIONS.get(i));
			}
			for (int i = 0; i < DEFAULT_EXCLUDED_FOLDERS.size(); i++) {
				sub1.put("Output" + i, DEFAULT_EXCLUDED_FOLDERS.get(i));
			}
		}
	}

	@Override
	protected Control createContents(Composite parent) {
		GridLayout gridLayout = new GridLayout(1, false);
		parent.setLayout(gridLayout);

		Group grpExtensions = new Group(parent, SWT.NONE);
		grpExtensions.setText("Excluded Annotated File Extensions:");
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		grpExtensions.setLayoutData(gridData);
		gridLayout = new GridLayout(3, false);
		grpExtensions.setLayout(gridLayout);

		lstExtensions = new List(grpExtensions, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 2;
		lstExtensions.setLayoutData(gridData);
		lstExtensions.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (lstExtensions.getSelectionIndex() != -1)
					btnRemoveExtension.setEnabled(true);
				else
					btnRemoveExtension.setEnabled(false);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		btnRemoveExtension = new Button(grpExtensions, SWT.PUSH);
		btnRemoveExtension.setText("Remove");
		btnRemoveExtension.setEnabled(false);
		gridData = new GridData(GridData.FILL, GridData.BEGINNING, false, false);
		btnRemoveExtension.setLayoutData(gridData);
		btnRemoveExtension.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (lstExtensions.getSelectionIndex() != -1) {
					lstExtensions.remove(lstExtensions.getSelectionIndex());
					btnRemoveExtension.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Label lblExtension = new Label(grpExtensions, SWT.NONE);
		lblExtension.setText("New File Extension:");

		Text txtExtension = new Text(grpExtensions, SWT.BORDER);
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		txtExtension.setLayoutData(gridData);
		txtExtension.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (txtExtension.getText().isEmpty()) {
					btnAddExtension.setEnabled(false);
				} else
					btnAddExtension.setEnabled(true);
			}

		});

		btnAddExtension = new Button(grpExtensions, SWT.PUSH);
		gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		btnAddExtension.setEnabled(false);
		btnAddExtension.setText("Add");
		btnAddExtension.setLayoutData(gridData);
		btnAddExtension.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!txtExtension.getText().isEmpty()) {
					lstExtensions.add(txtExtension.getText());
					txtExtension.setText("");
					btnAddExtension.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Group grpOutputs = new Group(parent, SWT.NONE);
		grpOutputs.setText("Excluded Folders (Project Relative Path):");
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		grpOutputs.setLayoutData(gridData);
		gridLayout = new GridLayout(3, false);
		grpOutputs.setLayout(gridLayout);

		lstOutputs = new List(grpOutputs, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 2;
		lstOutputs.setLayoutData(gridData);
		lstOutputs.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (lstOutputs.getSelectionIndex() != -1)
					btnRemoveOutput.setEnabled(true);
				else
					btnRemoveOutput.setEnabled(false);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		updateLists();

		btnRemoveOutput = new Button(grpOutputs, SWT.PUSH);
		btnRemoveOutput.setText("Remove");
		btnRemoveOutput.setEnabled(false);
		gridData = new GridData(GridData.FILL, GridData.BEGINNING, false, false);
		btnRemoveOutput.setLayoutData(gridData);
		btnRemoveOutput.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (lstOutputs.getSelectionIndex() != -1) {
					lstOutputs.remove(lstOutputs.getSelectionIndex());
					btnRemoveOutput.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Label lblOutput = new Label(grpOutputs, SWT.NONE);
		lblOutput.setText("New Folder:");

		Text txtOutput = new Text(grpOutputs, SWT.BORDER);
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		txtOutput.setLayoutData(gridData);
		txtOutput.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (txtOutput.getText().isEmpty()) {
					btnAddOutput.setEnabled(false);
				} else
					btnAddOutput.setEnabled(true);
			}

		});

		btnAddOutput = new Button(grpOutputs, SWT.PUSH);
		gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		btnAddOutput.setEnabled(false);
		btnAddOutput.setText("Add");
		btnAddOutput.setLayoutData(gridData);
		btnAddOutput.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!txtOutput.getText().isEmpty()) {
					lstOutputs.add(txtOutput.getText());
					txtOutput.setText("");
					btnAddOutput.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		return new Composite(parent, SWT.NONE);
	}

	@Override
	public void performApply() {
		Preferences preferences = ConfigurationScope.INSTANCE.getNode("se.gu.featuredashboard.ui.mainPreferences.page");
		Preferences sub1 = preferences.node("node1");

		try {
			sub1.clear();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

		sub1.put("initialized", "yes");

		for (int i = 0; i < lstExtensions.getItems().length; i++) {
			sub1.put("Extension" + i, lstExtensions.getItems()[i]);
		}

		for (int i = 0; i < lstOutputs.getItems().length; i++) {
			sub1.put("Output" + i, lstOutputs.getItems()[i]);
		}
	}

	@Override
	public boolean performOk() {
		performApply();
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		lstExtensions.removeAll();
		lstExtensions.setTopIndex(0);
		DEFAULT_EXCLUDED_EXTENSIONS.forEach(extension -> {
			lstExtensions.add(extension);
		});
		DEFAULT_EXCLUDED_FOLDERS.forEach(folder -> {
			lstOutputs.add(folder);
		});
	}

	private void updateLists() {
		Preferences preferences = ConfigurationScope.INSTANCE.getNode("se.gu.featuredashboard.ui.mainPreferences.page");
		Preferences sub1 = preferences.node("node1");

		lstExtensions.removeAll();
		lstExtensions.setTopIndex(0);
		int i = 0;

		while (true) {
			String extensionKey = "Extension" + i;
			String extensionValue = sub1.get(extensionKey, "default");
			if (extensionValue.equals("default"))
				break;
			lstExtensions.add(extensionValue);
			i++;
		}

		lstOutputs.removeAll();
		lstOutputs.setTopIndex(0);
		i = 0;

		while (true) {
			String outputKey = "Output" + i;
			String outputValue = sub1.get(outputKey, "default");
			if (outputValue.equals("default"))
				break;
			lstOutputs.add(outputValue);
			i++;
		}

	}

}
