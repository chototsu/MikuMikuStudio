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
 */
public class Quaternion {
	public float x, y, z, w;

	public Quaternion() {
		x = 0;
		y = 0;
		z = 0;
		w = 0;
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
	 * <code>fromMatrix</code> creates a quaternion from the rotational 
	 * matrix. A quaternion can be created from a 3x3 or 4x4 matrix. 
	 * @param matrix
	 * @param rowColumnCount
	 */
	public void fromMatrix(float[] matrix, int rowColumnCount) {
		if (matrix == null || (rowColumnCount != 3) && (rowColumnCount != 4)) {
			throw new MonkeyRuntimeException(
				"matrix cannot be null, while"
					+ "rowColumnCount must be 3 or 4.");
		}
		// This function is used to take in a 3x3 or 4x4 matrix and convert the matrix
		// to a quaternion.  If rowColumnCount is a 3, then we need to convert the 3x3
		// matrix passed in to a 4x4 matrix, otherwise we just leave the matrix how it is.
		// Since we want to apply a matrix to an OpenGL matrix, we need it to be 4x4.

		// Point the matrix pointer to the matrix passed in, assuming it's a 4x4 matrix
		float[] pMatrix = matrix;

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
			pMatrix = m4x4;
		}

		// The next step, once we made sure we are dealing with a 4x4 matrix, is to check the
		// diagonal of the matrix.  This means that we add up all of the indices that comprise
		// the standard 1's in the identity matrix.  If you draw out the identity matrix of a
		// 4x4 matrix, you will see that they 1's form a diagonal line.  Notice we just assume
		// that the last index (15) is 1 because it is not effected in the 3x3 rotation matrix.

		// Find the diagonal of the matrix by adding up it's diagonal indices.
		// This is also known as the "trace", but I will call the variable diagonal.
		float diagonal = pMatrix[0] + pMatrix[5] + pMatrix[10] + 1;
		float scale = 0.0f;

		// Below we check if the diagonal is greater than zero.  To avoid accidents with
		// floating point numbers, we substitute 0 with 0.00000001.  If the diagonal is
		// great than zero, we can perform an "instant" calculation, otherwise we will need
		// to identify which diagonal element has the greatest value.  Note, that it appears
		// that %99 of the time, the diagonal IS greater than 0 so the rest is rarely used.

		// If the diagonal is greater than zero
		if (diagonal > 0.00000001f) {
			// Calculate the scale of the diagonal
			scale = (float) Math.sqrt(diagonal) * 2f;

			// Calculate the x, y, z and w of the quaternion through the respective equation
			x = (pMatrix[9] - pMatrix[6]) / scale;
			y = (pMatrix[2] - pMatrix[8]) / scale;
			z = (pMatrix[4] - pMatrix[1]) / scale;
			w = 0.25f * scale;
		} else {
			// If the first element of the diagonal is the greatest value
			if (pMatrix[0] > pMatrix[5] && pMatrix[0] > pMatrix[10]) {
				// Find the scale according to the first element, and double that value
				scale =
					(float) Math.sqrt(
						1.0f + pMatrix[0] - pMatrix[5] - pMatrix[10])
						* 2.0f;

				// Calculate the x, y, z and w of the quaternion through the respective equation
				x = 0.25f * scale;
				y = (pMatrix[4] + pMatrix[1]) / scale;
				z = (pMatrix[2] + pMatrix[8]) / scale;
				w = (pMatrix[9] - pMatrix[6]) / scale;
			}
			// Else if the second element of the diagonal is the greatest value
			else if (pMatrix[5] > pMatrix[10]) {
				// Find the scale according to the second element, and double that value
				scale =
					(float) Math.sqrt(
						1.0f + pMatrix[5] - pMatrix[0] - pMatrix[10])
						* 2.0f;

				// Calculate the x, y, z and w of the quaternion through the respective equation
				x = (pMatrix[4] + pMatrix[1]) / scale;
				y = 0.25f * scale;
				z = (pMatrix[9] + pMatrix[6]) / scale;
				w = (pMatrix[2] - pMatrix[8]) / scale;
			}
			// Else the third element of the diagonal is the greatest value
			else {
				// Find the scale according to the third element, and double that value
				scale =
					(float) Math.sqrt(
						1.0f + pMatrix[10] - pMatrix[0] - pMatrix[5])
						* 2.0f;

				// Calculate the x, y, z and w of the quaternion through the respective equation
				x = (pMatrix[2] + pMatrix[8]) / scale;
				y = (pMatrix[9] + pMatrix[6]) / scale;
				z = 0.25f * scale;
				w = (pMatrix[4] - pMatrix[1]) / scale;
			}
		}

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

	public float[] getMatrix() {
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
	 * <code>inverse</code> inverts the values of the quaternion.
	 *
	 */
	public void inverse() {
		x *= -1;
		y *= -1;
		z *= -1;
		w *= -1;
	}
}
