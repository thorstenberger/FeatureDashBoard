package se.gu.featuredashboard.model.location;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import se.gu.featuredashboard.model.featuremodel.Feature;
import org.eclipse.core.runtime.IPath;	// It seems this address is unnecessary but by removing that we have an error

public class FeatureLocation {
	
	private final Feature feature;
	private final IResource resource;
	private final List<BlockLine> blockLines;
	
	public FeatureLocation(Feature feature,IResource resource, List<BlockLine> blockLines) {
		this.feature = new Feature(feature);
		
		// valid resources are of type IFile or IFolder
		if(resource instanceof IFolder) {
			String relativePath = ((IFolder) resource).getProjectRelativePath().toString();
			this.resource = ((IFolder) resource).getProject().getFolder(relativePath);
		}
		else if(resource instanceof IFile) {
			String relativePath = ((IFile) resource).getProjectRelativePath().toString();
			this.resource = ((IFile) resource).getProject().getFile(relativePath);
		}
		//invalid case
		else { // the invalid data is not supposed to be saved separately, like the previous ones
			   // 'hasValidResource' should detect it.
			this.resource = resource;
		}	
		
		List<BlockLine> blocks = new ArrayList<>();
		if(blockLines!=null) {
			for (BlockLine block : blockLines) {
				if (block.isValid()) 
					blocks.add(block);
				else {
					System.out.println("FeatureDashboard skips an invalid annotation from feature: "+feature.getFeatureID()+
						" to resource: " + resource.getLocation().toString()+ " for block line: "+ block.toString());	
				}
			}
		}
		this.blockLines = blocks;
	}
	
	public boolean hasValidResource() {
		if( ((getResource() instanceof IFile)|| (getResource() instanceof IFolder))&& getResource().exists() && blockLines.isEmpty()) 
			return true;
		if( (getResource() instanceof IFile) && getResource().exists() && !blockLines.isEmpty()) 
			return true;
		return false;
	}
	
	public Feature getFeature() {
		return this.feature;
	}
	
	public IResource getResource() {
		return this.resource;
	}
	
	public List<BlockLine> getBlocklines() {
		return blockLines;
	}

	@Override
	public boolean equals(Object aFeatureLocation) {
		if (!(aFeatureLocation instanceof FeatureLocation))
			return false;
		
		IResource iresource = ((FeatureLocation) aFeatureLocation).getResource();
		System.out.println(iresource);
		if (	((FeatureLocation) aFeatureLocation).getFeature().equals(this.getFeature())
			 && ((FeatureLocation) aFeatureLocation).getResource()
						.equals(this.getResource())
			 && ((FeatureLocation) aFeatureLocation).getBlocklines().size() == this.blockLines
						.size()) {
			for (int i = 0; i < this.blockLines.size(); i++) {
				if (!((FeatureLocation) aFeatureLocation).getBlocklines().get(i)
						.equals(this.blockLines.get(i)))
					return false;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getFeature(), getResource(), getBlocklines());
	}
}
