/**
 */
package FM_Annotations.impl;

import FM_Annotations.FM_AnnotationsPackage;
import FM_Annotations.Feature;
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
 * An implementation of the model object '<em><b>Feature Model Relation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link FM_Annotations.impl.FeatureModelRelationImpl#getFeature <em>Feature</em>}</li>
 *   <li>{@link FM_Annotations.impl.FeatureModelRelationImpl#getRelationID <em>Relation ID</em>}</li>
 *   <li>{@link FM_Annotations.impl.FeatureModelRelationImpl#getFormula <em>Formula</em>}</li>
 * </ul>
 *
 * @generated
 */
public class FeatureModelRelationImpl extends MinimalEObjectImpl.Container implements FeatureModelRelation {
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
	 * The default value of the '{@link #getRelationID() <em>Relation ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRelationID()
	 * @generated
	 * @ordered
	 */
	protected static final int RELATION_ID_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getRelationID() <em>Relation ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRelationID()
	 * @generated
	 * @ordered
	 */
	protected int relationID = RELATION_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getFormula() <em>Formula</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFormula()
	 * @generated
	 * @ordered
	 */
	protected static final String FORMULA_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFormula() <em>Formula</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFormula()
	 * @generated
	 * @ordered
	 */
	protected String formula = FORMULA_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FeatureModelRelationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return FM_AnnotationsPackage.Literals.FEATURE_MODEL_RELATION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Feature> getFeature() {
		if (feature == null) {
			feature = new EObjectResolvingEList<Feature>(Feature.class, this, FM_AnnotationsPackage.FEATURE_MODEL_RELATION__FEATURE);
		}
		return feature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getRelationID() {
		return relationID;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRelationID(int newRelationID) {
		int oldRelationID = relationID;
		relationID = newRelationID;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FM_AnnotationsPackage.FEATURE_MODEL_RELATION__RELATION_ID, oldRelationID, relationID));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getFormula() {
		return formula;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFormula(String newFormula) {
		String oldFormula = formula;
		formula = newFormula;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FM_AnnotationsPackage.FEATURE_MODEL_RELATION__FORMULA, oldFormula, formula));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case FM_AnnotationsPackage.FEATURE_MODEL_RELATION__FEATURE:
				return getFeature();
			case FM_AnnotationsPackage.FEATURE_MODEL_RELATION__RELATION_ID:
				return getRelationID();
			case FM_AnnotationsPackage.FEATURE_MODEL_RELATION__FORMULA:
				return getFormula();
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
			case FM_AnnotationsPackage.FEATURE_MODEL_RELATION__FEATURE:
				getFeature().clear();
				getFeature().addAll((Collection<? extends Feature>)newValue);
				return;
			case FM_AnnotationsPackage.FEATURE_MODEL_RELATION__RELATION_ID:
				setRelationID((Integer)newValue);
				return;
			case FM_AnnotationsPackage.FEATURE_MODEL_RELATION__FORMULA:
				setFormula((String)newValue);
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
			case FM_AnnotationsPackage.FEATURE_MODEL_RELATION__FEATURE:
				getFeature().clear();
				return;
			case FM_AnnotationsPackage.FEATURE_MODEL_RELATION__RELATION_ID:
				setRelationID(RELATION_ID_EDEFAULT);
				return;
			case FM_AnnotationsPackage.FEATURE_MODEL_RELATION__FORMULA:
				setFormula(FORMULA_EDEFAULT);
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
			case FM_AnnotationsPackage.FEATURE_MODEL_RELATION__FEATURE:
				return feature != null && !feature.isEmpty();
			case FM_AnnotationsPackage.FEATURE_MODEL_RELATION__RELATION_ID:
				return relationID != RELATION_ID_EDEFAULT;
			case FM_AnnotationsPackage.FEATURE_MODEL_RELATION__FORMULA:
				return FORMULA_EDEFAULT == null ? formula != null : !FORMULA_EDEFAULT.equals(formula);
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
		result.append(" (relationID: ");
		result.append(relationID);
		result.append(", formula: ");
		result.append(formula);
		result.append(')');
		return result.toString();
	}

} //FeatureModelRelationImpl
