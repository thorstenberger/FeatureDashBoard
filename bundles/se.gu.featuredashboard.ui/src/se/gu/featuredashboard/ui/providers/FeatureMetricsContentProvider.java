package se.gu.featuredashboard.ui.providers;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;

public class FeatureMetricsContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof Object[]) {
			return (Object[]) inputElement;
		}
		
		return new Object[0];
	}

}
