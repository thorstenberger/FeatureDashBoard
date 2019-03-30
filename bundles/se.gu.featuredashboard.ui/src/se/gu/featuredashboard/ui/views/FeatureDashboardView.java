package se.gu.featuredashboard.ui.views;

import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.location.FeatureLocation;
import se.gu.featuredashboard.ui.viewscontroller.FeatureDashboardViewController;

public class FeatureDashboardView extends ViewPart {

	private FeatureDashboardViewController controller;
		
	CheckboxTreeViewer tvFeatureModel;
	CheckboxTreeViewer tvResources;
	Table tblTraces;
	
	CTabFolder ctfMain;
	CTabItem ctiFeatureModel;
	CTabItem ctiResources;
	CTabItem ctiTraces;
	
	Label lblFeatureModelTabInfo;
	Label lblResourcesTabInfo;
	Label lblTracesTabInfo;

	private java.util.List<Feature> featuresNotInFeatureModel = new ArrayList<Feature>();
	private java.util.List<IResource> notExistentResources = new ArrayList<IResource>();

	@Override
 	public void setFocus() {
	}
	
	public IProject getCurrentProject() {
		ISelectionService selectionService = getSite().getWorkbenchWindow().getSelectionService();
		IStructuredSelection selection = (IStructuredSelection) selectionService
				.getSelection("org.eclipse.ui.navigator.ProjectExplorer");

		IProject project = null;
		if (selection instanceof IStructuredSelection) {
			Object element = selection.getFirstElement();

			if (element instanceof IResource) {
				project = ((IResource) element).getProject();
			} else if (element instanceof IAdaptable) {
				project = ((IAdaptable) element).getAdapter(IResource.class).getProject();
			}
		}
		return project;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		controller = new FeatureDashboardViewController(null);

		ctfMain = new CTabFolder(parent, SWT.BORDER |SWT.BOTTOM);
	    ctfMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	    ctfMain.setSimple(false);
	    ctfMain.setUnselectedImageVisible(false);
	    ctfMain.setUnselectedCloseVisible(false); 

	    ctiFeatureModel = new CTabItem(ctfMain, SWT.CLOSE);
	    ctiFeatureModel.setText("Feature Model");
	    ctiFeatureModel.setShowClose(false);

	    ctiResources = new CTabItem(ctfMain, SWT.CLOSE);
	    ctiResources.setText("Resources");
	    ctiResources.setShowClose(false);

	    ctiTraces= new CTabItem(ctfMain, SWT.CLOSE);
	    ctiTraces.setText("Traces");
	    ctiTraces.setShowClose(false);
    
	    setFeatureModelTab();
	    setResourcesTab();
	    setTracesTab();
	    
	    ctfMain.addSelectionListener(new SelectionAdapter() {		
	        public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {    
	        	switch (ctfMain.getSelection().getText()) {
				case "Traces": 
					updateTracesTab();
					break;
				default:
					//doing nothing
					break;				
				}
	        }
	      });
	    
	    setupToolBar();
	}
	
