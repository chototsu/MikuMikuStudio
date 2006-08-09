/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.math;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.logging.Level;

import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>Quaternion</code> defines a single example of a more general class of
 * hypercomplex numbers. Quaternions extends a rotation in three dimensions to a
 * rotation in four dimensions. This avoids "gimbal lock" and allows for smooth
 * continuous rotation.
 * 
 * <code>Quaternion</code> is defined by four floating point numbers: {x y z
 * w}.
 * 
 * @author Mark Powell
 * @author Joshua Slack - Optimizations
 * @version $Id: Quaternion.java,v 1.54 2006-08-09 18:00:15 rherlitz Exp $
 */
public class Quaternion implements Externalizable, Savable {
    private static final long serialVersionUID = 1L;

    public static final Quaternion IDENTITY = new Quaternion();

    public float x, y, z, w;

    /**
     * Constructor instantiates a new <code>Quaternion</code> object
     * initializing all values to zero, except w which is initialized to 1.
     *
     */
    public Quaternion() {
        x = 0;
        y = 0;
        z = 0;
        w = 1;
    }

    /**
     * Constructor instantiates a new <code>Quaternion</code> object from the
     * given list of parameters.
     *
     * @param x
     *            the x value of the quaternion.
     * @param y
     *            the y value of the quaternion.
     * @param z
     *            the z value of the quaternion.
     * @param w
     *            the w value of the quaternion.
     */
    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * sets the data in a <code>Quaternion</code> object from the given list
     * of parameters.
     *
     * @param x
     *            the x value of the quaternion.
     * @param y
     *            the y value of the quaternion.
     * @param z
     *            the z value of the quaternion.
     * @param w
     *            the w value of the quaternion.
     */
    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Sets the data in this <code>Quaternion</code> object to be equal to the
     * passed <code>Quaternion</code> object. The values are copied producing
     * a new object.
     *
     * @param q
     *            The Quaternion to copy values from.
     * @return this for chaining
     */
    public Quaternion set(Quaternion q) {
        this.x = q.x;
        this.y = q.y;
        this.z = q.z;
        this.w = q.w;
        return this;
    }

    /**
     * Constructor instantiates a new <code>Quaternion</code> object from a
     * collection of rotation angles.
     *
     * @param angles
     *            the angles of rotation (x, y, z) that will define the
     *            <code>Quaternion</code>.
     */
    public Quaternion(float[] angles) {
        fromAngles(angles);
    }

    /**
     * Constructor instantiates a new <code>Quaternion</code> object from an
     * interpolation between two other quaternions.
     *
     * @param q1
     *            the first quaternion.
     * @param q2
     *            the second quaternion.
     * @param interp
     *            the amount to interpolate between the two quaternions.
     */
    public Quaternion(Quaternion q1, Quaternion q2, float interp) {
        slerp(q1, q2, interp);
    }

    /**
     * Constructor instantiates a new <code>Quaternion</code> object from an
     * existing quaternion, creating a copy.
     *
     * @param q
     *            the quaternion to copy.
     */
    public Quaternion(Quaternion q) {
        this.x = q.x;
        this.y = q.y;
        this.z = q.z;
        this.w = q.w;
    }

    /**
     * <code>fromAngles</code> builds a quaternion from the Euler rotation
     * angles (x,y,z).
     *
     * @param angles
     *            the Euler angles of rotation (in radians).
     */
    public void fromAngles(float[] angles) {
        if (angles.length != 3)
            throw new IllegalArgumentException(
                    "Angles array must have three elements");

        fromAngles(angles[0], angles[1], angles[2]);
    }

    /**
     * <code>fromAngles</code> builds a quaternion from the Euler rotation
     * angles (x,y,z).
     *
     * @param xAngle
     *            the Euler xangle of rotation (in radians).
     * @param yAngle
     *            the Euler yangle of rotation (in radians).
     * @param zAngle
     *            the Euler zangle of rotation (in radians).
     */
    public void fromAngles(float xAngle, float yAngle, float zAngle) {
        float angle;
        float sr, sp, sy, cr, cp, cy;
        angle = zAngle * 0.5f;
        sy = FastMath.sin(angle);
        cy = FastMath.cos(angle);
        angle = yAngle * 0.5f;
        sp = FastMath.sin(angle);
        cp = FastMath.cos(angle);
        angle = xAngle * 0.5f;
        sr = FastMath.sin(angle);
        cr = FastMath.cos(angle);

        float crcp = cr * cp;
        float srsp = sr * sp;

        x = (sr * cp * cy - cr * sp * sy);
        y = (cr * sp * cy + sr * cp * sy);
        z = (crcp * sy - srsp * cy);
        w = (crcp * cy + srsp * sy);
    }
    
