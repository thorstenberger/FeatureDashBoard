/**
 */
package FM_Annotations.impl;

import FM_Annotations.FM_AnnotationsPackage;
import FM_Annotations.Feature;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Feature</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link FM_Annotations.impl.FeatureImpl#getName <em>Name</em>}</li>
 *   <li>{@link FM_Annotations.impl.FeatureImpl#getFeatureID <em>Feature ID</em>}</li>
 * </ul>
 *
 * @generated
 */
public class FeatureImpl extends MinimalEObjectImpl.Container implements Feature {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getFeatureID() <em>Feature ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFeatureID()
	 * @generated
	 * @ordered
	 */
	protected static final int FEATURE_ID_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getFeatureID() <em>Feature ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFeatureID()
	 * @generated
	 * @ordered
	 */
	protected int featureID = FEATURE_ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FeatureImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return FM_AnnotationsPackage.Literals.FEATURE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FM_AnnotationsPackage.FEATURE__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getFeatureID() {
		return featureID;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFeatureID(int newFeatureID) {
		int oldFeatureID = featureID;
		featureID = newFeatureID;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FM_AnnotationsPackage.FEATURE__FEATURE_ID, oldFeatureID, featureID));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case FM_AnnotationsPackage.FEATURE__NAME:
				return getName();
			case FM_AnnotationsPackage.FEATURE__FEATURE_ID:
				return getFeatureID();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case FM_AnnotationsPackage.FEATURE__NAME:
				setName((String)newValue);
				return;
			case FM_AnnotationsPackage.FEATURE__FEATURE_ID:
				setFeatureID((Integer)newValue);
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
			case FM_AnnotationsPackage.FEATURE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case FM_AnnotationsPackage.FEATURE__FEATURE_ID:
				setFeatureID(FEATURE_ID_EDEFAULT);
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
			case FM_AnnotationsPackage.FEATURE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case FM_AnnotationsPackage.FEATURE__FEATURE_ID:
				return this.featureID != FEATURE_ID_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(", featureID: ");
		result.append(featureID);
		result.append(')');
		return result.toString();
	}

} //FeatureImpl
