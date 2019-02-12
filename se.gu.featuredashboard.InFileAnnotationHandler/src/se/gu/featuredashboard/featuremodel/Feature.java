package se.gu.featuredashboard.featuremodel;

import java.util.Objects;

public class Feature {
	private String featureID = "DefaultID";

	public Feature() {

	}

	public Feature(String featureID) {
		setFeatureID(featureID);
	}

	public String getFeatureID() {
		return featureID;
	}

	public void setFeatureID(String featureID) {
		this.featureID = featureID;
	}

	public boolean isInitializedFeature() {
		if (featureID.equals("DefaultID"))
			return false;
		return true;
	}

	@Override
	public boolean equals(Object aFeature) {
		if (!(aFeature instanceof Feature))
			return false;
		if (featureID.equals(((Feature) aFeature).getFeatureID()))
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(featureID);
	}

}
