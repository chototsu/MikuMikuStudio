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
 * @version $Id: Texture.java,v 1.18 2004-10-07 19:27:16 renanse Exp $
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
   * Wrapping modifier that clamps both the S and T values of the texture.  Uses CLAMP_TO_EDGE.
   */
  public static final int WM_ECLAMP_S_ECLAMP_T = 4;

  /**
   * Wrapping modifier that clamps both the S and T values of the texture.  Uses CLAMP_TO_BORDER.
   */
  public static final int WM_BCLAMP_S_BCLAMP_T = 5;

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
   * Apply modifier combines two textures.
   */
  public static final int AM_COMBINE = 4;

  /**
   * Correction modifier makes no color corrections, and is the fastest.
   */
  public static final int CM_AFFINE = 0;

  /**
   * Correction modifier makes color corrections based on perspective and
   * is slower than CM_AFFINE.
   */
  public static final int CM_PERSPECTIVE = 1;

  public static final int ACF_REPLACE = 0;
  public static final int ACF_MODULATE = 1;
  public static final int ACF_ADD = 2;
  public static final int ACF_ADD_SIGNED = 3;
  public static final int ACF_SUBTRACT = 4;
  public static final int ACF_INTERPOLATE = 5;
  public static final int ACF_DOT3_RGB = 6;
  public static final int ACF_DOT3_RGBA = 7;

  public static final int ACS_TEXTURE = 0;
  public static final int ACS_PRIMARY_COLOR = 1;
  public static final int ACS_CONSTANT = 2;
  public static final int ACS_PREVIOUS = 3;

  public static final int ACO_SRC_COLOR = 0;
  public static final int ACO_ONE_MINUS_SRC_COLOR = 1;
  public static final int ACO_SRC_ALPHA = 2;
  public static final int ACO_ONE_MINUS_SRC_ALPHA = 3;

  public static final int ACSC_ONE = 0;
  public static final int ACSC_TWO = 1;
  public static final int ACSC_FOUR = 2;

  public static final int EM_NONE = 0;
  public static final int EM_IGNORE = 1;
  public static final int EM_SPHERE = 2;

  // Optional String to point to where this texture is located
  private String imageLocation;

  //texture attributes.
  private Image image;
  private FloatBuffer blendColorBuffer;

  private float anisoLevel = 1.0f;

  private int mipmapState;
  private int textureId;
  private int correction;
  private int apply;
  private int wrap;
  private int filter;
  private int envMapMode;

  //only used if combine apply mode on
  private int combineFuncRGB;
  private int combineFuncAlpha;
  private int combineSrc0RGB;
  private int combineSrc1RGB;
  private int combineSrc2RGB;
  private int combineSrc0Alpha;
  private int combineSrc1Alpha;
  private int combineSrc2Alpha;
  private int combineOp0RGB;
  private int combineOp1RGB;
  private int combineOp2RGB;
  private int combineOp0Alpha;
  private int combineOp1Alpha;
  private int combineOp2Alpha;
  private float combineScaleRGB;
  private float combineScaleAlpha;

  private boolean needsFilterRefresh = true;
  private boolean needsWrapRefresh = false;

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
    wrap = WM_ECLAMP_S_ECLAMP_T;
    setBlendColor(new ColorRGBA(0, 0, 0, 0));
    combineScaleRGB = 1.0f;
    combineScaleAlpha = 1.0f;
  }

  /**
   * Constructor instantiates a new <code>Texture</code> object with
   * given attributes.
   *
   */
  public Texture(float aniso) {
    this.anisoLevel = aniso;

    mipmapState = MM_NONE;
    filter = FM_NEAREST;
    apply = AM_MODULATE;
    correction = CM_AFFINE;
    wrap = WM_ECLAMP_S_ECLAMP_T;
    setBlendColor(new ColorRGBA(0, 0, 0, 0));
    combineScaleRGB = 1.0f;
    combineScaleAlpha = 1.0f;
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
    float[] colorArray = {color.r, color.g, color.b, color.a};
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
    if (mipmapState < 0 || mipmapState > 6) {
      mipmapState = MM_NONE;
    }
    this.mipmapState = mipmapState;
    needsFilterRefresh = true;
  }

  /**
   * <code>setApply</code> sets the apply mode for this texture. If an
   * invalid value is passed, it is set to AM_MODULATE;
   * @param apply the apply mode for this texture.
   */
  public void setApply(int apply) {
    if (apply < 0 || apply > 4) {
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
    if (correction < 0 || correction > 2) {
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
    if (filter < 0 || filter > 1) {
      filter = FM_NEAREST;
    }
    this.filter = filter;
    needsFilterRefresh = true;
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
   * value is passed, it is set to WM_ECLAMP_S_ECLAMP_T.
   * @param wrap the wrap mode for this texture.
   */
  public void setWrap(int wrap) {
    if (wrap < 0 || wrap > 5) {
      wrap = WM_ECLAMP_S_ECLAMP_T;
    }
    this.wrap = wrap;
    needsWrapRefresh = true;
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
   * <code>setTextureId</code> sets the texture id for this
   * texture. Zero means no id is set.
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

  /**
   * @return Returns the combineFuncRGB.
   */
  public int getCombineFuncRGB() {
    return combineFuncRGB;
  }

  /**
   * @param combineFuncRGB The combineFuncRGB to set.
   */
  public void setCombineFuncRGB(int combineFuncRGB) {
    this.combineFuncRGB = combineFuncRGB;
  }

  /**
   * @return Returns the combineOp0Alpha.
   */
  public int getCombineOp0Alpha() {
    return combineOp0Alpha;
  }

  /**
   * @param combineOp0Alpha The combineOp0Alpha to set.
   */
  public void setCombineOp0Alpha(int combineOp0Alpha) {
    this.combineOp0Alpha = combineOp0Alpha;
  }

  /**
   * @return Returns the combineOp0RGB.
   */
  public int getCombineOp0RGB() {
    return combineOp0RGB;
  }

  /**
   * @param combineOp0RGB The combineOp0RGB to set.
   */
  public void setCombineOp0RGB(int combineOp0RGB) {
    this.combineOp0RGB = combineOp0RGB;
  }

  /**
   * @return Returns the combineOp1Alpha.
   */
  public int getCombineOp1Alpha() {
    return combineOp1Alpha;
  }

  /**
   * @param combineOp1Alpha The combineOp1Alpha to set.
   */
  public void setCombineOp1Alpha(int combineOp1Alpha) {
    this.combineOp1Alpha = combineOp1Alpha;
  }

  /**
   * @return Returns the combineOp1RGB.
   */
  public int getCombineOp1RGB() {
    return combineOp1RGB;
  }

  /**
   * @param combineOp1RGB The combineOp1RGB to set.
   */
  public void setCombineOp1RGB(int combineOp1RGB) {
    this.combineOp1RGB = combineOp1RGB;
  }

  /**
   * @return Returns the combineOp2Alpha.
   */
  public int getCombineOp2Alpha() {
    return combineOp2Alpha;
  }

  /**
   * @param combineOp2Alpha The combineOp2Alpha to set.
   */
  public void setCombineOp2Alpha(int combineOp2Alpha) {
    this.combineOp2Alpha = combineOp2Alpha;
  }

  /**
   * @return Returns the combineOp2RGB.
   */
  public int getCombineOp2RGB() {
    return combineOp2RGB;
  }

  /**
   * @param combineOp2RGB The combineOp2RGB to set.
   */
  public void setCombineOp2RGB(int combineOp2RGB) {
    this.combineOp2RGB = combineOp2RGB;
  }

  /**
   * @return Returns the combineScaleAlpha.
   */
  public float getCombineScaleAlpha() {
    return combineScaleAlpha;
  }

  /**
   * @param combineScaleAlpha The combineScaleAlpha to set.
   */
  public void setCombineScaleAlpha(float combineScaleAlpha) {
    this.combineScaleAlpha = combineScaleAlpha;
  }

  /**
   * @return Returns the combineScaleRGB.
   */
  public float getCombineScaleRGB() {
    return combineScaleRGB;
  }

  /**
   * @param combineScaleRGB The combineScaleRGB to set.
   */
  public void setCombineScaleRGB(float combineScaleRGB) {
    this.combineScaleRGB = combineScaleRGB;
  }

  /**
   * @return Returns the combineSrc0Alpha.
   */
  public int getCombineSrc0Alpha() {
    return combineSrc0Alpha;
  }

  /**
   * @param combineSrc0Alpha The combineSrc0Alpha to set.
   */
  public void setCombineSrc0Alpha(int combineSrc0Alpha) {
    this.combineSrc0Alpha = combineSrc0Alpha;
  }

  /**
   * @return Returns the combineSrc0RGB.
   */
  public int getCombineSrc0RGB() {
    return combineSrc0RGB;
  }

  /**
   * @param combineSrc0RGB The combineSrc0RGB to set.
   */
  public void setCombineSrc0RGB(int combineSrc0RGB) {
    this.combineSrc0RGB = combineSrc0RGB;
  }

  /**
   * @return Returns the combineSrc1Alpha.
   */
  public int getCombineSrc1Alpha() {
    return combineSrc1Alpha;
  }

  /**
   * @param combineSrc1Alpha The combineSrc1Alpha to set.
   */
  public void setCombineSrc1Alpha(int combineSrc1Alpha) {
    this.combineSrc1Alpha = combineSrc1Alpha;
  }

  /**
   * @return Returns the combineSrc1RGB.
   */
  public int getCombineSrc1RGB() {
    return combineSrc1RGB;
  }

  /**
   * @param combineSrc1RGB The combineSrc1RGB to set.
   */
  public void setCombineSrc1RGB(int combineSrc1RGB) {
    this.combineSrc1RGB = combineSrc1RGB;
  }

  /**
   * @return Returns the combineSrc2Alpha.
   */
  public int getCombineSrc2Alpha() {
    return combineSrc2Alpha;
  }

  /**
   * @param combineSrc2Alpha The combineSrc2Alpha to set.
   */
  public void setCombineSrc2Alpha(int combineSrc2Alpha) {
    this.combineSrc2Alpha = combineSrc2Alpha;
  }

  /**
   * @return Returns the combineSrc2RGB.
   */
  public int getCombineSrc2RGB() {
    return combineSrc2RGB;
  }

  /**
   * @param combineSrc2RGB The combineSrc2RGB to set.
   */
  public void setCombineSrc2RGB(int combineSrc2RGB) {
    this.combineSrc2RGB = combineSrc2RGB;
  }

  /**
   * @return Returns the combineFuncAlpha.
   */
  public int getCombineFuncAlpha() {
    return combineFuncAlpha;
  }

  /**
   * @param combineFuncAlpha The combineFuncAlpha to set.
   */
  public void setCombineFuncAlpha(int combineFuncAlpha) {
    this.combineFuncAlpha = combineFuncAlpha;
  }

  public void setEnvironmentalMapMode(int envMapMode) {
    this.envMapMode = envMapMode;
  }

  public int getEnvironmentalMapMode() {
    return envMapMode;
  }

  public String toString() {
    return "Texture with id: " + textureId;
  }

  public String getImageLocation() {
    return imageLocation;
  }

  public void setImageLocation(String imageLocation) {
    this.imageLocation = imageLocation;
  }

  public float getAnisoLevel() {
    return anisoLevel;
  }

  /**
   *
   * @param level float
   * @deprecated Must set aniso level when loading texture from manager.
   */
  public void setAnisoLevel(float level) {
    anisoLevel = level;
  }

  public void setNeedsFilterRefresh(boolean needed) {
    needsFilterRefresh = needed;
  }

  public boolean needsFilterRefresh() {
    return needsFilterRefresh;
  }

  public void setNeedsWrapRefresh(boolean needed) {
    needsWrapRefresh = needed;
  }

  public boolean needsWrapRefresh() {
    return needsWrapRefresh;
  }

  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (! (other instanceof Texture)) {
      return false;
    }
    Texture that = (Texture) other;
    if (this.textureId != that.textureId)return false;
    if (this.textureId == 0) {
      if (this.getImage() != null && !this.getImage().equals(that.getImage()))return false;
      if (this.getImage() == null && that.getImage() != null)return false;
      if (this.getAnisoLevel() != that.getAnisoLevel())return false;
      if (this.getApply() != that.getApply())return false;
      if (this.getCombineFuncAlpha() != that.getCombineFuncAlpha())return false;
      if (this.getCombineFuncRGB() != that.getCombineFuncRGB())return false;
      if (this.getCombineOp0Alpha() != that.getCombineOp0Alpha())return false;
      if (this.getCombineOp1RGB() != that.getCombineOp1RGB())return false;
      if (this.getCombineOp2Alpha() != that.getCombineOp2Alpha())return false;
      if (this.getCombineOp2RGB() != that.getCombineOp2RGB())return false;
      if (this.getCombineScaleAlpha() != that.getCombineScaleAlpha())return false;
      if (this.getCombineScaleRGB() != that.getCombineScaleRGB())return false;
      if (this.getCombineSrc0Alpha() != that.getCombineSrc0Alpha())return false;
      if (this.getCombineSrc0RGB() != that.getCombineSrc0RGB())return false;
      if (this.getCombineSrc1Alpha() != that.getCombineSrc1Alpha())return false;
      if (this.getCombineSrc1RGB() != that.getCombineSrc1RGB())return false;
      if (this.getCombineSrc2Alpha() != that.getCombineSrc2Alpha())return false;
      if (this.getCombineSrc2RGB() != that.getCombineSrc2RGB())return false;
      if (this.getCorrection() != that.getCorrection())return false;
      if (this.getEnvironmentalMapMode() != that.getEnvironmentalMapMode())return false;
      if (this.getFilter() != that.getFilter())return false;
      if (this.getMipmap() != that.getMipmap())return false;
      if (this.getMipmapState() != that.getMipmapState())return false;
      if (this.getWrap() != that.getWrap())return false;
      if (this.getBlendColor() != null &&
          !this.getBlendColor().equals(that.getBlendColor()))return false;
      if (this.getBlendColor() == null && that.getBlendColor() != null)return false;
    }
    return true;
  }

  /**
   * Retreive a basic clone of this Texture (ie, clone everything but the image data, which is shared)
   * @return Texture
   */
  public Texture createSimpleClone() {
    Texture rVal = new Texture(anisoLevel);

    if (blendColorBuffer != null) {
      FloatBuffer color =
          ByteBuffer
          .allocateDirect(16)
          .order(ByteOrder.nativeOrder())
          .asFloatBuffer();
      color.put(blendColorBuffer);
      blendColorBuffer.flip();
      color.flip();
      rVal.setBlendColorBuffer(color);
    }

    rVal.setApply(apply);
    rVal.setCombineFuncAlpha(combineFuncAlpha);
    rVal.setCombineFuncRGB(combineFuncRGB);
    rVal.setCombineOp0Alpha(combineOp0Alpha);
    rVal.setCombineOp0RGB(combineOp0RGB);
    rVal.setCombineOp1Alpha(combineOp1Alpha);
    rVal.setCombineOp1RGB(combineOp1RGB);
    rVal.setCombineOp2Alpha(combineOp2Alpha);
    rVal.setCombineOp2RGB(combineOp2RGB);
    rVal.setCombineScaleAlpha(combineScaleAlpha);
    rVal.setCombineScaleRGB(combineScaleRGB);
    rVal.setCombineSrc0Alpha(combineSrc0Alpha);
    rVal.setCombineSrc0RGB(combineSrc0RGB);
    rVal.setCombineSrc1Alpha(combineSrc1Alpha);
    rVal.setCombineSrc1RGB(combineSrc1RGB);
    rVal.setCombineSrc2Alpha(combineSrc2Alpha);
    rVal.setCombineSrc2RGB(combineSrc2RGB);
    rVal.setCorrection(correction);
    rVal.setEnvironmentalMapMode(envMapMode);
    rVal.setFilter(filter);
    rVal.setImage(image);  // NOT CLONED.
    rVal.setImageLocation(imageLocation);
    rVal.setMipmapState(mipmapState);
    rVal.setTextureId(textureId);
    rVal.setWrap(wrap);
    return rVal;
  }
}
