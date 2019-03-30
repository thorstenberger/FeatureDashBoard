package se.gu.featuredashboard.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.location.FeatureLocation;

public class FeatureFileFolderParser {

	private StringBuilder parsingMessage = new StringBuilder("");
	private IFile parsingFile;
	
	public FeatureFileFolderParser(IFile parsingFile){
		this.parsingFile = parsingFile;
	}

	public IFile getParsingFileAddress(){
		return parsingFile;
	}
	
	public void setParsingFileAddress(IFile parsingFile) {
		this.parsingFile = parsingFile;
	}

	public boolean hasValidFeatureFile() {
		if (parsingFile!=null && parsingFile.exists() && parsingFile.getFileExtension().equals("feature-file"))
			return true;
		return false;
	}
	
	public boolean hasValidFeatureFolder() {
		if (parsingFile!=null && parsingFile.exists() && parsingFile.getFileExtension().equals("feature-folder"))
			return true;
		return false;
	}
	
	public List<FeatureLocation> readParse(){
		ArrayList<FeatureLocation> featureLocations = new ArrayList<>(); // the result of method
		parsingMessage = new StringBuilder("");
		
		if(!hasValidFeatureFile() && !hasValidFeatureFolder())
			return featureLocations; // returning an empty list

		ArrayList<String> phrases = new ArrayList<String>();	
		String line = null;
		try {
			FileReader fileReader = new FileReader(parsingFile.getLocation().toString());
			BufferedReader buffereReader = new BufferedReader(fileReader);
			while( (line=buffereReader.readLine())!=null){
				line = line.replaceAll("\\s+", ""); // removing white spaces
				String[] parts = line.split(";");	
				for(int i=0;i<parts.length;i++) {
					if(i!=parts.length-1) {
						phrases.add(parts[i]+";");
					}
					else {
						if(line.charAt(line.length()-1) ==';')
							phrases.add(parts[i]+";");
						else
							phrases.add(parts[i]);
					}
				}
			}

			String basicTerm = "";
			for(int i=0;i<phrases.size();i++) {
				if(phrases.get(i).charAt(phrases.get(i).length()-1)==';') {
					basicTerm += phrases.get(i);
					featureLocations.addAll(getTracesOfOneFeature(basicTerm));
					basicTerm = "";
				}
				else {
					basicTerm += phrases.get(i);
				}		
			}
			
			buffereReader.close(); 
			
		} catch (Exception e) {
			if(hasValidFeatureFile())
				parsingMessage.append("Error in reading and parsing the .feature-file file\n");
			else
				parsingMessage.append("Error in reading and parsing the .feature-folder file\n");
		}
		
		return featureLocations;
		
	}
	
	private List<FeatureLocation> getTracesOfOneFeature(String featureLocationsString) {
		List<FeatureLocation> results = new ArrayList<FeatureLocation>();
		
		String[] parts = featureLocationsString.split(";");// removing ';' from the end
		String featureID = parts[0].split(":")[0];
		String[] resources = parts[0].split(":")[1].split(",");
		
		for(int i=0;i<resources.length;i++) {
			IProject project = parsingFile.getProject();
			FeatureLocation newLocation;
			if( hasValidFeatureFile() )
				results.add(new FeatureLocation(new Feature(featureID), project.getFile(resources[i]), null));
			else 
				results.add(new FeatureLocation(new Feature(featureID), project.getFolder(resources[i]), null));
			
		}		
		return results;		
	}
	
	public String getParsingMessage() {
		return parsingMessage.toString();
	}
}
