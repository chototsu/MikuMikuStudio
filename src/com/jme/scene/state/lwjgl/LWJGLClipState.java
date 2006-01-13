/*
 * Copyright (c) 2003-2006 jMonkeyEngine
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

package com.jme.scene.state.lwjgl;

import com.jme.scene.state.ClipState;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;

/**
 * <code>LWJGLClipState</code>
 */
public class LWJGLClipState extends ClipState {

    private static final long serialVersionUID = 1L;

    private DoubleBuffer buf;

    public LWJGLClipState() {
        buf = ByteBuffer.allocateDirect(8 * 4).order(ByteOrder.nativeOrder())
                .asDoubleBuffer();
    }

    /**
     * <code>apply</code>
     * 
     * @see com.jme.scene.state.ClipState#apply()
     */
    public void apply() {
        if (isEnabled()) {
            for (int i = 0; i < MAX_CLIP_PLANES; i++) {
                if (enabledClipPlanes[i]) {
                    buf.put(0, planeEquations[i][0]);
                    buf.put(1, planeEquations[i][1]);
                    buf.put(2, planeEquations[i][2]);
                    buf.put(3, planeEquations[i][3]);

                    int clipPlane = GL11.GL_CLIP_PLANE0;
                    switch (i) {
                    case CLIP_PLANE0:
                        clipPlane = GL11.GL_CLIP_PLANE0;
                        break;
                    case CLIP_PLANE1:
                        clipPlane = GL11.GL_CLIP_PLANE1;
                        break;
                    case CLIP_PLANE2:
                        clipPlane = GL11.GL_CLIP_PLANE2;
                        break;
                    case CLIP_PLANE3:
                        clipPlane = GL11.GL_CLIP_PLANE3;
                        break;
                    case CLIP_PLANE4:
                        clipPlane = GL11.GL_CLIP_PLANE4;
                        break;
                    case CLIP_PLANE5:
                        clipPlane = GL11.GL_CLIP_PLANE5;
                        break;
                    }

                    GL11.glEnable(clipPlane);
                    GL11.glClipPlane(clipPlane, buf);
                }
            }
        } else {
            for (int i = 0; i < MAX_CLIP_PLANES; i++) {
                int clipPlane = GL11.GL_CLIP_PLANE0;
                switch (i) {
                case CLIP_PLANE0:
                    clipPlane = GL11.GL_CLIP_PLANE0;
                    break;
                case CLIP_PLANE1:
                    clipPlane = GL11.GL_CLIP_PLANE1;
                    break;
                case CLIP_PLANE2:
                    clipPlane = GL11.GL_CLIP_PLANE2;
                    break;
                case CLIP_PLANE3:
                    clipPlane = GL11.GL_CLIP_PLANE3;
                    break;
                case CLIP_PLANE4:
                    clipPlane = GL11.GL_CLIP_PLANE4;
                    break;
                case CLIP_PLANE5:
                    clipPlane = GL11.GL_CLIP_PLANE5;
                    break;
                }

                GL11.glDisable(clipPlane);
            }
        }
    }
}