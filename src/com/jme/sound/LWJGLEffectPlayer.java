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
 * Created on 31 oct. 2003
 *
 */
package com.jme.sound;

import org.lwjgl.openal.AL;
//import org.lwjgl.openal.eax.BaseEAX;

import com.jme.math.Vector3f;
import com.jme.sound.filter.*;
import com.jme.sound.utils.EffectRepository;

/**
 * @author Arman Ozcelik
 *
 */
public class LWJGLEffectPlayer implements IPlayer {

	private int sourceNumber;
	private int status;
	private Vector3f position;
	private Vector3f velocity;
	private float gain= 1.0f;
	private float pitch= 1.0f;
	private static float maxVolume= 1.0f;

	public LWJGLEffectPlayer(int playerNumber) {
		sourceNumber= playerNumber;
		position= new Vector3f(0, 0, 1);
		velocity= new Vector3f(0, 0, .1f);
	}

	public void play(String name) {
		ISound effect= EffectRepository.getRepository().getSource(name);
		if (effect == null)
			return;
		status= PLAYING;
		AL.alGetError();
		if (AL.alGetError() != AL.AL_NO_ERROR) {
			System.err.println("Error generating audio source.");
		}
		AL.alSourcei(sourceNumber, AL.AL_BUFFER, effect.getID());
		AL.alSourcef(sourceNumber, AL.AL_PITCH, getPitch());
		AL.alSourcef(sourceNumber, AL.AL_GAIN, getVolume());
		AL.alSource3f(sourceNumber, AL.AL_POSITION, position.x, position.y, position.z);
		AL.alSource3f(sourceNumber, AL.AL_VELOCITY, velocity.x, velocity.y, velocity.z);
		AL.alSourcei(sourceNumber, AL.AL_LOOPING, AL.AL_FALSE);
		AL.alSourcePlay(sourceNumber);

	}

	/* (non-Javadoc)
	 * @see com.jme.sound.IEffectPlayer#loop(com.jme.sound.IEffect, int)
	 */
	public void loop(String name) {
		ISound effect= EffectRepository.getRepository().getSource(name);
		if (effect == null)
			return;
		status= LOOPING;
		AL.alGetError();
		if (AL.alGetError() != AL.AL_NO_ERROR) {
			System.err.println("Error generating audio source.");
		}
		AL.alSourcei(sourceNumber, AL.AL_BUFFER, effect.getID());
		AL.alSourcef(sourceNumber, AL.AL_PITCH, getPitch());
		AL.alSourcef(sourceNumber, AL.AL_GAIN, getVolume());
		AL.alSource3f(sourceNumber, AL.AL_POSITION, position.x, position.y, position.z);
		AL.alSource3f(sourceNumber, AL.AL_VELOCITY, velocity.x, velocity.y, velocity.z);
		AL.alSourcei(sourceNumber, AL.AL_LOOPING, AL.AL_TRUE);

		AL.alSourcePlay(sourceNumber);

	}

	public void pause() {
		AL.alSourcePause(sourceNumber);
	}

	public void stop() {
		AL.alSourceStop(sourceNumber);
		status= STOPPED;
	}

	public int getStatus() {
		if (AL.alGetSourcei(sourceNumber, AL.AL_SOURCE_STATE) != AL.AL_PLAYING) {
			status= STOPPED;
		}
		return status;
	}

	public int getType() {
		return BUFFERING;
	}

	public int getSourceNumber() {
		return sourceNumber;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f pos) {
		position.x= pos.x;
		position.y= pos.y;
		position.z= pos.z;
		if ((status == PLAYING || status == LOOPING) && status != PAUSED)
			AL.alSource3f(sourceNumber, AL.AL_POSITION, position.x, position.y, position.z);
	}

	public Vector3f getVelocity() {

		return velocity;
	}

	public void setVelocity(Vector3f vel) {
		velocity.x= vel.x;
		velocity.y= vel.y;
		velocity.z= vel.z;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch= pitch;
	}

	public void setVolume(float volume) {
		gain= volume * maxVolume;
	}

	public float getVolume() {
		return gain;
	}

	public void setPlayersVolume(float volume) {
		maxVolume= volume;
	}

	public void setMaxDistance(float maxDistance) {
		AL.alSourcef(sourceNumber, AL.AL_MAX_DISTANCE, maxDistance);
	}

	/**
	 * 
	 * @param the buffer filter for this player
	 */
	public void applyFilter(BufferFilter f) {
		// (BaseEAX.isCreated()) {
			if (f instanceof LWJGLBufferFilter) {
				((LWJGLBufferFilter)f).applyOnSource(sourceNumber);
			}
		//

	}

	public void applyFilter(ListenerFilter f) {
		//if (BaseEAX.isCreated()) {
		if (f instanceof LWJGLListenerFilter) {
			((LWJGLListenerFilter)f).applyOnSource(sourceNumber);
		}
		//}
	}

}
