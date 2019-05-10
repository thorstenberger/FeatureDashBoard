/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.ui.views;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import se.gu.featuredashboard.core.WorkspaceData;
import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.location.ProjectData;
import se.gu.featuredashboard.model.metrics.FeatureResourceMetrics;
import se.gu.featuredashboard.model.metrics.ResourceMetrics;
import se.gu.featuredashboard.ui.views.FeatureDashboardView.FeatureModelLabelProvider;
import se.gu.featuredashboard.ui.viewscontroller.MetricsViewController;;

public class MetricsView extends ViewPart {

	CTabFolder ctfMain;
	CTabItem ctiFeatureMetrics;
	CTabItem ctiResourceMetrics;
	
	private TreeViewer tvFeatureMetrics;
	private TreeViewer tvResourceMetrics;
	
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void createPartControl(Composite parent) {
		ctfMain = new CTabFolder(parent, SWT.BORDER |SWT.BOTTOM);
	    ctfMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	    ctfMain.setSimple(false);
	    ctfMain.setUnselectedImageVisible(false);
	    ctfMain.setUnselectedCloseVisible(false); 

	    ctiFeatureMetrics = new CTabItem(ctfMain, SWT.CLOSE);
	    ctiFeatureMetrics.setText("Feature Metrics");
	    ctiFeatureMetrics.setShowClose(false);

	    ctiResourceMetrics = new CTabItem(ctfMain, SWT.CLOSE);
	    ctiResourceMetrics.setText("Resource Metrics");
	    ctiResourceMetrics.setShowClose(false);
	    	   
	    setupFeatureMetricsTab();
	    setupResourceMetricsTab();
	       
		ctfMain.setSelection(ctiFeatureMetrics);
	    setupToolBar();
		
	}

	private void setupToolBar() {
	    IActionBars bars = getViewSite().getActionBars();
	    
	    Action refreshProject = new Action() {
	    	@Override
			public void run() {    		
	    		MetricsViewController.getInstance().refreshWorkspaceData();   		
	    	}
		};
		refreshProject.setImageDescriptor(PlatformUI.getWorkbench().
				getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
		refreshProject.setToolTipText("Refresh Workspace Data");
		bars.getToolBarManager().add(refreshProject);	
	}

	private void setupFeatureMetricsTab() {
		CTabItem parent = ctiFeatureMetrics;
		
		Group grpFeatureModel = new Group(parent.getParent(), SWT.NONE);
		parent.setControl(grpFeatureModel);
		
		GridData gridData = new GridData(GridData.FILL, GridData.FILL,true, true);
		grpFeatureModel.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout(1, false);
		grpFeatureModel.setLayout(gridLayout);
		
		FeatureFilter filter = new FeatureFilter();	

		Text txtFeatureFilter = new Text(grpFeatureModel, SWT.NONE);
		txtFeatureFilter.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtFeatureFilter.setText("filter features here");
		txtFeatureFilter.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		txtFeatureFilter.addFocusListener(new FocusListener() {	
			@Override
			public void focusLost(FocusEvent e) {
            	if(txtFeatureFilter.getText().isEmpty()){
            		txtFeatureFilter.setText("filter features here");
            		txtFeatureFilter.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
            	}				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if(txtFeatureFilter.getText().equals("filter features here")) {
					txtFeatureFilter.setText("");
					txtFeatureFilter.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));	
				}
			}
		});
		
