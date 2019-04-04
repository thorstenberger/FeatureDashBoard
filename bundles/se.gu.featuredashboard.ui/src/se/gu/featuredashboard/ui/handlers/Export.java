package se.gu.featuredashboard.ui.handlers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.ui.PlatformUI;

import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.model.featuremodel.Project;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.utils.FeaturedashboardConstants;

// Export currently selected project's feature metrics to a CSV file
public class Export extends ResourceHandler {
	
	private static Logger logger = PlatformUI.getWorkbench().getService(org.eclipse.e4.core.services.log.Logger.class);
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IResource resource = RemoveMarkers.getResource(event);
			
		if(!(resource instanceof IProject))
			return null;
		
		Project project = ProjectStore.getProject(resource.getLocation());
		
		if(project == null)
			return null;
		
		try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(resource.getLocation().toString(), FeaturedashboardConstants.EXPORT_FILENAME))){
			
			writer.write("FeatureName,LOFC,NoFiA,NoFoA,TanglingDegree,ScatteringDegree,MaxND,AvgND,MinND");
			writer.newLine();
			
			for(FeatureContainer featureContainer : project.getFeatureContainers()) {
				writer.write(String.format("%s,%d,%d,%d,%d,%d,%d,%s,%d", featureContainer.getFeature().getFeatureID(),
						 												 featureContainer.getLOFC(),
						 												 featureContainer.getNumberOfFileAnnotations(),
						 												 featureContainer.getNumberOfFolderAnnotations(),
						 												 featureContainer.getTanglingDegree(),
						 												 featureContainer.getScatteringDegree(),
						 												 featureContainer.getMaxND(),
						 												 featureContainer.getAvgND(),
						 												 featureContainer.getMinND()));
				writer.newLine();
			}
			
		} catch(IOException e) {
			logger.warn("Unable to write feature metrics file");
		}
		
		return resource;
	}

}
