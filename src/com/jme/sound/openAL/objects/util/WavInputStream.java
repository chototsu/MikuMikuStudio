/*
 * Created on 23 avr. 2005
 */
package com.jme.sound.openAL.objects.util;

/*
WAVInputStream - reads a WAV file.


Issues: 
Available method gives raw bytes of file, not actual bytes of audio.
default reads, plus skip just pass through - use readSample
*/
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;

import org.lwjgl.openal.AL10;

import com.jme.sound.openAL.objects.util.dsp.Filter;
import com.jme.system.JmeException;

public class WavInputStream extends JMEAudioInputStream{
    private static final int RIFFid = ('R' << 24) | ('I' << 16) + ('F' << 8) + 'F';
    private static final int WAVEid = ('W' << 24) | ('A' << 16) + ('V' << 8) + 'E';
    private static final int fmtid = ('f' << 24) | ('m' << 16) + ('t' << 8) + ' ';
    private static final int dataid = ('d' << 24) | ('a' << 16) + ('t' << 8) + 'a';

    private int numChannels;
    private int sampleDepth;
    private int sampleRate;
    private int numSamples;

    //for intel byte order. YAY!
    private short readShort() throws IOException
    {
        int a = in.read();
        int b = in.read();
        if(b == -1) throw(new EOFException());
        return (short)(a | (b << 8));
    }

    private int readInt() throws IOException
    {
        int a = in.read();
        int b = in.read();
        int c = in.read();
        int d = in.read();
        if(d == -1) throw(new EOFException());
        return a | (b << 8) | (c << 16) | (d << 24);
    }

    /**
     * Reads up to len bytes of data from the input stream into a ByteBuffer.
     * @param b the buffer into which the data is read.
     * @param off the start offset of the data.
     * @param len the maximum number of bytes read.
     * @return the total number of bytes read into the buffer, or -1 if there is
     *         no more data because the end of the stream has been reached. 
     */
    public int read(ByteBuffer b, int off, int len) throws IOException {
        byte[] buffer=new byte[b.capacity()];
        int bytesRead=read(buffer, off, len);
        if(bytesRead>0 && filters.size()>0){
            Iterator it=filters.iterator();
            while(it.hasNext()){
                buffer=((Filter)it.next()).filter(buffer);
            }
        }
        b.put(buffer);
        b.position(off);
        return bytesRead;
    }
    
    
    //this allows us to read binary data from the stream
    private DataInputStream DataIn;

    public int channels()
    {
        return numChannels;
    }

    public int depth()
    {
        return sampleDepth;
    }

    public int rate()
    {
        return sampleRate;
    }

    public int length()
    {
        return numSamples;
    }

    public WavInputStream(InputStream _in) throws IOException
    {
        super(_in);
        DataIn = new DataInputStream(in);

        //read "RIFF"
        if(DataIn.readInt() != RIFFid)
            throw(new IOException("Not a valid RIFF file"));
        //read chunk size
        DataIn.readInt();
        //read "WAVE"
        if(DataIn.readInt() != WAVEid)
            throw(new IOException("Not a valid WAVE file"));
        //find the beg of the next audio chunk. This'll get the chunk with format info.
        SeekAudio();
    }

    public boolean marksupported()
    {
        return false;
    }

    public void readSample(byte b[], int start, int length) throws IOException
    {
        in.read(b, start * numChannels, length * numChannels);
    }

    public void readSample(short b[], int start, int length) throws IOException
    {
        for(int off = 0; off < length; off++)
            for(int channel = 0; channel < numChannels; channel++)
                b[channel + (start + off) * numChannels] = readShort();
    }

    public void readSample(int b[], int start, int length) throws IOException
    {
        for(int off = 0; off < length; off++)
            for(int channel = 0; channel < numChannels; channel++)
                b[b[channel + (start + off) * numChannels]] = readInt();
    }

    private void Readfmt(int size) throws IOException
    {
        //PCM format thingy
        if(readShort() != 1)
            throw(new IOException("Can only read PCM files"));
        numChannels = readShort();
        sampleRate = readInt();
        //bytes/sec, block alignment
        DataIn.readInt();
        DataIn.readShort();
        sampleDepth = readShort();
        if(sampleDepth != 8 && sampleDepth != 16 && sampleDepth != 32)
            throw(new IOException("Only 8, 16, or 32 bit samples are handled"));
        int read = 16;
        while(size > read)
        {
            read();
            read++;
        }
    }

    private void SeekAudio() throws IOException
    {
        while(true) //Since we're using datain, we will get an exception at eof., so this won't go forever.
        {
            int ChunkType = DataIn.readInt();
            int ckSize = readInt();
            switch(ChunkType)
            {
            case fmtid:
                Readfmt(ckSize);
                break;
            case dataid:
                numSamples = ckSize * 8 / numChannels / sampleDepth;
                return;
            default:
                if(in.skip(ckSize) != ckSize)
                    throw(new IOException("Input didn't fully skip chunk"));
            }
        }
    }
    
    /**
     * @return
     */
    public int getChannels() {
        //      get channels
        if (channels() == 1) {
            if (depth() == 8) {
                return AL10.AL_FORMAT_MONO8;
            } else if (depth() == 16) {
                return AL10.AL_FORMAT_MONO16;
            } else {
                throw new JmeException("Illegal sample size");
            }
        } else if (channels() == 2) {
            if (depth() == 8) {
                return AL10.AL_FORMAT_STEREO8;
            } else if (depth() == 16) {
                return AL10.AL_FORMAT_STEREO16;
            } else {
                throw new JmeException("Illegal sample size");
            }
        } else {
            throw new JmeException("Only mono or stereo is supported");
        }
    }
    
    public int getAudioChannels(){
        return channels();
    }
    
    private float length;
    
    protected void setLength(float time){
        this.length=time;
    }
    
    public float getLength(){
        return length;
    }
}

