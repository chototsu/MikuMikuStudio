/*
 * Copyright (c) 2003-2009 jMonkeyEngine
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

package com.jme.scene.state.lwjgl.shader;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexProgram;
import org.lwjgl.opengl.ARBVertexShader;

import com.jme.util.geom.BufferUtils;
import com.jme.util.shader.ShaderVariable;
import com.jme.util.shader.uniformtypes.*;

/** Utility class for updating shadervariables(uniforms and attributes) */
public class LWJGLShaderUtil {

    private static final Logger logger = Logger.getLogger(LWJGLShaderUtil.class.getName());

    private static ByteBuffer nameBuf = BufferUtils.createByteBuffer(2048);

    /**
     * Updates a uniform shadervariable.
     * 
     * @param shaderVariable
     *            variable to update
     */
    public static void updateShaderUniform(final ShaderVariable shaderVariable) {
        if (shaderVariable instanceof ShaderVariableInt) {
            updateShaderUniform((ShaderVariableInt) shaderVariable);
        } else if (shaderVariable instanceof ShaderVariableInt2) {
            updateShaderUniform((ShaderVariableInt2) shaderVariable);
        } else if (shaderVariable instanceof ShaderVariableInt3) {
            updateShaderUniform((ShaderVariableInt3) shaderVariable);
        } else if (shaderVariable instanceof ShaderVariableInt4) {
            updateShaderUniform((ShaderVariableInt4) shaderVariable);
        } else if (shaderVariable instanceof ShaderVariableFloat) {
            updateShaderUniform((ShaderVariableFloat) shaderVariable);
        } else if (shaderVariable instanceof ShaderVariableFloat2) {
            updateShaderUniform((ShaderVariableFloat2) shaderVariable);
        } else if (shaderVariable instanceof ShaderVariableFloat3) {
            updateShaderUniform((ShaderVariableFloat3) shaderVariable);
        } else if (shaderVariable instanceof ShaderVariableFloat4) {
            updateShaderUniform((ShaderVariableFloat4) shaderVariable);
        } else if (shaderVariable instanceof ShaderVariableMatrix2) {
            updateShaderUniform((ShaderVariableMatrix2) shaderVariable);
        } else if (shaderVariable instanceof ShaderVariableMatrix3) {
            updateShaderUniform((ShaderVariableMatrix3) shaderVariable);
        } else if (shaderVariable instanceof ShaderVariableMatrix4) {
            updateShaderUniform((ShaderVariableMatrix4) shaderVariable);
        } else if (shaderVariable instanceof ShaderVariableMatrix4Array) {
            updateShaderUniform((ShaderVariableMatrix4Array) shaderVariable);
        } else {
            logger.warning("updateShaderUniform: Unknown shaderVariable type!");
        }
    }

    /**
     * Update variableID for uniform shadervariable if needed.
     * 
     * @param variable
     *            shadervaribale to update ID on
     * @param programID
     *            shader program context ID
     */
    public static void updateUniformLocation(final ShaderVariable variable, final int programID) {
        if (variable.variableID == ShaderVariable.UNINITIALIZED) {
            nameBuf.clear();
            nameBuf.put(variable.name.getBytes());
            nameBuf.put((byte) 0);
            nameBuf.rewind();

            variable.variableID = ARBShaderObjects.glGetUniformLocationARB(programID, nameBuf);

            if (variable.variableID == ShaderVariable.UNINITIALIZED) {
                logger.log(Level.SEVERE, "Shader uniform [{0}]" + " could not be located in shader", variable.name);
                variable.variableID = ShaderVariable.UNAVAILABLE;
            }
        }
    }

    private static void updateShaderUniform(final ShaderVariableInt shaderUniform) {
        if (shaderUniform.variableID >= 0) {
            ARBShaderObjects.glUniform1iARB(shaderUniform.variableID, shaderUniform.value1);
        }
    }

    private static void updateShaderUniform(final ShaderVariableInt2 shaderUniform) {
        if (shaderUniform.variableID >= 0) {
            ARBShaderObjects.glUniform2iARB(shaderUniform.variableID, shaderUniform.value1, shaderUniform.value2);
        }
    }

    private static void updateShaderUniform(final ShaderVariableInt3 shaderUniform) {
        if (shaderUniform.variableID >= 0) {
            ARBShaderObjects.glUniform3iARB(shaderUniform.variableID, shaderUniform.value1, shaderUniform.value2,
                    shaderUniform.value3);
        }
    }

    private static void updateShaderUniform(final ShaderVariableInt4 shaderUniform) {
        if (shaderUniform.variableID >= 0) {
            ARBShaderObjects.glUniform4iARB(shaderUniform.variableID, shaderUniform.value1, shaderUniform.value2,
                    shaderUniform.value3, shaderUniform.value4);
        }
    }

    private static void updateShaderUniform(final ShaderVariableFloat shaderUniform) {
        if (shaderUniform.variableID >= 0) {
            ARBShaderObjects.glUniform1fARB(shaderUniform.variableID, shaderUniform.value1);
        }
    }

    private static void updateShaderUniform(final ShaderVariableFloat2 shaderUniform) {
        if (shaderUniform.variableID >= 0) {
            ARBShaderObjects.glUniform2fARB(shaderUniform.variableID, shaderUniform.value1, shaderUniform.value2);
        }
    }

    private static void updateShaderUniform(final ShaderVariableFloat3 shaderUniform) {
        if (shaderUniform.variableID >= 0) {
            ARBShaderObjects.glUniform3fARB(shaderUniform.variableID, shaderUniform.value1, shaderUniform.value2,
                    shaderUniform.value3);
        }
    }

