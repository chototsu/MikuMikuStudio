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
package com.jme.widget.impl.lwjgl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLU;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.scene.state.TextureState;

/**
 * @author Gregg Patton
 *
 * Extends com.jme.scene.state.TextureState to add
 *   GL.GL_LUMINANCE8_ALPHA8, and GL.GL_LUMINANCE_ALPHA used
 *   by WidgetText.
 *
 * Note: Might be better, if it's possible, to make renderer 
 * specific implementations extendable.
 * 
 */
public class WidgetLWJGLTextureState extends TextureState {
    //OpenGL texture attributes.
    private int[] textureCorrection = { GL.GL_FASTEST, GL.GL_NICEST };

    private int[] textureApply =
        { GL.GL_REPLACE, GL.GL_DECAL, GL.GL_MODULATE, GL.GL_BLEND };

    private int[] textureFilter = { GL.GL_NEAREST, GL.GL_LINEAR };

    private int[] textureMipmap = { GL.GL_NEAREST, // MM_NONE (no mipmap)
        GL.GL_NEAREST,
            GL.GL_LINEAR,
            GL.GL_NEAREST_MIPMAP_NEAREST,
            GL.GL_NEAREST_MIPMAP_LINEAR,
            GL.GL_LINEAR_MIPMAP_NEAREST,
            GL.GL_LINEAR_MIPMAP_LINEAR };

    private int[] imageComponents =
        { GL.GL_RGBA4, GL.GL_RGB8, GL.GL_RGB5_A1, GL.GL_RGBA8, GL.GL_LUMINANCE8_ALPHA8 };

    private int[] imageFormats = { GL.GL_RGBA, GL.GL_RGB, GL.GL_RGBA, GL.GL_RGBA, GL.GL_LUMINANCE_ALPHA };

    /**
     * <code>unset</code> disables texture mapping.
     * @see com.jme.scene.state.RenderState#set()
     */
    public void unset() {
        if(isEnabled()) {
            GL.glDisable(GL.GL_TEXTURE_2D);
        }

    }

    /**
     * <code>set</code> manages the texture being described by the state. If
     * the texture has not been loaded yet, it is generated and loaded using
     * OpenGL. This means the initial pass to set will be longer than subsequent
     * calls.
     * @see com.jme.scene.state.RenderState#unset()
     */
    public void set() {
        if (isEnabled()) {
        
            GL.glEnable(GL.GL_TEXTURE_2D);

            Texture texture = getTexture();
            if (texture == null) {
                GL.glDisable(GL.GL_TEXTURE_2D);
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
                GL.glGenTextures(buf);

                GL.glBindTexture(GL.GL_TEXTURE_2D, buf.get(0));

                texture.setTextureId(buf.get(0));
                
                // pass image data to OpenGL
                Image image =  texture.getImage();
                if (texture.getMipmap() == Texture.MM_NONE) {
                    GL.glTexImage2D(
                        GL.GL_TEXTURE_2D,
                        0,
                        imageComponents[image.getType()],
                        image.getWidth(),
                        image.getHeight(),
                        0,
                        imageFormats[image.getType()],
                        GL.GL_UNSIGNED_BYTE,
                        image.getData());
                } else {
                    GLU.gluBuild2DMipmaps(
                        GL.GL_TEXTURE_2D,
                        imageComponents[image.getType()],
                        image.getWidth(),
                        image.getHeight(),
                        imageFormats[image.getType()],
                        GL.GL_UNSIGNED_BYTE,
                        image.getData());
                }
            } else {
                // texture already exists in OpenGL, just bind it
                GL.glBindTexture(GL.GL_TEXTURE_2D, texture.getTextureId());
            }

            // set up correction mode
            GL.glHint(
                GL.GL_PERSPECTIVE_CORRECTION_HINT,
                textureCorrection[texture.getCorrection()]);

            // set up apply mode
            GL.glTexEnvi(
                GL.GL_TEXTURE_ENV,
                GL.GL_TEXTURE_ENV_MODE,
                textureApply[texture.getApply()]);

            GL.glTexEnv(
                GL.GL_TEXTURE_ENV,
                GL.GL_TEXTURE_ENV_COLOR,
                texture.getBlendColor());

            // set up wrap mode
            switch (texture.getWrap()) {
                case Texture.WM_CLAMP_S_CLAMP_T :
                    GL.glTexParameteri(
                        GL.GL_TEXTURE_2D,
                        GL.GL_TEXTURE_WRAP_S,
                        GL.GL_CLAMP);
                    GL.glTexParameteri(
                        GL.GL_TEXTURE_2D,
                        GL.GL_TEXTURE_WRAP_T,
                        GL.GL_CLAMP);
                    break;
                case Texture.WM_CLAMP_S_WRAP_T :
                    GL.glTexParameteri(
                        GL.GL_TEXTURE_2D,
                        GL.GL_TEXTURE_WRAP_S,
                        GL.GL_CLAMP);
                    GL.glTexParameteri(
                        GL.GL_TEXTURE_2D,
                        GL.GL_TEXTURE_WRAP_T,
                        GL.GL_REPEAT);
                    break;
                case Texture.WM_WRAP_S_CLAMP_T :
                    GL.glTexParameteri(
                        GL.GL_TEXTURE_2D,
                        GL.GL_TEXTURE_WRAP_S,
                        GL.GL_REPEAT);
                    GL.glTexParameteri(
                        GL.GL_TEXTURE_2D,
                        GL.GL_TEXTURE_WRAP_T,
                        GL.GL_CLAMP);
                    break;
                case Texture.WM_WRAP_S_WRAP_T :
                    GL.glTexParameteri(
                        GL.GL_TEXTURE_2D,
                        GL.GL_TEXTURE_WRAP_S,
                        GL.GL_REPEAT);
                    GL.glTexParameteri(
                        GL.GL_TEXTURE_2D,
                        GL.GL_TEXTURE_WRAP_T,
                        GL.GL_REPEAT);
                    break;
            }

            // set up filter mode
            GL.glTexParameteri(
                GL.GL_TEXTURE_2D,
                GL.GL_TEXTURE_MAG_FILTER,
                textureFilter[texture.getFilter()]);

            // set up mipmap mode
            GL.glTexParameteri(
                GL.GL_TEXTURE_2D,
                GL.GL_TEXTURE_MIN_FILTER,
                textureMipmap[texture.getMipmap()]);

        } else {
            GL.glDisable(GL.GL_TEXTURE_2D);
        }

    }

}
