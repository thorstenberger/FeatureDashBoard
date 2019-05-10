/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.utils;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.ui.PlatformUI;

public class Builder extends IncrementalProjectBuilder {
	private Logger logger = PlatformUI.getWorkbench().getService(org.eclipse.e4.core.services.log.Logger.class);
	
	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		
		logger.info("Featuredashboard builder was triggered");
		
		IProject project = getProject();
		IResourceDelta delta = getDelta(project);
		
		if(kind == AUTO_BUILD) {
			logger.info("Featuredashboard builder autobuild");
		}
		
		return null;
	}
	
	// Finds the child of the project that was modfified
	private void findAffectedChildren(IProject project, IResourceDelta delta) {
		Arrays.stream(delta.getAffectedChildren()).forEach(child -> {
			IResource resource = child.getResource();
			if(resource instanceof IFile) {
				
			} else {
				findAffectedChildren(project, child);
			}
		});
	}
}