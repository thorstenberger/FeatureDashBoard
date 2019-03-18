package se.gu.featuredashboard.model.featuremodel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;

import se.gu.featuredashboard.model.location.BlockLine;

public class FeatureContainer {
	
	private Integer maxNestingDepth;
	private Integer minNestingDepth;
	private String avgNestingDepth;
	private Integer totalNestingDepth;
	private Integer LOFC;
	private Integer scatteringDegree;
	private Integer tanglingDegree;
	private Integer folderAnnotations;
	private Integer fileAnnotations;
	
	private Feature feature;
	private Map<IFile, Tuple<List<BlockLine>, Integer>> inFileAnnotations;
	private Map<IFolder, List<Tuple<IResource, Integer>>> directAnnotations;
	private DecimalFormat df = new DecimalFormat("#.##");
	
	public FeatureContainer(Feature feature) {
		this.feature = feature;
		this.inFileAnnotations = new HashMap<>();
		this.directAnnotations = new HashMap<>();
	}
	
	public Feature getFeature() {
		return feature;
	}
	
	public boolean isAnnotatedIn(IFile file) {
		return inFileAnnotations.containsKey(file);
	}
	
	public boolean isMappedIn(IFolder folder) {
		return directAnnotations.containsKey(folder);
	}
	
	public List<IFile> getFiles(){
		List<IFile> files = new ArrayList<>();
		files.addAll(inFileAnnotations.keySet());
		directAnnotations.values().stream().forEach(resourceList -> {
			resourceList.forEach(resourceTuple -> {
				if(resourceTuple.getLeft() instanceof IFile)
					files.add((IFile) resourceTuple.getLeft());
			});
		});
		
		return files;
	}
	
	public List<BlockLine> getBlocks(){
		List<BlockLine> featureBlocks = new ArrayList<>(); 
		inFileAnnotations.values().stream().map(Tuple::getLeft).forEach(featureBlocks::addAll);
		return featureBlocks;
	}
	
	public int getTanglingDegree() {
		if(tanglingDegree == null)
			tanglingDegree = inFileAnnotations.values().stream().mapToInt(Tuple::getRight).sum();

		return tanglingDegree;
	}
	
	public void removeInAnnotationFile(IFile file) {
		inFileAnnotations.remove(file);
		resetMetrics();
	}
	
	public void removeMapping(IFolder folder) {
		directAnnotations.remove(folder);
		resetMetrics();
	}
	
	public void addInFileAnnotations(IFile file, List<BlockLine> annotatedLines, int otherFeatures) {
		Tuple<List<BlockLine>, Integer> tuple = inFileAnnotations.get(file);
		if(tuple == null)
			tuple = new Tuple<List<BlockLine>, Integer>(annotatedLines, otherFeatures);
		else {
			tuple.setLeft(annotatedLines);
			tuple.setRight(otherFeatures);
		}
		inFileAnnotations.put(file, tuple);
		
		// When we run the builder we will update a certain FeatureContainer and then we need to recalcualte the metrics
		resetMetrics();
	}
	
	public List<BlockLine> getLines(IFile file){
		if(inFileAnnotations.containsKey(file))
			return inFileAnnotations.get(file).getLeft();
		else {
			IFolder parentFolder = findParentFolder(file);
			Tuple<IResource, Integer> tupleToFind = directAnnotations.get(parentFolder).stream().filter(tuple -> {
				if(tuple.getLeft() instanceof IFile)
					return file.equals((IFile) tuple.getLeft());
				else
					return false;
			}).findFirst().get();
			return Arrays.asList(new BlockLine(1, tupleToFind.getRight()));
		}
		
	}
	
	private IFolder findParentFolder(IResource resource) {
		if(resource instanceof IWorkspace)
			return null;
		else if(directAnnotations.containsKey(resource.getParent()))
			return (IFolder) resource.getParent();
		else
			return findParentFolder(resource.getParent());
	}
	
	public int getLinesOfFeatureCode() {
		if(LOFC == null) {
			LOFC = getBlocks().stream().mapToInt(block -> {
				if(block.getStartLine() == block.getEndLine())
					return 1;
				else
					return Math.abs(block.getEndLine()-block.getStartLine());
			}).sum();
			LOFC += directAnnotations.values().stream().mapToInt(resources -> {
				int lines = 0;
				for(Tuple<IResource, Integer> resource : resources)
					lines += resource.getRight();
				return lines;
			}).sum();
		}
		return LOFC;
	}
	
	public void addMappingResource(IFolder folder, List<Tuple<IResource, Integer>> resources) {
		List<Tuple<IResource, Integer>> presentResources = directAnnotations.get(folder);
		if(presentResources != null)
			presentResources.addAll(resources);
		else
			directAnnotations.put(folder, resources);
		resetMetrics();
	}
	
	private void getDirectAnnotationCount() {
		folderAnnotations = 0;
		fileAnnotations = 0;
		for(List<Tuple<IResource, Integer>> directAnnotations : directAnnotations.values()) {
			for(Tuple<IResource, Integer> tuple : directAnnotations) {
				if(tuple.getLeft() instanceof IFolder)
					folderAnnotations++;
				else if(tuple.getLeft() instanceof IFile)
					fileAnnotations++;
			}
		}
	}
	
	public int getNumberOfFolderAnnotations() {
		if(folderAnnotations == null)
			getDirectAnnotationCount();
		return folderAnnotations;
	}
	
	public int getNumberOfFileAnnotations() {
		if(fileAnnotations == null)
			getDirectAnnotationCount();
		return fileAnnotations;
	}
	
	public int getScatteringDegree() {
		if(scatteringDegree == null) {
			scatteringDegree = 0;
			scatteringDegree += getBlocks().size();
			scatteringDegree += getNumberOfFileAnnotations();
			scatteringDegree += getNumberOfFolderAnnotations();
		}
		return scatteringDegree;
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
		
		// Calculate for inFileAnnotations
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
		folderAnnotations = null;
		fileAnnotations = null;
	}

}
