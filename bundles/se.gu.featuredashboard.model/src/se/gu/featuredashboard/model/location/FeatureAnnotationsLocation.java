package se.gu.featuredashboard.model.location;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import se.gu.featuredashboard.model.featuremodel.Feature;

public class FeatureAnnotationsLocation extends FeatureLocation {
	private List<BlockLine> blockLines = new ArrayList<>();

	public FeatureAnnotationsLocation() {

	}

	public FeatureAnnotationsLocation(Feature feature, File fileAddress, List<BlockLine> blockLines) {
		setFeature(feature);
		setFileAddress(fileAddress);
		setBlockLines(blockLines);
	}

	public List<BlockLine> getBlocklines() {
		return blockLines;
	}

	public boolean setBlockLines(List<BlockLine> blockLines) {
		for (BlockLine line : blockLines) {
			if (!line.isInitializedBlock())
				return false;
		}
		this.blockLines = blockLines;
		return true;
	}

	@Override
	public boolean setFileAddress(File fileAddress) {
		if (fileAddress.isFile())
			return super.setFileAddress(fileAddress);
		return false;
	}

	@Override
	public boolean isInitializedLocation() {
		if (getFeature().isInitializedFeature() && getFileAddress().exists() && !blockLines.isEmpty())
			return true;
		return false;
	}

	@Override
	public boolean equals(Object aFeatureAnnotationsLocation) {
		if (!(aFeatureAnnotationsLocation instanceof FeatureAnnotationsLocation))
			return false;
		if (((FeatureAnnotationsLocation) aFeatureAnnotationsLocation).getFeature().equals(this.getFeature())
				&& ((FeatureAnnotationsLocation) aFeatureAnnotationsLocation).getFileAddress()
						.equals(this.getFileAddress())
				&& ((FeatureAnnotationsLocation) aFeatureAnnotationsLocation).getBlocklines().size() == this.blockLines
						.size()) {
			for (int i = 0; i < this.blockLines.size(); i++) {
				if (!((FeatureAnnotationsLocation) aFeatureAnnotationsLocation).getBlocklines().get(i)
						.equals(this.blockLines.get(i)))
					return false;
			}
			return true;

		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getFeature(), getFileAddress(), getBlocklines());
	}

}
