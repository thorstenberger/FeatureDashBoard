package se.gu.featuredashboard.model.featuremodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;

public class ProjectStore {
	
	private static Map<IPath, Project> parsedProjects = new HashMap<>();
	private static Project activeProject;
	
	public static void addProject(Project project) {
		parsedProjects.put(project.getLocation(), project);
		activeProject = project;
	}
	
	public static void setActiveProject(Project project) {
		activeProject = project;
	}
	
	public static void setActiveProject(IPath location) {
		activeProject = parsedProjects.get(location);
	}
	
	public static Project getActiveProject() {
		return activeProject;
	}
	
	public static void removeProject(IPath location) {
		parsedProjects.remove(location);
	}
	
	public static void removeProject(Project project) {
		parsedProjects.remove(project.getLocation());
	}
	
	public static boolean isProjectParsed(IPath location) {
		return parsedProjects.containsKey(location);
	}
	
	public static Project getProject(IPath location) {
		return parsedProjects.get(location);
	}
	
	public static Collection<Project> getAll(){
		return parsedProjects.values();
	}
	
}
