/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.ui.views;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef.mvc.fx.ui.MvcFxUiModule;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.zest.fx.ui.parts.ZestFxUiView;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.location.FeatureLocation;
import se.gu.featuredashboard.ui.listeners.IFeatureSelectionListener;
import se.gu.featuredashboard.ui.listeners.IProjectSelectionListener;
import se.gu.featuredashboard.ui.providers.GraphContentProvider;
import se.gu.featuredashboard.ui.viewscontroller.FeatureDashboardViewController;
import se.gu.featuredashboard.ui.viewscontroller.GeneralViewsController;
import se.gu.featuredashboard.utils.FeaturedashboardConstants;
import se.gu.featuredashboard.utils.gef.CustomEdge;
import se.gu.featuredashboard.utils.gef.CustomZestFxModule;
import se.gu.featuredashboard.utils.gef.FeatureNode;

public class FeatureTanglingView extends ZestFxUiView implements IFeatureSelectionListener, IProjectSelectionListener {

	private GeneralViewsController viewController;

	private SpringLayoutAlgorithm springLayoutAlgorithm = new SpringLayoutAlgorithm();

	private Map<IFile, Set<Feature>> fileToFeatures;
	private Map<Feature, Node> allNodes;

	public FeatureTanglingView() {
		super(Guice.createInjector(Modules.override(new MvcFxUiModule()).with(new CustomZestFxModule())));
	}

	public FeatureTanglingView(Injector injector) {
		super(injector);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		viewController = GeneralViewsController.getInstance();
		viewController.registerFeatureSelectionListener(this);
		viewController.registerProjectSelectionListener(this);

		updateFeatureSelection(viewController.getLocations());

		springLayoutAlgorithm.setResizing(false);
		springLayoutAlgorithm.setSpringLength(100);
	}

	/**
	 * Called fist time view is opened and when the selected project is changed
	 */
	private void initilizeStructure() {
		List<FeatureLocation> allLocations = FeatureDashboardViewController.getInstance().getAllLocations();

		fileToFeatures = new HashMap<>();
		allNodes = new HashMap<>();

		// Consider tangling in files and not folders..
		allLocations.forEach(location -> {
			if (location.getResource() instanceof IFolder || location.getResource() instanceof IContainer)
				return;

			Feature feature = location.getFeature();
			IFile file = (IFile) location.getResource();

			FeatureNode featureNode = (FeatureNode) allNodes.get(feature);
			if (featureNode == null) {
				featureNode = GraphContentProvider.getFeatureNode(feature);
				allNodes.put(feature, featureNode);
			}

			// Acociate features with files
			Set<Feature> features = fileToFeatures.get(file);
			if (features == null) {
				features = new HashSet<>();
				fileToFeatures.put(file, features);
			}
			features.add(feature);
		});
	}

	@Override
	public void updateFeatureSelection(List<FeatureLocation> featureLocations) {
		if (fileToFeatures == null || allNodes == null)
			initilizeStructure();
		
		Map<String, Edge> graphEdges = new HashMap<>();
		Set<Node> graphNodes = new HashSet<>();
		Set<Feature> selectedFeatures = new HashSet<>();

		featureLocations.forEach(location -> {
			if (location.getResource() instanceof IFolder || location.getResource() instanceof IContainer)
				return;

			IFile file = (IFile) location.getResource();

			Set<Feature> featuresInFile = fileToFeatures.get(file);
			Feature currentFeature = location.getFeature();

			selectedFeatures.add(currentFeature);

			featuresInFile.forEach(featureInFile -> {
				graphNodes.add(allNodes.get(featureInFile));

				ZestProperties.setShapeCssStyle(allNodes.get(featureInFile), "-fx-fill:green;");

				if (currentFeature.equals(featureInFile))
					return;

				String key = currentFeature.getFeatureID() + "->" + featureInFile.getFeatureID();

				CustomEdge edge = (CustomEdge) graphEdges.get(key);
				if (edge == null) {
					edge = new CustomEdge(allNodes.get(currentFeature), allNodes.get(featureInFile));
					// In EdgeOnClickPolicy when we have the node that we clicked on, for some
					// reason using setLabel won't work if a label isn't already set before the edge
					// is attached to the graph. Therefore, set an empty label here.
					ZestProperties.setLabel(edge, "");
					graphEdges.put(key, edge);
				}
				edge.addFile(file);

			});
		});
		
		selectedFeatures.forEach(feature -> {
			// Make the feature(s) selected blue so that they are easy to distinguish
			ZestProperties.setShapeCssStyle(allNodes.get(feature), "-fx-fill:blue;");
		});

		setGraph(GraphContentProvider.getGraph(FeaturedashboardConstants.FEATURETANGLING_VIEW_ID, graphEdges.values(),
						graphNodes, springLayoutAlgorithm));

	}

	@Override
	public void updateProjectSelected() {
		// The current project has been changed. Reset structure
		fileToFeatures = null;
		allNodes = null;
	}

	@Override
	public void dispose() {
		super.dispose();

		updateProjectSelected();

		viewController.removeFeatureSelectionListener(this);
		viewController.removeProjectSelectionListener(this);
	}

}
