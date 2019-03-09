package se.gu.featuredashboard.model.featuremodel;

import java.text.DecimalFormat;
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
	private DecimalFormat df = new DecimalFormat("#.##");
	
	public Project(IProject project, String ID, IPath absoluteLocation) {
		this.project = project;
		this.ID = ID;
		this.absoluteLocation = absoluteLocation;
		features = new ArrayList<>();
	}
	
	public IProject getIProject() {
		return project;
	}
	
	public FeatureContainer getFeatureContainer(Feature feature){
		for(FeatureContainer container : features) {
			if(container.getFeature().equals(feature))
				return container;
		}
		return null;
	}
	
	public List<FeatureContainer> getFeatureContainers(){
		return features;
	}
	
	public void addFeatures(Collection<FeatureContainer> featureContainers) {
		features.addAll(featureContainers);
	}
	
	public void addFeature(FeatureContainer container) {
		features.add(container);
	}
	
	public IPath getLocation() {
		return absoluteLocation;
	}
	
	public String getID() {
		return ID;
	}
	
	public int getNumberOfFeatures() {
		return this.features.size();
	}
	
	public int getTotalLoFC() {
		int totalLoFC = 0;
		
		for(FeatureContainer container : features) {
			totalLoFC += container.getLinesOfFeatureCode();
		}
		
		return totalLoFC;
	}
	
	public String getAvgLoFC() {
		return df.format((double)this.getTotalLoFC()/(double)features.size());
	}
	
	public String getAverageSD() {
		double avgNestingDegree = 0;
		
		for(FeatureContainer container : features) {
			avgNestingDegree += container.getScatteringDegree();
		}
		
		avgNestingDegree /= features.size();
		
		return df.format(avgNestingDegree);
	}
	
	public String getAverageND() {
		double totalDepth = 0;
		
		for(FeatureContainer container : features) {
			totalDepth = (int) container.getNestingInfo()[2];
		}
		
		return df.format(totalDepth/features.size());
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
				&& this.project == p.getIProject();
		
	}
	
}
