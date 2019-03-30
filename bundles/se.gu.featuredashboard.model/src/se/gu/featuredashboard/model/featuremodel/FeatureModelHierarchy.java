package se.gu.featuredashboard.model.featuremodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FeatureModelHierarchy {
	private ArrayList<Feature> rootFeatures = new ArrayList<>();

	public ArrayList<Feature> getRootFeatures() {
		return rootFeatures;
	}

	public void setRootFeatures(ArrayList<Feature> topFeatures) {
		this.rootFeatures.clear();
		topFeatures.forEach(feature->{
			this.rootFeatures.add(new Feature(feature));
		});;
	}
	
	public List<Feature> getAllFeatures(){
		ArrayList<Feature> result = new ArrayList<Feature>();
		
		Stack<Feature> myStack = new Stack<Feature>();
		rootFeatures.forEach(feature->{myStack.push(feature);});
		
		while(!myStack.isEmpty()){
			Feature topFeature = myStack.pop();
			result.add(new Feature(topFeature));
			// supposing there is no features with the same id in the input feature model
			topFeature.getSubFeatures().forEach(feature->{myStack.add(feature);});
		}
			
		return result;
	}

}
