/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved. Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of the Mojo Monkey Coding, jME,
 * jMonkey Engine, nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme.app;

import com.jme.scene.Node;
import com.jme.system.DisplaySystem;
import com.jme.input.InputHandler;
import com.jme.renderer.Camera;

/**
 * A typical game state that initializes a rootNode and camera.
 * The input handler is left to be initialized by derived classes.
 * 
 * @author Per Thulin
 */
public abstract class StandardGameState implements GameState {
	
	// The input handler of this game state.
	protected InputHandler input;
	
	// The node that gets attatched right under the scene tree root.
	protected Node stateNode;
	
	// The camera of this game state.
	protected Camera cam;
	
	/**
	 * Initializes rootNode and camera.
	 */
	public StandardGameState() {
		stateNode = new Node("State rootNode");
		initCamera();
		initInput();
	}

	/**
	 * Updates the InputHandler.
	 * 
	 * @param tpf The time since last frame.
	 */
	public void update(float tpf) {
		input.update(tpf);
	}
	
	/**
	 * Empty.
	 * @see GameState#render(float)
	 * 
	 * @param tpf The time since last frame.
	 */
	public void render(float tpf) {
	
	}
	
	/**
	 * Empty.
	 * @see GameState#switchTo()
	 */
	public void switchTo() {
		
	}

	/**
	 * Empty. 
	 * @see GameState#cleanup()
	 */
	public void cleanup() {

	}
	
	/**
	 * Gets the camera of this state.
	 * 
	 * @return The camera of this state.
	 */
	public Camera getCamera() {
		return cam;
	}
	
	/**
	 * Sets the camera of this state.
	 */
	public void setCamera(Camera cam) {
		this.cam = cam;
	}
	
	/**
	 * Gets the state node of this state.
	 * 
	 * @return The state node of this state.
	 */
	public Node getStateNode() {
		return stateNode;
	}
	
	/**
	 * Initializes a standard camera.
	 */
	private void initCamera() {
		DisplaySystem display = DisplaySystem.getDisplaySystem();
		
		cam = display.getRenderer().createCamera(
				display.getWidth(),
				display.getHeight());
		
		cam.setFrustumPerspective(45.0f,
				(float) display.getWidth() /
				(float) display.getHeight(), 1, 1000);
		
		cam.update();
	}
	
	/**
	 * Initialize the input handler.
	 */
	protected abstract void initInput();
}
