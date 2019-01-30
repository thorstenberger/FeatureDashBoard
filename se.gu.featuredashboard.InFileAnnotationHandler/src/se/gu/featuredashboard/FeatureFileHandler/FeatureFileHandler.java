package se.gu.featuredashboard.FeatureFileHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FeatureFileHandler {

	public static Map<String, ArrayList<String>> readFeatureFile(String featureFileAddress) {

		if (!(new File(featureFileAddress).isFile()) || !(featureFileAddress.split("\\.")[1].equals("feature-file")))
			return null;

		List<String> inputList = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(featureFileAddress))) {
			inputList = stream.collect(Collectors.toList());

		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}

		Map<String, ArrayList<String>> featureFiles = new HashMap<>();

		String lastMentionedFeature;
		ArrayList<String> addresses = new ArrayList<>();

		for (String line : inputList) {
			line = line.replaceAll("\\s+", ""); // removing white spaces
			if (line.split(":").length > 1) { // feature exists in the line
				lastMentionedFeature = line.split(":")[0];
				addresses = new ArrayList<>();
				featureFiles.put(lastMentionedFeature, addresses);
				line = line.split(":")[1];
			}
			addresses.add(line.split(",")[0]);
		}

		return featureFiles;
	}

}
