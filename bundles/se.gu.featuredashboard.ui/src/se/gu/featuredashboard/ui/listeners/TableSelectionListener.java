package se.gu.featuredashboard.ui.listeners;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class TableSelectionListener implements SelectionListener {

	private TableViewer tableViewer;
	
	public TableSelectionListener(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		/*comparator.setColumn(2);
		int dir = comparator.getDirection();
		tableViewer.getTable().setSortDirection(dir);
		tableViewer.getTable().setSortColumn(column1);
		tableViewer.refresh();
		//System.out.println(e.getSource().toString());*/
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
