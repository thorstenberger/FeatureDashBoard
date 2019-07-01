/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.parsing;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.location.BlockLine;
import se.gu.featuredashboard.model.location.FeatureLocation;

public class InFileAnnotationParser {
	
	private StringBuilder parsingMessage = new StringBuilder("");

	public static final Pattern DEFAULT_LINE_ANNOTATION_REGEX = Pattern.compile(".*&line\\[(.+)\\].*");
	public static final Pattern DEFAULT_BEGIN_ANNOTATION_REGEX = Pattern.compile(".*&begin\\[(.+)\\].*");
	public static final Pattern DEFAULT_END_ANNOTATION_REGEX = Pattern.compile(".*&end\\[(.+)\\].*");

	// regex string must include feature ID within '[' and ']' braces
	private List<Pattern> regex_lineAnnotations = new ArrayList<>(Arrays.asList(DEFAULT_LINE_ANNOTATION_REGEX));
	private List<Pattern> regex_beginAnnotations = new ArrayList<>(Arrays.asList(DEFAULT_BEGIN_ANNOTATION_REGEX));
	private List<Pattern> regex_endAnnotations = new ArrayList<>(Arrays.asList(DEFAULT_END_ANNOTATION_REGEX));

	public void addRegex_LineAnnotation(String strRegex) {
		regex_lineAnnotations.add(Pattern.compile(strRegex));
	}

	public void setRegex_LineAnnotation(List<String> strRegexes) {
		regex_lineAnnotations.clear();
		strRegexes.stream().forEach(regex -> regex_lineAnnotations.add(Pattern.compile(regex)));
	}
	
	public boolean removeRegex_LineAnnotation(String strRegex) {
		return regex_lineAnnotations.remove(Pattern.compile(strRegex));
	}
	
	public void addRegex_beginAnnotation(Pattern strRegex) {
		regex_beginAnnotations.add(strRegex);
	}

	public void setRegex_beginAnnotation(List<String> strRegexes) {
		regex_beginAnnotations.clear();
		strRegexes.stream().forEach(regex -> regex_beginAnnotations.add(Pattern.compile(regex)));
	}

	public boolean removeRegex_beginAnnotation(String strRegex) {
		return regex_beginAnnotations.remove(Pattern.compile(strRegex));
	}

	public void addRegex_endAnnotation(Pattern strRegex) {
		regex_endAnnotations.add(strRegex);
	}

	public void setRegex_endAnnotation(List<String> strRegexes) {
		regex_endAnnotations.clear();
		strRegexes.stream().forEach(regex -> regex_endAnnotations.add(Pattern.compile(regex)));
	}

	public boolean removeRegex_endAnnotation(String strRegex) {
		return regex_endAnnotations.remove(Pattern.compile(strRegex));
	}

	public String getParsingMessage() {
		return parsingMessage.toString();
	}
	
