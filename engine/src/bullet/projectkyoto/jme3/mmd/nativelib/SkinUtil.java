/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectkyoto.jme3.mmd.nativelib;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 *
 * @author kobayasi
 */
public class SkinUtil {
    public static native void copy(Buffer src, Buffer dist, int size);
    public static native void setSkin(FloatBuffer buf, ShortBuffer indexBuf, FloatBuffer skinBuf, float weight);
    public static native void copyBoneMatrix(FloatBuffer src, FloatBuffer dist, ShortBuffer indexBuffer);
    public static native void clear(Buffer buf);
}
