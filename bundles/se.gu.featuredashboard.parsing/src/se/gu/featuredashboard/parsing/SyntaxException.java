package se.gu.featuredashboard.parsing;

public class SyntaxException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private int lineNumber;
	
	public SyntaxException(String message) {
		super(message);
	}
	
	public SyntaxException(String message, int lineNumber) {
		super(message);
		this.lineNumber = lineNumber;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}

}
