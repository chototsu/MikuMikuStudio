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

import java.util.logging.Level;

import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;

/**
 * <code>Matrix3f</code> defines a 3x3 matrix. Matrix data is maintained 
 * internally and is acessible via the get and set methods. Convenience 
 * methods are used for matrix operations as well as generating a matrix from
 * a given set of values.
 * @author Mark Powell
 * @version $Id: Matrix3f.java,v 1.4 2003-11-24 15:07:44 mojomonkey Exp $
 */
public class Matrix3f {
    private float[][] matrix;

    /**
     * Constructor instantiates a new <code>Matrix3f</code> object. The
     * initial values for the matrix is that of the identity matrix.
     *
     */
    public Matrix3f() {
        matrix = new float[3][3];
        loadIdentity();
    }

    /**
     * <code>get</code> retrieves a value from the matrix at the given 
     * position. If the position is invalid a <code>JmeException</code>
     * is thrown.
     * @param i the row index.
     * @param j the colum index.
     * @return the value at (i, j).
     */
    public float get(int i, int j) {
        if (i < 0 || i > 2 || j < 0 || j > 2) {
            LoggingSystem.getLogger().log(
                Level.WARNING,
                "Invalid matrix index.");
            throw new JmeException("Invalid indices into matrix.");
        }
        return matrix[i][j];
    }

    /**
     * <code>getColumn</code> returns one of three columns specified by the
     * parameter. This column is returned as a <code>Vector3f</code> object.
     * 
     * @param i the column to retrieve. Must be between 0 and 2.
     * @return the column specified by the index.
     */
    public Vector3f getColumn(int i) {
        if (i < 0 || i > 2) {
            LoggingSystem.getLogger().log(
                Level.WARNING,
                "Invalid column index.");
            throw new JmeException("Invalid column index. " + i);
        }
        return new Vector3f(matrix[0][i], matrix[1][i], matrix[2][i]);
    }

    /**
     * <code>set</code> places a given value into the matrix at the given
     * position. If the position is invalid a <code>JmeException</code>
     * is thrown.
     * @param i the row index.
     * @param j the colum index.
     * @param value the value for (i, j).
     */
    public void set(int i, int j, float value) {
        if (i < 0 || i > 2 || j < 0 || j > 2) {
            LoggingSystem.getLogger().log(
                Level.WARNING,
                "Invalid matrix index.");
            throw new JmeException("Invalid indices into matrix.");
        }
        matrix[i][j] = value;
    }
    
    /**
     * 
     * <code>set</code> sets the values of the matrix to those supplied by
     * the 3x3 two dimenion array. 
     * @param matrix the new values of the matrix.
     */
    public void set(float[][] matrix) {
        if(matrix.length != 3 || matrix[0].length != 3) {
            return;
        }
        
        this.matrix = matrix;
    }
    
    /**
     * 
     * <code>set</code> defines the values of the matrix based on a supplied
     * <code>Quaternion</code>. It should be noted that all previous values
     * will be overridden.
     * @param quat the quaternion to create a rotational matrix from.
     */
    public void set(Quaternion quat) {
        float[] matrix = new float[16];
        matrix[0] = 1.0f - 2.0f * (quat.y * quat.y + quat.z * quat.z);
        matrix[1] = 2.0f * (quat.x * quat.y - quat.w * quat.z);
        matrix[2] = 2.0f * (quat.x * quat.z + quat.w * quat.y);
        
        // Second row
        matrix[3] = 2.0f * (quat.x * quat.y + quat.w * quat.z);
        matrix[4] = 1.0f - 2.0f * (quat.x * quat.x + quat.z * quat.z);
        matrix[5] = 2.0f * (quat.y * quat.z - quat.w * quat.x);
        
        // Third row
        matrix[6] = 2.0f * (quat.x * quat.z - quat.w * quat.y);
        matrix[7] = 2.0f * (quat.y * quat.z + quat.w * quat.x);
        matrix[8] = 1.0f - 2.0f * (quat.x * quat.x + quat.y * quat.y);
    }

