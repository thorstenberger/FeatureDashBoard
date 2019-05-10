/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0.
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import se.gu.featuredashboard.parsing.InFileAnnotationParser;

/**
 * This is the class of preferences for Regex expressions used for annotated
 * files parser.
 *
 */
public class RegexPreferences extends PreferencePage implements IWorkbenchPreferencePage {

	private static final String DEFAULT_LINE_ANNOTATION_REGEX = InFileAnnotationParser.DEFAULT_LINE_ANNOTATION_REGEX;
	private static final String DEFAULT_BEGIN_ANNOTATION_REGEX = InFileAnnotationParser.DEFAULT_BEGIN_ANNOTATION_REGEX;
	private static final String DEFAULT_END_ANNOTATION_REGEX = InFileAnnotationParser.DEFAULT_END_ANNOTATION_REGEX;

	GridLayout gridLayout;
	Group grpSingleLineAnnotation;
	GridData gridData;
	List lstSingleLineAnnotation;
	Button btnRemoveSingleLineAnnotation;
	Button btnAddSingleLineAnnotation;
	Button btnRemoveMultipleLineAnnotation;
	Text txtEndLineAnnotationRegex;
	Button btnAddMultipleLineAnnotation;
	Label lblOneLineAnnotationRegex;
	Text txtOneLineAnnotationRegex;
	Group grpMultipleLineAnnotation;
	Table table;
	Label lblBeginAnnotationRegex;
	Text txtBeginLineAnnotationRegex;
	TableColumn column;
	Label lblEmpty;
	Label lblEndAnnotationRegex;
	TableItem item;

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		Preferences preferences = ConfigurationScope.INSTANCE.getNode("se.gu.featuredashboard.ui.regexPreference.page");
		Preferences sub1 = preferences.node("node1");

