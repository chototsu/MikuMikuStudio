/*
 * Created on 23 avr. 2005
 */
package com.jme.sound.openAL.objects.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author Arman
 */
public abstract class JMEAudioInputStream extends FilterInputStream {
    
    public static final String INVALID_OGG_MESSAGE="Input does not appear to be an Ogg bitstream.";
       
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
