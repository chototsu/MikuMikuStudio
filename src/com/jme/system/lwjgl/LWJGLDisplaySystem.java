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

package com.jme.system.lwjgl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.RenderTexture;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.glu.GLU;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.renderer.RendererType;
import com.jme.renderer.TextureRenderer;
import com.jme.renderer.lwjgl.LWJGLRenderer;
import com.jme.renderer.lwjgl.LWJGLTextureRenderer;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.util.awt.lwjgl.LWJGLCanvas;
import com.jme.widget.font.WidgetFont;
import com.jme.widget.impl.lwjgl.WidgetLWJGLFont;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.opengl.Pbuffer;

import java.awt.Canvas;
import java.awt.Toolkit;

/**
 * <code>LWJGLDisplaySystem</code> defines an implementation of
 * <code>DisplaySystem</code> that uses the LWJGL API for window creation and
 * rendering via OpenGL. <code>LWJGLRenderer</code> is also created that gives
 * a way of displaying data to the created window.
 * 
 * @author Mark Powell
 * @author Gregg Patton
 * @author Joshua Slack - Optimizations and Headless rendering
 * @version $Id: LWJGLDisplaySystem.java,v 1.28 2005-07-28 16:42:16 renanse Exp $
 */
public class LWJGLDisplaySystem extends DisplaySystem {

    private LWJGLRenderer renderer;

    private Pbuffer headlessDisplay;

    /**
     * Constructor instantiates a new <code>LWJGLDisplaySystem</code> object.
     * During instantiation confirmation is made to determine if the LWJGL API
     * is installed properly. If not, a JmeException is thrown.
     * 
     */
    public LWJGLDisplaySystem() {
        super();
        LoggingSystem.getLogger().log(Level.INFO,
                "LWJGL Display System created.");
    }

    /**
     * @see com.jme.system.DisplaySystem#isValidDisplayMode(int, int, int, int)
     */
    public boolean isValidDisplayMode(int width, int height, int bpp, int freq) {
        return getValidDisplayMode(width, height, bpp, freq) != null;
    }

    /**
     * @see com.jme.system.DisplaySystem#setVSyncEnabled(boolean)
     */
    public void setVSyncEnabled(boolean enabled) {
        Display.setVSyncEnabled(enabled);
    }

    /**
     * <code>setTitle</code> sets the window title of the created window.
     * 
     * @param title
     *            the title.
     */
    public void setTitle(String title) {
        Display.setTitle(title);
    }

    /**
     * <code>createWindow</code> will create a LWJGL display context. This
     * window will be a purely native context as defined by the LWJGL API.
     * 
     * @see com.jme.system.DisplaySystem#createWindow(int, int, int, int,
     *      boolean)
     */
    public void createWindow(int w, int h, int bpp, int frq, boolean fs) {
        // confirm that the parameters are valid.
        if (w <= 0 || h <= 0) {
            throw new JmeException("Invalid resolution values: " + w + " " + h);
        } else if ((bpp != 32) && (bpp != 16) && (bpp != 24)) {
            throw new JmeException("Invalid pixel depth: " + bpp);
        }

        // set the window attributes
        this.width = w;
        this.height = h;
        this.bpp = bpp;
        this.frq = frq;
        this.fs = fs;

        initDisplay();
        renderer = new LWJGLRenderer(width, height);
        updateStates(renderer);

        created = true;
    }

    /**
     * <code>createHeadlessWindow</code> will create a headless LWJGL display
     * context. This window will be a purely native context as defined by the
     * LWJGL API.
     * 
     * @see com.jme.system.DisplaySystem#createHeadlessWindow(int, int, int)
     */
    public void createHeadlessWindow(int w, int h, int bpp) {
        // confirm that the parameters are valid.
        if (w <= 0 || h <= 0) {
            throw new JmeException("Invalid resolution values: " + w + " " + h);
        } else if ((bpp != 32) && (bpp != 16) && (bpp != 24)) {
            throw new JmeException("Invalid pixel depth: " + bpp);
        }

        // set the window attributes
        this.width = w;
        this.height = h;
        this.bpp = bpp;

        initHeadlessDisplay();
        renderer = new LWJGLRenderer(width, height);
        renderer.setHeadless(true);
        updateStates(renderer);

        created = true;
    }

    /**
     * <code>createCanvas</code> will create an AWTGLCanvas context. This
     * window will be a purely native context as defined by the LWJGL API.
     * 
     * @see com.jme.system.DisplaySystem#createCanvas(int, int)
     */
    public Canvas createCanvas(int w, int h) {
        // confirm that the parameters are valid.
        if (w <= 0 || h <= 0) {
            throw new JmeException("Invalid resolution values: " + w + " " + h);
        }

        // set the window attributes
        this.width = w;
        this.height = h;

        LWJGLCanvas canvas = null;
        try {
            canvas = new LWJGLCanvas();
        } catch (LWJGLException e) {
            throw new JmeException("Unable to create canvas.", e);
        }

        created = true;

        return canvas;
    }

