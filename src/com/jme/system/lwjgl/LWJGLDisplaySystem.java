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

import java.awt.Toolkit;

import org.lwjgl.BufferUtils;
import org.lwjgl.Display;
import org.lwjgl.DisplayMode;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.RenderTexture;
import org.lwjgl.opengl.Window;
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
import com.jme.widget.font.WidgetFont;
import com.jme.widget.impl.lwjgl.WidgetLWJGLFont;

/**
 * <code>LWJGLDisplaySystem</code> defines an implementation of
 * <code>DisplaySystem</code> that uses the LWJGL API for window creation and
 * rendering via OpenGL. <code>LWJGLRenderer</code> is also created that gives
 * a way of displaying data to the created window.
 *
 * @author Mark Powell
 * @author Gregg Patton
 * @version $Id: LWJGLDisplaySystem.java,v 1.11 2004/04/26 20:31:14 mojomonkey
 *          Exp $
 */
public class LWJGLDisplaySystem extends DisplaySystem {

    private int bpp;

    private int frq;

    private String title = "";

    private boolean fs;

    private boolean created;

    private LWJGLRenderer renderer;

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
        Window.setVSyncEnabled(enabled);
    }

    /**
     * <code>setTitle</code> sets the window title of the created window.
     *
     * @param title
     *            the title.
     */
    public void setTitle(String title) {
        Window.setTitle(title);
    }

    /**
     * <code>createWindow</code> will create a LWJGL display context. This
     * window will be a purely native context as defined by the LWJGL API.
     *
     * @see com.jme.system.DisplaySystem#createWindow(int, int, int, int,
     *      boolean)
     */
    public void createWindow(int w, int h, int bpp, int frq, boolean fs) {
        //confirm that the parameters are valid.
        if (w <= 0 || h <= 0) {
            throw new JmeException("Invalid resolution values: " + w + " " + h);
        } else if ((bpp != 32) && (bpp != 16) && (bpp != 24)) { throw new JmeException(
                "Invalid pixel depth: " + bpp); }

        //set the window attributes
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
     * <code>isCreated</code> returns true if the current display is created,
     * false otherwise.
     *
     * @see com.jme.system.DisplaySystem#isCreated()
     * @return true if display is created.
     */
    public boolean isCreated() {
        return created;
    }

    /**
     * <code>isClosing</code> returns any close requests. True if any exist,
     * false otherwise.
     *
     * @see com.jme.system.DisplaySystem#isClosing()
     * @return true if a close request is active.
     */
    public boolean isClosing() {
        return Window.isCloseRequested();
    }

    /**
     * <code>reset</code> prepares the window for closing or restarting.
     *
     * @see com.jme.system.DisplaySystem#reset()
     */
    public void reset() {
        Display.resetDisplayMode();
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
     * @return @see com.jme.system.DisplaySystem#getRendererType()
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
        if (!isCreated()) return null;

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

    /**
     * <code>getScreenCoordinates</code> translate world to screen coordinates.
     *
     * @param worldPosition the world position to translate.
     * @return the screen position.
     * @author Marius
     * @author Joshua Slack -- rewritten for lwjgl .9
     */
    public Vector3f getScreenCoordinates(Vector3f worldPosition) {
        // Modelview matrix
        FloatBuffer mvBuffer = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, mvBuffer);
        float mvArray[][] = new float[4][4];
        for (int x = 0; x < 4; x++)
            for (int y = 0; y < 4; y++)
                mvArray[x][y] = mvBuffer.get();

        // Projection_matrix
        FloatBuffer prBuffer = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, prBuffer);
        float prArray[][] = new float[4][4];
        for (int x = 0; x < 4; x++)
            for (int y = 0; y < 4; y++)
                prArray[x][y] = prBuffer.get();

        // Viewport matrix
        IntBuffer vpBuffer = BufferUtils.createIntBuffer(16);
        GL11.glGetInteger(GL11.GL_VIEWPORT, vpBuffer);
        int[] vpArray = new int[vpBuffer.capacity()];
        for (int i = 0; i < vpArray.length; i++) {
            vpArray[i] = vpBuffer.get();
        }

        float[] result = new float[4];

        GLU.gluProject(worldPosition.x, worldPosition.y, worldPosition.z,
                mvArray, prArray, vpArray, result);

        return new Vector3f(result[0], result[1], result[2]);
    }

    /**
     * <code>getWorldCoordinates</code> translate screen to world coordinates.
     *
     * @param screenPosition the screen coordinates to translate.
     * @param zPos between 0 and 1.
     * @return world position pointed to by screen coordinate.
     * @author Marius
     * @author Joshua Slack -- rewritten for lwjgl .9
     */
    public Vector3f getWorldCoordinates(Vector2f screenPosition, float zPos) {

        // Modelview matrix
        FloatBuffer mvBuffer = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, mvBuffer);
        float mvArray[][] = new float[4][4];
        for (int x = 0; x < 4; x++)
            for (int y = 0; y < 4; y++)
                mvArray[x][y] = mvBuffer.get();

        // Projection_matrix
        FloatBuffer prBuffer = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, prBuffer);
        float prArray[][] = new float[4][4];
        for (int x = 0; x < 4; x++)
            for (int y = 0; y < 4; y++)
                prArray[x][y] = prBuffer.get();

        // Viewport matrix
        IntBuffer vpBuffer = BufferUtils.createIntBuffer(16);
        GL11.glGetInteger(GL11.GL_VIEWPORT, vpBuffer);

        // 3d coordinates
        float[] result = new float[4];
        int[] vpArray = new int[vpBuffer.capacity()];
        for (int i = 0; i < vpArray.length; i++) {
            vpArray[i] = vpBuffer.get();
        }
        GLU.gluUnProject(screenPosition.x, screenPosition.y, zPos, mvArray,
                prArray, vpArray, result);

        return new Vector3f(result[0], result[1], result[2]);
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
        //get all the modes, and find one that matches our width, height, bpp.
        DisplayMode[] modes = Display.getAvailableDisplayModes();
        //Make sure that we find the mode that uses our current monitor freq.

        for (int i = 0; i < modes.length; i++) {
            if (modes[i].width == width && modes[i].height == height
                    && modes[i].bpp == bpp && modes[i].freq == freq) {

            return modes[i]; }
        }

        //none found
        return null;
    }

    /**
     * <code>initDisplay</code> creates the LWJGL window with the desired
     * specifications.
     *
     */
    private void initDisplay() {
        //create the Display.
        DisplayMode mode = getValidDisplayMode(width, height, bpp, frq);
        if (null == mode) { throw new JmeException("Bad display mode"); }

        try {
            if (fs) {
                Display.setDisplayMode(mode);
                Window.create(title, bpp, alphaBits, depthBits, stencilBits,
                        samples);
            } else {
                int x, y;
                x = (Toolkit.getDefaultToolkit().getScreenSize().width - width) / 2;
                y = (Toolkit.getDefaultToolkit().getScreenSize().height - height) / 2;
                Window.create(title, x, y, width, height, bpp, alphaBits,
                        depthBits, stencilBits, samples);
            }

            // kludge added here...  LWJGL does not properly clear their
            // keyboard and mouse buffers when you call the destroy method,
            // so if you run two jme programs in the same jvm back to back
            // the second one will pick up the esc key used to exit out of
            // the first.
            Keyboard.poll();
            Mouse.poll();

        } catch (Exception e) {
            //System.exit(1);
            LoggingSystem.getLogger().log(Level.SEVERE, "Cannot create window");
            LoggingSystem.getLogger().throwing(this.getClass().toString(),
                    "initDisplay()", e);
            throw new Error("Cannot create window: " + e.getMessage());
        }
    }
}
