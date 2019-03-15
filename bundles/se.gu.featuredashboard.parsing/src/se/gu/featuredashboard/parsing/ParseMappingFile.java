package se.gu.featuredashboard.parsing;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;

import se.gu.featuredashboard.model.featuremodel.Feature;

public class ParseMappingFile {
	
	public static final String FEATUREFILE_FILE = "feature-file";
	public static final String FEATUREFOLDER_FILE = "feature-folder";
	
	public static final String ERROR_MESSAGE_COLONS = "Wrong amount of colons";
	public static final String ERROR_MESSAGE_COMMAS = "To many commas";
	
	public static Map<Feature, List<IResource>> readMappingFile(IFile featureFile, IProject project) throws SyntaxException {	
		Map<Feature, List<IResource>> featureFiles = new HashMap<>();
		try(Stream<String> fileStream = Files.lines(Paths.get(featureFile.getLocation().toString()), Charset.defaultCharset())){
			fileStream.forEach(line -> {
				String[] lineElements = line.split(":");
				
				// This split should always only produce two items
				if(lineElements.length != 2)
					throw new SyntaxException(ERROR_MESSAGE_COLONS);
				
				Feature feature = new Feature(lineElements[0].replaceAll("\\s+", ""));
				
				List<IResource> resources = new ArrayList<IResource>();
				String featureFilePath = featureFile.getFullPath().toString();
				
				for(String lineElement : lineElements[1].replaceAll("\\s+", "").split(",")) {	
					if(lineElement.equals(""))
						throw new SyntaxException(ERROR_MESSAGE_COMMAS);
					
					String newResourceLocation = featureFilePath.substring(0, (featureFilePath.length() - featureFile.getName().length())) + lineElement;
					
					if(featureFile.getFileExtension().equals(FEATUREFILE_FILE))
						resources.add(project.getWorkspace().getRoot().getFile(new Path(newResourceLocation)));
					else
						resources.add(project.getWorkspace().getRoot().getFolder(new Path(newResourceLocation)));
				}
				
				featureFiles.put(feature, resources);
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return featureFiles;
	}
}
