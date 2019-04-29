package se.gu.featuredashboard.model.metrics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;

import se.gu.featuredashboard.model.featuremodel.Feature;

/**
 * Contains the feature location dashboard metrics regarding one feature 
 * to one resource. If the name of the feature does not exist in this resource or 
 * the subtree files of this resource, this object is invalid.
 * 
 *@see Feature
 */

public class FeatureResourceMetrics {
	
	/**
	 * The feature that the metrics belongs to that.
	 * 
	 *@see Feature
	 */
	private Feature feature;
	
	/**
	 * The resource that the metrics belongs to that.
	 * 
	 */
	private IResource resource;
	
	/**
	 * For a file resource, which this feature is annotated in that file, it includes all of the author names appear
	 * in that file.
	 * <br>
	 * For a container, which this feature is annotated in some files of its subtree, it includes all of the
	 * author names in this feature's annotated files in the subtree of this resource.
	 * 
	 */
	private Set<String> authors = new HashSet<String>();
	
	/**
	 * For a file resource, its value does not have any meaning and must be zero.
	 * <br>
	 * For a container, its value represents the total number of this feature's direct file annotations
	 * in the subtree of this container, in the feature-to-file mapping files.
	 */
	private int totalFileAnnotations= 0;
	
	/**
	 * For a file resource, its value does not have any meaning and must be zero.
	 * <br>
	 * For a container, its value represents the total number of this feature's direct folder annotations
	 * in the subtree of this container, in the feature-to-folder mapping files.
	 */
	private int totalFolderAnnotations= 0;	
	
	/**
	 * For a file resource that this feature is annotated in that file with Regex expressions,
	 * it is the minimum nesting depth of the feature annotations. For a feature to file and folder
	 * mapping file resource, all annotations has the same nesting depth, as well as the minimum 
	 * nesting depth, and it is the depth of the resource plus one.
	 * <p>
	 * For a container, it is the minimum nesting depth of that feature in the subtree files
	 * of the container.
	 * <p>
	 * The default value is -1, which means this field is not initialized yet. However, in a valid
	 * object of this class, because there is an annotation of this feature to this resource
	 * or a file in the subtree of this resource, its value must be more or equal to 1 (the smallest 
	 * annotation depth appears in a file member of a project, which is 1).
	 * 
	 */
	private int minNestingDepth = -1;		
	
	/**
	 * For a file resource that this feature is annotated in that file with Regex expressions,
	 * it is the maximum nesting depth of the feature annotations. For a feature to file and folder
	 * mapping file resource, all annotations has the same nesting depth, as well as the maximum 
	 * nesting depth, and it is the depth of the resource plus one.
	 * <p>
	 * For a container, it is the maximum nesting depth of that feature in the files in the
	 * subtree of the container.
	 * <p>
	 * The default value is -1, which means this field is not initialized yet. However, in a valid
	 * object of this class, because there is an annotation of this feature to this resource
	 * or a file in the subtree of this resource, its value must be more or equal to 1 (the smallest 
	 * annotation depth appears in a file member of a project, which is 1).
	 * 
	 */
	private int maxNestingDepth = -1 ;
	
	/**
	 * For a file resource that this feature is annotated in that file, it includes all of the features
	 * that are annotated in that file.
	 * <br>
	 * For a container that this feature is annotated in some subtree files of that resource, it is the union of
	 * all of the common features in those files.
	 */
	private Set<Feature> allCommonFeatures = new HashSet<Feature>();
	
	/**
	 * For a file resource that this feature is annotated in that file, it is the summation of all of the lines
	 * that belongs to this feature.
	 * <br>
	 * For a container that this feature is annotated in some subtree files of that resource, it is the summation
	 * of all of the lines that belong to this feature. 
	 * <p>
	 * If a folder is directly mapped to a feature, it is the total lines of all files in the subtree of that folder. The 
	 * valid value is more than zero, because the feature is annotated in at least one Regex expression or one line
	 * in a feature-to-file or feature-to-folder mapping file.
	 */
	private int totalLines = 0;		
	
	/**
	 * For a file resource that this feature is annotated in that file with Regex expressions,
	 * it is the total nesting depth of the feature annotations. For a file resource 
	 * that is used for direct mapping of this feature to files and folder, all annotations has 
	 * the same nesting depth, and it is the depth of the resource plus one.
	 * <p>
	 * For a container, it is the total nesting depth of that feature in the files in the
	 * subtree of the container.
	 * <p>
	 * The default value is -1, which means this field is not initialized yet. However, in a valid
	 * object of this class, because there is an annotation of this feature to this resource, if it is file,
	 * or a file in the subtree of this resource, it must be a value more or equal to 1 (the smallest 
	 * annotation depth appears in a file member of a project, which is 1).
	 * 
	 */
	private int totalNestingDepths=-1;
	
