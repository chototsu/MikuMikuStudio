/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme.util;

import java.nio.FloatBuffer;

import com.jme.math.Matrix3f;
import com.jme.math.Matrix4f;

/**
 * An utily class to store shader's uniform variables content. Used by the
 * <code>ShaderObjectsState</code> class.
 * 
 * @author Thomas Hourdel
 * @see com.jme.scene.state.ShaderObjectsState
 */
public class ShaderUniform {

    public final static int SU_INT = 0;

    public final static int SU_INT2 = 1;

    public final static int SU_INT3 = 2;

    public final static int SU_INT4 = 3;

    public final static int SU_FLOAT = 4;

    public final static int SU_FLOAT2 = 5;

    public final static int SU_FLOAT3 = 6;

    public final static int SU_FLOAT4 = 7;

    public final static int SU_MATRIX2 = 8;

    public final static int SU_MATRIX3 = 9;

    public final static int SU_MATRIX4 = 10;

    /** Name of the uniform variable. * */
    public String name;

    /** Type of uniform value. * */
    public int type;

    /** ID of uniform. * */
    public int uniformID = -1;

    /** For int content. * */
    public int vint[];

    /** For float content. * */
    public float vfloat[];

    /** Matrix2f storage. * */
    public float matrix2f[];

    /** Matrix3f storage. * */
    public Matrix3f matrix3f;

    /** Matrix4f storage. * */
    public Matrix4f matrix4f;

    /** Used to transpose the matrix if wanted. * */
    public boolean transpose;
    
    public FloatBuffer matrixBuffer = null;

    /**
     * Create a new uniform object.
     * 
     * @param name
     *            uniform's name
     * @param type
     *            uniform's value type
     */
    public ShaderUniform(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ShaderUniform) {
            ShaderUniform temp = (ShaderUniform)obj;
            if (name.equals(temp.name)) return true;
        }
        return false;
    }
}