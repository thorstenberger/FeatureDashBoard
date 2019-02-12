package se.gu.featuredashboard.testsuit.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.location.BlockLine;
import se.gu.featuredashboard.model.location.FeatureLocationDashboard;

public class FeaturesLocationDashboard_Test {

	FeatureLocationDashboard locations;
	Feature f1;
	Feature f2;
	Feature f3;

	File noFile;
	File file1;
	File file2;

	File noFolder;
	File folder1;
	File folder2;

	ArrayList<BlockLine> annotation1;
	ArrayList<BlockLine> annotation2;
	// ArrayList<BlockLine> invalidAnnotation;
	private static final String mainDirecoty = "src/se/gu/featuredashboard/testsuit/testdata";

	@Before
	public void initialize() {

		locations = new FeatureLocationDashboard();

		f1 = new Feature("f1");
		f2 = new Feature("f2");
		f3 = new Feature("f3");

		noFile = new File("noFile.xxx");
		file1 = new File(mainDirecoty + File.separator + "testFile1.txt");
		file2 = new File(mainDirecoty + File.separator + "testFile2.txt");

		noFolder = new File("noFolder");
		folder1 = new File(mainDirecoty + File.separator + "testFolder1");
		folder2 = new File(mainDirecoty + File.separator + "testFolder2");

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

		// List<Integer> lines3 = new ArrayList<>();
		// List<Integer> begins3 = Arrays.asList(10, 11);
		// List<Integer> ends3 = Arrays.asList(12, 9);
		// invalidAnnotation = new AnnotatedLocations(lines3, begins3, ends3);

	}

	@Test
	public void setTrace_toFolderTest() {
		assertFalse("not existed address of a folder", locations.setTrace_toFolder(f1, noFolder));
		assertEquals("not existed address of a folder", 0, locations.getFolders(f1).size());

		assertTrue("existed address of a folder", locations.setTrace_toFolder(f1, folder1));
		assertEquals(1, locations.getFolders(f1).size());

		assertFalse("previously added feature-folder trace", locations.setTrace_toFolder(f1, folder1));
		assertEquals(1, locations.getFolders(f1).size());

		assertTrue("more than one Folder trace of a feature", locations.setTrace_toFolder(f1, folder2));
		assertEquals(2, locations.getFolders(f1).size());

		assertTrue("more than one folder trace of a folder", locations.setTrace_toFolder(f2, folder1));
		assertEquals(2, locations.getFeatures_ofFolder(folder1).size());
	}

	@Test
	public void setTrace_toFileTest() {
		assertFalse("not existed address of a file", locations.setTrace_toFile(f1, noFile));
		assertEquals("not existed address of a file", 0, locations.getNonAnnotatedFiles(f1).size());

		assertTrue("existed address of a file", locations.setTrace_toFile(f1, file1));
		assertEquals(1, locations.getNonAnnotatedFiles(f1).size());

		assertFalse("previously added feature-file trace", locations.setTrace_toFile(f1, file1));
		assertEquals(1, locations.getNonAnnotatedFiles(f1).size());

		assertTrue("more than one file trace of a feature", locations.setTrace_toFile(f1, file2));
		assertEquals(2, locations.getNonAnnotatedFiles(f1).size());

		assertTrue("more than one file trace of a file", locations.setTrace_toFile(f2, file1));
		assertEquals(2, locations.getFeatures_ofNonAnnotatedFile(file1).size());
	}

	@Test
	public void setTrace_toAnnotationTest() {
		assertFalse("not existed file address", locations.setTrace_toAnnotation(f1, noFile, annotation1));

		assertTrue("valid input", locations.setTrace_toAnnotation(f1, file1, annotation1));
		assertEquals("valid input", 1, locations.getFeatures_inAnnotatedFile(file1).size());

		assertFalse("trying to reset previously added annotation trace",
				locations.setTrace_toAnnotation(f1, file1, annotation2));
		assertEquals("trying to reset previously added annotation trace", 1, locations.getAllAnnotatedFiles().size());

		assertTrue("more than one annotation trace of a feature",
				locations.setTrace_toAnnotation(f1, file2, annotation2));
		assertEquals("more than one annotation trace of a feature", 2, locations.getAnnotatedFiles(f1).size());

		assertTrue("more than one annotation trace of a file", locations.setTrace_toAnnotation(f2, file1, annotation1));
		assertEquals("more than one annotation trace of a feature", 1, locations.getAnnotatedFiles(f2).size());

	}

