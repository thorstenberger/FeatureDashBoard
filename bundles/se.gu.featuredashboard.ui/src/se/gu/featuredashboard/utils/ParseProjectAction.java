package se.gu.featuredashboard.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import se.gu.featuredashboard.model.featuremodel.Project;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.ui.listeners.JobChangeListener;

public class ParseProjectAction extends Action {
	
	private IWorkbenchWindow window;
	private ParseProjectJob parseProjectJob;
	private Project activeProject;
	private ICallbackListener callback;
	private Shell shell;
	private JobChangeListener jobChangeListener;
	private Logger logger = PlatformUI.getWorkbench().getService(org.eclipse.e4.core.services.log.Logger.class);
	
	private static final String PACKAGE_EXPLORER = "org.eclipse.jdt.ui.PackageExplorer";
	private static final String PROJECT_EXPLORER = "org.eclipse.ui.navigator.ProjectExplorer";
	private static final String WRONG_SELECTION_MESSAGE = "Current selection is not a project";
	
	public ParseProjectAction(ICallbackListener callback, Shell shell) {
		this.callback = callback;
		this.shell = shell;
		jobChangeListener = new JobChangeListener(callback);
	}
	
	@Override
	public void run() {
		
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		try {
			String explorerOfChoice = PROJECT_EXPLORER;
			
			// If project explorer is active/visible, take selection from there otherwise look for package explorer
			IViewPart part = window.getActivePage().findView(PROJECT_EXPLORER);
			
			if(part == null) {
				logger.info("Project explorer is not open, trying to use Package explorer instead");
				explorerOfChoice = PACKAGE_EXPLORER;
			}
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection(explorerOfChoice);
			
			if(selection != null) {
				Object firstElement = selection.getFirstElement();
				
				if(firstElement instanceof IAdaptable) {
					IProject selectedProject = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
					
					// Have we already parsed this project without making any changes since?
					activeProject = ProjectStore.getProject(selectedProject.getLocation());
					
					if(activeProject == null) {
						activeProject = new Project(selectedProject, selectedProject.getName(), selectedProject.getLocation());
						ProjectStore.addProject(activeProject.getLocation(), activeProject);
						
						parseProjectJob = new ParseProjectJob("Parse project", activeProject);
						parseProjectJob.addJobChangeListener(jobChangeListener);
						parseProjectJob.setUser(true);
						parseProjectJob.schedule();
					} else {
						callback.callbackMethod(new ICallbackEvent(ICallbackEvent.EventType.ParsingComplete));
					}	
				}
			} else {
				MessageDialog.openInformation(shell, "Information", WRONG_SELECTION_MESSAGE);
			}
			
		} catch(ClassCastException e) {
			e.printStackTrace();
		}
	}
	
	public Project getActiveProject() {
		return activeProject;
	}
	
}
