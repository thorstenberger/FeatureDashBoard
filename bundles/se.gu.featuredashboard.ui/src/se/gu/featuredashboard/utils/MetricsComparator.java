package se.gu.featuredashboard.utils;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.model.featuremodel.Project;

public class MetricsComparator extends ViewerComparator {
	
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
		int result = 0;
		
		if(obj1 instanceof FeatureContainer && obj2 instanceof FeatureContainer) {
			FeatureContainer feature1 = (FeatureContainer) obj1;
			FeatureContainer feature2 = (FeatureContainer) obj2;
	
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
					Object[] nestingInfo1 = feature1.getNestingInfo();
					Object[] nestingInfo2 = feature2.getNestingInfo();
					if(column == 7)
						result = Integer.compare((int) nestingInfo1[0], (int) nestingInfo2[0]);
					else if(column == 8)
						result = Double.compare(Double.parseDouble((String) nestingInfo1[3]), Double.parseDouble((String) nestingInfo2[3]));
					else
						result = Integer.compare((int) nestingInfo1[1], (int) nestingInfo2[1]);
			}
		} else if(obj1 instanceof Project && obj2 instanceof Project) {
			Project p1 = (Project) obj1;
			Project p2 = (Project) obj2;
			
			switch(column) {
				case 1:
					result = compareStrings(p1.getID(), p2.getID());
					break;
				case 2:
					result = Integer.compare(p1.getNumberOfFeatures(), p2.getNumberOfFeatures());
				case 3:
					result = Integer.compare(p1.getTotalLoFC(), p1.getTotalLoFC());
				case 4:
					result = Double.compare(Double.parseDouble(p1.getAvgLoFC()), Double.parseDouble(p2.getAvgLoFC()));
				case 5:
					result = Double.compare(Double.parseDouble(p1.getAverageND()), Double.parseDouble(p2.getAverageND()));
				case 6:
					result = Double.compare(Double.parseDouble(p1.getAverageSD()), Double.parseDouble(p2.getAverageSD()));
			}
		}
		
		if(direction == DESCENDING) {
			result = -result;
		}
		
		return result;
	
	}
	
}