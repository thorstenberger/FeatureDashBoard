package se.gu.featuredashboard.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
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
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import se.gu.featuredashboard.model.featuremodel.FeatureFileContainer;

public class FeatureFolderView extends AbstractFXView {

	private List<Edge> graphEdges;
	private List<Node> graphNodes;
	
	public FeatureFolderView() {
		super(Guice.createInjector(Modules.override(new ZestFxModule()).with(new ZestFxUiModule())));
	}
	
	public FeatureFolderView(Injector injector) {
		super(injector);
	}
	
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
	}
	
	// We can use the same list as we can get the folder that the file is located in from IFile.getParent()
	public void setInputToView(List<FeatureFileContainer> featureFileList) {
		
		Map<IContainer, Node> lookup = new HashMap<>();
		
		graphNodes = new ArrayList<>(); 
		graphEdges = new ArrayList<>();
		
		for(FeatureFileContainer featureFileContainer : featureFileList) {
			
			Node featureNode = new Node.Builder()
					.attr(ZestProperties.LABEL__NE, "Feature: \n " + featureFileContainer.getFeature().getFeatureID())
					.attr(ZestProperties.SIZE__N, new Dimension(70,30))
					.attr(ZestProperties.LABEL_CSS_STYLE__NE, "-fx-font-size:14;-fx-font-weight:bold;-fx-fill:white;")
					.attr(ZestProperties.SHAPE_CSS_STYLE__N, "-fx-fill:green;")
					.buildNode();
			
			featureFileContainer.getFiles().forEach(file -> {
				
				IFolder folder = (IFolder) file.getParent();
				
				if(!lookup.containsKey(folder)) {
					Node folderNode = new Node.Builder()
							.attr(ZestProperties.LABEL__NE, folder.getName())
							.buildNode();
					
					lookup.put(folder, folderNode);
					graphNodes.add(folderNode);
				}
				graphEdges.add(new Edge(lookup.get(folder), featureNode));
				getParentStructure(folder.getParent(), folder, lookup);
			});
			
			graphNodes.add(featureNode);
		}
		
		if(graphNodes == null) {
			System.out.println("NUULL");
		}
		for(Edge edge : graphEdges) {
			System.out.println("SRC: " + edge.getSource().toString());
			System.out.println("DEST: " + edge.getTarget().toString());
		}
		
		Graph featureFileGraph = new Graph.Builder()
				.nodes(graphNodes)
				.edges(graphEdges)
				.attr(ZestProperties.LAYOUT_ALGORITHM__G, new TreeLayoutAlgorithm())
				.build();
		
		if(featureFileGraph == null) {
			System.out.println("WHAAAT");
		}
		
		setGraph(featureFileGraph);
		
	}
	
	private void getParentStructure(IContainer parent, IContainer child, Map<IContainer, Node> lookup) {
		
		if(parent != null) {
			
			System.out.println("Parent: " + parent.getName());
			System.out.println("Child: " + child.getName());
			
			if(!(parent instanceof IProject)) {
			
				IFolder parentFolder = (IFolder) parent;
				IFolder childFolder = (IFolder) child;
				
				if(!lookup.containsKey(parent)){
					
					Node folderNode = new Node.Builder()
							.attr(ZestProperties.LABEL__NE, parent.getName())
							.buildNode();
					lookup.put(parent, folderNode);
					graphNodes.add(folderNode);
				}
				graphEdges.add(new Edge(lookup.get(parent), lookup.get(child)));
				
				getParentStructure(parent.getParent(), parent, lookup);
			} else {
				if(!lookup.containsKey(parent)) {
					Node rootNode = new Node.Builder()
							.attr(ZestProperties.LABEL__NE,	parent.getName())
							.buildNode();
					lookup.put(parent, rootNode);
					graphNodes.add(rootNode);
				}
				
				graphEdges.add(new Edge(lookup.get(parent), lookup.get(child)));
			}
			
		}
		
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
	
}
