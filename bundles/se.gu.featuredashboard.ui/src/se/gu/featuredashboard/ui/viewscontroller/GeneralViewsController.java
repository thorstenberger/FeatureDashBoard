package se.gu.featuredashboard.ui.viewscontroller;

import java.util.ArrayList;
import java.util.List;

import se.gu.featuredashboard.model.location.FeatureLocation;
import se.gu.featuredashboard.ui.listeners.IFeatureSelectionListener;
import se.gu.featuredashboard.ui.listeners.IProjectSelectionListener;
import se.gu.featuredashboard.ui.listeners.ISelectionListener;

public class GeneralViewsController {

	private List<FeatureLocation> locations = new ArrayList<FeatureLocation>();
	private List<ISelectionListener> listeners = new ArrayList<ISelectionListener>();

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

		listeners.forEach(listener -> {
			// A listner can implement both listeners
			if (listener instanceof IFeatureSelectionListener) {
				((IFeatureSelectionListener) listener).updateFeatureSelection(locations);
			}

			if (listener instanceof IProjectSelectionListener) {
				((IProjectSelectionListener) listener).updateProjectSelected();
			}

		});
	}

	public void registerFeatureSelectionListener(IFeatureSelectionListener listener) {
		listeners.add(listener);
	}

	public void removeFeatureSelectionListener(IFeatureSelectionListener listener) {
		listeners.remove(listener);
	}

	public List<FeatureLocation> getLocations() {
		return locations;
	}

}
