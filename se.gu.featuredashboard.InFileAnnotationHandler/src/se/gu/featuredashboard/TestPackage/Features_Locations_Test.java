package se.gu.featuredashboard.TestPackage;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.gu.featuredashboard.LocationHandler.Locations.AnnotatedLocations;
import se.gu.featuredashboard.LocationHandler.Locations.Features_Locations;

public class Features_Locations_Test {

	Features_Locations locations;
	AnnotatedLocations validAnnotation;
	AnnotatedLocations validAnnotation2;
	AnnotatedLocations invalidAnnotation;
	private static final String mainDirecoty = "src/se/gu/featuredashboard/TestPackage/testData";
	private static final String folder1 = "testFolder1";
	private static final String folder2 = "testFolder2";
	private static final String file1 = "testFile1.txt";
	private static final String file2 = "testFile2.txt";

	@Before
	public void initialize() {
		locations = new Features_Locations();

		List<Integer> lines1 = Arrays.asList(1, 2, 3);
		List<Integer> begins1 = Arrays.asList(10, 11);
		List<Integer> ends1 = Arrays.asList(20, 30);
		validAnnotation = new AnnotatedLocations(lines1, begins1, ends1);

		List<Integer> lines2 = Arrays.asList(4, 5);
		List<Integer> begins2 = Arrays.asList(12, 13);
		List<Integer> ends2 = Arrays.asList(21, 31);
		validAnnotation2 = new AnnotatedLocations(lines2, begins2, ends2);

		List<Integer> lines3 = new ArrayList<>();
		List<Integer> begins3 = Arrays.asList(10, 11);
		List<Integer> ends3 = Arrays.asList(12, 9);
		invalidAnnotation = new AnnotatedLocations(lines3, begins3, ends3);
	}

	@Test
	public void setTrace_toFolderTest() {
		assertFalse("not existed address of a folder",
				locations.setTrace_toFolder("f1", mainDirecoty + File.separator + "noFolder"));
		assertEquals("not existed address of a folder", 0, locations.getFolders("f1").size());

		assertTrue("existed address of a folder",
				locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder1));
		assertEquals(1, locations.getFolders("f1").size());

