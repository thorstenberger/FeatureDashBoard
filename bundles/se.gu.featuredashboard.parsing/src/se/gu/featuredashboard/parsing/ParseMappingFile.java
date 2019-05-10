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
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.location.FeatureLocation;

public class ParseMappingFile {

	public static final String FEATUREFILE_FILE = "feature-file";
	public static final String FEATUREFOLDER_FILE = "feature-folder";
	public static final String VPFILE_FILE = "vp-files";
	public static final String VPFOLDER_FILE = "vp-folder";

	public static final String ERRORMESSAGE_COLONS = "Wrong amount of colons";
	public static final String ERRORMESSAGE_COMMAS = "To many commas";
	public static final String ERRORMESSAGE_LINES = "Resources not mapped to a feature";
	public static final String ERRORMESSAGE_NO_MAPPINGS = "No mappings to feature found";
	public static final String ERRORMESSAGE_DUPLICATED_FEATURE = "Same feature occures twice in file";

	public static final String WHITESPACE_REGEX = "\\s+";

	private ParseMappingFile() {
	}

	public static List<FeatureLocation> readMappingFile(IFile featureFile, IProject project)
			throws SyntaxException {

		if (!Files.exists(Paths.get(featureFile.getLocation().toString())))
			return Collections.emptyList();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(featureFile.getContents()))) {
			if (featureFile.getFileExtension().equals(VPFILE_FILE))
				return parseVPFileMappingFile(reader, featureFile, project);
			else if (featureFile.getFileExtension().equals(VPFOLDER_FILE) || featureFile.getFileExtension().equals(FEATUREFOLDER_FILE))
				return parseFolderMappingFile(reader, featureFile);
			return parseFeatureMappingFile(reader, featureFile, project);
		} catch (IOException | CoreException e) {
			throw new SyntaxException(e.getMessage());
		}

	}

	private static List<FeatureLocation> parseFolderMappingFile(BufferedReader reader, IFile featureFile)
			throws IOException, CoreException {
		List<FeatureLocation> locations = new ArrayList<FeatureLocation>();

		String line = null;

		while ((line = reader.readLine()) != null) {
			Feature feature = new Feature(line.replaceAll(WHITESPACE_REGEX, ""));
			locations.add(new FeatureLocation(feature, featureFile.getParent(), null));
			//Arrays.asList(featureFile.getParent().members()).forEach(resource->{
			//	locations.add(new FeatureLocation(feature, resource, null));
			//});
		}

		return locations;
	}

	private static List<FeatureLocation>  parseVPFileMappingFile(BufferedReader reader, IFile featureFile,
			IProject project) throws SyntaxException, IOException {
		
		List<FeatureLocation> locations = new ArrayList<FeatureLocation>();
		List<Feature> allFeatures = new ArrayList<Feature>();

		String currentLine = null;
		String nextLine = null;

		int lineCounter = 0;

		while (true) {
			lineCounter++;

			currentLine = reader.readLine();
			nextLine = reader.readLine();

			if (currentLine == null && nextLine == null)
				break;

			if (currentLine != null && nextLine == null)
				throw new SyntaxException(featureFile.getLocation().toString(), ERRORMESSAGE_LINES, lineCounter);

			String[] resources = currentLine.split(" ");
			Feature feature = new Feature(nextLine.replaceAll(WHITESPACE_REGEX, ""));
			
			if(allFeatures.contains(feature)) {
				throw new SyntaxException(ERRORMESSAGE_DUPLICATED_FEATURE, lineCounter);
			}
			
			allFeatures.add(feature);
			getResources(Arrays.asList(resources), lineCounter, featureFile, project).forEach(resource->{
				locations.add(new FeatureLocation(feature, resource, null));
			});
		}

		return locations;
	}

	private static List<FeatureLocation> parseFeatureMappingFile(BufferedReader reader, IFile featureFile,
			IProject project) throws SyntaxException, IOException {
		
		List<FeatureLocation> locations = new ArrayList<FeatureLocation>();
		List<Feature> allFeatures = new ArrayList<Feature>();

		String line = null;

		int lineCounter = 0;

		while ((line = reader.readLine()) != null) {

			lineCounter++;

			if (line.chars().filter(ch -> ch == ':').count() != 1)
				throw new SyntaxException(ERRORMESSAGE_COLONS, lineCounter);

			String[] lineElements = line.replaceAll(WHITESPACE_REGEX, "").split(":");

			if (lineElements.length < 2)
				throw new SyntaxException(ERRORMESSAGE_NO_MAPPINGS, lineCounter);

			String featureString = lineElements[0];

			if (featureString.equals(""))
				throw new SyntaxException(ERRORMESSAGE_NO_MAPPINGS, lineCounter);

			Feature feature = new Feature(featureString);

			if (allFeatures.contains(feature))
				throw new SyntaxException(ERRORMESSAGE_DUPLICATED_FEATURE, lineCounter);
			
			allFeatures.add(feature);
			String mappingElements[] = lineElements[1].split(",");

			if (mappingElements.length == 0)
				throw new SyntaxException(ERRORMESSAGE_COMMAS, lineCounter);

			getResources(Arrays.asList(mappingElements), lineCounter, featureFile, project).forEach(resource->{
				locations.add(new FeatureLocation(feature,resource,null));
			});
		}

		return locations;
	}

	private static List<IResource> getResources(List<String> elements, int lineCounter, IFile mappingFile,
			IProject project) throws SyntaxException {
		List<IResource> resources = new ArrayList<>();
		String featureFilePath = mappingFile.getFullPath().toString();

		for (String lineElement : elements) {
			if (lineElement.equals(""))
				throw new SyntaxException(ERRORMESSAGE_COMMAS, lineCounter);

			String newResourceLocation = featureFilePath.substring(0,
					(featureFilePath.length() - mappingFile.getName().length())) + lineElement;

			if (mappingFile.getFileExtension().equals(FEATUREFILE_FILE)
					|| mappingFile.getFileExtension().equals(VPFILE_FILE))
				resources.add(project.getWorkspace().getRoot().getFile(new Path(newResourceLocation)));
			else
				resources.add(project.getWorkspace().getRoot().getFolder(new Path(newResourceLocation)));
		}

		return resources;
	}

}
