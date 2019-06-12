/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.model.location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.featuremodel.FeatureModelHierarchy;
import se.gu.featuredashboard.model.location.FeatureLocation;
import se.gu.featuredashboard.model.metrics.FeatureResourceMetrics;
import se.gu.featuredashboard.model.metrics.MetricsCalculator;
import se.gu.featuredashboard.model.metrics.ResourceMetrics;

/**
 * This class includes the feature location dashboard data of a project and
 * also some methods to access and manipulate it
 */
public class ProjectData {

	private IProject project;
	private List<FeatureLocation> traces = new ArrayList<>();
	private FeatureModelHierarchy featureModelHierarchy = new FeatureModelHierarchy();
	private List<IResource> notExistentResources = new ArrayList<IResource>();
	private List<Feature> featuresNotInFeatureModel = new ArrayList<Feature>();
	private List<Feature> featuresOfFeatureModel = new ArrayList<Feature>();
	
	MetricsCalculator metricsCalculator;
	
	public ProjectData(){
		metricsCalculator = new MetricsCalculator(this);
	}

	/**
	 * Sets the project that all the data belong to that.
	 *
	 * @param project 
	 *		the project all the data belongs to that
	 */
	public void setProject(IProject project) {
		this.project = project;
	}

	/**
	 * Gets the project which the data belongs to that.
	 *
	 * @return project 
	 * 		the project all the data belongs to that
	 */
	public IProject getProject() {
		return project;
	}

	// **************************Traces*******************************
	
	/**
	 * Appends new feature traces to the list of feature traces.
	 *
	 * @param newLocations 
	 * 		List of feature traces that will be appended
	 */
	public void addTraces(List<FeatureLocation> newLocations) {
		if (newLocations == null)
			return;
		newLocations.forEach(location -> {
			addTrace(location);
		});
	}

	/**
	 * Appends a new feature trace to the list of feature traces.
	 * If the traces is null of if it is already exists, nothing will happen.
	 *
	 * @param newLocation 
	 * 		 feature trace that will be appended
	 */
	public void addTrace(FeatureLocation newLocation) {
		if (newLocation == null || traceExists(newLocation.getFeature(), newLocation.getResource()))
			return;
		traces.add(newLocation);
		if (!newLocation.getResource().exists())
			notExistentResources.add(newLocation.getResource());

		if (!featuresOfFeatureModel.contains(newLocation.getFeature())
				&& !featuresNotInFeatureModel.contains(newLocation.getFeature()))
			featuresNotInFeatureModel.add(newLocation.getFeature());
	}

	/**
	 * Clears all of the traces.
	 */
	public void clearAllTraces() {
		traces.clear();
		notExistentResources.clear();
		featuresNotInFeatureModel.clear();
	}

	/**
	 * Gets the feature traces corresponding to specific features or resources 
	 * @param features
	 * 		List of features
	 * @param resources
	 * 		List of resources
	 * @return
	 * 		List of feature traces which the feature or resource of each trace of that is a member of the
	 * 		list of features or the list of resources specified in the input	
	 */
	public List<FeatureLocation> getTraces(List<Object> features, List<Object> resources) {
		List<FeatureLocation> answer = new ArrayList<FeatureLocation>();
		for (FeatureLocation featureLocation : traces) {
			
			if (features.contains(featureLocation.getFeature())) {
				answer.add(featureLocation);
				continue;
			}
			
			if( !featureLocation.getBlocklines().isEmpty() || 
				(featureLocation.getResource() instanceof IFile) ||
				!featureLocation.getResource().exists()) {
				for (Object resource : resources) {
					if (((IResource) resource).getProjectRelativePath()
							.equals(featureLocation.getResource().getProjectRelativePath())) {
						answer.add(featureLocation);
						break;
					}
				}
			}
			else { // the trace is to a folder or project
				for (Object resource : resources) {
					if(featureLocation.getResource().getProjectRelativePath()
							.isPrefixOf(((IResource) resource).getProjectRelativePath())) {
						FeatureLocation newLocation = new FeatureLocation(featureLocation.getFeature(),(IResource)resource,null);
						if(!answer.contains(newLocation))
							answer.add(newLocation);
					}
				}
			}		
		}

		return answer;
	}

	public List<FeatureLocation> getDirectFileTraces(IContainer container){
		return traces.stream()
				.filter(trace->(trace.getResource().getParent().equals(container) 
					&& trace.getBlocklines().size()==0))
				.collect(Collectors.toList());

	}
	
	public List<FeatureLocation> getDirectFolderTraces(IContainer container){
		return traces.stream()
				.filter(trace->(trace.getResource().equals(container) 
					&& trace.getBlocklines().size()==0))
				.collect(Collectors.toList());

	}
	
