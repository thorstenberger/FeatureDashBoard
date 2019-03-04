package se.gu.featuredashboard.ui.listeners;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TableColumn;

import se.gu.featuredashboard.ui.views.FeatureMetricsView;
import se.gu.featuredashboard.utils.FeatureMetricsComparator;

public class TableSelectionListener implements SelectionListener {

	private TableViewer tableViewer;
	private FeatureMetricsComparator comparator;
	
	public TableSelectionListener(TableViewer tableViewer, FeatureMetricsComparator comparator) {
		this.tableViewer = tableViewer;
		this.comparator = comparator;
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		TableColumn column = (TableColumn) e.getSource();
		
		int columnToSort = 0;
		
		switch(column.getText()) {
			case FeatureMetricsView.COLUMN_1_NAME:
				columnToSort = 1;
				break;
			case FeatureMetricsView.COLUMN_2_NAME:
				columnToSort = 2;
				break;
			case FeatureMetricsView.COLUMN_3_NAME:
				columnToSort = 3;
				break;
			case FeatureMetricsView.COLUMN_4_NAME:
				columnToSort = 4;
				break;
			case FeatureMetricsView.COLUMN_5_NAME:
				columnToSort = 5;
				break;
			case FeatureMetricsView.COLUMN_6_NAME:
				columnToSort = 6;
				break;
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
