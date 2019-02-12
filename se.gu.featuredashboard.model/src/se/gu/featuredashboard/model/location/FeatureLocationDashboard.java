package se.gu.featuredashboard.model.location;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import se.gu.featuredashboard.model.featuremodel.Feature;

public class FeatureLocationDashboard {
	private List<FeatureFolderLocation> folderLocations = new ArrayList<>();
	private List<FeatureFileLocation> fileLocations = new ArrayList<>();
	private List<FeatureAnnotationsLocation> annotationsLocations = new ArrayList<>();

	// *********************************************addTrace*************************************************

	public boolean setTrace_toFolder(Feature feature, File fileAddress) {
		FeatureFolderLocation folderLocation = new FeatureFolderLocation(feature, fileAddress);
		if (folderLocation.isInitializedLocation()) {// valid input
			if (!folderLocations.contains(folderLocation)) {
				folderLocations.add(folderLocation);
				return true;
			}
			return false;
		}
		return false;
	}

	public boolean setTrace_toFolder(FeatureFolderLocation folderLocation){
		return setTrace_toFolder(folderLocation.getFeature(), folderLocation.getFileAddress());
	}

	public boolean resetAllFolderTraces( List<FeatureFolderLocation> newFolderLocations){
		List<FeatureFolderLocation> addedLocations = new ArrayList<>();
		for (FeatureFolderLocation featureFolderLocation : newFolderLocations) {
			if(setTrace_toFolder(featureFolderLocation))
				addedLocations.add(featureFolderLocation);

			else{
				addedLocations.forEach(location->{
					folderLocations.remove(location);
				});
				return false;
			}
		}
		return true;
	}

	public boolean setTrace_toFile(Feature feature, File fileAddress) {
		FeatureFileLocation fileLocation = new FeatureFileLocation(feature, fileAddress);
		if (fileLocation.isInitializedLocation()) {
			if (!fileLocations.contains(fileLocation)) {
				fileLocations.add(fileLocation);
				return true;
			}
			return false;
		}
		return false;
	}

	public boolean setTrace_toFile(FeatureFileLocation fileLocation){
		return setTrace_toFile(fileLocation.getFeature(), fileLocation.getFileAddress());
	}


	public boolean resetAllFileTraces( List<FeatureFileLocation> newFileLocations){
		List<FeatureFileLocation> addedLocations = new ArrayList<>();
		for (FeatureFileLocation featureFileLocation : newFileLocations) {
			if(setTrace_toFile(featureFileLocation))
				addedLocations.add(featureFileLocation);

			else{
				addedLocations.forEach(location->{
					fileLocations.remove(location);
				});
				return false;
			}
		}
		return true;
	}

	public boolean setTrace_toAnnotation(Feature feature, File fileAddress, List<BlockLine> blockLines) {
		if (existsAnnotationsTrace(feature, fileAddress))
			return false;
		FeatureAnnotationsLocation featureAnnotations = new FeatureAnnotationsLocation(feature, fileAddress,
				blockLines);
		if (featureAnnotations.isInitializedLocation()) {
			if (!annotationsLocations.contains(featureAnnotations)) {
				annotationsLocations.add(featureAnnotations);
				return true;
			}
			return false;
		}
		return false;
	}


	public boolean setTrace_toAnnotation(FeatureAnnotationsLocation annotationLocation){
		return setTrace_toAnnotation(annotationLocation.getFeature(), annotationLocation.getFileAddress(), annotationLocation.getBlocklines());
	}

	// ******************************************************************************************************

	public List<FeatureFolderLocation> getAllFolderLocations(){
		return folderLocations;
	}

	public List<FeatureFileLocation> getAllFileLocations(){
		return fileLocations;
	}

	public List<FeatureAnnotationsLocation> getAllAnnotationLocations(){
		return annotationsLocations;
	}

	// ********************************************getFeatures***********************************************

	public boolean resetAllAnnotationTraces(List<FeatureAnnotationsLocation> annotations) {
		List<FeatureAnnotationsLocation> addedLocations = new ArrayList<>();
		for (FeatureAnnotationsLocation annotationLocation : annotations) {
			if(setTrace_toAnnotation(annotationLocation))
				addedLocations.add(annotationLocation);

			else{
				addedLocations.forEach(location->{
					annotationsLocations.remove(location);
				});
				return false;
			}
		}
		return true;
	}

