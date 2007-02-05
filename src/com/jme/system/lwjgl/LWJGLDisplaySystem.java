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

package com.jme.system.lwjgl;

import java.awt.Canvas;
import java.awt.Toolkit;
import java.nio.ByteBuffer;
import java.util.logging.Level;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;

import com.jme.image.Image;
import com.jme.renderer.RenderContext;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.renderer.lwjgl.LWJGLRenderer;
import com.jme.renderer.lwjgl.LWJGLTextureRenderer;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.ImageUtils;
import com.jme.util.LoggingSystem;
import com.jme.util.WeakIdentityCache;
import com.jmex.awt.JMECanvas;
import com.jmex.awt.lwjgl.LWJGLCanvas;

/**
 * <code>LWJGLDisplaySystem</code> defines an implementation of
 * <code>DisplaySystem</code> that uses the LWJGL API for window creation and
 * rendering via OpenGL. <code>LWJGLRenderer</code> is also created that gives
 * a way of displaying data to the created window.
 *
 * @author Mark Powell
 * @author Gregg Patton
 * @author Joshua Slack - Optimizations, Headless rendering, RenderContexts, AWT integration
 * @version $Id: LWJGLDisplaySystem.java,v 1.46 2007-02-05 16:39:08 nca Exp $
 */
public class LWJGLDisplaySystem extends DisplaySystem {

    private LWJGLRenderer renderer;

    private Pbuffer headlessDisplay;
    private JMECanvas canvas;

    private RenderContext currentContext = null;
    private WeakIdentityCache<Object, RenderContext> contextStore = new WeakIdentityCache<Object, RenderContext>();

    /**
     * Constructor instantiates a new <code>LWJGLDisplaySystem</code> object.
     * During instantiation confirmation is made to determine if the LWJGL API
     * is installed properly. If not, a JmeException is thrown.
     */
    public LWJGLDisplaySystem() {
        super();
        LoggingSystem.getLogger().log( Level.INFO,
                "LWJGL Display System created." );
    }

    /**
     * @see com.jme.system.DisplaySystem#isValidDisplayMode(int, int, int, int)
     */
    public boolean isValidDisplayMode( int width, int height, int bpp, int freq ) {
        return getValidDisplayMode( width, height, bpp, freq ) != null;
    }

    /**
     * @see com.jme.system.DisplaySystem#setVSyncEnabled(boolean)
     */
    public void setVSyncEnabled( boolean enabled ) {
        Display.setVSyncEnabled( enabled );
    }

    /**
     * <code>setTitle</code> sets the window title of the created window.
     *
     * @param title the title.
     */
    public void setTitle( String title ) {
        Display.setTitle( title );
    }

    /**
     * <code>createWindow</code> will create a LWJGL display context. This
     * window will be a purely native context as defined by the LWJGL API.
     *
     * @see com.jme.system.DisplaySystem#createWindow(int, int, int, int,
     *      boolean)
     */
    public void createWindow( int w, int h, int bpp, int frq, boolean fs ) throws JmeException {
        // confirm that the parameters are valid.
        if ( w <= 0 || h <= 0 ) {
            throw new JmeException( "Invalid resolution values: " + w + " " + h );
        }
        else if ( ( bpp != 32 ) && ( bpp != 16 ) && ( bpp != 24 ) ) {
            throw new JmeException( "Invalid pixel depth: " + bpp );
        }

        // set the window attributes
        this.width = w;
        this.height = h;
        this.bpp = bpp;
        this.frq = frq;
        this.fs = fs;

        initDisplay();
        renderer = new LWJGLRenderer( width, height );
        switchContext(this);
        updateStates( renderer );

        created = true;
    }

