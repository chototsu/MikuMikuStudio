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

package com.jme.renderer.lwjgl;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.opengl.RenderTexture;

import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.Spatial;
import com.jme.scene.state.lwjgl.LWJGLTextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.system.lwjgl.LWJGLDisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;
import com.jmex.awt.lwjgl.LWJGLCanvas;

/**
 * This class is used by LWJGL to render textures. Users should <b>not </b>
 * create this class directly. Instead, allow DisplaySystem to create it for
 * you.
 * 
 * @author Joshua Slack, Mark Powell
 * @version $Id: LWJGLPbufferTextureRenderer.java,v 1.9 2007/08/20 20:53:29 nca Exp $
 * @see com.jme.system.DisplaySystem#createTextureRenderer
 */
public class LWJGLPbufferTextureRenderer implements TextureRenderer {
    private static final Logger logger = Logger.getLogger(LWJGLPbufferTextureRenderer.class.getName());

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

    public LWJGLPbufferTextureRenderer(int width, int height,
            LWJGLRenderer parentRenderer, RenderTexture texture) {

        this(width, height, parentRenderer, texture, DisplaySystem
                .getDisplaySystem().getBitDepth(), DisplaySystem
                .getDisplaySystem().getMinAlphaBits(), DisplaySystem
                .getDisplaySystem().getMinDepthBits(), DisplaySystem
                .getDisplaySystem().getMinStencilBits(), DisplaySystem
                .getDisplaySystem().getMinSamples());
    }

    public LWJGLPbufferTextureRenderer(int width, int height,
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
            pBufferWidth = width;
            pBufferHeight = height;

            this.texture = texture;
            this.parentRenderer = parentRenderer;
            this.display = (LWJGLDisplaySystem) DisplaySystem
                    .getDisplaySystem();

            setMultipleTargets(false);

            logger.info("Creating Pbuffer sized: "+pBufferWidth+" x "+pBufferHeight);
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
    public void setupTexture(Texture2D tex) {
        setupTexture(tex, pBufferWidth, pBufferHeight);
    }

    /**
     * <code>setupTexture</code> initializes a new Texture object for use with
     * TextureRenderer. Generates a valid gl texture id for this texture and
     * inits the data type for the texture.
     */
    public void setupTexture(Texture2D tex, int width, int height) {
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
        LWJGLTextureState.doTextureBind(tex.getTextureId(), 0, Texture.Type.TwoDimensional);

        int source = GL11.GL_RGBA;
        switch (tex.getRTTSource()) {
            case RGBA: break;
            case RGB: source = GL11.GL_RGB; break;
            case Alpha: source = GL11.GL_ALPHA; break;
            case Depth: source = GL11.GL_DEPTH_COMPONENT; break;
            case Intensity: source = GL11.GL_INTENSITY; break;
            case Luminance: source = GL11.GL_LUMINANCE; break;
            case LuminanceAlpha: source = GL11.GL_LUMINANCE_ALPHA; break;
        }
        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, source, 0, 0, width, height, 0);
        logger.info("setup tex" + tex.getTextureId() + ": " + width + ","
                + height);
    }

    public void render(Spatial spat, Texture tex) {
        render(spat, tex, true);
    }
    /**
     * <code>render</code> renders a scene. As it recieves a base class of
     * <code>Spatial</code> the renderer hands off management of the scene to
     * spatial for it to determine when a <code>Geometry</code> leaf is
     * reached. The result of the rendering is then copied into the given
     * texture(s). What is copied is based on the Texture object's rttSource
     * field. 
     * 
     * NOTE: If more than one texture is given, copy-texture is used
     * regardless of card capabilities to decrease render time.
     * 
     * @param spat
     *            the scene to render.
     * @param tex
     *            the Texture(s) to render it to.
     */
    public void render(Spatial spat, Texture tex, boolean doClear) {
        if (!isSupported) {
            return;
        }
        
        // clear the current states since we are renderering into a new location
        // and can not rely on states still being set.
        try {
            if (pbuffer.isBufferLost()) {
                logger
                        .warning("PBuffer contents lost - will recreate the buffer");
                deactivate();
                pbuffer.destroy();
                initPbuffer();
            }

            // Override parent's last frustum test to avoid accidental incorrect
            // cull
            if (spat.getParent() != null)
                spat.getParent().setLastFrustumIntersection(
                        Camera.FrustumIntersect.Intersects);

            if (useDirectRender
                    && tex.getRTTSource() != Texture.RenderToTextureType.Depth) {
                // setup and render directly to a 2d texture.
                pbuffer.releaseTexImage(Pbuffer.FRONT_LEFT_BUFFER);
                activate();
                switchCameraIn(doClear);
                doDraw(spat);
                deactivate();
                switchCameraOut();
                LWJGLTextureState.doTextureBind(tex.getTextureId(), 0, Texture.Type.TwoDimensional);
                pbuffer.bindTexImage(Pbuffer.FRONT_LEFT_BUFFER);
            } else {
                // render and copy to a texture
                activate();
                switchCameraIn(doClear);
                doDraw(spat);
                switchCameraOut();
                
                copyToTexture(tex, pBufferWidth, pBufferHeight);
                
                deactivate();
            }

        } catch (Exception e) {
            logger.logp(Level.SEVERE, this.getClass().toString(),
                    "render(Spatial, Texture)", "Exception", e);
        }
    }

