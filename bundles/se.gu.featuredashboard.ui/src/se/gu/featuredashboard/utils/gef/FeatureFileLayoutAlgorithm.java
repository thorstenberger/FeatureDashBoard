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

		double featureNodeX = 0;
		double featureNodeY = graphBounds.getHeight() / 2;

		double nestedNodeY = 50;

		double commonNodeX = 0;
		double commonNodeY = graphBounds.getHeight() - 50;

		for (Node node : nodes) {
			if (node instanceof FeatureNode) {
				featureNodeX += LayoutProperties.getSize(node).width + NODE_X_SPACING;

				boolean exeededBounds = exeedsGraphXBounds(featureNodeX);

				if (exeededBounds) {
					featureNodeX = LayoutProperties.getSize(node).width + NODE_X_SPACING;
					featureNodeY += LayoutProperties.getSize(node).height + NODE_Y_SPACING;
				}
				LayoutProperties.setLocation(node, new Point(featureNodeX, featureNodeY));

				// Place the nested node which contains the rest of the files that contain this feature but not the
				// other selected features directly above this feature node
				for (Edge outgoingEdge : node.getAllOutgoingEdges()) {
					Node targetNode = outgoingEdge.getTarget();

					if (!(targetNode instanceof FileNode)) {
						if (exeededBounds) {
							nestedNodeY += LayoutProperties.getSize(targetNode).height + NODE_Y_SPACING;
						}
						LayoutProperties.setLocation(targetNode, new Point(featureNodeX, nestedNodeY));
						break;
					}
				}
			} else {
				Graph nestedGraph = node.getNestedGraph();

				if (nestedGraph == null) {
					commonNodeX += LayoutProperties.getSize(node).width + NODE_X_SPACING;

					if (exeedsGraphXBounds(commonNodeX)) {
						commonNodeX = LayoutProperties.getSize(node).width + NODE_X_SPACING;
						commonNodeY += LayoutProperties.getSize(node).height + NODE_Y_SPACING;
					}
					
					LayoutProperties.setLocation(node, new Point(commonNodeX, commonNodeY));
				}
			}
		}
	}

	private boolean exeedsGraphXBounds(double xValue) {
		return xValue > bounds.getWidth();
	}

}
