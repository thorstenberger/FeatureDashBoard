package se.gu.featuredashboard.model.featuremodel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

public class Project {
	
	private int numberOfFeatures = 0;
	
	private IProject project;
	private Map<Feature, FeatureContainer> featureContainers;
	private String ID;
	private IPath absoluteLocation;
	private List<IPath> outputFolders;
	private DecimalFormat df = new DecimalFormat("#.##");
	
	public Project(IProject project, String ID, IPath absoluteLocation) {
		this.project = project;
		this.ID = ID;
		this.absoluteLocation = absoluteLocation;
		featureContainers = new HashMap<>();
		outputFolders = new ArrayList<>();
	}
	
	public IProject getIProject() {
		return project;
	}
	
	public FeatureContainer getFeatureContaier(Feature feature) {
		return featureContainers.get(feature);
	}
	
	public void removeFeatureContainer(Feature feature) {
		featureContainers.remove(feature);
	}
	
	public List<FeatureContainer> getFeatureContainers(){
		return featureContainers.values().stream().collect(Collectors.toList());
	}
	
	public void addFeatures(Collection<FeatureContainer> newFeatureContainers) {
		newFeatureContainers.forEach(container -> {
			featureContainers.put(container.getFeature(), container);
		});
		numberOfFeatures += featureContainers.size();
	}
	
	public void addFeatureContainer(FeatureContainer container) {
		featureContainers.put(container.getFeature(), container);
		numberOfFeatures++;
	}
	
	public void removeFeature(FeatureContainer container) {
		featureContainers.remove(container.getFeature());
	}
	
	public IPath getLocation() {
		return absoluteLocation;
	}
	
	public void setOutputFolder(IPath path) {
		outputFolders.add(path);
	}
	
	public List<IPath> getOutputFolders() {
		return outputFolders;
	}
	
	public String getID() {
		return ID;
	}
	
	public int getNumberOfFeatures() {
		return numberOfFeatures;
	}
	
	public int getTotalLoFC() {
		return featureContainers.values().stream().mapToInt(FeatureContainer::getLOFC).sum();
	}
	
	public String getAvgLoFC() {
		if(getNumberOfFeatures() == 0)
			return df.format(0);
		return df.format((double)this.getTotalLoFC()/(double)getNumberOfFeatures());
	}
	
	public String getAverageSD() {
		int totalNestingDegree = featureContainers.values().stream().mapToInt(FeatureContainer::getScatteringDegree).sum();
		if(getNumberOfFeatures() == 0)
			return df.format(0);
		return df.format((double)totalNestingDegree/(double)getNumberOfFeatures());
	}
	
	public String getAverageND() {
		int totalDepth = featureContainers.values().stream().mapToInt(FeatureContainer::getTotalND).sum();
		if(getNumberOfFeatures() == 0)
			return df.format(0);
		return df.format((double)totalDepth/(double)getNumberOfFeatures());
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null)
			return false;
		
		if(!(obj instanceof Project))
			return false;
		
		Project p = (Project) obj;
		
		if(p == obj)
			return true;
		
		return this.absoluteLocation.equals(p.getLocation()) 
				&& this.ID.equals(p.getID()) 
				&& this.project.equals(p.getIProject());
		
	}
	
}
