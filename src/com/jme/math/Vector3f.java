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

import com.jme.util.LoggingSystem;

/**
 * <code>Vector3f</code> defines a Vector for a three float value tuple.
 * @author Mark Powell
 * @author Joshua Slack -- Added *Local methods to cut down on object creation
 * @version $Id: Vector3f.java,v 1.13 2004-03-28 17:04:09 greggpatton Exp $
 */
public class Vector3f {
    /**
     * the x value of the vector.
     */
    public float x;
    /**
     * the y value of the vector.
     */
    public float y;
    /**
     * the z value of the vector.
     */
    public float z;

    /**
     * Constructor instantiates a new <code>Vector3f</code> with
     * default values of (0,0,0).
     *
     */
    public Vector3f() {
        x = y = z = 0;
    }

    /**
     * Constructor instantiates a new <code>Vector3f</code> with provides
     * values.
     * @param x the x value of the vector.
     * @param y the y value of the vector.
     * @param z the z value of the vector.
     */
    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * set the x,y,z values of the vector
     * @param x the x value of the vector.
     * @param y the y value of the vector.
     * @param z the z value of the vector.
     * @return this vector
     */
    public Vector3f set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
     * set the x,y,z values of the vector by copying the supplied vector
     * @param vect the vector to copy.
     * @return this vector
     */
    public Vector3f set(Vector3f vect) {
        this.x = vect.x;
        this.y = vect.y;
        this.z = vect.z;
        return this;
    }

    /**
     *
     * <code>add</code> adds a provided vector to this vector creating a
     * resultant vector which is returned. If the provided vector is null,
     * null is returned.
     * @param vec the vector to add to this.
     * @return the resultant vector.
     */
    public Vector3f add(Vector3f vec) {
        if(null == vec) {
            LoggingSystem.getLogger().log(Level.WARNING, "Provided vector is " +
                "null, null returned.");
            return null;
        }
        return new Vector3f(x + vec.x, y + vec.y, z + vec.z);
    }

    /**
     * <code>addLocal</code> adds a provided vector to this vector internally,
     * and returns a handle to this vector for easy chaining of calls.
     * If the provided vector is null, null is returned.
     * @param vec the vector to add to this vector.
     * @return this
     */
    public Vector3f addLocal(Vector3f vec) {
        if(null == vec) {
            LoggingSystem.getLogger().log(Level.WARNING, "Provided vector is " +
                "null, null returned.");
            return null;
        }
        x += vec.x;
        y += vec.y;
        z += vec.z;
        return this;
    }

    /**
     * <code>addLocal</code> adds the provided values to this vector internally,
     * and returns a handle to this vector for easy chaining of calls.
      * @param addX value to add to x
      * @param addY value to add to y
      * @param addZ value to add to z
     * @return this
     */
    public Vector3f addLocal(float addX, float addY, float addZ) {
        x += addX;
        y += addY;
        z += addZ;
        return this;
    }

    /**
     *
     * <code>add</code>
     * @param vec the vector to add to this
     * @param result the vector to store the result in
     * @return result
     */
    public Vector3f add(Vector3f vec, Vector3f result) {
        result.x = x + vec.x;
        result.y = y + vec.y;
        result.z = z + vec.z;
        return result;
    }

    /**
     *
     * <code>dot</code> calculates the dot product of this vector with a
     * provided vector. If the provided vector is null, 0 is returned.
     * @param vec the vector to dot with this vector.
     * @return the resultant dot product of this vector and a given vector.
     */
    public float dot(Vector3f vec) {
        if(null == vec) {
            LoggingSystem.getLogger().log(Level.WARNING, "Provided vector is " +
                "null, 0 returned.");
            return 0;
        }
        return x*vec.x + y*vec.y + z*vec.z;
    }

    /**
     * <code>cross</code> calculates the cross product of this vector
     * with a parameter vector v.
     * @param v the vector to take the cross product of with this.
     * @return the cross product vector.
     */
    public Vector3f cross(Vector3f v) {
        return new Vector3f(
            ((y * v.z) - (z * v.y)),
            ((z * v.x) - (x * v.z)),
            ((x * v.y) - (y * v.x)));
    }

