/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.jme.math;

import com.jme.util.LoggingSystem;
import com.jme.system.JmeException;
import java.util.logging.Level;

/**
 * <code>Quaternion</code> defines a single example of a more general class of
 * hypercomplex numbers. Quaternions extends a rotation in three dimensions to
 * a rotation in four dimensions. This avoids "gimbal lock" and allows for
 * smooth continuous rotation.
 *
 * <code>Quaternion</code> is defined by four floating point numbers:
 * {x y z w}.
 *
 * @author Mark Powell
 * @author Joshua Slack - Optimizations
 * @version $Id: Quaternion.java,v 1.6 2004-03-02 19:55:15 renanse Exp $
 */
public class Quaternion {
    public float x, y, z, w;
    private Vector3f uv, uuv; // work horses

    /**
     * Constructor instantiates a new <code>Quaternion</code> object
     * initializing all values to zero.
     *
     */
    public Quaternion() {
        x = 0;
        y = 0;
        z = 0;
        w = 1;
        uv = new Vector3f();
        uuv = new Vector3f();
    }

    /**
     * Constructor instantiates a new <code>Quaternion</code> object
     * from the given list of parameters.
     * @param x the x value of the quaternion.
     * @param y the y value of the quaternion.
     * @param z the z value of the quaternion.
     * @param w the w value of the quaternion.
     */
    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * sets the data in a <code>Quaternion</code> object
     * from the given list of parameters.
     * @param x the x value of the quaternion.
     * @param y the y value of the quaternion.
     * @param z the z value of the quaternion.
     * @param w the w value of the quaternion.
     */
    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Constructor instantiates a new <code>Quaternion</code> object from
     * a collection of rotation angles.
     * @param angles the angles of rotation that will define the
     *      <code>Quaternion</code>.
     */
    public Quaternion(float[] angles) {
        fromAngles(angles);
    }

    /**
     * Constructor instantiates a new <code>Quaternion</code> object from
     * an interpolation between two other quaternions.
     * @param q1 the first quaternion.
     * @param q2 the second quaternion.
     * @param interp the amount to interpolate between the two quaternions.
     */
    public Quaternion(Quaternion q1, Quaternion q2, float interp) {
        slerp(q1, q2, interp);
    }

    /**
     * Constructor instantiates a new <code>Quaternion</code> object from
     * an existing quaternion, creating a copy.
     * @param q the quaternion to copy.
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
     * @param angles the Euler angles of rotation.
     */
    public void fromAngles(float[] angles) {
        float angle;
        float sr, sp, sy, cr, cp, cy;
        angle = angles[2] * 0.5f;
        sy = (float) Math.sin(angle);
        cy = (float) Math.cos(angle);
        angle = angles[1] * 0.5f;
        sp = (float) Math.sin(angle);
        cp = (float) Math.cos(angle);
        angle = angles[0] * 0.5f;
        sr = (float) Math.sin(angle);
        cr = (float) Math.cos(angle);

        float crcp = cr * cp;
        float srsp = sr * sp;

        x = (sr * cp * cy - cr * sp * sy);
        y = (cr * sp * cy + sr * cp * sy);
        z = (crcp * sy - srsp * cy);
        w = (crcp * cy + srsp * sy);
    }

    /**
     *
     * <code>fromRotationMatrix</code> generates a quaternion from a supplied
     * matrix. This matrix is assumed to be a rotational matrix.
     * @param matrix the matrix that defines the rotation.
     */
    public void fromRotationMatrix(Matrix3f matrix) {
        float t = matrix.m00 + matrix.m11 + matrix.m22 + 1;

        if (t > 0f) {
            float s = 0.5f / (float)Math.sqrt(t);
            w = 0.25f / s;
            x = ( matrix.m21 - matrix.m12 ) * s;
            y = ( matrix.m02 - matrix.m20 ) * s;
            z = ( matrix.m10 - matrix.m01 ) * s;
        } else if ((matrix.m00 > matrix.m11) && (matrix.m00 > matrix.m22)) {
            float s = (float)Math.sqrt( 1.0f + matrix.m00 - matrix.m11 - matrix.m22 ) * 2;
            x = 0.25f * s;
            y = (matrix.m01 + matrix.m10 ) / s;
            z = (matrix.m02 + matrix.m20 ) / s;
            w = (matrix.m12 - matrix.m21 ) / s;
        } else if (matrix.m11 > matrix.m22) {
            float s = (float)Math.sqrt( 1.0f + matrix.m11 - matrix.m00 - matrix.m22 ) * 2;
            x = (matrix.m01 + matrix.m10 ) / s;
            y = 0.25f * s;
            z = (matrix.m12 + matrix.m21 ) / s;
            w = (matrix.m02 - matrix.m20 ) / s;
        } else {
            float s = (float)Math.sqrt( 1.0f + matrix.m22 - matrix.m00 - matrix.m11 ) * 2;
            x = (matrix.m02 + matrix.m20 ) / s;
            y = (matrix.m12 + matrix.m21 ) / s;
            z = 0.25f * s;
            w = (matrix.m01 - matrix.m10 ) / s;
        }

    }

