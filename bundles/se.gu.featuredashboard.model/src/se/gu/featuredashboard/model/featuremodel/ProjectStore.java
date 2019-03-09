package se.gu.featuredashboard.model.featuremodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;

public class ProjectStore {
	
	private static Map<IPath, Project> parsedProjects = new HashMap<>();
	private static Project activeProject;
	
	public static void addProject(IPath absoluteLocation, Project parsedProject) {
		parsedProjects.put(absoluteLocation, parsedProject);
		activeProject = parsedProject;
	}
	
	public static void setActiveProject(Project project) {
		activeProject = project;
	}
	
	public static void setActiveProject(IPath absoluteLocation) {
		activeProject = parsedProjects.get(absoluteLocation);
	}
	
	public static Project getActiveProject() {
		return activeProject;
	}
	
	public static void removeProject(IPath absoluteLocation) {
		parsedProjects.remove(absoluteLocation);
	}
	
	public static boolean isProjectParsed(IPath absoluteLocation) {
		return parsedProjects.containsKey(absoluteLocation);
	}
	
	public static Project getProject(IPath absoluteLocation) {
		return parsedProjects.get(absoluteLocation);
	}
	
	public static Collection<Project> getAll(){
		return parsedProjects.values();
	}
	
}
