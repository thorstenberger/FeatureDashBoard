/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.utils.gef;

import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.zest.fx.behaviors.GraphLayoutBehavior;

import se.gu.featuredashboard.utils.FeaturedashboardConstants;

public class CustomGraphLayoutContextBehavior extends GraphLayoutBehavior {

	@Override
	protected Rectangle computeLayoutBounds() {
		Graph graph = getHost().getContent();
		
		String graphID = (String) graph.getAttributes().get(FeaturedashboardConstants.GRAPH_ID_KEY);
		
		if (graphID == null)
			return super.computeLayoutBounds();

		// Determine the graph bounds depending on what we want to visualize
		if (graphID.equals(FeaturedashboardConstants.FEATUREFOLDER_VIEW_ID))
			return new Rectangle(0, 0, 1000, 6000);
		else if (graphID.equals(FeaturedashboardConstants.FEATURETANGLING_VIEW_ID))
			return new Rectangle(0, 0, 1000, 1000);
		else
			return super.computeLayoutBounds();
	}
	
}
