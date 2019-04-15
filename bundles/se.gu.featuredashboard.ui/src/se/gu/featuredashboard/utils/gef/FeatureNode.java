package se.gu.featuredashboard.utils.gef;

import java.util.Objects;

import org.eclipse.gef.graph.Node;

public class FeatureNode extends Node {

	private String featureID;

	public FeatureNode(String featureID) {
		this.featureID = featureID;
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

		return this.featureID.equals(fn.featureID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(featureID);
	}

}
