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
 * <code>VariableTimestepGame</code> implements a very simple loop, updating
 * game logic and rendering as fast as hardware permits. To compensate for the
 * variable framerate, every frame the update method is passed the amount of
 * elapsed time, in seconds, since the previous update. The game should execute
 * the logic based on the time elapsed.
 *
 * @author Eric Woroshow
 * @version $Id: VariableTimestepGame.java,v 1.6 2004-03-27 04:04:33 renanse Exp $
 */
public abstract class VariableTimestepGame extends AbstractGame {

	//Timing stuff
	private Timer timer;
	private float frametime;

	/**
	 * <code>getFramesPerSecond</code> gets the current frame rate.
	 * @return the current number of frames rendering per second
	 */
	public float getFramesPerSecond() {
		return 1 / frametime;
	}

    /**
     * <code>updateTime</code> calculates the start and stop time of the
     * frame.
     */
	private void updateTime() {
	    timer.update();
		//time1 = timer.getTime();
		frametime = timer.getTimePerFrame(); //(time1 - time0) / (float)timer.getResolution();
		//time0 = time1;
	}

	/**
	 * Renders and updates logic as fast as possible, but keeps track
     * of time elapsed between frames.
	 */
	public final void start() {
		LoggingSystem.getLogger().log(Level.INFO, "Application started.");
		try {
			getAttributes();
			timer = Timer.getTimer(properties.getRenderer());

			initSystem();

			assertDisplayCreated();

			initGame();

			//main loop
			while (!finished && !display.isClosing()) {
				//determine time elapsed since last frame
				updateTime();

				//update game state, pass amount of elapsed time
				update(frametime);

				//render, do not use interpolation parameter
				render(-1.0f);

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
	 * Quits the program abruptly using <code>System.exit</code>.
	 * @see AbstractGame#quit()
	 */
	protected void quit() {
		System.exit(0);
	}

	/**
	 * @param interpolation the time elapsed since the last frame, in seconds
	 * @see AbstractGame#update(float interpolation)
	 */
	protected abstract void update(float deltaTime);

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
