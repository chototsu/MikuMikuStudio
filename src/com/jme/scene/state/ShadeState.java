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
package com.jme.scene.state;

/**
 * <code>ShadeState</code> maintains the interpolation of color between 
 * vertices. Smooth shades the colors with proper linear interpolation, while
 * flat provides no smoothing.
 * @author Mark Powell
 * @version $Id: ShadeState.java,v 1.1 2003-10-13 18:30:08 mojomonkey Exp $
 */
public abstract class ShadeState extends RenderState {
    /**
     * Pick the color of just one vertex of a triangle and rasterize all pixels
     * of the triangle with this color.
     */
    public static final int SM_FLAT = 0;
    /**
     * Smoothly interpolate the color values between the three colors of the
     * three vertices.
     */
    public static final int SM_SMOOTH = 1;
    
    //shade mode.
    protected int shade;
    
    /**
     * Constructor instantiates a new <code>ShadeState</code> object with the
     * default mode being smooth.
     *
     */
    public ShadeState() {
        shade = SM_SMOOTH;
    }

    /**
     * <code>getShade</code> returns the current shading state.
     * @return the current shading state.
     */
    public int getShade() {
        return shade;
    }

    /**
     * <code>setShade</code> sets the current shading state. If an 
     * invalid value is passed, the shade is set to SM_SMOOTH.
     * @param shade the current shading state.
     */
    public void setShade(int shade) {
        if(shade < 0 || shade > 1) {
            shade = SM_SMOOTH;
        }
        this.shade = shade;
    }

    /**
     * <code>getType</code> returns this type of this render state.
     * (RS_SHADE).
     * @see com.jme.scene.state.RenderState#getType()
     */
    public int getType() {
        return RS_SHADE;
    }

}
