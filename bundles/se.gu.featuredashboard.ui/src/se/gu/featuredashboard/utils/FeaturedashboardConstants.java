package se.gu.featuredashboard.utils;

import org.eclipse.gef.geometry.planar.Dimension;

public class FeaturedashboardConstants {

	// Parse action in FeatureListView
	public static final String ACTION_TEXT = "Parse selected project";
	public static final String ACTION_TOOLTOP_TEXT = "Parses the selected project in the Project/Package Exlorer";
	
	// ID's
	public static final String HISTORY_VIEW_ID = "se.gu.featuredashboard.ui.views.HistoryView";
	public static final String FEATUREFOLDER_VIEW_ID = "se.gu.featuredashboard.ui.views.FeatureFolderView";
	public static final String FEATUREFILE_VIEW_ID = "se.gu.featuredashboard.ui.views.FeatureFileView";
	public static final String FEATURELIST_VIEW_ID = "se.gu.featuredashboard.ui.views.FeatureListView";
	public static final String FEATUREMETRICS_VIEW_ID = "se.gu.featuredashboard.ui.views.FeatureMetricsView";
	public static final String PROJECTMETRICS_VIEW_ID = "se.gu.featuredashboard.ui.views.ProjectMetricsView";
	public static final String PACKAGE_EXPLORER = "org.eclipse.jdt.ui.PackageExplorer";
	public static final String PROJECT_EXPLORER = "org.eclipse.ui.navigator.ProjectExplorer";
	public static final String BUILDER_ID = "se.gu.featuredashboard.utils.Builder";
	public static final String FEATURE_MARKER_ID = "se.gu.featuredashboard.ui.AnnotationMarker";
	public static final String REMOVEMARKERS_COMMAND_ID = "se.gu.featuredashboard.ui.commands.RemoveMarkers";
	
	public static final String FEATUREDASHBOARD_FOLDER_PATH = ".featuredashboard";
	
	public static final Dimension NODE_SIZE = new Dimension(75, 25);
	public static final Dimension NODE_SPACING = new Dimension(NODE_SIZE.width+30, NODE_SIZE.height+30);
	
	public static final String GRAPH_ID_KEY = "GRAPH_ID";
	public static final String NESTEDGRAPH_ID = "GRAPH_ID";
	
	// File name associated strings
	public static final String FEATUREFILE_FILE = "feature-file";
	public static final String FEATUREFOLDER_FILE = "feature-folder";
	public static final String VPFILE_FILE = "vp-files";
	public static final String VPFOLDER_FILE = "vp-folder";
	
	public static final String EXPORT_FILENAME = "FeatureMetrics.csv";
	
	public static final String SYNTAXERROR_DIALOG_TITLE = "Parsing Errors!";
	public static final String SYNTAXERROR_DIALOG_MESSAGE = "Errors occured when parsing resources";
	
	// Column names/tooltips for FeatureMetricsView
	public static final String FEATURETABLE_COLUMN_1_NAME = "Feature";
	public static final String FEATURETABLE_COLUMN_1_TOOLTIP = "The feature of interest";
	public static final int FEATURETABLE_COLUMN_1_SIZE = 175;
	
	public static final String FEATURETABLE_COLUMN_2_NAME = "LOFC";
	public static final String FEATURETABLE_COLUMN_2_TOOLTIP = "How many annotated lines for this feature";
	
	public static final String FEATURETABLE_COLUMN_3_NAME = "NoFiA";
	public static final String FEATURETABLE_COLUMN_3_TOOLTIP = "How many in-file annotations for this feature";
	
	public static final String FEATURETABLE_COLUMN_4_NAME = "NoFoA";
	public static final String FEATURETABLE_COLUMN_4_TOOLTIP = "How folders directly referensing this this feature";
	
	public static final String FEATURETABLE_COLUMN_5_NAME = "Tangling Degree";
	public static final String FEATURETABLE_COLUMN_5_TOOLTIP = "How many other features share the same artefact as this feature";
	
	public static final String FEATURETABLE_COLUMN_6_NAME = "Scattering Degree";
	public static final String FEATURETABLE_COLUMN_6_TOOLTIP = "Across how many artefacts is this feature implemented in";
	
	public static final String FEATURETABLE_COLUMN_7_NAME = "Max Nesting degree";
	public static final String FEATURETABLE_COLUMN_7_TOOLTIP = "Max nesting (folder) degree of annotations";
	
	public static final String FEATURETABLE_COLUMN_8_NAME = "Avg Nesting degree";
	public static final String FEATURETABLE_COLUMN_8_TOOLTIP = "Avg esting (folder) degree of annotations";
	
	public static final String FEATURETABLE_COLUMN_9_NAME = "Min Nesting degree";
	public static final String FEATURETABLE_COLUMN_9_TOOLTIP = "Min esting (folder) degree of annotations";
	
	public static final String FEATURETABLE_ID = "FEATURE_TABLE";
	
	// Column names/tooltips for ProjectMetricsView	
	public static final String PROJECTTABLE_COLUMN_1_NAME = "Project";
	public static final String PROJECTTABLE_COLUMN_1_TOOLTIP = "Current selected project";
	
	public static final String PROJECTTABLE_COLUMN_2_NAME = "Features";
	public static final String PROJECTTABLE_COLUMN_2_TOOLTIP = "How many distict features does this project contain";
	
	public static final String PROJECTTABLE_COLUMN_3_NAME = "Total LoFC";
	public static final String PROJECTTABLE_COLUMN_3_TOOLTIP = "Total lines of feature code";
	
	public static final String PROJECTTABLE_COLUMN_4_NAME = "Avg LoFC";
	public static final String PROJECTTABLE_COLUMN_4_TOOLTIP = "Average lines of feature code";
	
	public static final String PROJECTTABLE_COLUMN_5_NAME = "Avg Nesting Degree";
	public static final String PROJECTTABLE_COLUMN_5_TOOLTIP = "Average nesting degree";
	
	public static final String PROJECTTABLE_COLUMN_6_NAME = "Avg Scattering Degree";
	public static final String PROJECTTABLE_COLUMN_6_TOOLTIP = "Average scattering degree";
	
	public static final int FEATURELISTTABLE_SORT_COLUMN = 99;
	public static final int DOUBLECLICK_DURATION = 500;
	
	public static final String PROJECTTABLE_ID = "PROJECT_TABLE";

	// When selecting something that isn't a project in package/project explorer
	public static final String WRONG_SELECTION_MESSAGE = "Current selection is not a project";
	
	// Used to determine which operation is responsible for graph node clicks
	public static final String NODE_ONCLICK_OPERATION_ID = "OnClickOperation";
	
}
