/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
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
package com.jme.app;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.system.PropertiesDialog;
import com.jme.system.PropertiesIO;
import com.jme.util.LoggingSystem;

/**
 * <code>AbstractGame</code> defines a common method for implementing game functionality.
 * Client applications should not subclass <code>AbstractGame</code> directly.
 *
 * @author Eric Woroshow
 * @version $Id: AbstractGame.java,v 1.10 2004-03-25 19:25:47 renanse Exp $
 */
public abstract class AbstractGame {
  //Flag for running the system.
    protected boolean finished;

    private final static String JME_VERSION_TAG = "jME version 0.5";
    private final static String DEFAULT_IMAGE = "/jmetest/data/images/Monkey.jpg";

    /** Never displays a <code>PropertiesDialog</code> on startup, using defaults
     * if no configuration file is found. */
    protected final static int NEVER_SHOW_PROPS_DIALOG = 0;

    /** Displays a <code>PropertiesDialog</code> only if the properties file is not
     * found or could not be loaded. */
    protected final static int FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG = 1;

    /** Always displays a <code>PropertiesDialog</code> on startup. */
    protected final static int ALWAYS_SHOW_PROPS_DIALOG = 2;

    //Default to first-run-only behaviour
    private int dialogBehaviour = FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG;
    private URL dialogImage = null;

    /** Game display properties */
    protected PropertiesIO properties;

    /** Renderer used to display the game */
    protected DisplaySystem display;

    //
    //Utility methods common to all game implementations
    //

    /**
     * <code>getVersion</code> returns the version of the API.
     * @return the version of the API.
     */
    public String getVersion() {
        return JME_VERSION_TAG;
    }

    /**
     * <code>assertDisplayCreated</code> determines if the display system
     * was successfully created before use.
     * @throws JmeException if the display system was not successfully created
     */
    protected void assertDisplayCreated() throws JmeException {
        if (display == null) {
            LoggingSystem.getLogger().log(Level.SEVERE, "Display system is null.");

            throw new JmeException("Window must be created during" + " initialization.");
        }
        if (!display.isCreated()) {
            LoggingSystem.getLogger().log(Level.SEVERE, "Display system not initialized.");

            throw new JmeException("Window must be created during" + " initialization.");
        }
    }

    /**
     * <code>setDialogBehaviour</code> defines if and when the display properties
     * dialog should be shown. Setting the behaviour after <code>start</code> has
     * been called has no effect.
     * @param behaviour properties dialog behaviour ID
     */
    public void setDialogBehaviour(int behaviour) {
        URL url = null;
        try {
            url = AbstractGame.class.getResource(DEFAULT_IMAGE);
            System.err.println("url: "+url);
        } catch (Exception e) {
            LoggingSystem.getLogger().throwing(getClass().toString(), "setDialogBehavior(int)", e);
        }
        if (url != null)
            setDialogBehaviour(behaviour, url);
        else
            setDialogBehaviour(behaviour, DEFAULT_IMAGE);
    }

    /**
     * <code>setDialogBehaviour</code> defines if and when the display properties
     * dialog should be shown as well as its accompanying image. Setting the
     * behaviour after <code>start</code> has been called has no effect.
     * @param behaviour properties dialog behaviour ID
     * @param dialogImage a String specifying the filename of an image to be displayed
	 *                       	  with the <code>PropertiesDialog</code>. Passing <code>null</code>
	 *                       	  will result in no image being used.
     */
    public void setDialogBehaviour(int behaviour, String image){
        if (behaviour < NEVER_SHOW_PROPS_DIALOG || behaviour > ALWAYS_SHOW_PROPS_DIALOG)
            throw new IllegalArgumentException("No such properties dialog behaviour");

        dialogBehaviour = behaviour;

        URL file = null;
        try {
            file = new URL("file:" + image);
        } catch (MalformedURLException e) {}
        dialogImage = file;
    }

    /**
     *
     * <code>setDialogBehaviour</code> sets how the properties dialog should
     * appear. ALWAYS_SHOW_PROPS, NEVER_SHOW_PROPS and FIRSTRUN_OR_NOCONFIGFILE
     * are the three valid choices. The url of an image file is also used so
     * you can customize the dialog.
     * @param behaviour ALWAYS_SHOW_PROPS, NEVER_SHOW_PROPS and
     *      FIRSTRUN_OR_NOCONFIGFILE are the valid choices.
     * @param image the image to display in the box.
     */
    public void setDialogBehaviour(int behaviour, URL image){
        if (behaviour < NEVER_SHOW_PROPS_DIALOG || behaviour > ALWAYS_SHOW_PROPS_DIALOG)
            throw new IllegalArgumentException("No such properties dialog behaviour");

        dialogBehaviour = behaviour;
        dialogImage = image;
    }

    /**
     * <code>getAttributes</code> attempts to first obtain the properties
     * information from the "properties.cfg" file, then a dialog depending
     * on the dialog behaviour.
     */
    protected void getAttributes() {
        properties = new PropertiesIO("properties.cfg");
        boolean loaded = properties.load();

        if ((!loaded && dialogBehaviour == FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG)
            || dialogBehaviour == ALWAYS_SHOW_PROPS_DIALOG) {

            PropertiesDialog dialog = new PropertiesDialog(properties, dialogImage);

            while (!dialog.isDone()) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    LoggingSystem.getLogger().log(Level.WARNING, "Error waiting for dialog system, using defaults.");
                }
            }
        }
    }

    //
    //Main game behavior
    //

    /**
     * <code>start</code> begins the game. The game is initialized by calling
     * first <code>initSystem</code> then <code>initGame</code>. Assuming no
     * errors were encountered during initialization, the main game loop is
     * entered. How the loop operates is implementation-dependent. After the
     * game loop is broken out of via a call to <code>finish</code>,
     * <code>cleanup</code> is called. Subclasses should declare this method
     * final.
     */
    public abstract void start();

    /**
     * <code>finish</code> breaks out of the main game loop. It is preferable to
     * call <code>finish</code> instead of <code>quit</code>.
     */
    public abstract void finish();

    /**
     * <code>quit</code> exits the program. By default, it simply uses the
     * <code>System.exit()</code> method.
     */
    protected abstract void quit();

    //
    //Should be overridden by classes _extending_ implementations of Game
    //

    /**
     * <code>update</code> updates the game state. Physics, AI, networking, score
     * checking and like should be completed in this method. How often and when
     * this method is called depends on the main loop implementation.
     * @param interpolation definition varies on implementation, -1.0f if unused
     */
    protected abstract void update(float interpolation);

    /**
     * <code>render</code> displays the game information to the OpenGL context.
     * Nothing altering the game state should be run during a render. How often
     * and when this method is called depends on the main loop implementation.
     * @param interpolation definition varies on implementation, -1.0f if unused
     */
    protected abstract void render(float interpolation);

    /**
     * <code>initSystem</code> creates all the necessary system components
     * for the client application. It is is called once after <code>start</code>
     * is called. The display <b>must</b> be initialized within this method.
     */
    protected abstract void initSystem();

    /**
     * <code>initGame</code> creates and initializes all game data required
     * for startup. It is suggested that caching of frequently used resources
     * is done within this method. It is called once after <code>initSystem</code>
     * has completed.
     */
    protected abstract void initGame();

    /**
     * <code>reinit</code> rebuilds the subsystems. It may be called at any time
     * by the client application.
     */
    protected abstract void reinit();

    /**
     * <code>cleanup</code> cleans up any created objects before exiting the
     * application. It is called once after <code>finish</code> is called.
     */
    protected abstract void cleanup();
}
