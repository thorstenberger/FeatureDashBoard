package se.gu.featuredashboard.model.metrics;


import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import se.gu.featuredashboard.model.featuremodel.Feature;

/**
 * Contains the feature location dashboard metrics of one resource.
 *@see Feature
 */

public class ResourceMetrics {

	/**
	 * The resource that the metrics belongs to that.
	 * 
	 */
	private IResource resource;
	
	/**
	 * For a file resource, it includes all of the features that are annotated in that file.
	 * </br>
	 * For a container, it is the union of all of the annotated features in its subtree files.
	 */
	private Set<Feature> allFeatures = new HashSet<Feature>();
	
	/**
	 * For a file resource, it includes all of the authors that are mentioned in that file.
	 * </br>
	 * For a container, it is the union of all of the mentioned authors in its subtree files.
	 */
	private Set<String> allAuthors = new HashSet<String>();

	/**
	 * For a container, its value equals to the total number of files that exists in the subtree of
	 * that container. For a file resource, its value equals to zero.
	 */
	private int subtreeFileNumbers =0;
	
	/**
	 * For a file resource, its value equals the total number of lines in that file.
	 * For a container resource, its values equals the total number of lines of files
	 * in the subtree of that container.
	 */
	private int totalLines = 0;
	
	/**
	 * For a file resource, its value equals the total number of nesting depth of all features annotated in that file.
	 * For a container resource, its value equals the total number of nesting depth of all features in the subtree
	 * of that container.
	 * For the files that do not contain any feature, its default values is -1.
	 */
	private int totalNestingDepth =-1;		
	
	/**
	 * For a file resource, its value equals total number of all features annotations in that file.
	 * For a container resource, its value equals total number of all features annotations in the
	 * subtree files of that container.
	 */
	private int totalScatteringDegree = 0;	
	
	public ResourceMetrics(IResource resource) {
		this.resource = resource;
	}
	
	public void addChildMetrics(ResourceMetrics childMetrics) {
		if( this.resource instanceof IProject){ 
			IProject projectParent = (IProject) this.resource;
			
			if(childMetrics.getResource() instanceof IFolder) {
				IFolder folder = (IFolder) childMetrics.getResource();
				
				if(!(folder.getParent() instanceof IProject) || 
						!folder.getParent().getName().equals(projectParent.getName())) {
					System.out.println("Trying to add metrics of folder: "+ folder.getProjectRelativePath()+ 
										" to not parent container, the project: "+ projectParent.getName());
					return;
				}	
			}
			else if(childMetrics.getResource() instanceof IFile) {
				IFile file = (IFile) childMetrics.getResource();
				
				if(!(file.getParent() instanceof IProject) || 
						!file.getParent().getName().equals(projectParent.getName())) {
					System.out.println("Trying to add metrics of folder: "+ file.getProjectRelativePath()+ 
										" to not parent container, the project: "+ projectParent.getName());
					return;
				}
			}else {
				System.out.print("Trying to add neither folder nor file metrics to a project metrics. ");
				System.out.println("adding:"+childMetrics.getResource().getProjectRelativePath()+
						" with parent:"+childMetrics.getResource().getParent()+
						" to project:"+ projectParent.getName());
				return;
			}
		}
		
		if( this.resource instanceof IFolder){ 
			IFolder folderParent = (IFolder) this.resource;
			
			if( childMetrics.getResource() instanceof IFolder ) {

				IFolder folderChild = (IFolder) childMetrics.getResource();
				if(!folderChild.getParent().equals(folderParent)) {
					System.out.println("Trying to add metrics of folder: "+ folderChild.getProjectRelativePath()+ 
										" to not parent folder: "+ folderParent.getProjectRelativePath());
					return;
				}
			}
			else if( childMetrics.getResource() instanceof IFile ) {
				
				IFile fileChild = (IFile) childMetrics.getResource();
				if(!fileChild.getParent().equals(folderParent)) {
					System.out.println("Trying to add metrics of file: "+ fileChild.getProjectRelativePath()+ 
										" to not parent folder: "+ folderParent.getProjectRelativePath());
					return;
				}
			}
			else {
				System.out.println("Trying to add neither folder nor file metrics to a folder metrics. ");
				return;
			}	
		}
		
		if(this.resource instanceof IFile) {
			System.out.println("Trying to add child metrics to file: "+(IFile)resource.getProjectRelativePath());
			return;
		}
				
		childMetrics.getAllFeatures().forEach(feature->{
			if(!this.allFeatures.contains(feature)) {
				this.allFeatures.add(feature);
			}
		});

		childMetrics.getAllAuthors().forEach(author->{
			if(!this.allAuthors.contains(author)){
				this.allAuthors.add(author);
			}
		});
		
		if(childMetrics.getResource() instanceof IFile)		
			this.subtreeFileNumbers +=1;
		else
			this.subtreeFileNumbers += childMetrics.getSubtreeFileNumbers();
		
		this.totalLines += childMetrics.getTotalLines();
		if(childMetrics.getTotalNestingDepth()>0)
			this.totalNestingDepth += childMetrics.getTotalNestingDepth();
		this.totalScatteringDegree += childMetrics.getTotalScatteringDegree();
	}
	
	//***************************************Getters and Setters*********************************//
	
	/**
	 * 
	 * @return the resource that the metrics belong to that
	 */
	public IResource getResource() {
		return resource;
	}
	
	public int getSubtreeFileNumbers() {
		return subtreeFileNumbers;
	}
	public void setSubtreeFileNumbers(int subtreeFileNumbers) {
		if(resource instanceof IFile) {
			this.subtreeFileNumbers=0;
			return;
		}
		this.subtreeFileNumbers = subtreeFileNumbers;
	}
	
	public Set<Feature> getAllFeatures() {
		return allFeatures;
	}
	public void setAllFeatures(Set<Feature> allFeatures) {
		this.allFeatures = allFeatures;
	}
	
	public Set<String> getAllAuthors() {
		return allAuthors;
	}
	public void setAllAuthors(Set<String> authors) {
		allAuthors = authors;
	}
	
	public int getTotalLines() {
		return totalLines;
	}
	public void setTotalLines(int totalLines) {
		this.totalLines = totalLines;
	}
	
	public int getTotalNestingDepth() {
		return totalNestingDepth;
	}
	public void setTotalNestingDepth(int totalNestingDepth) {
		this.totalNestingDepth = totalNestingDepth;
	}
	
	public int getTotalScatteringDegree() {
		return totalScatteringDegree;
	}
	public void setTotalScatteringDegree(int totalScatteringDegree) {
		this.totalScatteringDegree = totalScatteringDegree;
	}
	
	//***************************************Getters and Setters*********************************//
	

}
