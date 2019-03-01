package se.gu.featuredashboard.ui.views;

import java.util.ArrayList;

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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import se.gu.featuredashboard.ui.viewscontroller.MainViewController;

public class MainView extends ViewPart {

	private MainViewController controller = new MainViewController();

	private Group resourcesGroup;
	private DirectoryDialog directoryDialog;
	private FileDialog fileDialog;
	private Label lblFeatureFolder;
	private Text txtFeatureFolder;
	private Button btnFeatureFolderBrowse;
	private Label lblFeatureFile;
	private Text txtFeatureFile;
	private Button btnFeatureFileBrowse;
	private Label lblAnnotatedFilesDirectory;
	private Text txtAnnotatedFilesDirectory;
	private Button btnAnnotatedFilesDirectory;
	private Button btnEnterResources;

	private Group terminalsGroup;
	private Group featuresSelectionGroup;
	private Button btnSelectAllFeatures;
	private Button btnDeselectAllFeatures;
	private List featuresList;
	private Group folderSelectionGroup;
	private Button btnSelectAllFolders;
	private Button btnDeselectAllFolders;
	private Group nonAnnotatedFilesSelectionGroup;
	private List foldersList;
	private Button btnSelectAllNonAnnotatedFiles;
	private Button btnDeselectAllNonAnnotatedFiles;
	private List nonAnnotatedFilesList;
	private Group annotatedFilesSelectionGroup;
	private Button btnSelectAllAnnotatedFiles;
	private Button btnDeselectAllAnnotatedFiles;
	private List annotatedFilesList;

	private Group traceGroup ;
	private Table table;
	private TableColumn column;
	private TableItem item;
	private GridLayout gridLayout;
	private GridData gridData;

	@Override
	public void createPartControl(Composite parent) {
		gridLayout = new GridLayout(1, false);
		parent.setLayout(gridLayout);

		setupFilesGroup(parent);
		setupTerminalsGroup(parent);
		setupTracesGroup(parent);
	}

	private void setupFilesGroup(Composite parent){
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.BORDER|SWT.H_SCROLL | SWT.V_SCROLL);


		resourcesGroup = new Group(sc, SWT.NONE);
		gridData = new GridData(GridData.FILL, GridData.FILL,true, true);
		resourcesGroup.setLayoutData(gridData);
		resourcesGroup.setText("Resourses:");
		gridLayout = new GridLayout(3, false);
		resourcesGroup.setLayout(gridLayout);

