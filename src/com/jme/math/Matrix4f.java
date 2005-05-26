/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.logging.Level;

import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;

/**
 * <code>Matrix</code> defines and maintains a 4x4 matrix in row major order. 
 * This matrix is intended for use in a translation and rotational capacity. 
 * It provides convinience methods for creating the matrix from a multitude 
 * of sources.
 * 
 * @author Mark Powell
 * @author Joshua Slack (revamp and various methods)
 * @version $Id: Matrix4f.java,v 1.14 2005-05-26 20:53:38 renanse Exp $
 */
public class Matrix4f {

    public float m00, m01, m02, m03;

    public float m10, m11, m12, m13;

    public float m20, m21, m22, m23;

    public float m30, m31, m32, m33;

    /**
     * Constructor instantiates a new <code>Matrix</code> that is set to the
     * identity matrix.
     *  
     */
    public Matrix4f() {
        loadIdentity();
    }

    /**
     * constructs a matrix with the given values.
     */
    public Matrix4f(float m00, float m01, float m02, float m03, 
            float m10, float m11, float m12, float m13,
            float m20, float m21, float m22, float m23,
            float m30, float m31, float m32, float m33) {

        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
    }

    /**
     * Constructor instantiates a new <code>Matrix</code> that is set to the
     * provided matrix. This constructor copies a given Matrix. If the provided
     * matrix is null, the constructor sets the matrix to the identity.
     * 
     * @param mat
     *            the matrix to copy.
     */
    public Matrix4f(Matrix4f mat) {
        copy(mat);
    }

    /**
     * <code>copy</code> transfers the contents of a given matrix to this
     * matrix. If a null matrix is supplied, this matrix is set to the identity
     * matrix.
     * 
     * @param matrix
     *            the matrix to copy.
     */
    public void copy(Matrix4f matrix) {
        if (null == matrix) {
            loadIdentity();
        } else {
            m00 = matrix.m00;
            m01 = matrix.m01;
            m02 = matrix.m02;
            m03 = matrix.m03;
            m10 = matrix.m10;
            m11 = matrix.m11;
            m12 = matrix.m12;
            m13 = matrix.m13;
            m20 = matrix.m20;
            m21 = matrix.m21;
            m22 = matrix.m22;
            m23 = matrix.m23;
            m30 = matrix.m30;
            m31 = matrix.m31;
            m32 = matrix.m32;
            m33 = matrix.m33;
        }
    }

    /**
     * <code>get</code> retrieves the values of this object into
     * a float array in row-major order.
     * 
     * @param matrix
     *            the matrix to set the values into.
     */
    public void get(float[] matrix) {
        get(matrix, true);
    }

    /**
     * <code>set</code> retrieves the values of this object into
     * a float array.
     * 
     * @param matrix
     *            the matrix to set the values into.
     * @param rowMajor
     *            whether the outgoing data is in row or column major order.
     */
    public void get(float[] matrix, boolean rowMajor) {
        if (matrix.length != 16) throw new JmeException(
                "Array must be of size 16.");

        if (rowMajor) {
	        matrix[0] = m00;
	        matrix[1] = m01;
	        matrix[2] = m02;
	        matrix[3] = m03;
	        matrix[4] = m10;
	        matrix[5] = m11;
	        matrix[6] = m12;
	        matrix[7] = m13;
	        matrix[8] = m20;
	        matrix[9] = m21;
	        matrix[10] = m22;
	        matrix[11] = m23;
	        matrix[12] = m30;
	        matrix[13] = m31;
	        matrix[14] = m32;
	        matrix[15] = m33;
        } else {
	        matrix[0] = m00;
	        matrix[4] = m01;
	        matrix[8] = m02;
	        matrix[12] = m03;
	        matrix[1] = m10;
	        matrix[5] = m11;
	        matrix[9] = m12;
	        matrix[13] = m13;
	        matrix[2] = m20;
	        matrix[6] = m21;
	        matrix[10] = m22;
	        matrix[14] = m23;
	        matrix[3] = m30;
	        matrix[7] = m31;
	        matrix[11] = m32;
	        matrix[15] = m33;
        }
    }

    /**
     * <code>get</code> retrieves a value from the matrix at the given
     * position. If the position is invalid a <code>JmeException</code> is
     * thrown.
     * 
     * @param i
     *            the row index.
     * @param j
     *            the colum index.
     * @return the value at (i, j).
     */
    public float get(int i, int j) {
        switch (i) {
        case 0:
            switch (j) {
            case 0: return m00;
            case 1: return m01;
            case 2: return m02;
            case 3: return m03;
            }
        case 1:
            switch (j) {
            case 0: return m10;
            case 1: return m11;
            case 2: return m12;
            case 3: return m13;
            }
        case 2:
            switch (j) {
            case 0: return m20;
            case 1: return m21;
            case 2: return m22;
            case 3: return m23;
            }
        case 3:
            switch (j) {
            case 0: return m30;
            case 1: return m31;
            case 2: return m32;
            case 3: return m33;
            }
        }

        LoggingSystem.getLogger().log(Level.WARNING, "Invalid matrix index.");
        throw new JmeException("Invalid indices into matrix.");
    }

    /**
     * <code>getColumn</code> returns one of three columns specified by the
     * parameter. This column is returned as a float array of length 4.
     * 
     * @param i
     *            the column to retrieve. Must be between 0 and 3.
     * @return the column specified by the index.
     */
    public float[] getColumn(int i) {
        return getColumn(i, null);
    }

