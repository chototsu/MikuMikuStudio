/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding All rights reserved.
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
import com.jme.util.LoggingSystem;
import com.jme.util.Timer;

/**
 * <code>FixedFramerateGame</code> attempts to run the game at a fixed frame rate. 
 * The main loop makes every effort to render at the specified rate, however it is
 * not guaranteed that the frame rate will not dip below the desired value. Game
 * logic is updated at the same rate as the rendering. For example, if the rendering
 * is running at 60 frames per second, the logic will also be updated 60 times per
 * second.
 * 
 * Note that <code>setFrameRate(int)</code> cannot be called prior to calling
 * <code>start()</code> or a <code>NullPointerException</code> will be thrown. If
 * no frame rate is specified, the game will run at 60 frames per second.
 *  
 * @author Eric Woroshow
 * @version $Id: FixedFramerateGame.java,v 1.1 2004-02-02 23:02:42 ericthered Exp $
 */
public abstract class FixedFramerateGame extends AbstractGame {
	//Flag for running the system.
	private boolean finished;

	//Frame-rate managing stuff
	private Timer timer;
	private int frames = 0;
	private long startTime;
	private long preferredTicksPerFrame;
	private long frameStartTick;
	private long frameDurationTicks;

	/**
	 * Set preferred frame rate. The main loop will make every attempt to
	 * maintain the given frame rate. This should not be called prior to
	 * the application being <code>start()</code>-ed.
	 * @param fps the desired frame rate in frames per second
	 */
	public void setFrameRate(int fps) {
		if (fps <= 0)
			throw new IllegalArgumentException("Frames per second cannot be less than one.");
		
		LoggingSystem.getLogger().log(Level.INFO, "Attempting to run at " + fps + " fps.");
		preferredTicksPerFrame = timer.getResolution() / fps;
	}
	
	/**
	 * Gets the current frame rate.
	 * @return the current FPS
	 */    
	public float getFramesPerSecond() {
		float time =  (timer.getTime() - startTime) / (float)timer.getResolution();
		float fps  = frames / time;

		startTime = timer.getTime();
		frames = 0;
		
		return fps;
	}

	/**
	 * <code>startFrame</code> begin monitoring the current frame. This method 
	 * should be called every frame before update and drawing code.
	 */
	private void startFrame() {
		frameStartTick = timer.getTime();
	}
	
	/** 
	 * <code>endFrame</code> ends the current frame.  Pads any excess time in the 
	 * frame by sleep()-ing the thread in order to maintain the desired frame rate.  
	 * No attempt is made to rectify frames which have taken too much time.
	 */    
	private void endFrame() {
		frames++;
		
		frameDurationTicks = timer.getTime() - frameStartTick;
		
		while (frameDurationTicks < preferredTicksPerFrame){
			long sleepTime =  ((preferredTicksPerFrame - frameDurationTicks) * 1000) / timer.getResolution();
			
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e){
				LoggingSystem.getLogger().log(Level.WARNING, "Error sleeping during main loop.");
			}
			
			frameDurationTicks = timer.getTime() - frameStartTick;
		}
	}

	/**
	 * Render and update logic at a specified fixed rate.
	 */
	public final void start() {
		LoggingSystem.getLogger().log(Level.INFO, "Application started.");
		try {
			getAttributes();
			timer = Timer.getTimer(properties.getRenderer());
			setFrameRate(60); //default to 60 fps

			initSystem();

			assertDisplayCreated();

			initGame();

			//main loop
			while (!finished && !display.isClosing()) {
				startFrame();

				//update game state, do not use interpolation parameter
				update(-1.0f);

				//render, do not use interpolation parameter
				render(-1.0f);

				//swap buffers
				display.getRenderer().displayBackBuffer();
				
				endFrame();
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
	 * <code>finish</code> is called to break out of the main game loop.
	 */
	public final void finish() {
		finished = true;
	}

	/**
	 * Quits the program abruptly using <code>System.exit</code>.
	 * 
	 * @see AbstractGame#quit()
	 */
	protected void quit() {
		System.exit(0);
	}

	/**
	 * @param interpolation unused in this implementation
	 * @see AbstractGame#update(float interpolation)
	 */
	protected abstract void update(float interpolation);

	/**
	 * @param interpolation unused in this implementation
	 * @see AbstractGame#render(float interpolation)
	 */
	protected abstract void render(float interpolation);

	/**
	 * @see AbstractGame#initSystem()
	 */
	protected abstract void initSystem();

	/**
	 * @see AbstractGame#initGame()
	 */
	protected abstract void initGame();

	/**
	 * @see AbstractGame#reinit()
	 */
	protected abstract void reinit();

	/**
	 * @see AbstractGame#cleanup()
	 */
	protected abstract void cleanup();
}
