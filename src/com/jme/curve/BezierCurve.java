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
package com.jme.curve;

import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;

/**
 * <code>BezierCurve</code> uses an ordered-list of three-dimensional 
 * points and the equation:
 * x(t) = Sum(n, i=0) Bn,i(t)Pi<br>
 * t [0,1]<br>
 * Bn,i(t) = C(n;i)t^i(1-t)^(n-i)<br>
 * The input (t) provides the current point of the curve at a interval
 * [0,1] where 0 is the first control point and 1 is the second control
 * point.
 * @author Mark Powell
 * @version $Id: BezierCurve.java,v 1.7 2004-03-12 17:36:47 mojomonkey Exp $
 */
public class BezierCurve extends Curve {

    /**
     * Constructor instantiates a new <code>BezierCurve</code> object.
     * @param name the name of the scene element. This is required for identification and
     * 		comparision purposes.
     */
    public BezierCurve(String name) {
        super(name);
    }

    /**
     * Constructor instantiates a new <code>BezierCurve</code> object. 
     * The control points that define the curve are supplied.
     * @param name the name of the scene element. This is required for identification and
     * 		comparision purposes.
     * @param controlPoints the points that define the curve.
     */
    public BezierCurve(String name, Vector3f[] controlPoints) {
        super(name, controlPoints);
    }

    /**
     * <code>getPoint</code> calculates a point on a Bezier curve 
     * from a given time value within the interval [0, 1]. If the 
     * value is zero or less, the first control point is returned. If
     * the value is one or more, the last control point is returned.
     * Using the equation of a Bezier Curve, the point at the interval
     * is calculated and returned. 
     * @see com.jme.curve.Curve#getPoint(float)
     */
    public Vector3f getPoint(float time) {
        //first point
        if (time < 0) {
            return vertex[0];
        }
        //last point.
        if (time > 1) {
            return vertex[vertex.length - 1];
        }

        Vector3f point = new Vector3f();

        float muk = 1;
        float munk = (float) Math.pow(1 - time, vertex.length - 1);

        for (int i = 0; i < vertex.length; i++) {
            int count = vertex.length - 1;
            int iCount = i;
            int diff = count - iCount;
            float blend = muk * munk;
            muk *= time;
            munk /= (1 - time);
            while (count >= 1) {
                blend *= count;
                count--;
                if (iCount > 1) {
                    blend /= iCount;
                    iCount--;
                }

                if (diff > 1) {
                    blend /= diff;
                    diff--;
                }
            }
            point.x += vertex[i].x * blend;
            point.y += vertex[i].y * blend;
            point.z += vertex[i].z * blend;
        }

        return point;
    }

    /**
     *  <code>getOrientation</code> calculates the rotation matrix
     * for any given point along to the line to still be facing
     * in the direction of the line.
     * @param time the current time (between 0 and 1)
     * @param precision how accurate to (i.e. the next time) to 
     *      check against.
     * @return the rotation matrix.
     * @see com.jme.curve.Curve#getOrientation(float, float)
     */
    public Matrix3f getOrientation(float time, float precision) {
        Matrix3f rotation = new Matrix3f();

        //calculate tangent
        Vector3f point = getPoint(time);
        if(point == vertex[vertex.length-1] || point == vertex[0]) {
            return rotation;
        }
        Vector3f tangent = point.subtract(getPoint(time + precision));
        tangent = tangent.normalize();
        //calculate normal
        Vector3f tangent2 = getPoint(time - precision).subtract(point);
        Vector3f normal = tangent.cross(tangent2);
        normal = normal.normalize();
        //calculate binormal
        Vector3f binormal = tangent.cross(normal);
        binormal = binormal.normalize();

        rotation.setColumn(0, tangent);
        rotation.setColumn(1, normal);
        rotation.setColumn(2, binormal);
        return rotation;
    }

    /**
     *  <code>getOrientation</code> calculates the rotation matrix
     * for any given point along to the line to still be facing
     * in the direction of the line. A up vector is supplied, this
     * keep the rotation matrix following the line, but insures the
     * object's up vector is not drastically changed.
     * @param time the current time (between 0 and 1)
     * @param precision how accurate to (i.e. the next time) to 
     *      check against.
     * @return the rotation matrix.
     * @see com.jme.curve.Curve#getOrientation(float, float)
     */
    public Matrix3f getOrientation(float time, float precision, Vector3f up) {
        if(up == null) {
            return getOrientation(time, precision);
        }
        Matrix3f rotation = new Matrix3f();

        //calculate tangent
        Vector3f tangent = getPoint(time).subtract(getPoint(time + precision));
        tangent = tangent.normalize();

        //calculate binormal
        Vector3f binormal = tangent.cross(up);
        binormal = binormal.normalize();

        //calculate normal
        Vector3f normal = binormal.cross(tangent);
        normal = normal.normalize();

        rotation.setColumn(0, tangent);
        rotation.setColumn(1, normal);
        rotation.setColumn(2, binormal);
        
        return rotation;
    }
    
    public void resetVertices() {
        
    }

}
