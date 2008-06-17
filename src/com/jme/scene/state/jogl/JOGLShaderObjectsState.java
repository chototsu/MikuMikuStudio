/*
 * Copyright (c) 2003-2008 jMonkeyEngine
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

package com.jme.scene.state.jogl;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.jme.renderer.RenderContext;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.StateRecord;
import com.jme.scene.state.jogl.records.ShaderObjectsStateRecord;
import com.jme.scene.state.jogl.shader.JOGLShaderUtil;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.geom.BufferUtils;
import com.jme.util.shader.ShaderVariable;

/**
 * Implementation of the GL_ARB_shader_objects extension.
 *
 * @author Thomas Hourdel
 * @author Joshua Slack (attributes and StateRecord)
 * @author Rikard Herlitz (MrCoder)
 */
public class JOGLShaderObjectsState extends GLSLShaderObjectsState {
    private static final Logger logger = Logger.getLogger(JOGLShaderObjectsState.class.getName());

    private static final long serialVersionUID = 1L;

    /** OpenGL id for this program. * */
    private int programID = -1;

    /** OpenGL id for the attached vertex shader. */
    private int vertexShaderID = -1;

    /** OpenGL id for the attached fragment shader. */
    private int fragmentShaderID = -1;

    /** Holds the maximum number of vertex attributes available. */
    private static int maxVertexAttribs;

    private static boolean inited = false;

    public JOGLShaderObjectsState() {
        super();


        final GL gl = GLU.getCurrentGL();


        if (!inited) {
            glslSupported = glslSupportedDetected = gl.isExtensionAvailable("GL_ARB_shader_objects")
                    && gl.isExtensionAvailable("GL_ARB_fragment_shader")
                    && gl.isExtensionAvailable("GL_ARB_vertex_shader")
                    && gl.isExtensionAvailable("GL_ARB_shading_language_100");

            // get the number of supported shader attributes
            if (isSupported()) {
                IntBuffer buf = BufferUtils.createIntBuffer(16);
                gl.glGetIntegerv(GL.GL_MAX_VERTEX_ATTRIBS_ARB,
                        buf); // TODO Check for integer
                maxVertexAttribs = buf.get(0);

                if (logger.isLoggable(Level.FINE)) {
                    StringBuffer shaderInfo = new StringBuffer();
                    shaderInfo.append("GL_MAX_VERTEX_ATTRIBS: "
                            + maxVertexAttribs + "\n");

                    gl.glGetIntegerv(
                                    GL.GL_MAX_VERTEX_UNIFORM_COMPONENTS_ARB,
                                    buf); // TODO Check for integer
                    shaderInfo.append("GL_MAX_VERTEX_UNIFORM_COMPONENTS: "
                            + buf.get(0) + "\n");

                    gl.glGetIntegerv(GL.GL_MAX_FRAGMENT_UNIFORM_COMPONENTS_ARB,
                            buf); // TODO Check for integer
                    shaderInfo.append("GL_MAX_FRAGMENT_UNIFORM_COMPONENTS: "
                            + buf.get(0) + "\n");

                    gl.glGetIntegerv(GL.GL_MAX_TEXTURE_COORDS_ARB, buf); // TODO Check for integer
                    shaderInfo.append("GL_MAX_TEXTURE_COORDS: " + buf.get(0)
                            + "\n");

                    gl.glGetIntegerv(GL.GL_MAX_TEXTURE_IMAGE_UNITS_ARB, buf); // TODO Check for integer
                    shaderInfo.append("GL_MAX_TEXTURE_IMAGE_UNITS: "
                            + buf.get(0) + "\n");

                    gl.glGetIntegerv(GL.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS_ARB,
                            buf); // TODO Check for integer
                    shaderInfo.append("GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS: "
                            + buf.get(0) + "\n");

                    gl.glGetIntegerv(GL.GL_MAX_VARYING_FLOATS_ARB, buf); // TODO Check for integer
                    shaderInfo.append("GL_MAX_VARYING_FLOATS: " + buf.get(0)
                            + "\n");

                    shaderInfo.append(gl
                            .glGetString(GL.GL_SHADING_LANGUAGE_VERSION_ARB));

                    logger.fine(shaderInfo.toString());
                }
            }

            // We're done initing! Wee! :)
            inited = true;
        }
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

        final GL gl = GLU.getCurrentGL();


        if (vertexByteBuffer == null && fragmentByteBuffer == null) {
            logger.warning("Could not find shader resources!"
                    + "(both inputbuffers are null)");
            return;
        }

        if (programID == -1)
            programID = gl.glCreateProgramObjectARB();

        if (vertexByteBuffer != null) {
            if (vertexShaderID != -1)
                removeVertShader();

            vertexShaderID = gl.glCreateShaderObjectARB(
                    GL.GL_VERTEX_SHADER_ARB);

            // Create the sources
            gl.glShaderSourceARB(vertexShaderID, 1, new String[] { new String(
                    vertexByteBuffer.array()) }, new int[] { vertexByteBuffer
                    .limit() }, 0); // TODO Check <size>

            // Compile the vertex shader
            IntBuffer compiled = BufferUtils.createIntBuffer(1);
            gl.glCompileShaderARB(vertexShaderID);
            gl.glGetObjectParameterivARB(vertexShaderID,
                    GL.GL_OBJECT_COMPILE_STATUS_ARB, compiled); // TODO Check for int
            checkProgramError(compiled, vertexShaderID);

            // Attach the program
            gl.glAttachObjectARB(programID, vertexShaderID);
        } else if (vertexShaderID != -1) {
            removeVertShader();
            vertexShaderID = -1;
        }

        if (fragmentByteBuffer != null) {
            if (fragmentShaderID != -1)
                removeFragShader();

            fragmentShaderID = gl.glCreateShaderObjectARB(
                    GL.GL_FRAGMENT_SHADER_ARB);

            // Create the sources
            gl
                    .glShaderSourceARB(fragmentShaderID, 1, new String[] {new String(fragmentByteBuffer.array())}, new int[] {fragmentByteBuffer.limit()}, 0); // TODO Check <size>

            // Compile the fragment shader
            IntBuffer compiled = BufferUtils.createIntBuffer(1);
            gl.glCompileShaderARB(fragmentShaderID);
            gl.glGetObjectParameterivARB(fragmentShaderID,
                    GL.GL_OBJECT_COMPILE_STATUS_ARB, compiled); // TODO Check for int
            checkProgramError(compiled, fragmentShaderID);

            // Attatch the program
            gl.glAttachObjectARB(programID, fragmentShaderID);
        } else if (fragmentShaderID != -1) {
            removeFragShader();
            fragmentShaderID = -1;
        }

        gl.glLinkProgramARB(programID);
        setNeedsRefresh(true);
    }

