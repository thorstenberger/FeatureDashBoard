package se.gu.featuredashboard.ui.views;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import se.gu.featuredashboard.model.featuremodel.Project;
import se.gu.featuredashboard.ui.listeners.TableSelectionListener;
import se.gu.featuredashboard.ui.providers.MetricsTableContentProvider;
import se.gu.featuredashboard.ui.providers.MetricsTableLabelProvider;
import se.gu.featuredashboard.utils.MetricsComparator;

public class ProjectMetricsView extends ViewPart {
	
	private TableViewer projectViewer;
	private Table projectTable;
	
	public static final String COLUMN_1_NAME = "Project";
	private static final String COLUMN_1_TOOLTIP = "Current selected project";
	
	public static final String COLUMN_2_NAME = "Features";
	private static final String COLUMN_2_TOOLTIP = "How many distict features does this project contain";
	
	public static final String COLUMN_3_NAME = "Total LoFC";
	private static final String COLUMN_3_TOOLTIP = "Total lines of feature code";
	
	public static final String COLUMN_4_NAME = "Avg LoFC";
	private static final String COLUMN_4_TOOLTIP = "Average lines of feature code";
	
	public static final String COLUMN_5_NAME = "Avg Nesting Degree";
	private static final String COLUMN_5_TOOLTIP = "Average nesting degree";
	
	public static final String ID = "ProjectTable";
	
	@Override
	public void createPartControl(Composite parent) {
		projectViewer = new TableViewer(parent, SWT.NONE);
		projectViewer.setContentProvider(new MetricsTableContentProvider());
		projectViewer.setLabelProvider(new MetricsTableLabelProvider());
		
		MetricsComparator comparator = new MetricsComparator();
		projectViewer.setComparator(comparator);
		
		TableSelectionListener tableSelectionListener = new TableSelectionListener(projectViewer, comparator, ID);
		
		projectTable = projectViewer.getTable();
		projectTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		TableColumn column = new TableColumn(projectTable, SWT.LEFT);
		column.setText(COLUMN_1_NAME);
		column.setToolTipText(COLUMN_1_TOOLTIP);
		column.setWidth(100);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(projectTable, SWT.LEFT);
		column.setText(COLUMN_2_NAME);
		column.setToolTipText(COLUMN_2_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(projectTable, SWT.LEFT);
		column.setText(COLUMN_3_NAME);
		column.setToolTipText(COLUMN_3_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(projectTable, SWT.LEFT);
		column.setText(COLUMN_4_NAME);
		column.setToolTipText(COLUMN_4_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		column = new TableColumn(projectTable, SWT.LEFT);
		column.setText(COLUMN_5_NAME);
		column.setToolTipText(COLUMN_5_TOOLTIP);
		column.addSelectionListener(tableSelectionListener);
		
		// Pack the columns
	    for (int i = 1, n = projectTable.getColumnCount(); i < n; i++) {
	      projectTable.getColumn(i).pack();
	    }
		
		projectTable.setHeaderVisible(true);
		projectTable.setLinesVisible(true);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	public void inputToView(Project project) {
		projectViewer.setInput(new Object[] {project});
	}
	
}