    /**
	 * <code>toAngles</code> returns this quaternion converted to Euler
	 * rotation angels (x,y,z).<br/>
	 * Contributed by vear.
	 * 
	 * @param angles
	 *            the float[] in which the angles should be stored, or null if
	 *            you want a new float[] to be created
	 * @return the float[] in which the angles are stored.
	 */
	public float[] toAngles(float[] angles) {
		if (angles == null)
			angles = new float[3];
		else if (angles.length != 3)
			throw new IllegalArgumentException("Angles array must have three elements");

		float sqw = w * w;
		float sqx = x * x;
		float sqy = y * y;
		float sqz = z * z;
		float unit = sqx + sqy + sqz + sqw; // if normalised is one, otherwise
											// is correction factor
		float test = x * y + z * w;
		if (test > 0.499 * unit) { // singularity at north pole
			angles[1] = 2 * FastMath.atan2(x, w);
			angles[2] = FastMath.HALF_PI;
			angles[0] = 0;
		} else if (test < -0.499 * unit) { // singularity at south pole
			angles[1] = -2 * FastMath.atan2(x, w);
			angles[2] = -FastMath.HALF_PI;
			angles[0] = 0;
		} else {
			angles[1] = FastMath.atan2(2 * y * w - 2 * x * z, sqx - sqy - sqz + sqw);
			angles[2] = FastMath.asin(2 * test / unit);
			angles[0] = FastMath.atan2(2 * x * w - 2 * y * z, -sqx + sqy - sqz + sqw);
		}
		return angles;
	}

    /**
	 * 
	 * <code>fromRotationMatrix</code> generates a quaternion from a supplied
	 * matrix. This matrix is assumed to be a rotational matrix.
	 * 
	 * @param matrix
	 *            the matrix that defines the rotation.
	 */
    public void fromRotationMatrix(Matrix3f matrix) {
        float t = matrix.m00 + matrix.m11 + matrix.m22 + 1;

        if (t > FastMath.FLT_EPSILON * 3) {
            float s = 0.5f / FastMath.sqrt(t);
            w = 0.25f / s;
            x = (matrix.m21 - matrix.m12) * s;
            y = (matrix.m02 - matrix.m20) * s;
            z = (matrix.m10 - matrix.m01) * s;
        } else if ((matrix.m00 > matrix.m11) && (matrix.m00 > matrix.m22)) {
            float s = FastMath
                    .sqrt(1.0f + matrix.m00 - matrix.m11 - matrix.m22) * 2;
            x = 0.25f * s;
            y = (matrix.m01 + matrix.m10) / s;
            z = (matrix.m02 + matrix.m20) / s;
            w = (matrix.m12 - matrix.m21) / s;
        } else if (matrix.m11 > matrix.m22) {
            float s = FastMath
                    .sqrt(1.0f + matrix.m11 - matrix.m00 - matrix.m22) * 2;
            x = (matrix.m01 + matrix.m10) / s;
            y = 0.25f * s;
            z = (matrix.m12 + matrix.m21) / s;
            w = (matrix.m02 - matrix.m20) / s;
        } else {
            float s = FastMath
                    .sqrt(1.0f + matrix.m22 - matrix.m00 - matrix.m11) * 2;
            x = (matrix.m02 + matrix.m20) / s;
            y = (matrix.m12 + matrix.m21) / s;
            z = 0.25f * s;
            w = (matrix.m01 - matrix.m10) / s;
        }

    }

    /**
     *
     * <code>toRotationMatrix</code> converts this quaternion to a rotational
     * matrix.
     *
     * @return the rotation matrix representation of this quaternion.
     */
    public Matrix3f toRotationMatrix() {
        Matrix3f matrix = new Matrix3f();
        return toRotationMatrix(matrix);
    }

    /**
     *
     * <code>toRotationMatrix</code> converts this quaternion to a rotational
     * matrix. The result is stored in result.
     *
     * @param result
     *            The Matrix3f to store the result in.
     * @return the rotation matrix representation of this quaternion.
     */
    public Matrix3f toRotationMatrix(Matrix3f result) {
        float fTx = 2.0f * x;
        float fTy = 2.0f * y;
        float fTz = 2.0f * z;
        float fTwx = fTx * w;
        float fTwy = fTy * w;
        float fTwz = fTz * w;
        float fTxx = fTx * x;
        float fTxy = fTy * x;
        float fTxz = fTz * x;
        float fTyy = fTy * y;
        float fTyz = fTz * y;
        float fTzz = fTz * z;

        result.m00 = 1.0f - (fTyy + fTzz);
        result.m01 = fTxy - fTwz;
        result.m02 = fTxz + fTwy;
        result.m10 = fTxy + fTwz;
        result.m11 = 1.0f - (fTxx + fTzz);
        result.m12 = fTyz - fTwx;
        result.m20 = fTxz - fTwy;
        result.m21 = fTyz + fTwx;
        result.m22 = 1.0f - (fTxx + fTyy);

        return result;
    }

