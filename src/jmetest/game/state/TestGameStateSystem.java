/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
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
package jmetest.game.state;

import java.util.logging.Level;

import com.jme.app.BaseGame;
import com.jme.app.GameStateManager;
import com.jme.input.InputSystem;
import com.jme.scene.Node;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.util.Timer;


/**
 * This test shows how to use the game state system. It can not extend
 * SimpleGame because a lot of SimpleGames functions (e.g. camera and input)
 * has been delegated down to the individual game states. So this class is
 * basically a stripped down version of SimpleGame which inits the
 * GameStateManager.
 * 
 * @author Per Thulin
 */
public class TestGameStateSystem extends BaseGame {
	
	// The root of our normal scene graph.
	protected Node rootNode;
	
	// High resolution timer for jME.
	protected Timer timer;
	
	// Simply an easy way to get at timer.getTimePerFrame().
	protected float tpf;
	
	/**
	 * This is called every frame in BaseGame.start()
	 * @param interpolation unused in this implementation
	 * @see AbstractGame#update(float interpolation)
	 */
	protected final void update(float interpolation) {
		// Recalculate the framerate.
		timer.update();
		tpf = timer.getTimePerFrame();
		
		// Update the current game state.
		GameStateManager.getInstance().update(tpf);
		
		// Update controllers/render states/transforms/bounds for rootNode.
		rootNode.updateGeometricState(tpf, true);
	}
	
	/**
	 * This is called every frame in BaseGame.start(), after update()
	 * @param interpolation unused in this implementation
	 * @see AbstractGame#render(float interpolation)
	 */
	protected final void render(float interpolation) {	
		// Clears the previously rendered information.
		display.getRenderer().clearBuffers();
		// Draw the rootNode and all its children.
		display.getRenderer().draw(rootNode);
		// Render the current game state.
		GameStateManager.getInstance().render(tpf);
	}
	
	/**
	 * Creates display, sets  up camera, and binds keys.  Called in BaseGame.start() directly after
	 * the dialog box.
	 * @see AbstractGame#initSystem()
	 */
	protected final void initSystem() {
		try {
			/** Get a DisplaySystem acording to the renderer selected in the startup box. */
			display = DisplaySystem.getDisplaySystem(properties.getRenderer());
			/** Create a window with the startup box's information. */
			display.createWindow(
					properties.getWidth(),
					properties.getHeight(),
					properties.getDepth(),
					properties.getFreq(),
					properties.getFullscreen());
			/** Create a camera specific to the DisplaySystem that works with
			 * the display's width and height*/			
		}
		catch (JmeException e) {
			/** If the displaysystem can't be initialized correctly, exit instantly. */
			e.printStackTrace();
			System.exit(1);
		}
		
		/** Get a high resolution timer for FPS updates. */
		timer = Timer.getTimer(properties.getRenderer());
		
		InputSystem.createInputSystem(properties.getRenderer());
	}
	
	/**
	 * Called in BaseGame.start() after initSystem().
	 * @see AbstractGame#initGame()
	 */
	protected final void initGame() {		
		display.setTitle("Test Game State System");
		
		rootNode = new Node("rootNode");
		
		// Create a ZBuffer to display pixels closer to the camera above
		// farther ones.
		ZBufferState buf = display.getRenderer().createZBufferState();
		buf.setEnabled(true);
		buf.setFunction(ZBufferState.CF_LEQUAL);		
		rootNode.setRenderState(buf);
		
		// Creates the GameStateManager. The individual game state nodes will
		// be attached to the scene tree root we send as a parameter.
		GameStateManager.create(rootNode);
		// Adds a new menu state to the game state manager. "Menu" is the key
		// which we in the future will use in order to switch to the menu, or
		// remove it.
		GameStateManager.getInstance().addGameState("Menu", new MenuState());
		// MUST call this before game loop kicks in.
		GameStateManager.getInstance().switchTo("Menu");
		
		rootNode.updateGeometricState(0.0f, true);
		rootNode.updateRenderState();
	}
	
	/**
	 * unused
	 * @see AbstractGame#reinit()
	 */
	protected void reinit() {}
	
	/**
	 * Cleans up the keyboard.
	 * @see AbstractGame#cleanup()
	 */
	protected void cleanup() {
		LoggingSystem.getLogger().log(Level.INFO, "Cleaning up resources.");
		
		// Performs cleanup on all loaded game states.
		GameStateManager.getInstance().cleanup();
		
		if (InputSystem.getKeyInput() != null)
			InputSystem.getKeyInput().destroy();
		if (InputSystem.getMouseInput() != null)
			InputSystem.getMouseInput().destroy();
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TestGameStateSystem app = new TestGameStateSystem();
		app.setDialogBehaviour(TestGameStateSystem.ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}
}
