/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
