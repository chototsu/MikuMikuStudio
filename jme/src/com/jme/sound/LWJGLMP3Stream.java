package com.jme.sound;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.sound.sampled.AudioFormat;

import org.lwjgl.openal.AL;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

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

/**
 * @author Arman Ozcelik
 *
 */
public class LWJGLMP3Stream implements SoundStream {

	private AudioFormat format;

	private int sampleRate;

	private int channels;

	private String file;

	private boolean autoReload;

	private int length;

	private int offset;

	private Decoder decoder;

	private Bitstream stream;

	public boolean flag;

	private SampleBuffer sampleBuf;

	public LWJGLMP3Stream(String file) {
		this.file = file;
		try {

			InputStream in = new FileInputStream(file);
			BufferedInputStream bin = new BufferedInputStream(in);
			decoder = new Decoder();
			stream = new Bitstream(bin);

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see com.jme.sound.SoundStream#close()
	 */
	public void close() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.jme.sound.SoundStream#getChannels()
	 */
	public int getChannels() {
		return channels;
	}

	/* (non-Javadoc)
	 * @see com.jme.sound.SoundStream#getFormat()
	 */
	public AudioFormat getFormat() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.jme.sound.SoundStream#getLength()
	 */
	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.jme.sound.SoundStream#getSampleRate()
	 */
	public int getSampleRate() {

		return sampleRate;
	}

	public ByteBuffer read() throws IOException {
		try {

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Header header = stream.readFrame();
			if (header == null) {
				//TODO reload?
				return ByteBuffer.allocateDirect(0);
			}
			if (sampleBuf == null) {
				sampleBuf =
					new SampleBuffer(
						header.frequency(),
						(header.mode() == Header.SINGLE_CHANNEL) ? 1 : 2);
				decoder.setOutputBuffer(sampleBuf);
			}
			channels =
				(header.mode() == Header.SINGLE_CHANNEL)
					? AL.AL_FORMAT_MONO16
					: AL.AL_FORMAT_STEREO16;
			sampleRate = header.frequency();
			sampleBuf = (SampleBuffer) decoder.decodeFrame(header, stream);
			stream.closeFrame();

			buffer.write(toByteArray(sampleBuf.getBuffer(), 0, sampleBuf.getBufferLength()));
			ByteBuffer obuf = ByteBuffer.allocateDirect(buffer.size());
			obuf.put(buffer.toByteArray());
			obuf.rewind();
			return obuf;

		} catch (BitstreamException bs) {
			bs.printStackTrace();
		} catch (DecoderException e) {
			e.printStackTrace();
		}

		return ByteBuffer.allocateDirect(0);
	}

	/* (non-Javadoc)
	 * @see com.jme.sound.SoundStream#read(int)
	 */
	public ByteBuffer read(int nbOfBytes) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	protected byte[] toByteArray(short[] samples, int offs, int len) {
		byte[] b = new byte[len * 2];
		int idx = 0;
		short s;
		while (len-- > 0) {
			s = samples[offs++];
			b[idx++] = (byte) (s & 0x00FF);
			b[idx++] = (byte) ((s >>> 8) & 0x00FF);
		}
		return b;
	}

	protected IntBuffer createIntBuffer(int size) {
		ByteBuffer temp = ByteBuffer.allocateDirect(4 * size);
		temp.order(ByteOrder.nativeOrder());
		return temp.asIntBuffer();
	}

	public int getStreamType() {
		return MP3_SOUND_STREAM;
	}

}
