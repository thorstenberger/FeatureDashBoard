package se.gu.featuredashboard.ui.viewscontroller;

import java.util.ArrayList;
import java.util.List;

public class MainViewController {

	private String featureDashboard;

	private String featureFolderAddress;
	private String featureFileAddress;
	private String annotatedFilesDirectory;

	public String getFeatureFolderAddress() {
		return featureFolderAddress;
	}

	public boolean setFeatureFolderAddress(String featureFolderAddress) {
		this.featureFolderAddress = featureFolderAddress;
		return true;
	}

	public String getFeatureFileAddress() {
		return featureFileAddress;
	}

	public boolean setFeatureFileAddress(String featureFileAddress) {
		this.featureFileAddress = featureFileAddress;
		return true;
	}

	public String getAnnotatedFilesDirectory() {
		return annotatedFilesDirectory;
	}

	public boolean setAnnotatedFilesDirectory(String annotatedFilesDirectory) {
		this.annotatedFilesDirectory = annotatedFilesDirectory;
		return true;
	}

	public void prepareData(){
		System.out.println("data is extracted from the addresses and stored in 'featuredashboard'.");
	}

	public ArrayList<String> getAllFeaureIDs(){
		//the result is in featuredashboard
		ArrayList<String> sampleAnswer = new ArrayList<String>();
		for(int i=1;i<11;i++){
			sampleAnswer.add("feature"+i);
		}
		return sampleAnswer;
	}

	public ArrayList<String> getAllFolderAddresses(){
		//the result is in featuredashboard
		ArrayList<String> sampleAnswer = new ArrayList<String>();
		for(int i=1;i<11;i++){
			sampleAnswer.add("folder"+i);
		}
		return sampleAnswer;
	}

	public ArrayList<String> getAllNonAnnotatedFilesAddresses(){
		//the result is in featuredashboard
		ArrayList<String> sampleAnswer = new ArrayList<String>();
		for(int i=1;i<11;i++){
			sampleAnswer.add("file"+i);
		}
		return sampleAnswer;
	}

	public ArrayList<String> getAllAnnotatedFilesAddresses(){
		//the result is in featuredashboard
		ArrayList<String> sampleAnswer = new ArrayList<String>();
		for(int i=1;i<11;i++){
			sampleAnswer.add("annotated file"+i);
		}
		return sampleAnswer;
	}

	public ArrayList<String>  getAllTraces(List<String> features, List<String> folders,
									  List<String> nonAnnotatedFiles, List<String> annotatedFiles){
		//the result is in featuredashboard
		ArrayList<String> sampleAnswer = new ArrayList<String>();
		for(int i=1;i<11;i++){
			sampleAnswer.add("feature"+i +" folder"+i+" file"+i+" line"+i);
		}
		return sampleAnswer;
	}


}
