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
import com.jme.system.JmeException;

/**
 * <code>Curve</code> defines a collection of points that make up a curve.
 * How this curve is constructed is undefined, and the job of a subclass.
 * <code>Curve</code> is abstract only maintaining the point collection. It
 * defines <code>getPoint</code> and <code>getOrientation</code>. Extending
 * classes are responsible for implementing these methods in the appropriate
 * way.
 * @author Mark Powell
 * @version $Id: Curve.java,v 1.5 2004-01-07 02:49:38 mojomonkey Exp $
 */
public abstract class Curve {

    /**
     * The array of control points.
     */
    protected Vector3f[] controlPoints;

    /**
     * Constructor creates a default <code>Curve</code> object with a
     * zero size array for the points.
     *
     */
    public Curve() {
        controlPoints = new Vector3f[0];
    }

    /**
     * Constructor creates a <code>Curve</code> object. The control
     * point list is set during creation. If the control point list is
     * null or has fewer than 2 points, an exception is thrown.
     * @param controlPoints the points that define the curve.
     */
    public Curve(Vector3f[] controlPoints) {
        if (null == controlPoints) {
            throw new JmeException("Control Points may not be null.");
        }

        if (controlPoints.length < 2) {
            throw new JmeException("There must be at least two control points.");
        }

        this.controlPoints = controlPoints;
    }

    /**
     * 
     * <code>setControlPoints</code> sets the control point list that 
     * defines the curve. If the control point list is null or has 
     * fewer than 2 points, an exception is thrown.
     * @param controlPoints the points that define the curve.
     */
    public void setControlPoints(Vector3f[] controlPoints) {
        if (null == controlPoints) {
            throw new JmeException("Control Points may not be null.");
        }

        if (controlPoints.length < 2) {
            throw new JmeException("There must be at least two control points.");
        }

        this.controlPoints = controlPoints;
    }
    
    /**
     * 
     * <code>getControlPoints</code> retrieves the list of points that
     * defines the curve.
     * @return the point list that defines the curve.
     */
    public Vector3f[] getControlPoints() {
        return controlPoints;
    }

    /**
     * 
     * <code>getPoint</code> calculates a point on the curve based on 
     * the time, where time is [0, 1]. How the point is calculated is
     * defined by the subclass. 
     * @param time the time frame on the curve, [0, 1].
     * @return the point on the curve at a specified time.
     */
    public abstract Vector3f getPoint(float time);
    
    /**
     * 
     * <code>getOrientation</code> calculates a rotation matrix that 
     * defines the orientation along a curve. How the matrix is 
     * calculated is defined by the subclass.
     * @param time the time frame on the curve, [0, 1].
     * @param precision the accuracy of the orientation (lower is more
     *      precise). Recommended (0.1).
     * @return the rotational matrix that defines the orientation of
     *      along a curve.
     */
    public abstract Matrix3f getOrientation(float time, float precision);
    
    /**
     * 
     * <code>getOrientation</code> calculates a rotation matrix that 
     * defines the orientation along a curve. The up vector is provided
     * keeping the orientation from "rolling" along the curve. This is
     * useful for camera tracks. How the matrix is calculated is defined 
     * by the subclass.
     * @param time the time frame on the curve, [0, 1].
     * @param precision the accuracy of the orientation (lower is more
     *      precise). Recommended (0.1).
     * @param up the up vector to lock.
     * @return the rotational matrix that defines the orientation of
     *      along a curve.
     */
    public abstract Matrix3f getOrientation(float time, float precision, Vector3f up);
}
