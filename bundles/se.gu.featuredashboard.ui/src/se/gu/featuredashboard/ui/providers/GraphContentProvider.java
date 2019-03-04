package se.gu.featuredashboard.ui.providers;

import java.util.List;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.gef.zest.fx.ZestProperties;

public class GraphContentProvider {
	
	private static Node node;
	private static Graph graph;
	private static Dimension featureNodeDimension = new Dimension(70,30);
	
	public static Node getFeatureNode(String nodeLabel) {
		node = new Node.Builder()
				.attr(ZestProperties.LABEL__NE, "Feature: \n " + nodeLabel)
				.attr(ZestProperties.SIZE__N, featureNodeDimension)
				.attr(ZestProperties.LABEL_CSS_STYLE__NE, "-fx-font-size:14;-fx-font-weight:bold;-fx-fill:white;")
				.attr(ZestProperties.SHAPE_CSS_STYLE__N, "-fx-fill:green;")
				.buildNode();
		
		return node;
	}
	
	public static Node getNormalNode(String nodeLabel) {
		node = new Node.Builder()
				.attr(ZestProperties.LABEL__NE, nodeLabel)
				.buildNode();
		
		return node;
	}
	
	public static Graph getGraph(List<Edge> edges, List<Node> nodes) {
		graph = new Graph.Builder()
				.nodes(nodes)
				.edges(edges)
				.attr(ZestProperties.LAYOUT_ALGORITHM__G, new TreeLayoutAlgorithm())
				.build();
		
		return graph;
	}

}