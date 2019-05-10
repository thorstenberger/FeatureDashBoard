/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.gef.mvc.fx.ui.MvcFxUiModule;
import org.eclipse.gef.zest.fx.ui.parts.ZestFxUiView;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.location.FeatureLocation;
import se.gu.featuredashboard.ui.listeners.IFeatureSelectionListener;
import se.gu.featuredashboard.ui.providers.GraphContentProvider;
import se.gu.featuredashboard.ui.viewscontroller.GeneralViewsController;
import se.gu.featuredashboard.utils.FeaturedashboardConstants;
import se.gu.featuredashboard.utils.gef.CustomEdge;
import se.gu.featuredashboard.utils.gef.CustomZestFxModule;
import se.gu.featuredashboard.utils.gef.FeatureNode;

public class FeatureFolderView extends ZestFxUiView implements IFeatureSelectionListener {

	private GeneralViewsController viewController = GeneralViewsController.getInstance();

	private Set<Edge> graphEdges;
	private Map<Feature, FeatureNode> featureToNode;
	private Map<IContainer, Node> resourceToNode;
	private TreeLayoutAlgorithm layoutAlgorithm = new TreeLayoutAlgorithm(TreeLayoutAlgorithm.TOP_DOWN,
			FeaturedashboardConstants.FOLDER_NODE_SPACING);

	public FeatureFolderView() {
		super(Guice.createInjector(Modules.override(new MvcFxUiModule()).with(new CustomZestFxModule())));
	}

	public FeatureFolderView(Injector injector) {
		super(injector);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		viewController.registerFeatureSelectionListener(this);
		updateFeatureSelection(viewController.getLocations());
	}

	@Override
	public void updateFeatureSelection(List<FeatureLocation> featureLocations) {
		resourceToNode = new HashMap<>();
		featureToNode = new HashMap<>();
		graphEdges = new HashSet<>();

		for (FeatureLocation featureLocation : featureLocations) {
			Feature feature = featureLocation.getFeature();
			
			FeatureNode featureNode = featureToNode.get(feature);
			if(featureNode == null) {
				featureNode = GraphContentProvider.getFeatureNode(feature);
				featureToNode.put(feature, featureNode);
			}

			if (!(featureLocation.getResource() instanceof IFile))
				continue;

			IContainer folder = featureLocation.getResource().getParent();

			if (!resourceToNode.containsKey(folder)) {
				Node folderNode = GraphContentProvider.getNode(folder.getName());
				resourceToNode.put(folder, folderNode);
				graphEdges.add(new CustomEdge(folderNode, featureNode));
				if (!(folder instanceof IProject)) {
					getParentStructure(folder.getParent(), folder);
				}
			} else {
				CustomEdge e = new CustomEdge(resourceToNode.get(folder), featureNode);
				if (!graphEdges.contains(e)) {
					graphEdges.add(e);
				}
			}
		}

		List<Node> nodes = new ArrayList<>();

		nodes.addAll(resourceToNode.values());
		nodes.addAll(featureToNode.values());

		setGraph(GraphContentProvider.getGraph(FeaturedashboardConstants.FEATUREFOLDER_VIEW_ID, graphEdges, nodes,
						layoutAlgorithm));

	}

	private void getParentStructure(IContainer parent, IContainer child) {
		if (parent != null) {
			if (resourceToNode.containsKey(parent)) {
				graphEdges.add(new CustomEdge(resourceToNode.get(parent), resourceToNode.get(child)));
			} else {
				Node folderNode = GraphContentProvider.getNode(parent.getName());
				resourceToNode.put(parent, folderNode);
				graphEdges.add(new CustomEdge(folderNode, resourceToNode.get(child)));

				if (parent instanceof IProject)
					return;

				getParentStructure(parent.getParent(), parent);
			}
		}
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();
		viewController.removeFeatureSelectionListener(this);
	}
}