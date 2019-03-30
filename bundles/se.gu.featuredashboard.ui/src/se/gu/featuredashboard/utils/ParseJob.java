package se.gu.featuredashboard.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.model.featuremodel.Project;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.model.featuremodel.Triple;
import se.gu.featuredashboard.model.featuremodel.Tuple;
import se.gu.featuredashboard.model.location.BlockLine;
import se.gu.featuredashboard.parsing.InFileAnnotationParser;
import se.gu.featuredashboard.parsing.ParseMappingFile;
import se.gu.featuredashboard.parsing.SyntaxException;

public class ParseJob extends Job {

	private Project project;
	private InFileAnnotationParser parser = new InFileAnnotationParser();
	private IFile fileToParse;
	private Shell shell;
	private Map<Feature, FeatureContainer> projectFeatures;
	private Logger logger = PlatformUI.getWorkbench().getService(org.eclipse.e4.core.services.log.Logger.class);
	private JobType jobType;
	private ErrorDialog errorDialog;
	private List<Triple<String, String, Integer>> parsingExceptions;
	
	private enum JobType {
		FULL, SINGLE
	};
	
	public ParseJob(String name, Project project, Shell shell) {
		super(name);
		this.project = project;
		this.shell = shell;
		projectFeatures = new HashMap<>();
		parsingExceptions = new ArrayList<>();
		jobType = JobType.FULL;
	}
	
	public ParseJob(String name, Project project, IFile file, Shell shell) {
		super(name);
		this.project = project;
		this.fileToParse = file;
		this.shell = shell;
		projectFeatures = new HashMap<>();
		parsingExceptions = new ArrayList<>();
		jobType = JobType.SINGLE;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		logger.info("Start parsing " + project.getID() + " for annotations");
		
		if(project == null) {
			monitor.done();
			return Status.CANCEL_STATUS;
		}
		
		try {
			if(jobType == JobType.FULL) {
				handleResource(project.getIProject(), monitor);
				project.addFeatures(projectFeatures.values());
			} else {
				if(fileToParse == null) {
					monitor.done();
					return Status.CANCEL_STATUS;
				}
				if(equalsMappingFile(fileToParse) && !project.getOutputFolders().stream().map(IPath::toString).anyMatch(fileToParse.getFullPath().toString()::contains))
					handleMappingFile(fileToParse, monitor);
				else
					handleFile(fileToParse, monitor);
			}
			
			if(parsingExceptions.size() > 0) {
				
				StringBuilder errorMessage = new StringBuilder();
				
				parsingExceptions.forEach(wrongSyntax -> {
					if(wrongSyntax.getRight() == null)
						errorMessage.append(wrongSyntax.getLeft() + "  ---> " + wrongSyntax.getCentre());
					else
						errorMessage.append(wrongSyntax.getLeft() + "  ---> " + wrongSyntax.getCentre() + " at line number: " + wrongSyntax.getRight());
					errorMessage.append("\n");
				});
				
				Display.getDefault().asyncExec(() -> {
					errorDialog = new ErrorDialog(shell, FeaturedashboardConstants.SYNTAXERROR_DIALOG_TITLE, FeaturedashboardConstants.SYNTAXERROR_DIALOG_MESSAGE, errorMessage.toString());
					errorDialog.create();
					errorDialog.open();
				});
			}
			
			monitor.done();
			
			if(monitor.isCanceled()) {
				System.out.println("monitor was canceled");
				if(jobType == JobType.FULL) {
					ProjectStore.removeProject(project);
				}
					
				return Status.CANCEL_STATUS;
			}
			else
				return Status.OK_STATUS;
			
		} catch(RuntimeException e) {
			// So that we can remove the project from the ProjectStore, otherwise the user has to close the IDE to re-parse the project
			logger.warn("Runtime exception occured: " + e.getMessage());
			
			Display.getDefault().asyncExec(() -> {
				MessageDialog.openError(shell, "Error!", "Runtime error while parsing project: " + e.getMessage());
			});
			
			monitor.done();
			
			if(jobType == JobType.FULL)
				ProjectStore.removeProject(project);
			
			return Status.CANCEL_STATUS;
		}
		 
	}
	