    private static void updateShaderUniform(final ShaderVariableFloat4 shaderUniform) {
        if (shaderUniform.variableID >= 0) {
            ARBShaderObjects.glUniform4fARB(shaderUniform.variableID, shaderUniform.value1, shaderUniform.value2,
                    shaderUniform.value3, shaderUniform.value4);
        }
    }

    private static void updateShaderUniform(final ShaderVariableMatrix2 shaderUniform) {
        if (shaderUniform.variableID >= 0) {
            shaderUniform.matrixBuffer.rewind();
            ARBShaderObjects.glUniformMatrix2ARB(shaderUniform.variableID, shaderUniform.rowMajor,
                    shaderUniform.matrixBuffer);
        }
    }

    private static void updateShaderUniform(final ShaderVariableMatrix3 shaderUniform) {
        if (shaderUniform.variableID >= 0) {
            shaderUniform.matrixBuffer.rewind();
            ARBShaderObjects.glUniformMatrix3ARB(shaderUniform.variableID, shaderUniform.rowMajor,
                    shaderUniform.matrixBuffer);
        }
    }

    private static void updateShaderUniform(final ShaderVariableMatrix4 shaderUniform) {
        if (shaderUniform.variableID >= 0) {
            shaderUniform.matrixBuffer.rewind();
            ARBShaderObjects.glUniformMatrix4ARB(shaderUniform.variableID, shaderUniform.rowMajor,
                    shaderUniform.matrixBuffer);
        }
    }

    private static void updateShaderUniform(final ShaderVariableMatrix4Array shaderUniform) {
        if (shaderUniform.variableID >= 0) {
            shaderUniform.matrixBuffer.rewind();
            ARBShaderObjects.glUniformMatrix4ARB(shaderUniform.variableID, shaderUniform.rowMajor,
                    shaderUniform.matrixBuffer);
        }
    }

    /**
     * Update variableID for attribute shadervariable if needed.
     * 
     * @param variable
     *            shadervaribale to update ID on
     * @param programID
     *            shader program context ID
     */
    public static void updateAttributeLocation(final ShaderVariable variable, final int programID) {
        if (variable.variableID == ShaderVariable.UNINITIALIZED) {
            nameBuf.clear();
            nameBuf.put(variable.name.getBytes());
            nameBuf.put((byte) 0);
            nameBuf.rewind();

            variable.variableID = ARBVertexShader.glGetAttribLocationARB(programID, nameBuf);

            if (variable.variableID == ShaderVariable.UNINITIALIZED) {
                logger.log(Level.SEVERE, "Shader attribute [{0}]" + " could not be located in shader", variable.name);
                variable.variableID = ShaderVariable.UNAVAILABLE;
            }
        }
    }

    /**
     * Updates an attribute shadervariable.
     * 
     * @param shaderVariable
     *            variable to update
     */
    public static void updateShaderAttribute(final ShaderVariable shaderVariable) {
        if (shaderVariable instanceof ShaderVariablePointerFloat) {
            updateShaderAttribute((ShaderVariablePointerFloat) shaderVariable);
        } else if (shaderVariable instanceof ShaderVariablePointerByte) {
            updateShaderAttribute((ShaderVariablePointerByte) shaderVariable);
        } else if (shaderVariable instanceof ShaderVariablePointerInt) {
            updateShaderAttribute((ShaderVariablePointerInt) shaderVariable);
        } else if (shaderVariable instanceof ShaderVariablePointerShort) {
            updateShaderAttribute((ShaderVariablePointerShort) shaderVariable);
        } else {
            logger.warning("updateShaderAttribute: Unknown shaderVariable type!");
        }
    }

    private static void updateShaderAttribute(final ShaderVariablePointerFloat shaderUniform) {
        if (shaderUniform.variableID >= 0) {
            shaderUniform.data.rewind();
            ARBVertexProgram.glEnableVertexAttribArrayARB(shaderUniform.variableID);
            ARBVertexProgram.glVertexAttribPointerARB(shaderUniform.variableID, shaderUniform.size,
                    shaderUniform.normalized, shaderUniform.stride, shaderUniform.data);
        }
    }

    private static void updateShaderAttribute(final ShaderVariablePointerByte shaderUniform) {
        if (shaderUniform.variableID >= 0) {
            shaderUniform.data.rewind();
            ARBVertexProgram.glEnableVertexAttribArrayARB(shaderUniform.variableID);
            ARBVertexProgram.glVertexAttribPointerARB(shaderUniform.variableID, shaderUniform.size,
                    shaderUniform.unsigned, shaderUniform.normalized, shaderUniform.stride, shaderUniform.data);
        }
    }

    private static void updateShaderAttribute(final ShaderVariablePointerInt shaderUniform) {
        if (shaderUniform.variableID >= 0) {
            shaderUniform.data.rewind();
            ARBVertexProgram.glEnableVertexAttribArrayARB(shaderUniform.variableID);
            ARBVertexProgram.glVertexAttribPointerARB(shaderUniform.variableID, shaderUniform.size,
                    shaderUniform.unsigned, shaderUniform.normalized, shaderUniform.stride, shaderUniform.data);
        }
    }

    private static void updateShaderAttribute(final ShaderVariablePointerShort shaderUniform) {
        if (shaderUniform.variableID >= 0) {
            shaderUniform.data.rewind();
            ARBVertexProgram.glEnableVertexAttribArrayARB(shaderUniform.variableID);
            ARBVertexProgram.glVertexAttribPointerARB(shaderUniform.variableID, shaderUniform.size,
                    shaderUniform.unsigned, shaderUniform.normalized, shaderUniform.stride, shaderUniform.data);
        }
    }
}
