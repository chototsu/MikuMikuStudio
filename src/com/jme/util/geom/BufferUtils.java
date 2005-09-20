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
 * @version $Id: BufferUtils.java,v 1.4 2005-09-20 16:46:33 renanse Exp $
 */
public final class BufferUtils {

    ////  -- TEMP DATA OBJECTS --  ////
    private static final Vector2f _tempVec2 = new Vector2f();
    private static final Vector3f _tempVec3 = new Vector3f();

    ////  -- COLORRGBA METHODS -- ////
    
    /**
     * Generate a new FloatBuffer using the given array of ColorRGBA objects.
     * The FloatBuffer will be 4 * data.length long and contain the color data
     * as data[0].r, data[0].g, data[0].b, data[0].a, data[1].r... etc.
     * 
     * @param data
     *            array of ColorRGBA objects to place into a new FloatBuffer
     */
    public static FloatBuffer createFloatBuffer(ColorRGBA[] data) {
        if (data == null) return null;
        FloatBuffer buff = createFloatBuffer(4 * data.length);
        for (int x = 0; x < data.length; x++) {
            if (data[x] != null)
                buff.put(data[x].r).put(data[x].g).put(data[x].b).put(data[x].a);
            else
                buff.put(0).put(0).put(0).put(0);
        }
        buff.flip();
        return buff;
    }

    /**
     * Create a new FloatBuffer of an appropriate size to hold the specified
     * number of ColorRGBA object data.
     * 
     * @param colors
     *            number of colors that need to be held by the newly created
     *            buffer
     * @return the requested new FloatBuffer
     */
    public static FloatBuffer createColorBuffer(int colors) {
        FloatBuffer colorBuff = createFloatBuffer(4 * colors);
        return colorBuff;
    }

    /**
     * Sets the data contained in the given color into the FloatBuffer at the
     * specified index.
     * 
     * @param color
     *            the data to insert
     * @param buf
     *            the buffer to insert into
     * @param index
     *            the postion to place the data; in terms of colors not floats
     */
    public static void setInBuffer(ColorRGBA color, FloatBuffer buf,
            int index) {
        buf.rewind();
        buf.put(index * 4, color.r);
        buf.put((index * 4) + 1, color.g);
        buf.put((index * 4) + 2, color.b);
        buf.put((index * 4) + 3, color.a);
    }

    /**
     * Updates the values of the given color from the specified buffer at the
     * index provided.
     * 
     * @param color
     *            the color to set data on
     * @param buf
     *            the buffer to read from
     * @param index
     *            the position (in terms of colors, not floats) to read from the
     *            buf
     */
    public static void populateFromBuffer(ColorRGBA color, FloatBuffer buf, int index) {
        color.r = buf.get(index*4);
        color.g = buf.get(index*4+1);
        color.b = buf.get(index*4+2);        
        color.a = buf.get(index*4+3);
    }

    /**
     * Generates a ColorRGBA array from the given FloatBuffer.
     * 
     * @param buff
     *            the FloatBuffer to read from
     * @return a newly generated array of ColorRGBA objects
     */
    public static ColorRGBA[] getColorArray(FloatBuffer buff) {
        buff.rewind();
        ColorRGBA[] colors = new ColorRGBA[buff.capacity() << 2];
        for (int x = 0; x < colors.length; x++) {
            ColorRGBA c = new ColorRGBA(buff.get(), buff.get(), buff.get(),
                    buff.get());
            colors[x] = c;
        }
        return colors;
    }

    /**
     * Copies a ColorRGBA from one position in the buffer to another. The index
     * values are in terms of color number (eg, color number 0 is postions 0-3
     * in the FloatBuffer.)
     * 
     * @param buf
     *            the buffer to copy from/to
     * @param fromPos
     *            the index of the color to copy
     * @param toPos
     *            the index to copy the color to
     */
    public static void copyInternalColor(FloatBuffer buf, int fromPos, int toPos) {
        copyInternal(buf, fromPos*4, toPos*4, 4);
    }

    
    ////  -- VECTOR3F METHODS -- ////
    
    /**
     * Generate a new FloatBuffer using the given array of Vector3f objects.
     * The FloatBuffer will be 3 * data.length long and contain the vector data
     * as data[0].x, data[0].y, data[0].z, data[1].x... etc.
     * 
     * @param data array of Vector3f objects to place into a new FloatBuffer
     */
    public static FloatBuffer createFloatBuffer(Vector3f[] data) {
        if (data == null) return null;
        FloatBuffer buff = createFloatBuffer(3 * data.length);
        for (int x = 0; x < data.length; x++) {
            if (data[x] != null)
                buff.put(data[x].x).put(data[x].y).put(data[x].z);
            else
                buff.put(0).put(0).put(0);
        }
        buff.flip();
        return buff;
    }

