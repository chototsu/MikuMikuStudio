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
 * <code>LWJGLAlphaState</code> subclasses the AlphaState using the LWJGL API
 * to set OpenGL's alpha state. 
 * @author Mark Powell
 * @version $Id: LWJGLAlphaState.java,v 1.1.1.1 2003-10-29 10:56:38 Anakan Exp $
 */
public class LWJGLAlphaState extends AlphaState {
    //gl alpha values
    private int[] glSrcBlend =
        {
            GL.GL_ZERO,
            GL.GL_ONE,
            GL.GL_DST_COLOR,
            GL.GL_ONE_MINUS_DST_COLOR,
            GL.GL_SRC_ALPHA,
            GL.GL_ONE_MINUS_SRC_ALPHA,
            GL.GL_DST_ALPHA,
            GL.GL_ONE_MINUS_DST_ALPHA,
            GL.GL_SRC_ALPHA_SATURATE };

    private int[] glDestBlend =
        {
            GL.GL_ZERO,
            GL.GL_ONE,
            GL.GL_SRC_COLOR,
            GL.GL_ONE_MINUS_SRC_COLOR,
            GL.GL_SRC_ALPHA,
            GL.GL_ONE_MINUS_SRC_ALPHA,
            GL.GL_DST_ALPHA,
            GL.GL_ONE_MINUS_DST_ALPHA };

    private int[] glAlphaTest =
        {
            GL.GL_NEVER,
            GL.GL_LESS,
            GL.GL_EQUAL,
            GL.GL_LEQUAL,
            GL.GL_GREATER,
            GL.GL_NOTEQUAL,
            GL.GL_GEQUAL,
            GL.GL_ALWAYS };

    /**
     * Constructor instantiates a new <code>LWJGLAlphaState</code> object with
     * default values. 
     *
     */
    public LWJGLAlphaState() {
        super();
    }

    /**
     * <code>set</code> is called to set the alpha state. If blending is
     * enabled, the blend function is set up and if alpha testing is enabled
     * the alpha functions are set.
     * @see com.jme.scene.state.RenderState#set()
     */
    public void set() {
        if (blendEnabled) {
            GL.glEnable(GL.GL_BLEND);
            GL.glBlendFunc(
                glSrcBlend[srcBlend],
                glDestBlend[dstBlend]);
        } else {
            GL.glDisable(GL.GL_BLEND);
        }

        if (testEnabled) {
            GL.glEnable(GL.GL_ALPHA_TEST);
            GL.glAlphaFunc(glAlphaTest[test], reference);
        } else {
            GL.glDisable(GL.GL_ALPHA_TEST);
        }

    }

    /**
     * <code>unset</code> turns off blending if it is enabled, and turns off
     * alpha testing if it is enabled.
     * @see com.jme.scene.state.RenderState#unset()
     */
    public void unset() {
        if(blendEnabled) {
            GL.glDisable(GL.GL_BLEND);
        }
        
        if(testEnabled) {
            GL.glDisable(GL.GL_ALPHA_TEST);
        }

    }
}
