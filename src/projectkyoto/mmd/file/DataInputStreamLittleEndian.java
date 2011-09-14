/*
 * Copyright (c) 2011 Kazuhiko Kobayashi
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
 * * Neither the name of 'MMDLoaderJME' nor the names of its contributors
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
package projectkyoto.mmd.file;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 *
 * @author Kazuhiko Kobayashi
 */
public class DataInputStreamLittleEndian extends FilterInputStream {
//    public URL url;
    private DataInputStream dis;
    byte[] buf;

    public DataInputStreamLittleEndian(InputStream in) {
        super(in);
        dis = new DataInputStream(this);
    }
    public DataInputStreamLittleEndian(URL url) throws IOException {
        super(new BufferedInputStream(url.openStream()));
//        this.url = url;
        dis = new DataInputStream(this);
    }
    final byte[] getBuf(int size) {
        if (buf == null || buf.length < size) {
            buf = new byte[size];
        }
        return buf;
    }
    public final int readInt() throws IOException {
        return Integer.reverseBytes(dis.readInt());
    }

    public final short readShort() throws IOException {
        return Short.reverseBytes(dis.readShort());
    }
    public final int readUnsignedShort() throws IOException {
        short shortValue = readShort();
        int intValue = shortValue;
        intValue = intValue & 0xffff;
        return intValue;
    }
    public final int readUnsignedByte() throws IOException {
        byte byteValue = readByte();
        int intValue = byteValue;
        intValue = intValue & 0xff;
        return intValue;
    }

    public final long readLong() throws IOException {
        return Long.reverseBytes(dis.readLong());
    }

    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }
    public final String readString(int size) throws IOException {
        byte[] buf = getBuf(size);
        read(buf,0,size);
        for(int i=0;i<size;i++) {
            if (buf[i] == 0) {
                return new String(buf,0,i,"Shift_JIS").intern();
            }
        }
        return new String(buf,"Shift_JIS").intern();
    }
    public final byte readByte() throws IOException{
        byte[] buf = getBuf(1);
        read(buf,0,1);
        return buf[0];
    }
}
