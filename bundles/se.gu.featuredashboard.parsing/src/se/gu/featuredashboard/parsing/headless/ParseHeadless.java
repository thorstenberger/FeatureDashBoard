/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.parsing.headless;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import se.gu.featuredashboard.model.location.FeatureLocation;
import se.gu.featuredashboard.model.location.ProjectData;
import se.gu.featuredashboard.parsing.InFileAnnotationParser;
import se.gu.featuredashboard.parsing.MainParser;

/*
 * This 'Headless' application takes an input folder and parses all files in this folder and then writes all feature metrics to file.
 * It also takes an output folder to which the output file is written to. In order to load the specified project as an eclipse IProject 
 * there needs to be a .project file located in the project directory.  
 * Open terminal and navigate to unzipped folder:
 * ./Eclipse.exe <arguments>
 * 		-nosplash 											 // Skip the splash icon on startup 																		*OPTIONAL*
 * 		-consoleLog											 // Write program output to console 																		*OPTIONAL*
 * 		-application se.gu.featuredashboard.parsing.headless // Tells eclipse to run this application 																	*REQUIRED*
 * 		-input <Input-folder>								 // Absolute path for the location of the input folder 														*REQUIRED*
 * 		-output <Output-folder>								 // Absolute path for the location of the output file. In this folder a 'metrics.csv' file will be written. *REQUIRED*
 * 		-filename <filename>								 // Name of the file that the metrics should be written to  		 										*OPTIONAL*
 * */

public class ParseHeadless implements IApplication, IJobChangeListener {

	private ProjectData projectData;
	private InFileAnnotationParser parser = new InFileAnnotationParser();
	private String[] appArgs;
	private static String OUTPUT_FILE = "metrics.csv";
	private MainParser annotationParser = new MainParser();
	private List<FeatureLocation> locations;

	private File inputFolder;
	private File outputFolder;

	private static final String INPUT_FOLDER_ARG = "-input";
	private static final String OUTPUT_FOLDER_ARG = "-output";
	private static final String FILENAME_ARG = "-filename";

	@Override
	public Object start(IApplicationContext context) throws Exception {
		final Map<?, ?> args = context.getArguments();
		appArgs = (String[]) args.get("application.args");

		// Get arguments
		int i = 0;
		// Prevent IndexOutOfIndexException
		while (i < appArgs.length - 1) {
			switch (appArgs[i]) {
			case INPUT_FOLDER_ARG:
				inputFolder = new File(appArgs[i + 1]);
				break;
			case OUTPUT_FOLDER_ARG:
				outputFolder = new File(appArgs[i + 1]);
				break;
			case FILENAME_ARG:
				OUTPUT_FILE = appArgs[i + 1];
				break;
			}
			i++;
		}

		if (inputFolder == null || outputFolder == null) {
			System.out.println("Input and/or output folder isn't specified");
		}

		if (!inputFolder.exists()) {
			System.out.println("Input path doesn't exist!");
			return null;
		}

		if (!outputFolder.exists()) {
			System.out.println("Output path doesn't exist!");
			return null;
		}

		locations = new ArrayList<>();

		IProject project = null;
		try {
			// Fist time project is imported to workspace
			IProjectDescription description = ResourcesPlugin.getWorkspace()
					.loadProjectDescription(new Path(inputFolder.getAbsolutePath() + "/.project"));
			description.setLocation(new Path(inputFolder.getAbsolutePath()));
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
			project.create(description, new NullProgressMonitor());
			project.open(new NullProgressMonitor());
		} catch (ResourceException e) {
			// The project has been imported previously and already exists in workspace.
			project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(Paths.get(inputFolder.getAbsolutePath()).getFileName().toString());
		} catch (CoreException e) {
			e.printStackTrace();
		} finally {
			// Do something with project.
		}

		return IApplication.EXIT_OK;
	}

	private void writeCSV() {

		try (BufferedWriter bos = new BufferedWriter(
				new FileWriter(outputFolder.getAbsolutePath() + "/" + OUTPUT_FILE))) {

			bos.write("Echo!");

		} catch (IOException e) {
			System.out.println("Could not write metrics to file");
		}

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void aboutToRun(IJobChangeEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void awake(IJobChangeEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void done(IJobChangeEvent event) {
		IStatus status = (IStatus) event.getResult();
		if (status.getCode() == Status.OK_STATUS.getCode()) {
			writeCSV();
		}
	}

	@Override
	public void running(IJobChangeEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scheduled(IJobChangeEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sleeping(IJobChangeEvent event) {
		// TODO Auto-generated method stub

	}

}
