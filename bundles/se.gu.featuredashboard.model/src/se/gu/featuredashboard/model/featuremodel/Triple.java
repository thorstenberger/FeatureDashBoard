package se.gu.featuredashboard.model.featuremodel;

import java.util.Objects;

public class Triple<L, C, R> {

	private L left;
	private C centre;
	private R right;
	
	public Triple(L left, C centre, R right) {
		this.left = left;
		this.centre = centre;
		this.right = right;
	}
	
	public L getLeft() {
		return left;
	}
	
	public C getCenter() {
		return centre;
	}
	
	public R getRight() {
		return right;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(left, centre, right);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null)
			return false;
		
		if(!(o instanceof Triple))
			return false;
		
		Triple<?, ?, ?> t = (Triple<?, ?, ?>) o;
		
		if(this == t)
			return true;
		
		return this.hashCode() == t.hashCode() &&
				this.getLeft().equals(t.getLeft()) &&
				this.getCenter().equals(this.getCenter()) &&
				this.getRight().equals(t.getRight());
		
	}
	
}
