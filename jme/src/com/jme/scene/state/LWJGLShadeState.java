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

import org.lwjgl.opengl.GL;

/**
 * <code>LWJGLShadeState</code> subclasses the ShadeState class using the
 * LWJGL API to access OpenGL to set the shade state.
 * @author Mark Powell
 * @version $Id: LWJGLShadeState.java,v 1.1.1.1 2003-10-29 10:56:39 Anakan Exp $
 */
public class LWJGLShadeState extends ShadeState {
    //open gl params
    int[] glShadeState =
    {
        GL.GL_FLAT,
        GL.GL_SMOOTH
    };

    /**
     * Constructor instantiates a new <code>LWJGLShadeState</code> object.
     *
     */
    public LWJGLShadeState() {
        super();
    }

    /**
     * <code>set</code> sets the OpenGL shade state to that specified by
     * the state.
     * @see com.jme.scene.state.RenderState#set()
     */
    public void set() {
        
        GL.glShadeModel(glShadeState[shade]);
    }

    /**
     * <code>unset</code> resets the shade state to defaul SMOOTH.
     * @see com.jme.scene.state.RenderState#unset()
     */
    public void unset() {
        GL.glShadeModel(GL.GL_SMOOTH);
    }

}
