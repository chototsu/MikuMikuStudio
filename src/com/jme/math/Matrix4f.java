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
 * <code>Matrix</code> defines and maintains a 4x4 matrix. This matrix is
 * intended for use in a translation and rotational capacity. It provides
 * convinience methods for creating the matrix from a multitude of sources.
 * 
 * @author Mark Powell
 * @version $Id: Matrix4f.java,v 1.1 2004-02-01 07:50:26 mojomonkey Exp $
 */
public class Matrix4f {
	private float matrix[][];

	/**
	 * Constructor instantiates a new <code>Matrix</code> that is set to the
	 * identity matrix.
	 *
	 */
	public Matrix4f() {
		matrix = new float[4][4];
		loadIdentity();
	}

	/**
	 * Constructor instantiates a new <code>Matrix</code> that is set to the
	 * provided matrix. This constructor copies a given Matrix. If the 
	 * provided matrix is null, the constructor sets the matrix to the 
	 * identity.
	 * @param mat the matrix to copy.
	 */
	public Matrix4f(Matrix4f mat) {
		copy(mat);
	}

	/**
	 * <code>copy</code> transfers the contents of a given matrix to this
	 * matrix. If a null matrix is supplied, this matrix is set to the
	 * identity matrix.
	 * @param matrix the matrix to copy.
	 */
	public void copy(Matrix4f matrix) {
		if (null == matrix) {
			loadIdentity();
		} else {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					this.matrix[i][j] = matrix.matrix[i][j];
				}
			}
		}
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
		if (i < 0 || i > 3 || j < 0 || j > 3) {
			LoggingSystem.getLogger().log(
				Level.WARNING,
				"Invalid matrix index.");
			throw new JmeException("Invalid indices into matrix.");
		}
		return matrix[i][j];
	}

	/**
	 * <code>getColumn</code> returns one of three columns specified by the
	 * parameter. This column is returned as a float array of length 4.
	 * 
	 * @param i the column to retrieve. Must be between 0 and 3.
	 * @return the column specified by the index.
	 */
	public float[] getColumn(int i) {
		if (i < 0 || i > 3) {
			LoggingSystem.getLogger().log(
				Level.WARNING,
				"Invalid column index.");
			throw new JmeException("Invalid column index. " + i);
		}
		return new float[] { matrix[0][i], matrix[1][i], matrix[2][i] };
	}

	/**
	 * 
	 * <code>setColumn</code> sets a particular column of this matrix to that
	 * represented by the provided vector.
	 * @param i the column to set.
	 * @param column the data to set.
	 */
	public void setColumn(int i, float[] column) {
		if (i < 0 || i > 3) {
			LoggingSystem.getLogger().log(
				Level.WARNING,
				"Invalid column index.");
			throw new JmeException("Invalid column index. " + i);
		}

		if (column.length != 4) {
			LoggingSystem.getLogger().log(
				Level.WARNING,
				"Column is not length 4. Ignoring.");
			return;
		}

		matrix[0][i] = column[0];
		matrix[1][i] = column[1];
		matrix[2][i] = column[2];
		matrix[3][i] = column[3];
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
		if (i < 0 || i > 3 || j < 0 || j > 3) {
			LoggingSystem.getLogger().log(
				Level.WARNING,
				"Invalid matrix index.");
			throw new JmeException("Invalid indices into matrix.");
		}
		matrix[i][j] = value;
	}

	/**
	 * <code>set</code> sets the values of this matrix from an array of
	 * values.
	 * @param matrix the matrix to set the value to.
	 * @throws MonkeyRuntimeException if the array is not of size 16.
	 */
	public void set(float[][] matrix) {
		if (matrix.length != 4 || matrix[0].length != 4) {
			throw new JmeException("Array must be of size 16.");
		}

		this.matrix = matrix;
	}

	/**
	 * <code>set</code> sets the values of this matrix from an array of
	 * values;
	 * @param matrix the matrix to set the value to.
	 */
	public void set(float[] matrix) {
		if (matrix.length != 16) {
			throw new JmeException("Array must be of size 16.");
		}
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				this.matrix[i][j] = matrix[j * 4 + i];
			}
		}
	}

	/**
	 * 
	 * <code>set</code> defines the values of the matrix based on a supplied
	 * <code>Quaternion</code>. It should be noted that all previous values
	 * will be overridden.
	 * @param quat the quaternion to create a rotational matrix from.
	 */
	public void set(Quaternion quaternion) {
		loadIdentity();
		matrix[0][0] =
			(float) (1.0
				- 2.0 * quaternion.y * quaternion.y
				- 2.0 * quaternion.z * quaternion.z);
		matrix[1][0] =
			(float) (2.0 * quaternion.x * quaternion.y
				+ 2.0 * quaternion.w * quaternion.z);
		matrix[2][0] =
			(float) (2.0 * quaternion.x * quaternion.z
				- 2.0 * quaternion.w * quaternion.y);

		matrix[0][1] =
			(float) (2.0 * quaternion.x * quaternion.y
				- 2.0 * quaternion.w * quaternion.z);
		matrix[1][1] =
			(float) (1.0
				- 2.0 * quaternion.x * quaternion.x
				- 2.0 * quaternion.z * quaternion.z);
		matrix[2][1] =
			(float) (2.0 * quaternion.y * quaternion.z
				+ 2.0 * quaternion.w * quaternion.x);

		matrix[0][2] =
			(float) (2.0 * quaternion.x * quaternion.z
				+ 2.0 * quaternion.w * quaternion.y);
		matrix[1][2] =
			(float) (2.0 * quaternion.y * quaternion.z
				- 2.0 * quaternion.w * quaternion.x);
		matrix[2][2] =
			(float) (1.0
				- 2.0 * quaternion.x * quaternion.x
				- 2.0 * quaternion.y * quaternion.y);

	}

	/**
	 * <code>loadIdentity</code> sets this matrix to the identity matrix, 
	 * namely all zeros with ones along the diagonal.
	 *
	 */
	public void loadIdentity() {
		matrix = new float[4][4];
		matrix[0][0] = matrix[1][1] = matrix[2][2] = matrix[3][3] = 1;
	}

	/**
	 * <code>multiply</code> multiplies this matrix by a scalar.
	 * @param scalar the scalar to multiply this matrix by.
	 */
	public void multiply(float scalar) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				matrix[i][j] *= scalar;
			}
		}
	}

	/**
	 * <code>multiply</code> multiplies this matrix with another matrix. The
	 * result matrix will then be returned.
	 * This matrix will be on the left hand side, while the parameter matrix
	 * will be on the right.
	 * @param in2 the matrix to multiply this matrix by.
	 * @return the resultant matrix
	 * @throws MonkeyRuntimeException if matrix is null.
	 */
	public Matrix4f mult(Matrix4f in2) {
		Matrix4f out = new Matrix4f();
		out.matrix[0][0] =
			matrix[0][0] * in2.matrix[0][0]
				+ matrix[0][1] * in2.matrix[1][0]
				+ matrix[0][2] * in2.matrix[2][0];
		out.matrix[0][1] =
			matrix[0][0] * in2.matrix[0][1]
				+ matrix[0][1] * in2.matrix[1][1]
				+ matrix[0][2] * in2.matrix[2][1];
		out.matrix[0][2] =
			matrix[0][0] * in2.matrix[0][2]
				+ matrix[0][1] * in2.matrix[1][2]
				+ matrix[0][2] * in2.matrix[2][2];
		out.matrix[0][3] =
			matrix[0][0] * in2.matrix[0][3]
				+ matrix[0][1] * in2.matrix[1][3]
				+ matrix[0][2] * in2.matrix[2][3]
				+ matrix[0][3];
		out.matrix[1][0] =
			matrix[1][0] * in2.matrix[0][0]
				+ matrix[1][1] * in2.matrix[1][0]
				+ matrix[1][2] * in2.matrix[2][0];
		out.matrix[1][1] =
			matrix[1][0] * in2.matrix[0][1]
				+ matrix[1][1] * in2.matrix[1][1]
				+ matrix[1][2] * in2.matrix[2][1];
		out.matrix[1][2] =
			matrix[1][0] * in2.matrix[0][2]
				+ matrix[1][1] * in2.matrix[1][2]
				+ matrix[1][2] * in2.matrix[2][2];
		out.matrix[1][3] =
			matrix[1][0] * in2.matrix[0][3]
				+ matrix[1][1] * in2.matrix[1][3]
				+ matrix[1][2] * in2.matrix[2][3]
				+ matrix[1][3];
		out.matrix[2][0] =
			matrix[2][0] * in2.matrix[0][0]
				+ matrix[2][1] * in2.matrix[1][0]
				+ matrix[2][2] * in2.matrix[2][0];
		out.matrix[2][1] =
			matrix[2][0] * in2.matrix[0][1]
				+ matrix[2][1] * in2.matrix[1][1]
				+ matrix[2][2] * in2.matrix[2][1];
		out.matrix[2][2] =
			matrix[2][0] * in2.matrix[0][2]
				+ matrix[2][1] * in2.matrix[1][2]
				+ matrix[2][2] * in2.matrix[2][2];
		out.matrix[2][3] =
			matrix[2][0] * in2.matrix[0][3]
				+ matrix[2][1] * in2.matrix[1][3]
				+ matrix[2][2] * in2.matrix[2][3]
				+ matrix[2][3];
		out.matrix[3][0] =
			this.matrix[0][0] * in2.get(3,0)
				+ this.matrix[1][0] * in2.get(3,1)
				+ this.matrix[2][0] * in2.get(3,2)
				+ this.matrix[3][0];
		out.matrix[3][1] =
			this.matrix[0][1] * in2.get(3,0)
				+ this.matrix[1][1] * in2.get(3,1)
				+ this.matrix[2][1] * in2.get(3,2)
				+ this.matrix[3][1];
		out.matrix[3][2] =
			this.matrix[0][2] * in2.get(3,0)
				+ this.matrix[1][2] * in2.get(3,1)
				+ this.matrix[2][2] * in2.get(3,2)
				+ this.matrix[3][2];
		out.matrix[3][3] = 1;
		return out;
	}

	/**
	 * <code>rotate</code> rotates a vector about a rotation matrix. The
	 * resulting vector is returned.
	 * @param m the rotation matrix.
	 * @return the rotated vector.
	 */
	public Vector3f rotate(Vector3f v) {
		Vector3f out = new Vector3f();
		out.x = v.dot(new Vector3f(matrix[0][0], matrix[0][1], matrix[0][2]));
		out.y = v.dot(new Vector3f(matrix[1][0], matrix[1][1], matrix[1][2]));
		out.z = v.dot(new Vector3f(matrix[2][0], matrix[2][1], matrix[2][2]));
		return out;
	}

	/**
	 * <code>add</code> adds the values of a parameter matrix to this matrix.
	 * @param matrix the matrix to add to this.
	 */
	public void add(Matrix4f matrix) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				this.matrix[i][j] += matrix.get(i,j);
			}
		}
	}

	/**
	 * <code>setTranslation</code> will set the matrix's translation values.
	 * @param translation the new values for the translation.
	 * @throws MonkeyRuntimeException if translation is not size 3.
	 */
	public void setTranslation(float[] translation) {
		if (translation.length != 3) {
			throw new JmeException("Translation size must be 3.");
		}
		matrix[3][0] = translation[0];
		matrix[3][1] = translation[1];
		matrix[3][2] = translation[2];
	}

	/**
	 * <code>setInverseTranslation</code> will set the matrix's inverse 
	 * translation values.
	 * @param translation the new values for the inverse translation.
	 * @throws MonkeyRuntimeException if translation is not size 3.
	 */
	public void setInverseTranslation(float[] translation) {
		if (translation.length != 3) {
			throw new JmeException("Translation size must be 3.");
		}
		matrix[3][0] = -translation[0];
		matrix[3][1] = -translation[1];
		matrix[3][2] = -translation[2];
	}

	/**
	 * <code>angleRotation</code> sets this matrix to that
	 * of a rotation about three axes (x, y, z). Where each
	 * axis has a specified rotation in degrees. These rotations
	 * are expressed in a single <code>Vector3f</code> object.
	 * @param angles the angles to rotate.
	 */
	public void angleRotation(Vector3f angles) {
		float angle;
		float sr, sp, sy, cr, cp, cy;

		angle = (float) (angles.z * (Math.PI * 2 / 360));
		sy = (float) java.lang.Math.sin(angle);
		cy = (float) java.lang.Math.cos(angle);
		angle = (float) (angles.y * (Math.PI * 2 / 360));
		sp = (float) java.lang.Math.sin(angle);
		cp = (float) java.lang.Math.cos(angle);
		angle = (float) (angles.x * (Math.PI * 2 / 360));
		sr = (float) java.lang.Math.sin(angle);
		cr = (float) java.lang.Math.cos(angle);

		// matrix = (Z * Y) * X
		matrix[0][0] = cp * cy;
		matrix[1][0] = cp * sy;
		matrix[2][0] = -sp;
		matrix[0][1] = sr * sp * cy + cr * -sy;
		matrix[1][1] = sr * sp * sy + cr * cy;
		matrix[2][1] = sr * cp;
		matrix[0][2] = (cr * sp * cy + -sr * -sy);
		matrix[1][2] = (cr * sp * sy + -sr * cy);
		matrix[2][2] = cr * cp;
		matrix[0][3] = 0.0f;
		matrix[1][3] = 0.0f;
		matrix[2][3] = 0.0f;
	}

	/**
	 * <code>setRotationQuaternion</code> builds a rotation from a 
	 * <code>Quaternion</code>.
	 * @param quat the quaternion to build the rotation from.
	 * @throws MonkeyRuntimeException if quat is null.
	 */
	public void setRotationQuaternion(Quaternion quat) {
		if (null == quat) {
			throw new JmeException("Quat may not be null.");
		}
		matrix[0][0] =
			(float) (1.0 - 2.0 * quat.y * quat.y - 2.0 * quat.z * quat.z);
		matrix[0][1] = (float) (2.0 * quat.x * quat.y + 2.0 * quat.w * quat.z);
		matrix[0][2] = (float) (2.0 * quat.x * quat.z - 2.0 * quat.w * quat.y);

		matrix[1][0] = (float) (2.0 * quat.x * quat.y - 2.0 * quat.w * quat.z);
		matrix[1][1] =
			(float) (1.0 - 2.0 * quat.x * quat.x - 2.0 * quat.z * quat.z);
		matrix[1][2] = (float) (2.0 * quat.y * quat.z + 2.0 * quat.w * quat.x);

		matrix[2][0] = (float) (2.0 * quat.x * quat.z + 2.0 * quat.w * quat.y);
		matrix[2][1] = (float) (2.0 * quat.y * quat.z - 2.0 * quat.w * quat.x);
		matrix[2][2] =
			(float) (1.0 - 2.0 * quat.x * quat.x - 2.0 * quat.y * quat.y);
	}

	/**
	 * <code>setInverseRotationRadians</code> builds an inverted rotation
	 * from Euler angles that are in radians.
	 * @param angles the Euler angles in radians.
	 * @throws JmeException if angles is not size 3.
	 */
	public void setInverseRotationRadians(float[] angles) {
		if (angles.length != 3) {
			throw new JmeException("Angles must be of size 3.");
		}
		double cr = Math.cos(angles[0]);
		double sr = Math.sin(angles[0]);
		double cp = Math.cos(angles[1]);
		double sp = Math.sin(angles[1]);
		double cy = Math.cos(angles[2]);
		double sy = Math.sin(angles[2]);

		matrix[0][0] = (float) (cp * cy);
		matrix[1][0] = (float) (cp * sy);
		matrix[2][0] = (float) (-sp);

		double srsp = sr * sp;
		double crsp = cr * sp;

		matrix[0][1] = (float) (srsp * cy - cr * sy);
		matrix[1][1] = (float) (srsp * sy + cr * cy);
		matrix[2][1] = (float) (sr * cp);

		matrix[0][2] = (float) (crsp * cy + sr * sy);
		matrix[1][2] = (float) (crsp * sy - sr * cy);
		matrix[2][2] = (float) (cr * cp);
	}

	/**
	 * <code>setInverseRotationDegrees</code> builds an inverted rotation
	 * from Euler angles that are in degrees.
	 * @param angles the Euler angles in degrees.
	 * @throws JmeException if angles is not size 3.
	 */
	public void setInverseRotationDegrees(float[] angles) {
		if (angles.length != 3) {
			throw new JmeException("Angles must be of size 3.");
		}
		float vec[] = new float[3];
		vec[0] = (float) (angles[0] * 180.0 / Math.PI);
		vec[1] = (float) (angles[1] * 180.0 / Math.PI);
		vec[2] = (float) (angles[2] * 180.0 / Math.PI);
		setInverseRotationRadians(vec);
	}

	/**
	 * 
	 * <code>inverseTranslateVect</code> translates a given Vector3f by the
	 * translation part of this matrix.
	 * @param Vector3f the Vector3f to be translated.
	 * @throws JmeException if the size of the Vector3f is not 3.
	 */
	public void inverseTranslateVect(float[] Vector3f) {
		if (Vector3f.length != 3) {
			throw new JmeException("Vector3f must be of size 3.");
		}

		Vector3f[0] = Vector3f[0] - matrix[3][0];
		Vector3f[1] = Vector3f[1] - matrix[3][1];
		Vector3f[2] = Vector3f[2] - matrix[3][2];
	}

	/**
	 * 
	 * <code>inverseRotateVect</code> rotates a given Vector3f by the rotation
	 * part of this matrix.
	 * @param Vector3f the Vector3f to be rotated.
	 * @throws JmeException if the size of the Vector3f is not 3.
	 */
	public void inverseRotateVect(float[] vec) {
		if (vec.length != 3) {
			throw new JmeException("Vector3f must be of size 3.");
		}

		vec[0] =
			vec[0] * matrix[0][0]
				+ vec[1] * matrix[0][1]
				+ vec[2] * matrix[0][2];
		vec[1] =
			vec[0] * matrix[1][0]
				+ vec[1] * matrix[1][1]
				+ vec[2] * matrix[1][2];
		vec[2] =
			vec[0] * matrix[2][0]
				+ vec[1] * matrix[2][1]
				+ vec[2] * matrix[2][2];
	}

	/**
	 * <code>inverseRotate</code> uses the rotational part of
	 * the matrix to rotate a vector in the opposite direction.
	 * @param v the vector to rotate.
	 * @return the rotated vector.
	 */
	public Vector3f inverseRotate(Vector3f v) {
		Vector3f out = new Vector3f();
		out.x = v.x * matrix[0][0] + v.y * matrix[1][0] + v.z * matrix[2][0];
		out.y = v.x * matrix[0][1] + v.y * matrix[1][1] + v.z * matrix[2][1];
		out.z = v.x * matrix[0][2] + v.y * matrix[1][2] + v.z * matrix[2][2];
		return out;
	}

	
	/**
	 * <code>toString</code> returns the string representation of this object.
	 * It is in a format of a 4x4 matrix. For example, an identity matrix would
	 * be represented by the following string.
	 * com.jme.math.Matrix3f<br>
	 * [<br>
	 *   1.0  0.0  0.0 0.0<br>
	 *   0.0  1.0  0.0 0.0<br>
	 *   0.0  0.0  1.0 0.0<br>
	 *   0.0  0.0  0.0 1.0 <br>
	 * ]<br>
	 * 
	 * @return the string representation of this object.
	 */
	public String toString() {
		String result = "com.jme.math.Matrix4f\n[\n";
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				result += " " + matrix[i][j] + " ";
			}
			result += "\n";
		}
		result += "]";
		return result;
	}
}
