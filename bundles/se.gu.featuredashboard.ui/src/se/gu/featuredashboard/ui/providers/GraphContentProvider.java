package se.gu.featuredashboard.ui.providers;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.zest.fx.ZestProperties;

import se.gu.featuredashboard.model.location.BlockLine;
import se.gu.featuredashboard.utils.FeaturedashboardConstants;
import se.gu.featuredashboard.utils.gef.FeatureNode;
import se.gu.featuredashboard.utils.gef.FileNode;

public class GraphContentProvider {
	
	public static Node getNestedGraphNode(String nodeLabel) {
		FeatureNode featureNode = (FeatureNode) getFeatureNode(nodeLabel);
		
		ZestProperties.setSize(featureNode, new Dimension(200, 100));
		
		return featureNode;
	}
	
	public static Node getFeatureNode(String nodeLabel) {
		FeatureNode featureNode = new FeatureNode();
		
		ZestProperties.setLabel(featureNode, nodeLabel);
		ZestProperties.setTooltip(featureNode, "Double-click to node to see files");
		ZestProperties.setLabelCssStyle(featureNode, "-fx-font-size:12;-fx-fill:white;");
		ZestProperties.setShapeCssStyle(featureNode, "-fx-fill:green;");
		ZestProperties.setSize(featureNode, FeaturedashboardConstants.NODE_SIZE);
		
		return featureNode;
	}
	
	public static Node getNode(String nodeLabel) {
		Node node = new Node();
		
		ZestProperties.setLabelCssStyle(node, "-fx-font-size:12;-fx-fill:white;");
		ZestProperties.setLabel(node, nodeLabel);
		ZestProperties.setSize(node, FeaturedashboardConstants.NODE_SIZE);
		
		return node;
	}

	public static Node getFileNode(String nodeLabel, String tooltip, IFile file, List<BlockLine> annotatedLines) {		
		FileNode fileNode = new FileNode(file, annotatedLines);
		
		ZestProperties.setLabel(fileNode, nodeLabel);
		ZestProperties.setLabelCssStyle(fileNode, "-fx-font-size:12;-fx-fill:white;");
		ZestProperties.setTooltip(fileNode, tooltip);
		ZestProperties.setSize(fileNode, new Dimension(100, 50));
		
		return fileNode;
	}
	
	public static Graph getGraph(String id, List<Edge> edges, List<Node> nodes, ILayoutAlgorithm layoutAlgorithm) {
		Graph graph = new Graph.Builder()
				.nodes(nodes)
				.edges(edges)
				.attr(FeaturedashboardConstants.GRAPH_ID_KEY, id)
				.attr(ZestProperties.LAYOUT_ALGORITHM__G, layoutAlgorithm)
				.build();
		
		return graph;
	}
	
	public static Graph getGraph(String id, List<Node> nodes, ILayoutAlgorithm layoutAlgorithm) {
		Graph graph = new Graph.Builder()
				.nodes(nodes)
				.attr(FeaturedashboardConstants.GRAPH_ID_KEY, id)
				.attr(ZestProperties.LAYOUT_ALGORITHM__G, layoutAlgorithm)
				.build();
		
		return graph;
	}
	
}