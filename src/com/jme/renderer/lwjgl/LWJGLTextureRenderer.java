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

package com.jme.renderer.lwjgl;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.logging.Level;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.opengl.RenderTexture;

import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.Spatial;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.system.lwjgl.LWJGLDisplaySystem;
import com.jme.util.LoggingSystem;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;
import com.jmex.awt.lwjgl.LWJGLCanvas;

/**
 * This class is used by LWJGL to render textures. Users should <b>not </b>
 * create this class directly. Instead, allow DisplaySystem to create it for
 * you.
 * 
 * @author Joshua Slack, Mark Powell
 * @version $Id: LWJGLTextureRenderer.java,v 1.14 2005/04/04 19:10:58 renanse
 *          Exp $
 * @see com.jme.system.DisplaySystem#createTextureRenderer(int, int, boolean,
 *      boolean, boolean, boolean, int, int)
 */
public class LWJGLTextureRenderer implements TextureRenderer {

    private LWJGLCamera camera;

    private ColorRGBA backgroundColor = new ColorRGBA(1, 1, 1, 1);

    private int pBufferWidth = 16;

    private int pBufferHeight = 16;

    /* Pbuffer instance */
    private Pbuffer pbuffer;

    private int active, caps;

    private boolean useDirectRender = false;

    private boolean isSupported = true;

    private LWJGLRenderer parentRenderer;

    private RenderTexture texture;

    private LWJGLDisplaySystem display;

    private boolean headless = false;

    private int bpp, alpha, depth, stencil, samples;

    public LWJGLTextureRenderer(int width, int height,
            LWJGLRenderer parentRenderer, RenderTexture texture) {

        this(width, height, parentRenderer, texture, DisplaySystem
                .getDisplaySystem().getBitDepth(), DisplaySystem
                .getDisplaySystem().getMinAlphaBits(), DisplaySystem
                .getDisplaySystem().getMinDepthBits(), DisplaySystem
                .getDisplaySystem().getMinStencilBits(), DisplaySystem
                .getDisplaySystem().getMinSamples());
    }

    public LWJGLTextureRenderer(int width, int height,
            LWJGLRenderer parentRenderer, RenderTexture texture, int bpp,
            int alpha, int depth, int stencil, int samples) {

        this.bpp = bpp;
        this.alpha = alpha;
        this.depth = depth;
        this.stencil = stencil;
        this.samples = samples;

        caps = Pbuffer.getCapabilities();

        if (((caps & Pbuffer.PBUFFER_SUPPORTED) != 0)) {
            isSupported = true;

            // Check if we have non-power of two sizes. If so,
            // find the smallest power of two size that is greater than
            // the provided size.
            if (!FastMath.isPowerOfTwo(width)) {
                int newWidth = 2;
                do {
                    newWidth <<= 1;

                } while (newWidth < width);
                width = newWidth;
            }

            if (!FastMath.isPowerOfTwo(height)) {
                int newHeight = 2;
                do {
                    newHeight <<= 1;

                } while (newHeight < height);
                height = newHeight;
            }

            if (width > 0)
                pBufferWidth = width;
            if (height > 0)
                pBufferHeight = height;

            if ((caps & Pbuffer.RENDER_TEXTURE_SUPPORTED) != 0) {
                LoggingSystem.getLogger().log(Level.INFO,
                        "Render to Texture Pbuffer supported!");
                if (texture == null) {
                    LoggingSystem
                            .getLogger()
                            .log(Level.INFO,
                                    "No RenderTexture used in init, falling back to Copy Texture PBuffer.");
                } else {
                    useDirectRender = true;
                    validateForCopy();
                }
            } else {
                LoggingSystem.getLogger().log(Level.INFO,
                        "Copy Texture Pbuffer supported!");
                texture = null;
                validateForCopy();
            }

            if (pBufferWidth != pBufferHeight
                    && (caps & Pbuffer.RENDER_TEXTURE_RECTANGLE_SUPPORTED) == 0) {
                pBufferWidth = pBufferHeight = Math.max(width, height);
            }

            this.parentRenderer = parentRenderer;
            this.display = (LWJGLDisplaySystem) DisplaySystem
                    .getDisplaySystem();
            this.texture = texture;
            initPbuffer();
        } else {
            isSupported = false;
        }
    }

