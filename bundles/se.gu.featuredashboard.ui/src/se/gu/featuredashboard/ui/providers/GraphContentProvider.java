package se.gu.featuredashboard.ui.providers;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.zest.fx.ZestProperties;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.utils.FeaturedashboardConstants;
import se.gu.featuredashboard.utils.gef.FeatureNode;
import se.gu.featuredashboard.utils.gef.FileNode;

public class GraphContentProvider {

	public static Node getNestedGraphNode() {
		return new Node.Builder()
				.attr(ZestProperties.SIZE__N, FeaturedashboardConstants.NESTED_NODE_SIZE)
				.attr(ZestProperties.LABEL__NE, "Double-Click to see files")
				.buildNode();
	}

	public static FeatureNode getFeatureNode(Feature feature) {
		FeatureNode featureNode = new FeatureNode(feature);

		ZestProperties.setLabel(featureNode, feature.getFeatureID());
		ZestProperties.setShapeCssStyle(featureNode, "-fx-fill:green;");

		return featureNode;
	}

	public static Node getNode(String nodeLabel) {
		Node node = new Node();

		ZestProperties.setLabelCssStyle(node, "-fx-font-size:12;-fx-fill:white;");
		ZestProperties.setLabel(node, nodeLabel);
		ZestProperties.setSize(node, FeaturedashboardConstants.NODE_SIZE);

		return node;
	}

	public static FileNode getFileNode(IFile file) {
		FileNode fileNode = new FileNode(file);

		ZestProperties.setLabel(fileNode, file.getName().toString());
		ZestProperties.setTooltip(fileNode, file.getFullPath().toString());
		ZestProperties.setLabelCssStyle(fileNode, "-fx-font-size:12;-fx-fill:white;");
		ZestProperties.setSize(fileNode, FeaturedashboardConstants.FILE_NODE_SIZE);

		return fileNode;
	}

	public static Graph getGraph(String id, Collection<Edge> edges, Collection<Node> nodes,
			ILayoutAlgorithm layoutAlgorithm) {
		Graph graph = new Graph.Builder().nodes(nodes).edges(edges).attr(FeaturedashboardConstants.GRAPH_ID_KEY, id)
				.attr(ZestProperties.LAYOUT_ALGORITHM__G, layoutAlgorithm).build();

		return graph;
	}

	public static Graph getGraph(String id, Collection<Node> nodes, ILayoutAlgorithm layoutAlgorithm) {
		Graph graph = new Graph.Builder().nodes(nodes).attr(FeaturedashboardConstants.GRAPH_ID_KEY, id)
				.attr(ZestProperties.LAYOUT_ALGORITHM__G, layoutAlgorithm).build();

		return graph;
	}

}