package se.gu.featuredashboard.ui.viewscontroller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.osgi.service.prefs.Preferences;

import se.gu.featuredashboard.core.FeatureLocationDashboard;
import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.location.FeatureLocation;
import se.gu.featuredashboard.parsing.ClaferFileParser;
import se.gu.featuredashboard.parsing.FeatureFileFolderParser;
import se.gu.featuredashboard.parsing.InFileAnnotationParser;
import se.gu.featuredashboard.parsing.ProjectParser;

public class FeatureDashboardViewController {

	private IProject project;
	
	private String parsingMessages="";
	
	private FeatureLocationDashboard data = new FeatureLocationDashboard();

	private ProjectParser projectParser;
	private InFileAnnotationParser annotationParser;
	private FeatureFileFolderParser featureFileFolderParser;
	private ClaferFileParser claferParser;

	public FeatureDashboardViewController(IProject project){
		this.project = project;

		projectParser = new ProjectParser(project, getAnnotatedFilesExtensions_ofPreferences());
		annotationParser = new InFileAnnotationParser();
		setRegex_ofPreferences();
		featureFileFolderParser = new FeatureFileFolderParser(projectParser.getFeatureFileAddress());
		claferParser = new ClaferFileParser(projectParser.getClaferAddress());
			
		updateData();        
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

	private List<String> getAnnotatedFilesExtensions_ofPreferences() {
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
			return projectParser.DEFAULT_ANNOTATED_FILES_EXTENSIONS;
		
	}
	
	public void updateData(){
		data.clearAllTraces();
		
		// feature model data should be parsed before than traces
		// it is for accuracy of 'featuresNotInFeatureModel' in FeatureLocationDashboard
		claferParser.setParsingFileAddress(projectParser.getClaferAddress());
		data.setFeatureModel(null);
		if(claferParser.hasValidClaferFile()) {
			data.setFeatureModel(claferParser.readParse());
		}
		if(!claferParser.getParsingMessage().isEmpty())
			parsingMessages+= claferParser.getParsingMessage()+"\n";
		
		data.addTraces(annotationParser.readParseAnnotations(projectParser.getAllFiles()));
		if(!annotationParser.getParsingMessage().isEmpty())
			parsingMessages+= annotationParser.getParsingMessage()+"\n";
		
		featureFileFolderParser.setParsingFileAddress(projectParser.getFeatureFileAddress());
		if(featureFileFolderParser.hasValidFeatureFile()){
			data.addTraces(featureFileFolderParser.readParse());
		}
		if(!featureFileFolderParser.getParsingMessage().isEmpty())
			parsingMessages+= featureFileFolderParser.getParsingMessage()+"\n";

		featureFileFolderParser.setParsingFileAddress(projectParser.getFeatureFolderAddress());
		if(featureFileFolderParser.hasValidFeatureFolder()){
			data.addTraces(featureFileFolderParser.readParse());
		}
		if(!featureFileFolderParser.getParsingMessage().isEmpty())
			parsingMessages+= featureFileFolderParser.getParsingMessage()+"\n";
	}
	
	public void updateController(IProject project) {
		this.project = project;
		parsingMessages="";
		
		projectParser = new ProjectParser(project, getAnnotatedFilesExtensions_ofPreferences());
		setRegex_ofPreferences();
		
		updateData(); 
	}

	public IProject getProject() {
		return project;
	}

	public boolean hasFeatureModel() {
		return (projectParser.getClaferAddress()!=null);
	}
	
	public boolean hasFeatureFileTraces() {
		return projectParser.getFeatureFileAddress()!=null;
	}
	
	public boolean hasFeatureFolderTraces() {
		return projectParser.getFeatureFolderAddress()!=null;
	}
	
	public String getParsingMessage() {
		return parsingMessages;
	}
	
	public List<Feature> getFeaturesNotInFeatureModel(){
		return data.getFeaturesNotInFeatureModel();
	}

	public List<Feature> getRootFeaturesOfFeatureModel(){
		return data.getRootFeaturesOfFeatureModel();
	}
	
	public List<Feature>  getAllFeaturesOfFeatureModel(){
		return data.getFeaturesOfFeatureModel();
	}

	public List<IResource> getNotExistentResources(){
		return data.getNotExistentResources();
	}
	
	public List<FeatureLocation> getTraces(List<Object> featureIDs, List<Object> projectRelativePaths){
		return data.getTraces(featureIDs, projectRelativePaths);
	}
	
}