    // inherited docs
    public void render(ArrayList<? extends Spatial> spats, ArrayList<Texture> texs) {
        render(spats, texs, true);
    }
    public void render(ArrayList<? extends Spatial> spats, ArrayList<Texture> texs, boolean doClear) {
        if (!isSupported) {
            return;
        }
        
        // clear the current states since we are renderering into a new location
        // and can not rely on states still being set.
        try {
            if (pbuffer.isBufferLost()) {
                logger
                        .warning("PBuffer contents lost - will recreate the buffer");
                deactivate();
                pbuffer.destroy();
                initPbuffer();
            }

            if (texs.size() == 1 && useDirectRender
                    && texs.get(0).getRTTSource() != Texture.RenderToTextureType.Depth) {
                // setup and render directly to a 2d texture.
                LWJGLTextureState.doTextureBind(texs.get(0).getTextureId(), 0, Texture.Type.TwoDimensional);
                activate();
                switchCameraIn(doClear);
                pbuffer.releaseTexImage(Pbuffer.FRONT_LEFT_BUFFER);
                for (int x = 0, max = spats.size(); x < max; x++) {
                    Spatial spat = spats.get(x);
                    // Override parent's last frustum test to avoid accidental incorrect
                    // cull
                    if (spat.getParent() != null)
                        spat.getParent().setLastFrustumIntersection(
                                Camera.FrustumIntersect.Intersects);

                    doDraw(spat);
                }
                switchCameraOut();

                deactivate();
                pbuffer.bindTexImage(Pbuffer.FRONT_LEFT_BUFFER);
            } else {
                // render and copy to a texture
                activate();
                switchCameraIn(doClear);
                for (int x = 0, max = spats.size(); x < max; x++) {
                    Spatial spat = spats.get(x);
                    // Override parent's last frustum test to avoid accidental incorrect
                    // cull
                    if (spat.getParent() != null)
                        spat.getParent().setLastFrustumIntersection(
                                Camera.FrustumIntersect.Intersects);

                    doDraw(spat);
                }
                switchCameraOut();

                for (int i = 0; i < texs.size(); i++) {
                    copyToTexture(texs.get(i), pBufferWidth, pBufferHeight);
                }
                
                deactivate();
            }

        } catch (Exception e) {
            logger.logp(Level.SEVERE, this.getClass().toString(),
                    "render(Spatial, Texture)", "Exception", e);
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
        LWJGLTextureState.doTextureBind(tex.getTextureId(), 0, Texture.Type.TwoDimensional);

        int source = GL11.GL_RGBA;
        switch (tex.getRTTSource()) {
            case RGBA: break;
            case RGB: source = GL11.GL_RGB; break;
            case Alpha: source = GL11.GL_ALPHA; break;
            case Depth: source = GL11.GL_DEPTH_COMPONENT; break;
            case Intensity: source = GL11.GL_INTENSITY; break;
            case Luminance: source = GL11.GL_LUMINANCE; break;
            case LuminanceAlpha: source = GL11.GL_LUMINANCE_ALPHA; break;
        }
        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, source, 0, 0, width, height, 0);
    }

    private Camera oldCamera;
    private int oldWidth, oldHeight;
    private void switchCameraIn(boolean doClear) {
        // grab non-rtt settings
        oldCamera = parentRenderer.getCamera();
        oldWidth = parentRenderer.getWidth();
        oldHeight = parentRenderer.getHeight();
        parentRenderer.setCamera(getCamera());

        // swap to rtt settings
        parentRenderer.getQueue().swapBuckets();
        parentRenderer.reinit(pBufferWidth, pBufferHeight);

        // clear the scene
        if (doClear) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            parentRenderer.clearBuffers();
        }

