package se.gu.featuredashboard.utils.gef;

import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.layout.LayoutProperties;

public class CustomGridLayoutAlgorithm implements ILayoutAlgorithm {
	
	@Override
	public void applyLayout(LayoutContext layoutContext, boolean clean) {		
		setPositions(layoutContext.getNodes(), LayoutProperties.getBounds(layoutContext.getGraph()));
	}
	
	private void setPositions(Node[] nodes, Rectangle bounds) {
		int x = 100;
		int y = 100;
		
		for(int i = 0; i < nodes.length; i++) {	
			if(x + (int)LayoutProperties.getSize(nodes[i]).getWidth() > bounds.getWidth() + 600) {
				x = 100;
				y += (int)LayoutProperties.getSize(nodes[i]).getHeight() + 40;
			}
			
			LayoutProperties.setLocation(nodes[i], new Point(x, y));
			
			x += LayoutProperties.getSize(nodes[i]).getWidth() + 50;
				
		}
		
	}
	
}
