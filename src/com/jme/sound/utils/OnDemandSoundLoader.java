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

/*
 * Created on 1 nov. 2003
 *
 */
package com.jme.sound.utils;

import java.util.Vector;

import java.util.logging.Level;

import org.lwjgl.openal.AL;


import com.jme.sound.ISound;
import com.jme.sound.ISoundBuffer;
import com.jme.sound.LWJGLMP3Buffer;
import com.jme.sound.LWJGLWaveBuffer;
import com.jme.sound.SoundEffect;

import com.jme.util.LoggingSystem;

/**
 * @author Arman Ozcelik
 *
 */
public class OnDemandSoundLoader extends Thread {

	private boolean killed;
	private Vector batchList= new Vector();
	private Vector output= new Vector();
	private long waitTime;

	public OnDemandSoundLoader(long wait) {
		waitTime= wait;
	}

	public synchronized OnDemandSoundLoader queueSound(String name, String file) {
		batchList.add(name);
		batchList.add(file);
		return this;
	}

	public void impale() {
		killed= true;
	}

	public void run() {
		while (!killed) {
			String name= null;
			String file= null;
			if (batchList.size() >= 2) {
				synchronized (batchList) {
					name= (String)batchList.remove(0);
					file= (String)batchList.remove(0);

				}
				//SoundSystem.getSoundEffectSystem("LWJGL").load(file, name);
				
				
				ISoundBuffer buffer= null;
				LoggingSystem.getLogger().log(Level.INFO, "Loading " + file + " as " + name);
				if (file.endsWith(".wav")) {
					buffer= new LWJGLWaveBuffer();
					buffer.load(file);
				}
				if (file.endsWith(".mp3")) {
					buffer= new LWJGLMP3Buffer();
					buffer.load(file);
				}
				AL.alBufferData(
					buffer.getBufferNumber(),
					buffer.getChannels(),
					buffer.getBufferData(),
					buffer.getBufferData().capacity(),
					buffer.getSampleRate());
				if (AL.alGetError() != AL.AL_NO_ERROR) {
					LoggingSystem.getLogger().log(Level.WARNING, "Error generating audio buffer");
				}
				
				SoundEffect effect= new SoundEffect(buffer.getBufferNumber(), ISound.SOUND_TYPE_EFFECT);
				EffectRepository rep= EffectRepository.getRepository();
				synchronized (rep) {
					rep.bind(name, effect);
				}
				buffer.release();
				LoggingSystem.getLogger().log(Level.INFO, "Loaded file "+file);

			}
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException ie) {
			}

		}

	}

}
