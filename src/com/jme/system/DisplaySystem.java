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

/*
 * EDIT: 02/09/2004 - Added getRendererType method. GOP
 */

package com.jme.system;

import java.awt.Canvas;

import com.jme.renderer.Renderer;
import com.jme.renderer.RendererType;
import com.jme.renderer.TextureRenderer;
import com.jme.system.lwjgl.*;
import com.jme.widget.font.WidgetFont;
import com.jme.scene.Spatial;
import com.jme.scene.state.RenderState;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;

/**
 * <code>DisplaySystem</code> defines an interface for system creation.
 * Specifically, any implementing class will create a window for rendering. It
 * also should create the appropriate <code>Renderer</code> object that allows
 * the client to render to this window.
 * 
 * Implmenting classes should check for the appropriate libraries to insure
 * these libraries are indeed installed on the system. This will allow users to
 * cleanly exit if an improper library was chosen for rendering.
 * 
 * Example usage:
 * 
 * <code>
 * DisplaySystem ds = DisplaySystem.getDisplaySystem("LWJGL");<br>
 * ds.createWindow(640,480,32,60,true);<br>
 * Renderer r = ds.getRenderer();<br>
 * </code>
 * 
 * @see com.jme.renderer.Renderer
 * 
 * @author Mark Powell
 * @author Gregg Patton
 * @author Joshua Slack - Optimizations and Headless rendering
 * @version $Id: DisplaySystem.java,v 1.38 2005-04-05 23:45:48 renanse Exp $
 */
public abstract class DisplaySystem {

    /** The display system that has been created. */
    private static DisplaySystem display;

    /** Width selected for the renderer. */
    protected int width;

    /** height selected for the renderer. */
    protected int height;

    /** Bit depth selected for renderer. */
    protected int bpp;

    /** Frequency selected for renderer. */
    protected int frq;

    /** Is the display full screen? */
    protected boolean fs;

    /** Is the display created already? */
    protected boolean created;

    /** Alpha bits to use for the renderer. */
    protected int alphaBits = 0;

    /** Depth bits to use for the renderer. */
    protected int depthBits = 8;

    /** Stencil bits to use for the renderer. */
    protected int stencilBits = 0;

    /** Number of samples to use for the multisample buffer. */
    protected int samples = 0;

    /**
     * The list of current implemented rendering APIs that subclass Display.
     */
    public static final String[] rendererNames = { "LWJGL" };

    /**
     * A new display system has been created. The default static display system
     * is set to the newly created display system.
     */
    protected DisplaySystem() {
        display = this;
    }

    /**
     * <code>getDisplaySystem</code> is a factory method that creates the
     * appropriate display system specified by the key parameter. If the key
     * given is not a valid identifier for a specific display system, null is
     * returned. For valid display systems see the <code>rendererNames</code>
     * array.
     * 
     * @param key
     *            the display system to use.
     * @return the appropriate display system specified by the key.
     */
    public static DisplaySystem getDisplaySystem(String key) {
        if ("LWJGL".equalsIgnoreCase(key)) {
            new LWJGLDisplaySystem();
        } else {
            display = null;
        }

        return display;
    }

    /**
     * Returns the currently created display system.
     * 
     * @return The current display system.
     */
    public static DisplaySystem getDisplaySystem() {
        return display;
    }

    /**
     * Returns the set width for the display system.
     * 
     * @return The set width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the set height for the display system.
     * 
     * @return The set height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the set bitdepth for the display system.
     * 
     * @return the set bit depth
     */
    public int getBitDepth() {
        return bpp;
    }

    /**
     * Returns the set frequency for the display system.
     * 
     * @return the set frequency
     */
    public int getFrequency() {
        return frq;
    }

    /**
     * Returns whether or not the display system is set to be full screen.
     * 
     * @return true if full screen
     */
    public boolean isFullScreen() {
        return fs;
    }