    /**
     *
     * <code>toRotationMatrix</code> converts this quaternion to a rotational
     * matrix. The result is stored in result. The outer col, row is 0, with 3,3 =
     * 1
     *
     * @param result
     *            The Matrix4f to store the result in.
     * @return the rotation matrix representation of this quaternion.
     */
    public Matrix4f toRotationMatrix(Matrix4f result) {
        float fTx = 2.0f * x;
        float fTy = 2.0f * y;
        float fTz = 2.0f * z;
        float fTwx = fTx * w;
        float fTwy = fTy * w;
        float fTwz = fTz * w;
        float fTxx = fTx * x;
        float fTxy = fTy * x;
        float fTxz = fTz * x;
        float fTyy = fTy * y;
        float fTyz = fTz * y;
        float fTzz = fTz * z;

        result.zero();
        result.m33 = 1;

        result.m00 = 1.0f - (fTyy + fTzz);
        result.m01 = fTxy - fTwz;
        result.m02 = fTxz + fTwy;
        result.m10 = fTxy + fTwz;
        result.m11 = 1.0f - (fTxx + fTzz);
        result.m12 = fTyz - fTwx;
        result.m20 = fTxz - fTwy;
        result.m21 = fTyz + fTwx;
        result.m22 = 1.0f - (fTxx + fTyy);

        return result;
    }

    /**
     * <code>getRotationColumn</code> returns one of three columns specified
     * by the parameter. This column is returned as a <code>Vector3f</code>
     * object.
     *
     * @param i
     *            the column to retrieve. Must be between 0 and 2.
     * @return the column specified by the index.
     */
    public Vector3f getRotationColumn(int i) {
        return getRotationColumn(i, null);
    }

    /**
     * <code>getRotationColumn</code> returns one of three columns specified
     * by the parameter. This column is returned as a <code>Vector3f</code>
     * object.
     *
     * @param i
     *            the column to retrieve. Must be between 0 and 2.
     * @param store
     *            the vector object to store the result in. if null, a new one
     *            is created.
     * @return the column specified by the index.
     */
    public Vector3f getRotationColumn(int i, Vector3f store) {
        if (store == null)
            store = new Vector3f();
        float fTx = 2.0f * x;
        float fTy = 2.0f * y;
        float fTz = 2.0f * z;
        float fTwx = fTx * w;
        float fTwy = fTy * w;
        float fTwz = fTz * w;
        float fTxx = fTx * x;
        float fTxy = fTy * x;
        float fTxz = fTz * x;
        float fTyy = fTy * y;
        float fTyz = fTz * y;
        float fTzz = fTz * z;

        switch (i) {
        case 0:
            store.x = 1.0f - (fTyy + fTzz);
            store.y = fTxy + fTwz;
            store.z = fTxz - fTwy;
            break;
        case 1:
            store.x = fTxy - fTwz;
            store.y = 1.0f - (fTxx + fTzz);
            store.z = fTyz + fTwx;
            break;
        case 2:
            store.x = fTxz + fTwy;
            store.y = fTyz - fTwx;
            store.z = 1.0f - (fTxx + fTyy);
            break;
        default:
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Invalid column index.");
            throw new JmeException("Invalid column index. " + i);
        }
        return store;
    }

    /**
     * <code>fromAngleAxis</code> sets this quaternion to the values specified
     * by an angle and an axis of rotation. This method creates an object, so
     * use fromAngleNormalAxis if your axis is already normalized.
     *
     * @param angle
     *            the angle to rotate (in radians).
     * @param axis
     *            the axis of rotation.
     * @return this quaternion
     */
    public Quaternion fromAngleAxis(float angle, Vector3f axis) {
        Vector3f normAxis = axis.normalize();
        fromAngleNormalAxis(angle, normAxis);
        return this;
    }

    /**
     * <code>fromAngleNormalAxis</code> sets this quaternion to the values
     * specified by an angle and a normalized axis of rotation.
     *
     * @param angle
     *            the angle to rotate (in radians).
     * @param axis
     *            the axis of rotation (already normalized).
     */
    public Quaternion fromAngleNormalAxis(float angle, Vector3f axis) {
        float halfAngle = 0.5f * angle;
        float sin = FastMath.sin(halfAngle);
        w = FastMath.cos(halfAngle);
        x = sin * axis.x;
        y = sin * axis.y;
        z = sin * axis.z;
        return this;
    }