    /**
     * Create a new FloatBuffer of an appropriate size to hold the specified
     * number of Vector3f object data.
     * 
     * @param vertices
     *            number of vertices that need to be held by the newly created
     *            buffer
     * @return the requested new FloatBuffer
     */
    public static FloatBuffer createVector3Buffer(int vertices) {
        FloatBuffer vBuff = createFloatBuffer(3 * vertices);
        return vBuff;
    }

    /**
     * Create a new FloatBuffer of an appropriate size to hold the specified
     * number of Vector3f object data only if the given buffer if not already
     * the right size.
     * 
     * @param buf
     *            the buffer to first check and rewind
     * @param vertices
     *            number of vertices that need to be held by the newly created
     *            buffer
     * @return the requested new FloatBuffer
     */
    public static FloatBuffer createVector3Buffer(FloatBuffer buf, int vertices) {
        if (buf != null && buf.capacity() == 3 * vertices) {
            buf.rewind();
            return buf;
        } else return createFloatBuffer(3 * vertices);
    }

    /**
     * Sets the data contained in the given Vector3F into the FloatBuffer at the
     * specified index.
     * 
     * @param vector
     *            the data to insert
     * @param buf
     *            the buffer to insert into
     * @param index
     *            the postion to place the data; in terms of vectors not floats
     */
    public static void setInBuffer(Vector3f vector, FloatBuffer buf, int index) {
        buf.put(index * 3, vector.x);
        buf.put((index * 3) + 1, vector.y);
        buf.put((index * 3) + 2, vector.z);
    }
    
    /**
     * Updates the values of the given vector from the specified buffer at the
     * index provided.
     * 
     * @param vector
     *            the vector to set data on
     * @param buf
     *            the buffer to read from
     * @param index
     *            the position (in terms of vectors, not floats) to read from
     *            the buf
     */
    public static void populateFromBuffer(Vector3f vector, FloatBuffer buf, int index) {
        vector.x = buf.get(index*3);
        vector.y = buf.get(index*3+1);
        vector.z = buf.get(index*3+2);        
    }

    /**
     * Generates a Vector3f array from the given FloatBuffer.
     * 
     * @param buff
     *            the FloatBuffer to read from
     * @return a newly generated array of Vector3f objects
     */
    public static Vector3f[] getVector3Array(FloatBuffer buff) {
        buff.clear();
        Vector3f[] verts = new Vector3f[buff.capacity() / 3];
        for (int x = 0; x < verts.length; x++) {
            Vector3f v = new Vector3f(buff.get(), buff.get(), buff.get());
            verts[x] = v;
        }
        return verts;
    }

    /**
     * Copies a Vector3f from one position in the buffer to another. The index
     * values are in terms of vector number (eg, vector number 0 is postions 0-2
     * in the FloatBuffer.)
     * 
     * @param buf
     *            the buffer to copy from/to
     * @param fromPos
     *            the index of the vector to copy
     * @param toPos
     *            the index to copy the vector to
     */
    public static void copyInternalVector3(FloatBuffer buf, int fromPos, int toPos) {
        copyInternal(buf, fromPos*3, toPos*3, 3);
    }

    /**
     * Normalize a Vector3f in-buffer.
     * 
     * @param buf
     *            the buffer to find the Vector3f within
     * @param index
     *            the position (in terms of vectors, not floats) of the vector
     *            to normalize
     */
    public static void normalizeVector3(FloatBuffer buf, int index) {
        populateFromBuffer(_tempVec3, buf, index);
        _tempVec3.normalizeLocal();
        setInBuffer(_tempVec3, buf, index);
    }

    /**
     * Add to a Vector3f in-buffer.
     * 
     * @param toAdd
     *            the vector to add from
     * @param buf
     *            the buffer to find the Vector3f within
     * @param index
     *            the position (in terms of vectors, not floats) of the vector
     *            to add to
     */
    public static void addInBuffer(Vector3f toAdd, FloatBuffer buf, int index) {
        populateFromBuffer(_tempVec3, buf, index);
        _tempVec3.addLocal(toAdd);
        setInBuffer(_tempVec3, buf, index);
    }

    /**
     * Multiply and store a Vector3f in-buffer.
     * 
     * @param toMult
     *            the vector to multiply against
     * @param buf
     *            the buffer to find the Vector3f within
     * @param index
     *            the position (in terms of vectors, not floats) of the vector
     *            to multiply
     */
    public static void multInBuffer(Vector3f toMult, FloatBuffer buf, int index) {
        populateFromBuffer(_tempVec3, buf, index);
        _tempVec3.multLocal(toMult);
        setInBuffer(_tempVec3, buf, index);
    }


    ////  -- VECTOR2F METHODS -- ////
    
