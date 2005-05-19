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

import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.image.Texture;
import com.jme.input.InputHandler;
import com.jme.util.TextureManager;
import com.jme.math.Vector3f;

import com.jme.app.GameState;
import com.jme.app.GameStateManager;
import com.jme.app.StandardGameState;
import com.jmex.ui.UIActiveObject;
import com.jmex.ui.UIButton;
import com.jmex.ui.UIInputAction;
import com.jmex.ui.UIObject;

/** 
 * @author Per Thulin
 */
public class MenuState extends StandardGameState {
	
	/** The cursor node which holds the mouse gotten from input. */
	private Node cursor;
	
	/** Our display system. */
	private DisplaySystem display;
	
	/** The play button located at the center of the screen. */
	private UIButton playButton;
	
	private InputHandler input;
	
	public MenuState(String name) {
		super(name);
		
		display = DisplaySystem.getDisplaySystem();
		initInput();
		initCursor();
		initButton();
		
		rootNode.setLightCombineMode(LightState.OFF);
		rootNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		rootNode.updateRenderState();
		rootNode.updateGeometricState(0, true);
	}
	
	/**
	 * @see GameStateManager#switchTo(String)
	 */
	public void onActivate() {
		display.setTitle("Test Game State System - Menu State");
		super.onActivate();
	}
	
	/**
	 * Inits the input handler we will use for navigation of the menu.
	 * 
	 * @see StandardGameState#initInput()
	 */
	protected void initInput() {
		input = new MenuHandler();
	}
	
	/**
	 * Creates a pretty cursor.
	 */
	private void initCursor() {		
		Texture texture =
	        TextureManager.loadTexture(
	    	        MenuState.class.getClassLoader().getResource(
	    	        "jmetest/data/cursor/cursor1.png"),
	    	        Texture.MM_LINEAR_LINEAR,
	    	        Texture.FM_LINEAR);
		
		TextureState ts = display.getRenderer().createTextureState();
		ts.setEnabled(true);
		ts.setTexture(texture);
		
		AlphaState alpha = display.getRenderer().createAlphaState();
		alpha.setBlendEnabled(true);
		alpha.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		alpha.setDstFunction(AlphaState.DB_ONE);
		alpha.setTestEnabled(true);
		alpha.setTestFunction(AlphaState.TF_GREATER);
		alpha.setEnabled(true);
		
		input.getMouse().setRenderState(ts);
		input.getMouse().setRenderState(alpha);
		input.getMouse().setLocalScale(new Vector3f(1, 1, 1));
		
		cursor = new Node("Cursor");
		cursor.attachChild(input.getMouse());
		
		rootNode.attachChild(cursor);
	}
	
	/**
	 * Inits the button placed at the center of the screen.
	 */
	private void initButton() {		
        playButton = new UIButton("Play Button",
				(display.getWidth()/2)-128, (display.getHeight()/2), 256, 64,
				input,
				"jmetest/data/images/buttonup.png",
				"jmetest/data/images/buttonover.png",
				"jmetest/data/images/buttondown.png",
				UIActiveObject.TEXTURE
                | UIActiveObject.DRAW_DOWN | UIActiveObject.DRAW_OVER);
		
		playButton.addAction(new UIInputAction() {
			public void performAction(UIObject object) {
				UIActiveObject aObject = (UIActiveObject) object;
				// If the button has been pressed we create and switch to 
				// the IngameState.
				if (aObject.getState() == UIActiveObject.DOWN) {
					GameState ingame = new IngameState("ingame");
					ingame.setActive(true);
					GameStateManager.getInstance().attachChild(ingame);
					setActive(false); // Deactivate this (the menu) state.
				}
			}
		});
		
		rootNode.attachChild(playButton);
	}
	
	/**
	 * Updates input and button.
	 * 
	 * @param tpf The time since last frame.
	 * @see GameState#update(float)
	 */
	protected void stateUpdate(float tpf) {
		input.update(tpf);
		// Check if the button has been pressed.
		playButton.update(tpf);
	}
	
}