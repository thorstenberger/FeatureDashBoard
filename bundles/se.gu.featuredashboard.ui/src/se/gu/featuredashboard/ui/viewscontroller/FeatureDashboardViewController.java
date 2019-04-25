/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.ui.viewscontroller;


import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import Listeners.IUpdateInformationListener;
import Listeners.ParseChangeListener;
import metrics.Feature_ProjectMetrics;
import se.gu.featuredashboard.core.ProjectData_FeatureLocationDashboard;
import se.gu.featuredashboard.core.WorkspaceData_FeatureLocationDashboard;
import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.location.FeatureLocation;
import se.gu.featuredashboard.parsing.MainParser;
import se.gu.featuredashboard.ui.views.FeatureDashboardView;

/**
 * This class is the controller of FeatureDashboarView and is used for communications
 * between different classes with that view.
 *
 */
public class FeatureDashboardViewController implements IUpdateInformationListener {

	private static FeatureDashboardViewController INSTANCE;
	public static FeatureDashboardViewController getInstance() {		
		if (INSTANCE == null) {
			synchronized (FeatureDashboardViewController.class) {
				if (INSTANCE == null) {
					INSTANCE = new FeatureDashboardViewController();
				}
			}
		}
		return INSTANCE;
	}
	
	private String parsingMessages="";
	
	private IProject project;
	private ProjectData_FeatureLocationDashboard selectedProjectData = new ProjectData_FeatureLocationDashboard();
	private MainParser mainParser = new MainParser();
	
	private FeatureDashboardViewController() {
		mainParser.setUser(true);
		mainParser.addJobChangeListener(new ParseChangeListener(Arrays.asList(this)));
	}

	/*
	private ProjectParser projectParser;
	private InFileAnnotationParser annotationParser;
	private FeatureFileFolderParser featureFileFolderParser;
	private ClaferFileParser claferParser;

	private FeatureDashboardViewController(){

		
		projectParser = new ProjectParser(project, 
				getExcludedAnnotatedFilesExtensions_ofPreferences(),
				getExcludedFoldersOfAnnotatedFiles_ofPreferences());
		
		annotationParser = new InFileAnnotationParser();
		setRegex_ofPreferences();
		featureFileFolderParser = new FeatureFileFolderParser(projectParser.getFeatureFileAddress(), 
				projectParser.getAllFolders(), projectParser.getAllFiles());
		claferParser = new ClaferFileParser(projectParser.getClaferAddress());
			
		updateSelectedProjectData();        
	}
	
	private void setRegex_ofPreferences() {
		Preferences preferences = ConfigurationScope.INSTANCE.getNode("se.gu.featuredashboard.ui.regexPreference.page");
		Preferences sub1 = preferences.node("node1");
		
		if(sub1.get("initialized", "default").equals("yes")) {		
			ArrayList<String> singleLineRegexList = new ArrayList<String>();
			int i=0;		
			while(true) {
				String singleLineRegexKey = "SingleLineRegex"+i;
				String singleLineRegexValue = sub1.get(singleLineRegexKey, "default");
				if(singleLineRegexValue.equals("default"))
					break;
				singleLineRegexList.add(singleLineRegexValue);
				i++;
			}
			
			ArrayList<String> beginRegexList = new ArrayList<String>();
			ArrayList<String> endRegexList = new ArrayList<String>();
			i=0;
			while(true) {
				String beginRegexKey = "BeginRegex"+i;
				String endRegexKey = "EndRegex"+i;
				String beginRegexValue = sub1.get(beginRegexKey, "default");
				String endRegexValue = sub1.get(endRegexKey, "default");
				if(beginRegexValue.equals("default") || endRegexValue.equals("default") )
					break;
				beginRegexList.add(beginRegexValue);
				endRegexList.add(endRegexValue);
				i++;
			}
			
			annotationParser.setRegex_LineAnnotation(singleLineRegexList);
			annotationParser.setRegex_beginAnnotation(beginRegexList);
			annotationParser.setRegex_endAnnotation(endRegexList);	
		}
		else {
			annotationParser.setRegex_LineAnnotation(Arrays.asList(InFileAnnotationParser.DEFAULT_LINE_ANNOTATION_REGEX));
			annotationParser.setRegex_beginAnnotation(Arrays.asList(InFileAnnotationParser.DEFAULT_BEGIN_ANNOTATION_REGEX));
			annotationParser.setRegex_endAnnotation(Arrays.asList(InFileAnnotationParser.DEFAULT_END_ANNOTATION_REGEX));
		}
	}

	private List<String> getExcludedAnnotatedFilesExtensions_ofPreferences() {
		Preferences preferences = ConfigurationScope.INSTANCE.getNode("se.gu.featuredashboard.ui.mainPreferences.page");
		Preferences sub1 = preferences.node("node1");
		
		if(sub1.get("initialized", "default").equals("yes")) {	
			ArrayList<String> annotatedFilesExtensions = new ArrayList<String>();
			int i=0;
			
			while(true) {
				String extensionKey = "Extension"+i;
				String extensionValue = sub1.get(extensionKey, "default");
				if(extensionValue.equals("default"))
					break;
				annotatedFilesExtensions.add(extensionValue);
				i++;
			}
			return annotatedFilesExtensions;
		}
		else 
			return projectParser.DEFAULT_EXCLUDED_ANNOTATED_FILES_EXTENSIONS;
		
	}
	
	private List<String> getExcludedFoldersOfAnnotatedFiles_ofPreferences() {
		Preferences preferences = ConfigurationScope.INSTANCE.getNode("se.gu.featuredashboard.ui.mainPreferences.page");
		Preferences sub1 = preferences.node("node1");
		
		if(sub1.get("initialized", "default").equals("yes")) {	
			ArrayList<String> excludedExtentions = new ArrayList<String>();
			int i=0;
			
			while(true) {
				String folderKey = "Output"+i;
				String folderValue = sub1.get(folderKey, "default");
				if(folderValue.equals("default"))
					break;
				excludedExtentions.add(folderValue);
				i++;
			}
			return excludedExtentions;
		}
		else 
			return projectParser.DEFAULT_EXCLUDED_FOLDERS_OF_ANNOTATED_FILES;	
	}
	
	public void updateSelectedProjectData(){
		selectedProjectData.clearAllTraces();
		selectedProjectData.setProject(project);
		
		// feature model data should be parsed before than traces
		// it is for accuracy of 'featuresNotInFeatureModel' in FeatureLocationDashboard
		claferParser.setParsingFileAddress(projectParser.getClaferAddress());
		selectedProjectData.setFeatureModel(null);
		if(claferParser.hasValidClaferFile()) {
			selectedProjectData.setFeatureModel(claferParser.readParse());
		}
		if(!claferParser.getParsingMessage().isEmpty())
			parsingMessages+= claferParser.getParsingMessage()+"\n";
		
		selectedProjectData.addTraces(annotationParser.readParseAnnotations(projectParser.getAllAnnotatedFiles()));
		if(!annotationParser.getParsingMessage().isEmpty())
			parsingMessages+= annotationParser.getParsingMessage()+"\n";
		
		featureFileFolderParser.setParsingInfo(projectParser.getFeatureFileAddress(), 
				projectParser.getAllFolders(),projectParser.getAllFiles());
		if(featureFileFolderParser.hasValidFeatureFile()){
			selectedProjectData.addTraces(featureFileFolderParser.readParse());
		}
		if(!featureFileFolderParser.getParsingMessage().isEmpty())
			parsingMessages+= featureFileFolderParser.getParsingMessage()+"\n";

		featureFileFolderParser.setParsingInfo(projectParser.getFeatureFolderAddress(), 
				projectParser.getAllFolders(),projectParser.getAllFiles());
		if(featureFileFolderParser.hasValidFeatureFolder()){
			selectedProjectData.addTraces(featureFileFolderParser.readParse());
		}
		if(!featureFileFolderParser.getParsingMessage().isEmpty())
			parsingMessages+= featureFileFolderParser.getParsingMessage()+"\n";
	}
	*/
	
