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

/**
 *
 * <code>Rectangle</code> defines a finite plane within three dimensional space
 * that is specified via three points (A, B, C). These three points define a
 * triangle with the forth point defining the rectangle ((B + C) - A.
 * @author Mark Powell
 * @version $Id: Rectangle.java,v 1.3 2004-04-27 17:10:14 renanse Exp $
 */

public class Rectangle {
  private Vector3f a, b, c;

  /**
   * Constructor creates a new <code>Rectangle</code> with no defined
   * corners. A, B, and C must be set to define a valid rectangle.
   *
   */
  public Rectangle() {
    a = new Vector3f();
    b = new Vector3f();
    c = new Vector3f();
  }

  /**
   * Constructor creates a new <code>Rectangle</code> with defined A, B, and
   * C points that define the area of the rectangle.
   * @param a the first corner of the rectangle.
   * @param b the second corner of the rectangle.
   * @param c the third corner of the rectangle.
   */
  public Rectangle(Vector3f a, Vector3f b, Vector3f c) {
    this.a = a;
    this.b = b;
    this.c = c;
  }

  /**
   * <code>getA</code> returns the first point of the rectangle.
   * @return the first point of the rectangle.
   */
  public Vector3f getA() {
    return a;
  }

  /**
   * <code>setA</code> sets the first point of the rectangle.
   * @param a the first point of the rectangle.
   */
  public void setA(Vector3f a) {
    this.a = a;
  }

  /**
   * <code>getB</code> returns the second point of the rectangle.
   * @return the second point of the rectangle.
   */
  public Vector3f getB() {
    return b;
  }

  /**
   * <code>setB</code> sets the second point of the rectangle.
   * @param b the second point of the rectangle.
   */
  public void setB(Vector3f b) {
    this.b = b;
  }

  /**
   * <code>getC</code> returns the third point of the rectangle.
   * @return the third point of the rectangle.
   */
  public Vector3f getC() {
    return c;
  }

  /**
   * <code>setC</code> sets the third point of the rectangle.
   * @param c the third point of the rectangle.
   */
  public void setC(Vector3f c) {
    this.c = c;
  }

  /**
   *
   * <code>random</code> returns a random point within the plane defined by:
   * A, B, C, and (B + C) - A.
   * @return a random point within the rectangle.
   */
  public Vector3f random() {
    Vector3f result = new Vector3f();

    float s = (float) FastMath.nextRandomFloat();
    float t = (float) FastMath.nextRandomFloat();

    float aMod = 1.0f - s - t;
    result = a.mult(aMod).addLocal(b.mult(s).addLocal(c.mult(t)));
    return result;
  }
}
