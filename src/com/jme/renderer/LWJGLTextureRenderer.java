/*
 * Copyright (c) 2004, jMonkeyEngine - Mojo Monkey Coding
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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.util.LoggingSystem;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.Pbuffer;

/**
 * @author Joshua Slack
 * @version $Id: LWJGLTextureRenderer.java,v 1.2 2004-03-04 19:43:26 renanse Exp $
 */
public class LWJGLTextureRenderer implements TextureRenderer {

    private LWJGLCamera camera;
    private ColorRGBA backgroundColor = new ColorRGBA(1,1,1,1);

    private int PBUFFER_WIDTH = 512;
    private int PBUFFER_HEIGHT = 512;

    /** Pbuffer instance */
    private Pbuffer pbuffer;

    private LWJGLRenderer parentRenderer;

    public LWJGLTextureRenderer(LWJGLRenderer parentRenderer) {
        if (!isSupported()) {
            System.err.println("No Pbuffer support!");
            System.exit(1);  // Clean this up.
        } else
            System.err.println("Pbuffer support detected");

        this.parentRenderer = parentRenderer;
        initPbuffer();
    }

    public static boolean isSupported() { // Perhaps we should move this to Renderer?
        if ((Pbuffer.getPbufferCaps() & Pbuffer.PBUFFER_SUPPORTED) == 0)
            return false;
        else return true;
    }

    /**
     * <code>getCamera</code> retrieves the camera this renderer is using.
     * @return the camera this renderer is using.
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * <code>setBackgroundColor</code> sets the OpenGL clear color to the
     * color specified.
     *
     * @see com.jme.renderer.TextureRenderer#setBackgroundColor(com.jme.renderer.ColorRGBA)
     * @param c
     *            the color to set the background color to.
     */
    public void setBackgroundColor(ColorRGBA c) {
        //if color is null set background to white.
        if (c == null) {
            backgroundColor.a = 1.0f;
            backgroundColor.b = 1.0f;
            backgroundColor.g = 1.0f;
            backgroundColor.r = 1.0f;
        } else {
            backgroundColor = c;
        }

        activate();
        GL.glClearColor(
            backgroundColor.r,
            backgroundColor.g,
            backgroundColor.b,
            backgroundColor.a);
        deactivate();
    }

    /**
     * <code>getBackgroundColor</code> retrieves the clear color of the
     * current OpenGL context.
     *
     * @see com.jme.renderer.Renderer#getBackgroundColor()
     * @return the current clear color.
     */
    public ColorRGBA getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * <code>setupTexture</code> generates a new Texture object for use with
     * TextureRenderer.  Generates a valid gl texture id for this texture.
     * @return the new Texture
     */
    public Texture setupTexture() {
        IntBuffer ibuf =
            ByteBuffer
                .allocateDirect(4)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer();

        //Create the texture
        GL.glGenTextures(ibuf);
        int glTextureID = ibuf.get(0);

        return setupTexture(glTextureID);
    }

    /**
     * <code>setupTexture</code> retrieves the color used for the
     * window background.
     * @param glTextureID a valid gl texture id to use
     * @return the new Texture
     */
    public Texture setupTexture(int glTextureID) {
        GL.glBindTexture(GL.GL_TEXTURE_2D, glTextureID);
        GL.glCopyTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 0, 0, 512, 512, 0);

        Texture rVal = new Texture();
        rVal.setTextureId(glTextureID);
        rVal.setBlendColor(new ColorRGBA(1, 1, 1, 1f));
        return rVal;
    }

    /**
     * <code>render</code> renders a scene. As it recieves a base class of
     * <code>Spatial</code> the renderer hands off management of the
     * scene to spatial for it to determine when a <code>Geometry</code>
     * leaf is reached.  All of this is done in the context of the underlying
     * texture buffer.
     * @param spat the scene to render.
     * @param tex the Texture to render it to.
     */
    public void render(Spatial spat, Texture tex) {
        try {
            if (pbuffer.isBufferLost()) {
                System.out.println("Buffer contents lost - will recreate the buffer");
                deactivate();
                pbuffer.destroy();
                initPbuffer();
            }

            activate();
            parentRenderer.clearBuffers();
            parentRenderer.draw(spat);
            GL.glBindTexture(GL.GL_TEXTURE_2D, tex.getTextureId());
            GL.glCopyTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0, 0, 0, PBUFFER_WIDTH, PBUFFER_HEIGHT);
            pbuffer.releaseContext();
        } catch (Exception e) {
            LoggingSystem.getLogger().throwing(this.getClass().toString(), "render(Spatial, Texture)", e);
        }
    }


    private void initPbuffer() {
        if (camera == null) initCamera();
        try {
            pbuffer = new Pbuffer(PBUFFER_WIDTH, PBUFFER_HEIGHT, 32, 0, 8, 0);
            activate();

            GL.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
            camera.update();

            deactivate();
            parentRenderer.getCamera().update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void activate() {
        pbuffer.makeCurrent();
    }

    private void deactivate() {
        Pbuffer.releaseContext();
    }

    private void initCamera() {
        camera = new LWJGLCamera(PBUFFER_WIDTH, PBUFFER_HEIGHT);
        camera.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        Vector3f loc = new Vector3f(0.0f, 0.0f, 75.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        camera.setFrame(loc, left, up, dir);
    }
}
