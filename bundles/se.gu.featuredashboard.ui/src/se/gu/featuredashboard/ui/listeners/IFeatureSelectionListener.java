package se.gu.featuredashboard.ui.listeners;

import java.util.List;

import se.gu.featuredashboard.model.location.FeatureLocation;

public interface IFeatureSelectionListener {

	void updateFeatureSelection(List<FeatureLocation> featureLocations);

}
