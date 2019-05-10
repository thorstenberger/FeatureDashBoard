/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.utils;

import org.eclipse.gef.geometry.planar.Dimension;

public class FeaturedashboardConstants {

	// Parse action in FeatureListView
	public static final String ACTION_TEXT = "Parse selected project";
	public static final String ACTION_TOOLTOP_TEXT = "Parses the selected project in the Project/Package Exlorer";

	// ID's
	public static final String FEATUREFOLDER_VIEW_ID = "se.gu.featuredashboard.ui.views.FeatureFolderView";
	public static final String FEATURETANGLING_VIEW_ID = "se.gu.featuredashboard.ui.views.FeatureTanglingView";
	public static final String FEATUREFILE_VIEW_ID = "se.gu.featuredashboard.ui.views.FeatureFileView";
	public static final String FEATURELIST_VIEW_ID = "se.gu.featuredashboard.ui.views.FeatureListView";
	public static final String PACKAGE_EXPLORER = "org.eclipse.jdt.ui.PackageExplorer";
	public static final String PROJECT_EXPLORER = "org.eclipse.ui.navigator.ProjectExplorer";
	public static final String BUILDER_ID = "se.gu.featuredashboard.utils.Builder";
	public static final String FEATURE_MARKER_ID = "se.gu.featuredashboard.ui.AnnotationMarker";
	public static final String REMOVEMARKERS_COMMAND_ID = "se.gu.featuredashboard.ui.commands.RemoveMarkers";
	public static final String FEATURETREE_VIEW_ID = "se.gu.featuredashboard.ui.views.CommonFeaturesView";
	public static final String FEATURE_DASHBOARD_VIEW_ID = "se.gu.featuredashboard.ui.views.FeatureDashboardView";

	public static final String FEATUREDASHBOARD_PREFERENCES = "se.gu.featuredashboard.ui.mainPreferences.page";

	public static final Dimension NODE_SIZE = new Dimension(95, 45);
	public static final Dimension FOLDER_NODE_SPACING = new Dimension(NODE_SIZE.width + 100, NODE_SIZE.height + 30);

	public static final String GRAPH_ID_KEY = "GRAPH_ID";

	// File name associated strings
	public static final String FEATUREFILE_FILE = "feature-file";
	public static final String FEATUREFOLDER_FILE = "feature-folder";
	public static final String VPFILE_FILE = "vp-files";
	public static final String VPFOLDER_FILE = "vp-folder";

	public static final String SYNTAXERROR_DIALOG_TITLE = "Parsing Errors!";
	public static final String SYNTAXERROR_DIALOG_MESSAGE = "Errors occured when parsing resources";

	public static final int DOUBLECLICK_DURATION = 400;

	// When selecting something that isn't a project in package/project explorer
	public static final String WRONG_SELECTION_MESSAGE = "Current selection is not a project";

	public static final String EDGE_NORMAL_CSS = "-fx-opacity:0.4;";
	public static final String EDGE_HIGHLIGHTED_CSS = "-fx-opacity:1;";

	// Used to determine which operation is responsible for graph node clicks
	public static final String NODE_ONCLICK_OPERATION_ID = "NodeOnClickOperation";
	public static final String EDGE_ONCLICK_OPERATION_ID = "EdgeOnClickOperation";

}
