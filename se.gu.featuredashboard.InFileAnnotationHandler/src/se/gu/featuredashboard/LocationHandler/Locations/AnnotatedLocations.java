package se.gu.featuredashboard.LocationHandler.Locations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// the annotations specified for one feature in one file
public class AnnotatedLocations {
	private List<Integer> singleLineAnnotations = new ArrayList<>();
	private List<Integer> beginAnnotations = new ArrayList<>();
	private List<Integer> endAnnotations = new ArrayList<>();

	public AnnotatedLocations(List<Integer> singleLineAnnotations, List<Integer> beginAnnotations,
			List<Integer> endAnnotations) {
		this.setSingleLineAnnotations(singleLineAnnotations);
		this.setMultipleLineAnnotations(beginAnnotations, endAnnotations);
	}

	public AnnotatedLocations() {

	}

	public boolean setSingleLineAnnotations(List<Integer> lines) {
		if (lines != null && lines.size() > 0) {
			singleLineAnnotations.clear();
			for (Integer line : lines) {
				if (line <= 0) {
					singleLineAnnotations.clear();
					return false;
				}
				singleLineAnnotations.add((int) line);
			}
			return true;
		}
		return false;
	}

	public List<Integer> getSingleLinAnnotations() {
		return singleLineAnnotations;
	}

	public List<Integer> getBeginAnnotations() {
		return beginAnnotations;
	}

	public List<Integer> getEndAnnotations() {
		return endAnnotations;
	}

	public boolean setMultipleLineAnnotations(List<Integer> begins, List<Integer> ends) {
		if (begins != null && ends != null && begins.size() == ends.size() && begins.size() > 0) {
			for (int i = 0; i < begins.size(); i++) {
				if (begins.get(i) <= 0 || ends.get(i) <= 0 || begins.get(i) >= ends.get(i))
					return false;
			}
			beginAnnotations.clear();
			begins.forEach(begin -> {
				beginAnnotations.add((int) begin);
			});
			endAnnotations.clear();
			ends.forEach(end -> {
				endAnnotations.add((int) end);
			});
			return true;
		}
		return false;
	}

	public void clearAnnotations() {
		singleLineAnnotations.clear();
		beginAnnotations.clear();
		endAnnotations.clear();
	}

	public boolean isEmpty() {
		if (singleLineAnnotations.size() == 0 && beginAnnotations.size() == 0 && endAnnotations.size() == 0)
			return true;
		return false;
	}

	public AnnotatedLocations copy() {
		AnnotatedLocations copiedAnnotation = new AnnotatedLocations();
		copiedAnnotation.setSingleLineAnnotations(singleLineAnnotations);
		copiedAnnotation.setMultipleLineAnnotations(beginAnnotations, endAnnotations);
		return copiedAnnotation;
	}

	@Override
	public String toString() {
		if (isEmpty())
			return "";
		StringBuilder answer = new StringBuilder();
		answer.append("Line(s): ");

		if (singleLineAnnotations.size() != 0) {
			singleLineAnnotations.forEach(line -> {
				answer.append(line + " , ");
			});
		}
		if (beginAnnotations.size() != 0) { // endAnnotations.size !=0 too
			int length = beginAnnotations.size();
			for (int i = 0; i < length; i++) {
				answer.append("(" + beginAnnotations.get(i) + "-" + endAnnotations.get(i) + ") ");
				if (i != length - 1)
					answer.append(", ");
			}
		} else
			answer.deleteCharAt(answer.length() - 2); // removing the extra
														// colon

		return answer.toString();
	}

	@Override
	public boolean equals(Object anAnnotations) {

		if (!(anAnnotations instanceof AnnotatedLocations))
			return false;

		AnnotatedLocations newAnnotations = (AnnotatedLocations) anAnnotations;

		if (singleLineAnnotations.size() != newAnnotations.singleLineAnnotations.size()
				|| beginAnnotations.size() != newAnnotations.beginAnnotations.size()
				|| endAnnotations.size() != newAnnotations.getEndAnnotations().size())
			return false;
		for (Integer lineNum : singleLineAnnotations) {
			if (!newAnnotations.getSingleLinAnnotations().contains((int) lineNum))
				return false;
		}
		for (Integer lineNum : beginAnnotations) {
			if (!newAnnotations.getBeginAnnotations().contains((int) lineNum))
				return false;
		}
		for (Integer lineNum : endAnnotations) {
			if (!newAnnotations.getEndAnnotations().contains((int) lineNum))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(singleLineAnnotations, beginAnnotations, endAnnotations);
	}
}
