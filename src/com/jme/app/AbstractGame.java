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

package com.jme.app;

import java.awt.EventQueue;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.system.PropertiesIO;
import com.jme.system.lwjgl.LWJGLPropertiesDialog;

/**
 * <code>AbstractGame</code> defines a common method for implementing game
 * functionality. Client applications should not subclass
 * <code>AbstractGame</code> directly.
 * 
 * @author Eric Woroshow
 * @version $Id: AbstractGame.java,v 1.35 2008/04/10 02:49:38 renanse Exp $
 */
public abstract class AbstractGame {
    private static final Logger logger = Logger.getLogger(AbstractGame.class
            .getName());

    public enum ConfigShowMode {
        /**
         * Never displays a <code>PropertiesDialog</code> on startup, using
         * defaults if no configuration file is found.
         */
        NeverShow,
        /** Always displays a <code>PropertiesDialog</code> on startup. */
        AlwaysShow,
        /**
         * Displays a <code>PropertiesDialog</code> only if the properties
         * file is not found or could not be loaded.
         */
        ShowIfNoConfig;
    }

    protected AbstractGame() {
        // let joystick disabled by default
        // JoystickInput.setProvider( InputSystem.INPUT_SYSTEM_LWJGL );
    }

    /** Flag for running the system. */
    protected boolean finished;

    private final static String JME_VERSION_TAG = "jME version 2.0 dev build 1";
    private final static String DEFAULT_IMAGE = "/jmetest/data/images/Monkey.png";

    // Default to first-run-only behaviour
    private ConfigShowMode configShowMode = ConfigShowMode.ShowIfNoConfig;
    private URL dialogImage = null;

    /** Game display properties. */
    protected PropertiesIO properties;

    /** Renderer used to display the game */
    protected DisplaySystem display;

    //
    // Utility methods common to all game implementations
    //

    /**
     * <code>getVersion</code> returns the version of the API.
     * 
     * @return the version of the API.
     */
    public String getVersion() {
        return JME_VERSION_TAG;
    }

    /**
     * <code>assertDisplayCreated</code> determines if the display system was
     * successfully created before use.
     * 
     * @throws JmeException
     *             if the display system was not successfully created
     */
    protected void assertDisplayCreated() throws JmeException {
        if (display == null) {
            logger.severe("Display system is null.");

            throw new JmeException("Window must be created during"
                    + " initialization.");
        }
        if (!display.isCreated()) {
            logger.severe("Display system not initialized.");

            throw new JmeException("Window must be created during"
                    + " initialization.");
        }
    }

    /**
     * <code>setConfigShowMode</code> defines if and when the display
     * properties dialog should be shown. Setting the behaviour after
     * <code>start</code> has been called has no effect.
     * 
     * @param mode
     *            properties dialog behaviour
     */
    public void setConfigShowMode(ConfigShowMode mode) {
        URL url = null;
        try {
            url = AbstractGame.class.getResource(DEFAULT_IMAGE);
        } catch (Exception e) {
            logger.logp(Level.SEVERE, getClass().toString(),
                    "setDialogShowMode(int)", "Exception", e);
        }
        if (url != null) {
            setConfigShowMode(mode, url);
        } else {
            setConfigShowMode(mode, DEFAULT_IMAGE);
        }
    }

    /**
     * <code>setConfigShowMode</code> defines if and when the display
     * properties dialog should be shown as well as its accompanying image.
     * Setting the behaviour after <code>start</code> has been called has no
     * effect.
     * 
     * @param mode
     *            properties dialog behaviour
     * @param image
     *            a String specifying the filename of an image to be displayed
     *            with the <code>PropertiesDialog</code>. Passing
     *            <code>null</code> will result in no image being used.
     */
    public void setConfigShowMode(ConfigShowMode mode, String image) {
        URL file = null;
        try {
            file = new URL("file:" + image);
        } catch (MalformedURLException e) {
        }

        setConfigShowMode(mode, file);
    }

    /**
     * <code>setConfigShowMode</code> sets how the properties dialog should
     * appear. The url of an image file is also used so you can customize the
     * dialog.
     * 
     * @param mode
     *            ALWAYS_SHOW_PROPS, NEVER_SHOW_PROPS and
     *            FIRSTRUN_OR_NOCONFIGFILE are the valid choices.
     * @param image
     *            the image to display in the box.\
     * @see ConfigShowMode
     */
    public void setConfigShowMode(ConfigShowMode mode, URL image) {
        if (mode == null) {
            throw new NullPointerException("mode can not be null");
        }

        configShowMode = mode;
        dialogImage = image;
    }

