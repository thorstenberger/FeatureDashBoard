package se.gu.featuredashboard.utils.gef;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.mvc.fx.handlers.AbstractHandler;
import org.eclipse.gef.mvc.fx.handlers.IOnClickHandler;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.zest.fx.parts.NodePart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

import javafx.scene.input.MouseEvent;
import se.gu.featuredashboard.model.location.BlockLine;
import se.gu.featuredashboard.utils.FeaturedashboardConstants;

public class NodeOnClickHandler extends AbstractHandler implements IOnClickHandler {

	private boolean isRunning = false;
	private Long firstClick;
	private Logger logger = PlatformUI.getWorkbench().getService(org.eclipse.e4.core.services.log.Logger.class);

	public static final String ERRORDIALOG_HEADER = "Error!";
	public static final String ERRORDIALOG_MESSAGE = "Couldn't add highlights to code. Add new line at the end of the file and try again.";

	@Override
	public void click(MouseEvent e) {

		if (e.getClickCount() > 2)
			return;

		highlightEdges(getHost());

		if (firstClick != null) {
			long diff = System.currentTimeMillis() - firstClick;
			if (diff > FeaturedashboardConstants.DOUBLECLICK_DURATION)
				isRunning = false;
		}

		if (isRunning) {
			long diff = System.currentTimeMillis() - firstClick;
			if (diff < FeaturedashboardConstants.DOUBLECLICK_DURATION)
				highlightFeatureCode(getHost());
			firstClick = null;
			isRunning = false;
		} else {
			firstClick = new Long(System.currentTimeMillis());
			isRunning = true;
		}

	}

	private void highlightFeatureCode(IVisualPart<?> part) {
		FileNode fileNode = isFileNode(part);

		if (fileNode == null)
			return;

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IFile file = fileNode.getFile();

		try {
			ITextEditor editorPart = (ITextEditor) IDE.openEditor(page, file);
			IDocument document = (IDocument) editorPart.getDocumentProvider().getDocument(editorPart.getEditorInput());

			file.deleteMarkers(FeaturedashboardConstants.FEATURE_MARKER_ID, true, IResource.DEPTH_INFINITE);

			for (BlockLine block : fileNode.getAnnotatedLines()) {
				int lineStartOffset;
				int lineEndOffset;

				lineStartOffset = document.getLineOffset(block.getStartLine());

				if ((block.getStartLine() - block.getEndLine()) == 0)
					lineEndOffset = document.getLineOffset(block.getEndLine() + 1);
				else
					lineEndOffset = document.getLineOffset(block.getEndLine() - 1);

				IMarker marker = file.createMarker(FeaturedashboardConstants.FEATURE_MARKER_ID);
				marker.setAttribute(IMarker.CHAR_START, lineStartOffset);
				marker.setAttribute(IMarker.CHAR_END, lineEndOffset);
			}
		} catch (CoreException e) {
			logger.warn("Error while trying to display the specific file in the editor. " + e.getMessage());
		} catch (BadLocationException e) {
			// Only a problem If the entire file should be highligted and there is no new-line at the end of the
			// file
			MessageDialog.openError(Display.getDefault().getActiveShell(), ERRORDIALOG_HEADER, ERRORDIALOG_MESSAGE);
		}
	}

	private void highlightEdges(IVisualPart<?> part) {
		FileNode fileNode = isFileNode(part);

		if (fileNode == null)
			return;

		// Currently I just want this behaviour in the FeatureFileView
		String graphID = (String) fileNode.getGraph().getAttributes().get(FeaturedashboardConstants.GRAPH_ID_KEY);

		if (graphID == null)
			return;

		if (!graphID.equals(FeaturedashboardConstants.FEATUREFILE_VIEW_ID))
			return;

		fileNode.getAllIncomingEdges().stream().map(edge -> (CustomEdge) edge).forEach(customEdge -> {
			if (customEdge.getAttributes().get(ZestProperties.CURVE_CSS_STYLE__E)
							.equals(FeaturedashboardConstants.EDGE_HIGHLIGHTED_CSS)) {
				ZestProperties.setCurveCssStyle(customEdge, FeaturedashboardConstants.EDGE_NORMAL_CSS);
			} else {
				ZestProperties.setCurveCssStyle(customEdge, FeaturedashboardConstants.EDGE_HIGHLIGHTED_CSS);
			}
		});

	}

	private FileNode isFileNode(IVisualPart<?> part) {
		if (!(part instanceof NodePart))
			return null;

		Node clickedNode = ((NodePart) part).getContent();

		if (!(clickedNode instanceof FileNode))
			return null;

		return (FileNode) clickedNode;
	}

}
