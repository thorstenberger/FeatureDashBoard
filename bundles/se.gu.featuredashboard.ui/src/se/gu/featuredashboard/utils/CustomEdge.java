package se.gu.featuredashboard.utils;

import java.util.Objects;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Node;

public class CustomEdge extends Edge {

	private Node source;
	private Node target;

	public CustomEdge(Node source, Node target) {
		super(source, target);
		this.source = source;
		this.target = target;
	}

	public Node getSource() {
		return source;
	}

	public Node getTarget() {
		return target;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Source: " + source.toString());
		builder.append("Target: " + target.toString());
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(source, target);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;

		if (!(o instanceof CustomEdge))
			return false;

		CustomEdge ce = (CustomEdge) o;

		if (this == ce)
			return true;

		if (ce.getSource() == null || ce.getTarget() == null)
			return false;

		return this.hashCode() == ce.hashCode() && this.source.equals(ce.getSource())
				&& this.target.equals(ce.getTarget());
	}

}
