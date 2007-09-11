/*
 * Copyright (c) 2003-2007 jMonkeyEngine
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

package com.jme.scene.state.lwjgl;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;

import com.jme.renderer.RenderContext;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.lwjgl.records.ShaderObjectsStateRecord;
import com.jme.scene.state.lwjgl.records.StateRecord;
import com.jme.scene.state.lwjgl.shader.LWJGLShaderUtil;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.shader.ShaderVariable;

/**
 * Implementation of the GL_ARB_shader_objects extension.
 *
 * @author Thomas Hourdel
 * @author Joshua Slack (attributes and StateRecord)
 * @author Rikard Herlitz (MrCoder)
 */
public class LWJGLShaderObjectsState extends GLSLShaderObjectsState {
    private static final Logger logger = Logger.getLogger(LWJGLShaderObjectsState.class.getName());

    private static final long serialVersionUID = 1L;

    /** OpenGL id for this program. * */
    private int programID = -1;

    /** OpenGL id for the attached vertex shader. */
    private int vertexShaderID = -1;

    /** OpenGL id for the attached fragment shader. */
    private int fragmentShaderID = -1;
    
    /** Holds the maximum number of vertex attributes available. */
    private int maxVertexAttribs;

    public LWJGLShaderObjectsState() {
        super();
        
        // get the number of supported shader attributes
        if(isSupported()) {
            IntBuffer buf = BufferUtils.createIntBuffer(16);
            GL11.glGetInteger(GL20.GL_MAX_VERTEX_ATTRIBS, buf);
            maxVertexAttribs = buf.get(0);
            
            if (logger.isLoggable(Level.FINE)) {
                StringBuffer shaderInfo = new StringBuffer();
                shaderInfo.append("GL_MAX_VERTEX_ATTRIBS: " + maxVertexAttribs + "\n");
                GL11.glGetInteger(GL20.GL_MAX_VERTEX_UNIFORM_COMPONENTS, buf);
                shaderInfo.append("GL_MAX_VERTEX_UNIFORM_COMPONENTS: " + buf.get(0) + "\n");
                GL11.glGetInteger(GL20.GL_MAX_FRAGMENT_UNIFORM_COMPONENTS, buf);
                shaderInfo.append("GL_MAX_FRAGMENT_UNIFORM_COMPONENTS: " + buf.get(0) + "\n");
                GL11.glGetInteger(GL20.GL_MAX_TEXTURE_COORDS, buf);
                shaderInfo.append("GL_MAX_TEXTURE_COORDS: " + buf.get(0) + "\n");
                GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS, buf);
                shaderInfo.append("GL_MAX_TEXTURE_IMAGE_UNITS: " + buf.get(0) + "\n");
                GL11.glGetInteger(GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, buf);
                shaderInfo.append("GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS: " + buf.get(0) + "\n");
                GL11.glGetInteger(GL20.GL_MAX_VARYING_FLOATS, buf);
                shaderInfo.append("GL_MAX_VARYING_FLOATS: " + buf.get(0) + "\n");
                shaderInfo.append(GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
                
                logger.fine(shaderInfo.toString());
            }
        }
    }
    
    /**
     * Determines if the current OpenGL context supports the
     * GL_ARB_shader_objects extension.
     *
     * @see com.jme.scene.state.GLSLShaderObjectsState#isSupported()
     */
    public boolean isSupported() {
        return GLContext.getCapabilities().GL_ARB_shader_objects &&
                GLContext.getCapabilities().GL_ARB_fragment_shader &&
                GLContext.getCapabilities().GL_ARB_vertex_shader &&
                GLContext.getCapabilities().GL_ARB_shading_language_100;
    }

    /**
     * Load an URL and grab content into a ByteBuffer.
     *
     * @param url the url to load
     * @return the loaded url
     */
    private ByteBuffer load(java.net.URL url) {
        try {
            BufferedInputStream bufferedInputStream =
                    new BufferedInputStream(url.openStream());
            DataInputStream dataStream =
                    new DataInputStream(bufferedInputStream);
            byte shaderCode[] = new byte[bufferedInputStream.available()];
            dataStream.readFully(shaderCode);
            bufferedInputStream.close();
            dataStream.close();
            ByteBuffer shaderByteBuffer =
                    BufferUtils.createByteBuffer(shaderCode.length);
            shaderByteBuffer.put(shaderCode);
            shaderByteBuffer.rewind();

            return shaderByteBuffer;
        } catch (Exception e) {
            logger.severe("Could not load shader object: " + e);
            logger.logp(Level.SEVERE, getClass().getName(), "load(URL)", "Exception", e);
            return null;
        }
    }

