package se.gu.featuredashboard.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.gu.featuredashboard.model.featuremodel.WritableFeatureContainer;
import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.model.featuremodel.Project;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;

public class ObjectToFileHandler implements IUpdateInformationListener {
	
	private static ObjectToFileHandler objectWriter = null;
	
	private ObjectToFileHandler() {}
	
	public static ObjectToFileHandler getInstance() {
		if(objectWriter == null)
			objectWriter = new ObjectToFileHandler();
		return objectWriter;
	}
	
	public void removeFile(FeatureContainer container) {
		File file = new File(ProjectStore.getActiveProject().getLocation().toString() + "/" + FeaturedashboardConstants.FEATUREDASHBOARD_FOLDER_PATH + "/" + container.getFeature().getFeatureID());
		file.delete();
	}
	
	public void writeObjectToFile(FeatureContainer container) {
		String outputPath = ProjectStore.getActiveProject().getLocation().toString() + "/" + FeaturedashboardConstants.FEATUREDASHBOARD_FOLDER_PATH;
		
		try(FileOutputStream fos = new FileOutputStream(outputPath + "/" + container.getFeature().getFeatureID())){
			try(ObjectOutputStream oos = new ObjectOutputStream(fos)){
				oos.writeObject(container.getWritableObject());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeObjectsToFile() {	 
		String outputPath = ProjectStore.getActiveProject().getLocation().toString() + "/" + FeaturedashboardConstants.FEATUREDASHBOARD_FOLDER_PATH;
		File directory = new File(outputPath);
		if(!directory.exists()) {
			if(!directory.mkdir())
				return;		
		}
		
		ProjectStore.getActiveProject().getFeatureContainers().forEach(fc -> {
			try(FileOutputStream fos = new FileOutputStream(outputPath + "/" + fc.getFeature().getFeatureID())){
				try(ObjectOutputStream oos = new ObjectOutputStream(fos)){
					System.out.println("Actual Lines of code: " + fc.getWritableObject().getLOFC());
					System.out.println("Write lines of code: " + fc.getWritableObject().getLOFC());
					oos.writeObject(fc.getWritableObject());
				}
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			}
		});
		
	}
	
	public WritableFeatureContainer readObjectFromStream(InputStream is) {
		WritableFeatureContainer wo = null;
		
		try(ObjectInputStream ois = new ObjectInputStream(is)){
			wo = (WritableFeatureContainer) ois.readObject();
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return wo;
	}
	
	public List<WritableFeatureContainer> readObjectsFromFile() {
		
		Project p = ProjectStore.getActiveProject();
		
		if(p == null)
			return null;
		
		List<WritableFeatureContainer> objects = new ArrayList<>();
		
		File folder = new File(p.getLocation().toString() + "/" + FeaturedashboardConstants.FEATUREDASHBOARD_FOLDER_PATH);
		
		Arrays.stream(folder.listFiles()).forEach(file -> {
			try(FileInputStream fis = new FileInputStream(file.getAbsolutePath())){
				try(ObjectInputStream ois = new ObjectInputStream(fis)){
					objects.add((WritableFeatureContainer) ois.readObject());
				} catch(ClassNotFoundException e) {
					e.printStackTrace();
				}
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			} catch(SecurityException e) {
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			}
		});
				
		return objects;
	}

	@Override
	public void updateData() {
		writeObjectsToFile();
	}
	
}
