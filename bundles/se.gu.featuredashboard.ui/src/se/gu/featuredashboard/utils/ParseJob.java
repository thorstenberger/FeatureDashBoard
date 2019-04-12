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

import org.eclipse.core.internal.resources.Marker;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.Preferences;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.model.featuremodel.FeatureModelHierarchy;
import se.gu.featuredashboard.model.featuremodel.Project;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.model.featuremodel.Tuple;
import se.gu.featuredashboard.model.location.BlockLine;
import se.gu.featuredashboard.parsing.ClaferFileParser;
import se.gu.featuredashboard.parsing.InFileAnnotationParser;
import se.gu.featuredashboard.parsing.ParseMappingFile;
import se.gu.featuredashboard.parsing.SyntaxException;

public class ParseJob extends Job {

	private Project project;
	private InFileAnnotationParser parser = new InFileAnnotationParser();
	private IFile fileToParse;
	private Map<Feature, FeatureContainer> projectFeatures;
	private List<String> excludedFolders;
	private Logger logger = PlatformUI.getWorkbench().getService(org.eclipse.e4.core.services.log.Logger.class);
	private JobType jobType;

	private enum JobType {
		FULL, SINGLE
	};

	public ParseJob(String name, Project project) {
		super(name);
		this.project = project;
		projectFeatures = new HashMap<>();
		excludedFolders = new ArrayList<>();
		jobType = JobType.FULL;
	}

	public ParseJob(String name, Project project, IFile file, Shell shell) {
		super(name);
		this.project = project;
		this.fileToParse = file;
		projectFeatures = new HashMap<>();
		excludedFolders = new ArrayList<>();
		jobType = JobType.SINGLE;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		Preferences preferences = ConfigurationScope.INSTANCE
				.getNode(FeaturedashboardConstants.FEATUREDASHBOARD_PREFERENCES);
		Preferences sub1 = preferences.node("node1");

		if (sub1.get("initialized", "default").equals("yes")) {
			int i = 0;
			while (true) {
				String folderKey = "Output" + i;
				String folderValue = sub1.get(folderKey, "default");
				if (folderValue.equals("default"))
					break;
				excludedFolders.add(folderValue);
				i++;
			}
		}

		logger.info("Start parsing " + project.getID() + " for annotations");

		if (project == null) {
			monitor.done();
			return Status.CANCEL_STATUS;
		}

		try {
			if (jobType == JobType.FULL) {
				handleResource(project.getIProject(), monitor);
				project.addFeatures(projectFeatures.values());
			} else {
				if (fileToParse == null) {
					monitor.done();
					return Status.CANCEL_STATUS;
				}
				if (equalsMappingFile(fileToParse) && !project.getOutputFolders().stream().map(IPath::toString)
						.anyMatch(fileToParse.getFullPath().toString()::contains))
					handleMappingFile(fileToParse, monitor);
				else if (fileToParse.getName().contains(".cfr"))
					handleFeatureModel(fileToParse, monitor);
				else
					handleFile(fileToParse, monitor);
			}

			monitor.done();

			if (monitor.isCanceled()) {
				System.out.println("monitor was canceled");
				if (jobType == JobType.FULL) {
					ProjectStore.removeProject(project);
				}

				return Status.CANCEL_STATUS;
			} else
				return Status.OK_STATUS;

		} catch (RuntimeException e) {
			// So that we can remove the project from the ProjectStore, otherwise the user
			// has to close the IDE to re-parse the project
			logger.warn("Runtime exception occured: " + e.getMessage());

			monitor.done();

			if (jobType == JobType.FULL)
				ProjectStore.removeProject(project);

			return Status.CANCEL_STATUS;
		}

	}

