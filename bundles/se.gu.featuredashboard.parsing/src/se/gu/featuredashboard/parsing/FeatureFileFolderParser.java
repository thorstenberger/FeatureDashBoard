/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.location.FeatureLocation;

/**
 * This class is used for parsing .feature-file and .feature-folder files
 * 
 */
public class FeatureFileFolderParser {

	private StringBuilder parsingMessage = new StringBuilder("");
	private final String MESSAGE1="Error in reading and parsing the .feature-file file\n";
	private final String MESSAGE2="Error in reading and parsing the .feature-folder file\n";
	
	private IFile parsingFile;
	
	/**
	 * Parsing Constructor 
	 * 
	 * @param parsingFile
	 * 		the file that the will be parsed. The valid file extension must be feature-file
	 * 		or feature-folder 
	 */
	public FeatureFileFolderParser(IFile parsingFile){
		this.parsingFile = parsingFile;
	}

	
	/**
	 * Returns the file that is the target of parsing
	 */
	public IFile getParsingFileAddress(){
		return parsingFile;
	}
	
	/**
	 * Updating parsing Info
	 * 
	 * @param parsingFile
	 * 		the file that the will be parsed. The valid file extension must be feature-file
	 * 		or feature-folder
	 */
	public void setParsingInfo(IFile parsingFile, List<IFolder> allFolders, List<IFile> allFiles) {
		this.parsingFile = parsingFile;
	}

	
	/**
	 * Returns <code>true</code> if the parsing target file exists and has the extension .feature-file 
	 */
	public boolean hasValidFeatureFile() {
		if (parsingFile!=null && parsingFile.exists() && parsingFile.getFileExtension().equals("feature-file"))
			return true;
		return false;
	}
	
	/**
	 * Returns <code>true</code> if the parsing target file exists and has the extension .feature-folder 
	 */
	public boolean hasValidFeatureFolder() {
		if (parsingFile!=null && parsingFile.exists() && parsingFile.getFileExtension().equals("feature-folder"))
			return true;
		return false;
	}
	
	/**
	 * Reads and parses the parsing target file.
	 * 
	 * @return all of the traces among features and resources
	 */
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
				parsingMessage.append(MESSAGE1);
			else
				parsingMessage.append(MESSAGE2);
		}
		
		return featureLocations;
		
	}
	
	/**
	 * Returns all of the traces of one feature to different resources
	 * 
	 * @param featureLocationsString
	 * 		the part of of parsing target file starting from a feature id to its next semicolon
	 * 
	 */
	private List<FeatureLocation> getTracesOfOneFeature(String featureLocationsString) {
		List<FeatureLocation> results = new ArrayList<FeatureLocation>();
		
		String[] parts = featureLocationsString.split(";");// removing ';' from the end
		String featureID = parts[0].split(":")[0];
		String[] resourcesNames = parts[0].split(":")[1].split(",");
		
		IContainer parent = parsingFile.getParent();
		
		for(int i=0;i<resourcesNames.length;i++) {
			if( hasValidFeatureFile() ) {
				IFile theFile;
				if(parent instanceof IProject)
					theFile = ((IProject) parent).getFile(resourcesNames[i]);
				else
					theFile = ((IFolder) parent).getFile(resourcesNames[i]);
				
				results.add(new FeatureLocation(new Feature(featureID), theFile , null));
			}
			else { 
				IFolder theFolder;
				if(parent instanceof IProject)
					theFolder = ((IProject) parent).getFolder(resourcesNames[i]);
				else
					theFolder = ((IFolder) parent).getFolder(resourcesNames[i]);
				
				results.add(new FeatureLocation(new Feature(featureID), theFolder, null));
			}	
		}
				
		return results;		
	}
	
	
	/**
	 * Returns the message of parsing.
	 */
	public String getParsingMessage() {
		return parsingMessage.toString();
	}
}
