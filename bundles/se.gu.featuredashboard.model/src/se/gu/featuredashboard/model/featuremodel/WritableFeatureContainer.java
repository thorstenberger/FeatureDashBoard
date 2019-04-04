package se.gu.featuredashboard.model.featuremodel;

import java.io.Serializable;

public class WritableFeatureContainer implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String feature;
	private int LOFC;
	private int scatteringDegree;
	private int tanglingDegree;
	private int fileAnnotations;
	private int folderAnnotations;
	private int maxND;
	private int minND;
	private String avgND;
	
	public WritableFeatureContainer(String feature, int LOFC, int scatteringDegree, int tanglingDegree, int fileAnnotations, int folderAnnotations, int maxND, int minND, String avgND) {
		this.feature = feature;
		this.LOFC = LOFC;
		this.scatteringDegree = scatteringDegree;
		this.tanglingDegree = tanglingDegree;
		this.fileAnnotations = fileAnnotations;
		this.folderAnnotations = folderAnnotations;
		this.maxND = maxND;
		this.minND = minND;
		this.avgND = avgND;
	}

	public String getFeature() {
		return feature;
	}
	
	public int getLOFC() {
		return LOFC;
	}

	public int getScatteringDegree() {
		return scatteringDegree;
	}

	public int getTanglingDegree() {
		return tanglingDegree;
	}

	public int getNumberOfFileAnnotations() {
		return fileAnnotations;
	}

	public int getNumberOfFolderAnnotations() {
		return folderAnnotations;
	}

	public int getMaxND() {
		return maxND;
	}

	public int getMinND() {
		return minND;
	}

	public String getAvgND() {
		return avgND;
	}
	
}

