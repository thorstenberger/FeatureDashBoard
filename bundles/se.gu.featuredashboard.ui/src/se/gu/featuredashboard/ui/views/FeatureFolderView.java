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
import org.eclipse.gef.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.gef.zest.fx.ZestFxModule;
import org.eclipse.gef.zest.fx.ui.ZestFxUiModule;
import org.eclipse.gef.zest.fx.ui.parts.ZestFxUiView;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.ui.providers.GraphContentProvider;
import se.gu.featuredashboard.utils.CustomEdge;

public class FeatureFolderView extends ZestFxUiView {

	private Set<CustomEdge> graphEdges;
	private List<Node> graphNodes;
	private Map<IContainer, Node> lookup;
	
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
		lookup = new HashMap<>();
		graphNodes = new ArrayList<>(); 
		graphEdges = new HashSet<>();
		
		for(FeatureContainer featureFileContainer : featureFileList) {
			Node featureNode = GraphContentProvider.getFeatureNode(featureFileContainer.getFeature().getFeatureID());
			featureFileContainer.getFiles().forEach(file -> {
				IFolder folder = (IFolder) file.getParent();
				if(!lookup.containsKey(folder)) {
					Node folderNode = GraphContentProvider.getNode(folder.getName());
					lookup.put(folder, folderNode);
					graphNodes.add(folderNode); 
					graphEdges.add(new CustomEdge(folderNode, featureNode));
					getParentStructure(folder.getParent(), folder);
				} else {
					CustomEdge e = new CustomEdge(lookup.get(folder), featureNode);
					if(!graphEdges.contains(e)) {
						graphEdges.add(new CustomEdge(lookup.get(folder), featureNode));
					}				
				}
			});
			graphNodes.add(featureNode);
		}
		setGraph(GraphContentProvider.getGraph(graphEdges.stream().map(customEdge -> (Edge) customEdge).collect(Collectors.toList()), graphNodes, new TreeLayoutAlgorithm()));
	} 
	
	private void getParentStructure(IContainer parent, IContainer child) {
		if(parent != null) {
			if(lookup.containsKey(parent)) {
				graphEdges.add(new CustomEdge(lookup.get(parent), lookup.get(child)));	
			} else {
				Node folderNode = GraphContentProvider.getNode(parent.getName());
				lookup.put(parent, folderNode);
				graphNodes.add(folderNode);
				graphEdges.add(new CustomEdge(lookup.get(parent), lookup.get(child)));
				if(!(parent instanceof IProject)) {
					getParentStructure(parent.getParent(), parent);
				}
			}
		}
	}
}