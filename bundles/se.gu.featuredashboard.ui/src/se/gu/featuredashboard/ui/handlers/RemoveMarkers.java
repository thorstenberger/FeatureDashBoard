package se.gu.featuredashboard.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import se.gu.featuredashboard.utils.FeaturedashboardConstants;

public class RemoveMarkers extends AbstractHandler implements IHandler {
	
	private Logger logger = PlatformUI.getWorkbench().getService(org.eclipse.e4.core.services.log.Logger.class);
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IResource resource = getResource(event);
		
		if(resource == null)
			return null;
		
		try {
			resource.deleteMarkers(FeaturedashboardConstants.FEATURE_MARKER_ID, true, IProject.DEPTH_INFINITE);
		} catch (CoreException e) {
			logger.error("Couldn't remove markers for project.");
		}
		
		return resource;
	}
	
	private static IResource getResource(final ExecutionEvent event) {
		final ISelection selection = HandlerUtil.getCurrentSelection(event);
		
		if(!(selection instanceof IStructuredSelection))
			return null;
		
		final Object element =((IStructuredSelection) selection).getFirstElement();
		
		return (IResource) Platform.getAdapterManager().getAdapter(element, IResource.class);
	}
	
}
