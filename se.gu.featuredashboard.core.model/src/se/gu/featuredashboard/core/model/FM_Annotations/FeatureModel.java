/**
 */
package se.gu.featuredashboard.core.model.FM_Annotations;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Feature Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link se.gu.featuredashboard.core.model.FM_Annotations.FeatureModel#getFeature <em>Feature</em>}</li>
 *   <li>{@link se.gu.featuredashboard.core.model.FM_Annotations.FeatureModel#getFeaturemodelrelation <em>Featuremodelrelation</em>}</li>
 *   <li>{@link se.gu.featuredashboard.core.model.FM_Annotations.FeatureModel#getName <em>Name</em>}</li>
 * </ul>
 *
 * @see se.gu.featuredashboard.core.model.FM_Annotations.AnnotationsPackage#getFeatureModel()
 * @model
 * @generated
 */
public interface FeatureModel extends EObject {
	/**
	 * Returns the value of the '<em><b>Feature</b></em>' reference list.
	 * The list contents are of type {@link se.gu.featuredashboard.core.model.FM_Annotations.Feature}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Feature</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Feature</em>' reference list.
	 * @see se.gu.featuredashboard.core.model.FM_Annotations.AnnotationsPackage#getFeatureModel_Feature()
	 * @model
	 * @generated
	 */
	EList<Feature> getFeature();

	/**
	 * Returns the value of the '<em><b>Featuremodelrelation</b></em>' reference list.
	 * The list contents are of type {@link se.gu.featuredashboard.core.model.FM_Annotations.FeatureModelRelation}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Featuremodelrelation</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Featuremodelrelation</em>' reference list.
	 * @see se.gu.featuredashboard.core.model.FM_Annotations.AnnotationsPackage#getFeatureModel_Featuremodelrelation()
	 * @model
	 * @generated
	 */
	EList<FeatureModelRelation> getFeaturemodelrelation();

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see se.gu.featuredashboard.core.model.FM_Annotations.AnnotationsPackage#getFeatureModel_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link se.gu.featuredashboard.core.model.FM_Annotations.FeatureModel#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

} // FeatureModel
