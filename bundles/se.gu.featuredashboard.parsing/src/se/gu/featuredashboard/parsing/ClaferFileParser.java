/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.parsing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.featuremodel.FeatureModelHierarchy;

public class ClaferFileParser {
	private IFile parsingFile;
	private StringBuilder parsingMessage = new StringBuilder(""); 
	
	private enum ClaferSentence{
		ENUM_DECL,
		ELEMENT_DECL,
		CLAFER,
		ASSERTION,
		CONSTRAINT,
		GOAL
	}
	
	public ClaferFileParser(IFile parsingFile){
		this.parsingFile = parsingFile;
	}
	
	public void setParsingFileAddress(IFile parsingFile){
		this.parsingFile = parsingFile;
	}
	
	public IFile getParsingFileAddress(){
		return parsingFile;
	}
	
	public boolean hasValidClaferFile() {
		if (parsingFile!=null && parsingFile.exists() && parsingFile.getFileExtension().equals("cfr"))
			return true;
		return false;
	}
		
	public ClaferSentence getClaferSentenceType(String sentenceLine) {
		String[] tokens = sentenceLine.split("\\s+");
		if(tokens.length==0)	// when the line is only white space
			return null;
		String firstNonEmptyToken = tokens[0];
		if(tokens[0].isEmpty()&& tokens.length>1) { // when the line starts with white space
			firstNonEmptyToken = tokens[1];
		}
		
	    try{ //when the first non-empty token is integer
	            Integer.parseInt(firstNonEmptyToken);
				return ClaferSentence.CLAFER;
	    }  
	    catch (NumberFormatException e) {}
		
	    if(firstNonEmptyToken.matches("[a-zA-Z][a-zA-Z_0-9']*")) //when the first non-empty token is PosIdent
	    	return ClaferSentence.CLAFER;
	    
		switch(firstNonEmptyToken) {
		case "enum":
			return ClaferSentence.ENUM_DECL;
		case "`":
			return ClaferSentence.ELEMENT_DECL;
		case "assert":
			return ClaferSentence.ASSERTION;
		case "[":
			return ClaferSentence.CONSTRAINT;
		case "min":
		case "max":
		case "minimize":
		case "maximize":
			return ClaferSentence.GOAL;
		case "abstract":
		case "initial":
		case "final":
		case "finalref":
		case "finaltarget":
		case "xor":
		case "or":
		case "mux":
		case "opt":
			return ClaferSentence.CLAFER;
		default:
			return null;			
		}
	}	

	public FeatureModelHierarchy readParse() {
		if(!hasValidClaferFile())
			return new FeatureModelHierarchy();
		
		List<String> inputList = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(parsingFile.getLocation().toString()))) {
			inputList = stream.collect(Collectors.toList());
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return new FeatureModelHierarchy();
		}
		
		FeatureStack featureStack = new FeatureStack();
		
		for (String line : inputList) {
			if(ClaferSentence.CLAFER.equals(getClaferSentenceType(line))) {
				// just skip the other sentences
				int level =0;
				while(line.charAt(level) == '\t')
					level++;
				
				String[] tokens = line.split("\\s+");
				ArrayList<String> claferTerminals = new ArrayList<>(Arrays.asList(
						"abstract","initial","final","finalref","finaltarget","xor","or","mux","opt"));

				int i=0;
				if( tokens[i].isEmpty())
					i++;
				//checking the length is needed
				while(claferTerminals.contains(tokens[i]))
					i++;
				
				Feature_Level fl = new Feature_Level(new Feature(tokens[i]), level);
				featureStack.push(fl);			
			}	
		}
		
		FeatureModelHierarchy result = new FeatureModelHierarchy();
		result.setRootFeatures(featureStack.getRoots());
			
		return result;
	}
	
	private class Feature_Level {
		private Feature feature;
		private int level;
		
		Feature_Level(Feature feature, int level){
			this.feature = feature;
			this.level = level;
		}

	}

	private class FeatureStack {
		private Stack<Feature_Level> stack = new Stack<Feature_Level>();
		
		private void push(Feature_Level newOne) {
			if(stack.isEmpty())
				stack.push(newOne);
			else {
				Feature_Level top = stack.get(stack.size()-1);
				while(newOne.level<=top.level) {
					if(top.level==0)
						break;
					stack.pop();
					top = stack.get(stack.size()-1);
				}
				if(newOne.level>top.level) 
					top.feature.addToSubFeatures(newOne.feature);
				stack.push(newOne);
			}
		}
		
		private ArrayList<Feature> getRoots() {
			Feature_Level endNode = new Feature_Level(new Feature("END_NODE"), 0);
			push(endNode); //removing non-root nodes from the stack
			stack.pop();   //removing 'END_NODE' from the stack
			
			ArrayList<Feature> roots = new ArrayList<Feature>();
			while(!stack.isEmpty()) {
				roots.add(stack.pop().feature);
			}
			return roots;		
		}	
	}	

	public String getParsingMessage() {
		return parsingMessage.toString();
	}
}