    /**
     * Generate a new FloatBuffer using the given array of Vector2f objects.
     * The FloatBuffer will be 2 * data.length long and contain the vector data
     * as data[0].x, data[0].y, data[1].x... etc.
     * 
     * @param data array of Vector2f objects to place into a new FloatBuffer
     */
    public static FloatBuffer createFloatBuffer(Vector2f[] data) {
        if (data == null) return null;
        FloatBuffer buff = createFloatBuffer(2 * data.length);
        for (int x = 0; x < data.length; x++) {
            if (data[x] != null)
                buff.put(data[x].x).put(data[x].y);
            else
                buff.put(0).put(0);
        }
        buff.flip();
        return buff;
    }

    /**
     * Create a new FloatBuffer of an appropriate size to hold the specified
     * number of Vector2f object data.
     * 
     * @param vertices
     *            number of vertices that need to be held by the newly created
     *            buffer
     * @return the requested new FloatBuffer
     */
    public static FloatBuffer createVector2Buffer(int vertices) {
        FloatBuffer vBuff = createFloatBuffer(2 * vertices);
        return vBuff;
    }

    /**
     * Create a new FloatBuffer of an appropriate size to hold the specified
     * number of Vector2f object data only if the given buffer if not already
     * the right size.
     * 
     * @param buf
     *            the buffer to first check and rewind
     * @param vertices
     *            number of vertices that need to be held by the newly created
     *            buffer
     * @return the requested new FloatBuffer
     */
    public static FloatBuffer createVector2Buffer(FloatBuffer buf, int vertices) {
        if (buf != null && buf.capacity() == 2 * vertices) {
            buf.rewind();
            return buf;
        } else return createFloatBuffer(2 * vertices);
    }

    /**
     * Sets the data contained in the given Vector2F into the FloatBuffer at the
     * specified index.
     * 
     * @param vector
     *            the data to insert
     * @param buf
     *            the buffer to insert into
     * @param index
     *            the postion to place the data; in terms of vectors not floats
     */
    public static void setInBuffer(Vector2f vector, FloatBuffer buf, int index) {
        buf.put(index * 2, vector.x);
        buf.put((index * 2) + 1, vector.y);
    }
    
    /**
     * Updates the values of the given vector from the specified buffer at the
     * index provided.
     * 
     * @param vector
     *            the vector to set data on
     * @param buf
     *            the buffer to read from
     * @param index
     *            the position (in terms of vectors, not floats) to read from
     *            the buf
     */
    public static void populateFromBuffer(Vector2f vector, FloatBuffer buf, int index) {
        vector.x = buf.get(index*2);
        vector.y = buf.get(index*2+1);
    }

    /**
     * Generates a Vector2f array from the given FloatBuffer.
     * 
     * @param buff
     *            the FloatBuffer to read from
     * @return a newly generated array of Vector2f objects
     */
    public static Vector2f[] getVector2Array(FloatBuffer buff) {
        buff.clear();
        Vector2f[] verts = new Vector2f[buff.capacity() / 2];
        for (int x = 0; x < verts.length; x++) {
            Vector2f v = new Vector2f(buff.get(), buff.get());
            verts[x] = v;
        }
        return verts;
    }

    /**
     * Copies a Vector2f from one position in the buffer to another. The index
     * values are in terms of vector number (eg, vector number 0 is postions 0-1
     * in the FloatBuffer.)
     * 
     * @param buf
     *            the buffer to copy from/to
     * @param fromPos
     *            the index of the vector to copy
     * @param toPos
     *            the index to copy the vector to
     */
    public static void copyInternalVector2(FloatBuffer buf, int fromPos, int toPos) {
        copyInternal(buf, fromPos*2, toPos*2, 2);
    }

    /**
     * Normalize a Vector2f in-buffer.
     * 
     * @param buf
     *            the buffer to find the Vector2f within
     * @param index
     *            the position (in terms of vectors, not floats) of the vector
     *            to normalize
     */
    public static void normalizeVector2(FloatBuffer buf, int index) {
        populateFromBuffer(_tempVec2, buf, index);
        _tempVec2.normalizeLocal();
        setInBuffer(_tempVec2, buf, index);
    }

    /**
     * Add to a Vector2f in-buffer.
     * 
     * @param toAdd
     *            the vector to add from
     * @param buf
     *            the buffer to find the Vector2f within
     * @param index
     *            the position (in terms of vectors, not floats) of the vector
     *            to add to
     */
    public static void addInBuffer(Vector2f toAdd, FloatBuffer buf, int index) {
        populateFromBuffer(_tempVec2, buf, index);
        _tempVec2.addLocal(toAdd);
        setInBuffer(_tempVec2, buf, index);
    }

