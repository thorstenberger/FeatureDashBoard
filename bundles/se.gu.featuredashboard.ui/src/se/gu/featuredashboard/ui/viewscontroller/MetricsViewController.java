package se.gu.featuredashboard.ui.viewscontroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import Listeners.IUpdateInformationListener;
import Listeners.ParseChangeListener;
import se.gu.featuredashboard.core.WorkspaceData;
import se.gu.featuredashboard.model.location.ProjectData;
import se.gu.featuredashboard.parsing.MainParser;
import se.gu.featuredashboard.ui.views.FeatureDashboardView;
import se.gu.featuredashboard.ui.views.MetricsView;

public class MetricsViewController implements IUpdateInformationListener{

	private static MetricsViewController INSTANCE;
	public static MetricsViewController getInstance() {		
		if (INSTANCE == null) {
			synchronized (MetricsViewController.class) {
				if (INSTANCE == null) {
					INSTANCE = new MetricsViewController();
				}
			}
		}
		return INSTANCE;
	}
	
	private MetricsViewController() {
		
	}
	
	private List<MainParser> workspaceParsers = new ArrayList<MainParser>();
	private volatile int doneParsers = 0;
	
	public void refreshWorkspaceData() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		List<IProject> rootProjects = Arrays.asList(root.getProjects());
		
		for(int i=0;i<rootProjects.size();i++) {
			MainParser parser = new MainParser();
			workspaceParsers.add(parser);
			parser.setProject(rootProjects.get(i));
			parser.addJobChangeListener(new ParseChangeListener(Arrays.asList(this)));
			parser.setUser(true);
		}
		
		for(int i=0;i<rootProjects.size();i++)
			workspaceParsers.get(i).schedule();
		
	}

	@Override
	synchronized public void parsingComplete() {
		if(workspaceParsers.size()>0){
			doneParsers++;
			if(workspaceParsers.size()!=0 && doneParsers == workspaceParsers.size()) {
				workspaceParsers.forEach(parser->{
					ProjectData projectData =parser.getProjectData(); 
					if(projectData != null) {
						projectData.updateMetrics();
						WorkspaceData.getInstance().setProjectData(projectData);	
					}
				});
				
				Display.getDefault().asyncExec(() -> {
					MetricsView view = (MetricsView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().findView("se.gu.featuredashboard.ui.views.MetricsView");
					if (view != null) {
						view.updateFeatureMetricsTab();
						view.updateResourceMetricsTab();
					}
				});
				
				workspaceParsers.clear();
				doneParsers=0;
			}
		}
		
	}
	
	

}
