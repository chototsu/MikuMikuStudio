package com.jme.math;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.math.Matrix3f;
import com.jme.util.LoggingSystem;
import com.jme.system.JmeException;

import java.util.logging.Level;


/**
 * TransformMatrix holds a rotation (Matrix3f)  and translation (Vector3f) for point manipulation
 *
 * @author Jack Lindamood
 */
public class TransformMatrix {

    private Matrix3f rot=new Matrix3f();
    private Vector3f translation=new Vector3f();

    /**
     * Constructor instantiates a new <code>TransformMatrix</code> that is set to the
     * identity matrix by default.
     *
     */
    public TransformMatrix() {
    }

    /**
     * Constructor instantiates a new <code>TransformMatrix</code> that is set to the
     * provided matrix. This constructor copies a given matrix. If the
     * provided matrix is null, the constructor sets the matrix to the
     * identity.
     * @param mat the matrix to copy.
     */
    public TransformMatrix(TransformMatrix mat) {
        set(mat);
    }

    /**
     * Constructor instantiates a new <code>TransformMatrix</code> that has rotation
     * and translation defined by its parameters
     * @param myRot The given rotation, as a <code>Quaternion</code>
     * @param myPos The given translation, as a <code>Vector3f</code>
     */
    public TransformMatrix(Quaternion myRot, Vector3f myPos) {
        rot.set(myRot);
        translation.set(myPos);
    }

    /**
     * <code>set</code> transfers the contents of a given matrix to this
     * matrix. If a null matrix is supplied, this matrix is set to the
     * identity matrix.
     * @param matrix the matrix to copy.
     */
    public void set(TransformMatrix matrix) {
        if (matrix == null) {
            loadIdentity();
        } else {
            rot.copy(matrix.rot);
            translation.set(matrix.translation);
        }
    }


    /**
     *
     * <code>set</code> defines the values of the matrix based on a supplied
     * <code>Quaternion</code>. It should be noted that all previous values
     * will be overridden.
     * @param quaternion the quaternion to create a rotational matrix from.
     */
    public void set(Quaternion quaternion) {
        rot.set(quaternion);
        translation.zero();
    }

    /**
     * <code>loadIdentity</code> sets this matrix to the identity matrix,
     * namely all zeros with ones along the diagonal.
     *
     */
    public void loadIdentity() {
        rot.loadIdentity();
        translation.zero();
    }

    /**
     * Multiplies every value in the matrix by a scalar
     * @param scalar
     */
    public void mult(float scalar) {
        rot.multiply(scalar);
        translation.mult(scalar);
    }

    /**
     * <code>multLocal</code> multiplies this matrix with another matrix and stores
     * the result back in this, returning this.  if null is passed, nothing happens
     * This function is equivilent to this*=in2;
     * @param inMatrix The matrix to multiply by
     * @return this matrix after multiplication
     */
    public TransformMatrix multLocal(TransformMatrix inMatrix){

//      Math: {this=2: inMatrix=1 } (R2 ( R1 V + T1) + T2) = (R2 R1) V + (R2 T1 + T2)
        translation.addLocal(rot.mult(inMatrix.translation));
        rot.multLocal(inMatrix.rot);
        return this;
    }

    /**
     * <code>mult</code> multiplies a normal about a transform matrix and
     * stores the result back in vec. The resulting vector is returned
     * with translational ignored.
     * @param vec the rotation normal.
     * @return The given Vector3f, after rotation
     */
    public Vector3f multNormal(Vector3f vec) {
        if (null == vec) {
            LoggingSystem.getLogger().log(
                Level.WARNING,
                "Source vector is null, null result returned.");
            return null;
        }
        return rot.multLocal(vec);
    }

    /**
     * <code>mult</code> multiplies a vector about a transform matrix. The
     * resulting vector is saved in vec and returned.
     * @param vec The point to rotate.
     * @return The rotated vector.
     */
    public Vector3f multPoint(Vector3f vec) {
        if (null == vec) {
            LoggingSystem.getLogger().log(
                Level.WARNING,
                "Source vector is null, null result returned.");
            return null;
        }
        return rot.multLocal(vec).addLocal(translation);
    }

    /**
     * <code>setTranslation</code> will set the matrix's translation values.
     * @param transArray the new values for the translation.
     * @throws JmeException if translation is null or not size 3.
     */
    public void setTranslation(float[] transArray) {
        if (transArray == null || transArray.length != 3) {
            throw new JmeException("Translation size must be 3.");
        }
        translation.x = transArray[0];
        translation.y = transArray[1];
        translation.z = transArray[2];
    }

