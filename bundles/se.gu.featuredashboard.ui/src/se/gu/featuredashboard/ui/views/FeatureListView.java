package se.gu.featuredashboard.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.tools.DocumentationTool.Location;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.featuremodel.FeatureFileContainer;
import se.gu.featuredashboard.model.location.FeatureAnnotationsLocation;
import se.gu.featuredashboard.providers.FeatureTableContentProvider;
import se.gu.featuredashboard.providers.FeatureTableLabelProvider;
import se.gu.featuredashboard.ui.viewscontroller.MainViewController;
import se.gu.featuredashboard.utils.jobs.ParseProjectJob;

public class FeatureListView extends ViewPart {
	
	private CheckboxTableViewer table;
	private ProgressBar progressBar;
	private IWorkbenchWindow window;
	private Label label;
	private IProject project;
	private Map<Feature, List<IFile>> featureFile;
	private ParseProjectJob parseProject;
	
	private FeatureFileView featureFileView;
	private FeatureFolderView featureFolderView;
	
	private static final String FEATUREFOLDER_VIEW_ID = "se.gu.featuredashboard.ui.views.FeatureFolderView";
	private static final String FEATUREFILE_VIEW_ID = "se.gu.featuredashboard.ui.views.FeatureFileView";
	
	@Override
	public void createPartControl(Composite parent) {
		
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		try {
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
			
			if(selection != null) {
				Object firstElement = selection.getFirstElement();
				
				if(firstElement instanceof IAdaptable) {
					project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
				
					parseProject = new ParseProjectJob("ParseProject", project);
					parseProject.setUser(true);
					parseProject.schedule();
					parseProject.addJobChangeListener(new IJobChangeListener() {

						@Override
						public void aboutToRun(IJobChangeEvent event) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void awake(IJobChangeEvent event) {

							
						}

						@Override
						public void done(IJobChangeEvent event) {
							IStatus status = (IStatus) event.getResult();
							
							if(status.getCode() == IStatus.OK) {
								setInputToTable();
							}
							
							if(status.getCode() == IStatus.CANCEL) {
								// Do something?
							}
							
						}

						@Override
						public void running(IJobChangeEvent event) {
							System.out.println("Job is running..");
						}

						@Override
						public void scheduled(IJobChangeEvent event) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void sleeping(IJobChangeEvent event) {
							// TODO Auto-generated method stub
							
						}
						
					});
					
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
					List<FeatureFileContainer> featureFileList = new ArrayList<>();
					
					Arrays.stream(table.getCheckedElements()).forEach(checkedElement -> {
						Feature feature = (Feature) checkedElement;
						featureFileList.add(new FeatureFileContainer(feature, featureFile.get(feature)));
					});
					
					try {
						// Starting these views should probably be handled by a background job
						
//						featureFileView = (FeatureFileView) window.getActivePage().showView(FEATUREFILE_VIEW_ID);
//						featureFileView.setInputToView(featureFileList);
						
						featureFolderView = (FeatureFolderView) window.getActivePage().showView(FEATUREFOLDER_VIEW_ID);
						featureFolderView.setInputToView(featureFileList);

					} catch (PartInitException e) {
						e.printStackTrace();
					}
					
				}
				
			}
			
		});
	}
	
	// Called when the job has finished successfully.
	private void setInputToTable() {		
		featureFile = parseProject.getFeatures();
		
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				table.setInput(featureFile.keySet().toArray());
			}
			
		});
		
		System.out.println("Parsing complete");
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
