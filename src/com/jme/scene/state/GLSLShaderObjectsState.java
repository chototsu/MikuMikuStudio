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
package com.jme.scene.state;

import java.net.URL;
import java.util.ArrayList;

import com.jme.math.Matrix3f;
import com.jme.math.Matrix4f;
import com.jme.util.ShaderUniform;

/**
 * Implementation of the GL_ARB_shader_objects extension.
 * 
 * @author Thomas Hourdel
 */
public abstract class GLSLShaderObjectsState extends RenderState {

    public ArrayList uniforms = new ArrayList();

    /**
     * <code>isSupported</code> determines if the ARB_shader_objects extension
     * is supported by current graphics configuration.
     * 
     * @return if ARB shader objects are supported
     */
    public abstract boolean isSupported();

    /**
     * Set an uniform value for this shader object.
     * 
     * @param var
     *            uniform variable to change
     * @param value
     *            the new value
     */

    public void setUniform(String var, int value) {
        ShaderUniform object = getShaderUniform(var, ShaderUniform.SU_INT);
        object.vint = new int[1];
        object.vint[0] = value;
        if (object.uniformID == -1)
            uniforms.add(object);
    }

    /**
     * Set an uniform value for this shader object.
     * 
     * @param var
     *            uniform variable to change
     * @param value
     *            the new value
     */

    public void setUniform(String var, float value) {
        ShaderUniform object = getShaderUniform(var, ShaderUniform.SU_FLOAT);
        object.vfloat = new float[1];
        object.vfloat[0] = value;
        if (object.uniformID == -1)
            uniforms.add(object);
    }

    /**
     * Set an uniform value for this shader object.
     * 
     * @param var
     *            uniform variable to change
     * @param value1
     *            the new value
     * @param value2
     *            the new value
     */

    public void setUniform(String var, int value1, int value2) {
        ShaderUniform object = getShaderUniform(var, ShaderUniform.SU_INT2);
        object.vint = new int[2];
        object.vint[0] = value1;
        object.vint[1] = value2;
        if (object.uniformID == -1)
            uniforms.add(object);
    }

    /**
     * Set an uniform value for this shader object.
     * 
     * @param var
     *            uniform variable to change
     * @param value1
     *            the new value
     * @param value2
     *            the new value
     */

    public void setUniform(String var, float value1, float value2) {
        ShaderUniform object = getShaderUniform(var, ShaderUniform.SU_FLOAT2);
        object.vfloat = new float[2];
        object.vfloat[0] = value1;
        object.vfloat[1] = value2;
        if (object.uniformID == -1)
            uniforms.add(object);
    }

    /**
     * Set an uniform value for this shader object.
     * 
     * @param var
     *            uniform variable to change
     * @param value1
     *            the new value
     * @param value2
     *            the new value
     * @param value3
     *            the new value
     */

    public void setUniform(String var, int value1, int value2, int value3) {
        ShaderUniform object = getShaderUniform(var, ShaderUniform.SU_INT3);
        object.vint = new int[3];
        object.vint[0] = value1;
        object.vint[1] = value2;
        object.vint[2] = value3;
        if (object.uniformID == -1)
            uniforms.add(object);
    }

    /**
     * Set an uniform value for this shader object.
     * 
     * @param var
     *            uniform variable to change
     * @param value1
     *            the new value
     * @param value2
     *            the new value
     * @param value3
     *            the new value
     */

    public void setUniform(String var, float value1, float value2, float value3) {
        ShaderUniform object = getShaderUniform(var, ShaderUniform.SU_FLOAT3);
        object.vfloat = new float[3];
        object.vfloat[0] = value1;
        object.vfloat[1] = value2;
        object.vfloat[2] = value3;
        if (object.uniformID == -1)
            uniforms.add(object);
    }

    /**
     * Set an uniform value for this shader object.
     * 
     * @param var
     *            uniform variable to change
     * @param value1
     *            the new value
     * @param value2
     *            the new value
     * @param value3
     *            the new value
     * @param value4
     *            the new value
     */

