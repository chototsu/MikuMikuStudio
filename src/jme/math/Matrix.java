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
package jme.math;

import jme.exception.MonkeyRuntimeException;

/**
 * <code>Matrix</code> defines and maintains a 4x4 matrix. This matrix is
 * intended for use in a translation and rotational capacity. It provides
 * convinience methods for creating the matrix from a multitude of sources.
 * 
 * @author Mark Powell
 * @version $Id: Matrix.java,v 1.5 2003-08-28 18:48:21 mojomonkey Exp $
 */
public class Matrix {
    private float matrix[];

    /**
     * Constructor instantiates a new <code>Matrix</code> that is set to the
     * identity matrix.
     *
     */
    public Matrix() {
        loadIdentity();
    }
    
    /**
     * Constructor instantiates a new <code>Matrix</code> that is set to the
     * provided matrix. This constructor copies a given Matrix. If the 
     * provided matrix is null, the constructor sets the matrix to the 
     * identity.
     * @param mat the matrix to copy.
     */
    public Matrix(Matrix mat) {
    	if(null == mat) {
    		loadIdentity();
    	} else {
	    	for(int i = 0; i < 16; i++) {
	    		matrix[i] = mat.getMatrix()[i];
	    	}
    	}
    }

    /**
     * <code>loadIdentity</code> sets this matrix to the identity matrix, 
     * namely all zeros with ones along the diagonal.
     *
     */
    public void loadIdentity() {
        matrix = new float[16];
        matrix[0] = matrix[5] = matrix[10] = matrix[15] = 1;
    }

    /**
     * <code>set</code> sets the values of this matrix from an array of
     * values.
     * @param matrix the matrix to set the value to.
     * @throws MonkeyRuntimeException if the array is not of size 16.
     */
    public void set(float[] matrix) {
        if (matrix.length != 16) {
            throw new MonkeyRuntimeException("Array must be of size 16.");
        }

        for (int i = 0; i < 16; i++) {
            this.matrix[i] = matrix[i];
        }
    }
    
    public void add(Matrix matrix) {
        for(int i = 0; i < 16; i++) {
            this.matrix[i] += matrix.getMatrix()[i];
        }
    }
    
    /**
     * <code>multiply</code> multiplies this matrix by a scalar.
     * @param scalar the scalar to multiply this matrix by.
     */
    public void multiply(float scalar) {
        for(int i = 0; i < 16; i++) {
            matrix[i] *= scalar;
        }
    }

    /**
     * <code>multiply</code> multiplies this matrix with another matrix.
     * This matrix will be on the left hand side, while the parameter matrix
     * will be on the right.
     * @param matrix the matrix to multiply this matrix by.
     * @throws MonkeyRuntimeException if matrix is null.
     */
    public void multiply(Matrix matrix) {
        if(null == matrix) {
            throw new MonkeyRuntimeException("Matrix may not be null.");
        }
        this.matrix[0] =
            this.matrix[0] * matrix.getMatrix()[0]
                + this.matrix[4] * matrix.getMatrix()[1]
                + this.matrix[8] * matrix.getMatrix()[2];
        this.matrix[1] =
            this.matrix[1] * matrix.getMatrix()[0]
                + this.matrix[5] * matrix.getMatrix()[1]
                + this.matrix[9] * matrix.getMatrix()[2];
        this.matrix[2] =
            this.matrix[2] * matrix.getMatrix()[0]
                + this.matrix[6] * matrix.getMatrix()[1]
                + this.matrix[10] * matrix.getMatrix()[2];
        this.matrix[3] = 0;

        this.matrix[4] =
            this.matrix[0] * matrix.getMatrix()[4]
                + this.matrix[4] * matrix.getMatrix()[5]
                + this.matrix[8] * matrix.getMatrix()[6];
        this.matrix[5] =
            this.matrix[1] * matrix.getMatrix()[4]
                + this.matrix[5] * matrix.getMatrix()[5]
                + this.matrix[9] * matrix.getMatrix()[6];
        this.matrix[6] =
            this.matrix[2] * matrix.getMatrix()[4]
                + this.matrix[6] * matrix.getMatrix()[5]
                + this.matrix[10] * matrix.getMatrix()[6];
        this.matrix[7] = 0;

        this.matrix[8] =
            this.matrix[0] * matrix.getMatrix()[8]
                + this.matrix[4] * matrix.getMatrix()[9]
                + this.matrix[8] * matrix.getMatrix()[10];
        this.matrix[9] =
            this.matrix[1] * matrix.getMatrix()[8]
                + this.matrix[5] * matrix.getMatrix()[9]
                + this.matrix[9] * matrix.getMatrix()[10];
        this.matrix[10] =
            this.matrix[2] * matrix.getMatrix()[8]
                + this.matrix[6] * matrix.getMatrix()[9]
                + this.matrix[10] * matrix.getMatrix()[10];
        this.matrix[11] = 0;

        this.matrix[12] =
            this.matrix[0] * matrix.getMatrix()[12]
                + this.matrix[4] * matrix.getMatrix()[13]
                + this.matrix[8] * matrix.getMatrix()[14]
                + this.matrix[12];
        this.matrix[13] =
            this.matrix[1] * matrix.getMatrix()[12]
                + this.matrix[5] * matrix.getMatrix()[13]
                + this.matrix[9] * matrix.getMatrix()[14]
                + this.matrix[13];
        this.matrix[14] =
            this.matrix[2] * matrix.getMatrix()[12]
                + this.matrix[6] * matrix.getMatrix()[13]
                + this.matrix[10] * matrix.getMatrix()[14]
                + this.matrix[14];
        this.matrix[15] = 1;

    }
    
