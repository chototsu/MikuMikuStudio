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
import java.util.logging.Level;

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.util.LoggingSystem;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.RenderTexture;
import org.lwjgl.opengl.Window;

/**
 * @author Joshua Slack
 * @version $Id: LWJGLTextureRenderer.java,v 1.12 2004-03-08 17:20:51 renanse Exp $
 */
public class LWJGLTextureRenderer implements TextureRenderer {

    private LWJGLCamera camera;
    private ColorRGBA backgroundColor = new ColorRGBA(1,1,1,1);

    private int PBUFFER_WIDTH = 256;
    private int PBUFFER_HEIGHT = 256;

    /** Pbuffer instance */
    private Pbuffer pbuffer;
    private int active, caps;
    private boolean useDirectRender = false;

    private LWJGLRenderer parentRenderer;
    private RenderTexture texture;

    public LWJGLTextureRenderer(LWJGLRenderer parentRenderer) {
        this(parentRenderer, null);
    }

    public LWJGLTextureRenderer(LWJGLRenderer parentRenderer, RenderTexture texture) {
        caps = Pbuffer.getPbufferCaps();

        if (((caps & Pbuffer.PBUFFER_SUPPORTED) == 0)) {
            LoggingSystem.getLogger().log(Level.SEVERE, "No Pbuffer support detected, exiting!");
            System.exit(1);  // Clean this up?
        }

        if ((caps & Pbuffer.RENDER_TEXTURE_SUPPORTED) != 0) {
            System.err.println("Render to Texture Pbuffer supported!");
            if (texture == null)
                System.err.println("No RenderTexture used in init, falling back to Copy Texture PBuffer.");
            LoggingSystem.getLogger().log(Level.INFO, "Copy Texture Pbuffer supported!");
            else useDirectRender = true;
        } else {
            LoggingSystem.getLogger().log(Level.INFO, "Copy Texture Pbuffer supported!");
            texture = null;
        }

        this.parentRenderer = parentRenderer;
        this.texture = texture;
        initPbuffer();
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
        GL11.glClearColor(
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
        GL11.glGenTextures(ibuf);
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
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTextureID);
        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 0, 0, PBUFFER_WIDTH, PBUFFER_HEIGHT, 0);

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
                LoggingSystem.getLogger().log(Level.WARNING, "PBuffer contents lost - will recreate the buffer");
                deactivate();
                pbuffer.destroy();
                initPbuffer();
            }

            if (useDirectRender) {
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.getTextureId());
                pbuffer.releaseTexImage(Pbuffer.FRONT_LEFT_BUFFER);
                activate();
                parentRenderer.clearBuffers();
                parentRenderer.draw(spat);
                deactivate();
                pbuffer.bindTexImage(Pbuffer.FRONT_LEFT_BUFFER);
            } else {
                activate();
                parentRenderer.clearBuffers();
                parentRenderer.draw(spat);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.getTextureId());
                GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, PBUFFER_WIDTH, PBUFFER_HEIGHT);
                deactivate();
            }

        } catch (Exception e) {
            LoggingSystem.getLogger().throwing(this.getClass().toString(), "render(Spatial, Texture)", e);
        }
    }


    private void initPbuffer() {

        try {
            pbuffer = new Pbuffer(PBUFFER_WIDTH, PBUFFER_HEIGHT, 32, 0, 8, 0, 0, texture);
        } catch (Exception e) {
            LoggingSystem.getLogger().throwing(this.getClass().toString(), "initPbuffer()", e);
            if (texture != null && useDirectRender) {
                LoggingSystem.getLogger().log(Level.WARNING, "LWJGL reports this card supports Render to Texture, but fails to enact it.  Please report this to the LWJGL team.");
                LoggingSystem.getLogger().log(Level.WARNING, "Attempting to fall back to Copy Texture.");
                texture = null;
                useDirectRender = false;
                initPbuffer();
                return;
            }
        }
        try {
            activate();

            PBUFFER_WIDTH = pbuffer.getWidth();
            PBUFFER_HEIGHT = pbuffer.getHeight();

            GL11.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);

            if (camera == null) initCamera();
            camera.update();

            deactivate();
            parentRenderer.getCamera().update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void activate() {
        if (active == 0) {
            pbuffer.makeCurrent();
        }
        active++;
    }

    public void deactivate() {
        if (active == 1) {
            Window.makeCurrent();
        }
        active--;
    }

    private void initCamera() {
        camera = new LWJGLCamera(PBUFFER_WIDTH, PBUFFER_HEIGHT, this);
        camera.setFrustum(1.0f, 1000.0f, -0.50f, 0.50f, 0.50f, -0.50f);
        Vector3f loc = new Vector3f(0.0f, 0.0f, 0.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        camera.setFrame(loc, left, up, dir);
    }
}
