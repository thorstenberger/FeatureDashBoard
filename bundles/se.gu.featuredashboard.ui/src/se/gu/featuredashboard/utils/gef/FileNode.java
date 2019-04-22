package se.gu.featuredashboard.utils.gef;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.graph.Node;

import se.gu.featuredashboard.model.location.BlockLine;

public class FileNode extends Node {
	
	private IFile file;
	private List<BlockLine> annotatedLines;
	
	public FileNode(IFile file) {
		this.file = file;
	}
	
	public IFile getFile() {
		return this.file;
	}
	
	public void addAnnotatedLines(List<BlockLine> annotatedLines) {
		if(this.annotatedLines == null)
			this.annotatedLines = new ArrayList<>();
		this.annotatedLines.addAll(annotatedLines);
	}
	
	public List<BlockLine> getAnnotatedLines(){
		return annotatedLines;
	}

	@Override
	public int hashCode() {
		return Objects.hash(file);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;

		if (!(o instanceof FileNode))
			return false;

		FileNode fn = (FileNode) o;

		if (this == fn)
			return true;
		
		return this.file.equals(file);

	}
}
