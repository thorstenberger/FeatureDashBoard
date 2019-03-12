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
import se.gu.featuredashboard.ui.listeners.JobChangeListener;
import se.gu.featuredashboard.ui.views.FeatureListView;
import se.gu.featuredashboard.ui.views.FeatureMetricsView;
import se.gu.featuredashboard.ui.views.ProjectMetricsView;

public class Builder extends IncrementalProjectBuilder {
	private Logger logger = PlatformUI.getWorkbench().getService(org.eclipse.e4.core.services.log.Logger.class);
	
	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		
		logger.info("Featuredashboard builder was triggered");
		
		IProject iProject = getProject();
		IResourceDelta delta = getDelta(iProject);
		
		if(kind == AUTO_BUILD) {
			startParseJob(iProject, delta);
		}
		
		return null;
	}
	
	private void startParseJob(IProject iProject, IResourceDelta delta) {
		Display.getDefault().asyncExec(() -> {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			
			FeatureListView featureListView = (FeatureListView) page.findView(FeaturedashboardConstants.FEATURELIST_VIEW_ID);
			FeatureMetricsView featureMetricsView = (FeatureMetricsView) page.findView(FeaturedashboardConstants.FEATUREMETRICS_VIEW_ID);
			ProjectMetricsView projectMetricsView = (ProjectMetricsView) page.findView(FeaturedashboardConstants.PROJECTMETRICS_VIEW_ID);
			
			JobChangeListener jobChangeListener = new JobChangeListener(Arrays.asList(featureListView, featureMetricsView, projectMetricsView));
			
			Arrays.stream(delta.getAffectedChildren()).forEach(child -> {
				findAffectedChildren(ProjectStore.getProject(iProject.getLocation()), child, jobChangeListener);
			});
			
		});
	}

	private void findAffectedChildren(Project project, IResourceDelta delta, JobChangeListener jobChangeListener) {
		Arrays.stream(delta.getAffectedChildren()).forEach(child -> {
			IResource resource = child.getResource();
			if(resource instanceof IFile) {
				ParseJob job = new ParseJob("Parse file", project, (IFile) resource);
				job.addJobChangeListener(jobChangeListener);
				job.setUser(true);
				job.schedule();
			} else {
				findAffectedChildren(project, child, jobChangeListener);
			}
		});
	}
}