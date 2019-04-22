package se.gu.featuredashboard.utils.gef;

import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.layout.LayoutProperties;

public class FileGridLayoutAlgorithm implements ILayoutAlgorithm {

	private int xSpacing;
	private int ySpacing;

	public FileGridLayoutAlgorithm(int xSpacing, int ySpacing) {
		this.xSpacing = xSpacing;
		this.ySpacing = ySpacing;
	}

	@Override
	public void applyLayout(LayoutContext layoutContext, boolean clean) {
		if (!clean)
			return;
		setPositions(layoutContext.getNodes(), LayoutProperties.getBounds(layoutContext.getGraph()));
	}

	private void setPositions(Node[] nodes, Rectangle bounds) {
		double x = 0;
		double y = 0;

		if (nodes.length > 0) {
			x = LayoutProperties.getSize(nodes[0]).getWidth() + 20;
			y = LayoutProperties.getSize(nodes[0]).getHeight() + 20;
		}

		for (int i = 0; i < nodes.length; i++) {
			if (x + (int) LayoutProperties.getSize(nodes[i]).getWidth() > bounds.getWidth()) {
				x = 100;
				y += (int) LayoutProperties.getSize(nodes[i]).getHeight() + ySpacing;
			}

			LayoutProperties.setLocation(nodes[i], new Point(x, y));

			x += LayoutProperties.getSize(nodes[i]).getWidth() + xSpacing;

		}

	}

}
