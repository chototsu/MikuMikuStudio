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
package com.jme.scene.state.lwjgl;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.logging.Level;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexProgram;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GLContext;

import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.util.LoggingSystem;
import com.jme.util.ShaderAttribute;
import com.jme.util.ShaderUniform;

/**
 * Implementation of the GL_ARB_shader_objects extension.
 * 
 * @author Thomas Hourdel
 * @author Joshua Slack (attributes)
 */
public class LWJGLShaderObjectsState extends GLSLShaderObjectsState {

    private static final long serialVersionUID = 1L;

    /** OpenGL id for this program. * */
    private int programID = -1;

    /**
     * Determines if the current OpenGL context supports the
     * GL_ARB_shader_objects extension.
     * 
     * @see com.jme.scene.state.ShaderObjectsState#isSupported()
     */
    public boolean isSupported() {
        return GLContext.GL_ARB_shader_objects;
    }

    /**
     * <code>relinkProgram</code> instructs openGL to relink the associated 
     * program.  This should be used after setting ShaderAttributes.
     */
    public void relinkProgram() {
        ByteBuffer nameBuf = BufferUtils.createByteBuffer(64);
        for (int x = 0, aSize = attribs.size(); x<aSize; x++) {
            ShaderAttribute attrib = (ShaderAttribute)attribs.get(x);
            nameBuf.clear();
            nameBuf.put(attrib.name.getBytes());
            nameBuf.rewind();
            attrib.attributeID = x+1;
            ARBVertexShader.glBindAttribLocationARB(programID, x+1, nameBuf);            
        }

        ARBShaderObjects.glLinkProgramARB(programID);     
    }

    /**
     * Get uniform variable location according to his string name.
     * 
     * @param name
     *            uniform variable name
     */
    private int getUniLoc(ShaderUniform uniform) {
        if (uniform.uniformID == -1) {
            ByteBuffer nameBuf = BufferUtils
            	.createByteBuffer(uniform.name.getBytes().length+1);
            nameBuf.clear();
            nameBuf.put(uniform.name.getBytes());
            nameBuf.rewind();
            
            uniform.uniformID = ARBShaderObjects.glGetUniformLocationARB(programID, nameBuf);
        }
        return uniform.uniformID; 
    }

    /**
     * Load an URL and grab content into a ByteBuffer.
     * 
     * @param url
     *            the url to load
     */

    private ByteBuffer load(java.net.URL url) {
        try {
            byte shaderCode[] = null;
            ByteBuffer shaderByteBuffer = null;

            BufferedInputStream bufferedInputStream = new BufferedInputStream(
                    url.openStream());
            DataInputStream dataStream = new DataInputStream(
                    bufferedInputStream);
            dataStream.readFully(shaderCode = new byte[bufferedInputStream
                    .available()]);
            bufferedInputStream.close();
            dataStream.close();

            shaderByteBuffer = BufferUtils.createByteBuffer(shaderCode.length);
            shaderByteBuffer.put(shaderCode);
            shaderByteBuffer.rewind();

            return shaderByteBuffer;
        } catch (Exception e) {
            LoggingSystem.getLogger().log(Level.SEVERE,
                    "Could not load shader object: " + e);
            LoggingSystem.getLogger().throwing(getClass().getName(),
                    "load(URL)", e);
            return null;
        }
    }

    /**
     * Loads the shader object.
     * 
     * @see com.jme.scene.state.ShaderObjectsState#load(java.net.URL,
     *      java.net.URL)
     */
    public void load(URL vert, URL frag) {
        ByteBuffer vertexByteBuffer = load(vert);
        ByteBuffer fragmentByteBuffer = load(frag);

        if (vertexByteBuffer != null || fragmentByteBuffer != null) {
            // Create the shader objects
            int vertexShaderID = ARBShaderObjects
                    .glCreateShaderObjectARB(ARBVertexShader.GL_VERTEX_SHADER_ARB);
            int fragmentShaderID = ARBShaderObjects
                    .glCreateShaderObjectARB(ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);

            // Create the sources
            ARBShaderObjects
                    .glShaderSourceARB(vertexShaderID, vertexByteBuffer);
            ARBShaderObjects.glShaderSourceARB(fragmentShaderID,
                    fragmentByteBuffer);

            // Compile the vertex shader
            IntBuffer compiled = BufferUtils.createIntBuffer(1);
            ARBShaderObjects.glCompileShaderARB(vertexShaderID);
            ARBShaderObjects.glGetObjectParameterARB(vertexShaderID,
                    ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB, compiled);
            checkProgramError(compiled, vertexShaderID);

            // Compile the fragment shader
            compiled.clear();
            ARBShaderObjects.glCompileShaderARB(fragmentShaderID);
            ARBShaderObjects.glGetObjectParameterARB(fragmentShaderID,
                    ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB, compiled);
            checkProgramError(compiled, fragmentShaderID);

            // Create the program
            programID = ARBShaderObjects.glCreateProgramObjectARB();
            ARBShaderObjects.glAttachObjectARB(programID, vertexShaderID);
            ARBShaderObjects.glAttachObjectARB(programID, fragmentShaderID);
            
            ARBShaderObjects.glLinkProgramARB(programID);
        }

    }