		assertFalse("previously added feature-folder trace",
				locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder1));
		assertEquals(1, locations.getFolders("f1").size());

		assertTrue("more than one Folder trace of a feature",
				locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder2));
		assertEquals(2, locations.getFolders("f1").size());

		assertTrue("more than one folder trace of a folder",
				locations.setTrace_toFolder("f2", mainDirecoty + File.separator + folder1));
		assertEquals(2, locations.getFeatures_ofFolder(mainDirecoty + File.separator + folder1).size());
	}

	@Test
	public void setTrace_toFileTest() {
		assertFalse("not existed address of a file",
				locations.setTrace_toFile("f1", mainDirecoty + File.separator + "noFile.xxx"));
		assertEquals("not existed address of a file", 0, locations.getNonAnnotatedFiles("f1").size());

		assertTrue("existed address of a file", locations.setTrace_toFile("f1", mainDirecoty + File.separator + file1));
		assertEquals(1, locations.getNonAnnotatedFiles("f1").size());

		assertFalse("previously added feature-file trace",
				locations.setTrace_toFile("f1", mainDirecoty + File.separator + file1));
		assertEquals(1, locations.getNonAnnotatedFiles("f1").size());

		assertTrue("more than one file trace of a feature",
				locations.setTrace_toFile("f1", mainDirecoty + File.separator + file2));
		assertEquals(2, locations.getNonAnnotatedFiles("f1").size());

		assertTrue("more than one file trace of a file",
				locations.setTrace_toFile("f2", mainDirecoty + File.separator + file1));
		assertEquals(2, locations.getFeatures_ofNonAnnotatedFile(mainDirecoty + File.separator + file1).size());
	}

	@Test
	public void setTrace_toAnnotationTest() {
		assertFalse("not existed file address", locations.setTrace_toAnnotation("f1", "noAddress", validAnnotation));
		assertFalse("not valid annotations",
				locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file1, invalidAnnotation));

		assertTrue("valid input",
				locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file1, validAnnotation));
		assertEquals("valid input", 1,
				locations.getFeatures_inAnnotatedFile(mainDirecoty + File.separator + file1).size());

		assertTrue("resetting previously added annotation trace",
				locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file1, validAnnotation2));
		assertEquals("previously added annotation trace", 1, locations.getAllAnnotatedFiles().size());

		assertArrayEquals("previously added annotation trace", validAnnotation2.getBeginAnnotations().toArray(),
				locations.getAnnotations("f1", mainDirecoty + File.separator + file1).getBeginAnnotations().toArray());

		assertTrue("more than one annotation trace of a feature",
				locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file2, validAnnotation));
		assertEquals("more than one annotation trace of a feature", 2, locations.getAnnotatedFiles("f1").size());

		assertTrue("more than one annotation trace of a file",
				locations.setTrace_toAnnotation("f2", mainDirecoty + File.separator + file1, validAnnotation));
		assertEquals("more than one annotation trace of a feature", 1, locations.getAnnotatedFiles("f2").size());

	}

	@Test
	public void addAnnotationTracesInOtherFilesTest() {
		Features_Locations locations1 = new Features_Locations();
		assertFalse("empty locations", locations.addAnnotationTracesInOtherFiles(locations1));

		locations1.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file1, validAnnotation);
		assertTrue("valid locations", locations.addAnnotationTracesInOtherFiles(locations1));
		assertEquals("valid locations", 1, locations.getAllAnnotatedFiles().size());

		locations1.removeAllTraces();
		locations1.setTrace_toAnnotation("f2", mainDirecoty + File.separator + file1, validAnnotation);
		assertFalse("not consistent locations, there is file annotated by both",
				locations.addAnnotationTracesInOtherFiles(locations1));
	}

	// ****************************************************************************************************************

	@Test
	public void getFeatures_ofFolderTest() {
		assertEquals("not existed address", 0, locations.getFeatures_ofFolder("invalidAddress").size());

		assertEquals("existed address but with no trace", 0,
				locations.getFeatures_ofFolder(mainDirecoty + File.separator + folder1).size());

		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder1);
		assertArrayEquals("one feature with a folder trace", new String[] { "f1" },
				locations.getFeatures_ofFolder(mainDirecoty + File.separator + folder1).toArray());

		locations.setTrace_toFolder("f2", mainDirecoty + File.separator + folder1);
		assertArrayEquals("more than one trace to a folder", new String[] { "f1", "f2" },
				locations.getFeatures_ofFolder(mainDirecoty + File.separator + folder1).toArray());

		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder2);
		assertEquals("having more than one folder", 1,
				locations.getFeatures_ofFolder(mainDirecoty + File.separator + folder2).size());
	}

	@Test
	public void getFeatures_ofAllFoldersTest() {
		assertEquals("no folder yet", 0, locations.getFeatures_ofAllFolders().size());

		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder1);
		assertEquals("one feature and folder is already added.", 1, locations.getFeatures_ofAllFolders().size());

		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder2);
		assertEquals("one feature to more than one folder", 1, locations.getFeatures_ofAllFolders().size());

		locations.setTrace_toFolder("f2", mainDirecoty + File.separator + folder1);
		assertEquals("one folder to more than one feature", 2, locations.getFeatures_ofAllFolders().size());
	}

	@Test
	public void getFeatures_ofNonAnnotatedFileTest() {
		assertEquals("not existed address", 0, locations.getFeatures_ofNonAnnotatedFile("invalidAddress").size());

		assertEquals("existed address but with no trace", 0,
				locations.getFeatures_ofNonAnnotatedFile(mainDirecoty + File.separator + file1).size());

		locations.setTrace_toFile("f1", mainDirecoty + File.separator + file1);
		assertArrayEquals("one feature with a file trace", new String[] { "f1" },
				locations.getFeatures_ofNonAnnotatedFile(mainDirecoty + File.separator + file1).toArray());

		locations.setTrace_toFile("f2", mainDirecoty + File.separator + file1);
		assertArrayEquals("more than one trace to a file", new String[] { "f1", "f2" },
				locations.getFeatures_ofNonAnnotatedFile(mainDirecoty + File.separator + file1).toArray());

		locations.setTrace_toFile("f1", mainDirecoty + File.separator + file2);
		assertEquals("more than one file to one feature", 1,
				locations.getFeatures_ofNonAnnotatedFile(mainDirecoty + File.separator + file2).size());
	}

	@Test
	public void getFeatures_ofAllNonAnnotatedFilesTest() {
		assertEquals("no file yet", 0, locations.getFeatures_ofAllNonAnnotatedFiles().size());

		locations.setTrace_toFile("f1", mainDirecoty + File.separator + file1);
		assertEquals("one feature and file is already added.", 1,
				locations.getFeatures_ofAllNonAnnotatedFiles().size());

		locations.setTrace_toFile("f1", mainDirecoty + File.separator + file2);
		assertEquals("one feature to more than one file", 1, locations.getFeatures_ofAllNonAnnotatedFiles().size());

		locations.setTrace_toFile("f2", mainDirecoty + File.separator + file1);
		assertEquals("one file to more than one feature", 2, locations.getFeatures_ofAllNonAnnotatedFiles().size());
	}

	@Test
	public void getFeatures_inAnnotatedFileTest() {
		assertEquals("not existed address", 0, locations.getFeatures_inAnnotatedFile("NoAddress").size());

		assertEquals("not annotated file", 0,
				locations.getFeatures_inAnnotatedFile(mainDirecoty + File.separator + file1).size());

		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file1, validAnnotation);
		assertArrayEquals("one feature in one annotated file", new String[] { "f1" },
				locations.getFeatures_inAnnotatedFile(mainDirecoty + File.separator + file1).toArray());

		locations.setTrace_toAnnotation("f2", mainDirecoty + File.separator + file1, validAnnotation);
		assertArrayEquals("two features in one annotated file", new String[] { "f1", "f2" },
				locations.getFeatures_inAnnotatedFile(mainDirecoty + File.separator + file1).toArray());

		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file2, validAnnotation);
		assertArrayEquals("one features in two annotated files", new String[] { "f1" },
				locations.getFeatures_inAnnotatedFile(mainDirecoty + File.separator + file2).toArray());
	}

	@Test
	public void getFeatures_inAllAnnotatedFilesTest() {
		assertEquals("no annotated file", 0, locations.getFeatures_inAllAnnotatedFiles().size());

		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file1, validAnnotation);
		assertArrayEquals("one feature in one annotated file", new String[] { "f1" },
				locations.getFeatures_inAllAnnotatedFiles().toArray());

		locations.setTrace_toAnnotation("f2", mainDirecoty + File.separator + file1, validAnnotation);
		assertArrayEquals("two features in one annotated file", new String[] { "f1", "f2" },
				locations.getFeatures_inAllAnnotatedFiles().toArray());

		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file2, validAnnotation);
		assertArrayEquals("one feature in two annotated files", new String[] { "f1", "f2" },
				locations.getFeatures_inAllAnnotatedFiles().toArray());

	}

	@Test
	public void getAllFeaturesTest() {
		assertEquals("no added feature", 0, locations.getAllFeatures().size());

		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder1);
		assertArrayEquals("folder trace added", new String[] { "f1" }, locations.getAllFeatures().toArray());

		locations.setTrace_toFile("f2", mainDirecoty + File.separator + file1);
		assertArrayEquals("file trace is added as well", new String[] { "f1", "f2" },
				locations.getAllFeatures().toArray());

		locations.setTrace_toAnnotation("f3", mainDirecoty + File.separator + file2, validAnnotation);
		assertArrayEquals("annotation trace is added as well", new String[] { "f1", "f2", "f3" },
				locations.getAllFeatures().toArray());

		locations.setTrace_toFolder("f3", mainDirecoty + File.separator + folder1);
		assertArrayEquals("shared feature", new String[] { "f1", "f2", "f3" }, locations.getAllFeatures().toArray());

	}

	// ****************************************************************************************************************

	@Test
	public void getFoldersTest() {
		assertEquals("no folder trace", 0, locations.getFolders("f1").size());

		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder1);
		assertArrayEquals("one feature to one folder", new String[] { mainDirecoty + File.separator + folder1 },
				locations.getFolders("f1").toArray());

		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder2);
		assertEquals("one feature to two folders", 2, locations.getFolders("f1").size());

		locations.setTrace_toFolder("f2", mainDirecoty + File.separator + folder1);
		assertEquals("one folder to two features", 2, locations.getFolders("f1").size());
	}

	@Test
	public void getAllFoldersTest() {
		assertEquals("no folder trace", 0, locations.getAllFolders().size());

		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder1);
		assertArrayEquals("one feature to one folder", new String[] { mainDirecoty + File.separator + folder1 },
				locations.getAllFolders().toArray());

		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder2);
		assertEquals("one feature to two folders", 2, locations.getAllFolders().size());

		locations.setTrace_toFolder("f2", mainDirecoty + File.separator + folder1);
		assertEquals("one folder to two features", 2, locations.getAllFolders().size());

	}

	@Test
	public void getNonAnnotatedFilesTest() {
		assertEquals("no file trace", 0, locations.getNonAnnotatedFiles("f1").size());

		locations.setTrace_toFile("f1", mainDirecoty + File.separator + file1);
		assertArrayEquals("one feature to one file", new String[] { mainDirecoty + File.separator + file1 },
				locations.getNonAnnotatedFiles("f1").toArray());

		locations.setTrace_toFile("f1", mainDirecoty + File.separator + file2);
		assertEquals("one feature to two files", 2, locations.getNonAnnotatedFiles("f1").size());

		locations.setTrace_toFile("f2", mainDirecoty + File.separator + file1);
		assertEquals("one folder to two features", 2, locations.getNonAnnotatedFiles("f1").size());
	}

	@Test
	public void getAllNonAnnotatedFilesTest() {
		assertEquals("no file trace", 0, locations.getAllNonAnnotatedFiles().size());

		locations.setTrace_toFile("f1", mainDirecoty + File.separator + file1);
		assertArrayEquals("one feature to one file", new String[] { mainDirecoty + File.separator + file1 },
				locations.getAllNonAnnotatedFiles().toArray());

		locations.setTrace_toFile("f1", mainDirecoty + File.separator + file2);
		assertEquals("one feature to two files", 2, locations.getAllNonAnnotatedFiles().size());

		locations.setTrace_toFile("f2", mainDirecoty + File.separator + file1);
		assertEquals("one folder to two features", 2, locations.getAllNonAnnotatedFiles().size());
	}

	@Test
	public void getAnnotatedFilesTest() {
		assertEquals("not annotated trace", 0, locations.getAnnotatedFiles("f1").size());

		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file1, validAnnotation);
		assertEquals("one feature to one file", 1, locations.getAnnotatedFiles("f1").size());

		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file2, validAnnotation);
		assertEquals("one feature to two file", 2, locations.getAnnotatedFiles("f1").size());

		locations.setTrace_toAnnotation("f2", mainDirecoty + File.separator + file1, validAnnotation);
		assertEquals("one feature to two file", 2, locations.getAnnotatedFiles("f1").size());
	}

	@Test
	public void getAllAnnotatedFilesTest() {
		assertEquals("not annotated trace", 0, locations.getAllAnnotatedFiles().size());

		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file1, validAnnotation);
		assertEquals("one feature to one file", 1, locations.getAllAnnotatedFiles().size());

		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file2, validAnnotation);
		assertEquals("one feature to two file", 2, locations.getAllAnnotatedFiles().size());

		locations.setTrace_toAnnotation("f2", mainDirecoty + File.separator + file1, validAnnotation);
		assertEquals("one file to two features", 2, locations.getAllAnnotatedFiles().size());
	}

	@Test
	public void getAnnotationsTest() {
		assertTrue("no annotated trace", locations.getAnnotations("f1", "noAddress").isEmpty());

		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file1, validAnnotation);
		assertTrue("one feature to one file",
				validAnnotation.equals(locations.getAnnotations("f1", mainDirecoty + File.separator + file1)));

		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file2, validAnnotation);
		assertTrue("one feature to two files",
				validAnnotation.equals(locations.getAnnotations("f1", mainDirecoty + File.separator + file2)));

		locations.setTrace_toAnnotation("f2", mainDirecoty + File.separator + file1, validAnnotation2);
		assertTrue("one file to two features",
				validAnnotation2.equals(locations.getAnnotations("f2", mainDirecoty + File.separator + file1)));

	}

	// ****************************************************************************************************************

	@Test
	public void removeFolderTraceTest() {
		assertFalse("no folder trace yet", locations.removeFolderTrace("f1", "noAddress"));

		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder1);
		assertTrue("remove from one folder",
				locations.removeFolderTrace("f1", mainDirecoty + File.separator + folder1));
		assertEquals("remove from one folder", 0, locations.getAllFolders().size());

		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder1);
		locations.setTrace_toFolder("f2", mainDirecoty + File.separator + folder2);
		assertTrue("remove from two folders",
				locations.removeFolderTrace("f1", mainDirecoty + File.separator + folder1));
		assertArrayEquals("remove from two folders", new String[] { mainDirecoty + File.separator + folder2 },
				locations.getAllFolders().toArray());

	}

	@Test
	public void removeAllFolderTraces_toFolderTest() {
		assertFalse("no folder trace", locations.removeAllFolderTraces_toFolder("noAddress"));

		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder1);
		assertTrue("remove from one folder",
				locations.removeAllFolderTraces_toFolder(mainDirecoty + File.separator + folder1));
		assertEquals("remove from one folder", 0, locations.getAllFolders().size());

		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder1);
		locations.setTrace_toFolder("f2", mainDirecoty + File.separator + folder1);
		assertTrue("remove from one folder having traces of two features",
				locations.removeAllFolderTraces_toFolder(mainDirecoty + File.separator + folder1));
		assertEquals("remove from one folder having traces of two features", 0, locations.getAllFolders().size());

		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder1);
		locations.setTrace_toFolder("f2", mainDirecoty + File.separator + folder1);
		locations.setTrace_toFolder("f2", mainDirecoty + File.separator + folder2);
		assertTrue("checking not to remove anything everytime",
				locations.removeAllFolderTraces_toFolder(mainDirecoty + File.separator + folder1));
		assertArrayEquals("checking not to remove anything everytime",
				new String[] { mainDirecoty + File.separator + folder2 }, locations.getAllFolders().toArray());
	}

	@Test
	public void removeAllFolderTraces_toFeatureTest() {
		assertFalse("no folder trace", locations.removeAllFolderTraces_toFeature("f1"));

		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder1);
		assertTrue("remove from one folder", locations.removeAllFolderTraces_toFeature("f1"));
		assertEquals("remove from one folder", 0, locations.getAllFolders().size());

		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder1);
		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder2);
		assertTrue("remove from one feature having traces of two folders",
				locations.removeAllFolderTraces_toFeature("f1"));
		assertEquals("remove from one feature having traces of two folders", 0, locations.getAllFolders().size());

		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder1);
		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder2);
		locations.setTrace_toFolder("f2", mainDirecoty + File.separator + folder1);
		assertTrue("checking not to remove anything everytime", locations.removeAllFolderTraces_toFeature("f1"));
		assertArrayEquals("checking not to remove anything everytime",
				new String[] { mainDirecoty + File.separator + folder1 }, locations.getAllFolders().toArray());
	}

	@Test
	public void removeFileTrace_featureToFileTest() {
		assertFalse("no file trace yet", locations.removeFileTrace_featureToFile("f1", "noAddress.xxx"));

		locations.setTrace_toFile("f1", mainDirecoty + File.separator + file1);
		assertTrue("remove from one file",
				locations.removeFileTrace_featureToFile("f1", mainDirecoty + File.separator + file1));
		assertEquals("remove from one file", 0, locations.getAllNonAnnotatedFiles().size());

		locations.setTrace_toFile("f1", mainDirecoty + File.separator + file1);
		locations.setTrace_toFile("f2", mainDirecoty + File.separator + file2);
		assertTrue("remove from two files",
				locations.removeFileTrace_featureToFile("f1", mainDirecoty + File.separator + file1));
		assertArrayEquals("remove from two folders", new String[] { mainDirecoty + File.separator + file2 },
				locations.getAllNonAnnotatedFiles().toArray());
	}

	@Test
	public void removeAllFileTraces_toFileTest() {
		assertFalse("no file trace", locations.removeAllFileTraces_toFile("noAddress.xxx"));

		locations.setTrace_toFile("f1", mainDirecoty + File.separator + file1);
		assertTrue("remove from one file", locations.removeAllFileTraces_toFile(mainDirecoty + File.separator + file1));
		assertEquals("remove from one file", 0, locations.getAllNonAnnotatedFiles().size());

		locations.setTrace_toFile("f1", mainDirecoty + File.separator + file1);
		locations.setTrace_toFile("f2", mainDirecoty + File.separator + file1);
		assertTrue("remove from one file having traces of two features",
				locations.removeAllFileTraces_toFile(mainDirecoty + File.separator + file1));
		assertEquals("remove from one file having traces of two features", 0,
				locations.getAllNonAnnotatedFiles().size());

		locations.setTrace_toFile("f1", mainDirecoty + File.separator + file1);
		locations.setTrace_toFile("f2", mainDirecoty + File.separator + file1);
		locations.setTrace_toFile("f2", mainDirecoty + File.separator + file2);
		assertTrue("checking not to remove anything everytime",
				locations.removeAllFileTraces_toFile(mainDirecoty + File.separator + file1));
		assertArrayEquals("checking not to remove anything everytime",
				new String[] { mainDirecoty + File.separator + file2 }, locations.getAllNonAnnotatedFiles().toArray());
	}

	@Test
	public void removeAllFileTraces_toFeatureTest() {
		assertFalse("no file trace yet", locations.removeAllFileTraces_toFeature("f1"));

		locations.setTrace_toFile("f1", mainDirecoty + File.separator + file1);
		assertTrue("remove from one file", locations.removeAllFileTraces_toFeature("f1"));
		assertEquals("remove from one file", 0, locations.getAllNonAnnotatedFiles().size());

		locations.setTrace_toFile("f1", mainDirecoty + File.separator + file1);
		locations.setTrace_toFile("f1", mainDirecoty + File.separator + file2);
		assertTrue("remove from one feature having traces to two files", locations.removeAllFileTraces_toFeature("f1"));
		assertEquals("remove from one feature having traces to two files", 0,
				locations.getAllNonAnnotatedFiles().size());

		locations.setTrace_toFile("f1", mainDirecoty + File.separator + file1);
		locations.setTrace_toFile("f1", mainDirecoty + File.separator + file2);
		locations.setTrace_toFile("f2", mainDirecoty + File.separator + file1);
		assertTrue("checking not to remove anything everytime", locations.removeAllFileTraces_toFeature("f1"));
		assertArrayEquals("checking not to remove anything everytime",
				new String[] { mainDirecoty + File.separator + file1 }, locations.getAllNonAnnotatedFiles().toArray());
	}

	@Test
	public void removeAnnotationsTraceTest() {
		assertFalse("no annotation trace yet", locations.removeAnnotationsTrace("f1", "noAddress.xxx"));

		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file1, validAnnotation);
		assertTrue("removal of the only annotation",
				locations.removeAnnotationsTrace("f1", mainDirecoty + File.separator + file1));
		assertEquals("removal of the only annotation", 0, locations.getAllAnnotatedFiles().size());

		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file1, validAnnotation);
		locations.setTrace_toAnnotation("f2", mainDirecoty + File.separator + file2, validAnnotation);
		assertTrue("remove from more than one annotation",
				locations.removeAnnotationsTrace("f1", mainDirecoty + File.separator + file1));
		assertEquals("remove from more than one annotation", 1, locations.getAllAnnotatedFiles().size());
	}

	@Test
	public void removeAllAnnotationsTraces_toFileTest() {
		assertFalse("no annotation trace yet", locations.removeAllAnnotationsTraces_toFile("noAddress.xxx"));

		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file1, validAnnotation);
		assertTrue("removal of the only annotation",
				locations.removeAllAnnotationsTraces_toFile(mainDirecoty + File.separator + file1));
		assertEquals("removal of the only annotation", 0, locations.getAllAnnotatedFiles().size());

		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file1, validAnnotation);
		locations.setTrace_toAnnotation("f2", mainDirecoty + File.separator + file1, validAnnotation2);
		assertTrue("remove from one file having traces to two features",
				locations.removeAllAnnotationsTraces_toFile(mainDirecoty + File.separator + file1));
		assertEquals("remove from one file having traces to two features", 0, locations.getAllAnnotatedFiles().size());

		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file1, validAnnotation);
		locations.setTrace_toAnnotation("f2", mainDirecoty + File.separator + file1, validAnnotation2);
		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file2, validAnnotation);
		assertTrue("checking not to remove anything everytime",
				locations.removeAllAnnotationsTraces_toFile(mainDirecoty + File.separator + file1));
		assertArrayEquals("checking not to remove anything everytime",
				new String[] { mainDirecoty + File.separator + file2 }, locations.getAllAnnotatedFiles().toArray());
	}

	@Test
	public void removeAllAnnotationsTraces_toFeatureTest() {
		assertFalse("no annotation trace yet", locations.removeAllAnnotationsTraces_toFeature("f1"));

		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file1, validAnnotation);
		assertTrue("removal of the only annotation", locations.removeAllAnnotationsTraces_toFeature("f1"));
		assertEquals("removal of the only annotation", 0, locations.getAllAnnotatedFiles().size());

		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file1, validAnnotation);
		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file2, validAnnotation2);
		assertTrue("remove from one feature having traces to two files",
				locations.removeAllAnnotationsTraces_toFeature("f1"));
		assertEquals("remove from one feature having traces to two files", 0, locations.getAllAnnotatedFiles().size());

		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file1, validAnnotation);
		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file2, validAnnotation2);
		locations.setTrace_toAnnotation("f2", mainDirecoty + File.separator + file1, validAnnotation);
		assertTrue("checking not to remove anything everytime", locations.removeAllAnnotationsTraces_toFeature("f1"));
		assertArrayEquals("checking not to remove anything everytime",
				new String[] { mainDirecoty + File.separator + file1 }, locations.getAllAnnotatedFiles().toArray());
	}

	public void removeAllTracesTest() {
		assertFalse("no trace yet", locations.removeAllTraces());

		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder1);
		assertTrue("folder trace", locations.removeAllTraces());
		assertEquals("folder trace", 0, locations.getAllFolders().size());

		locations.setTrace_toFile("f1", mainDirecoty + File.separator + file1);
		assertTrue("file trace", locations.removeAllTraces());
		assertEquals("file trace", 0, locations.getAllNonAnnotatedFiles().size());

		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file1, validAnnotation);
		assertTrue("annotations trace", locations.removeAllTraces());
		assertEquals("annotations trace", 0, locations.getAllAnnotatedFiles().size());

		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder1);
		locations.setTrace_toFile("f1", mainDirecoty + File.separator + file1);
		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file2, validAnnotation);
		assertTrue("folder+file+annotations traces", locations.removeAllTraces());
		assertEquals("folder+file+annotations traces", 0, locations.getAllFolders().size());
		assertEquals("folder+file+annotations traces", 0, locations.getAllNonAnnotatedFiles().size());
		assertEquals("folder+file+annotations traces", 0, locations.getAllAnnotatedFiles().size());

	}

	// ****************************************************************************************************************

	@Test
	public void existsFolderTraceTest() {
		assertFalse("not existed address", locations.existsFolderTrace("f1", "noAddress"));

		locations.setTrace_toFolder("f1", mainDirecoty + File.separator + folder1);
		assertTrue("one folder trace is existed",
				locations.existsFolderTrace("f1", mainDirecoty + File.separator + folder1));

		locations.setTrace_toFolder("f2", mainDirecoty + File.separator + folder2);
		assertTrue("more than one folder trace is existed",
				locations.existsFolderTrace("f2", mainDirecoty + File.separator + folder2));
	}

	@Test
	public void existsFileTraceTest() {
		assertFalse("not existed address", locations.existsFileTrace("f1", "noAddress.xxx"));

		locations.setTrace_toFile("f1", mainDirecoty + File.separator + file1);
		assertTrue("one file trace is existed", locations.existsFileTrace("f1", mainDirecoty + File.separator + file1));

		locations.setTrace_toFile("f2", mainDirecoty + File.separator + file2);
		assertTrue("more than one file trace is existed",
				locations.existsFileTrace("f2", mainDirecoty + File.separator + file2));
	}

	@Test
	public void existsAnnotationTest() {
		assertFalse("not existed address", locations.existsAnnotationsTrace("f1", "noAddress.xxx"));

		locations.setTrace_toAnnotation("f1", mainDirecoty + File.separator + file1, validAnnotation);
		assertTrue("one annotation trace is existed",
				locations.existsAnnotationsTrace("f1", mainDirecoty + File.separator + file1));

		locations.setTrace_toAnnotation("f2", mainDirecoty + File.separator + file2, validAnnotation);
		assertTrue("more than one annotation trace is existed",
				locations.existsAnnotationsTrace("f2", mainDirecoty + File.separator + file2));
	}

	/*
	 * @Test public void toStringTest ( ) { assertEquals("empty locations" , "",
	 * locations . toString ( ) ) ; locations . add_set_Annotation ( "f1",
	 * mainDirecoty + File . separator + file1, validAnnotation ) ; locations .
	 * add_set_Annotation ( "f1", mainDirecoty + File . separator + file2,
	 * validAnnotation ) ; locations . addFeature_NonAnnotatedAddressTrace (
	 * "f1", mainDirecoty + File . separator + file2 ) ; locations .
	 * addFeature_NonAnnotatedAddressTrace ( "f1", mainDirecoty + File .
	 * separator + file3 ) ; locations . addFeature_NonAnnotatedAddressTrace (
	 * "f1", mainDirecoty + File . separator + folder1 ) ;
	 *
	 * locations . add_set_Annotation ( "f3", mainDirecoty + File . separator +
	 * file1, validAnnotation ) ; locations . add_set_Annotation ( "f3",
	 * mainDirecoty + File . separator + file2, validAnnotation ) ; locations .
	 * addFeature_NonAnnotatedAddressTrace ( "f3", mainDirecoty + File .
	 * separator + file2 ) ; locations . addFeature_NonAnnotatedAddressTrace (
	 * "f3", mainDirecoty + File . separator + file3 ) ; locations .
	 * addFeature_NonAnnotatedAddressTrace ( "f3", mainDirecoty + File .
	 * separator + folder2 ) ; System . out . println ( locations . toString ( )
	 * ) ; }
	 */

}
