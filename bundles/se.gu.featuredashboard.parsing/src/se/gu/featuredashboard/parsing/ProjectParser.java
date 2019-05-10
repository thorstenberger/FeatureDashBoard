/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class ProjectParser{

	private IProject project;
	private IFile featureFolderAddress;
	private IFile featureFileAddress;
	private IFile claferFileAddress;
	private ArrayList<IFile> allAnnotatedFiles = new ArrayList<>();
	private ArrayList<IFile> allFiles = new ArrayList<>();
	private ArrayList<IFolder> allFolders = new ArrayList<>();
	
	public static final List<String> DEFAULT_EXCLUDED_ANNOTATED_FILES_EXTENSIONS = Arrays.asList("pdf", "class", "DS_Store", "docx");
	public static final List<String> DEFAULT_EXCLUDED_FOLDERS_OF_ANNOTATED_FILES = Arrays.asList("bin");
	
	private List<String> excludedAnnotatedFilesExtensions = new ArrayList<String>(DEFAULT_EXCLUDED_ANNOTATED_FILES_EXTENSIONS);
	private List<String> excludedFoldersOfAnnotatedFiles = new ArrayList<String>(DEFAULT_EXCLUDED_FOLDERS_OF_ANNOTATED_FILES);
	
	public ProjectParser(IProject project, List<String> excludedAnnotatedFilesExtensions, List<String> excludedFoldersOfAnnotatedFiles) {
		this.project = project;
		this.excludedAnnotatedFilesExtensions = new ArrayList<String>(excludedAnnotatedFilesExtensions);
		this.excludedFoldersOfAnnotatedFiles = new ArrayList<String>(excludedFoldersOfAnnotatedFiles);
		
		findFilesForParse(project);
		if(project != null)
			traverseAllProjectMembers(project);

	}

	private void findFilesForParse(IProject project){
		try{
			featureFolderAddress = null;
			featureFileAddress = null;
			claferFileAddress = null;
			
			if(project==null)
				return;

			for (IResource member : project.members()) {
				if(member instanceof IFile) {
					IFile newMember = (IFile) member;
					// in case of having multiple .feature_file file (plus others) we should decide
					// which one to set as the asset defining the project
					if(newMember.getName().split("\\.")[1].equals("feature-folder"))
						featureFolderAddress = newMember;
					else if(newMember.getName().split("\\.")[1].equals("feature-file"))
						featureFileAddress = newMember;
					else if(newMember.getName().split("\\.")[1].equals("cfr"))
						claferFileAddress = newMember;
				}
			}

		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	private void traverseAllProjectMembers(IContainer container) {
		try {
			Arrays.stream(container.members()).forEach(member ->{
				if(member instanceof IFolder) {
					IFolder theFolder = (IFolder)member;
					allFolders.add(theFolder);
					
					String folderPath = theFolder.getProjectRelativePath().toString();
					if(!excludedFoldersOfAnnotatedFiles.contains(folderPath))
						traverseAllProjectMembers(theFolder);
				} else if(member instanceof IFile) {
					IFile theFile = (IFile) member;
					allFiles.add(theFile);
					
					String extension = theFile.getFileExtension();
					if(extension==null || !excludedAnnotatedFilesExtensions.contains(extension))
						allAnnotatedFiles.add( (IFile) member);
				}
			});
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public IFile getFeatureFolderAddress(){
		return featureFolderAddress;
	}

	public IFile getFeatureFileAddress(){
		return featureFileAddress;
	}

	public IFile getClaferAddress(){
		return claferFileAddress;
	}
	
	public ArrayList<IFile> getAllAnnotatedFiles(){
		return allAnnotatedFiles;
	}

	public ArrayList<IFolder> getAllFolders() {
		return allFolders;
	}
	
	public ArrayList<IFile> getAllFiles() {
		return allFiles;
	}
	
}
