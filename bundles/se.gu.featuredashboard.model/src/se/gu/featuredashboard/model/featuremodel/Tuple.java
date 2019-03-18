package se.gu.featuredashboard.model.featuremodel;

public class Tuple<L, R> {

	private L left;
	private R right;
	
	public Tuple(L left, R right) {
		this.left = left;
		this.right = right;
	}
	
	public void setLeft(L left) {
		this.left = left;
	}
	
	public void setRight(R right) {
		this.right = right;
	}
	
	public L getLeft() {
		return left;
	}
	
	public R getRight() {
		return right;
	}
	
}