    /**
     * 
     * <code>isSupported</code> obtains the capability of the graphics card.
     * If the graphics card does not have pbuffer support, false is returned,
     * otherwise, true is returned. TextureRenderer will not process any scene
     * elements if pbuffer is not supported.
     * 
     * @return if this graphics card supports pbuffers or not.
     */
    public boolean isSupported() {
        return isSupported;
    }

    /**
     * <code>getCamera</code> retrieves the camera this renderer is using.
     * 
     * @return the camera this renderer is using.
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * <code>setCamera</code> sets the camera this renderer should use.
     * 
     * @param camera
     *            the camera this renderer should use.
     */
    public void setCamera(Camera camera) {

        this.camera = (LWJGLCamera) camera;
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

        // if color is null set background to white.
        if (c == null) {
            backgroundColor.a = 1.0f;
            backgroundColor.b = 1.0f;
            backgroundColor.g = 1.0f;
            backgroundColor.r = 1.0f;
        } else {
            backgroundColor = c;
        }

        if (!isSupported) {
            return;
        }

        activate();
        GL11.glClearColor(backgroundColor.r, backgroundColor.g,
                backgroundColor.b, backgroundColor.a);
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
     * <code>setupTexture</code> initializes a new Texture object for use with
     * TextureRenderer. Generates a valid gl texture id for this texture and
     * inits the data type for the texture.
     */
    public void setupTexture(Texture tex) {
        setupTexture(tex, pBufferWidth, pBufferHeight);
    }

    /**
     * <code>setupTexture</code> initializes a new Texture object for use with
     * TextureRenderer. Generates a valid gl texture id for this texture and
     * inits the data type for the texture.
     */
    public void setupTexture(Texture tex, int width, int height) {
        if (!isSupported) {
            return;
        }

        IntBuffer ibuf = BufferUtils.createIntBuffer(1);

        if (tex.getTextureId() != 0) {
            ibuf.put(tex.getTextureId());
            GL11.glDeleteTextures(ibuf);
            ibuf.clear();
        }

        // Create the texture
        GL11.glGenTextures(ibuf);
        tex.setTextureId(ibuf.get(0));
        TextureManager.registerForCleanup(tex.getTextureKey(), tex.getTextureId());

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.getTextureId());

        int source = GL11.GL_RGBA;
        switch (tex.getRTTSource()) {
        case Texture.RTT_SOURCE_RGB: source = GL11.GL_RGB; break;
        case Texture.RTT_SOURCE_ALPHA: source = GL11.GL_ALPHA; break;
        case Texture.RTT_SOURCE_DEPTH: source = GL11.GL_DEPTH_COMPONENT; break;
        case Texture.RTT_SOURCE_INTENSITY: source = GL11.GL_INTENSITY; break;
        case Texture.RTT_SOURCE_LUMINANCE: source = GL11.GL_LUMINANCE; break;
        case Texture.RTT_SOURCE_LUMINANCE_ALPHA: source = GL11.GL_LUMINANCE_ALPHA; break;
        }
        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, source, 0, 0, width, height, 0);
    }

    /**
     * <code>render</code> renders a scene. As it recieves a base class of
     * <code>Spatial</code> the renderer hands off management of the scene to
     * spatial for it to determine when a <code>Geometry</code> leaf is
     * reached. The result of the rendering is then copied into the given
     * texture. What is copied is based on the Texture object's rttSource field.
     * 
     * @param spat
     *            the scene to render.
     * @param tex
     *            the Texture to render it to.
     */
    public void render(Spatial spat, Texture tex) {
        if (!isSupported) {
            return;
        }
        // clear the current states since we are renderering into a new location
        // and can not rely on states still being set.
        try {
            if (pbuffer.isBufferLost()) {
                LoggingSystem.getLogger().log(Level.WARNING,
                        "PBuffer contents lost - will recreate the buffer");
                deactivate();
                pbuffer.destroy();
                initPbuffer();
            }

            // Override parent's last frustum test to avoid accidental incorrect
            // cull
            if (spat.getParent() != null)
                spat.getParent().setLastFrustumIntersection(
                        Camera.INTERSECTS_FRUSTUM);

            if (!useDirectRender
                    || tex.getRTTSource() == Texture.RTT_SOURCE_DEPTH) {
                // render and copy to a texture
                activate();
                doDraw(spat);
                copyToTexture(tex, pBufferWidth, pBufferHeight);
                deactivate();
            } else {
                // setup and render directly to a 2d texture.
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.getTextureId());
                activate();
                pbuffer.releaseTexImage(Pbuffer.FRONT_LEFT_BUFFER);
                doDraw(spat);
                deactivate();
                pbuffer.bindTexImage(Pbuffer.FRONT_LEFT_BUFFER);
            }
            tex.setNeedsFilterRefresh(true);

        } catch (Exception e) {
            LoggingSystem.getLogger().throwing(this.getClass().toString(),
                    "render(Spatial, Texture)", e);
        }
    }

    /**
     * <code>render</code> renders a scene. As it recieves a base class of
     * <code>Spatial</code> the renderer hands off management of the scene to
     * spatial for it to determine when a <code>Geometry</code> leaf is
     * reached. The result of the rendering is then copied into the given
     * textures. What is copied is based on each Texture object's rttSource
     * field.
     * 
     * NOTE: Only copy-texture is supported when using this method.
     * 
     * @param spat
     *            the scene to render.
     * @param texs
     *            an array of Texture objects to render to.
     */
    public void render(Spatial spat, Texture[] texs) {
        if (!isSupported) {
            return;
        }
        // clear the current states since we are renderering into a new location
        // and can not rely on states still being set.
        try {
            if (pbuffer.isBufferLost()) {
                LoggingSystem.getLogger().log(Level.WARNING,
                        "PBuffer contents lost - will recreate the buffer");
                deactivate();
                pbuffer.destroy();
                initPbuffer();
            }

            // Override parent's last frustum test to avoid accidental incorrect
            // cull
            if (spat.getParent() != null)
                spat.getParent().setLastFrustumIntersection(
                        Camera.INTERSECTS_FRUSTUM);

            activate();
            doDraw(spat);

            for (int i = 0; i < texs.length; i++) {
                copyToTexture(texs[i], pBufferWidth, pBufferHeight);
                texs[i].setNeedsFilterRefresh(true);
            }

            deactivate();

        } catch (Exception e) {
            LoggingSystem.getLogger().throwing(this.getClass().toString(),
                    "render(Spatial, Texture[])", e);
        }
    }


    // inherited docs
    public void render(ArrayList spats, Texture tex) {
        if (!isSupported) {
            return;
        }
        // clear the current states since we are renderering into a new location
        // and can not rely on states still being set.
        try {
            if (pbuffer.isBufferLost()) {
                LoggingSystem.getLogger().log(Level.WARNING,
                        "PBuffer contents lost - will recreate the buffer");
                deactivate();
                pbuffer.destroy();
                initPbuffer();
            }

            if (!useDirectRender
                    || tex.getRTTSource() == Texture.RTT_SOURCE_DEPTH) {
                // render and copy to a texture
                activate();
                for (int x = 0, max = spats.size(); x < max; x++) {
                    Spatial spat = (Spatial)spats.get(x);
                    // Override parent's last frustum test to avoid accidental incorrect
                    // cull
                    if (spat.getParent() != null)
                        spat.getParent().setLastFrustumIntersection(
                                Camera.INTERSECTS_FRUSTUM);

                    doDraw(spat);
                }
                copyToTexture(tex, pBufferWidth, pBufferHeight);
                deactivate();
            } else {
                // setup and render directly to a 2d texture.
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.getTextureId());
                activate();
                pbuffer.releaseTexImage(Pbuffer.FRONT_LEFT_BUFFER);
                for (int x = 0, max = spats.size(); x < max; x++) {
                    Spatial spat = (Spatial)spats.get(x);
                    // Override parent's last frustum test to avoid accidental incorrect
                    // cull
                    if (spat.getParent() != null)
                        spat.getParent().setLastFrustumIntersection(
                                Camera.INTERSECTS_FRUSTUM);

                    doDraw(spat);
                }
                deactivate();
                pbuffer.bindTexImage(Pbuffer.FRONT_LEFT_BUFFER);
            }
            tex.setNeedsFilterRefresh(true);

        } catch (Exception e) {
            LoggingSystem.getLogger().throwing(this.getClass().toString(),
                    "render(Spatial, Texture)", e);
        }
    }

    // inherited docs
    public void render(ArrayList spats, Texture[] texs) {
        if (!isSupported) {
            return;
        }
        // clear the current states since we are renderering into a new location
        // and can not rely on states still being set.
        try {
            if (pbuffer.isBufferLost()) {
                LoggingSystem.getLogger().log(Level.WARNING,
                        "PBuffer contents lost - will recreate the buffer");
                deactivate();
                pbuffer.destroy();
                initPbuffer();
            }

            activate();
            for (int x = 0, max = spats.size(); x < max; x++) {
                Spatial spat = (Spatial)spats.get(x);
                // Override parent's last frustum test to avoid accidental incorrect
                // cull
                if (spat.getParent() != null)
                    spat.getParent().setLastFrustumIntersection(
                            Camera.INTERSECTS_FRUSTUM);

                doDraw(spat);
            }

            for (int i = 0; i < texs.length; i++) {
                copyToTexture(texs[i], pBufferWidth, pBufferHeight);
                texs[i].setNeedsFilterRefresh(true);
            }

            deactivate();

        } catch (Exception e) {
            LoggingSystem.getLogger().throwing(this.getClass().toString(),
                    "render(Spatial, Texture[])", e);
        }
    }

    /**
     * <code>copyToTexture</code> copies the pbuffer contents to
     * the given Texture. What is copied is up to the Texture object's rttSource
     * field.
     * 
     * @param tex
     *            The Texture to copy into.
     * @param width
     *            the width of the texture image
     * @param height
     *            the height of the texture image
     */
    public void copyToTexture(Texture tex, int width, int height) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.getTextureId());