    public void setUniform(String var, int value1, int value2, int value3,
            int value4) {
        ShaderUniform object = getShaderUniform(var, ShaderUniform.SU_INT4);
        object.vint = new int[4];
        object.vint[0] = value1;
        object.vint[1] = value2;
        object.vint[2] = value3;
        object.vint[3] = value4;
        if (object.uniformID == -1)
            uniforms.add(object);
    }

    /**
     * Set an uniform value for this shader object.
     * 
     * @param var
     *            uniform variable to change
     * @param value1
     *            the new value
     * @param value2
     *            the new value
     * @param value3
     *            the new value
     * @param value4
     *            the new value
     */

    public void setUniform(String var, float value1, float value2,
            float value3, float value4) {
        ShaderUniform object = getShaderUniform(var, ShaderUniform.SU_FLOAT4);
        object.vfloat = new float[4];
        object.vfloat[0] = value1;
        object.vfloat[1] = value2;
        object.vfloat[2] = value3;
        object.vfloat[3] = value4;
        if (object.uniformID == -1)
            uniforms.add(object);
    }

    /**
     * Set an uniform value for this shader object.
     * 
     * @param var
     *            uniform variable to change
     * @param value
     *            the new value (a float buffer of size 4)
     * @param transpose
     *            transpose the matrix ?
     */

    public void setUniform(String var, float value[], boolean transpose) {
        if (value.length != 4) return;

        ShaderUniform object = getShaderUniform(var, ShaderUniform.SU_MATRIX2);
        object.matrix2f = new float[4];
        object.matrix2f = value;
        object.transpose = transpose;
        if (object.uniformID == -1)
            uniforms.add(object);
    }

    /**
     * Set an uniform value for this shader object.
     * 
     * @param var
     *            uniform variable to change
     * @param value
     *            the new value
     * @param transpose
     *            transpose the matrix ?
     */

    public void setUniform(String var, Matrix3f value, boolean transpose) {
        ShaderUniform object = getShaderUniform(var, ShaderUniform.SU_MATRIX3);
        object.matrix3f = value;
        object.transpose = transpose;
        if (object.uniformID == -1)
            uniforms.add(object);
    }

    /**
     * Set an uniform value for this shader object.
     * 
     * @param var
     *            uniform variable to change
     * @param value
     *            the new value
     * @param transpose
     *            transpose the matrix ?
     */

    public void setUniform(String var, Matrix4f value, boolean transpose) {
        ShaderUniform object = getShaderUniform(var, ShaderUniform.SU_MATRIX4);
        object.matrix4f = value;
        object.transpose = transpose;
        if (object.uniformID == -1)
            uniforms.add(object);
    }

    /**
     * Set an uniform value for the shader object.
     * 
     * @param var
     *            uniform variable to change
     * @param sampler
     *            the new value
     */
    public void setUniform(String var, com.jme.image.Texture sampler) {
        setUniform(var, sampler.getTextureId());
    }

    /**
     * <code>clearUniforms</code> clears all uniform values from this state.
     *
     */
    public void clearUniforms() {
        uniforms.clear();
    }
    
    /**
     * @return RS_SHADER_OBJECTS
     * @see com.jme.scene.state.RenderState#getType()
     */
    public int getType() {
        return RS_GLSL_SHADER_OBJECTS;
    }

    private ShaderUniform getShaderUniform(String name, int type) {
        for (int x = uniforms.size(); --x >= 0; ) {
            ShaderUniform temp = (ShaderUniform)uniforms.get(x);
            if (name.equals(temp.name))
                return temp;
        }

        return new ShaderUniform(name, type);
    }
        
    /**
     * <code>load</code> loads the shader object from the specified file. The
     * program must be in ASCII format. We delegate the loading to each
     * implementation because we do not know in what format the underlying API
     * wants the data.
     * 
     * @param vert
     *            text file containing the vertex shader object
     * @param frag
     *            text file containing the fragment shader object
     */
    public abstract void load(URL vert, URL frag);
}