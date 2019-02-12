package se.gu.featuredashboard.model.location;

import java.io.File;
import java.util.Objects;

import se.gu.featuredashboard.model.featuremodel.Feature;

public class FeatureFolderLocation extends FeatureLocation {

	public FeatureFolderLocation() {

	}

	public FeatureFolderLocation(Feature feature, File fileAddress) {
		setFeature(feature);
		setFileAddress(fileAddress);
	}

	@Override
	public boolean setFileAddress(File fileAddress) {
		if (fileAddress.isDirectory())
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
	public boolean equals(Object aFeatureFolderLocation) {
		if (!(aFeatureFolderLocation instanceof FeatureFolderLocation))
			return false;
		if (((FeatureFolderLocation) aFeatureFolderLocation).getFeature().equals(this.getFeature())
				&& ((FeatureFolderLocation) aFeatureFolderLocation).getFileAddress().equals(this.getFileAddress()))
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getFeature(), getFileAddress());
	}

}
