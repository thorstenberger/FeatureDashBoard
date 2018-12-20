/**
 */
package se.gu.featuredashboard.core.model.FM_Annotations;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Annotations</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link se.gu.featuredashboard.core.model.FM_Annotations.Annotations#getAnnotation <em>Annotation</em>}</li>
 * </ul>
 *
 * @see se.gu.featuredashboard.core.model.FM_Annotations.AnnotationsPackage#getAnnotations()
 * @model
 * @generated
 */
public interface Annotations extends EObject {
	/**
	 * Returns the value of the '<em><b>Annotation</b></em>' reference list.
	 * The list contents are of type {@link se.gu.featuredashboard.core.model.FM_Annotations.Annotation}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Annotation</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Annotation</em>' reference list.
	 * @see se.gu.featuredashboard.core.model.FM_Annotations.AnnotationsPackage#getAnnotations_Annotation()
	 * @model
	 * @generated
	 */
	EList<Annotation> getAnnotation();

} // Annotations
