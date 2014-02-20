/*
 * Copyright (c) 2010-2014, Kazuhiko Kobayashi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package projectkyoto.mmd.file;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author kobayasi
 */
public class DataOutputStreamLittleEndian extends FilterOutputStream implements DataOutput{
    DataOutputStream dos;
    public DataOutputStreamLittleEndian(OutputStream out) {
        super(out);
        dos = new DataOutputStream(out);
    }

    @Override
    public void writeBoolean(boolean bln) throws IOException {
        dos.writeBoolean(bln);
    }

    @Override
    public void writeByte(int i) throws IOException {
        dos.writeByte(i);
    }

    @Override
    public void writeShort(int i) throws IOException {
        dos.writeShort(Short.reverseBytes((short)i));
    }

    @Override
    public void writeChar(int i) throws IOException {
        dos.writeChar(i);
    }

    @Override
    public void writeInt(int i) throws IOException {
        dos.writeInt(Integer.reverseBytes(i));
    }

    @Override
    public void writeLong(long l) throws IOException {
        dos.writeLong(Long.reverseBytes(l));
    }

    @Override
    public void writeFloat(float f) throws IOException {
        writeInt(Float.floatToIntBits(f));
    }

    @Override
    public void writeDouble(double d) throws IOException {
        writeLong(Double.doubleToLongBits(d));
    }

    @Override
    public void writeBytes(String string) throws IOException {
        dos.writeBytes(string);
    }

    @Override
    public void writeChars(String string) throws IOException {
        dos.writeChars(string);
    }

    @Override
    public void writeUTF(String string) throws IOException {
        dos.writeUTF(string);
    }

    @Override
    public void flush() throws IOException {
        dos.flush();
    }

    @Override
    public void close() throws IOException {
        dos.close();
    }
}
