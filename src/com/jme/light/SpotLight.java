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

package com.jme.light;

import com.jme.math.Vector3f;

/**
 * <code>SpotLight</code> defines a light that has a location in space and
 * emits light within a cone. This cone is defined by an angle and exponent.
 * Typically this light's values are attenuated based on the
 * distance of the point light and the object it illuminates.
 * @author Mark Powell
 * @version $Id: SpotLight.java,v 1.7 2005-10-22 14:48:05 renanse Exp $
 */
public class SpotLight extends PointLight {
    private static final long serialVersionUID = 1L;
	//attributes
    private float angle;
    private float exponent;
    private Vector3f direction;

    /**
     * Constructor instantiates a new <code>SpotLight</code> object. The
     * initial position of the light is (0,0,0) with angle 0, and colors white.
     *
     */
    public SpotLight() {
        super();
        direction = new Vector3f();
    }
    /**
     * <code>getDirection</code> returns the direction the spot light is coming
     * from.
     * @return the direction the spot light is coming from.
     */
    public Vector3f getDirection() {
        return direction;
    }

    /**
     * <code>setDirection</code> sets the direction the spot light is coming
     * from.
     * @param direction the direction the spot light is coming from.
     */
    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    /**
     * <code>getAngle</code> returns the angle of the spot light.
     * 
     * @see #setAngle(float) for more info
     * @return the angle (in degrees)
     */
    public float getAngle() {
        return angle;
    }

    /**
     * <code>setAngle</code> sets the angle of focus of the spot light
     * measured from the direction vector. Think of this as the angle of a cone.
     * Therefore, if you specify 10 degrees, you will get a 20 degree cone (10
     * degrees off either side of the direction vector.) 180 degrees means
     * radiate in all directions.
     * 
     * @param angle
     *            the angle (in degrees)
     */
    public void setAngle(float angle) {
        this.angle = angle;
    }

    /**
     * <code>getExponent</code> gets the spot exponent of this light.
     * 
     * @see #setExponent(float) for more info
     * @return the spot exponent of this light.
     */
    public float getExponent() {
        return exponent;
    }

    /**
     * <code>setExponent</code> sets the spot exponent of this light. This
     * value represents how focused the light beam is.
     * 
     * @param exponent
     *            the spot exponent of this light. Should be between 0-128
     */
    public void setExponent(float exponent) {
        this.exponent = exponent;
    }

    /**
     * <code>getType</code> returns the type of this light (LT_SPOT).
     * @see com.jme.light.Light#getType()
     */
    public int getType() {
        return LT_SPOT;
    }

}
