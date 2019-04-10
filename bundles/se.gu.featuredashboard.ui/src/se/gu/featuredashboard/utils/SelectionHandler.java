package se.gu.featuredashboard.utils;

import java.util.ArrayList;
import java.util.List;

import se.gu.featuredashboard.model.featuremodel.FeatureContainer;

public class SelectionHandler {

	private static List<FeatureContainer> selection = new ArrayList<>();
	
	public static void setSelection(List<FeatureContainer> newSelection) {
		selection = newSelection;
	}
	
	public static List<FeatureContainer> getSelection(){
		return selection;
	}
	
}
