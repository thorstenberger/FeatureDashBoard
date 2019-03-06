package se.gu.featuredashboard.utils.gef;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.graph.Node;

public class FileNode extends Node {
	
	private IFile file;
	
	public void setFile(IFile file) {
		this.file = file;
	}
	
	public IFile getFile() {
		return this.file;
	}
	
}
