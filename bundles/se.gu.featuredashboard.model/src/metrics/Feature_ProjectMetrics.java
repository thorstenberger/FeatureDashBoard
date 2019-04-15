package metrics;

import se.gu.featuredashboard.model.featuremodel.Feature;

public class Feature_ProjectMetrics {
	
	private Feature feature;
	public Feature_ProjectMetrics(Feature feature){
		this.feature = feature;
	}

	private Integer maxNestingDepth;	//refers to the existence of feature in the maximum depth file
	private Integer minNestingDepth;
	private String avgNestingDepth;
	private Integer totalNestingDepth; 	// summation of all the nesting depths
	private Integer LOFC;				// lines of feature code (summation of blocklines)
	private Integer scatteringDegree; 	// the number of block line plus number of feature file and folder and subfolder reference
	private Integer tanglingDegree;		// the number of features that share a resource with that feature
	private Integer folderAnnotations;	// how many folders and subfolders are mentioned in .feature_folder
	private Integer fileAnnotations;	// how many folders and subfolders are mentioned in .feature_file

	public Integer getMaxNestingDepth() {
		return maxNestingDepth;
	}
	public void setNestingDepth(Integer nestingDepth) {
		if(nestingDepth > maxNestingDepth) {
			maxNestingDepth = nestingDepth;
		}
		this.maxNestingDepth = maxNestingDepth;
	}
	public Integer getMinNestingDepth() {
		return minNestingDepth;
	}
	public void setMinNestingDepth(Integer minNestingDepth) {
		this.minNestingDepth = minNestingDepth;
	}
	public String getAvgNestingDepth() {
		return avgNestingDepth;
	}
	public void setAvgNestingDepth(String avgNestingDepth) {
		this.avgNestingDepth = avgNestingDepth;
	}
	public Integer getTotalNestingDepth() {
		return totalNestingDepth;
	}
	public void setTotalNestingDepth(Integer totalNestingDepth) {
		this.totalNestingDepth = totalNestingDepth;
	}
	public Integer getLOFC() {
		return LOFC;
	}
	public void setLOFC(Integer lOFC) {
		LOFC = lOFC;
	}
	public Integer getScatteringDegree() {
		return scatteringDegree;
	}
	public void setScatteringDegree(Integer scatteringDegree) {
		this.scatteringDegree = scatteringDegree;
	}
	public Integer getTanglingDegree() {
		return tanglingDegree;
	}
	public void setTanglingDegree(Integer tanglingDegree) {
		this.tanglingDegree = tanglingDegree;
	}
	public Integer getFolderAnnotations() {
		return folderAnnotations;
	}
	public void setFolderAnnotations(Integer folderAnnotations) {
		this.folderAnnotations = folderAnnotations;
	}
	public Integer getFileAnnotations() {
		return fileAnnotations;
	}
	public void setFileAnnotations(Integer fileAnnotations) {
		this.fileAnnotations = fileAnnotations;
	}

	
	
	
}
