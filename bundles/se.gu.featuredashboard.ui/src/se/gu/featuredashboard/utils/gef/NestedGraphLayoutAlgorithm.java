package se.gu.featuredashboard.utils.gef;

import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.layout.LayoutProperties;

// Simple grid layout algoritm for nested graphs since GridLayoutAlgorithm is bugged
public class NestedGraphLayoutAlgorithm implements ILayoutAlgorithm {

	@Override
	public void applyLayout(LayoutContext layoutContext, boolean clean) {
		Rectangle bounds = LayoutProperties.getBounds(layoutContext.getGraph());

		double startX = 100;

		double x = startX;
		double y = 50;

		double xSpacing = 70;
		double ySpacing = 70;
		
		Node[] nodes = layoutContext.getNodes();

		for (Node node : nodes) {
			// Set node at current X, Y, also check if node bounds exeed graph bounds
			if (x + LayoutProperties.getSize(node).width + xSpacing > bounds.getWidth()) {
				// Reset X and increase Y for next iteration
				x = startX;
				y += ySpacing;
			}
			LayoutProperties.setLocation(node, new Point(x, y));
			
			// Determine location for next node
			x += LayoutProperties.getSize(node).width + xSpacing;
		}
	}
}
