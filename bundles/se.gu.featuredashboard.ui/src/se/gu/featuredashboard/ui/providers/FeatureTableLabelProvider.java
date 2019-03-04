package se.gu.featuredashboard.ui.providers;

import org.eclipse.jface.viewers.LabelProvider;

import se.gu.featuredashboard.model.featuremodel.Feature;

public class FeatureTableLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		
		if(element instanceof Feature) {
			Feature feature = (Feature) element;
			return feature.getFeatureID();
		}
		return "Placeholder";
	}
	
}
