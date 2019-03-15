package se.gu.featuredashboard.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import se.gu.featuredashboard.model.location.BlockLine;
import se.gu.featuredashboard.model.location.FeatureAnnotationsLocation;
import se.gu.featuredashboard.parsing.InFileAnnotationParser;
import se.gu.featuredashboard.parsing.ParseMappingFile;

public class ParseJob extends Job {

	private Project project;
	private InFileAnnotationParser parser = new InFileAnnotationParser();
	private IFile file;
	private IProject iProject;
	private Map<Feature, FeatureContainer> information;
	private Logger logger = PlatformUI.getWorkbench().getService(org.eclipse.e4.core.services.log.Logger.class);
	private Set<IResource> visited;
	
	/**
	 * @param name - identifier for this {@link Job}
	 * @param project - the affected {@link Project}
	 * */
	public ParseJob(String name, Project project) {
		super(name);
		this.project = project;
		iProject = project.getIProject();
		information = new HashMap<>();
		visited = new HashSet<>();
	}
	
	/**
	 * @param name - identifier for this {@link Job}
	 * @param project - the affected {@link Project}
	 * @param file - the {@link IFile} that has been updated and should be parsed
	 * */
	public ParseJob(String name, Project project, IFile file) {
		super(name);
		this.project = project;
		this.file = file;
		iProject = project.getIProject();
		visited = new HashSet<>();
	}

	/**
	 * When .schedule is called on {@link Job} this method will be executed and starting the job.
	 * Calls either {@link ParseJob#handleResource(IContainer, IProgressMonitor)} when we should parse an entire Project or
	 * {@link ParseJob#handleSingleFile(IProgressMonitor)} when a single file has been updated
	 * @param monitor - the {@link IProgressMonitor} for this job
	 * @return {link IStatus} to indicate the successfulness of this job
	 * */
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

