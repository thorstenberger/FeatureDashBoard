package se.gu.featuredashboard.ui.views;

import org.eclipse.swt.widgets.Composite;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import se.gu.featuredashboard.model.featuremodel.FeatureFileContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.gef.mvc.fx.ui.parts.AbstractFXView;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.zest.fx.ZestFxModule;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.zest.fx.ui.ZestFxUiModule;

public class FeatureFileView extends AbstractFXView {

	public FeatureFileView() {
		super(Guice.createInjector(Modules.override(new ZestFxModule()).with(new ZestFxUiModule())));
	}
	
	public FeatureFileView(Injector injector) {
		super(injector);
	}
	
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
	}
	
	public void setInputToView(List<FeatureFileContainer> featureFileList) {
		
		Map<IFile, Node> lookup = new HashMap<>();
		
		List<Node> graphNodes = new ArrayList<>(); 
		List<Edge> graphEdges = new ArrayList<>();
		
		for(FeatureFileContainer featureFileContainer : featureFileList) {
			
			Node featureNode = new Node.Builder()
					.attr(ZestProperties.LABEL__NE, "Feature: \n " + featureFileContainer.getFeature().getFeatureID())
					.attr(ZestProperties.SIZE__N, new Dimension(70,30))
					.attr(ZestProperties.LABEL_CSS_STYLE__NE, "-fx-font-size:14;-fx-font-weight:bold;-fx-fill:white;")
					.attr(ZestProperties.SHAPE_CSS_STYLE__N, "-fx-fill:green;")
					.buildNode();
			
			featureFileContainer.getFiles().forEach(file -> {
				
				if(lookup.containsKey(file)) {
					graphEdges.add(new Edge(featureNode, lookup.get(file)));
				} else {				
					Node fileNode = new Node.Builder()
							.attr(ZestProperties.LABEL__NE, file.getName())
							.buildNode();
					
					lookup.put(file, fileNode);
					
					graphEdges.add(new Edge(featureNode, fileNode));
					graphNodes.add(fileNode);
				}
			});
			
			graphNodes.add(featureNode);
		}
		
		Graph featureFileGraph = new Graph.Builder()
				.nodes(graphNodes)
				.edges(graphEdges)
				.attr(ZestProperties.LAYOUT_ALGORITHM__G, new TreeLayoutAlgorithm())
				.build();
		
		setGraph(featureFileGraph);
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
