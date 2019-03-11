package se.gu.featuredashboard.ui.views;

import org.eclipse.swt.widgets.Composite;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.ui.providers.GraphContentProvider;
import se.gu.featuredashboard.utils.gef.CustomZestFxModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.mvc.fx.ui.MvcFxUiModule;
import org.eclipse.gef.zest.fx.ui.parts.ZestFxUiView;

public class FeatureFileView extends ZestFxUiView {

	private List<Node> graphNodes; 
	private List<Edge> graphEdges;
	
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
		Map<IFile, Node> lookup = new HashMap<>();
		
		graphNodes = new ArrayList<>(); 
		graphEdges = new ArrayList<>();
		
		for(FeatureContainer featureFileContainer : featureFileList) {
			Node featureNode = GraphContentProvider.getFeatureNode(featureFileContainer.getFeature().getFeatureID());
			featureFileContainer.getFiles().forEach(file -> {				
				if(!lookup.containsKey(file)) {
					Node fileNode = GraphContentProvider.getFileNode(file.getName(), file, featureFileContainer.getLines(file));
					lookup.put(file, fileNode);
					graphNodes.add(fileNode);
				}
				graphEdges.add(new Edge(featureNode, lookup.get(file)));
			});	
			graphNodes.add(featureNode);
		}
		
		setGraph(GraphContentProvider.getGraph(graphEdges, graphNodes));
	}

	@Override
	public void setFocus() {
	}

}
