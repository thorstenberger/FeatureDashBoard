package se.gu.featuredashboard.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.model.featuremodel.Project;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.ui.listeners.JobChangeListener;
import se.gu.featuredashboard.ui.listeners.ProjectChangeListener;
import se.gu.featuredashboard.ui.providers.FeatureTableContentProvider;
import se.gu.featuredashboard.ui.providers.FeatureTableLabelProvider;
import se.gu.featuredashboard.utils.ICallbackEvent;
import se.gu.featuredashboard.utils.ICallbackListener;
import se.gu.featuredashboard.utils.ParseProjectJob;

public class FeatureListView extends ViewPart implements ICallbackListener {
	
	private CheckboxTableViewer table;
	private IWorkbenchWindow window;
	private ParseProjectJob parseProject;
	private Project activeProject;
	
	private FeatureFileView featureFileView;
	private FeatureFolderView featureFolderView;
	private FeatureMetricsView featureMetricsView;
	private ProjectMetricsView projectMetricsView;
	
	private static final String FEATUREFOLDER_VIEW_ID = "se.gu.featuredashboard.ui.views.FeatureFolderView";
	private static final String FEATUREFILE_VIEW_ID = "se.gu.featuredashboard.ui.views.FeatureFileView";
	private static final String FEATUREMETRICS_VIEW_ID = "se.gu.featuredashboard.ui.views.FeatureMetricsView";
	private static final String PROJECTMETRICS_VIEW_ID = "se.gu.featuredashboard.ui.views.ProjectMetricsView";

	private ProjectChangeListener projectChangeListener;
	
	@Override
	public void createPartControl(Composite parent) {
		
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		parseProject = new ParseProjectJob("Parse selected project");
		parseProject.addJobChangeListener(new JobChangeListener(this));
		
		projectChangeListener = ProjectChangeListener.getInstance(this);
		
		// When opening/closing this view to parse a project this method will be called multiple times.
		// Thus to prevent that we add more than 1 listener, remove if there is one attached and then 
		// attach a new one. It would be best just to check if there was a listener attacked or not
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(projectChangeListener);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(projectChangeListener);
	
		try {
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
			
			if(selection != null) {
				Object firstElement = selection.getFirstElement();
				
				if(firstElement instanceof IAdaptable) {
					IProject selectedProject = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
					
					// Have we already parsed this project without making any changes since?
					activeProject = ProjectStore.getProject(selectedProject.getLocation());
					
					if(activeProject == null) {
						activeProject = new Project(selectedProject, selectedProject.getName(), selectedProject.getLocation());
						ProjectStore.addProject(activeProject.getLocation(), activeProject);
						
						parseProject.setProject(activeProject);
						parseProject.setUser(true);
						parseProject.schedule();
					} else {
						callbackMethod(new ICallbackEvent(ICallbackEvent.EventType.ParsingComplete));
					}
					
				} else {
					MessageDialog.openError(parent.getShell(), "Error", "Current selection is not a project.");
				}
				
			}
			
		} catch(ClassCastException e) {
			MessageDialog.openError(parent.getShell(), "Error", "Current selection is not a project.");
		}
		
		table = CheckboxTableViewer.newCheckList(parent, SWT.NONE);
		table.setContentProvider(new FeatureTableContentProvider());
		table.setLabelProvider(new FeatureTableLabelProvider());
		
		table.addCheckStateListener(new ICheckStateListener() {
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				
				if(table.getCheckedElements().length > 0) {			
					List<FeatureContainer> featureFileList = new ArrayList<>();
					
					Arrays.stream(table.getCheckedElements()).forEach(checkedElement -> {
						FeatureContainer feature = (FeatureContainer) checkedElement;
						featureFileList.add(feature);
					});
					
					try {
						featureFileView = (FeatureFileView) window.getActivePage().showView(FEATUREFILE_VIEW_ID);
						featureFileView.inputToView(featureFileList);
						
						featureFolderView = (FeatureFolderView) window.getActivePage().showView(FEATUREFOLDER_VIEW_ID);
						featureFolderView.inputToView(featureFileList);

					} catch (PartInitException e) {
						e.printStackTrace();
					}
					
				}
				
			}
			
		});
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	@Override
	public void callbackMethod(ICallbackEvent event) {

		if(event.getEventType() == ICallbackEvent.EventType.ParsingComplete) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					table.setInput(activeProject.getFeatureInformation().toArray());
					
					try {
						featureMetricsView = (FeatureMetricsView) window.getActivePage().showView(FEATUREMETRICS_VIEW_ID);
						featureMetricsView.inputToView(activeProject);
						
						projectMetricsView = (ProjectMetricsView) window.getActivePage().showView(PROJECTMETRICS_VIEW_ID);
						projectMetricsView.updateTable();
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}

			});
		} else if(event.getEventType() == ICallbackEvent.EventType.ChangeDetected) {
			// If we have previously parsed the project but there has been a change detected
			// then we want to parse it again next time it is selected
			ProjectStore.removeProject((IPath) event.getData());
		}
		
	}
		
}