    /**
     * <code>createHeadlessWindow</code> will create a headless LWJGL display
     * context. This window will be a purely native context as defined by the
     * LWJGL API.
     *
     * @see com.jme.system.DisplaySystem#createHeadlessWindow(int, int, int)
     */
    public void createHeadlessWindow( int w, int h, int bpp ) {
        // confirm that the parameters are valid.
        if ( w <= 0 || h <= 0 ) {
            throw new JmeException( "Invalid resolution values: " + w + " " + h );
        }
        else if ( ( bpp != 32 ) && ( bpp != 16 ) && ( bpp != 24 ) ) {
            throw new JmeException( "Invalid pixel depth: " + bpp );
        }

        // set the window attributes
        this.width = w;
        this.height = h;
        this.bpp = bpp;

        initHeadlessDisplay();
        renderer = new LWJGLRenderer( width, height );
        switchContext(this);
        renderer.setHeadless( true );
        updateStates( renderer );

        created = true;
    }

    /**
     * <code>createCanvas</code> will create an AWTGLCanvas context. This
     * window will be a purely native context as defined by the LWJGL API.
     *
     * @see com.jme.system.DisplaySystem#createCanvas(int, int)
     */
    public Canvas createCanvas( int w, int h ) {
        // confirm that the parameters are valid.
        if ( w <= 0 || h <= 0 ) {
            throw new JmeException( "Invalid resolution values: " + w + " " + h );
        }

        // set the window attributes
        this.width = w;
        this.height = h;

        Canvas newCanvas;
        try {
            newCanvas = new LWJGLCanvas();
        } catch ( LWJGLException e ) {
            throw new JmeException( "Unable to create canvas.", e );
        }

        currentContext = new RenderContext();
        contextStore.put(newCanvas, currentContext);

        created = true;

        return newCanvas;
    }

