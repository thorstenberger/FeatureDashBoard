/**
 */
package FM_Annotations.impl;

import FM_Annotations.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class FM_AnnotationsFactoryImpl extends EFactoryImpl implements FM_AnnotationsFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static FM_AnnotationsFactory init() {
		try {
			FM_AnnotationsFactory theFM_AnnotationsFactory = (FM_AnnotationsFactory)EPackage.Registry.INSTANCE.getEFactory(FM_AnnotationsPackage.eNS_URI);
			if (theFM_AnnotationsFactory != null) {
				return theFM_AnnotationsFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new FM_AnnotationsFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FM_AnnotationsFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case FM_AnnotationsPackage.FEATURE_MODEL: return createFeatureModel();
			case FM_AnnotationsPackage.ANNOTATIONS: return createAnnotations();
			case FM_AnnotationsPackage.FEATURE: return createFeature();
			case FM_AnnotationsPackage.FEATURE_MODEL_RELATION: return createFeatureModelRelation();
			case FM_AnnotationsPackage.ANNOTATION: return createAnnotation();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureModel createFeatureModel() {
		FeatureModelImpl featureModel = new FeatureModelImpl();
		return featureModel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Annotations createAnnotations() {
		AnnotationsImpl annotations = new AnnotationsImpl();
		return annotations;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Feature createFeature() {
		FeatureImpl feature = new FeatureImpl();
		return feature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureModelRelation createFeatureModelRelation() {
		FeatureModelRelationImpl featureModelRelation = new FeatureModelRelationImpl();
		return featureModelRelation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Annotation createAnnotation() {
		AnnotationImpl annotation = new AnnotationImpl();
		return annotation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FM_AnnotationsPackage getFM_AnnotationsPackage() {
		return (FM_AnnotationsPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static FM_AnnotationsPackage getPackage() {
		return FM_AnnotationsPackage.eINSTANCE;
	}

} //FM_AnnotationsFactoryImpl