	/*
	 * @Test public void addAnnotationTracesInOtherFilesTest() {
	 * Features_Locations locations1 = new Features_Locations();
	 * assertFalse("empty locations",
	 * locations.addAnnotationTracesInOtherFiles(locations1));
	 *
	 * locations1.setTrace_toAnnotation("f1", mainDirecoty + File.separator +
	 * file1, validAnnotation); assertTrue("valid locations",
	 * locations.addAnnotationTracesInOtherFiles(locations1));
	 * assertEquals("valid locations", 1,
	 * locations.getAllAnnotatedFiles().size());
	 *
	 * locations1.removeAllTraces(); locations1.setTrace_toAnnotation("f2",
	 * mainDirecoty + File.separator + file1, validAnnotation);
	 * assertFalse("not consistent locations, there is file annotated by both",
	 * locations.addAnnotationTracesInOtherFiles(locations1)); }
	 */

	// *************************************************************************
	// ***************************************

	@Test
	public void getFeatures_ofFolderTest() {
		assertEquals("not existed address", 0, locations.getFeatures_ofFolder(noFolder).size());

		assertEquals("existed address but with no trace", 0, locations.getFeatures_ofFolder(folder1).size());

		locations.setTrace_toFolder(f1, folder1);
		assertEquals("one feature with a folder trace", 1, locations.getFeatures_ofFolder(folder1).size());

		locations.setTrace_toFolder(f2, folder1);
		assertEquals("more than one trace to a folder", 2, locations.getFeatures_ofFolder(folder1).size());

		locations.setTrace_toFolder(f1, folder2);
		assertEquals("having more than one folder", 1, locations.getFeatures_ofFolder(folder2).size());
	}

	@Test
	public void getFeatures_ofAllFoldersTest() {
		assertEquals("no folder yet", 0, locations.getFeatures_ofAllFolders().size());

		locations.setTrace_toFolder(f1, folder1);
		assertEquals("one feature and folder is already added.", 1, locations.getFeatures_ofAllFolders().size());

		locations.setTrace_toFolder(f1, folder2);
		assertEquals("one feature to more than one folder", 1, locations.getFeatures_ofAllFolders().size());

		locations.setTrace_toFolder(f2, folder1);
		assertEquals("one folder to more than one feature", 2, locations.getFeatures_ofAllFolders().size());
	}

	@Test
	public void getFeatures_ofNonAnnotatedFileTest() {
		assertEquals("not existed address", 0, locations.getFeatures_ofNonAnnotatedFile(noFile).size());

		assertEquals("existed address but with no trace", 0, locations.getFeatures_ofNonAnnotatedFile(file1).size());

		locations.setTrace_toFile(f1, file1);
		assertEquals("one feature with a file trace", 1, locations.getFeatures_ofNonAnnotatedFile(file1).size());

		locations.setTrace_toFile(f2, file1);
		assertEquals("more than one trace to a file", 2, locations.getFeatures_ofNonAnnotatedFile(file1).size());

		locations.setTrace_toFile(f1, file2);
		assertEquals("more than one file to one feature", 1, locations.getFeatures_ofNonAnnotatedFile(file2).size());
	}

	@Test
	public void getFeatures_ofAllNonAnnotatedFilesTest() {
		assertEquals("no file yet", 0, locations.getFeatures_ofAllNonAnnotatedFiles().size());

		locations.setTrace_toFile(f1, file1);
		assertEquals("one feature and file is already added.", 1,
				locations.getFeatures_ofAllNonAnnotatedFiles().size());

		locations.setTrace_toFile(f1, file2);
		assertEquals("one feature to more than one file", 1, locations.getFeatures_ofAllNonAnnotatedFiles().size());

		locations.setTrace_toFile(f2, file1);
		assertEquals("one file to more than one feature", 2, locations.getFeatures_ofAllNonAnnotatedFiles().size());
	}

	@Test
	public void getFeatures_inAnnotatedFileTest() {
		assertEquals("not existed address", 0, locations.getFeatures_inAnnotatedFile(noFile).size());

		assertEquals("not annotated file", 0, locations.getFeatures_inAnnotatedFile(file1).size());

		locations.setTrace_toAnnotation(f1, file1, annotation1);
		assertEquals("one feature in one annotated file", 1, locations.getFeatures_inAnnotatedFile(file1).size());

		locations.setTrace_toAnnotation(f2, file1, annotation2);
		assertEquals("two features in one annotated file", 2, locations.getFeatures_inAnnotatedFile(file1).size());

		locations.setTrace_toAnnotation(f1, file2, annotation1);
		assertEquals("one features in two annotated files", 1, locations.getFeatures_inAnnotatedFile(file2).size());
	}