	private void setupToolBar() {
	    IActionBars bars = getViewSite().getActionBars();

	    Action selectAllFeatures = new Action() {
	    	@Override
			public void run() {
	    		FeatureModelContentProvider contentProvider = (FeatureModelContentProvider) tvFeatureModel.getContentProvider();
	    		Object[] rootElements = contentProvider.getElements(null);
	    		Arrays.asList(rootElements).forEach(element->{
		    		tvFeatureModel.setSubtreeChecked(element, true);
	    		});
	    		if(ctfMain.getSelection().getText().equals("Traces"))
	    			updateTracesTab();
	    	}
		};
		selectAllFeatures.setImageDescriptor(createImageDescriptor("icons/selectAllFeatures_icon.png"));
		selectAllFeatures.setToolTipText("select all features");
		bars.getToolBarManager().add(selectAllFeatures);
		
		
		
	    Action deselectAllFeatures = new Action() {
	    	@Override
			public void run() {
	    		FeatureModelContentProvider contentProvider = (FeatureModelContentProvider) tvFeatureModel.getContentProvider();
	    		Object[] rootElements = contentProvider.getElements(null);
	    		Arrays.asList(rootElements).forEach(element->{
		    		tvFeatureModel.setSubtreeChecked(element, false);
	    		});
	    		if(ctfMain.getSelection().getText().equals("Traces"))
	    			updateTracesTab();
	    	}
		};
		deselectAllFeatures.setImageDescriptor(createImageDescriptor("icons/deselectAllFeatures_icon.png"));
		deselectAllFeatures.setToolTipText("deselect all features");
		bars.getToolBarManager().add(deselectAllFeatures);
		
	   
		Action selectAllResources = new Action() {
	    	@Override
			public void run() {
	    		ResourcesContentProvider contentProvider = (ResourcesContentProvider) tvResources.getContentProvider();
	    		Object[] rootElements = contentProvider.getElements(null);
	    		Arrays.asList(rootElements).forEach(element->{
		    		tvResources.setSubtreeChecked(element, true);
	    		});
	    		if(ctfMain.getSelection().getText().equals("Traces"))
	    			updateTracesTab();
	    	}
		};
		selectAllResources.setImageDescriptor(createImageDescriptor("icons/selectAllResources_icon.png"));
		selectAllResources.setToolTipText("select all resources");
		bars.getToolBarManager().add(selectAllResources);
		
	    Action deselectAllResources = new Action() {
	    	@Override
			public void run() {
	    		ResourcesContentProvider contentProvider = (ResourcesContentProvider) tvResources.getContentProvider();
	    		Object[] rootElements = contentProvider.getElements(null);
	    		Arrays.asList(rootElements).forEach(element->{
		    		tvResources.setSubtreeChecked(element, false);
	    		});
	    		if(ctfMain.getSelection().getText().equals("Traces"))
	    			updateTracesTab();
	    	}
		};
		deselectAllResources.setImageDescriptor(createImageDescriptor("icons/deselectAllResources_icon.png"));
		deselectAllResources.setToolTipText("deselect all resources");
		bars.getToolBarManager().add(deselectAllResources);
		
	    
	    Action refreshProject = new Action() {
	    	@Override
			public void run() {
    			controller.updateController(getCurrentProject());
    			updateFeatureModelTab();
    			updateResourcesTab();
    			updateTracesTab();
    			if(!controller.getParsingMessage().isEmpty())
    				showMessage(controller.getParsingMessage());
	    	}
		};
		refreshProject.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
		refreshProject.setToolTipText("refresh the view for the selected project");
		bars.getToolBarManager().add(refreshProject);
	    
		
	}
	
