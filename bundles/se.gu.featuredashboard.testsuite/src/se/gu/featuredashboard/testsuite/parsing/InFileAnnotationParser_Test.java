package se.gu.featuredashboard.testsuite.parsing;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.location.BlockLine;
import se.gu.featuredashboard.model.location.FeatureAnnotationsLocation;
import se.gu.featuredashboard.model.location.FeatureLocationDashboard;
import se.gu.featuredashboard.parsing.InFileAnnotationParser;

public class InFileAnnotationParser_Test {

	private FeatureAnnotationsLocation c_code_annotation_feature1;
	private FeatureAnnotationsLocation c_code_annotation_feature2;
	private FeatureAnnotationsLocation c_code_annotation_fileUpload;
	private FeatureAnnotationsLocation c_code_annotation_fileProcessing;
	private FeatureAnnotationsLocation js_code_annotation_polling;
	private FeatureAnnotationsLocation js_code_annotation_fileProcessing;
	private FeatureAnnotationsLocation js_code_annotation_fileUpload;
	private FeatureAnnotationsLocation js_code_annotation_cancellation;

	private static final String TESTFILE_C_CODE_ADDRESS = "src/se/gu/featuredashboard/TestPackage/testData/testExample_Ccode.c";
	private static final String TESTFILE_JS_CODE_ADDRESS = "src/se/gu/featuredashboard/TestPackage/testData/testExample_Server.js";
	private static final String TESTFILE_NOANNOTATION_ADDRESS = "src/se/gu/featuredashboard/TestPackage/testData/NoAnnotation.txt";

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

	@Before
	public void initialize() {
		ArrayList<BlockLine> c_code_feature1_Lines = new ArrayList<>();
		c_code_feature1_Lines.add(new BlockLine(5, 5));
		c_code_annotation_feature1 = new FeatureAnnotationsLocation(c_code_feature1, c_codeFile, c_code_feature1_Lines);

		ArrayList<BlockLine> c_code_feature2_Lines = new ArrayList<>();
		c_code_feature2_Lines.add(new BlockLine(9, 12));
		c_code_feature2_Lines.add(new BlockLine(16, 18));
		c_code_annotation_feature2 = new FeatureAnnotationsLocation(c_code_feature2, c_codeFile, c_code_feature2_Lines);

		ArrayList<BlockLine> c_code_fileUpload_Lines = new ArrayList<>();
		c_code_fileUpload_Lines.add(new BlockLine(21, 24));
		c_code_annotation_fileUpload = new FeatureAnnotationsLocation(c_code_fileUpload, c_codeFile, c_code_fileUpload_Lines);

		ArrayList<BlockLine> c_code_fileProcessing_Lines = new ArrayList<>();
		c_code_fileProcessing_Lines.add(new BlockLine(27, 27));
		c_code_annotation_fileProcessing = new FeatureAnnotationsLocation(c_code_fileProcessing, c_codeFile, c_code_fileProcessing_Lines);

		ArrayList<BlockLine> js_code_fileUpload_Lines = new ArrayList<>();
		js_code_fileUpload_Lines.add(new BlockLine(184, 266));
		js_code_annotation_fileUpload = new FeatureAnnotationsLocation(js_code_fileUpload, js_codeFile, js_code_fileUpload_Lines);

		ArrayList<BlockLine> js_code_fileProcessing_Lines = new ArrayList<>();
		js_code_fileProcessing_Lines.add(new BlockLine(199, 262));
		js_code_annotation_fileProcessing = new FeatureAnnotationsLocation(js_code_fileProcessing, js_codeFile, js_code_fileProcessing_Lines);

		ArrayList<BlockLine> js_code_polling_Lines = new ArrayList<>();
		js_code_polling_Lines.add(new BlockLine(267, 441));
		js_code_annotation_polling = new FeatureAnnotationsLocation(js_code_polling, js_codeFile, js_code_polling_Lines);

		ArrayList<BlockLine> js_code_cancellation_Lines = new ArrayList<>();
		js_code_cancellation_Lines.add(new BlockLine(413, 433));
		js_code_annotation_cancellation = new FeatureAnnotationsLocation(js_code_cancellation, js_codeFile, js_code_cancellation_Lines);
	}

