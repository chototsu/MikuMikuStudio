package com.jme.math;

import com.jme.scene.Spatial;

/**
 * Started Date: Jul 16, 2004<br><br>
 * Same as TransformMatrix, but stores rotations as quats, not Matrix3f.  This is faster for interpolation, but slower for
 * point translation. 
 * @author Jack Lindamood
 */
public class TransformMatrixQuat {

    private Quaternion rot=new Quaternion();
    private Vector3f translation=new Vector3f();
    private Vector3f scale=new Vector3f(1,1,1);

    public void setRotationQuaternion(Quaternion rot) {
        this.rot.set(rot);
    }

    public void setTranslation(Vector3f trans) {
        this.translation.set(trans);
    }

    public void setScale(Vector3f scale) {
        this.scale.set(scale);
    }

    public Vector3f getTranslation(Vector3f trans) {
        if (trans==null) trans=new Vector3f();
        trans.set(this.translation);
        return trans;
    }

    public Quaternion getRotation(Quaternion quat) {
        if (quat==null) quat=new Quaternion();
        quat.set(rot);
        return quat;
    }

    public Vector3f getScale(Vector3f scale) {
        if (scale==null) scale=new Vector3f();
        scale.set(this.scale);
        return scale;
    }

    public void interpolateTransforms(TransformMatrixQuat t1, TransformMatrixQuat t2, float delta) {
        this.rot.slerp(t1.rot,t2.rot,delta);
        this.translation.interpolate(t1.translation,t2.translation,delta);
        this.scale.interpolate(t1.scale,t2.scale,delta);
    }

    public TransformMatrixQuat combineWithParent(TransformMatrixQuat parent) {
        scale.multLocal(parent.scale);
        rot.set(parent.rot.mult(rot));
            parent
            .rot
            .multLocal(translation)
            .multLocal(parent.scale)
            .addLocal(parent.translation);
        return this;
    }

    public void applyToSpatial(Spatial spatial) {
        spatial.setLocalScale(scale);
        spatial.setLocalRotation(rot);
        spatial.setLocalTranslation(translation);
    }

    public void setTranslation(float x,float y, float z) {
        translation.set(x,y,z);
    }

    public void setScale(float x, float y, float z) {
        scale.set(x,y,z);
    }

    public void loadIdentity() {
        translation.set(0,0,0);
        scale.set(1,1,1);
        rot.set(0,0,0,1);
    }

    public void set(TransformMatrixQuat matrixQuat) {
        this.translation.set(matrixQuat.translation);
        this.rot.set(matrixQuat.rot);
        this.scale.set(matrixQuat.scale);
    }

    public TransformMatrixQuat multLocal(TransformMatrixQuat child, Vector3f tempStore) {
        this.scale.multLocal(child.scale);
        this.translation.addLocal(rot.mult(child.translation,tempStore).multLocal(child.scale));
        this.rot.multLocal(child.rot);
        return this;
    }
}
