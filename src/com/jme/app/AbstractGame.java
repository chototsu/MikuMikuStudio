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

import java.util.logging.Level;

import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.system.PropertiesDialog;
import com.jme.system.PropertiesIO;
import com.jme.util.LoggingSystem;

/**
 * <code>AbstractGame</code> defines a common way to organize the flow of a 
 * game. A subclass must override the init, update, render and cleanup methods.
 * A call to the start method causes the mainloop to begin. The main loop
 * continues to run until finish is called.
 * 
 * @author Mark Powell
 * @version $Id: AbstractGame.java,v 1.4 2004-01-18 22:46:23 mojomonkey Exp $
 */

public abstract class AbstractGame {
    private final static String JME_VERSION_TAG = "jME version 0.4.0";

    //Flag for running the system.
    private boolean finished;
    private boolean dialogRequested = false;
    private boolean noDialog = false;
    protected PropertiesIO properties;
    //display system
    protected DisplaySystem display;

    /**
     * <code>start</code> begins the game. First, <code>initSystem</code> 
     * is called, <code>initGame</code> to set up the game data.
     * After this it enters the main game loop. Here, each frame, 
     * <code>update</code> is called, then <code>render</code>. After the
     * game loop is broken out of via a call to <code>finish</code>,
     * <code>cleanup</code> is called. This method is final and cannot
     * be overridden by the subclass.
     */
    public final void start() {
        LoggingSystem.getLogger().log(Level.INFO, "Application started.");
        try {
            getAttributes();

            initSystem();

            //check if user initialized gl and glu;
            if (display == null) {
                LoggingSystem.getLogger().log(
                    Level.SEVERE,
                    "Display system is null.");

                throw new JmeException("Window must be created during" +                    " initialization.");
            }
            if (!display.isCreated()) {
                LoggingSystem.getLogger().log(
                    Level.SEVERE,
                    "Display system not initialized.");

                throw new JmeException("Window must be created during" +                    " initialization.");
            }
            initGame();

            //main loop
            while (!finished && !display.isClosing()) {
                //update game state
                update();

                //render
                render();

                //swap buffers
                display.getRenderer().displayBackBuffer();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            cleanup();
        }
        LoggingSystem.getLogger().log(Level.INFO, "Application ending.");

        display.reset();
        quit();
    }

    /**
     * 
     * <code>getVersion</code> returns the version of the API.
     * @return the version of the API.
     */
    public String getVersion() {
        return JME_VERSION_TAG;
    }

    /**
     * 
     * <code>useDialogAlways</code> if true will always display a
     * <code>PropertiesDialog</code> for selection of screen properties.
     * If this is false AND useDialogNever is false the dialog will be 
     * displayed only if the properties file is not found.
     * @param value true if the dialog is always to be display false otherwise.
     */
    public void useDialogAlways(boolean value) {
        dialogRequested = value;
        noDialog = false;
    }

    /**
     * 
     * <code>useDialogNever</code> if true will never display a
     * <code>PropertiesDialog</code> for selection of screen properties.
     * If this is false AND useDialogAlways is false the dialog will be 
     * displayed only if the properties file is not found.
     * @param value true if the dialog is never to be display false otherwise.
     */
    public void useDialogNever(boolean value) {
        noDialog = value;
        dialogRequested = false;
    }
    
    /**
     * <code>finish</code> is called to break out of the main game loop. This
     * method is final and cannot be overridden.
     */
    public final void finish() {
        finished = true;
    }

    /**
     * <code>quit</code> is called to exit the program. By default it simply
     * uses the <code>System.exit()</code> method.
     *
     */
    protected void quit() {
        System.exit(0);
    }

    /**
     * <code>update</code> is called each frame and is intended to update 
     * the game state. That is run physics for game entities, check scores,
     * etc.
     */
    protected abstract void update();

    /**
     * <code>render</code> is called each frame and is inteded to display
     * the game information to the OpenGL context.
     */
    protected abstract void render();

    /**
     * <code>initSystem</code> is called once after <code>start</code> is called.
     * This is meant to create all the necessary system components for the client
     * application.
     */
    protected abstract void initSystem();

    /**
     * <code>initGame</code> is called after <code>showTitle</code> to allow
     * the creation of the game data. 
     */
    protected abstract void initGame();

    /**
     * <code>reinit</code> is called at any time by the client application 
     * to rebuild the sub systems.
     *
     */
    protected abstract void reinit();

    /**
     * <code>cleanup</code> is called once after <code>finish</code> is called.
     * This is meant to clean up any created objects before exiting the 
     * application.
     */
    protected abstract void cleanup();

    /**
     * 
     * <code>getAttributes</code> obtains the properties information from
     * either a properties file or dialog.
     *
     */
    private void getAttributes() {
        properties = new PropertiesIO("properties.cfg");
        if ((!properties.load() || dialogRequested) && !noDialog) {
            PropertiesDialog dialog =
                new PropertiesDialog(properties, "data/Images/Monkey.jpg");
            while (!dialog.isDone()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    LoggingSystem.getLogger().log(
                        Level.WARNING,
                        "Error waiting for dialog system, using defaults.");
                }
            }
        }
    }
}
