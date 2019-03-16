package se.gu.featuredashboard.parsing;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;

import se.gu.featuredashboard.model.featuremodel.Feature;

public class ParseMappingFile {
	
	public static final String FEATUREFILE_FILE = "feature-file";
	public static final String FEATUREFOLDER_FILE = "feature-folder";
	
	public static final String ERRORMESSAGE_COLONS = "Wrong amount of colons";
	public static final String ERRORMESSAGE_COMMAS = "To many commas";
	public static final String ERRORMESSAGE_NO_MAPPINGS = "No mappings to feature found";
	public static final String ERRORMESSAGE_DUPLICATED_FEATURE = "Same feature occures twice in file";
	
	//An additional feature to the plugin is to develop a specific editor for .feature-file/.feature-folder instead of checking syntax like this
	public static Map<Feature, List<IResource>> readMappingFile(IFile featureFile, IProject project) throws SyntaxException {	
		Map<Feature, List<IResource>> featureFiles = new HashMap<>();
		Set<Feature> featuresInFile = new HashSet<>();
		try(Stream<String> fileStream = Files.lines(Paths.get(featureFile.getLocation().toString()), Charset.defaultCharset())){
			fileStream.forEach(line -> {
				
				if(line.chars().filter(ch -> ch == ':').count() != 1)
					throw new SyntaxException(ERRORMESSAGE_COLONS); 
				
				String[] lineElements = line.replaceAll("\\s+", "").split(":");
				
				if(lineElements.length < 2)
					throw new SyntaxException(ERRORMESSAGE_NO_MAPPINGS);
				
				String featureString = lineElements[0];
				
				if(featureString.equals(""))
					throw new SyntaxException(ERRORMESSAGE_NO_MAPPINGS);
				
				Feature feature = new Feature(featureString);
				
				if(!featuresInFile.add(feature))
					throw new SyntaxException(ERRORMESSAGE_DUPLICATED_FEATURE);
								
				String mappingElements[] = lineElements[1].split(",");
				
				if(mappingElements.length == 0)
					throw new SyntaxException(ERRORMESSAGE_COMMAS);
				
				List<IResource> resources = new ArrayList<>();
				String featureFilePath = featureFile.getFullPath().toString();
				
				for(String lineElement : mappingElements) {	
					if(lineElement.equals(""))
						throw new SyntaxException(ERRORMESSAGE_COMMAS);
					
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
