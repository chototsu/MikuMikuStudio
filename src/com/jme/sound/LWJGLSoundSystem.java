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

import org.lwjgl.openal.AL;

/**
 * @author Arman Ozcelik
 * @version $Id: LWJGLSoundSystem.java,v 1.2 2003-10-20 19:23:34 mojomonkey Exp $
 */
public class LWJGLSoundSystem extends SoundSystem {

	private LWJGLSoundRenderer renderer;
	private int maxSources;
	private int maxBuffers;
	private boolean created;
	/**
	 * TODO Comment
	 */
	public void createSoundSystem(int maxSources, int maxBuffers) {
		this.maxSources = maxSources;
		this.maxBuffers = maxBuffers;
		initOpenAL();
		renderer = new LWJGLSoundRenderer(maxBuffers, maxSources);
		created = true;

	}

	/**
	 * TODO Comment
	 */
	public SoundRenderer getRenderer() {
		return renderer;
	}

	/**
	 * TODO Comment
	 */
	public boolean isCreated() {
		return created;
	}

	/**
	 * TODO Comment
	 */

	private void initOpenAL() {
		try {
			AL.create();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

}
