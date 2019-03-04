package se.gu.featuredashboard.utils;

import java.util.Comparator;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.ui.views.FeatureMetricsView;

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
	
	private int compareStrings(String s1, String s2) {
		int result = s1.compareTo(s2);
		
		if(result > 0) {
			return 1;
		} else if(result < 0) {
			return -1;
		} else {
			return 0;
		}
	}
	
	@Override
	public int compare(Viewer viewer, Object obj1, Object obj2) {
		FeatureContainer feature1 = (FeatureContainer) obj1;
		FeatureContainer feature2 = (FeatureContainer) obj2;

		int result = 0;
		
		switch (column) {
			case 1:
				result = compareStrings(feature1.getFeature().getFeatureID(), feature2.getFeature().getFeatureID());
				break;
			case 2:
				result = Integer.compare(feature1.getLinesOfFeatureCode(), feature2.getLinesOfFeatureCode());
				break;
			case 3:
				result = Integer.compare(feature1.getNumberOfInFileAnnotations(), feature2.getNumberOfInFileAnnotations());
				break;
			case 4:
				break;
			case 5:
				result = Integer.compare(feature1.getTanglingDegree(), feature2.getTanglingDegree());
				break;
			case 6:
				result = Integer.compare(feature1.getScatteringDegree(), feature2.getScatteringDegree());
				break;
			default:
				break;
		}
		
		if(direction == DESCENDING) {
			result = -result;
		}
		
		return result;
	
	}
	
}