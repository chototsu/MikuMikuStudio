package com.jme.util;

import java.io.*;

/**
 * <code>LittleEndien</code> is a class to read littleendien stored data
 * via a InputStream.  All functions work as defined in DataInput, but
 * assume they come from a LittleEndien input stream.  Currently used to read .ms3d and .3ds files.
 * @author Jack Lindamood
 */
public class LittleEndien implements DataInput{

    private BufferedInputStream in;
    private BufferedReader inRead;

    /**
     * Creates a new LittleEndien reader from the given input stream.  The
     * stream is wrapped in a BufferedReader automatically.
     * @param in The input stream to read from.
     */
    public LittleEndien(InputStream in){
        this.in = new BufferedInputStream(in);
        inRead=new BufferedReader(new InputStreamReader(in));
    }

    public final int readUnsignedShort() throws IOException{
        return (int)(in.read()&0xff) | ((in.read()&0xff) << 8);
    }

    public final boolean readBoolean() throws IOException{
        return (in.read()!=0);
    }

    public final byte readByte() throws IOException{
        return (byte) in.read();
    }

    public final int readUnsignedByte() throws IOException{
        return in.read();
    }

    public final short readShort() throws IOException{
        return (short) this.readUnsignedShort();
    }

    public final char readChar() throws IOException{
        return (char) this.readUnsignedShort();
    }
    public final int readInt() throws IOException{
        return (int)(
            (in.read()&0xff) |
            ((in.read()&0xff) << 8) |
            ((in.read()&0xff) << 16) |
            ((in.read()&0xff) << 24)
        );
    }

    public final long readLong() throws IOException{
        return (long)(
            (long)(in.read()&0xff) |
            ((long)(in.read()&0xff) << 8) |
            ((long)(in.read()&0xff) << 16) |
            ((long)(in.read()&0xff) << 24) |
            ((long)(in.read()&0xff) << 32) |
            ((long)(in.read()&0xff) << 40) |
            ((long)(in.read()&0xff) << 48) |
            ((long)(in.read()&0xff) << 56)
        );
    }

    public final float readFloat() throws IOException{
        return Float.intBitsToFloat(readInt());
    }

    public final double readDouble() throws IOException{
        return Double.longBitsToDouble(readLong());
    }

    public final void readFully(byte b[]) throws IOException{
        in.read(b, 0, b.length);
    }

    public final void readFully(byte b[], int off, int len) throws IOException{
        in.read(b, off, len);
    }

    public final int skipBytes(int n) throws IOException{
        return (int) in.skip(n);
    }

    public final String readLine() throws IOException{
        return inRead.readLine();
    }

    public final String readUTF() throws IOException{
        throw new IOException("Unsupported operation");
    }

    public final  void close() throws IOException{
        in.close();
    }
}