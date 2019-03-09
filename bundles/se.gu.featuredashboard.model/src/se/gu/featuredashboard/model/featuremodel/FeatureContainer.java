package se.gu.featuredashboard.model.featuremodel;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import se.gu.featuredashboard.model.location.BlockLine;

public class FeatureContainer {
	
	private Feature feature;
	private Map<IFile, List<BlockLine>> fileToLines;
	private int tanglingDegree = 0;
	private DecimalFormat df = new DecimalFormat("#.##");
	
	public FeatureContainer(Feature feature) {
		this.feature = feature;
		this.fileToLines = new HashMap<>();
	}
	
	public Feature getFeature() {
		return feature;
	}
	
	public Set<IFile> getFiles(){
		return fileToLines.keySet();
	}
	
	public Collection<List<BlockLine>> getBlocks(){	
		return fileToLines.values();
	}
	
	public void addFileToLines(IFile file, List<BlockLine> annotatedLines) {
		fileToLines.put(file, annotatedLines);
	}
	
	public List<BlockLine> getLines(IFile file){
		return fileToLines.get(file);
	}
	
	public int getLinesOfFeatureCode() {
		int linesOfFeatureCode = 0;
		
		for(List<BlockLine> blocks : getBlocks()) {
			for(BlockLine block : blocks) {
				if(block.getStartLine() == block.getEndLine()) {
					linesOfFeatureCode = linesOfFeatureCode + 1;
				}
				linesOfFeatureCode += Math.abs(block.getStartLine() - block.getEndLine());
			}
		}
		
		return linesOfFeatureCode;
	}
	
	public int getScatteringDegree() {
		return getFiles().size();
	}
	
	public int getNumberOfInFileAnnotations() {
		int fileAnnotations = 0;
		for(List<BlockLine> blocks : getBlocks()) {
			for(BlockLine block : blocks) {
				fileAnnotations++;
			}
		}
		return fileAnnotations;
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
		
		for(IFile file : getFiles()) {
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
		return new Object[] {maxDepth, minDepth, totalDepth, df.format((double)totalDepth/(double)getFiles().size())};
	}
	
	private int returnMaxDepth(IResource resource) {
		if(resource instanceof IProject) {
			return 0;
		} else {
			return 1 + returnMaxDepth(resource.getParent());
		}
	}
	
}
