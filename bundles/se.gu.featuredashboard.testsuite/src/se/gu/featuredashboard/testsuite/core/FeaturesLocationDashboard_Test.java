package se.gu.featuredashboard.testsuite.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.junit.Before;
import org.junit.Test;

import se.gu.featuredashboard.model.location.ProjectData;
import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.location.BlockLine;
import se.gu.featuredashboard.model.location.FeatureLocation;

public class FeaturesLocationDashboard_Test {

	ProjectData locations;
	
	Feature feature1;
	Feature feature2;
	Feature feature3;
	
	IFolder folder1;
	IFolder folder2;
	
	IFile file1;
	IFile file2;
	
	IFile file1InFolder1;
	IFile file1InFolder1InFolder1;
	
	ArrayList<BlockLine> annotation1;
	ArrayList<BlockLine> annotation2;

	@Before
	public void initialize() throws CoreException {

		locations = new ProjectData();
		locations.clearAll();
		
		feature1 = new Feature("feature1");
		feature2 = new Feature("feature2");
		feature3 = new Feature("feature3");
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject("FD_Test1");
		
		folder1 = project.getFolder("testFolder1");
		folder2 = project.getFolder("testFolder2");
		
		file1 = project.getFile("file1");
		file2 = project.getFile("file2");
		file1InFolder1 = folder1.getFile("file1");
		file1InFolder1InFolder1 =  folder1.getFolder("folder1").getFile("file1");
		

		annotation1 = new ArrayList<>();
		annotation1.add(new BlockLine(1, 1));
		annotation1.add(new BlockLine(2, 2));
		annotation1.add(new BlockLine(3, 3));
		annotation1.add(new BlockLine(10, 11));
		annotation1.add(new BlockLine(20, 30));

		annotation2 = new ArrayList<>();
		annotation2.add(new BlockLine(4, 5));
		annotation2.add(new BlockLine(12, 13));
		annotation2.add(new BlockLine(21, 31));
	}
	
	@Test
	public void addTraceTest() {
		FeatureLocation location1 = new FeatureLocation(feature1,folder1,null);
		FeatureLocation location2 = new FeatureLocation(feature1,folder2,null);
		
		locations.addTrace(location1);
		FeatureLocation foundLocation = locations.getTraces(Arrays.asList(feature1), Arrays.asList(folder1)).get(0);
		assertTrue("add first location", 
					foundLocation.equals(location1));
		
		locations.addTrace(location2);
		FeatureLocation foundLocation2 = locations.getTraces(Arrays.asList(feature1), Arrays.asList(folder1)).get(1);
		assertTrue("add second location", 
				foundLocation2.equals(location2));	
	}

	@Test
	public void traceExistsTest() {
		FeatureLocation location1 = new FeatureLocation(feature1,folder1,null);
		locations.addTrace(location1);
		assertTrue("positive1", 
				locations.traceExists(feature1, folder1));
		
		assertFalse("negative1", 
				locations.traceExists(feature1, folder2));
		
		FeatureLocation location2 = new FeatureLocation(feature2,file1InFolder1,null);
		locations.addTrace(location2);
		assertTrue("add second location", 
				locations.traceExists(feature2, folder1));
		
		FeatureLocation location3 = new FeatureLocation(feature3,file2,null);
		locations.addTrace(location3);
		assertTrue("add third location", 
				locations.traceExists(feature2, folder1));
		
		locations.clearAllTraces();
		locations.addTrace(location2);
		assertFalse("negative1", 
				locations.traceExists(feature2, file1InFolder1InFolder1));
		
		
		
	}


}
