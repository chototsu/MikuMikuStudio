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
package com.jme.app;

import com.jme.scene.Node;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.input.InputHandler;
import com.jme.renderer.Camera;

/**
 * <p>
 * A typical game state that initializes a rootNode, camera and a ZBufferState.
 * The input handler is left to be initialized by derived classes.
 * </p>
 * 
 * <p>
 * In update(float) we update the input and call updateGeometricState(0, true)
 * on our rootNode. In render(float) we just draw the rootNode.
 * </p>
 * 
 * <p>
 * stateUpdate and stateRender can be defined for custom updating and 
 * rendering. Much like in SimpleGame.
 * </p>
 * 
 * @author Per Thulin
 */
public abstract class StandardGameState implements GameState {
	
	/** The input handler of this game state. */
	protected InputHandler input;
	
	/** The node that gets updated and rendered by this GameState. */
	protected Node rootNode;
	
	/** The camera of this game state. */
	protected Camera cam;
	
	/**
	 * Inits rootNode, camera and ZBufferState. Also invokes initInput().
	 */
	public StandardGameState() {
		rootNode = new Node("State rootNode");
		initCamera();
		initInput();
		
		// Create a ZBuffer to display pixels closer to the camera above
		// farther ones.
		ZBufferState buf = DisplaySystem.getDisplaySystem().
			getRenderer().createZBufferState();
		buf.setEnabled(true);
		buf.setFunction(ZBufferState.CF_LEQUAL);		
		rootNode.setRenderState(buf);
		
	    // Update geometric and rendering information for the rootNode.
		rootNode.updateGeometricState(0.0f, true);
	    rootNode.updateRenderState();
	}
	
	/**
	 * Updates the InputHandler and the geometric state of the rootNode.
	 * Also calls stateUpdate(float).
	 * 
	 * @param tpf The elapsed time since last frame.
	 * @see GameState#update(float)
	 * @see StandardGameState#stateUpdate(float)
	 */
	public final void update(float tpf) {
		input.update(tpf);
		
		stateUpdate(tpf);
		
		rootNode.updateGeometricState(tpf, true);
	}
	
	/**
	 * Draws the rootNode. Also calls stateRender(float).
	 * 
	 * @param tpf The elapsed time since last frame.
	 * @see GameState#render(float)
	 * @see StandardGameState#stateRender(float)
	 */
	public final void render(float tpf) {
		stateRender(tpf);
		DisplaySystem.getDisplaySystem().getRenderer().draw(this.rootNode); 
	}
	
	/**
	 * This is where derived classes are supposed to put their game logic.
	 * Gets called between the input.update and 
	 * rootNode.updateGeometricState calls.
	 * 
	 * <p>
	 * Much like the structure of <code>SimpleGame</code>.
	 * </p>
	 * 
	 * @param tpf The time since the last frame.
	 */
	protected void stateUpdate(float tpf) {		
	}
	
	/**
	 * This is where derived classes are supposed to put their render logic.
	 * Gets called before the rootNode gets rendered.
	 * 
	 * <p>
	 * Much like the structure of <code>SimpleGame</code>.
	 * </p>
	 * 
	 * @param tpf The time since the last frame.
	 */
	protected void stateRender(float tpf) {		
	}
	
	/**
	 * Empty.
	 * 
	 * @see GameState#switchTo()
	 */
	public void switchTo() {
	}
	
	/**
	 * Empty.
	 * 
	 * @see GameState#switchFrom()
	 */
	public void switchFrom() {
	}

	/**
	 * Empty.
	 *  
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
	 * Invoked by the constructor after the rootNode and camera has been
	 * initialized. <b>Derived classes must define input here!</b>
	 */
	protected abstract void initInput();
	
}