    /**
     * Loads a string into a ByteBuffer
     *
     * @param data string to load into ByteBuffer
     * @return the converted string
     */
    private ByteBuffer load(String data) {
        try {
            byte[] bytes = data.getBytes();
            ByteBuffer program = BufferUtils.createByteBuffer(bytes.length);
            program.put(bytes);
            program.rewind();
            return program;
        } catch (Exception e) {
            logger.severe("Could not load fragment program: " + e);
            logger.logp(Level.SEVERE, getClass().getName(), "load(URL)", "Exception", e);
            return null;
        }
    }

    /**
     * Loads the shader object. Use null for an empty vertex or empty fragment
     * shader.
     *
     * @param vert vertex shader
     * @param frag fragment shader
     * @see com.jme.scene.state.GLSLShaderObjectsState#load(java.net.URL,
     *java.net.URL)
     */
    public void load(URL vert, URL frag) {
        ByteBuffer vertexByteBuffer = vert != null ? load(vert) : null;
        ByteBuffer fragmentByteBuffer = frag != null ? load(frag) : null;
        load(vertexByteBuffer, fragmentByteBuffer);
    }

    /**
     * Loads the shader object. Use null for an empty vertex or empty fragment
     * shader.
     *
     * @param vert vertex shader
     * @param frag fragment shader
     * @see com.jme.scene.state.GLSLShaderObjectsState#load(java.net.URL,
     *java.net.URL)
     */
    public void load(String vert, String frag) {
        ByteBuffer vertexByteBuffer = vert != null ? load(vert) : null;
        ByteBuffer fragmentByteBuffer = frag != null ? load(frag) : null;
        load(vertexByteBuffer, fragmentByteBuffer);
    }

    /**
     * Loads the shader object. Use null for an empty vertex or empty fragment
     * shader.
     *
     * @param vertexByteBuffer vertex shader
     * @param fragmentByteBuffer fragment shader
     * @see com.jme.scene.state.GLSLShaderObjectsState#load(java.net.URL,
     *java.net.URL)
     */
    private void load(ByteBuffer vertexByteBuffer,
            ByteBuffer fragmentByteBuffer) {

        if (vertexByteBuffer == null && fragmentByteBuffer == null) {
            logger.warning("Could not find shader resources!"
                    + "(both inputbuffers are null)");
            return;
        }

        if (programID == -1)
            programID = ARBShaderObjects.glCreateProgramObjectARB();

        if (vertexByteBuffer != null) {
            if (vertexShaderID != -1)
                removeVertShader();

            vertexShaderID = ARBShaderObjects.glCreateShaderObjectARB(
                    ARBVertexShader.GL_VERTEX_SHADER_ARB);

            // Create the sources
            ARBShaderObjects
                    .glShaderSourceARB(vertexShaderID, vertexByteBuffer);

            // Compile the vertex shader
            IntBuffer compiled = BufferUtils.createIntBuffer(1);
            ARBShaderObjects.glCompileShaderARB(vertexShaderID);
            ARBShaderObjects.glGetObjectParameterARB(vertexShaderID,
                    ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB, compiled);
            checkProgramError(compiled, vertexShaderID);

            // Attach the program
            ARBShaderObjects.glAttachObjectARB(programID, vertexShaderID);
        } else if (vertexShaderID != -1) {
            removeVertShader();
            vertexShaderID = -1;
        }

        if (fragmentByteBuffer != null) {
            if (fragmentShaderID != -1)
                removeFragShader();

            fragmentShaderID = ARBShaderObjects.glCreateShaderObjectARB(
                    ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);

            // Create the sources
            ARBShaderObjects
                    .glShaderSourceARB(fragmentShaderID, fragmentByteBuffer);

            // Compile the fragment shader
            IntBuffer compiled = BufferUtils.createIntBuffer(1);
            ARBShaderObjects.glCompileShaderARB(fragmentShaderID);
            ARBShaderObjects.glGetObjectParameterARB(fragmentShaderID,
                    ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB, compiled);
            checkProgramError(compiled, fragmentShaderID);

            // Attatch the program
            ARBShaderObjects.glAttachObjectARB(programID, fragmentShaderID);
        } else if (fragmentShaderID != -1) {
            removeFragShader();
            fragmentShaderID = -1;
        }

        ARBShaderObjects.glLinkProgramARB(programID);
        setNeedsRefresh(true);
    }

