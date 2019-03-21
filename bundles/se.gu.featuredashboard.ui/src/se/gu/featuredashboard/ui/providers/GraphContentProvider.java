package se.gu.featuredashboard.ui.providers;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.zest.fx.ZestProperties;

import se.gu.featuredashboard.model.location.BlockLine;
import se.gu.featuredashboard.utils.gef.FeatureNode;
import se.gu.featuredashboard.utils.gef.FileNode;

public class GraphContentProvider {
	
	private static FeatureNode featureNode;
	private static Node node;
	private static Graph graph;
	private static FileNode fileNode;
	
	public static Node getFeatureNode(String nodeLabel) {
		featureNode = new FeatureNode();
		
		ZestProperties.setLabel(featureNode, nodeLabel);
		ZestProperties.setLabelCssStyle(featureNode, "-fx-font-size:16;-fx-fill:white;");
		ZestProperties.setShapeCssStyle(featureNode, "-fx-fill:green;");
		ZestProperties.setTooltip(featureNode, "Double-click to node to see files");
		
		return featureNode;
	}
	
	public static Node getNode(String nodeLabel) {
		node = new Node.Builder()
				.attr(ZestProperties.LABEL_CSS_STYLE__NE, "-fx-font-size:12;-fx-fill:white;")
				.attr(ZestProperties.LABEL__NE, nodeLabel)
				.buildNode();
		
		return node;
	}

	public static Node getFileNode(String nodeLabel, String tooltip, IFile file, List<BlockLine> annotatedLines) {		
		fileNode = new FileNode(file, annotatedLines);
		
		ZestProperties.setLabel(fileNode, nodeLabel);
		ZestProperties.setLabelCssStyle(fileNode, "-fx-font-size:14;-fx-fill:white;");
		ZestProperties.setTooltip(fileNode, tooltip);
		
		return fileNode;
	}
	
	public static Graph getGraph(List<Edge> edges, List<Node> nodes, ILayoutAlgorithm layoutAlgorithm) {
		graph = new Graph.Builder()
				.nodes(nodes)
				.edges(edges)
				.attr(ZestProperties.LAYOUT_ALGORITHM__G, layoutAlgorithm)
				.build();
		
		return graph;
	}
	
	public static Graph getGraph(List<Node> nodes, ILayoutAlgorithm layoutAlgorithm) {
		graph = new Graph.Builder()
				.nodes(nodes)
				.attr(ZestProperties.LAYOUT_ALGORITHM__G, layoutAlgorithm)
				.build();
		
		return graph;
	}

	public static Graph getGraph(List<Node> nodes) {
		graph = new Graph.Builder()
				.nodes(nodes)
				.build();
		
		return graph;
	}
	
}