    /**
     *
     * <code>toRotationMatrix</code> converts this quaternion to a rotational
     * matrix.
     * @return the rotation matrix representation of this quaternion.
     */
    public Matrix3f toRotationMatrix( ) {
        float fTx  = 2.0f*x;
        float fTy  = 2.0f*y;
        float fTz  = 2.0f*z;
        float fTwx = fTx*w;
        float fTwy = fTy*w;
        float fTwz = fTz*w;
        float fTxx = fTx*x;
        float fTxy = fTy*x;
        float fTxz = fTz*x;
        float fTyy = fTy*y;
        float fTyz = fTz*y;
        float fTzz = fTz*z;

        Matrix3f matrix = new Matrix3f();

        matrix.m00 = 1.0f-(fTyy+fTzz);
        matrix.m01 = fTxy-fTwz;
        matrix.m02 = fTxz+fTwy;
        matrix.m10 = fTxy+fTwz;
        matrix.m11 = 1.0f-(fTxx+fTzz);
        matrix.m12 = fTyz-fTwx;
        matrix.m20 = fTxz-fTwy;
        matrix.m21 = fTyz+fTwx;
        matrix.m22 = 1.0f-(fTxx+fTyy);

        return matrix;
    }

    /**
     * <code>getRotationColumn</code> returns one of three columns specified by the
     * parameter. This column is returned as a <code>Vector3f</code> object.
     *
     * @param i the column to retrieve. Must be between 0 and 2.
     * @return the column specified by the index.
     */
    public Vector3f getRotationColumn(int i) {
        return getRotationColumn(i, null);
    }

    /**
     * <code>getRotationColumn</code> returns one of three columns specified by the
     * parameter. This column is returned as a <code>Vector3f</code> object.
     *
     * @param i the column to retrieve. Must be between 0 and 2.
     * @param store the vector object to store the result in.  if null, a new one is created.
     * @return the column specified by the index.
     */
    public Vector3f getRotationColumn(int i, Vector3f store) {
        if (store == null) store = new Vector3f();
        float fTx  = 2.0f*x;
        float fTy  = 2.0f*y;
        float fTz  = 2.0f*z;
        float fTwx = fTx*w;
        float fTwy = fTy*w;
        float fTwz = fTz*w;
        float fTxx = fTx*x;
        float fTxy = fTy*x;
        float fTxz = fTz*x;
        float fTyy = fTy*y;
        float fTyz = fTz*y;
        float fTzz = fTz*z;

        switch (i) {
            case 0:
                store.x = 1.0f-(fTyy+fTzz);
                store.y = fTxy+fTwz;
                store.z = fTxz-fTwy;
                break;
            case 1:
                store.x = fTxy-fTwz;
                store.y = 1.0f-(fTxx+fTzz);
                store.z = fTyz+fTwx;
                break;
            case 2:
                store.x = fTxz+fTwy;
                store.y = fTyz-fTwx;
                store.z = 1.0f-(fTxx+fTyy);
                break;
            default:
                LoggingSystem.getLogger().log(
                    Level.WARNING,
                    "Invalid column index.");
                throw new JmeException("Invalid column index. " + i);
        }
        return store;
    }

    /**
     * <code>fromAngleAxis</code> sets this quaternion to the values
     * specified by an angle and an axis of rotation.
     * @param angle the angle to rotate (in radians).
     * @param axis the axis of rotation.
     */
    public void fromAngleAxis(float angle, Vector3f axis) {
        Vector3f normAxis = axis.normalize();
        float halfAngle = 0.5f * angle;
        float sin = (float) Math.sin(halfAngle);
        w = (float) Math.cos(halfAngle);
        x = sin * normAxis.x;
        y = sin * normAxis.y;
        z = sin * normAxis.z;
    }

