package se.gu.featuredashboard.model.metrics;

import java.awt.Container;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.Preferences;

import se.gu.featuredashboard.model.location.ProjectData;
import se.gu.featuredashboard.model.featuremodel.Feature;
import se.gu.featuredashboard.model.location.BlockLine;
import se.gu.featuredashboard.model.location.FeatureLocation;

public class MetricsCalculator {
	
	ProjectData data;
	List<String> excludedFolders;
	List<String> excludedFileExtensions;
	private final List<String> DEFAULT_EXCLUDED_ANNOTATED_FILES_EXTENSIONS = Arrays.asList("pdf", "class", "DS_Store", "docx");
	private final List<String> DEFAULT_EXCLUDED_FOLDERS_OF_ANNOTATED_FILES = Arrays.asList("bin");
	private final String FEATURE_FILE_MAPPING__FILE_EXTENSION = "feature-file";
	private final String FEATURE_FOLDER_MAPPING__FILE_EXTENSION = "vp-folder";
	
	private List<FeatureResourceMetrics> allFeatureResourceMetrics = new ArrayList<>();
	private List<ResourceMetrics> allResourceMetrics = new ArrayList<>();
	
	public MetricsCalculator(ProjectData data){
		this.data = data;
	}
	
	public ResourceMetrics updateMetrics() {
		if(data.getProject()==null)
			return null;
		
		excludedFolders = getPreferencesExcludedFolders();
		excludedFileExtensions = getPreferencesExcludedFilesExtensions();
		clear();
		return calculateContainerMetrics(data.getProject());
	}
	
	public void clear() {
		allFeatureResourceMetrics.clear();
		allResourceMetrics.clear();
	}
	
