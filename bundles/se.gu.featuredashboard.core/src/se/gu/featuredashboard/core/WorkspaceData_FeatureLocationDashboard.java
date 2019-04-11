package se.gu.featuredashboard.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;

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
	
	public ProjectData_FeatureLocationDashboard getProjectData(IProject project) {
		for(ProjectData_FeatureLocationDashboard projectData:workspaceData) {
			if(projectData.getProject().equals(project))
				return projectData;
		}
		return null;
	}
	
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
