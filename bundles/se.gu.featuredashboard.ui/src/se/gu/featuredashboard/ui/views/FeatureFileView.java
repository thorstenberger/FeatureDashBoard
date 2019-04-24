package se.gu.featuredashboard.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.mvc.fx.ui.MvcFxUiModule;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.zest.fx.ui.parts.ZestFxUiView;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.featuremodel.Tuple;
import se.gu.featuredashboard.model.location.FeatureLocation;
import se.gu.featuredashboard.ui.listeners.IFeatureSelectionListener;
import se.gu.featuredashboard.ui.providers.GraphContentProvider;
import se.gu.featuredashboard.ui.viewscontroller.GeneralViewsController;
import se.gu.featuredashboard.utils.FeaturedashboardConstants;
import se.gu.featuredashboard.utils.gef.CustomEdge;
import se.gu.featuredashboard.utils.gef.CustomZestFxModule;
import se.gu.featuredashboard.utils.gef.FeatureFileLayoutAlgorithm;
import se.gu.featuredashboard.utils.gef.FeatureNode;
import se.gu.featuredashboard.utils.gef.FileNode;
import se.gu.featuredashboard.utils.gef.NestedGraphLayoutAlgorithm;

public class FeatureFileView extends ZestFxUiView implements IFeatureSelectionListener {

	private GeneralViewsController viewController = GeneralViewsController.getInstance();

	public FeatureFileView() {
		// Use CustomZestFxModule which contains custom bindings instead of the default one
		super(Guice.createInjector(Modules.override(new MvcFxUiModule()).with(new CustomZestFxModule())));
	}

	public FeatureFileView(Injector injector) {
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
		// We need to know for each file which features are implemented in it. For
		// convenience, save the graphNode here as well.
		Map<IFile, Tuple<Set<Feature>, Node>> fileToTuple = new HashMap<>();
		// Mapping between a feature and its node in the graph
		Map<Feature, FeatureNode> featureToNode = new HashMap<>();
		// We need to know which nodes belongs to which feature
		Map<Feature, List<Node>> featureToNestedNodes = new HashMap<>();

		// All graph edges
		Set<Edge> graphEdges = new HashSet<>();
		// All graph nodes
		Set<Node> graphNodes = new HashSet<>();

		// First we need to know which features are located in a file
		featureLocations.forEach(location -> {
			if (!(location.getResource() instanceof IFile))
				return;

			Feature feature = location.getFeature();

			if (!featureToNode.containsKey(feature))
				featureToNode.put(feature, GraphContentProvider.getFeatureNode(feature));

			IFile file = (IFile) location.getResource();
			
			if (equalsMappingFile(file))
				return;

			Tuple<Set<Feature>, Node> fileTuple = fileToTuple.get(file);
			if (fileTuple == null) {
				Set<Feature> featuresInFile = new HashSet<>();
				featuresInFile.add(feature);

				FileNode fileNode = GraphContentProvider.getFileNode(file);
				fileNode.addAnnotatedLines(location.getBlocklines());

				fileTuple = new Tuple<>(featuresInFile, fileNode);
				fileToTuple.put(file, fileTuple);
			} else {
				Set<Feature> featuresInFile = fileTuple.getLeft();
				featuresInFile.add(feature);

				FileNode fileNode = (FileNode) fileTuple.getRight();
				fileNode.addAnnotatedLines(location.getBlocklines());
			}
		});

		// If there is only one of the selected features in a file, then that file should
		// be place in a nested graph for that feature. Otherwise, the filenode should
		// be placed outside any nested graphs and be connected to the feature nodes directly
		fileToTuple.values().forEach(fileTuple -> {
			FileNode fileNode = (FileNode) fileTuple.getRight();
			List<Feature> features = fileTuple.getLeft().stream().collect(Collectors.toList());

			// If file only has one feature then it should be placed in the nested graph
			if (features.size() == 1) {
				List<Node> nestedNodes = featureToNestedNodes.get(features.get(0));
				if (nestedNodes == null) {
					nestedNodes = new ArrayList<>();
					featureToNestedNodes.put(features.get(0), nestedNodes);
				}
				nestedNodes.add(fileNode);
				graphNodes.add(featureToNode.get(features.get(0)));
			} else {
				features.forEach(feature -> {
					graphNodes.add(featureToNode.get(feature));
					CustomEdge edge = new CustomEdge(featureToNode.get(feature), fileNode);
					// Same problem here as in FeatureTanglingView, i.e., if I don't set any property here, it doesn't
					// seem like I can set It in the click handler. Therefore, add empty css here and add focused in
					// click handler
					ZestProperties.setCurveCssStyle(edge, FeaturedashboardConstants.EDGE_NORMAL_CSS);
					graphEdges.add(edge);
				});
				graphNodes.add(fileNode);
			}
		});

		// All the file nodes that only belong to one feature should be placed in a nested graph. Loop
		// through the featureToNestedNodes which contains these file nodes for each feature.
		for (Feature feature : featureToNestedNodes.keySet()) {
			Node nestedNode = GraphContentProvider.getNestedGraphNode(feature);

			nestedNode.setNestedGraph(GraphContentProvider.getGraph(FeaturedashboardConstants.FEATUREFILE_VIEW_ID,
							featureToNestedNodes.get(feature), new NestedGraphLayoutAlgorithm()));

			graphNodes.add(nestedNode);
			graphEdges.add(new CustomEdge(nestedNode, featureToNode.get(feature)));

		}

		/*featureToNestedNodes.entrySet().forEach(entry -> {
			Node nestedNode = GraphContentProvider.getNestedGraphNode(entry.getKey());
		
			nestedNode.setNestedGraph(GraphContentProvider.getGraph(FeaturedashboardConstants.FEATUREFILE_VIEW_ID,
							entry.getValue(), new NestedGraphLayoutAlgorithm()));
		
			graphNodes.add(nestedNode);
			graphEdges.add(new CustomEdge(featureToNode.get(entry.getKey()), nestedNode));
		});*/

		// Set graph
		setGraph(GraphContentProvider.getGraph(FeaturedashboardConstants.FEATUREFILE_VIEW_ID, graphEdges, graphNodes,
						new FeatureFileLayoutAlgorithm()));

	}

	@Override
	public void dispose() {
		super.dispose();
		viewController.removeFeatureSelectionListener(this);
	}

	private boolean equalsMappingFile(IFile file) {
		if (file.getFileExtension() == null)
			return false;

		return file.getFileExtension().equals(FeaturedashboardConstants.FEATUREFILE_FILE)
				|| file.getFileExtension().equals(FeaturedashboardConstants.FEATUREFOLDER_FILE)
				|| file.getFileExtension().equals(FeaturedashboardConstants.VPFOLDER_FILE)
				|| file.getFileExtension().equals(FeaturedashboardConstants.VPFILE_FILE);

	}

}
