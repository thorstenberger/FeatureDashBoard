package se.gu.featuredashboard.ui.providers;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.model.location.BlockLine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

public class FeatureMetricsLabelProvider implements ITableLabelProvider {

	private Random rand = new Random();
	
	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		FeatureContainer featureContainer = (FeatureContainer) element;
		
		switch (columnIndex) {
			case 0:
				return featureContainer.getFeature().getFeatureID();
			case 1:
				return Integer.toString(featureContainer.getLinesOfFeatureCode());
			case 2:
				return Integer.toString(featureContainer.getNumberOfInFileAnnotations());
			case 3:
				return "TBD";
			case 4:
				return Integer.toString(featureContainer.getTanglingDegree());
			case 5:
				return Integer.toString(featureContainer.getScatteringDegree());
			default:
				return "Error";
		}		
	}	
	
	private String getNumberOfLines(List<BlockLine> lines) {
		int numberOfLines = 0;
		
		for(BlockLine block : lines) {
			numberOfLines += Math.abs(block.getStartLine() - block.getEndLine());
		}
		
		return Integer.toString(numberOfLines);
	}

}	