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
package com.jme.scene.state.lwjgl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import com.jme.scene.state.MaterialState;

/**
 * <code>LWJGLMaterialState</code> subclasses MaterialState using the
 * LWJGL API to access OpenGL to set the material for a given node and it's
 * children.
 * @author Mark Powell
 * @version $Id: LWJGLMaterialState.java,v 1.3 2004-04-22 22:26:58 renanse Exp $
 */
public class LWJGLMaterialState extends MaterialState {
    //buffer for color
    private FloatBuffer buffer;

    /**
     * Constructor instantiates a new <code>LWJGLMaterialState</code> object.
     *
     */
    public LWJGLMaterialState() {
        super();
        buffer =
            ByteBuffer
                .allocateDirect(16)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
    }

    /**
     * <code>set</code> calls the OpenGL material function to set the proper
     * material state.
     * @see com.jme.scene.state.RenderState#set()
     */
    public void apply() {
        if(isEnabled()) {
            float[] color = new float[4];
            color[3] = 1.0f;

            color[0] = getEmissive().r;
            color[1] = getEmissive().g;
            color[2] = getEmissive().b;

            buffer.clear();
            buffer.put(color);
            buffer.flip();

            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_EMISSION, buffer);

            color[0] = getAmbient().r;
            color[1] = getAmbient().g;
            color[2] = getAmbient().b;

            buffer.clear();
            buffer.put(color);
            buffer.flip();
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT, buffer);

            color[0] = getDiffuse().r;
            color[1] = getDiffuse().g;
            color[2] = getDiffuse().b;

            buffer.clear();
            buffer.put(color);
            buffer.flip();
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, buffer);

            color[0] = getSpecular().r;
            color[1] = getSpecular().g;
            color[2] = getSpecular().b;

            buffer.clear();
            buffer.put(color);
            buffer.flip();

            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, buffer);

            GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, getShininess());
        }
    }

}