	@Test
	public void getFeatures_inAllAnnotatedFilesTest() {
		assertEquals("no annotated file", 0, locations.getFeatures_inAllAnnotatedFiles().size());

		locations.setTrace_toAnnotation(f1, file1, annotation1);
		assertEquals("one feature in one annotated file", 1, locations.getFeatures_inAllAnnotatedFiles().size());

		locations.setTrace_toAnnotation(f2, file1, annotation2);
		assertEquals("two features in one annotated file", 2, locations.getFeatures_inAllAnnotatedFiles().size());

		locations.setTrace_toAnnotation(f1, file2, annotation1);
		assertEquals("one feature in two annotated files", 2, locations.getFeatures_inAllAnnotatedFiles().size());
	}

	@Test
	public void getAllFeaturesTest() {
		assertEquals("no added feature", 0, locations.getAllFeatures().size());

		locations.setTrace_toFolder(f1, folder1);
		assertEquals("folder trace added", 1, locations.getAllFeatures().size());

		locations.setTrace_toFile(f2, file1);
		assertEquals("file trace is added as well", 2, locations.getAllFeatures().size());

		locations.setTrace_toAnnotation(f3, file2, annotation1);
		assertEquals("annotation trace is added as well", 3, locations.getAllFeatures().size());

		locations.setTrace_toFolder(f3, folder1);
		assertEquals("shared feature", 3, locations.getAllFeatures().size());

	}

	// **********************************************************************************************************************

	@Test
	public void getFoldersTest() {
		assertEquals("no folder trace", 0, locations.getFolders(f1).size());

		locations.setTrace_toFolder(f1, folder1);
		assertEquals("one feature to one folder", 1, locations.getFolders(f1).size());

		locations.setTrace_toFolder(f1, folder2);
		assertEquals("one feature to two folders", 2, locations.getFolders(f1).size());

		locations.setTrace_toFolder(f2, folder1);
		assertEquals("one folder to two features", 2, locations.getFolders(f1).size());
	}

	@Test
	public void getAllFoldersTest() {
		assertEquals("no folder trace", 0, locations.getAllFolders().size());

		locations.setTrace_toFolder(f1, folder1);
		assertEquals("one feature to one folder", 1, locations.getAllFolders().size());

		locations.setTrace_toFolder(f1, folder2);
		assertEquals("one feature to two folders", 2, locations.getAllFolders().size());

		locations.setTrace_toFolder(f2, folder1);
		assertEquals("one folder to two features", 2, locations.getAllFolders().size());

	}

	@Test
	public void getNonAnnotatedFilesTest() {
		assertEquals("no file trace", 0, locations.getNonAnnotatedFiles(f1).size());

		locations.setTrace_toFile(f1, file1);
		assertEquals("one feature to one file", 1, locations.getNonAnnotatedFiles(f1).size());

		locations.setTrace_toFile(f1, file2);
		assertEquals("one feature to two files", 2, locations.getNonAnnotatedFiles(f1).size());

		locations.setTrace_toFile(f2, file1);
		assertEquals("one folder to two features", 2, locations.getNonAnnotatedFiles(f1).size());
	}

	@Test
	public void getAllNonAnnotatedFilesTest() {
		assertEquals("no file trace", 0, locations.getAllNonAnnotatedFiles().size());

		locations.setTrace_toFile(f1, file1);
		assertEquals("one feature to one file", 1, locations.getAllNonAnnotatedFiles().size());

		locations.setTrace_toFile(f1, file2);
		assertEquals("one feature to two files", 2, locations.getAllNonAnnotatedFiles().size());

		locations.setTrace_toFile(f2, file1);
		assertEquals("one folder to two features", 2, locations.getAllNonAnnotatedFiles().size());
	}

	@Test
	public void getAnnotatedFilesTest() {
		assertEquals("not annotated trace", 0, locations.getAnnotatedFiles(f1).size());

		locations.setTrace_toAnnotation(f1, file1, annotation1);
		assertEquals("one feature to one file", 1, locations.getAnnotatedFiles(f1).size());

		locations.setTrace_toAnnotation(f1, file2, annotation1);
		assertEquals("one feature to two file", 2, locations.getAnnotatedFiles(f1).size());

		locations.setTrace_toAnnotation(f2, file1, annotation1);
		assertEquals("one feature to two file", 2, locations.getAnnotatedFiles(f1).size());
	}

