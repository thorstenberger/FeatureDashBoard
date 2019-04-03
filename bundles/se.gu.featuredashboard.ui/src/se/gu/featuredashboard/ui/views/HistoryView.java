package se.gu.featuredashboard.ui.views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.figures.Axis;
import org.eclipse.nebula.visualization.xygraph.figures.IXYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.PointStyle;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import se.gu.featuredashboard.model.featuremodel.FeatureContainer;
import se.gu.featuredashboard.model.featuremodel.ProjectStore;
import se.gu.featuredashboard.model.featuremodel.WritableFeatureContainer;
import se.gu.featuredashboard.utils.FeaturedashboardConstants;
import se.gu.featuredashboard.utils.ObjectToFileHandler;

public class HistoryView extends ViewPart {

	private static final int STEPS_IN_HISTORY = 8;
	
	private static final String GIT_ERROR_FATAL = "fatal: not a git repository (or any of the parent directories): .git";
	private static final String GIT_COMMIT_HASH = "git log -n " + STEPS_IN_HISTORY + " --pretty=format:\"%H\"";
	private static final String GIT_BRANCH_NAME = "git rev-parse --abbrev-ref HEAD";
	private static final String GIT_STASH = "git stash";
	private static final String GIT_STASH_NO_OUTPUT = "git stash > nul 2>&1";
	private static final String GIT_STASH_POP = "git stash pop";
	
	private static final String DIR_NOT_EXISTS = "The system cannot find the path specified.\n";
	private static final String CMD_EXEC = "cmd.exe /c ";
	private static final String APPEND_COMMAND = " && ";
	
	private Axis xAxis;
	private Axis yAxis;
	private IXYGraph graph;
	private LightweightSystem lws;
	
	private List<Trace> graphTraces = new ArrayList<>();
	
	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(1, true);
		parent.setLayout(layout);
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		
		Canvas graphCanvas = new Canvas(parent, SWT.BORDER);
		graphCanvas.setLayoutData(gridData);
		
		lws = new LightweightSystem(graphCanvas);
		graph = new XYGraph();
		
		xAxis = graph.getPrimaryXAxis();
		xAxis.setTitle("Commits ago");
		xAxis.setFormatPattern("#");
		xAxis.setRange(STEPS_IN_HISTORY, 0);
		
		yAxis = graph.getPrimaryYAxis();
		yAxis.setFormatPattern("#");
		
