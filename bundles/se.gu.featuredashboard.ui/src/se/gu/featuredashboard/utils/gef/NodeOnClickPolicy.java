package se.gu.featuredashboard.utils.gef;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.gef.mvc.fx.handlers.IOnClickHandler;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.policies.AbstractPolicy;
import org.eclipse.gef.zest.fx.parts.NodePart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import se.gu.featuredashboard.model.location.BlockLine;
import se.gu.featuredashboard.utils.FeaturedashboardConstants;

public class NodeOnClickPolicy extends AbstractPolicy implements IOnClickHandler {
	
	private boolean isRunning = false;
	private Long firstClick;
	private Logger logger = PlatformUI.getWorkbench().getService(org.eclipse.e4.core.services.log.Logger.class);
	
	public class OnClickOperation extends AbstractOperation implements ITransactionalOperation {

		private IVisualPart<? extends Node> clickedPart;
		
		public OnClickOperation(String label) {
			super(label);
		}
		
		public OnClickOperation(String label, IContentPart<? extends Node> part) {
			super(label);
			clickedPart = part;
		}

		@Override
		public boolean isContentRelevant() {
			return false;
		}

		@Override
		public boolean isNoOp() {
			return false;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			NodePart nodePart = (NodePart) clickedPart;
			org.eclipse.gef.graph.Node node = nodePart.getContent();
			
			if(node instanceof FileNode) {
				FileNode n = (FileNode) node;
				
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IFile file = n.getFile();
				try {
					ITextEditor part = (ITextEditor) IDE.openEditor(page, file);				
					IDocument document = (IDocument) part.getDocumentProvider().getDocument(part.getEditorInput());
					
					file.deleteMarkers(FeaturedashboardConstants.FEATURE_MARKER_ID, true, IResource.DEPTH_INFINITE);
					
					for(BlockLine block : n.getAnnotatedLines()) {
						IMarker marker = file.createMarker(FeaturedashboardConstants.FEATURE_MARKER_ID);
						if((block.getStartLine()-block.getEndLine()) == 0) {
							marker.setAttribute(IMarker.CHAR_START, document.getLineOffset(block.getStartLine()));
							marker.setAttribute(IMarker.CHAR_END, document.getLineOffset(block.getEndLine()+1));
						} else {
							marker.setAttribute(IMarker.CHAR_START, document.getLineOffset(block.getStartLine()));
							marker.setAttribute(IMarker.CHAR_END, document.getLineOffset(block.getEndLine()));
						}	
					}
				} catch (CoreException | BadLocationException e) {
					logger.warn(e.getMessage());
				}
			}
			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			// TODO Auto-generated method stub
			return execute(monitor, info);
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return Status.OK_STATUS;
		}
		
	}
	
	@Override
	public void click(MouseEvent e) {
		// Handle double-clicks
		try {
			if(e.getClickCount() > 2)
				return;
			
			if(firstClick != null) {
				long diff = System.currentTimeMillis() - firstClick;
				if(diff > FeaturedashboardConstants.DOUBLECLICK_DURATION)
					isRunning = false;
			}
			
			if(isRunning) {
				long diff = System.currentTimeMillis() - firstClick;	
				if(diff < FeaturedashboardConstants.DOUBLECLICK_DURATION)
					getHost().getRoot().getViewer().getDomain().execute(createOperation(), null);
				firstClick = null;
				isRunning = false;
			} else {
				firstClick = new Long(System.currentTimeMillis());
				isRunning = true;
			}
		} catch (ExecutionException e1) {
			logger.warn("Failed to start node click operation. " + e1.getMessage() );
		}
	}

	@Override
	protected ITransactionalOperation createOperation() {
		return new OnClickOperation(FeaturedashboardConstants.NODE_ONCLICK_OPERATION_ID, getHost());
	}

	@Override
	public NodePart getHost() {
		return (NodePart) super.getHost();
	}
	
}