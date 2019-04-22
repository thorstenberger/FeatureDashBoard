/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import metrics.Feature_ProjectMetrics;
import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.featuremodel.FeatureModelHierarchy;
import se.gu.featuredashboard.model.location.BlockLine;
import se.gu.featuredashboard.model.location.FeatureLocation;

/**
 * This class includes the feature location dashboard data of a project and
 * also some methods to access and manipulate it
 */
public class ProjectData_FeatureLocationDashboard {

	private IProject project;
	private List<FeatureLocation> traces = new ArrayList<>();
	private FeatureModelHierarchy featureModelHierarchy = new FeatureModelHierarchy();
	
	private Map<Feature, Feature_ProjectMetrics> featureProjectMetrics = new HashMap<>();

	private List<IResource> notExistentResources = new ArrayList<IResource>();
	private List<Feature> featuresNotInFeatureModel = new ArrayList<Feature>();
	private List<Feature> featuresOfFeatureModel = new ArrayList<Feature>();

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

			if (features.contains(featureLocation.getFeature()))
				answer.add(featureLocation);
			else {
				if (notExistentResources.contains(featureLocation.getResource())) {
					for (Object resource : resources) {
						if (((IResource) resource).getProjectRelativePath()
								.equals(featureLocation.getResource().getProjectRelativePath())) {
							answer.add(featureLocation);
							break;
						}
					}
				} else {
					for (Object resource : resources) {
						if (((IResource) resource).getProjectRelativePath()
								.isPrefixOf(featureLocation.getResource().getProjectRelativePath())) {
							answer.add(featureLocation);
							break;
						}
					}
				}
			}
		}

		return answer;
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
				if (resource.getProjectRelativePath().isPrefixOf(location.getResource().getProjectRelativePath()))
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
	 * Returns all feature locations
	 */
	public List<FeatureLocation> getAllLocations() {
		return traces;
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
	
	public void calculateMetrics() {
		traces.forEach(featureTrace->{		
			Feature feature = featureTrace.getFeature();
			Feature_ProjectMetrics metrics = featureProjectMetrics.get(feature);
			
			updateByAdd_maxNestingDepth(metrics,featureTrace);
			updateByAdd_minNestingDepth(metrics,featureTrace);
			updateByAdd_avgNestingDepth(metrics,featureTrace);
			updateByAdd_totalNestingDepth(metrics,featureTrace);
			updateByAdd_LOFC(metrics,featureTrace);
			updateByAdd_tanglingDegree(metrics,featureTrace);
			updateByAdd_folderAnnotations(metrics,featureTrace);
			updateByAdd_fileAnnotations(metrics,featureTrace);	
		});
	}
	
	public List<Feature_ProjectMetrics> getProjectMetrics(){
		return featureProjectMetrics.values().stream().collect(Collectors.toList());
	}
	
	public void appendMetrics(Feature_ProjectMetrics metrics, FeatureLocation featureTrace) {
		Feature feature = featureTrace.getFeature();
		IResource resource = featureTrace.getResource();
		List<BlockLine> blockLines = featureTrace.getBlocklines();
		
		if(metrics == null) {
			metrics = new Feature_ProjectMetrics(feature);
		}
		//metrics.setMaxNestingDepth(returnNestingDepth(resource));
	}
	
	private int returnNestingDepth(IResource resource) {
		if (resource instanceof IProject) {
			return 0;
		} else {
			return 1 + returnNestingDepth(resource.getParent());
		}
	}
	
	private void updateByAdd_maxNestingDepth(Feature_ProjectMetrics projectMetrics, FeatureLocation featureTrace) {
		
	}
	
	private void updateByRemove_maxNestingDepth(Feature_ProjectMetrics projectMetrics, FeatureLocation featureTrace) {
		
	}
	
	private void updateByAdd_minNestingDepth(Feature_ProjectMetrics projectMetrics, FeatureLocation featureTrace) {
		
	}
	
	private void updateByRemove_minNestingDepth(Feature_ProjectMetrics projectMetrics, FeatureLocation featureTrace) {
		
	}
	
	private void updateByAdd_avgNestingDepth(Feature_ProjectMetrics projectMetrics, FeatureLocation featureTrace) {
		
	}
	
	private void updateByRemove_avgNestingDepth(Feature_ProjectMetrics projectMetrics, FeatureLocation featureTrace) {
		
	}
	
	private void updateByAdd_totalNestingDepth(Feature_ProjectMetrics projectMetrics, FeatureLocation featureTrace) {
		
	}
	
	private void updateByRemove_totalNestingDepth(Feature_ProjectMetrics projectMetrics, FeatureLocation featureTrace) {
		
	}
	
	private void updateByAdd_LOFC(Feature_ProjectMetrics projectMetrics, FeatureLocation featureTrace) {
		
	}
	
	private void updateByRemvoe_LOFC(Feature_ProjectMetrics projectMetrics, FeatureLocation featureTrace) {
		
	}
	
	private void updateByAdd_tanglingDegree(Feature_ProjectMetrics projectMetrics, FeatureLocation featureTrace) {
		
	}
	
	private void updateByRemove_tanglingDegree(Feature_ProjectMetrics projectMetrics, FeatureLocation featureTrace) {
		
	}
	
	private void updateByAdd_folderAnnotations(Feature_ProjectMetrics projectMetrics, FeatureLocation featureTrace) {
		
	}
	
	private void updateByRemove_folderAnnotations(Feature_ProjectMetrics projectMetrics, FeatureLocation featureTrace) {
		
	}
	
	private void updateByAdd_fileAnnotations(Feature_ProjectMetrics projectMetrics, FeatureLocation featureTrace) {
		
	}
	
	private void updateByRemove_fileAnnotations(Feature_ProjectMetrics projectMetrics, FeatureLocation featureTrace) {
		
	}
	
	
}
