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
package com.jme.sound;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

/**
 * @author Arman Ozcelik
 *
 */
public abstract class MP3Buffer implements ISoundBuffer {

	private Decoder decoder;

	private Bitstream stream;

	private SampleBuffer sampleBuf;

	private int sampleRate;

	protected int channels;
	
	protected ByteBuffer data;

	public void load(String file) {
		try {
			
			InputStream in= new FileInputStream(file);
			BufferedInputStream bin= new BufferedInputStream(in);
			decoder= new Decoder();
			stream= new Bitstream(bin);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		try {
			Header header= null;
			while ((header= stream.readFrame()) != null) {
				if (sampleBuf == null) {
					sampleBuf=
						new SampleBuffer(
							header.frequency(),
							(header.mode() == Header.SINGLE_CHANNEL) ? 1 : 2);
					decoder.setOutputBuffer(sampleBuf);
				}
				if (channels == 0) {
					channels= (header.mode() == Header.SINGLE_CHANNEL) ? MONO16 : STEREO16;
				}
				if (sampleRate == 0) {
					sampleRate= header.frequency();
				}
				//sampleBuf = (SampleBuffer) decoder.decodeFrame(header, stream);
				decoder.decodeFrame(header, stream);
				stream.closeFrame();
				out.write(toByteArray(sampleBuf.getBuffer(), 0, sampleBuf.getBufferLength()));
				
			}
			byte[] obuf= out.toByteArray();
			data= ByteBuffer.allocateDirect(obuf.length);
			data.put(obuf);
			data.rewind();

			
		} catch (BitstreamException bs) {
			bs.printStackTrace();
		} catch (DecoderException e) {
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}

	}

	
	
	public ByteBuffer getBufferData() {
		return data;
	}

	

	
	public int getSampleRate() {
		return sampleRate;
	}

	
	public void release() {
		data.clear();
		data=null;
	}

	protected byte[] toByteArray(short[] samples, int offs, int len) {
		byte[] b= new byte[len * 2];
		int idx= 0;
		while (len-- > 0) {
			b[idx++]= (byte) (samples[offs++] & 0x00FF);
			b[idx++]= (byte) ((samples[offs] >>> 8) & 0x00FF);
		}
		return b;
	}

}
