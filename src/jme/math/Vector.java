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
 * <code>Vector</code> defines a three dimensional vector of (x,y,z). 
 * @author Mark Powell
 * @version $Id: Vector.java,v 1.8 2003-09-04 21:17:51 mojomonkey Exp $
 */
public class Vector {
	public float x;
	public float y;
	public float z;

	/**
	 * Constructor creates a base <code>Vector</code> with values of
	 * (0, 0, 0).
	 */
	public Vector() {
		x = 0;
		y = 0;
		z = 0;
	}

	/**
	 * Constructor creates a <code>Vector</code> with the value given
	 * in the parameter as (x, y, z).
	 * @param x the x value of the vector.
	 * @param y the y value of the vector.
	 * @param z the z value of the vector.
	 */
	public Vector(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Constructor creates a <code>Vector</code> based on an array of
	 * length three. Where the first position is x, second y, and third
	 * z. 
	 * @param attributes the attributes of the Vector.
	 * @throws MonkeyRuntimeException if the attributes is not length 3.
	 */
	public Vector(float[] attributes) {
		if (attributes.length != 3) {
			throw new MonkeyRuntimeException("Attributes must be length 3.");
		}

		this.x = attributes[0];
		this.y = attributes[1];
		this.z = attributes[2];
	}

	/**
	 * Constructor builds a new <code>Vector</code> as a copy of a 
	 * passed in vector.
	 * @param v the vector to copy.
	 */
	public Vector(Vector v) {
		if (null == v) {
			x = y = z = 0;
		} else {
			this.x = v.x;
			this.y = v.y;
			this.z = v.z;
		}
	}

	/**
	 * <code>divide</code> divides the values of this vector by a 
	 * scalar and returns the result. The values of this vector 
	 * remain untouched.
	 * @param scalar the value to divide this vectors attributes by.
	 * @return the result <code>Vector</code>.
	 */
	public Vector divide(float scalar) {
		return new Vector(x / scalar, y / scalar, z / scalar);
	}

	/**
	 * <code>mult</code> multiplies the values of this vector by a 
	 * scalar and returns the result. The values of this vector 
	 * remain untouched.
	 * @param scalar the value to multiply the vector attributes by.
	 * @return the result </code>Vector</code>.
	 */
	public Vector mult(float scalar) {
		return new Vector(x * scalar, y * scalar, z * scalar);
	}

	/**
	 * <code>add</code> adds the values of this vector by another 
	 * vector and returns the result. The values of this vector 
	 * remain untouched.
	 * @param vector the vector to add the vector attributes by.
	 * @return the result </code>Vector</code>.
	 */
	public Vector add(Vector v) {
		return new Vector(x + v.x, y + v.y, z + v.z);
	}

	/**
	 * <code>subtract</code> subtracts the values of this vector by  
	 * another vector and returns the result. The values of this vector 
	 * remain untouched.
	 * @param vector the vector to subtract from this vector.
	 * @return the result </code>Vector</code>.
	 */
	public Vector subtract(Vector v) {
		return new Vector(x - v.x, y - v.y, z - v.z);
	}

	/**
	 * <code>length</code> calculates the magnitude of this vector.
	 * @return the length or magnitude of the vector.
	 */
	public float length() {
		return (float) Math.sqrt(lengthSquared());
	}

	/**
	 * <code>lengthSquared</code> calculates the squared value of
	 * the magnitude of the vector.
	 * @return the magnitude squared of the vector.
	 */
	public float lengthSquared() {
		return x * x + y * y + z * z;
	}

	/**
	 * <code>dot</code> calculates the dot product of this
	 * vector with the parameter vector.
	 * @param v the vector to use for the dot product with this.
	 * @return the dot product of this vector with the parameter vector.
	 */
	public float dot(Vector v) {
		return x * v.x + y * v.y + z * v.z;
	}

	/**
	 * <code>cross</code> calculates the cross product of this vector
	 * with a parameter vector v.
	 * @param v the vector to take the cross product of with this.
	 * @return the cross product vector.
	 */
	public Vector cross(Vector v) {
		return new Vector(
			((y * v.z) - (z * v.y)),
			((z * v.x) - (x * v.z)),
			((x * v.y) - (y * v.x)));
	}
    
    /**
     * <code>rotate</code> rotates a vector about a rotation matrix. The
     * resulting vector is returned.
     * @param m the rotation matrix.
     * @return the rotated vector.
     */
    public Vector rotate(Matrix m) {
       Vector out = new Vector();
       out.x = dot(new Vector(m.matrix[0][0], m.matrix[0][1], m.matrix[0][2]));
       out.y = dot(new Vector(m.matrix[1][0], m.matrix[1][1], m.matrix[1][2]));
       out.z = dot(new Vector(m.matrix[2][0], m.matrix[2][1], m.matrix[2][2]));
       return out;
    }
    
    public Vector inverseRotate(Matrix m) {
        Vector out = new Vector();
        out.x = x * m.matrix[0][0] + y * m.matrix[1][0] + z * m.matrix[2][0];
        out.y = x * m.matrix[0][1] + y * m.matrix[1][1] + z * m.matrix[2][1];
        out.z = x * m.matrix[0][2] + y * m.matrix[1][2] + z * m.matrix[2][2];
        return out;
    }

	/**
	 * <code>negate</code> sets this vector to the negative (-x, -y, -z).
	 *
	 */
	public void negate() {
		x = -x;
		y = -y;
		z = -z;
	}

	/**
	 * <code>normalize</code> returns the unit vector of this vector.
	 * @return unit vector of this vector.
	 */
	public Vector normalize() {
		float length = length();

		return divide(length);
	}
	
	/**
	 * <code>unitize</code> sets this vector to the unit vector or
	 * direction vector.
	 *
	 */
	public void unitize() {
		float length = length();
			
		float fInvLength = 1.0f/length;
		x *= fInvLength;
		y *= fInvLength;
		z *= fInvLength;
	}

	/**
	 * <code>generateOrthonormalBasis</code> generates a vector that
	 * satisfies u and v are perpendicular in respect to w.
	 * @param u the u vector to be set.
	 * @param v the v vector to be set.
	 * @param w the w vector should already be set.
	 * @param isUnitLength true if w is unitized, false otherwise.
	 */
	public static void generateOrthonormalBasis(
		Vector u,
		Vector v,
		Vector w,
		boolean isUnitLength) {
		if (!isUnitLength) {
			w.unitize();
		}

		if (Math.abs(w.x) >= Math.abs(w.y)
			&& Math.abs(w.x) >= Math.abs(w.z)) {
			u.x = -w.y;
			u.y = +w.x;
			u.z = 0.0f;
		} else {
			u.x = 0.0f;
			u.y = +w.z;
			u.z = -w.y;
		}

		u.unitize();
		v = w.cross(u);
	}

	/**
	 * <code>toString</code> returns the string representation of
	 * this Vector. The format is as follows:
	 * <br><br>
	 * (XXX.XXX, YYY.YYY, ZZZ.ZZZ)
	 * 
	 * @return the string representation of the vector.
	 */
	public String toString() {
		return "(" + x + "," + y + "," + z + ")";
	}

}
