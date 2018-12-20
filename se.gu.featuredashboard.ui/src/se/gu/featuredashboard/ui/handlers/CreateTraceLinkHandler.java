package se.gu.featuredashboard.ui.handlers;

import static org.eclipse.capra.testsuite.TestHelper.createSimpleProject;
import static org.eclipse.capra.testsuite.TestHelper.getProject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.capra.core.adapters.TraceMetaModelAdapter;
import org.eclipse.capra.core.adapters.TracePersistenceAdapter;
import org.eclipse.capra.core.helpers.ArtifactHelper;
import org.eclipse.capra.core.helpers.ExtensionPointHelper;
import org.eclipse.capra.core.helpers.TraceHelper;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import se.gu.featuredashboard.core.model.FM_Annotations.Annotation;
import se.gu.featuredashboard.core.model.FM_Annotations.AnnotationsFactory;
import se.gu.featuredashboard.core.model.FM_Annotations.Feature;
import org.eclipse.jdt.core.*;
import org.eclipse.cdt.core.model.ICElement;

import org.eclipse.capra.testsuite.TestHelper;

import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class CreateTraceLinkHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"Example1",
				"Creating a tracelink between a feature and an annotation is on the table.");
		try {
			createLinks_AnnotationsFM();
			MessageDialog.openInformation(
					window.getShell(),
					"Successfully Done!",
					"Tracelink is already existed or created successfully!");
		} catch (Exception e) {
			MessageDialog.openInformation(
					window.getShell(),
					"Failed!",
					"An Error occured during tracelink creation.");
		}
		
		return null;
	}
	
	private void createLinks_AnnotationsFM() throws CoreException, IOException {
		
		AnnotationsFactory MyFactory = AnnotationsFactory.eINSTANCE;
		
		Feature feature1 = MyFactory.createFeature();
		feature1.setFeatureID(5);
		feature1.setName("GoodFeature");
		
		Annotation ann1 = MyFactory.createAnnotation();
		ann1.setFeature(feature1);
		ann1.setFileName("myJavaClass1.java");
		ann1.setLineNumber(6);
		
		IProject MiddleModelProject;
		if(!org.eclipse.capra.testsuite.TestHelper.projectExists("FeatureDashboardMiddleModel") )
			MiddleModelProject = createSimpleProject("FeatureDashboardMiddleModel");
		else
			MiddleModelProject = getProject("FeatureDashboardMiddleModel");	
		
		ResourceSet rs = new ResourceSetImpl();
		URI path1 = URI.createFileURI(MiddleModelProject.getLocation().toString() + "/" + feature1.getName() + ".txt");
		Resource r1 = rs.createResource(path1);
		r1.getContents().add(feature1);
		r1.save(null);
		
		URI path2 = URI.createFileURI(MiddleModelProject.getLocation().toString() + "/" + ann1.getFileName() + "_"+ann1.getLineNumber()+".txt");
		Resource r2 = rs.createResource(path2);
		r2.getContents().add(ann1);
		r2.save(null);
		
		List<EObject> artifacts = new ArrayList<>();
		artifacts.add(feature1);
		artifacts.add(ann1);

		if(org.eclipse.capra.testsuite.TestHelper.thereIsATraceBetween(ann1, feature1)){
			System.out.println("There is already a link betwween them.");
			return;
		}
		
		TraceMetaModelAdapter traceAdapter = ExtensionPointHelper.getTraceMetamodelAdapter().get();
		TracePersistenceAdapter persistenceAdapter = ExtensionPointHelper.getTracePersistenceAdapter().get();

		ResourceSet resourceSet = new ResourceSetImpl();
		// add trace model to resource set
		EObject traceModel = persistenceAdapter.getTraceModel(resourceSet);
		// add artifact model to resource set
		EObject artifactModel = persistenceAdapter.getArtifactWrappers(resourceSet);
		
		ArtifactHelper artifactHelper = new ArtifactHelper(artifactModel);
		TraceHelper traceHelper = new TraceHelper(traceModel);
		
		// Create the artifact wrappers
		//List<EObject> wrappers = artifactHelper.createWrappers(artifacts);
				
		EClass traceType = (EClass)(new ArrayList(traceAdapter.getAvailableTraceTypes(artifacts))).get(0);
		
		traceHelper.createTrace(artifacts, traceType);
		persistenceAdapter.saveTracesAndArtifacts(traceModel, artifactModel);
		traceHelper.annotateTrace(artifacts);
		

		System.out.println("Trace link creation is finished.");
	}

	
}