	public List<Feature> getFeatures_ofFolder(File address) {
		Set<Feature> answer = new HashSet<>();
		folderLocations.forEach(featureFolder -> {
			if (featureFolder.getFileAddress().equals(address)) {
				answer.add(featureFolder.getFeature());
			}
		});
		return new ArrayList<Feature>(answer);
	}

	public List<Feature> getFeatures_ofAllFolders() {
		Set<Feature> answer = new HashSet<>();
		folderLocations.forEach(featureFolder -> {
			answer.add(featureFolder.getFeature());
		});
		return new ArrayList<Feature>(answer);
	}

	public List<Feature> getFeatures_ofNonAnnotatedFile(File address) {
		Set<Feature> answer = new HashSet<>();
		fileLocations.forEach(featureFile -> {
			if (featureFile.getFileAddress().equals(address))
				answer.add(featureFile.getFeature());

		});
		return new ArrayList<Feature>(answer);
	}

	public List<Feature> getFeatures_ofAllNonAnnotatedFiles() {
		Set<Feature> answer = new HashSet<>();
		fileLocations.forEach(featureFile -> {
			answer.add(featureFile.getFeature());
		});
		return new ArrayList<Feature>(answer);
	}

	public List<Feature> getFeatures_inAnnotatedFile(File fileAddress) {
		Set<Feature> answer = new HashSet<>();
		annotationsLocations.forEach((featureAnnotation) -> {
			if (featureAnnotation.getFileAddress().equals(fileAddress))
				answer.add(featureAnnotation.getFeature());
		});
		return new ArrayList<Feature>(answer);
	}

	public List<Feature> getFeatures_inAllAnnotatedFiles() {
		Set<Feature> answer = new HashSet<>();
		annotationsLocations.forEach((featureAnnotation) -> {
			answer.add(featureAnnotation.getFeature());
		});
		return new ArrayList<Feature>(answer);
	}

	public List<Feature> getAllFeatures() {
		Set<Feature> featuresOfFolders = new HashSet<>(getFeatures_ofAllFolders());
		Set<Feature> featuresOfNonAnnotationFiles = new HashSet<>(getFeatures_ofAllNonAnnotatedFiles());
		Set<Feature> featuresInAnnotationFiles = new HashSet<>(getFeatures_inAllAnnotatedFiles());

		Set<Feature> answer = new HashSet<>(featuresOfFolders);
		answer.addAll(featuresOfNonAnnotationFiles);
		answer.addAll(featuresInAnnotationFiles);
		return new ArrayList<>(answer);
	}
	// ******************************************************************************************************

	// *******************************************getLocations***********************************************

	public List<File> getFolders(Feature feature) {
		Set<File> answer = new HashSet<>();
		folderLocations.forEach(featureFolder -> {
			if (featureFolder.getFeature().equals(feature)) {
				answer.add(featureFolder.getFileAddress());
			}
		});
		return new ArrayList<File>(answer);
	}

	public List<File> getAllFolders() {
		Set<File> answer = new HashSet<>();
		folderLocations.forEach(featureFolder -> {
			answer.add(featureFolder.getFileAddress());
		});
		return new ArrayList<File>(answer);
	}

	public List<File> getNonAnnotatedFiles(Feature feature) {
		Set<File> answer = new HashSet<>();
		fileLocations.forEach(featureFile -> {
			if (featureFile.getFeature().equals(feature)) {
				answer.add(featureFile.getFileAddress());
			}
		});
		return new ArrayList<File>(answer);
	}

	public List<File> getAllNonAnnotatedFiles() {
		Set<File> answer = new HashSet<>();
		fileLocations.forEach(featureFile -> {
			answer.add(featureFile.getFileAddress());
		});
		return new ArrayList<File>(answer);
	}

	public List<File> getAnnotatedFiles(Feature feature) {
		Set<File> answer = new HashSet<>();
		annotationsLocations.forEach((featureAnnotations) -> {
			if (featureAnnotations.getFeature().equals(feature))
				answer.add(featureAnnotations.getFileAddress());
		});
		return new ArrayList<>(answer);
	}

	public List<File> getAllAnnotatedFiles() {
		Set<File> answer = new HashSet<>();
		annotationsLocations.forEach((featureAnnotations) -> {
			answer.add(featureAnnotations.getFileAddress());
		});
		return new ArrayList<>(answer);
	}

	public List<BlockLine> getAnnotations(Feature feature, File fileAddress) {
		for (FeatureAnnotationsLocation featureAnnotations : annotationsLocations) {
			if (featureAnnotations.getFeature().equals(feature)
					&& featureAnnotations.getFileAddress().equals(fileAddress))
				return featureAnnotations.getBlocklines();
		}
		return new ArrayList<>();
	}

