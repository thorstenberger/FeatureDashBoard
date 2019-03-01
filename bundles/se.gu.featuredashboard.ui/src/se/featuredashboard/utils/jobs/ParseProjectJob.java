package se.featuredashboard.utils.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.location.FeatureAnnotationsLocation;
import se.gu.featuredashboard.parsing.location.InFileAnnotationParser;

public class ParseProjectJob extends Job {

	private IProject project;
	private InFileAnnotationParser parser = new InFileAnnotationParser();
	// A mapping from a file to all feature annotations in that file
	private Map<Feature, List<IFile>> featureFile;
	
	@Deprecated
	public ParseProjectJob(String name) {
		super(name);
		throw new UnsupportedOperationException("This constructure should not be used");
	}
	
	public ParseProjectJob(String name, IProject project) {
		super(name);
		this.project = project;
		this.featureFile = new HashMap<>();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		if(project == null) {
			return Status.CANCEL_STATUS;
		}
		
		return handleProject(project, monitor);
	}

	private IStatus handleProject(IProject project, IProgressMonitor monitor) {
		
		handleResource(project, monitor);
		
		if(monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
	
		
		monitor.done();
		
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
			List<IFile> list = featureFile.get(location.getFeature());
			
			if(list == null) {
				List<IFile> temp = new ArrayList<>();
				temp.add(resource);
				featureFile.put(location.getFeature(), temp);
			} else {
				list.add(resource);
				featureFile.put(location.getFeature(), list);
			}
		}
		
	}
	
	public Map<Feature, List<IFile>> getFeatures(){
		return featureFile;
	}
	
}