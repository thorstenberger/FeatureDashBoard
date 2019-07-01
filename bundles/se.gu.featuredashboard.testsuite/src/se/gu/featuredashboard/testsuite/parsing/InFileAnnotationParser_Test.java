package se.gu.featuredashboard.testsuite.parsing;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.gu.featuredashboard.model.location.ProjectData;
import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.location.BlockLine;
import se.gu.featuredashboard.model.location.FeatureLocation;
import se.gu.featuredashboard.parsing.InFileAnnotationParser;

public class InFileAnnotationParser_Test {

	
	private FeatureLocation c_code_annotation_feature1;
	private FeatureLocation c_code_annotation_feature2;
	private FeatureLocation c_code_annotation_fileUpload;
	private FeatureLocation c_code_annotation_fileProcessing;
	private FeatureLocation js_code_annotation_polling;
	private FeatureLocation js_code_annotation_fileProcessing;
	private FeatureLocation js_code_annotation_fileUpload;
	private FeatureLocation js_code_annotation_cancellation;
	
	//private static final String MainPath = "Users⁩/⁨admin⁩/git⁩/⁨se.gu.featuredashboard/⁩⁨bundles⁩/⁨se.gu.featuredashboard.testsuite/"; 
	private static final String TESTFILE_C_CODE_ADDRESS = "src/se/gu/featuredashboard/testsuite/testdata/testExample_Ccode.c";
	private static final String TESTFILE_JS_CODE_ADDRESS = "./src/se/gu/featuredashboard/testsuite/testData/testExample_Server.js";
	private static final String TESTFILE_NOANNOTATION_ADDRESS ="./src/se/gu/featuredashboard/testsuite/testData/NoAnnotation.txt";

	File c_codeFile = new File(TESTFILE_C_CODE_ADDRESS);
	File js_codeFile = new File(TESTFILE_JS_CODE_ADDRESS);
	File noAnnotationFile = new File(TESTFILE_NOANNOTATION_ADDRESS);

	private Feature c_code_feature1 = new Feature("feature1");
	private Feature c_code_feature2 = new Feature("feature2");
	private Feature c_code_fileUpload = new Feature("fileUpload");
	private Feature c_code_fileProcessing = new Feature("fileProcessing");

	private Feature js_code_polling = new Feature("polling");
	private Feature js_code_fileProcessing = new Feature("fileProcessing");
	private Feature js_code_fileUpload = new Feature("fileUpload");
	private Feature js_code_cancellation = new Feature("cancellation");
	
	IFile TESTFILE_C_CODE;
	IFile TESTFILE_JS_CODE;
	IFile TESTFILE_NOANNOTATION;
	
	@Before
	public void initialize() throws FileNotFoundException, CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject("FD_Test2");
		if(!project.exists())
			project.create(new NullProgressMonitor());

		project.open(new NullProgressMonitor());

		TESTFILE_C_CODE = project.getFile(new Path(c_codeFile.getName()));
		if(!TESTFILE_C_CODE.exists())
			TESTFILE_C_CODE.create(new FileInputStream(c_codeFile), true, new NullProgressMonitor());
		
		TESTFILE_JS_CODE = project.getFile(js_codeFile.getName());
		if(!TESTFILE_JS_CODE.exists())
			TESTFILE_JS_CODE.create(new FileInputStream(js_codeFile), true, null);
		
		TESTFILE_NOANNOTATION = project.getFile(noAnnotationFile.getName());
		if(!TESTFILE_NOANNOTATION.exists())
			TESTFILE_NOANNOTATION.create(new ByteArrayInputStream("".getBytes()), true, null);
		
		ArrayList<BlockLine> c_code_feature1_Lines = new ArrayList<>();
		c_code_feature1_Lines.add(new BlockLine(5, 5));
		c_code_annotation_feature1 = new FeatureLocation(c_code_feature1, TESTFILE_C_CODE, c_code_feature1_Lines);

		ArrayList<BlockLine> c_code_feature2_Lines = new ArrayList<>();
		c_code_feature2_Lines.add(new BlockLine(9, 12));
		c_code_feature2_Lines.add(new BlockLine(16, 18));
		c_code_annotation_feature2 = new FeatureLocation(c_code_feature2, TESTFILE_C_CODE, c_code_feature2_Lines);

		ArrayList<BlockLine> c_code_fileUpload_Lines = new ArrayList<>();
		c_code_fileUpload_Lines.add(new BlockLine(21, 24));
		c_code_annotation_fileUpload = new FeatureLocation(c_code_fileUpload, TESTFILE_C_CODE, c_code_fileUpload_Lines);

		ArrayList<BlockLine> c_code_fileProcessing_Lines = new ArrayList<>();
		c_code_fileProcessing_Lines.add(new BlockLine(27, 27));
		c_code_annotation_fileProcessing = new FeatureLocation(c_code_fileProcessing, TESTFILE_C_CODE, c_code_fileProcessing_Lines);

		ArrayList<BlockLine> js_code_fileUpload_Lines = new ArrayList<>();
		js_code_fileUpload_Lines.add(new BlockLine(184, 266));
		js_code_annotation_fileUpload = new FeatureLocation(js_code_fileUpload, TESTFILE_JS_CODE, js_code_fileUpload_Lines);

		ArrayList<BlockLine> js_code_fileProcessing_Lines = new ArrayList<>();
		js_code_fileProcessing_Lines.add(new BlockLine(199, 262));
		js_code_annotation_fileProcessing = new FeatureLocation(js_code_fileProcessing, TESTFILE_JS_CODE, js_code_fileProcessing_Lines);

