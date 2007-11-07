/*
 * Copyright (c) 2003-2007 jMonkeyEngine
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

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.RenderContext;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.Spatial;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.lwjgl.LWJGLTextureState;
import com.jme.scene.state.lwjgl.records.TextureRecord;
import com.jme.scene.state.lwjgl.records.TextureStateRecord;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;

/**
 * This class is used by LWJGL to render textures. Users should <b>not </b>
 * create this class directly. Instead, allow DisplaySystem to create it for
 * you.
 * 
 * @author Joshua Slack, Mark Powell
 * @version $Id: LWJGLTextureRenderer.java,v 1.51 2007-11-07 23:07:24 nca Exp $
 * @see com.jme.system.DisplaySystem#createTextureRenderer
 */
public class LWJGLTextureRenderer implements TextureRenderer {
    private static final Logger logger = Logger.getLogger(LWJGLTextureRenderer.class.getName());

    private LWJGLCamera camera;

    private ColorRGBA backgroundColor = new ColorRGBA(1, 1, 1, 1);

    private int active, fboID, depthRBID, width, height;

    private boolean isSupported = true;

    private LWJGLRenderer parentRenderer;

    public LWJGLTextureRenderer(int width, int height,
            LWJGLRenderer parentRenderer) {
        
        isSupported = GLContext.getCapabilities().GL_EXT_framebuffer_object;
        if (!isSupported) {
            logger.warning("FBO not supported.");
            return;
        } else {
            logger.info("FBO support detected.");
        }

        if (!GLContext.getCapabilities().GL_ARB_texture_non_power_of_two) {
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
        }
        
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        EXTFramebufferObject.glGenFramebuffersEXT( buffer ); // generate id
        fboID = buffer.get(0);
        
        if (fboID <= 0) {
            logger.severe("Invalid FBO id returned! " + fboID);
            isSupported = false;
            return;
        }

        EXTFramebufferObject.glGenRenderbuffersEXT( buffer ); // generate id
        depthRBID = buffer.get(0);
        EXTFramebufferObject.glBindRenderbufferEXT(
                EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthRBID);
        EXTFramebufferObject.glRenderbufferStorageEXT(
                EXTFramebufferObject.GL_RENDERBUFFER_EXT,
                GL11.GL_DEPTH_COMPONENT, width, height);
        
        this.width = width;
        this.height = height;
        
        this.parentRenderer = parentRenderer;
        initCamera();
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

        LWJGLTextureState.doTextureBind(tex.getTextureId(), 0);
        
        int format = GL11.GL_RGBA;
        switch (tex.getRTTSource()) {
            case Texture.RTT_SOURCE_RGBA: break;
            case Texture.RTT_SOURCE_RGB: format = GL11.GL_RGB; break;
            case Texture.RTT_SOURCE_ALPHA: format = GL11.GL_ALPHA; break;
            case Texture.RTT_SOURCE_DEPTH: format = GL11.GL_DEPTH_COMPONENT; break;
            case Texture.RTT_SOURCE_INTENSITY: format = GL11.GL_INTENSITY; break;
            case Texture.RTT_SOURCE_LUMINANCE: format = GL11.GL_LUMINANCE; break;
            case Texture.RTT_SOURCE_LUMINANCE_ALPHA: format = GL11.GL_LUMINANCE_ALPHA; break;
        }
        
        int components = GL11.GL_RGBA8;
        switch (tex.getRTTSource()) {
            case Texture.RTT_SOURCE_RGBA: break;
            case Texture.RTT_SOURCE_RGB: components = GL11.GL_RGB8; break;
            case Texture.RTT_SOURCE_ALPHA: components = GL11.GL_ALPHA8; break;
            case Texture.RTT_SOURCE_DEPTH: components = GL11.GL_DEPTH_COMPONENT; break;
            case Texture.RTT_SOURCE_INTENSITY: components = GL11.GL_INTENSITY8; break;
            case Texture.RTT_SOURCE_LUMINANCE: components = GL11.GL_LUMINANCE8; break;
            case Texture.RTT_SOURCE_LUMINANCE_ALPHA: components = GL11.GL_LUMINANCE_ALPHA; break;
        }

        // Initialize our texture with some default data.
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, components, width, height, 0,
                format, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);

        // Initialize mipmapping for this texture, if requested
        if (tex.getMipmap() != Texture.MM_NONE) {
            EXTFramebufferObject.glGenerateMipmapEXT(GL11.GL_TEXTURE_2D);
        }

        // Setup filtering and wrap XXX: not sure if this is still necessary
        RenderContext context = DisplaySystem.getDisplaySystem().getCurrentContext();
        TextureStateRecord record = (TextureStateRecord) context.getStateRecord(RenderState.RS_TEXTURE);
        TextureRecord texRecord = record.getTextureRecord(tex.getTextureId());

        LWJGLTextureState.applyFilter(tex, texRecord, 0, record);
        LWJGLTextureState.applyWrap(tex, texRecord, 0, record);
        