	@Test
	public void getAllAnnotatedFilesTest() {
		assertEquals("not annotated trace", 0, locations.getAllAnnotatedFiles().size());

		locations.setTrace_toAnnotation(f1, file1, annotation1);
		assertEquals("one feature to one file", 1, locations.getAllAnnotatedFiles().size());

		locations.setTrace_toAnnotation(f1, file2, annotation1);
		assertEquals("one feature to two file", 2, locations.getAllAnnotatedFiles().size());

		locations.setTrace_toAnnotation(f2, file1, annotation1);
		assertEquals("one file to two features", 2, locations.getAllAnnotatedFiles().size());
	}

	@Test
	public void getAnnotationsTest() {
		assertTrue("no annotated trace", locations.getAnnotations(f1, noFile).isEmpty());

		locations.setTrace_toAnnotation(f1, file1, annotation1);
		assertTrue("one feature to one file", annotation1.equals(locations.getAnnotations(f1, file1)));

		locations.setTrace_toAnnotation(f1, file2, annotation1);
		assertTrue("one feature to two files", annotation1.equals(locations.getAnnotations(f1, file2)));

		locations.setTrace_toAnnotation(f2, file1, annotation2);
		assertTrue("one file to two features", annotation2.equals(locations.getAnnotations(f2, file1)));

	}

	// **********************************************************************************************************************

	@Test
	public void removeFolderTraceTest() {
		assertFalse("no folder trace yet", locations.removeFolderTrace(f1, noFolder));

		locations.setTrace_toFolder(f1, folder1);
		assertTrue("remove from one folder", locations.removeFolderTrace(f1, folder1));
		assertEquals("remove from one folder", 0, locations.getAllFolders().size());

		locations.setTrace_toFolder(f1, folder1);
		locations.setTrace_toFolder(f2, folder2);
		assertTrue("remove from two folders", locations.removeFolderTrace(f1, folder1));
		assertEquals("remove from two folders", 1, locations.getAllFolders().size());
		assertEquals("checking the correct removal", folder2, locations.getAllFolders().get(0));
	}

	@Test
	public void removeAllFolderTraces_toFolderTest() {
		assertFalse("no folder trace", locations.removeAllFolderTraces_toFolder(noFolder));

		locations.setTrace_toFolder(f1, folder1);
		assertTrue("remove from one folder", locations.removeAllFolderTraces_toFolder(folder1));
		assertEquals("remove from one folder", 0, locations.getAllFolders().size());

		locations.setTrace_toFolder(f1, folder1);
		locations.setTrace_toFolder(f2, folder1);
		assertTrue("remove from one folder having traces of two features",
				locations.removeAllFolderTraces_toFolder(folder1));
		assertEquals("remove from one folder having traces of two features", 0, locations.getAllFolders().size());

		locations.setTrace_toFolder(f1, folder1);
		locations.setTrace_toFolder(f2, folder1);
		locations.setTrace_toFolder(f2, folder2);
		assertTrue("checking not to remove anything everytime", locations.removeAllFolderTraces_toFolder(folder1));
		assertEquals("checking not to remove anything everytime", folder2, locations.getAllFolders().get(0));
	}

	@Test
	public void removeAllFolderTraces_toFeatureTest() {
		assertFalse("no folder trace", locations.removeAllFolderTraces_toFeature(f1));

		locations.setTrace_toFolder(f1, folder1);
		assertTrue("remove from one folder", locations.removeAllFolderTraces_toFeature(f1));
		assertEquals("remove from one folder", 0, locations.getAllFolders().size());

		locations.setTrace_toFolder(f1, folder1);
		locations.setTrace_toFolder(f1, folder2);
		assertTrue("remove from one feature having traces of two folders",
				locations.removeAllFolderTraces_toFeature(f1));
		assertEquals("remove from one feature having traces of two folders", 0, locations.getAllFolders().size());

		locations.setTrace_toFolder(f1, folder1);
		locations.setTrace_toFolder(f1, folder2);
		locations.setTrace_toFolder(f2, folder1);
		assertTrue("checking not to remove anything everytime", locations.removeAllFolderTraces_toFeature(f1));
		assertEquals("checking not to remove anything everytime", folder1, locations.getAllFolders().get(0));
	}

