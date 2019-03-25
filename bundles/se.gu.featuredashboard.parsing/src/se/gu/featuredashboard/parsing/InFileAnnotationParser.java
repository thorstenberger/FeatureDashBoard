package se.gu.featuredashboard.parsing;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
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

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.location.BlockLine;
import se.gu.featuredashboard.model.location.FeatureAnnotationsLocation;

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

	public ArrayList<FeatureAnnotationsLocation> readParseAnnotations(String fileName) {
		if (regex_beginAnnotations.size() != regex_endAnnotations.size())
			return null; // check input inconsistency
		int multipleRegexSize = regex_beginAnnotations.size();  // equals regex_endAnnotations.size()
		ArrayList<FeatureAnnotationsLocation> parsedLocations = new ArrayList<>();

		// single line annotations parsing
		Map<String, ArrayList<Integer>> singleLineLocations = new HashMap<>();
		for (String regexStr : regex_lineAnnotations) {
			Map<String, ArrayList<Integer>> newSingleLineAnnotations = parseRegex(fileName, regexStr);
			singleLineLocations = appendAnnotations(singleLineLocations, newSingleLineAnnotations);
		}

		Map<String, ArrayList<Integer>> multipleLineBeginLocations = new HashMap<>();
		Map<String, ArrayList<Integer>> multipleLineEndLocations = new HashMap<>();
		for (int i = 0; i < multipleRegexSize; i++) {
			Map<String, ArrayList<Integer>> newBeginLineAnnotations = parseRegex(fileName, regex_beginAnnotations.get(i));
			Map<String, ArrayList<Integer>> newEndLineAnnotations = parseRegex(fileName, regex_endAnnotations.get(i));

			// validation of the input
			boolean isValidAnnotation = false;
			if (newBeginLineAnnotations.size() == newEndLineAnnotations.size()) {
				for (Map.Entry<String, ArrayList<Integer>> beginAnnotation : newBeginLineAnnotations.entrySet()) {
					if (!newEndLineAnnotations.containsKey(beginAnnotation.getKey())) {
						isValidAnnotation = false;
						break;
					}
				}
				isValidAnnotation = true;
			}
			if (isValidAnnotation) { // just skipping the invalid annotations
				multipleLineBeginLocations = appendAnnotations(multipleLineBeginLocations, newBeginLineAnnotations);
				multipleLineEndLocations = appendAnnotations(multipleLineEndLocations, newEndLineAnnotations);
			}
		}

		Set<String> allFeatureIDs = new HashSet<>(singleLineLocations.keySet());
		allFeatureIDs.addAll(multipleLineBeginLocations.keySet());
		allFeatureIDs.addAll(multipleLineEndLocations.keySet());

		for (String id : allFeatureIDs) {
			ArrayList<BlockLine> blocks = new ArrayList<>();
			Feature feature = new Feature(id);
			File file = new File(fileName);

			if (singleLineLocations.get(id) != null)
				singleLineLocations.get(id).forEach(line -> {
					blocks.add(new BlockLine(line, line));
				});

			if(multipleLineBeginLocations.get(id) != null){ // multipleLineEndLocations.get(i) is not null as well
				for(int i=0; i< multipleLineBeginLocations.get(id).size();i++){ // the same size of multipleLineEndLocations.get(i)
					blocks.add(new BlockLine(multipleLineBeginLocations.get(id).get(i), multipleLineEndLocations.get(id).get(i)-1));
				}
			}

			parsedLocations.add(new FeatureAnnotationsLocation(feature, file, blocks));
		}

		return parsedLocations;
	}

	public ArrayList<FeatureAnnotationsLocation> readParseAnnotations(List<String> fileNames) {
		ArrayList<FeatureAnnotationsLocation> AllLocations = new ArrayList<>();
		for (String fileName : fileNames) {
			ArrayList<FeatureAnnotationsLocation> locations = readParseAnnotations(fileName);
			AllLocations.addAll(locations);
		}

		return AllLocations;
	}

	public static Map<String, ArrayList<Integer>> parseRegex(String fileName, String regexString) {
		List<String> inputList = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(fileName), Charset.defaultCharset())) {
			inputList = stream.collect(Collectors.toList());

		} catch (UncheckedIOException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
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
