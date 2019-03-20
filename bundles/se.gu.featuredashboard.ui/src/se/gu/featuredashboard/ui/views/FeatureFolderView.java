package se.gu.featuredashboard.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.zest.fx.ZestFxModule;
import org.eclipse.gef.zest.fx.ui.ZestFxUiModule;
import org.eclipse.gef.zest.fx.ui.parts.ZestFxUiView;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.ui.providers.GraphContentProvider;

public class FeatureFolderView extends ZestFxUiView {

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
	public void inputToView(List<FeatureContainer> featureFileList) {
		Map<IContainer, Node> lookup = new HashMap<>();
		
		graphNodes = new ArrayList<>(); 
		graphEdges = new ArrayList<>();
		
		for(FeatureContainer featureFileContainer : featureFileList) {
			Node featureNode = GraphContentProvider.getFeatureNode(featureFileContainer.getFeature().getFeatureID());
			featureFileContainer.getFiles().forEach(file -> {
				IFolder folder = (IFolder) file.getParent();
				if(!lookup.containsKey(folder)) {
					Node folderNode = GraphContentProvider.getNormalNode(folder.getName());
					lookup.put(folder, folderNode);
					graphNodes.add(folderNode); 
					graphEdges.add(new Edge(folderNode, featureNode));
					getParentStructure(folder.getParent(), folder, lookup);
				}
			});
			graphNodes.add(featureNode);
		}
		setGraph(GraphContentProvider.getGraph(graphEdges, graphNodes));
	} 
	
	private void getParentStructure(IContainer parent, IContainer child, Map<IContainer, Node> lookup) {
		if(parent != null) {
			if(lookup.containsKey(parent)) {
				graphEdges.add(new Edge(lookup.get(parent), lookup.get(child)));	
			} else {
				Node folderNode = GraphContentProvider.getNormalNode(parent.getName());
				lookup.put(parent, folderNode);
				graphNodes.add(folderNode);
				graphEdges.add(new Edge(lookup.get(parent), lookup.get(child)));
				if(!(parent instanceof IProject)) {
					getParentStructure(parent.getParent(), parent, lookup);
				}
			}
		}
	}
	
}