    /**
     * <code>toAngleAxis</code> sets a given angle and axis to that
     * represented by the current quaternion. The values are stored as
     * following: The axis is provided as a parameter and built by the method,
     * the angle is returned as a float.
     *
     * @param axis
     *            the object to contain the axis.
     * @return the angle of rotation in radians.
     */
    public float toAngleAxis(Vector3f axis) {
        float sqrLength = x * x + y * y + z * z;
        float angle;
        if (sqrLength == 0.0f) {
            angle = 0.0f;
            axis.x = 1.0f;
            axis.y = 0.0f;
            axis.z = 0.0f;
        } else {
            angle = (2.0f * FastMath.acos(w));
            float invLength = (1.0f / FastMath.sqrt(sqrLength));
            axis.x = x * invLength;
            axis.y = y * invLength;
            axis.z = z * invLength;
        }

        return angle;
    }

    /**
     * <code>slerp</code> sets this quaternion's value as an interpolation
     * between two other quaternions.
     *
     * @param q1
     *            the first quaternion.
     * @param q2
     *            the second quaternion.
     * @param t
     *            the amount to interpolate between the two quaternions.
     */
    public Quaternion slerp(Quaternion q1, Quaternion q2, float t) {
        // Create a local quaternion to store the interpolated quaternion
        if (q1.x == q2.x && q1.y == q2.y && q1.z == q2.z && q1.w == q2.w) {
            this.set(q1);
            return this;
        }

        float result = (q1.x * q2.x) + (q1.y * q2.y) + (q1.z * q2.z)
                + (q1.w * q2.w);

        if (result < 0.0f) {
            // Negate the second quaternion and the result of the dot product
            q2.x = -q2.x;
            q2.y = -q2.y;
            q2.z = -q2.z;
            q2.w = -q2.w;
            result = -result;
        }

        // Set the first and second scale for the interpolation
        float scale0 = 1 - t;
        float scale1 = t;

        // Check if the angle between the 2 quaternions was big enough to
        // warrant such calculations
        if ((1 - result) > 0.1f) {// Get the angle between the 2 quaternions,
            // and then store the sin() of that angle
            float theta = FastMath.acos(result);
            float invSinTheta = 1f / FastMath.sin(theta);

            // Calculate the scale for q1 and q2, according to the angle and
            // it's sine value
            scale0 = FastMath.sin((1 - t) * theta) * invSinTheta;
            scale1 = FastMath.sin((t * theta)) * invSinTheta;
        }

        // Calculate the x, y, z and w values for the quaternion by using a
        // special
        // form of linear interpolation for quaternions.
        this.x = (scale0 * q1.x) + (scale1 * q2.x);
        this.y = (scale0 * q1.y) + (scale1 * q2.y);
        this.z = (scale0 * q1.z) + (scale1 * q2.z);
        this.w = (scale0 * q1.w) + (scale1 * q2.w);

        // Return the interpolated quaternion
        return this;
    }

    /**
     * Sets the values of this quaternion to the slerp from itself to q2 by
     * changeAmnt
     *
     * @param q2
     *            Final interpolation value
     * @param changeAmnt
     *            The amount diffrence
     */
    public void slerp(Quaternion q2, float changeAmnt) {
        if (this.x == q2.x && this.y == q2.y && this.z == q2.z
                && this.w == q2.w) {
            return;
        }

        float result = (this.x * q2.x) + (this.y * q2.y) + (this.z * q2.z)
                + (this.w * q2.w);

        if (result < 0.0f) {
            // Negate the second quaternion and the result of the dot product
            q2.x = -q2.x;
            q2.y = -q2.y;
            q2.z = -q2.z;
            q2.w = -q2.w;
            result = -result;
        }

        // Set the first and second scale for the interpolation
        float scale0 = 1 - changeAmnt;
        float scale1 = changeAmnt;

        // Check if the angle between the 2 quaternions was big enough to
        // warrant such calculations
        if ((1 - result) > 0.1f) {
            // Get the angle between the 2 quaternions, and then store the sin()
            // of that angle
            float theta = FastMath.acos(result);
            float invSinTheta = 1f / FastMath.sin(theta);

            // Calculate the scale for q1 and q2, according to the angle and
            // it's sine value
            scale0 = FastMath.sin((1 - changeAmnt) * theta) * invSinTheta;
            scale1 = FastMath.sin((changeAmnt * theta)) * invSinTheta;
        }

        // Calculate the x, y, z and w values for the quaternion by using a
        // special
        // form of linear interpolation for quaternions.
        this.x = (scale0 * this.x) + (scale1 * q2.x);
        this.y = (scale0 * this.y) + (scale1 * q2.y);
        this.z = (scale0 * this.z) + (scale1 * q2.z);
        this.w = (scale0 * this.w) + (scale1 * q2.w);
    }

