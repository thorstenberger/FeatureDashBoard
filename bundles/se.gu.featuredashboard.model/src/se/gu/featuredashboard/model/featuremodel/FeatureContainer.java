package se.gu.featuredashboard.model.featuremodel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;

import se.gu.featuredashboard.model.location.BlockLine;

public class FeatureContainer {
	
	private Feature feature;
	private List<IFile> files;
	private List<BlockLine> annotatedLines;
	private Integer linesOfFeatureCode;
	private int tanglingDegree = 0;
	
	public FeatureContainer(Feature feature) {
		this.feature = feature;
		files = new ArrayList<>();
		annotatedLines = new ArrayList<>();
	}
	
	public FeatureContainer(Feature feature, List<IFile> files, List<BlockLine> annotatedLines) {
		this.feature = feature;
		this.files = files;
		this.annotatedLines = annotatedLines;
	}
	
	public Feature getFeature() {
		return feature;
	}
	
	public List<IFile> getFiles(){
		return files;
	}
	
	public List<BlockLine> getBlocks(){
		return annotatedLines;
	}
	
	public void addFile(IFile file) {
		files.add(file);
	}
	
	public void addFiles(List<IFile> files) {
		this.files.addAll(files);
	}
	
	public void addBlockLine(BlockLine block) {
		annotatedLines.add(block);
	}
	
	public void addBlockLines(List<BlockLine> annotatedLines) {
		this.annotatedLines.addAll(annotatedLines);
	}
	
	public int getLinesOfFeatureCode() {
		if(linesOfFeatureCode != null) {
			return linesOfFeatureCode;
		}
		
		linesOfFeatureCode = new Integer(0);
		
		for(BlockLine block : annotatedLines) {
			if(block.getStartLine() == block.getEndLine()) {
				linesOfFeatureCode = linesOfFeatureCode + 1;
			}
			linesOfFeatureCode += Math.abs(block.getStartLine() - block.getEndLine());
		}
		
		return linesOfFeatureCode;
	}
	
	public int getScatteringDegree() {
		return files.size();
	}
	
	public int getNumberOfInFileAnnotations() {
		return annotatedLines.size();
	}
	
	public int getNuberOfFolderAnnotations() {
		throw new UnsupportedOperationException();
	}
	
	public void incrementTanglingDegree(int increment) {
		this.tanglingDegree += increment;
	}
	
	public int getTanglingDegree() {
		return tanglingDegree;
	}
	
}