        getCamera().update();
        getCamera().apply();
    }
    
    private void switchCameraOut() {
        parentRenderer.setCamera(oldCamera);
        parentRenderer.reinit(oldWidth, oldHeight);

        // back to the non rtt settings
        parentRenderer.getQueue().swapBuckets();
        oldCamera.update();
        oldCamera.apply();
    }
    
    private void doDraw(Spatial spat) {
        // do rtt scene render
        spat.onDraw(parentRenderer);
        parentRenderer.renderQueue();
    }

    private void initPbuffer() {
        if (!isSupported) {
            return;
        }

        try {
            if (pbuffer != null) {
                giveBackContext();
                DisplaySystem.getDisplaySystem().removeContext(pbuffer);
            }
            pbuffer = new Pbuffer(pBufferWidth, pBufferHeight, new PixelFormat(
                    bpp, alpha, depth, stencil, samples), texture, null);
        } catch (Exception e) {
            logger.logp(Level.SEVERE, this.getClass().toString(), "initPbuffer()", "Exception", e);

            if (texture != null && useDirectRender) {
                logger.warning("Your card claims to support Render to Texture but fails to enact it.  Updating your driver might solve this problem.");
                logger.warning("Attempting to fall back to Copy Texture.");
                texture = null;
                useDirectRender = false;
                initPbuffer();
                return;
            }

            logger.log(Level.WARNING, "Failed to create Pbuffer.", e);
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
		} catch( Exception e ) {
			logger.log(Level.WARNING, "Failed to initialize created Pbuffer.",
                    e);
			isSupported = false;
			return;
		}
	}

    public void activate() {
        if (!isSupported) {
            return;
        }
        if (active == 0) {
            try {
                pbuffer.makeCurrent();
                display.switchContext(pbuffer);
            } catch (LWJGLException e) {
                logger.logp(Level.SEVERE, this.getClass().toString(), "activate()", "Exception",
                        e);
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
                if (!useDirectRender)
                    display.getCurrentContext().invalidateStates();
                giveBackContext();
                ((LWJGLRenderer)display.getRenderer()).reset();
            } catch (LWJGLException e) {
                logger.logp(Level.SEVERE, this.getClass().toString(),
                        "deactivate()", "Exception", e);
                throw new JmeException();
            }
        }
        active--;
    }

    private void giveBackContext() throws LWJGLException {
        if (!headless && Display.isCreated()) {
            Display.makeCurrent();
            display.switchContext(display);
        } else if (display.getCurrentCanvas() != null) {
            ((LWJGLCanvas)display.getCurrentCanvas()).makeCurrent();
            display.switchContext(display.getCurrentCanvas());
        } else if (display.getHeadlessDisplay() != null) {
            display.getHeadlessDisplay().makeCurrent();
            display.switchContext(display.getHeadlessDisplay());
        }
    }

    private void initCamera() {
        if (!isSupported) {
            return;
        }
        logger.info("Init RTT camera");
        camera = new LWJGLCamera(pBufferWidth, pBufferHeight, false);
        camera.setFrustum(1.0f, 1000.0f, -0.50f, 0.50f, 0.50f, -0.50f);
        Vector3f loc = new Vector3f(0.0f, 0.0f, 0.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        camera.setFrame(loc, left, up, dir);
    }

    public void cleanup() {
        if (!isSupported) {
            return;
        }

        display.removeContext(pbuffer);
        pbuffer.destroy();
    }

    public int getWidth() {
        return pBufferWidth;
    }

    public int getHeight() {
        return pBufferHeight;
    }
    
    public void setMultipleTargets(boolean force) {
        if (force) {
            logger.info("Copy Texture Pbuffer used!");
            useDirectRender = false;
            texture = null;
            initPbuffer();
        } else {
            if ((caps & Pbuffer.RENDER_TEXTURE_SUPPORTED) != 0) {
                logger.info("Render to Texture Pbuffer supported!");
                if (texture == null) {
                    logger.info("No RenderTexture used in init, falling back to Copy Texture PBuffer.");
                    useDirectRender = false;
                } else {
                    useDirectRender = true;
                }
            } else {
                logger.info("Copy Texture Pbuffer supported!");
                texture = null;
            }
        }
    }
}