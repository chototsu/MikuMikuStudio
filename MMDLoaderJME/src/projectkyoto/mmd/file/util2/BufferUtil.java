/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectkyoto.mmd.file.util2;

import com.jme3.util.BufferUtils;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;

/**
 *
 * @author kobayasi
 */
public class BufferUtil {
    public static File tmpDir = null;
    public static boolean useTempFile = false;
    private static final Logger logger = Logger.getLogger(BufferUtil.class.getName());
    public static ByteBuffer createByteBuffer(int size) {
        if (useTempFile && tmpDir != null) {
            return createByteBufferFile(size);
        } else {
            return createByteBufferHeap(size);
        }
    }
    private static ByteBuffer createByteBufferHeap(int size) {
        ByteBuffer bb = ByteBuffer.allocateDirect(size);
        bb.order(ByteOrder.nativeOrder());
        return bb;
    }
    private static ByteBuffer createByteBufferFile(int size) {
        try {
            if (tmpDir != null && logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "tmpDir = {0}", tmpDir.getAbsoluteFile());
            }
            File tmpFile = File.createTempFile("pmd","tmp", tmpDir);
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "tmpFile = {0}", tmpFile.getAbsoluteFile());
            }
            RandomAccessFile os = new RandomAccessFile(tmpFile, "rw");
            os.seek(size);
            os.write(0);
            FileChannel ch = os.getChannel();
            MappedByteBuffer  bb = ch.map(MapMode.READ_WRITE, 0, size);
            os.close();
            ch.close();
            tmpFile.delete();
            bb.order(ByteOrder.nativeOrder());
            return bb;
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    private static ThreadLocal<byte[]> threadLocalBuf = new ThreadLocal<byte[]>();
    private static byte[] getBuf(int size) {
        byte[] buf = threadLocalBuf.get();
        if (buf == null) {
            buf = new byte[size];
        } else if (buf.length < size) {
            buf = new byte[size];
        }
        threadLocalBuf.set(buf);
        return buf;
        
    }
    public static void writeString(ByteBuffer bb, String s, int size) {
        try {
            byte[] buf = getBuf(size);
            byte[] buf2 = s.getBytes("Shift_JIS");
            int i = 0;
            for(;i<size;i++) {
                if (buf2.length >= i) {
                    buf[i] = 0;
                } else {
                    buf[i] = buf2[i];
                }
            }
            bb.put(buf, 0, size);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
       }
    }
    public static String readString(ByteBuffer bb, int size) {
        try {
            byte[] buf = getBuf(size);
            bb.get(buf, 0, size);
            for(int i=0;i<size;i++) {
                if (buf[i] == 0) {
                    return new String(buf,0,i,"Shift_JIS");
                }
            }
            return new String(buf,"Shift_JIS");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
    public static Point3f readPoint3f(ByteBuffer bb, Point3f p) {
        p.x = bb.getFloat();
        p.y = bb.getFloat();
        p.z = bb.getFloat();
        return p;
    }
    public static void writePoint3f(ByteBuffer bb, Point3f p) {
        bb.putFloat(p.x);
        bb.putFloat(p.y);
        bb.putFloat(p.z);
    }
    public static Quat4f readQuat4f(ByteBuffer bb, Quat4f q) {
        q.x = bb.getFloat();
        q.y = bb.getFloat();
        q.z = bb.getFloat();
        q.w = bb.getFloat();
        return q;
    }
    public static void writeQuat4f(ByteBuffer bb, Quat4f q) {
        bb.putFloat(q.x);
        bb.putFloat(q.y);
        bb.putFloat(q.z);
        bb.putFloat(q.w);
    }
    public static void write(ByteBuffer bb, DataOutputStream os, byte[] buf) throws IOException {
        bb.position(0);
        final int capacity = bb.capacity();
        os.writeInt(capacity);
        while(bb.position() < capacity) {
            int size = capacity - bb.position();
            if (size > buf.length) {
                size = buf.length;
            }
            bb.get(buf, 0, size);
            os.write(buf, 0, size);
        }
    }
    public static ByteBuffer read(DataInputStream is, byte[]buf) throws IOException {
        final int capacity = is.readInt();
        ByteBuffer bb = BufferUtils.createByteBuffer(capacity);
        while(bb.position() < capacity) {
            int size = capacity - bb.position();
            if (size > buf.length) {
                size = buf.length;
            }
            int i = is.read(buf, 0, size);
            bb.put(buf, 0, i);
        }
        bb.position(0);
        return bb;
    }

}
