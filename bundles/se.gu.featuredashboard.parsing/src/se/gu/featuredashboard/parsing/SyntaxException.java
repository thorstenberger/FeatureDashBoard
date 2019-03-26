package se.gu.featuredashboard.parsing;

public class SyntaxException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private Integer lineNumber;
	
	public SyntaxException(String message) {
		super(message);
	}
	
	public SyntaxException(String message, int lineNumber) {
		super(message);
		this.lineNumber = lineNumber;
	}
	
	public Integer getLineNumber() {
		return lineNumber;
	}

}
