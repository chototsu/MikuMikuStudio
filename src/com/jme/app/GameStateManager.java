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

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;

import com.jme.scene.Node;
import com.jme.sound.SoundAPIController;
import com.jme.system.DisplaySystem;
import com.jme.util.LoggingSystem;

/**
 * A manager to handle multiple game states. The game states are binded to
 * strings that function as keys. Only the active game state gets updated.
 * Use switchTo(String) to switch the active game state.
 * 
 * @author Per Thulin
 */
public class GameStateManager {
	
	// The singleton.
	private static GameStateManager instance;
	
	// The hashmap that will store all our game states.
	private HashMap states;
	
	// The active game state.
	private GameState current;
	
	// The root of the scenegraph.
	private Node rootNode;
	
	/**
	 * Private constructor.
	 * 
	 * @param rootNode The root of the scene graph.
	 */
	private GameStateManager(Node rootNode) {
		this.rootNode = rootNode;
		states = new HashMap();
	}
	
	/**
	 * Creates a new <code>GameStateManager</code> connected to the passed
	 * rootNode.
	 * 
	 * @param rootNode The root of the scene graph.
	 * @return If this is the first time create() is called, a new instance
	 * will be created and returned. Otherwise one should use getInstance()
	 * instead.
	 */
	public static GameStateManager create(Node rootNode) {
		if (instance == null) {
			instance = new GameStateManager(rootNode);
			LoggingSystem.getLogger().log(Level.INFO, "Created GameStateManager");
		}
		return instance;
	}
	
	/**
	 * Returns the singleton instance of this class. Note that create() has to
	 * have been called before this.
	 * 
	 * @return The singleton.
	 */
	public static GameStateManager getInstance() {
		return instance;
	}
	
	/**
	 * Binds a name/key to a <GameState>. Note that this doesn't make the
	 * passed game state the active one. In order for that, you'd have to call
	 * switchTo(String).
	 * 
	 * @param name The name/key of the Game State to add.
	 * @param state The <code>GameState</code> to bind to the passed name.
	 */
	public void addGameState(String name, GameState state) {
		states.put(name, state);
	}
	
	/**
	 * Removes a <code>GameState</code>, meaning that we call its cleanup() and
	 * removes it from the hashmap. It also runs the garbage collector.
	 * 
	 * @param name The name/key of the Game State to remove.
	 */
	public void removeGameState(String name) {
		GameState state = (GameState) states.get(name);
		state.cleanup();
		states.remove(name);
		System.gc();
	}
	
	/**
	 * Switches to a <code>GameState</code>, meaning that we detatch the active
	 * states node and attaches the new ones instead. We also
	 * set the new states <code>Camera</code> to our <code>Renderer</code>. If
	 * there is an active <code>ISoundRenderer</code> it also receives the new
	 * <code>Camera</code>.
	 * 
	 * @param name The name/key of the game state to switch to.
	 */
	public void switchTo(String name) {
		rootNode.detachAllChildren();

		current = (GameState)states.get(name);
		rootNode.attachChild(current.getStateNode());

		DisplaySystem.getDisplaySystem().getRenderer().
			setCamera(current.getCamera());
		
		try {
			if (SoundAPIController.getRenderer() != null)
				SoundAPIController.getRenderer().setCamera(current.getCamera());
		} catch (IllegalArgumentException e) {
			// Do nothing - it just means that the sound system hasn't been
			// initialised.
		}
			
		current.switchTo();

		rootNode.updateRenderState();
		rootNode.updateGeometricState(0, true);
	}
	
	/**
	 * Updates the current/active game state. Should be called every frame.
	 * 
	 * @param tpf The time since last frame.
	 */
	public void update(float tpf) {
		current.update(tpf);
	}
	
	/**
	 * Renders the current/active game state. Should be called every frame.
	 * 
	 * @param tpf The time since last frame.
	 */
	public void render(float tpf) {
		current.render(tpf);
	}
	
	/**
	 * Performs cleanup on all loaded game states. Should be called before
	 * ending your application.
	 */
	public void cleanup() {
		Iterator it = states.entrySet().iterator();
		
		GameState state;
		java.util.Map.Entry next;
		
		while(it.hasNext()) {
			next = (java.util.Map.Entry) it.next();
			state = (GameState) states.get(next.getKey());
			state.cleanup();
		}
		
		states.clear();
	}
}
