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

import javax.sound.sampled.AudioFormat;

/**
 * @author Arman Ozcelik
 *
 */
public interface SoundStream {

	public static final int MP3_SOUND_STREAM=1;
	public static final int WAV_SOUND_STREAM=2;
	public static final int OGG_SOUND_STREAM=3;
	
	/**
	 * Closes this stream .
	 *
	 */
	public void close();

	/**
	 * 
	 * @return the audio channels provided by the stream 
	 */
	public int getChannels();

	/**
	 * 
	 * @return the <code>AudioFormat</code> of this stream
	 */
	public AudioFormat getFormat();

	/**
	 * 
	 * @return The length in bytes of the sound data.
	 */
	public int getLength();

	/**
	 * 
	 * @return the sample rate of the sound data contained in this stream
	 */
	public int getSampleRate();

	/**
	 * Reads the rest of the data in the stream.
	 * @return the data in the stream as a direct <code>ByteBuffer</code>
	 * The length is 0 if no more data is available in the Stream.
	 */
	public ByteBuffer read() throws IOException;

		
	
	/**
	 * 
	 * @return the stream type. The values can be  
	 * MP3_SOUND_STREAM=1;
	 * WAV_SOUND_STREAM=2;
	 * OGG_SOUND_STREAM=3;
	 */
	public int getStreamType();

}
