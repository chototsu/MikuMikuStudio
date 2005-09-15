/*
 * Copyright (c) 2003-2005 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * Created on 23 avr. 2005
 */
package com.jmex.sound.openAL.objects.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.jmex.sound.openAL.objects.util.dsp.Filter;

/**
 * @author Arman
 */
public abstract class JMEAudioInputStream extends FilterInputStream {
    
    public static final String INVALID_OGG_MESSAGE="Input does not appear to be an Ogg bitstream.";
    protected ArrayList filters=new ArrayList();  
    
    
    public JMEAudioInputStream(InputStream in) {
        super(in);
    }
    
    /**
     * Reads up to len bytes of data from the input stream into a ByteBuffer.
     * @param b the buffer into which the data is read.
     * @param off the start offset of the data.
     * @param len the maximum number of bytes read.
     * @return the total number of bytes read into the buffer, or -1 if there is
     *         no more data because the end of the stream has been reached. 
     */
    public abstract int read(ByteBuffer buffer, int offset, int length) throws IOException;

    /**
     * Adds a DSP filter on this stream
     * @param f the filter to apply on the stream
     * @return
     */
    public int addFilter(Filter f){
        filters.add(f);
        return filters.size()-1;
    }
    
    public abstract int getChannels();
    
    public abstract int getAudioChannels();
    
    public abstract int rate();
    
    protected abstract void setLength(float time);
    
    protected abstract float getLength();
    
    private String currentFile;
    public void setFileName(String file){
        currentFile=file;
    }
    
    public String getFileName(){
        return currentFile;
    }
    
    
    
}
