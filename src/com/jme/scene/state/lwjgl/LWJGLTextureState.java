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
import java.nio.IntBuffer;
import java.util.logging.Level;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.glu.GLU;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.scene.state.TextureState;
import com.jme.util.LoggingSystem;
import com.jme.scene.state.RenderState;
import java.util.Stack;
import com.jme.scene.Spatial;

/**
 * <code>LWJGLTextureState</code> subclasses the TextureState object using
 * the LWJGL API to access OpenGL for texture processing.
 * @author Mark Powell
 * @version $Id: LWJGLTextureState.java,v 1.3 2004-04-16 17:12:53 renanse Exp $
 */
public class LWJGLTextureState extends TextureState {
    //OpenGL texture attributes.
    private int[] textureCorrection = { GL11.GL_FASTEST, GL11.GL_NICEST };

    private int[] textureApply =
        { GL11.GL_REPLACE, GL11.GL_DECAL, GL11.GL_MODULATE, GL11.GL_BLEND, GL13.GL_COMBINE };

    private int[] textureFilter = { GL11.GL_NEAREST, GL11.GL_LINEAR };

    private int[] textureMipmap = { GL11.GL_NEAREST, // MM_NONE (no mipmap)
        GL11.GL_NEAREST,
            GL11.GL_LINEAR,
            GL11.GL_NEAREST_MIPMAP_NEAREST,
            GL11.GL_NEAREST_MIPMAP_LINEAR,
            GL11.GL_LINEAR_MIPMAP_NEAREST,
            GL11.GL_LINEAR_MIPMAP_LINEAR };

    private int[] textureCombineFunc = { GL11.GL_REPLACE,
            GL11.GL_MODULATE,
            GL11.GL_ADD,
            GL13.GL_ADD_SIGNED,
            GL13.GL_SUBTRACT,
            GL13.GL_INTERPOLATE };

    private int[] textureCombineSrc = { GL11.GL_TEXTURE,
            GL13.GL_PRIMARY_COLOR,
            GL13.GL_CONSTANT,
            GL13.GL_PREVIOUS};

    private int[] textureCombineOpRgb = { GL11.GL_SRC_COLOR,
            GL11.GL_ONE_MINUS_SRC_COLOR};

    private int[] textureCombineOpAlpha = { GL11.GL_SRC_ALPHA,
            GL11.GL_ONE_MINUS_SRC_ALPHA };

    private float[] textureCombineScale = { 1.0f,2.0f,4.0f };

    private int[] imageComponents =
        {
            GL11.GL_RGBA4,
            GL11.GL_RGB8,
            GL11.GL_RGB5_A1,
            GL11.GL_RGBA8,
            GL11.GL_LUMINANCE8_ALPHA8 };

    private int[] imageFormats =
        {
            GL11.GL_RGBA,
            GL11.GL_RGB,
            GL11.GL_RGBA,
            GL11.GL_RGBA,
            GL11.GL_LUMINANCE_ALPHA };

    private static int numTexUnits = 0;

    /**
     * Constructor instantiates a new <code>LWJGLTextureState</code> object.
     * The number of textures that can be combined is determined during
     * construction. This equates the number of texture units supported by
     * the graphics card.
     *
     */
    public LWJGLTextureState() {
        super();
        if (numTexUnits == 0) {
          IntBuffer buf = ByteBuffer
              .allocateDirect(64)
              .order(ByteOrder.nativeOrder())
              .asIntBuffer();
          GL11.glGetInteger(GL13.GL_MAX_TEXTURE_UNITS, buf);

          numTexUnits = buf.get(0);
        }
        texture = new Texture[numTexUnits];
    }

