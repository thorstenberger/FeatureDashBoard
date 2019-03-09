package se.gu.featuredashboard.utils;

public class FeaturedashboardConstants {

	// Parse action in FeatureListView
	public static final String ACTION_TEXT = "Parse selected project";
	public static final String ACTION_TOOLTOP_TEXT = "Parses the selected project in the Project/Package Exlorer";
	
	// View ID's
	public static final String FEATUREFOLDER_VIEW_ID = "se.gu.featuredashboard.ui.views.FeatureFolderView";
	public static final String FEATUREFILE_VIEW_ID = "se.gu.featuredashboard.ui.views.FeatureFileView";
	public static final String FEATURELIST_VIEW_ID = "se.gu.featuredashboard.ui.views.FeatureListView";
	public static final String FEATUREMETRICS_VIEW_ID = "se.gu.featuredashboard.ui.views.FeatureMetricsView";
	public static final String PROJECTMETRICS_VIEW_ID = "se.gu.featuredashboard.ui.views.ProjectMetricsView";
	public static final String PACKAGE_EXPLORER = "org.eclipse.jdt.ui.PackageExplorer";
	public static final String PROJECT_EXPLORER = "org.eclipse.ui.navigator.ProjectExplorer";
	
	// Column names/tooltips for FeatureMetricsView
	public static final String FEATURETABLE_COLUMN_1_NAME = "Feature";
	public static final String FEATURETABLE_COLUMN_1_TOOLTIP = "The feature of interest";
	
	public static final String FEATURETABLE_COLUMN_2_NAME = "LOFC";
	public static final String FEATURETABLE_COLUMN_2_TOOLTIP = "How many annotated lines for this feature";
	
	public static final String FEATURETABLE_COLUMN_3_NAME = "NoFiA";
	public static final String FEATURETABLE_COLUMN_3_TOOLTIP = "How many in-file annotations for this feature";
	
	public static final String FEATURETABLE_COLUMN_4_NAME = "NoFoA";
	public static final String FEATURETABLE_COLUMN_4_TOOLTIP = "How many folder annotations for this feature";
	
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
	
	public static final String PROJECTTABLE_ID = "PROJECT_TABLE";

	// When selecting something that isn't a project in package/project explorer
	public static final String WRONG_SELECTION_MESSAGE = "Current selection is not a project";
	
	public static final String BUILDER_ID = "se.gu.featuredashboard.utils.Builder";
	
	// Used to determine which operation is responsible for graph node clicks
	public static final String NODE_ONCLICK_OPERATION_ID = "OnClickOperation";
	
}