    /**
     * <code>crossLocal</code> calculates the cross product of this vector
     * with a parameter vector v.
     * @param v the vector to take the cross product of with this.
     * @return this.
     */
    public Vector3f crossLocal(Vector3f v) {
        float tempx, tempy;
        tempx = (y * v.z) - (z * v.y);
        tempy = (z * v.x) - (x * v.z);
        z = (x * v.y) - (y * v.x);
        x = tempx; y = tempy;
        return this;
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
     * <code>distanceSquared</code> calculates the distance squared between this vector and vector v.
     * @param v
     * @return
     */
    public float distanceSquared(Vector3f v) {
        double dx = x - v.x;
        double dy = y - v.y;
        double dz = z - v.z;
        return (float) (dx * dx + dy * dy + dz * dz);
    }

    /**
     * <code>distance</code> calculates the distance between this vector and vector v.
     * @param v
     * @return
     */
    public float distance(Vector3f v) {
        return (float) Math.sqrt(distanceSquared(v));
    }

    /**
     *
     * <code>scaleAdd</code> multiplies this vector by a scalar then adds the given Vector3f.
     * @param scalar the value to multiply this vector by.
     * @param add the value to add
     */
    public void scaleAdd(float scalar, Vector3f add) {
      x = x*scalar + add.x;
      y = y*scalar + add.y;
      z = z*scalar + add.z;
    }

    /**
     *
     * <code>scaleAdd</code> multiplies the given vector by a scalar then adds the given vector.
     * @param scalar the value to multiply this vector by.
     * @param mult the value to multiply the scalar by
     * @param add the value to add
     */
    public void scaleAdd(float scalar, Vector3f mult, Vector3f add) {
      this.x = mult.x*scalar + add.x;
      this.y = mult.y*scalar + add.y;
      this.z = mult.z*scalar + add.z;
    }

    /**
     *
     * <code>mult</code> multiplies this vector by a scalar. The resultant
     * vector is returned.
     * @param scalar the value to multiply this vector by.
     * @return the new vector.
     */
    public Vector3f mult(float scalar) {
        return new Vector3f(x*scalar, y*scalar, z*scalar);
    }

    /**
     * <code>multLocal</code> multiplies this vector by a scalar internally,
     * and returns a handle to this vector for easy chaining of calls.
     * @param scalar the value to multiply this vector by.
     * @return this
     */
    public Vector3f multLocal(float scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }

    /**
     * <code>multLocal</code> multiplies a provided vector to this vector internally,
     * and returns a handle to this vector for easy chaining of calls.
     * If the provided vector is null, null is returned.
     * @param vec the vector to mult to this vector.
     * @return this
     */
    public Vector3f multLocal(Vector3f vec) {
        if(null == vec) {
            LoggingSystem.getLogger().log(Level.WARNING, "Provided vector is " +
                "null, null returned.");
            return null;
        }
        x *= vec.x;
        y *= vec.y;
        z *= vec.z;
        return this;
    }

    public void mult(float scalar, Vector3f product) {
        if(null == product) {
            product = new Vector3f();
        }

        product.x = x * scalar;
        product.y = y * scalar;
        product.z = z * scalar;
    }

    /**
     * <code>divide</code> divides the values of this vector by a
     * scalar and returns the result. The values of this vector
     * remain untouched.
     * @param scalar the value to divide this vectors attributes by.
     * @return the result <code>Vector</code>.
     */
    public Vector3f divide(float scalar) {
        return new Vector3f(x / scalar, y / scalar, z / scalar);
    }

    /**
     * <code>divideLocal</code> divides this vector by a scalar internally,
     * and returns a handle to this vector for easy chaining of calls.
     * Dividing by zero will result in an exception.
     * @param scalar the value to divides this vector by.
     * @return this
     */
    public Vector3f divideLocal(float scalar) {
        x /= scalar;
        y /= scalar;
        z /= scalar;
        return this;
    }

    /**
     *
     * <code>negate</code> returns the negative of this vector. All values are
     * negated and set to a new vector.
     * @return the negated vector.
     */
    public Vector3f negate() {
        return new Vector3f(-x,-y,-z);
    }

    /**
     *
     * <code>negateLocal</code> negates the internal values of this vector.
     * @return this.
     */
    public Vector3f negateLocal() {
        x= -x;
        y= -y;
        z= -z;
        return this;
    }

    /**
     *
     * <code>subtract</code> subtracts the values of a given vector from those
     * of this vector creating a new vector object. If the provided vector
     * is null, null is returned.
     * @param vec the vector to subtract from this vector.
     * @return the result vector.
     */
    public Vector3f subtract(Vector3f vec) {
        return new Vector3f(x - vec.x, y - vec.y, z - vec.z);
    }


    /**
     * <code>subtractLocal</code> subtracts a provided vector to this vector internally,
     * and returns a handle to this vector for easy chaining of calls.
     * If the provided vector is null, null is returned.
     * @param vec the vector to subtract
     * @return this
     */
    public Vector3f subtractLocal(Vector3f vec) {
        if(null == vec) {
            LoggingSystem.getLogger().log(Level.WARNING, "Provided vector is " +
                "null, null returned.");
            return null;
        }
        x -= vec.x;
        y -= vec.y;
        z -= vec.z;
        return this;
    }

    /**
     *
     * <code>subtract</code>
     * @param vec the vector to subtract from this
     * @param result the vector to store the result in
     * @return result
     */
    public Vector3f subtract(Vector3f vec, Vector3f result) {
        result.x = x - vec.x;
        result.y = y - vec.y;
        result.z = z - vec.z;
        return result;
    }

    /**
     * <code>normalize</code> returns the unit vector of this vector.
     * @return unit vector of this vector.
     */
    public Vector3f normalize() {
        float length = length();
        if(length != 0) {
            return divide(length);
        } else {
            return divide(1);
        }
    }

    /**
     * <code>normalizeLocal</code> makes this vector into a unit vector of itself.
     * @return this.
     */
    public Vector3f normalizeLocal() {
        float length = length();
        if(length != 0) {
            return divideLocal(length);
        } else {
            return divideLocal(1);
        }
    }

    /**
     * <code>zero</code> resets this vector's data to zero internally.
     */
    public void zero() {
        x = y = z = 0;
    }

    /**
     * <code>clone</code> creates a new Vector3f object containing the same
     * data as this one.
     * @return the new Vector3f
     */
    public Object clone() {
        return new Vector3f(x,y,z);
    }


    /**
     * are these two vectors the same?  they are is they both have the same x,y, and z
     * values.
     * @param o the object to compare for equality
     * @return this vector
     */
    public boolean equals(Object o) {
        Vector3f comp = (Vector3f)o;
        if (x != comp.x) return false;
        if (y != comp.y) return false;
        if (z != comp.z) return false;
        return true;
    }


    /**
     * <code>toString</code> returns the string representation of this
     * vector. The format is:
     *
     * org.jme.math.Vector3f [X=XX.XXXX, Y=YY.YYYY, Z=ZZ.ZZZZ]
     *
     * @return the string representation of this vector.
     */
    public String toString() {
        return "org.jme.math.Vector3f [X="+x+", Y="+y+", Z="+z+"]";
    }
}
