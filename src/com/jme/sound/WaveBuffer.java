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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * @author Arman Ozcelik
 *
 */
public abstract class WaveBuffer implements ISoundBuffer {

	protected int bufferNumber;
	protected int channels;
	protected int sampleRate;
	protected ByteBuffer data;
	private AudioFormat format;
	private AudioInputStream audioStream;

	public void load(String file) {
		try {
			audioStream = AudioSystem.getAudioInputStream(new File(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		format = audioStream.getFormat();
		sampleRate = (int) format.getSampleRate();
		System.out.println("Sample Rate "+sampleRate);
		initChannels();
		int length =
			format.getChannels() * (int) audioStream.getFrameLength() * format.getSampleSizeInBits() / 8;
		byte[] temp = new byte[length];
		try {
			audioStream.read(temp, 0, length);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		data = ByteBuffer.allocateDirect(length);
		data.put(temp);
		data.rewind();
		System.out.println("Data length "+data.capacity());	
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public ByteBuffer getBufferData() {
		return data;
	}

	public void release() {
		data.clear();
		data = null;
	}

	private void initChannels() {
		//		get channels
		if (format.getChannels() == 1) {
			if (format.getSampleSizeInBits() == 8) {
				channels = MONO8;
			} else if (format.getSampleSizeInBits() == 16) {
				channels = MONO16;
			} else {
				assert false : "Illegal sample size";
			}
		} else if (format.getChannels() == 2) {
			if (format.getSampleSizeInBits() == 8) {
				channels = STEREO8;
			} else if (format.getSampleSizeInBits() == 16) {
				channels = STEREO16;
			} else {
				assert false : "Illegal sample size";
			}
		} else {
			assert false : "Only mono or stereo is supported";
		}
		System.out.println("Channels "+ channels);

	}

}