	/**
	 * Returns <code>true</code>
	 * 		if there is a trace between the input feature and resource
	 * @param feature
	 * 		the feature to be searched
	 * @param resource
	 * 		the resource to be searched
	 * 
	 */
	public boolean traceExists(Feature feature, IResource resource) {
		if (resource instanceof IFile) {
			for (FeatureLocation location : traces) {
				if (location.getFeature().equals(feature) && location.getResource().equals(resource))
					return true;
			}
		} else if (resource instanceof IFolder) {
			for (FeatureLocation location : traces) {
				if (location.getFeature().equals(feature) && resource.getProjectRelativePath().isPrefixOf(location.getResource().getProjectRelativePath()))
					return true;
			}
		}

		return false;
	}
	
	/**
	 * Returns the list of resources which there are feature traces for them, 
	 * but they do not exist in the file system
	 * 
	 */
	public List<IResource> getNotExistentResources() {
		return notExistentResources;
	}

	public List<FeatureLocation> getRelatedLocations(Feature feature){
		
		List<FeatureLocation> ans = new ArrayList<FeatureLocation>();
		traces.forEach(location->{
			if(location.getFeature().equals(feature))
				ans.add(location);
		});
		
		return ans;
	}
	
	public List<FeatureLocation>  getRelatedLocations(IResource resource){
		List<FeatureLocation> ans = new ArrayList<FeatureLocation>();
		traces.forEach(location->{
			if(location.getResource().equals(resource))
				ans.add(location);
		});
		
		return ans;
	}
	
	/**
	 * Returns all feature locations
	 */
	public List<FeatureLocation> getAllLocations() {
		return traces;
	}
	
	// ***********************Feature Model*****************************

	/**
	 * Sets the feature model of the project
	 * 
	 * @param featureModel
	 * 		the feature model of the project
	 * @return true
	 * 		if the feature model is not <code>null</code>
	 */
	public boolean setFeatureModel(FeatureModelHierarchy featureModel) {
		if (featureModel == null) {
			featureModelHierarchy = new FeatureModelHierarchy();
			return false;
		}
		featureModelHierarchy = featureModel;
		featuresOfFeatureModel = featureModel.getAllFeatures();
		for(Feature feature:featuresOfFeatureModel) {
			if(featuresNotInFeatureModel.contains(feature)) {
				featuresNotInFeatureModel.remove(feature);
			}
		}
		return true;
	}

	/**
	 * Returns the list of root features in the feature model of the project.
	 * If there is no feature model, an empty list will be returned.
	 */
	public List<Feature> getRootFeaturesOfFeatureModel() {
		return featureModelHierarchy.getRootFeatures();
	}

	/**
	 * Returns the list of features which there are traces for them, but they are mentioned in the feature model.
	 * If there is no feature model, an empty list will be returned.
	 */
	public List<Feature> getFeaturesNotInFeatureModel() {
		return featuresNotInFeatureModel;
	}

	/**
	 * Returns the list of features which they are mentioned in the feature model.
	 * If there is no feature model, an empty list will be returned.
	 */
	public List<Feature> getFeaturesOfFeatureModel() {
		return featuresOfFeatureModel;
	}

	/**
	 * Clears all of the information regarding feature traces and feature model
	 */
	public void clearAll() {
		featureModelHierarchy = new FeatureModelHierarchy();
		traces.clear();
		notExistentResources.clear();
		featuresNotInFeatureModel.clear();
		featuresOfFeatureModel.clear();
	}

	//***************************Metrics************************************
	
	public List<Feature> getAllFeatures(){
		List<Feature> features = new ArrayList<Feature>();
		features.addAll(featuresOfFeatureModel);
		features.addAll(featuresNotInFeatureModel);
		return features;
	}
	
	public void updateMetrics() { 
		metricsCalculator.updateMetrics();
	}
	
	public List<ResourceMetrics> getAllResourceMetrics(){
		// updateMetrics() must be called before calling this method to update the values
		return metricsCalculator.getAllResourceMetrics();
	}
	
	public List<FeatureResourceMetrics> getAllFeatureResourceMetrics(){
		// updateMetrics() must be called before calling this method to update the values
		return metricsCalculator.getAllFeatureResourceMetrics();
	}
		
	public List<FeatureResourceMetrics> getFeatureResourceMetrics(Feature feature){
		return metricsCalculator.getFeature_Resource_Metrics(feature);
	}
	
	public FeatureResourceMetrics getFeatureResourceMetrics(Feature feature,IResource resource){
		return metricsCalculator.getFeature_Resource_Metrics(feature, resource);
	}
	
	public ResourceMetrics getResourceMetrics(IResource resource) {
		return metricsCalculator.getResource_Metrics(resource);
	}
}