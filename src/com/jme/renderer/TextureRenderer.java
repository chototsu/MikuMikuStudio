/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved.
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

import com.jme.image.Texture;
import com.jme.scene.Spatial;

/**
 * <code>TextureRenderer</code> defines an interface that handles rendering a
 * scene to a buffer and copying it to a texture. Creation of this object is
 * typically handled via a call to a <code>DisplaySystem</code> subclass.
 *
 * Example Usage: <br>
 * NOTE: This example uses the <code>DisplaySystem</code> class to obtain the
 * <code>TextureRenderer</code>.
 *
 * <code>
 * </code>
 *
 * @see com.jme.system.DisplaySystem
 * @author Joshua Slack
 * @version $Id: TextureRenderer.java,v 1.6 2004-07-12 23:22:50 renanse Exp $
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
     * reached. All of this is done in the context of the underlying texture
     * buffer.
     *
     * @param spat
     *            the scene to render.
     * @param tex
     *            the Texture to render it to.
     */
    public void render(Spatial spat, Texture tex);

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
     * <code>setupTexture</code> generates a new Texture object for use with
     * TextureRenderer. Generates a valid gl texture id for this texture.
     *
     * @return the new Texture
     */
    public Texture setupTexture();

    /**
     * <code>setupTexture</code> retrieves the color used for the window
     * background.
     *
     * @param glTextureID
     *            a valid gl texture id to use
     * @return the new Texture
     */
    public Texture setupTexture(int glTextureID);

    public void cleanup();
}