    /**
     * <code>setTranslation</code> will set the matrix's translation values.
     * @param translation the new values for the translation.
     * @throws MonkeyRuntimeException if translation is not size 3.
     */
    public void setTranslation(float[] translation) {
        if (translation.length != 3) {
            throw new MonkeyRuntimeException("Translation size must be 3.");
        }
        matrix[12] = translation[0];
        matrix[13] = translation[1];
        matrix[14] = translation[2];
    }

    /**
     * <code>setInverseTranslation</code> will set the matrix's inverse 
     * translation values.
     * @param translation the new values for the inverse translation.
     * @throws MonkeyRuntimeException if translation is not size 3.
     */
    public void setInverseTranslation(float[] translation) {
        if (translation.length != 3) {
            throw new MonkeyRuntimeException("Translation size must be 3.");
        }
        matrix[12] = -translation[0];
        matrix[13] = -translation[1];
        matrix[14] = -translation[2];
    }

    /**
     * <code>setRotationRadians</code> builds a rotation from Euler angles that
     * are in radians.
     * @param angles the Euler angles in radians.
     * @throws MonkeyRuntimeException if angles is not size 3.
     */
    public void setRotationRadians(float[] angles) {
        if (angles.length != 3) {
            throw new MonkeyRuntimeException("Angles must be of size 3.");
        }
        double cr = Math.cos(angles[0]);
        double sr = Math.sin(angles[0]);
        double cp = Math.cos(angles[1]);
        double sp = Math.sin(angles[1]);
        double cy = Math.cos(angles[2]);
        double sy = Math.sin(angles[2]);

        matrix[0] = (float) (cp * cy);
        matrix[1] = (float) (cp * sy);
        matrix[2] = (float) (-sp);

        double srsp = sr * sp;
        double crsp = cr * sp;

        matrix[4] = (float) (srsp * cy - cr * sy);
        matrix[5] = (float) (srsp * sy + cr * cy);
        matrix[6] = (float) (sr * cp);

        matrix[8] = (float) (crsp * cy + sr * sy);
        matrix[9] = (float) (crsp * sy - sr * cy);
        matrix[10] = (float) (cr * cp);
    }

    /**
    * <code>setRotationDegrees</code> builds a rotation from Euler angles that
    * are in degrees.
    * @param angles the Euler angles in degrees.
    * @throws MonkeyRuntimeException if angles is not size 3.
    */
    public void setRotationDegrees(float[] angles) {
        if (angles.length != 3) {
            throw new MonkeyRuntimeException("Angles must be of size 3.");
        }
        float vec[] = new float[3];
        vec[0] = (float) (angles[0] * 180.0 / Math.PI);
        vec[1] = (float) (angles[1] * 180.0 / Math.PI);
        vec[2] = (float) (angles[2] * 180.0 / Math.PI);
        setRotationRadians(vec);
    }

    /**
     * <code>setRotationQuaternion</code> builds a rotation from a 
     * <code>Quaternion</code>.
     * @param quat the quaternion to build the rotation from.
     * @throws MonkeyRuntimeException if quat is null.
     */
    public void setRotationQuaternion(Quaternion quat) {
        if(null == quat) {
            throw new MonkeyRuntimeException("Quat may not be null.");
        }
        matrix[0] =
            (float) (1.0 - 2.0 * quat.y * quat.y - 2.0 * quat.z * quat.z);
        matrix[1] = (float) (2.0 * quat.x * quat.y + 2.0 * quat.w * quat.z);
        matrix[2] = (float) (2.0 * quat.x * quat.z - 2.0 * quat.w * quat.y);

        matrix[4] = (float) (2.0 * quat.x * quat.y - 2.0 * quat.w * quat.z);
        matrix[5] =
            (float) (1.0 - 2.0 * quat.x * quat.x - 2.0 * quat.z * quat.z);
        matrix[6] = (float) (2.0 * quat.y * quat.z + 2.0 * quat.w * quat.x);

        matrix[8] = (float) (2.0 * quat.x * quat.z + 2.0 * quat.w * quat.y);
        matrix[9] = (float) (2.0 * quat.y * quat.z - 2.0 * quat.w * quat.x);
        matrix[10] =
            (float) (1.0 - 2.0 * quat.x * quat.x - 2.0 * quat.y * quat.y);
    }

