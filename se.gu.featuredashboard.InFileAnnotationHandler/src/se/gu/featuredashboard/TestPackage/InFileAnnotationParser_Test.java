package se.gu.featuredashboard.TestPackage;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import se.gu.featuredashboard.InFileAnnotationHandler.InFileAnnotationParser;
import se.gu.featuredashboard.LocationHandler.Locations.AnnotatedLocations;
import se.gu.featuredashboard.LocationHandler.Locations.Features_Locations;

public class InFileAnnotationParser_Test {

	private AnnotatedLocations c_code_feature1_annotation;
	private AnnotatedLocations c_code_feature2_annotation;
	private AnnotatedLocations c_code_fileUpload_annotation;
	private AnnotatedLocations c_code_fileProcessing_annotation;

	private AnnotatedLocations js_code_polling_annotation;
	private AnnotatedLocations js_code_fileProcessing_annotation;
	private AnnotatedLocations js_code_fileUpload_annotation;
	private AnnotatedLocations js_code_cancellation_annotation;

	private static final String TESTFILE_C_CODE_ADDRESS = "src/se/gu/featuredashboard/TestPackage/testData/testExample_Ccode.c";
	private static final String TESTFILE_JS_CODE_ADDRESS = "src/se/gu/featuredashboard/TestPackage/testData/testExample_Server.js";
	private static final String TESTFILE_NOANNOTATION_ADDRESS = "src/se/gu/featuredashboard/TestPackage/testData/NoAnnotation.txt";

	@Before
	public void initialize() {
		c_code_feature1_annotation = new AnnotatedLocations(new ArrayList<>(Arrays.asList(5)), new ArrayList<>(),
				new ArrayList<>());

		c_code_feature2_annotation = new AnnotatedLocations(new ArrayList<>(), new ArrayList<>(Arrays.asList(9, 16)),
				new ArrayList<>(Arrays.asList(12, 18)));

		c_code_fileUpload_annotation = new AnnotatedLocations(new ArrayList<>(), new ArrayList<>(Arrays.asList(21)),
				new ArrayList<>(Arrays.asList(24)));

		c_code_fileProcessing_annotation = new AnnotatedLocations(new ArrayList<>(Arrays.asList(27)), new ArrayList<>(),
				new ArrayList<>());

		js_code_polling_annotation = new AnnotatedLocations(new ArrayList<>(), new ArrayList<>(Arrays.asList(267)),
				new ArrayList<>(Arrays.asList(441)));

		js_code_fileProcessing_annotation = new AnnotatedLocations(new ArrayList<>(),
				new ArrayList<>(Arrays.asList(199)), new ArrayList<>(Arrays.asList(262)));

		js_code_fileUpload_annotation = new AnnotatedLocations(new ArrayList<>(), new ArrayList<>(Arrays.asList(184)),
				new ArrayList<>(Arrays.asList(266)));

		js_code_cancellation_annotation = new AnnotatedLocations(new ArrayList<>(), new ArrayList<>(Arrays.asList(413)),
				new ArrayList<>(Arrays.asList(433)));

	}

	@Test
	public void appendAnnotationsTest() {
		Map<String, ArrayList<Integer>> annotation1 = new HashMap<>();
		Map<String, ArrayList<Integer>> annotation2 = new HashMap<>();

		assertEquals("empty annotations", 0,
				InFileAnnotationParser.appendLineAnnotations(annotation1, annotation2).size());

		annotation1.put("f1", new ArrayList<Integer>(Arrays.asList(new Integer[] { 1 })));
		assertArrayEquals("one is empty, one has one element", new Integer[] { 1 }, InFileAnnotationParser
				.appendLineAnnotations(annotation1, annotation2).get("f1").toArray(new Integer[1]));

		annotation2.put("f2", new ArrayList<Integer>(Arrays.asList(new Integer[] { 10, 20 })));
		assertArrayEquals("two of them have different features", new Integer[] { 10, 20, }, InFileAnnotationParser
				.appendLineAnnotations(annotation1, annotation2).get("f2").toArray(new Integer[2]));

		annotation2.put("f1", new ArrayList<Integer>(Arrays.asList(new Integer[] { 3, 4 })));
		assertArrayEquals("they have one feature in common", new Integer[] { 1, 3, 4 }, InFileAnnotationParser
				.appendLineAnnotations(annotation1, annotation2).get("f1").toArray(new Integer[3]));
		assertArrayEquals("they have one feature in common", new Integer[] { 10, 20 }, InFileAnnotationParser
				.appendLineAnnotations(annotation1, annotation2).get("f2").toArray(new Integer[2]));
	}