	private void handleResource(IContainer container, IProgressMonitor monitor) {
		if(monitor.isCanceled())
			return;
		
		try {
			Arrays.stream(container.members()).filter(this::equalsMappingFile).forEach(resource -> handleMappingFile((IFile) resource, monitor));
			
			Arrays.stream(container.members()).forEach(member -> {
				if(member instanceof IContainer) {
					if(!project.getOutputFolders().stream().map(IPath::toString).anyMatch(member.getFullPath().toString()::contains))
						handleResource((IContainer) member, monitor);
				} else if(member instanceof IFile) {
					handleFile((IFile) member, monitor);
				}
			});
		} catch(CoreException e) {
			parsingExceptions.add(new Triple<String, String, Integer>(container.getFullPath().toString(), e.getMessage(), null));
			monitor.setCanceled(true);
		}
	}
	
	private void handleFile(IFile resource, IProgressMonitor monitor) {
		if(monitor.isCanceled())
			return;
		
		List<FeatureContainer> containersImplementedInFile = null;
		
		if(jobType == JobType.SINGLE)
			containersImplementedInFile = project.getFeatureContainers().stream().filter(c -> c.isAnnotatedIn(resource)).collect(Collectors.toList());
					
		Map<Feature, List<BlockLine>> featureToLines = new HashMap<>();
		parser.readParseAnnotations(new ArrayList<IFile>(Arrays.asList(resource))).stream().forEach(location -> featureToLines.put(location.getFeature(), location.getBlocklines()));
		
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
	
	private void handleMappingFile(IFile mappingFile, IProgressMonitor monitor) {
		if(monitor.isCanceled())
			return;
		
		try {
			List<FeatureContainer> containersMappedTo = null;
			
			if(jobType == JobType.SINGLE)
				containersMappedTo = project.getFeatureContainers().stream().filter(c -> c.isMappedIn(mappingFile)).collect(Collectors.toList());
			
			Map<Feature, List<IResource>> mapping = ParseMappingFile.readMappingFile(mappingFile, project.getIProject());
			
			mapping.keySet().forEach(feature -> {				
				List<Tuple<IResource, Integer>> folderResources = new ArrayList<>();
				
				if(mappingFile.getFileExtension().equals(FeaturedashboardConstants.VPFOLDER_FILE))
					folderResources.add(new Tuple<IResource, Integer>(mappingFile.getParent(), 0));
				
				List<IResource> resources = mapping.get(feature);
				
				for(IResource resource : resources)
					mapResourceToFeature(feature, resource, folderResources, monitor);
				
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
			parsingExceptions.add(new Triple<String, String, Integer>(mappingFile.getFullPath().toString(), e.getMessage(), e.getLineNumber()));
		}
	}
	
	private void mapResourceToFeature(Feature feature, IResource resource, List<Tuple<IResource, Integer>> folderResources, IProgressMonitor monitor) {
		if(monitor.isCanceled())
			return;
		
		try {
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
			parsingExceptions.add(new Triple<String, String, Integer>(resource.getFullPath().toString(), "Error trying to map resource to feature. " + e.getMessage(), null));
			monitor.setCanceled(true);
		}
	}
	
	private FeatureContainer getFeatureContainer(Feature feature) {
		FeatureContainer featureContainer = null;
		if(jobType == JobType.FULL)
			featureContainer = projectFeatures.get(feature);
		else
			featureContainer = project.getFeatureContaier(feature);
	
		if(featureContainer == null) {
			featureContainer = new FeatureContainer(feature);
			if(jobType == JobType.FULL)
				projectFeatures.put(feature, featureContainer);
			else
				project.addFeature(featureContainer);
		}
		return featureContainer;
	}
	
	private boolean equalsMappingFile(IResource resource) {
		if(!(resource instanceof IFile))
			return false;
		
		if(!resource.getName().contains("."))
			return false;
		
		return resource.getFileExtension().equals(FeaturedashboardConstants.FEATUREFILE_FILE) || 
				resource.getFileExtension().equals(FeaturedashboardConstants.FEATUREFOLDER_FILE) || 
				resource.getFileExtension().equals(FeaturedashboardConstants.VPFILE_FILE) ||
				resource.getFileExtension().equals(FeaturedashboardConstants.VPFOLDER_FILE);
	}
	
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