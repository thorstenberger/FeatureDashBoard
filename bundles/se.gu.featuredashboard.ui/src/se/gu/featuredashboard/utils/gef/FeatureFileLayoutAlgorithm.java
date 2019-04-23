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
	private final double NESTEDNODE_Y_SPACING = 40;

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

		double startX = 70;

		double featureNodeX = startX;
		double featureNodeY = graphBounds.getHeight() / 2;

		double nestedNodeY = 50;

		double commonNodeX = startX;
		double commonNodeY = graphBounds.getHeight() - 50;

		for (Node node : nodes) {
			if (node instanceof FeatureNode) {
				boolean exeedsBounds = featureNodeX + LayoutProperties.getSize(node).width + NODE_X_SPACING > bounds
								.getWidth();

				if (exeedsBounds) {
					featureNodeX = startX;
					featureNodeY += NODE_Y_SPACING;
				}

				LayoutProperties.setLocation(node, new Point(featureNodeX, featureNodeY));

				// Place the nested node which contains the rest of the files that contain this feature but not the
				// other selected features directly above this feature node
				for (Edge outgoingEdge : node.getAllOutgoingEdges()) {
					Node targetNode = outgoingEdge.getTarget();

					if (!(targetNode instanceof FileNode)) {
						if (exeedsBounds) {
							nestedNodeY += NESTEDNODE_Y_SPACING;
						}
						LayoutProperties.setLocation(targetNode, new Point(featureNodeX, nestedNodeY));
						break;
					}
				}

				featureNodeX += LayoutProperties.getSize(node).width + NODE_X_SPACING;
			} else {
				Graph nestedGraph = node.getNestedGraph();

				if (nestedGraph == null) {
					if (commonNodeX + LayoutProperties.getSize(node).width + NODE_X_SPACING > bounds.getWidth()) {
						commonNodeX = startX;
						commonNodeY += NODE_Y_SPACING;
					}
					LayoutProperties.setLocation(node, new Point(commonNodeX, commonNodeY));

					commonNodeX += LayoutProperties.getSize(node).width + NODE_X_SPACING;
				}
			}
		}
	}
}
