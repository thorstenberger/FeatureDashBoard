package se.gu.featuredashboard.utils;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

public class FeatureMetricsComparator extends ViewerComparator {
	
	private int propertyIndex = 0;
	private int column;
	
	private static final int DESCENDING = 1;
    private int direction = DESCENDING;
	
	public void setColumn(int column) {
		this.column = column;
        if (column == this.propertyIndex) {
            // Same column as last sort; toggle the direction
            direction = 1 - direction;
        } else {
            // New column; do an ascending sort
            this.propertyIndex = column;
            direction = DESCENDING;
        }
    }

	public int getDirection() {
        return direction == 1 ? SWT.DOWN : SWT.UP;
    }
	
	@Override
	public int compare(Viewer viewer, Object obj1, Object obj2) {
		System.out.println(obj1.toString());
		System.out.println(obj2.toString());

		return super.compare(viewer, obj1, obj2);
	
	}
	
}