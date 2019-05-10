/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.location.ProjectData;


/**
 * This static class includes the feature location dashboard data of all of the projects in the workspace
 * 
 */
public class WorkspaceData {

	private static WorkspaceData INSTANCE;	
	public static WorkspaceData getInstance() {	
		if (INSTANCE == null) {
			synchronized (WorkspaceData.class) {
				if (INSTANCE == null) {
					INSTANCE = new WorkspaceData();
				}
			}
		}
		return INSTANCE;
	}
	
	private List<ProjectData> workspaceData = new ArrayList<ProjectData>();
	private List<IProject> allProjects =  new ArrayList<IProject>();
	
	/**
	 * Gets the given project's feature location dashboard data.
	 */
	public ProjectData getProjectData(IProject project) {
		for(ProjectData projectData:workspaceData) {
			if(projectData.getProject().equals(project))
				return projectData;
		}
		return null;
	}
	
	public void removeProjectData(IProject project) {
		for(ProjectData projectData:workspaceData) {
			if(projectData.getProject().equals(project)) {
				workspaceData.remove(projectData);
				allProjects.remove(projectData.getProject());
			}
		}
	}
	
	/**
	 * Sets the given project's feature location dashboard data.
	 * If the project data is already exists, it will be replaces with the new value.
	 */
	public void setProjectData(ProjectData NewProjectData) {
		
		if(NewProjectData.getProject()==null)
			return;
		workspaceData.removeIf(projectData -> projectData.getProject().equals(NewProjectData.getProject()));
		workspaceData.add(NewProjectData);
		
		if(!allProjects.contains(NewProjectData.getProject()))
				allProjects.add(NewProjectData.getProject());
	}
	
	/**
	 * Returns workspace data
	 */
	public List<ProjectData> getWorkspaceData() {
		return workspaceData;
	}
	
	public List<Feature> getAllFeatures(){
		Set<Feature> ans = new HashSet<Feature>();
		workspaceData.forEach(projectData->{
			ans.addAll(projectData.getAllFeatures());
		});
		return new ArrayList<Feature>(ans);
	}
	
	public List<IProject> getAllProjects(){
		return allProjects;
	}

		
}