		sc.setContent(resourcesGroup);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		//********************************************FeatureFolder*********************************************
		lblFeatureFolder = new Label(resourcesGroup, SWT.NONE);
		lblFeatureFolder.setText("Feature-to-Folder Traces:");
		txtFeatureFolder = new Text(resourcesGroup, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		txtFeatureFolder.setLayoutData(gridData);
		txtFeatureFolder.setEnabled(false);

	    btnFeatureFolderBrowse = new Button(resourcesGroup, SWT.PUSH);
	    btnFeatureFolderBrowse.setText("Browse...");
		btnFeatureFolderBrowse.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fileDialog = new FileDialog(resourcesGroup.getShell());
				fileDialog.setFilterPath(txtFeatureFolder.getText());
				fileDialog.setText("Select feature-folder");
				String selectedFile = fileDialog.open();
				if (selectedFile != null) {
					txtFeatureFolder.setText(selectedFile);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		//****************************************************************************************************

		//********************************************FeatureFile*********************************************
		lblFeatureFile = new Label(resourcesGroup, SWT.NONE);
		lblFeatureFile.setText("Feature-to-File Traces:");
		txtFeatureFile = new Text(resourcesGroup, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		txtFeatureFile.setLayoutData(gridData);
		txtFeatureFile.setEnabled(false);

	    btnFeatureFileBrowse = new Button(resourcesGroup, SWT.PUSH);
	    btnFeatureFileBrowse.setText("Browse...");
	    btnFeatureFileBrowse.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fileDialog = new FileDialog(resourcesGroup.getShell());
				fileDialog.setFilterPath(txtFeatureFile.getText());
				fileDialog.setText("Select feature-file");
				String selectedFile = fileDialog.open();
				if (selectedFile != null) {
					txtFeatureFile.setText(selectedFile);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
	  //********************************************************************************************************

	    //********************************************Annotated Files*********************************************
		lblAnnotatedFilesDirectory = new Label(resourcesGroup, SWT.NONE);
		lblAnnotatedFilesDirectory.setText("Annotated Files Directory:");
		txtAnnotatedFilesDirectory = new Text(resourcesGroup, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		txtAnnotatedFilesDirectory.setLayoutData(gridData);
		txtAnnotatedFilesDirectory.setEnabled(false);

	    btnAnnotatedFilesDirectory = new Button(resourcesGroup, SWT.PUSH);
	    btnAnnotatedFilesDirectory.setText("Browse...");
	    btnAnnotatedFilesDirectory.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				directoryDialog = new DirectoryDialog(resourcesGroup.getShell());
				directoryDialog.setFilterPath(txtAnnotatedFilesDirectory.getText());
				directoryDialog.setText("Select annotated files directory");
				String selectedDirectory = directoryDialog.open();
				if (selectedDirectory != null) {
					txtAnnotatedFilesDirectory.setText(selectedDirectory);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
	    //********************************************************************************************************

		btnEnterResources = new Button(resourcesGroup, SWT.PUSH);
		btnEnterResources.setText("Enter Resources");
		gridData = new GridData(GridData.END, GridData.CENTER, false, true);
		gridData.horizontalSpan = 3;
		btnEnterResources.setLayoutData(gridData);
		btnEnterResources.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("set resources is working!");

				controller.setFeatureFolderAddress(txtFeatureFolder.getText());
				controller.setFeatureFileAddress(txtFeatureFile.getText());
				controller.setAnnotatedFilesDirectory(txtAnnotatedFilesDirectory.getText());
				controller.prepareData();

				featuresList.removeAll();
				for(String aFeature:controller.getAllFeaureIDs())
					featuresList.add(aFeature);

				foldersList.removeAll();
				for(String aFolder:controller.getAllFolderAddresses())
					foldersList.add(aFolder);

				nonAnnotatedFilesList.removeAll();
				for(String aNonAnnotatedFile:controller.getAllNonAnnotatedFilesAddresses())
					nonAnnotatedFilesList.add(aNonAnnotatedFile);

				annotatedFilesList.removeAll();
				for(String aAnnotatedFile:controller.getAllAnnotatedFilesAddresses())
					annotatedFilesList.add(aAnnotatedFile);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

	}

	private void setupTerminalsGroup(Composite parent){
		terminalsGroup = new Group(parent, SWT.NONE);
		gridData = new GridData(GridData.FILL, GridData.FILL,true, true);
		terminalsGroup.setLayoutData(gridData);
		terminalsGroup.setText("Terminals:");
		gridLayout = new GridLayout(4, false);
		terminalsGroup.setLayout(gridLayout);

		//***************************************Feature Selection Group******************************************
		featuresSelectionGroup = new Group(terminalsGroup, SWT.NONE);
		btnSelectAllFeatures = new Button(featuresSelectionGroup, SWT.CHECK);
		btnDeselectAllFeatures = new Button(featuresSelectionGroup, SWT.CHECK);
		featuresList = new List(featuresSelectionGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);

		gridData = new GridData(GridData.FILL, GridData.FILL,true, true);
		featuresSelectionGroup.setLayoutData(gridData);
		featuresSelectionGroup.setText("Features:");
		gridLayout = new GridLayout(1,true);
		featuresSelectionGroup.setLayout(gridLayout);

		btnSelectAllFeatures.setText("Select All");
		btnSelectAllFeatures.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnSelectAllFeatures.getSelection()){
					btnDeselectAllFeatures.setSelection(false);
					featuresList.selectAll();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		btnDeselectAllFeatures.setText("Deselect All");
		btnDeselectAllFeatures.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnDeselectAllFeatures.getSelection()){
					btnSelectAllFeatures.setSelection(false);
					featuresList.deselectAll();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 1;
		int listHeight = featuresList.getItemHeight() * 10;
		Rectangle trim = featuresList.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
		featuresList.setLayoutData(gridData);
		featuresList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(featuresList.getSelectionCount() != featuresList.getItemCount())
					btnSelectAllFeatures.setSelection(false);
				if(featuresList.getSelectionCount() != 0)
					btnDeselectAllFeatures.setSelection(false);

			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		//**************************************************************************************************

		//***************************************Folder Selection Group*************************************
		folderSelectionGroup = new Group(terminalsGroup, SWT.NONE);
		btnSelectAllFolders = new Button(folderSelectionGroup, SWT.CHECK);
		btnDeselectAllFolders = new Button(folderSelectionGroup, SWT.CHECK);
		foldersList = new List(folderSelectionGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);

		gridData = new GridData(GridData.FILL, GridData.FILL,true, true);
		folderSelectionGroup.setLayoutData(gridData);
		folderSelectionGroup.setText("Folders:");
		gridLayout = new GridLayout(1,true);
		folderSelectionGroup.setLayout(gridLayout);

		btnSelectAllFolders.setText("Select All");
		btnSelectAllFolders.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnSelectAllFolders.getSelection()){
					btnDeselectAllFolders.setSelection(false);
					foldersList.selectAll();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		btnDeselectAllFolders.setText("Deselect All");
		btnDeselectAllFolders.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnDeselectAllFolders.getSelection()){
					btnSelectAllFolders.setSelection(false);
					foldersList.deselectAll();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 1;
		listHeight = featuresList.getItemHeight() * 10;
		trim = featuresList.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
		foldersList.setLayoutData(gridData);
		foldersList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(foldersList.getSelectionCount() != foldersList.getItemCount())
					btnSelectAllFolders.setSelection(false);
				if(foldersList.getSelectionCount() != 0)
					btnDeselectAllFolders.setSelection(false);

			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		//**************************************************************************************************

		//***************************************Non-Annotated Files Selection Group****************************
		nonAnnotatedFilesSelectionGroup = new Group(terminalsGroup, SWT.NONE);
		btnSelectAllNonAnnotatedFiles = new Button(nonAnnotatedFilesSelectionGroup, SWT.CHECK);
		btnDeselectAllNonAnnotatedFiles = new Button(nonAnnotatedFilesSelectionGroup, SWT.CHECK);
		nonAnnotatedFilesList = new List(nonAnnotatedFilesSelectionGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);

		gridData = new GridData(GridData.FILL, GridData.FILL,true, true);
		nonAnnotatedFilesSelectionGroup.setLayoutData(gridData);
		nonAnnotatedFilesSelectionGroup.setText("Non-Annotated Files:");
		gridLayout = new GridLayout(1,true);
		nonAnnotatedFilesSelectionGroup.setLayout(gridLayout);



		btnSelectAllNonAnnotatedFiles.setText("Select All");
		btnSelectAllNonAnnotatedFiles.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnSelectAllNonAnnotatedFiles.getSelection()){
					btnDeselectAllNonAnnotatedFiles.setSelection(false);
					nonAnnotatedFilesList.selectAll();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});


		btnDeselectAllNonAnnotatedFiles.setText("Deselect All");
		btnDeselectAllNonAnnotatedFiles.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnDeselectAllNonAnnotatedFiles.getSelection()){
					btnSelectAllNonAnnotatedFiles.setSelection(false);
					nonAnnotatedFilesList.deselectAll();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 1;
		listHeight = featuresList.getItemHeight() * 10;
		trim = featuresList.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
		nonAnnotatedFilesList.setLayoutData(gridData);
		nonAnnotatedFilesList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(nonAnnotatedFilesList.getSelectionCount() != nonAnnotatedFilesList.getItemCount())
					btnSelectAllNonAnnotatedFiles.setSelection(false);
				if(nonAnnotatedFilesList.getSelectionCount() != 0)
					btnDeselectAllNonAnnotatedFiles.setSelection(false);

			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		//**********************************************************************************************************

		//***************************************Annotated Files Selection Group************************************
		annotatedFilesSelectionGroup = new Group(terminalsGroup, SWT.NONE);
		btnSelectAllAnnotatedFiles = new Button(annotatedFilesSelectionGroup, SWT.CHECK);
		btnDeselectAllAnnotatedFiles = new Button(annotatedFilesSelectionGroup, SWT.CHECK);
		annotatedFilesList = new List(annotatedFilesSelectionGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);

		gridData = new GridData(GridData.FILL, GridData.FILL,true, true);
		annotatedFilesSelectionGroup.setLayoutData(gridData);
		annotatedFilesSelectionGroup.setText("Annotated Files:");
		gridLayout = new GridLayout(1,true);
		annotatedFilesSelectionGroup.setLayout(gridLayout);


		btnSelectAllAnnotatedFiles.setText("Select All");
		btnSelectAllAnnotatedFiles.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnSelectAllAnnotatedFiles.getSelection()){
					btnDeselectAllAnnotatedFiles.setSelection(false);
					annotatedFilesList.selectAll();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});


		btnDeselectAllAnnotatedFiles.setText("Deselect All");
		btnDeselectAllAnnotatedFiles.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnDeselectAllAnnotatedFiles.getSelection()){
					btnSelectAllAnnotatedFiles.setSelection(false);
					annotatedFilesList.deselectAll();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 1;
		listHeight = featuresList.getItemHeight() * 10;
		trim = featuresList.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
		annotatedFilesList.setLayoutData(gridData);
		annotatedFilesList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(annotatedFilesList.getSelectionCount() != annotatedFilesList.getItemCount())
					btnSelectAllAnnotatedFiles.setSelection(false);
				if(annotatedFilesList.getSelectionCount() != 0)
					btnDeselectAllAnnotatedFiles.setSelection(false);

			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		//**********************************************************************************************************

		Button enter = new Button(terminalsGroup, SWT.PUSH);
		enter.setText("Show Traces");
		gridData = new GridData(GridData.END, GridData.CENTER, false, false);
		gridData.horizontalSpan = 4;
		enter.setLayoutData(gridData);
		enter.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("button is working!");
				ArrayList<String> traces = controller.getAllTraces(controller.getAllFeaureIDs(), controller.getAllFolderAddresses(),
												controller.getAllNonAnnotatedFilesAddresses(), controller.getAllAnnotatedFilesAddresses());
				updateTraces(traces);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

	}

	private void setupTracesGroup(Composite parent){
		traceGroup = new Group(parent, SWT.NONE);
		gridData = new GridData(GridData.FILL, GridData.FILL,true, true);
		traceGroup.setLayoutData(gridData);
		traceGroup.setText("Traces:");
		gridLayout = new GridLayout(1,true);
		traceGroup.setLayout(gridLayout);

	    table = new Table(traceGroup, SWT.BORDER );
	    gridData = new GridData(GridData.FILL, GridData.FILL,true, true);
	    table.setLayoutData(gridData);
	    table.setLinesVisible(true);
	    table.setHeaderVisible(true);


	}

	private void updateTraces(ArrayList<String> traces){
	    String[] titles = { "Feature", "Folder", "File", "Line Number(s)" };
	    for (int i = 0; i < titles.length; i++) {
	      column = new TableColumn(table, SWT.NONE);
	      column.setText(titles[i]);
	    }

	    for (int i = 0; i < traces.size(); i++) {
	        item = new TableItem(table, SWT.NONE);
	        item.setText(0, traces.get(i).split("\\s+")[0]);
	        item.setText(1, traces.get(i).split("\\s+")[1]);
	        item.setText(2, traces.get(i).split("\\s+")[2]);
	        item.setText(3, traces.get(i).split("\\s+")[3]);
	      }

	    table.setSize(table.computeSize(SWT.DEFAULT, 200));

	    for (int i=0; i<titles.length; i++) {
	        table.getColumn (i).pack ();
	      }
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