	private void setFeatureModelTab() {
		CTabItem parent = ctiFeatureModel;
		Group grpFeatureModel = new Group(parent.getParent(), SWT.NONE);
		parent.setControl(grpFeatureModel);
		
		GridData gridData = new GridData(GridData.FILL, GridData.FILL,true, true);
		grpFeatureModel.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout(1, false);
		grpFeatureModel.setLayout(gridLayout);
				
		tvFeatureModel = new CheckboxTreeViewer(grpFeatureModel, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		tvFeatureModel.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		TreeViewerColumn mainColumn = new TreeViewerColumn(tvFeatureModel, SWT.NONE);
		mainColumn.getColumn().setText("Name");
		mainColumn.getColumn().setWidth(300);
		mainColumn.setLabelProvider(
					new DelegatingStyledCellLabelProvider(
						new FeatureModelLabelProvider(
							createImageDescriptor("icons/feature_icon.png"),
							createImageDescriptor("icons/featurequestion_icon.png")
							)));

		// When user checks a checkbox in the tree, check all its children
		tvFeatureModel.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()) {
					tvFeatureModel.setSubtreeChecked(event.getElement(), true);
				}
			}
		});
	
		lblFeatureModelTabInfo = new Label(grpFeatureModel, SWT.LEFT);
		gridData = new GridData();
		gridData.widthHint= 1000;
		lblFeatureModelTabInfo.setLayoutData(gridData);
		lblFeatureModelTabInfo.setText("No project is selected.");
		updateFeatureModelTab();

	}
	
	private void updateFeatureModelTab() {	
		final String message1 = "No project is selected.";
		final String message2 = "No feature model (.cfr) exists in the project.";
		final String message3 = "Syntax error in defining the feature model.";
		
		//the default values will be ignored if there is a feature model file
		ArrayList<Feature> emptyFeatureList = new ArrayList<Feature>();
		featuresNotInFeatureModel = emptyFeatureList;
		tvFeatureModel.setContentProvider(new FeatureModelContentProvider( 
				emptyFeatureList,
				emptyFeatureList,
				emptyFeatureList
		));	
		
		if(controller.getProject()==null) {
			lblFeatureModelTabInfo.setText(message1);
			lblFeatureModelTabInfo.setVisible(true);
		}
		else if(!controller.hasFeatureModel()) {
			lblFeatureModelTabInfo.setText(message2);
			lblFeatureModelTabInfo.setVisible(true);
			
			featuresNotInFeatureModel = controller.getFeaturesNotInFeatureModel();
			tvFeatureModel.setContentProvider(new FeatureModelContentProvider( 
					emptyFeatureList,
					emptyFeatureList,
					featuresNotInFeatureModel
			));
		}	
		else {		
			lblFeatureModelTabInfo.setVisible(false);
			featuresNotInFeatureModel = controller.getFeaturesNotInFeatureModel();
			tvFeatureModel.setContentProvider(new FeatureModelContentProvider( 
					controller.getAllFeaturesOfFeatureModel(),
					controller.getRootFeaturesOfFeatureModel(),
					featuresNotInFeatureModel
			));
		}
		tvFeatureModel.setInput("root"); // passing a non-null value that will be ignored later
	}

	private void setResourcesTab() {
		CTabItem parent= ctiResources;
		
		Group grpResources = new Group(parent.getParent(), SWT.NONE);
		parent.setControl(grpResources);
		
		GridData gridData = new GridData(GridData.FILL, GridData.FILL,true, true);
		grpResources.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout(1, false);
		grpResources.setLayout(gridLayout);
		
		tvResources = new CheckboxTreeViewer(grpResources, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        tvResources.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		TreeViewerColumn mainColumn = new TreeViewerColumn(tvResources, SWT.NONE);
		mainColumn.getColumn().setText("Name");
		mainColumn.getColumn().setWidth(300);
        mainColumn.setLabelProvider(
                new DelegatingStyledCellLabelProvider(
                        new ResourceLabelProvider(
                        		createImageDescriptor("icons/folder_icon.png"),
                        		createImageDescriptor("icons/file_icon.png"),
    							createImageDescriptor("icons/folderquestion_icon.png")
                        		)));
		
		tvResources.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()) {
					tvResources.setSubtreeChecked(event.getElement(), true);
				}
			}
		});

		lblResourcesTabInfo = new Label(grpResources, SWT.NONE);
		gridData = new GridData();
		gridData.widthHint= 1000;
		lblResourcesTabInfo.setLayoutData(gridData);
		lblResourcesTabInfo.setText("No project is selected.");
		
		updateResourcesTab();

	}
	
	private void updateResourcesTab() {
		final String message1 = "No project is selected.";
		final String message2 = "No feature folder traces(.feature-folder) exists in the project.";
		final String message3 = "No feature file traces(.feature-file) exists in the project.";
		final String message4 = "No feature file(.feature-file) and feature folder(.feature-folder) traces exist in the project.";
		final String message5 = "Syntax error in defining feature folder traces.";
		final String message6 = "Syntax error in defining feature file traces.";
		final String message7 = "Syntax error in defining feature file and feature folder traces.";
		
		ArrayList<IResource> emptyResources = new ArrayList<IResource>();
		notExistentResources = emptyResources;
		
		if(controller.getProject()==null) {
			lblResourcesTabInfo.setText(message1);	
			lblResourcesTabInfo.setVisible(true);
			tvResources.setContentProvider(new ResourcesContentProvider(null));
		}
		else {
			tvResources.setContentProvider(new ResourcesContentProvider(getCurrentProject()));
			
			if(!controller.hasFeatureFolderTraces()) {
				if(controller.hasFeatureFileTraces()) {
					lblResourcesTabInfo.setText(message2);	
					lblResourcesTabInfo.setVisible(true);
				}
				else {
					lblResourcesTabInfo.setText(message4);	
					lblResourcesTabInfo.setVisible(true);
				}
			}
			else {
				if(controller.hasFeatureFileTraces()) {
					lblResourcesTabInfo.setVisible(false);			
					notExistentResources = controller.getNotExistentResources();
				}
				else {
					lblResourcesTabInfo.setText(message3);	
					lblResourcesTabInfo.setVisible(true);
				}
			}
		}	
		
		tvResources.setInput("root"); // passing a non-null value that will be ignored later
	}

	private void setTracesTab() {
		CTabItem parent = ctiTraces;

		Group grpTraces = new Group(parent.getParent(), SWT.NONE);
		parent.setControl(grpTraces);
		
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		grpTraces.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout(1, false);
		grpTraces.setLayout(gridLayout);
	
		tblTraces = new Table(grpTraces, SWT.BORDER);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		tblTraces.setLayoutData(gridData);
		tblTraces.setLinesVisible(true);
		tblTraces.setHeaderVisible(true);
		tblTraces.setSortDirection(SWT.UP);

		Listener sortListener = new Listener() {
			public void handleEvent(Event e) {
				TableItem[] items = tblTraces.getItems();
				Collator collator = Collator.getInstance(Locale.getDefault());
				TableColumn column = (TableColumn) e.widget;
				int index = 0;
				switch (column.getText()) {
				case "Feature":
					index = 0;
					break;
				case "Address":
					index = 1;
					break;
				case "Line Number(s)":
					index = 2;
				}
				if(tblTraces.getSortDirection() == SWT.UP) {
					for (int i = 1; i < items.length; i++) {
						String value1 = items[i].getText(index);
						for (int j = 0; j < i; j++) {
							String value2 = items[j].getText(index);
							if (collator.compare(value1, value2) < 0) {
								String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2)};
								items[i].dispose();
								TableItem item = new TableItem(tblTraces, SWT.NONE, j);
								item.setText(values);
								items = tblTraces.getItems();
								break;
							}
						}
					}
					tblTraces.setSortDirection(SWT.DOWN);
				}
				else {
					for (int i = 1; i < items.length; i++) {
						String value1 = items[i].getText(index);
						for (int j = 0; j < i; j++) {
							String value2 = items[j].getText(index);
							if (collator.compare(value1, value2) > 0) {
								String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2)};
								items[i].dispose();
								TableItem item = new TableItem(tblTraces, SWT.NONE, j);
								item.setText(values);
								items = tblTraces.getItems();
								break;
							}
						}
					}
					tblTraces.setSortDirection(SWT.UP);
				}

				tblTraces.setSortColumn(column);

			}
		};

		TableColumn column;
		String[] titles = { "Feature", "Address", "Line Number(s)" };
		for (int i = 0; i < titles.length; i++) {
			column = new TableColumn(tblTraces, SWT.NONE);
			column.setText(titles[i]);
			column.addListener(SWT.Selection, sortListener);

			if (i == 1) {
				tblTraces.setSortColumn(column);
			}
		}
		tblTraces.addMouseListener(new  MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				
				if(tblTraces.getSelectionCount()==1) {
					String resourceName = tblTraces.getItem(tblTraces.getSelectionIndex()).getText(1);
					IFile resource = controller.getProject().getFile(resourceName);
					if(!resource.exists())
						return;	
					
					try {
						IEditorPart editorPart = IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(),resource);
						if (editorPart instanceof ITextEditor) {
							ITextEditor editor = (ITextEditor) editorPart;
							IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
							if (document != null) {
								IRegion lineInfo = null;
								try {
									String blockLine = tblTraces.getItem(tblTraces.getSelectionIndex()).getText(2);
									if(blockLine.isEmpty())
										return;
									
									int startLine, endLine;
									
									if(blockLine.contains("(")){ // it contains ')' too
										blockLine = blockLine.replace("(", "");
										blockLine = blockLine.replace(")", "");
										
										startLine = Integer.parseInt(blockLine.split(",")[0])-1;
										endLine = Integer.parseInt(blockLine.split(",")[1])-1;		
									}
									else {
										startLine = Integer.parseInt(blockLine)-1;
										endLine = startLine;
									}
									
									lineInfo = document.getLineInformation(startLine);
									int offset = lineInfo.getOffset();
									int length = 0;
									
									while(startLine<=endLine) {
										lineInfo = document.getLineInformation(startLine);
										if (lineInfo == null)
											break;
										length += lineInfo.getLength()+1;	
										startLine++;
									}
									editor.selectAndReveal(offset, length);

								} catch (BadLocationException e1) {
									// ignored because line number may
									// not really exist in document
								}						
							}
						}
					} catch (PartInitException e2) {
						System.out.println("Error initialising editor: " + e2.getMessage());
						e2.printStackTrace();
					}
				}
				
			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		lblTracesTabInfo = new Label(grpTraces, SWT.NONE);
		gridData = new GridData();
		gridData.widthHint= 1000;
		lblTracesTabInfo.setLayoutData(gridData);
		lblTracesTabInfo.setText("No project is selected.");
		
	}
	
	private void updateTracesTab() {
		tblTraces.removeAll();
		tblTraces.setTopIndex(0);
		
		final String message1 = "No project is selected.";
		final String message2 = "No Features and Resources are selected.";
		final String message3 = "Finding the traces..";

		java.util.List<Object> selectedFeatures = Arrays.asList(tvFeatureModel.getCheckedElements());
		java.util.List<Object> selectedResources = Arrays.asList(tvResources.getCheckedElements());

		java.util.List<FeatureLocation> traces = controller.getTraces(selectedFeatures, selectedResources);

		if (controller.getProject() == null) {
			lblTracesTabInfo.setText(message1);
			lblTracesTabInfo.setVisible(true);
			return;
		} else if (selectedFeatures.isEmpty() && selectedResources.isEmpty()) {
			lblTracesTabInfo.setText(message2);
			lblTracesTabInfo.setVisible(true);
			return;
		}
		lblTracesTabInfo.setText(message3);
		lblTracesTabInfo.setVisible(true);

		TableItem item;

		for (int i = 0; i < traces.size(); i++) {
			if (traces.get(i).getBlocklines().size() > 0) {
				for (int j = 0; j < traces.get(i).getBlocklines().size(); j++) {
					item = new TableItem(tblTraces, SWT.NONE);
					item.setText(0, traces.get(i).getFeature().getFeatureID());
					item.setText(1, traces.get(i).getResource().getProjectRelativePath().toString());

					if (traces.get(i).getBlocklines().get(j).isSignleLine())
						item.setText(2, Integer.toString(traces.get(i).getBlocklines().get(j).getStartLine()));
					else
						item.setText(2, traces.get(i).getBlocklines().get(j).toString());
				}
			} else {
				item = new TableItem(tblTraces, SWT.NONE);
				item.setText(0, traces.get(i).getFeature().getFeatureID());
				item.setText(1, traces.get(i).getResource().getProjectRelativePath().toString());
			}
		}

		for (int i = 0; i < 3; i++) {
			tblTraces.getColumn(i).pack();
		}

		lblTracesTabInfo.setVisible(false);
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(ctfMain.getShell(), "Feature Locations View", message);
	}
	
	private ImageDescriptor createImageDescriptor(String path) {
		Bundle bundle = FrameworkUtil.getBundle(FeatureModelLabelProvider.class);
		URL url = FileLocator.find(bundle, new Path(path), null);
		return ImageDescriptor.createFromURL(url);
	}

	public class FeatureModelContentProvider implements ITreeContentProvider {

		private final java.util.List<Feature> featureInFeatureModel;
		private final java.util.List<Feature> rootFeatuesOfFeatureModel;
		
		public FeatureModelContentProvider(java.util.List<Feature> featureInFeatureModel, 
										   java.util.List<Feature> rootFeatuesOfFeatureModel,
										   java.util.List<Feature> featuresNotInFeatureModel){
			this.featureInFeatureModel = featureInFeatureModel;
			this.rootFeatuesOfFeatureModel = rootFeatuesOfFeatureModel;
		}
		
		@Override
		public Object[] getElements(Object inputElement) {
			java.util.List<Feature> allRoots = new ArrayList<Feature>(rootFeatuesOfFeatureModel);
			allRoots.addAll(featuresNotInFeatureModel);
			return allRoots.toArray(new Feature[allRoots.size()]);
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return ((Feature) parentElement).getSubFeatures()
					.toArray(new Feature[((Feature) parentElement).getSubFeatures().size()]);
		}

		@Override
		public Object getParent(Object element) {
			for (Feature feature : featureInFeatureModel) {
				if (feature.getSubFeatures().contains(element))
					return feature;
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (((Feature) element).getSubFeatures().size() > 0)
				return true;
			return false;
		}

	}

	public class FeatureModelLabelProvider extends LabelProvider implements IStyledLabelProvider, IColorProvider {

		private ImageDescriptor featureImage;
		private ImageDescriptor invalidFeatureImage;
		
		private ResourceManager resourceManager;

		public FeatureModelLabelProvider(ImageDescriptor validFeatureImage, ImageDescriptor invalidFeatureImage) {
			this.featureImage = validFeatureImage;
			this.invalidFeatureImage = invalidFeatureImage;
		}

		@Override
		public void addListener(ILabelProviderListener arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isLabelProperty(Object arg0, String arg1) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public Image getImage(Object arg0) {
			if(featuresNotInFeatureModel.contains(arg0))
				return getResourceManager().createImage(invalidFeatureImage);
			return getResourceManager().createImage(featureImage);
		}

		@Override
		public void dispose() {
			// garbage collect system resources
			if (resourceManager != null) {
				resourceManager.dispose();
				resourceManager = null;
			}
		}

		protected ResourceManager getResourceManager() {
			if (resourceManager == null) {
				resourceManager = new LocalResourceManager(JFaceResources.getResources());
			}
			return resourceManager;
		}

		@Override
		public StyledString getStyledText(Object element) {
			return new StyledString(((Feature) element).getFeatureID());
		}

		@Override
		public Color getForeground(Object element) {
			if(featuresNotInFeatureModel.contains(element))
				return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
			return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
		}
		
		@Override
		public Color getBackground(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

	}
	
	public class ResourcesContentProvider implements ITreeContentProvider{

		private IProject project;
		
		public ResourcesContentProvider(IProject project){
			this.project = project;
		}
	
		@Override
		public Object[] getElements(Object inputElement) {
			java.util.List<IResource> allRoots = new ArrayList<>();
			if(project!=null)
				allRoots.add(project);
			allRoots.addAll(notExistentResources);
			return allRoots.toArray(new IResource[allRoots.size()]);
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if(!((IResource)parentElement).exists() || parentElement instanceof IFile) {
				return new Object[0];
			}	
			try {
				return ((IContainer)parentElement).members();
			} catch (CoreException e) {}
			
			return null;
		}

		@Override
		public Object getParent(Object element) {
			return ((IResource)element).getParent();
		}

		@Override
		public boolean hasChildren(Object element) {
			try {
				if(!((IResource)element).exists())
					return false;
				if(element instanceof IProject)
					return ((IProject)element).members().length>0;
				if(element instanceof IFolder)
					return ((IFolder)element).members().length>0;
				if(element instanceof IFile)
					return false;
			} catch (Exception e) {}

			return false;
		}
	}
		
	public class ResourceLabelProvider extends LabelProvider implements IStyledLabelProvider, IColorProvider{
		
		private ImageDescriptor directoryImage;
		private ImageDescriptor fileImage;
		private ImageDescriptor invalidResourcesImage;
		
		private ResourceManager resourceManager;
		
		public ResourceLabelProvider(ImageDescriptor directoryImage, ImageDescriptor fileImage, ImageDescriptor invalidResourceImage ) {
			this.directoryImage = directoryImage;
			this.fileImage = fileImage;
			this.invalidResourcesImage = invalidResourceImage;
		}
		
		@Override
		public StyledString getStyledText(Object element) {
			if(element instanceof IResource) {
				IResource iResource = (IResource)element;
				StyledString styledString = new StyledString(getFileName(iResource));
				return styledString;
			}
			return null;
		}
		
		private String getFileName(IResource iResource) {
			String name = iResource.getName();
			return name.isEmpty() ? iResource.getLocation().toString() : name;
		}
		
		@Override
		public void dispose() {
			// garbage collect system resources
			if (resourceManager != null) {
				resourceManager.dispose();
				resourceManager = null;
			}
		}
		
		@Override
		public Image getImage(Object element) {
			if(notExistentResources.contains(element))
				return getResourceManager().createImage(invalidResourcesImage);
			if (element instanceof IFile)
				return getResourceManager().createImage(fileImage);
			return getResourceManager().createImage(directoryImage);
		}
		
		protected ResourceManager getResourceManager() {
			if (resourceManager == null) {
				resourceManager = new LocalResourceManager(JFaceResources.getResources());
			}
			return resourceManager;
		}

		@Override
		public Color getForeground(Object element) {
			if(notExistentResources.contains(element))
				return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
			return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
		}

		@Override
		public Color getBackground(Object element) {
			// TODO Auto-generated method stub
			return null;
		}
	
	}
	
}
