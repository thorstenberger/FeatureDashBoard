/*******************************************************************************
 * Copyright (c) 2019 Chalmers | University of Gothenburg
 * All rights reserved.
 * 
 * Contributors:
 *      Chalmers | University of Gothenburg
 *******************************************************************************/

package se.gu.featuredashboard.model.location;

import java.util.Objects;

/**
 * This class is used for specifying a block of code, including a start and end line.
 * 
 */
public class BlockLine {

	private final int startLine;
	private final int endLine;

	/**
	 * Constructing a block of code with the same start and end of the given block
	 */ 
	public BlockLine(BlockLine b) {
		this.startLine = b.startLine;
		this.endLine = b.endLine;
	}
	
	/**
	 * Constructs a block with the given start and end line. 
	 * If start and end line are not given validly, the block is constructed
	 * where its start and end lines are zero.
	 * 
	 * @param startLine
	 * 		the start line of the block of code which must be more than zero and 
	 * 		less or equal to the end line of the block
	 * @param endLine
	 * 		the end line of the block of code which must be more than zero and 
	 * 		more or equal to the start line of the block
	 */
	public BlockLine(int startLine, int endLine) {
		if (startLine <= endLine && startLine > 0) {
			this.startLine = startLine;
			this.endLine = endLine;
		}
		else {
			this.startLine = 0;
			this.endLine =0;
		}
	}

	public int getStartLine() {
		return startLine;
	}

	public int getEndLine() {
		return endLine;
	}

	/**
	 * Returns <code>true</code> if the block line is requested to get constructed for a start line and end line
	 * where both are more than zero and the start line is less or equal to the end line
	 */
	public boolean isValid() {
		if (startLine > 0)
			return true;
		return false;
	}

	/**
	 * Returns <code>true</code> if start line is equal to the end line of the block
	 */
	public boolean isSignleLine() {
		if(startLine == endLine)
			return true;
		return false;
	}
	
	/**
	 * Returns the info in the format: (startLine,endLine)
	 */
	@Override
	public String toString(){
		return "("+startLine+","+endLine+")";
	}

	@Override
	public boolean equals(Object aBlockLine) {
		if (!(aBlockLine instanceof BlockLine))
			return false;
		if (startLine == ((BlockLine) aBlockLine).getStartLine() && endLine == ((BlockLine) aBlockLine).getEndLine())
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(startLine, endLine);
	}
}
