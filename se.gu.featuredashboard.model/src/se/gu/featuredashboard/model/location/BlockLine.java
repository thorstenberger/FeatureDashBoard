package se.gu.featuredashboard.model.location;

import java.util.Objects;

public class BlockLine {

	private int startLine = 0;
	private int endLine = 0;

	public BlockLine(int startLine, int endLine) {
		if (startLine <= endLine && startLine > 0) {
			this.startLine = startLine;
			this.endLine = endLine;
		}
	}

	public int getStartLine() {
		return startLine;
	}

	public int getEndLine() {
		return endLine;
	}

	public boolean isInitializedBlock() {
		if (startLine > 0)
			return true;
		return false;
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
