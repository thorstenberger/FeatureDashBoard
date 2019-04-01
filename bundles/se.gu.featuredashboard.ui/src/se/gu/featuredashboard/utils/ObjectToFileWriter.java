package se.gu.featuredashboard.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import se.gu.featuredashboard.model.featuremodel.Project;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;

public class ObjectToFileWriter implements IUpdateInformationListener {
	
	private static ObjectToFileWriter objectWriter = null;
	
	private ObjectToFileWriter() {}
	
	public static ObjectToFileWriter getInstance() {
		if(objectWriter == null)
			objectWriter = new ObjectToFileWriter();
		return objectWriter;
	}
	
	private void writeFeaturesToFile() {	
		Project p = ProjectStore.getActiveProject();
		
		if(p == null)
			return;
		
		String outputPath = p.getLocation().toString() + "/" + FeaturedashboardConstants.FEATUREDASHBOARD_OUTPUT_PATH;
		
		File directory = new File(outputPath);
		
		if(!directory.exists()) {
			
			if(!directory.mkdir())
				return;	
			
		}
		
		p.getFeatureContainers().forEach(fc -> {
			try(FileOutputStream fos = new FileOutputStream(outputPath + "/" + fc.getFeature().getFeatureID())){
				
				try(ObjectOutputStream oos = new ObjectOutputStream(fos)){
					
					oos.writeObject(fc);
					
				}
				
			} catch(FileNotFoundException e) {
				
			} catch(SecurityException e) {
				
			} catch(IOException e) {
				
			}
		});
		
	}

	@Override
	public void updateData() {
		writeFeaturesToFile();
	}
	
}
