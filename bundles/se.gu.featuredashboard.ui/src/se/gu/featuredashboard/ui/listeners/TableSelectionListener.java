package se.gu.featuredashboard.ui.listeners;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TableColumn;

import se.gu.featuredashboard.utils.FeaturedashboardConstants;
import se.gu.featuredashboard.utils.MetricsComparator;

public class TableSelectionListener implements SelectionListener {

	private TableViewer tableViewer;
	private MetricsComparator comparator;
	private String id;
	
	public TableSelectionListener(TableViewer tableViewer, MetricsComparator comparator, String id) {
		this.tableViewer = tableViewer;
		this.comparator = comparator;
		this.id = id;
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		TableColumn column = (TableColumn) e.getSource();
		
		int columnToSort = 0;
		
		if(id.equals(FeaturedashboardConstants.FEATURETABLE_ID)) {
			switch(column.getText()) {
				case FeaturedashboardConstants.FEATURETABLE_COLUMN_1_NAME:
					columnToSort = 1;
					break;
				case FeaturedashboardConstants.FEATURETABLE_COLUMN_2_NAME:
					columnToSort = 2;
					break;
				case FeaturedashboardConstants.FEATURETABLE_COLUMN_3_NAME:
					columnToSort = 3;
					break;
				case FeaturedashboardConstants.FEATURETABLE_COLUMN_4_NAME:
					columnToSort = 4;
					break;
				case FeaturedashboardConstants.FEATURETABLE_COLUMN_5_NAME:
					columnToSort = 5;
					break;
				case FeaturedashboardConstants.FEATURETABLE_COLUMN_6_NAME:
					columnToSort = 6;
					break;
				case FeaturedashboardConstants.FEATURETABLE_COLUMN_7_NAME:
					columnToSort = 7;
					break;
				case FeaturedashboardConstants.FEATURETABLE_COLUMN_8_NAME:
					columnToSort = 8;
					break;
				case FeaturedashboardConstants.FEATURETABLE_COLUMN_9_NAME:
					columnToSort = 9;
					break;
			}
		} else if(id.equals(FeaturedashboardConstants.PROJECTTABLE_ID)) {
			switch(column.getText()) {
				case FeaturedashboardConstants.PROJECTTABLE_COLUMN_1_NAME:
					columnToSort = 1;
					break;
				case FeaturedashboardConstants.PROJECTTABLE_COLUMN_2_NAME:
					columnToSort = 2;
					break;
				case FeaturedashboardConstants.PROJECTTABLE_COLUMN_3_NAME:
					columnToSort = 3;
					break;
				case FeaturedashboardConstants.PROJECTTABLE_COLUMN_4_NAME:
					columnToSort = 4;
					break;
				case FeaturedashboardConstants.PROJECTTABLE_COLUMN_5_NAME:
					columnToSort = 5;
					break;
				case FeaturedashboardConstants.PROJECTTABLE_COLUMN_6_NAME:
					columnToSort = 6;
					break;
			}
		}
		
		comparator.setColumn(columnToSort);
		
		int dir = comparator.getDirection();
		tableViewer.getTable().setSortDirection(dir);
		tableViewer.getTable().setSortColumn(column);
		tableViewer.refresh();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