	/**
	 * Wrapper function for {@link ParseJob#handleResource(IContainer, IProgressMonitor)} and calls it with an {@link IProject} to go through
	 * @param monitor - the {@link IProgressMonitor} for this job
	 * @return {link IStatus} to indicate the successfulness of this job 
	 * */
	private IStatus handleProject(IProgressMonitor monitor) {
		
		handleResource(iProject, monitor);
		
		if(monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		
		project.addFeatures(information.values());
		
		return Status.OK_STATUS;
	}
	
	/**
	 * Recursive function called by {@link ParseJob#handleProject(IProgressMonitor)} to go through all the resources in a specific {@link IProject}
	 * @param container - the {@link IContainer} we are currently in
	 * @param monitor - the {@link IProgressMonitor} for this job
	 * */
	private void handleResource(IContainer container, IProgressMonitor monitor) {
		if(monitor.isCanceled()) {
			return;
		}
		
		try {
			// Handle mapping file first and mark resources as visited, this will prevent us accessing sub-folders twice in the case of .feature-folder
			Arrays.stream(container.members()).filter(resource -> {
				if(!(resource instanceof IFile))
					return false;
				return resource.getFileExtension().equals(FeaturedashboardConstants.FEATUREFILE_FILE) || resource.getFileExtension().equals(FeaturedashboardConstants.FEATUREFOLDER_FILE);
			}).forEach(resource -> handleMappingFile((IFile) resource, monitor));
			
			Arrays.stream(container.members()).forEach(member -> {
				if(!visited.add(member))
					return;
				if(member instanceof IContainer) {
					if(!project.getOutputFolders().stream().anyMatch(path -> member.getFullPath().equals(path)))
						handleResource((IContainer) member, monitor);
				} else if(member instanceof IFile) {
					IFile currentFile = (IFile) member;
					if(currentFile.getFileExtension().equals(FeaturedashboardConstants.FEATUREFILE_FILE) || currentFile.getFileExtension().equals(FeaturedashboardConstants.FEATUREFOLDER_FILE))
						handleMappingFile(currentFile, monitor);
					else
						handleFile(currentFile, monitor);
				}
			});
		} catch (CoreException e) {
			monitor.setCanceled(true);
			e.printStackTrace();
		}
	}
	
	/**
	 * Called by {@link ParseJob#handleResource(IContainer, IProgressMonitor)} to parse a single file for any annotations
	 * @param resource - the {@link IFile} to parse
	 * @param monitor - the {@link IProgressMonitor} for this job
	 * */
	private void handleFile(IFile resource, IProgressMonitor monitor) {
		if(monitor.isCanceled())
			return;
		
		List<FeatureAnnotationsLocation> locations = parser.readParseAnnotations(resource.getLocation().toString());
		Set<Feature> uniqueFeatures = new HashSet<>();
		
		for(FeatureAnnotationsLocation location : locations) {
			uniqueFeatures.add(location.getFeature());
			FeatureContainer container = information.get(location.getFeature());
			
			if(container == null) {	
				container = new FeatureContainer(location.getFeature());
				information.put(location.getFeature(), container);
			}
			container.addFileToBlocks(resource, location.getBlocklines());
		}
		
		for(Feature feature : uniqueFeatures) {
			FeatureContainer container = information.get(feature);
			container.setTanglingDegree(resource, uniqueFeatures.size()-1);
		}
		
	}
	
	/**
	 * When the {@link Builder} for this project runs an AUTO_BUILD we call this job with a 
	 * single file to update metrics for all associated {@link FeatureContainer}
	 * @param monitor - the {@link IProgressMonitor} for this job
	 * @return {link IStatus} to indicate the successfulness of this job
	 * */
	private IStatus handleSingleFile(IProgressMonitor monitor) {
		List<FeatureAnnotationsLocation> features = parser.readParseAnnotations(file.getLocation().toString());
		Set<Feature> uniqueFeatures = new HashSet<>();
		
		FeatureContainer container = null;
		boolean newFeature = false;
		
		for(FeatureAnnotationsLocation location : features) {
			uniqueFeatures.add(location.getFeature());
			container = project.getFeatureContainer(location.getFeature());
			if(container == null) {
				newFeature = true;
				container = new FeatureContainer(location.getFeature());
				project.addFeature(container);
			}
			container.addFileToBlocks(file, location.getBlocklines());
		}
		
		// If we introduce a new feature then we need to update the tangling degree with all the other features as well
		if(container != null) {
			if(newFeature) {
				project.getFeatureContainers().forEach(parsedContainers -> {
					parsedContainers.setTanglingDegree(file, uniqueFeatures.size()-1);
				});
			} else {
				container.setTanglingDegree(file, uniqueFeatures.size()-1);
			}
		}

		return Status.OK_STATUS;
	}
	
	/**
	 * Called by {@link ParseJob#handleResource(IContainer, IProgressMonitor)} when a .feature-file or .feature-folder is detected.
	 * This method then calls {@link ParseMappingFile#readMappingFile(IFile, IProject)} to get the feature and its associated file(s)/folder(s)
	 * @param resource - the mapping file as an {@link IFile}
	 * @param monitor - the {@link IProgressMonitor} for this job  
	 * */
	private void handleMappingFile(IFile resource, IProgressMonitor monitor) {
		if(monitor.isCanceled())
			return;
		
		Map<Feature, List<IResource>> mapping = ParseMappingFile.readMappingFile(resource, iProject);
		visited.add(resource);
		
		mapping.keySet().forEach(feature -> {
			FeatureContainer featureContainer = information.get(feature);
			if(featureContainer == null) {
				featureContainer = new FeatureContainer(feature);
				information.put(feature, featureContainer);
			}		
			for(IResource file : mapping.get(feature)) {
				mapResourceToFeature(featureContainer, file, monitor);
			}
		});
	}
	
	/**
	 * Called by {@link ParseJob#handleMappingFile(IFile, IProgressMonitor)} to recursively associate a specfic {@link FeatureContainer} with a resource
	 * @param featureContainer - the {@link FeatureContainer} that contains the specific feature
	 * @param resource - the current {@link IResource} we are looking at
	 * @param monitor - the {@link IProgressMonitor} for this job
	 * */
	private void mapResourceToFeature(FeatureContainer featureContainer, IResource resource, IProgressMonitor monitor) {
		if(monitor.isCanceled())
			return;
		
		try {
			visited.add(resource);
			if(resource instanceof IContainer) {
				featureContainer.incrementNumberOfFolderAnnotations();
				IContainer folder = (IContainer) resource;
				Arrays.stream(folder.members()).forEach(member -> mapResourceToFeature(featureContainer, member, monitor));
			} else {
				featureContainer.addFileToBlocks((IFile) resource, Arrays.asList(new BlockLine(1, countLines((IFile) resource))));
				featureContainer.setTanglingDegree((IFile) resource, 0);
				featureContainer.incrementNumberOfFileAnnotations();
			}
		} catch (CoreException e) {
			logger.warn("Error trying to map folder and files to feature");
		}
	}
	
	/**
	 * Used by {@link ParseJob#mapResourceToFeature(FeatureContainer, IResource)} to efficiently get the number of lines for a file
	 * @param file - the {@link IFile} to read 
	 * */
	private int countLines(IFile file) {
		int count = 0;
		try (InputStream is = new BufferedInputStream(file.getContents())){
	        byte[] c = new byte[1024];
	        int readChars = 0;
	        boolean endsWithoutNewLine = false;
	        while ((readChars = is.read(c)) != -1) {
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n')
	                    ++count;
	            }
	            endsWithoutNewLine = (c[readChars - 1] != '\n');
	        }
	        if(endsWithoutNewLine) {
	            ++count;
	        } 
	    } catch(IOException | CoreException e) {
	    	e.printStackTrace();
	    }
	    return count;
	}
}