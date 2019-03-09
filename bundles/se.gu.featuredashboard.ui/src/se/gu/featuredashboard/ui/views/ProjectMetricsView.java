	package se.gu.featuredashboard.ui.views;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.ui.listeners.TableSelectionListener;
import se.gu.featuredashboard.ui.providers.MetricsTableContentProvider;
import se.gu.featuredashboard.ui.providers.MetricsTableLabelProvider;
import se.gu.featuredashboard.utils.FeaturedashboardConstants;
import se.gu.featuredashboard.utils.IUpdateViewListener;
import se.gu.featuredashboard.utils.MetricsComparator;

public class ProjectMetricsView extends ViewPart implements IUpdateViewListener {
	
	private TableViewer projectViewer;
	private Table projectTable;
	
	@Override
	public void createPartControl(Composite parent) {
		projectViewer = new TableViewer(parent, SWT.NONE);
		projectViewer.setContentProvider(new MetricsTableContentProvider());
		projectViewer.setLabelProvider(new MetricsTableLabelProvider());
		
		MetricsComparator comparator = new MetricsComparator();
		projectViewer.setComparator(comparator);
		
		TableSelectionListener tableSelectionListener = new TableSelectionListener(projectViewer, comparator, FeaturedashboardConstants.PROJECTTABLE_ID);
		
		projectTable = projectViewer.getTable();
		projectTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		TableColumn column = new TableColumn(projectTable, SWT.LEFT);
		column.setText(FeaturedashboardConstants.PROJECTTABLE_COLUMN_1_NAME);
		column.setToolTipText(FeaturedashboardConstants.PROJECTTABLE_COLUMN_1_TOOLTIP);
		column.setWidth(100);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(projectTable, SWT.LEFT);
		column.setText(FeaturedashboardConstants.PROJECTTABLE_COLUMN_2_NAME);
		column.setToolTipText(FeaturedashboardConstants.PROJECTTABLE_COLUMN_2_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(projectTable, SWT.LEFT);
		column.setText(FeaturedashboardConstants.PROJECTTABLE_COLUMN_3_NAME);
		column.setToolTipText(FeaturedashboardConstants.PROJECTTABLE_COLUMN_3_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(projectTable, SWT.LEFT);
		column.setText(FeaturedashboardConstants.PROJECTTABLE_COLUMN_4_NAME);
		column.setToolTipText(FeaturedashboardConstants.PROJECTTABLE_COLUMN_4_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(projectTable, SWT.LEFT);
		column.setText(FeaturedashboardConstants.PROJECTTABLE_COLUMN_5_NAME);
		column.setToolTipText(FeaturedashboardConstants.PROJECTTABLE_COLUMN_5_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(projectTable, SWT.LEFT);
		column.setText(FeaturedashboardConstants.PROJECTTABLE_COLUMN_6_NAME);
		column.setToolTipText(FeaturedashboardConstants.PROJECTTABLE_COLUMN_6_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		// Pack the columns
	    for (int i = 1, n = projectTable.getColumnCount(); i < n; i++) {
	      projectTable.getColumn(i).pack();
	    }
		
		projectTable.setHeaderVisible(true);
		projectTable.setLinesVisible(true);
		
		updateView();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	public void updateView() {
		Display.getDefault().asyncExec(() -> {
			projectViewer.setInput(ProjectStore.getAll().toArray());
		});
	}
}