    /**
     * <code>add</code> adds the values of this quaternion to those of the
     * parameter quaternion. The result is returned as a new quaternion.
     *
     * @param q
     *            the quaternion to add to this.
     * @return the new quaternion.
     */
    public Quaternion add(Quaternion q) {
        return new Quaternion(x + q.x, y + q.y, z + q.z, w + q.w);
    }

    /**
     * <code>add</code> adds the values of this quaternion to those of the
     * parameter quaternion. The result is stored in this Quaternion.
     *
     * @param q
     *            the quaternion to add to this.
     * @return This Quaternion after addition.
     */
    public Quaternion addLocal(Quaternion q) {
        this.x += q.x;
        this.y += q.y;
        this.z += q.z;
        this.w += q.w;
        return this;
    }

    /**
     * <code>subtract</code> subtracts the values of the parameter quaternion
     * from those of this quaternion. The result is returned as a new
     * quaternion.
     *
     * @param q
     *            the quaternion to subtract from this.
     * @return the new quaternion.
     */
    public Quaternion subtract(Quaternion q) {
        return new Quaternion(x - q.x, y - q.y, z - q.z, w - q.w);
    }

	/**
	 * <code>subtract</code> subtracts the values of the parameter quaternion
	 * from those of this quaternion. The result is stored in this Quaternion.
	 *
	 * @param q
	 *            the quaternion to subtract from this.
	 * @return This Quaternion after subtraction.
	 */
	public Quaternion subtractLocal(Quaternion q) {
		this.x -= q.x;
		this.y -= q.y;
		this.z -= q.z;
		this.w -= q.w;
		return this;
	}

	/**
     * <code>mult</code> multiplies this quaternion by a parameter quaternion.
     * The result is returned as a new quaternion. It should be noted that
     * quaternion multiplication is not cummulative so q * p != p * q.
     *
     * @param q
     *            the quaternion to multiply this quaternion by.
     * @return the new quaternion.
     */
    public Quaternion mult(Quaternion q) {
        return mult(q, null);
    }

    /**
     * <code>mult</code> multiplies this quaternion by a parameter quaternion.
     * The result is returned as a new quaternion. It should be noted that
     * quaternion multiplication is not cummulative so q * p != p * q.
     *
     * It IS safe for q and res to be the same object.
     *
     * @param q
     *            the quaternion to multiply this quaternion by.
     * @param res
     *            the quaternion to store the result in.
     * @return the new quaternion.
     */
    public Quaternion mult(Quaternion q, Quaternion res) {
        if (res == null)
            res = new Quaternion();
        float qw = q.w, qx = q.x, qy = q.y, qz = q.z;
        res.x = x * qw + y * qz - z * qy + w * qx;
        res.y = -x * qz + y * qw + z * qx + w * qy;
        res.z = x * qy - y * qx + z * qw + w * qz;
        res.w = -x * qx - y * qy - z * qz + w * qw;
        return res;
    }

    /**
     * <code>apply</code> multiplies this quaternion by a parameter matrix
     * internally.
     *
     * @param matrix
     *            the matrix to apply to this quaternion.
     */
    public void apply(Matrix3f matrix) {
        float oldX = x, oldY = y, oldZ = z, oldW = w;
        fromRotationMatrix(matrix);
        float tempX = x, tempY = y, tempZ = z, tempW = w;

        x = oldX * tempW + oldY * tempZ - oldZ * tempY + oldW * tempX;
        y = -oldX * tempZ + oldY * tempW + oldZ * tempX + oldW * tempY;
        z = oldX * tempY - oldY * tempX + oldZ * tempW + oldW * tempZ;
        w = -oldX * tempX - oldY * tempY - oldZ * tempZ + oldW * tempW;
    }

    /**
     *
     * <code>fromAxes</code> creates a <code>Quaternion</code> that
     * represents the coordinate system defined by three axes. These axes are
     * assumed to be orthogonal and no error checking is applied. Thus, the user
     * must insure that the three axes being provided indeed represents a proper
     * right handed coordinate system.
     *
     * @param axis
     *            the array containing the three vectors representing the
     *            coordinate system.
     */
    public void fromAxes(Vector3f[] axis) {
        if (axis.length != 3)
            throw new IllegalArgumentException(
                    "Axis array must have three elements");
        fromAxes(axis[0], axis[1], axis[2]);
    }

