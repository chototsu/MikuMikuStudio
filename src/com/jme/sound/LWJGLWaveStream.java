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
 * Created on 21 oct. 2003
 *
 */
package com.jme.sound;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lwjgl.openal.AL;

import java.nio.ByteBuffer;

/**
 * @author Arman Ozcelik
 *
 */
public class LWJGLWaveStream implements SoundStream {

	private AudioInputStream audioStream;

	private AudioFormat format;

	private int sampleRate;

	private int channels;

	private String file;

	private int length;

	private int offset;

	private boolean isRead;

	/**
	* Creates an audio input stream from the provided file name.
	* The stream must point to valid .wav file data.
	* <code>LWJGLWaveStream</code> provides also the 
	* format of the data contained in the file.
	*  
	*/

	public LWJGLWaveStream(String file) {
		this.file = file;

	}

	private void initChannels() {
		//		get channels
		if (format.getChannels() == 1) {
			if (format.getSampleSizeInBits() == 8) {
				channels = AL.AL_FORMAT_MONO8;
			} else if (format.getSampleSizeInBits() == 16) {
				channels = AL.AL_FORMAT_MONO16;
			} else {
				assert false : "Illegal sample size";
			}
		} else if (format.getChannels() == 2) {
			if (format.getSampleSizeInBits() == 8) {
				channels = AL.AL_FORMAT_STEREO8;
			} else if (format.getSampleSizeInBits() == 16) {
				channels = AL.AL_FORMAT_STEREO16;
			} else {
				assert false : "Illegal sample size";
			}
		} else {
			assert false : "Only mono or stereo is supported";
		}

	}

	public ByteBuffer read() throws IOException {
		if (isRead)
			return ByteBuffer.allocateDirect(0);
		try {
			audioStream = AudioSystem.getAudioInputStream(new File(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (format == null) {
			format = audioStream.getFormat();
		}
		if (sampleRate == 0) {
			sampleRate = (int) format.getSampleRate();
		}
		if(channels==0){
			initChannels();
		}
		
		length = format.getChannels() * (int) audioStream.getFrameLength() * format.getSampleSizeInBits() / 8;
		ByteBuffer buffer = null;
		byte[] temp = new byte[length];
		audioStream.read(temp);
		buffer = ByteBuffer.allocateDirect(length);
		buffer.put(temp);
		buffer.rewind();
		isRead = true;
		return buffer;

	}

	public ByteBuffer read(int nbOfBytes) throws IOException {
		ByteBuffer buffer = null;
		int available = audioStream.available();
		if (available > 0 && available > nbOfBytes) {
			byte[] temp = new byte[nbOfBytes];
			audioStream.read(temp, offset, nbOfBytes);
			offset += nbOfBytes;
			buffer = ByteBuffer.allocateDirect(nbOfBytes);
			buffer.put(temp);
			return buffer;
		}
		if (available > 0 && available <= nbOfBytes) {
			byte[] temp = new byte[available];
			audioStream.read(temp, offset, available);
			offset += available;
			buffer = ByteBuffer.allocateDirect(available);
			buffer.put(temp);
			return buffer;
		}

		return ByteBuffer.allocateDirect(0);
	}

	public void close() {
		if (audioStream != null) {
			try {
				audioStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * @return the format of this wave stream
	 */
	public AudioFormat getFormat() {
		return format;
	}

	/**
	 * @return the sample rate
	 */
	public int getSampleRate() {
		return sampleRate;
	}

	/**
	 * @return
	 */
	public int getChannels() {
		return channels;
	}

	/**
	 * @return
	 */
	public int getLength() {
		return length;
	}

	public int getStreamType() {
		return WAV_SOUND_STREAM;
	}

}