    /**
     * Multiply and store a Vector2f in-buffer.
     * 
     * @param toMult
     *            the vector to multiply against
     * @param buf
     *            the buffer to find the Vector2f within
     * @param index
     *            the position (in terms of vectors, not floats) of the vector
     *            to multiply
     */
    public static void multInBuffer(Vector2f toMult, FloatBuffer buf, int index) {
        populateFromBuffer(_tempVec2, buf, index);
        _tempVec2.multLocal(toMult);
        setInBuffer(_tempVec2, buf, index);
    }


    ////  -- INT METHODS -- ////
    
    /**
     * Generate a new IntBuffer using the given array of ints. The IntBuffer
     * will be data.length long and contain the int data as data[0], data[1]...
     * etc.
     * 
     * @param data
     *            array of ints to place into a new IntBuffer
     */
    public static IntBuffer createIntBuffer(int[] data) {
        if (data == null) return null;
        IntBuffer buff = createIntBuffer(data.length);
        buff.clear();
        buff.put(data);
        buff.flip();
        return buff;
    }

    /**
     * Create a new int[] array and populate it with the given IntBuffer's
     * contents.
     * 
     * @param buff
     *            the IntBuffer to read from
     * @return a new int array populated from the IntBuffer
     */
    public static int[] getIntArray(IntBuffer buff) {
        buff.clear();
        int[] inds = new int[buff.capacity()];
        for (int x = 0; x < inds.length; x++) {
            inds[x] = buff.get();
        }
        return inds;
    }

    
    //// -- GENERAL FLOAT ROUTINES -- ////
    
    /**
     * Create a new FloatBuffer of the specified size.
     * 
     * @param size
     *            required number of floats to store.
     * @return the new FloatBuffer
     */
    public static FloatBuffer createFloatBuffer(int size) {
        FloatBuffer buf = ByteBuffer.allocateDirect(4 * size).order(ByteOrder.nativeOrder()).asFloatBuffer();
        buf.clear();
        return buf;
    }

    /**
     * Copies floats from one position in the buffer to another.
     * 
     * @param buf
     *            the buffer to copy from/to
     * @param fromPos
     *            the starting point to copy from
     * @param toPos
     *            the starting point to copy to
     * @param length
     *            the number of floats to copy
     */
    public static void copyInternal(FloatBuffer buf, int fromPos, int toPos, int length) {
        float[] data = new float[length];
        buf.position(fromPos);
        buf.get(data);
        buf.position(toPos);
        buf.put(data);
    }

    /**
     * Creates a new FloatBuffer with the same contents as the given
     * FloatBuffer. The new FloatBuffer is seperate from the old one and changes
     * are not reflected across. If you want to reflect changes, consider using
     * Buffer.duplicate().
     * 
     * @param buf
     *            the FloatBuffer to copy
     * @return the copy
     */
    public static FloatBuffer clone(FloatBuffer buf) {
        if (buf == null) return null;
        buf.rewind();
        
        FloatBuffer copy = createFloatBuffer(buf.capacity());
        copy.put(buf);
        
        return copy;
    }

    
    //// -- GENERAL INT ROUTINES -- ////
    
    /**
     * Create a new IntBuffer of the specified size.
     * 
     * @param size
     *            required number of ints to store.
     * @return the new IntBuffer
     */
    public static IntBuffer createIntBuffer(int size) {
        IntBuffer buf = ByteBuffer.allocateDirect(4 * size).order(ByteOrder.nativeOrder()).asIntBuffer();
        buf.clear();
        return buf;
    }
    
    /**
     * Create a new IntBuffer of an appropriate size to hold the specified
     * number of ints only if the given buffer if not already the right size.
     * 
     * @param buf
     *            the buffer to first check and rewind
     * @param size
     *            number of ints that need to be held by the newly created
     *            buffer
     * @return the requested new IntBuffer
     */
    public static IntBuffer createIntBuffer(IntBuffer buf, int size) {
        if (buf != null && buf.capacity() == size) {
            buf.rewind();
            return buf;
        } else { 
            buf = ByteBuffer.allocateDirect(4 * size).order(ByteOrder.nativeOrder()).asIntBuffer();
            buf.clear();
            return buf;
        }
    }

    /**
     * Creates a new IntBuffer with the same contents as the given IntBuffer.
     * The new IntBuffer is seperate from the old one and changes are not
     * reflected across. If you want to reflect changes, consider using
     * Buffer.duplicate().
     * 
     * @param buf
     *            the IntBuffer to copy
     * @return the copy
     */
    public static IntBuffer clone(IntBuffer buf) {
        if (buf == null) return null;
        buf.rewind();
        
        IntBuffer copy = createIntBuffer(buf.capacity());
        copy.put(buf);
        
        return copy;
    }

}
