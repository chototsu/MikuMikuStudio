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

import org.lwjgl.opengl.GL11;

import com.jme.scene.state.WireframeState;

/**
 * <code>LWJGLWireframeState</code> subclasses WireframeState to use the
 * LWJGL API to access OpenGL. If the state is enabled, wireframe mode is
 * used, otherwise solid fill is used.
 * @author Mark Powell
 * @version $Id: LWJGLWireframeState.java,v 1.1 2004-04-02 23:29:03 mojomonkey Exp $
 */
public class LWJGLWireframeState extends WireframeState {
    
    /**
     * <code>set</code> sets the polygon mode to line or fill depending on
     * if the state is enabled or not.
     * @see com.jme.scene.state.RenderState#set()
     */
    public void set() {
        if (isEnabled()) {
            GL11.glLineWidth(lineWidth);
            switch (face) {
            case WS_FRONT_AND_BACK:
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                break;
            case WS_FRONT:
                GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
            case WS_BACK:
                GL11.glPolygonMode(GL11.GL_BACK, GL11.GL_LINE);
                break;
            default:
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                break;
            }
            
        }
    }

    /**
     * <code>unset</code> sets the render state to the opposite of what
     * set did.
     * @see com.jme.scene.state.RenderState#unset()
     */
    public void unset() {
        if (isEnabled()) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            GL11.glLineWidth(1.0f);
        }
    }

}