    /**
     *
     * <code>fromAxes</code> creates a <code>Quaternion</code> that
     * represents the coordinate system defined by three axes. These axes are
     * assumed to be orthogonal and no error checking is applied. Thus, the user
     * must insure that the three axes being provided indeed represents a proper
     * right handed coordinate system.
     *
     * @param xAxis vector representing the x-axis of the coordinate system.
     * @param yAxis vector representing the y-axis of the coordinate system.
     * @param zAxis vector representing the z-axis of the coordinate system.
     */
    public void fromAxes(Vector3f xAxis, Vector3f yAxis, Vector3f zAxis) {
        float t = xAxis.x + yAxis.y + zAxis.z + 1;

        if (t > FastMath.FLT_EPSILON * 3) {
            float s = 0.5f / FastMath.sqrt(t);
            w = 0.25f / s;
            x = (yAxis.z - zAxis.y) * s;
            y = (zAxis.x - xAxis.z) * s;
            z = (xAxis.y - yAxis.x) * s;
        } else if ((xAxis.x > yAxis.y) && (xAxis.x > zAxis.z)) {
            float s = FastMath
                    .sqrt(1.0f + xAxis.x - yAxis.y - zAxis.z) * 2;
            x = 0.25f * s;
            y = (yAxis.x + xAxis.y) / s;
            z = (zAxis.x + xAxis.z) / s;
            w = (zAxis.y - yAxis.z) / s;
        } else if (yAxis.y > zAxis.z) {
            float s = FastMath
                    .sqrt(1.0f + yAxis.y - xAxis.x - zAxis.z) * 2;
            x = (yAxis.x + xAxis.y) / s;
            y = 0.25f * s;
            z = (zAxis.y + yAxis.z) / s;
            w = (zAxis.x - xAxis.z) / s;
        } else {
            float s = FastMath
                    .sqrt(1.0f + zAxis.z - xAxis.x - yAxis.y) * 2;
            x = (zAxis.x + xAxis.z) / s;
            y = (zAxis.y + yAxis.z) / s;
            z = 0.25f * s;
            w = (yAxis.x - xAxis.y) / s;
        }
    }

    /**
     *
     * <code>toAxes</code> takes in an array of three vectors. Each vector
     * corresponds to an axis of the coordinate system defined by the quaternion
     * rotation.
     *
     * @param axis
     *            the array of vectors to be filled.
     */
    public void toAxes(Vector3f axis[]) {
        Matrix3f tempMat = toRotationMatrix();
        axis[0] = tempMat.getColumn(0, axis[0]);
        axis[1] = tempMat.getColumn(1, axis[1]);
        axis[2] = tempMat.getColumn(2, axis[2]);
    }

    /**
     * <code>mult</code> multiplies this quaternion by a parameter vector. The
     * result is returned as a new vector.
     *
     * @param v
     *            the vector to multiply this quaternion by.
     * @return the new vector.
     */
    public Vector3f mult(Vector3f v) {
        return mult(v, null);
    }

    /**
     * <code>mult</code> multiplies this quaternion by a parameter vector. The
     * result is stored in the supplied vector
     *
     * @param v
     *            the vector to multiply this quaternion by.
     * @return v
     */
    public Vector3f multLocal(Vector3f v) {
        float tempX, tempY;
        tempX = w * w * v.x + 2 * y * w * v.z - 2 * z * w * v.y + x * x * v.x
                + 2 * y * x * v.y + 2 * z * x * v.z - z * z * v.x - y * y * v.x;
        tempY = 2 * x * y * v.x + y * y * v.y + 2 * z * y * v.z + 2 * w * z
                * v.x - z * z * v.y + w * w * v.y - 2 * x * w * v.z - x * x
                * v.y;
        v.z = 2 * x * z * v.x + 2 * y * z * v.y + z * z * v.z - 2 * w * y * v.x
                - y * y * v.z + 2 * w * x * v.y - x * x * v.z + w * w * v.z;
        v.x = tempX;
        v.y = tempY;
        return v;
    }

    /**
     * Multiplies this Quaternion by the supplied quaternion. The result is
     * stored in this Quaternion, which is also returned for chaining. Similar
     * to this *= q.
     *
     * @param q
     *            The Quaternion to multiply this one by.
     * @return This Quaternion, after multiplication.
     */
    public Quaternion multLocal(Quaternion q) {
        float x1 = x * q.w + y * q.z - z * q.y + w * q.x;
        float y1 = -x * q.z + y * q.w + z * q.x + w * q.y;
        float z1 = x * q.y - y * q.x + z * q.w + w * q.z;
        w = -x * q.x - y * q.y - z * q.z + w * q.w;
        x = x1;
        y = y1;
        z = z1;
        return this;
    }

