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

package com.jme.renderer;

import java.util.ArrayList;

import com.jme.image.Texture;
import com.jme.scene.Spatial;

/**
 * <code>TextureRenderer</code> defines an abstract class that handles rendering a
 * scene to a buffer and copying it to a texture. Creation of this object is
 * typically handled via a call to a <code>DisplaySystem</code> subclass.
 *
 * Example Usage: <br>
 * NOTE: This example uses the <code>DisplaySystem</code> class to obtain the
 * <code>TextureRenderer</code>.
 *
 * <code> DisplaySystem.getDisplaySystem().createTextureRenderer(...)
 * </code>
 *
 * @see com.jme.system.DisplaySystem
 * @author Joshua Slack
 * @version $Id: TextureRenderer.java,v 1.16 2006-07-21 22:25:14 nca Exp $
 */
public interface TextureRenderer {

    /**
     * defines a constant for usage of a one dimensional texture.
     */
    public static final int RENDER_TEXTURE_1D = 1;

    /**
     * defines a constant for usage of a two dimensional texture.
     */
    public static final int RENDER_TEXTURE_2D = 2;

    /**
     * defines a constant for usage of a rectangular texture.
     */
    public static final int RENDER_TEXTURE_RECTANGLE = 3;

    /**
     * defines a constant for usage of a cubic texture.
     */
    public static final int RENDER_TEXTURE_CUBE_MAP = 4;
    
    /**
     * 
     * <code>isSupported</code> obtains the capability of the graphics card.
     * If the graphics card does not have pbuffer support, false is returned, 
     * otherwise, true is returned. TextureRenderer will not process any
     * scene elements if pbuffer is not supported.
     *
     * @return if this graphics card supports pbuffers or not.
     */
    public boolean isSupported();

    /**
     * <code>getCamera</code> retrieves the camera this renderer is using.
     *
     * @return the camera this renderer is using.
     */
    public Camera getCamera();

    /**
     * <code>setCamera</code> sets the camera this renderer should use.
     *
     * @param camera
     *            the camera this renderer should use.
     */
    public void setCamera(Camera camera);

    /**
     * <code>updateCamera</code> updates the camera in the pbuffer context.
     */
    public void updateCamera();

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
    public void render(Spatial spat, Texture tex);

    /**
     * <code>render</code> renders a scene. As it recieves a base class of
     * <code>Spatial</code> the renderer hands off management of the scene to
     * spatial for it to determine when a <code>Geometry</code> leaf is
     * reached. The result of the rendering is then copied into the given
     * textures. What is copied is based on each Texture object's rttSource
     * field.
     * 
     * @param spat
     *            the scene to render.
     * @param texs
     *            an array of Texture objects to copy the rendering to.
     */
    public void render(Spatial spat, Texture[] texs);

    /**
     * <code>render</code> renders a scene. As it recieves a base class of
     * <code>Spatial</code> the renderer hands off management of the scene to
     * spatial for it to determine when a <code>Geometry</code> leaf is
     * reached. The result of the rendering is then copied into the given
     * texture. What is copied is based on the Texture object's rttSource field.
     * 
     * @param spats
     *            an array of Spatials to render.
     * @param tex
     *            the Texture to render it to.
     */
    public void render(ArrayList spats, Texture tex);

    /**
     * <code>render</code> renders a scene. As it recieves a base class of
     * <code>Spatial</code> the renderer hands off management of the scene to
     * spatial for it to determine when a <code>Geometry</code> leaf is
     * reached. The result of the rendering is then copied into the given
     * textures. What is copied is based on each Texture object's rttSource
     * field.
     * 
     * @param spats
     *            an array of Spatials to render.
     * @param texs
     *            an array of Texture objects to copy the rendering to.
     */
    public void render(ArrayList spats, Texture[] texs);

    /**
     * <code>setBackgroundColor</code> sets the color of window. This color
     * will be shown for any pixel that is not set via typical rendering
     * operations.
     *
     * @param c
     *            the color to set the background to.
     */
    public void setBackgroundColor(ColorRGBA c);

    /**
     * <code>getBackgroundColor</code> retrieves the color used for the window
     * background.
     *
     * @return the background color that is currently set to the background.
     */
    public ColorRGBA getBackgroundColor();

    /**
     * <code>setupTexture</code> initializes a Texture object for use with
     * TextureRenderer. Generates a valid gl texture id for this texture and
     * sets up data storage for it.  The texture will be equal to the pbuffer size.
     * 
     * Note that the pbuffer size is not necessarily what is specified in the constructor.
     * 
     * @param tex
     *            The texture to setup for use in Texture Rendering.
     */
    public void setupTexture(Texture tex);

    /**
     * <code>setupTexture</code> initializes a Texture object for use with
     * TextureRenderer. Generates a valid gl texture id for this texture and
     * sets up data storage for it.
     * 
     * @param tex
     *            The texture to setup for use in Texture Rendering.
     */
    public void setupTexture(Texture tex, int width, int height);

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
    public void copyToTexture(Texture tex, int width, int height);

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
    public void copyBufferToTexture(Texture tex, int width, int height, int buffer);
    
    /**
     * Any wrapping up and cleaning up of TextureRenderer information is performed here.
     */
    public void cleanup();
    
    /**
     * Force the texture renderering to be done via copy versus direct. Useful
     * if you are trying to use a single texture renderer to render to multiple
     * textures in certain contexts.
     * 
     * @param force - true to force this.
     */
    public void forceCopy(boolean force);
    
    public int getPBufferWidth();
    public int getPBufferHeight();

}
