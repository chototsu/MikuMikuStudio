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
package com.jme.scene.state.lwjgl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import com.jme.scene.state.FogState;

/**
 * <code>LWJGLFogState</code> subclasses the fog state using the LWJGL API
 * to set the OpenGL fog state.
 * @author Mark Powell
 * @version $Id: LWJGLFogState.java,v 1.1 2004-04-02 23:29:01 mojomonkey Exp $
 */
public class LWJGLFogState extends FogState {
    //buffer to hold the color
    FloatBuffer colorBuf;

    private int[] glFogDensity = { GL11.GL_LINEAR, GL11.GL_EXP, GL11.GL_EXP2 };

    private int[] glFogApply = { GL11.GL_FASTEST, GL11.GL_NICEST };

    /**
     * Constructor instantiates a new <code>LWJGLFogState</code> object with
     * default values.
     *
     */
    public LWJGLFogState() {
        super();
        colorBuf =
            ByteBuffer
                .allocateDirect(16)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
    }

    /**
     * <code>set</code> sets the OpenGL fog values if the state is enabled.
     * @see com.jme.scene.state.RenderState#set()
     */
    public void set() {
        if (isEnabled()) {
            GL11.glEnable(GL11.GL_FOG);
            GL11.glFogf(GL11.GL_FOG_START, start);
            GL11.glFogf(GL11.GL_FOG_END, end);

            colorBuf.clear();
            colorBuf.put(color.getColorArray());
            colorBuf.flip();

            GL11.glFog(GL11.GL_FOG_COLOR, colorBuf);

            GL11.glFogf(GL11.GL_FOG_DENSITY, density);
            GL11.glFogi(GL11.GL_FOG_MODE, glFogDensity[densityFunction]);
            GL11.glHint(GL11.GL_FOG_HINT, glFogApply[applyFunction]);
        } else {
            GL11.glDisable(GL11.GL_FOG);
        }
    }

    /**
     * <code>unset</code> disables the fog state if it was enabled previously.
     * @see com.jme.scene.state.RenderState#unset()
     */
    public void unset() {
        if (isEnabled()) {
            GL11.glDisable(GL11.GL_FOG);
        }

    }

}
