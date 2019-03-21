package se.gu.featuredashboard.ui.views;

import org.eclipse.swt.widgets.Composite;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.ui.providers.GraphContentProvider;
import se.gu.featuredashboard.utils.gef.CustomGridLayoutAlgorithm;
import se.gu.featuredashboard.utils.gef.CustomZestFxModule;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.algorithms.*;
import org.eclipse.gef.mvc.fx.ui.MvcFxUiModule;
import org.eclipse.gef.zest.fx.ui.parts.ZestFxUiView;

public class FeatureFileView extends ZestFxUiView {
	
	public FeatureFileView() {
		super(Guice.createInjector(Modules.override(new MvcFxUiModule()).with(new CustomZestFxModule())));
	}
	
	public FeatureFileView(Injector injector) {
		super(injector);
	}
	
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
	}
	
	public void inputToView(List<FeatureContainer> featureFileList) {
		List<Node> graphNodes = new ArrayList<>();
		
		for(FeatureContainer featureFileContainer : featureFileList) {
			Node featureNode = GraphContentProvider.getFeatureNode(featureFileContainer.getFeature().getFeatureID());
			
			List<Node> featureFileNodes = new ArrayList<>();
			
			featureFileContainer.getFiles().forEach(file -> {
				Node fileNode = GraphContentProvider.getFileNode(file.getName(), file.getFullPath().toString(), file, featureFileContainer.getLines(file));
				featureFileNodes.add(fileNode);
			});	
				
			featureNode.setNestedGraph(GraphContentProvider.getGraph(featureFileNodes, new CustomGridLayoutAlgorithm()));
			graphNodes.add(featureNode);
		}
		
		if(graphNodes.size() > 1)
			setGraph(GraphContentProvider.getGraph(graphNodes, new GridLayoutAlgorithm()));
		else
			setGraph(GraphContentProvider.getGraph(graphNodes));
	}

	@Override
	public void setFocus() {
	}

}
