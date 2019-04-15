package se.gu.featuredashboard.ui.views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.action.Action;
import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.figures.Axis;
import org.eclipse.nebula.visualization.xygraph.figures.IXYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.PointStyle;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.model.featuremodel.Project;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.model.featuremodel.Triple;
import se.gu.featuredashboard.model.featuremodel.Tuple;
import se.gu.featuredashboard.utils.FeaturedashboardConstants;

// See how metrics have evolved over time on the current branch in a traditional graph over time
public class HistoryView extends ViewPart {

	private static final int STEPS_IN_HISTORY = 4;

	private static final String GIT_ERROR_FATAL = "fatal: not a git repository (or any of the parent directories): .git";
	private static final String GIT_COMMIT_HASH = "git log -n " + STEPS_IN_HISTORY + " --pretty=format:\"%H\"";
	private static final String GIT_STASH = "git stash";
	private static final String GIT_CHECKOUT = "git checkout ";
	private static final String GIT_BRANCH = "git rev-parse --abbrev-ref HEAD";
	private static final String GIT_STASH_POP = "git stash pop";
	private static final String GIT_DIFF = "git diff --name-status ";

	private static final String GIT_ADDED = "A";
	private static final String GIT_DELETED = "D";
	private static final String GIT_MODIFIED = "M";

	private static final String CMD_EXEC = "cmd.exe /c ";
	private static final String APPEND_COMMAND = " && ";

	private static final String X_AXIS_LABEL = "Commits ago";
	private static final String Y_AXIS_LABEL = "Value";

	private Axis xAxis;
	private Axis yAxis;
	private IXYGraph graph;
	private LightweightSystem lws;

	private Map<String, Tuple<Trace, double[]>> allTraces = new HashMap<>();
	private Map<String, Tuple<Trace, double[]>> activeTraces = new HashMap<>();

	private static final List<String> METRICS = Arrays.asList(FeaturedashboardConstants.FEATURETABLE_COLUMN_2_NAME,
			FeaturedashboardConstants.FEATURETABLE_COLUMN_3_NAME, FeaturedashboardConstants.FEATURETABLE_COLUMN_4_NAME,
			FeaturedashboardConstants.FEATURETABLE_COLUMN_5_NAME, FeaturedashboardConstants.FEATURETABLE_COLUMN_6_NAME,
			FeaturedashboardConstants.FEATURETABLE_COLUMN_7_NAME, FeaturedashboardConstants.FEATURETABLE_COLUMN_8_NAME,
			FeaturedashboardConstants.FEATURETABLE_COLUMN_9_NAME);

