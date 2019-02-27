package se.gu.featuredashboard.model.featuremodel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;

public class FeatureFileContainer {
	
	private Feature feature;
	private List<IFile> files;
	
	public FeatureFileContainer(Feature feature) {
		this.feature = feature;
		files = new ArrayList<>();
	}
	
	public FeatureFileContainer(Feature feature, List<IFile> files) {
		this.feature = feature;
		this.files = files;
	}
	
	public Feature getFeature() {
		return feature;
	}
	
	public List<IFile> getFiles(){
		return files;
	}
	
	public void addFiles(IFile file) {
		files.add(file);
	}
	
}
