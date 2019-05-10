/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.ui.listeners;

import java.util.List;

import se.gu.featuredashboard.model.location.FeatureLocation;

public interface IFeatureSelectionListener {

	void updateFeatureSelection(List<FeatureLocation> featureLocations);

}
