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

package jme;

import java.util.ArrayList;

import jme.exception.MonkeyRuntimeException;
import jme.geometry.hud.SplashScreen;
import jme.system.DisplaySystem;

import org.lwjgl.Display;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.Window;

/**
 * <code>AbstractGame</code> defines a common way to organize the flow of a 
 * game. A subclass must override the init, update, render and cleanup methods.
 * A call to the start method causes the mainloop to begin. The main loop
 * continues to run until finish is called.
 * 
 * @author Mark Powell
 * @version 0.1.0
 */

public abstract class AbstractGame {

	//Flag for running the system.
	private boolean finished;
	//holds all splashscreens
	private ArrayList splashScreens = new ArrayList();

	/**
	 * <code>start</code> begins the game. First, <code>initSystem</code> 
	 * is called, next <code>showTitle</code> to display and splash screens
	 * and then <code>initGame</code> to set up the game data.
	 * After this it enters the main game loop. Here, each frame, 
	 * <code>update</code> is called, then <code>render</code>. After the
	 * game loop is broken out of via a call to <code>finish</code>,
	 * <code>cleanup</code> is called. This method is final and cannot
	 * be overridden by the subclass.
	 */
	public final void start() {
		try {
			initSystem();
			
            //check if user initialized gl and glu;
			if (!Window.isCreated()) {
				throw new MonkeyRuntimeException(
					"Window must be created during initialization.");
			}
			showTitle();
			initGame();

			//main loop
			while (!finished && !Window.isCloseRequested()) {
				//update game state
				update();

				//render
				render();

				//swap buffers
				Window.paint();
				Window.update();
			}
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			cleanup();
		}
        Display.resetDisplayMode();
		quit();
	}
	
	/**
	 * <code>showTitle</code> is called after <code>initSystem</code> and is
	 * intended to allow the display of any <code>SplashScreens</code> that
	 * the user desires.
	 */
	public final void showTitle() {
		
		for(int i = 0; i < splashScreens.size(); i++) {
			GL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			GL.glLoadIdentity();
			((SplashScreen)splashScreens.get(i)).render();
			Window.paint();
			Window.update();
			((SplashScreen)splashScreens.get(i)).holdDisplay();
		}
	}

	/**
	 * <code>resetDisplay</code> resets the display system to reflect the
	 * new desired attributes. It calls <code>reinit</code> to reset the
	 * Open GL rendering sub system. This falls on the shoulders of the
	 * subclass to implement.
	 * @param width new width of the resolution.
	 * @param height new height of the resolution.
	 * @param bpp the depth of the resolution.
     * @param freq the frequency of the monitor.
	 * @param fullscreen fullscreen or not.
	 * @param title the name of the window.
	 */
	public void resetDisplay(
		int width,
		int height,
		int bpp,
        int freq,
		boolean fullscreen,
		String title) {
		Keyboard.destroy();
		Mouse.destroy();

		DisplaySystem.getDisplaySystem().setAttributes(
			width,
			height,
			bpp,
            freq,
			fullscreen,
			title);
		reinit();
	}
	
	/**
	 * <code>addSplashScreen</code> adds splash screens to be displayed during
	 * the <code>showTitle</code> portion of the initialization. All 
	 * splash screens should be added in the <code>initSystem</code> phase.
	 * @param screen the splash screen to add.
	 */
	public void addSplashScreen(SplashScreen screen) {
		splashScreens.add(screen);
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
	 * <code>finish</code> is called to break out of the main game loop. This
	 * method is final and cannot be overridden.
	 */
	protected final void finish() {
		finished = true;
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
}