	public void updateController(IProject project) {
		this.project = project;
		parsingMessages="";
		
		/*
		projectParser = new ProjectParser(project, 
				getExcludedAnnotatedFilesExtensions_ofPreferences(),
				getExcludedFoldersOfAnnotatedFiles_ofPreferences());
		setRegex_ofPreferences();
		*/
		
		mainParser.updateProject(project);
		mainParser.schedule();
		
		//updateSelectedProjectData();
	}

	
	private void updateWorkspaceData() {
		if (selectedProjectData.getProject() != null) {
			WorkspaceData_FeatureLocationDashboard.getInstance().setProjectData(selectedProjectData);
		}
	}

	public List<ProjectData_FeatureLocationDashboard> getWorkspaceData() {
		return WorkspaceData_FeatureLocationDashboard.getInstance().getWorkspaceData();
	}

	public IProject getProject() {
		return project;
	}

	public boolean hasFeatureModel() {
		return (mainParser.getClaferFile()!=null);
	}
	
	public IFile getFeatureModel() {
		return mainParser.getClaferFile();
	}
	
	public String getParsingMessage() {
		return parsingMessages;
	}
	
	public List<Feature> getFeaturesNotInFeatureModel(){
		return selectedProjectData.getFeaturesNotInFeatureModel();
	}

	public List<Feature> getRootFeaturesOfFeatureModel(){
		return selectedProjectData.getRootFeaturesOfFeatureModel();

	}
	
	public List<Feature>  getAllFeaturesOfFeatureModel(){
		return selectedProjectData.getFeaturesOfFeatureModel();
	}

	public List<IResource> getNotExistentResources(){
		return selectedProjectData.getNotExistentResources();
	}
	
	public List<FeatureLocation> getTraces(List<Object> features, List<Object> projectRelativePaths){
		return selectedProjectData.getTraces(features, projectRelativePaths);
	}
	
	public List<Feature_ProjectMetrics> getProjectMetrics(){
		return selectedProjectData.getProjectMetrics();
	}

	public List<FeatureLocation> getAllLocations() {
		return selectedProjectData.getAllLocations();
	}

	@Override
	public void parsingComplete() {
		Display.getDefault().asyncExec(() -> {
			selectedProjectData = mainParser.getProjectData();
			updateWorkspaceData();
			FeatureDashboardView view = (FeatureDashboardView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("se.gu.featuredashboard.ui.views.FeatureDashboardView");
			GeneralViewsController.getInstance().projectUpdated();
			if(view!=null) {
				view.updateFeatureModelTab();
				view.updateResourcesTab();
				view.updateTracesTab();
			}	
		});
	}
}
