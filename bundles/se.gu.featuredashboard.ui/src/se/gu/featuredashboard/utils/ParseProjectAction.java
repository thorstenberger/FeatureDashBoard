package se.gu.featuredashboard.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import se.gu.featuredashboard.model.featuremodel.Project;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.ui.listeners.JobChangeListener;
import se.gu.featuredashboard.ui.views.FeatureListView;
import se.gu.featuredashboard.ui.views.FeatureMetricsView;
import se.gu.featuredashboard.ui.views.ProjectMetricsView;

public class ParseProjectAction extends Action {
	
	private ParseJob parseProjectJob;
	private Logger logger = PlatformUI.getWorkbench().getService(org.eclipse.e4.core.services.log.Logger.class);
	
	@Override
	public void run() {
		
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		
		FeatureListView featureListView = (FeatureListView) page.findView(FeaturedashboardConstants.FEATURELIST_VIEW_ID);
		FeatureMetricsView featureMetricsView = (FeatureMetricsView) page.findView(FeaturedashboardConstants.FEATUREMETRICS_VIEW_ID);
		ProjectMetricsView projectMetricsView = (ProjectMetricsView) page.findView(FeaturedashboardConstants.PROJECTMETRICS_VIEW_ID);
		
		List<IUpdateViewListener> viewsToUpdate = Arrays.asList(featureListView, featureMetricsView, projectMetricsView);
		
		try {
			String explorerOfChoice = FeaturedashboardConstants.PROJECT_EXPLORER;
			
			// If project explorer is active/visible, take selection from there otherwise look for package explorer
			IViewPart projectExplorer = page.findView(explorerOfChoice);
			if(projectExplorer == null) {
				logger.info("Project explorer is not open, trying to use Package explorer instead");
				explorerOfChoice = FeaturedashboardConstants.PACKAGE_EXPLORER;
			}
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection(explorerOfChoice);
			
			if(selection != null) {
				Object firstElement = selection.getFirstElement();
				
				if(firstElement instanceof IAdaptable) {
					IProject selectedProject = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
					
					if(!hasBuilder(selectedProject)) {
						logger.info("This project doesn't have the builder attached to it. Attaching");
						addBuilder(selectedProject);
					}
					
					if(!ProjectStore.isProjectParsed(selectedProject.getLocation())) {
						logger.info("This project hasn't beeen parsed yet");
						Project project = new Project(selectedProject, selectedProject.getName(), selectedProject.getLocation());
						
						ProjectStore.addProject(project.getLocation(), project);
						
						parseProjectJob = new ParseJob("Parse project", project);
						parseProjectJob.addJobChangeListener(new JobChangeListener(viewsToUpdate));
						parseProjectJob.setUser(true);
						parseProjectJob.schedule();	 
					} else {
						logger.info("This project has already been parsed. Updating views");
						ProjectStore.setActiveProject(selectedProject.getLocation());
						viewsToUpdate.forEach(view -> {
							if(view != null)
								view.updateView();
						});
					}
				}
			}
		} catch(ClassCastException | CoreException e) {
			logger.error(e.getMessage());
		}
	}
	
	private void addBuilder(IProject project) throws CoreException {
		IProjectDescription projectDescription = project.getDescription();
		ICommand[] buildSpec = projectDescription.getBuildSpec();
		ICommand command = projectDescription.newCommand();
		command.setBuilderName(FeaturedashboardConstants.BUILDER_ID);
		Collection<ICommand> list = new ArrayList<>(Arrays.asList(buildSpec));
		list.add(command);
		projectDescription.setBuildSpec(list.toArray(new ICommand[list.size()]));
		project.setDescription(projectDescription, new NullProgressMonitor());
	}
	
	private boolean hasBuilder(IProject project) throws CoreException {
		for (final ICommand buildSpec : project.getDescription().getBuildSpec()) {
			if (FeaturedashboardConstants.BUILDER_ID.equals(buildSpec.getBuilderName()))
				return true;
		}
		return false;
	}
}