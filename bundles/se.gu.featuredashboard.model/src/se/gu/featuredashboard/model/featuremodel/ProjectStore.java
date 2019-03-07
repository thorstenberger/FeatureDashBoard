package se.gu.featuredashboard.model.featuremodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;

public class ProjectStore {
	
	private static Map<IPath, Project> parsedProjects = new HashMap<>();
	
	public static void addProject(IPath absoluteLocation, Project parsedProject) {
		parsedProjects.put(absoluteLocation, parsedProject);
	}
	
	public static void removeProject(IPath absoluteLocation) {
		parsedProjects.remove(absoluteLocation);
	}
	
	public static Project getProject(IPath absoluteLocation) {
		return parsedProjects.get(absoluteLocation);
	}
	
	public static Collection<Project> getAll(){
		return parsedProjects.values();
	}
	
}
