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
 * <code>Quaternion</code> defines a single example of a more general class of
 * hypercomplex numbers. Quaternions extends a rotation in three dimensions to
 * a rotation in four dimensions. This avoids "gimbal lock" and allows for
 * smooth continuous rotation.
 * 
 * <code>Quaternion</code> is defined by four floating point numbers:
 * {x y z w}.
 * 
 * @author Mark Powell
 * @version $Id: Quaternion.java,v 1.5 2003-08-22 02:26:48 mojomonkey Exp $
 */
public class Quaternion {
	public float x, y, z, w;

	/**
	 * Constructor instantiates a new <code>Quaternion</code> object 
	 * initializing all values to zero.
	 *
	 */
	public Quaternion() {
		x = 0;
		y = 0;
		z = 0;
		w = 0;
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
	 * <code>fromAngleAxis</code> sets this quaternion to the values
	 * specified by an angle and an axis of rotation.
	 * @param angle the angle to rotate.
	 * @param axis the axis of rotation.
	 */
	public void fromAngleAxis(float angle, Vector axis) {
		float halfAngle = 0.5f * angle;
		float sin = (float) Math.sin(halfAngle);
		w = (float) Math.cos(halfAngle);
		x = sin * axis.x;
		y = sin * axis.y;
		z = sin * axis.z;
	}

	/**
	 * <code>fromMatrix</code> creates a quaternion from the rotational 
	 * matrix. A quaternion can be created from a 3x3 or 4x4 matrix. If
	 * a 3x3 matrix is passed, it will be converted into a 4x4.
	 * @param matrix the matrix to generate the quaternion from.
	 * @param rowColumnCount the number of rows/colums (3 or 4).
	 */
	public void fromMatrix(float[] matrix, int rowColumnCount) {
		if (matrix == null || (rowColumnCount != 3) && (rowColumnCount != 4)) {
			throw new MonkeyRuntimeException(
				"matrix cannot be null, while"
					+ "rowColumnCount must be 3 or 4.");
		}

		// Point the matrix pointer to the matrix passed in, assuming it's a 4x4 matrix
		float[] tempMatrix = matrix;

		// Create a 4x4 matrix to convert a 3x3 matrix to a 4x4 matrix (If rowColumnCount == 3)
		float[] m4x4 = new float[16];

		// If the matrix is a 3x3 matrix (which it is for Quake3), then convert it to a 4x4
		if (matrix.length == 9) {
			// Set the 9 top left indices of the 4x4 matrix to the 9 indices in the 3x3 matrix.
			// It would be a good idea to actually draw this out so you can visualize it.
			m4x4[0] = matrix[0];
			m4x4[1] = matrix[1];
			m4x4[2] = matrix[2];
			m4x4[3] = 0f;
			m4x4[4] = matrix[3];
			m4x4[5] = matrix[4];
			m4x4[6] = matrix[5];
			m4x4[7] = 0f;
			m4x4[8] = matrix[6];
			m4x4[9] = matrix[7];
			m4x4[10] = matrix[8];
			m4x4[11] = 0f;

			// Since the bottom and far right indices are zero, set the bottom right corner to 1.
			// This is so that it follows the standard diagonal line of 1's in the identity matrix.
			m4x4[12] = 0f;
			m4x4[13] = 0f;
			m4x4[14] = 0f;
			m4x4[15] = 1;

			// Set the matrix pointer to the first index in the newly converted matrix
			tempMatrix = m4x4;
		}

		//calculate the trace of the matrix.
		float diagonal = tempMatrix[0] + tempMatrix[5] + tempMatrix[10] + 1;
		float scale = 0.0f;

		// If the diagonal is greater than zero
		if (diagonal > 0.00000001f) {
			// Calculate the scale of the diagonal
			scale = (float) Math.sqrt(diagonal) * 2f;

			// Calculate the x, y, z and w of the quaternion through the respective equation
			x = (tempMatrix[9] - tempMatrix[6]) / scale;
			y = (tempMatrix[2] - tempMatrix[8]) / scale;
			z = (tempMatrix[4] - tempMatrix[1]) / scale;
			w = 0.25f * scale;
		} else {
			// If the first element of the diagonal is the greatest value
			if (tempMatrix[0] > tempMatrix[5]
				&& tempMatrix[0] > tempMatrix[10]) {
				// Find the scale according to the first element, and double that value
				scale =
					(float) Math.sqrt(
						1.0f + tempMatrix[0] - tempMatrix[5] - tempMatrix[10])
						* 2.0f;

				// Calculate the x, y, z and w of the quaternion through the respective equation
				x = 0.25f * scale;
				y = (tempMatrix[4] + tempMatrix[1]) / scale;
				z = (tempMatrix[2] + tempMatrix[8]) / scale;
				w = (tempMatrix[9] - tempMatrix[6]) / scale;
			}
			// Else if the second element of the diagonal is the greatest value
			else if (tempMatrix[5] > tempMatrix[10]) {
				// Find the scale according to the second element, and double that value
				scale =
					(float) Math.sqrt(
						1.0f + tempMatrix[5] - tempMatrix[0] - tempMatrix[10])
						* 2.0f;

				// Calculate the x, y, z and w of the quaternion through the respective equation
				x = (tempMatrix[4] + tempMatrix[1]) / scale;
				y = 0.25f * scale;
				z = (tempMatrix[9] + tempMatrix[6]) / scale;
				w = (tempMatrix[2] - tempMatrix[8]) / scale;
			}
			// Else the third element of the diagonal is the greatest value
			else {
				// Find the scale according to the third element, and double that value
				scale =
					(float) Math.sqrt(
						1.0f + tempMatrix[10] - tempMatrix[0] - tempMatrix[5])
						* 2.0f;

				// Calculate the x, y, z and w of the quaternion through the respective equation
				x = (tempMatrix[2] + tempMatrix[8]) / scale;
				y = (tempMatrix[9] + tempMatrix[6]) / scale;
				z = 0.25f * scale;
				w = (tempMatrix[4] - tempMatrix[1]) / scale;
			}
		}

	}

	/**
	 * <code>getMatrix</code> converts the values of this quaternion into
	 * a 4x4 matrix. 
	 * @return the matrix representation of this quaternion.
	 */
	public float[] toMatrix() {
		float[] matrix = new float[16];
		matrix[0] = 1.0f - 2.0f * (y * y + z * z);
		matrix[1] = 2.0f * (x * y - w * z);
		matrix[2] = 2.0f * (x * z + w * y);
		matrix[3] = 0.0f;

		// Second row
		matrix[4] = 2.0f * (x * y + w * z);
		matrix[5] = 1.0f - 2.0f * (x * x + z * z);
		matrix[6] = 2.0f * (y * z - w * x);
		matrix[7] = 0.0f;

		// Third row
		matrix[8] = 2.0f * (x * z - w * y);
		matrix[9] = 2.0f * (y * z + w * x);
		matrix[10] = 1.0f - 2.0f * (x * x + y * y);
		matrix[11] = 0.0f;

		// Fourth row
		matrix[12] = 0;
		matrix[13] = 0;
		matrix[14] = 0;
		matrix[15] = 1.0f;

		return matrix;
	}

	/**
	 * <code>toAngleAxis</code> sets a given angle and axis to that
	 * represented by the current quaternion. The values are stored 
     * as following: The axis is provided as a parameter and built
     * by the method, the angle is returned as a float.
	 * @param axis the object to contain the axis.
     * @return the angle of rotation.
	 */
	public float toAngleAxis(Vector axis) {
		float sqrLength = x * x + y * y + z * z;
        float angle;
		if (sqrLength > 0.0) {
			angle = (float) (2.0 * Math.cos(w));
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
		return new Quaternion(
			w * q.w - x * q.x - y * q.y - z * q.z,
			w * q.x + x * q.w + y * q.z - z * q.y,
			w * q.y + y * q.w + z * q.x - x * q.z,
			w * q.z + z * q.w + x * q.y - y * q.x);
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
    
    public static void main(String[] args) {
        Quaternion q = new Quaternion(4,1,0,0);
        float angle = 0;
        Vector vec = new Vector();
        angle = q.toAngleAxis(vec);
        System.out.println(angle);
        System.out.println(vec.toString());
    }
}