    /**
     * <code>isValidDisplayMode</code> determines if the given parameters
     * constitute a valid display mode on this system. Returning true does not
     * necessarily guarantee that the system is capable of running in the
     * specified display mode, merely that it <i>believes </i> it is possible.
     * 
     * @param width
     *            the width/horizontal resolution of the display.
     * @param height
     *            the height/vertical resolution of the display.
     * @param bpp
     *            the bit depth of the display.
     * @param freq
     *            the frequency of refresh of the display (in Hz).
     */
    public abstract boolean isValidDisplayMode(int width, int height, int bpp,
            int freq);

    /**
     * <code>setVSyncEnabled</code> attempts to enable or disable monitor
     * vertical synchronization. The method is a "best attempt" to change the
     * monitor vertical refresh synchronization, and is <b>not </b> guaranteed
     * to be successful.
     * 
     * @param enabled
     *            <code>true</code> to synchronize, <code>false</code> to
     *            ignore synchronization
     */
    public abstract void setVSyncEnabled(boolean enabled);

    /**
     * Sets the title of the display system. This is usually reflected by the
     * renderer as text in the menu bar.
     * 
     * @param title
     *            The new display title.
     */
    public abstract void setTitle(String title);

    /**
     * <code>createWindow</code> creates a window with the desired settings.
     * The width and height defined by w and h define the size of the window if
     * fullscreen is false, otherwise it defines the resolution of the
     * fullscreen display. The color depth is defined by bpp. The implementing
     * class should only allow 16, 24, and 32. The monitor frequency is defined
     * by the frq parameter and should not exceed the capabilities of the
     * connected hardware, the implementing class should attempt to assure this
     * does not happen. Lastly, the boolean flag fs determines if the display
     * should be windowed or fullscreen. If false, windowed is chosen. This
     * window will be placed in the center of the screen initially. If true
     * fullscreen mode will be entered with the appropriate settings.
     * 
     * @param w
     *            the width/horizontal resolution of the display.
     * @param h
     *            the height/vertical resolution of the display.
     * @param bpp
     *            the color depth of the display.
     * @param frq
     *            the frequency of refresh of the display.
     * @param fs
     *            flag determining if fullscreen is to be used or not. True will
     *            use fullscreen, false will use windowed mode.
     */
    public abstract void createWindow(int w, int h, int bpp, int frq, boolean fs);

    /**
     * <code>createHeadlessWindow</code> creates a headless window with the
     * desired settings. A headless window is a rendering target that is not
     * shown on screen. It is useful for doing offline rendering, integration
     * and so forth. You can not have a regular and headless
     * window at the same time. The width and height defined by w and h define
     * the size of the window. The color depth is defined by bpp.
     * 
     * @param w
     *            the width/horizontal resolution of the display.
     * @param h
     *            the height/vertical resolution of the display.
     * @param bpp
     *            the color depth of the display.
     */
    public abstract void createHeadlessWindow(int w, int h, int bpp);

    /**
     * <code>createCanvas</code> creates an awt canvas with the
     * desired settings. The width and height defined by w and h define
     * the size of the canvas.
     * 
     * @param w
     *            the width/horizontal resolution of the display.
     * @param h
     *            the height/vertical resolution of the display.
     */
    public abstract Canvas createCanvas(int w, int h);

    /**
     * <code>recreateWindow</code> recreates a window with the desired
     * settings.
     * 
     * @param w
     *            the width/horizontal resolution of the display.
     * @param h
     *            the height/vertical resolution of the display.
     * @param bpp
     *            the color depth of the display.
     * @param frq
     *            the frequency of refresh of the display.
     * @param fs
     *            flag determining if fullscreen is to be used or not. True will
     *            use fullscreen, false will use windowed mode.
     */
    public abstract void recreateWindow(int w, int h, int bpp, int frq,
            boolean fs);

    /**
     * <code>getRenderer</code> returns the <code>Renderer</code>
     * implementation that is compatible with the chosen
     * <code>DisplaySystem</code>. For example, if
     * <code>LWJGLDisplaySystem</code> is used, the returned
     * <code>Renderer</code> will be</code> LWJGLRenderer</code>.
     * 
     * @see com.jme.renderer.Renderer
     * @return the appropriate <code>Renderer</code> implementation that is
     *         compatible with the used <code>DisplaySystem</code>.
     */
    public abstract Renderer getRenderer();

    public abstract void setRenderer(Renderer r);
    
