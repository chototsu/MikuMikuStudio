/*
 * Copyright (c) 2003-2005 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.math;

/**
 * <code>Triangle</code> defines a object for containing triangle information.
 * The triangle is defined by a collection of three <code>Vector3f</code>
 * objects.
 * @author Mark Powell
 * @version $Id: Triangle.java,v 1.5 2005-09-15 17:13:47 renanse Exp $
 */
public class Triangle {
    private Vector3f pointa;
    private Vector3f pointb;
    private Vector3f pointc;

    /**
     * Constructor instantiates a new <Code>Triangle</code> object with the
     * supplied vectors as the points. It is recommended that the vertices
     * be supplied in a counter clockwise winding to support normals for a
     * right handed coordinate system.
     * @param p1 the first point of the triangle.
     * @param p2 the second point of the triangle.
     * @param p3 the third point of the triangle.
     */
    public Triangle(Vector3f p1, Vector3f p2, Vector3f p3) {
        pointa = p1;
        pointb = p2;
        pointc = p3;
    }

    /**
     *
     * <code>get</code> retrieves a point on the triangle denoted by the index
     * supplied.
     * @param i the index of the point.
     * @return the point.
     */
    public Vector3f get(int i) {
        if (i==0) return pointa;
        else if (i==1) return pointb;
        else if (i==2) return pointc;
        return null;
    }

    /**
     *
     * <code>set</code> sets one of the triangles points to that specified as
     * a parameter.
     * @param i the index to place the point.
     * @param point the point to set.
     */
    public void set(int i, Vector3f point) {
        if (i==0) pointa=point;
        else if (i==1) pointb=point;
        else if (i==2) pointc=point;
    }
}
