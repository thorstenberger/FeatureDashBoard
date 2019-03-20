package se.gu.featuredashboard.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import se.gu.featuredashboard.utils.IUpdateViewListener;
import se.gu.featuredashboard.utils.ParseProjectAction;

public class FeatureListView extends ViewPart implements IUpdateViewListener {
	
	private CheckboxTableViewer table;
	private IWorkbenchWindow window;
	
	private FeatureFileView featureFileView;
	private FeatureFolderView featureFolderView;
	
	private ParseProjectAction parseProject;
	
	@Override
	public void createPartControl(Composite parent) {
		
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		table = CheckboxTableViewer.newCheckList(parent, SWT.NONE);
		table.setContentProvider(ArrayContentProvider.getInstance());
		table.setLabelProvider(new FeatureTableLabelProvider());
		
		table.addCheckStateListener(new ICheckStateListener() {
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				
				Display.getDefault().asyncExec(() -> {
					
					if(table.getCheckedElements().length > 0) {			
						List<FeatureContainer> featureFileList = new ArrayList<>();
						
						Arrays.stream(table.getCheckedElements()).forEach(checkedElement -> {
							FeatureContainer feature = (FeatureContainer) checkedElement;
							featureFileList.add(feature);
						});
						
						try {
//							featureFileView = (FeatureFileView) window.getActivePage().showView(FeaturedashboardConstants.FEATUREFILE_VIEW_ID);
//							featureFileView.inputToView(featureFileList);
							
							featureFolderView = (FeatureFolderView) window.getActivePage().showView(FeaturedashboardConstants.FEATUREFOLDER_VIEW_ID);
							featureFolderView.inputToView(featureFileList);

						} catch (PartInitException e) {
							e.printStackTrace();
						}
						
					}
					
				});
							
			}
			
		});
		
		parseProject = new ParseProjectAction(parent.getShell());
		parseProject.setText(FeaturedashboardConstants.ACTION_TEXT);
		parseProject.setToolTipText(FeaturedashboardConstants.ACTION_TOOLTOP_TEXT);
		parseProject.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
		
		getViewSite().getActionBars().getToolBarManager().add(parseProject);
		getViewSite().getActionBars().getMenuManager().add(parseProject);
		
		updateView();
	}
	
	@Override
	public void setFocus() {
	}

	@Override
	public void updateView() {
		Display.getDefault().asyncExec(() -> {
			Project activeProject = ProjectStore.getActiveProject();
			if(activeProject != null)
				table.setInput(activeProject.getFeatureContainers().toArray());
		});
	}	
}