	// ******************************************************************************************************

	// **********************************************RemoveTrace*********************************************

	public boolean removeFolderTrace(Feature feature, File fileAddress) {
		FeatureFolderLocation location = new FeatureFolderLocation(feature, fileAddress);
		return folderLocations.remove(location);
	}

	public boolean removeAllFolderTraces_toFolder(File fileAddress) {
		int firstSize = folderLocations.size();
		for (int i = 0; i < folderLocations.size(); i++) {
			if (folderLocations.get(i).getFileAddress().equals(fileAddress)) {
				folderLocations.remove(i);
				i--;
			}
		}
		return !(folderLocations.size() == firstSize);
	}

	public boolean removeAllFolderTraces_toFeature(Feature feature) {
		int firstSize = folderLocations.size();
		for (int i = 0; i < folderLocations.size(); i++) {
			if (folderLocations.get(i).getFeature().equals(feature)) {
				folderLocations.remove(i);
				i--;
			}
		}
		return !(folderLocations.size() == firstSize);
	}

	public boolean removeFileTrace_featureToFile(Feature feature, File fileAddress) {
		FeatureFileLocation location = new FeatureFileLocation(feature, fileAddress);
		return fileLocations.remove(location);
	}

	public boolean removeAllFileTraces_toFile(File fileAddress) {
		int firstSize = fileLocations.size();
		for (int i = 0; i < fileLocations.size(); i++) {
			if (fileLocations.get(i).getFileAddress().equals(fileAddress)) {
				fileLocations.remove(i);
				i--;
			}
		}
		return !(fileLocations.size() == firstSize);
	}

	public boolean removeAllFileTraces_toFeature(Feature feature) {
		int firstSize = fileLocations.size();
		for (int i = 0; i < fileLocations.size(); i++) {
			if (fileLocations.get(i).getFeature().equals(feature)) {
				fileLocations.remove(i);
				i--;
			}
		}
		return !(fileLocations.size() == firstSize);
	}

	public boolean removeAnnotationsTrace(Feature feature, File fileAddress) {
		int firstSize = annotationsLocations.size();
		for (int i = 0; i < annotationsLocations.size(); i++) {
			if (annotationsLocations.get(i).getFileAddress().equals(fileAddress)
					&& annotationsLocations.get(i).getFeature().equals(feature)) {
				annotationsLocations.remove(i);
				i--;
			}
		}
		return !(annotationsLocations.size() == firstSize);
	}

	public boolean removeAllAnnotationsTraces_toFile(File fileAddress) {
		int firstSize = annotationsLocations.size();
		for (int i = 0; i < annotationsLocations.size(); i++) {
			if (annotationsLocations.get(i).getFileAddress().equals(fileAddress)) {
				annotationsLocations.remove(i);
				i--;
			}
		}
		return !(annotationsLocations.size() == firstSize);
	}

	public boolean removeAllAnnotationsTraces_toFeature(Feature feature) {
		int firstSize = annotationsLocations.size();
		for (int i = 0; i < annotationsLocations.size(); i++) {
			if (annotationsLocations.get(i).getFeature().equals(feature)) {
				annotationsLocations.remove(i);
				i--;
			}
		}
		return !(annotationsLocations.size() == firstSize);
	}

	public boolean removeAllTraces() {
		if (isEmpty())
			return false;
		folderLocations.clear();
		fileLocations.clear();
		annotationsLocations.clear();
		return true;
	}

	// ******************************************************************************************************

	public boolean existsFolderTrace(Feature feature, File fileAddress) {
		FeatureFolderLocation location = new FeatureFolderLocation(feature, fileAddress);
		return folderLocations.contains(location);
	}

	public boolean existsFileTrace(Feature feature, File fileAddress) {
		FeatureFileLocation location = new FeatureFileLocation(feature, fileAddress);
		return fileLocations.contains(location);
	}

	public boolean existsAnnotationsTrace(Feature feature, File fileAddress) {
		for (FeatureAnnotationsLocation location : annotationsLocations) {
			if (location.getFeature().equals(feature) && location.getFileAddress().equals(fileAddress))
				return true;
		}
		return false;
	}

	public boolean isEmpty() {
		return folderLocations.isEmpty() && fileLocations.isEmpty() && annotationsLocations.isEmpty();
	}

}
