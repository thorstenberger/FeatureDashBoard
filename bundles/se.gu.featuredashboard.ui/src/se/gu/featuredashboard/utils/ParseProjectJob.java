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

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.model.featuremodel.Project;
import se.gu.featuredashboard.model.location.FeatureAnnotationsLocation;
import se.gu.featuredashboard.parsing.location.InFileAnnotationParser;

public class ParseProjectJob extends Job {

	private Project project;
	private InFileAnnotationParser parser = new InFileAnnotationParser();
	
	private Map<Feature, FeatureContainer> information;
	
	public ParseProjectJob(String name) {
		super(name);
		
	}
	
	public ParseProjectJob(String name, Project project) {
		super(name);
		this.project = project;
	}
	
	public void setProject(Project project) {
		this.project = project;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		if(project == null) {
			return Status.CANCEL_STATUS;
		}
		
		return handleProject(project, monitor);
	}

	private IStatus handleProject(Project project, IProgressMonitor monitor) {
		
		information = new HashMap<>();
		
		handleResource(project.getIProject(), monitor);
		
		if(monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
	
		monitor.done();
		
		project.addFeatures(information.values());
		
		return Status.OK_STATUS;
	}
	
	private void handleResource(IContainer container, IProgressMonitor monitor) {
		if(monitor.isCanceled()) {
			return;
		}
		
		try {
			Arrays.stream(container.members()).forEach(member ->{
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
		
		List<FeatureAnnotationsLocation> features = parser.readParseAnnotations(resource.getLocation().toString());
		
		for(FeatureAnnotationsLocation location : features) {
			FeatureContainer container = information.get(location.getFeature());
			
			if(container == null) {	
				container = new FeatureContainer(location.getFeature());
				information.put(location.getFeature(), container);
			}
			// Since we want to get how many other features apart from itself, subtract 1
			container.incrementTanglingDegree(features.size()-1);
			
			// Not sure if yu need addFile, addBlockLines and addFileToLines. Later on we can see if it's necessary
			container.addFile(resource);
			container.addBlockLines(location.getBlocklines());
			container.addFileToLines(resource, location.getBlocklines());
		}
		
	}
	
}