    /** Removes the fragment shader */
    private void removeFragShader() {
        if (fragmentShaderID != -1) {
            ARBShaderObjects.glDetachObjectARB(programID, fragmentShaderID);
            ARBShaderObjects.glDeleteObjectARB(fragmentShaderID);
        }
    }

    /** Removes the vertex shader */
    private void removeVertShader() {
        if (vertexShaderID != -1) {
            ARBShaderObjects.glDetachObjectARB(programID, vertexShaderID);
            ARBShaderObjects.glDeleteObjectARB(vertexShaderID);
        }
    }

    /**
     * Check for program errors. If an error is detected, program exits.
     *
     * @param compiled the compiler state for a given shader
     * @param id shader's id
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

            logger.severe(out);
            
            throw new JmeException("Error compiling GLSL shader: " + out);
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
            //Ask for the current state record
            RenderContext context = DisplaySystem.getDisplaySystem()
                    .getCurrentContext();
            ShaderObjectsStateRecord record = (ShaderObjectsStateRecord) context
                    .getStateRecord(RS_GLSL_SHADER_OBJECTS);
            context.currentStates[RS_GLSL_SHADER_OBJECTS] = this;

            if (shaderDataLogic != null) {
                shaderDataLogic.applyData(this, batch);            
            }

            if (!record.isValid() || record.getReference() != this ||
                    needsRefresh()) {
                record.setReference(this);
                if (isEnabled()) {
                    if (programID != -1) {
                        ARBShaderObjects.glUseProgramObjectARB(programID);
 
                        for (int i = shaderAttributes.size(); --i >= 0;) {
                            ShaderVariable shaderVariable =
                                    shaderAttributes.get(i);
                            if (shaderVariable.needsRefresh) {                                                           
                                LWJGLShaderUtil.updateAttributeLocation(
                                        shaderVariable, programID);
                                shaderVariable.needsRefresh = false;
                            }
                            LWJGLShaderUtil
                                    .updateShaderAttribute(shaderVariable);
                        }

                        for (int i = shaderUniforms.size(); --i >= 0;) {
                            ShaderVariable shaderVariable =
                                    shaderUniforms.get(i);
                            if (shaderVariable.needsRefresh) {
                                LWJGLShaderUtil.updateUniformLocation(
                                        shaderVariable, programID);
                                LWJGLShaderUtil
                                        .updateShaderUniform(shaderVariable);
                                shaderVariable.needsRefresh = false;
                            }
                        }
                    }
                } else {
                    ARBShaderObjects.glUseProgramObjectARB(0);
                }
            }

            if (!record.isValid())
                record.validate();
        }
    }

    @Override
    public StateRecord createStateRecord() {
        return new ShaderObjectsStateRecord();
    }

    /* (non-Javadoc)
     * @see com.jme.scene.state.GLSLShaderObjectsState#checkAttributeSizeLimits()
     */
    @Override
    public void checkAttributeSizeLimits() {
      if (shaderAttributes.size() > maxVertexAttribs) {
            logger.severe("Too many shader attributes(standard+defined): "
                            + shaderAttributes.size() + " maximum: "
                            + maxVertexAttribs);
        } else if (shaderAttributes.size() + 16 > maxVertexAttribs) {
            logger.warning("User defined attributes might overwrite default OpenGL attributes");
        }
    }

    /* (non-Javadoc)
     * @see com.jme.scene.state.GLSLShaderObjectsState#checkUniformSizeLimits()
     */
    @Override
    public void checkUniformSizeLimits() {
    }
    
    
}