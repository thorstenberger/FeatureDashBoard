package se.gu.featuredashboard.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import se.gu.featuredashboard.model.featuremodel.Project;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.ui.listeners.JobChangeListener;

public class ParseProjectAction extends Action {
	
	private IWorkbenchWindow window;
	private ParseProjectJob parseProjectJob = new ParseProjectJob("Parse selected project");
	private Project activeProject;
	private ICallbackListener callback;
	private Shell shell;
	
	private static final String EXPLORER_ID = "org.eclipse.jdt.ui.PackageExplorer";
	private static final String WRONG_SELECTION_MESSAGE = "Current selection is not a project";
	
	public ParseProjectAction(ICallbackListener callback, Shell shell) {
		this.callback = callback;
		this.shell = shell;
		this.parseProjectJob.addJobChangeListener(new JobChangeListener(callback));
	}
	
	@Override
	public void run() {
		
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		try {
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection(EXPLORER_ID);
			
			if(selection != null) {
				Object firstElement = selection.getFirstElement();
				
				if(firstElement instanceof IAdaptable) {
					IProject selectedProject = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
					
					// Have we already parsed this project without making any changes since?
					activeProject = ProjectStore.getProject(selectedProject.getLocation());
					
					if(activeProject == null) {
						activeProject = new Project(selectedProject, selectedProject.getName(), selectedProject.getLocation());
						ProjectStore.addProject(activeProject.getLocation(), activeProject);
						
						parseProjectJob.setProject(activeProject);
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
