package se.gu.featuredashboard.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import se.gu.featuredashboard.model.featuremodel.Project;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.ui.listeners.TableSelectionListener;
import se.gu.featuredashboard.ui.providers.FeatureMetricsContentProvider;
import se.gu.featuredashboard.ui.providers.FeatureMetricsLabelProvider;
import se.gu.featuredashboard.utils.FeatureMetricsComparator;

public class FeatureMetricsView extends ViewPart {
	
	private TableViewer tableViewer;
	private Table table;
	
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
	
	//private static final String COLUMN_1_NAME = "ND";
	//private static final String COLUMN_1_NAME = "NoAu";
	
	@Override
	public void createPartControl(Composite parent) {
		tableViewer = new TableViewer(parent, SWT.NONE);
		tableViewer.setContentProvider(new FeatureMetricsContentProvider());
		tableViewer.setLabelProvider(new FeatureMetricsLabelProvider());
		
		FeatureMetricsComparator comparator = new FeatureMetricsComparator();
		
		tableViewer.setComparator(comparator);
		
		TableSelectionListener tableSelectionListener = new TableSelectionListener(tableViewer, comparator);
		
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setText(COLUMN_1_NAME);
		column.setToolTipText(COLUMN_1_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		column.setWidth(100);
		
		column = new TableColumn(table, SWT.RIGHT);
		column.setText(COLUMN_2_NAME);
		column.setToolTipText(COLUMN_2_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(table, SWT.LEFT);
		column.setText(COLUMN_3_NAME);
		column.setToolTipText(COLUMN_3_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(table, SWT.LEFT);
		column.setText(COLUMN_4_NAME);
		column.setToolTipText(COLUMN_4_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(table, SWT.LEFT);
		column.setText(COLUMN_5_NAME);
		column.setToolTipText(COLUMN_5_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(table, SWT.LEFT);
		column.setText(COLUMN_6_NAME);
		column.setToolTipText(COLUMN_6_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		  // Pack the columns
	    for (int i = 1, n = table.getColumnCount(); i < n; i++) {
	      table.getColumn(i).pack();
	    }

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	public void inputToView(Project project) {
		tableViewer.setInput(project.getFeatureInformation().toArray());
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}