package se.gu.featuredashboard.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.graph.Node;
import org.eclipse.gef.mvc.fx.ui.MvcFxUiModule;
import org.eclipse.gef.zest.fx.ui.parts.ZestFxUiView;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.model.location.FeatureLocation;
import se.gu.featuredashboard.ui.listeners.FeatureSelectionListener;
import se.gu.featuredashboard.ui.providers.GraphContentProvider;
import se.gu.featuredashboard.ui.viewscontroller.FeatureDashboardViewController;
import se.gu.featuredashboard.ui.viewscontroller.GeneralViewsController;
import se.gu.featuredashboard.utils.FeaturedashboardConstants;
import se.gu.featuredashboard.utils.SelectionHandler;
import se.gu.featuredashboard.utils.gef.CustomGridLayoutAlgorithm;
import se.gu.featuredashboard.utils.gef.CustomZestFxModule;

public class FeatureFileView extends ZestFxUiView implements FeatureSelectionListener {
	
	private GeneralViewsController viewController = GeneralViewsController.getInstance();
	
	public FeatureFileView() {
		super(Guice.createInjector(Modules.override(new MvcFxUiModule()).with(new CustomZestFxModule())));
	}

	public FeatureFileView(Injector injector) {
		super(injector);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		dataUpdated(viewController.getLocations());
	}
	

	@Override
	public void setFocus() {
	}

	@Override
	public void dataUpdated(List<FeatureLocation> featureLocations) {
		List<Node> graphNodes = new ArrayList<>();
		Map<Feature, Node> map = new HashMap<>();
		
		for(FeatureLocation featureLocation : featureLocations) {
			Node featureNode = map.get(featureLocation.getFeature());
			
			if(featureNode == null) {
				featureNode = GraphContentProvider.getFeatureNode(featureLocation.getFeature().getFeatureID());
				map.put(featureLocation.getFeature(), featureNode);
				graphNodes.add(featureNode);
			}
			
		}

		setGraph(GraphContentProvider.getGraph(FeaturedashboardConstants.FEATUREFILE_VIEW_ID, graphNodes,
				new CustomGridLayoutAlgorithm(40, 40)));
	}

}