    /**
     * <code>set</code> manages the textures being described by the state. If
     * the texture has not been loaded yet, it is generated and loaded using
     * OpenGL11. This means the initial pass to set will be longer than subsequent
     * calls. The multitexture extension is used to define the multiple texture
     * states, with the number of units being determined at construction time.
     * @see com.jme.scene.state.RenderState#unset()
     */
    public void apply() {
        if (isEnabled()) {
            for (int i = 0; i < getNumberOfUnits(); i++) {
                if (getTexture(i) == null) {
                    continue;
                }
                GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);

                GL11.glEnable(GL11.GL_TEXTURE_2D);

                Texture texture = getTexture(i);
                if (texture == null) {
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    return;
                }

                //texture not yet loaded.
                if (texture.getTextureId() == 0) {
                    // Create A IntBuffer For Image Address In Memory
                    IntBuffer buf =
                        ByteBuffer
                            .allocateDirect(4)
                            .order(ByteOrder.nativeOrder())
                            .asIntBuffer();

                    //Create the texture
                    GL11.glGenTextures(buf);

                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, buf.get(0));

                    texture.setTextureId(buf.get(0));

                    // pass image data to OpenGL
                    Image image = texture.getImage();
                    if (image == null) {
                        LoggingSystem.getLogger().log(
                            Level.WARNING,
                            "Image data for texture is null.");
                        texture.setTextureId(-1);
                        return;
                    }
                    if (texture.getMipmap() == Texture.MM_NONE) {
                        GL11.glTexImage2D(
                            GL11.GL_TEXTURE_2D,
                            0,
                            imageComponents[image.getType()],
                            image.getWidth(),
                            image.getHeight(),
                            0,
                            imageFormats[image.getType()],
                            GL11.GL_UNSIGNED_BYTE,
                            image.getData());
                    } else {
                        GLU.gluBuild2DMipmaps(
                            GL11.GL_TEXTURE_2D,
                            imageComponents[image.getType()],
                            image.getWidth(),
                            image.getHeight(),
                            imageFormats[image.getType()],
                            GL11.GL_UNSIGNED_BYTE,
                            image.getData());
                    }
                } else {
                    // texture already exists in OpenGL, just bind it
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureId());
                }

                // set up correction mode
                GL11.glHint(
                    GL11.GL_PERSPECTIVE_CORRECTION_HINT,
                    textureCorrection[texture.getCorrection()]);

