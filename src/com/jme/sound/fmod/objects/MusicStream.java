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
 * Created on Apr 19, 2005
 */
package com.jme.sound.fmod.objects;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.fmod3.FSound;
import org.lwjgl.fmod3.FSoundStream;
import org.lwjgl.fmod3.callbacks.FSoundStreamCallback;


public class MusicStream {
    
    private ByteBuffer memoryData;
    private FSoundStream stream;
    private int playingChannel=-2;
    
    public MusicStream(String file, boolean memoryLoad){
        if(memoryLoad){
            memoryData=getData(file);
            stream=FSound.FSOUND_Stream_Open(memoryData, FSound.FSOUND_LOADMEMORY, 0, memoryData.capacity());
        }else{
            stream = FSound.FSOUND_Stream_Open(file, FSound.FSOUND_NORMAL | FSound.FSOUND_MPEGACCURATE, 0, 0);
        }
        
    }

    
    public void play(){
        playingChannel=FSound.FSOUND_Stream_Play(FSound.FSOUND_FREE, stream);
        FSound.FSOUND_Stream_SetEndCallback(stream, new EndCallback());        
    }
    
    /**
     * Pause the stream
     * @return true if the stream is paused
     */
    public boolean pause(){
        FSound.FSOUND_SetPaused(playingChannel, !FSound.FSOUND_GetPaused(playingChannel));
        return FSound.FSOUND_GetPaused(playingChannel);
    }
    
    
    public void stop(){
        FSound.FSOUND_Stream_Stop(stream);
    }
    
    public void close(){
        FSound.FSOUND_Stream_Close(stream);        
    }
    
    /**
     * Reads the file into a ByteBuffer
     * @param filename Name of file to load
     * @return ByteBuffer containing file data
     */
    static protected ByteBuffer getData(String filename) {
        ByteBuffer buffer = null;
        try {
            BufferedInputStream bis = new BufferedInputStream(MusicStream.class.getClassLoader().getResourceAsStream(filename));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int bufferLength = 4096;
            byte[] readBuffer = new byte[bufferLength];
            int read = -1;
            while ((read = bis.read(readBuffer, 0, bufferLength)) != -1) {
                baos.write(readBuffer, 0, read);
            }
            //done reading, close
            bis.close();
            // place it in a buffer
            buffer = ByteBuffer.allocateDirect(baos.size());
            buffer.order(ByteOrder.nativeOrder());
            buffer.put(baos.toByteArray());
            buffer.flip();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return buffer;
    }


    private class EndCallback implements FSoundStreamCallback{
        public void FSOUND_STREAMCALLBACK(FSoundStream arg0, ByteBuffer arg1, int arg2) {
           close();
        }        
    }

}
