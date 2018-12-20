/**
 */
package se.gu.featuredashboard.core.model.FM_Annotations;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Feature Model Relation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link se.gu.featuredashboard.core.model.FM_Annotations.FeatureModelRelation#getFeature <em>Feature</em>}</li>
 *   <li>{@link se.gu.featuredashboard.core.model.FM_Annotations.FeatureModelRelation#getRelationID <em>Relation ID</em>}</li>
 *   <li>{@link se.gu.featuredashboard.core.model.FM_Annotations.FeatureModelRelation#getFormula <em>Formula</em>}</li>
 * </ul>
 *
 * @see se.gu.featuredashboard.core.model.FM_Annotations.AnnotationsPackage#getFeatureModelRelation()
 * @model
 * @generated
 */
public interface FeatureModelRelation extends EObject {
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
	 * @see se.gu.featuredashboard.core.model.FM_Annotations.AnnotationsPackage#getFeatureModelRelation_Feature()
	 * @model lower="2"
	 * @generated
	 */
	EList<Feature> getFeature();

	/**
	 * Returns the value of the '<em><b>Relation ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Relation ID</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Relation ID</em>' attribute.
	 * @see #setRelationID(int)
	 * @see se.gu.featuredashboard.core.model.FM_Annotations.AnnotationsPackage#getFeatureModelRelation_RelationID()
	 * @model
	 * @generated
	 */
	int getRelationID();

	/**
	 * Sets the value of the '{@link se.gu.featuredashboard.core.model.FM_Annotations.FeatureModelRelation#getRelationID <em>Relation ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Relation ID</em>' attribute.
	 * @see #getRelationID()
	 * @generated
	 */
	void setRelationID(int value);

	/**
	 * Returns the value of the '<em><b>Formula</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Formula</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Formula</em>' attribute.
	 * @see #setFormula(String)
	 * @see se.gu.featuredashboard.core.model.FM_Annotations.AnnotationsPackage#getFeatureModelRelation_Formula()
	 * @model
	 * @generated
	 */
	String getFormula();

	/**
	 * Sets the value of the '{@link se.gu.featuredashboard.core.model.FM_Annotations.FeatureModelRelation#getFormula <em>Formula</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Formula</em>' attribute.
	 * @see #getFormula()
	 * @generated
	 */
	void setFormula(String value);

} // FeatureModelRelation
