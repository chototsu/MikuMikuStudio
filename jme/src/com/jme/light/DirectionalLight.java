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
package com.jme.light;

import com.jme.math.Vector3f;

/**
 * <code>DirectionalLight</code> defines a light that is assumed to be 
 * infintely far away (something similar to the sun). This means the direction
 * of the light rays are all parallel. The direction the light is coming from
 * is defined by the class.
 * @author Mark Powell
 * @version $Id: DirectionalLight.java,v 1.1.1.1 2003-10-29 10:56:25 Anakan Exp $
 */
public class DirectionalLight extends Light {
    //direction the light is coming from.
    private Vector3f direction;
    
    /**
     * Constructor instantiates a new <code>DirectionalLight</code> object. 
     * The initial light colors are white and the direction the light emits
     * from is (0,0,0).
     *
     */
    public DirectionalLight() {
        super();
        direction = new Vector3f();
    }
    
    /**
     * <code>getDirection</code> returns the direction the light is 
     * emitting from.
     * @return the direction the light is emitting from.
     */ 
    public Vector3f getDirection() {
        return direction;
    }

    /**
     * <code>setDirection</code> sets the direction the light is emitting from.
     * @param direction the direction the light is emitting from.
     */
    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    /**
     * <code>getType</code> returns this light's type (LT_DIRECTIONAL).
     * @see com.jme.light.Light#getType()
     */
    public int getType() {
        return LT_DIRECTIONAL;
    }

}
