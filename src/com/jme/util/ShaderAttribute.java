/*
 * Copyright (c) 2003-2006 jMonkeyEngine
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

package com.jme.util;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * An utily class to store shader's attribute variables content. Used by the
 * <code>ShaderObjectsState</code> class.
 * 
 * @author Joshua Slack
 * @see com.jme.scene.state.ShaderObjectsState
 */
public class ShaderAttribute implements Savable {

    public final static int SU_SHORT = 0;

    public final static int SU_SHORT2 = 1;

    public final static int SU_SHORT3 = 2;

    public final static int SU_SHORT4 = 3;

    public final static int SU_FLOAT = 4;

    public final static int SU_FLOAT2 = 5;

    public final static int SU_FLOAT3 = 6;

    public final static int SU_FLOAT4 = 7;

    public final static int SU_NORMALIZED_UBYTE4 = 8;

    public final static int SU_POINTER_BYTE = 9;

    public final static int SU_POINTER_FLOAT = 10;

    public final static int SU_POINTER_SHORT = 11;

    public final static int SU_POINTER_INT = 12;
    
    public final static int SB_FLOAT = 13;
    
    public final static int SB_DOUBLE = 14;
    
    public final static int SB_INT = 15;
    
    public final static int SB_SHORT = 16;
    
    public final static int SB_BYTE = 17;

    
    /** Name of the attribute variable. */
    public String name;

    /** Type of attribute value. */
    public int type;

    /** ID of attribute.  */
    public int attributeID = -1;

    /** For short content. */
    public short s1, s2, s3, s4;

    /** For float content. */
    public float f1, f2, f3, f4;

    /** For byte content. */
    public byte b1, b2, b3, b4;

    /** For nio content. */
    public Buffer data;
    public int bufferType;
    
    public boolean normalized, unsigned;
    
    public int stride, size;
    
    /**
     * Create a new attribute object.
     * 
     * @param name
     *            attribute's name
     * @param type
     *            attribute's value type
     */
    public ShaderAttribute(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ShaderAttribute) {
            ShaderAttribute temp = (ShaderAttribute)obj;
            if (name.equals(temp.name)) return true;
        }
        return false;
    }

    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        
        capsule.write(name, "name", "");
        capsule.write(type, "type", SU_SHORT);
        capsule.write(attributeID, "attributeID", -1);
        capsule.write(s1, "s1", 0);
        capsule.write(s2, "s2", 0);
        capsule.write(s3, "s3", 0);
        capsule.write(s4, "s4", 0);
        capsule.write(f1, "f1", 0);
        capsule.write(f2, "f2", 0);
        capsule.write(f3, "f3", 0);
        capsule.write(f4, "f4", 0);
        capsule.write(b1, "b1", 0);
        capsule.write(b2, "b2", 0);
        capsule.write(b3, "b3", 0);
        capsule.write(b4, "b4", 0);
        capsule.write(bufferType, "bufferType", 0);
        if(bufferType == SB_FLOAT) {
            capsule.write((FloatBuffer)data, "data", null);
        } else if(bufferType == SB_BYTE) {
            capsule.write((ByteBuffer)data, "data", null);
        } else if(bufferType == SB_INT) {
            capsule.write((IntBuffer)data, "data", null);
        } else if(bufferType == SB_SHORT) {
            capsule.write((ShortBuffer)data, "data", null);
        }
        capsule.write(normalized, "normalized", false);
        capsule.write(unsigned, "boolean", false);
        capsule.write(stride, "stride", 0);
        capsule.write(size, "size", 0);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        
        name = capsule.readString("name", "");
        type = capsule.readInt("type", SU_SHORT);
        attributeID = capsule.readInt("attributeID", -1);
        s1 = capsule.readShort("s1", (short)0);
        s2 = capsule.readShort("s2", (short)0);
        s3 = capsule.readShort("s3", (short)0);
        s4 = capsule.readShort("s4", (short)0);
        f1 = capsule.readFloat("f1", 0);
        f2 = capsule.readFloat("f2", 0);
        f3 = capsule.readFloat("f3", 0);
        f4 = capsule.readFloat("f4", 0);
        b1 = capsule.readByte("b1", (byte)0);
        b2 = capsule.readByte("b2", (byte)0);
        b3 = capsule.readByte("b3", (byte)0);
        b4 = capsule.readByte("b4", (byte)0);
        bufferType = capsule.readInt("bufferType", 0);
        if(bufferType == SB_FLOAT) {
            data = capsule.readFloatBuffer("data", null);
        } else if(bufferType == SB_BYTE) {
            data = capsule.readByteBuffer("data", null);
        } else if(bufferType == SB_INT) {
            data = capsule.readIntBuffer("data", null);
        } else if(bufferType == SB_SHORT) {
            data = capsule.readShortBuffer("data", null);
        }
        normalized = capsule.readBoolean("normalized", false);
        unsigned = capsule.readBoolean("unsigned", false);
        stride = capsule.readInt("stride", 0);
        size = capsule.readInt("size", 0);
    }
    
    public Class getClassTag() {
        return this.getClass();
    }
}