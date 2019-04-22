package se.gu.featuredashboard.ui.views;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.zest.fx.ui.parts.ZestFxUiView;
import org.eclipse.swt.widgets.Composite;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.location.FeatureLocation;
import se.gu.featuredashboard.ui.listeners.IFeatureSelectionListener;
import se.gu.featuredashboard.ui.listeners.IProjectSelectionListener;
import se.gu.featuredashboard.ui.providers.GraphContentProvider;
import se.gu.featuredashboard.ui.viewscontroller.FeatureDashboardViewController;
import se.gu.featuredashboard.ui.viewscontroller.GeneralViewsController;
import se.gu.featuredashboard.utils.CustomEdge;
import se.gu.featuredashboard.utils.FeaturedashboardConstants;
import se.gu.featuredashboard.utils.gef.FeatureNode;

public class FeatureTanglingView extends ZestFxUiView implements IFeatureSelectionListener, IProjectSelectionListener {

	private SpringLayoutAlgorithm springLayoutAlgorithm = new SpringLayoutAlgorithm();

	private Map<IFile, Set<Feature>> fileToFeatures;
	private Map<Feature, FeatureNode> allNodes;

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		GeneralViewsController.getInstance().registerFeatureSelectionListener(this);
		springLayoutAlgorithm.setResizing(false);
		springLayoutAlgorithm.setSpringLength(50);
	}

	/**
	 * Called first time view is showed. Afterwards just want to listen to selection
	 * changes. This should be calculated after/during parsing the project
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

			FeatureNode featureNode = allNodes.get(feature);
			if (featureNode == null) {
				featureNode = GraphContentProvider.getFeatureNode(feature.getFeatureID());
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

		Set<CustomEdge> graphEdges = new HashSet<>();
		Set<FeatureNode> graphNodes = new HashSet<>();

		featureLocations.forEach(location -> {
			if (location.getResource() instanceof IFolder || location.getResource() instanceof IContainer)
				return;

			IFile file = (IFile) location.getResource();

			Set<Feature> featuresInFile = fileToFeatures.get(file);
			Feature currentFeature = location.getFeature();
			// Make the feature(s) selected blue so that they are easy to distinguish
			ZestProperties.setShapeCssStyle(allNodes.get(currentFeature), "-fx-fill:blue;");

			featuresInFile.forEach(featureInFile -> {
				graphNodes.add(allNodes.get(featureInFile));

				if (currentFeature.equals(featureInFile))
					return;

				// Make the rest of the nodes green
				ZestProperties.setShapeCssStyle(allNodes.get(featureInFile), "-fx-fill:green;");

				CustomEdge e = new CustomEdge(allNodes.get(currentFeature), allNodes.get(featureInFile));

				graphEdges.add(e);
			});
		});

		List<Edge> edges = graphEdges.stream().map(customEdge -> (Edge) customEdge).collect(Collectors.toList());
		List<Node> nodes = graphNodes.stream().map(featureNode -> (Node) featureNode).collect(Collectors.toList());

		setGraph(GraphContentProvider.getGraph(FeaturedashboardConstants.FEATURETANGLING_VIEW_ID, edges, nodes,
				springLayoutAlgorithm));

	}

	@Override
	public void updateProjectSelected() {
		// The current project has been changed. Reset structure
		fileToFeatures = null;
		allNodes = null;
	}

}
