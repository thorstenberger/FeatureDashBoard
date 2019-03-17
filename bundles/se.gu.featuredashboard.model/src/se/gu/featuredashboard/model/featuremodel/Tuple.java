package se.gu.featuredashboard.model.featuremodel;

public class Tuple<L, R> {

	private L left;
	private R right;
	
	public Tuple(L key, R value) {
		this.left = key;
		this.right = value;
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
