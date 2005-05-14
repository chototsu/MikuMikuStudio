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

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;

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
	
	/** The singleton. */
	private static GameStateManager instance;
	
	/** The hashmap that will store all our game states. */
	private HashMap states;
	
	/** The active game state. */
	private GameState current;
	
	/**
	 * Private constructor.
	 */
	private GameStateManager() {
		states = new HashMap();
	}
	
	/**
	 * Creates a new <code>GameStateManager</code>.
	 * 
	 * @return If this is the first time create() is called, a new instance
	 * will be created and returned. Otherwise one should use getInstance()
	 * instead.
	 */
	public static GameStateManager create() {
		if (instance == null) {
			instance = new GameStateManager();
			LoggingSystem.getLogger().log(Level.INFO, "Created GameStateManager");
		}
		return instance;
	}
	
	/**
	 * Returns the singleton instance of this class. <b>Note that create() has
	 * to have been called before this.</b>
	 * 
	 * @return The singleton.
	 */
	public static GameStateManager getInstance() {
		return instance;
	}
	
	/**
	 * Binds a name/key to a <code>GameState</code>. Note that this doesn't make
	 * the passed game state the active one. In order for that, you'd have to call
	 * switchTo(String).
	 * 
	 * @param name The name/key of the Game State to add.
	 * @param state The <code>GameState</code> to bind the passed name to.
	 */
	public void addGameState(String name, GameState state) {
		states.put(name, state);
	}
	
	/**
	 * Removes a <code>GameState</code>, meaning that we call its cleanup() and
	 * remove it from the hashmap.
	 * 
	 * @param name The name/key of the Game State to remove.
	 * @see GameState#cleanup()
	 */
	public void removeGameState(String name) {
		GameState state = (GameState) states.get(name);
		state.cleanup();
		states.remove(name);
	}
	
	/**
	 * Returns the current <code>GameState</code> that is being updated and 
	 * rendered.
	 * 
	 * @return The current game state.
	 */
	public GameState getCurrentState() {
		return current;
	}
	
	/**
	 * Returns the <code>GameState</code> associated with the given name/key.
	 *
	 * @param name The name/key of the game state to find.
	 */
	public GameState getGameState(String name) {
		return (GameState) states.get(name);
	}
	
	/**
	 * <p>
	 * Switches the <code>GameState</code> that should get updated and rendered, 
	 * meaning that we call the old game states switchFrom() method, and call the
	 * new ones switchTo().
	 * </p>
	 * 
	 * <p>
	 * We also
	 * set the new states <code>Camera</code> to our <code>Renderer</code>. If
	 * there is an active <code>ISoundRenderer</code> it also receives the new
	 * <code>Camera</code>.
	 * </p>
	 * 
	 * @param name The name/key of the game state to switch to.
	 * @see GameState#switchTo()
	 * @see GameState#switchFrom()
	 */
	public void switchTo(String name) {
		// Current will be null if it's the first time this method is called.
		if (current != null) {
			current.switchFrom();
		}
		
		current = (GameState) states.get(name);

		DisplaySystem.getDisplaySystem().getRenderer().
			setCamera(current.getCamera());
		
		/*try {
			if (SoundAPIController.getRenderer() != null)
				SoundAPIController.getRenderer().setCamera(current.getCamera());
		} catch (IllegalArgumentException e) {
			// Do nothing - it just means that the sound system hasn't been
			// initialised.
		}*/ //XXX Sound is no longer a core component. Removed due to dependancy 
			
		current.switchTo();
	}
	
	/**
	 * Updates the current/active game state. Should be called every frame.
	 * 
	 * @param tpf The time since last frame.
	 * @see GameState#update()
	 */
	public void update(float tpf) {
		current.update(tpf);
	}
	
	/**
	 * Renders the current/active game state. Should be called every frame.
	 * 
	 * @param tpf The time since last frame.
	 * @see GameState#render()
	 */
	public void render(float tpf) {
		current.render(tpf);
	}
	
	/**
	 * Performs cleanup on all loaded game states. Should be called before
	 * ending your application.
	 * 
	 * @see GameState#cleanup()
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
