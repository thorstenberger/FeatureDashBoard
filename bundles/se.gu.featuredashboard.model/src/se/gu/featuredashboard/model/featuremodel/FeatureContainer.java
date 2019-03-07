package se.gu.featuredashboard.model.featuremodel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import se.gu.featuredashboard.model.location.BlockLine;

public class FeatureContainer {
	
	private Feature feature;
	private List<IFile> files;
	private List<BlockLine> annotatedLines;
	private Map<IFile, List<BlockLine>> fileToLines;
	private int tanglingDegree = 0;
	private DecimalFormat df = new DecimalFormat("#.##");
	
	public FeatureContainer(Feature feature) {
		this.feature = feature;
		files = new ArrayList<>();
		annotatedLines = new ArrayList<>();
		this.fileToLines = new HashMap<>();
	}
	
	public FeatureContainer(Feature feature, List<IFile> files, List<BlockLine> annotatedLines) {
		this.feature = feature;
		this.files = files;
		this.annotatedLines = annotatedLines;
		this.fileToLines = new HashMap<>();
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
	
	public void addFileToLines(IFile file, List<BlockLine> annotatedLines) {
		fileToLines.put(file, annotatedLines);
	}
	
	public List<BlockLine> getLines(IFile file){
		return fileToLines.get(file);
	}
	
	public int getLinesOfFeatureCode() {
		int linesOfFeatureCode = 0;
		
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
	
	public Object[] getNestingInfo() {
		int maxDepth = Integer.MIN_VALUE;
		int minDepth = Integer.MAX_VALUE;
		int totalDepth = 0;
		
		for(IFile file : files) {
			int depth = returnMaxDepth(file);
			
			totalDepth += depth;
			
			if(depth < minDepth) {
				minDepth = depth;
			}
			
			if(depth > maxDepth) {
				maxDepth = depth;
			}
		}
		
		//Not very strict but it works for now..
		return new Object[] {maxDepth, minDepth, totalDepth, df.format((double)totalDepth/(double)files.size())};
	}
	
	private int returnMaxDepth(IResource resource) {
		if(resource instanceof IProject) {
			return 0;
		} else {
			return 1 + returnMaxDepth(resource.getParent());
		}
	}
	
}
