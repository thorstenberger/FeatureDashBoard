package se.gu.featuredashboard.ui.providers;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.model.featuremodel.Project;

public class MetricsTableLabelProvider implements ITableLabelProvider {
	
	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if(element instanceof FeatureContainer) {
			FeatureContainer featureContainer = (FeatureContainer) element;
			
			switch (columnIndex) {
				case 0:
					return featureContainer.getFeature().getFeatureID();
				case 1:
					return Integer.toString(featureContainer.getLinesOfFeatureCode());
				case 2:
					return Integer.toString(featureContainer.getNumberOfFileAnnotations());
				case 3:
					return Integer.toString(featureContainer.getNumberOfFolderAnnotations());
				case 4:
					return Integer.toString(featureContainer.getTanglingDegree());
				case 5:
					return Integer.toString(featureContainer.getScatteringDegree());
				case 6:
					return Integer.toString(featureContainer.getMaxND());
				case 7:
					return featureContainer.getAvgND();
				case 8:
					return Integer.toString(featureContainer.getMinND());
				default:
					return "Error";
			}
		} else if(element instanceof Project) {
			Project project = (Project) element;
			
			switch (columnIndex) {
			case 0:
				return project.getID();
			case 1:
				return Integer.toString(project.getNumberOfFeatures());
			case 2:
				return Integer.toString(project.getTotalLoFC());
			case 3:
				return project.getAvgLoFC();
			case 4:
				return project.getAverageND();
			case 5:
				return project.getAverageSD();
			default:
				return "Error";
		}
		} else {
			return element.toString();
		}
				
	}	

}	