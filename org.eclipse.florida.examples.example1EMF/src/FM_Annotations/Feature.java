/**
 */
package FM_Annotations;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Feature</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link FM_Annotations.Feature#getName <em>Name</em>}</li>
 *   <li>{@link FM_Annotations.Feature#getFeatureID <em>Feature ID</em>}</li>
 * </ul>
 *
 * @see FM_Annotations.FM_AnnotationsPackage#getFeature()
 * @model
 * @generated
 */
public interface Feature extends EObject {
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
	 * @see FM_Annotations.FM_AnnotationsPackage#getFeature_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link FM_Annotations.Feature#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Feature ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Feature ID</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Feature ID</em>' attribute.
	 * @see #setFeatureID(int)
	 * @see FM_Annotations.FM_AnnotationsPackage#getFeature_FeatureID()
	 * @model
	 * @generated
	 */
	int getFeatureID();

	/**
	 * Sets the value of the '{@link FM_Annotations.Feature#getFeatureID <em>Feature ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Feature ID</em>' attribute.
	 * @see #getFeatureID()
	 * @generated
	 */
	void setFeatureID(int value);

} // Feature
