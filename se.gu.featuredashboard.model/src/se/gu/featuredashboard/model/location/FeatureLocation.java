package se.gu.featuredashboard.model.location;

import java.io.File;

import se.gu.featuredashboard.model.featuremodel.Feature;

public abstract class FeatureLocation {

	private Feature feature = new Feature();
	private File fileAddress = new File("DefaultAddress");

	public Feature getFeature() {
		return feature;
	}

	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	public File getFileAddress() {
		return fileAddress;
	}

	public boolean setFileAddress(File fileAddress) {
		if (fileAddress.exists()) {
			this.fileAddress = fileAddress;
			return true;
		}
		return false;
	}

	public abstract boolean isInitializedLocation();

}
