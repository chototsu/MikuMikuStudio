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
package com.jme.image;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.jme.renderer.ColorRGBA;

/**
 * <code>Texture</code> defines a texture object to be used to display an
 * image on a piece of geometry. The image to be displayed is defined by the
 * <code>Image</code> class. All attributes required for texture mapping are
 * contained within this class. This includes mipmapping if desired, filter
 * options, apply options and correction options. Default values are as 
 * follows: mipmap - MM_NONE, filter - FM_NEAREST, wrap - WM_CLAMP_S_CLAMP_T,
 * apply - AM_MODULATE, correction - CM_AFFINE.
 * @see com.jme.image.Image
 * @author Mark Powell
 * @version $Id: Texture.java,v 1.1.1.1 2003-10-29 10:56:18 Anakan Exp $
 */
public class Texture {

    /**
     * Mipmap option for no mipmap.
     */
    public static final int MM_NONE = 0;
    /**
     * Mipmap option to return the value of the texture element that is
     * nearest to the center of the pixel being textured.
     */
    public static final int MM_NEAREST = 1;
    /**
     * Mipmap option to return the weighted average of the four texture
     * elements that are closest to the center of the pixel being textured.
     */
    public static final int MM_LINEAR = 2;
    /**
     * Mipmap option that picks the mipmap that most closely matches the size 
     * of the pixel being textured and uses MM_NEAREST criteria.
     */
    public static final int MM_NEAREST_NEAREST = 3;
    /**
     * Mipmap option that picks the mipmap most closely matches the size of the
     * pixel being textured and uses MM_LINEAR criteria.
     */
    public static final int MM_NEAREST_LINEAR = 4;
    /**
     * Mipmap option that picks the two mipmaps that most closely match the
     * size of the pixel being textured and uses MM_NEAREST criteria.
     */
    public static final int MM_LINEAR_NEAREST = 5;
    /**
     * Mipmap option that picks the two mipmaps that most closely match the
     * size of the pixel being textured and uses MM_LINEAR criteria.
     */
    public static final int MM_LINEAR_LINEAR = 6;
    /**
     * Filter option to return the value of the texture element that is
     * nearest to the center of the pixel being textured.
     */
    public static final int FM_NEAREST = 0;
    /**
     * Filter option to return the weighted average of the four texture
     * elements that are closest to the center of the pixel being textured.
     */
    public static final int FM_LINEAR = 1;

    /**
     * Wrapping modifier that clamps both the S and T values of the texture.
     */
    public static final int WM_CLAMP_S_CLAMP_T = 0;
    /**
     * Wrapping modifier that clamps the S value but wraps the T value of the
     * texture.
     */
    public static final int WM_CLAMP_S_WRAP_T = 1;
    /**
     * Wrapping modifier that wraps the S value but clamps the T value of the
     * texture.
     */
    public static final int WM_WRAP_S_CLAMP_T = 2;
    /**
     * Wrapping modifier that wraps both the S and T values of the texture.
     */
    public static final int WM_WRAP_S_WRAP_T = 3;

    /**
     * Apply modifier that replaces the previous pixel color with the texture
     * color.
     */
    public static final int AM_REPLACE = 0;
    /**
     * Apply modifier that replaces the color values of the pixel but makes 
     * use of the alpha values.
     */
    public static final int AM_DECAL = 1;
    /**
     * Apply modifier multiples the color of the pixel with the texture color.
     */
    public static final int AM_MODULATE = 2;
    /**
     * Apply modifier that combines the color of the pixel with the texture
     * color, such that the final color value is Cv = (1 - Ct) Cf. Where
     * Ct is the color of the texture and Cf is the initial pixel color.
     */
    public static final int AM_BLEND = 3;

    /**
     * Correction modifier makes no color corrections, and is the fastest.
     */
    public static final int CM_AFFINE = 0;
    /**
     * Correction modifier makes color corrections based on perspective and
     * is slower than CM_AFFINE.
     */
    public static final int CM_PERSPECTIVE = 1;

    //texture attributes.
    private Image image;
    private FloatBuffer blendColorBuffer;

    private int mipmapState;
    private int textureId;
    private int correction;
    private int apply;
    private int wrap;
    private int filter;

    /**
     * Constructor instantiates a new <code>Texture</code> object with 
     * default attributes.
     *
     */
    public Texture() {
        mipmapState = MM_NONE;
        filter = FM_NEAREST;
        apply = AM_MODULATE;
        correction = CM_AFFINE;
        wrap = WM_CLAMP_S_CLAMP_T;
    }

    /**
     * <code>getBlendColorBuffer</code> returns the buffer that contains
     * the color values that are used to tint the texture.
     * @return the buffer that contains the texture tint color.
     */
    public FloatBuffer getBlendColorBuffer() {
        return blendColorBuffer;
    }

