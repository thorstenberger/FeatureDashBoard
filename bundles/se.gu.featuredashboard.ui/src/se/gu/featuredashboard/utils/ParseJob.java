package se.gu.featuredashboard.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.ui.PlatformUI;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.model.featuremodel.Project;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.model.location.FeatureAnnotationsLocation;
import se.gu.featuredashboard.parsing.location.InFileAnnotationParser;

public class ParseJob extends Job {

	private Project project;
	private InFileAnnotationParser parser = new InFileAnnotationParser();
	private IFile file;
	private Map<Feature, FeatureContainer> information;
	private Logger logger = PlatformUI.getWorkbench().getService(org.eclipse.e4.core.services.log.Logger.class);
	
	private static final String JAVA_FILE = "java";
	
	public ParseJob(String name, Project project) {
		super(name);
		this.project = project;
		information = new HashMap<>();
	}
	
	public ParseJob(String name, Project project, IFile file) {
		super(name);
		this.project = project;
		this.file = file;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
			
		logger.info("Run ParseJob");
		
		IStatus statusToReturn = Status.OK_STATUS;
		
		if(project == null) {
			statusToReturn = Status.CANCEL_STATUS;
		} else if(file == null) {
			statusToReturn = handleProject(monitor);
		} else {
			statusToReturn = handleSingleFile(monitor);
		}
		
		if(statusToReturn != Status.CANCEL_STATUS && !monitor.isCanceled()) {
			ProjectStore.setActiveProject(project);
			logger.warn("Parse job was cancelled");
		}
		
		monitor.done();
		
		return statusToReturn;
	}

	private IStatus handleProject(IProgressMonitor monitor) {
		
		handleResource(project.getIProject(), monitor);
		
		if(monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		
		project.addFeatures(information.values());
		
		return Status.OK_STATUS;
	}
	
	private void handleResource(IContainer container, IProgressMonitor monitor) {
		if(monitor.isCanceled()) {
			return;
		}
		
		try {
			Arrays.stream(container.members()).forEach(member -> {
				if(member instanceof IContainer) {
					handleResource((IContainer) member, monitor);
				} else if(member instanceof IFile) {
					handleFile((IFile) member, monitor);
				}
			});
		} catch (CoreException e) {
			monitor.setCanceled(true);
			e.printStackTrace();
		}
	}
	
	private void handleFile(IFile resource, IProgressMonitor monitor) {
		if(monitor.isCanceled()) {
			return;
		}
		
		// Have an preference option which will allow you to include/exlude file extensions
		if(!resource.getFileExtension().equals(JAVA_FILE))
			return;
		
		List<FeatureAnnotationsLocation> features = parser.readParseAnnotations(resource.getLocation().toString());
		
		for(FeatureAnnotationsLocation location : features) {
			FeatureContainer container = information.get(location.getFeature());
			
			if(container == null) {	
				container = new FeatureContainer(location.getFeature());
				information.put(location.getFeature(), container);
			}
			// Since we want to get how many other features apart from itself, subtract 1
			container.incrementTanglingDegree(features.size()-1);
			container.addFileToLines(resource, location.getBlocklines());
		}
		
	}
	
	private IStatus handleSingleFile(IProgressMonitor monitor) {
		// Have an preference option which will allow you to include/exlude file extensions
		if(!file.getFileExtension().equals(JAVA_FILE))
			return Status.CANCEL_STATUS;
		
		List<FeatureAnnotationsLocation> features = parser.readParseAnnotations(file.getLocation().toString());
		
		for(FeatureAnnotationsLocation location : features) {
			FeatureContainer container = project.getFeatureContainer(location.getFeature());
			if(container == null) {
				container = new FeatureContainer(location.getFeature());
				project.addFeature(container);
			}
			// TODO - increment tangling degree correctly.
			container.addFileToLines(file, location.getBlocklines());
		}
		
		return Status.OK_STATUS;
	}	
}