package se.gu.featuredashboard.utils.gef;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.mvc.fx.handlers.IOnClickHandler;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.policies.AbstractPolicy;
import org.eclipse.gef.zest.fx.parts.NodePart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class NodeOnClickPolicy extends AbstractPolicy implements IOnClickHandler {
	
	private boolean isRunning = false;
	private Long firstClick;
	
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
					IDE.openEditor(page, file);
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		
		try {
			if(e.getClickCount() > 2) {
				return;
			}
			// Handle double-clicks
			if(isRunning) {
				long diff = System.currentTimeMillis() - firstClick;
				
				if(diff < 500) {
					getHost().getRoot().getViewer().getDomain().execute(createOperation(), null);
				} else {
					firstClick = null;
					isRunning = false;
				}
			} else {
				firstClick = new Long(System.currentTimeMillis());
				isRunning = true;
			}
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	protected ITransactionalOperation createOperation() {
		return new OnClickOperation("OnClickOperation", getHost());
	}

	@Override
	public NodePart getHost() {
		return (NodePart) super.getHost();
	}
	
}