	@Test
	public void removeFileTrace_featureToFileTest() {
		assertFalse("no file trace yet", locations.removeFileTrace_featureToFile(f1, noFile));

		locations.setTrace_toFile(f1, file1);
		assertTrue("remove from one file", locations.removeFileTrace_featureToFile(f1, file1));
		assertEquals("remove from one file", 0, locations.getAllNonAnnotatedFiles().size());

		locations.setTrace_toFile(f1, file1);
		locations.setTrace_toFile(f2, file2);
		assertTrue("remove from two files", locations.removeFileTrace_featureToFile(f1, file1));
		assertEquals("remove from two folders", file2, locations.getAllNonAnnotatedFiles().get(0));
	}

	@Test
	public void removeAllFileTraces_toFileTest() {
		assertFalse("no file trace", locations.removeAllFileTraces_toFile(noFile));

		locations.setTrace_toFile(f1, file1);
		assertTrue("remove from one file", locations.removeAllFileTraces_toFile(file1));
		assertEquals("remove from one file", 0, locations.getAllNonAnnotatedFiles().size());

		locations.setTrace_toFile(f1, file1);
		locations.setTrace_toFile(f2, file1);
		assertTrue("remove from one file having traces of two features", locations.removeAllFileTraces_toFile(file1));
		assertEquals("remove from one file having traces of two features", 0,
				locations.getAllNonAnnotatedFiles().size());

		locations.setTrace_toFile(f1, file1);
		locations.setTrace_toFile(f2, file1);
		locations.setTrace_toFile(f2, file2);
		assertTrue("checking not to remove anything everytime", locations.removeAllFileTraces_toFile(file1));
		assertEquals("checking not to remove anything everytime", file2, locations.getAllNonAnnotatedFiles().get(0));
	}

	@Test
	public void removeAllFileTraces_toFeatureTest() {
		assertFalse("no file trace yet", locations.removeAllFileTraces_toFeature(f1));

		locations.setTrace_toFile(f1, file1);
		assertTrue("remove from one file", locations.removeAllFileTraces_toFeature(f1));
		assertEquals("remove from one file", 0, locations.getAllNonAnnotatedFiles().size());

		locations.setTrace_toFile(f1, file1);
		locations.setTrace_toFile(f1, file2);
		assertTrue("remove from one feature having traces to two files", locations.removeAllFileTraces_toFeature(f1));
		assertEquals("remove from one feature having traces to two files", 0,
				locations.getAllNonAnnotatedFiles().size());

		locations.setTrace_toFile(f1, file1);
		locations.setTrace_toFile(f1, file2);
		locations.setTrace_toFile(f2, file1);
		assertTrue("checking not to remove anything everytime", locations.removeAllFileTraces_toFeature(f1));
		assertEquals("checking not to remove anything everytime", file1, locations.getAllNonAnnotatedFiles().get(0));
	}

	@Test
	public void removeAnnotationsTraceTest() {
		assertFalse("no annotation trace yet", locations.removeAnnotationsTrace(f1, noFile));

		locations.setTrace_toAnnotation(f1, file1, annotation1);
		assertTrue("removal of the only annotation", locations.removeAnnotationsTrace(f1, file1));
		assertEquals("removal of the only annotation", 0, locations.getAllAnnotatedFiles().size());

		locations.setTrace_toAnnotation(f1, file1, annotation1);
		locations.setTrace_toAnnotation(f2, file2, annotation2);
		assertTrue("remove from more than one annotation", locations.removeAnnotationsTrace(f1, file1));
		assertEquals("remove from more than one annotation", 1, locations.getAllAnnotatedFiles().size());
	}

	@Test
	public void removeAllAnnotationsTraces_toFileTest() {
		assertFalse("no annotation trace yet", locations.removeAllAnnotationsTraces_toFile(noFile));

		locations.setTrace_toAnnotation(f1, file1, annotation1);
		assertTrue("removal of the only annotation", locations.removeAllAnnotationsTraces_toFile(file1));
		assertEquals("removal of the only annotation", 0, locations.getAllAnnotatedFiles().size());

		locations.setTrace_toAnnotation(f1, file1, annotation1);
		locations.setTrace_toAnnotation(f2, file1, annotation2);
		assertTrue("remove from one file having traces to two features",
				locations.removeAllAnnotationsTraces_toFile(file1));
		assertEquals("remove from one file having traces to two features", 0, locations.getAllAnnotatedFiles().size());

		locations.setTrace_toAnnotation(f1, file1, annotation1);
		locations.setTrace_toAnnotation(f2, file1, annotation2);
		locations.setTrace_toAnnotation(f1, file2, annotation1);
		assertTrue("checking not to remove anything everytime", locations.removeAllAnnotationsTraces_toFile(file1));
		assertEquals("checking not to remove anything everytime", file2, locations.getAllAnnotatedFiles().get(0));
	}

