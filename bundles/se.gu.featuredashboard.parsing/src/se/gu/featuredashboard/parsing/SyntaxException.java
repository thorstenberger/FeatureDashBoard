/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.parsing;

public class SyntaxException extends Exception {

	private static final long serialVersionUID = 1L;

	private String message;
	private String filePath;
	private Integer lineNumber;

	public SyntaxException(String message) {
		super(message);
		this.message = message;
	}

	public SyntaxException(String filePath, String message) {
		this.filePath = filePath;
		this.message = message;
	}

	public SyntaxException(String message, int lineNumber) {
		this.message = message;
		this.lineNumber = lineNumber;
	}

	public SyntaxException(String filePath, String message, int lineNumber) {
		super(message);
		this.filePath = filePath;
		this.message = message;
		this.lineNumber = lineNumber;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getMessage() {
		return message;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

}