    /**
     * <code>getRendererType</code> returns an instance of a strongly typed
     * enumeration that can be used to determine the renderer that the
     * DisplaySystem is currently using.
     * 
     * @see com.jme.util.JmeType
     * @return
     */
    public abstract RendererType getRendererType();

    /**
     * <code>isCreated</code> returns the current status of the display
     * system. If the window and renderer are created, true is returned,
     * otherwise false.
     * 
     * @return whether the display system is created.
     */
    public boolean isCreated() {
        return created;
    }

    /**
     * <code>isClosing</code> notifies if the window is currently closing.
     * This could be caused via the application itself or external interrupts
     * such as alt-f4 etc.
     * 
     * @return true if the window is closing, false otherwise.
     */
    public abstract boolean isClosing();

    /**
     * <code>reset</code> cleans up the display system for closing or
     * restarting.
     *  
     */
    public abstract void reset();

    /**
     * <code>close</code> shutdowns and destroys any window contexts.
     *  
     */
    public abstract void close();

    /**
     * @param fontName -
     *            name of the font to loaded
     * @return an instance of the requested font, null of the isn't loaded.
     */
    public abstract WidgetFont getFont(String fontName);

    /**
     * Returns the minimum bits per pixel in the alpha buffer.
     * 
     * @return the int value of alphaBits.
     */
    public int getMinAlphaBits() {
        return alphaBits;
    }

    /**
     * Sets the minimum bits per pixel in the alpha buffer.
     * 
     * @param alphaBits -
     *            the new value for alphaBits
     */
    public void setMinAlphaBits(int alphaBits) {
        this.alphaBits = alphaBits;
    }

    /**
     * Returns the minimum bits per pixel in the depth buffer.
     * 
     * @return the int value of depthBits.
     */
    public int getMinDepthBits() {
        return depthBits;
    }

    /**
     * Sets the minimum bits per pixel in the depth buffer.
     * 
     * @param depthBits -
     *            the new value for depthBits
     */
    public void setMinDepthBits(int depthBits) {
        this.depthBits = depthBits;
    }

    /**
     * Returns the minimum bits per pixel in the stencil buffer.
     * 
     * @return the int value of stencilBits.
     */
    public int getMinStencilBits() {
        return stencilBits;
    }

    /**
     * Sets the minimum bits per pixel in the stencil buffer.
     * 
     * @param stencilBits -
     *            the new value for stencilBits
     */
    public void setMinStencilBits(int stencilBits) {
        this.stencilBits = stencilBits;
    }

    /**
     * Returns the minimum samples in multisample buffer.
     * 
     * @return the int value of samples.
     */
    public int getMinSamples() {
        return samples;
    }

    /**
     * Sets the minimum samples in the multisample buffer.
     * 
     * @param samples -
     *            the new value for samples
     */
    public void setMinSamples(int samples) {
        this.samples = samples;
    }