    /**
     * <code>toAngleAxis</code> sets a given angle and axis to that
     * represented by the current quaternion. The values are stored
     * as following: The axis is provided as a parameter and built
     * by the method, the angle is returned as a float.
     * @param axis the object to contain the axis.
     * @return the angle of rotation.
     */
    public float toAngleAxis(Vector3f axis) {
        float sqrLength = x * x + y * y + z * z;
        float angle;
        if (sqrLength > 0.0) {
            angle = (float) (2.0 * Math.acos(w));
            float invLength = (float) (1.0 / Math.sqrt(sqrLength));
            axis.x = x * invLength;
            axis.y = y * invLength;
            axis.z = z * invLength;
        } else {
            angle = 0.0f;
            axis.x = 1.0f;
            axis.y = 0.0f;
            axis.z = 0.0f;
        }
        angle = (float)(angle*180/Math.PI);

        return angle;
    }

    /**
     * <code>slerp</code> sets this quaternion's value as an interpolation
     * between two other quaternions.
     * @param q1 the first quaternion.
     * @param q2 the second quaternion.
     * @param interp the amount to interpolate between the two quaternions.
     */
    public Quaternion slerp(Quaternion q1, Quaternion q2, float t) {
        // Create a local quaternion to store the interpolated quaternion
        Quaternion interpolated = new Quaternion();

        if (q1.x == q2.x && q1.y == q2.y && q1.z == q2.z && q1.w == q2.w) {
            return q1;
        }

        float result =
            (q1.x * q2.x) + (q1.y * q2.y) + (q1.z * q2.z) + (q1.w * q2.w);

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

        // Check if the angle between the 2 quaternions was big enough to warrant such calculations
        if ((1 - result) > 0.1f) {
            // Get the angle between the 2 quaternions, and then store the sin() of that angle
            float theta = (float) Math.acos(result);
            float sinTheta = (float) Math.sin(theta);

            // Calculate the scale for q1 and q2, according to the angle and it's sine value
            scale0 = (float) Math.sin((1 - t) * theta) / sinTheta;
            scale1 = (float) Math.sin((t * theta)) / sinTheta;
        }

        // Calculate the x, y, z and w values for the quaternion by using a special
        // form of linear interpolation for quaternions.
        interpolated.x = (scale0 * q1.x) + (scale1 * q2.x);
        interpolated.y = (scale0 * q1.y) + (scale1 * q2.y);
        interpolated.z = (scale0 * q1.z) + (scale1 * q2.z);
        interpolated.w = (scale0 * q1.w) + (scale1 * q2.w);

        // Return the interpolated quaternion
        x = interpolated.x;
        y = interpolated.y;
        z = interpolated.z;
        w = interpolated.w;
        return interpolated;
    }

    /**
     * <code>add</code> adds the values of this quaternion to those
     * of the parameter quaternion. The result is returned as a new
     * quaternion.
     * @param q the quaternion to add to this.
     * @return the new quaternion.
     */
    public Quaternion add(Quaternion q) {
        return new Quaternion(x + q.x, y + q.y, z + q.z, w + q.w);
    }

    /**
     * <code>subtract</code> subtracts the values of the parameter
     * quaternion from those of this quaternion. The result is
     * returned as a new quaternion.
     * @param q the quaternion to subtract from this.
     * @return the new quaternion.
     */
    public Quaternion subtract(Quaternion q) {
        return new Quaternion(x - q.x, y - q.y, z - q.z, w - q.w);
    }

    /**
     * <code>mult</code> multiplies this quaternion by a parameter
     * quaternion. The result is returned as a new quaternion. It should
     * be noted that quaternion multiplication is not cummulative so
     * q * p != p * q.
     * @param q the quaternion to multiply this quaternion by.
     * @return the new quaternion.
     */
    public Quaternion mult(Quaternion q) {
        Quaternion res = new Quaternion();
        res.x =  x * q.w + y * q.z - z * q.y + w * q.x;
        res.y = -x * q.z + y * q.w + z * q.x + w * q.y;
        res.z =  x * q.y - y * q.x + z * q.w + w * q.z;
        res.w = -x * q.x - y * q.y - z * q.z + w * q.w;
        return res;
    }

    /**
     * <code>mult</code> multiplies this quaternion by a parameter
     * quaternion. The result is returned as a new quaternion. It should
     * be noted that quaternion multiplication is not cummulative so
     * q * p != p * q.
     * @param q the quaternion to multiply this quaternion by.
     * @param res the quaternion to store the result in.
     * @return the new quaternion.
     */
    public Quaternion mult(Quaternion q, Quaternion res) {
        if (res == null) res = new Quaternion();
        res.x =  x * q.w + y * q.z - z * q.y + w * q.x;
        res.y = -x * q.z + y * q.w + z * q.x + w * q.y;
        res.z =  x * q.y - y * q.x + z * q.w + w * q.z;
        res.w = -x * q.x - y * q.y - z * q.z + w * q.w;
        return res;
    }