    /**
     * Multiplies this Quaternion by the supplied quaternion. The result is
     * stored in this Quaternion, which is also returned for chaining. Similar
     * to this *= q.
     *
     * @param qx -
     *            quat x value
     * @param qy -
     *            quat y value
     * @param qz -
     *            quat z value
     * @param qw -
     *            quat w value
     *
     * @return This Quaternion, after multiplication.
     */
    public Quaternion multLocal(float qx, float qy, float qz, float qw) {
        float x1 = x * qw + y * qz - z * qy + w * qx;
        float y1 = -x * qz + y * qw + z * qx + w * qy;
        float z1 = x * qy - y * qx + z * qw + w * qz;
        w = -x * qx - y * qy - z * qz + w * qw;
        x = x1;
        y = y1;
        z = z1;
        return this;
    }

    /**
     * <code>mult</code> multiplies this quaternion by a parameter vector. The
     * result is returned as a new vector.
     * 
     * @param v
     *            the vector to multiply this quaternion by.
     * @param store
     *            the vector to store the result in. It IS safe for v and store
     *            to be the same object.
     * @return the result vector.
     */
    public Vector3f mult(Vector3f v, Vector3f store) {
        if (store == null)
            store = new Vector3f();
        if (v.x == 0 && v.y == 0 && v.z == 0) {
            store.set(0, 0, 0);
        } else {
            float vx = v.x, vy = v.y, vz = v.z;
            store.x = w * w * vx + 2 * y * w * vz - 2 * z * w * vy + x * x
                    * vx + 2 * y * x * vy + 2 * z * x * vz - z * z * vx - y
                    * y * vx;
            store.y = 2 * x * y * vx + y * y * vy + 2 * z * y * vz + 2 * w
                    * z * vx - z * z * vy + w * w * vy - 2 * x * w * vz - x
                    * x * vy;
            store.z = 2 * x * z * vx + 2 * y * z * vy + z * z * vz - 2 * w
                    * y * vx - y * y * vz + 2 * w * x * vy - x * x * vz + w
                    * w * vz;
        }
        return store;
    }

    /**
     * <code>mult</code> multiplies this quaternion by a parameter scalar. The
     * result is returned as a new quaternion.
     *
     * @param scalar
     *            the quaternion to multiply this quaternion by.
     * @return the new quaternion.
     */
    public Quaternion mult(float scalar) {
        return new Quaternion(scalar * w, scalar * x, scalar * y, scalar * z);
    }

    /**
     * <code>mult</code> multiplies this quaternion by a parameter scalar. The
     * result is stored locally.
     *
     * @param scalar
     *            the quaternion to multiply this quaternion by.
     * @return this.
     */
    public Quaternion multLocal(float scalar) {
        w *= scalar;
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }

    /**
     * <code>dot</code> calculates and returns the dot product of this
     * quaternion with that of the parameter quaternion.
     *
     * @param q
     *            the quaternion to calculate the dot product of.
     * @return the dot product of this and the parameter quaternion.
     */
    public float dot(Quaternion q) {
        return w * q.w + x * q.x + y * q.y + z * q.z;
    }

    /**
     * <code>norm</code> returns the norm of this quaternion. This is the dot
     * product of this quaternion with itself.
     *
     * @return the norm of the quaternion.
     */
    public float norm() {
        return w * w + x * x + y * y + z * z;
    }

    /**
     * <code>normalize</code> normalizes the current <code>Quaternion</code>
     */
    public void normalize() {
        double n = FastMath.sqrt(norm());
        x /= n;
        y /= n;
        z /= n;
        w /= n;
    }

    /**
     * <code>inverse</code> returns the inverse of this quaternion as a new
     * quaternion. If this quaternion does not have an inverse (if its normal is
     * 0 or less), then null is returned.
     *
     * @return the inverse of this quaternion or null if the inverse does not
     *         exist.
     */
    public Quaternion inverse() {
        float norm = norm();
        if (norm > 0.0) {
            float invNorm = 1.0f / norm;
            return new Quaternion(-x * invNorm, -y * invNorm, -z * invNorm, w
                    * invNorm);
        } 
        // return an invalid result to flag the error
        return null;        
    }

    /**
     * <code>inverse</code> calculates the inverse of this quaternion and
     * returns this quaternion after it is calculated. If this quaternion does
     * not have an inverse (if it's norma is 0 or less), nothing happens
     *
     * @return the inverse of this quaternion
     */
    public Quaternion inverseLocal() {
        float norm = norm();
        if (norm > 0.0) {
            float invNorm = 1.0f / norm;
            x *= -invNorm;
            y *= -invNorm;
            z *= -invNorm;
            w *= invNorm;
        }
        return this;
    }

