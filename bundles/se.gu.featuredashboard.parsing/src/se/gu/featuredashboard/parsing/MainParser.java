package se.gu.featuredashboard.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.internal.resources.Marker;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.Preferences;

import se.gu.featuredashboard.core.ProjectData_FeatureLocationDashboard;

/*
 * This class is for parsing whole project, finding feature model file (.cfr), all annotated files,
 * .feature-file, .feature-folder, .vp-file, .vp-folder, and updating feature location dashboard
 * project data except the project metrics
 */


public class MainParser extends Job {
	
	private final String FEATUREDASHBOARD_PREFERENCES_MAIN = "se.gu.featuredashboard.ui.mainPreferences.page";
	private final String VP_FOLDER_FILE = "vp-folder";
	private final String VP_FILE_FILE = "vp-file";
	private final String FEATURE_FILE_FILE = "feature-file";
	private final String FEATURE_FOLDER_FILE = "feature-folder";
	private final String CLAFER_FILE = "cfr";
	
	public static final List<String> DEFAULT_EXCLUDED_ANNOTATED_FILES_EXTENSIONS = Arrays.asList("pdf", "class", "DS_Store", "docx");
	public static final List<String> DEFAULT_EXCLUDED_FOLDERS_OF_ANNOTATED_FILES = Arrays.asList("bin");
	
	private IProject project;
	List<String> excludedFolders;
	List<String> excludedFileExtensions;
	
	private ProjectData_FeatureLocationDashboard projectData = new ProjectData_FeatureLocationDashboard();
	private IFile claferFile;
	
	private InFileAnnotationParser annotationParser = new InFileAnnotationParser();
	//private FeatureFileFolderParser featureFileFolderParser = new FeatureFileFolderParser();
	private ClaferFileParser claferParser = new ClaferFileParser(null);
	

	
 	private Logger logger = PlatformUI.getWorkbench().getService(org.eclipse.e4.core.services.log.Logger.class);

	public MainParser() {	
		super("FeatureLocationDashboard is parsing.");
	}

	public ProjectData_FeatureLocationDashboard getProjectData() {
		return this.projectData;
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		logger.info("Start parsing " + project.getName() + " for annotations");
		projectData = new ProjectData_FeatureLocationDashboard();
		projectData.setProject(project);

		if (project == null) {
			monitor.done();
			return Status.CANCEL_STATUS;
		}

		try {
			handleClaferFile(monitor);
			excludedFolders = getPreferencesExcludedFolders();
			excludedFileExtensions = getPreferencesExcludedFilesExtensions();
			setRegex_ofPreferences();
			handleResource(project, monitor);
			
			monitor.done();
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;

		} catch (RuntimeException e) {
			// So that we can remove the project from the ProjectStore, otherwise the user
			// has to close the IDE to re-parse the project
			logger.warn("Runtime exception occured: " + e.getMessage());
			monitor.done();
			return Status.CANCEL_STATUS;
		}
		
		return Status.OK_STATUS;
	}

	/**
	 * Finds the first Clafer file that exists in the project folder.
	 */
	private IFile findClaferFile() {
		claferFile = null;
		if(project==null)
			return null;
		
		try {
			for(IResource resource: project.members()) {
				if(resource instanceof IFile) {
					IFile theFile = (IFile) resource;
					if(theFile.getFileExtension()!=null && theFile.getFileExtension().equals(CLAFER_FILE)) {
						claferFile = theFile;
						return claferFile;
					}
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null; 
		
	}
	
	public IFile getClaferFile() {
		return claferFile;
	}
	
	private void handleResource(IContainer container, IProgressMonitor monitor) {
		if (monitor.isCanceled())
			return;

		try {
			Arrays.stream(container.members()).forEach(member -> {
				if (member instanceof IContainer) {
					if (!excludedFolders.contains(member.getProjectRelativePath().toString()))
						handleResource((IContainer) member, monitor);
				} else if (member instanceof IFile) {
					IFile theFile = (IFile) member;
					if(theFile.getFileExtension()==null || !excludedFileExtensions.contains(theFile.getFileExtension())) {
						if(isMappingFile(theFile))
							handleMappingFile(theFile, monitor);
						else
							handleAnnotatedFile(theFile, monitor);
					}
				}
			});
		} catch (CoreException e) {
			try {
				IMarker marker = container.createMarker(Marker.PROBLEM);
				marker.setAttribute(Marker.MESSAGE, e.getMessage() + ". Cancelling parsing of project.");
			} catch (CoreException e1) {
				logger.warn(e1.getMessage());
			}
			monitor.setCanceled(true);
		}
	}

	private void handleClaferFile(IProgressMonitor monitor) {
		if (monitor.isCanceled())
			return;
		
		claferFile = findClaferFile();
		if(claferFile==null)
			return;
		
		claferParser.setParsingFileAddress(claferFile);
		projectData.setFeatureModel(null);
		if(claferParser.hasValidClaferFile()) {
			projectData.setFeatureModel(claferParser.readParse());
		}
	}

	private void handleAnnotatedFile(IFile file, IProgressMonitor monitor) {
		if (monitor.isCanceled())
			return;
		
		try {
			projectData.addTraces(annotationParser.readParseAnnotations(new ArrayList<IFile>(Arrays.asList(file))));
			
		} 
		catch (Exception e) {
			/*
			try {
				IMarker marker = file.createMarker(Marker.PROBLEM);
				marker.setAttribute(Marker.MESSAGE, e.getMessage());
				marker.setAttribute(Marker.LINE_NUMBER, e.getLineNumber());
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
			*/
		}
	}
	
	private void handleMappingFile(IFile mappingFile, IProgressMonitor monitor) {
		if (monitor.isCanceled())
			return;

		try {
			projectData.addTraces(ParseMappingFile.readMappingFile(mappingFile,project));
			
		} catch (SyntaxException e) {
			try {
				IMarker marker = mappingFile.createMarker(Marker.PROBLEM);
				marker.setAttribute(Marker.MESSAGE, e.getMessage());
				marker.setAttribute(Marker.LINE_NUMBER, e.getLineNumber());
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
		}
	}

	private List<String> getPreferencesExcludedFilesExtensions() {
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
			return MainParser.DEFAULT_EXCLUDED_ANNOTATED_FILES_EXTENSIONS;
		
	}
	
	private List<String> getPreferencesExcludedFolders() {
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
			return MainParser.DEFAULT_EXCLUDED_FOLDERS_OF_ANNOTATED_FILES;	
	}
	
	/*
	 * Returns if the input file is a feature-to-resource mapping file.
	 * The mapping files are of extensions: vp-folder, vp-file, feature-file, feature-folder.
	 */
	private boolean isMappingFile(IResource resource) {
		if (!(resource instanceof IFile))
			return false;

		if (!resource.getName().contains("."))
			return false;

		return resource.getFileExtension().equals(FEATURE_FILE_FILE) 
			|| resource.getFileExtension().equals(FEATURE_FOLDER_FILE)
			|| resource.getFileExtension().equals(VP_FILE_FILE)
			|| resource.getFileExtension().equals(VP_FOLDER_FILE);
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
	
	public void updateProject(IProject project) {
		this.project = project;		
	}


}