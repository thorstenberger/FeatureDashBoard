package se.gu.featuredashboard.ui.viewscontroller;

import java.util.ArrayList;
import java.util.List;

import se.gu.featuredashboard.model.location.FeatureLocation;
import se.gu.featuredashboard.ui.listeners.IFeatureSelectionListener;
import se.gu.featuredashboard.ui.listeners.IProjectSelectionListener;

public class GeneralViewsController {

	private List<FeatureLocation> locations = new ArrayList<FeatureLocation>();
	private List<IFeatureSelectionListener> featureSelectionListeners = new ArrayList<>();
	private List<IProjectSelectionListener> projectSelectionListeners = new ArrayList<>();

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
		featureSelectionListeners.forEach(listener -> {
			listener.updateFeatureSelection(locations);
		});
	}

	public void projectUpdated() {
		projectSelectionListeners.forEach(listener -> {
			listener.updateProjectSelected();
		});
	}

	public void registerFeatureSelectionListener(IFeatureSelectionListener listener) {
		featureSelectionListeners.add(listener);
	}

	public void removeFeatureSelectionListener(IFeatureSelectionListener listener) {
		featureSelectionListeners.remove(listener);
	}

	public void registerProjectSelectionListener(IProjectSelectionListener listener) {
		projectSelectionListeners.add(listener);
	}

	public void removeProjectSelectionListener(IProjectSelectionListener listener) {
		projectSelectionListeners.remove(listener);
	}

	public List<FeatureLocation> getLocations() {
		return locations;
	}

}
