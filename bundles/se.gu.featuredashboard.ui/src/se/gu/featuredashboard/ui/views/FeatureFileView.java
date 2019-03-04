package se.gu.featuredashboard.ui.views;

import org.eclipse.swt.widgets.Composite;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.ui.providers.GraphContentProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.mvc.fx.ui.MvcFxUiModule;
import org.eclipse.gef.mvc.fx.ui.parts.AbstractFXView;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.zest.fx.ZestFxModule;

public class FeatureFileView extends AbstractFXView {

	private List<Node> graphNodes; 
	private List<Edge> graphEdges;
	
	public FeatureFileView() {
		super(Guice.createInjector(Modules.override(new MvcFxUiModule()).with(new ZestFxModule())));
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
					Node fileNode = GraphContentProvider.getNormalNode(file.getName());
					lookup.put(file, fileNode);
					graphNodes.add(fileNode);
				}
				graphEdges.add(new Edge(featureNode, lookup.get(file)));
			});	
			graphNodes.add(featureNode);
		}
		
		setGraph(GraphContentProvider.getGraph(graphEdges, graphNodes));
	}
	
	private void setGraph(Graph graph) {
		// check we have a content viewer
		IViewer contentViewer = getContentViewer();
		if (contentViewer == null) {
			throw new IllegalStateException("Invalid configuration: Content viewer could not be retrieved.");
		}
		// set contents (will wrap graph into contents list)
		List<Object> contents = new ArrayList<>(1);
		if (graph != null) {
			contents.add(graph);
		}
		contentViewer.getContents().setAll(contents);
	}

	@Override
	public void setFocus() {
	}

}
