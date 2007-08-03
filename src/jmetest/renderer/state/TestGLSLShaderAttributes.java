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

package jmetest.renderer.state;

import com.jme.app.SimpleGame;
import com.jme.input.NodeHandler;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.util.geom.BufferUtils;
import java.nio.FloatBuffer;
import java.util.logging.Logger;

/**
 * Tests GLSL shader attributes functionality
 *
 * @author Rikard Herlitz (MrCoder)
 */
public class TestGLSLShaderAttributes extends SimpleGame {
    private static final Logger logger = Logger
            .getLogger(TestGLSLShaderAttributes.class.getName());
    
    /** Shader attribute buffer for vertex colors */
    private FloatBuffer vertexColors;
    /** Shader attribute buffer for amount of offset to normal */
    private FloatBuffer vertexOffset;

    public static void main(String[] args) {
        TestGLSLShaderAttributes app = new TestGLSLShaderAttributes();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleInitGame() {
        display.setTitle("Test GLSL attributes");

        cam.setLocation(new Vector3f(0, 0, 2));
        cam.update();
        input = new NodeHandler(rootNode, 10, 2);

        Quad brick = createBrickQuad();
        rootNode.attachChild(brick);

        rootNode.updateRenderState();
    }

    protected void simpleUpdate() {
        vertexColors.rewind();
        for (int i = 0; i < 4; i++) {
            float adder = i * 0.5f;
            vertexColors.
                    put(FastMath.sin(timer.getTimeInSeconds() * 5.0f + adder) *
                            0.5f + 0.5f).
                    put(FastMath.sin(
                            timer.getTimeInSeconds() * 5.0f + 1.0f + adder) *
                            0.5f + 0.5f).
                    put(FastMath.sin(
                            timer.getTimeInSeconds() * 5.0f + 2.0f + adder) *
                            0.5f + 0.5f).
                    put(1.0f);
        }

        vertexOffset.rewind();
        vertexOffset.put(
                FastMath.sin(timer.getTimeInSeconds() * 5.0f) * 0.5f + 0.5f)
                .
                        put(FastMath
                                .sin(timer.getTimeInSeconds() * 5.0f + 1.0f) *
                                0.5f + 0.5f).
                put(FastMath.sin(timer.getTimeInSeconds() * 5.0f + 2.0f) *
                        0.5f + 0.5f).
                put(FastMath.sin(timer.getTimeInSeconds() * 5.0f + 3.0f) *
                        0.5f + 0.5f);
    }

    private Quad createBrickQuad() {
        GLSLShaderObjectsState so =
                display.getRenderer().createGLSLShaderObjectsState();

        // Check is GLSL is supported on current hardware.
        if (!so.isSupported()) {
            logger.severe("Your graphics card does not support GLSL programs, and thus cannot run this test.");
            quit();
        }

        so.load(TestGLSLShaderAttributes.class.getClassLoader().getResource(
                "jmetest/data/images/attributeshader.vert"),
                TestGLSLShaderAttributes.class.getClassLoader().getResource(
                        "jmetest/data/images/attributeshader.frag"));

        vertexColors = BufferUtils.createFloatBuffer(16);
        so.setAttributePointer("vertexColors", 4, true, 0, vertexColors);

        vertexOffset = BufferUtils.createFloatBuffer(4);
        so.setAttributePointer("vertexOffset", 1, true, 0, vertexOffset);

        so.setEnabled(true);

        //Generate the torus
        Quad box = new Quad("glslQuad", 1f, 1f);
        box.setRenderState(so);

        return box;
    }
}
