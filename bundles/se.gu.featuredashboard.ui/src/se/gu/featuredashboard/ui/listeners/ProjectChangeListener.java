package se.gu.featuredashboard.ui.listeners;

import java.util.stream.IntStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;

import se.gu.featuredashboard.utils.ICallbackEvent;
import se.gu.featuredashboard.utils.ICallbackListener;

public class ProjectChangeListener implements IResourceChangeListener {

	private static ICallbackListener callback;
	private static ProjectChangeListener instance;
	
	private ProjectChangeListener() {
	}
	
	public static ProjectChangeListener getInstance(ICallbackListener newCallback) {
		if(instance == null) {
			instance = new ProjectChangeListener();
			callback = newCallback;
		}
		return instance;
	}
	
	@Override
	public void resourceChanged(IResourceChangeEvent event) {

		IResourceDelta delta = event.getDelta();
		
		if(event.getType() == IResourceChangeEvent.POST_CHANGE) {		
			IntStream.range(0, delta.getAffectedChildren().length).forEach(i -> {
				IResource resource = delta.getAffectedChildren()[i].getResource();
				if(resource instanceof IProject) {
					System.out.println(resource.getName());
					// Everytime there is a change on a file, this will be called twice, once for the .java and another for .class. Not optimal but will have to do
					ICallbackEvent callbackEvent = new ICallbackEvent(ICallbackEvent.EventType.ChangeDetected, resource.getLocation());
					callback.callbackMethod(callbackEvent);
				}
			});;
		}
		
	}

}
