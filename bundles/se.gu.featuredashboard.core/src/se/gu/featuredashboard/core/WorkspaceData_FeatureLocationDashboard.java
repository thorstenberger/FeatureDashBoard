/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;


/**
 * This static class includes the feature location dashboard data of all of the projects in the workspace
 * 
 */
public class WorkspaceData_FeatureLocationDashboard {
	
	private static WorkspaceData_FeatureLocationDashboard INSTANCE;	
	public static WorkspaceData_FeatureLocationDashboard getInstance() {	
		if (INSTANCE == null) {
			synchronized (WorkspaceData_FeatureLocationDashboard.class) {
				if (INSTANCE == null) {
					INSTANCE = new WorkspaceData_FeatureLocationDashboard();
				}
			}
		}
		return INSTANCE;
	}
	
	List<ProjectData_FeatureLocationDashboard> workspaceData = new ArrayList<ProjectData_FeatureLocationDashboard>();
	
	/**
	 * Gets the given project's feature location dashboard data.
	 */
	public ProjectData_FeatureLocationDashboard getProjectData(IProject project) {
		for(ProjectData_FeatureLocationDashboard projectData:workspaceData) {
			if(projectData.getProject().equals(project))
				return projectData;
		}
		return null;
	}
	
	/**
	 * Sets the given project's feature location dashboard data.
	 * If the project data is already exists, it will be replaces with the new value.
	 */
	public void setProjectData(ProjectData_FeatureLocationDashboard NewProjectData) {
		
		if(NewProjectData.getProject()==null)
			return;
	
		for(ProjectData_FeatureLocationDashboard projectData:workspaceData) {
			if(projectData.getProject().equals(NewProjectData.getProject())) {
				workspaceData.remove(projectData);
				break;
			}
		}
		workspaceData.add(NewProjectData);
	}
	
}