    /** Removes the fragment shader */
    private void removeFragShader() {
        final GL gl = GLU.getCurrentGL();

        if (fragmentShaderID != -1) {
            gl.glDetachObjectARB(programID, fragmentShaderID);
            gl.glDeleteObjectARB(fragmentShaderID);
        }
    }

    /** Removes the vertex shader */
    private void removeVertShader() {
        final GL gl = GLU.getCurrentGL();

        if (vertexShaderID != -1) {
            gl.glDetachObjectARB(programID, vertexShaderID);
            gl.glDeleteObjectARB(vertexShaderID);
        }
    }

    /**
     * Check for program errors. If an error is detected, program exits.
     *
     * @param compiled the compiler state for a given shader
     * @param id shader's id
     */
    private void checkProgramError(IntBuffer compiled, int id) {
        final GL gl = GLU.getCurrentGL();

        if (compiled.get(0) == 0) {
            IntBuffer iVal = BufferUtils.createIntBuffer(1);
            gl.glGetObjectParameterivARB(id,
                    GL.GL_OBJECT_INFO_LOG_LENGTH_ARB, iVal); // TODO Check for int
            int length = iVal.get();
            String out = null;

            if (length > 0) {
                ByteBuffer infoLog = BufferUtils.createByteBuffer(length);

                iVal.flip();
                gl.glGetInfoLogARB(id, infoLog.limit(), iVal, infoLog); // TODO Check <bufSize>

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
        final GL gl = GLU.getCurrentGL();

        if (isSupported()) {
            //Ask for the current state record
            RenderContext context = DisplaySystem.getDisplaySystem()
                    .getCurrentContext();
            ShaderObjectsStateRecord record = (ShaderObjectsStateRecord) context
                    .getStateRecord(RS_GLSL_SHADER_OBJECTS);
            context.currentStates[RS_GLSL_SHADER_OBJECTS] = this;

            if (shaderDataLogic != null) {
                shaderDataLogic.applyData(this, geom);
            }

            if (!record.isValid() || record.getReference() != this ||
                    needsRefresh()) {
                record.setReference(this);
                if (isEnabled()) {
                    if (programID != -1) {
                        gl.glUseProgramObjectARB(programID);

                        for (int i = shaderAttributes.size(); --i >= 0;) {
                            ShaderVariable shaderVariable =
                                    shaderAttributes.get(i);
                            if (shaderVariable.needsRefresh) {
                                JOGLShaderUtil.updateAttributeLocation(
                                        shaderVariable, programID);
                                shaderVariable.needsRefresh = false;
                            }
                            JOGLShaderUtil
                                    .updateShaderAttribute(shaderVariable);
                        }

                        for (int i = shaderUniforms.size(); --i >= 0;) {
                            ShaderVariable shaderVariable =
                                    shaderUniforms.get(i);
                            if (shaderVariable.needsRefresh) {
                                JOGLShaderUtil.updateUniformLocation(
                                        shaderVariable, programID);
                                JOGLShaderUtil
                                        .updateShaderUniform(shaderVariable);
                                shaderVariable.needsRefresh = false;
                            }
                        }
                    }
                } else {
                    gl.glUseProgramObjectARB(0);
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
