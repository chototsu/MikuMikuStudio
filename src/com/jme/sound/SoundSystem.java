/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
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
package com.jme.sound;

import java.util.logging.Level;

import com.jme.util.LoggingSystem;

/**
 * @author Arman Ozcelik
 * @version $Id: SoundSystem.java,v 1.6 2004-01-19 23:06:46 Anakan Exp $
 */
public abstract class SoundSystem {
	/**
		* The list of current implemented rendering APIs that subclass SoundSystem.
		*/
	public static final String[] rendererNames= { "LWJGL" };

	protected static boolean created;
	protected static boolean eaxSupported;

	/**
		 * 
		 * <code>getSoundSystem</code> is a factory method that creates the
		 * appropriate sound system specified by the key parameter. If the
		 * key given is not a valid identifier for a specific display system,
		 * null is returned. For valid sound systems see the
		 * <code>rendererNames</code> array.
		 * @param key the display system to use.
		 * @return the appropriate display system specified by the key.
		 */
	public static SoundSystem getSoundEffectSystem(String key) {
		if ("LWJGL".equalsIgnoreCase(key)) {
			LoggingSystem.getLogger().log(Level.INFO, "Initializing Sound System");
			return new LWJGLSoundSystem();
		}
		LoggingSystem.getLogger().log(Level.INFO, "Unknown Sound System key");
		return null;
	}
	
	
	public static SoundSystem getSoundEffectSystem(String key, boolean enableEAX) {
			if ("LWJGL".equalsIgnoreCase(key)) {
				LoggingSystem.getLogger().log(Level.INFO, "Initializing Sound System");
				return new LWJGLSoundSystem(enableEAX);
			}
			LoggingSystem.getLogger().log(Level.INFO, "Unknown Sound System key");
			return null;
	}

	/**
	* <code>isCreated</code> returns the current status of the sound
	* system. If the sound system and  the sound renderer are created, true is returned,
	* otherwise false.
	* 
	* @return whether the sound system is created.
	*/

	public abstract boolean isCreated();

	/**
		* <code>isEAXSupported</code> returns the current status of the sound
		* system's EAX support. 
		* 
		* @return whether the sound system supports EAX.
		*/
	public abstract boolean isEAXSupported();

	/**
	 * Adds a sound sample playing source.
	 * @param name The object that should play the sound, but can also be any object.
	 */

	public abstract void addSource(Object name);

	/**
	 * Loads a sound sample.
	 * @param file The file from which the sound should be loaded.
	 * @param name The sample unique name.
	 */
	public abstract void load(String file, String name);

	/**
	 * Retrieves the "sound player" attached to an object. This object is generally an <code>Entity</code> object.
	 * @param name the object that is attached to the source player
	 * @return the sound player
	 */
	public abstract IPlayer getPlayer(Object name);

}
