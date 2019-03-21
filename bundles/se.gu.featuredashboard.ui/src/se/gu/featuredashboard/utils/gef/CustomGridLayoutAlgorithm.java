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
		int x = 50;
		int y = 50;
	
		for(int i = 0; i < nodes.length; i++) {	
			
			LayoutProperties.setLocation(nodes[i], new Point(x, y));
			
			x += LayoutProperties.getSize(nodes[i]).getWidth() + 50;
			
			if(i+1 < nodes.length) {
				if(x + (int)LayoutProperties.getSize(nodes[i+1]).getWidth() + 50 > bounds.getWidth()) {
					x = 50;
					y += (int)LayoutProperties.getSize(nodes[i+1]).getHeight() + 40;
			 	}
			}
				
		}
		
	}
	
}
