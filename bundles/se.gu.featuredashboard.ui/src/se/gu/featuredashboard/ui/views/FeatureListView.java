package se.gu.featuredashboard.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.ui.listeners.ProjectChangeListener;
import se.gu.featuredashboard.ui.providers.FeatureTableContentProvider;
import se.gu.featuredashboard.ui.providers.FeatureTableLabelProvider;
import se.gu.featuredashboard.utils.ICallbackEvent;
import se.gu.featuredashboard.utils.ICallbackListener;
import se.gu.featuredashboard.utils.ParseProjectAction;

public class FeatureListView extends ViewPart implements ICallbackListener {
	
	private CheckboxTableViewer table;
	private IWorkbenchWindow window;
	
	private FeatureFileView featureFileView;
	private FeatureFolderView featureFolderView;
	private FeatureMetricsView featureMetricsView;
	private ProjectMetricsView projectMetricsView;
	
	private ParseProjectAction parseProject;
	private static final String ACTION_TEXT = "Parse selected project";
	private static final String ACTION_TOOLTOP_TEXT = "Parses the selected project in the Package Exlorer";
	
	private static final String FEATUREFOLDER_VIEW_ID = "se.gu.featuredashboard.ui.views.FeatureFolderView";
	private static final String FEATUREFILE_VIEW_ID = "se.gu.featuredashboard.ui.views.FeatureFileView";
	private static final String FEATUREMETRICS_VIEW_ID = "se.gu.featuredashboard.ui.views.FeatureMetricsView";
	private static final String PROJECTMETRICS_VIEW_ID = "se.gu.featuredashboard.ui.views.ProjectMetricsView";

	private ProjectChangeListener projectChangeListener;
	
	@Override
	public void createPartControl(Composite parent) {
		
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		projectChangeListener = ProjectChangeListener.getInstance(this);
		
		// When opening/closing this view to parse a project this method will be called multiple times.
		// Thus to prevent that we add more than 1 listener, remove if there is one attached and then 
		// attach a new one. Can't check if one is already attached which is unfortunate
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(projectChangeListener);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(projectChangeListener);
		
		parseProject = new ParseProjectAction(this, parent.getShell());
		
		table = CheckboxTableViewer.newCheckList(parent, SWT.NONE);
		table.setContentProvider(new FeatureTableContentProvider());
		table.setLabelProvider(new FeatureTableLabelProvider());
		
		parseProject.run();
		
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
		
		parseProject.setText(ACTION_TEXT);
		parseProject.setToolTipText(ACTION_TOOLTOP_TEXT);
		parseProject.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
		
		getViewSite().getActionBars().getToolBarManager().add(parseProject);
		getViewSite().getActionBars().getMenuManager().add(parseProject);
	}
	
	@Override
	public void setFocus() {
	}

	@Override
	public void callbackMethod(ICallbackEvent event) {

		if(event.getEventType() == ICallbackEvent.EventType.ParsingComplete) {
			Display.getDefault().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					table.setInput(parseProject.getActiveProject().getFeatureInformation().toArray());
					
					try {
						featureMetricsView = (FeatureMetricsView) window.getActivePage().showView(FEATUREMETRICS_VIEW_ID);
						featureMetricsView.inputToView(parseProject.getActiveProject());
						
						projectMetricsView = (ProjectMetricsView) window.getActivePage().showView(PROJECTMETRICS_VIEW_ID);
						projectMetricsView.updateTable();
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}

			});
		} else if(event.getEventType() == ICallbackEvent.EventType.ChangeDetected) {
			ProjectStore.removeProject((IPath) event.getData());
		}
		
	}
		
}