        logger.info("setup tex with id " + tex.getTextureId() + ": " + width + ","
                + height);
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
     * @param toDraw
     *            the scene to render.
     * @param tex
     *            the Texture(s) to render it to.
     */
    public void render(Spatial toDraw, Texture tex) {
        render(toDraw, tex, true);
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
     * @param toDraw
     *            the scene to render.
     * @param tex
     *            the Texture(s) to render it to.
     */
    public void render(Spatial toDraw, Texture tex, boolean doClear) {
        if (!isSupported) {
            return;
        }
        
        try {
            
            activate();

            LWJGLTextureState.doTextureBind(tex.getTextureId(), 0);

            if (tex.getRTTSource() == Texture.RTT_SOURCE_DEPTH) {
                // Set textures into FBO
                EXTFramebufferObject.glFramebufferTexture2DEXT(
                        EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                        EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
                        GL11.GL_TEXTURE_2D, tex.getTextureId(), 0);
                GL11.glDrawBuffer(GL11.GL_NONE);
            } else {
                // setup depth RB
                EXTFramebufferObject.glFramebufferRenderbufferEXT(
                        EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                        EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
                        EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthRBID);
    
                // Set textures into FBO
                EXTFramebufferObject.glFramebufferTexture2DEXT(
                        EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                        EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT,
                        GL11.GL_TEXTURE_2D, tex.getTextureId(), 0);
            }

            // Check FBO complete
            checkFBOComplete();
            
            switchCameraIn(doClear);
            
            // Override parent's last frustum test to avoid accidental incorrect
            // cull
            if (toDraw.getParent() != null)
                toDraw.getParent().setLastFrustumIntersection(
                        Camera.INTERSECTS_FRUSTUM);
            doDraw(toDraw);

            switchCameraOut();
            deactivate();

            // automatically generate mipmaps for our texture.
            if (tex.getMipmap() != Texture.MM_NONE) {
                LWJGLTextureState.doTextureBind(tex.getTextureId(), 0);
                EXTFramebufferObject.glGenerateMipmapEXT(GL11.GL_TEXTURE_2D);
            }
            
        } catch (Exception e) {
            logger.logp(Level.SEVERE, this.getClass().toString(),
                    "render(Spatial, Texture, boolean)", "Exception", e);
        }
    }

    public void render(ArrayList<? extends Spatial> toDraw, ArrayList<Texture> texs) {
        render(toDraw, texs, true);
    }

    public void render(ArrayList<? extends Spatial> toDraw, ArrayList<Texture> texs, boolean doClear) {
        if (!isSupported) {
            return;
        }
        
        try {

            // setup and render directly to a 2d texture.
            activate();
            
            EXTFramebufferObject.glFramebufferRenderbufferEXT(
                    EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                    EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
                    EXTFramebufferObject.GL_RENDERBUFFER_EXT, 0);

            boolean foundDepth = false;
            boolean foundColor = false;
            int colors = 0;
            
            // Set textures into FBO
            for (int i = 0; i < texs.size(); i++) {
                Texture tex = texs.get(i);
                if (tex.getRTTSource() == Texture.RTT_SOURCE_DEPTH) {
                    if (!foundDepth) {
                        // Set textures into FBO
                        EXTFramebufferObject.glFramebufferTexture2DEXT(
                                EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                                EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
                                GL11.GL_TEXTURE_2D, tex.getTextureId(), 0);
                        foundDepth = true;
                    }
                } else {
                    foundColor = true;
                    // Set textures into FBO
                    EXTFramebufferObject.glFramebufferTexture2DEXT(
                            EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                            EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT + colors,
                            GL11.GL_TEXTURE_2D, tex.getTextureId(), 0);
                    
                    colors++;
                }
            }
            
            if (!foundDepth) {
                // setup depth RB
                EXTFramebufferObject.glFramebufferRenderbufferEXT(
                        EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                        EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
                        EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthRBID);
            }
            
            if (!foundColor) {
                GL11.glDrawBuffer(GL11.GL_NONE);
                GL11.glReadBuffer(GL11.GL_NONE); 
            }

            // Check FBO complete
            checkFBOComplete();
            
            switchCameraIn(doClear);
            
            for (int x = 0, max = toDraw.size(); x < max; x++) {
                Spatial spat = toDraw.get(x);
                // Override parent's last frustum test to avoid accidental
                // incorrect cull
                if (spat.getParent() != null)
                    spat.getParent().setLastFrustumIntersection(
                            Camera.INTERSECTS_FRUSTUM);

                doDraw(spat);
            }
            
            switchCameraOut();
            deactivate();

            // automatically generate mipmaps for our textures.
            for (int x = 0, max = texs.size(); x < max; x++) {
                if (texs.get(x).getMipmap() != Texture.MM_NONE && texs.get(x).getMipmap() != Texture.MM_LINEAR && texs.get(x).getMipmap() != Texture.MM_NEAREST) {
                    LWJGLTextureState.doTextureBind(texs.get(x).getTextureId(), 0);
                    EXTFramebufferObject.glGenerateMipmapEXT(GL11.GL_TEXTURE_2D);
                }
            }
            
        } catch (Exception e) {
            logger.logp(Level.SEVERE, this.getClass().toString(),
                    "render(Spatial, Texture)", "Exception", e);
        }
    }

    private void checkFBOComplete() {
        int framebuffer = EXTFramebufferObject.glCheckFramebufferStatusEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT ); 
        switch ( framebuffer ) {
            case EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT:
                break;
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
                throw new RuntimeException( "FrameBuffer: " + fboID
                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT exception" );
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
                throw new RuntimeException( "FrameBuffer: " + fboID
                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT exception" );
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
                throw new RuntimeException( "FrameBuffer: " + fboID
                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT exception" );
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
                throw new RuntimeException( "FrameBuffer: " + fboID
                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT exception" );
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
                throw new RuntimeException( "FrameBuffer: " + fboID
                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT exception" );
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
                throw new RuntimeException( "FrameBuffer: " + fboID
                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT exception" );
            case EXTFramebufferObject.GL_FRAMEBUFFER_UNSUPPORTED_EXT:
                throw new RuntimeException( "FrameBuffer: " + fboID
                        + ", has caused a GL_FRAMEBUFFER_UNSUPPORTED_EXT exception" );
            default:
                throw new RuntimeException( "Unexpected reply from glCheckFramebufferStatusEXT: " + framebuffer );
        }
    }

    /**
     * <code>copyToTexture</code> copies the FBO contents to
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
        LWJGLTextureState.doTextureBind(tex.getTextureId(), 0);

        int source = GL11.GL_RGBA;
        switch (tex.getRTTSource()) {
            case Texture.RTT_SOURCE_RGBA: break;
            case Texture.RTT_SOURCE_RGB: source = GL11.GL_RGB; break;
            case Texture.RTT_SOURCE_ALPHA: source = GL11.GL_ALPHA; break;
            case Texture.RTT_SOURCE_DEPTH: source = GL11.GL_DEPTH_COMPONENT; break;
            case Texture.RTT_SOURCE_INTENSITY: source = GL11.GL_INTENSITY; break;
            case Texture.RTT_SOURCE_LUMINANCE: source = GL11.GL_LUMINANCE; break;
            case Texture.RTT_SOURCE_LUMINANCE_ALPHA: source = GL11.GL_LUMINANCE_ALPHA; break;
        }
        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, source, 0, 0, width, height, 0);
    }

    private Camera oldCamera;
    private int oldWidth, oldHeight;
    public void switchCameraIn(boolean doClear) {
        // grab non-rtt settings
        oldCamera = parentRenderer.getCamera();
        oldWidth = parentRenderer.getWidth();
        oldHeight = parentRenderer.getHeight();
        parentRenderer.setCamera(getCamera());

        // swap to rtt settings
        parentRenderer.getQueue().swapBuckets();
        parentRenderer.reinit(width, height);

        // clear the scene
        if (doClear) {
            parentRenderer.clearBuffers();
        }

        getCamera().update();
        getCamera().apply();
    }
    
    public void switchCameraOut() {
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

    public void activate() {
        if (!isSupported) {
            return;
        }
        if (active == 0) {
            GL11.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
            EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fboID );
//            GL11.glMatrixMode(GL11.GL_MODELVIEW);
//            GL11.glPushMatrix();
        }
        active++;
    }

    public void deactivate() {
        if (!isSupported) {
            return;
        }
        if (active == 1) {
            GL11.glClearColor(parentRenderer.getBackgroundColor().r,
                    parentRenderer.getBackgroundColor().g, parentRenderer
                            .getBackgroundColor().b, parentRenderer
                            .getBackgroundColor().a);
            EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0 );
//            GL11.glMatrixMode(GL11.GL_MODELVIEW);
//            GL11.glPopMatrix();
        }
        active--;
    }

    private void initCamera() {
        if (!isSupported) {
            return;
        }
        logger.info("Init RTT camera");
        camera = new LWJGLCamera(width, height, this, true);
        camera.setFrustum(1.0f, 1000.0f, -0.50f, 0.50f, 0.50f, -0.50f);
        Vector3f loc = new Vector3f(0.0f, 0.0f, 0.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        camera.setFrame(loc, left, up, dir);
        camera.setDataOnly(false);
    }

    public void cleanup() {
        if (!isSupported) {
            return;
        }

        if (fboID > 0) {
            IntBuffer id = BufferUtils.createIntBuffer(1);
            id.put(fboID);
            EXTFramebufferObject.glDeleteFramebuffersEXT(id);
        }
    }

    public LWJGLRenderer getParentRenderer() {
        return parentRenderer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setMultipleTargets(boolean multi) {
        ; // ignore.  Does not matter to FBO.
    }
}