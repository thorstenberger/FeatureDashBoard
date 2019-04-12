package se.gu.featuredashboard.ui.views;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
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
import org.eclipse.ui.part.ViewPart;

import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.utils.SelectionHandler;

public class TreeView extends ViewPart {

	private TreeViewer fileViewer;

	private static final String TOOLTIP = "Search for files";

	@Override
	public void createPartControl(Composite parent) {

		/*--------------------FILE----------------------*/
		Group fileGroup = new Group(parent, SWT.NONE);
		fileGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		fileGroup.setLayout(new GridLayout(1, false));

		Text filter = new Text(fileGroup, SWT.BORDER);
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

		fileViewer = new TreeViewer(fileGroup);
		fileViewer.setContentProvider(new ITreeContentProvider() {
			@Override
			public Object[] getElements(Object inputElement) {
				return (Object[]) inputElement;
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				FeatureContainer fc = (FeatureContainer) parentElement;
				return fc.getFiles().toArray();
			}

			@Override
			public Object getParent(Object element) {
				if (element instanceof IFile) {
					IFile file = (IFile) element;
					return ProjectStore.getActiveProject().getFeatureContainers().stream().filter(fc -> {
						return fc.isAnnotatedIn(file) || fc.isMappedIn(file);
					}).findFirst().get();
				}
				return null;
			}

			@Override
			public boolean hasChildren(Object element) {
				if (element instanceof IFile)
					return false;
				FeatureContainer fc = (FeatureContainer) element;
				return !fc.getFiles().isEmpty();
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
					return ((FeatureContainer) element).getFeature().getFeatureID();
				}
			}
		});
		fileViewer.setFilters(viewerFilter);

		inputToView(SelectionHandler.getSelection());
	}

	public void inputToView(List<FeatureContainer> selection) {
		Display.getDefault().asyncExec(() -> {
			fileViewer.setInput(selection.toArray());
		});

	}

	@Override
	public void setFocus() {
	}

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
				for (IFile file : ((FeatureContainer) element).getFiles()) {
					if (select(viewer, parentElement, file)) {
						return true;
					}
				}
			}

			return false;
		}

	}

}
