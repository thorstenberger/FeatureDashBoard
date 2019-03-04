package se.gu.featuredashboard.ui.providers;

import org.eclipse.jface.viewers.LabelProvider;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.featuremodel.FeatureContainer;

public class FeatureTableLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		
		if(element instanceof FeatureContainer) {
			FeatureContainer featureContainer = (FeatureContainer) element;
			return featureContainer.getFeature().getFeatureID();
		} else {
			return "Error!";
		}
	}
	
}
