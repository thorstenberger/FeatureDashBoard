package se.gu.featuredashboard.utils.gef;

import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.zest.fx.behaviors.GraphLayoutBehavior;

import se.gu.featuredashboard.utils.FeaturedashboardConstants;

public class CustomGraphLayoutContextBehavior extends GraphLayoutBehavior {

	@Override
	protected Rectangle computeLayoutBounds() {
		Graph graph = getHost().getContent();
		
		if(graph.getAttributes().get(FeaturedashboardConstants.GRAPH_ID_KEY).equals(FeaturedashboardConstants.FEATUREFOLDER_VIEW_ID))
			return new Rectangle(-100, -100, 1000, 6000);
		else
			return super.computeLayoutBounds();
	}
	
}
