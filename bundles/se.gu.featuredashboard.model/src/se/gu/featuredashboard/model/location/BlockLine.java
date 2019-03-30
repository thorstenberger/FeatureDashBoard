package se.gu.featuredashboard.model.location;

import java.util.Objects;

public class BlockLine {

	private final int startLine;
	private final int endLine;

	public BlockLine(BlockLine b) {
		this.startLine = b.startLine;
		this.endLine = b.endLine;
	}
	
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

	public boolean isValid() {
		if (startLine > 0)
			return true;
		return false;
	}

	public boolean isSignleLine() {
		if(startLine == endLine)
			return true;
		return false;
	}
	
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
