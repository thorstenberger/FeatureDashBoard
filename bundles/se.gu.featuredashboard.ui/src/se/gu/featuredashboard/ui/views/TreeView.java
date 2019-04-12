package se.gu.featuredashboard.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.part.ViewPart;

import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.utils.SelectionHandler;

public class TreeView extends ViewPart {

	private TreeViewer fileViewer;
	private TreeViewer folderViewer;

	private Map<IContainer, List<FeatureContainer>> folderStructure = new HashMap<>();
	private Object root;

	@Override
	public void createPartControl(Composite parent) {

		CTabFolder tabFolder = new CTabFolder(parent, SWT.BOTTOM);
		tabFolder.setSimple(false);

		CTabItem fileTab = new CTabItem(tabFolder, SWT.NONE);
		fileTab.setText("Feature to file");

		CTabItem folderTab = new CTabItem(tabFolder, SWT.NONE);
		folderTab.setText("Folder to feature");

		/*--------------------FOLDER----------------------*/
		Group folderGroup = new Group(tabFolder, SWT.NONE);
		folderGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		folderGroup.setLayout(new GridLayout(1, false));
		folderTab.setControl(folderGroup);

		folderViewer = new TreeViewer(folderGroup);
		folderViewer.setContentProvider(new ITreeContentProvider() {
			@Override
			public Object[] getElements(Object inputElement) {
				Object[] inputElements = (Object[]) inputElement;
				if (inputElements.length == 0) {
					return inputElements;
				}
				List<FeatureContainer> elements = Arrays.stream(inputElements)
						.map(element -> (FeatureContainer) element).collect(Collectors.toList());

				for (Object element : elements) {
					FeatureContainer fc = (FeatureContainer) element;

					for (IFile file : fc.getFiles()) {
						IContainer parent = file.getParent();

						if (!folderStructure.containsKey(parent)) {
							getStructure(parent.getParent());
							List<FeatureContainer> fcList = new ArrayList<>();
							fcList.add(fc);

							folderStructure.put(parent, fcList);
						} else {
							System.out.println("Parent folder name:" + parent.getName());
							System.out.println("Put feature: " + fc.getFeature().getFeatureID());
							List<FeatureContainer> features = folderStructure.get(parent);
							if (features != null) {
								System.out.println("Features are not null");
								for (FeatureContainer featureContainers : features) {
									System.out.println(
											"ALready added features: " + featureContainers.getFeature().getFeatureID());
								}
								folderStructure.put(parent, features);
							}
						}
					}
				}

				return inputElements = new Object[] { root };
			}

			public void getStructure(IContainer container) {
				if (container instanceof IProject) {
					root = container;
				} else {
					folderStructure.put(container, null);
					getStructure(container.getParent());
				}
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				List<Object> elements = new ArrayList<>();

				IContainer container = (IContainer) parentElement;

				List<FeatureContainer> fcList = folderStructure.get(container);
				if (fcList != null) {
					elements.addAll(fcList);
				}
				try {
					for (IResource resource : container.members()) {
						if (resource instanceof IContainer) {
							if (folderStructure.containsKey((IContainer) resource)) {
								elements.add((IContainer) resource);
							}
						}
					}

				} catch (CoreException e) {
					e.printStackTrace();
				}

				return elements.toArray();
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
				if (element instanceof FeatureContainer)
					return false;
				else
					return true;
			}

		});
		folderViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		// folderViewer.setFilters(treeFilter);
		folderViewer.setLabelProvider(new ILabelProvider() {
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
				if (element instanceof IContainer) {
					return ((IContainer) element).getName();
				} else {
					return ((FeatureContainer) element).getFeature().getFeatureID();
				}
			}
		});

		/*--------------------FILE----------------------*/
		Group fileGroup = new Group(tabFolder, SWT.NONE);
		fileGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		fileGroup.setLayout(new GridLayout(1, false));
		fileTab.setControl(fileGroup);

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

		tabFolder.setSelection(fileTab);
		inputToView(SelectionHandler.getSelection());
	}

	public void inputToView(List<FeatureContainer> selection) {
		Display.getDefault().asyncExec(() -> {
			fileViewer.setInput(selection.toArray());
			folderViewer.setInput(selection.toArray());
		});

	}

	@Override
	public void setFocus() {
	}

}
