package com.jme.math;

import com.jme.scene.Spatial;

/**
 * Started Date: Jul 16, 2004<br><br>
 * Same as TransformMatrix, but stores rotations as quats, not Matrix3f.  This is faster for interpolation, but slower
 * than a matrix using Matrix3f for rotation when doing point translation.
 * @author Jack Lindamood
 */
public class TransformMatrixQuat {

    private Quaternion rot=new Quaternion();
    private Vector3f translation=new Vector3f();
    private Vector3f scale=new Vector3f(1,1,1);

    /**
     * Sets this rotation to the given Quaternion value.
     * @param rot The new rotation for this matrix.
     */
    public void setRotationQuaternion(Quaternion rot) {
        this.rot.set(rot);
    }

    /**
     * Sets this translation to the given value.
     * @param trans The new translation for this matrix.
     */
    public void setTranslation(Vector3f trans) {
        this.translation.set(trans);
    }

    /**
     * Sets this scale to the given value.
     * @param scale The new scale for this matrix.
     */
    public void setScale(Vector3f scale) {
        this.scale.set(scale);
    }

    /**
     * Stores this translation value into the given vector3f.  If trans is null, a new vector3f is created to
     * hold the value.  The value, once stored, is returned.
     * @param trans The store location for this matrix's translation.
     * @return The value of this matrix's translation.
     */
    public Vector3f getTranslation(Vector3f trans) {
        if (trans==null) trans=new Vector3f();
        trans.set(this.translation);
        return trans;
    }

    /**
     * Stores this rotation value into the given Quaternion.  If quat is null, a new Quaternion is created to
     * hold the value.  The value, once stored, is returned.
     * @param quat The store location for this matrix's rotation.
     * @return The value of this matrix's rotation.
     */
    public Quaternion getRotation(Quaternion quat) {
        if (quat==null) quat=new Quaternion();
        quat.set(rot);
        return quat;
    }

    /**
     * Stores this scale value into the given vector3f.  If scale is null, a new vector3f is created to
     * hold the value.  The value, once stored, is returned.
     * @param scale The store location for this matrix's scale.
     * @return The value of this matrix's scale.
     */
    public Vector3f getScale(Vector3f scale) {
        if (scale==null) scale=new Vector3f();
        scale.set(this.scale);
        return scale;
    }

    /**
     * Sets this matrix to the interpolation between the first matrix and the second by delta amount.
     * @param t1 The begining transform.
     * @param t2 The ending transform.
     * @param delta An amount between 0 and 1 representing how far to interpolate from t1 to t2.
     */
    public void interpolateTransforms(TransformMatrixQuat t1, TransformMatrixQuat t2, float delta) {
        this.rot.slerp(t1.rot,t2.rot,delta);
        this.translation.interpolate(t1.translation,t2.translation,delta);
        this.scale.interpolate(t1.scale,t2.scale,delta);
    }

    /**
     * Changes the values of this matrix acording to it's parent.  Very similar to the concept of Node/Spatial transforms.
     * @param parent The parent matrix.
     * @return This matrix, after combining.
     */
    public TransformMatrixQuat combineWithParent(TransformMatrixQuat parent) {
        scale.multLocal(parent.scale);
        rot.multLocal(parent.rot);
        parent
            .rot
            .multLocal(translation)
            .multLocal(parent.scale)
            .addLocal(parent.translation);
        return this;
    }

    /**
     * Applies the values of this matrix to the given Spatial.
     * @param spatial The spatial to be affected by this matrix.
     */
    public void applyToSpatial(Spatial spatial) {
        spatial.getLocalScale().set(scale);
        spatial.getLocalRotation().set(rot);
        spatial.getLocalTranslation().set(translation);
    }

    /**
     * Sets this matrix's translation to the given x,y,z values.
     * @param x This matrix's new x translation.
     * @param y This matrix's new y translation.
     * @param z This matrix's new z translation.
     */
    public void setTranslation(float x,float y, float z) {
        translation.set(x,y,z);
    }

    /**
     * Sets this matrix's scale to the given x,y,z values.
     * @param x This matrix's new x scale.
     * @param y This matrix's new y scale.
     * @param z This matrix's new z scale.
     */     public void setScale(float x, float y, float z) {
        scale.set(x,y,z);
    }

    /**
     * Loads the identity.  Equal to translation=1,1,1 scale=0,0,0 rot=0,0,0,1.
     */
    public void loadIdentity() {
        translation.set(0,0,0);
        scale.set(1,1,1);
        rot.set(0,0,0,1);
    }

    /**
     * Sets this matrix to be equal to the given matrix.
     * @param matrixQuat The matrix to be equal to.
     */
    public void set(TransformMatrixQuat matrixQuat) {
        this.translation.set(matrixQuat.translation);
        this.rot.set(matrixQuat.rot);
        this.scale.set(matrixQuat.scale);
    }
}
