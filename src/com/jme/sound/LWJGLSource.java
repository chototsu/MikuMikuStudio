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
 * Created on 23 oct. 2003
 *
 */
package com.jme.sound;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.logging.Level;

import org.lwjgl.openal.AL;

import com.jme.math.Vector3f;
import com.jme.util.LoggingSystem;

/**
 * @author Arman Ozcelik
 *
 */
public class LWJGLSource implements IMusicPlayer {

	private int sourceNumber;
	private SoundStream stream;
	private boolean paused, playing;
	private BufferedPlayer player;
	private int numberOfBuffers= 1;
	private Vector3f position;

	public LWJGLSource(int sourceNum) {
		this.sourceNumber= sourceNum;
		position= new Vector3f();
		player= new BufferedPlayer();
		player.start();
		LoggingSystem.getLogger().log(Level.INFO, "LWJGLSoundSource created. SourceNumber:  " + sourceNum);
	}

	/**
	 * @return the identification number for this source
	 */
	public int getSourceNumber() {
		return sourceNumber;
	}

	/**
	 * sets the <code>SoundStream</code> that will be played by this source.
	 * @param stream the stream to be played
	 */
	public void setStream(SoundStream stream) {
		if (isPlaying() || isPaused()) {
			stop();
		}
		stream.close();
		this.stream= stream;
	}

	/**
	 * @return the <code>SoundStream</code> is assigned to this source 
	 */
	public SoundStream getStream() {
		return stream;
	}

	/**
	 * Plays the sound that is identified by name.
	 * If the sound type is mp3 the method checks if there are enough back buffers
	 * for streaming. (At least 8)
	 * If the sound type is wave then the number of buffers is set to 1.
	 * @param name the sound name.
	 */
	public void play(String file) {
		playing= true;
		paused= false;
		//String file= StreamRepository.getInstance().getStream(name);
		if (file.endsWith(".mp3")) {
			if (numberOfBuffers < 8)
				numberOfBuffers= 128;
			setStream(new LWJGLMP3Stream(file));
		}
		if (file.endsWith(".wav")) {
			setNumberOfBuffers(1);
			setStream(new LWJGLWaveStream(file));
		}
		

	}

	/**
	 * Updates the position where the sound is played.
	 * The values passed to the method will be put in a <code>Vector3f</code>
	 * and normalized in order to fit with openAL default value ranges.
	 * @param x the x position of the source
	 * @param y the y position of the source
	 * @param z the z position of the source
	 */
	public void updatePosition(float x, float y, float z) {
		Vector3f newPos= new Vector3f(x, y, z);
		updatePosition(newPos);
	}

	/**
	* Updates the position where the sound is played.
	* The vevtor passed to the method will be normalized in 
	* order to fit with openAL default value ranges.
	* @param pos the new position of the source
	*/
	public void updatePosition(Vector3f pos) {
		position.x= pos.x;
		position.y= pos.y;
		position.z= pos.z;
		float length= position.length();
		if (isPlaying() && !isPaused()) {
			AL.alSource3f(
				sourceNumber,
				AL.AL_POSITION,
				position.x / length,
				position.y / length,
				position.z / length);
		}
	}

	/**
	 * @return The actual position ef the source.
	 */
	public Vector3f getPosition() {
		return position;
	}

	public void updateVelocity(float x, float y, float z) {
		if (isPlaying() && !isPaused()) {
			AL.alSource3f(sourceNumber, AL.AL_VELOCITY, x, y, z);
		}
	}

	public boolean isPlaying() {
		return playing;
	}

	public boolean isPaused() {

		return paused;
	}

	public boolean isStopped() {
		return !playing;
	}

	public void stop() {
		AL.alSourceStop(sourceNumber);
		playing= false;
		paused= false;
	}

	public void pause() {
		if (isPlaying()) {
			AL.alSourcePause(sourceNumber);
			paused= true;
		}
	}

	public void setNumberOfBuffers(int buffs) {
		numberOfBuffers= buffs;

	}

	public void setMaxVolume(float value) {
		AL.alSourcef(sourceNumber, AL.AL_MAX_GAIN, value);

	}

	public void setVolume(float value) {
		AL.alSourcef(sourceNumber, AL.AL_GAIN, value);
	}

	private class BufferedPlayer extends Thread {

		private IntBuffer buffers;
		private IntBuffer temp;
		protected BufferedPlayer() {
			buffers=
				ByteBuffer.allocateDirect(4 * numberOfBuffers).order(ByteOrder.nativeOrder()).asIntBuffer();
			temp= ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
			AL.alGenBuffers(buffers);
		}

		public boolean plays() {
			return (AL.alGetSourcei(sourceNumber, AL.AL_SOURCE_STATE) == AL.AL_PLAYING);
		}

		public boolean streamBuffer(int bufferNumber) {
			ByteBuffer data= null;
			try {
				data= stream.read();
				if (data.capacity() == 0) {
					return false;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			AL.alBufferData(
				bufferNumber,
				stream.getChannels(),
				data,
				data.capacity(),
				stream.getSampleRate());
				data.clear();
				data=null;
			return true;
		}

		public boolean playback() {

			if (plays())
				return true;
			for (int a= 0; a < buffers.capacity(); a++) {
				if (!streamBuffer(buffers.get(a)))
					return false;
			}
			AL.alSourceQueueBuffers(sourceNumber, buffers);
			AL.alSourcePlay(sourceNumber);
			return true;
		}

		public boolean update() {
			boolean active= true;
			int processed= AL.alGetSourcei(sourceNumber, AL.AL_BUFFERS_PROCESSED);
			while ((processed > 0)) {
				processed--;
				AL.alSourceUnqueueBuffers(sourceNumber, temp);
				active= streamBuffer(temp.get(0));

				AL.alSourceQueueBuffers(sourceNumber, temp);
			}

			return active;
		}

		public void run() {

			while (stream == null) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			LoggingSystem.getLogger().log(Level.INFO, "New Stream " + stream);

			playback();
			while (update()) {
				if (!plays()) {
					if (!playback()) {

					}
				}
			}

			AL.alSourceStop(sourceNumber);
			AL.alSourceUnqueueBuffers(sourceNumber, buffers);
			playing= false;
			paused= false;
			stream= null;
			run();

		}

	}
	/* (non-Javadoc)
	 * @see com.jme.sound.IMusicPlayer#getNumberOfBuffers()
	 */
	public int getNumberOfBuffers() {
		return numberOfBuffers;
	}

	public int getType() {
		return STREAMING;
	}

}
