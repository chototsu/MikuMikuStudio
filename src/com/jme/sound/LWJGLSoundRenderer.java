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

import org.lwjgl.openal.AL;

/**
 * @author Arman Ozcelik
 * @version $Id: LWJGLSoundRenderer.java,v 1.2 2003-10-20 19:23:34 mojomonkey Exp $
 */
public class LWJGLSoundRenderer implements SoundRenderer {

	private int maxSources;
	private int maxBuffers;

	private IntBuffer buffer;
	private IntBuffer source;
	private static int bufferCounter;
	//Position of the source sounds.
	private float[] sourcePos = { 0.0f, 0.0f, 0.0f };
	//Velocity of the source sounds.
	private float[] sourceVel = { 0.0f, 0.0f, 0.0f };
	//Position of the listener.
	private float[] listenerPos = { 0.0f, 0.0f, 0.0f };
	//Velocity of the listener.
	private float[] listenerVel = { 0.0f, 0.0f, 0.0f };
	//Orientation of the listener. (first 3 elements are "at", second 3 are "up")
	private float[] listenerOri = { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f };

	public LWJGLSoundRenderer(int maxSources, int maxBuffers) {
		this.maxSources = maxSources;
		this.maxBuffers = maxBuffers;
		source = createIntBuffer(maxSources);
		buffer = createIntBuffer(maxBuffers);
		AL.alGenBuffers(buffer);
		AL.alGenSources(source);
		setListenerValues();
	}

	private void setListenerValues() {
		AL.alListener3f(AL.AL_POSITION, listenerPos[0], listenerPos[1], listenerPos[2]);
		AL.alListener3f(AL.AL_VELOCITY, listenerVel[0], listenerVel[1], listenerVel[2]);
		AL.alListener3f(AL.AL_ORIENTATION, listenerOri[0], listenerOri[1], listenerOri[2]);
	}

	/**
	* Creates an integer buffer to hold specified ints
	* - strictly a utility method
	*
	* @param size how many int to contain
	* @return created IntBuffer
	*/
	protected IntBuffer createIntBuffer(int size) {
		ByteBuffer temp = ByteBuffer.allocateDirect(4 * size);
		temp.order(ByteOrder.nativeOrder());
		return temp.asIntBuffer();
	}

	/**
	 * TODO Comment
	 */
	public void loadWaveAs(String bindingName, String file, int source) {
		//load wave data
		WaveData wavefile = new LWJGLWaveData();
        wavefile.create(file);
		AL.alBufferData(
			buffer.get(bufferCounter),
			wavefile.format,
			wavefile.data,
			wavefile.data.capacity(),
			wavefile.samplerate);
		SoundBindingManager.getInstance().bind(
			bindingName,
			new Integer(bufferCounter),
			new Integer(source));
		bufferCounter++;
		wavefile.dispose();
	}
	
	
	/**
	 * TODO Comment
	 */
	public void play(String sound) {
		AL.alGetError();
		if (AL.alGetError() != AL.AL_NO_ERROR) {
			System.err.println("Error generating audio source.");
		}
		AL.alSourcei(
			source.get(SoundBindingManager.getInstance().getSourceByName(sound)),
			AL.AL_BUFFER,
			buffer.get(SoundBindingManager.getInstance().getByName(sound)));
		AL.alSourcef(
			source.get(SoundBindingManager.getInstance().getSourceByName(sound)),
			AL.AL_PITCH,
			1.0f);
		AL.alSourcef(
			source.get(SoundBindingManager.getInstance().getSourceByName(sound)),
			AL.AL_GAIN,
			1.0f);
		AL.alSource3f(
			source.get(SoundBindingManager.getInstance().getSourceByName(sound)),
			AL.AL_POSITION,
			sourcePos[0],
			sourcePos[1],
			sourcePos[2]);
		AL.alSource3f(
			source.get(SoundBindingManager.getInstance().getSourceByName(sound)),
			AL.AL_VELOCITY,
			sourceVel[0],
			sourceVel[1],
			sourceVel[2]);
		AL.alSourcei(
			source.get(SoundBindingManager.getInstance().getSourceByName(sound)),
			AL.AL_LOOPING,
			AL.AL_FALSE);
		AL.alSourcePlay(source.get(SoundBindingManager.getInstance().getSourceByName(sound)));

	}

