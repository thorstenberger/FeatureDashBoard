package se.gu.featuredashboard.utils;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import se.gu.featuredashboard.model.featuremodel.Project;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.ui.views.FeatureDashboardView;
import se.gu.featuredashboard.ui.views.FeatureMetricsView;
import se.gu.featuredashboard.ui.views.ProjectMetricsView;

public class Builder extends IncrementalProjectBuilder {
	private Logger logger = PlatformUI.getWorkbench().getService(org.eclipse.e4.core.services.log.Logger.class);
	
	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		
		logger.info("Featuredashboard builder was triggered");
		
		IProject project = getProject();
		IResourceDelta delta = getDelta(project);
		
		if(kind == AUTO_BUILD) {
			logger.info("Featuredashboard builder autobuild");
//			startParseJob(iProject, delta);
		}
		
		return null;
	}
	
//	private void startParseJob(IProject iProject, IResourceDelta delta) {
//		Display.getDefault().asyncExec(() -> {
//			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//			
//			// Views that should listen when the parsing job is done
//			FeatureDashboardView featureDashboardView = (FeatureDashboardView) page.findView(FeaturedashboardConstants.FEATURE_DASHBOARD_VIEW_ID);
//			//FeatureMetricsView featureMetricsView = (FeatureMetricsView) page.findView(FeaturedashboardConstants.FEATUREMETRICS_VIEW_ID);
//			ProjectMetricsView projectMetricsView = (ProjectMetricsView) page.findView(FeaturedashboardConstants.PROJECTMETRICS_VIEW_ID);
//			
//			JobChangeListener jobChangeListener = new JobChangeListener(Arrays.asList(featureDashboardView, projectMetricsView));
//			
//			Project project = ProjectStore.getProject(iProject.getLocation());
//			
//			// Means that a builder is attached in a previous session but we haven't parsed the project in this session
//			if(project == null)
//				return;
//			
//			findAffectedChildren(project, delta, jobChangeListener);
//			
//		});
//	}
//
//	Finds the child of the project that was modfified
	private void findAffectedChildren(IProject project, IResourceDelta delta) {
		Arrays.stream(delta.getAffectedChildren()).forEach(child -> {
			IResource resource = child.getResource();
			if(resource instanceof IFile) {
				
			} else {
				findAffectedChildren(project, child);
			}
		});
	}
}