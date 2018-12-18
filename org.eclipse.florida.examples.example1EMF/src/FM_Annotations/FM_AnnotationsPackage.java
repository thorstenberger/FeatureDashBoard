/**
 */
package FM_Annotations;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see FM_Annotations.FM_AnnotationsFactory
 * @model kind="package"
 * @generated
 */
public interface FM_AnnotationsPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "FM_Annotations";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.example.org/FM_Annotations";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "FM_Annotations";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	FM_AnnotationsPackage eINSTANCE = FM_Annotations.impl.FM_AnnotationsPackageImpl.init();

	/**
	 * The meta object id for the '{@link FM_Annotations.impl.FeatureModelImpl <em>Feature Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see FM_Annotations.impl.FeatureModelImpl
	 * @see FM_Annotations.impl.FM_AnnotationsPackageImpl#getFeatureModel()
	 * @generated
	 */
	int FEATURE_MODEL = 0;

	/**
	 * The feature id for the '<em><b>Feature</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MODEL__FEATURE = 0;

	/**
	 * The feature id for the '<em><b>Featuremodelrelation</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MODEL__FEATUREMODELRELATION = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MODEL__NAME = 2;

	/**
	 * The number of structural features of the '<em>Feature Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MODEL_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Feature Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MODEL_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link FM_Annotations.impl.AnnotationsImpl <em>Annotations</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see FM_Annotations.impl.AnnotationsImpl
	 * @see FM_Annotations.impl.FM_AnnotationsPackageImpl#getAnnotations()
	 * @generated
	 */
	int ANNOTATIONS = 1;

	/**
	 * The feature id for the '<em><b>Annotation</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANNOTATIONS__ANNOTATION = 0;

	/**
	 * The number of structural features of the '<em>Annotations</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANNOTATIONS_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Annotations</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANNOTATIONS_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link FM_Annotations.impl.FeatureImpl <em>Feature</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see FM_Annotations.impl.FeatureImpl
	 * @see FM_Annotations.impl.FM_AnnotationsPackageImpl#getFeature()
	 * @generated
	 */
	int FEATURE = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Feature ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE__FEATURE_ID = 1;

	/**
	 * The number of structural features of the '<em>Feature</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Feature</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link FM_Annotations.impl.FeatureModelRelationImpl <em>Feature Model Relation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see FM_Annotations.impl.FeatureModelRelationImpl
	 * @see FM_Annotations.impl.FM_AnnotationsPackageImpl#getFeatureModelRelation()
	 * @generated
	 */
	int FEATURE_MODEL_RELATION = 3;

	/**
	 * The feature id for the '<em><b>Feature</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MODEL_RELATION__FEATURE = 0;

	/**
	 * The feature id for the '<em><b>Relation ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MODEL_RELATION__RELATION_ID = 1;

	/**
	 * The feature id for the '<em><b>Formula</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MODEL_RELATION__FORMULA = 2;

	/**
	 * The number of structural features of the '<em>Feature Model Relation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MODEL_RELATION_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Feature Model Relation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MODEL_RELATION_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link FM_Annotations.impl.AnnotationImpl <em>Annotation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see FM_Annotations.impl.AnnotationImpl
	 * @see FM_Annotations.impl.FM_AnnotationsPackageImpl#getAnnotation()
	 * @generated
	 */
	int ANNOTATION = 4;

	/**
	 * The feature id for the '<em><b>Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANNOTATION__FEATURE = 0;

	/**
	 * The feature id for the '<em><b>File Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANNOTATION__FILE_NAME = 1;

	/**
	 * The feature id for the '<em><b>Line Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANNOTATION__LINE_NUMBER = 2;

	/**
	 * The number of structural features of the '<em>Annotation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANNOTATION_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Annotation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANNOTATION_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link FM_Annotations.FeatureModel <em>Feature Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Feature Model</em>'.
	 * @see FM_Annotations.FeatureModel
	 * @generated
	 */
	EClass getFeatureModel();

	/**
	 * Returns the meta object for the reference list '{@link FM_Annotations.FeatureModel#getFeature <em>Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Feature</em>'.
	 * @see FM_Annotations.FeatureModel#getFeature()
	 * @see #getFeatureModel()
	 * @generated
	 */
	EReference getFeatureModel_Feature();

	/**
	 * Returns the meta object for the reference list '{@link FM_Annotations.FeatureModel#getFeaturemodelrelation <em>Featuremodelrelation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Featuremodelrelation</em>'.
	 * @see FM_Annotations.FeatureModel#getFeaturemodelrelation()
	 * @see #getFeatureModel()
	 * @generated
	 */
	EReference getFeatureModel_Featuremodelrelation();

	/**
	 * Returns the meta object for the attribute '{@link FM_Annotations.FeatureModel#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see FM_Annotations.FeatureModel#getName()
	 * @see #getFeatureModel()
	 * @generated
	 */
	EAttribute getFeatureModel_Name();

	/**
	 * Returns the meta object for class '{@link FM_Annotations.Annotations <em>Annotations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Annotations</em>'.
	 * @see FM_Annotations.Annotations
	 * @generated
	 */
	EClass getAnnotations();

	/**
	 * Returns the meta object for the reference list '{@link FM_Annotations.Annotations#getAnnotation <em>Annotation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Annotation</em>'.
	 * @see FM_Annotations.Annotations#getAnnotation()
	 * @see #getAnnotations()
	 * @generated
	 */
	EReference getAnnotations_Annotation();

	/**
	 * Returns the meta object for class '{@link FM_Annotations.Feature <em>Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Feature</em>'.
	 * @see FM_Annotations.Feature
	 * @generated
	 */
	EClass getFeature();

	/**
	 * Returns the meta object for the attribute '{@link FM_Annotations.Feature#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see FM_Annotations.Feature#getName()
	 * @see #getFeature()
	 * @generated
	 */
	EAttribute getFeature_Name();

	/**
	 * Returns the meta object for the attribute '{@link FM_Annotations.Feature#getFeatureID <em>Feature ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Feature ID</em>'.
	 * @see FM_Annotations.Feature#getFeatureID()
	 * @see #getFeature()
	 * @generated
	 */
	EAttribute getFeature_FeatureID();

	/**
	 * Returns the meta object for class '{@link FM_Annotations.FeatureModelRelation <em>Feature Model Relation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Feature Model Relation</em>'.
	 * @see FM_Annotations.FeatureModelRelation
	 * @generated
	 */
	EClass getFeatureModelRelation();

	/**
	 * Returns the meta object for the reference list '{@link FM_Annotations.FeatureModelRelation#getFeature <em>Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Feature</em>'.
	 * @see FM_Annotations.FeatureModelRelation#getFeature()
	 * @see #getFeatureModelRelation()
	 * @generated
	 */
	EReference getFeatureModelRelation_Feature();

	/**
	 * Returns the meta object for the attribute '{@link FM_Annotations.FeatureModelRelation#getRelationID <em>Relation ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Relation ID</em>'.
	 * @see FM_Annotations.FeatureModelRelation#getRelationID()
	 * @see #getFeatureModelRelation()
	 * @generated
	 */
	EAttribute getFeatureModelRelation_RelationID();

	/**
	 * Returns the meta object for the attribute '{@link FM_Annotations.FeatureModelRelation#getFormula <em>Formula</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Formula</em>'.
	 * @see FM_Annotations.FeatureModelRelation#getFormula()
	 * @see #getFeatureModelRelation()
	 * @generated
	 */
	EAttribute getFeatureModelRelation_Formula();

	/**
	 * Returns the meta object for class '{@link FM_Annotations.Annotation <em>Annotation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Annotation</em>'.
	 * @see FM_Annotations.Annotation
	 * @generated
	 */
	EClass getAnnotation();

	/**
	 * Returns the meta object for the reference '{@link FM_Annotations.Annotation#getFeature <em>Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Feature</em>'.
	 * @see FM_Annotations.Annotation#getFeature()
	 * @see #getAnnotation()
	 * @generated
	 */
	EReference getAnnotation_Feature();

	/**
	 * Returns the meta object for the attribute '{@link FM_Annotations.Annotation#getFileName <em>File Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>File Name</em>'.
	 * @see FM_Annotations.Annotation#getFileName()
	 * @see #getAnnotation()
	 * @generated
	 */
	EAttribute getAnnotation_FileName();

	/**
	 * Returns the meta object for the attribute '{@link FM_Annotations.Annotation#getLineNumber <em>Line Number</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Line Number</em>'.
	 * @see FM_Annotations.Annotation#getLineNumber()
	 * @see #getAnnotation()
	 * @generated
	 */
	EAttribute getAnnotation_LineNumber();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	FM_AnnotationsFactory getFM_AnnotationsFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link FM_Annotations.impl.FeatureModelImpl <em>Feature Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see FM_Annotations.impl.FeatureModelImpl
		 * @see FM_Annotations.impl.FM_AnnotationsPackageImpl#getFeatureModel()
		 * @generated
		 */
		EClass FEATURE_MODEL = eINSTANCE.getFeatureModel();

		/**
		 * The meta object literal for the '<em><b>Feature</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FEATURE_MODEL__FEATURE = eINSTANCE.getFeatureModel_Feature();

		/**
		 * The meta object literal for the '<em><b>Featuremodelrelation</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FEATURE_MODEL__FEATUREMODELRELATION = eINSTANCE.getFeatureModel_Featuremodelrelation();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_MODEL__NAME = eINSTANCE.getFeatureModel_Name();

		/**
		 * The meta object literal for the '{@link FM_Annotations.impl.AnnotationsImpl <em>Annotations</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see FM_Annotations.impl.AnnotationsImpl
		 * @see FM_Annotations.impl.FM_AnnotationsPackageImpl#getAnnotations()
		 * @generated
		 */
		EClass ANNOTATIONS = eINSTANCE.getAnnotations();

		/**
		 * The meta object literal for the '<em><b>Annotation</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ANNOTATIONS__ANNOTATION = eINSTANCE.getAnnotations_Annotation();

		/**
		 * The meta object literal for the '{@link FM_Annotations.impl.FeatureImpl <em>Feature</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see FM_Annotations.impl.FeatureImpl
		 * @see FM_Annotations.impl.FM_AnnotationsPackageImpl#getFeature()
		 * @generated
		 */
		EClass FEATURE = eINSTANCE.getFeature();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE__NAME = eINSTANCE.getFeature_Name();

		/**
		 * The meta object literal for the '<em><b>Feature ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE__FEATURE_ID = eINSTANCE.getFeature_FeatureID();

		/**
		 * The meta object literal for the '{@link FM_Annotations.impl.FeatureModelRelationImpl <em>Feature Model Relation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see FM_Annotations.impl.FeatureModelRelationImpl
		 * @see FM_Annotations.impl.FM_AnnotationsPackageImpl#getFeatureModelRelation()
		 * @generated
		 */
		EClass FEATURE_MODEL_RELATION = eINSTANCE.getFeatureModelRelation();

		/**
		 * The meta object literal for the '<em><b>Feature</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FEATURE_MODEL_RELATION__FEATURE = eINSTANCE.getFeatureModelRelation_Feature();

		/**
		 * The meta object literal for the '<em><b>Relation ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_MODEL_RELATION__RELATION_ID = eINSTANCE.getFeatureModelRelation_RelationID();

		/**
		 * The meta object literal for the '<em><b>Formula</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_MODEL_RELATION__FORMULA = eINSTANCE.getFeatureModelRelation_Formula();

		/**
		 * The meta object literal for the '{@link FM_Annotations.impl.AnnotationImpl <em>Annotation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see FM_Annotations.impl.AnnotationImpl
		 * @see FM_Annotations.impl.FM_AnnotationsPackageImpl#getAnnotation()
		 * @generated
		 */
		EClass ANNOTATION = eINSTANCE.getAnnotation();

		/**
		 * The meta object literal for the '<em><b>Feature</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ANNOTATION__FEATURE = eINSTANCE.getAnnotation_Feature();

		/**
		 * The meta object literal for the '<em><b>File Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ANNOTATION__FILE_NAME = eINSTANCE.getAnnotation_FileName();

		/**
		 * The meta object literal for the '<em><b>Line Number</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ANNOTATION__LINE_NUMBER = eINSTANCE.getAnnotation_LineNumber();

	}

} //FM_AnnotationsPackage
