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
package com.jme.renderer;

import com.jme.math.FastMath;

import java.io.*;

/**
 * <code>ColorRGBA</code> defines a color made from a collection of
 * red, green and blue values. An alpha value determines is transparency.
 * All values must be between 0 and 1. If any value is set higher or lower
 * than these constraints they are clamped to the min or max. That is, if
 * a value smaller than zero is set the value clamps to zero. If a value
 * higher than 1 is passed, that value is clamped to 1. However, because the
 * attributes r, g, b, a are public for efficiency reasons, they can be
 * directly modified with invalid values. The client should take care when
 * directly addressing the values. A call to clamp will assure that the values
 * are within the constraints.
 * @author Mark Powell
 * @version $Id: ColorRGBA.java,v 1.15 2004-08-03 03:21:19 cep21 Exp $
 */
public class ColorRGBA implements Externalizable{

    /**
     * the color black (0,0,0).
     */
    public static final ColorRGBA black = new ColorRGBA(0f, 0f, 0f, 1f);
    /**
     * the color white (1,1,1).
     */
    public static final ColorRGBA white = new ColorRGBA(1f, 1f, 1f, 1f);
    /**
     * the color gray (.2,.2,.2).
     */
    public static final ColorRGBA darkGray = new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f);
    /**
     * the color gray (.5,.5,.5).
     */
    public static final ColorRGBA gray = new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f);
    /**
     * the color gray (.8,.8,.8).
     */
    public static final ColorRGBA lightGray = new ColorRGBA(0.8f, 0.8f, 0.8f, 1.0f);
    /**
     * the color red (1,0,0).
     */
    public static final ColorRGBA red = new ColorRGBA(1f, 0f, 0f, 1f);
    /**
     * the color green (0,1,0).
     */
    public static final ColorRGBA green = new ColorRGBA(0f, 1f, 0f, 1f);
    /**
     * the color blue (0,0,1).
     */
    public static final ColorRGBA blue = new ColorRGBA(0f, 0f, 1f, 1f);

    /**
     * The red component of the color.
     */
    public float r;
    /**
     * The green component of the color.
     */
    public float g;
    /**
     * the blue component of the color.
     */
    public float b;
    /**
     * the alpha component of the color.  0 is transparent and 1 is opaque
     */
    public float a;

    /**
     * Constructor instantiates a new <code>ColorRGBA</code> object. This
     * color is the default "white" with all values 1.
     *
     */
    public ColorRGBA() {
        r = g = b = a = 1.0f;
    }

    /**
     * Constructor instantiates a new <code>ColorRGBA</code> object. The
     * values are defined as passed parameters. These values are then clamped
     * to insure that they are between 0 and 1.
     * @param r the red component of this color.
     * @param g the green component of this color.
     * @param b the blue component of this color.
     * @param a the alpha component of this color.
     */
    public ColorRGBA(float r, float g, float b, float a) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = a;
      clamp();
    }

    /**
     * Copy constructor creates a new <code>ColorRGBA</code> object, based on
     * a provided color.
     * @param rgba the <code>ColorRGBA</code> object to copy.
     */
    public ColorRGBA(ColorRGBA rgba) {
        this.a = rgba.a;
        this.r = rgba.r;
        this.g = rgba.g;
        this.b = rgba.b;
        clamp();
    }

    /**
     *
     * <code>set</code> sets the RGBA values of this color. The values are then
     * clamped to insure that they are between 0 and 1.
     *
     * @param r the red component of this color.
     * @param g the green component of this color.
     * @param b the blue component of this color.
     * @param a the alpha component of this color.
     */
    public void set(float r, float g, float b, float a) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = a;
      clamp();
    }

    /**
     * <code>set</code> sets the values of this color to those set by a parameter
     * color.
     *
     * @param rgba ColorRGBA the color to set this color to.
     */
    public void set(ColorRGBA rgba) {
      if(rgba == null) {
          r = 0;
          g = 0;
          b = 0;
          a = 0;
      } else {
	      r = rgba.r;
	      g = rgba.g;
	      b = rgba.b;
	      a = rgba.a;
      }
    }

    /**
     * <code>clamp</code> insures that all values are between 0 and 1. If any
     * are less than 0 they are set to zero. If any are more than 1 they are
     * set to one.
     *
     */
    public void clamp() {
        if (r < 0) {
            r = 0;
        } else if (r > 1) {
            r = 1;
        }

        if (g < 0) {
            g = 0;
        } else if (g > 1) {
            g = 1;
        }

        if (b < 0) {
            b = 0;
        } else if (b > 1) {
            b = 1;
        }

        if (a < 0) {
            a = 0;
        } else if (a > 1) {
            a = 1;
        }
    }

    /**
     *
     * <code>getColorArray</code> retrieves the color values of this object as
     * a four element float array.
     * @return the float array that contains the color elements.
     */
    public float[] getColorArray() {
        float[] f = {r,g,b,a};
        return f;
    }

    /**
     * Sets this color to the interpolation by changeAmnt from this to the finalColor
     * this=(1-changeAmnt)*this + changeAmnt * finalColor
     * @param finalColor The final color to interpolate towards
     * @param changeAmnt An amount between 0.0 - 1.0 representing a precentage
     *  change from this towards finalColor
     */
    public void interpolate(ColorRGBA finalColor,float changeAmnt){
        this.r=(1-changeAmnt)*this.r + changeAmnt*finalColor.r;
        this.g=(1-changeAmnt)*this.g + changeAmnt*finalColor.g;
        this.b=(1-changeAmnt)*this.b + changeAmnt*finalColor.b;
        this.a=(1-changeAmnt)*this.a + changeAmnt*finalColor.a;
    }

    /**
     * Sets this color to the interpolation by changeAmnt from beginColor to finalColor
     * this=(1-changeAmnt)*beginColor + changeAmnt * finalColor
     * @param beginColor The begining color (changeAmnt=0)
     * @param finalColor The final color to interpolate towards (changeAmnt=1)
     * @param changeAmnt An amount between 0.0 - 1.0 representing a precentage
     *  change from beginColor towards finalColor
     */
    public void interpolate(ColorRGBA beginColor,ColorRGBA finalColor,float changeAmnt){
        this.r=(1-changeAmnt)*beginColor.r + changeAmnt*finalColor.r;
        this.g=(1-changeAmnt)*beginColor.g + changeAmnt*finalColor.g;
        this.b=(1-changeAmnt)*beginColor.b + changeAmnt*finalColor.b;
        this.a=(1-changeAmnt)*beginColor.a + changeAmnt*finalColor.a;
    }

    /**
     *
     * <code>randomColor</code> is a utility method that generates a random
     * color.
     *
     * @return a random color.
     */
    public static ColorRGBA randomColor() {
      ColorRGBA rVal = new ColorRGBA(0, 0, 0, 1);
      rVal.r = FastMath.nextRandomFloat();
      rVal.g = FastMath.nextRandomFloat();
      rVal.b = FastMath.nextRandomFloat();
      rVal.clamp();
      return rVal;
    }

    /**
     * Multiplies each r/g/b/a of this color by the r/g/b/a of the given color and
     * returns the result as a new ColorRGBA.  Used as a way of combining colors and lights.
     * @param c The color to multiply.
     * @return The new ColorRGBA.  this*c
     */
    public ColorRGBA mult(ColorRGBA c) {
    	return new ColorRGBA(c.r * r, c.g * g, c.b * b, c.a * a);
    }

    /**
     * Adds each r/g/b/a of this color by the r/g/b/a of the given color and
     * returns the result as a new ColorRGBA.
     * @param c The color to add.
     * @return The new ColorRGBA.  this+c
     */
    public ColorRGBA add(ColorRGBA c) {
    	return new ColorRGBA(c.r + r, c.g + g, c.b + b, c.a + a);
    }

    /**
     * <code>toString</code> returns the string representation of this color.
     * The format of the string is:<br>
     * com.jme.ColorRGBA: [R=RR.RRRR, G=GG.GGGG, B=BB.BBBB, A=AA.AAAA]
     * @return the string representation of this color.
     */
    public String toString() {
        return "com.jme.renderer.ColorRGBA: [R="+r+", G="+g+", B="+b+", A="+a+"]";
    }


    /**
     * <code>clone</code> creates a new ColorRGBA object containing the same
     * data as this one.
     * @return the color that is the same as this.
     */
    public Object clone() {
        return new ColorRGBA(r,g,b,a);
    }

    /**
     * <code>equals</code> returns true if this color is logically equivalent
     * to a given color. That is, if the values of the two colors are the same.
     * False is returned otherwise.
     * @param o the object to compare againts.
     * @return true if the colors are equal, false otherwise.
     */
    public boolean equals(Object o) {
        if(!(o instanceof ColorRGBA) || o == null) {
            return false;
        }

        if(this == o) {
            return true;
        }

        ColorRGBA comp = (ColorRGBA)o;
        if (r != comp.r) return false;
        if (g != comp.g) return false;
        if (b != comp.b) return false;
        if (a != comp.a) return false;
        return true;
    }

    /**
     * <code>hashCode</code> returns a unique code for this color object based
     * on it's values. If two colors are logically equivalent, they will return
     * the same hash code value.
     * @return the hash code value of this color.
     */
    public int hashCode() {
      int hash = 7;
      hash += 31 * hash + Float.floatToIntBits(r);
      hash += 31 * hash + Float.floatToIntBits(g);
      hash += 31 * hash + Float.floatToIntBits(b);
      hash += 31 * hash + Float.floatToIntBits(a);
      return hash;
    }

    /**
     * Used with serialization.  Not to be called manually.
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     * @see java.io.Externalizable
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        r=in.readFloat();
        g=in.readFloat();
        b=in.readFloat();
        a=in.readFloat();
    }

    /**
     * Used with serialization.  Not to be called manually.     *
     * @param out
     * @throws IOException
     * @see java.io.Externalizable
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(r);
        out.writeFloat(g);
        out.writeFloat(b);
        out.writeFloat(a);
    }
}
