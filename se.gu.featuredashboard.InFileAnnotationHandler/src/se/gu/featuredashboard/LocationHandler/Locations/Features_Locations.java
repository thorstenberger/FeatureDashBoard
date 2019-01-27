package se.gu.featuredashboard.LocationHandler.Locations;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Features_Locations {

	private Map<String, List<String>> folderTraces = new HashMap<>();
	private Map<String, List<String>> fileTraces = new HashMap<>();
	private Map<FeatureAddress, AnnotatedLocations> annotationTraces = new HashMap<>();

	// *********************************************addTrace*************************************************
	public boolean setTrace_toFolder(String featureID, String address) {
		if (!new File(address).isDirectory())
			return false;

		if (folderTraces.get(featureID) != null) {
			if (folderTraces.get(featureID).contains(address))
				return false;
			folderTraces.get(featureID).add(address);
			return true;
		}
		ArrayList<String> foldersList = new ArrayList<>(Arrays.asList(address));
		folderTraces.put(featureID, foldersList);
		return true;
	}

	public boolean setTrace_toFile(String featureID, String address) {
		if (!new File(address).isFile())
			return false;

		if (fileTraces.get(featureID) != null) {
			if (fileTraces.get(featureID).contains(address))
				return false;
			fileTraces.get(featureID).add(address);
			return true;
		}
		ArrayList<String> filesList = new ArrayList<>(Arrays.asList(address));
		fileTraces.put(featureID, filesList);
		return true;
	}

	public boolean setTrace_toAnnotation(String featureID, String fileAddress, AnnotatedLocations annotatedLines) {
		FeatureAddress feautreAddress = new FeatureAddress(featureID, fileAddress);
		if (feautreAddress.getAddress() == null || annotatedLines.isEmpty())
			return false;

		// checking if previously any annotation for the feature and the address
		// is added
		for (Map.Entry<FeatureAddress, AnnotatedLocations> fannotation : annotationTraces.entrySet()) {
			if (fannotation.getKey().getFeatureID().equals(featureID)
					&& fannotation.getKey().getAddress().equals(fileAddress)) {
				annotationTraces.put(fannotation.getKey(), annotatedLines);
				return true;
			}
		}

		annotationTraces.put(feautreAddress, annotatedLines);
		return true;

	}

	public boolean addAnnotationTracesInOtherFiles(Features_Locations locations) {
		// two annotations are consistent if there is no file where both
		// annotations has trace-links in that file

		if (locations == null || locations.getAllAnnotatedFiles().isEmpty())
			return false;

		// checking consistency
		for (Map.Entry<FeatureAddress, AnnotatedLocations> annotations : annotationTraces.entrySet()) {
			if (locations.getAllAnnotatedFiles().contains(annotations.getKey().getAddress())) {
				return false;
			}
		}

		// adding annotations
		for (Map.Entry<FeatureAddress, AnnotatedLocations> annotations : locations.annotationTraces.entrySet()) {
			setTrace_toAnnotation(annotations.getKey().getFeatureID(), annotations.getKey().getAddress(),
					annotations.getValue());
		}
		return true;
	}
	// ******************************************************************************************************

	// ********************************************getFeatures***********************************************

	public List<String> getFeatures_ofFolder(String address) {
		ArrayList<String> answer = new ArrayList<>();
		folderTraces.forEach((feature, addresses) -> {
			if (addresses.contains(address))
				answer.add(feature);
		});
		return answer;
	}

	public List<String> getFeatures_ofAllFolders() {
		return new ArrayList<>(folderTraces.keySet());
	}

	public List<String> getFeatures_ofNonAnnotatedFile(String address) {
		ArrayList<String> answer = new ArrayList<>();
		fileTraces.forEach((feature, addresses) -> {
			if (addresses.contains(address))
				answer.add(feature);
		});
		return answer;
	}

	public List<String> getFeatures_ofAllNonAnnotatedFiles() {
		return new ArrayList<>(fileTraces.keySet());
	}

	public List<String> getFeatures_inAnnotatedFile(String fileAddress) {
		Set<String> answer = new HashSet<>();
		annotationTraces.forEach((featureAddress, annotations) -> {
			if (featureAddress.getAddress().equals(fileAddress))
				answer.add(featureAddress.getFeatureID());
		});
		return new ArrayList<String>(answer);
	}

	public List<String> getFeatures_inAllAnnotatedFiles() {
		Set<String> answer = new HashSet<>();
		annotationTraces.forEach((featureAddress, annotations) -> {
			answer.add(featureAddress.getFeatureID());
		});
		return new ArrayList<>(answer);
	}

	public List<String> getAllFeatures() {
		Set<String> featuresOfFolders = new HashSet<>(getFeatures_ofAllFolders());
		Set<String> featuresOfNonAnnotationFiles = new HashSet<>(getFeatures_ofAllNonAnnotatedFiles());
		Set<String> featuresInAnnotationFiles = new HashSet<>(getFeatures_inAllAnnotatedFiles());

		Set<String> answer = new HashSet<>(featuresOfFolders);
		answer.addAll(featuresOfNonAnnotationFiles);
		answer.addAll(featuresInAnnotationFiles);
		return new ArrayList<>(answer);
	}

	// ******************************************************************************************************

	// *******************************************getLocations***********************************************
	public List<String> getFolders(String featureID) {
		if (folderTraces.get(featureID) == null)
			return new ArrayList<>();
		return folderTraces.get(featureID);
	}

	public List<String> getAllFolders() {
		Set<String> answer = new HashSet<>();
		for (List<String> addresses : folderTraces.values()) {
			addresses.forEach(address -> {
				answer.add(address);
			});
		}
		return new ArrayList<String>(answer);
	}

	public List<String> getNonAnnotatedFiles(String featureID) {
		if (fileTraces.get(featureID) == null)
			return new ArrayList<>();
		return fileTraces.get(featureID);
	}

	public List<String> getAllNonAnnotatedFiles() {
		Set<String> answer = new HashSet<>();
		for (List<String> addresses : fileTraces.values()) {
			addresses.forEach(address -> {
				answer.add(address);
			});
		}
		return new ArrayList<String>(answer);
	}

	public List<String> getAnnotatedFiles(String featureID) {
		Set<String> answer = new HashSet<>();
		annotationTraces.forEach((featureAddress, annotations) -> {
			if (featureAddress.getFeatureID().equals(featureID))
				answer.add(featureAddress.getAddress());
		});
		return new ArrayList<>(answer);
	}

	public List<String> getAllAnnotatedFiles() {
		Set<String> answer = new HashSet<>();
		annotationTraces.forEach((featureAddress, annotations) -> {
			answer.add(featureAddress.getAddress());
		});
		return new ArrayList<>(answer);
	}

	public AnnotatedLocations getAnnotations(String featureID, String fileName) {
		for (Map.Entry<FeatureAddress, AnnotatedLocations> annotations : annotationTraces.entrySet()) {
			if (annotations.getKey().getFeatureID().equals(featureID)
					&& annotations.getKey().getAddress().equals(fileName)) {
				return annotations.getValue();
			}
		}
		return new AnnotatedLocations();
	}
	// ******************************************************************************************************

	// **********************************************RemoveTrace*********************************************

	public boolean removeFolderTrace(String featureID, String address) {
		if (folderTraces.get(featureID) == null)
			return false;
		if (folderTraces.get(featureID).contains(address)) {
			folderTraces.get(featureID).remove(address);

			if (folderTraces.get(featureID).isEmpty())
				folderTraces.remove(featureID);
			return true;
		}
		return false;
	}

	public boolean removeAllFolderTraces_toFolder(String address) {
		ArrayList<String> elementsToRemove = new ArrayList<>();
		boolean isRemoved = false;

		for (Map.Entry<String, List<String>> featureFolder : folderTraces.entrySet()) {
			if (featureFolder.getValue().contains(address)) {
				featureFolder.getValue().remove(address);
				isRemoved = true;

				if (featureFolder.getValue().isEmpty())
					elementsToRemove.add(featureFolder.getKey());
			}
		}
		elementsToRemove.forEach(element -> {
			folderTraces.remove(element);
		});
		return isRemoved;
	}

	public boolean removeAllFolderTraces_toFeature(String featureID) {
		if (folderTraces.containsKey(featureID)) {
			folderTraces.remove(featureID);
			return true;
		}
		return false;
	}

	public boolean removeFileTrace_featureToFile(String featureID, String address) {
		if (fileTraces.get(featureID) == null)
			return false;
		if (fileTraces.get(featureID).contains(address)) {
			fileTraces.get(featureID).remove(address);

			if (fileTraces.get(featureID).isEmpty())
				fileTraces.remove(featureID);
			return true;
		}
		return false;
	}

	public boolean removeAllFileTraces_toFile(String address) {
		ArrayList<String> elementsToRemove = new ArrayList<>();
		boolean isRemoved = false;

		for (Map.Entry<String, List<String>> featureFolder : fileTraces.entrySet()) {
			if (featureFolder.getValue().contains(address)) {
				featureFolder.getValue().remove(address);
				isRemoved = true;

				if (featureFolder.getValue().isEmpty())
					elementsToRemove.add(featureFolder.getKey());
			}
		}
		elementsToRemove.forEach(element -> {
			fileTraces.remove(element);
		});
		return isRemoved;
	}

	public boolean removeAllFileTraces_toFeature(String featureID) {
		if (fileTraces.containsKey(featureID)) {
			fileTraces.remove(featureID);
			return true;
		}
		return false;
	}

	public boolean removeAnnotationsTrace(String featureID, String fileAddress) {
		FeatureAddress featureAddress = new FeatureAddress();
		for (Map.Entry<FeatureAddress, AnnotatedLocations> annotations : annotationTraces.entrySet()) {
			if (annotations.getKey().getAddress().equals(fileAddress)
					&& annotations.getKey().getFeatureID().equals(featureID)) {
				featureAddress = annotations.getKey();
				break;
			}
		}
		if (featureAddress.getFeatureID() != null) {
			annotationTraces.remove(featureAddress);
			return true;
		}
		return false;
	}

	public boolean removeAllAnnotationsTraces_toFile(String fileAddress) {
		ArrayList<FeatureAddress> featureAddress = new ArrayList<>();
		for (Map.Entry<FeatureAddress, AnnotatedLocations> annotations : annotationTraces.entrySet()) {
			if (annotations.getKey().getAddress().equals(fileAddress)) {
				featureAddress.add(annotations.getKey());
			}
		}
		if (featureAddress.isEmpty())
			return false;

		featureAddress.forEach(fAdd -> {
			annotationTraces.remove(fAdd);
		});
		return true;
	}

	public boolean removeAllAnnotationsTraces_toFeature(String featureID) {
		ArrayList<FeatureAddress> featureAddress = new ArrayList<>();
		for (Map.Entry<FeatureAddress, AnnotatedLocations> annotations : annotationTraces.entrySet()) {
			if (annotations.getKey().getFeatureID().equals(featureID)) {
				featureAddress.add(annotations.getKey());
			}
		}
		if (featureAddress.isEmpty())
			return false;

		featureAddress.forEach(fAdd -> {
			annotationTraces.remove(fAdd);
		});
		return true;
	}

	public boolean removeAllTraces() {
		boolean removeFlag = isEmpty() ? true : false;
		folderTraces.clear();
		fileTraces.clear();
		annotationTraces.clear();
		return removeFlag;
	}

	// ******************************************************************************************************

	public boolean existsFolderTrace(String featureID, String address) {
		if (folderTraces.get(featureID) != null && folderTraces.get(featureID).contains(address))
			return true;
		return false;
	}

	public boolean existsFileTrace(String featureID, String address) {
		if (fileTraces.get(featureID) != null && fileTraces.get(featureID).contains(address))
			return true;
		return false;
	}

	public boolean existsAnnotationsTrace(String featureID, String address) {
		for (Map.Entry<FeatureAddress, AnnotatedLocations> annotations : annotationTraces.entrySet()) {
			if (annotations.getKey().getFeatureID().equals(featureID)
					&& annotations.getKey().getAddress().equals(address)) {
				return true;
			}
		}
		return false;
	}

	public boolean isEmpty() {
		return folderTraces.isEmpty() && fileTraces.isEmpty() && annotationTraces.isEmpty();
	}

	@Override
	public String toString() {
		if (isEmpty())
			return "";

		StringBuilder answer = new StringBuilder();
		if (!folderTraces.isEmpty()) {
			answer.append("Folder Traces:\n");
			folderTraces.forEach((feature, addresses) -> {
				answer.append("feature \"" + feature + "\" in folder(s):\n");
				addresses.forEach(address -> {
					answer.append(address + "\n");
				});
			});
			answer.append("\n");
		}

		if (!fileTraces.isEmpty()) {
			answer.append("File Traces:\n");
			fileTraces.forEach((feature, addresses) -> {
				answer.append("feature \"" + feature + "\" in file(s):\n");
				addresses.forEach(address -> {
					answer.append(address + "\n");
				});
			});
			answer.append("\n");
		}

		if (!annotationTraces.isEmpty()) {
			answer.append("Annotation Traces:\n");
			annotationTraces.forEach((featureLocation, annotations) -> {
				answer.append("feature \"" + featureLocation.getFeatureID() + "\" in \"" + featureLocation.getAddress()
						+ "\" is shown in " + annotations + "\n");
			});
		}

		return answer.toString();
	}
}

class FeatureAddress {

	private String featureID;
	private String featureAddress;

	public FeatureAddress() {
		// TODO Auto-generated constructor stub
	}

	public FeatureAddress(String featureID, String address) {
		setFeatureID(featureID);
		setAddress(address);
	}

	public String getFeatureID() {
		return featureID;
	}

	public void setFeatureID(String featureID) {
		this.featureID = featureID;
	}

	public String getAddress() {
		return featureAddress;
	}

	public boolean setAddress(String address) {
		if (new File(address).exists()) {
			this.featureAddress = address;
			return true;
		}
		return false;
	}

	public void setForceAddress(String address) {
		this.featureAddress = address;
	}

	@Override
	public boolean equals(Object anObject) {
		if (!(anObject instanceof FeatureAddress))
			return false;
		FeatureAddress classObj = (FeatureAddress) anObject;
		if (featureID.equals(classObj.getAddress()) && featureAddress.equals(classObj.getAddress()))
			return true;
		return false;

	}

	@Override
	public int hashCode() {
		return Objects.hash(featureID, featureAddress);
	}

}
