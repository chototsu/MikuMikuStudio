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
import com.jme.renderer.Camera;

/**
 * The basic frame of a <code>GameState</code>.
 * 
 * @author Per Thulin
 */
public interface GameState {
	
	/**
	 * Gets called every frame by <code>GameStateManager</code>.
	 * 
	 * @param tpf The time since last frame.
	 */
    public abstract void update(float tpf);
    
	/**
	 * Gets called every frame by <code>GameStateManager</code>.
	 * 
	 * @param tpf The time since last frame.
	 */
    public abstract void render(float tpf);
    
    /**
     * Gets called by GameStateManager when switched to.
     */
    public abstract void switchTo();
    
    /**
     * Gets called on removal of this game state.
     */
    public abstract void cleanup();
    
	/**
	 * Gets the camera of this state.
	 * 
	 * @return The camera of this state.
	 */
    public abstract Camera getCamera();
    
    /**
     * Sets the camera of this state.
     */
    public abstract void setCamera(Camera cam);
    
	/**
	 * Gets the state node of this state.
	 * 
	 * @return The state node of this state.
	 */
    public abstract Node getStateNode();
}