		txtFeatureFilter.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
            	filter.setSearchText(txtFeatureFilter.getText());
            	tvFeatureMetrics.refresh(); 	
            	//tvFeatureMetrics.setCheckedElements(allCheckedFeatures.toArray());
            }
        });
	
		tvFeatureMetrics = new TreeViewer(grpFeatureModel,SWT.H_SCROLL | SWT.V_SCROLL);
		tvFeatureMetrics.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		tvFeatureMetrics.getTree().setHeaderVisible(true);

		tvFeatureMetrics.addFilter(filter);
		
		TreeViewerColumn featureColumn = new TreeViewerColumn(tvFeatureMetrics, SWT.H_SCROLL | SWT.V_SCROLL);
		featureColumn.getColumn().setText("Feature");
		featureColumn.getColumn().setWidth(150);
		featureColumn.setLabelProvider(
					new DelegatingStyledCellLabelProvider(
						new FeatureMetrics_FeatureResource_LabelProvider(
							createImageDescriptor("icons/feature_icon.png"),
							createImageDescriptor("icons/project_icon.png"),
							createImageDescriptor("icons/folder_icon.png"),
							createImageDescriptor("icons/file_icon.png")
							)));
		
		TreeViewerColumn scatterigDegreeColumn = new TreeViewerColumn(tvFeatureMetrics, SWT.NONE);
		scatterigDegreeColumn.getColumn().setText("SD");
		scatterigDegreeColumn.getColumn().setWidth(70);
		scatterigDegreeColumn.getColumn().setAlignment(SWT.CENTER);
		scatterigDegreeColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(
						new FeatureMetrics_ScatteringDegree_LabelProvider() ));
		
		TreeViewerColumn filaAnnotationsColumn = new TreeViewerColumn(tvFeatureMetrics, SWT.NONE);
		filaAnnotationsColumn.getColumn().setText("NoFiA");
		filaAnnotationsColumn.getColumn().setWidth(70);
		filaAnnotationsColumn.getColumn().setAlignment(SWT.CENTER);
		filaAnnotationsColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(
						new FeatureMetrics_AnnotatedFiles_LabelProvider() ));
		
		TreeViewerColumn folderAnnotationsColumn = new TreeViewerColumn(tvFeatureMetrics, SWT.NONE);
		folderAnnotationsColumn.getColumn().setText("NoFoA");
		folderAnnotationsColumn.getColumn().setWidth(70);
		folderAnnotationsColumn.getColumn().setAlignment(SWT.CENTER);
		folderAnnotationsColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(
						new FeatureMetrics_AnnotatedFolders_LabelProvider() ));
		
		TreeViewerColumn tanglingDegreeColumn = new TreeViewerColumn(tvFeatureMetrics, SWT.NONE);
		tanglingDegreeColumn.getColumn().setText("TD");
		tanglingDegreeColumn.getColumn().setWidth(70);
		tanglingDegreeColumn.getColumn().setAlignment(SWT.CENTER);
		tanglingDegreeColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(
						new FeatureMetrics_TanglingDegree_LabelProvider()));
		
		TreeViewerColumn lineNumberColumn = new TreeViewerColumn(tvFeatureMetrics, SWT.NONE);
		lineNumberColumn.getColumn().setText("LoFC");
		lineNumberColumn.getColumn().setWidth(70);
		lineNumberColumn.getColumn().setAlignment(SWT.CENTER);
		lineNumberColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(
						new FeatureMetrics_LinesNumber_LabelProvider() ));
		
		TreeViewerColumn neestingDepthAverageColumn = new TreeViewerColumn(tvFeatureMetrics, SWT.NONE);
		neestingDepthAverageColumn.getColumn().setText("AvgND");
		neestingDepthAverageColumn.getColumn().setWidth(70);
		neestingDepthAverageColumn.getColumn().setAlignment(SWT.CENTER);
		neestingDepthAverageColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(
						new FeatureMetrics_NestingDepthAverage_LabelProvider() ));
		
		
		TreeViewerColumn nestingDepthMaxColumn = new TreeViewerColumn(tvFeatureMetrics, SWT.NONE);
		nestingDepthMaxColumn.getColumn().setText("MaxND");
		nestingDepthMaxColumn.getColumn().setWidth(70);
		nestingDepthMaxColumn.getColumn().setAlignment(SWT.CENTER);
		nestingDepthMaxColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(
						new FeatureMetrics_NestingDepthMax_LabelProvider() ));
		
		TreeViewerColumn nestingDepthMinColumn = new TreeViewerColumn(tvFeatureMetrics, SWT.NONE);
		nestingDepthMinColumn.getColumn().setText("MinND");
		nestingDepthMinColumn.getColumn().setWidth(70);
		nestingDepthMinColumn.getColumn().setAlignment(SWT.CENTER);
		nestingDepthMinColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(
						new FeatureMetrics_NestingDepthMin_LabelProvider() ));
		
		TreeViewerColumn authorsColumn = new TreeViewerColumn(tvFeatureMetrics, SWT.NONE);
		authorsColumn.getColumn().setText("NoAu");
		authorsColumn.getColumn().setWidth(70);
		authorsColumn.getColumn().setAlignment(SWT.CENTER);
		authorsColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(
						new FeatureMetrics_AuthorsNumber_LabelProvider()));

		updateFeatureMetricsTab();
	}
	
	public void updateFeatureMetricsTab() {
		tvFeatureMetrics.setContentProvider(new FeatureMetrics_ContentProvider());
		tvFeatureMetrics.setInput("root"); // passing a non-null value that will be ignored later
	}
	
	private void setupResourceMetricsTab() {
		CTabItem parent = ctiResourceMetrics;
		
		Group grpResourceMetrics = new Group(parent.getParent(), SWT.NONE);
		parent.setControl(grpResourceMetrics);
		
		GridData gridData = new GridData(GridData.FILL, GridData.FILL,true, true);
		grpResourceMetrics.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout(1, false);
		grpResourceMetrics.setLayout(gridLayout);
		
		ResourceFilter filter = new ResourceFilter();	

		Text txtResourceFilter = new Text(grpResourceMetrics, SWT.NONE);
		txtResourceFilter.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtResourceFilter.setText("filter resources here");
		txtResourceFilter.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		txtResourceFilter.addFocusListener(new FocusListener() {	
			@Override
			public void focusLost(FocusEvent e) {
            	if(txtResourceFilter.getText().isEmpty()){
            		txtResourceFilter.setText("filter resources here");
            		txtResourceFilter.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
            	}				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if(txtResourceFilter.getText().equals("filter resources here")) {
					txtResourceFilter.setText("");
					txtResourceFilter.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));	
				}
			}
		});
		
		txtResourceFilter.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
            	filter.setSearchText(txtResourceFilter.getText());
            	tvResourceMetrics.refresh(); 	
            	//tvFeatureMetrics.setCheckedElements(allCheckedFeatures.toArray());
            }
        });
	
		tvResourceMetrics = new TreeViewer(grpResourceMetrics,SWT.H_SCROLL | SWT.V_SCROLL);
		tvResourceMetrics.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		tvResourceMetrics.getTree().setHeaderVisible(true);

		tvResourceMetrics.addFilter(filter);
		
		TreeViewerColumn resourceNameColumn = new TreeViewerColumn(tvResourceMetrics, SWT.H_SCROLL | SWT.V_SCROLL);
		resourceNameColumn.getColumn().setText("Resource");
		resourceNameColumn.getColumn().setWidth(200);
		resourceNameColumn.setLabelProvider(
					new DelegatingStyledCellLabelProvider(
						new ResourceMetrics_Resource_LabelProvider(
							createImageDescriptor("icons/project_icon.png"),
							createImageDescriptor("icons/folder_icon.png"),
							createImageDescriptor("icons/file_icon.png")
							)));
		
		TreeViewerColumn allFeaturesColumn = new TreeViewerColumn(tvResourceMetrics, SWT.NONE);
		allFeaturesColumn.getColumn().setText("NoF");
		allFeaturesColumn.getColumn().setWidth(70);
		allFeaturesColumn.getColumn().setAlignment(SWT.CENTER);
		allFeaturesColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(
						new ResourceMetrics_AllFeatures_LabelProvider()));
		
		TreeViewerColumn numberOfFilesColumn = new TreeViewerColumn(tvResourceMetrics, SWT.NONE);
		numberOfFilesColumn.getColumn().setText("NoFi");
		numberOfFilesColumn.getColumn().setWidth(70);
		numberOfFilesColumn.getColumn().setAlignment(SWT.CENTER);
		numberOfFilesColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(
						new ResourceMetrics_NumberOfFile_LabelProvider()));
		
		TreeViewerColumn totalLinesColumn = new TreeViewerColumn(tvResourceMetrics, SWT.NONE);
		totalLinesColumn.getColumn().setText("LoFC");
		totalLinesColumn.getColumn().setWidth(70);
		totalLinesColumn.getColumn().setAlignment(SWT.CENTER);
		totalLinesColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(
						new ResourceMetrics_TotalLines_LabelProvider()));
		
		TreeViewerColumn AverageLinesColumn = new TreeViewerColumn(tvResourceMetrics, SWT.NONE);
		AverageLinesColumn.getColumn().setText("Avg. LoFC");
		AverageLinesColumn.getColumn().setWidth(70);
		AverageLinesColumn.getColumn().setAlignment(SWT.CENTER);
		AverageLinesColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(
						new ResourceMetrics_AverageLines_LabelProvider()));
		
		TreeViewerColumn averageNestingDepthColumn = new TreeViewerColumn(tvResourceMetrics, SWT.NONE);
		averageNestingDepthColumn.getColumn().setText("Avg. ND");
		averageNestingDepthColumn.getColumn().setWidth(70);
		averageNestingDepthColumn.getColumn().setAlignment(SWT.CENTER);
		averageNestingDepthColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(
						new ResourceMetrics_AverageNestingDepth_LabelProvider()));
		
		TreeViewerColumn averageScatteringDegreeColumn = new TreeViewerColumn(tvResourceMetrics, SWT.NONE);
		averageScatteringDegreeColumn.getColumn().setText("Avg. SD");
		averageScatteringDegreeColumn.getColumn().setWidth(70);
		averageScatteringDegreeColumn.getColumn().setAlignment(SWT.CENTER);
		averageScatteringDegreeColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(
						new ResourceMetrics_AverageScatteringDegree_LabelProvider()));
		
		
		updateResourceMetricsTab();
	}
	
	public void updateResourceMetricsTab() {
		tvResourceMetrics.setContentProvider(new ResourceMetrics_ContentProvider());
		tvResourceMetrics.setInput("root"); // passing a non-null value that will be ignored later
	}
	
	private ImageDescriptor createImageDescriptor(String path) {
		Bundle bundle = FrameworkUtil.getBundle(FeatureModelLabelProvider.class);
		URL url = FileLocator.find(bundle, new Path(path), null);
		return ImageDescriptor.createFromURL(url);
	}

	public class FeatureMetrics_ContentProvider implements ITreeContentProvider{

		@Override
		public Object[] getElements(Object inputElement) {
			List<FeatureResourceMetricsTreeNode> ans = new ArrayList<MetricsView.FeatureResourceMetricsTreeNode>();
			
			List<Feature> allFeatures = WorkspaceData.getInstance().getAllFeatures();			
			IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
			
			for(Feature feature:allFeatures) {		
				FeatureResourceMetrics featureWorkspaceMetrics = new FeatureResourceMetrics(feature, workspace);
				List<FeatureResourceMetrics> allFeatureSubtreeMetrics = new ArrayList<FeatureResourceMetrics>();

				for (IProject project : WorkspaceData.getInstance().getAllProjects()) {
					ProjectData projectData = WorkspaceData.getInstance().getProjectData(project);	
					if (projectData != null) {
						FeatureResourceMetrics featureProjectMetrics = projectData.getFeatureResourceMetrics(feature,project);
						
						if(featureProjectMetrics!=null) {
							allFeatureSubtreeMetrics.addAll(projectData.getFeatureResourceMetrics(feature));
							featureWorkspaceMetrics.addFeature_ChildResourceMetrics(featureProjectMetrics);	
						}
					}
				}	
				
				if(allFeatureSubtreeMetrics.size()>0) {
					ans.add(new FeatureResourceMetricsTreeNode(null,featureWorkspaceMetrics,allFeatureSubtreeMetrics));
				}
			}

			return ans.toArray(new Object[ans.size()]);
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			List<Object> ans = new ArrayList<Object>();
			
			if(parentElement instanceof FeatureResourceMetricsTreeNode) {
				FeatureResourceMetricsTreeNode parentNode = ((FeatureResourceMetricsTreeNode) parentElement);
				IResource resource = parentNode.getNodeMetrics().getResource();
				
				if(resource instanceof IFile) {
					return new Object[0];
				}
				
				else if(resource instanceof IContainer) {
					try {
						for(IResource member:((IContainer) resource).members()) {

							FeatureResourceMetrics nodeMetrics = null;
							List<FeatureResourceMetrics> nodeAllSubTreeMetrics = new ArrayList<FeatureResourceMetrics>();
							
							for(FeatureResourceMetrics featureMetrics:parentNode.getChildNodeSubMetrics()) {
								if(featureMetrics.getResource().equals(member)) {
									nodeMetrics =featureMetrics;
								}
								if(member.getProjectRelativePath().
										isPrefixOf(featureMetrics.getResource().getProjectRelativePath()))
									nodeAllSubTreeMetrics.add(featureMetrics);
							}
							
							if(nodeMetrics!=null) {
								FeatureResourceMetricsTreeNode treeNode = new FeatureResourceMetricsTreeNode(
										parentNode,nodeMetrics, nodeAllSubTreeMetrics );
								ans.add(treeNode);
							}
						}
						
					} 
					catch (CoreException e) {e.printStackTrace();}
				}
			}
			
			return ans.toArray(new Object[ans.size()]);

		}

		@Override
		public Object getParent(Object element) {
			if(element instanceof FeatureResourceMetricsTreeNode) {
				return ((FeatureResourceMetricsTreeNode)element).getParentNode();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			
			if(element instanceof FeatureResourceMetricsTreeNode) {
				FeatureResourceMetricsTreeNode node = (FeatureResourceMetricsTreeNode)element;
				if(node.getNodeMetrics().getResource() instanceof IFile) {
					return false;
				}
				if(node.getNodeMetrics().getResource() instanceof IContainer) {
					return true;
				}
			}
			return false;
		}
		
	}
	
	public class FeatureMetrics_FeatureResource_LabelProvider extends LabelProvider implements IStyledLabelProvider{

		private ImageDescriptor featureImage;
		private ImageDescriptor projectImage;
		private ImageDescriptor folderImage;
		private ImageDescriptor fileImage;
		
		private ResourceManager resourceManager;
		
		public FeatureMetrics_FeatureResource_LabelProvider(ImageDescriptor featureImage,
				ImageDescriptor projectImage,ImageDescriptor folderImage, ImageDescriptor fileImage){
			this.featureImage = featureImage;
			this.projectImage = projectImage;
			this.folderImage = folderImage;
			this.fileImage = fileImage;
		}
		
		@Override
		public StyledString getStyledText(Object element) {

			if(element instanceof FeatureResourceMetricsTreeNode) {		
				FeatureResourceMetricsTreeNode node = (FeatureResourceMetricsTreeNode)element;
				StyledString styledString = null;
				if(node.getParentNode()==null) {
					Feature feature = node.getNodeMetrics().getFeature();
					styledString = new StyledString(feature.getFeatureID());
				}
				else {
					IResource iResource = node.getNodeMetrics().getResource();
					styledString = new StyledString(getFileName(iResource));
				}
			
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
		
		protected ResourceManager getResourceManager() {
			if (resourceManager == null) {
				resourceManager = new LocalResourceManager(JFaceResources.getResources());
			}
			return resourceManager;
		}

		@Override
		public Image getImage(Object element) {

			if(element instanceof FeatureResourceMetricsTreeNode) {
				FeatureResourceMetricsTreeNode node = (FeatureResourceMetricsTreeNode)element;
				IResource iResource = node.getNodeMetrics().getResource();
				
				if(node.getParentNode()==null)
					return getResourceManager().createImage(featureImage);
				if(iResource instanceof IProject)
					return getResourceManager().createImage(projectImage);
				if(iResource instanceof IFolder)
					return getResourceManager().createImage(folderImage);
				if (iResource instanceof IFile)
					return getResourceManager().createImage(fileImage);
			}

			return null;
		}
	
	}

	public class FeatureMetrics_ScatteringDegree_LabelProvider extends LabelProvider implements IStyledLabelProvider{
	       
		@Override
        public StyledString getStyledText(Object element) {
            if (element instanceof FeatureResourceMetricsTreeNode) {
            	FeatureResourceMetricsTreeNode node = (FeatureResourceMetricsTreeNode) element;
                return new StyledString(node.getNodeMetrics().getTotalScatteringDegree()+"");
            }
            return null;
        }
	}
	
	public class FeatureMetrics_AnnotatedFiles_LabelProvider extends LabelProvider implements IStyledLabelProvider{
	       
		@Override
        public StyledString getStyledText(Object element) {
            if (element instanceof FeatureResourceMetricsTreeNode) {
            	FeatureResourceMetricsTreeNode node = (FeatureResourceMetricsTreeNode) element;
                return new StyledString(node.getNodeMetrics().getTotalFileAnnotations()+"");
            }
            return null;
        }
	}
	
	public class FeatureMetrics_AnnotatedFolders_LabelProvider extends LabelProvider implements IStyledLabelProvider{
	       
		@Override
        public StyledString getStyledText(Object element) {
            if (element instanceof FeatureResourceMetricsTreeNode) {
            	FeatureResourceMetricsTreeNode node = (FeatureResourceMetricsTreeNode) element;
                return new StyledString(node.getNodeMetrics().getTotalFolderAnnotations()+"");
            }
            return null;
        }
	}
	
	public class FeatureMetrics_TanglingDegree_LabelProvider extends LabelProvider implements IStyledLabelProvider {

		@Override
        public StyledString getStyledText(Object element) {
            if (element instanceof FeatureResourceMetricsTreeNode) {
            	FeatureResourceMetricsTreeNode node = (FeatureResourceMetricsTreeNode) element;
                return new StyledString(node.getNodeMetrics().getTotalCommonFeatures().size()-1+"");
            }
            return null;
        }
	}
	
	public class FeatureMetrics_AuthorsNumber_LabelProvider extends LabelProvider implements IStyledLabelProvider{
	       
		@Override
        public StyledString getStyledText(Object element) {
            if (element instanceof FeatureResourceMetricsTreeNode) {
            	FeatureResourceMetricsTreeNode node = (FeatureResourceMetricsTreeNode) element;
                return new StyledString(node.getNodeMetrics().getAuthors().size()+"");
            }
            return null;
        }
	}
	
	public class FeatureMetrics_LinesNumber_LabelProvider extends LabelProvider implements IStyledLabelProvider{
       
		@Override
        public StyledString getStyledText(Object element) {
            if (element instanceof FeatureResourceMetricsTreeNode) {
            	FeatureResourceMetricsTreeNode node = (FeatureResourceMetricsTreeNode) element;
                return new StyledString(node.getNodeMetrics().getTotalLines()+"");
            }
            return null;
        }
	}
	
	public class FeatureMetrics_NestingDepthAverage_LabelProvider extends LabelProvider implements IStyledLabelProvider{
	       
		@Override
        public StyledString getStyledText(Object element) {
            if (element instanceof FeatureResourceMetricsTreeNode) {
            	FeatureResourceMetricsTreeNode node = (FeatureResourceMetricsTreeNode) element;
            	double value = node.getNodeMetrics().getAverageNestingDepth();
            	if(value>0)
            		return new StyledString(String.format("%.2f", value));
            	else
            		return new StyledString("");
            }
            return null;
        }
	}
	
	public class FeatureMetrics_NestingDepthMax_LabelProvider extends LabelProvider implements IStyledLabelProvider{
	       
		@Override
        public StyledString getStyledText(Object element) {
            if (element instanceof FeatureResourceMetricsTreeNode) {
            	FeatureResourceMetricsTreeNode node = (FeatureResourceMetricsTreeNode) element;
            	int value =node.getNodeMetrics().getMaxNestingDepth();
            	if(value>0)
            		return new StyledString(value+"");
            	else
            		return new StyledString("");
            }
            return null;
        }
	}
	
	public class FeatureMetrics_NestingDepthMin_LabelProvider extends LabelProvider implements IStyledLabelProvider{
	       
		@Override
        public StyledString getStyledText(Object element) {
            if (element instanceof FeatureResourceMetricsTreeNode) {
            	FeatureResourceMetricsTreeNode node = (FeatureResourceMetricsTreeNode) element;
            	int value =node.getNodeMetrics().getMinNestingDepth();
            	if(value>0)
            		return new StyledString(value+"");
            	else
            		return new StyledString("");
            }
            return null;
        }
	}
		
	public class FeatureFilter extends ViewerFilter {

	    private String searchString = ".*";
		
		public void setSearchText(String s) {
		        // ensure that the value can be used for matching
		        this.searchString = ".*" + s + ".*";
		}

		public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
			if (element instanceof FeatureResourceMetricsTreeNode) { 
				Feature feature = ((FeatureResourceMetricsTreeNode) element).getNodeMetrics().getFeature();
				if(feature.getFeatureID().matches(searchString))
					return true;
				return false;
			}
			else
				System.out.println("not recognized type for filtering"+element.getClass().getName());

			return true;
		}
	}
	
	public class FeatureResourceMetricsTreeNode{
		
		private FeatureResourceMetricsTreeNode parentNode;;
		private FeatureResourceMetrics nodeMetrics;
		private List<FeatureResourceMetrics> childNodeSubMetrics;

		public FeatureResourceMetricsTreeNode(FeatureResourceMetricsTreeNode parentNode) {
			this.parentNode = parentNode;
		}
		
		public FeatureResourceMetricsTreeNode(FeatureResourceMetricsTreeNode parentNode,
				FeatureResourceMetrics nodeMetrics,
				List<FeatureResourceMetrics> childNodeSubMetrics){
			this.parentNode = parentNode;
			this.nodeMetrics = nodeMetrics;
			this.childNodeSubMetrics = childNodeSubMetrics;
		}
		
		public FeatureResourceMetricsTreeNode getParentNode() {
			return parentNode;
		}
		
		public FeatureResourceMetrics getNodeMetrics() {
			return nodeMetrics;
		}
		public void setNodeMetrics(FeatureResourceMetrics nodeMetrics) {
			this.nodeMetrics = nodeMetrics;
		}
		
		public List<FeatureResourceMetrics> getChildNodeSubMetrics() {
			return childNodeSubMetrics;
		}
		public void setChildNodeSubMetrics(List<FeatureResourceMetrics> childNodeSubMetrics) {
			this.childNodeSubMetrics = childNodeSubMetrics;
		}

	}

	//************************************************************************************************
	
	public class ResourceFilter extends ViewerFilter{

	    private String searchString = ".*";
		
		public void setSearchText(String s) {
		        // ensure that the value can be used for matching
		        this.searchString = ".*" + s + ".*";
		}

		public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
			if( element instanceof ResourceMetrics)
				return true;
			if (element instanceof IResource) { 
				IResource resource = (IResource) element;
				if(resource.getName().matches(searchString))
					return true;
				ArrayList<IResource> subResources = getSubTreeElements(resource);
				for(IResource subResource:subResources ) {
					if(select(viewer,parentElement,subResource)) 
						return true;		
				}	
				return false;
			}
			else
				System.out.println("not recognized type for filtering "+element.getClass().getName());	
			return true;
		}
	}
	
	private ArrayList<IResource> getSubTreeElements(IResource resource){
		ArrayList<IResource> subTreeElemets = new ArrayList<IResource>();
		if(resource instanceof IFile || !resource.exists()) 
			return subTreeElemets;
		if(resource instanceof IContainer) {
			try {
				for(IResource subResource: ((IContainer)resource).members()) {
					subTreeElemets.add(subResource);
					if(subResource instanceof IContainer)
						subTreeElemets.addAll(getSubTreeElements(subResource));			
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return subTreeElemets;
		}
		return subTreeElemets;
	}
	
	public class ResourceMetrics_ContentProvider implements ITreeContentProvider{

		private ResourceMetrics rootMetrics;
		
		@Override
		public Object[] getElements(Object inputElement) {
			List<Object> ans = new ArrayList<Object>();
						
			IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
			rootMetrics = new ResourceMetrics(workspace);

			for (IProject project : WorkspaceData.getInstance().getAllProjects()) {
				ProjectData projectData = WorkspaceData.getInstance().getProjectData(project);
				if (projectData != null) {
					ResourceMetrics projectMetrics = projectData.getResourceMetrics(project);
					if (projectMetrics != null)
						rootMetrics.addChildMetrics(projectMetrics);
				}
			}
			
			ans.add(rootMetrics);
			return ans.toArray(new Object[ans.size()]);
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if(parentElement instanceof ResourceMetrics) {
				return ResourcesPlugin.getWorkspace().getRoot().getProjects();
			}
			try {
				if(parentElement instanceof IProject)
					return ((IProject) parentElement).members();
				if(parentElement instanceof IFolder)
					return ((IFolder)parentElement).members();
				if(parentElement instanceof IFile)
					return null;
				return null;
			}
			catch (Exception e) {
				// TODO: handle exception
			}
			return null;
		}

		@Override
		public Object getParent(Object element) {
			if(element instanceof ResourceMetrics) // for workspace
				return null;
			if(element instanceof IProject)
				return rootMetrics;
			if(element instanceof IFolder)
				return ((IFolder)element).getParent();
			if(element instanceof IFile)
				return ((IFile)element).getParent();
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if(element instanceof ResourceMetrics) // for workspace
				return true;
			if(element instanceof IFile)
				return false;
			if(element instanceof IContainer)
				return true;
			return false;
		}
		
	}
	
	public class ResourceMetrics_Resource_LabelProvider extends LabelProvider implements IStyledLabelProvider{

		private ImageDescriptor projectImage;
		private ImageDescriptor folderImage;
		private ImageDescriptor fileImage;
		private ResourceManager resourceManager;

		public ResourceMetrics_Resource_LabelProvider(ImageDescriptor projectImage,
				ImageDescriptor folderImage,ImageDescriptor fileImage) {
			this.projectImage = projectImage;
			this.folderImage = folderImage;
			this.fileImage = fileImage;
		}

		@Override
		public StyledString getStyledText(Object element) {
			if(element instanceof ResourceMetrics) {
				return new StyledString("workspace");
			}
			if (element instanceof IResource) {
				IResource iResource = (IResource)element;
				return new StyledString(getFileName(iResource));
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
		
		protected ResourceManager getResourceManager() {
			if (resourceManager == null) {
				resourceManager = new LocalResourceManager(JFaceResources.getResources());
			}
			return resourceManager;
		}

		@Override
		public Image getImage(Object element) {

			if(element instanceof ResourceMetrics) 
				return null;
			if (element instanceof IProject)
				return getResourceManager().createImage(projectImage);
			if (element instanceof IFolder)
				return getResourceManager().createImage(folderImage);
			if (element instanceof IFile)
				return getResourceManager().createImage(fileImage);

			return null;
		}	
	}
	
	public class ResourceMetrics_AllFeatures_LabelProvider extends LabelProvider implements IStyledLabelProvider{

		@Override
		public StyledString getStyledText(Object element) {
			if(element instanceof ResourceMetrics) 
				return new StyledString(((ResourceMetrics) element).getAllFeatures().size()+"");
			if(element instanceof IResource) {
				ProjectData data = WorkspaceData.getInstance().getProjectData(((IResource)element).getProject());
				ResourceMetrics metrics = data.getResourceMetrics((IResource)element);
				if(metrics!=null)
					return new StyledString(metrics.getAllFeatures().size()+"");
				else
					return new StyledString("0");
			}
			return new StyledString("0");
		}
		
	}
	
	public class ResourceMetrics_TotalLines_LabelProvider extends LabelProvider implements IStyledLabelProvider{

		@Override
		public StyledString getStyledText(Object element) {
			if(element instanceof ResourceMetrics) 
				return new StyledString(((ResourceMetrics) element).getTotalLines()+"");
			if(element instanceof IResource) {
				ProjectData data = WorkspaceData.getInstance().getProjectData(((IResource)element).getProject());
				ResourceMetrics metrics = data.getResourceMetrics((IResource)element);
				if(metrics!=null)
					return new StyledString(metrics.getTotalLines()+"");
				else
					return new StyledString("0");
			}
			return new StyledString("0");
		}
		
	}
	
	public class ResourceMetrics_AverageLines_LabelProvider extends LabelProvider implements IStyledLabelProvider{

		@Override
		public StyledString getStyledText(Object element) {
			int totalLines = 0, totalFeatures=1;
			double ans=0;
			if(element instanceof ResourceMetrics) {
				totalFeatures = ((ResourceMetrics) element).getAllFeatures().size();
				if(totalFeatures<1)
					return new StyledString("0");
				
				totalLines = ((ResourceMetrics) element).getTotalLines();
				ans = (double)totalLines/totalFeatures;
				return new StyledString(String.format("%.2f", ans));
			}
			
			if(element instanceof IResource) {
				ProjectData data = WorkspaceData.getInstance().getProjectData(((IResource)element).getProject());
				ResourceMetrics metrics = data.getResourceMetrics((IResource)element);
				if(metrics!=null) {
					totalFeatures = metrics.getAllFeatures().size();
					if(totalFeatures<1)
						return new StyledString("0");
					
					totalLines = metrics.getTotalLines();
					ans = (double)totalLines/totalFeatures;
					return new StyledString(String.format("%.2f", ans));
				}
				else
					return new StyledString("0");
			}
			return new StyledString("0");
		}	
	}
	
	public class ResourceMetrics_NumberOfFile_LabelProvider extends LabelProvider implements IStyledLabelProvider{

		@Override
		public StyledString getStyledText(Object element) {
			if(element instanceof ResourceMetrics) 
				return new StyledString(((ResourceMetrics) element).getSubtreeFileNumbers()+"");
			if(element instanceof IResource) {
				ProjectData data = WorkspaceData.getInstance().getProjectData(((IResource)element).getProject());
				ResourceMetrics metrics = data.getResourceMetrics((IResource)element);
				if(metrics!=null)
					return new StyledString(metrics.getSubtreeFileNumbers()+"");
				else
					return new StyledString("0");
			}
			return new StyledString("0");
		}	
	}
	
	public class ResourceMetrics_AverageNestingDepth_LabelProvider extends LabelProvider implements IStyledLabelProvider{
		
		@Override
		public StyledString getStyledText(Object element) {
			int totalLines = 0, totalFeatures=1;
			double ans=0;
			if(element instanceof ResourceMetrics) {
				totalFeatures = ((ResourceMetrics) element).getAllFeatures().size();
				if(totalFeatures<1)
					return new StyledString("0");
				
				totalLines = ((ResourceMetrics) element).getTotalNestingDepth();
				ans = (double)totalLines/totalFeatures;
				return new StyledString(String.format("%.2f", ans));
			}
			if(element instanceof IResource) {
				ProjectData data = WorkspaceData.getInstance().getProjectData(((IResource)element).getProject());
				ResourceMetrics metrics = data.getResourceMetrics((IResource)element);
				if(metrics!=null) {
					totalFeatures = metrics.getAllFeatures().size();
					if(totalFeatures<1)
						return new StyledString("0");
					
					totalLines = metrics.getTotalNestingDepth();
					ans = (double)totalLines/totalFeatures;
					return new StyledString(String.format("%.2f", ans));
				}
				else
					return new StyledString("0");
			}
			return new StyledString("0");
		}
	}
	
	public class ResourceMetrics_AverageScatteringDegree_LabelProvider extends LabelProvider implements IStyledLabelProvider{

		@Override
		public StyledString getStyledText(Object element) {
			int totalLines = 0, totalFeatures=1;
			double ans=0;
			if(element instanceof ResourceMetrics) {
				totalFeatures = ((ResourceMetrics) element).getAllFeatures().size();
				if(totalFeatures<1)
					return new StyledString("0");
				
				totalLines = ((ResourceMetrics) element).getTotalScatteringDegree();
				ans = (double)totalLines/totalFeatures;
				return new StyledString(String.format("%.2f", ans));
			}
			if(element instanceof IResource) {
				ProjectData data = WorkspaceData.getInstance().getProjectData(((IResource)element).getProject());
				ResourceMetrics metrics = data.getResourceMetrics((IResource)element);
				if(metrics!=null) {
					totalFeatures = metrics.getAllFeatures().size();
					if(totalFeatures<1)
						return new StyledString("0");
					
					totalLines = metrics.getTotalScatteringDegree();
					ans = (double)totalLines/totalFeatures;
					return new StyledString(String.format("%.2f", ans));
				}
				else
					return new StyledString("0");
			}
			return new StyledString("0");
		}	
	}
	
	public class ResourceMetrics_AllAthours_LabelProvider extends LabelProvider implements IStyledLabelProvider{

		@Override
		public StyledString getStyledText(Object element) {
			if(element instanceof ResourceMetrics) 
				return new StyledString(((ResourceMetrics) element).getAllAuthors().size()+"");
			if(element instanceof IResource) {
				ProjectData data = WorkspaceData.getInstance().getProjectData(((IResource)element).getProject());
				ResourceMetrics metrics = data.getResourceMetrics((IResource)element);
				if(metrics!=null)
					return new StyledString(metrics.getAllAuthors().size()+"");
				else
					return new StyledString("0");
			}
			return new StyledString("0");
		}	
	}
	
	public class ResourceMetricsTreeNode{
		
		private ResourceMetricsTreeNode parentNode;;
		private ResourceMetrics nodeMetrics;
		private List<ResourceMetrics> childNodeSubMetrics;

		public ResourceMetricsTreeNode(ResourceMetricsTreeNode parentNode) {
			this.parentNode = parentNode;
		}
		
		public ResourceMetricsTreeNode(ResourceMetricsTreeNode parentNode,
				ResourceMetrics nodeMetrics,
				List<ResourceMetrics> childNodeSubMetrics){
			this.parentNode = parentNode;
			this.nodeMetrics = nodeMetrics;
			this.childNodeSubMetrics = childNodeSubMetrics;
		}
		
		public ResourceMetricsTreeNode getParentNode() {
			return parentNode;
		}
		
		public ResourceMetrics getNodeMetrics() {
			return nodeMetrics;
		}
		public void setNodeMetrics(ResourceMetrics nodeMetrics) {
			this.nodeMetrics = nodeMetrics;
		}
		
		public List<ResourceMetrics> getChildNodeSubMetrics() {
			return childNodeSubMetrics;
		}
		public void setChildNodeSubMetrics(List<ResourceMetrics> childNodeSubMetrics) {
			this.childNodeSubMetrics = childNodeSubMetrics;
		}

	}
	
}