    /**
     * <code>setBlendColorBuffer</code> sets the buffer that contains the
     * color values that are used to tint the texture.
     * @param blendColorBuffer the buffer that contains the texture tint color.
     */
    public void setBlendColorBuffer(FloatBuffer blendColorBuffer) {
        this.blendColorBuffer = blendColorBuffer;
    }

    /**
     * 
     * <code>setBlendColor</code> sets the color to be used to tint the 
     * texture. This color is used to create the new blend color buffer.
     * @param color the color of the texture tint.
     */
    public void setBlendColor(ColorRGBA color) {
        blendColorBuffer =
            ByteBuffer
                .allocateDirect(16)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        float[] colorArray = { color.r, color.g, color.b, color.a };
        blendColorBuffer.put(colorArray);
        blendColorBuffer.flip();
    }

    /**
     * <code>getMipmapState</code> returns the current mipmap state of this
     * texture.
     * @return the mipmap state of this texture.
     */
    public int getMipmapState() {
        return mipmapState;
    }

    /**
     * <code>setMipmapState</code> sets the mipmap state for this texture. 
     * If the state is invalid it is set to MM_NONE.
     * @param mipmapState the new mipmap state.
     */
    public void setMipmapState(int mipmapState) {
        if(mipmapState < 0 || mipmapState > 6) {
            mipmapState = MM_NONE;
        }
        this.mipmapState = mipmapState;
    }

    /**
     * <code>setApply</code> sets the apply mode for this texture. If an
     * invalid value is passed, it is set to AM_MODULATE;
     * @param apply the apply mode for this texture.
     */
    public void setApply(int apply) {
        if(apply < 0 || apply > 3) {
            apply = AM_MODULATE;
        }
        this.apply = apply;
    }

    /**
     * <code>setCorrection</code> sets the image correction mode for this
     * texture. If an invalid value is passed, it is set to CM_AFFINE.
     * @param correction the correction mode for this texture.
     */
    public void setCorrection(int correction) {
        if(correction < 0 || correction > 2) {
            correction = CM_AFFINE;
        }
        this.correction = correction;
    }

    /**
     * <code>setFilter</code> sets the texture filter mode for this 
     * texture. If an invalid value is passed, it is set to FM_NEAREST.
     * @param filter the filter mode for this texture.
     */
    public void setFilter(int filter) {
        if(filter < 0 || filter > 1) {
            filter = FM_NEAREST;
        }
        this.filter = filter;
    }

    /**
     * <code>setImage</code> sets the image object that defines the texture.
     * @param image the image that defines the texture.
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * <code>setWrap</code> sets the wrap mode of this texture. If an invalid
     * value is passed, it is set to WM_CLAMP_S_CLAMP_T.
     * @param wrap the wrap mode for this texture.
     */
    public void setWrap(int wrap) {
        if(wrap < 0 || wrap > 3) {
            wrap = WM_CLAMP_S_CLAMP_T;
        }
        this.wrap = wrap;
    }

    /**
     * <code>getTextureId</code> returns the texture id of this texture. This
     * id is required to be unique to any other texture objects running in the
     * same JVM. However, no guarantees are made that it will be unique, and as
     * such, the user is responsible for this.
     * @return the id of the texture.
     */
    public int getTextureId() {
        return textureId;
    }

    /**
     * <code>setTextureId</code> returns the texture id assigned to this 
     * texture. If no id has been set, zero is returned.
     * @param textureId the texture id of this texture.
     */
    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    /**
     * 
     * <code>getImage</code> returns the image data that makes up this texture.
     * If no image data has been set, this will return null.
     * @return the image data that makes up the texture.
     */
    public Image getImage() {
        return image;
    }

    /**
     * 
     * <code>getMipmap</code> returns the mipmap mode for the texture.
     * @return the mipmap mode of the texture.
     */
    public int getMipmap() {
        return mipmapState;
    }

    /**
     * <code>getCorrection</code> returns the correction mode for the 
     * texture. 
     * @return the correction mode for the texture.
     */
    public int getCorrection() {
        return correction;
    }

    /**
     * <code>getApply</code> returns the apply mode for the texture.
     * @return the apply mode of the texture.
     */
    public int getApply() {
        return apply;
    }

    /**
     * <code>getBlendColor</code> returns the buffer that contains the
     * texture's tint color.
     * @return the buffer that contains the texture's tint color.
     */
    public FloatBuffer getBlendColor() {
        return blendColorBuffer;
    }

    /**
     * <code>getWrap</code> returns the wrap mode for the texture.
     * @return the wrap mode of the texture.
     */
    public int getWrap() {
        return wrap;
    }

    /**
     * <code>getFilter</code> returns the filter mode for the texture.
     * @return the filter mode of the texture.
     */
    public int getFilter() {
        return filter;
    }
}
