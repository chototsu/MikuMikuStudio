/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
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
 *
 */
package com.jme.scene.state.lwjgl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import com.jme.light.DirectionalLight;
import com.jme.light.Light;
import com.jme.light.PointLight;
import com.jme.light.SpotLight;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import java.util.Stack;
import com.jme.scene.Spatial;

/**
 * <code>LWJGLLightState</code> subclasses the Light class using the LWJGL
 * API to access OpenGL for light processing.
 * @author Mark Powell
 * @version $Id: LWJGLLightState.java,v 1.4 2004-04-16 17:50:28 renanse Exp $
 */
public class LWJGLLightState extends LightState {
    //buffer for light colors.
    private FloatBuffer buffer;
    private float[] ambient = { 0.0f, 0.0f, 0.0f, 1.0f };;
    private float[] color;
    private float[] posParam = new float[4];
    private float[] spotDir = new float[4];
    private float[] defaultDirection = new float[4];

    /**
     * Constructor instantiates a new <code>LWJGLLightState</code>.
     *
     */
    public LWJGLLightState() {
        super();
        buffer =
            ByteBuffer
                .allocateDirect(16*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        color = new float[4];
        color[3] = 1.0f;
    }

    /**
     * <code>set</code> iterates over the light queue and processes each
     * individual light.
     * @see com.jme.scene.state.RenderState#set()
     */
    public void apply() {
        int quantity = getQuantity();
        ambient[0] = 0;
        ambient[1] = 0;
        ambient[2] = 0;
        ambient[3] = 1;

        color[0] = 0;
        color[1] = 0;
        color[2] = 0;
        color[3] = 1;

        if (quantity > 0 && isEnabled()) {
            GL11.glEnable(GL11.GL_LIGHTING);

            if(twoSidedOn) {
                GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_TRUE);
            }

            for (int i = 0; i < quantity; i++) {
              int index = GL11.GL_LIGHT0 + i;

                Light light = get(i);
                if (light.isEnabled()) {

                    GL11.glEnable(index);

                    color[0] = light.getAmbient().r;
                    color[1] = light.getAmbient().g;
                    color[2] = light.getAmbient().b;

                    buffer.clear();
                    buffer.put(color);
                    buffer.flip();

                    GL11.glLight(index, GL11.GL_AMBIENT, buffer);

                    color[0] = light.getDiffuse().r;
                    color[1] = light.getDiffuse().g;
                    color[2] = light.getDiffuse().b;

                    buffer.clear();
                    buffer.put(color);
                    buffer.flip();

                    GL11.glLight(index, GL11.GL_DIFFUSE, buffer);

                    color[0] = light.getSpecular().r;
                    color[1] = light.getSpecular().g;
                    color[2] = light.getSpecular().b;

                    buffer.clear();
                    buffer.put(color);
                    buffer.flip();

                    GL11.glLight(index, GL11.GL_SPECULAR, buffer);

                    if (light.isAttenuate()) {
                        GL11.glLightf(
                            index,
                            GL11.GL_CONSTANT_ATTENUATION,
                            light.getConstant());
                        GL11.glLightf(
                            index,
                            GL11.GL_LINEAR_ATTENUATION,
                            light.getLinear());
                        GL11.glLightf(
                            index,
                            GL11.GL_QUADRATIC_ATTENUATION,
                            light.getQuadratic());
                    } else {
                        GL11.glLightf(index, GL11.GL_CONSTANT_ATTENUATION, 1.0f);
                        GL11.glLightf(index, GL11.GL_LINEAR_ATTENUATION, 0.0f);
                        GL11.glLightf(index, GL11.GL_QUADRATIC_ATTENUATION, 0.0f);
                    }

                    if (light.getType() == Light.LT_AMBIENT) {
                        ambient[0] += light.getAmbient().r;
                        ambient[1] += light.getAmbient().g;
                        ambient[2] += light.getAmbient().b;
                    }


                    switch (light.getType()) {
                        case Light.LT_DIRECTIONAL :
                            {
                                DirectionalLight pkDL =
                                    (DirectionalLight) light;
                                posParam[0] = -pkDL.getDirection().x;
                                posParam[1] = -pkDL.getDirection().y;
                                posParam[2] = -pkDL.getDirection().z;
                                posParam[3] = 0.0f;

                                buffer.clear();
                                buffer.put(posParam);
                                buffer.flip();
                                GL11.glLight(index, GL11.GL_POSITION, buffer);
                                break;
                            }
                        case Light.LT_POINT :
                        case Light.LT_SPOT :
                            {
                                PointLight pointLight = (PointLight) light;
                                posParam[0] = pointLight.getLocation().x;
                                posParam[1] = pointLight.getLocation().y;
                                posParam[2] = pointLight.getLocation().z;
                                posParam[3] = 1.0f;
                                buffer.clear();
                                buffer.put(posParam);
                                buffer.flip();
                                GL11.glLight(index, GL11.GL_POSITION, buffer);
                                break;
                            }
                    }

                    if (light.getType() == Light.LT_SPOT) {
                        SpotLight spot = (SpotLight) light;
                        GL11.glLightf(
                            index,
                            GL11.GL_SPOT_CUTOFF,
                            spot.getAngle());
                        buffer.clear();
                        spotDir[0]= spot.getDirection().x;
                        spotDir[1]= spot.getDirection().y;
                        spotDir[2]= spot.getDirection().z;
                        buffer.put(spotDir);
                        buffer.flip();
                        GL11.glLight(index, GL11.GL_SPOT_DIRECTION, buffer);
                        GL11.glLightf(
                            index,
                            GL11.GL_SPOT_EXPONENT,
                            spot.getExponent());
                    } else {
                        defaultDirection[0] = 0.0f;
                        defaultDirection[1] = 0.0f;
                        defaultDirection[2] = -1.0f;
                        GL11.glLightf(index, GL11.GL_SPOT_CUTOFF, 180.0f);
                        buffer.clear();
                        buffer.put(defaultDirection);
                        buffer.flip();
                        GL11.glLight(index, GL11.GL_SPOT_DIRECTION, buffer);
                        GL11.glLightf(index, GL11.GL_SPOT_EXPONENT, 0.0f);
                    }
                } else {
                    GL11.glDisable(index);
                }
            }

            buffer.clear();
            buffer.put(ambient);
            buffer.flip();
            GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, buffer);

            for (int i = quantity; i < MAX_LIGHTS_ALLOWED; i++)
                GL11.glDisable((GL11.GL_LIGHT0 + i));
        } else {
            GL11.glDisable(GL11.GL_LIGHTING);
        }

    }

    public RenderState extract(Stack stack, Spatial spat) {
      int mode = spat.getLightCombineMode();
      if (mode == REPLACE) return (LWJGLLightState) stack.peek();

      // accumulate the lights in the stack into a single LightState object
      LWJGLLightState newLState = new LWJGLLightState();
      Object states[] = stack.toArray();
      boolean foundEnabled = false;
      switch (mode) {
        case COMBINE_CLOSEST:
        case COMBINE_RECENT_ENABLED:
          for (int iIndex = states.length-1; iIndex >= 0; iIndex--) {
            LWJGLLightState pkLState = (LWJGLLightState) states[iIndex];
            if (!pkLState.isEnabled()) {
              if (mode == COMBINE_RECENT_ENABLED) break;
              else continue;
            } else foundEnabled = true;
            if (pkLState.twoSidedOn) newLState.setTwoSidedLighting(true);
            for (int i = 0, maxL = pkLState.getQuantity(); i < maxL; i++) {
              Light pkLight = pkLState.get(i);
              if (pkLight != null) {
                newLState.attach(pkLight);
              }
            }
          }
          break;
        case COMBINE_FIRST:
          for (int iIndex = 0, max = states.length; iIndex < max; iIndex++) {
            LWJGLLightState pkLState = (LWJGLLightState) states[iIndex];
            if (!pkLState.isEnabled()) continue;
            else foundEnabled = true;
            if (pkLState.twoSidedOn) newLState.setTwoSidedLighting(true);
            for (int i = 0, maxL = pkLState.getQuantity(); i < maxL; i++) {
              Light pkLight = pkLState.get(i);
              if (pkLight != null) {
                newLState.attach(pkLight);
              }
            }
          }
          break;
      }
      newLState.setEnabled(foundEnabled);
      return newLState;
    }
}
