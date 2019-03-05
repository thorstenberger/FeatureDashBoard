package se.gu.featuredashboard.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import se.gu.featuredashboard.model.featuremodel.Project;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.ui.listeners.TableSelectionListener;
import se.gu.featuredashboard.ui.providers.MetricsTableContentProvider;
import se.gu.featuredashboard.ui.providers.MetricsTableLabelProvider;
import se.gu.featuredashboard.utils.MetricsComparator;

public class FeatureMetricsView extends ViewPart {
	
	private TableViewer featureViewer;
	private TableViewer projectViewer;
	private Table projectTable;
	private Table featureTable;
	
	public static final String COLUMN_1_NAME = "Feature";
	private static final String COLUMN_1_TOOLTIP = "The feature of interest";
	
	public static final String COLUMN_2_NAME = "LOFC";
	private static final String COLUMN_2_TOOLTIP = "How many annotated lines for this feature";
	
	public static final String COLUMN_3_NAME = "NoFiA";
	private static final String COLUMN_3_TOOLTIP = "How many in-file annotations for this feature";
	
	public static final String COLUMN_4_NAME = "NoFoA";
	private static final String COLUMN_4_TOOLTIP = "How many folder annotations for this feature";
	
	public static final String COLUMN_5_NAME = "Tangling Degree";
	private static final String COLUMN_5_TOOLTIP = "How many other features share the same artefact as this feature";
	
	public static final String COLUMN_6_NAME = "Scattering Degree";
	private static final String COLUMN_6_TOOLTIP = "Across how many artefacts is this feature implemented in";
	
	public static final String COLUMN_7_NAME = "Max Nesting degree";
	private static final String COLUMN_7_TOOLTIP = "Max nesting (folder) degree of annotations";
	
	public static final String COLUMN_8_NAME = "Avg Nesting degree";
	private static final String COLUMN_8_TOOLTIP = "Avg esting (folder) degree of annotations";
	
	public static final String COLUMN_9_NAME = "Min Nesting degree";
	private static final String COLUMN_9_TOOLTIP = "Min esting (folder) degree of annotations";
	
	public static final String ID = "FeatureTable";
	
	@Override
	public void createPartControl(Composite parent) {
		MetricsComparator comparator = new MetricsComparator();
		
		featureViewer = new TableViewer(parent, SWT.NONE);
		featureViewer.setContentProvider(new MetricsTableContentProvider());
		featureViewer.setLabelProvider(new MetricsTableLabelProvider());
		featureViewer.setComparator(comparator);
		
		TableSelectionListener tableSelectionListener = new TableSelectionListener(featureViewer, comparator, ID);
		
		featureTable = featureViewer.getTable();
		featureTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		TableColumn column = new TableColumn(featureTable, SWT.LEFT);
		column.setText(COLUMN_1_NAME);
		column.setToolTipText(COLUMN_1_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		column.setWidth(100);
		
		column = new TableColumn(featureTable, SWT.RIGHT);
		column.setText(COLUMN_2_NAME);
		column.setToolTipText(COLUMN_2_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(featureTable, SWT.LEFT);
		column.setText(COLUMN_3_NAME);
		column.setToolTipText(COLUMN_3_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(featureTable, SWT.LEFT);
		column.setText(COLUMN_4_NAME);
		column.setToolTipText(COLUMN_4_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(featureTable, SWT.LEFT);
		column.setText(COLUMN_5_NAME);
		column.setToolTipText(COLUMN_5_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(featureTable, SWT.LEFT);
		column.setText(COLUMN_6_NAME);
		column.setToolTipText(COLUMN_6_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(featureTable, SWT.LEFT);
		column.setText(COLUMN_7_NAME);
		column.setToolTipText(COLUMN_7_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(featureTable, SWT.LEFT);
		column.setText(COLUMN_8_NAME);
		column.setToolTipText(COLUMN_8_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(featureTable, SWT.LEFT);
		column.setText(COLUMN_9_NAME);
		column.setToolTipText(COLUMN_9_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		// Pack the columns
	    for (int i = 1, n = featureTable.getColumnCount(); i < n; i++) {
	      featureTable.getColumn(i).pack();
	    }

		featureTable.setHeaderVisible(true);
		featureTable.setLinesVisible(true);
	}

	public void inputToView(Project project) {
		featureViewer.setInput(project.getFeatureInformation().toArray());
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}