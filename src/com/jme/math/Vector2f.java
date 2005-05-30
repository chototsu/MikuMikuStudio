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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.logging.Level;

import com.jme.util.LoggingSystem;

/**
 * <code>Vector2f</code> defines a Vector for a two float value vector.
 * @author Mark Powell
 * @author Joshua Slack
 * @version $Id: Vector2f.java,v 1.15 2005-05-30 16:19:16 renanse Exp $
 */
public class Vector2f implements Externalizable{
    private static final long serialVersionUID = 1L;
	/**
     * the x value of the vector.
     */
    public float x;
    /**
     * the y value of the vector.
     */
    public float y;

    /**
     * Creates a Vector2f with the given initial x and y values.
     * @param x The x value of this Vector2f.
     * @param y The y value of this Vector2f.
     */
    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a Vector2f with x and y set to 0.  Equivalent to Vector2f(0,0).
     */
    public Vector2f() {
        x=y=0;
    }

    /**
     * Creates a new Vector2f that contains the passed vector's information
     * @param vector2f The vector to copy
     */
    public Vector2f(Vector2f vector2f) {
        this.x=vector2f.x;
        this.y=vector2f.y;
    }

    /**
     * set the x and y values of the vector
     * @param x the x value of the vector.
     * @param y the y value of the vector.
     * @return this vector
     */
    public Vector2f set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * set the x and y values of the vector from another vector
     * @param vec the vector to copy from
     * @return this vector
     */
    public Vector2f set(Vector2f vec) {
        this.x = vec.x;
        this.y = vec.y;
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
    public Vector2f add(Vector2f vec) {
        if(null == vec) {
            LoggingSystem.getLogger().log(Level.WARNING, "Provided vector is " +
                "null, null returned.");
            return null;
        }
        return new Vector2f(x + vec.x, y + vec.y);
    }

    /**
     * <code>addLocal</code> adds a provided vector to this vector internally,
     * and returns a handle to this vector for easy chaining of calls.
     * If the provided vector is null, null is returned.
     * @param vec the vector to add to this vector.
     * @return this
     */
    public Vector2f addLocal(Vector2f vec) {
        if(null == vec) {
            LoggingSystem.getLogger().log(Level.WARNING, "Provided vector is " +
                "null, null returned.");
            return null;
        }
        x += vec.x;
        y += vec.y;
        return this;
    }

    /**
     * <code>addLocal</code> adds the provided values to this vector internally,
     * and returns a handle to this vector for easy chaining of calls.
      * @param addX value to add to x
      * @param addY value to add to y
     * @return this
     */
    public Vector2f addLocal(float addX, float addY) {
        x += addX;
        y += addY;
        return this;
    }

    /**
     * <code>add</code> adds this vector by <code>vec</code> and stores the result in <code>result</code>.
     * @param vec The vector to add.
     * @param result The vector to store the result in.
     * @return The result vector, after adding.
     */
    public Vector2f add(Vector2f vec, Vector2f result) {
        if(null == vec) {
            LoggingSystem.getLogger().log(Level.WARNING, "Provided vector is " +
            "null, null returned.");
            return null;
        }
        if (result==null) result=new Vector2f();
        result.x = x + vec.x;
        result.y = y + vec.y;
        return result;
    }

    /**
     *
     * <code>dot</code> calculates the dot product of this vector with a
     * provided vector. If the provided vector is null, 0 is returned.
     * @param vec the vector to dot with this vector.
     * @return the resultant dot product of this vector and a given vector.
     */
    public float dot(Vector2f vec) {
        if(null == vec) {
            LoggingSystem.getLogger().log(Level.WARNING, "Provided vector is " +
                "null, 0 returned.");
            return 0;
        }
        return x*vec.x + y*vec.y;
    }

    /**
     * <code>cross</code> calculates the cross product of this vector
     * with a parameter vector v.
     * @param v the vector to take the cross product of with this.
     * @return the cross product vector.
     */
    public Vector3f cross(Vector2f v) {
        return new Vector3f(0, 0, ((x * v.y) - (y * v.x)));
    }

    /**
     * Sets this vector to the interpolation by changeAmnt from this to the finalVec
     * this=(1-changeAmnt)*this + changeAmnt * finalVec
     * @param finalVec The final vector to interpolate towards
     * @param changeAmnt An amount between 0.0 - 1.0 representing a precentage
     *  change from this towards finalVec
     */
    public void interpolate(Vector2f finalVec, float changeAmnt) {
        this.x=(1-changeAmnt)*this.x + changeAmnt*finalVec.x;
        this.y=(1-changeAmnt)*this.y + changeAmnt*finalVec.y;
    }

    /**
     * Sets this vector to the interpolation by changeAmnt from beginVec to finalVec
     * this=(1-changeAmnt)*beginVec + changeAmnt * finalVec
     * @param beginVec The begining vector (delta=0)
     * @param finalVec The final vector to interpolate towards (delta=1)
     * @param changeAmnt An amount between 0.0 - 1.0 representing a precentage
     *  change from beginVec towards finalVec
     */
    public void interpolate(Vector2f beginVec,Vector2f finalVec, float changeAmnt) {
        this.x=(1-changeAmnt)*beginVec.x + changeAmnt*finalVec.x;
        this.y=(1-changeAmnt)*beginVec.y + changeAmnt*finalVec.y;
    }

    /**
     * <code>length</code> calculates the magnitude of this vector.
     * @return the length or magnitude of the vector.
     */
    public float length() {
        return FastMath.sqrt(lengthSquared());
    }

    /**
     * <code>lengthSquared</code> calculates the squared value of
     * the magnitude of the vector.
     * @return the magnitude squared of the vector.
     */
    public float lengthSquared() {
        return x * x + y * y;
    }

    /**
     *
     * <code>mult</code> multiplies this vector by a scalar. The resultant
     * vector is returned.
     * @param scalar the value to multiply this vector by.
     * @return the new vector.
     */
    public Vector2f mult(float scalar) {
        return new Vector2f(x*scalar, y*scalar);
    }

    /**
     * <code>multLocal</code> multiplies this vector by a scalar internally,
     * and returns a handle to this vector for easy chaining of calls.
     * @param scalar the value to multiply this vector by.
     * @return this
     */
    public Vector2f multLocal(float scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    /**
     * <code>multLocal</code> multiplies a provided vector to this vector internally,
     * and returns a handle to this vector for easy chaining of calls.
     * If the provided vector is null, null is returned.
     * @param vec the vector to mult to this vector.
     * @return this
     */
    public Vector2f multLocal(Vector2f vec) {
        if(null == vec) {
            LoggingSystem.getLogger().log(Level.WARNING, "Provided vector is " +
                "null, null returned.");
            return null;
        }
        x *= vec.x;
        y *= vec.y;
        return this;
    }

    /**
     * Multiplies this Vector2f's x and y by the scalar and stores the result in product.  The
     * result is returned for chaining.  Similar to product=this*scalar;
     * @param scalar The scalar to multiply by.
     * @param product The vector2f to store the result in.
     * @return product, after multiplication.
     */
    public Vector2f mult(float scalar, Vector2f product) {
        if(null == product) {
            product = new Vector2f();
        }

        product.x = x * scalar;
        product.y = y * scalar;
        return product;
    }

    /**
     * <code>divide</code> divides the values of this vector by a
     * scalar and returns the result. The values of this vector
     * remain untouched.
     * @param scalar the value to divide this vectors attributes by.
     * @return the result <code>Vector</code>.
     */
    public Vector2f divide(float scalar) {
        return new Vector2f(x / scalar, y / scalar);
    }

    /**
     * <code>divideLocal</code> divides this vector by a scalar internally,
     * and returns a handle to this vector for easy chaining of calls.
     * Dividing by zero will result in an exception.
     * @param scalar the value to divides this vector by.
     * @return this
     */
    public Vector2f divideLocal(float scalar) {
        x /= scalar;
        y /= scalar;
        return this;
    }

    /**
     *
     * <code>negate</code> returns the negative of this vector. All values are
     * negated and set to a new vector.
     * @return the negated vector.
     */
    public Vector2f negate() {
        return new Vector2f(-x,-y);
    }

    /**
     *
     * <code>negateLocal</code> negates the internal values of this vector.
     * @return this.
     */
    public Vector2f negateLocal() {
        x= -x;
        y= -y;
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
    public Vector2f subtract(Vector2f vec) {
        return new Vector2f(x - vec.x, y - vec.y);
    }


    /**
     * <code>subtractLocal</code> subtracts a provided vector to this vector internally,
     * and returns a handle to this vector for easy chaining of calls.
     * If the provided vector is null, null is returned.
     * @param vec the vector to subtract
     * @return this
     */
    public Vector2f subtractLocal(Vector2f vec) {
        if(null == vec) {
            LoggingSystem.getLogger().log(Level.WARNING, "Provided vector is " +
                "null, null returned.");
            return null;
        }
        x -= vec.x;
        y -= vec.y;
        return this;
    }

    /**
     * <code>normalize</code> returns the unit vector of this vector.
     * @return unit vector of this vector.
     */
    public Vector2f normalize() {
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
    public Vector2f normalizeLocal() {
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
        x = y = 0;
    }

    /**
     * <code>hashCode</code> returns a unique code for this vector object based
     * on it's values. If two vectors are logically equivalent, they will return
     * the same hash code value.
     * @return the hash code value of this vector.
     */
    public int hashCode() {
      int hash = 7;
      hash += 31 * hash + Float.floatToIntBits(x);
      hash += 31 * hash + Float.floatToIntBits(y);
      return hash;
    }

    /**
     * <code>clone</code> creates a new Vector2f object containing the same
     * data as this one.
     * @return the new Vector2f
     */
    public Object clone() {
        return new Vector2f(x,y);
    }

    /**
     * are these two vectors the same? they are is they both have the same x
     * and y values.
     *
     * @param o
     *            the object to compare for equality
     * @return true if they are equal
     */
    public boolean equals(Object o) {
        if (!(o instanceof Vector2f) || o == null) { return false; }

        if (this == o) { return true; }

        Vector2f comp = (Vector2f) o;
        if (x != comp.x) return false;
        if (y != comp.y) return false;
        return true;
    }

    /**
     * <code>toString</code> returns the string representation of this
     * vector object. The format of the string is such:
     *
     * com.jme.math.Vector2f [X=XX.XXXX, Y=YY.YYYY]
     *
     * @return the string representation of this vector.
     */
    public String toString() {
        return "com.jme.math.Vector2f [X="+x+", Y="+y+"]";
    }

    /**
     * Used with serialization.  Not to be called manually.
     * @param in ObjectInput
     * @throws IOException
     * @throws ClassNotFoundException
     * @see java.io.Externalizable
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        x=in.readFloat();
        y=in.readFloat();
    }

    /**
     * Used with serialization.  Not to be called manually.
     * @param out ObjectOutput
     * @throws IOException
     * @see java.io.Externalizable
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(x);
        out.writeFloat(y);
    }
}
