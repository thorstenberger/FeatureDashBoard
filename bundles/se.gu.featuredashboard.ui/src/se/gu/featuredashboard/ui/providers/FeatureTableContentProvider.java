package se.gu.featuredashboard.ui.providers;

import org.eclipse.jface.viewers.IStructuredContentProvider;

public class FeatureTableContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof Object[]) {
			return (Object[]) inputElement;
		}
		
		return new Object[0];
	}

}