	/**
	 * TODO Comment
	 */
	public void loop(String sound) {
		AL.alGetError();

		if (AL.alGetError() != AL.AL_NO_ERROR) {
			System.err.println("Error generating audio source.");

		}
		AL.alSourcei(
			source.get(SoundBindingManager.getInstance().getSourceByName(sound)),
			AL.AL_BUFFER,
			buffer.get(SoundBindingManager.getInstance().getByName(sound)));
		AL.alSourcef(
			source.get(SoundBindingManager.getInstance().getSourceByName(sound)),
			AL.AL_PITCH,
			1.0f);
		AL.alSourcef(
			source.get(SoundBindingManager.getInstance().getSourceByName(sound)),
			AL.AL_GAIN,
			1.0f);
		AL.alSource3f(
			source.get(SoundBindingManager.getInstance().getSourceByName(sound)),
			AL.AL_POSITION,
			sourcePos[0],
			sourcePos[1],
			sourcePos[2]);
		AL.alSource3f(
			source.get(SoundBindingManager.getInstance().getSourceByName(sound)),
			AL.AL_VELOCITY,
			sourceVel[0],
			sourceVel[1],
			sourceVel[2]);
		AL.alSourcei(
			source.get(SoundBindingManager.getInstance().getSourceByName(sound)),
			AL.AL_LOOPING,
			AL.AL_TRUE);
		AL.alSourcePlay(source.get(SoundBindingManager.getInstance().getSourceByName(sound)));

	}
	
	
	/**
	 * TODO Comment
	 */
	public void playLocalized(String sound, float posx, float posy, float posz) {
		AL.alGetError();
		if (AL.alGetError() != AL.AL_NO_ERROR) {
			System.err.println("Error generating audio source.");
		}
		AL.alSourcei(
			source.get(SoundBindingManager.getInstance().getSourceByName(sound)),
			AL.AL_BUFFER,
			buffer.get(SoundBindingManager.getInstance().getByName(sound)));
		AL.alSourcef(
			source.get(SoundBindingManager.getInstance().getSourceByName(sound)),
			AL.AL_PITCH,
			1.0f);
		AL.alSourcef(
			source.get(SoundBindingManager.getInstance().getSourceByName(sound)),
			AL.AL_GAIN,
			1.0f);
		AL.alSource3f(
			source.get(SoundBindingManager.getInstance().getSourceByName(sound)),
			AL.AL_POSITION,
			posx,
			posy,
			posz);
		AL.alSource3f(
			source.get(SoundBindingManager.getInstance().getSourceByName(sound)),
			AL.AL_VELOCITY,
			sourceVel[0],
			sourceVel[1],
			sourceVel[2]);
		AL.alSourcei(
			source.get(SoundBindingManager.getInstance().getSourceByName(sound)),
			AL.AL_LOOPING,
			AL.AL_FALSE);
		AL.alSourcePlay(source.get(SoundBindingManager.getInstance().getSourceByName(sound)));

	}
	
	
	/**
	 * TODO Comment
	 */
	public void playFaded(
		String sound,
		final float posx,
		final float posy,
		final float posz,
		final float velx,
		final float vely,
		final float velz) {
		playLocalized(sound, posx, posy, posz);
		
		final int srcNum = SoundBindingManager.getInstance().getSourceByName(sound);
		Thread t = new Thread(new Runnable() {
			float x = posx;
			float y = posy;
			float z = posz;
			public void run() {
				long ticker = 0;
				long lastTime = 0;
				ticker += System.currentTimeMillis() - lastTime;
				lastTime = System.currentTimeMillis();

				while (AL.alGetSourcei(source.get(srcNum), AL.AL_SOURCE_STATE) == AL.AL_PLAYING) {
					if (ticker > 100) {
						ticker=0;
						x += velx;
						y += vely;
						z += velz;
						AL.alSource3f(source.get(srcNum), AL.AL_POSITION, x, y, z);
					}
					ticker += System.currentTimeMillis() - lastTime;
					lastTime = System.currentTimeMillis();
				}
				
			}
		});
		t.start();
	}

}
