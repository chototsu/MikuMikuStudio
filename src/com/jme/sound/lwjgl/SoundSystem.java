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
import java.util.logging.Level;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

import org.lwjgl.openal.*;
import org.lwjgl.openal.eax.EAX;

import com.jme.sound.IBuffer;
import com.jme.sound.IListener;
import com.jme.sound.ISoundSystem;
import com.jme.sound.ISource;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;

/**
 * @author Arman Ozcelik
 *
 */
public class SoundSystem implements ISoundSystem {

    private IListener listener;

    public SoundSystem() {
        initializeOpenAL();
        initalizeEAX();
        listener= new Listener();
    }

    /**
     *
     */
    private void initalizeEAX() {
        try {
            LoggingSystem.getLogger().log(Level.INFO, "Initalizing EAX");
            EAX.create();
        } catch (Exception e1) {
            LoggingSystem.getLogger().log(Level.WARNING, "Failed to Initialize EAX");
            e1.printStackTrace();

        }

    }

    /**
     *
     */
    private void initializeOpenAL() {
        try {
            AL.create();
            LoggingSystem.getLogger().log(Level.INFO, "OpenAL initalized!");
        } catch (Exception e) {
            LoggingSystem.getLogger().log(Level.SEVERE, "Failed to Initialize OpenAL...");
            e.printStackTrace();
        }

    }

    /* (non-Javadoc)
     * @see com.jme.sound.ISoundSystem#getAPIName()
     */
    public String getAPIName() {
        return "LWJGL";
    }

    /* (non-Javadoc)
     * @see com.jme.sound.ISoundSystem#generateBuffers(int)
     */
    public IBuffer[] generateBuffers(int numOfBuffers) {
        Buffer[] result= new Buffer[numOfBuffers];
        IntBuffer alBuffers=
            ByteBuffer.allocateDirect(4 * numOfBuffers).order(ByteOrder.nativeOrder()).asIntBuffer();
        AL10.alGenBuffers(alBuffers);
        for (int i= 0; i < numOfBuffers; i++) {
            result[i]= new Buffer(alBuffers.get(i));
        }
        return result;
    }

    /** <code>loadBuffer</code>
     * @param file
     * @return
     * @see com.jme.sound.ISoundSystem#loadBuffer(java.lang.String)
     */
    public IBuffer loadBuffer(String file) {
        try {
            URL url = new URL("file:"+file);
            return loadBuffer(url);
        } catch (MalformedURLException e) {
            LoggingSystem.getLogger().log(Level.WARNING, "Could not load: "+file);
            return null;
        }
    }

    /* (non-Javadoc)
     * @see com.jme.sound.ISoundSystem#loadBuffer(java.lang.String)
     */
    public IBuffer loadBuffer(URL file) {
        String fileName = file.getFile();
        if (".wav".equalsIgnoreCase(fileName.substring(fileName.lastIndexOf('.')))) {
            return loadWAV(file);
        }
        if (".mp3".equalsIgnoreCase(fileName.substring(fileName.lastIndexOf('.')))) {
            return loadMP3(file);
        }
        return null;
    }

    /**
     * @return
     */
    private IBuffer loadMP3(URL file) {
        Decoder decoder= null;
        Bitstream stream= null;
        SampleBuffer sampleBuf= null;
        int sampleRate= 0;
        int channels= 0;
        ByteBuffer data= null;
        try {

            InputStream in= file.openStream();
            BufferedInputStream bin= new BufferedInputStream(in);
            decoder= new Decoder();
            stream= new Bitstream(bin);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        ByteArrayOutputStream out= new ByteArrayOutputStream();
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
                    channels=
                        (header.mode() == Header.SINGLE_CHANNEL)
                            ? AL10.AL_FORMAT_MONO16
                            : AL10.AL_FORMAT_STEREO16;
                }
                if (sampleRate == 0) {
                    sampleRate= header.frequency();
                }
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
        IBuffer[] tmp= generateBuffers(1);
        tmp[0].configure(data, channels, sampleRate);
        //cleanup
        data.clear();
        data= null;
        try {
            stream.close();
        } catch (BitstreamException e1) {
            e1.printStackTrace();
        }

        return tmp[0];
    }

    public static byte[] toByteArray(short[] samples, int offs, int len) {
        byte[] b= new byte[len * 2];
        int idx= 0;
        while (len-- > 0) {
            b[idx++]= (byte) (samples[offs++] & 0x00FF);
            b[idx++]= (byte) ((samples[offs] >>> 8) & 0x00FF);
        }
        return b;
    }

    /**
     * @return
     */
    private IBuffer loadWAV(URL file) {
        AudioInputStream audioStream= null;
        try {
            audioStream= AudioSystem.getAudioInputStream(file.openStream());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int length=
            audioStream.getFormat().getChannels()
                * (int)audioStream.getFrameLength()
                * audioStream.getFormat().getSampleSizeInBits()
                / 8;
        byte[] temp= new byte[length];
        try {
            audioStream.read(temp, 0, length);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        ByteBuffer data= ByteBuffer.allocateDirect(length);
        data.put(temp);
        data.rewind();
        int channels= getChannels(audioStream.getFormat());
        IBuffer[] tmp= generateBuffers(1);
        tmp[0].configure(data, channels, (int)audioStream.getFormat().getSampleRate());
        //cleanup
        data.clear();
        data= null;
        try {
            audioStream.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return tmp[0];
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

    /* (non-Javadoc)
     * @see com.jme.sound.ISoundSystem#loadSource(java.lang.String)
     */
    public ISource loadSource(String file) {
        IBuffer buffer= loadBuffer(file);
        return generateSource(buffer);
    }

    public ISource loadSource(URL file) {
        IBuffer buffer= loadBuffer(file);
        return generateSource(buffer);
    }

    /* (non-Javadoc)
     * @see com.jme.sound.ISoundSystem#generateSources(int)
     */
    public ISource[] generateSources(int numOfSources) {
        Source[] result= new Source[numOfSources];
        IntBuffer alSources=
            ByteBuffer.allocateDirect(4 * numOfSources).order(ByteOrder.nativeOrder()).asIntBuffer();
        AL10.alGenSources(alSources);
        for (int i= 0; i < numOfSources; i++) {
            result[i]= new Source(alSources.get(i));
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.jme.sound.ISoundSystem#generateSource(com.jme.sound.IBuffer)
     */
    public ISource generateSource(IBuffer buffer) {
        ISource[] tmp= generateSources(1);
        tmp[0].setBuffer(buffer);
        return tmp[0];
    }

    /* (non-Javadoc)
     * @see com.jme.sound.ISoundSystem#getListener()
     */
    public IListener getListener() {
        return listener;
    }



}