	/**
	 * calculating folder-metrics to the given folder and adding it to metrics DB
	 * calculating feature-to-this-folder metrics and adding it to metrics DB
	 * @param resourceName
	 * @return
	 */
	private ResourceMetrics calculateContainerMetrics(IContainer container) {

		try {
			ResourceMetrics ansMetrics = returnInstance(container);
			
			ResourceMetrics childMetrics;
			IFile directFeatureToFileMappingFile = null;
			IFile directFeatureToFolderMappingFile = null;
				
			for(IResource member:container.members()){
				if (member instanceof IFile) {
					IFile theFile = (IFile) member;
					if(theFile.getFileExtension()!=null && excludedFileExtensions.contains(theFile.getFileExtension()))
						continue;
					
					
					if(theFile.getFileExtension()!=null && theFile.getFileExtension().equals(FEATURE_FILE_MAPPING__FILE_EXTENSION) ) {
						directFeatureToFileMappingFile =theFile;		
					}
					
					else if(theFile.getFileExtension()!=null && theFile.getFileExtension().equals(FEATURE_FOLDER_MAPPING__FILE_EXTENSION)) {
						directFeatureToFolderMappingFile = theFile;
					}
					else {
						childMetrics = calculateAndSaveFileMetrics_ofNormalFile(theFile);
						ansMetrics.addChildMetrics(childMetrics);
					}
										
				} else {
					// member is instance of IContainer
					if( excludedFolders.contains(((IFolder)member).getProjectRelativePath().toString()) )
						continue;
					
					childMetrics = calculateContainerMetrics((IContainer)member);			
					ansMetrics.addChildMetrics(childMetrics);
					
					childMetrics.getAllFeatures().forEach(feature->{
						FeatureResourceMetrics aFeatureToParentMetrics = returnInstance(feature, container);
						
						//if there is a feature to folder metrics, that folder include at least one annotated file or one mapping file
						FeatureResourceMetrics aFeatureToChildContainerMetrics = returnInstance(feature, member);
						aFeatureToParentMetrics.addFeature_ChildResourceMetrics(aFeatureToChildContainerMetrics);
					});
				}	
			}
			
			
			if(directFeatureToFileMappingFile!=null) {
				calculateAndSaveFileMetrics_ofFileMappingFile(directFeatureToFileMappingFile);
			}
			
			//supposing to have at most one feature to folder mapping file			
			if(directFeatureToFolderMappingFile!=null) {
				calculateAndSaveFileMetrics_ofFolderMappingFile(directFeatureToFolderMappingFile);
			}
			
			return ansMetrics;
			
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * calculating fileMetrics and adding that to metrics DB
	 * calculating all feature-to-thisFile metrics and adding to DB
	 * updating all feature-to-parentOfThisFile metrics
	 * 
	 * @param file
	 * @return null if the given input file does not exist in the file system
	 */
	private ResourceMetrics calculateAndSaveFileMetrics_ofNormalFile(IFile file) {
		ResourceMetrics thisFileMetrics = new ResourceMetrics(file);
		
		List<FeatureLocation> tracesToThisFile = data.getRelatedLocations(file);
		Set<Feature> allFeatures = new HashSet<Feature>();
		if(!tracesToThisFile.isEmpty())
			allFeatures = tracesToThisFile.stream().map(location->location.getFeature()).collect(Collectors.toSet());
		thisFileMetrics.setAllFeatures(allFeatures);
		
		//thisFileMetrics.setAuthors(authors); // Author names should be added
		
		File systemFile = new File(file.getLocation().toString());
		if(systemFile.exists()) {
			try {thisFileMetrics.setTotalLines(getLineCount(systemFile));} 
			catch (IOException e) {e.printStackTrace();}
		}
		else {
			System.out.println("Error: attempt to calculate metrics for a file which does not exist: "+file.getProjectRelativePath());
			thisFileMetrics.setTotalLines(0);
		}
		
		int totalNestingDepth = 0;
		int totalScatteringDegree=0;
		
		for(FeatureLocation location: tracesToThisFile) {
			List<BlockLine> blocks =location.getBlocklines();			
			if(blocks.isEmpty()) {
				totalScatteringDegree++;
				
				FeatureResourceMetrics aFeatureFileMetrics = returnInstance(location.getFeature(),location.getResource());
				aFeatureFileMetrics.setTotalScatteringDegree(aFeatureFileMetrics.getTotalScatteringDegree()+1);
				
				FeatureResourceMetrics aFeatureParentFileMetrics = returnInstance(location.getFeature(),location.getResource().getParent());

				aFeatureParentFileMetrics.setTotalScatteringDegree(aFeatureParentFileMetrics.getTotalScatteringDegree()+1);
				aFeatureParentFileMetrics.setTotalFileAnnotationLines(1);
					
			}
			else {
				totalScatteringDegree+= blocks.size();
				int resourceDepth = getResourceDepth(location.getResource());
				for(BlockLine blockLine:blocks) {
					totalNestingDepth += (resourceDepth + blockLine.getInFileNestingDepth());
				}
				//******updating feature-resource-metrics and adding it to DB**********
				FeatureResourceMetrics aFeatureFileMetrics = calculateFeatureFileMetrics(location.getFeature(), 
						file, blocks, allFeatures, thisFileMetrics.getAllAuthors());			
				allFeatureResourceMetrics.add(aFeatureFileMetrics);
				
				FeatureResourceMetrics aFeatureToParentMetrics = returnInstance(location.getFeature(), file.getParent());
				aFeatureToParentMetrics.addFeature_ChildResourceMetrics(aFeatureFileMetrics);
				//******updating feature-resource-metrics and adding it to DB**********
			}	
		}
		thisFileMetrics.setTotalNestingDepth(totalNestingDepth);	
		thisFileMetrics.setTotalScatteringDegree(totalScatteringDegree);
		
		
		allResourceMetrics.add(thisFileMetrics);
		
		return thisFileMetrics;
	}
	
	private ResourceMetrics calculateAndSaveFileMetrics_ofFileMappingFile(IFile mappingFile) {
		if( mappingFile.getFileExtension()==null || 
		   !mappingFile.getFileExtension().equals(FEATURE_FILE_MAPPING__FILE_EXTENSION))
			return null;
		
		List<FeatureLocation> allFileMappingLocations =  data.getDirectFileTraces(mappingFile.getParent());
		Map<Feature, ArrayList<IFile>> featureToFiles = new HashMap<Feature, ArrayList<IFile>>();
		Map<IFile, ArrayList<Feature>> fileToFeatures = new HashMap<IFile, ArrayList<Feature>>();
		
		for(FeatureLocation location:allFileMappingLocations) {
			Feature feature = location.getFeature();
			IFile file = (IFile) location.getResource();
			
			ArrayList<IFile> relatedFiles = featureToFiles.get(feature);
			if(relatedFiles==null) {
				relatedFiles = new ArrayList<IFile>(Arrays.asList(file));
				featureToFiles.put(feature, relatedFiles);
			}
			else
				relatedFiles.add(file);
			
			ArrayList<Feature> relatedFeatures = fileToFeatures.get(file);
			if(relatedFeatures==null) {
				relatedFeatures = new ArrayList<Feature>(Arrays.asList(feature));
				fileToFeatures.put(file, relatedFeatures);
			}
			else
				relatedFeatures.add(feature);			
		}
			

		for(IFile aFile:fileToFeatures.keySet()) {
			// updating file-metrics
			ResourceMetrics fileMetrics = returnInstance(aFile);
			fileMetrics.getAllFeatures().addAll(fileToFeatures.get(aFile));
			
			for(Feature feature:fileMetrics.getAllFeatures()) {
				//update commonFeatures of all affected-feature-to-file metrics
				FeatureResourceMetrics featureFileMetrics = returnInstance(feature, aFile);
				featureFileMetrics.setTotalCommonFeatures(fileMetrics.getAllFeatures());
				
				//update commonFeatures of all affected-feature-to-parent metrics
				FeatureResourceMetrics featureParentMetrics = returnInstance(feature, aFile.getParent());
				featureParentMetrics.setTotalCommonFeatures(fileMetrics.getAllFeatures());		
			}
		}
		

		int nestingDepth = getResourceDepth(mappingFile)+1;
		
		for(Feature afeature: featureToFiles.keySet()) {
			
			//adding feature-to-mappingFile-metrics
			FeatureResourceMetrics featureMappingFileMetrics = new FeatureResourceMetrics(afeature,mappingFile);
			featureMappingFileMetrics.setMaxNestingDepth(nestingDepth);
			featureMappingFileMetrics.setMinNestingDepth(nestingDepth);
			featureMappingFileMetrics.setTotalCommonFeatures(featureToFiles.keySet());
			featureMappingFileMetrics.setTotalLines(1);
			featureMappingFileMetrics.setTotalNestingDepths(nestingDepth*featureToFiles.get(afeature).size());
			featureMappingFileMetrics.setTotalScatteringDegree(featureToFiles.get(afeature).size());
			allFeatureResourceMetrics.add(featureMappingFileMetrics);
			
			//update mappingFeature-to-parent metrics
			FeatureResourceMetrics featureParentMetrics = returnInstance(afeature, mappingFile.getParent());
			if( nestingDepth>featureParentMetrics.getMaxNestingDepth())
				featureParentMetrics.setMaxNestingDepth(nestingDepth);
			
			if( featureParentMetrics.getMinNestingDepth()==-1 ||
				nestingDepth<featureParentMetrics.getMinNestingDepth()) {
				featureParentMetrics.setMinNestingDepth(nestingDepth);		
			}
			
			//totalCommonFeatures are already set
				
			featureParentMetrics.setTotalFileAnnotationLines(featureParentMetrics.getTotalFileAnnotations()
					+ featureToFiles.get(afeature).size());
			
			featureParentMetrics.setTotalLines(featureParentMetrics.getTotalLines()+1);
			
			featureParentMetrics.setTotalNestingDepths(featureParentMetrics.getTotalNestingDepths()+
					nestingDepth*featureToFiles.get(afeature).size());
			
			featureParentMetrics.setTotalScatteringDegree(featureParentMetrics.getTotalScatteringDegree()
					+ featureToFiles.get(afeature).size());
		}
		
		//adding mapping-file metrics
		ResourceMetrics mappingFileMetrics = new ResourceMetrics(mappingFile);
		mappingFileMetrics.setAllFeatures(featureToFiles.keySet());
		try {mappingFileMetrics.setTotalLines(getLineCount(new File(mappingFile.getLocation().toString())));} 
		catch (IOException e) {e.printStackTrace();}
		mappingFileMetrics.setTotalNestingDepth( nestingDepth * allFileMappingLocations.size() );
		mappingFileMetrics.setTotalScatteringDegree(allFileMappingLocations.size());
		allResourceMetrics.add(mappingFileMetrics);
		
		//update parent-metrics
		ResourceMetrics parentMetrics = returnInstance(mappingFile.getParent());
		parentMetrics.addChildMetrics(mappingFileMetrics);
			
		return mappingFileMetrics;
	}
	
	private ResourceMetrics calculateAndSaveFileMetrics_ofFolderMappingFile(IFile mappingFile) {
		if( mappingFile.getFileExtension()==null || 
		   !mappingFile.getFileExtension().equals(FEATURE_FOLDER_MAPPING__FILE_EXTENSION))
			return null;
	
		List<FeatureLocation> allFolderMappingLocations =  data.getDirectFolderTraces(mappingFile.getParent());
		int mappingSize = allFolderMappingLocations.size();
		int nestingDepth = getResourceDepth(mappingFile)+1;
		Set<Feature> allMappingFeautres = allFolderMappingLocations.stream()
											.map(featueLocation->featueLocation.getFeature())
											.collect(Collectors.toSet());
		
		for(Feature feature:allMappingFeautres) {
			//add feature-to-mappingFile metrics
			FeatureResourceMetrics featureMappingFileMetrics = new FeatureResourceMetrics(feature,mappingFile);
			featureMappingFileMetrics.setMaxNestingDepth(nestingDepth);
			featureMappingFileMetrics.setMinNestingDepth(nestingDepth);
			featureMappingFileMetrics.setTotalCommonFeatures(allMappingFeautres);
			featureMappingFileMetrics.setTotalLines(1);
			featureMappingFileMetrics.setTotalNestingDepths(nestingDepth);
			featureMappingFileMetrics.setTotalScatteringDegree(1);
			allFeatureResourceMetrics.add(featureMappingFileMetrics);
			
			//add feature-to-parent metrics if it does not exist before
			returnInstance(feature, mappingFile.getParent());
		}
		
		//mapping-file metrics
		ResourceMetrics mappingFileMetrics = new ResourceMetrics(mappingFile);	
		mappingFileMetrics.setAllFeatures(allMappingFeautres);
		try {mappingFileMetrics.setTotalLines(getLineCount(new File(mappingFile.getLocation().toString())));} 
		catch (IOException e) {e.printStackTrace();}
		mappingFileMetrics.setTotalNestingDepth( nestingDepth * mappingSize);
		mappingFileMetrics.setTotalScatteringDegree(mappingSize);
		allResourceMetrics.add(mappingFileMetrics);
		
		//parent-metrics
		ResourceMetrics parentMetrics = returnInstance(mappingFile.getParent());
		parentMetrics.addChildMetrics(mappingFileMetrics);
			
		//updating feature-to-parent metrics
		for(FeatureResourceMetrics featureParentMetrics: getFeature_Resource_Metrics(mappingFile.getParent())) {
			//updating the commonFeatures of all of the feature-to-parent metrics
			featureParentMetrics.getTotalCommonFeatures().addAll(allMappingFeautres);
			
			if(allMappingFeautres.contains(featureParentMetrics.getFeature())) {
				if(nestingDepth>featureParentMetrics.getMaxNestingDepth())
					featureParentMetrics.setMaxNestingDepth(nestingDepth);
				if(featureParentMetrics.getMinNestingDepth()==-1 ||
						nestingDepth<featureParentMetrics.getMinNestingDepth())
					featureParentMetrics.setMinNestingDepth(nestingDepth);
				featureParentMetrics.setTotalFolderAnnotations(1);
				featureParentMetrics.setTotalLines(parentMetrics.getTotalLines());
				featureParentMetrics.setTotalNestingDepths(featureParentMetrics.getTotalNestingDepths()+nestingDepth);
				featureParentMetrics.setTotalScatteringDegree(featureParentMetrics.getTotalScatteringDegree()+1);
			}
		}
		
		return mappingFileMetrics;
	}
	
	private FeatureResourceMetrics calculateFeatureFileMetrics(Feature feature, IFile file, List<BlockLine> blocks, 
																	Set<Feature> commonFeatures, Set<String> authors) {
		FeatureResourceMetrics newMetrics = new FeatureResourceMetrics(feature, file);

		int maxNestingDepth = Integer.MIN_VALUE;
		int minNestingDepth = Integer.MAX_VALUE;
		int totalBlocksSize =0;
		int totalNestingDepths =0;
		int resourceNestingDepth = getResourceDepth(file);
		
		for(BlockLine block: blocks) {
			int featureDepth = resourceNestingDepth+block.getInFileNestingDepth();
			totalNestingDepths+= featureDepth;
			
			if(featureDepth>maxNestingDepth)
				maxNestingDepth = featureDepth;
			if(featureDepth<minNestingDepth)
				minNestingDepth = featureDepth;
			
			totalBlocksSize+=block.getLinesSize();
		}
		
		newMetrics.setAuthors(authors);
		newMetrics.setMaxNestingDepth(maxNestingDepth);
		newMetrics.setMinNestingDepth(minNestingDepth);
		newMetrics.setTotalScatteringDegree(blocks.size());
		newMetrics.setTotalCommonFeatures(commonFeatures);
		newMetrics.setTotalLines(totalBlocksSize);
		newMetrics.setTotalNestingDepths(totalNestingDepths);
		
		return newMetrics;
	}
	
	private FeatureResourceMetrics returnInstance(Feature feature, IResource resource) {
		List<FeatureResourceMetrics> theMetrics = allFeatureResourceMetrics.stream()
				.filter(metrics -> (metrics.getFeature().equals(feature) && metrics.getResource().equals(resource)))
				.collect(Collectors.toList());
		if (theMetrics.size() == 0) {
			FeatureResourceMetrics newMetrics = new FeatureResourceMetrics(feature, resource);
			allFeatureResourceMetrics.add(newMetrics);
			return newMetrics;
		}
		if (theMetrics.size() == 1)
			return theMetrics.get(0);
		else {
			System.out.println("Error in returnInstance(feature,reeource) for: "+ 
				feature.getFeatureID()+", and "+resource.getProjectRelativePath());
			return null;
		}
	}
	
	private ResourceMetrics returnInstance(IResource resource) {
		List<ResourceMetrics> theMetrics = allResourceMetrics.stream()
				.filter(metrics -> (metrics.getResource().equals(resource))).collect(Collectors.toList());
		if (theMetrics.size() == 0) {
			ResourceMetrics newMetrics = new ResourceMetrics(resource);
			allResourceMetrics.add(newMetrics);
			return newMetrics;
		}
		if (theMetrics.size() == 1)
			return theMetrics.get(0);
		else {
			System.out.println("Error in returnInstance(feature) for: "+resource.getProjectRelativePath());
			return null;
		}
	}
	
	public FeatureResourceMetrics getFeature_Resource_Metrics(Feature feature, IResource resource) {
		List<FeatureResourceMetrics> theMetrics = allFeatureResourceMetrics.stream()
			.filter(metrics->(metrics.getFeature().equals(feature) && metrics.getResource().equals(resource)))
			.collect(Collectors.toList());
		if(theMetrics.size()==0) {
			return null;
		}
		if(theMetrics.size()==1)
			return theMetrics.get(0);
		else {
			System.out.println("Error in getFeature_Resource_Metrics(feature,reeource) for: "+ 
					feature.getFeatureID()+", and "+resource.getProjectRelativePath());
			return null;
		}		
	}
	
	public List<FeatureResourceMetrics> getFeature_Resource_Metrics(Feature feature) {
		List<FeatureResourceMetrics> theMetrics = allFeatureResourceMetrics.stream()
			.filter(metrics->(metrics.getFeature().equals(feature) ))
			.collect(Collectors.toList());
		
		return theMetrics;		
	}
	
	public List<FeatureResourceMetrics> getFeature_Resource_Metrics(IResource resource) {
		List<FeatureResourceMetrics> theMetrics = allFeatureResourceMetrics.stream()
			.filter(metrics->(metrics.getResource().equals(resource) ))
			.collect(Collectors.toList());
		
		return theMetrics;		
	}
	
	public ResourceMetrics getResource_Metrics(IResource resource) {
		List<ResourceMetrics> theMetrics = allResourceMetrics.stream()
			.filter(metrics->(metrics.getResource().equals(resource)))
			.collect(Collectors.toList());
		if(theMetrics.size()==0) {
			return null;
		}
		if(theMetrics.size()==1)
			return theMetrics.get(0);
		else {
			System.out.println("Error in getResource_Metrics(resource) for: "+resource.getProjectRelativePath());
			return null;
		}		
	}
	
	public List<ResourceMetrics> getAllResourceMetrics(){
		return allResourceMetrics;
	}
	
	public List<FeatureResourceMetrics> getAllFeatureResourceMetrics(){
		return allFeatureResourceMetrics;
	}
	
	private List<String> getPreferencesExcludedFilesExtensions() {
		Preferences preferences = ConfigurationScope.INSTANCE.getNode("se.gu.featuredashboard.ui.mainPreferences.page");
		Preferences sub1 = preferences.node("node1");
		
		if(sub1.get("initialized", "default").equals("yes")) {	
			ArrayList<String> annotatedFilesExtensions = new ArrayList<String>();
			int i=0;
			
			while(true) {
				String extensionKey = "Extension"+i;
				String extensionValue = sub1.get(extensionKey, "default");
				if(extensionValue.equals("default"))
					break;
				annotatedFilesExtensions.add(extensionValue);
				i++;
			}
			return annotatedFilesExtensions;
		}
		else 
			return DEFAULT_EXCLUDED_ANNOTATED_FILES_EXTENSIONS;	
	}
	
	private List<String> getPreferencesExcludedFolders() {
		Preferences preferences = ConfigurationScope.INSTANCE.getNode("se.gu.featuredashboard.ui.mainPreferences.page");
		Preferences sub1 = preferences.node("node1");
		
		if(sub1.get("initialized", "default").equals("yes")) {	
			ArrayList<String> excludedExtentions = new ArrayList<String>();
			int i=0;
			
			while(true) {
				String folderKey = "Output"+i;
				String folderValue = sub1.get(folderKey, "default");
				if(folderValue.equals("default"))
					break;
				excludedExtentions.add(folderValue);
				i++;
			}
			return excludedExtentions;
		}
		else 
			return DEFAULT_EXCLUDED_FOLDERS_OF_ANNOTATED_FILES;	
	}
		

	/**
	 * Count file rows. 
	 * All files are read one time for annotation parsing. At that point, the information
	 * related to the number of files can be stores somewhere and used here instead of counting 
	 * them again.
	 *
	 * @param file file
	 * @return file row count
	 * @throws IOException
	 */
	public static int getLineCount(File file) throws IOException {

	    try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(file), 1024)) {

	        byte[] c = new byte[1024];
	        boolean empty = true,
	                lastEmpty = false;
	        int count = 0;
	        int read;
	        while ((read = is.read(c)) != -1) {
	            for (int i = 0; i < read; i++) {
	                if (c[i] == '\n') {
	                    count++;
	                    lastEmpty = true;
	                } else if (lastEmpty) {
	                    lastEmpty = false;
	                }
	            }
	            empty = false;
	        }

	        if (!empty) {
	            if (count == 0) {
	                count = 1;
	            } else if (!lastEmpty) {
	                count++;
	            }
	        }

	        return count;
	    }
	}
	
	public static int getResourceDepth(IResource resource) {
		if ( (resource instanceof IProject) || resource.getParent() instanceof IProject) 
			return 0;
		else 
			return 1 + getResourceDepth(resource.getParent());
		
	}
	
}
