/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.featuremodel.Tuple;
import se.gu.featuredashboard.model.location.ProjectData;
import se.gu.featuredashboard.ui.listeners.IProjectSelectionListener;
import se.gu.featuredashboard.ui.viewscontroller.FeatureDashboardViewController;
import se.gu.featuredashboard.ui.viewscontroller.GeneralViewsController;

public class CommonFeaturesView extends ViewPart implements IProjectSelectionListener {

	private FeatureDashboardViewController controller = FeatureDashboardViewController.getInstance();
	private GeneralViewsController viewController = GeneralViewsController.getInstance();

	private Table table;

	private Color present = Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
	private Color absent = Display.getDefault().getSystemColor(SWT.COLOR_RED);

	@Override
	public void createPartControl(Composite parent) {
		viewController.registerProjectSelectionListener(this);

		table = new Table(parent, SWT.RESIZE | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		table.setLayoutData(gridData);
		// table.setSize(Display.getDefault().getClientArea().width, 2000);
		initCommonFeaturesTable();
	}

	private void initCommonFeaturesTable() {
		Arrays.stream(table.getColumns()).forEach(column -> {
			column.dispose();
		});
		Arrays.stream(table.getItems()).forEach(row -> {
			row.dispose();
		});
		// All features in a project
		List<Tuple<IProject, Set<Feature>>> projectToFeatures = new ArrayList<>();
		// Unique features across all projects
		Set<Feature> workspaceFeaturesSet = new HashSet<>();

		List<ProjectData> workspaceData = controller.getWorkspaceData();
		
		if (workspaceData.size() == 1)
			return;

		workspaceData.forEach(projectData -> {
			Set<Feature> projectFeatures = new HashSet<>();
			
			projectData.getAllLocations().forEach(location -> {
				projectFeatures.add(location.getFeature());
				workspaceFeaturesSet.add(location.getFeature());
			});

			projectToFeatures.add(new Tuple<>(projectData.getProject(), projectFeatures));
		});

		TableColumn tableColumn = new TableColumn(table, SWT.LEFT);
		tableColumn.setText("Feature");

		for (int column = 0; column < projectToFeatures.size(); column++) {
			TableColumn projectColumn = new TableColumn(table, SWT.LEFT);
			projectColumn.setText(projectToFeatures.get(column).getLeft().getName());
		}
		
		List<Feature> workspaceFeaturesList = workspaceFeaturesSet.stream().collect(Collectors.toList());

		for (int row = 0; row < workspaceFeaturesList.size(); row++) {
			TableItem item = new TableItem(table, SWT.NONE);
			for (int column = 0; column < projectToFeatures.size() + 1; column++) {
				if (column == 0) {
					item.setText(column, workspaceFeaturesList.get(row).getFeatureID());
				} else {
					if (projectToFeatures.get(column - 1).getRight().contains(workspaceFeaturesList.get(row))) {
						item.setBackground(column, present);
					} else {
						item.setBackground(column, absent);
					}
				}
			}
		}

		// Pack the columns
		for (int i = 0, n = table.getColumnCount(); i < n; i++) {
			table.getColumn(i).pack();
		}

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void dispose() {
		super.dispose();
		viewController.removeProjectSelectionListener(this);
	}

	@Override
	public void updateProjectSelected() {
		initCommonFeaturesTable();
	}

}