		parent.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseScrolled(org.eclipse.swt.events.MouseEvent e) {
//				PlotArea plotArea = graph.getPlotArea();
//				plotArea.zoomInOut(true, true, e.x, e.y, e.count * 0.1 / 3);
				
				double valuePosition = yAxis.getPositionValue(yAxis.isHorizontal() ? e.x : e.y, false);
				yAxis.zoomInOut(valuePosition, e.count * 0.1 / 3);
			}

		});
		
	}

	@Override
	public void setFocus() {
	} 

	public void inputToView(List<FeatureContainer> featureContainers) {
		
		if(featureContainers.size() == 0)
			return;
		
		// Remove any current traces
		graphTraces.forEach(graph::removeTrace);
		
		ObjectToFileHandler handler = ObjectToFileHandler.getInstance();
		String projectLocation = ProjectStore.getActiveProject().getLocation().toString();
		
		StringBuilder command = new StringBuilder();
		
		List<WritableFeatureContainer> objectsToCompare = new ArrayList<>();
		graphTraces = new ArrayList<>();
		
		String currentBranch = null;
		
		try {
			command.append(CMD_EXEC);
			command.append("cd " + projectLocation);
			command.append(APPEND_COMMAND);
			command.append(GIT_STASH);
			
			Process p = Runtime.getRuntime().exec(command.toString());
			try(BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getErrorStream()))){
				String line = null;
				while((line = stdInput.readLine()) != null) {
					// Not a Git director
					if(line.equals(GIT_ERROR_FATAL))
						return;
				}
					
			}
			
			command = new StringBuilder();
			command.append(CMD_EXEC);
			command.append("cd " + projectLocation);
			command.append(APPEND_COMMAND);
			command.append(GIT_BRANCH_NAME);
			
			p = Runtime.getRuntime().exec(command.toString());
			try(BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()))){
				String line = null;
				while((line = stdInput.readLine()) != null)
					currentBranch = line;
			}
			
			
			// Get commit hashes
			List<String> commitHashes = new ArrayList<>();
			command = new StringBuilder();
			command.append(CMD_EXEC);
			command.append("cd " + projectLocation);
			command.append(APPEND_COMMAND);
			command.append(GIT_COMMIT_HASH);
			
			p = Runtime.getRuntime().exec(command.toString());
			try(BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()))){
				String line = null;
				while((line = stdInput.readLine()) != null)
					commitHashes.add(line);
			}
			
			// Get objects that should be used for comparison
			for(String commitHash : commitHashes){
				boolean folderExists = true;
				
				command = new StringBuilder();
				command.append(CMD_EXEC);
				command.append("cd " + projectLocation);
				command.append(APPEND_COMMAND);
				command.append("git checkout " + commitHash + " > nul 2>&1");
				command.append(APPEND_COMMAND);
				command.append("cd " + FeaturedashboardConstants.FEATUREDASHBOARD_FOLDER_PATH);
				command.append(APPEND_COMMAND);
				command.append("type " + featureContainers.get(0).getFeature().getFeatureID());
				
				p = Runtime.getRuntime().exec(command.toString());
				
				// Check if the folder exists in commit
				try(BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()))){
					String line = null;
					while((line = br.readLine()) != null) {
						folderExists = line.equals(DIR_NOT_EXISTS);
					}
				}
				
				if(!folderExists)
					objectsToCompare.add(null);
				else
					objectsToCompare.add(handler.readObjectFromStream(p.getInputStream()));
				
			}
			
			// Checkout correct branch and pop stash
			command = new StringBuilder();
			command.append(CMD_EXEC);
			command.append("cd " + projectLocation);
			command.append(APPEND_COMMAND);
			command.append("git checkout " + currentBranch + " > nul 2>&1");
			command.append(APPEND_COMMAND);
			command.append(GIT_STASH_POP);
			
			p = Runtime.getRuntime().exec(command.toString());
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		double[] lofc = new double[objectsToCompare.size()+1];
		double[] sd = new double[objectsToCompare.size()+1];
		
		for(int i = 0; i < objectsToCompare.size(); i++) {
			WritableFeatureContainer container = objectsToCompare.get(i);
			if(container == null) {
				lofc[i+1] = 0;
				sd[i+1] = 0;
			}
			else {
				lofc[i+1] = (double) container.getLOFC();
				sd[i+1] = (double) container.getScatteringDegree();
			}
		}
		
		lofc[0] = (double) featureContainers.get(0).getLinesOfFeatureCode();
		sd[0] = (double) featureContainers.get(0).getScatteringDegree();
		
		CircularBufferDataProvider traceDataProvider = new CircularBufferDataProvider(true);
		traceDataProvider.setCurrentYDataArray(lofc);
		
		Trace trace = new Trace("LOFC", xAxis, yAxis, traceDataProvider);
		trace.setPointStyle(PointStyle.DIAMOND);
		
		graphTraces.add(trace);
		
		traceDataProvider = new CircularBufferDataProvider(true);
		traceDataProvider.setCurrentYDataArray(sd);
		
		trace = new Trace("Scattering Degree", xAxis, yAxis, traceDataProvider);
		trace.setPointStyle(PointStyle.CIRCLE);
		
		graphTraces.add(trace);
		
		graphTraces.forEach(graph::addTrace);
		graph.performAutoScale();
		
		lws.setContents(graph);
	}
}
