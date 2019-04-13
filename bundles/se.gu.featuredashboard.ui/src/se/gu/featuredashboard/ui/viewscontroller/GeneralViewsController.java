package se.gu.featuredashboard.ui.viewscontroller;

import java.util.ArrayList;
import java.util.List;

import se.gu.featuredashboard.model.location.FeatureLocation;
import se.gu.featuredashboard.ui.listeners.FeatureSelectionListener;

public class GeneralViewsController {

	private List<FeatureLocation> locations = new ArrayList<FeatureLocation>();
	private List<FeatureSelectionListener> listeners = new ArrayList<FeatureSelectionListener>();
	
	private static GeneralViewsController INSTANCE;
	
	private GeneralViewsController() {
		
	}
	
	public static GeneralViewsController getInstance() {
		if (INSTANCE == null) {
			synchronized (GeneralViewsController.class) {
				if (INSTANCE == null) {
					INSTANCE = new GeneralViewsController();
				}
			}
		}
		return INSTANCE;
	}
	
	public void updateLocation(List<FeatureLocation> locations) {
		this.locations = locations;
		
		listeners.forEach(listener->{
			listener.dataUpdated(locations);
		});
	}
	
	public void registerFeatureSelectionListener(FeatureSelectionListener listener) {
		listeners.add(listener);
	}
	
	public void removeFeatureSelectionListener(FeatureSelectionListener listener) {
		listeners.remove(listener);
	}
	
	public List<FeatureLocation> getLocations(){
		return locations;
	}

}