	@Test
	public void removeAllAnnotationsTraces_toFeatureTest() {
		assertFalse("no annotation trace yet", locations.removeAllAnnotationsTraces_toFeature(f1));

		locations.setTrace_toAnnotation(f1, file1, annotation1);
		assertTrue("removal of the only annotation", locations.removeAllAnnotationsTraces_toFeature(f1));
		assertEquals("removal of the only annotation", 0, locations.getAllAnnotatedFiles().size());

		locations.setTrace_toAnnotation(f1, file1, annotation1);
		locations.setTrace_toAnnotation(f1, file2, annotation2);
		assertTrue("remove from one feature having traces to two files",
				locations.removeAllAnnotationsTraces_toFeature(f1));
		assertEquals("remove from one feature having traces to two files", 0, locations.getAllAnnotatedFiles().size());

		locations.setTrace_toAnnotation(f1, file1, annotation1);
		locations.setTrace_toAnnotation(f1, file2, annotation2);
		locations.setTrace_toAnnotation(f2, file1, annotation1);
		assertTrue("checking not to remove anything everytime", locations.removeAllAnnotationsTraces_toFeature(f1));
		assertEquals("checking not to remove anything everytime", file1, locations.getAllAnnotatedFiles().get(0));
	}

	@Test
	public void removeAllTracesTest() {
		assertFalse("no trace yet", locations.removeAllTraces());

		locations.setTrace_toFolder(f1, folder1);
		assertTrue("folder trace", locations.removeAllTraces());
		assertEquals("folder trace", 0, locations.getAllFolders().size());

		locations.setTrace_toFile(f1, file1);
		assertTrue("file trace", locations.removeAllTraces());
		assertEquals("file trace", 0, locations.getAllNonAnnotatedFiles().size());

		locations.setTrace_toAnnotation(f1, file1, annotation1);
		assertTrue("annotations trace", locations.removeAllTraces());
		assertEquals("annotations trace", 0, locations.getAllAnnotatedFiles().size());

		locations.setTrace_toFolder(f1, folder1);
		locations.setTrace_toFile(f1, file1);
		locations.setTrace_toAnnotation(f1, file2, annotation1);
		assertTrue("folder+file+annotations traces", locations.removeAllTraces());
		assertEquals("folder+file+annotations traces", 0, locations.getAllFolders().size());
		assertEquals("folder+file+annotations traces", 0, locations.getAllNonAnnotatedFiles().size());
		assertEquals("folder+file+annotations traces", 0, locations.getAllAnnotatedFiles().size());

	}
	// **********************************************************************************************************************

	@Test
	public void existsFolderTraceTest() {
		assertFalse("not existed address", locations.existsFolderTrace(f1, noFolder));

		locations.setTrace_toFolder(f1, folder1);
		assertTrue("one folder trace is existed", locations.existsFolderTrace(f1, folder1));

		locations.setTrace_toFolder(f2, folder2);
		assertTrue("more than one folder trace is existed", locations.existsFolderTrace(f2, folder2));
	}

	@Test
	public void existsFileTraceTest() {
		assertFalse("not existed address", locations.existsFileTrace(f1, noFile));

		locations.setTrace_toFile(f1, file1);
		assertTrue("one file trace is existed", locations.existsFileTrace(f1, file1));

		locations.setTrace_toFile(f2, file2);
		assertTrue("more than one file trace is existed", locations.existsFileTrace(f2, file2));
	}

	@Test
	public void existsAnnotationTest() {
		assertFalse("not existed address", locations.existsAnnotationsTrace(f1, noFile));
		locations.setTrace_toAnnotation(f1, file1, annotation1);
		assertTrue("one annotation trace exists", locations.existsAnnotationsTrace(f1, file1));

		locations.setTrace_toAnnotation(f2, file2, annotation1);
		assertTrue("more than one annotation trace exists", locations.existsAnnotationsTrace(f2, file2));
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