	/**
	 * For a file resource that this feature is annotated in that file with Regex expressions,
	 * it is the total number of blocks that refers to this feature. For a file resource 
	 * that is used for direct mapping of this feature to files and folder, its value is one.
	 * <p>
	 * For a container, it is the total values of that for the same feature in the files in the
	 * subtree of the container.
	 * <p>
	 * The default value is 0, but a valid value is more than 0.
	 * 
	 */
	private int totalScatteringDegree = 0;

	
	public FeatureResourceMetrics(Feature feature, IResource resource){
		this.feature = feature;
		this.resource = resource;
	}
	
	public void addFeature_ChildResourceMetrics(FeatureResourceMetrics childMetrics) {
		if(!childMetrics.getFeature().equals(feature)) {
			System.out.println("Wrongly Trying to add feature metrics of feaure: "+childMetrics.getFeature().getFeatureID()+ 
					" to the metrics of feature: "+ feature.getFeatureID());
			return;
		}
			
		childMetrics.getAuthors().forEach(author->{
			if(!this.authors.contains(author))
				this.authors.add(author);
		});
		
		if(childMetrics.getMaxNestingDepth()!=-1) {
			if(maxNestingDepth==-1)
				maxNestingDepth = Integer.MIN_VALUE;
			if(childMetrics.getMaxNestingDepth()>maxNestingDepth)
				maxNestingDepth = childMetrics.getMaxNestingDepth();
		}
		
		if(childMetrics.getMinNestingDepth()!=-1) {
			if(minNestingDepth==-1)
				minNestingDepth = Integer.MAX_VALUE;
			if(childMetrics.getMinNestingDepth()<minNestingDepth)
				minNestingDepth = childMetrics.getMinNestingDepth();	
		}
		
		childMetrics.getTotalCommonFeatures().forEach(feature->{
			if(!allCommonFeatures.contains(feature))
				allCommonFeatures.add(feature);
		});
		
		totalFileAnnotations += childMetrics.getTotalFileAnnotations();
		totalFolderAnnotations += childMetrics.getTotalFolderAnnotations();
		totalLines+=childMetrics.getTotalLines();
		
		if(childMetrics.getTotalNestingDepths()!=-1) {
			if(totalNestingDepths==-1)
				totalNestingDepths=0;
			totalNestingDepths+=childMetrics.getTotalNestingDepths();
		}

		totalScatteringDegree+=childMetrics.getTotalScatteringDegree();	
	}
		
	
	//***************************************Getters and Setters*********************************//

	/**
	 * 
	 * @return the feature which this metrics belong to that
	 */
	public Feature getFeature() {
		return feature;
	}
	
	/**
	 * 
	 * @return the resource which this metrics belong to that
	 */
	public IResource getResource() {
		return resource;
	}

	
	public int getTotalScatteringDegree() {
		return totalScatteringDegree;
	}

	public void setTotalScatteringDegree(int totalScatteringDegree) {
		this.totalScatteringDegree = totalScatteringDegree;
	}

	
	public int getTotalFileAnnotations() {
		return totalFileAnnotations;
	}

	public void setTotalFileAnnotationLines(int totalFileAnnotations) { 
		this.totalFileAnnotations = totalFileAnnotations;
	}

	
	public int getTotalFolderAnnotations() {
		return totalFolderAnnotations;
	}

	public void setTotalFolderAnnotations(int totalFolderAnnotations) {
		this.totalFolderAnnotations = totalFolderAnnotations;
	}

	
	public int getTanglingDegree() {
		return allCommonFeatures.size()-1;
	}


	public Set<Feature> getTotalCommonFeatures(){
		return allCommonFeatures;
	}

	public void setTotalCommonFeatures(Set<Feature> commonFeatures) {
		allCommonFeatures = commonFeatures;
	}
	
	
	public int getTotalLines() {
		return totalLines;
	}

	public void setTotalLines(int totalLines) {
		this.totalLines = totalLines;
	}

	
	public int getMaxNestingDepth() {
		return maxNestingDepth;
	}

	public void setMaxNestingDepth(int maxNestingDepth) {
		this.maxNestingDepth = maxNestingDepth;
	}

	
	public int getMinNestingDepth() {
		return minNestingDepth;
	}

	public void setMinNestingDepth(int minNestingDepth) {
		this.minNestingDepth = minNestingDepth;
	}

	
	public int getTotalNestingDepths() {
		return totalNestingDepths;
	}

	public void setTotalNestingDepths(int totalNestingDepths) {
		this.totalNestingDepths = totalNestingDepths;
	}

	public double getAverageNestingDepth() {
		if(totalScatteringDegree>0 && totalNestingDepths>0)
			return (float)totalNestingDepths/totalScatteringDegree;
		return -1;
	}
	
	public Set<String> getAuthors() {
		return authors;
	}

	public void setAuthors(Set<String> authors) {
		this.authors = authors;
	}
	
	//***************************************Getters and Setters*********************************//
	
}
