package se.gu.featuredashboard.utils.gef;

import java.util.Objects;

import org.eclipse.gef.graph.Node;

import se.gu.featuredashboard.model.featuremodel.Feature;

public class FeatureNode extends Node {

	private Feature feature;

	public FeatureNode(Feature feature) {
		this.feature = feature;
	}

	@Override
	public int hashCode() {
		return Objects.hash(feature.getFeatureID());
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;

		if (!(o instanceof FeatureNode))
			return false;

		FeatureNode fn = (FeatureNode) o;

		if (this == o)
			return true;

		return this.feature.equals(fn.feature);
	}

}