	private void handleResource(IContainer container, IProgressMonitor monitor) {
		if (monitor.isCanceled())
			return;

		try {
			Arrays.stream(container.members()).filter(this::equalsMappingFile)
					.forEach(resource -> handleMappingFile((IFile) resource, monitor));

			Arrays.stream(container.members()).forEach(member -> {
				if (member instanceof IContainer) {
					if (!project.getOutputFolders().stream().map(IPath::toString)
							.anyMatch(member.getFullPath().toString()::contains)
							&& !excludedFolders.stream().anyMatch(member.getFullPath().toString()::contains))
						handleResource((IContainer) member, monitor);
				} else if (member instanceof IFile) {
					IFile file = (IFile) member;
					if (file.getName().contains(".cfr"))
						handleFeatureModel(file, monitor);
					handleFile(file, monitor);
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

	private void handleFeatureModel(IFile file, IProgressMonitor monitor) {
		ClaferFileParser parser = new ClaferFileParser(file);
		FeatureModelHierarchy featureModelHierarchy = parser.readParse();

		project.setRootFeatures(featureModelHierarchy.getRootFeatures());
		project.setFeatureModel(featureModelHierarchy.getAllFeatures());

		for (Feature feature : featureModelHierarchy.getAllFeatures()) {
			FeatureContainer featureContainer = null;
			if (jobType == JobType.FULL) {
				featureContainer = projectFeatures.get(feature);
			} else {
				featureContainer = project.getFeatureContainer(feature);
			}

			if (featureContainer == null) {
				featureContainer = new FeatureContainer(feature);
				if (jobType == JobType.FULL)
					projectFeatures.put(feature, featureContainer);
				else
					project.addFeatureContainer(featureContainer);
			} else {
				featureContainer.setFeature(feature);
			}
		}
	}

	private void handleFile(IFile resource, IProgressMonitor monitor) {
		if (monitor.isCanceled())
			return;

		List<FeatureContainer> containersImplementedInFile = null;

		if (jobType == JobType.SINGLE)
			containersImplementedInFile = project.getFeatureContainers().stream().filter(c -> c.isAnnotatedIn(resource))
					.collect(Collectors.toList());

		Map<Feature, List<BlockLine>> featureToLines = new HashMap<>();
		parser.readParseAnnotations(new ArrayList<IFile>(Arrays.asList(resource))).stream()
				.forEach(location -> featureToLines.put(location.getFeature(), location.getBlocklines()));

		int uniqueFeatures = featureToLines.keySet().size();

		featureToLines.entrySet().stream().forEach(entry -> {
			FeatureContainer featureContainer = getFeatureContainer(entry.getKey());
			featureContainer.addInFileAnnotations(resource, entry.getValue(), uniqueFeatures - 1);
		});

		if (jobType == JobType.SINGLE) {
			for (FeatureContainer fc : containersImplementedInFile) {
				if (!featureToLines.containsKey(fc.getFeature())) {
					fc.removeFile(resource);
					if (fc.getScatteringDegree() == 0) {
						project.removeFeature(fc);
						continue;
					}
				}
			}
		}
	}

	private void handleMappingFile(IFile mappingFile, IProgressMonitor monitor) {
		if (monitor.isCanceled())
			return;

		try {
			List<FeatureContainer> containersMappedTo = null;

			if (jobType == JobType.SINGLE)
				containersMappedTo = project.getFeatureContainers().stream().filter(c -> c.isMappedIn(mappingFile))
						.collect(Collectors.toList());

			Map<Feature, List<IResource>> mapping = ParseMappingFile.readMappingFile(mappingFile,
					project.getIProject());

			mapping.keySet().forEach(feature -> {
				List<Tuple<IResource, Integer>> folderResources = new ArrayList<>();

				if (mappingFile.getFileExtension().equals(FeaturedashboardConstants.VPFOLDER_FILE))
					folderResources.add(new Tuple<IResource, Integer>(mappingFile.getParent(), 0));

				List<IResource> resources = mapping.get(feature);

				for (IResource resource : resources)
					mapResourceToFeature(resource, folderResources, monitor);

				if (monitor.isCanceled())
					return;

				FeatureContainer featureContainer = getFeatureContainer(feature);
				featureContainer.addMappingResource(mappingFile, folderResources);
			});

			if (jobType == JobType.SINGLE) {
				containersMappedTo.stream().filter(c -> !mapping.containsKey(c.getFeature())).forEach(c -> {
					c.removeFile(mappingFile);
					if (c.getScatteringDegree() == 0)
						project.removeFeature(c);
				});
			}

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

	private void mapResourceToFeature(IResource resource, List<Tuple<IResource, Integer>> folderResources,
			IProgressMonitor monitor) {
		if (monitor.isCanceled())
			return;

		try {
			if (resource instanceof IContainer) {
				IFolder folder = (IFolder) resource;
				IResource[] members = folder.members();

				folderResources.add(new Tuple<IResource, Integer>(folder, 0));
				Arrays.stream(members).forEach(member -> mapResourceToFeature(member, folderResources, monitor));
			} else {
				IFile leafFile = (IFile) resource;
				int lineCount = countLines(leafFile);

				folderResources.add(new Tuple<IResource, Integer>(leafFile, lineCount));
			}
		} catch (CoreException | IOException e) {
			try {
				IMarker marker = project.getIProject().createMarker(Marker.PROBLEM);
				marker.setAttribute(Marker.MESSAGE, e.getMessage() + ". Cancel project parsing.");
				marker.setAttribute(Marker.LOCATION, resource.getFullPath().toString());
			} catch (CoreException e1) {
				logger.warn(e.getMessage());
			}
			monitor.setCanceled(true);
		}
	}

	private FeatureContainer getFeatureContainer(Feature feature) {
		FeatureContainer featureContainer = null;
		if (jobType == JobType.FULL)
			featureContainer = projectFeatures.get(feature);
		else
			featureContainer = project.getFeatureContainer(feature);

		if (featureContainer == null) {
			featureContainer = new FeatureContainer(feature);
			if (jobType == JobType.FULL)
				projectFeatures.put(feature, featureContainer);
			else
				project.addFeatureContainer(featureContainer);
		}
		return featureContainer;
	}

	// Checks if a file is a mapping file or not
	private boolean equalsMappingFile(IResource resource) {
		if (!(resource instanceof IFile))
			return false;

		if (!resource.getName().contains("."))
			return false;

		return resource.getFileExtension().equals(FeaturedashboardConstants.FEATUREFILE_FILE)
				|| resource.getFileExtension().equals(FeaturedashboardConstants.FEATUREFOLDER_FILE)
				|| resource.getFileExtension().equals(FeaturedashboardConstants.VPFILE_FILE)
				|| resource.getFileExtension().equals(FeaturedashboardConstants.VPFOLDER_FILE);
	}

	// If an entire file is annoted then we need to get the length of that file so
	// the metrics are correct
	private int countLines(IFile file) throws IOException, CoreException {
		int count = 0;
		try (InputStream is = new BufferedInputStream(file.getContents())) {
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
			if (endsWithoutNewLine) {
				++count;
			}
		}
		return count;
	}
}