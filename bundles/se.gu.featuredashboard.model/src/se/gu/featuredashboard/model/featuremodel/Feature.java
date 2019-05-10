/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.model.featuremodel;

import java.util.ArrayList;
import java.util.Objects;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

/**
 * This is the class of a feature.
 * 
 */
public class Feature {
	private final String featureID;
	private ArrayList<Feature> subFeatures = new ArrayList<>();
	private IProject project;
	private IFile featureModelFile;

	/**
	 * Constructing a feature with its ID
	 * @param featureID
	 * 		the id of the feature
	 */
	public Feature(String featureID) {
		this.featureID = featureID;
	}

	/**
	 * Constructing a feature with its ID, and its subfeatures.
	 * The given subfeatues are not cloned and directly mapped as the subfeatures of the feature
	 * under construction. 
	 * 
	 * @param featureID
	 * 		the id of the feature
	 * @param subFeatures
	 * 		the subfeatures of the feature
	 */
	public Feature(String featureID, ArrayList<Feature> subFeatures) {
		this.featureID = featureID;
		setSubFeatures(subFeatures);
	}
	
	/**
	 * Constructing a feature with its ID, and its subfeatures.
	 * The given subfeatues are not cloned and directly mapped as the subfeatures of the feature
	 * under construction. The given project and the feature model of the feature can be given and used
	 * in integrating the featuredahboard with Capra plugin.
	 * 
	 * @param featureID
	 * 		the id of the feature
	 * @param subFeatures
	 * 		the subfeatures of the feature
	 * @param project
	 * 		the project of the feature
	 * @param featureModelFile
	 * 		the feature model of the feature
	 */
	public Feature(String featureID, ArrayList<Feature> subFeatures, IProject project, IFile featureModelFile) {
		this.featureID = featureID;
		setSubFeatures(subFeatures);
		setProject(project);
		setFeatureModelFile(featureModelFile);
	}
	
	/**
	 * Constructing a feature with the information of the given feature
	 * 
	 * @param f
	 * 		the feature which its information will be used in feature construction
	 */
	public Feature(Feature f) {
		featureID = f.getFeatureID();
		setSubFeatures(f.getSubFeatures());
		setProject(f.getproject());
		setFeatureModelFile(f.getFeatureModelFile());
	}

	/**
	 * Returns the id of the feature
	 */
	public String getFeatureID() {
		return featureID;
	}

	/**
	 * Returns a list of features having only the id of the features that
	 * are direct subfeatures of the this feature
	 */
	public ArrayList<Feature> getSubFeatures(){
		ArrayList<Feature> result = new ArrayList<Feature>();
		this.subFeatures.forEach(subFeature->{
			result.add(new Feature(subFeature));
		});
		
		return result;
	}

	/**
	 * Sets the subFeautures of this feature. These subfeatures will not be cloned
	 * and will directly get defined as this feature's subfeartures.
	 * 
	 * @param subFeatures
	 * 		the List of features that will be set to this feature's subfeatures
	 * 		
	 */
	public void setSubFeatures(ArrayList<Feature> subFeatures){
		this.subFeatures.clear();
		subFeatures.forEach(subFeature->{
			this.subFeatures.add(new Feature(subFeature));
		});
	}
	
	/**
	 * Appends a feature to the subFeautures of this feature. The input feature will not be cloned
	 * and will directly get appended to this feature's subfeartures.
	 * 
	 * @param newFeature
	 * 		feature that will be appended to this feature's subfeatures
	 * 		
	 */
	public void addToSubFeatures(Feature newFeature) {
		//adding the same newFeature not a copy of that used in constructing feature model
		subFeatures.add(newFeature);		
	}
	
	/**
	 * Returns all of the features in the subtree of this feature in the feature model tree
	 * 		
	 */
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

	/**
	 * Sets the project that this feature belong to that
	 * 		
	 */
	public void setProject(IProject project) {
		this.project = project;
	}
	
	/**
	 * Returns the project that this feature belong to that
	 * 		
	 */
	public IProject getproject() {
		return project;
	}
	
	/**
	 * Sets the feature model that this feature belong to that
	 * 		
	 */
	public void setFeatureModelFile(IFile featureModelFile) {
		this.featureModelFile = featureModelFile;
	}

	/**
	 * Returns the feature model that this feature belong to that
	 * 		
	 */
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
