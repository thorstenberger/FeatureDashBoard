package se.gu.featuredashboard.ui.views;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import se.gu.featuredashboard.model.featuremodel.Project;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.ui.listeners.TableSelectionListener;
import se.gu.featuredashboard.ui.providers.MetricsTableLabelProvider;
import se.gu.featuredashboard.utils.FeaturedashboardConstants;
import se.gu.featuredashboard.utils.IUpdateInformationListener;
import se.gu.featuredashboard.utils.MetricsComparator;

public class FeatureMetricsView extends ViewPart implements IUpdateInformationListener {
	
	private TableViewer featureViewer;
	private Table featureTable;
	
	@Override
	public void createPartControl(Composite parent) {
		MetricsComparator comparator = new MetricsComparator();
		
		featureViewer = new TableViewer(parent, SWT.NONE);
		featureViewer.setContentProvider(ArrayContentProvider.getInstance());
		featureViewer.setLabelProvider(new MetricsTableLabelProvider());
		featureViewer.setComparator(comparator);
		
		TableSelectionListener tableSelectionListener = new TableSelectionListener(featureViewer, comparator, FeaturedashboardConstants.FEATURETABLE_ID);
		
		featureTable = featureViewer.getTable();
		featureTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		TableColumn column = new TableColumn(featureTable, SWT.LEFT);
		column.setText(FeaturedashboardConstants.FEATURETABLE_COLUMN_1_NAME);
		column.setToolTipText(FeaturedashboardConstants.FEATURETABLE_COLUMN_1_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		column.setWidth(FeaturedashboardConstants.FEATURETABLE_COLUMN_1_SIZE);
		
		column = new TableColumn(featureTable, SWT.RIGHT);
		column.setText(FeaturedashboardConstants.FEATURETABLE_COLUMN_2_NAME);
		column.setToolTipText(FeaturedashboardConstants.FEATURETABLE_COLUMN_2_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(featureTable, SWT.LEFT);
		column.setText(FeaturedashboardConstants.FEATURETABLE_COLUMN_3_NAME);
		column.setToolTipText(FeaturedashboardConstants.FEATURETABLE_COLUMN_3_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(featureTable, SWT.LEFT);
		column.setText(FeaturedashboardConstants.FEATURETABLE_COLUMN_4_NAME);
		column.setToolTipText(FeaturedashboardConstants.FEATURETABLE_COLUMN_4_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(featureTable, SWT.LEFT);
		column.setText(FeaturedashboardConstants.FEATURETABLE_COLUMN_5_NAME);
		column.setToolTipText(FeaturedashboardConstants.FEATURETABLE_COLUMN_5_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(featureTable, SWT.LEFT);
		column.setText(FeaturedashboardConstants.FEATURETABLE_COLUMN_6_NAME);
		column.setToolTipText(FeaturedashboardConstants.FEATURETABLE_COLUMN_6_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(featureTable, SWT.LEFT);
		column.setText(FeaturedashboardConstants.FEATURETABLE_COLUMN_7_NAME);
		column.setToolTipText(FeaturedashboardConstants.FEATURETABLE_COLUMN_7_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(featureTable, SWT.LEFT);
		column.setText(FeaturedashboardConstants.FEATURETABLE_COLUMN_8_NAME);
		column.setToolTipText(FeaturedashboardConstants.FEATURETABLE_COLUMN_8_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(featureTable, SWT.LEFT);
		column.setText(FeaturedashboardConstants.FEATURETABLE_COLUMN_9_NAME);
		column.setToolTipText(FeaturedashboardConstants.FEATURETABLE_COLUMN_9_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		// Pack the columns
	    for (int i = 1, n = featureTable.getColumnCount(); i < n; i++) {
	      featureTable.getColumn(i).pack();
	    }

		featureTable.setHeaderVisible(true);
		featureTable.setLinesVisible(true);
		
		updateData();
	}

	public void updateData() {
		Display.getDefault().asyncExec(() -> {
			Project activeProject = ProjectStore.getActiveProject();
			if(activeProject != null)
				featureViewer.setInput(activeProject.getFeatureContainers().toArray());
		});
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}