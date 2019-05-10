/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.ui.PlatformUI;

import se.gu.featuredashboard.utils.FeaturedashboardConstants;

public class RemoveMarkers extends ResourceHandler {
	
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
	
}
