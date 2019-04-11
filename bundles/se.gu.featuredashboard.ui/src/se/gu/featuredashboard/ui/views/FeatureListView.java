package se.gu.featuredashboard.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.model.featuremodel.Project;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.utils.FeaturedashboardConstants;
import se.gu.featuredashboard.utils.IUpdateInformationListener;
import se.gu.featuredashboard.utils.MetricsComparator;
import se.gu.featuredashboard.utils.ParseProjectAction;
import se.gu.featuredashboard.utils.SelectionHandler;

public class FeatureListView extends ViewPart implements IUpdateInformationListener {

	private CheckboxTreeViewer treeViewer;
	private IWorkbenchWindow window;

	private ParseProjectAction parseProject;
	private Action sortTableAction;

	private Project activeProject;

	@Override
	public void createPartControl(Composite parent) {
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		treeViewer = new CheckboxTreeViewer(parent);
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.setContentProvider(new FeatureTreeContentProvider());
		treeViewer.setLabelProvider(new FeatureTreeLabelProvider());

		treeViewer.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				Display.getDefault().asyncExec(() -> {

					treeViewer.setSubtreeChecked(event.getElement(), event.getChecked());

					List<FeatureContainer> featureFileList = new ArrayList<>();

					if (treeViewer.getCheckedElements().length > 0) {
						Arrays.stream(treeViewer.getCheckedElements()).forEach(checkedElement -> {
							FeatureContainer feature = (FeatureContainer) checkedElement;
							featureFileList.add(feature);
						});
					}

					SelectionHandler.setSelection(featureFileList);

					FeatureFileView featureFileView = (FeatureFileView) window.getActivePage()
							.findView(FeaturedashboardConstants.FEATUREFILE_VIEW_ID);
					if (featureFileView != null)
						featureFileView.inputToView(featureFileList);

					FeatureFolderView featureFolderView = (FeatureFolderView) window.getActivePage()
							.findView(FeaturedashboardConstants.FEATUREFOLDER_VIEW_ID);
					if (featureFolderView != null)
						featureFolderView.inputToView(featureFileList);

					// Work in progress
//					HistoryView historyView = (HistoryView) window.getActivePage()
//							.findView(FeaturedashboardConstants.HISTORY_VIEW_ID);
//					if (historyView != null)
//						historyView.inputToView(featureFileList);
				});
			}

		});

		MetricsComparator comparator = new MetricsComparator();
		treeViewer.setComparator(comparator);

		parseProject = new ParseProjectAction(parent.getShell());
		parseProject.setText(FeaturedashboardConstants.ACTION_TEXT);
		parseProject.setToolTipText(FeaturedashboardConstants.ACTION_TOOLTOP_TEXT);
		parseProject.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
		addActionToView(parseProject);

		sortTableAction = new Action() {
			public void run() {
				comparator.setColumn(FeaturedashboardConstants.FEATURELISTTABLE_SORT_COLUMN);
				treeViewer.refresh();
			}
		};

		sortTableAction.setText("Sort alphabetical order");
		sortTableAction.setToolTipText("Sort table");
		sortTableAction.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_UP));
		addActionToView(sortTableAction);

		updateData();
	}

	private void addActionToView(Action action) {
		getViewSite().getActionBars().getMenuManager().add(action);
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void updateData() {
		Display.getDefault().asyncExec(() -> {
			activeProject = ProjectStore.getActiveProject();
			if (activeProject != null) {
				treeViewer.setInput(activeProject);
				treeViewer.refresh();
			}
		});
	}

	public class FeatureTreeContentProvider implements ITreeContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {
			activeProject = (Project) inputElement;

			List<FeatureContainer> list = new ArrayList<>();

			for (Feature feature : activeProject.getRootFeatures()) {
				FeatureContainer fc = activeProject.getFeatureContainer(feature);
				if (fc != null)
					list.add(fc);
			}

			list.addAll(activeProject.getFeatureContainers().stream().filter(featureContainer -> {
				return !activeProject.getFeaturesInModel().contains(featureContainer.getFeature());
			}).collect(Collectors.toList()));

			return list.toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			FeatureContainer parent = (FeatureContainer) parentElement;
			List<FeatureContainer> children = new ArrayList<>();
			for (Feature feature : parent.getFeature().getSubFeatures()) {
				FeatureContainer fc = activeProject.getFeatureContainer(feature);
				if (fc != null)
					children.add(activeProject.getFeatureContainer(feature));
			}

			return children.toArray();
		}

		@Override
		public Object getParent(Object element) {
			FeatureContainer child = (FeatureContainer) element;
			for (Feature feature : activeProject.getRootFeatures()) {
				if (feature.getSubFeatures().contains(child.getFeature())) {
					return activeProject.getFeatureContainer(feature);
				}
			}

			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			FeatureContainer container = (FeatureContainer) element;
			return container.getFeature().getSubFeatures().size() > 0;
		}

	}

	public class FeatureTreeLabelProvider implements ILabelProvider, IColorProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub

		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub

		}

		@Override
		public Image getImage(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getText(Object element) {
			FeatureContainer container = (FeatureContainer) element;
			return container.getFeature().getFeatureID();
		}

		@Override
		public Color getForeground(Object element) {
			FeatureContainer container = (FeatureContainer) element;
			if (activeProject.getFeaturesInModel().contains(container.getFeature())) {
				return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
			}
			return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
		}

		@Override
		public Color getBackground(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
