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

import com.jme.app.GameStateManager;
import com.jme.app.StandardGameState;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

/**
 * @author Per Thulin
 */
public class IngameState extends StandardGameState {

	public IngameState() {
		super();		
		initGame();
	}
	
	/**
	 * Gets called every time the game state manager switches to this game state.
	 * Sets the window title.
	 */
	public void switchTo() {
		DisplaySystem.getDisplaySystem().
			setTitle("Test Game State System - Ingame State");
	}
	
	/**
	 * Inits the quad and its texture.
	 */
	private void initGame() {
	    cam.setLocation(new Vector3f(0,10,0));
	    cam.update();
		
	    Quad q = new Quad("Quad", 200, 200);
	    q.setModelBound(new BoundingSphere());
	    q.updateModelBound();
	    q.setLocalRotation(new Quaternion(new float[] {90*FastMath.DEG_TO_RAD,0,0}));
	    
	    stateNode.attachChild(q);
	    
	    TextureState ts = 
	    	DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
	    Texture texture =
	    	TextureManager.loadTexture(
                IngameState.class.getClassLoader().getResource(
                "jmetest/data/texture/dirt.jpg"),
                Texture.MM_LINEAR_LINEAR,
                Texture.FM_LINEAR);
	    texture.setWrap(Texture.WM_WRAP_S_WRAP_T);
	    ts.setTexture(texture);
	    ts.setEnabled(true);
	    
	    q.setRenderState(ts);
	}
	
	/**
	 * Gets called from super constructor. Sets up the input handler that let
	 * us walk around using the w,s,a,d keys and mouse.
	 */
	protected void initInput() {
	    input = new IngameHandler(cam, "LWJGL");
	    input.setKeySpeed(10f);
	    input.setMouseSpeed(1f);
	    
	    // Bind the exit action to the escape key.
	    KeyBindingManager.getKeyBindingManager().set(
	        "exit",
	        KeyInput.KEY_ESCAPE);
	}
	
	/**
	 * Gets called every frame. Updates input and checks if user has pressed
	 * the exit key. If the user has, we go back to main menu.
	 */
	public void update(float tpf) {
		super.update(tpf);
		if (KeyBindingManager
		        .getKeyBindingManager()
		        .isValidCommand("exit", false)) {
			// Here we switch to the menu state which is already loaded
			GameStateManager.getInstance().switchTo("Menu");
			// And remove this state, because we don't want to keep it in memory.
			GameStateManager.getInstance().removeGameState("Ingame");
		}
	}
}