    /**
     * <code>setInverseRotationRadians</code> builds an inverted rotation
     * from Euler angles that are in radians.
     * @param angles the Euler angles in radians.
     * @throws MonkeyRuntimeException if angles is not size 3.
     */
    public void setInverseRotationRadians(float[] angles) {
        if (angles.length != 3) {
            throw new MonkeyRuntimeException("Angles must be of size 3.");
        }
        double cr = Math.cos(angles[0]);
        double sr = Math.sin(angles[0]);
        double cp = Math.cos(angles[1]);
        double sp = Math.sin(angles[1]);
        double cy = Math.cos(angles[2]);
        double sy = Math.sin(angles[2]);

        matrix[0] = (float) (cp * cy);
        matrix[4] = (float) (cp * sy);
        matrix[8] = (float) (-sp);

        double srsp = sr * sp;
        double crsp = cr * sp;

        matrix[1] = (float) (srsp * cy - cr * sy);
        matrix[5] = (float) (srsp * sy + cr * cy);
        matrix[9] = (float) (sr * cp);

        matrix[2] = (float) (crsp * cy + sr * sy);
        matrix[6] = (float) (crsp * sy - sr * cy);
        matrix[10] = (float) (cr * cp);
    }

    /**
     * <code>setInverseRotationDegrees</code> builds an inverted rotation
     * from Euler angles that are in degrees.
     * @param angles the Euler angles in degrees.
     * @throws MonkeyRuntimeException if angles is not size 3.
     */
    public void setInverseRotationDegrees(float[] angles) {
        if (angles.length != 3) {
            throw new MonkeyRuntimeException("Angles must be of size 3.");
        }
        float vec[] = new float[3];
        vec[0] = (float) (angles[0] * 180.0 / Math.PI);
        vec[1] = (float) (angles[1] * 180.0 / Math.PI);
        vec[2] = (float) (angles[2] * 180.0 / Math.PI);
        setInverseRotationRadians(vec);
    }

    /**
     * <code>getMatrix</code> returns the current matrix as an array of
     * floats. Size 16.
     * @return the array of floats that represent this matrix.
     */
    public float[] getMatrix() {
        return matrix;
    }
    
    /**
     * 
     * <code>inverseTranslateVect</code> translates a given vector by the
     * translation part of this matrix.
     * @param vector the vector to be translated.
     * @throws MonkeyRuntimeException if the size of the vector is not 3.
     */
    public void inverseTranslateVect(float[] vector) {
        if (vector.length != 3) {
            throw new MonkeyRuntimeException("Vector must be of size 3.");
        }

        vector[0] = vector[0] - matrix[12];
        vector[1] = vector[1] - matrix[13];
        vector[2] = vector[2] - matrix[14];
    }

    /**
     * 
     * <code>inverseRotateVect</code> rotates a given vector by the rotation
     * part of this matrix.
     * @param vector the vector to be rotated.
     * @throws MonkeyRuntimeException if the size of the vector is not 3.
     */
    public void inverseRotateVect(float[] vector) {
        if (vector.length != 3) {
            throw new MonkeyRuntimeException("Vector must be of size 3.");
        }

        vector[0] =
            vector[0] * matrix[0]
                + vector[1] * matrix[1]
                + vector[2] * matrix[2];
        vector[1] =
            vector[0] * matrix[4]
                + vector[1] * matrix[5]
                + vector[2] * matrix[6];
        vector[2] =
            vector[0] * matrix[8]
                + vector[1] * matrix[9]
                + vector[2] * matrix[10];
    }
    
    public void tensorProduct (Vector u, Vector v) {
        matrix[0] = u.x * v.x;
        matrix[1] = u.x * v.y;
        matrix[2] = u.x * v.z;
        matrix[4] = u.y * v.x;
        matrix[5] = u.y * v.y;
        matrix[6] = u.y * v.z;
        matrix[8] = u.z * v.x;
        matrix[9] = u.z * v.y;
        matrix[10] = u.z * v.z;
    }
}
