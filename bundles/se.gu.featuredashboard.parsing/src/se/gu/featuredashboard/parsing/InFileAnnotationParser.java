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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.location.BlockLine;
import se.gu.featuredashboard.model.location.FeatureLocation;

public class InFileAnnotationParser {
	
	private StringBuilder parsingMessage = new StringBuilder("");

	public static final String DEFAULT_LINE_ANNOTATION_REGEX = "\\p{Space}*//&line\\[.+\\]\\p{Space}*";
	public static final String DEFAULT_BEGIN_ANNOTATION_REGEX = "\\p{Space}*//&begin\\[.+\\]\\p{Space}*";
	public static final String DEFAULT_END_ANNOTATION_REGEX = "\\p{Space}*//&end\\[.+\\]\\p{Space}*";

	// regex string must include feature ID within '[' and ']' braces
	private List<String> regex_lineAnnotations = new ArrayList<>(Arrays.asList(DEFAULT_LINE_ANNOTATION_REGEX));
	private List<String> regex_beginAnnotations = new ArrayList<>(Arrays.asList(DEFAULT_BEGIN_ANNOTATION_REGEX));
	private List<String> regex_endAnnotations = new ArrayList<>(Arrays.asList(DEFAULT_END_ANNOTATION_REGEX));

	public void addRegex_LineAnnotation(String strRegex) {
		regex_lineAnnotations.add(strRegex);
	}

	public void setRegex_LineAnnotation(List<String> strRegexes) {
		regex_lineAnnotations.clear();
		regex_lineAnnotations.addAll(strRegexes);
	}

	public boolean removeRegex_LineAnnotation(String strRegex) {
		return regex_lineAnnotations.remove(strRegex);
	}
	
	public void addRegex_beginAnnotation(String strRegex) {
		regex_beginAnnotations.add(strRegex);
	}

	public void setRegex_beginAnnotation(List<String> strRegexes) {
		regex_beginAnnotations.clear();
		regex_beginAnnotations.addAll(strRegexes);
	}

	public boolean removeRegex_beginAnnotation(String strRegex) {
		return regex_beginAnnotations.remove(strRegex);
	}

	public void addRegex_endAnnotation(String strRegex) {
		regex_endAnnotations.add(strRegex);
	}

	public void setRegex_endAnnotation(List<String> strRegexes) {
		regex_endAnnotations.clear();
		regex_endAnnotations.addAll(strRegexes);
	}

	public boolean removeRegex_endAnnotation(String strRegex) {
		return regex_endAnnotations.remove(strRegex);
	}

	public String getParsingMessage() {
		return parsingMessage.toString();
	}
	
	private ArrayList<FeatureLocation> readParseAnnotations(IFile parsingFile) {
		
		ArrayList<FeatureLocation> parsedLocations = new ArrayList<>();
		
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
		Map<String, ArrayList<Integer>> singleLineLocations = new HashMap<>();
		for (String regexStr : regex_lineAnnotations) {
			Map<String, ArrayList<Integer>> newSingleLineAnnotations = parseRegex(inputList, regexStr);
			singleLineLocations = appendAnnotations(singleLineLocations, newSingleLineAnnotations);
		}
		//*********************************************************************************************
		
		//************************************ multiple line annotations parsing***********************
		Map<String, ArrayList<Integer>> multipleLineBeginLocations = new HashMap<>();
		Map<String, ArrayList<Integer>> multipleLineEndLocations = new HashMap<>();
		for (int i = 0; i < multipleRegexSize; i++) {
			Map<String, ArrayList<Integer>> newBeginLineAnnotations = parseRegex(inputList, regex_beginAnnotations.get(i));
			Map<String, ArrayList<Integer>> newEndLineAnnotations = parseRegex(inputList, regex_endAnnotations.get(i));

			// validation of the input
			boolean isValidAnnotation = true;
			if (newBeginLineAnnotations.size() != newEndLineAnnotations.size()) {
				isValidAnnotation = false;
			}	
			else{
				for (Map.Entry<String, ArrayList<Integer>> beginAnnotation : newBeginLineAnnotations.entrySet()) {
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

		return parsedLocations;
	}

	public ArrayList<FeatureLocation> readParseAnnotations(ArrayList<IFile> fileNames) {
		ArrayList<FeatureLocation> AllLocations = new ArrayList<>();
		parsingMessage = new StringBuilder("");
		
		if(fileNames == null)
			return AllLocations;
		for (IFile fileName : fileNames) {
			ArrayList<FeatureLocation> locations = readParseAnnotations(fileName);
			AllLocations.addAll(locations);
		}

		return AllLocations;
	}

	public static Map<String, ArrayList<Integer>> parseRegex(List<String> inputList, String regexString) {
		
		Map<String, ArrayList<Integer>> annotatedFeaturesLines = new HashMap<>();
		ArrayList<Integer> annotatedLines;

		for (int i = 0; i < inputList.size(); i++) {
			if (inputList.get(i).matches(regexString)) {
				String featureID = inputList.get(i).split("\\[")[1].split("\\]")[0];
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

	public static Map<String, ArrayList<Integer>> appendAnnotations(Map<String, ArrayList<Integer>> annotation1,
			Map<String, ArrayList<Integer>> annotation2) {
		// appending annotations for specific features in one file

		Map<String, ArrayList<Integer>> answer = new HashMap<>(annotation1);
		annotation2.forEach((featureID, lines) -> {
			if (answer.containsKey(featureID)) {
				answer.get(featureID).addAll(lines);
			} else {
				answer.put(featureID, lines);
			}
		});
		return answer;
	}

}
