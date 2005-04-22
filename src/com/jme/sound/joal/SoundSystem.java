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

/*
 * Created on 25 janv. 2004
 *
 */
package com.jme.sound.joal;

import java.net.URL;

import com.jme.sound.IBuffer;
import com.jme.sound.IListener;
import com.jme.sound.ISoundSystem;
import com.jme.sound.ISource;

/**
 * @author Arman Ozcelik
 * @deprecated Use the new sound system implementation please
 */
public class SoundSystem implements ISoundSystem {

	/* (non-Javadoc)
	 * @see com.jme.sound.ISoundSystem#getAPIName()
	 */
	public String getAPIName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.jme.sound.ISoundSystem#generateBuffers(int)
	 */
	public IBuffer[] generateBuffers(int numOfBuffers) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.jme.sound.ISoundSystem#loadBuffer(java.lang.String)
	 */
	public IBuffer loadBuffer(String file) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.jme.sound.ISoundSystem#loadSource(java.lang.String)
	 */
	public ISource loadSource(String file) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.jme.sound.ISoundSystem#generateSources(int)
	 */
	public ISource[] generateSources(int numOfSources) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.jme.sound.ISoundSystem#generateSource(com.jme.sound.IBuffer)
	 */
	public ISource generateSource(IBuffer buffer) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.jme.sound.ISoundSystem#getListener()
	 */
	public IListener getListener() {
		// TODO Auto-generated method stub
		return null;
	}

	/** <code>loadBuffer</code>
	 * @param file
	 * @return
	 * @see com.jme.sound.ISoundSystem#loadBuffer(java.net.URL)
	 */
	public IBuffer loadBuffer(URL file) {
		// TODO Auto-generated method stub
		return null;
	}

	/** <code>loadSource</code>
	 * @param file
	 * @return
	 * @see com.jme.sound.ISoundSystem#loadSource(java.net.URL)
	 */
	public ISource loadSource(URL file) {
		// TODO Auto-generated method stub
		return null;
	}

}
