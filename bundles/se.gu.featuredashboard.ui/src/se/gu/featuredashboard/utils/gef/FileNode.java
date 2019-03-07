package se.gu.featuredashboard.utils.gef;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.graph.Node;

import se.gu.featuredashboard.model.location.BlockLine;

public class FileNode extends Node {
	
	private IFile file;
	private List<BlockLine> annotatedLines;
	
	public void setFile(IFile file) {
		this.file = file;
	}
	
	public IFile getFile() {
		return this.file;
	}
	
	public void setAnnotatedLines(List<BlockLine> annotatedLines) {
		this.annotatedLines = annotatedLines;
	}
	
	public List<BlockLine> getAnnotatedLines(){
		return annotatedLines;
	}
	
}
