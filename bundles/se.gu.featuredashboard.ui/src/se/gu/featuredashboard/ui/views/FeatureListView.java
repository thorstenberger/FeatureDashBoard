package se.gu.featuredashboard.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.featuremodel.Tuple;
import se.gu.featuredashboard.model.location.FeatureLocation;
import se.gu.featuredashboard.model.location.ProjectData;
import se.gu.featuredashboard.ui.listeners.IFeatureSelectionListener;
import se.gu.featuredashboard.ui.listeners.IProjectSelectionListener;
import se.gu.featuredashboard.ui.viewscontroller.FeatureDashboardViewController;
import se.gu.featuredashboard.ui.viewscontroller.GeneralViewsController;
import se.gu.featuredashboard.utils.FeaturedashboardConstants;

public class FeatureListView extends ViewPart implements IFeatureSelectionListener, IProjectSelectionListener {

	private TreeViewer fileViewer;

	private FeatureDashboardViewController controller = FeatureDashboardViewController.getInstance();
	private GeneralViewsController viewController = GeneralViewsController.getInstance();

	private static final String TOOLTIP = "Search for files";
	private Map<Feature, List<IFile>> featureToFileList;

	private Table table;

	private CTabFolder tabFolder;

	private CTabItem commonFeatureTable;
	private Group commonFeaturesGroup;

	private CTabItem fileListView;
	private Group fileListGroup;

	private Color present = Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
	private Color absent = Display.getDefault().getSystemColor(SWT.COLOR_RED);

	@Override
	public void createPartControl(Composite parent) {
		viewController.registerFeatureSelectionListener(this);
		viewController.registerProjectSelectionListener(this);

		tabFolder = new CTabFolder(parent, SWT.NONE);
		tabFolder.setSimple(false);

		commonFeatureTable = new CTabItem(tabFolder, SWT.NONE);
		commonFeatureTable.setText("Common features");

		commonFeaturesGroup = new Group(tabFolder, SWT.NONE);
		commonFeaturesGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		commonFeaturesGroup.setLayout(new GridLayout(1, false));

		table = new Table(commonFeaturesGroup, SWT.RESIZE | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		table.setLayoutData(gridData);
		table.setSize(Display.getDefault().getClientArea().width, 2000);

		fileListView = new CTabItem(tabFolder, SWT.NONE);
		fileListView.setText("Feature-file list");

		fileListGroup = new Group(tabFolder, SWT.NONE);
		fileListGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		fileListGroup.setLayout(new GridLayout(1, false));

		initFeatureListView();

		commonFeatureTable.setControl(commonFeaturesGroup);
		fileListView.setControl(fileListGroup);

		tabFolder.setSelection(commonFeatureTable);

		updateFeatureSelection(viewController.getLocations());
	}

	private void initCommonFeaturesTable() {
		Arrays.stream(table.getColumns()).forEach(column -> {
			column.dispose();
		});
		Arrays.stream(table.getItems()).forEach(row -> {
			row.dispose();
		});
		// All features in a project
		List<Tuple<IProject, Set<Feature>>> projectToFeatures = new ArrayList<>();
		// Unique features across all projects
		Set<Feature> workspaceFeaturesSet = new HashSet<>();

		List<ProjectData> workspaceData = controller.getWorkspaceData();
		
		if (workspaceData.size() == 1)
			return;

		workspaceData.forEach(projectData -> {
			Set<Feature> projectFeatures = new HashSet<>();
			
			projectData.getAllLocations().forEach(location -> {
				projectFeatures.add(location.getFeature());
				workspaceFeaturesSet.add(location.getFeature());
			});

			projectToFeatures.add(new Tuple<>(projectData.getProject(), projectFeatures));
		});

		TableColumn tableColumn = new TableColumn(table, SWT.LEFT);
		tableColumn.setText("Feature");

		for (int column = 0; column < projectToFeatures.size(); column++) {
			TableColumn projectColumn = new TableColumn(table, SWT.LEFT);
			projectColumn.setText(projectToFeatures.get(column).getLeft().getName());
		}
		
		List<Feature> workspaceFeaturesList = workspaceFeaturesSet.stream().collect(Collectors.toList());

		for (int row = 0; row < workspaceFeaturesList.size(); row++) {
			TableItem item = new TableItem(table, SWT.NONE);
			for (int column = 0; column < projectToFeatures.size() + 1; column++) {
				if (column == 0) {
					item.setText(column, workspaceFeaturesList.get(row).getFeatureID());
				} else {
					if (projectToFeatures.get(column - 1).getRight().contains(workspaceFeaturesList.get(row))) {
						item.setBackground(column, present);
					} else {
						item.setBackground(column, absent);
					}
				}
			}
		}

		// Pack the columns
		for (int i = 0, n = table.getColumnCount(); i < n; i++) {
			table.getColumn(i).pack();
		}

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	private void initFeatureListView() {
		Text filter = new Text(fileListGroup, SWT.BORDER);
		filter.setText(TOOLTIP);
		filter.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		filter.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (filter.getText().equals(TOOLTIP))
					filter.setText("");
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (filter.getText().equals(""))
					filter.setText(TOOLTIP);
			}

		});