	@Test
	public void parseRegexTest() {
		final String DEFAULT_LINE_ANNOTATION_REGEX = "\\p{Space}*//&line\\[.+\\]\\p{Space}*";
		final String DEFAULT_BEGIN_ANNOTATION_REGEX = "\\p{Space}*//&begin\\[.+\\]\\p{Space}*";
		Map<String, ArrayList<Integer>> result = InFileAnnotationParser.parseRegex(TESTFILE_C_CODE_ADDRESS,
				DEFAULT_LINE_ANNOTATION_REGEX);
		assertArrayEquals("feature annotation in one line of a file",
				c_code_feature1_annotation.getSingleLinAnnotations().toArray(), result.get("feature1").toArray());

		result = InFileAnnotationParser.parseRegex(TESTFILE_C_CODE_ADDRESS, DEFAULT_BEGIN_ANNOTATION_REGEX);
		assertArrayEquals("feature annotations in more than one line of a file",
				c_code_feature2_annotation.getBeginAnnotations().toArray(), result.get("feature2").toArray());

	}

	@Test
	public void readParseAnnotations_StringInputTest() {
		InFileAnnotationParser parser = new InFileAnnotationParser();

		Features_Locations locations = parser.readParseAnnotations(TESTFILE_NOANNOTATION_ADDRESS);
		assertEquals("File with no annotation inside", 0, locations.getAllAnnotatedFiles().size());

		locations = parser.readParseAnnotations(TESTFILE_C_CODE_ADDRESS);

		assertTrue("line annotation",
				locations.getAnnotations("feature1", TESTFILE_C_CODE_ADDRESS).equals(c_code_feature1_annotation));
		assertTrue("begin-end annotation",
				locations.getAnnotations("feature2", TESTFILE_C_CODE_ADDRESS).equals(c_code_feature2_annotation));

	}

	@Test
	public void readParseAnnotations_ListInputTest() {
		InFileAnnotationParser parser = new InFileAnnotationParser();
		ArrayList<String> addresses = new ArrayList<>();
		addresses.add(TESTFILE_C_CODE_ADDRESS);
		addresses.add(TESTFILE_JS_CODE_ADDRESS);
		Features_Locations locations = parser.readParseAnnotations(addresses);

		assertTrue("feature1 in C code",
				locations.getAnnotations("feature1", TESTFILE_C_CODE_ADDRESS).equals(c_code_feature1_annotation));

		assertTrue("feature2 in C code",
				locations.getAnnotations("feature2", TESTFILE_C_CODE_ADDRESS).equals(c_code_feature2_annotation));

		assertTrue("fileUpload in C code",
				locations.getAnnotations("fileUpload", TESTFILE_C_CODE_ADDRESS).equals(c_code_fileUpload_annotation));

		assertTrue("fileProcessing in C code", locations.getAnnotations("fileProcessing", TESTFILE_C_CODE_ADDRESS)
				.equals(c_code_fileProcessing_annotation));

		assertTrue("polling in js code",
				locations.getAnnotations("polling", TESTFILE_JS_CODE_ADDRESS).equals(js_code_polling_annotation));

		assertTrue("fileProcessing in js code", locations.getAnnotations("fileProcessing", TESTFILE_JS_CODE_ADDRESS)
				.equals(js_code_fileProcessing_annotation));

		assertTrue("fileUpload in js code",
				locations.getAnnotations("fileUpload", TESTFILE_JS_CODE_ADDRESS).equals(js_code_fileUpload_annotation));

		assertTrue("cancellation in js code", locations.getAnnotations("cancellation", TESTFILE_JS_CODE_ADDRESS)
				.equals(js_code_cancellation_annotation));
	}

}