    /** <code>setTranslation</code> will copy the given Vector3f's values
     * into this Matrix's translational component
     *
     * @param trans
     */
    public void setTranslation(Vector3f trans){
        if (trans==null){
            throw new JmeException("Vector3f translation must be non-null");
        }
        translation.set(trans);
    }

    /**
     * Sets the Transform's Translational component
     * @param x New X translation
     * @param y New Y translation
     * @param z New Z translation
     */
    public void setTranslation(float x,float y,float z){
        translation.set(x,y,z);
    }

    /**
     * Sets the rotational component of this transform to the matrix represented
     * by an Euler rotation about x, y, then z and the translational component to
     * the identity
     * @param x The X rotation, in radians
     * @param y The Y rotation, in radians
     * @param z The Z rotation, in radians
     */
    public void setEulerRot(float x,float y,float z){
        double A = Math.cos(x);
        double B = Math.sin(x);
        double C = Math.cos(y);
        double D = Math.sin(y);
        double E = Math.cos(z);
        double F = Math.sin(z);
        double AD =   A * D;
        double BD =   B * D;
        rot.m00 = (float) (C * E);
        rot.m01 = (float) (BD * E + -(A * F));
        rot.m02 = (float) (AD * E + B * F);
        rot.m10 = (float) (C * F);
        rot.m11 = (float) (BD * F + A * E);
        rot.m12 = (float) (AD * F + -(B * E));
        rot.m20 = (float) -D;
        rot.m21 = (float) (B * C);
        rot.m22 = (float) (A * C);
        translation.set(0,0,0);
    }

    /**
     * <code>setRotationQuaternion</code> builds a rotation from a
     * <code>Quaternion</code>.  The translational component of the
     * transform is set to the identity.
     * @param quat The quaternion to build the rotation from.
     * @throws JmeException if quat is null.
     */
    public void setRotationQuaternion(Quaternion quat) {
        if (null == quat) {
            throw new JmeException("Quat may not be null.");
        }
        rot.set(quat);
        translation.set(0,0,0);
    }

    /**
     * <code>invertRotInPlace</code> inverts the rotational component of this Matrix
     * in place
     */
    private void invertRotInPlace() {
        float temp;
        temp=rot.m01;
        rot.m01=rot.m10;
        rot.m10=temp;
        temp=rot.m02;
        rot.m02=rot.m20;
        rot.m20=temp;
        temp=rot.m21;
        rot.m21=rot.m12;
        rot.m12=temp;

    }


    /**
     * Stores the rotational part of this matrix into the passed matrix.
     * Will create a new Matrix3f if given matrix is null.  Returns the
     * given matrix after it has been loaded with rotation values, to allow
     * chaining
     *
     * @param rotStore The matrix to store rotation values
     * @return Matrix3f The given matrix with updated values
     */
    public Matrix3f getRotation(Matrix3f rotStore){
        if (rotStore==null) rotStore=new Matrix3f();
        rotStore.copy(rot);
        return rotStore;
    }

    /**
     * <code>toString</code> returns the string representation of this object.
     * It is simply a toString() call of the rotational matrix and the translational vector
     * @return the string representation of this object.
     */
    public String toString() {
        return "com.jme.math.TransformMatrix\n[\n"+
                rot.toString() + ":" +
                translation.toString();
    }

    /**
     * <code>inverse</code> turns this matrix into it's own inverse
     */
    public void inverse() {
        invertRotInPlace();
        rot.multLocal(translation);
        translation.multLocal(-1);
    }

    /**
     * <code>setEulerRot</code> is equivalent to
     * setEulerRot(eulerVec.x,eulverVec.y,eulverVec.z){
     * @param eulerVec A Vector3f representing the new rotation in Euler angles
     */
    public void setEulerRot(Vector3f eulerVec) {
        this.setEulerRot(eulerVec.x,eulerVec.y,eulerVec.z);
    }

    /**
     * <code>set</code> changes this matrix's rotational and translational components
     * to that represented by the given parameters
     * @param rotation The new rotaiton
     * @param translation The new translation
     */
    public void set(Quaternion rotation, Vector3f translation) {
        this.set(rotation);
        this.setTranslation(translation);
    }
}