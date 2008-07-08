/*
 * Copyright (c) 2003-2008 jMonkeyEngine
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

package com.jme.renderer.jogl;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.jme.math.Matrix4f;
import com.jme.renderer.AbstractCamera;
import com.jme.scene.state.jogl.records.RendererRecord;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;

/**
 * <code>JOGLCamera</code> defines a concrete implementation of a
 * <code>AbstractCamera</code> using the JOGL library for view port setting.
 * Most functionality is provided by the <code>AbstractCamera</code> class with
 * this class handling the OpenGL specific calls to set the frustum and
 * viewport.
 * @author Mark Powell
 * @author Steve Vaughan - JOGL port
 * @version $Id$
 */
public class JOGLCamera extends AbstractCamera {

    private static final long serialVersionUID = 1L;

    public JOGLCamera() {}

    /**
     * Constructor instantiates a new <code>JOGLCamera</code> object. The
     * width and height are provided, which cooresponds to either the
     * width and height of the rendering window, or the resolution of the
     * fullscreen display.
     * @param width the width/resolution of the display.
     * @param height the height/resolution of the display.
     */
    public JOGLCamera(int width, int height) {
        super();
        this.width = width;
        this.height = height;
        update();
        apply();
    }

    /**
     * Constructor instantiates a new <code>JOGLCamera</code> object. The
     * width and height are provided, which cooresponds to either the
     * width and height of the rendering window, or the resolution of the
     * fullscreen display.
     * @param width the width/resolution of the display.
     * @param height the height/resolution of the display.
     */
    public JOGLCamera(int width, int height, boolean dataOnly) {
        super(dataOnly);
        this.width = width;
        this.height = height;
        setDataOnly(dataOnly);
        update();
        apply();
    }

    /**
     * @return the width/resolution of the display.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return the height/resolution of the display.
     */
    public int getWidth() {
        return width;
    }

    /**
     * <code>resize</code> resizes this cameras view with the given width/height.
     * This is similar to constructing a new camera, but reusing the same
     * Object.
     * @param width int
     * @param height int
     */
    public void resize(int width, int height) {
      this.width = width;
      this.height = height;
      onViewPortChange();
    }

    private boolean frustumDirty;
    private boolean viewPortDirty;
    private boolean frameDirty;

    public void apply() {
        if ( frustumDirty ) {
            doFrustumChange();
            frustumDirty = false;
        }
        if ( viewPortDirty ) {
            doViewPortChange();
            viewPortDirty = false;
        }
        if ( frameDirty ) {
            doFrameChange();
            frameDirty = false;
        }
    }

    @Override
    public void onFrustumChange() {
        super.onFrustumChange();
        frustumDirty = true;
    }

    public void onViewPortChange() {
        viewPortDirty = true;
    }

    @Override
    public void onFrameChange() {
        super.onFrameChange();
        frameDirty = true;
    }

    /**
     * Sets the OpenGL frustum.
     * @see com.jme.renderer.Camera#onFrustumChange()
     */
    public void doFrustumChange() {

        final GL gl = GLU.getCurrentGL();


        if (!isDataOnly()) {
            // set projection matrix
            RendererRecord matRecord = (RendererRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getRendererRecord();
            matRecord.switchMode(GL.GL_PROJECTION);
            gl.glLoadIdentity();
            if ( !isParallelProjection() )
            {
                gl.glFrustum(
                    frustumLeft,
                    frustumRight,
                    frustumBottom,
                    frustumTop,
                    frustumNear,
                    frustumFar);
            }
            else
            {
                gl.glOrtho(
                        frustumLeft,
                        frustumRight,
                        frustumTop,
                        frustumBottom,
                        frustumNear,
                        frustumFar);
            }
            if ( projection != null )
            {
                tmp_FloatBuffer.rewind();
                gl.glGetFloatv(GL.GL_PROJECTION_MATRIX, tmp_FloatBuffer); // TODO Check for float
                tmp_FloatBuffer.rewind();
                projection.readFloatBuffer( tmp_FloatBuffer );
            }
        }

    }

    /**
     * Sets OpenGL's viewport.
     * @see com.jme.renderer.Camera#onViewPortChange()
     */
    public void doViewPortChange() {

        final GL gl = GLU.getCurrentGL();


        if (!isDataOnly()) {
            // set view port
            int x = (int) (viewPortLeft * width);
            int y = (int) (viewPortBottom * height);
            int w = (int) ((viewPortRight - viewPortLeft) * width);
            int h = (int) ((viewPortTop - viewPortBottom) * height);
            gl.glViewport(x, y, w, h);
        }
    }

    /**
     * Uses GLU's lookat function to set the OpenGL frame.
     * @see com.jme.renderer.Camera#onFrameChange()
     */
    public void doFrameChange() {
        final GL gl = GLU.getCurrentGL();
        final GLU glu = new GLU();

        super.onFrameChange();

        if (!isDataOnly()) {
            // set view matrix
            RendererRecord matRecord = (RendererRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getRendererRecord();
            matRecord.switchMode(GL.GL_MODELVIEW);
            gl.glLoadIdentity();
            glu.gluLookAt(
                location.x,
                location.y,
                location.z,
                location.x + direction.x,
                location.y + direction.y,
                location.z + direction.z,
                up.x,
                up.y,
                up.z);

            if ( modelView != null )
            {
                tmp_FloatBuffer.rewind();
                gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, tmp_FloatBuffer); // TODO Check for float
                tmp_FloatBuffer.rewind();
                modelView.readFloatBuffer( tmp_FloatBuffer );
            }
        }
    }

    private static final FloatBuffer tmp_FloatBuffer = BufferUtils.createFloatBuffer(16);
    private Matrix4f projection;

    public Matrix4f getProjectionMatrix() {
        if ( projection == null )
        {
            projection = new Matrix4f();
            doFrustumChange();
        }
        return projection;
    }

    private Matrix4f modelView;

    public Matrix4f getModelViewMatrix() {
        if ( modelView == null )
        {
            modelView = new Matrix4f();
            doFrameChange();
        }
        return modelView;
    }
}