    /**
     * Returns the Pbuffer used for headless display or null if not headless.
     * 
     * @return Pbuffer
     */
    public Pbuffer getHeadlessDisplay() {
        return headlessDisplay;
    }

    /**
     * <code>recreateWindow</code> will recreate a LWJGL display context. This
     * window will be a purely native context as defined by the LWJGL API.
     * 
     * If a window is not already created, it calls createWindow and exits.
     * Other wise it calls reinitDisplay and renderer.reinit(width,height)
     * 
     * @see com.jme.system.DisplaySystem#recreateWindow(int, int, int, int,
     *      boolean)
     */
    public void recreateWindow(int w, int h, int bpp, int frq, boolean fs) {
        if (!created) {
            createWindow(w, h, bpp, frq, fs);
            return;
        }
        // confirm that the parameters are valid.
        if (w <= 0 || h <= 0) {
            throw new JmeException("Invalid resolution values: " + w + " " + h);
        } else if ((bpp != 32) && (bpp != 16) && (bpp != 24)) {
            throw new JmeException("Invalid pixel depth: " + bpp);
        }

        // set the window attributes
        this.width = w;
        this.height = h;
        this.bpp = bpp;
        this.frq = frq;
        this.fs = fs;

        reinitDisplay();
        renderer.reinit(width, height);
    }

    /**
     * <code>getRenderer</code> returns the created rendering class for LWJGL (
     * <code>LWJGLRenderer</code>). This will give the needed access to
     * display data to the window.
     * 
     * @see com.jme.system.DisplaySystem#getRenderer()
     */
    public Renderer getRenderer() {
        return renderer;
    }

    /**
     * <code>isClosing</code> returns any close requests. True if any exist,
     * false otherwise.
     * 
     * @see com.jme.system.DisplaySystem#isClosing()
     * @return true if a close request is active.
     */
    public boolean isClosing() {
        if (headlessDisplay == null)
            return Display.isCloseRequested();
        else
            return false;
    }

    /**
     * <code>reset</code> prepares the window for closing or restarting.
     * 
     * @see com.jme.system.DisplaySystem#reset()
     */
    public void reset() {
    }

    public void close() {
        Display.destroy();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.system.DisplaySystem#getFont(java.lang.String)
     */
    public WidgetFont getFont(String fontName) {
        WidgetFont f = new WidgetLWJGLFont("Default");
        return f;
    }

    /**
     * <code>getRendererType</code>
     * 
     * @return
     * @see com.jme.system.DisplaySystem#getRendererType()
     */
    public RendererType getRendererType() {
        return RendererType.LWJGL;
    }

    /**
     * <code>createTextureRenderer</code> builds the renderer used to render
     * to a texture.
     */
    public TextureRenderer createTextureRenderer(int width, int height,
            boolean useRGB, boolean useRGBA, boolean useDepth,
            boolean isRectangle, int target, int mipmaps) {
        if (!isCreated())
            return null;

        if (target == TextureRenderer.RENDER_TEXTURE_1D)
            target = RenderTexture.RENDER_TEXTURE_1D;
        else if (target == TextureRenderer.RENDER_TEXTURE_2D)
            target = RenderTexture.RENDER_TEXTURE_2D;
        else if (target == TextureRenderer.RENDER_TEXTURE_CUBE_MAP)
            target = RenderTexture.RENDER_TEXTURE_CUBE_MAP;
        else if (target == TextureRenderer.RENDER_TEXTURE_RECTANGLE)
            target = RenderTexture.RENDER_TEXTURE_RECTANGLE;

        return new LWJGLTextureRenderer(width, height,
                (LWJGLRenderer) getRenderer(), new RenderTexture(useRGB,
                        useRGBA, useDepth, isRectangle, target, mipmaps));
    }
    
    /* (non-Javadoc)
     * @see com.jme.system.DisplaySystem#createTextureRenderer(int, int, boolean, boolean, boolean, boolean, int, int, int, int, int, int, int)
     */
    public TextureRenderer createTextureRenderer(int width, int height, boolean useRGB, 
            boolean useRGBA, boolean useDepth, boolean isRectangle, int target, int mipmaps, 
            int bpp, int alpha, int depth, int stencil, int samples) {
        if (!isCreated())
            return null;

        if (target == TextureRenderer.RENDER_TEXTURE_1D)
            target = RenderTexture.RENDER_TEXTURE_1D;
        else if (target == TextureRenderer.RENDER_TEXTURE_2D)
            target = RenderTexture.RENDER_TEXTURE_2D;
        else if (target == TextureRenderer.RENDER_TEXTURE_CUBE_MAP)
            target = RenderTexture.RENDER_TEXTURE_CUBE_MAP;
        else if (target == TextureRenderer.RENDER_TEXTURE_RECTANGLE)
            target = RenderTexture.RENDER_TEXTURE_RECTANGLE;

        return new LWJGLTextureRenderer(width, height,
                (LWJGLRenderer) getRenderer(), new RenderTexture(useRGB,
                        useRGBA, useDepth, isRectangle, target, mipmaps), bpp, alpha, depth, stencil, samples);
    }

    /**
     * <code>getScreenCoordinates</code> translate world to screen
     * coordinates. Written by Marius, rewritten for LWJGL .9 by Joshua Slack.
     * 
     * @param worldPosition
     *            the world position to translate.
     * @return the screen position.
     */
    public Vector3f getScreenCoordinates(Vector3f worldPosition) {
        return getScreenCoordinates(worldPosition, null);
    }

    // getScreenCoordinates or getWorldCoordinates is called
    private FloatBuffer tmp_FloatBuffer = BufferUtils.createFloatBuffer(16);

    private IntBuffer tmp_IntBuffer = BufferUtils.createIntBuffer(16);

    /**
     * <code>getScreenCoordinates</code> translate world to screen
     * coordinates. Written by Marius, rewritten for LWJGL .9 by Joshua Slack.
     * 
     * @param worldPosition
     *            the world position to translate.
     * @return the screen position.
     */
    public Vector3f getScreenCoordinates(Vector3f worldPosition, Vector3f store) {
        if (store == null)
            store = new Vector3f();

        // Modelview matrix
        tmp_FloatBuffer.rewind();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, tmp_FloatBuffer);
        float mvArray[][] = new float[4][4];
        for (int x = 0; x < 4; x++)
            for (int y = 0; y < 4; y++)
                mvArray[x][y] = tmp_FloatBuffer.get();

        // Projection_matrix
        tmp_FloatBuffer.rewind();
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, tmp_FloatBuffer);
        float prArray[][] = new float[4][4];
        for (int x = 0; x < 4; x++)
            for (int y = 0; y < 4; y++)
                prArray[x][y] = tmp_FloatBuffer.get();

