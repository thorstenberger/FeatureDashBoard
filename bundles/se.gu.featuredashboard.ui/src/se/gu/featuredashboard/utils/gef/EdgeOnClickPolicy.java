package se.gu.featuredashboard.utils.gef;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.mvc.fx.handlers.IOnClickHandler;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.policies.AbstractPolicy;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.zest.fx.parts.EdgePart;
import org.eclipse.ui.PlatformUI;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import se.gu.featuredashboard.utils.FeaturedashboardConstants;

public class EdgeOnClickPolicy extends AbstractPolicy implements IOnClickHandler {

	private boolean isRunning = false;
	private Long firstClick;
	private Logger logger = PlatformUI.getWorkbench().getService(org.eclipse.e4.core.services.log.Logger.class);

	public class OnDoubleClickOperation extends AbstractOperation implements ITransactionalOperation {

		private IVisualPart<? extends Node> clickedPart;

		public OnDoubleClickOperation(String label) {
			super(label);
		}

		public OnDoubleClickOperation(String label, IContentPart<? extends Node> part) {
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
			if(clickedPart instanceof EdgePart) {
				Edge edge = ((EdgePart) clickedPart).getContent();

				if (edge instanceof CustomEdge) {
					CustomEdge customEdge = (CustomEdge) edge;

					if (customEdge.isLabelVisible()) {
						ZestProperties.setLabel(customEdge, "");
						customEdge.setLabelIsVisible(false);
					} else {
						customEdge.setLabelIsVisible(true);

						StringBuilder edgeLabel = new StringBuilder();
						customEdge.getFiles().forEach(file -> {
							edgeLabel.append(file.getProjectRelativePath().toString() + "\n");
						});

						ZestProperties.setLabel(customEdge, edgeLabel.toString());
					}
				}
			}
			return null;
		}
		
		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
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
			if (e.getClickCount() > 2)
				return;

			if (firstClick != null) {
				long diff = System.currentTimeMillis() - firstClick;
				if (diff > FeaturedashboardConstants.DOUBLECLICK_DURATION)
					isRunning = false;
			}

			if (isRunning) {
				long diff = System.currentTimeMillis() - firstClick;
				if (diff < FeaturedashboardConstants.DOUBLECLICK_DURATION)
					getHost().getRoot().getViewer().getDomain().execute(createOperation(), null);
				firstClick = null;
				isRunning = false;
			} else {
				firstClick = new Long(System.currentTimeMillis());
				isRunning = true;
			}
		} catch (ExecutionException e1) {
			logger.warn("Failed to start node click operation. " + e1.getMessage());
		}
	}

	@Override
	protected ITransactionalOperation createOperation() {
		return new OnDoubleClickOperation(FeaturedashboardConstants.EDGE_ONCLICK_OPERATION_ID, getHost());
	}

	@Override
	public EdgePart getHost() {
		return (EdgePart) super.getHost();
	}

}
