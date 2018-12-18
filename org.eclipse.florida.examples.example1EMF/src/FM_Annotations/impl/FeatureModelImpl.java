/**
 */
package FM_Annotations.impl;

import FM_Annotations.FM_AnnotationsPackage;
import FM_Annotations.Feature;
import FM_Annotations.FeatureModel;
import FM_Annotations.FeatureModelRelation;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Feature Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link FM_Annotations.impl.FeatureModelImpl#getFeature <em>Feature</em>}</li>
 *   <li>{@link FM_Annotations.impl.FeatureModelImpl#getFeaturemodelrelation <em>Featuremodelrelation</em>}</li>
 *   <li>{@link FM_Annotations.impl.FeatureModelImpl#getName <em>Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class FeatureModelImpl extends MinimalEObjectImpl.Container implements FeatureModel {
	/**
	 * The cached value of the '{@link #getFeature() <em>Feature</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFeature()
	 * @generated
	 * @ordered
	 */
	protected EList<Feature> feature;

	/**
	 * The cached value of the '{@link #getFeaturemodelrelation() <em>Featuremodelrelation</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFeaturemodelrelation()
	 * @generated
	 * @ordered
	 */
	protected EList<FeatureModelRelation> featuremodelrelation;

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FeatureModelImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return FM_AnnotationsPackage.Literals.FEATURE_MODEL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Feature> getFeature() {
		if (feature == null) {
			feature = new EObjectResolvingEList<Feature>(Feature.class, this, FM_AnnotationsPackage.FEATURE_MODEL__FEATURE);
		}
		return feature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<FeatureModelRelation> getFeaturemodelrelation() {
		if (featuremodelrelation == null) {
			featuremodelrelation = new EObjectResolvingEList<FeatureModelRelation>(FeatureModelRelation.class, this, FM_AnnotationsPackage.FEATURE_MODEL__FEATUREMODELRELATION);
		}
		return featuremodelrelation;
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
			eNotify(new ENotificationImpl(this, Notification.SET, FM_AnnotationsPackage.FEATURE_MODEL__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case FM_AnnotationsPackage.FEATURE_MODEL__FEATURE:
				return getFeature();
			case FM_AnnotationsPackage.FEATURE_MODEL__FEATUREMODELRELATION:
				return getFeaturemodelrelation();
			case FM_AnnotationsPackage.FEATURE_MODEL__NAME:
				return getName();
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
			case FM_AnnotationsPackage.FEATURE_MODEL__FEATURE:
				getFeature().clear();
				getFeature().addAll((Collection<? extends Feature>)newValue);
				return;
			case FM_AnnotationsPackage.FEATURE_MODEL__FEATUREMODELRELATION:
				getFeaturemodelrelation().clear();
				getFeaturemodelrelation().addAll((Collection<? extends FeatureModelRelation>)newValue);
				return;
			case FM_AnnotationsPackage.FEATURE_MODEL__NAME:
				setName((String)newValue);
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
			case FM_AnnotationsPackage.FEATURE_MODEL__FEATURE:
				getFeature().clear();
				return;
			case FM_AnnotationsPackage.FEATURE_MODEL__FEATUREMODELRELATION:
				getFeaturemodelrelation().clear();
				return;
			case FM_AnnotationsPackage.FEATURE_MODEL__NAME:
				setName(NAME_EDEFAULT);
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
			case FM_AnnotationsPackage.FEATURE_MODEL__FEATURE:
				return feature != null && !feature.isEmpty();
			case FM_AnnotationsPackage.FEATURE_MODEL__FEATUREMODELRELATION:
				return featuremodelrelation != null && !featuremodelrelation.isEmpty();
			case FM_AnnotationsPackage.FEATURE_MODEL__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
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
		result.append(')');
		return result.toString();
	}

} //FeatureModelImpl
