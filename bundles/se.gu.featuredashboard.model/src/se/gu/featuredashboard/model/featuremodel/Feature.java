package se.gu.featuredashboard.model.featuremodel;

import java.util.ArrayList;
import java.util.Objects;

public class Feature implements Serializable {
public class Feature {
	private final String featureID;
	private ArrayList<Feature> subFeatures = new ArrayList<>();
	

	public Feature(String featureID) {
		this.featureID = featureID;
	}

	public Feature(String featureID, ArrayList<Feature> subFeatures) {
		this.featureID = featureID;
		setSubFeatures(subFeatures);
	}
	
	public Feature(Feature f) {
		featureID = f.getFeatureID();
		setSubFeatures(f.getSubFeatures());
	}

	public String getFeatureID() {
		return featureID;
	}

	public ArrayList<Feature> getSubFeatures(){
		ArrayList<Feature> result = new ArrayList<Feature>();
		this.subFeatures.forEach(subFeature->{
			result.add(new Feature(subFeature));
		});
		
		return result;
	}

	public void setSubFeatures(ArrayList<Feature> subFeatures){
		this.subFeatures.clear();
		subFeatures.forEach(subFeature->{
			this.subFeatures.add(new Feature(subFeature));
		});
	}
	
	public void addToSubFeatures(Feature newFeature) {
		//adding the same newFeature not a copy of that used in constructing feature model
		subFeatures.add(newFeature);		
	}

	@Override
	public boolean equals(Object aFeature) {
		if (!(aFeature instanceof Feature))
			return false;
		
		Feature toCompare = (Feature) aFeature;
		
		if (featureID.equals(toCompare.getFeatureID()))
			return true;
		
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(featureID);
	}

}
