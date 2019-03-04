package se.gu.featuredashboard.model.featuremodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

public class Project {
	
	private IProject project;
	private List<FeatureContainer> features;
	private String ID;
	private IPath absoluteLocation;
	
	public Project(IProject project, String ID, IPath absoluteLocation) {
		this.project = project;
		this.ID = ID;
		this.absoluteLocation = absoluteLocation;
		features = new ArrayList<>();
	}
	
	public IProject getIProject() {
		return project;
	}
	
	public List<FeatureContainer> getFeatureInformation(){
		return features;
	}
	
	public void addFeatures(Collection<FeatureContainer> features) {
		this.features.addAll(features);
	}
	
	public IPath getLocation() {
		return absoluteLocation;
	}
	
	public String getID() {
		return ID;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null) {
			return false;
		}
		
		if(!(obj instanceof Project)) {
			return false;
		}
		
		Project p = (Project) obj;
		
		return this.absoluteLocation.equals(p.getLocation()) 
				&& this.ID.equals(p.getID()) 
				&& this.features == p.getFeatureInformation() 
				&& this.project == p.getIProject();
		
	}
	
}
