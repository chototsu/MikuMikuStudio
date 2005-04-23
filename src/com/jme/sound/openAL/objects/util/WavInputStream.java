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
import java.io.*;

public class WavInputStream extends java.io.FilterInputStream
{
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
}

