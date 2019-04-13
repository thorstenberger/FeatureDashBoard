package se.gu.featuredashboard.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IContainer;
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

import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.model.location.FeatureLocation;
import se.gu.featuredashboard.ui.listeners.FeatureSelectionListener;
import se.gu.featuredashboard.ui.providers.GraphContentProvider;
import se.gu.featuredashboard.ui.viewscontroller.GeneralViewsController;
import se.gu.featuredashboard.utils.CustomEdge;
import se.gu.featuredashboard.utils.FeaturedashboardConstants;
import se.gu.featuredashboard.utils.SelectionHandler;
import se.gu.featuredashboard.utils.gef.CustomZestFxModule;

public class FeatureFolderView extends ZestFxUiView implements FeatureSelectionListener {
	
	private GeneralViewsController viewController = GeneralViewsController.getInstance();
	
	private Set<CustomEdge> graphEdges;
	private List<Node> graphNodes;
	private Map<IContainer, Node> lookup;
	private TreeLayoutAlgorithm layoutAlgorithm = new TreeLayoutAlgorithm(TreeLayoutAlgorithm.TOP_DOWN,
			FeaturedashboardConstants.NODE_SPACING);

	public FeatureFolderView() {
		super(Guice.createInjector(Modules.override(new MvcFxUiModule()).with(new CustomZestFxModule())));
	}

	public FeatureFolderView(Injector injector) {
		super(injector);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
	}

	// We can use the same list as we can get the folder that the file is located in
	// from IFile.getParent()
	private void getParentStructure(IContainer parent, IContainer child) {
		if(parent != null) {
			if(lookup.containsKey(parent)) {
				graphEdges.add(new CustomEdge(lookup.get(parent), lookup.get(child)));	
			} else {
				Node folderNode = GraphContentProvider.getNode(parent.getName());
				lookup.put(parent, folderNode);
				graphNodes.add(folderNode);
				graphEdges.add(new CustomEdge(lookup.get(parent), lookup.get(child)));
				
				if(parent instanceof IProject)
					return;
				
				getParentStructure(parent.getParent(), parent);
			}
		}
	}

	
	// We can use the same list as we can get the folder that the file is located in from IFile.getParent()

	@Override
	public void dataUpdated(List<FeatureLocation> featureLocations) {
		/*
>>>>>>> Stashed changes
		lookup = new HashMap<>();
		graphNodes = new ArrayList<>();
		graphEdges = new HashSet<>();

		for (FeatureContainer featureFileContainer : featureFileList) {
			if (featureFileContainer.getFiles().isEmpty())
				continue;
			Node featureNode = GraphContentProvider.getFeatureNode(featureFileContainer.getFeature().getFeatureID());
			featureFileContainer.getFiles().forEach(file -> {
				IContainer folder = file.getParent();
				if (!lookup.containsKey(folder)) {
					Node folderNode = GraphContentProvider.getNode(folder.getName());
					lookup.put(folder, folderNode);
					graphNodes.add(folderNode);
					graphEdges.add(new CustomEdge(folderNode, featureNode));
					if (!(folder instanceof IProject))
						getParentStructure(folder.getParent(), folder);
				} else {
					CustomEdge e = new CustomEdge(lookup.get(folder), featureNode);
					if (!graphEdges.contains(e)) {
						graphEdges.add(e);
					}
				}
			});
			graphNodes.add(featureNode);
		}
<<<<<<< Updated upstream

		setGraph(GraphContentProvider.getGraph(FeaturedashboardConstants.FEATUREFOLDER_VIEW_ID,
				graphEdges.stream().map(customEdge -> (Edge) customEdge).collect(Collectors.toList()), graphNodes,
				layoutAlgorithm));
	}

	private void getParentStructure(IContainer parent, IContainer child) {
		if (parent != null) {
			if (lookup.containsKey(parent)) {
				graphEdges.add(new CustomEdge(lookup.get(parent), lookup.get(child)));
			} else {
				Node folderNode = GraphContentProvider.getNode(parent.getName());
				lookup.put(parent, folderNode);
				graphNodes.add(folderNode);
				graphEdges.add(new CustomEdge(lookup.get(parent), lookup.get(child)));

				if (parent instanceof IProject)
					return;

				getParentStructure(parent.getParent(), parent);
			}
		}
=======
		
		setGraph(GraphContentProvider.getGraph(FeaturedashboardConstants.FEATUREFOLDER_VIEW_ID, graphEdges.stream().map(customEdge -> (Edge) customEdge).collect(Collectors.toList()), graphNodes, layoutAlgorithm)); 
		*/
	}
}