		if (!sub1.get("initialized", "default").equals("yes")) {
			sub1.put("initialized", "yes");
			sub1.put("SingleLineRegex0", DEFAULT_LINE_ANNOTATION_REGEX);
			sub1.put("BeginRegex0", DEFAULT_BEGIN_ANNOTATION_REGEX);
			sub1.put("EndRegex0", DEFAULT_END_ANNOTATION_REGEX);
		}
	}

	@Override
	protected Control createContents(Composite parent) {
		gridLayout = new GridLayout(1, false);
		parent.setLayout(gridLayout);

		grpSingleLineAnnotation = new Group(parent, SWT.NONE);
		grpSingleLineAnnotation.setText("One Line Annotation Regex:");
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		grpSingleLineAnnotation.setLayoutData(gridData);
		gridLayout = new GridLayout(3, false);
		grpSingleLineAnnotation.setLayout(gridLayout);

		lstSingleLineAnnotation = new List(grpSingleLineAnnotation, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		lstSingleLineAnnotation.setToolTipText("feature id must be in a bracket '[]'");
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 2;
		lstSingleLineAnnotation.setLayoutData(gridData);
		lstSingleLineAnnotation.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (lstSingleLineAnnotation.getSelectionIndex() != -1)
					btnRemoveSingleLineAnnotation.setEnabled(true);
				else
					btnRemoveSingleLineAnnotation.setEnabled(false);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		btnRemoveSingleLineAnnotation = new Button(grpSingleLineAnnotation, SWT.PUSH);
		btnRemoveSingleLineAnnotation.setText("Remove");
		btnRemoveSingleLineAnnotation.setEnabled(false);
		gridData = new GridData(GridData.FILL, GridData.BEGINNING, false, false);
		btnRemoveSingleLineAnnotation.setLayoutData(gridData);
		btnRemoveSingleLineAnnotation.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (lstSingleLineAnnotation.getSelectionIndex() != -1) {
					lstSingleLineAnnotation.remove(lstSingleLineAnnotation.getSelectionIndex());
					btnRemoveSingleLineAnnotation.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		lblOneLineAnnotationRegex = new Label(grpSingleLineAnnotation, SWT.NONE);
		lblOneLineAnnotationRegex.setText("Regex:");

		txtOneLineAnnotationRegex = new Text(grpSingleLineAnnotation, SWT.BORDER);
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		txtOneLineAnnotationRegex.setLayoutData(gridData);
		txtOneLineAnnotationRegex.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (txtOneLineAnnotationRegex.getText().isEmpty()) {
					btnAddSingleLineAnnotation.setEnabled(false);
				} else
					btnAddSingleLineAnnotation.setEnabled(true);
			}

		});

		btnAddSingleLineAnnotation = new Button(grpSingleLineAnnotation, SWT.PUSH);
		gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		btnAddSingleLineAnnotation.setEnabled(false);
		btnAddSingleLineAnnotation.setText("Add");
		btnAddSingleLineAnnotation.setLayoutData(gridData);
		btnAddSingleLineAnnotation.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!txtOneLineAnnotationRegex.getText().isEmpty()) {
					lstSingleLineAnnotation.add(txtOneLineAnnotationRegex.getText());
					txtOneLineAnnotationRegex.setText("");
					btnAddSingleLineAnnotation.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		grpMultipleLineAnnotation = new Group(parent, SWT.NONE);
		grpMultipleLineAnnotation.setText("Multiple Line Annotation Regex:");
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		grpMultipleLineAnnotation.setLayoutData(gridData);
		gridLayout = new GridLayout(3, false);
		grpMultipleLineAnnotation.setLayout(gridLayout);

		table = new Table(grpMultipleLineAnnotation, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		table.setLayoutData(gridData);
		table.setToolTipText("feature id must be in a bracket '[]'");
		table.setLinesVisible(false);
		table.setHeaderVisible(true);
		table.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (table.getSelectionIndex() != -1) {
					btnRemoveMultipleLineAnnotation.setEnabled(true);
				} else
					btnRemoveMultipleLineAnnotation.setEnabled(false);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		updateListAndTable();

		btnRemoveMultipleLineAnnotation = new Button(grpMultipleLineAnnotation, SWT.PUSH);
		btnRemoveMultipleLineAnnotation.setText("Remove");
		btnRemoveMultipleLineAnnotation.setEnabled(false);
		gridData = new GridData(GridData.FILL, GridData.BEGINNING, false, false);
		btnRemoveMultipleLineAnnotation.setLayoutData(gridData);
		btnRemoveMultipleLineAnnotation.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (table.getSelectionIndex() != -1) {
					table.remove(table.getSelectionIndex());
					btnRemoveMultipleLineAnnotation.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		lblBeginAnnotationRegex = new Label(grpMultipleLineAnnotation, SWT.NONE);
		lblBeginAnnotationRegex.setText("Begin Regex:");

		txtBeginLineAnnotationRegex = new Text(grpMultipleLineAnnotation, SWT.BORDER);
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		txtBeginLineAnnotationRegex.setLayoutData(gridData);
		txtBeginLineAnnotationRegex.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!txtBeginLineAnnotationRegex.getText().isEmpty()
						&& !txtEndLineAnnotationRegex.getText().isEmpty()) {
					btnAddMultipleLineAnnotation.setEnabled(true);
				} else
					btnAddMultipleLineAnnotation.setEnabled(false);
			}

		});

		lblEmpty = new Label(grpMultipleLineAnnotation, SWT.NONE);
		lblEmpty.setText("");

		lblEndAnnotationRegex = new Label(grpMultipleLineAnnotation, SWT.NONE);
		lblEndAnnotationRegex.setText("End Regex:");

		txtEndLineAnnotationRegex = new Text(grpMultipleLineAnnotation, SWT.BORDER);
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		txtEndLineAnnotationRegex.setLayoutData(gridData);
		txtEndLineAnnotationRegex.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!txtBeginLineAnnotationRegex.getText().isEmpty()
						&& !txtEndLineAnnotationRegex.getText().isEmpty()) {
					btnAddMultipleLineAnnotation.setEnabled(true);
				} else
					btnAddMultipleLineAnnotation.setEnabled(false);
			}

		});

		btnAddMultipleLineAnnotation = new Button(grpMultipleLineAnnotation, SWT.PUSH);
		gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		btnAddMultipleLineAnnotation.setText("Add");
		btnAddMultipleLineAnnotation.setEnabled(false);
		btnAddMultipleLineAnnotation.setLayoutData(gridData);
		btnAddMultipleLineAnnotation.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				item = new TableItem(table, SWT.NONE);
				item.setText(0, txtBeginLineAnnotationRegex.getText());
				item.setText(1, txtEndLineAnnotationRegex.getText());

				table.getColumn(0).pack();
				table.getColumn(1).pack();

				btnAddMultipleLineAnnotation.setEnabled(false);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		return new Composite(parent, SWT.NONE);
	}

	@Override
	public void performApply() {
		Preferences preferences = ConfigurationScope.INSTANCE.getNode("se.gu.featuredashboard.ui.regexPreference.page");
		Preferences sub1 = preferences.node("node1");
		try {
			sub1.clear();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

		sub1.put("initialized", "yes");

		for (int i = 0; i < lstSingleLineAnnotation.getItems().length; i++) {
			sub1.put("SingleLineRegex" + i, lstSingleLineAnnotation.getItems()[i]);
		}

		for (int i = 0; i < table.getItems().length; i++) {
			sub1.put("BeginRegex" + i, table.getItems()[i].getText(0));
		}

		for (int i = 0; i < table.getItems().length; i++) {
			sub1.put("EndRegex" + i, table.getItems()[i].getText(1));
		}

	}

	@Override
	public boolean performOk() {
		performApply();
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		lstSingleLineAnnotation.removeAll();
		lstSingleLineAnnotation.setTopIndex(0);
		lstSingleLineAnnotation.add(DEFAULT_LINE_ANNOTATION_REGEX);

		table.removeAll();
		table.setTopIndex(0);
		String[] titles = { "Begin Regex", "End Regex" };
		for (int j = 0; j < titles.length; j++) {
			column = new TableColumn(table, SWT.NONE);
			column.setText(titles[j]);
		}

		item = new TableItem(table, SWT.NONE);
		item.setText(0, DEFAULT_BEGIN_ANNOTATION_REGEX);
		item.setText(1, DEFAULT_END_ANNOTATION_REGEX);
		table.getColumn(0).pack();
		table.getColumn(1).pack();
	}

	private void updateListAndTable() {

		Preferences preferences = ConfigurationScope.INSTANCE.getNode("se.gu.featuredashboard.ui.regexPreference.page");
		Preferences sub1 = preferences.node("node1");

		lstSingleLineAnnotation.removeAll();
		lstSingleLineAnnotation.setTopIndex(0);
		int i = 0;

		while (true) {
			String singleLineRegexKey = "SingleLineRegex" + i;
			String singleLineRegexValue = sub1.get(singleLineRegexKey, "default");
			if (singleLineRegexValue.equals("default"))
				break;
			lstSingleLineAnnotation.add(singleLineRegexValue);
			i++;
		}

		table.removeAll();
		table.setTopIndex(0);

		String[] titles = { "Begin Regex", "End Regex" };
		for (int j = 0; j < titles.length; j++) {
			column = new TableColumn(table, SWT.NONE);
			column.setText(titles[j]);
		}

		i = 0;
		while (true) {
			String beginRegexKey = "BeginRegex" + i;
			String endRegexKey = "EndRegex" + i;
			String beginRegexValue = sub1.get(beginRegexKey, "default");
			String endRegexValue = sub1.get(endRegexKey, "default");
			if (beginRegexValue.equals("default") || endRegexValue.equals("default"))
				break;
			item = new TableItem(table, SWT.NONE);
			item.setText(0, beginRegexValue);
			item.setText(1, endRegexValue);
			i++;
		}
		table.getColumn(0).pack();
		table.getColumn(1).pack();
	}

}