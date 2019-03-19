package se.gu.featuredashboard.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.model.featuremodel.Project;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.model.featuremodel.Tuple;
import se.gu.featuredashboard.model.location.BlockLine;
import se.gu.featuredashboard.parsing.InFileAnnotationParser;
import se.gu.featuredashboard.parsing.ParseMappingFile;
import se.gu.featuredashboard.parsing.SyntaxException;

public class ParseJob extends Job {

	private Project project;
	private InFileAnnotationParser parser = new InFileAnnotationParser();
	private IFile file;
	private IProject iProject;
	private Shell shell;
	private Map<Feature, FeatureContainer> information;
	private Logger logger = PlatformUI.getWorkbench().getService(org.eclipse.e4.core.services.log.Logger.class);
	private Set<IResource> visited;
	private JobType jobType;
	private List<Tuple<String, String>> syntaxExceptions;
	
	private enum JobType {
		FULL, SINGLE
	};
	
	/**
	 * @param name - identifier for this {@link Job}
	 * @param project - the affected {@link Project}
	 * */
	public ParseJob(String name, Project project, Shell shell) {
		super(name);
		this.project = project;
		this.shell = shell;
		init();
		jobType = JobType.FULL;
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
		init();
		jobType = JobType.SINGLE;
	}

	private void init() {
		iProject = project.getIProject();
		information = new HashMap<>();
		visited = new HashSet<>();
		syntaxExceptions = new ArrayList<>();
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
		
		if(project == null)
			return Status.CANCEL_STATUS;
		
		if(jobType == JobType.FULL) {
			statusToReturn = handleProject(monitor);
		} else {
			if((file.getFileExtension().equals(FeaturedashboardConstants.FEATUREFILE_FILE) || file.getFileExtension().equals(FeaturedashboardConstants.FEATUREFOLDER_FILE)) 
					&& !project.getOutputFolders().stream().anyMatch(folder -> file.getLocation().toString().contains(folder.toString())))
				statusToReturn = handleMappingFile(file, monitor);
			else
				handleFile(file, monitor);
		}
		
		if(syntaxExceptions.size() > 0) {
			StringBuilder errorMessage = new StringBuilder();
			syntaxExceptions.forEach(wrongSyntax -> {
				errorMessage.append(wrongSyntax.getLeft() + "  ---> " + wrongSyntax.getRight());
				errorMessage.append("\n");
			});	
			Display.getDefault().asyncExec(() -> {
				CustomDialog dialog = new CustomDialog(shell, 
													   FeaturedashboardConstants.SYNTAXERROR_DIALOG_TITLE, 
													   FeaturedashboardConstants.SYNTAXERROR_DIALOG_MESSAGE, 
													   errorMessage.toString());
				dialog.create();
				dialog.open();
			});
		}
		
		if(statusToReturn != Status.CANCEL_STATUS && !monitor.isCanceled()) {
			ProjectStore.setActiveProject(project);
			monitor.done();
		}
		
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
					handleFile((IFile) member, monitor);
				}
			});
		} catch (CoreException e) {
			monitor.setCanceled(true);
			e.printStackTrace();
		}
	}
	
	/**
	 * Called by {@link ParseJob#handleResource(IContainer, IProgressMonitor)} or in {@link ParseJob#run(IProgressMonitor)} directlty to parse a single file for any annotations
	 * @param resource - the {@link IFile} to parse
	 * @param monitor - the {@link IProgressMonitor} for this job
	 * */
	private void handleFile(IFile resource, IProgressMonitor monitor) {
		if(monitor.isCanceled())
			return;
		
		List<FeatureContainer> containersImplementedInFile = null;
		
		if(jobType == JobType.SINGLE)
			containersImplementedInFile = project.getFeatureContainers().stream().filter(c -> c.isAnnotatedIn(resource)).collect(Collectors.toList());
					
		Map<Feature, List<BlockLine>> featureToLines = new HashMap<>();
		parser.readParseAnnotations(resource.getLocation().toString()).stream().forEach(location -> featureToLines.put(location.getFeature(), location.getBlocklines()));
		
		int uniqueFeatures = featureToLines.keySet().size();
		
		featureToLines.entrySet().stream().forEach(entry -> {
			FeatureContainer featureContainer = getFeatureContainer(entry.getKey());
			featureContainer.addInFileAnnotations(resource, entry.getValue(), uniqueFeatures-1);
		});
		
		if(jobType == JobType.SINGLE) {
			containersImplementedInFile.stream()
				.filter(c -> !featureToLines.containsKey(c.getFeature()))
					.forEach(c -> {
						c.removeInAnnotationFile(resource);
						if(c.getScatteringDegree() == 0)
							project.removeFeature(c);
					});
		}
	}
	
	/**
	 * Called by {@link ParseJob#handleResource(IContainer, IProgressMonitor)} when a .feature-file or .feature-folder is detected.
	 * This method then calls {@link ParseMappingFile#readMappingFile(IFile, IProject)} to get the feature and its associated file(s)/folder(s)
	 * @param resource - the mapping file as an {@link IFile}
	 * @param monitor - the {@link IProgressMonitor} for this job  
	 * */
	private IStatus handleMappingFile(IFile mappingFile, IProgressMonitor monitor) {
		if(monitor.isCanceled())
			return Status.CANCEL_STATUS;
		
		try {
			List<FeatureContainer> containersMappedTo = null;
			
			if(jobType == JobType.SINGLE)
				containersMappedTo = project.getFeatureContainers().stream().filter(c -> c.isMappedIn(mappingFile)).collect(Collectors.toList());
			
			Map<Feature, List<IResource>> mapping = ParseMappingFile.readMappingFile(mappingFile, iProject);
			visited.add(mappingFile);
			
			mapping.keySet().forEach(feature -> {
				List<Tuple<IResource, Integer>> folderResources = new ArrayList<>();
				List<IResource> resources = mapping.get(feature); 
				for(IResource resource : resources) {
					mapResourceToFeature(feature, resource, folderResources, monitor);
				}
				FeatureContainer featureContainer = getFeatureContainer(feature);
				featureContainer.addMappingResource(mappingFile, folderResources);
			});
			
			if(jobType == JobType.SINGLE) {
				containersMappedTo.stream()
					.filter(c -> !mapping.containsKey(c.getFeature()))
						.forEach(c -> {
							c.removeMapping(mappingFile);
							if(c.getScatteringDegree() == 0)
								project.removeFeature(c);
						});
					
			}
		} catch(SyntaxException e) {
			syntaxExceptions.add(new Tuple<String, String>(mappingFile.getFullPath().toString(), e.getMessage()));
		}
		
		return Status.OK_STATUS;
	}
	
	/**
	 * Called by {@link ParseJob#handleMappingFile(IFile, IProgressMonitor)} to recursively associate a specfic {@link FeatureContainer} with a resource
	 * @param featureContainer - the {@link FeatureContainer} that contains the specific feature
	 * @param resource - the current {@link IResource} we are looking at
	 * @param parentFolder - the folder that the mapping file is contained in
	 * @param folderResources - a list of resources belonging to the feature in the mapping file
	 * @param monitor - the {@link IProgressMonitor} for this job
	 * */
	private void mapResourceToFeature(Feature feature, IResource resource, List<Tuple<IResource, Integer>> folderResources, IProgressMonitor monitor) {
		if(monitor.isCanceled())
			return;
		
		try {	
			visited.add(resource);
			if(resource instanceof IContainer) {
				IFolder folder = (IFolder) resource;
				IResource[] members = folder.members();
				
				folderResources.add(new Tuple<IResource, Integer>(folder, 0));
				Arrays.stream(members).forEach(member -> mapResourceToFeature(feature, member, folderResources, monitor));
			} else {
				IFile leafFile = (IFile) resource;
				int lineCount = countLines(leafFile);
				
				folderResources.add(new Tuple<IResource, Integer>(leafFile, lineCount));
			}
		} catch (CoreException | IOException e) {
			logger.warn("Error trying to map resource to feature." + e.getMessage());
		}
	}
	
	private FeatureContainer getFeatureContainer(Feature feature) {
		FeatureContainer featureContainer = null;
		if(jobType == JobType.FULL)
			featureContainer = information.get(feature);
		else
			featureContainer = project.getFeatureContaier(feature);
	
		if(featureContainer == null) {
			featureContainer = new FeatureContainer(feature);
			if(jobType == JobType.FULL)
				information.put(feature, featureContainer);
			else
				project.addFeature(featureContainer);
		}
		return featureContainer;
	}
	
	/**
	 * Used by {@link ParseJob#mapResourceToFeature(FeatureContainer, IResource)} to efficiently get the number of lines for a file
	 * @param file - the {@link IFile} to read 
	 * */
	private int countLines(IFile file) throws IOException, CoreException {
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
	    }
	    return count;
	}
}