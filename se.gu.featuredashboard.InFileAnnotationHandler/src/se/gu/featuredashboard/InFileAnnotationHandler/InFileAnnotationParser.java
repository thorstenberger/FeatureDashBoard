package se.gu.featuredashboard.InFileAnnotationHandler;

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

import se.gu.featuredashboard.LocationHandler.Locations.AnnotatedLocations;
import se.gu.featuredashboard.LocationHandler.Locations.Features_Locations;

public class InFileAnnotationParser {

	private static final String DEFAULT_LINE_ANNOTATION_REGEX = "\\p{Space}*//&line\\[.+\\]\\p{Space}*";
	private static final String DEFAULT_BEGIN_ANNOTATION_REGEX = "\\p{Space}*//&begin\\[.+\\]\\p{Space}*";
	private static final String DEFAULT_END_ANNOTATION_REGEX = "\\p{Space}*//&end\\[.+\\]\\p{Space}*";

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

	public Features_Locations readParseAnnotations(String fileName) {
		if (regex_beginAnnotations.size() != regex_endAnnotations.size())
			return null; // check input inconsistency

		Map<String, ArrayList<Integer>> singleLineLocations = new HashMap<>();
		Map<String, ArrayList<Integer>> multipleLineBeginLocations = new HashMap<>();
		Map<String, ArrayList<Integer>> multipleLineEndLocations = new HashMap<>();

		for (String regexStr : regex_lineAnnotations) {
			Map<String, ArrayList<Integer>> newSingleLineAnnotations = parseRegex(fileName, regexStr);
			singleLineLocations = appendLineAnnotations(singleLineLocations, newSingleLineAnnotations);
		}

		int multipleRegexSize = regex_beginAnnotations.size();
		// equals regex_endAnnotations.size()

		for (int i = 0; i < multipleRegexSize; i++) {
			Map<String, ArrayList<Integer>> newBeginLineAnnotations = parseRegex(fileName,
					regex_beginAnnotations.get(i));
			Map<String, ArrayList<Integer>> newEndLineAnnotations = parseRegex(fileName, regex_endAnnotations.get(i));
			if (newBeginLineAnnotations.size() != newEndLineAnnotations.size())
				continue; // skipping the regex annotations with not equal
							// number of begins and ends
			multipleLineBeginLocations = appendLineAnnotations(multipleLineBeginLocations, newBeginLineAnnotations);
			multipleLineEndLocations = appendLineAnnotations(multipleLineEndLocations, newEndLineAnnotations);
		}

		List<String> allFeatures = new ArrayList<>(singleLineLocations.keySet());
		multipleLineBeginLocations.keySet().forEach(key -> {
			if (!allFeatures.contains(key))
				allFeatures.add(key);
		});
		multipleLineEndLocations.keySet().forEach(key -> {
			if (!allFeatures.contains(key))
				allFeatures.add(key);
		});

		Features_Locations locations = new Features_Locations();
		for (String id : allFeatures) {

			locations.setTrace_toAnnotation(id, fileName, new AnnotatedLocations(singleLineLocations.get(id),
					multipleLineBeginLocations.get(id), multipleLineEndLocations.get(id)));
		}

		return locations;
	}

	public Features_Locations readParseAnnotations(List<String> fileNames) {
		Features_Locations AllLocations = new Features_Locations();
		for (String fileName : fileNames) {
			Features_Locations locations = readParseAnnotations(fileName);
			AllLocations.addAnnotationTracesInOtherFiles(locations);
		}

		return AllLocations;
	}

	public static Map<String, ArrayList<Integer>> parseRegex(String fileName, String regexString) {
		List<String> inputList = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
			inputList = stream.collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}

		ArrayList<Integer> annotatedLines;
		Map<String, ArrayList<Integer>> annotatedFeaturesLines = new HashMap<>();
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

	public static Map<String, ArrayList<Integer>> appendLineAnnotations(Map<String, ArrayList<Integer>> annotation1,
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
