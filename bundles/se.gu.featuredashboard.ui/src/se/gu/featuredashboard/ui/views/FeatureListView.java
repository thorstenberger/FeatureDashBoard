package se.gu.featuredashboard.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ArrayContentProvider;
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
import se.gu.featuredashboard.model.featuremodel.Project;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.ui.providers.FeatureTableLabelProvider;
import se.gu.featuredashboard.utils.FeaturedashboardConstants;
import se.gu.featuredashboard.utils.IUpdateInformationListener;
import se.gu.featuredashboard.utils.MetricsComparator;
import se.gu.featuredashboard.utils.ParseProjectAction;

public class FeatureListView extends ViewPart implements IUpdateInformationListener {
	
	private CheckboxTableViewer tableViewer;
	private IWorkbenchWindow window;
	
	private ParseProjectAction parseProject;
	private Action sortTableAction;
	
	@Override
	public void createPartControl(Composite parent) {
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		tableViewer = CheckboxTableViewer.newCheckList(parent, SWT.NONE);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setLabelProvider(new FeatureTableLabelProvider());
		
		MetricsComparator comparator = new MetricsComparator();
		tableViewer.setComparator(comparator);
		
		tableViewer.addCheckStateListener(new ICheckStateListener() {
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				
				Display.getDefault().asyncExec(() -> {
					
					List<FeatureContainer> featureFileList = new ArrayList<>();
					
					if(tableViewer.getCheckedElements().length > 0) {				
						Arrays.stream(tableViewer.getCheckedElements()).forEach(checkedElement -> {
							FeatureContainer feature = (FeatureContainer) checkedElement;
							featureFileList.add(feature);
						});
					}			
					FeatureFileView featureFileView = (FeatureFileView) window.getActivePage().findView(FeaturedashboardConstants.FEATUREFILE_VIEW_ID);
					if(featureFileView != null)
						featureFileView.inputToView(featureFileList);
					
					FeatureFolderView featureFolderView = (FeatureFolderView) window.getActivePage().findView(FeaturedashboardConstants.FEATUREFOLDER_VIEW_ID);
					if(featureFolderView != null)
						featureFolderView.inputToView(featureFileList);
					
					HistoryView historyView = (HistoryView) window.getActivePage().findView(FeaturedashboardConstants.HISTORY_VIEW_ID);
					if(historyView != null)
						historyView.inputToView(featureFileList);
				});
							
			}
			
		});
		
		parseProject = new ParseProjectAction(parent.getShell());
		parseProject.setText(FeaturedashboardConstants.ACTION_TEXT);
		parseProject.setToolTipText(FeaturedashboardConstants.ACTION_TOOLTOP_TEXT);
		parseProject.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
		addActionToView(parseProject);
		
		sortTableAction = new Action() {
			public void run() {
				comparator.setColumn(FeaturedashboardConstants.FEATURELISTTABLE_SORT_COLUMN);
				tableViewer.refresh();
			}
		};
		sortTableAction.setText("Sort table in alphabetical order");
		sortTableAction.setToolTipText("Sort table");
		sortTableAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_UP));
		addActionToView(sortTableAction);
		
		updateData();
	}
	
	private void addActionToView(Action action) {
		getViewSite().getActionBars().getToolBarManager().add(action);
		getViewSite().getActionBars().getMenuManager().add(action);
	}
	
	@Override
	public void setFocus() {
	}

	@Override
	public void updateData() {
		Display.getDefault().asyncExec(() -> {
			Project activeProject = ProjectStore.getActiveProject();
			if(activeProject != null)
				tableViewer.setInput(activeProject.getFeatureContainers().toArray());
		});
	}	
}
