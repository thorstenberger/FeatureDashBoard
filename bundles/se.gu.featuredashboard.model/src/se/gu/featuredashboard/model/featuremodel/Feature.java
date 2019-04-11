package se.gu.featuredashboard.model.featuremodel;

import java.util.ArrayList;
import java.util.Objects;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

public class Feature {
	private final String featureID;
	private ArrayList<Feature> subFeatures = new ArrayList<>();
	private IProject project;
	private IFile featureModelFile;

	public Feature(String featureID) {
		this.featureID = featureID;
	}

	public Feature(String featureID, ArrayList<Feature> subFeatures) {
		this.featureID = featureID;
		setSubFeatures(subFeatures);
	}
	
	public Feature(String featureID, ArrayList<Feature> subFeatures, IProject project, IFile featureModelFile) {
		this.featureID = featureID;
		setSubFeatures(subFeatures);
		setProject(project);
		setFeatureModelFile(featureModelFile);
	}
	
	public Feature(Feature f) {
		featureID = f.getFeatureID();
		setSubFeatures(f.getSubFeatures());
		setProject(f.getproject());
		setFeatureModelFile(f.getFeatureModelFile());
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
	
	public ArrayList<Feature> getSubTreeElements() {
		ArrayList<Feature> subTree = new ArrayList<Feature>();
		if(subFeatures.isEmpty())
			return subTree;
		for(Feature subFeature:subFeatures) {
			subTree.add(subFeature);
			subTree.addAll(subFeature.getSubTreeElements());
		}
		return subTree;
	}

	public void setProject(IProject project) {
		this.project = project;
	}
	
	public IProject getproject() {
		return project;
	}
	
	public void setFeatureModelFile(IFile featureModelFile) {
		this.featureModelFile = featureModelFile;
	}

	public IFile getFeatureModelFile() {
		return featureModelFile;
	}
	
	@Override
	public Feature clone() {
		return new Feature(this.featureID);
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
