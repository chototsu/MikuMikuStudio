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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.logging.Level;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.eax.EAX;

import com.jme.sound.utils.EffectPlayerRepository;
import com.jme.sound.utils.EffectRepository;
import com.jme.util.LoggingSystem;

/**
 * @author Arman Ozcelik
 * @version $Id: LWJGLSoundSystem.java,v 1.7 2004-01-19 23:06:46 Anakan Exp $
 */
public class LWJGLSoundSystem extends SoundSystem {

	/**
	 * @param enableEAX
	 */
	public LWJGLSoundSystem(boolean enableEAX) {
		if (!created) {
			LoggingSystem.getLogger().log(Level.INFO, "Initializing OpenAL..."+(enableEAX ? "with EAX" : ""));
			initOpenAL();
			if(enableEAX){
				initEAX();
			}
		}
		created= true;
		setListenerValues();

	}

	private float[] listenerPos= { 0.0f, 0.0f, 0.0f };
	//Velocity of the listener.
	private float[] listenerVel= { 0.0f, 0.0f, 0.0f };
	//Orientation of the listener. (first 3 elements are "at", second 3 are "up")
	private float[] listenerOri= { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f };

	public LWJGLSoundSystem() {
		if (!created) {
			LoggingSystem.getLogger().log(Level.INFO, "Initializing OpenAL...");
			initOpenAL();
		}
		setListenerValues();
		

	}

	/**
	* <code>isCreated</code> returns the current status of the sound
	* system. If the sound system and  the sound renderer are created, true is returned,
	* otherwise false.
	* 
	* @return whether the sound system is created.
	*/
	public boolean isCreated() {
		return created;
	}

	/**
	* <code>isEAXSupported</code> returns the current status of the sound
	* system's EAX support. 
	*@return whether the sound system supports EAX.
	*/
	public boolean isEAXSupported() {
		return eaxSupported;
	}

	/**
	 * Initializes OpenAl and EAX
	 * 
	 */

	private void initOpenAL() {
		try {

			AL.create();
			created= true;
			
			LoggingSystem.getLogger().log(Level.INFO, "OpenAL initalized!");
		} catch (Exception e) {
			LoggingSystem.getLogger().log(Level.SEVERE, "Failed to Initialize OpenAL...");
			e.printStackTrace();
		}

	}

	private void initEAX() {
		if (created) {
			try {
				LoggingSystem.getLogger().log(Level.INFO, "Initalizing EAX");
				EAX.create();
				LoggingSystem.getLogger().log(Level.INFO, "EAX initalized!");
				eaxSupported= true;
			} catch (Exception e1) {
				LoggingSystem.getLogger().log(Level.WARNING, "Failed to Initialize EAX");
				e1.printStackTrace();
				eaxSupported= false;
			}

		}
	}

	private void setListenerValues() {
		AL.alListener3f(AL.AL_POSITION, listenerPos[0], listenerPos[1], listenerPos[2]);
		AL.alListener3f(AL.AL_VELOCITY, listenerVel[0], listenerVel[1], listenerVel[2]);
		AL.alListener3f(AL.AL_ORIENTATION, listenerOri[0], listenerOri[1], listenerOri[2]);
	}

	/**
	 * Adds a sound sample playing source.
	 * @param name The object that should play the sound, but can also be any object.
	 */
	public void addSource(Object name) {
		IntBuffer source= ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		AL.alGenSources(source);
		System.err.println("Source " + source.get(0));
		EffectPlayerRepository.getRepository().bind(name, new LWJGLEffectPlayer(source.get(0)));
	}

	/**
	 * Loads a sound sample.
	 * @param file The file from which the sound should be loaded.
	 * @param name The sample unique name.
	 */
	public void load(String file, String name) {
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
		EffectRepository.getRepository().bind(name, effect);
		buffer.release();
	}

	/**
	 * Retrieves the "sound player" attached to an object. This object is generally an <code>Entity</code> object.
	 * @param name the object that is attached to the source player
	 * @return the sound player
	 */
	public IPlayer getPlayer(Object name) {
		return EffectPlayerRepository.getRepository().getSource(name);
	}

}
