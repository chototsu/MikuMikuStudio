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
/**
 * Created on Apr 22, 2005
 */
package com.jmex.sound.openAL.objects.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.logging.Level;

import javax.sound.sampled.AudioFormat;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import com.jcraft.jorbis.Info;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;

public class SampleLoader {
    
    /**
     * <code>loadBuffer</code>
     *
     * @param file
     * @return @see com.jmex.sound.ISoundSystem#loadBuffer(java.lang.String)
     */
    public static Buffer loadBuffer(String file) {
        try {
            URL url = new URL("file:" + file);
            return loadBuffer(url);
        } catch (MalformedURLException e) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Could not load: " + file);
            return null;
        }
    }
    
    
    public static Buffer loadBuffer(URL file) {
        String fileName = file.getFile();
        if (".wav".equalsIgnoreCase(fileName.substring(fileName
                .lastIndexOf('.')))) {
            return loadWAV(file);
        }
        if (".ogg".equalsIgnoreCase(fileName.substring(fileName
                .lastIndexOf('.')))) {
            return loadOGG(file);
        }
        return null;
    }
    
    private static Buffer loadWAV(URL file) {
        
        InputStream in=null;
        Buffer[] tmp=null;
        try {
            in = new BufferedInputStream(file.openStream());            
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream(1024*256);
            byteOut.reset();
            byte copyBuffer[] = new byte[1024*4];
            WavInputStream wavInput = new WavInputStream(in);
            boolean done = false;
            int bytesRead=-1;
            int length=0;
            while (!done) {
                bytesRead = wavInput.read(copyBuffer, 0, copyBuffer.length);                
                byteOut.write(copyBuffer, 0, bytesRead);
                done = (bytesRead != copyBuffer.length || bytesRead < 0);                
            }
            ByteBuffer data = BufferUtils.createByteBuffer(byteOut.size());
            data.put(byteOut.toByteArray());
            data.rewind();
            if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN)
            {
                ShortBuffer tmp2 = data.duplicate().order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
                while(tmp2.hasRemaining())
                    data.putShort(tmp2.get());
                data.rewind();
            }
            int channels = wavInput.channels();
            tmp = Buffer.generateBuffers(1);
            float time = (byteOut.size()) / (float)(wavInput.rate() * channels * 2);
            LoggingSystem.getLogger().log(Level.INFO,
                    "Wav estimated time "+ time+ "  rate: "+wavInput.rate()+"  channels: "+channels+"  depth: "+wavInput.depth());
            tmp[0].configure(data, getChannels(wavInput), wavInput.rate(), time);
            //cleanup
            data.clear();
            data = null;            
            wavInput.close();
        } catch (IOException e) {
            
            e.printStackTrace();
        }
        return tmp[0];
        
    }
    
    private static Buffer loadOGG(URL file) {        
        
        InputStream in=null;
        Buffer[] tmp=null;
        try {
            in = new BufferedInputStream(file.openStream());            
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream(1024*256);
            byteOut.reset();
            byte copyBuffer[] = new byte[1024*4];
            OggInputStream oggInput = new OggInputStream(in);
            boolean done = false;
            int bytesRead=-1;
            int length=0;
            while (!done) {
                bytesRead = oggInput.read(copyBuffer, 0, copyBuffer.length);
                
                byteOut.write(copyBuffer, 0, bytesRead);
                done = (bytesRead != copyBuffer.length || bytesRead < 0);
                
            }
            ByteBuffer data = BufferUtils.createByteBuffer(byteOut.size());
            data.put(byteOut.toByteArray());
            data.rewind();
            if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN)
            {
                ShortBuffer tmp2 = data.duplicate().order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
                while(tmp2.hasRemaining())
                    data.putShort(tmp2.get());
                data.rewind();
            }
            int channels = oggInput.getInfo().channels;
            tmp = Buffer.generateBuffers(1);
            float time = (byteOut.size()) / (float)(oggInput.getInfo().rate * oggInput.getInfo().channels * 2);
            tmp[0].configure(data, getChannels(oggInput.getInfo()), oggInput.getInfo().rate, time);
            LoggingSystem.getLogger().log(Level.INFO,
                    "Ogg estimated time "+ time);
            //cleanup
            data.clear();
            data = null;            
            oggInput.close();
        } catch (IOException e) {
            
            e.printStackTrace();
        }
        return tmp[0];
    }
    
    private static int getChannels(Info vorbisInfo) {
        if (vorbisInfo.channels == 1)
            return AL10.AL_FORMAT_MONO16;
        return AL10.AL_FORMAT_STEREO16;
    }
    /**
     * @return
     */
    private static int getChannels(WavInputStream format) {
        //      get channels
        if (format.channels() == 1) {
            if (format.depth() == 8) {
                return AL10.AL_FORMAT_MONO8;
            } else if (format.depth() == 16) {
                return AL10.AL_FORMAT_MONO16;
            } else {
                throw new JmeException("Illegal sample size");
            }
        } else if (format.channels() == 2) {
            if (format.depth() == 8) {
                return AL10.AL_FORMAT_STEREO8;
            } else if (format.depth() == 16) {
                return AL10.AL_FORMAT_STEREO16;
            } else {
                throw new JmeException("Illegal sample size");
            }
        } else {
            throw new JmeException("Only mono or stereo is supported");
        }
    }
    
    private static float getPlayTime(byte[] data, AudioFormat format, int rate) {
//      get channels
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

}
