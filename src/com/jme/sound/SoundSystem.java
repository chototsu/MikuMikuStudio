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

/**
 * @author Arman Ozcelik
 * @version $Id: SoundSystem.java,v 1.2 2003-10-20 19:23:34 mojomonkey Exp $
 */
public abstract class SoundSystem {
	/**
		* The list of current implemented rendering APIs that subclass SoundSystem.
		*/
	public static final String[] rendererNames = { "LWJGL" };

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
	public static SoundSystem getSoundSystem(String key) {
		if ("LWJGL".equalsIgnoreCase(key)) {
			return new LWJGLSoundSystem();
		}
		return null;
	}

	/**
	 * 
	 * @param maxSouces
	 * @param maxBuffers
	 */

	public abstract void createSoundSystem(int maxSouces, int maxBuffers);

	/**
		* <code>getRenderer</code> returns the <code>SoundRenderer</code> implementation
		* that is compatible with the chosen <code>SoundSystem</code>. For 
		* example, if <code>LWJGLSoundSystem</code> is used, the returned 
		* <code>SoundRenderer</code> will be </code>LWJGLSoundRenderer</code>.
		* @see com.jme.sound.SoundRenderer
		* @return the appropriate <code>SoundRenderer</code> implementation that is
		*      compatible with the used <code>SoundSystem</code>.
		*/

	public abstract SoundRenderer getRenderer();

	/**
		* <code>isCreated</code> returns the current status of the sound
		* system. If the sound system and  the sound renderer are created, true is returned,
		* otherwise false.
		* 
		* @return whether the sound system is created.
		*/

	public abstract boolean isCreated();
}
