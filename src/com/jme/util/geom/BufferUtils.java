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

package com.jme.util.geom;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

/**
 * <code>BufferUtils</code> is a helper class for generating nio buffers from
 * jME data classes such as Vectors and ColorRGBA.
 * 
 * @author Joshua Slack
 * @version $Id: BufferUtils.java,v 1.1 2005-09-15 17:14:54 renanse Exp $
 */
public final class BufferUtils {

    ////  -- TEMP DATA OBJECTS --  ////
    private static final Vector2f _tempVec2 = new Vector2f();
    private static final Vector3f _tempVec3 = new Vector3f();

    ////  -- COLORRGBA METHODS -- ////
    
    public static FloatBuffer createFloatBuffer(ColorRGBA[] data) {
        if (data == null) return null;
        FloatBuffer buff = createFloatBuffer(4 * data.length);
        buff.clear();
        for (int x = 0; x < data.length; x++) {
            if (data[x] != null)
                buff.put(data[x].r).put(data[x].g).put(data[x].b).put(data[x].a);
            else
                buff.put(0).put(0).put(0).put(0);
        }
        buff.flip();
        return buff;
    }

    public static FloatBuffer createColorBuffer(int colors) {
        FloatBuffer colorBuff = createFloatBuffer(4 * colors);
        colorBuff.clear();
        return colorBuff;
    }

    public static void setInBuffer(ColorRGBA color, FloatBuffer buf,
            int index) {
        buf.rewind();
        buf.put(index * 4, color.r);
        buf.put((index * 4) + 1, color.g);
        buf.put((index * 4) + 2, color.b);
        buf.put((index * 4) + 3, color.a);
    }

    public static ColorRGBA[] getColorArray(FloatBuffer buff) {
        buff.clear();
        ColorRGBA[] colors = new ColorRGBA[buff.capacity() << 2];
        for (int x = 0; x < colors.length; x++) {
            ColorRGBA c = new ColorRGBA(buff.get(), buff.get(), buff.get(),
                    buff.get());
            colors[x] = c;
        }
        return colors;
    }

    public static void copyInternalColor(FloatBuffer buf, int fromPos, int toPos) {
        copyInternal(buf, fromPos*4, toPos*4, 4);
    }

    
    ////  -- VECTOR3F METHODS -- ////
    
    public static FloatBuffer createFloatBuffer(Vector3f[] data) {
        if (data == null) return null;
        FloatBuffer buff = createFloatBuffer(3 * data.length);
        buff.clear();
        for (int x = 0; x < data.length; x++) {
            if (data[x] != null)
                buff.put(data[x].x).put(data[x].y).put(data[x].z);
            else
                buff.put(0).put(0).put(0);
        }
        buff.flip();
        return buff;
    }

    public static FloatBuffer createVector3Buffer(int vertices) {
        FloatBuffer vBuff = createFloatBuffer(3 * vertices);
        vBuff.clear();
        return vBuff;
    }

    public static void setInBuffer(Vector3f vector, FloatBuffer buf, int index) {
        buf.put(index * 3, vector.x);
        buf.put((index * 3) + 1, vector.y);
        buf.put((index * 3) + 2, vector.z);
    }
    
    public static void populateFromBuffer(Vector3f vector, FloatBuffer buf, int index) {
        vector.x = buf.get(index*3);
        vector.y = buf.get(index*3+1);
        vector.z = buf.get(index*3+2);        
    }

    public static Vector3f[] getVector3Array(FloatBuffer buff) {
        buff.clear();
        Vector3f[] verts = new Vector3f[buff.capacity() / 3];
        for (int x = 0; x < verts.length; x++) {
            Vector3f v = new Vector3f(buff.get(), buff.get(), buff.get());
            verts[x] = v;
        }
        return verts;
    }

    public static void copyInternalVector3(FloatBuffer buf, int fromPos, int toPos) {
        copyInternal(buf, fromPos*3, toPos*3, 3);
    }

    public static void normalizeVector3(FloatBuffer buf, int index) {
        populateFromBuffer(_tempVec3, buf, index);
        _tempVec3.normalizeLocal();
        setInBuffer(_tempVec3, buf, index);
    }

    public static void addInBuffer(Vector3f toAdd, FloatBuffer buf, int index) {
        populateFromBuffer(_tempVec3, buf, index);
        _tempVec3.addLocal(toAdd);
        setInBuffer(_tempVec3, buf, index);
    }

