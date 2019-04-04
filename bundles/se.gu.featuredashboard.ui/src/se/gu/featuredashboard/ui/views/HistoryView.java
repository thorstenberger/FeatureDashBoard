package se.gu.featuredashboard.ui.views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.ui.part.ViewPart;

import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.model.featuremodel.Tuple;
import se.gu.featuredashboard.model.featuremodel.WritableFeatureContainer;
import se.gu.featuredashboard.utils.FeaturedashboardConstants;
import se.gu.featuredashboard.utils.ObjectToFileHandler;

// See how metrics have evolved over time on the current branch in a traditional graph over time
public class HistoryView extends ViewPart {

	private static final int STEPS_IN_HISTORY = 8;
	
	private static final String GIT_ERROR_FATAL = "fatal: not a git repository (or any of the parent directories): .git";
	private static final String GIT_COMMIT_HASH = "git log -n " + STEPS_IN_HISTORY + " --pretty=format:\"%H\"";
	private static final String GIT_BRANCH_NAME = "git rev-parse --abbrev-ref HEAD";
	private static final String GIT_STASH = "git stash";
	private static final String GIT_CHECKOUT = "git checkout ";
	private static final String GIT_STASH_POP = "git stash pop";
	
	private static final String DIR_NOT_EXISTS = "The system cannot find the path specified.\n";
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
	
	private static final List<String> METRICS = Arrays.asList(
			FeaturedashboardConstants.FEATURETABLE_COLUMN_2_NAME,
			FeaturedashboardConstants.FEATURETABLE_COLUMN_3_NAME,
			FeaturedashboardConstants.FEATURETABLE_COLUMN_4_NAME,
			FeaturedashboardConstants.FEATURETABLE_COLUMN_5_NAME,
			FeaturedashboardConstants.FEATURETABLE_COLUMN_6_NAME,
			FeaturedashboardConstants.FEATURETABLE_COLUMN_7_NAME,
			FeaturedashboardConstants.FEATURETABLE_COLUMN_8_NAME,
			FeaturedashboardConstants.FEATURETABLE_COLUMN_9_NAME
			);
	
