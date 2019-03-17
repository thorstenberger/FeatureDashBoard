package se.gu.featuredashboard.model.featuremodel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

public class Project {
	
	private int numberOfFeatures = 0;
	
	private IProject project;
	private List<FeatureContainer> featureContainers;
	private String ID;
	private IPath absoluteLocation;
	private List<IPath> outputFolders;
	private DecimalFormat df = new DecimalFormat("#.##");
	
	public Project(IProject project, String ID, IPath absoluteLocation) {
		this.project = project;
		this.ID = ID;
		this.absoluteLocation = absoluteLocation;
		featureContainers = new ArrayList<>();
		outputFolders = new ArrayList<>();
	}
	
	public IProject getIProject() {
		return project;
	}
	
	public FeatureContainer getFeatureContaier(Feature feature) {
		for(FeatureContainer container : featureContainers) {
			if(container.getFeature().equals(feature))
				return container;
		}
		return null;
	}
	
	public void removeFeatureContainer(Feature feature) {
		int indexOf = -1;
		for(int i = 0; i < featureContainers.size(); i++) {
			if(featureContainers.get(i).getFeature().equals(feature)) {
				indexOf = i;
				break;
			}
		}
		if(indexOf != -1)
			featureContainers.remove(indexOf);
	}
	
	public List<FeatureContainer> getFeatureContainers(){
		return featureContainers;
	}
	
	public void addFeatures(Collection<FeatureContainer> newFeatureContainers) {
		featureContainers.addAll(newFeatureContainers);
		numberOfFeatures += featureContainers.size();
	}
	
	public void addFeature(FeatureContainer container) {
		featureContainers.add(container);
		numberOfFeatures++;
	}
	
	public void removeFeature(FeatureContainer container) {
		featureContainers.remove(container);
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
		return featureContainers.stream().mapToInt(FeatureContainer::getLinesOfFeatureCode).sum();
	}
	
	public String getAvgLoFC() {
		return df.format((double)this.getTotalLoFC()/(double)getNumberOfFeatures());
	}
	
	public String getAverageSD() {
		int totalNestingDegree = featureContainers.stream().mapToInt(FeatureContainer::getScatteringDegree).sum();
		return df.format((double)totalNestingDegree/(double)getNumberOfFeatures());
	}
	
	public String getAverageND() {
		int totalDepth = featureContainers.stream().mapToInt(FeatureContainer::getTotalND).sum();
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
				&& this.project == p.getIProject();
		
	}
	
}
