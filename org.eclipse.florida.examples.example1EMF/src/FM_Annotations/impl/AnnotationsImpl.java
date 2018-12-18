/**
 */
package FM_Annotations.impl;

import FM_Annotations.Annotation;
import FM_Annotations.Annotations;
import FM_Annotations.FM_AnnotationsPackage;

import java.util.Collection;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Annotations</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link FM_Annotations.impl.AnnotationsImpl#getAnnotation <em>Annotation</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AnnotationsImpl extends MinimalEObjectImpl.Container implements Annotations {
	/**
	 * The cached value of the '{@link #getAnnotation() <em>Annotation</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAnnotation()
	 * @generated
	 * @ordered
	 */
	protected EList<Annotation> annotation;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AnnotationsImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return FM_AnnotationsPackage.Literals.ANNOTATIONS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Annotation> getAnnotation() {
		if (annotation == null) {
			annotation = new EObjectResolvingEList<Annotation>(Annotation.class, this, FM_AnnotationsPackage.ANNOTATIONS__ANNOTATION);
		}
		return annotation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case FM_AnnotationsPackage.ANNOTATIONS__ANNOTATION:
				return getAnnotation();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case FM_AnnotationsPackage.ANNOTATIONS__ANNOTATION:
				getAnnotation().clear();
				getAnnotation().addAll((Collection<? extends Annotation>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case FM_AnnotationsPackage.ANNOTATIONS__ANNOTATION:
				getAnnotation().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case FM_AnnotationsPackage.ANNOTATIONS__ANNOTATION:
				return annotation != null && !annotation.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //AnnotationsImpl
