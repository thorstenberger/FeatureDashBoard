package se.gu.featuredashboard.InFileAnnotationHandler;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.plaf.InputMapUIResource;

public class InFileAnnotationParser {
	
	private Map<String, ArrayList<Integer[]>> FeatureLocations = new HashMap<>();
	
	boolean setFeatureLocation(String featureName, int lineNumber){
		if(lineNumber<1){ // Error in setting the location of a feature
			return false;
		}
		if(FeatureLocations.get(featureName)!=null){
			FeatureLocations.get(featureName).add(new Integer[]{lineNumber});
		}
		else{
			ArrayList<Integer[]> locations = new ArrayList<>();
			locations.add(new Integer[]{lineNumber});
			FeatureLocations.put(featureName, locations);
		}
		return true;
	}
	
	boolean setFeatureLocation(String featureName, int startingLineNumber, int endingLineNumber){
		if(startingLineNumber<1 || startingLineNumber>= endingLineNumber){
			System.err.println("Error in setting the location of a feature.");
			return false;
		}
		if(FeatureLocations.get(featureName)!=null){
			FeatureLocations.get(featureName).add(new Integer[]{startingLineNumber, endingLineNumber});
		}
		else{
			ArrayList<Integer[]> locations = new ArrayList<>();
			locations.add(new Integer[]{startingLineNumber, endingLineNumber});
			FeatureLocations.put(featureName, locations);
		}
		return true;

	}
	
	public ArrayList<Integer[]> getLocations(String featureName){
		ArrayList<Integer[]> answer = new ArrayList<>();
		FeatureLocations.get(featureName).forEach((location)->{
			if(location.length==1)
				answer.add(new Integer[]{location[0]} );
			else if(location.length==2)
				answer.add(new Integer[]{location[0], location[1]} );
			else{
				System.err.println("Error: Trying to reach a location with a not valid length.");
				return;
			}
				
		});
		return answer;
	}
	
 	public ArrayList<String> getAllFeatures(){
		ArrayList<String> features = new ArrayList<>();
		FeatureLocations.forEach((feature, locations)->{
			features.add(new String(feature));
		});
		 return features;
	}
	
	public Map<String, ArrayList<Integer[]>> getAllFeatureLocations(){
		Map<String, ArrayList<Integer[]>> answer = new HashMap<>();
		FeatureLocations.forEach((feature, locations)->{
			answer.put(feature, getLocations(feature));
		});
		return answer;
	}
	
	public void clearFeaturesAndLocations(){
		FeatureLocations.clear();
	}

	@Override
	public String toString(){
		StringBuilder answer = new StringBuilder();
		FeatureLocations.forEach((feature, locations)->{
			answer.append("Feature:" + feature+" in locations: ");
			getLocations(feature).forEach(location->{
				if(location.length==1)
					answer.append(location[0]+" |");
				if(location.length==2)
					answer.append("("+location[0]+","+location[1]+") |");
			});
			answer.append("\n");
		});
		return answer.toString();
	}

	public boolean readInFileAnnotation(String fileName){
		List<String> inputList = new ArrayList<>();
		Map<String, ArrayList<Integer>>  oneLineLocations = new HashMap<>();
		Map<String, ArrayList<Integer>>  multipleLineBeginLocations = new HashMap<>();
		Map<String, ArrayList<Integer>>  multipleLineEndLocations = new HashMap<>();

		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
			inputList = stream.collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}
			
		for(int i=0;i<inputList.size();i++){
			String fName;
			ArrayList<Integer> fLocaions;
				
			if(inputList.get(i).matches("\\p{Space}*//&line\\[.+\\]\\p{Space}*")){
				fName = inputList.get(i).split("\\[")[1].split("\\]")[0];
				fLocaions = oneLineLocations.get(fName);
				if(fLocaions!=null)
					fLocaions.add(i+1);
				else{
					fLocaions = new ArrayList<>(Arrays.asList(i+1));
					oneLineLocations.put(fName, fLocaions);
				}
			}
			
			if(inputList.get(i).matches("\\p{Space}*//&begin\\[.+\\]\\p{Space}*")){
				fName = inputList.get(i).split("\\[")[1].split("\\]")[0];
				fLocaions = multipleLineBeginLocations.get(fName);
				if(fLocaions!=null)
					fLocaions.add(i+1);
				else{
					fLocaions = new ArrayList<>(Arrays.asList(i+1));
					multipleLineBeginLocations.put(fName, fLocaions);
				}
			}
			

			else if(inputList.get(i).matches("\\p{Space}*//&end\\[.+\\]\\p{Space}*")){
				fName = inputList.get(i).split("\\[")[1].split("\\]")[0];
				fLocaions = multipleLineEndLocations.get(fName);
				if(fLocaions!=null)
					fLocaions.add(i+1);
				else{
					fLocaions = new ArrayList<>(Arrays.asList(i+1));
					multipleLineEndLocations.put(fName, fLocaions);
				}
			}		
		}
		
		oneLineLocations.forEach((nameOfFeature,locations)->{
			locations.forEach(location->setFeatureLocation(nameOfFeature, location));
		});
		
		multipleLineBeginLocations.forEach((nameOfFeature,beginLocations)->{
			ArrayList<Integer> endLocations = multipleLineEndLocations.get(nameOfFeature);
			if(endLocations==null || endLocations.size()!= beginLocations.size())
				System.out.println("Mismatch of feature location Begins and Ends for feature "+nameOfFeature );			
			else
				for(int i=0;i<beginLocations.size();i++){
					setFeatureLocation(nameOfFeature, beginLocations.get(i), endLocations.get(i));
				}
		});
		
		return true;
	}
	
}