    /**
     * Check for program errors. If an error is detected, program exits.
     * 
     * @param compiled
     *            the compiler state for a given shader
     * @param id
     *            shader's id
     */
    private void checkProgramError(IntBuffer compiled, int id) {

        if (compiled.get(0) == 0) {
            IntBuffer iVal = BufferUtils.createIntBuffer(1);
            ARBShaderObjects.glGetObjectParameterARB(id,
                    ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB, iVal);
            int length = iVal.get();
            String out = null;

            if (length > 0) {
                ByteBuffer infoLog = BufferUtils.createByteBuffer(length);

                iVal.flip();
                ARBShaderObjects.glGetInfoLogARB(id, iVal, infoLog);

                byte[] infoBytes = new byte[length];
                infoLog.get(infoBytes);
                out = new String(infoBytes);
            }

            LoggingSystem.getLogger().log(Level.SEVERE, out);
            System.exit(0);
        }
    }

    /**
     * Applies those shader objects to the current scene. Checks if the
     * GL_ARB_shader_objects extension is supported before attempting to enable
     * those objects.
     * 
     * @see com.jme.scene.state.RenderState#apply()
     */
    public void apply() {
        if (isSupported()) {
            if (isEnabled()) {
                if (programID != -1) {
                    // Apply the shader...
                    ARBShaderObjects.glUseProgramObjectARB(programID);
                    
                    // Assign attribs...
                    if (!attribs.isEmpty()) {
                        for (int x = attribs.size(); --x >= 0; ) {
                            ShaderAttribute attVar = (ShaderAttribute) attribs.get(x);
                            switch (attVar.type) {
                            case ShaderAttribute.SU_SHORT:
                                ARBVertexProgram.glVertexAttrib1sARB(
                                        attVar.attributeID,
                                        attVar.s1);
                                break;
                            case ShaderAttribute.SU_SHORT2:
                                ARBVertexProgram.glVertexAttrib2sARB(
                                        attVar.attributeID,
                                        attVar.s1, attVar.s2);
                                break;
                            case ShaderAttribute.SU_SHORT3:
                                ARBVertexProgram.glVertexAttrib3sARB(
                                        attVar.attributeID,
                                        attVar.s1, attVar.s2,
                                        attVar.s3);
                                break;
                            case ShaderAttribute.SU_SHORT4:
                                ARBVertexProgram.glVertexAttrib4sARB(
                                        attVar.attributeID,
                                        attVar.s1, attVar.s2,
                                        attVar.s3, attVar.s4);
                                break;
                            case ShaderAttribute.SU_FLOAT:
                                ARBVertexProgram.glVertexAttrib1fARB(
                                        attVar.attributeID,
                                        attVar.f1);
                                break;
                            case ShaderAttribute.SU_FLOAT2:
                                ARBVertexProgram.glVertexAttrib2fARB(
                                        attVar.attributeID,
                                        attVar.f1, attVar.f2);
                                break;
                            case ShaderAttribute.SU_FLOAT3:
                                ARBVertexProgram.glVertexAttrib3fARB(
                                        attVar.attributeID,
                                        attVar.f1, attVar.f2,
                                        attVar.f3);
                                break;
                            case ShaderAttribute.SU_FLOAT4:
                                ARBVertexProgram.glVertexAttrib4fARB(
                                        attVar.attributeID,
                                        attVar.f1, attVar.f2,
                                        attVar.f3, attVar.f4);
                                break;
                            case ShaderAttribute.SU_NORMALIZED_UBYTE4:
                                ARBVertexProgram.glVertexAttrib4NubARB(
                                        attVar.attributeID,
                                        attVar.b1, attVar.b2,
                                        attVar.b3, attVar.b4);
                                break;
                            case ShaderAttribute.SU_POINTER_FLOAT:
                                ARBVertexProgram.glVertexAttribPointerARB(
                                        attVar.attributeID,
                                        attVar.size,
                                        attVar.normalized,
                                        attVar.stride,
                                        (FloatBuffer)attVar.data);
                                ARBVertexProgram.glEnableVertexAttribArrayARB(attVar.attributeID);
                                break;
                            case ShaderAttribute.SU_POINTER_BYTE:
                                ARBVertexProgram.glVertexAttribPointerARB(
                                        attVar.attributeID,
                                        attVar.size,
                                        attVar.unsigned,
                                        attVar.normalized,
                                        attVar.stride,
                                        (ByteBuffer)attVar.data);
                                ARBVertexProgram.glEnableVertexAttribArrayARB(attVar.attributeID);
                                break;
                            case ShaderAttribute.SU_POINTER_INT:
                                ARBVertexProgram.glVertexAttribPointerARB(
                                        attVar.attributeID,
                                        attVar.size,
                                        attVar.unsigned,
                                        attVar.normalized,
                                        attVar.stride,
                                        (IntBuffer)attVar.data);
                                ARBVertexProgram.glEnableVertexAttribArrayARB(attVar.attributeID);
                                break;
                            case ShaderAttribute.SU_POINTER_SHORT:
                                ARBVertexProgram.glVertexAttribPointerARB(
                                        attVar.attributeID,
                                        attVar.size,
                                        attVar.unsigned,
                                        attVar.normalized,
                                        attVar.stride,
                                        (ShortBuffer)attVar.data);
                                ARBVertexProgram.glEnableVertexAttribArrayARB(attVar.attributeID);
                                break;
                            default: // Should never happen.
                                break;
                            }
                        }
                    }
                    
                    // Assign uniforms...
                    if (!uniforms.isEmpty()) {
                        for (int x = uniforms.size(); --x >= 0; ) {
                            ShaderUniform uniformVar = (ShaderUniform) uniforms.get(x);
                            switch (uniformVar.type) {
                            case ShaderUniform.SU_INT:
                                ARBShaderObjects.glUniform1iARB(
                                        getUniLoc(uniformVar),
                                        uniformVar.vint[0]);
                                break;
                            case ShaderUniform.SU_INT2:
                                ARBShaderObjects.glUniform2iARB(
                                        getUniLoc(uniformVar),
                                        uniformVar.vint[0], uniformVar.vint[1]);
                                break;
                            case ShaderUniform.SU_INT3:
                                ARBShaderObjects.glUniform3iARB(
                                        getUniLoc(uniformVar),
                                        uniformVar.vint[0], uniformVar.vint[1],
                                        uniformVar.vint[2]);
                                break;
                            case ShaderUniform.SU_INT4:
                                ARBShaderObjects.glUniform4iARB(
                                        getUniLoc(uniformVar),

                                        uniformVar.vint[0], uniformVar.vint[1],
                                        uniformVar.vint[2], uniformVar.vint[3]);
                                break;
                            case ShaderUniform.SU_FLOAT:
                                ARBShaderObjects.glUniform1fARB(
                                        getUniLoc(uniformVar),
                                        uniformVar.vfloat[0]);
                                break;
                            case ShaderUniform.SU_FLOAT2:
                                ARBShaderObjects.glUniform2fARB(
                                        getUniLoc(uniformVar),
                                        uniformVar.vfloat[0],
                                        uniformVar.vfloat[1]);
                                break;
                            case ShaderUniform.SU_FLOAT3:
                                ARBShaderObjects.glUniform3fARB(
                                        getUniLoc(uniformVar),
                                        uniformVar.vfloat[0],
                                        uniformVar.vfloat[1],
                                        uniformVar.vfloat[2]);
                                break;
                            case ShaderUniform.SU_FLOAT4:
                                ARBShaderObjects.glUniform4fARB(
                                        getUniLoc(uniformVar),
                                        uniformVar.vfloat[0],
                                        uniformVar.vfloat[1],
                                        uniformVar.vfloat[2],
                                        uniformVar.vfloat[3]);
                                break;
                            case ShaderUniform.SU_MATRIX2:
                                if (uniformVar.matrixBuffer == null)
                                    uniformVar.matrixBuffer = org.lwjgl.BufferUtils.createFloatBuffer(4);
                                uniformVar.matrixBuffer.clear();
                                uniformVar.matrixBuffer.put(uniformVar.matrix2f);
                                uniformVar.matrixBuffer.rewind();
                                ARBShaderObjects.glUniformMatrix2ARB(
                                        getUniLoc(uniformVar),
                                        uniformVar.transpose, uniformVar.matrixBuffer);
                                break;
                            case ShaderUniform.SU_MATRIX3:
                                if (uniformVar.matrixBuffer == null)
                                    uniformVar.matrixBuffer = uniformVar.matrix3f.toFloatBuffer();
                                else 
                                    uniformVar.matrix3f.fillFloatBuffer(uniformVar.matrixBuffer);
                                ARBShaderObjects.glUniformMatrix3ARB(
                                        getUniLoc(uniformVar),
                                        uniformVar.transpose,
                                        uniformVar.matrixBuffer);
                                break;
                            case ShaderUniform.SU_MATRIX4:
                                if (uniformVar.matrixBuffer == null)
                                    uniformVar.matrixBuffer = uniformVar.matrix4f.toFloatBuffer();
                                else 
                                    uniformVar.matrix4f.fillFloatBuffer(uniformVar.matrixBuffer);
                                ARBShaderObjects.glUniformMatrix4ARB(
                                        getUniLoc(uniformVar),
                                        uniformVar.transpose,
                                        uniformVar.matrixBuffer);
                                break;
                            default: // Should never happen.
                                break;
                            }
                        }
                    }
                }
            } else {
                ARBShaderObjects.glUseProgramObjectARB(0);
            }
        }
    }

}