    /**
     * <code>getAttributes</code> attempts to first obtain the properties
     * information from the "properties.cfg" file, then a dialog depending on
     * the dialog behaviour.
     */
    protected void getAttributes() {
        properties = new PropertiesIO("properties.cfg");
        boolean loaded = properties.load();

        if ((!loaded && configShowMode == ConfigShowMode.ShowIfNoConfig)
                || configShowMode == ConfigShowMode.AlwaysShow) {

        	final AtomicReference<LWJGLPropertiesDialog> dialogRef = new AtomicReference<LWJGLPropertiesDialog>();
			final Stack<Runnable> mainThreadTasks = new Stack<Runnable>();
			try {
				if (EventQueue.isDispatchThread()) {
					dialogRef.set(new LWJGLPropertiesDialog(properties,
							dialogImage, mainThreadTasks));
				} else {
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							dialogRef.set(new LWJGLPropertiesDialog(properties,
									dialogImage, mainThreadTasks));
						}
					});
				}
			} catch (Exception e) {
				logger.logp(Level.SEVERE, this.getClass().toString(),
						"AbstractGame.getAttributes()", "Exception", e);
				return;
			}
        	
            LWJGLPropertiesDialog dialogCheck = dialogRef.get();
			while (dialogCheck == null || dialogCheck.isVisible()) {
                try {
                	// check worker queue for work
                	while (!mainThreadTasks.isEmpty()) {
                		mainThreadTasks.pop().run();
                	}
                	// go back to sleep for a while
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    logger.warning( "Error waiting for dialog system, using defaults.");
                }
                
                dialogCheck = dialogRef.get();
            }

            if (dialogCheck != null && dialogCheck.isCancelled()) {
                //System.exit(0);
                finish();
            }
        }
    }

    //
    // Main game behavior
    //

    /**
     * <code>start</code> begins the game. The game is initialized by calling
     * first <code>initSystem</code> then <code>initGame</code>. Assuming
     * no errors were encountered during initialization, the main game loop is
     * entered. How the loop operates is implementation-dependent. After the
     * game loop is broken out of via a call to <code>finish</code>,
     * <code>cleanup</code> is called. Subclasses should declare this method
     * final.
     */
    public abstract void start();

    /**
     * <code>finish</code> breaks out of the main game loop. It is preferable
     * to call <code>finish</code> instead of <code>quit</code>.
     */
    public void finish() {
        finished = true;
    }

    /**
     * <code>quit</code> exits the program. By default, it simply uses the
     * <code>System.exit()</code> method.
     */
    protected abstract void quit();

    //
    // Should be overridden by classes _extending_ implementations of Game
    //

    /**
     * <code>update</code> updates the game state. Physics, AI, networking,
     * score checking and like should be completed in this method. How often and
     * when this method is called depends on the main loop implementation.
     * 
     * @param interpolation
     *            definition varies on implementation, -1.0f if unused
     */
    protected abstract void update(float interpolation);

    /**
     * <code>render</code> displays the game information to the OpenGL
     * context. Nothing altering the game state should be run during a render.
     * How often and when this method is called depends on the main loop
     * implementation.
     * 
     * @param interpolation
     *            definition varies on implementation, -1.0f if unused
     */
    protected abstract void render(float interpolation);

    /**
     * <code>initSystem</code> creates all the necessary system components for
     * the client application. It is is called once after <code>start</code>
     * is called. The display <b>must</b> be initialized within this method.
     */
    protected abstract void initSystem();

    /**
     * <code>initGame</code> creates and initializes all game data required
     * for startup. It is suggested that caching of frequently used resources is
     * done within this method. It is called once after <code>initSystem</code>
     * has completed.
     */
    protected abstract void initGame();

    /**
     * <code>reinit</code> rebuilds the subsystems. It may be called at any
     * time by the client application.
     */
    protected abstract void reinit();

    /**
     * <code>cleanup</code> cleans up any created objects before exiting the
     * application. It is called once after <code>finish</code> is called.
     */
    protected abstract void cleanup();
}