    /**
     * <code>mult</code> multiplies this matrix by a given matrix. The
     * result matrix is returned as a new object. If the given matrix is null,
     * a null matrix is returned.
     * @param mat the matrix to multiply this matrix by.
     * @return the result matrix.
     */
    public Matrix3f mult(Matrix3f mat) {
        if (null == mat) {
            LoggingSystem.getLogger().log(
                Level.WARNING,
                "Source matrix is " + "null, null result returned.");
            return null;
        }
        Matrix3f product = new Matrix3f();
        for (int iRow = 0; iRow < 3; iRow++) {
            for (int iCol = 0; iCol < 3; iCol++) {
                product.set(
                    iRow,
                    iCol,
                    matrix[iRow][0] * mat.get(0, iCol)
                        + matrix[iRow][1] * mat.get(1, iCol)
                        + matrix[iRow][2] * mat.get(2, iCol));
            }
        }
        return product;
    }

    /**
     * <code>mult</code> multiplies this matrix by a given <code>Vector3f</code>
     * object. The result vector is returned. If the given vector is null,
     * null will be returned.
     * @param vec the vector to multiply this matrix by.
     * @return the result vector.
     */
    public Vector3f mult(Vector3f vec) {
        if (null == vec) {
            LoggingSystem.getLogger().log(
                Level.WARNING,
                "Source vector is" + " null, null result returned.");
            return null;
        }
        Vector3f product = new Vector3f();
        product.x =
            matrix[0][0] * vec.x + matrix[0][1] * vec.y + matrix[0][2] * vec.z;
        product.y =
            matrix[1][0] * vec.x + matrix[1][1] * vec.y + matrix[1][2] * vec.z;
        product.z =
            matrix[2][0] * vec.x + matrix[2][1] * vec.y + matrix[2][2] * vec.z;

        return product;
    }

    /**
     * <code>loadIdentity</code> sets this matrix to the identity matrix. 
     * Where all values are zero except those along the diagonal which are
     * one.
     *
     */
    public void loadIdentity() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == j) {
                    matrix[i][j] = 1.0f;
                } else {
                    matrix[i][j] = 0.0f;
                }
            }
        }
    }

    /**
     * 
     * <code>fromAxisAngle</code> creates a rotational matrix given an axis
     * and an angle. The angle is expected to be in radians.
     * @param axis the axis to rotate about.
     * @param radian the angle to rotate.
     */
    public void fromAxisAngle(Vector3f axis, float radian) {
        float fCos = (float)Math.cos(radian);
        float fSin = (float)Math.sin(radian);
        float fOneMinusCos = 1.0f - fCos;
        float fX2 = axis.x * axis.x;
        float fY2 = axis.y * axis.y;
        float fZ2 = axis.z * axis.z;
        float fXYM = axis.x * axis.y * fOneMinusCos;
        float fXZM = axis.x * axis.z * fOneMinusCos;
        float fYZM = axis.y * axis.z * fOneMinusCos;
        float fXSin = axis.x * fSin;
        float fYSin = axis.y * fSin;
        float fZSin = axis.z * fSin;

        matrix[0][0] = fX2 * fOneMinusCos + fCos;
        matrix[0][1] = fXYM - fZSin;
        matrix[0][2] = fXZM + fYSin;
        matrix[1][0] = fXYM + fZSin;
        matrix[1][1] = fY2 * fOneMinusCos + fCos;
        matrix[1][2] = fYZM - fXSin;
        matrix[2][0] = fXZM - fYSin;
        matrix[2][1] = fYZM + fXSin;
        matrix[2][2] = fZ2 * fOneMinusCos + fCos;
    }

    /**
     * <code>toString</code> returns the string representation of this object.
     * It is in a format of a 3x3 matrix. For example, an identity matrix would
     * be represented by the following string.
     * com.jme.math.Matrix3f<br>
     * [<br>
     *   1.0  0.0  0.0 <br>
     *   0.0  1.0  0.0 <br>
     *   0.0  0.0  1.0 <br>
     * ]<br>
     * 
     * @return the string representation of this object.
     */
    public String toString() {
        String result = "com.jme.math.Matrix3f\n[\n";
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result += " " + matrix[i][j] + " ";
            }
            result += "\n";
        }
        result += "]";
        return result;
    }
}