    /**
     * Returns the Pbuffer used for headless display or null if not headless.
     *
     * @return Pbuffer
     */
    public JMECanvas getCurrentCanvas() {
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
     * <p/>
     * If a window is not already created, it calls createWindow and exits.
     * Other wise it calls reinitDisplay and renderer.reinit(width,height)
     *
     * @see com.jme.system.DisplaySystem#recreateWindow(int, int, int, int,
     *      boolean)
     */
    public void recreateWindow( int w, int h, int bpp, int frq, boolean fs ) {
        if ( !created ) {
            createWindow( w, h, bpp, frq, fs );
            return;
        }
        // confirm that the parameters are valid.
        if ( w <= 0 || h <= 0 ) {
            throw new JmeException( "Invalid resolution values: " + w + " " + h );
        }
        else if ( ( bpp != 32 ) && ( bpp != 16 ) && ( bpp != 24 ) ) {
            throw new JmeException( "Invalid pixel depth: " + bpp );
        }

        // set the window attributes
        this.width = w;
        this.height = h;
        this.bpp = bpp;
        this.frq = frq;
        this.fs = fs;

        reinitDisplay();
        renderer.reinit( width, height );
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
     * @return true if a close request is active.
     * @see com.jme.system.DisplaySystem#isClosing()
     */
    public boolean isClosing() {
        if ( headlessDisplay == null ) {
            return Display.isCloseRequested();
        }
       
        return false;        
    }
    
    @Override
    public boolean isActive()
    {
    	return Display.isActive();
    }

    /**
     * <code>reset</code> prepares the window for closing or restarting.
     *
     * @see com.jme.system.DisplaySystem#reset()
     */
    public void reset() {
    }

    /**
     * <code>close</code> destroys the LWJGL Display context.
     */
    public void close() {
        Display.destroy();
    }

    /**
     * <code>createTextureRenderer</code> builds the renderer used to render
     * to a texture.
     */
    public TextureRenderer createTextureRenderer( int width, int height, int target) {
        if ( !isCreated() ) {
            return null;
        }

        return new LWJGLTextureRenderer( width, height,
                (LWJGLRenderer) getRenderer());
    }

    /**
     * <code>getValidDisplayMode</code> returns a <code>DisplayMode</code>
     * object that has the requested width, height and color depth. If there is
     * no mode that supports a requested resolution, null is returned.
     *
     * @param width  the width of the desired mode.
     * @param height the height of the desired mode.
     * @param bpp    the color depth of the desired mode.
     * @param freq   the frequency of the monitor.
     * @return <code>DisplayMode</code> object that supports the requested
     *         resolutions. Null is returned if no valid modes are found.
     */
    private DisplayMode getValidDisplayMode( int width, int height, int bpp,
                                             int freq ) {
        // get all the modes, and find one that matches our width, height, bpp.
        DisplayMode[] modes;
        try {
            modes = Display.getAvailableDisplayModes();
        } catch ( LWJGLException e ) {
            e.printStackTrace();
            return null;
        }
        // Make sure that we find the mode that uses our current monitor freq.

        for ( int i = 0; i < modes.length; i++ ) {
            if ( modes[i].getWidth() == width && modes[i].getHeight() == height
                    && modes[i].getBitsPerPixel() == bpp
                    && ( freq == 0 || modes[i].getFrequency() == freq ) ) {

                return modes[i];
            }
        }

        // none found
        return null;
    }

    /**
     * <code>initDisplay</code> creates the LWJGL window with the desired
     * specifications.
     */
    private void initDisplay() {
        // create the Display.
        DisplayMode mode = selectMode();
        PixelFormat format = getFormat();
        if ( null == mode ) {
            throw new JmeException( "Bad display mode" );
        }

        try {
            Display.setDisplayMode( mode );
            Display.setFullscreen( fs );
            if ( !fs ) {
                int x, y;
                x = ( Toolkit.getDefaultToolkit().getScreenSize().width - width ) >> 1;
                y = ( Toolkit.getDefaultToolkit().getScreenSize().height - height ) >> 1;
                Display.setLocation( x, y );
            }
            Display.create( format );
            // kludge added here... LWJGL does not properly clear their
            // keyboard and mouse buffers when you call the destroy method,
            // so if you run two jme programs in the same jvm back to back
            // the second one will pick up the esc key used to exit out of
            // the first.
            Keyboard.poll();
            Mouse.poll();

        } catch ( Exception e ) {
            // System.exit(1);
            LoggingSystem.getLogger().log( Level.SEVERE, "Cannot create window" );
            LoggingSystem.getLogger().throwing( this.getClass().toString(),
                    "initDisplay()", e );
            throw new JmeException( "Cannot create window: " + e.getMessage() );
        }
    }

    /**
     * <code>initHeadlessDisplay</code> creates the LWJGL window with the
     * desired specifications.
     */
    private void initHeadlessDisplay() {
        // create the Display.
        DisplayMode mode = getValidDisplayMode( width, height, bpp, frq );
        PixelFormat format = getFormat();

        try {
            Display.setDisplayMode( mode ); // done so the renderer has access
            // to this information.
            headlessDisplay = new Pbuffer( width, height, format, null, null );
            headlessDisplay.makeCurrent();
        } catch ( Exception e ) {
            // System.exit(1);
            LoggingSystem.getLogger().log( Level.SEVERE,
                    "Cannot create headless window" );
            LoggingSystem.getLogger().throwing( this.getClass().toString(),
                    "initHeadlessDisplay()", e );
            throw new Error( "Cannot create headless window: " + e.getMessage(), e );
        }
    }

    /**
     * <code>reinitDisplay</code> recreates the LWJGL window with the desired
     * specifications. All textures, etc should remain in the context.
     */
    private void reinitDisplay() {
        // create the Display.
        DisplayMode mode = selectMode();

        try {
            Display.setDisplayMode( mode );
            Display.setFullscreen( fs );
        } catch ( Exception e ) {
            // System.exit(1);
            LoggingSystem.getLogger().log( Level.SEVERE,
                    "Cannot recreate window" );
            LoggingSystem.getLogger().throwing( this.getClass().toString(),
                    "reinitDisplay()", e );
            throw new Error( "Cannot recreate window: " + e.getMessage() );
        }
    }

    // Grab a display mode for use with init / reinit display
    private DisplayMode selectMode() {
        DisplayMode mode;
        if ( fs ) {
            mode = getValidDisplayMode( width, height, bpp, frq );
            if ( null == mode ) {
                throw new JmeException( "Bad display mode" );
            }
        }
        else {
            mode = new DisplayMode( width, height );
        }
        return mode;
    }

    /**
     * <code>setRenderer</code> sets the supplied renderer as this display's
     * renderer. NOTE: If the supplied renderer is not LWJGLRenderer, then it
     * is ignored.
     *
     * @param r the renderer to set.
     */
    public void setRenderer( Renderer r ) {
        if ( r instanceof LWJGLRenderer ) {
            renderer = (LWJGLRenderer) r;
        }
        else {
            LoggingSystem.getLogger().log( Level.WARNING, "Invalid Renderer type" );
        }
    }


    /**
     * Update the display's gamma, brightness and contrast based on the set values.
     */
    protected void updateDisplayBGC() {
        try {
            Display.setDisplayConfiguration( gamma, brightness, contrast );
        } catch ( LWJGLException e ) {
            LoggingSystem.getLogger().warning( "Unable to apply gamma/brightness/contrast settings: " + e.getMessage() );
        }
    }
    
    /**
     * @see com.jme.system.DisplaySystem#setIcon(com.jme.image.Image[])
     * @author Tony Vera
     * @author Tijl Houtbeckers - some changes to handeling non-RGBA8888 Images.
     */
    public void setIcon(Image[] iconImages) {
        ByteBuffer[] iconData = new ByteBuffer[iconImages.length];
        for (int i = 0; i < iconData.length; i++) {
            // RGBA8888 is the format that LWJGL requires, so try to convert if it's not.
        	if (iconImages[i].getType() != Image.RGBA8888) {
        		try {
        			iconImages[i] = ImageUtils.convert(iconImages[i], Image.RGBA8888);
        		} catch(JmeException jmeE) {
        			throw new JmeException("Your icon is in a format that could not be converted to RGBA8888", jmeE);
        		}
        	}
    		
        	iconData[i] = iconImages[i].getData();    
        	iconData[i].rewind();
        }
        Display.setIcon(iconData);
    }

    @Override
    public String getAdapter() {
        return Display.getAdapter();
    }

    @Override
    public String getDriverVersion() {
        return Display.getVersion();
    }
    
    /**
	 * <code>getDisplayVendor</code> returns the vendor of the graphics
	 * adapter
	 * 
	 * @return The adapter vendor
	 */
	public String getDisplayVendor() {
		if(Display.isCreated()) {
			return GL11.glGetString(GL11.GL_VENDOR);
		} else {
			return "Display not yet created.";
		}
	}

	/**
	 * <code>getDisplayRenderer</code> returns details of the adapter
	 * 
	 * @return The adapter details
	 */
	public String getDisplayRenderer() {
		if(Display.isCreated()) {
			return GL11.glGetString(GL11.GL_RENDERER);
		} else {
			return "Display not yet created.";
		}
	}

	/**
	 * <code>getDisplayAPIVersion</code> returns the API version supported
	 * 
	 * @return The api version supported
	 */
	public String getDisplayAPIVersion() {
		if(Display.isCreated()) {
			return GL11.glGetString(GL11.GL_VERSION);
		} else {
			return "Display not yet created.";
		}
	}

    @Override
    public void setCurrentCanvas(JMECanvas canvas) {
        this.canvas = canvas;
    }

    public PixelFormat getFormat() {
        return new PixelFormat( bpp, alphaBits, depthBits,
                stencilBits, samples );
    }

    @Override
    public RenderContext getCurrentContext() {
        return currentContext;
    }
    
    public RenderContext switchContext(Object contextKey) {
        currentContext = contextStore.get(contextKey);
        if (currentContext == null) {
            currentContext = new RenderContext();
            currentContext.setupRecords(getRenderer());
            contextStore.put(contextKey, currentContext);
        }
        return currentContext;
    }
}
