package se.gu.featuredashboard.core;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.featuremodel.FeatureModelHierarchy;
import se.gu.featuredashboard.model.location.FeatureLocation;

public class FeatureLocationDashboard {

	private List<FeatureLocation> traces = new ArrayList<>();
	private FeatureModelHierarchy featureModelHierarchy = new FeatureModelHierarchy();

	private List<IResource> notExistentResources = new ArrayList<IResource>();
	private List<Feature> featuresNotInFeatureModel = new ArrayList<Feature>();
	private List<Feature> featuresOfFeatureModel =  new ArrayList<Feature>();
	
	// **************************Traces*******************************
	
	public void addTraces(List<FeatureLocation> newLocations) {
		if(newLocations == null)
			return;
		newLocations.forEach(location->{
			addTrace(location);
		});
	}
	
	public void addTrace(FeatureLocation newLocation) {
		if(newLocation==null || traceExists(newLocation.getFeature(), newLocation.getResource()))
			return;
		traces.add(newLocation);
		if(!newLocation.getResource().exists())
			notExistentResources.add(newLocation.getResource());
		
		if(!featuresOfFeatureModel.contains(newLocation.getFeature()) && 
		   !featuresNotInFeatureModel.contains(newLocation.getFeature()))
			featuresNotInFeatureModel.add(newLocation.getFeature());
	}
	
	public void clearAllTraces() {
		traces.clear();
		notExistentResources.clear();
		featuresNotInFeatureModel.clear();
	}

	public List<FeatureLocation> getTraces(List<Object> features, List<Object> resources){
		List<FeatureLocation> answer = new ArrayList<FeatureLocation>();
		for (FeatureLocation featureLocation : traces) {
			
			if(features.contains(featureLocation.getFeature()))
				answer.add(featureLocation);
			else {
				if(notExistentResources.contains(featureLocation.getResource())) {
					for (Object resource : resources) {
						if(((IResource) resource).getProjectRelativePath().equals(featureLocation.getResource().getProjectRelativePath())) {
							answer.add(featureLocation);
							break;
						}
					}
				}
				else {
					for (Object resource : resources) {
						if(((IResource) resource).getProjectRelativePath().isPrefixOf(featureLocation.getResource().getProjectRelativePath())) {
							answer.add(featureLocation);
							break;
						}	
					}
				}
			}
		}

		return answer;
	}
	
	public boolean traceExists(Feature feature, IResource resource) {
		if(resource instanceof IFile) {
			for (FeatureLocation location : traces) {
				if (location.getFeature().equals(feature) && location.getResource().equals(resource) )
					return true;
			}
		}
		else if(resource instanceof IFolder) {
			for (FeatureLocation location : traces) {
				if(resource.getProjectRelativePath().isPrefixOf(location.getResource().getProjectRelativePath()))
					return true;
			}
		}
		
		return false;
	}

	public List<IResource> getNotExistentResources(){
		return notExistentResources;
	}
	
	// ***********************Feature Model*****************************
	
	public boolean setFeatureModel(FeatureModelHierarchy featureModel) {
		if(featureModel==null) {
			featureModelHierarchy = new FeatureModelHierarchy();
			return false;
		}
		featureModelHierarchy = featureModel;
		featuresOfFeatureModel = featureModel.getAllFeatures();
		return true;
	}
	
	public List<Feature> getRootFeaturesOfFeatureModel(){
		return featureModelHierarchy.getRootFeatures();
	}
	
	public List<Feature> getFeaturesNotInFeatureModel(){
		return featuresNotInFeatureModel;
		/*
		List<Feature> answer = new ArrayList<Feature>();
		List<Feature> featuresOfFM = getFeaturesOfFeatureModel();
		getFeaturesOfTraces().forEach(feature->{
			if(!featuresOfFM.contains(feature))
				answer.add(feature);
		});
		return answer;
		*/
	}
	
	public List<Feature> getFeaturesOfFeatureModel() {
		return featuresOfFeatureModel;
	}
	
}
