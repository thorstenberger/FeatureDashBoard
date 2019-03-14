package se.gu.featuredashboard.model.featuremodel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

public class Project {
	
	private int numberOfFeatures = 0;
	
	private IProject project;
	private List<FeatureContainer> features;
	private String ID;
	private IPath absoluteLocation;
	private List<IPath> outputFolders;
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
		numberOfFeatures += featureContainers.size();
	}
	
	public void addFeature(FeatureContainer container) {
		features.add(container);
		numberOfFeatures++;
	}
	
	public IPath getLocation() {
		return absoluteLocation;
	}
	
	public void setOutputFolder(IPath path) {
		if(outputFolders == null)
			outputFolders = new ArrayList<>();
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
		return features.stream().mapToInt(FeatureContainer::getLinesOfFeatureCode).sum();
	}
	
	public String getAvgLoFC() {
		return df.format((double)this.getTotalLoFC()/(double)getNumberOfFeatures());
	}
	
	public String getAverageSD() {
		int totalNestingDegree = features.stream().mapToInt(FeatureContainer::getScatteringDegree).sum();
		return df.format((double)totalNestingDegree/(double)getNumberOfFeatures());
	}
	
	public String getAverageND() {
		int totalDepth = features.stream().mapToInt(FeatureContainer::getTotalND).sum();
		return df.format((double)totalDepth/(double)getNumberOfFeatures()	);
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
