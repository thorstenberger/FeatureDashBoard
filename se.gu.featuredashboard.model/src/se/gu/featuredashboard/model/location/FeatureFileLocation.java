package se.gu.featuredashboard.model.location;

import java.io.File;
import java.util.Objects;

import se.gu.featuredashboard.model.featuremodel.Feature;

public class FeatureFileLocation extends FeatureLocation {

	public FeatureFileLocation() {
		// TODO Auto-generated constructor stub
	}

	public FeatureFileLocation(Feature feature, File fileAddress) {
		setFeature(feature);
		setFileAddress(fileAddress);
	}

	@Override
	public boolean setFileAddress(File fileAddress) {
		if (fileAddress.isFile())
			return super.setFileAddress(fileAddress);
		return false;
	}

	@Override
	public boolean isInitializedLocation() {
		if (getFeature().isInitializedFeature() && getFileAddress().exists())
			return true;
		return false;
	}

	@Override
	public boolean equals(Object aFeatureFileLocation) {
		if (!(aFeatureFileLocation instanceof FeatureFileLocation))
			return false;
		if (((FeatureFileLocation) aFeatureFileLocation).getFeature().equals(this.getFeature())
				&& ((FeatureFileLocation) aFeatureFileLocation).getFileAddress().equals(this.getFileAddress()))
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getFeature(), getFileAddress());
	}

}