    /**
     * <code>getColumn</code> returns one of three columns specified by the
     * parameter. This column is returned as a float[4].
     * 
     * @param i
     *            the column to retrieve. Must be between 0 and 3.
     * @param store
     *            the float array to store the result in. if null, a new one
     *            is created.
     * @return the column specified by the index.
     */
    public float[] getColumn(int i, float[] store) {
        if (store == null) store = new float[4];
        switch (i) {
        case 0:
            store[0] = m00;
            store[1] = m10;
            store[2] = m20;
            store[3] = m30;
            break;
        case 1:
            store[0] = m01;
            store[1] = m11;
            store[2] = m21;
            store[3] = m31;
            break;
        case 2:
            store[0] = m02;
            store[1] = m12;
            store[2] = m22;
            store[3] = m32;
            break;
        case 3:
            store[0] = m03;
            store[1] = m13;
            store[2] = m23;
            store[3] = m33;
            break;
        default:
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Invalid column index.");
            throw new JmeException("Invalid column index. " + i);
        }
        return store;
    }

    /**
     * 
     * <code>setColumn</code> sets a particular column of this matrix to that
     * represented by the provided vector.
     * 
     * @param i
     *            the column to set.
     * @param column
     *            the data to set.
     */
    public void setColumn(int i, float[] column) {

        if (column == null) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Column is null. Ignoring.");
            return;
        }
        switch (i) {
        case 0:
            m00 = column[0];
            m10 = column[1];
            m20 = column[2];
            m30 = column[3];
            break;
        case 1:
            m01 = column[0];
            m11 = column[1];
            m21 = column[2];
            m31 = column[3];
            break;
        case 2:
            m02 = column[0];
            m12 = column[1];
            m22 = column[2];
            m32 = column[3];
            break;
        case 3:
            m03 = column[0];
            m13 = column[1];
            m23 = column[2];
            m33 = column[3];
            break;
        default:
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Invalid column index.");
            throw new JmeException("Invalid column index. " + i);
        }    }

    /**
     * <code>set</code> places a given value into the matrix at the given
     * position. If the position is invalid a <code>JmeException</code> is
     * thrown.
     * 
     * @param i
     *            the row index.
     * @param j
     *            the colum index.
     * @param value
     *            the value for (i, j).
     */
    public void set(int i, int j, float value) {
        switch (i) {
        case 0:
            switch (j) {
            case 0: m00 = value; return;
            case 1: m01 = value; return;
            case 2: m02 = value; return;
            case 3: m03 = value; return;
            }
        case 1:
            switch (j) {
            case 0: m10 = value; return;
            case 1: m11 = value; return;
            case 2: m12 = value; return;
            case 3: m13 = value; return;
            }
        case 2:
            switch (j) {
            case 0: m20 = value; return;
            case 1: m21 = value; return;
            case 2: m22 = value; return;
            case 3: m23 = value; return;
            }
        case 3:
            switch (j) {
            case 0: m30 = value; return;
            case 1: m31 = value; return;
            case 2: m32 = value; return;
            case 3: m33 = value; return;
            }
        }

        LoggingSystem.getLogger().log(Level.WARNING, "Invalid matrix index.");
        throw new JmeException("Invalid indices into matrix.");
    }

    /**
     * <code>set</code> sets the values of this matrix from an array of
     * values.
     * 
     * @param matrix
     *            the matrix to set the value to.
     * @throws JmeException
     *             if the array is not of size 16.
     */
    public void set(float[][] matrix) {
        if (matrix.length != 4 || matrix[0].length != 4) { throw new JmeException(
                "Array must be of size 16."); }

        m00 = matrix[0][0];
        m01 = matrix[0][1];
        m02 = matrix[0][2];
        m03 = matrix[0][3];
        m10 = matrix[1][0];
        m11 = matrix[1][1];
        m12 = matrix[1][2];
        m13 = matrix[1][3];
        m20 = matrix[2][0];
        m21 = matrix[2][1];
        m22 = matrix[2][2];
        m23 = matrix[2][3];
        m30 = matrix[3][0];
        m31 = matrix[3][1];
        m32 = matrix[3][2];
        m33 = matrix[3][3];
    }

    /**
     * <code>set</code> sets the values of this matrix from an array of
     * values assuming that the data is rowMajor order;
     * 
     * @param matrix
     *            the matrix to set the value to.
     */
    public void set(float[] matrix) {
        set(matrix, true);
    }

    /**
     * <code>set</code> sets the values of this matrix from an array of
     * values;
     * 
     * @param matrix
     *            the matrix to set the value to.
     * @param rowMajor
     *            whether the incoming data is in row or column major order.
     */
    public void set(float[] matrix, boolean rowMajor) {
        if (matrix.length != 16) throw new JmeException(
                "Array must be of size 16.");

        if (rowMajor) {
	        m00 = matrix[0];
	        m01 = matrix[1];
	        m02 = matrix[2];
	        m03 = matrix[3];
	        m10 = matrix[4];
	        m11 = matrix[5];
	        m12 = matrix[6];
	        m13 = matrix[7];
	        m20 = matrix[8];
	        m21 = matrix[9];
	        m22 = matrix[10];
	        m23 = matrix[11];
	        m30 = matrix[12];
	        m31 = matrix[13];
	        m32 = matrix[14];
	        m33 = matrix[15];
        } else {
	        m00 = matrix[0];
	        m01 = matrix[4];
	        m02 = matrix[8];
	        m03 = matrix[12];
	        m10 = matrix[1];
	        m11 = matrix[5];
	        m12 = matrix[9];
	        m13 = matrix[13];
	        m20 = matrix[2];
	        m21 = matrix[6];
	        m22 = matrix[10];
	        m23 = matrix[14];
	        m30 = matrix[3];
	        m31 = matrix[7];
	        m32 = matrix[11];
	        m33 = matrix[15];
        }
    }

    /**
     * 
     * <code>set</code> defines the values of the matrix based on a supplied
     * <code>Quaternion</code>. It should be noted that all previous values
     * will be overridden.
     * 
     * @param quat
     *            the quaternion to create a rotational matrix from.
     */
    public void set(Quaternion quaternion) {
        loadIdentity();
        m00 = (float) (1.0 - 2.0 * quaternion.y * quaternion.y - 2.0
                * quaternion.z * quaternion.z);
        m10 = (float) (2.0 * quaternion.x * quaternion.y + 2.0
                * quaternion.w * quaternion.z);
        m20 = (float) (2.0 * quaternion.x * quaternion.z - 2.0
                * quaternion.w * quaternion.y);

        m01 = (float) (2.0 * quaternion.x * quaternion.y - 2.0
                * quaternion.w * quaternion.z);
        m11 = (float) (1.0 - 2.0 * quaternion.x * quaternion.x - 2.0
                * quaternion.z * quaternion.z);
        m21 = (float) (2.0 * quaternion.y * quaternion.z + 2.0
                * quaternion.w * quaternion.x);

        m02 = (float) (2.0 * quaternion.x * quaternion.z + 2.0
                * quaternion.w * quaternion.y);
        m12 = (float) (2.0 * quaternion.y * quaternion.z - 2.0
                * quaternion.w * quaternion.x);
        m22 = (float) (1.0 - 2.0 * quaternion.x * quaternion.x - 2.0
                * quaternion.y * quaternion.y);

    }

    /**
     * <code>transpose</code> locally transposes this Matrix.
     * 
     * @return this object for chaining.
     */
    public Matrix4f transpose() {
        float temp = 0;
        temp = m01;
        m01 = m10;
        m10 = temp;

        temp = m02;
        m02 = m20;
        m20 = temp;

        temp = m03;
        m03 = m30;
        m30 = temp;

        temp = m12;
        m12 = m21;
        m21 = temp;

        temp = m13;
        m13 = m31;
        m31 = temp;

        temp = m23;
        m23 = m32;
        m32 = temp;      
        
        return this;
    }
    
    /**
     * <code>toFloatBuffer</code> returns a FloatBuffer object that contains
     * the matrix data.
     * 
     * @return matrix data as a FloatBuffer.
     */
    public FloatBuffer toFloatBuffer() {
        FloatBuffer fb = ByteBuffer.allocateDirect(16*4).order(
                ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(m00).put(m01).put(m02).put(m03);
        fb.put(m10).put(m11).put(m12).put(m13);
        fb.put(m20).put(m21).put(m22).put(m23);
        fb.put(m30).put(m31).put(m32).put(m33);
        fb.rewind();
        return fb;
    }

    /**
     * <code>fillFloatBuffer</code> fills a FloatBuffer object with
     * the matrix data.
     * @param fb the buffer to fill, must be correct size
     * @return matrix data as a FloatBuffer.
     */
    public FloatBuffer fillFloatBuffer(FloatBuffer fb) {
        fb.clear();
        fb.put(m00).put(m01).put(m02).put(m03);
        fb.put(m10).put(m11).put(m12).put(m13);
        fb.put(m20).put(m21).put(m22).put(m23);
        fb.put(m30).put(m31).put(m32).put(m33);
        fb.rewind();
        return fb;
    }

    /**
     * <code>loadIdentity</code> sets this matrix to the identity matrix,
     * namely all zeros with ones along the diagonal.
     *  
     */
    public void loadIdentity() {
        zero();
        m00 = m11 = m22 = m33 = 1;
    }

    /**
     * <code>fromAngleAxis</code> sets this matrix4f to the values specified
     * by an angle and an axis of rotation.  This method creates an object, so
     * use fromAngleNormalAxis if your axis is already normalized.
     * 
     * @param angle
     *            the angle to rotate (in radians).
     * @param axis
     *            the axis of rotation.
     */
    public void fromAngleAxis(float angle, Vector3f axis) {
        Vector3f normAxis = axis.normalize();
        fromAngleNormalAxis(angle, normAxis);
    }

    /**
     * <code>fromAngleNormalAxis</code> sets this matrix4f to the values
     * specified by an angle and a normalized axis of rotation.
     * 
     * @param angle
     *            the angle to rotate (in radians).
     * @param axis
     *            the axis of rotation (already normalized).
     */
    public void fromAngleNormalAxis(float angle, Vector3f axis) {
        zero();
        m33 = 1;

        float fCos = FastMath.cos(angle);
        float fSin = FastMath.sin(angle);
        float fOneMinusCos = ((float)1.0)-fCos;
        float fX2 = axis.x*axis.x;
        float fY2 = axis.y*axis.y;
        float fZ2 = axis.z*axis.z;
        float fXYM = axis.x*axis.y*fOneMinusCos;
        float fXZM = axis.x*axis.z*fOneMinusCos;
        float fYZM = axis.y*axis.z*fOneMinusCos;
        float fXSin = axis.x*fSin;
        float fYSin = axis.y*fSin;
        float fZSin = axis.z*fSin;
        
        m00 = fX2*fOneMinusCos+fCos;
        m01 = fXYM-fZSin;
        m02 = fXZM+fYSin;
        m10 = fXYM+fZSin;
        m11 = fY2*fOneMinusCos+fCos;
        m12 = fYZM-fXSin;
        m20 = fXZM-fYSin;
        m21 = fYZM+fXSin;
        m22 = fZ2*fOneMinusCos+fCos;
    }

    /**
     * <code>mult</code> multiplies this matrix by a scalar.
     * 
     * @param scalar
     *            the scalar to multiply this matrix by.
     */
    public void multLocal(float scalar) {
        m00 *= scalar;
        m01 *= scalar;
        m02 *= scalar;
        m03 *= scalar;
        m10 *= scalar;
        m11 *= scalar;
        m12 *= scalar;
        m13 *= scalar;
        m20 *= scalar;
        m21 *= scalar;
        m22 *= scalar;
        m23 *= scalar;
        m30 *= scalar;
        m31 *= scalar;
        m32 *= scalar;
        m33 *= scalar;
    }

    /**
     * <code>mult</code> multiplies this matrix with another matrix. The
     * result matrix will then be returned. This matrix will be on the left hand
     * side, while the parameter matrix will be on the right.
     * 
     * @param in2
     *            the matrix to multiply this matrix by.
     * @return the resultant matrix
     */
    public Matrix4f mult(Matrix4f in2) {
        return mult(in2, null);
    }

    /**
     * <code>mult</code> multiplies this matrix with another matrix. The
     * result matrix will then be returned. This matrix will be on the left hand
     * side, while the parameter matrix will be on the right.
     * 
     * @param in2
     *            the matrix to multiply this matrix by.
     * @param store
     *            where to store the result.
     * @return the resultant matrix
     */
    public Matrix4f mult(Matrix4f in2, Matrix4f store) {
        if (store == null) store = new Matrix4f();
        store.m00 = m00 * in2.m00 + m01
                * in2.m10 + m02 * in2.m20;
        store.m01 = m00 * in2.m01 + m01
                * in2.m11 + m02 * in2.m21;
        store.m02 = m00 * in2.m02 + m01
                * in2.m12 + m02 * in2.m22;
        store.m03 = m00 * in2.m03 + m01
                * in2.m13 + m02 * in2.m23
                + m03;
        store.m10 = m10 * in2.m00 + m11
                * in2.m10 + m12 * in2.m20;
        store.m11 = m10 * in2.m01 + m11
                * in2.m11 + m12 * in2.m21;
        store.m12 = m10 * in2.m02 + m11
                * in2.m12 + m12 * in2.m22;
        store.m13 = m10 * in2.m03 + m11
                * in2.m13 + m12 * in2.m23
                + m13;
        store.m20 = m20 * in2.m00 + m21
                * in2.m10 + m22 * in2.m20;
        store.m21 = m20 * in2.m01 + m21
                * in2.m11 + m22 * in2.m21;
        store.m22 = m20 * in2.m02 + m21
                * in2.m12 + m22 * in2.m22;
        store.m23 = m20 * in2.m03 + m21
                * in2.m13 + m22 * in2.m23
                + m23;
        store.m30 = this.m00 * in2.get(3, 0)
                + this.m10 * in2.get(3, 1) + this.m20
                * in2.get(3, 2) + this.m30;
        store.m31 = this.m01 * in2.get(3, 0)
                + this.m11 * in2.get(3, 1) + this.m21
                * in2.get(3, 2) + this.m31;
        store.m32 = this.m02 * in2.get(3, 0)
                + this.m12 * in2.get(3, 1) + this.m22
                * in2.get(3, 2) + this.m32;
        store.m33 = 1;
        return store;
    }

    /**
     * <code>mult</code> multiplies this matrix with another matrix. The
     * results are stored internally and a handle to this matrix will 
     * then be returned. This matrix will be on the left hand
     * side, while the parameter matrix will be on the right.
     * 
     * @param in2
     *            the matrix to multiply this matrix by.
     * @return the resultant matrix
     */
    public Matrix4f multLocal(Matrix4f in2) {
        float temp00, temp01, temp02, temp03;
        float temp10, temp11, temp12, temp13;
        float temp20, temp21, temp22, temp23;
        float temp30, temp31, temp32, temp33;

        temp00 = m00 * in2.m00 + m01
                * in2.m10 + m02 * in2.m20;
        temp01 = m00 * in2.m01 + m01
                * in2.m11 + m02 * in2.m21;
        temp02 = m00 * in2.m02 + m01
                * in2.m12 + m02 * in2.m22;
        temp03 = m00 * in2.m03 + m01
                * in2.m13 + m02 * in2.m23
                + m03;
        temp10 = m10 * in2.m00 + m11
                * in2.m10 + m12 * in2.m20;
        temp11 = m10 * in2.m01 + m11
                * in2.m11 + m12 * in2.m21;
        temp12 = m10 * in2.m02 + m11
                * in2.m12 + m12 * in2.m22;
        temp13 = m10 * in2.m03 + m11
                * in2.m13 + m12 * in2.m23
                + m13;
        temp20 = m20 * in2.m00 + m21
                * in2.m10 + m22 * in2.m20;
        temp21 = m20 * in2.m01 + m21
                * in2.m11 + m22 * in2.m21;
        temp22 = m20 * in2.m02 + m21
                * in2.m12 + m22 * in2.m22;
        temp23 = m20 * in2.m03 + m21
                * in2.m13 + m22 * in2.m23
                + m23;
        temp30 = this.m00 * in2.get(3, 0)
                + this.m10 * in2.get(3, 1) + this.m20
                * in2.get(3, 2) + this.m30;
        temp31 = this.m01 * in2.get(3, 0)
                + this.m11 * in2.get(3, 1) + this.m21
                * in2.get(3, 2) + this.m31;
        temp32 = this.m02 * in2.get(3, 0)
                + this.m12 * in2.get(3, 1) + this.m22
                * in2.get(3, 2) + this.m32;
        temp33 = 1;

        m00 = temp00;  m01 = temp01;  m02 = temp02;  m03 = temp03;
        m10 = temp10;  m11 = temp11;  m12 = temp12;  m13 = temp13;
        m20 = temp20;  m21 = temp21;  m22 = temp22;  m23 = temp23;
        m30 = temp30;  m31 = temp31;  m32 = temp32;  m33 = temp33;
        
        return this;
    }

    /**
     * <code>mult</code> multiplies a vector about a rotation matrix. The
     * resulting vector is returned as a new Vector3f.
     * 
     * @param vec
     *            vec to multiply against.
     * @return the rotated vector.
     */
    public Vector3f mult(Vector3f vec) {
        return mult(vec, null);
    }

    /**
     * <code>mult</code> multiplies a vector about a rotation matrix. The
     * resulting vector is returned.
     * 
     * @param vec
     *            vec to multiply against.
     * @param store
     *            a vector to store the result in.  created if null is passed.
     * @return the rotated vector.
     */
    public Vector3f mult(Vector3f vec, Vector3f store) {
        if (null == vec) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Source vector is" + " null, null result returned.");
            return null;
        }
        if (store == null) store = new Vector3f();
        
        store.x = m00 * vec.x + m10 * vec.y + m20 * vec.z;
        store.y = m01 * vec.x + m11 * vec.y + m21 * vec.z;
        store.z = m02 * vec.x + m12 * vec.y + m22 * vec.z;

        return store;
    }

    /**
     * <code>mult</code> multiplies an array of 4 floats against this rotation
     * matrix. The results are stored directly in the array. (vec4f x mat4f)
     * 
     * @param vec4f
     *            float array (size 4) to multiply by the matrix.
     * @return the vec4f for chaining.
     */
    public float[] mult(float[] vec4f) {
        if (null == vec4f || vec4f.length != 4) {
            System.err.println("invalid array given, must be nonnull and length 4");
            return null;
        }

        float x = vec4f[0], y = vec4f[1], z = vec4f[2], w = vec4f[3];
        
        vec4f[0] = m00 * x + m10 * y + m20 * z + m30 * w;
        vec4f[1] = m01 * x + m11 * y + m21 * z + m31 * w;
        vec4f[2] = m02 * x + m12 * y + m22 * z + m32 * w;
        vec4f[3] = m03 * x + m13 * y + m23 * z + m33 * w;

        return vec4f;
    }

    /**
     * Inverts this matrix as a new Matrix3f.
     * 
     * @return The new inverse matrix
     */
    public Matrix4f invert() {
        return invert(null);
    }

    /**
     * Inverts this matrix and stores it in the given store.
     * 
     * @return The store
     */
    public Matrix4f invert(Matrix4f store) {
        if (store == null) store = new Matrix4f();

        float fA0 = m00*m11 - m01*m10;
        float fA1 = m00*m12 - m02*m10;
        float fA2 = m00*m13 - m03*m10;
        float fA3 = m01*m12 - m02*m11;
        float fA4 = m01*m13 - m03*m11;
        float fA5 = m02*m13 - m03*m12;
        float fB0 = m20*m31 - m21*m30;
        float fB1 = m20*m32 - m22*m30;
        float fB2 = m20*m33 - m23*m30;
        float fB3 = m21*m32 - m22*m31;
        float fB4 = m21*m33 - m23*m31;
        float fB5 = m22*m33 - m23*m32;
        float fDet = fA0*fB5-fA1*fB4+fA2*fB3+fA3*fB2-fA4*fB1+fA5*fB0;

        if ( FastMath.abs(fDet) <= FastMath.FLT_EPSILON )
            return store.zero();

        store.m00 = + m11*fB5 - m12*fB4 + m13*fB3;
        store.m10 = - m10*fB5 + m12*fB2 - m13*fB1;
        store.m20 = + m10*fB4 - m11*fB2 + m13*fB0;
        store.m30 = - m10*fB3 + m11*fB1 - m12*fB0;
        store.m01 = - m01*fB5 + m02*fB4 - m03*fB3;
        store.m11 = + m00*fB5 - m02*fB2 + m03*fB1;
        store.m21 = - m00*fB4 + m01*fB2 - m03*fB0;
        store.m31 = + m00*fB3 - m01*fB1 + m02*fB0;
        store.m02 = + m31*fA5 - m32*fA4 + m33*fA3;
        store.m12 = - m30*fA5 + m32*fA2 - m33*fA1;
        store.m22 = + m30*fA4 - m31*fA2 + m33*fA0;
        store.m32 = - m30*fA3 + m31*fA1 - m32*fA0;
        store.m03 = - m21*fA5 + m22*fA4 - m23*fA3;
        store.m13 = + m20*fA5 - m22*fA2 + m23*fA1;
        store.m23 = - m20*fA4 + m21*fA2 - m23*fA0;
        store.m33 = + m20*fA3 - m21*fA1 + m22*fA0;

        float fInvDet = 1.0f/fDet;
        store.multLocal(fInvDet);

        return store;
    }

    /**
     * Inverts this matrix locally.
     * 
     * @return this
     */
    public Matrix4f invertLocal() {

        float fA0 = m00*m11 - m01*m10;
        float fA1 = m00*m12 - m02*m10;
        float fA2 = m00*m13 - m03*m10;
        float fA3 = m01*m12 - m02*m11;
        float fA4 = m01*m13 - m03*m11;
        float fA5 = m02*m13 - m03*m12;
        float fB0 = m20*m31 - m21*m30;
        float fB1 = m20*m32 - m22*m30;
        float fB2 = m20*m33 - m23*m30;
        float fB3 = m21*m32 - m22*m31;
        float fB4 = m21*m33 - m23*m31;
        float fB5 = m22*m33 - m23*m32;
        float fDet = fA0*fB5-fA1*fB4+fA2*fB3+fA3*fB2-fA4*fB1+fA5*fB0;

        if ( FastMath.abs(fDet) <= FastMath.FLT_EPSILON )
            return zero();

        float f00 = + m11*fB5 - m12*fB4 + m13*fB3;
        float f10 = - m10*fB5 + m12*fB2 - m13*fB1;
        float f20 = + m10*fB4 - m11*fB2 + m13*fB0;
        float f30 = - m10*fB3 + m11*fB1 - m12*fB0;
        float f01 = - m01*fB5 + m02*fB4 - m03*fB3;
        float f11 = + m00*fB5 - m02*fB2 + m03*fB1;
        float f21 = - m00*fB4 + m01*fB2 - m03*fB0;
        float f31 = + m00*fB3 - m01*fB1 + m02*fB0;
        float f02 = + m31*fA5 - m32*fA4 + m33*fA3;
        float f12 = - m30*fA5 + m32*fA2 - m33*fA1;
        float f22 = + m30*fA4 - m31*fA2 + m33*fA0;
        float f32 = - m30*fA3 + m31*fA1 - m32*fA0;
        float f03 = - m21*fA5 + m22*fA4 - m23*fA3;
        float f13 = + m20*fA5 - m22*fA2 + m23*fA1;
        float f23 = - m20*fA4 + m21*fA2 - m23*fA0;
        float f33 = + m20*fA3 - m21*fA1 + m22*fA0;
        
        m00 = f00;
        m01 = f01;
        m02 = f02;
        m03 = f03;
        m10 = f10;
        m11 = f11;
        m12 = f12;
        m13 = f13;
        m20 = f20;
        m21 = f21;
        m22 = f22;
        m23 = f23;
        m30 = f30;
        m31 = f31;
        m32 = f32;
        m33 = f33;

        float fInvDet = 1.0f/fDet;
        multLocal(fInvDet);

        return this;
    }
    
    /**
     * Returns a new matrix representing the adjoint of this matrix.
     * 
     * @return The adjoint matrix
     */
    public Matrix4f adjoint() {
        return adjoint(null);
    }
     
    
    /**
     * Places the adjoint of this matrix in store (creates store if null.)
     * 
     * @param store
     *            The matrix to store the result in.  If null, a new matrix is created.
     * @return store
     */
    public Matrix4f adjoint(Matrix4f store) {
        if (store == null) store = new Matrix4f();

        float fA0 = m00*m11 - m01*m10;
        float fA1 = m00*m12 - m02*m10;
        float fA2 = m00*m13 - m03*m10;
        float fA3 = m01*m12 - m02*m11;
        float fA4 = m01*m13 - m03*m11;
        float fA5 = m02*m13 - m03*m12;
        float fB0 = m20*m31 - m21*m30;
        float fB1 = m20*m32 - m22*m30;
        float fB2 = m20*m33 - m23*m30;
        float fB3 = m21*m32 - m22*m31;
        float fB4 = m21*m33 - m23*m31;
        float fB5 = m22*m33 - m23*m32;

        store.m00 = + m11*fB5 - m12*fB4 + m13*fB3;
        store.m10 = - m10*fB5 + m12*fB2 - m13*fB1;
        store.m20 = + m10*fB4 - m11*fB2 + m13*fB0;
        store.m30 = - m10*fB3 + m11*fB1 - m12*fB0;
        store.m01 = - m01*fB5 + m02*fB4 - m03*fB3;
        store.m11 = + m00*fB5 - m02*fB2 + m03*fB1;
        store.m21 = - m00*fB4 + m01*fB2 - m03*fB0;
        store.m31 = + m00*fB3 - m01*fB1 + m02*fB0;
        store.m02 = + m31*fA5 - m32*fA4 + m33*fA3;
        store.m12 = - m30*fA5 + m32*fA2 - m33*fA1;
        store.m22 = + m30*fA4 - m31*fA2 + m33*fA0;
        store.m32 = - m30*fA3 + m31*fA1 - m32*fA0;
        store.m03 = - m21*fA5 + m22*fA4 - m23*fA3;
        store.m13 = + m20*fA5 - m22*fA2 + m23*fA1;
        store.m23 = - m20*fA4 + m21*fA2 - m23*fA0;
        store.m33 = + m20*fA3 - m21*fA1 + m22*fA0;

        return store;
    }

    /**
     * <code>determinant</code> generates the determinate of this matrix.
     * 
     * @return the determinate
     */
    public float determinant() {
        float fA0 = m00*m11 - m01*m10;
        float fA1 = m00*m12 - m02*m10;
        float fA2 = m00*m13 - m03*m10;
        float fA3 = m01*m12 - m02*m11;
        float fA4 = m01*m13 - m03*m11;
        float fA5 = m02*m13 - m03*m12;
        float fB0 = m20*m31 - m21*m30;
        float fB1 = m20*m32 - m22*m30;
        float fB2 = m20*m33 - m23*m30;
        float fB3 = m21*m32 - m22*m31;
        float fB4 = m21*m33 - m23*m31;
        float fB5 = m22*m33 - m23*m32;
        float fDet = fA0*fB5-fA1*fB4+fA2*fB3+fA3*fB2-fA4*fB1+fA5*fB0;
        return fDet;
    }

    /**
     * Sets all of the values in this matrix to zero.
     * 
     * @return this matrix
     */
    public Matrix4f zero() {
        m00 = m01 = m02 = m03 = 0.0f;
        m10 = m11 = m12 = m13 = 0.0f;
        m20 = m21 = m22 = m23 = 0.0f;
        m30 = m31 = m32 = m33 = 0.0f;
        return this;
    }

    /**
     * <code>add</code> adds the values of a parameter matrix to this matrix.
     * 
     * @param mat
     *            the matrix to add to this.
     */
    public void add(Matrix4f mat) {
        m00 += mat.m00;
        m01 += mat.m01;
        m02 += mat.m02;
        m03 += mat.m03;
        m10 += mat.m10;
        m11 += mat.m11;
        m12 += mat.m12;
        m13 += mat.m13;
        m20 += mat.m20;
        m21 += mat.m21;
        m22 += mat.m22;
        m23 += mat.m23;
        m30 += mat.m30;
        m31 += mat.m31;
        m32 += mat.m32;
        m33 += mat.m33;
    }

    /**
     * <code>setTranslation</code> will set the matrix's translation values.
     * 
     * @param translation
     *            the new values for the translation.
     * @throws MonkeyRuntimeException
     *             if translation is not size 3.
     */
    public void setTranslation(float[] translation) {
        if (translation.length != 3) { throw new JmeException(
                "Translation size must be 3."); }
        m30 = translation[0];
        m31 = translation[1];
        m32 = translation[2];
    }

    /**
     * <code>setInverseTranslation</code> will set the matrix's inverse
     * translation values.
     * 
     * @param translation
     *            the new values for the inverse translation.
     * @throws MonkeyRuntimeException
     *             if translation is not size 3.
     */
    public void setInverseTranslation(float[] translation) {
        if (translation.length != 3) { throw new JmeException(
                "Translation size must be 3."); }
        m30 = -translation[0];
        m31 = -translation[1];
        m32 = -translation[2];
    }

    /**
     * <code>angleRotation</code> sets this matrix to that of a rotation about
     * three axes (x, y, z). Where each axis has a specified rotation in
     * degrees. These rotations are expressed in a single <code>Vector3f</code>
     * object.
     * 
     * @param angles
     *            the angles to rotate.
     */
    public void angleRotation(Vector3f angles) {
        float angle;
        float sr, sp, sy, cr, cp, cy;

        angle = (angles.z * FastMath.DEG_TO_RAD);
        sy = FastMath.sin(angle);
        cy = FastMath.cos(angle);
        angle = (angles.y * FastMath.DEG_TO_RAD);
        sp = FastMath.sin(angle);
        cp = FastMath.cos(angle);
        angle = (angles.x * FastMath.DEG_TO_RAD);
        sr = FastMath.sin(angle);
        cr = FastMath.cos(angle);

        // matrix = (Z * Y) * X
        m00 = cp * cy;
        m10 = cp * sy;
        m20 = -sp;
        m01 = sr * sp * cy + cr * -sy;
        m11 = sr * sp * sy + cr * cy;
        m21 = sr * cp;
        m02 = (cr * sp * cy + -sr * -sy);
        m12 = (cr * sp * sy + -sr * cy);
        m22 = cr * cp;
        m03 = 0.0f;
        m13 = 0.0f;
        m23 = 0.0f;
    }

    /**
     * <code>setRotationQuaternion</code> builds a rotation from a
     * <code>Quaternion</code>.
     * 
     * @param quat
     *            the quaternion to build the rotation from.
     * @throws MonkeyRuntimeException
     *             if quat is null.
     */
    public void setRotationQuaternion(Quaternion quat) {
        if (null == quat) { throw new JmeException("Quat may not be null."); }
        m00 = (float) (1.0 - 2.0 * quat.y * quat.y - 2.0 * quat.z
                * quat.z);
        m01 = (float) (2.0 * quat.x * quat.y + 2.0 * quat.w * quat.z);
        m02 = (float) (2.0 * quat.x * quat.z - 2.0 * quat.w * quat.y);

        m10 = (float) (2.0 * quat.x * quat.y - 2.0 * quat.w * quat.z);
        m11 = (float) (1.0 - 2.0 * quat.x * quat.x - 2.0 * quat.z
                * quat.z);
        m12 = (float) (2.0 * quat.y * quat.z + 2.0 * quat.w * quat.x);

        m20 = (float) (2.0 * quat.x * quat.z + 2.0 * quat.w * quat.y);
        m21 = (float) (2.0 * quat.y * quat.z - 2.0 * quat.w * quat.x);
        m22 = (float) (1.0 - 2.0 * quat.x * quat.x - 2.0 * quat.y
                * quat.y);
    }

    /**
     * <code>setInverseRotationRadians</code> builds an inverted rotation from
     * Euler angles that are in radians.
     * 
     * @param angles
     *            the Euler angles in radians.
     * @throws JmeException
     *             if angles is not size 3.
     */
    public void setInverseRotationRadians(float[] angles) {
        if (angles.length != 3) { throw new JmeException(
                "Angles must be of size 3."); }
        double cr = FastMath.cos(angles[0]);
        double sr = FastMath.sin(angles[0]);
        double cp = FastMath.cos(angles[1]);
        double sp = FastMath.sin(angles[1]);
        double cy = FastMath.cos(angles[2]);
        double sy = FastMath.sin(angles[2]);

        m00 = (float) (cp * cy);
        m10 = (float) (cp * sy);
        m20 = (float) (-sp);

        double srsp = sr * sp;
        double crsp = cr * sp;

        m01 = (float) (srsp * cy - cr * sy);
        m11 = (float) (srsp * sy + cr * cy);
        m21 = (float) (sr * cp);

        m02 = (float) (crsp * cy + sr * sy);
        m12 = (float) (crsp * sy - sr * cy);
        m22 = (float) (cr * cp);
    }

    /**
     * <code>setInverseRotationDegrees</code> builds an inverted rotation from
     * Euler angles that are in degrees.
     * 
     * @param angles
     *            the Euler angles in degrees.
     * @throws JmeException
     *             if angles is not size 3.
     */
    public void setInverseRotationDegrees(float[] angles) {
        if (angles.length != 3) { throw new JmeException(
                "Angles must be of size 3."); }
        float vec[] = new float[3];
        vec[0] = (angles[0] * FastMath.RAD_TO_DEG);
        vec[1] = (angles[1] * FastMath.RAD_TO_DEG);
        vec[2] = (angles[2] * FastMath.RAD_TO_DEG);
        setInverseRotationRadians(vec);
    }

    /**
     * 
     * <code>inverseTranslateVect</code> translates a given Vector3f by the
     * translation part of this matrix.
     * 
     * @param Vector3f
     *            the Vector3f to be translated.
     * @throws JmeException
     *             if the size of the Vector3f is not 3.
     */
    public void inverseTranslateVect(float[] Vector3f) {
        if (Vector3f.length != 3) { throw new JmeException(
                "Vector3f must be of size 3."); }

        Vector3f[0] = Vector3f[0] - m30;
        Vector3f[1] = Vector3f[1] - m31;
        Vector3f[2] = Vector3f[2] - m32;
    }

    /**
     * 
     * <code>inverseRotateVect</code> rotates a given Vector3f by the rotation
     * part of this matrix.
     * 
     * @param Vector3f
     *            the Vector3f to be rotated.
     * @throws JmeException
     *             if the size of the Vector3f is not 3.
     */
    public void inverseRotateVect(float[] vec) {
        if (vec.length != 3) { throw new JmeException(
                "Vector3f must be of size 3."); }

        vec[0] = vec[0] * m00 + vec[1] * m01 + vec[2]
                * m02;
        vec[1] = vec[0] * m10 + vec[1] * m11 + vec[2]
                * m12;
        vec[2] = vec[0] * m20 + vec[1] * m21 + vec[2]
                * m22;
    }

    /**
     * <code>inverseRotate</code> uses the rotational part of the matrix to
     * rotate a vector in the opposite direction.
     * 
     * @param v
     *            the vector to rotate.
     * @return the rotated vector.
     */
    public Vector3f inverseRotate(Vector3f v) {
        Vector3f out = new Vector3f();
        out.x = v.x * m00 + v.y * m10 + v.z * m20;
        out.y = v.x * m01 + v.y * m11 + v.z * m21;
        out.z = v.x * m02 + v.y * m12 + v.z * m22;
        return out;
    }

    /**
     * <code>toString</code> returns the string representation of this object.
     * It is in a format of a 4x4 matrix. For example, an identity matrix would
     * be represented by the following string. com.jme.math.Matrix3f <br>[<br>
     * 1.0  0.0  0.0  0.0 <br>
     * 0.0  1.0  0.0  0.0 <br>
     * 0.0  0.0  1.0  0.0 <br>
     * 0.0  0.0  0.0  1.0 <br>]<br>
     * 
     * @return the string representation of this object.
     */
    public String toString() {
        StringBuffer result = new StringBuffer("com.jme.math.Matrix4f\n[\n");
        result.append(" ");
        result.append(m00);
        result.append("  ");
        result.append(m01);
        result.append("  ");
        result.append(m02);
        result.append("  ");
        result.append(m03);
        result.append(" \n");
        result.append(" ");
        result.append(m10);
        result.append("  ");
        result.append(m11);
        result.append("  ");
        result.append(m12);
        result.append("  ");
        result.append(m13);
        result.append(" \n");
        result.append(" ");
        result.append(m20);
        result.append("  ");
        result.append(m21);
        result.append("  ");
        result.append(m22);
        result.append("  ");
        result.append(m23);
        result.append(" \n");
        result.append(" ");
        result.append(m30);
        result.append("  ");
        result.append(m31);
        result.append("  ");
        result.append(m32);
        result.append("  ");
        result.append(m33);
        result.append(" \n]");
        return result.toString();
    }
}