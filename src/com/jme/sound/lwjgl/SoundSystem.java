/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved.
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
 * Created on 24 janv. 2004
 *
 */
package com.jme.sound.lwjgl;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.logging.Level;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;
import com.jme.sound.IBuffer;
import com.jme.sound.IListener;
import com.jme.sound.ISoundSystem;
import com.jme.sound.ISource;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
/**
 * @author Arman Ozcelik
 * @deprecated Use the new sound system implementation please
 */
public class SoundSystem implements ISoundSystem {
	private IListener listener;
	public SoundSystem() {
		initializeOpenAL();
		listener = new Listener();
	}
	/**
	 *
	 */
	private void initializeOpenAL() {
		try {
			AL.create();
			LoggingSystem.getLogger().log(Level.INFO, "OpenAL initalized!");
		} catch (Exception e) {
			LoggingSystem.getLogger().log(Level.SEVERE,
					"Failed to Initialize OpenAL...");
			e.printStackTrace();
		}
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see com.jme.sound.ISoundSystem#getAPIName()
	 */
	public String getAPIName() {
		return "LWJGL";
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see com.jme.sound.ISoundSystem#generateBuffers(int)
	 */
	public IBuffer[] generateBuffers(int numOfBuffers) {
		Buffer[] result = new Buffer[numOfBuffers];
		IntBuffer alBuffers = BufferUtils.createIntBuffer(numOfBuffers);//ByteBuffer.allocateDirect(4
		// *
		// numOfBuffers).order(ByteOrder.nativeOrder()).asIntBuffer();
		AL10.alGenBuffers(alBuffers);
		for (int i = 0; i < numOfBuffers; i++) {
			result[i] = new Buffer(alBuffers.get(i));
		}
		return result;
	}
	/**
	 * <code>loadBuffer</code>
	 *
	 * @param file
	 * @return @see com.jme.sound.ISoundSystem#loadBuffer(java.lang.String)
	 */
	public IBuffer loadBuffer(String file) {
		try {
			URL url = new URL("file:" + file);
			return loadBuffer(url);
		} catch (MalformedURLException e) {
			LoggingSystem.getLogger().log(Level.WARNING,
					"Could not load: " + file);
			return null;
		}
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see com.jme.sound.ISoundSystem#loadBuffer(java.lang.String)
	 */
	public IBuffer loadBuffer(URL file) {
		String fileName = file.getFile();
		if (".wav".equalsIgnoreCase(fileName.substring(fileName
				.lastIndexOf('.')))) {
			return loadWAV(file);
		}
		if (".ogg".equalsIgnoreCase(fileName.substring(fileName
				.lastIndexOf('.')))) {
			return loadOGG(file);
		}
		//        if
		// (".mp3".equalsIgnoreCase(fileName.substring(fileName.lastIndexOf('.'))))
		// {
		//            return loadMP3(file);
		//        }
		return null;
	}

	/*
	 * public static byte[] toByteArray(short[] samples, int offs, int len) {
	 * byte[] b = new byte[len * 2]; int idx = 0; while (len-- > 0) { b[idx++] =
	 * (byte) (samples[offs++] & 0x00FF); b[idx++] = (byte) ((samples[offs] >>>
	 * 8) & 0x00FF); } return b; }
	 */
	/**
	 * @return
	 */
	private IBuffer loadWAV(URL file) {
		AudioInputStream audioStream = null;
		try {
			audioStream = AudioSystem.getAudioInputStream(new BufferedInputStream(file.openStream()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int length = audioStream.getFormat().getChannels()
				* (int) audioStream.getFrameLength()
				* audioStream.getFormat().getSampleSizeInBits() / 8;
		byte[] temp = new byte[length];
		try {
			audioStream.read(temp, 0, length);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ByteBuffer data = BufferUtils.createByteBuffer(length);//ByteBuffer.allocateDirect(length);
		data.put(temp);
		data.rewind();
		
		  // On Mac we need to convert this to big endian
		if (audioStream.getFormat().getSampleSizeInBits() == 16 && ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN)
		{
		    ShortBuffer tmp = data.duplicate().order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
		    while(tmp.hasRemaining())
		        data.putShort(tmp.get());
		    data.rewind();
		} 
		 
		int channels = getChannels(audioStream.getFormat());
		IBuffer[] tmp = generateBuffers(1);
		tmp[0].configure(data, channels, (int) audioStream.getFormat()
				.getSampleRate(), getPlayTime(temp, audioStream.getFormat(), (int)audioStream.getFormat().getSampleRate()));

        LoggingSystem.getLogger().log(Level.INFO,
                "Wav estimated time "+ getPlayTime(temp, audioStream.getFormat(), (int)audioStream.getFormat().getSampleRate()));
		//cleanup
		data.clear();
		data = null;
		try {
			audioStream.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		return tmp[0];
	}
	private IBuffer loadOGG(URL file) {
		int length = 0;
		InputStream input = null;
		ByteArrayOutputStream baout = new ByteArrayOutputStream();

		IBuffer[] tmp = null;
		try {

			input = new BufferedInputStream(file.openStream());
			int convsize = 4096 * 2;
			byte[] convbuffer = new byte[convsize];
			SyncState syncState = new SyncState();
			StreamState streamState = new StreamState();
			Page page = new Page();
			Packet packet = new Packet(); // one raw packet of data for decode
			Info vorbisInfo = new Info(); // struct that stores all the static
			Comment vorbisComment = new Comment(); // struct that stores all the
			DspState dspState = new DspState(); // central working state for the
			Block vorbisBlock = new Block(dspState); // local working space for
			byte[] buffer;
			int bytes = 0;
			syncState.init();
			while (true) {
				int eos = 0;
				int index = syncState.buffer(4096);
				buffer = syncState.data;
				try {
					bytes = input.read(buffer, index, 4096);
				} catch (Exception e) {
                    LoggingSystem.getLogger().log(Level.SEVERE,e.getMessage());
					
				}
				syncState.wrote(bytes);
				if (syncState.pageout(page) != 1) {
					if (bytes < 4096)
						break;
					LoggingSystem.getLogger().log(Level.SEVERE,
							"Input does not appear to be an Ogg bitstream.");
					return new Buffer(0);
				}
				streamState.init(page.serialno());
				vorbisInfo.init();
				vorbisComment.init();
				if (streamState.pagein(page) < 0) {
					LoggingSystem.getLogger().log(Level.SEVERE,
							"Error reading first page of Ogg bitstream data.");
					return new Buffer(0);
				}
				if (streamState.packetout(packet) != 1) {
					LoggingSystem.getLogger().log(Level.SEVERE,
							"Error reading initial header packet.");
					return new Buffer(0);
				}
				if (vorbisInfo.synthesis_headerin(vorbisComment, packet) < 0) {
					LoggingSystem
							.getLogger()
							.log(Level.SEVERE,
									"This Ogg bitstream does not contain Vorbis audio data.");
					return new Buffer(0);
				}
				int i = 0;
				while (i < 2) {
					while (i < 2) {
						int result = syncState.pageout(page);
						if (result == 0)
							break; // Need more data
						if (result == 1) {
							streamState.pagein(page);
							while (i < 2) {
								result = streamState.packetout(packet);
								if (result == 0)
									break;
								if (result == -1) {
									LoggingSystem
											.getLogger()
											.log(Level.SEVERE,
													"Corrupt secondary header.  Exiting.");
									return new Buffer(0);
								}
								vorbisInfo.synthesis_headerin(vorbisComment,
										packet);
								i++;
							}
						}
					}
					index = syncState.buffer(4096);
					buffer = syncState.data;
					try {
						bytes = input.read(buffer, index, 4096);
					} catch (Exception e) {
                        LoggingSystem.getLogger().log(Level.INFO,e.getMessage());
						return new Buffer(0);
					}
					if (bytes == 0 && i < 2) {
						LoggingSystem
								.getLogger()
								.log(Level.SEVERE,
										"End of file before finding all Vorbis headers!");
						return new Buffer(0);
					}
					syncState.wrote(bytes);
				}
				{
					byte[][] ptr = vorbisComment.user_comments;
					for (int j = 0; j < ptr.length; j++) {
						if (ptr[j] == null)
							break;
						LoggingSystem.getLogger().log(Level.INFO,(new String(ptr[j], 0,
								ptr[j].length - 1)));
					}
					LoggingSystem.getLogger().log(
							Level.INFO,
							"\nBitstream is " + vorbisInfo.channels
									+ " channel, " + vorbisInfo.rate + "Hz");
					LoggingSystem.getLogger().log(
							Level.INFO,
							"Encoded by: "
									+ new String(vorbisComment.vendor, 0,
											vorbisComment.vendor.length - 1)
									+ "\n");
				}
				convsize = 4096 / vorbisInfo.channels;
				dspState.synthesis_init(vorbisInfo);
				vorbisBlock.init(dspState);
				float[][][] _pcm = new float[1][][];
				int[] _index = new int[vorbisInfo.channels];
				while (eos == 0) {
					while (eos == 0) {
						int result = syncState.pageout(page);
						if (result == 0)
							break;
						if (result == -1) {
							LoggingSystem
									.getLogger()
									.log(Level.SEVERE,
											"Corrupt or missing data in bitstream; continuing...");
						} else {
							streamState.pagein(page); // can safely ignore
							while (true) {
								result = streamState.packetout(packet);
								if (result == 0)
									break;
								if (result == -1) {
								} else {
									int samples;
									if (vorbisBlock.synthesis(packet) == 0) { // test
										dspState.synthesis_blockin(vorbisBlock);
									}
									while ((samples = dspState
											.synthesis_pcmout(_pcm, _index)) > 0) {
										float[][] pcm = _pcm[0];
										int bout = (samples < convsize
												? samples
												: convsize);
										for (i = 0; i < vorbisInfo.channels; i++) {
											int ptr = i * 2;
											int mono = _index[i];
											for (int j = 0; j < bout; j++) {
												int val = (int) (pcm[i][mono
														+ j] * 32767.);
												if (val > 32767) {
													val = 32767;
												}
												if (val < -32768) {
													val = -32768;
												}
												if (val < 0)
													val = val | 0x8000;
												convbuffer[ptr] = (byte) (val);
												convbuffer[ptr + 1] = (byte) (val >>> 8);
												ptr += 2 * (vorbisInfo.channels);
											}
										}
										baout.write(convbuffer, 0, 2
												* vorbisInfo.channels * bout);

										length += 2 * vorbisInfo.channels
												* bout;
										dspState.synthesis_read(bout);
									}
								}
							}
							if (page.eos() != 0)
								eos = 1;
						}
					}
					if (eos == 0) {
						index = syncState.buffer(4096);
						buffer = syncState.data;
						try {
							bytes = input.read(buffer, index, 4096);
						} catch (Exception e) {
                            LoggingSystem.getLogger().log(Level.SEVERE,e.getMessage());
							return new Buffer(0);
						}
						syncState.wrote(bytes);
						if (bytes == 0)
							eos = 1;
					}
				}
				streamState.clear();
				vorbisBlock.clear();
				dspState.clear();
				vorbisInfo.clear();
			}
			syncState.clear();
			byte[] buf = baout.toByteArray();

			ByteBuffer data = BufferUtils.createByteBuffer(buf.length);//ByteBuffer.allocateDirect(buf.length);
			data.put(buf);
			data.rewind();

			
			  // On Mac we need to convert this to big endian
			if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN)
			{
			    ShortBuffer tmp2 = data.duplicate().order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
			    while(tmp2.hasRemaining())
			        data.putShort(tmp2.get());
			    data.rewind();
			} 
			
			
			tmp = generateBuffers(1);
			int chans = getChannels(vorbisInfo);
			int rate= chans == AL10.AL_FORMAT_MONO16
			? vorbisInfo.rate
					: vorbisInfo.rate;
			float time = (buf.length) / (float)(rate * vorbisInfo.channels * 2);
			tmp[0].configure(data, chans, rate, time);

			LoggingSystem.getLogger().log(Level.INFO,
                    "Sample rate= " + vorbisInfo.rate);
			LoggingSystem.getLogger().log(Level.INFO,"Estimated Play Time " + time);

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return tmp[0];
	}
	private int getChannels(Info vorbisInfo) {
		if (vorbisInfo.channels == 1)
			return AL10.AL_FORMAT_MONO16;
		return AL10.AL_FORMAT_STEREO16;
	}
	/**
	 * @return
	 */
	private int getChannels(AudioFormat format) {
		//		get channels
		if (format.getChannels() == 1) {
			if (format.getSampleSizeInBits() == 8) {
				return AL10.AL_FORMAT_MONO8;
			} else if (format.getSampleSizeInBits() == 16) {
				return AL10.AL_FORMAT_MONO16;
			} else {
				throw new JmeException("Illegal sample size");
			}
		} else if (format.getChannels() == 2) {
			if (format.getSampleSizeInBits() == 8) {
				return AL10.AL_FORMAT_STEREO8;
			} else if (format.getSampleSizeInBits() == 16) {
				return AL10.AL_FORMAT_STEREO16;
			} else {
				throw new JmeException("Illegal sample size");
			}
		} else {
			throw new JmeException("Only mono or stereo is supported");
		}
	}



	private float getPlayTime(byte[] data, AudioFormat format, int rate) {
//		get channels
		if (format.getChannels() == 1) {
			if (format.getSampleSizeInBits() == 8) {
				return (float)(data.length) / (float)(rate );
			} else if (format.getSampleSizeInBits() == 16) {
				return (float)(data.length) / (float)(rate*2);
			} else {
				throw new JmeException("Illegal sample size");
			}
		} else if (format.getChannels() == 2) {
			if (format.getSampleSizeInBits() == 8) {
				return (float)(data.length) / (float)(rate *2);
			} else if (format.getSampleSizeInBits() == 16) {
				return (float)(data.length) / (float)(rate * 4);
			} else {
				throw new JmeException("Illegal sample size");
			}
		} else {
			throw new JmeException("Only mono or stereo is supported");
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jme.sound.ISoundSystem#loadSource(java.lang.String)
	 */
	public ISource loadSource(String file) {
		IBuffer buffer = loadBuffer(file);
		return generateSource(buffer);
	}
	public ISource loadSource(URL file) {
		IBuffer buffer = loadBuffer(file);
		return generateSource(buffer);
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see com.jme.sound.ISoundSystem#generateSources(int)
	 */
	public ISource[] generateSources(int numOfSources) {
		Source[] result = new Source[numOfSources];
		IntBuffer alSources = BufferUtils.createIntBuffer(numOfSources);//ByteBuffer.allocateDirect(4
		// *
		// numOfSources).order(ByteOrder.nativeOrder()).asIntBuffer();
		AL10.alGenSources(alSources);
		for (int i = 0; i < numOfSources; i++) {
			result[i] = new Source(alSources.get(i));
		}
		return result;
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see com.jme.sound.ISoundSystem#generateSource(com.jme.sound.IBuffer)
	 */
	public ISource generateSource(IBuffer buffer) {
		ISource[] tmp = generateSources(1);
		tmp[0].setBuffer(buffer);
		return tmp[0];
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see com.jme.sound.ISoundSystem#getListener()
	 */
	public IListener getListener() {
		return listener;
	}
}
