/**
 */
package FM_Annotations;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see FM_Annotations.FM_AnnotationsPackage
 * @generated
 */
public interface FM_AnnotationsFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	FM_AnnotationsFactory eINSTANCE = FM_Annotations.impl.FM_AnnotationsFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Feature Model</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Feature Model</em>'.
	 * @generated
	 */
	FeatureModel createFeatureModel();

	/**
	 * Returns a new object of class '<em>Annotations</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Annotations</em>'.
	 * @generated
	 */
	Annotations createAnnotations();

	/**
	 * Returns a new object of class '<em>Feature</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Feature</em>'.
	 * @generated
	 */
	Feature createFeature();

	/**
	 * Returns a new object of class '<em>Feature Model Relation</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Feature Model Relation</em>'.
	 * @generated
	 */
	FeatureModelRelation createFeatureModelRelation();

	/**
	 * Returns a new object of class '<em>Annotation</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Annotation</em>'.
	 * @generated
	 */
	Annotation createAnnotation();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	FM_AnnotationsPackage getFM_AnnotationsPackage();

} //FM_AnnotationsFactory
