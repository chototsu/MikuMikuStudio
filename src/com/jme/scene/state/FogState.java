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
package com.jme.scene.state;

import com.jme.renderer.ColorRGBA;

/**
 * <code>FogState</code> maintains the fog qualities for a node and it's
 * children. The fogging function, color, start, end and density are all
 * set and maintained.
 * @author Mark Powell
 * @version $Id: FogState.java,v 1.2 2004-04-22 22:26:55 renanse Exp $
 */
public abstract class FogState extends RenderState {
    /**
     * The fog blending function defined as: (end - z) / (end - start).
     */
    public static final int DF_LINEAR = 0;
    /**
     * The fog blending function defined as: e^-(density*z)
     */
    public static final int DF_EXP= 1;
    /**
     * The fog blending function defined as: e^((-density*z)^2)
     */
    public static final int DF_EXPSQR = 2;

    /**
     * Defines the rendering method for the fogging, where each vertex color
     * is altered by the fogging function.
     */
    public static final int AF_PER_VERTEX = 0;
    /**
     * Defines the rendering method for the fogging, where each pixel color
     * is altered by the fogging function.
     */
    public static final int AF_PER_PIXEL = 1;

    //fogging attributes.
    protected float start;
    protected float end;
    protected float density;
    protected ColorRGBA color;
    protected int densityFunction;
    protected int applyFunction;

    /**
     * Constructor instantiates a new <code>FogState</code> with default
     * fog values.
     *
     */
    public FogState() {
        color = new ColorRGBA();
        densityFunction = DF_LINEAR;
        applyFunction = AF_PER_VERTEX;
    }

    /**
     * <code>setApplyFunction</code> sets the apply function used for the fog
     * attributes. If an invalid value is passed in, the default function
     * is set to AF_PER_VERTEX.
     * @param applyFunction the function used for the fog application.
     */
    public void setApplyFunction(int applyFunction) {
        if(applyFunction < 0 || applyFunction > 1) {
            applyFunction = AF_PER_VERTEX;
        }
        this.applyFunction = applyFunction;
    }

    /**
     * <code>setDensityFunction</code> sets the density function used for the
     * fog blending. If an invalid value is passed, the default function is
     * set to DF_LINEAR.
     * @param densityFunction the function used for the fog density.
     */
    public void setDensityFunction(int densityFunction) {
        if(densityFunction < 0 || densityFunction > 2) {
            densityFunction = DF_LINEAR;
        }
        this.densityFunction = densityFunction;
    }

    /**
     * <code>setColor</code> sets the color of the fog.
     * @param color the color of the fog.
     */
    public void setColor(ColorRGBA color) {
        this.color = color;
    }

    /**
     * <code>setDensity</code> sets the density of the fog. This value is
     * clamped to [0, 1].
     * @param density the density of the fog.
     */
    public void setDensity(float density) {
        if(density < 0) {
            density = 0;
        }

        if(density > 1) {
            density = 1;
        }
        this.density = density;
    }

    /**
     * <code>setEnd</code> sets the end distance, or the distance where fog
     * is at it's thickest.
     * @param end the distance where the fog is the thickest.
     */
    public void setEnd(float end) {
        this.end = end;
    }

    /**
     * <code>setStart</code> sets the start distance, or where fog begins
     * to be applied.
     * @param start the start distance of the fog.
     */
    public void setStart(float start) {
        this.start = start;
    }

    /**
     * <code>getType</code> returns the render state type of the fog state.
     * (RS_FOG).
     * @see com.jme.scene.state.RenderState#getType()
     */
    public int getType() {
        return RS_FOG;
    }



}
