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
	
	private Integer maxNestingDepth;
	private Integer minNestingDepth;
	private String avgNestingDepth;
	private Integer totalNestingDepth;
	private Integer LOFC;
	private Integer scatteringDegree;
	private Integer tanglingDegree;
	
	private int fileAnnotations = 0;
	private int folderAnnotations = 0;
	
	private Feature feature;
	private Map<IFile, Integer> tanglingInfo;
	private Map<IFile, List<BlockLine>> fileToLines;
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
	
	public void addFileToBlocks(IFile file, List<BlockLine> annotatedLines) {
		fileToLines.put(file, annotatedLines);
		// When we run the builder we will update a certain FeatureContainer and then we need to recalcualte the metrics
		resetMetrics();
	}
	
	public List<BlockLine> getLines(IFile file){
		return fileToLines.get(file);
	}
	
	public int getLinesOfFeatureCode() {
		if(LOFC == null) {
			LOFC = 0;
			for(List<BlockLine> blocks : getBlocks()) {
				for(BlockLine block : blocks) {
					if(block.getStartLine() == block.getEndLine()) {
						LOFC++;
					}
					LOFC += Math.abs(block.getStartLine() - block.getEndLine());
				}
			}
		}
		return LOFC;
	}
	
	public void incrementNumberOfFolderAnnotations() {
		folderAnnotations++;
	}
	
	public int getNumberOfFolderAnnotations() {
		return folderAnnotations;
	}
	
	public void incrementNumberOfFileAnnotations() {
		fileAnnotations++;
	}
	
	public int getNumberOfFileAnnotations() {
		return fileAnnotations;
	}
	
	public int getScatteringDegree() {
		if(scatteringDegree == null) {
			scatteringDegree = 0;
			for(List<BlockLine> blockLists : getBlocks()) {
				scatteringDegree += blockLists.size();
			}
			scatteringDegree += getNumberOfFolderAnnotations();
		}
		return scatteringDegree;
	}
	
	public void setTanglingDegree(IFile file, int otherFeatures) {
		if(tanglingInfo == null)
			tanglingInfo = new HashMap<>();
		tanglingInfo.put(file, otherFeatures);
	}
	
	public int getTanglingDegree() {
		if(tanglingDegree == null)
			tanglingDegree = tanglingInfo.values().stream().mapToInt(Integer::intValue).sum();
		return tanglingDegree;
	}
	
	public int getMaxND() {
		if(maxNestingDepth == null)
			calculateNestingInfo();
		return maxNestingDepth;
	}
	
	public int getMinND() {
		if(minNestingDepth == null)
			calculateNestingInfo();
		return minNestingDepth;
	}
	
	public int getTotalND() {
		if(totalNestingDepth == null)
			calculateNestingInfo();
		return totalNestingDepth;
	}
	
	public String getAvgND() {
		if(avgNestingDepth == null)
			calculateNestingInfo();
		return avgNestingDepth;
	}
	
	// Calculate all info at the same time
	private void calculateNestingInfo() {
		maxNestingDepth = Integer.MIN_VALUE;
		avgNestingDepth = new String();
		minNestingDepth = Integer.MAX_VALUE;
		totalNestingDepth = 0;
		
		for(IFile file : getFiles()) {
			int depth = returnDepth(file);
			
			totalNestingDepth += depth;
			
			if(depth < minNestingDepth) {
				minNestingDepth = depth;
			}
			
			if(depth > maxNestingDepth) {
				maxNestingDepth = depth;
			}
		}
		
		avgNestingDepth = df.format((double)totalNestingDepth/(double)getScatteringDegree());
	}
	
	private int returnDepth(IResource resource) {
		if(resource instanceof IProject) {
			return 0;
		} else {
			return 1 + returnDepth(resource.getParent());
		}
	}
	
	// When there is an update we need to recalculate the metrics
	private void resetMetrics() {
		tanglingDegree = null;
		maxNestingDepth = null;
		minNestingDepth = null;
		avgNestingDepth = null;
		LOFC = null;
		scatteringDegree = null;
	}
	
}