    public static void multInBuffer(Vector3f toMult, FloatBuffer buf, int index) {
        populateFromBuffer(_tempVec3, buf, index);
        _tempVec3.multLocal(toMult);
        setInBuffer(_tempVec3, buf, index);
    }


    ////  -- VECTOR2F METHODS -- ////
    
    public static FloatBuffer createFloatBuffer(Vector2f[] data) {
        if (data == null) return null;
        FloatBuffer buff = createFloatBuffer(2 * data.length);
        buff.clear();
        for (int x = 0; x < data.length; x++) {
            if (data[x] != null)
                buff.put(data[x].x).put(data[x].y);
            else
                buff.put(0).put(0);
        }
        buff.flip();
        return buff;
    }

    public static FloatBuffer createVector2Buffer(int vertices) {
        FloatBuffer vBuff = createFloatBuffer(2 * vertices);
        vBuff.clear();
        return vBuff;
    }

    public static void setInBuffer(Vector2f vector, FloatBuffer buf, int index) {
        buf.rewind();
        buf.put(index * 2, vector.x);
        buf.put((index * 2) + 1, vector.y);
    }
    
    public static void populateFromBuffer(Vector2f vector, FloatBuffer buf, int index) {
        vector.x = buf.get(index*2);
        vector.y = buf.get(index*2+1);
    }

    public static Vector2f[] getVector2Array(FloatBuffer buff) {
        buff.clear();
        Vector2f[] verts = new Vector2f[buff.capacity() / 2];
        for (int x = 0; x < verts.length; x++) {
            Vector2f v = new Vector2f(buff.get(), buff.get());
            verts[x] = v;
        }
        return verts;
    }

    public static void copyInternalVector2(FloatBuffer buf, int fromPos, int toPos) {
        copyInternal(buf, fromPos*2, toPos*2, 2);
    }

    public static void normalizeVector2(FloatBuffer buf, int index) {
        populateFromBuffer(_tempVec2, buf, index);
        _tempVec2.normalizeLocal();
        setInBuffer(_tempVec2, buf, index);
    }

    public static void addInBuffer(Vector2f toAdd, FloatBuffer buf, int index) {
        populateFromBuffer(_tempVec2, buf, index);
        _tempVec2.addLocal(toAdd);
        setInBuffer(_tempVec2, buf, index);
    }

    public static void multInBuffer(Vector2f toMult, FloatBuffer buf, int index) {
        populateFromBuffer(_tempVec2, buf, index);
        _tempVec2.multLocal(toMult);
        setInBuffer(_tempVec2, buf, index);
    }


    ////  -- INT METHODS -- ////
    
    public static IntBuffer createIntBuffer(int[] data) {
        if (data == null) return null;
        IntBuffer buff = createIntBuffer(data.length);
        buff.clear();
        for (int x = 0; x < data.length; x++)
            buff.put(data[x]);
        buff.flip();
        return buff;
    }

    public static int[] getIntArray(IntBuffer buff) {
        buff.clear();
        int[] inds = new int[buff.capacity()];
        for (int x = 0; x < inds.length; x++) {
            inds[x] = buff.get();
        }
        return inds;
    }

    
    //// -- GENERAL FLOAT ROUTINES -- ////
    
    public static FloatBuffer createFloatBuffer(int size) {
        return ByteBuffer.allocateDirect(4 * size).order(
                ByteOrder.nativeOrder()).asFloatBuffer();
    }

    public static void copyInternal(FloatBuffer buf, int fromPos, int toPos, int length) {
        float[] data = new float[length];
        buf.position(fromPos);
        buf.get(data);
        buf.position(toPos);
        buf.put(data);
    }

    public static FloatBuffer clone(FloatBuffer buf) {
        if (buf == null) return null;
        buf.rewind();
        
        FloatBuffer copy = createFloatBuffer(buf.capacity());
        copy.clear();
        copy.put(buf);
        
        return copy;
    }

    
    //// -- GENERAL INT ROUTINES -- ////
    
    public static IntBuffer createIntBuffer(int size) {
        IntBuffer buf = ByteBuffer.allocateDirect(4 * size).order(ByteOrder.nativeOrder()).asIntBuffer();
        buf.clear();
        return buf;
    }

    public static IntBuffer clone(IntBuffer buf) {
        if (buf == null) return null;
        buf.rewind();
        
        IntBuffer copy = createIntBuffer(buf.capacity());
        copy.clear();
        copy.put(buf);
        
        return copy;
    }

}