        // Viewport matrix
        tmp_IntBuffer.rewind();
        GL11.glGetInteger(GL11.GL_VIEWPORT, tmp_IntBuffer);
        int[] vpArray = new int[tmp_IntBuffer.capacity()];
        for (int i = 0; i < vpArray.length; i++) {
            vpArray[i] = tmp_IntBuffer.get();
        }

        float[] result = new float[4];

        GLU.gluProject(worldPosition.x, worldPosition.y, worldPosition.z,
                mvArray, prArray, vpArray, result);

        return store.set(result[0], result[1], result[2]);
    }

    /**
     * <code>getWorldCoordinates</code> translate screen to world coordinates.
     * Written by Marius, rewritten for lwjgl .9 by Joshua Slack.
     * 
     * @param screenPosition
     *            the screen coordinates to translate.
     * @param zPos
     *            between 0 and 1.
     * @return world position pointed to by screen coordinate.
     */
    public Vector3f getWorldCoordinates(Vector2f screenPosition, float zPos) {
        return getWorldCoordinates(screenPosition, zPos, null);
    }

    /**
     * <code>getWorldCoordinates</code> translate screen to world coordinates.
     * Written by Marius, rewritten for lwjgl .9 by Joshua Slack.
     * 
     * @param screenPosition
     *            the screen coordinates to translate.
     * @param zPos
     *            between 0 and 1.
     * @return world position pointed to by screen coordinate.
     */
    public Vector3f getWorldCoordinates(Vector2f screenPosition, float zPos,
            Vector3f store) {
        if (store == null)
            store = new Vector3f();

        // Modelview matrix
        tmp_FloatBuffer.rewind();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, tmp_FloatBuffer);
        float mvArray[][] = new float[4][4];
        for (int x = 0; x < 4; x++)
            for (int y = 0; y < 4; y++)
                mvArray[x][y] = tmp_FloatBuffer.get();

        // Projection_matrix
        tmp_FloatBuffer.rewind();
        FloatBuffer prBuffer = tmp_FloatBuffer;
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, prBuffer);
        float prArray[][] = new float[4][4];
        for (int x = 0; x < 4; x++)
            for (int y = 0; y < 4; y++)
                prArray[x][y] = prBuffer.get();

        // Viewport matrix
        tmp_IntBuffer.rewind();
        GL11.glGetInteger(GL11.GL_VIEWPORT, tmp_IntBuffer);

        // 3d coordinates
        float[] result = new float[4];
        int[] vpArray = new int[tmp_IntBuffer.capacity()];
        for (int i = 0; i < vpArray.length; i++) {
            vpArray[i] = tmp_IntBuffer.get();
        }
        GLU.gluUnProject(screenPosition.x, screenPosition.y, zPos, mvArray,
                prArray, vpArray, result);

        return store.set(result[0], result[1], result[2]);
    }

    /**
     * <code>getValidDisplayMode</code> returns a <code>DisplayMode</code>
     * object that has the requested width, height and color depth. If there is
     * no mode that supports a requested resolution, null is returned.
     * 
     * @param width
     *            the width of the desired mode.
     * @param height
     *            the height of the desired mode.
     * @param bpp
     *            the color depth of the desired mode.
     * @param freq
     *            the frequency of the monitor.
     * @return <code>DisplayMode</code> object that supports the requested
     *         resolutions. Null is returned if no valid modes are found.
     */
    private DisplayMode getValidDisplayMode(int width, int height, int bpp,
            int freq) {
        // get all the modes, and find one that matches our width, height, bpp.
        DisplayMode[] modes;
        try {
            modes = Display.getAvailableDisplayModes();
        } catch (LWJGLException e) {
            e.printStackTrace();
            return null;
        }
        // Make sure that we find the mode that uses our current monitor freq.

        for (int i = 0; i < modes.length; i++) {
            if (modes[i].getWidth() == width && modes[i].getHeight() == height
                    && modes[i].getBitsPerPixel() == bpp
                    && (freq == 0 || modes[i].getFrequency() == freq)) {

                return modes[i];
            }
        }

        // none found
        return null;
    }

    /**
     * <code>initDisplay</code> creates the LWJGL window with the desired
     * specifications.
     * 
     */
    private void initDisplay() {
        // create the Display.
        DisplayMode mode = getValidDisplayMode(width, height, bpp, frq);
        PixelFormat format = new PixelFormat(bpp, alphaBits, depthBits,
                stencilBits, samples);
        if (null == mode) {
            throw new JmeException("Bad display mode");
        }

        try {
            Display.setDisplayMode(mode);
            Display.setFullscreen(fs);
            if (!fs) {
                int x, y;
                x = (Toolkit.getDefaultToolkit().getScreenSize().width - width) >> 1;
                y = (Toolkit.getDefaultToolkit().getScreenSize().height - height) >> 1;
                Display.setLocation(x, y);
            }
            Display.create(format);
            // kludge added here... LWJGL does not properly clear their
            // keyboard and mouse buffers when you call the destroy method,
            // so if you run two jme programs in the same jvm back to back
            // the second one will pick up the esc key used to exit out of
            // the first.
            Keyboard.poll();
            Mouse.poll();

        } catch (Exception e) {
            // System.exit(1);
            LoggingSystem.getLogger().log(Level.SEVERE, "Cannot create window");
            LoggingSystem.getLogger().throwing(this.getClass().toString(),
                    "initDisplay()", e);
            throw new Error("Cannot create window: " + e.getMessage());
        }
    }

    /**
     * <code>initHeadlessDisplay</code> creates the LWJGL window with the
     * desired specifications.
     * 
     */
    private void initHeadlessDisplay() {
        // create the Display.
        DisplayMode mode = getValidDisplayMode(width, height, bpp, frq);
        PixelFormat format = new PixelFormat(bpp, alphaBits, depthBits,
                stencilBits, samples);

        try {
            Display.setDisplayMode(mode); // done so the renderer has access
                                            // to this information.
            headlessDisplay = new Pbuffer(width, height, format, null, null);
            headlessDisplay.makeCurrent();
        } catch (Exception e) {
            // System.exit(1);
            LoggingSystem.getLogger().log(Level.SEVERE,
                    "Cannot create headless window");
            LoggingSystem.getLogger().throwing(this.getClass().toString(),
                    "initHeadlessDisplay()", e);
            throw new Error("Cannot create headless window: " + e.getMessage());
        }
    }

    /**
     * <code>reinitDisplay</code> recreates the LWJGL window with the desired
     * specifications. All textures, etc should remain in the context.
     */
    private void reinitDisplay() {
        // create the Display.
    		DisplayMode mode; 
    	        if (fs) { 
    	            mode = getValidDisplayMode(width, height, bpp, frq); 
    	            if (null == mode) { 
    	                throw new JmeException("Bad display mode"); 
    	            } 
    	        } else { 
    	            mode = new DisplayMode(width, height); 
        }

        try {
            Display.setDisplayMode(mode);
            Display.setFullscreen(fs);
        } catch (Exception e) {
            // System.exit(1);
            LoggingSystem.getLogger().log(Level.SEVERE,
                    "Cannot recreate window");
            LoggingSystem.getLogger().throwing(this.getClass().toString(),
                    "reinitDisplay()", e);
            throw new Error("Cannot recreate window: " + e.getMessage());
        }
    }

    public void setRenderer(Renderer r) {
        renderer = (LWJGLRenderer) r;
    }

    
}
