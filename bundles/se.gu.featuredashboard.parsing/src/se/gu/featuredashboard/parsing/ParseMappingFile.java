package se.gu.featuredashboard.parsing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;

import se.gu.featuredashboard.model.featuremodel.Feature;

public class ParseMappingFile {
	
	public static final String FEATUREFILE_FILE = "feature-file";
	public static final String FEATUREFOLDER_FILE = "feature-folder";
	
	public static Map<Feature, List<IResource>> readMappingFile(IFile featureFile, IProject project) {		
		Map<Feature, List<IResource>> featureFiles = new HashMap<>();
		try(Stream<String> fileStream = Files.lines(Paths.get(featureFile.getLocation().toString()))){
			
			fileStream.forEach(line -> {
				
				String[] lineElements = line.split(":");
				if(lineElements.length == 0)
					return;
				
				Feature feature = new Feature(lineElements[0]);
				
				// Get the files listed after the feature and convert them to IFiles or IContainers
				List<IResource> files = Arrays.stream(lineElements[1].replaceAll("\\s", "")
						.split(","))
						.map(resource -> {
							String featureFilePath = featureFile.getLocation().toString();
							String resourceLocation = featureFilePath.substring(0, (featureFilePath.length()-featureFile.getName().length())) + resource;
							if(featureFile.getFileExtension().equals(FEATUREFILE_FILE))
								return project.getWorkspace().getRoot().getFileForLocation(new Path(resourceLocation));
							else 
								return project.getWorkspace().getRoot().getContainerForLocation(new Path(resourceLocation));
								
						})
						.collect(Collectors.toList());
				
				featureFiles.put(feature, files);
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return featureFiles;
	}
}