	private List<FeatureLocation> readParseAnnotations(IFile parsingFile) {
		
		List<FeatureLocation> parsedLocations = new ArrayList<>();
		
		if(parsingFile==null || !parsingFile.exists())	//File creation can be eliminated by changing the input from string to IFile
			return parsedLocations;
		
		List<String> inputList = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(parsingFile.getLocation().toString()), Charset.defaultCharset())) {
			inputList = stream.collect(Collectors.toList());

		} catch (Exception e) {
			parsingMessage.append("Error in reading file: " + parsingFile.getLocation().toString()+"\n");
		} 	

		if (regex_beginAnnotations.size() != regex_endAnnotations.size()) {
			parsingMessage.append("Bad Regex Defenition - FeatureDashboard is skipping multiple line annotations\n");
			return parsedLocations; 
		}
		int multipleRegexSize = regex_beginAnnotations.size(); 

		//****************************** single line annotations parsing*******************************
		Map<String, List<Integer>> singleLineLocations = new HashMap<>();
		for (Pattern regex : regex_lineAnnotations) {
			Map<String, List<Integer>> newSingleLineAnnotations = parseRegex(inputList, regex);
			singleLineLocations = appendAnnotations(singleLineLocations, newSingleLineAnnotations);
		}
		//*********************************************************************************************
		
		//************************************ multiple line annotations parsing***********************
		Map<String, List<Integer>> multipleLineBeginLocations = new HashMap<>();
		Map<String, List<Integer>> multipleLineEndLocations = new HashMap<>();
		for (int i = 0; i < multipleRegexSize; i++) {
			Map<String, List<Integer>> newBeginLineAnnotations = parseRegex(inputList, regex_beginAnnotations.get(i));
			Map<String, List<Integer>> newEndLineAnnotations = parseRegex(inputList, regex_endAnnotations.get(i));

			// validation of the input
			boolean isValidAnnotation = true;
			if (newBeginLineAnnotations.size() != newEndLineAnnotations.size()) {
				isValidAnnotation = false;
			}	
			else{
				for (Map.Entry<String, List<Integer>> beginAnnotation : newBeginLineAnnotations.entrySet()) {
					if (!newEndLineAnnotations.containsKey(beginAnnotation.getKey()) || 
						 newEndLineAnnotations.get(beginAnnotation.getKey()).size() != beginAnnotation.getValue().size()) {
						isValidAnnotation = false;
						break;
					}
					
				}
			}
			if (isValidAnnotation) { // just skipping the invalid annotations
				multipleLineBeginLocations = appendAnnotations(multipleLineBeginLocations, newBeginLineAnnotations);
				multipleLineEndLocations = appendAnnotations(multipleLineEndLocations, newEndLineAnnotations);
			}
			else {
				parsingMessage.append("Bad Annotation - FeatureDashboard is skipping annotations of "+
						(i+1)+"th multiple line regex in the file: "+parsingFile.getLocation().toString()+"\n");
			}
		}
		//*********************************************************************************************

		//************************************** Merging Parsing Results*******************************
		Set<String> allFeatureIDs = new HashSet<>(singleLineLocations.keySet());
		allFeatureIDs.addAll(multipleLineBeginLocations.keySet());
		allFeatureIDs.addAll(multipleLineEndLocations.keySet());

		for (String id : allFeatureIDs) {
			ArrayList<BlockLine> blocks = new ArrayList<>();
			Feature feature = new Feature(id);

			if (singleLineLocations.get(id) != null)
				singleLineLocations.get(id).forEach(line -> {
					blocks.add(new BlockLine(line, line));
				});

			if(multipleLineBeginLocations.get(id) != null){ // if true, multipleLineEndLocations.get(i) is not null as well
				for(int i=0; i< multipleLineBeginLocations.get(id).size();i++){ // the same size of multipleLineEndLocations.get(i)
					blocks.add(new BlockLine(multipleLineBeginLocations.get(id).get(i), multipleLineEndLocations.get(id).get(i)));
				}
			}
			FeatureLocation newTrace = new FeatureLocation(feature, parsingFile, blocks);
			parsedLocations.add(newTrace);
		}
		//*********************************************************************************************

		setBlocksDepth(parsedLocations);
		return parsedLocations;
	}

	public ArrayList<FeatureLocation> readParseAnnotations(ArrayList<IFile> fileNames) {
		ArrayList<FeatureLocation> AllLocations = new ArrayList<>();
		parsingMessage = new StringBuilder("");
		
		if(fileNames == null)
			return AllLocations;
		for (IFile fileName : fileNames) {
			List<FeatureLocation> locations = readParseAnnotations(fileName);
			AllLocations.addAll(locations);
		}

		return AllLocations;
	}

	public static Map<String, List<Integer>> parseRegex(List<String> inputList, Pattern regex) {
		
		Map<String, List<Integer>> annotatedFeaturesLines = new HashMap<>();
		List<Integer> annotatedLines;

		for (int i = 0; i < inputList.size(); i++) {
			Matcher matcher = regex.matcher(inputList.get(i));
			if (matcher.matches()) {
				String featureID = matcher.group(1);
				annotatedLines = annotatedFeaturesLines.get(featureID);
				if (annotatedLines != null)
					annotatedLines.add(i + 1);
				else {
					annotatedLines = new ArrayList<>(Arrays.asList(i + 1));
					annotatedFeaturesLines.put(featureID, annotatedLines);
				}
			}
		}
		return annotatedFeaturesLines;
	}

	public static Map<String, List<Integer>> appendAnnotations(Map<String, List<Integer>> annotation1,
			Map<String, List<Integer>> annotation2) {
		// appending annotations for specific features in one file

		Map<String, List<Integer>> answer = new HashMap<>(annotation1);
		annotation2.forEach((featureID, lines) -> {
			if (answer.containsKey(featureID)) {
				answer.get(featureID).addAll(lines);
			} else {
				answer.put(featureID, lines);
			}
		});
		return answer;
	}

	private void setBlocksDepth(List<FeatureLocation> parsedLocations) {
		List<BlockLine> allBlocks = new ArrayList<BlockLine>();
		for(FeatureLocation location:parsedLocations) {
			for(BlockLine blockLine: location.getBlocklines())
				allBlocks.add(blockLine);
		}
		if(allBlocks.size()==0)
			return;
		allBlocks.sort((BlockLine e1, BlockLine e2)->(e1.getStartLine() - e2.getStartLine()));
		
		List<BlockLine> parentBlocks = new ArrayList<BlockLine>();
		for(int i=0;i<allBlocks.size();i++) {
			BlockLine newBlock = allBlocks.get(i);
			while(parentBlocks.size()!=0 && newBlock.getStartLine()>=parentBlocks.get(parentBlocks.size()-1).getEndLine())
				parentBlocks.remove(parentBlocks.size()-1);
			
			if(parentBlocks.size()==0) 
				newBlock.setInFileNestinDepth(1);
			else 
				newBlock.setInFileNestinDepth( parentBlocks.get(parentBlocks.size()-1).getInFileNestingDepth()+1 );
				
			parentBlocks.add(newBlock);		
		}
	}
}