//        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, width, height);

        int source = GL11.GL_RGBA;
        switch (tex.getRTTSource()) {
        case Texture.RTT_SOURCE_RGB: source = GL11.GL_RGB; break;
        case Texture.RTT_SOURCE_ALPHA: source = GL11.GL_ALPHA; break;
        case Texture.RTT_SOURCE_DEPTH: source = GL11.GL_DEPTH_COMPONENT; break;
        case Texture.RTT_SOURCE_INTENSITY: source = GL11.GL_INTENSITY; break;
        case Texture.RTT_SOURCE_LUMINANCE: source = GL11.GL_LUMINANCE; break;
        case Texture.RTT_SOURCE_LUMINANCE_ALPHA: source = GL11.GL_LUMINANCE_ALPHA; break;
        }
        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, source, 0, 0, width, height, 0);
    }

    /**
     * <code>copyToTexture</code> copies the current frame buffer contents to
     * the given Texture. What is copied is up to the Texture object's rttSource
     * field.
     * 
     * @param tex
     *            The Texture to copy into.
     * @param width
     *            the width of the texture image
     * @param height
     *            the height of the texture image
     */
    public void copyBufferToTexture(Texture tex, int width, int height, int buffer) {
        GL11.glReadBuffer(GL11.GL_BACK);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.getTextureId());

        int source = GL11.GL_RGBA;
        switch (tex.getRTTSource()) {
        case Texture.RTT_SOURCE_RGB: source = GL11.GL_RGB; break;
        case Texture.RTT_SOURCE_ALPHA: source = GL11.GL_ALPHA; break;
        case Texture.RTT_SOURCE_DEPTH: source = GL11.GL_DEPTH_COMPONENT; break;
        case Texture.RTT_SOURCE_INTENSITY: source = GL11.GL_INTENSITY; break;
        case Texture.RTT_SOURCE_LUMINANCE: source = GL11.GL_LUMINANCE; break;
        case Texture.RTT_SOURCE_LUMINANCE_ALPHA: source = GL11.GL_LUMINANCE_ALPHA; break;
        }
        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, source, 0, 0, width, height, 0);
    }

    private void doDraw(Spatial spat) {
        // grab non-rtt settings
        Camera oldCamera = parentRenderer.getCamera();
        int oldWidth = parentRenderer.getWidth();
        int oldHeight = parentRenderer.getHeight();

        // swap to rtt settings
        parentRenderer.setCamera(getCamera());
        parentRenderer.reinit(pBufferWidth, pBufferHeight);

        // Clear the states.
        Renderer.clearCurrentStates();
        Renderer.applyDefaultStates();

        // do rtt scene render
        parentRenderer.clearBuffers();
        parentRenderer.getQueue().swapBuckets();
        spat.onDraw(parentRenderer);
        parentRenderer.renderQueue();

        // back to the non rtt settings
        parentRenderer.getQueue().swapBuckets();
        parentRenderer.setCamera(oldCamera);
        parentRenderer.reinit(oldWidth, oldHeight);

        // Clear the states again since we will be moving back to the old
        // location and don't want the states bleeding over causing things
        // *not* to be set when they should be.
        Renderer.clearCurrentStates();
        Renderer.applyDefaultStates();
    }

    private void initPbuffer() {
        if (!isSupported) {
            return;
        }

        try {
            pbuffer = new Pbuffer(pBufferWidth, pBufferHeight, new PixelFormat(
                    bpp, alpha, depth, stencil, samples), texture, null);
        } catch (Exception e) {
            LoggingSystem.getLogger().throwing(this.getClass().toString(),
                    "initPbuffer()", e);
            if (texture != null && useDirectRender) {
                LoggingSystem
                        .getLogger()
                        .log(
                                Level.WARNING,
                                "LWJGL reports this card supports Render to Texture, but fails to enact it.  Please report this to the LWJGL team.");
                LoggingSystem.getLogger().log(Level.WARNING,
                        "Attempting to fall back to Copy Texture.");
                texture = null;
                useDirectRender = false;
                initPbuffer();
                return;
            }
            
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Failed to create Pbuffer.", e);
            isSupported = false;
            return;            
        }

        try {
            activate();

            pBufferWidth = pbuffer.getWidth();
            pBufferHeight = pbuffer.getHeight();

            GL11.glClearColor(backgroundColor.r, backgroundColor.g,
                    backgroundColor.b, backgroundColor.a);

            if (camera == null)
                initCamera();
            camera.update();

            deactivate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void activate() {
        if (!isSupported) {
            return;
        }
        if (active == 0) {
            try {
                pbuffer.makeCurrent();
            } catch (LWJGLException e) {
                e.printStackTrace();
                throw new JmeException();
            }
        }
        active++;
    }

    public void deactivate() {
        if (!isSupported) {
            return;
        }
        if (active == 1) {
            try {
                if (!headless && Display.isCreated())
                    Display.makeCurrent();
                else if (display.getCurrentCanvas() != null)
                    ((LWJGLCanvas)display.getCurrentCanvas()).makeCurrent();
                else if (display.getHeadlessDisplay() != null)
                    display.getHeadlessDisplay().makeCurrent();
            } catch (LWJGLException e) {
                e.printStackTrace(); // To change body of catch statement use
                // File | Settings | File Templates.
                throw new JmeException();
            }
        }
        active--;
    }

    private void initCamera() {
        if (!isSupported) {
            return;
        }
        camera = new LWJGLCamera(pBufferWidth, pBufferHeight, this);
        camera.setFrustum(1.0f, 1000.0f, -0.50f, 0.50f, 0.50f, -0.50f);
        Vector3f loc = new Vector3f(0.0f, 0.0f, 0.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        camera.setFrame(loc, left, up, dir);
    }

    public void updateCamera() {
        if (!isSupported) {
            return;
        }
        activate();
        camera.update();
        deactivate();
    }

    public void cleanup() {
        if (!isSupported) {
            return;
        }

        pbuffer.destroy();
    }

    private void validateForCopy() {
        if (pBufferWidth > DisplaySystem.getDisplaySystem().getWidth()) {
            pBufferWidth = DisplaySystem.getDisplaySystem().getWidth();
        }

        if (pBufferHeight > DisplaySystem.getDisplaySystem().getHeight()) {
            pBufferHeight = DisplaySystem.getDisplaySystem().getHeight();
        }
    }

    public int getPBufferWidth() {
        return pBufferWidth;
    }

    public int getPBufferHeight() {
        return pBufferHeight;
    }
}