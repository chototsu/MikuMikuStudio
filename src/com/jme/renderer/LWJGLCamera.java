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
package com.jme.renderer;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLU;

import com.jme.math.Vector3f;

/**
 * <code>LWJGLCamera</code> defines a concrete implementation of a 
 * <code>AbstractCamera</code> using the LWJGL library for view port setting.
 * Most functionality is provided by the <code>AbstractCamera</code> class with
 * this class handling the OpenGL specific calls to set the frustum and 
 * viewport.
 * @author Mark Powell
 * @version $Id: LWJGLCamera.java,v 1.1 2003-10-02 15:01:17 mojomonkey Exp $
 */
public class LWJGLCamera extends AbstractCamera {

    private int width;
    private int height;

    /**
     * Constructor instantiates a new <code>LWJGLCamera</code> object. The 
     * width and height are provided, which cooresponds to either the 
     * width and height of the rendering window, or the resolution of the
     * fullscreen display.
     * @param width the width/resolution of the display.
     * @param height the height/resolution of the display.
     */
    public LWJGLCamera(int width, int height) {
        super();
        this.width = width;
        this.height = height;
        onFrustumChange();
        onViewPortChange();
        onFrameChange();
    }

    /**
     * <code>onFrustumChange</code> updates the frustum when needed. It calls
     * super to set the new frustum values then sets the OpenGL frustum.
     * @see com.jme.renderer.Camera#onFrustumChange()
     */
    public void onFrustumChange() {
        super.onFrustumChange();

        // set projection matrix
        GL.glMatrixMode(GL.GL_PROJECTION);
        GL.glLoadIdentity();
        GL.glFrustum(
            frustumLeft,
            frustumRight,
            frustumBottom,
            frustumTop,
            frustumNear,
            frustumFar);

    }

    /**
     * <code>onViewportChange</code> updates the viewport when needed. It 
     * calculates the viewport coordinates and then calls OpenGL's viewport.
     * @see com.jme.renderer.Camera#onViewPortChange()
     */
    public void onViewPortChange() {
        // set view port
        int x = (int) (viewPortLeft * width);
        int y = (int) (viewPortBottom * height);
        int w = (int) ((viewPortRight - viewPortLeft) * width);
        int h = (int) ((viewPortTop - viewPortBottom) * height);
        GL.glViewport(x, y, w, h);
    }

    /**
     * <code>onFrameChange</code> updates the view frame when needed. It calls
     * super to update the data and then uses GLU's lookat function to set the
     * OpenGL frame.
     * @see com.jme.renderer.Camera#onFrameChange()
     */
    public void onFrameChange() {
        super.onFrameChange();

        // set view matrix
        GL.glMatrixMode(GL.GL_MODELVIEW);
        GL.glLoadIdentity();
        Vector3f lookAt = location.add(direction);
        GLU.gluLookAt(
            location.x,
            location.y,
            location.z,
            lookAt.x,
            lookAt.y,
            lookAt.z,
            up.x,
            up.y,
            up.z);
    }
}
