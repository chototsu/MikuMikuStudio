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

package com.jme.util;

import java.nio.Buffer;

/**
 * An utily class to store shader's attribute variables content. Used by the
 * <code>ShaderObjectsState</code> class.
 * 
 * @author Joshua Slack
 * @see com.jme.scene.state.ShaderObjectsState
 */
public class ShaderAttribute {

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
}