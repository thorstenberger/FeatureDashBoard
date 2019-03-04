package se.gu.featuredashboard.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.mvc.fx.ui.parts.AbstractFXView;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.zest.fx.ZestFxModule;
import org.eclipse.gef.zest.fx.ui.ZestFxUiModule;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import se.gu.featuredashboard.model.featuremodel.FeatureFileContainer;
import se.gu.featuredashboard.ui.providers.GraphContentProvider;

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
			Node featureNode = GraphContentProvider.getFeatureNode(featureFileContainer.getFeature().getFeatureID());
			featureFileContainer.getFiles().forEach(file -> {
				IFolder folder = (IFolder) file.getParent();
				if(!lookup.containsKey(folder)) {
					Node folderNode = GraphContentProvider.getNormalNode(folder.getName());
					lookup.put(folder, folderNode);
					graphNodes.add(folderNode);
				}
				graphEdges.add(new Edge(lookup.get(folder), featureNode));
				getParentStructure(folder.getParent(), folder, lookup);
			});
			graphNodes.add(featureNode);
		}
		
		setGraph(GraphContentProvider.getGraph(graphEdges, graphNodes));
	}
	
	private void getParentStructure(IContainer parent, IContainer child, Map<IContainer, Node> lookup) {
		if(parent != null) {				
			if(!lookup.containsKey(parent)){
				Node folderNode = GraphContentProvider.getNormalNode(parent.getName());
				lookup.put(parent, folderNode);
				graphNodes.add(folderNode);
			}
			graphEdges.add(new Edge(lookup.get(parent), lookup.get(child)));
			if(!(parent instanceof IProject)) {
				getParentStructure(parent.getParent(), parent, lookup);
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