		ArrayList<BlockLine> js_code_polling_Lines = new ArrayList<>();
		js_code_polling_Lines.add(new BlockLine(267, 441));
		js_code_annotation_polling = new FeatureLocation(js_code_polling, TESTFILE_JS_CODE, js_code_polling_Lines);

		ArrayList<BlockLine> js_code_cancellation_Lines = new ArrayList<>();
		js_code_cancellation_Lines.add(new BlockLine(413, 433));
		js_code_annotation_cancellation = new FeatureLocation(js_code_cancellation, TESTFILE_JS_CODE, js_code_cancellation_Lines);
	}
	
	@Test
	public void readParseAnnotations_FileInputTest() {
		InFileAnnotationParser parser = new InFileAnnotationParser();

		ArrayList<FeatureLocation> locations = parser.readParseAnnotations(new ArrayList<IFile>(Arrays.asList(TESTFILE_NOANNOTATION)));
		assertEquals("File with no annotation inside", 0, locations.size());

		ProjectData fd = new ProjectData();
		fd.clearAll();
		fd.addTraces( parser.readParseAnnotations( new ArrayList<IFile>(Arrays.asList(TESTFILE_C_CODE))  ));
		
		List<Object> allFeatures = Arrays.asList(c_code_feature1, c_code_feature2, c_code_fileUpload,c_code_fileProcessing);

		assertEquals("size",4,fd.getTraces(allFeatures, new ArrayList<Object>()).size());
		assertTrue("feature1", fd.getTraces(Arrays.asList(c_code_feature1), new ArrayList<Object>()).contains(c_code_annotation_feature1));
		assertTrue("feature2",fd.getTraces(Arrays.asList(c_code_feature2), new ArrayList<Object>()).contains(c_code_annotation_feature2));
		assertTrue("fileUpload",fd.getTraces(Arrays.asList(c_code_fileUpload), new ArrayList<Object>()).contains(c_code_annotation_fileUpload));
		assertTrue("fileProcessing",fd.getTraces(Arrays.asList(c_code_fileProcessing), new ArrayList<Object>()).contains(c_code_annotation_fileProcessing));
	}

	@Test
	public void appendAnnotationsTest() {
		Map<String, List<Integer>> annotation1 = new HashMap<>();
		Map<String, List<Integer>> annotation2 = new HashMap<>();

		assertEquals("empty annotations", 0,
				InFileAnnotationParser.appendAnnotations(annotation1, annotation2).size());

		annotation1.put("f1", new ArrayList<Integer>(Arrays.asList(new Integer[] { 1 })));
		assertArrayEquals("one is empty, one has one element", new Integer[] { 1 }, InFileAnnotationParser
				.appendAnnotations(annotation1, annotation2).get("f1").toArray(new Integer[1]));

		annotation2.put("f2", new ArrayList<Integer>(Arrays.asList(new Integer[] { 10, 20 })));
		assertArrayEquals("two of them have different features", new Integer[] { 10, 20, }, InFileAnnotationParser
				.appendAnnotations(annotation1, annotation2).get("f2").toArray(new Integer[2]));

		annotation2.put("f1", new ArrayList<Integer>(Arrays.asList(new Integer[] { 3, 4 })));
		assertArrayEquals("they have one feature in common", new Integer[] { 1, 3, 4 }, InFileAnnotationParser
				.appendAnnotations(annotation1, annotation2).get("f1").toArray(new Integer[3]));
		assertArrayEquals("they have one feature in common", new Integer[] { 10, 20 }, InFileAnnotationParser
				.appendAnnotations(annotation1, annotation2).get("f2").toArray(new Integer[2]));
	}

	
	@Test
	public void readParseAnnotations_FilesInputTest() {
		InFileAnnotationParser parser = new InFileAnnotationParser();
		ArrayList<IFile> addresses = new ArrayList<>();
		addresses.add(TESTFILE_C_CODE);
		addresses.add(TESTFILE_JS_CODE);
		ProjectData fd = new ProjectData();
		fd.clearAll();
		fd.addTraces( parser.readParseAnnotations(addresses) );

		List<Object> allFeatures2 = Arrays.asList(c_code_feature1, c_code_feature2, c_code_fileUpload,c_code_fileProcessing,
								js_code_polling,js_code_fileUpload, js_code_fileProcessing, js_code_cancellation);

		assertEquals("size",8,fd.getTraces(allFeatures2, new ArrayList<Object>()).size());
		
		assertTrue("feature1", fd.getTraces(Arrays.asList(c_code_feature1), new ArrayList<Object>()).
				contains(c_code_annotation_feature1));
		assertTrue("feature2",fd.getTraces(Arrays.asList(c_code_feature2), new ArrayList<Object>()).
				contains(c_code_annotation_feature2));
		assertTrue("fileUpload",fd.getTraces(Arrays.asList(c_code_fileUpload), new ArrayList<Object>()).
				contains(c_code_annotation_fileUpload));
		assertTrue("fileProcessing",fd.getTraces(Arrays.asList(c_code_fileProcessing), new ArrayList<Object>()).
				contains(c_code_annotation_fileProcessing));

		assertTrue("fileUpload in js code",fd.getTraces(Arrays.asList(js_code_fileUpload), new ArrayList<Object>()).
				contains(js_code_annotation_fileUpload));
		assertTrue("fileProcessing in js code",fd.getTraces(Arrays.asList(c_code_fileProcessing), new ArrayList<Object>()).
				contains(js_code_annotation_fileProcessing));
		assertTrue("polling in js code",fd.getTraces(Arrays.asList(js_code_polling), new ArrayList<Object>()).
				contains(js_code_annotation_polling));
		assertTrue("cancellation in js code",fd.getTraces(Arrays.asList(js_code_cancellation), new ArrayList<Object>()).
				contains(js_code_annotation_cancellation));
	}
	

}