	@Override
	public void createPartControl(Composite parent) {
		Canvas graphCanvas = new Canvas(parent, SWT.BORDER);
		
		lws = new LightweightSystem(graphCanvas);
		graph = new XYGraph();
		
		
		lws.setContents(graph);
		
		xAxis = graph.getPrimaryXAxis();
		xAxis.setTitle(X_AXIS_LABEL);
		xAxis.setFormatPattern("#.#");
		xAxis.setRange(STEPS_IN_HISTORY, 0);
		
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
					if(activeTraces.containsKey(metric))
						graph.removeTrace(activeTraces.remove(metric).getLeft());
					else {
						Tuple<Trace, double[]> traceInfo = allTraces.get(metric);
						System.out.println("Metric: " + metric);
						Arrays.stream(traceInfo.getRight()).forEach(System.out::println);
						activeTraces.put(metric, traceInfo);
						graph.addTrace(traceInfo.getLeft());
					}
					graph.performAutoScale();
				}
			};
			action.setText(metric);
			getViewSite().getActionBars().getMenuManager().add(action);
		});
	}

	@Override
	public void setFocus() {} 

	public void inputToView(List<FeatureContainer> featureContainers) {
		if(featureContainers.size() == 0)
			return;
		
		activeTraces.values().stream().map(Tuple::getLeft).forEach(graph::removeTrace);
		
		ObjectToFileHandler handler = ObjectToFileHandler.getInstance();
		
		String projectLocation = ProjectStore.getActiveProject().getLocation().toString();
		
		StringBuilder commandToExecute;
		
		List<WritableFeatureContainer> objectsToCompare = new ArrayList<>();
		allTraces = new HashMap<>();
		
		String startingBranch = null;
		Process p;
		
		try {
			commandToExecute = new StringBuilder();
			commandToExecute.append(CMD_EXEC);
			commandToExecute.append("cd " + projectLocation);
			commandToExecute.append(APPEND_COMMAND);
			commandToExecute.append(GIT_STASH);
			
			p = Runtime.getRuntime().exec(commandToExecute.toString());
			try(BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getErrorStream()))){
				String line = null;
				while((line = stdInput.readLine()) != null) {
					// Not a Git director
					if(line.equals(GIT_ERROR_FATAL))
						return;
				}					
			}
			
			commandToExecute = new StringBuilder();
			commandToExecute.append(CMD_EXEC);
			commandToExecute.append("cd " + projectLocation);
			commandToExecute.append(APPEND_COMMAND);
			commandToExecute.append(GIT_BRANCH_NAME);
			
			p = Runtime.getRuntime().exec(commandToExecute.toString());
			try(BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()))){
				String line = null;
				while((line = stdInput.readLine()) != null)
					startingBranch = line;
			}
			
			// Get commit hashes
			List<String> commitHashes = new ArrayList<>();
			commandToExecute = new StringBuilder();
			commandToExecute.append(CMD_EXEC);
			commandToExecute.append("cd " + projectLocation);
			commandToExecute.append(APPEND_COMMAND);
			commandToExecute.append(GIT_COMMIT_HASH);
			
			p = Runtime.getRuntime().exec(commandToExecute.toString());
			try(BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()))){
				String line = null;
				while((line = stdInput.readLine()) != null)
					commitHashes.add(line);
			}
			
			// Get objects that should be used for comparison
			for(String commitHash : commitHashes){
				boolean folderExists = true;
				
				commandToExecute = new StringBuilder();
				commandToExecute.append(CMD_EXEC);
				commandToExecute.append("cd " + projectLocation);
				commandToExecute.append(APPEND_COMMAND);
				commandToExecute.append(GIT_CHECKOUT + commitHash + " > nul 2>&1");
				commandToExecute.append(APPEND_COMMAND);
				commandToExecute.append("cd " + FeaturedashboardConstants.FEATUREDASHBOARD_FOLDER_PATH);
				commandToExecute.append(APPEND_COMMAND);
				commandToExecute.append("type " + featureContainers.get(0).getFeature().getFeatureID());
				
				p = Runtime.getRuntime().exec(commandToExecute.toString());
				try(BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()))){
					String line = null;
					while((line = br.readLine()) != null)
						folderExists = line.equals(DIR_NOT_EXISTS);
				}
				
				if(!folderExists)
					objectsToCompare.add(null);
				else
					objectsToCompare.add(handler.readObjectFromStream(p.getInputStream()));
				
			}
			
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			commandToExecute = new StringBuilder();
			commandToExecute.append(CMD_EXEC);
			commandToExecute.append("cd " + projectLocation);
			commandToExecute.append(APPEND_COMMAND);
			commandToExecute.append(GIT_CHECKOUT + startingBranch);
			commandToExecute.append(APPEND_COMMAND);
			commandToExecute.append(GIT_STASH_POP);
			
			try {
				Runtime.getRuntime().exec(commandToExecute.toString());
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		for(int i = 0; i < objectsToCompare.size(); i++) {	
			WritableFeatureContainer container = objectsToCompare.get(i);
			
			for(String metric : METRICS) {
				Tuple<Trace, double[]> traceInfo = allTraces.get(metric);
				if(traceInfo == null)
					traceInfo = new Tuple<Trace, double[]>(new Trace(metric, xAxis, yAxis, new CircularBufferDataProvider(true)), new double[STEPS_IN_HISTORY+1]);
				
				if(container == null) {
					traceInfo.getRight()[i+1] = 0.0;
					continue;
				}
					
				switch(metric) {
					case FeaturedashboardConstants.FEATURETABLE_COLUMN_2_NAME:
						if(i == 0)
							traceInfo.getRight()[i] = (double) featureContainers.get(0).getLOFC();
						traceInfo.getRight()[i+1] = (double) container.getLOFC();
						break;
					case FeaturedashboardConstants.FEATURETABLE_COLUMN_3_NAME:
						if(i == 0)
							traceInfo.getRight()[i] = (double) featureContainers.get(0).getNumberOfFileAnnotations();
						traceInfo.getRight()[i+1] = (double) container.getNumberOfFileAnnotations();
						break;
					case FeaturedashboardConstants.FEATURETABLE_COLUMN_4_NAME:
						if(i == 0)
							traceInfo.getRight()[i] = (double) featureContainers.get(0).getNumberOfFolderAnnotations();
						traceInfo.getRight()[i+1] = (double) container.getNumberOfFolderAnnotations();
						break;
					case FeaturedashboardConstants.FEATURETABLE_COLUMN_5_NAME:
						if(i == 0)
							traceInfo.getRight()[i] = (double) featureContainers.get(0).getTanglingDegree();
						traceInfo.getRight()[i+1] = (double) container.getTanglingDegree();
						break;
					case FeaturedashboardConstants.FEATURETABLE_COLUMN_6_NAME:
						if(i == 0)
							traceInfo.getRight()[i] = (double) featureContainers.get(0).getScatteringDegree();
						traceInfo.getRight()[i+1] = (double) container.getScatteringDegree();
						break;
					case FeaturedashboardConstants.FEATURETABLE_COLUMN_7_NAME:
						if(i == 0)
							traceInfo.getRight()[i] = (double) featureContainers.get(0).getMaxND();
						traceInfo.getRight()[i+1] = (double) container.getMaxND();
						break;
					case FeaturedashboardConstants.FEATURETABLE_COLUMN_8_NAME:
						if(i == 0)
							traceInfo.getRight()[i] = Double.parseDouble(featureContainers.get(0).getAvgND());
						traceInfo.getRight()[i+1] = Double.parseDouble(container.getAvgND());
						break;
					case FeaturedashboardConstants.FEATURETABLE_COLUMN_9_NAME:
						if(i == 0)
							traceInfo.getRight()[i] = (double) featureContainers.get(0).getMinND();
						traceInfo.getRight()[i+1] = (double) container.getMinND();
						break;
				}
				
				allTraces.put(metric, traceInfo);
			}
			
		}
		
		allTraces.entrySet().stream().forEach(entry -> {
			Trace trace = entry.getValue().getLeft();
			trace.setPointStyle(PointStyle.CIRCLE);
			CircularBufferDataProvider traceDataProvider = (CircularBufferDataProvider) trace.getDataProvider();
			traceDataProvider.setCurrentYDataArray(entry.getValue().getRight());
		});
	}
}