		Filter viewerFilter = new Filter();

		filter.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyReleased(KeyEvent e) {
				viewerFilter.setFilter(filter.getText());
				fileViewer.refresh();
			}

		});

		fileViewer = new TreeViewer(fileListGroup);
		fileViewer.setContentProvider(new ITreeContentProvider() {
			@Override
			public Object[] getElements(Object inputElement) {
				return featureToFileList.keySet().toArray();
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				if (parentElement instanceof Feature) {
					return featureToFileList.get((Feature) parentElement).toArray();
				}
				return null;
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}

			@Override
			public boolean hasChildren(Object element) {
				if (element instanceof IFile)
					return false;
				return !featureToFileList.get((Feature) element).isEmpty();
			}

		});
		fileViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		fileViewer.setLabelProvider(new ILabelProvider() {
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
				if (element instanceof IFile) {
					return ((IFile) element).getName();
				} else {
					return ((Feature) element).getFeatureID();
				}
			}
		});
		fileViewer.setFilters(viewerFilter);
	}

	@Override
	public void setFocus() {
	}

	// Filter for the TreeViewer
	public class Filter extends ViewerFilter {

		private String filter = "";

		public void setFilter(String filter) {
			this.filter = filter;
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof IFile) {
				return ((IFile) element).getName().contains(filter);
			} else {
				for (IFile file : featureToFileList.get((Feature) element)) {
					if (select(viewer, parentElement, file)) {
						return true;
					}
				}
			}
			return false;
		}

	}

	private boolean equalsMappingFile(IFile file) {
		if (file.getFileExtension() == null)
			return false;

		return file.getFileExtension().equals(FeaturedashboardConstants.FEATUREFILE_FILE)
				|| file.getFileExtension().equals(FeaturedashboardConstants.FEATUREFOLDER_FILE)
				|| file.getFileExtension().equals(FeaturedashboardConstants.VPFOLDER_FILE)
				|| file.getFileExtension().equals(FeaturedashboardConstants.VPFILE_FILE);

	}

	@Override
	public void updateFeatureSelection(List<FeatureLocation> featureLocations) {
		featureToFileList = new HashMap<>();

		featureLocations.forEach(featureLocation -> {
			List<IFile> files = featureToFileList.get(featureLocation.getFeature());
			if (files == null) {
				files = new ArrayList<>();
				featureToFileList.put(featureLocation.getFeature(), files);
			}
			if (!equalsMappingFile((IFile) featureLocation.getResource()))
				files.add((IFile) featureLocation.getResource());
		});

		fileViewer.setInput(featureLocations);
	}

	@Override
	public void dispose() {
		super.dispose();
		viewController.removeFeatureSelectionListener(this);
	}

	@Override
	public void updateProjectSelected() {
		initCommonFeaturesTable();
	}

}
