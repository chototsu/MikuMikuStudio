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
 */
public class Vector {
	public float x;
	public float y;
	public float z;
	
	public Vector() {
		x = 0;
		y = 0;
		z = 0;
	}
	
	public Vector(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector(float[] attributes) {
		if(attributes.length != 3) {
			throw new MonkeyRuntimeException("Attributes must be length 3.");
		}
		
		this.x = attributes[0];
		this.y = attributes[1];
		this.z = attributes[2];
	}
	
	public Vector(Vector v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}
	
	public Vector divide(float scalar) {
		return new Vector(x/scalar, y/scalar, z/scalar);
	}
	
	public Vector mult(float scalar) {
		return new Vector(x * scalar, y * scalar, z * scalar);
	}
	
	public Vector add(Vector v) {
		return new Vector(x + v.x, y + v.y, z + v.z);
	}
	
	public Vector subtract(Vector v) {
		return new Vector(x - v.x, y - v.y, z - v.z);
	}
	
	public float length() {
		return (float)Math.sqrt(lengthSquared());
	}
	
	public float lengthSquared() {
		return x*x + y*y + z*z;
	}
	
	public float dot(Vector v) {
		return 0;
	}
	
	public void negate() {
		x = -x;
		y = -y;
		z = -z;
	}
	
}