	@Override
	public void createPartControl(Composite parent) {
		Canvas graphCanvas = new Canvas(parent, SWT.BORDER);

		lws = new LightweightSystem(graphCanvas);
		graph = new XYGraph();

		lws.setContents(graph);

		xAxis = graph.getPrimaryXAxis();
		xAxis.setTitle(X_AXIS_LABEL);
		xAxis.setFormatPattern("#.#");
		xAxis.setRange(STEPS_IN_HISTORY, 1);

		yAxis = graph.getPrimaryYAxis();
		yAxis.setTitle(Y_AXIS_LABEL);
		yAxis.setFormatPattern("#");

		parent.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseScrolled(org.eclipse.swt.events.MouseEvent e) {
				double valuePosition = yAxis.getPositionValue(yAxis.isHorizontal() ? e.x : e.y, false);
				yAxis.zoomInOut(valuePosition, e.count * 0.1 / 3);
			}
		});

		METRICS.forEach(metric -> {
			Action action = new Action() {
				public void run() {
					if (activeTraces.containsKey(metric))
						graph.removeTrace(activeTraces.remove(metric).getLeft());
					else {
						Tuple<Trace, double[]> traceInfo = allTraces.get(metric);
						activeTraces.put(metric, traceInfo);
						graph.addTrace(traceInfo.getLeft());
					}
					yAxis.performAutoScale(true);
				}
			};
			action.setText(metric);
			getViewSite().getActionBars().getMenuManager().add(action);
		});
	}

	@Override
	public void setFocus() {
	}

	public void inputToView(List<FeatureContainer> featureContainers) {
		if (featureContainers.size() == 0)
			return;

		activeTraces.values().stream().map(Tuple::getLeft).forEach(graph::removeTrace);

		activeTraces = new HashMap<>();
		allTraces = new HashMap<>();

		String projectLocation = ProjectStore.getActiveProject().getLocation().toString();
		String startingBranch = null;

		StringBuilder commandToExecute;
		Process p;

		List<Triple<String, Project, List<String>>> diffInfo = new ArrayList<>();

		try {
			commandToExecute = new StringBuilder();
			commandToExecute.append(CMD_EXEC);
			commandToExecute.append("cd " + projectLocation);
			commandToExecute.append(APPEND_COMMAND);
			commandToExecute.append(GIT_STASH);
			p = Runtime.getRuntime().exec(commandToExecute.toString());

			try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
				String line = null;
				while ((line = stdInput.readLine()) != null) {
					// Not a Git directory, return
					if (line.equals(GIT_ERROR_FATAL))
						return;
				}
			}

			commandToExecute = new StringBuilder();
			commandToExecute.append(CMD_EXEC);
			commandToExecute.append("cd " + projectLocation);
			commandToExecute.append(APPEND_COMMAND);
			commandToExecute.append(GIT_BRANCH);
			p = Runtime.getRuntime().exec(commandToExecute.toString());

			try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
				String line = null;
				while ((line = stdInput.readLine()) != null) {
					startingBranch = line;
				}
			}

			commandToExecute = new StringBuilder();
			commandToExecute.append(CMD_EXEC);
			commandToExecute.append("cd " + projectLocation);
			commandToExecute.append(APPEND_COMMAND);
			commandToExecute.append(GIT_COMMIT_HASH);
			p = Runtime.getRuntime().exec(commandToExecute.toString());

			try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
				String line = null;
				while ((line = stdInput.readLine()) != null) {
					diffInfo.add(new Triple<String, Project, List<String>>(line,
							ProjectStore.getActiveProject().clone(), new ArrayList<>()));
				}
			}

			for (Triple<String, Project, List<String>> entry : diffInfo) {
				List<String> diffs = entry.getRight();
				Project project = entry.getCenter();

				commandToExecute = new StringBuilder();
				commandToExecute.append(CMD_EXEC);
				commandToExecute.append("cd " + projectLocation);
				commandToExecute.append(APPEND_COMMAND);
				commandToExecute.append(GIT_DIFF + entry.getLeft());
				p = Runtime.getRuntime().exec(commandToExecute.toString());

				try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
					String line = null;
					while ((line = br.readLine()) != null) {
						diffs.add(line);
					}
				}

				// After diffs have been obtained, checkout commit and do appropriate action
				commandToExecute = new StringBuilder();
				commandToExecute.append(CMD_EXEC);
				commandToExecute.append("cd " + projectLocation);
				commandToExecute.append(APPEND_COMMAND);
				commandToExecute.append(GIT_CHECKOUT + entry.getLeft());
				p = Runtime.getRuntime().exec(commandToExecute.toString());

				for (String diff : diffs) {
					String[] info = diff.split("\\s+");

					IFile file = project.getIProject().getWorkspace().getRoot()
							.getFile(new Path("/" + project.getID() + "/" + info[1]));

					if (info[0].equals(GIT_ADDED)) {
						// If the diff show that the file has been added then it needs to be removed
						// from this project
						project.getFeatureContainers().stream()
								.filter(featureContainer -> featureContainer.isMappedIn(file)
										|| featureContainer.isAnnotatedIn(file))
								.forEach(featureContainer -> featureContainer.removeFile(file));

					} else if (info[0].equals(GIT_DELETED) || info[0].equals(GIT_MODIFIED)) {
						// If the diff showd that the file was removed/modified then we need to reparse
						// it.
//						ParseJob job = new ParseJob("Parse history", project, file,
//								Display.getDefault().getActiveShell());
//						job.setUser(true);
//						job.schedule();
					}
				}

				commandToExecute = new StringBuilder();
				commandToExecute.append(CMD_EXEC);
				commandToExecute.append("cd " + projectLocation);
				commandToExecute.append(APPEND_COMMAND);
				commandToExecute.append(GIT_CHECKOUT + startingBranch + " > nul 2>&1");
				p = Runtime.getRuntime().exec(commandToExecute.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Checkout correct branch and pop stash
			commandToExecute = new StringBuilder();
			commandToExecute.append(CMD_EXEC);
			commandToExecute.append("cd " + projectLocation);
			commandToExecute.append(APPEND_COMMAND);
			commandToExecute.append(GIT_CHECKOUT + startingBranch);
			commandToExecute.append(APPEND_COMMAND);
			commandToExecute.append(GIT_STASH_POP + " > nul 2>&1");
			try {
				p = Runtime.getRuntime().exec(commandToExecute.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		IntStream.range(0, diffInfo.size()).forEach(i -> {
			FeatureContainer featureContainer = diffInfo.get(i).getCenter()
					.getFeatureContainer(featureContainers.get(0).getFeature());

			METRICS.forEach(metric -> {
				Tuple<Trace, double[]> traceInfo = allTraces.get(metric);
				if (traceInfo == null)
					traceInfo = new Tuple<Trace, double[]>(
							new Trace(metric, xAxis, yAxis, new CircularBufferDataProvider(true)),
							new double[STEPS_IN_HISTORY + 1]);

				switch (metric) {
				case FeaturedashboardConstants.FEATURETABLE_COLUMN_2_NAME:
					traceInfo.getRight()[i] = featureContainer.getLOFC();
					break;
				case FeaturedashboardConstants.FEATURETABLE_COLUMN_3_NAME:
					traceInfo.getRight()[i] = featureContainer.getNumberOfFileAnnotations();
					break;
				case FeaturedashboardConstants.FEATURETABLE_COLUMN_4_NAME:
					traceInfo.getRight()[i] = featureContainer.getNumberOfFolderAnnotations();
					break;
				case FeaturedashboardConstants.FEATURETABLE_COLUMN_5_NAME:
					traceInfo.getRight()[i] = featureContainer.getTanglingDegree();
					break;
				case FeaturedashboardConstants.FEATURETABLE_COLUMN_6_NAME:
					traceInfo.getRight()[i] = featureContainer.getScatteringDegree();
					break;
				case FeaturedashboardConstants.FEATURETABLE_COLUMN_7_NAME:
					traceInfo.getRight()[i] = featureContainer.getMaxND();
					break;
				case FeaturedashboardConstants.FEATURETABLE_COLUMN_8_NAME:
					traceInfo.getRight()[i] = featureContainer.getAvgND();
					break;
				case FeaturedashboardConstants.FEATURETABLE_COLUMN_9_NAME:
					traceInfo.getRight()[i] = featureContainer.getMinND();
					break;
				}
				allTraces.put(metric, traceInfo);
			});

		});

		allTraces.entrySet().stream().forEach(entry -> {
			Trace trace = entry.getValue().getLeft();
			trace.setPointStyle(PointStyle.CIRCLE);
			CircularBufferDataProvider traceDataProvider = (CircularBufferDataProvider) trace.getDataProvider();
			// When all data has been collected set the data points with the correct data
			// provider
			traceDataProvider.setCurrentYDataArray(entry.getValue().getRight());
		});
	}
}