    /**
     * <code>negate</code> inverts the values of the quaternion.
     *
     */
    public void negate() {
        x *= -1;
        y *= -1;
        z *= -1;
        w *= -1;
    }

    /**
     *
     * <code>toString</code> creates the string representation of this
     * <code>Quaternion</code>. The values of the quaternion are displace (x,
     * y, z, w), in the following manner: <br>
     * com.jme.math.Quaternion: [x=1" y=2 z=3 w=1]
     *
     * @return the string representation of this object.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "com.jme.math.Quaternion: [x=" + x + " y=" + y + " z=" + z
                + " w=" + w + "]";
    }

    /**
     * <code>equals</code> determines if two quaternions are logically equal,
     * that is, if the values of (x, y, z, w) are the same for both quaternions.
     *
     * @param o
     *            the object to compare for equality
     * @return true if they are equal, false otherwise.
     */
    public boolean equals(Object o) {
        if (!(o instanceof Quaternion) ) {
            return false;
        }

        if (this == o) {
            return true;
        }

        Quaternion comp = (Quaternion) o;
        if (Float.floatToIntBits(x) != Float.floatToIntBits(comp.x))
            return false;
        if (Float.floatToIntBits(y) != Float.floatToIntBits(comp.y))
            return false;
        if (Float.floatToIntBits(z) != Float.floatToIntBits(comp.z))
            return false;
        if (Float.floatToIntBits(w) != Float.floatToIntBits(comp.w))
            return false;
        return true;
    }

    /**
     * 
     * <code>hashCode</code> returns the hash code value as an integer and is
     * supported for the benefit of hashing based collection classes such as
     * Hashtable, HashMap, HashSet etc.
     * 
     * @return the hashcode for this instance of Quaternion.
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hash = 37;
        hash = 37 * hash + Float.floatToIntBits(x);
        hash = 37 * hash + Float.floatToIntBits(y);
        hash = 37 * hash + Float.floatToIntBits(z);
        hash = 37 * hash + Float.floatToIntBits(w);
        return hash;

    }

    /**
     * <code>readExternal</code> builds a quaternion from an
     * <code>ObjectInput</code> object. <br>
     * NOTE: Used with serialization. Not to be called manually.
     * 
     * @param in
     *            the ObjectInput value to read from.
     * @throws IOException
     *             if the ObjectInput value has problems reading a float.
     * @see java.io.Externalizable
     */
    public void readExternal(ObjectInput in) throws IOException {
        x = in.readFloat();
        y = in.readFloat();
        z = in.readFloat();
        w = in.readFloat();
    }

    /**
     * <code>writeExternal</code> writes this quaternion out to a
     * <code>ObjectOutput</code> object. NOTE: Used with serialization. Not to
     * be called manually.
     * 
     * @param out
     *            the object to write to.
     * @throws IOException
     *             if writing to the ObjectOutput fails.
     * @see java.io.Externalizable
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(x);
        out.writeFloat(y);
        out.writeFloat(z);
        out.writeFloat(w);
    }

    private static final Vector3f tmpYaxis = new Vector3f();
    private static final Vector3f tmpZaxis = new Vector3f();
    private static final Vector3f tmpXaxis = new Vector3f();

    /**
     * <code>lookAt</code> is a convienence method for auto-setting the
     * quaternion based on a direction and an up vector. It computes
     * the rotation to transform the z-axis to point into 'direction'
     * and the y-axis to 'up'.
     *
     * @param direction
     *            where to look at in terms of local coordinates
     * @param up
     *            a vector indicating the local up direction.
     *            (typically {0, 1, 0} in jME.)
     */
    public void lookAt(Vector3f direction, Vector3f up ) {
        tmpZaxis.set( direction ).normalizeLocal();
        tmpXaxis.set( up ).crossLocal( direction ).normalizeLocal();
        tmpYaxis.set( direction ).crossLocal( tmpXaxis ).normalizeLocal();
        fromAxes( tmpXaxis, tmpYaxis, tmpZaxis );
    }

    public void write(JMEExporter e) throws IOException {
        OutputCapsule cap = e.getCapsule(this);
        cap.write(x, "x", 0);
        cap.write(y, "y", 0);
        cap.write(z, "z", 0);
        cap.write(w, "w", 1);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule cap = e.getCapsule(this);
        x = cap.readFloat("x", 0);
        y = cap.readFloat("y", 0);
        z = cap.readFloat("z", 0);
        w = cap.readFloat("w", 1);
    }
    
    public Class getClassTag() {
        return this.getClass();
    }
}