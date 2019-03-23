package se.gu.featuredashboard.model.featuremodel;

import java.util.Objects;

public class Tuple<L, R> {

	private L left;
	private R right;
	
	public Tuple(L left, R right) {
		this.left = left;
		this.right = right;
	}
	
	public L getLeft() {
		return left;
	}
	
	public R getRight() {
		return right;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(left, right);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null)
			return false;
		
		if(!(o instanceof Tuple))
			return false;
		
		Tuple<?, ?> t = (Tuple<?, ?>) o;
		
		if(this == t)
			return true;
		
		return this.hashCode() == t.hashCode() &&
				this.getLeft().equals(t.getLeft()) &&
				this.getRight().equals(t.getRight());
		
	}
}