                if(texture.getApply() == Texture.AM_COMBINE) {
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
                            GL11.GL_TEXTURE_ENV_MODE,
                            textureApply[texture.getApply()]);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_RGB,
                            textureCombineFunc[texture.getCombineFuncRGB()]);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_ALPHA,
                            textureCombineFunc[texture.getCombineFuncAlpha()]);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_RGB,
                            textureCombineSrc[texture.getCombineSrc0RGB()]);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE1_RGB,
                            textureCombineSrc[texture.getCombineSrc1RGB()]);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE2_RGB,
                            textureCombineSrc[texture.getCombineSrc2RGB()]);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_ALPHA,
                            textureCombineSrc[texture.getCombineSrc0Alpha()]);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE1_ALPHA,
                            textureCombineSrc[texture.getCombineSrc1Alpha()]);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE2_ALPHA,
                            textureCombineSrc[texture.getCombineSrc2Alpha()]);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_RGB,
                            textureCombineOpRgb[texture.getCombineOp0RGB()]);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_RGB,
                            textureCombineOpRgb[texture.getCombineOp1RGB()]);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND2_RGB,
                            textureCombineOpRgb[texture.getCombineOp2RGB()]);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_ALPHA,
                            textureCombineOpAlpha[texture.getCombineOp0Alpha()]);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_ALPHA,
                            textureCombineOpAlpha[texture.getCombineOp1Alpha()]);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND2_ALPHA,
                            textureCombineOpAlpha[texture.getCombineOp2Alpha()]);
                    GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL13.GL_RGB_SCALE,
                            textureCombineScale[texture.getCombineScaleRGB()]);
                    GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_ALPHA_SCALE,
                            textureCombineScale[texture.getCombineScaleRGB()]);

                } else {
                    // set up apply mode
                    GL11.glTexEnvi(
                        GL11.GL_TEXTURE_ENV,
                        GL11.GL_TEXTURE_ENV_MODE,
                        textureApply[texture.getApply()]);
                }

                GL11.glTexEnv(
                    GL11.GL_TEXTURE_ENV,
                    GL11.GL_TEXTURE_ENV_COLOR,
                    texture.getBlendColor());

                // set up wrap mode
                switch (texture.getWrap()) {
                    case Texture.WM_CLAMP_S_CLAMP_T :
                        GL11.glTexParameteri(
                            GL11.GL_TEXTURE_2D,
                            GL11.GL_TEXTURE_WRAP_S,
                            GL11.GL_CLAMP);
                        GL11.glTexParameteri(
                            GL11.GL_TEXTURE_2D,
                            GL11.GL_TEXTURE_WRAP_T,
                            GL11.GL_CLAMP);
                        break;
                    case Texture.WM_CLAMP_S_WRAP_T :
                        GL11.glTexParameteri(
                            GL11.GL_TEXTURE_2D,
                            GL11.GL_TEXTURE_WRAP_S,
                            GL11.GL_CLAMP);
                        GL11.glTexParameteri(
                            GL11.GL_TEXTURE_2D,
                            GL11.GL_TEXTURE_WRAP_T,
                            GL11.GL_REPEAT);
                        break;
                    case Texture.WM_WRAP_S_CLAMP_T :
                        GL11.glTexParameteri(
                            GL11.GL_TEXTURE_2D,
                            GL11.GL_TEXTURE_WRAP_S,
                            GL11.GL_REPEAT);
                        GL11.glTexParameteri(
                            GL11.GL_TEXTURE_2D,
                            GL11.GL_TEXTURE_WRAP_T,
                            GL11.GL_CLAMP);
                        break;
                    case Texture.WM_WRAP_S_WRAP_T :
                        GL11.glTexParameteri(
                            GL11.GL_TEXTURE_2D,
                            GL11.GL_TEXTURE_WRAP_S,
                            GL11.GL_REPEAT);
                        GL11.glTexParameteri(
                            GL11.GL_TEXTURE_2D,
                            GL11.GL_TEXTURE_WRAP_T,
                            GL11.GL_REPEAT);
                        break;
                }

                // set up filter mode
                GL11.glTexParameteri(
                    GL11.GL_TEXTURE_2D,
                    GL11.GL_TEXTURE_MAG_FILTER,
                    textureFilter[texture.getFilter()]);

                // set up mipmap mode
                GL11.glTexParameteri(
                    GL11.GL_TEXTURE_2D,
                    GL11.GL_TEXTURE_MIN_FILTER,
                    textureMipmap[texture.getMipmap()]);

            }
        } else {
          for (int i = 0; i < getNumberOfUnits(); i++) {
              GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
              GL11.glDisable(GL11.GL_TEXTURE_2D);
          }
        }
    }

    public RenderState extract(Stack stack, Spatial spat) {
      int mode = spat.getLightCombineMode();
      if (mode == REPLACE) return (LWJGLLightState) stack.peek();

      // accumulate the lights in the stack into a single LightState object
      LWJGLTextureState newTState = new LWJGLTextureState();
      newTState.setEnabled(true);
      Object states[] = stack.toArray();
      switch (mode) {
        case COMBINE_CLOSEST:
        case COMBINE_RECENT_ENABLED:
          for (int iIndex = states.length-1; iIndex >= 0; iIndex--) {
            TextureState pkTState = (TextureState) states[iIndex];
            if (!pkTState.isEnabled()) {
              if (mode == COMBINE_RECENT_ENABLED) break;
              else continue;
            }
            for (int i = 0, maxT = pkTState.getNumberOfUnits(); i < maxT; i++) {
              Texture pkText = pkTState.getTexture(i);
              if (newTState.getTexture(i) == null) {
                newTState.setTexture(pkText, i);
              }
            }
          }
          break;
        case COMBINE_FIRST:
          for (int iIndex = 0, max = states.length; iIndex < max; iIndex++) {
            TextureState pkTState = (TextureState) states[iIndex];
            if (!pkTState.isEnabled()) continue;
            for (int i = 0, maxT = pkTState.getNumberOfUnits(); i < maxT; i++) {
              Texture pkText = pkTState.getTexture(i);
              if (newTState.getTexture(i) == null) {
                newTState.setTexture(pkText, i);
              }
            }
          }
          break;
      }
      return newTState;
    }
}
