package se.gu.featuredashboard.utils.gef;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.mvc.fx.handlers.AbstractHandler;
import org.eclipse.gef.mvc.fx.handlers.IOnClickHandler;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.zest.fx.parts.EdgePart;

import javafx.scene.input.MouseEvent;
import se.gu.featuredashboard.utils.FeaturedashboardConstants;

public class EdgeOnClickHandler extends AbstractHandler implements IOnClickHandler {

	private boolean isRunning = false;
	private Long firstClick;

	@Override
	public void click(MouseEvent e) {
		if (e.getClickCount() > 2)
			return;

		if (firstClick != null) {
			long diff = System.currentTimeMillis() - firstClick;
			if (diff > FeaturedashboardConstants.DOUBLECLICK_DURATION)
				isRunning = false;
		}

		if (isRunning) {
			long diff = System.currentTimeMillis() - firstClick;
			if (diff < FeaturedashboardConstants.DOUBLECLICK_DURATION)
				showEdgeLabel(getHost());
			firstClick = null;
			isRunning = false;
		} else {
			firstClick = new Long(System.currentTimeMillis());
			isRunning = true;
		}
	}

	private void showEdgeLabel(IVisualPart<?> part) {
		CustomEdge edge = isCustomEdge(part);

		if (edge == null)
			return;

		// I just want to do this in the FeatureTanglingView
		String graphID = (String) edge.getGraph().getAttributes().get(FeaturedashboardConstants.GRAPH_ID_KEY);

		if(graphID == null)
			return;
		
		if (!graphID.equals(FeaturedashboardConstants.FEATURETANGLING_VIEW_ID))
			return;

		String label = ZestProperties.getLabel(edge);
		
		if(label == null)
			return;
		
		if(label.equals("")) {
			StringBuilder edgeLabel = new StringBuilder();
			edge.getFiles().forEach(file -> {
				edgeLabel.append(file.getProjectRelativePath().toString() + "\n");
			});

			ZestProperties.setLabel(edge, edgeLabel.toString());
		} else
			ZestProperties.setLabel(edge, "");
	}

	private CustomEdge isCustomEdge(IVisualPart<?> part) {
		if (!(part instanceof EdgePart))
			return null;

		Edge clickedEdge = ((EdgePart) part).getContent();

		if (!(clickedEdge instanceof CustomEdge))
			return null;

		return (CustomEdge) clickedEdge;
	}

}