    /**
     * Called when the display system is created, this function sets the default
     * render states for the renderer. It should not be called directly by the
     * user.
     * 
     * @param r
     *            The renderer to get the default states from.
     */
    public static void updateStates(Renderer r) {
        Spatial.defaultStateList[RenderState.RS_ALPHA] = r.createAlphaState();
        Spatial.defaultStateList[RenderState.RS_ALPHA].setEnabled(false);
        Spatial.defaultStateList[RenderState.RS_ATTRIBUTE] = r
                .createAttributeState();
        Spatial.defaultStateList[RenderState.RS_ATTRIBUTE].setEnabled(false);
        Spatial.defaultStateList[RenderState.RS_CULL] = r.createCullState();
        Spatial.defaultStateList[RenderState.RS_CULL].setEnabled(false);
        Spatial.defaultStateList[RenderState.RS_DITHER] = r.createDitherState();
        Spatial.defaultStateList[RenderState.RS_DITHER].setEnabled(false);
        Spatial.defaultStateList[RenderState.RS_FOG] = r.createFogState();
        Spatial.defaultStateList[RenderState.RS_FOG].setEnabled(false);
        Spatial.defaultStateList[RenderState.RS_LIGHT] = r.createLightState();
        Spatial.defaultStateList[RenderState.RS_LIGHT].setEnabled(false);
        Spatial.defaultStateList[RenderState.RS_MATERIAL] = r
                .createMaterialState();
        Spatial.defaultStateList[RenderState.RS_MATERIAL].setEnabled(false);
        Spatial.defaultStateList[RenderState.RS_SHADE] = r.createShadeState();
        Spatial.defaultStateList[RenderState.RS_SHADE].setEnabled(false);
        Spatial.defaultStateList[RenderState.RS_TEXTURE] = r
                .createTextureState();
        Spatial.defaultStateList[RenderState.RS_TEXTURE].setEnabled(false);
        Spatial.defaultStateList[RenderState.RS_VERTEX_PROGRAM] = r
                .createVertexProgramState();
        Spatial.defaultStateList[RenderState.RS_VERTEX_PROGRAM]
                .setEnabled(false);
        Spatial.defaultStateList[RenderState.RS_FRAGMENT_PROGRAM] = r
                .createFragmentProgramState();
        Spatial.defaultStateList[RenderState.RS_FRAGMENT_PROGRAM]
                .setEnabled(false);
        Spatial.defaultStateList[RenderState.RS_WIREFRAME] = r
                .createWireframeState();
        Spatial.defaultStateList[RenderState.RS_WIREFRAME].setEnabled(false);
        Spatial.defaultStateList[RenderState.RS_ZBUFFER] = r
                .createZBufferState();
        Spatial.defaultStateList[RenderState.RS_ZBUFFER].setEnabled(false);
        Spatial.defaultStateList[RenderState.RS_STENCIL] = r
                .createStencilState();
        Spatial.defaultStateList[RenderState.RS_STENCIL].setEnabled(false);
        Spatial.defaultStateList[RenderState.RS_GLSL_SHADER_OBJECTS] = r
                .createGLSLShaderObjectsState();
        Spatial.defaultStateList[RenderState.RS_GLSL_SHADER_OBJECTS]
                .setEnabled(false);
    }

    /**
     * Crate a TextureRenderer using the underlying system. This should not be
     * user called. It is called when the display system is created.
     * 
     * @param width
     *            width of texture
     * @param height
     *            height of texture
     * @param useRGB
     *            if this is true, useRGBA should not be
     * @param useRGBA
     *            if this is true, useRGB should not be
     * @param useDepth
     * @param isRectangle
     * @param target
     * @param mipmaps
     * @return A TextureRenderer for the display system.
     */
    public abstract TextureRenderer createTextureRenderer(int width,
            int height, boolean useRGB, boolean useRGBA, boolean useDepth,
            boolean isRectangle, int target, int mipmaps);

    /**
     * Translate world to screen coordinates
     * 
     * @param worldPosition
     *            Vector3f representing the world position to retrieve.
     * @return Vector2f The screen position of that vector3f world position.
     */
    public abstract Vector3f getScreenCoordinates(Vector3f worldPosition);

    /**
     * Translate world to screen coordinates
     * 
     * @param worldPosition
     *            Vector3f representing the world position to retrieve.
     * @param store
     *            Vector3f to store the world position in.
     * @return Vector3f The store vector3f, after storing.
     */
    public abstract Vector3f getScreenCoordinates(Vector3f worldPosition,
            Vector3f store);

    /**
     * Translate screen to world coordinates.
     * 
     * @param screenPosition
     *            Vector2f representing the screen position with 0,0 at the
     *            bottom left.
     * @param zPos
     *            float The z position away from the viewing plane.
     * @return Vector3f A vector3f representing the vector's world position.
     */
    public abstract Vector3f getWorldCoordinates(Vector2f screenPosition,
            float zPos);

    /**
     * Translate screen to world coordinates.
     * 
     * @param screenPosition
     *            Vector2f representing the screen position with 0,0 at the
     *            bottom left
     * @param zPos
     *            float The z position away from the viewing plane.
     * @param store
     *            Vector3f The vector to store the result in.
     * @return Vector3f The store vector, after storing it's result.
     */
    public abstract Vector3f getWorldCoordinates(Vector2f screenPosition,
            float zPos, Vector3f store);
}