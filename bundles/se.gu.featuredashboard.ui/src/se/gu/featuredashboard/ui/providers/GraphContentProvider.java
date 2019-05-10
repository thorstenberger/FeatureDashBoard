/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

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

	public static Node getNestedGraphNode(Feature feature) {
		Node nestedNode = new Node();

		ZestProperties.setLabel(nestedNode, "Additional files");
		ZestProperties.setTooltip(nestedNode,
						"Double-Click this node to see files belonging to: " + feature.getFeatureID());

		return nestedNode;
	}

	public static FeatureNode getFeatureNode(Feature feature) {
		FeatureNode featureNode = new FeatureNode(feature);

		ZestProperties.setLabel(featureNode, feature.getFeatureID());
		ZestProperties.setShapeCssStyle(featureNode, "-fx-fill:green;");

		return featureNode;
	}

	public static Node getNode(String nodeLabel) {
		return new Node.Builder().attr(ZestProperties.LABEL__NE, nodeLabel).buildNode();
	}

	public static FileNode getFileNode(IFile file) {
		FileNode fileNode = new FileNode(file);

		ZestProperties.setLabel(fileNode, file.getName().toString());
		ZestProperties.setTooltip(fileNode, file.getFullPath().toString());
		ZestProperties.setLabelCssStyle(fileNode, "-fx-font-size:12;-fx-fill:white;");

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