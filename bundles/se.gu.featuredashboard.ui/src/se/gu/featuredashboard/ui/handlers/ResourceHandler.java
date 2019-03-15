package se.gu.featuredashboard.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public abstract class ResourceHandler extends AbstractHandler implements IHandler {

	public static IResource getResource(final ExecutionEvent event) {
		final ISelection selection = HandlerUtil.getCurrentSelection(event);
		
		if(!(selection instanceof IStructuredSelection))
			return null;
		
		final Object element =((IStructuredSelection) selection).getFirstElement();
		
		return (IResource) Platform.getAdapterManager().getAdapter(element, IResource.class);
	}
	
}	