	@Test
	public void appendAnnotationsTest() {
		Map<String, ArrayList<Integer>> annotation1 = new HashMap<>();
		Map<String, ArrayList<Integer>> annotation2 = new HashMap<>();

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
	public void parseRegexTest() {
		final String DEFAULT_LINE_ANNOTATION_REGEX = "\\p{Space}*//&line\\[.+\\]\\p{Space}*";
		Map<String, ArrayList<Integer>> parsedResult = InFileAnnotationParser.parseRegex(TESTFILE_C_CODE_ADDRESS,
				DEFAULT_LINE_ANNOTATION_REGEX);
		assertEquals("Single line",2,parsedResult.size());
		assertEquals("Single line",1,
				  parsedResult.get(c_code_feature1.getFeatureID()).size());
		assertEquals("Single line",c_code_annotation_feature1.getBlocklines().get(0).getStartLine(),
				  (int)parsedResult.get(c_code_feature1.getFeatureID()).get(0));
		assertEquals("Single line",c_code_annotation_fileProcessing.getBlocklines().get(0).getStartLine(),
				  (int)parsedResult.get(c_code_fileProcessing.getFeatureID()).get(0));

	}

	@Test
	public void readParseAnnotations_FileInputTest() {
		InFileAnnotationParser parser = new InFileAnnotationParser();

		ArrayList<FeatureAnnotationsLocation> locations = parser.readParseAnnotations(TESTFILE_NOANNOTATION_ADDRESS);
		assertEquals("File with no annotation inside", 0, locations.size());

		FeatureLocationDashboard fd = new FeatureLocationDashboard();
		fd.resetAllAnnotationTraces( parser.readParseAnnotations(TESTFILE_C_CODE_ADDRESS) );

		assertEquals("size",4,fd.getAllAnnotationLocations().size());
		assertTrue("feature1", fd.getAllAnnotationLocations().contains(c_code_annotation_feature1));
		assertTrue("feature2",fd.getAllAnnotationLocations().contains(c_code_annotation_feature2));
		assertTrue("fileUpload",fd.getAllAnnotationLocations().contains(c_code_annotation_fileUpload));
		assertTrue("fileProcessing",fd.getAllAnnotationLocations().contains(c_code_annotation_fileProcessing));
	}

	@Test
	public void readParseAnnotations_FilesInputTest() {
		InFileAnnotationParser parser = new InFileAnnotationParser();
		ArrayList<String> addresses = new ArrayList<>();
		addresses.add(TESTFILE_C_CODE_ADDRESS);
		addresses.add(TESTFILE_JS_CODE_ADDRESS);
		FeatureLocationDashboard fd = new FeatureLocationDashboard();
		fd.resetAllAnnotationTraces( parser.readParseAnnotations(addresses) );

		assertEquals("size",8,fd.getAllAnnotationLocations().size());
		assertTrue("feature1", fd.getAllAnnotationLocations().contains(c_code_annotation_feature1));
		assertTrue("feature2",fd.getAllAnnotationLocations().contains(c_code_annotation_feature2));
		assertTrue("fileUpload",fd.getAllAnnotationLocations().contains(c_code_annotation_fileUpload));
		assertTrue("fileProcessing",fd.getAllAnnotationLocations().contains(c_code_annotation_fileProcessing));

		assertTrue("fileUpload in js code",fd.getAllAnnotationLocations().contains(js_code_annotation_fileUpload));
		assertTrue("fileProcessing in js code",fd.getAllAnnotationLocations().contains(js_code_annotation_fileProcessing));
		assertTrue("polling in js code",fd.getAllAnnotationLocations().contains(js_code_annotation_polling));
		assertTrue("cancellation in js code",fd.getAllAnnotationLocations().contains(js_code_annotation_cancellation));


	}


}