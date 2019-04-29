/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.ui.viewscontroller;


import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.ConfigurationScope;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import Listeners.IUpdateInformationListener;
import Listeners.ParseChangeListener;
import se.gu.featuredashboard.model.location.ProjectData;
import se.gu.featuredashboard.core.WorkspaceData;
import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.location.FeatureLocation;
import se.gu.featuredashboard.parsing.MainParser;
import se.gu.featuredashboard.ui.views.FeatureDashboardView;

/**
 * This class is the controller of FeatureDashboarView and is used for communications
 * between different classes with that view.
 *
 */
public class FeatureDashboardViewController implements IUpdateInformationListener {

	private static FeatureDashboardViewController INSTANCE;
	public static FeatureDashboardViewController getInstance() {		
		if (INSTANCE == null) {
			synchronized (FeatureDashboardViewController.class) {
				if (INSTANCE == null) {
					INSTANCE = new FeatureDashboardViewController();
				}
			}
		}
		return INSTANCE;
	}
	
	private String parsingMessages="";
	
	private IProject project;
	private ProjectData selectedProjectData = new ProjectData();
	private MainParser mainParser = new MainParser();
	
	private FeatureDashboardViewController() {
		mainParser.setUser(true);
		mainParser.addJobChangeListener(new ParseChangeListener(Arrays.asList(this)));
	}

	public void updateController(IProject project) {
		this.project = project;
		parsingMessages="";
		
		/*
		projectParser = new ProjectParser(project, 
				getExcludedAnnotatedFilesExtensions_ofPreferences(),
				getExcludedFoldersOfAnnotatedFiles_ofPreferences());
		setRegex_ofPreferences();
		*/
		

		mainParser.setProject(project);

		mainParser.setProject(project);
		mainParser.setUser(true);
		//mainParser.addJobChangeListener(new ParseChangeListener(Arrays.asList(this)));

		mainParser.schedule();
		
		//updateSelectedProjectData();
	}

	private void updateWorkspaceData() {
		if (selectedProjectData.getProject() != null) {
			WorkspaceData.getInstance().setProjectData(selectedProjectData);
		}
	}
	
	public List<ProjectData> getWorkspaceData() {
		return WorkspaceData.getInstance().getWorkspaceData();
	}

	private void updateWorkspaceData(ProjectData newData) {
		if(newData!=null)
			WorkspaceData.getInstance().setProjectData(newData);
	}
	
	public IProject getProject() {
		return project;
	}

	public boolean hasFeatureModel() {
		return (mainParser.getClaferFile()!=null);
	}
	
	public IFile getFeatureModel() {
		return mainParser.getClaferFile();
	}
	
	public String getParsingMessage() {
		return parsingMessages;
	}
	
	public List<Feature> getFeaturesNotInFeatureModel(){
		return selectedProjectData.getFeaturesNotInFeatureModel();
	}

	public List<Feature> getRootFeaturesOfFeatureModel(){
		return selectedProjectData.getRootFeaturesOfFeatureModel();

	}
	
	public List<Feature>  getAllFeaturesOfFeatureModel(){
		return selectedProjectData.getFeaturesOfFeatureModel();
	}

	public List<IResource> getNotExistentResources(){
		return selectedProjectData.getNotExistentResources();
	}
	
	public List<FeatureLocation> getTraces(List<Object> features, List<Object> projectRelativePaths){
		return selectedProjectData.getTraces(features, projectRelativePaths);
	}
	
	public List<FeatureLocation> getAllLocations() {
		return selectedProjectData.getAllLocations();
	}

	@Override
	public void parsingComplete() {
		Display.getDefault().asyncExec(()->{
			updateWorkspaceData(mainParser.getProjectData());
			selectedProjectData = mainParser.getProjectData();
			updateWorkspaceData();
			FeatureDashboardView view = (FeatureDashboardView)PlatformUI.getWorkbench().
					getActiveWorkbenchWindow().getActivePage().findView("se.gu.featuredashboard.ui.views.FeatureDashboardView");
			GeneralViewsController.getInstance().projectUpdated();		
			if (view != null) {
				view.updateFeatureModelTab();
				view.updateResourcesTab();
				view.updateTracesTab();
			}
			
		});
	}
}