    /**
     * <code>apply</code> multiplies this quaternion by a parameter
     * matrix internally.
     * @param matrix
     */
    public void apply(Matrix3f matrix) {
        float oldX = x, oldY = y, oldZ = z, oldW = w;
        fromRotationMatrix(matrix);
        float tempX = x, tempY = y, tempZ = z, tempW = w;

        x =  oldX * tempW + oldY * tempZ - oldZ * tempY + oldW * tempX;
        y = -oldX * tempZ + oldY * tempW + oldZ * tempX + oldW * tempY;
        z =  oldX * tempY - oldY * tempX + oldZ * tempW + oldW * tempZ;
        w = -oldX * tempX - oldY * tempY - oldZ * tempZ + oldW * tempW;
    }

    /**
     * <code>mult</code> multiplies this quaternion by a parameter
     * vector. The result is returned as a new vector.
     * @param v the vector to multiply this quaternion by.
     * @return the new vector.
     */
    public Vector3f mult(Vector3f v) {
        uv.set(x,y,z).crossLocal(v);
        uuv.set(x,y,z).crossLocal(uv);
        uv.multLocal(2f * w);
        uuv.multLocal(2f);
        Vector3f rVal = new Vector3f();
        rVal.x = v.x + (uv.x+uuv.x);
        rVal.y = v.y + (uv.y+uuv.y);
        rVal.z = v.z + (uv.z+uuv.z);
        return rVal;
    }

    /**
     * <code>mult</code> multiplies this quaternion by a parameter
     * vector.  The result is stored in the supplied vector
     * @param v the vector to multiply this quaternion by.
     * @return v
     */
    public Vector3f multLocal(Vector3f v) {
        uv.set(x,y,z).crossLocal(v);
        uuv.set(x,y,z).crossLocal(uv);
        uv.multLocal(2f * w);
        uuv.multLocal(2f);
        v.x = v.x + (uv.x+uuv.x);
        v.y = v.y + (uv.y+uuv.y);
        v.z = v.z + (uv.z+uuv.z);
        return v;
    }

    /**
     * <code>mult</code> multiplies this quaternion by a parameter
     * vector. The result is returned as a new vector.
     * @param v the vector to multiply this quaternion by.
     * @param store the vector to store the result in
     * @return the result vector.
     */
    public Vector3f mult(Vector3f v, Vector3f store) {
        uv.set(x,y,z).crossLocal(v);
        uuv.set(x,y,z).crossLocal(uv);
        uv.multLocal(2f*w);
        uuv.multLocal(2f);
        if (store == null) store = new Vector3f();
        store.x = v.x + (uv.x + uuv.x);
        store.y = v.y + (uv.y + uuv.y);
        store.z = v.z + (uv.z + uuv.z);
        return store;
    }

    /**
     * <code>mult</code> multiplies this quaternion by a parameter
     * scalar. The result is returned as a new quaternion.
     * @param q the quaternion to multiply this quaternion by.
     * @return the new quaternion.
     */
    public Quaternion mult(float scalar) {
        return new Quaternion(scalar * w, scalar * x, scalar * y, scalar * z);
    }

    /**
     * <code>dot</code> calculates and returns the dot product of this
     * quaternion with that of the parameter quaternion.
     * @param q the quaternion to calculate the dot product of.
     * @return the dot product of this and the parameter quaternion.
     */
    public float dot(Quaternion q) {
        return w * q.w + x * q.x + y * q.y + z * q.z;
    }

    /**
     * <code>norm</code> returns the norm of this quaternion. This is
     * the dot product of this quaternion with itself.
     * @return the norm of the quaternion.
     */
    public float norm() {
        return w * w + x * x + y * y + z * z;
    }

    public void normalize() {
        double n = Math.sqrt(x*x + y*y + z*z + w*w);
        x /= n;
        y /= n;
        z /= n;
        w /= n;
    }


    /**
     * <code>inverse</code> returns the inverse of this quaternion as
     * a new quaternion. If this quaternion does not have an inverse
     * (if it's norma is 0 or less), then null is returned.
     * @return the inverse of this quaternion or null if the inverse
     * 		does not exist.
     */
    public Quaternion inverse() {
        float norm = w * w + x * x + y * y + z * z;
        if (norm > 0.0) {
            float invNorm = 1.0f / norm;
            return new Quaternion(
                w * invNorm,
                -x * invNorm,
                -y * invNorm,
                -z * invNorm);
        } else {
            // return an invalid result to flag the error
            return null;
        }
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

    public String toString() {
        return "com.jme.math.Quaternion: [x=" +x+" y="+y+" z="+z+" w="+w+"]";
    }
}
