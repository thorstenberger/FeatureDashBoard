package se.gu.featuredashboard.TestPackage;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import se.gu.featuredashboard.LocationHandler.Locations.AnnotatedLocations;

public class AnnotatedLocations_Test {

	@Test
	public void setter_getter_SingleLineAnnotationTest() {
		AnnotatedLocations annotations = new AnnotatedLocations();
		List<Integer> lines = new ArrayList<>();
		assertFalse("check Empty single lines annotation seeting", annotations.setSingleLineAnnotations(lines));

		lines.addAll(Arrays.asList(2, 3, 5));
		assertTrue(annotations.setSingleLineAnnotations(lines));
		assertArrayEquals("checki not empty single line seeting", lines.toArray(),
				annotations.getSingleLinAnnotations().toArray());

		lines.remove(0);
		assertEquals("Check input changes not influence the class variable", 3,
				annotations.getSingleLinAnnotations().size());

		lines.add(0);
		assertFalse("check seeting a singleLine list having 0 ", annotations.setSingleLineAnnotations(lines));

		lines.remove(2);
		lines.add(-5);
		assertFalse("check seeting a singleLine list having negative number ",
				annotations.setSingleLineAnnotations(lines));

	}

	@Test
	public void setter_getter_MultipleLineAnnotationTest() {
		AnnotatedLocations annotations = new AnnotatedLocations();
		List<Integer> begins = new ArrayList<>();
		List<Integer> ends = new ArrayList<>();
		assertFalse("check Empty begines and ends multiple lines annotation seeting",
				annotations.setMultipleLineAnnotations(begins, ends));
		begins.add(1);
		assertFalse("check Empty end lines seeting", annotations.setMultipleLineAnnotations(begins, ends));
		begins.remove(0);
		ends.add(10);
		assertFalse("check Empty begin lines seeting", annotations.setMultipleLineAnnotations(begins, ends));
		begins.add(-3);
		assertFalse("check negative begin lines seeting", annotations.setMultipleLineAnnotations(begins, ends));
		begins.remove(0);
		begins.add(1);
		ends.remove(0);
		ends.add(-4);
		assertFalse("check negative end lines seeting", annotations.setMultipleLineAnnotations(begins, ends));
		ends.remove(0);
		ends.addAll(Arrays.asList(10, 11));
		assertFalse("check different sizes for begin and end lines while seeting",
				annotations.setMultipleLineAnnotations(begins, ends));
		begins.add(12);
		assertFalse("check setting when begins with element is in higher line than its corresponding end element",
				annotations.setMultipleLineAnnotations(begins, ends));
		begins.remove(1);
		begins.add(2);
		assertTrue("check setting valid begins and ends", annotations.setMultipleLineAnnotations(begins, ends));
		assertArrayEquals("Check the value of begins", begins.toArray(), annotations.getBeginAnnotations().toArray());
		assertArrayEquals("Check the value of ends", ends.toArray(), annotations.getEndAnnotations().toArray());
		begins.remove(0);
		assertEquals("Check the input pointer influence", 2, annotations.getBeginAnnotations().size());
	}

	@Test
	public void equal_SingleLineAnnotationTest() {
		AnnotatedLocations annotations1 = new AnnotatedLocations();
		AnnotatedLocations annotations2 = new AnnotatedLocations();
		assertTrue("checking equality of two empty annotations", annotations1.equals(annotations2));

		List<Integer> lines = new ArrayList<>();
		lines.addAll(Arrays.asList(2, 3, 5));
		annotations1.setSingleLineAnnotations(lines);
		annotations2.setSingleLineAnnotations(lines);
		assertTrue("checking equality of two equal annotations", annotations1.equals(annotations2));

		lines.remove(1);
		annotations1.setSingleLineAnnotations(lines);
		assertFalse("checking equality of not equal annotations", annotations1.equals(annotations2));

	}

	public void copyTest() {

		List<Integer> lines = new ArrayList<>();
		lines.addAll(Arrays.asList(2, 3, 5));
		List<Integer> begins = new ArrayList<>();
		lines.addAll(Arrays.asList(12, 13, 15));
		List<Integer> ends = new ArrayList<>();
		lines.addAll(Arrays.asList(22, 23, 25));
		AnnotatedLocations annotations1 = new AnnotatedLocations(lines, begins, ends);
		AnnotatedLocations annotations2 = annotations1.copy();
		assertEquals("check equal single line annotations after copy", annotations1.getSingleLinAnnotations(),
				annotations2.getSingleLinAnnotations());
		assertEquals("check equal begin line annotations after copy", annotations1.getBeginAnnotations(),
				annotations2.getBeginAnnotations());
		assertEquals("check equal end line annotations after copy", annotations1.getEndAnnotations(),
				annotations2.getEndAnnotations());

		lines.remove(2);
		annotations1.setSingleLineAnnotations(lines);
		assertEquals("check input pointer will not influence the result", 3,
				annotations2.getSingleLinAnnotations().size());

	}

}
