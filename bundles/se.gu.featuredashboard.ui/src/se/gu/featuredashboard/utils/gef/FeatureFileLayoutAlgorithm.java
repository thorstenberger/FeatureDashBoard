package se.gu.featuredashboard.utils.gef;

import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.layout.LayoutProperties;

public class FeatureFileLayoutAlgorithm implements ILayoutAlgorithm {

	private Rectangle bounds;
	private final double NODE_X_SPACING = 40;
	private final double NODE_Y_SPACING = 40;

	@Override
	public void applyLayout(LayoutContext layoutContext, boolean clean) {
		if (!clean)
			return;

		Node[] graphNodes = layoutContext.getNodes();
		Edge[] graphEdges = layoutContext.getEdges();
		bounds = LayoutProperties.getBounds(layoutContext.getGraph());
		performLayout(graphNodes, graphEdges, bounds);
	}

	private void performLayout(Node[] nodes, Edge[] edges, Rectangle graphBounds) {

		if (nodes.length == 0)
			return;

		double featureNodeX = 20;
		double featureNodeY = 20;

		double nestedNodeX = 20;
		double nestedNodeY = graphBounds.getHeight() - 100;

		double commonNodeX = 20;
		double commonNodeY = graphBounds.getHeight() / 2;

		for (Node node : nodes) {
			if (node instanceof FeatureNode) {
				featureNodeX += LayoutProperties.getSize(node).width + NODE_X_SPACING;

				if (exeedsGraphXBounds(featureNodeX)) {
					featureNodeX = LayoutProperties.getSize(node).width + NODE_X_SPACING;
					featureNodeY += LayoutProperties.getSize(node).height + NODE_Y_SPACING;
				}

				LayoutProperties.setLocation(node, new Point(featureNodeX, featureNodeY));
			} else {
				Graph nestedGraph = node.getNestedGraph();

				if (nestedGraph == null) {
					commonNodeX += LayoutProperties.getSize(node).width + NODE_X_SPACING;

					if (exeedsGraphXBounds(commonNodeX)) {
						commonNodeX = LayoutProperties.getSize(node).width + NODE_X_SPACING;
						commonNodeY += LayoutProperties.getSize(node).height + NODE_Y_SPACING;
					}
					
					LayoutProperties.setLocation(node, new Point(commonNodeX, commonNodeY));
				} else {
					nestedNodeX += LayoutProperties.getSize(node).width + NODE_X_SPACING;
					
					if (exeedsGraphXBounds(nestedNodeX)) {
						nestedNodeX = LayoutProperties.getSize(node).width + NODE_X_SPACING;
						nestedNodeY += LayoutProperties.getSize(node).height + NODE_Y_SPACING;
					}

					LayoutProperties.setLocation(node, new Point(nestedNodeX, nestedNodeY));
				}
			}
		}
	}

	private boolean exeedsGraphXBounds(double xValue) {
		return xValue > bounds.getWidth();
	}

}
