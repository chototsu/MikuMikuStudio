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

import org.lwjgl.openal.AL;

import com.jme.sound.utils.StreamRepository;

/**
 * @author Arman Ozcelik
 *
 */
public class LWJGLSource implements SoundSource {

	private int sourceNumber;
	private SoundStream stream;
	private boolean paused, playing;
	private BufferedPlayer player;
	private int numberOfBuffers = 1;

	public LWJGLSource(int sourceNum) {
		this.sourceNumber = sourceNum;
		player = new BufferedPlayer();
	}

	/**
	 * @return the identification number for this source
	 */
	public int getSourceNumber() {
		return sourceNumber;
	}

	public void setStream(SoundStream stream) {
		if (isPlaying() || isPaused()) {
			stop();
		}
		stream.close();
		this.stream = stream;
	}

	public SoundStream getStream() {
		return stream;
	}

	public void play(String name) {
		String file = StreamRepository.getInstance().getStream(name);
		if (file.endsWith(".mp3")) {
			setStream(new LWJGLMP3Stream(file));
		}
		if (file.endsWith(".wav")) {
			setStream(new LWJGLWaveStream(file));
		}

		player = new BufferedPlayer();
		player.start();
		playing = true;
		paused = false;
	}

	public void updatePosition(float x, float y, float z) {
		if (isPlaying() && !isPaused()) {
			AL.alSource3f(sourceNumber, AL.AL_POSITION, x, y, z);
		}
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
		playing = false;
		paused = false;
	}

	public void pause() {
		if (isPlaying()) {
			AL.alSourcePause(sourceNumber);
			paused = true;
		}
	}

	public void setNumberOfBuffers(int buffs) {
		numberOfBuffers = buffs;

	}

	private class BufferedPlayer extends Thread {

		private IntBuffer buffers;

		protected BufferedPlayer() {
			buffers =
				ByteBuffer
					.allocateDirect(4 * numberOfBuffers)
					.order(ByteOrder.nativeOrder())
					.asIntBuffer();
			AL.alGenBuffers(buffers);
		}

		public boolean plays() {
			return (AL.alGetSourcei(sourceNumber, AL.AL_SOURCE_STATE) == AL.AL_PLAYING);
		}

		public boolean streamBuffer(int bufferNumber) {
			ByteBuffer data = null;
			try {
				data = stream.read();
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
			return true;
		}

		public boolean playback() {
			if (plays())
				return true;
			for (int a = 0; a < buffers.capacity(); a++) {
				if (!streamBuffer(buffers.get(a)))
					return false;
			}
			AL.alSourceQueueBuffers(sourceNumber, buffers);
			AL.alSourcePlay(sourceNumber);
			return true;
		}

		public boolean update() {
			boolean active = true;
			int processed = AL.alGetSourcei(sourceNumber, AL.AL_BUFFERS_PROCESSED);
			while ((processed > 0)) {
				processed--;
				IntBuffer temp =
					ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();

				AL.alSourceUnqueueBuffers(sourceNumber, temp);

				active = streamBuffer(temp.get(0));
				AL.alSourceQueueBuffers(sourceNumber, temp);
			}
			return active;
		}

		public void run() {
			if (stream == null)
				return; //DISPLAY ERROR?

			playback();
			while (update()) {
				if (!plays()) {
					if (!playback()) {

					}
				}
			}
			playing = false;
			paused = false;
		}

	}

}
