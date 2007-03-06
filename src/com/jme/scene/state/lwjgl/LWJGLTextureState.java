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

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.RenderContext;
import com.jme.scene.SceneElement;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.lwjgl.records.StateRecord;
import com.jme.scene.state.lwjgl.records.TextureRecord;
import com.jme.scene.state.lwjgl.records.TextureStateRecord;
import com.jme.scene.state.lwjgl.records.TextureUnitRecord;
import com.jme.system.DisplaySystem;
import com.jme.util.LoggingSystem;
import com.jme.util.TextureManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBTextureCompression;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.EXTTextureCompressionS3TC;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.Util;
import org.lwjgl.opengl.glu.GLU;
import org.lwjgl.opengl.glu.MipMap;

/**
 * <code>LWJGLTextureState</code> subclasses the TextureState object using the
 * LWJGL API to access OpenGL for texture processing.
 * 
 * @author Mark Powell
 * @author Joshua Slack - updates, optimizations, etc. also StateRecords
 * @version $Id: LWJGLTextureState.java,v 1.88 2007-03-06 15:17:34 nca Exp $
 */
public class LWJGLTextureState extends TextureState {

    private static final long serialVersionUID = 1L;

    private static int[] imageComponents = { GL11.GL_RGBA4, GL11.GL_RGB8,
            GL11.GL_RGB5_A1, GL11.GL_RGBA8, GL11.GL_LUMINANCE8_ALPHA8,
            EXTTextureCompressionS3TC.GL_COMPRESSED_RGB_S3TC_DXT1_EXT,
            EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT,
            EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT,
            EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT,
            EXTTextureCompressionS3TC.GL_COMPRESSED_RGB_S3TC_DXT1_EXT,
            EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT,
            EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT,
            EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT };

    private static int[] imageFormats = { GL11.GL_RGBA, GL11.GL_RGB,
            GL11.GL_RGBA, GL11.GL_RGBA, GL11.GL_LUMINANCE_ALPHA, GL11.GL_RGB,
            GL11.GL_RGBA, GL11.GL_RGBA, GL11.GL_RGBA };

    private static boolean inited = false;

    /**
     * Constructor instantiates a new <code>LWJGLTextureState</code> object.
     * The number of textures that can be combined is determined during
     * construction. This equates the number of texture units supported by the
     * graphics card.
     */
    public LWJGLTextureState() {
        super();

        // get our array of texture objects ready.
        texture = new ArrayList<Texture>();

        // See if we haven't already setup a texturestate before.
        if (!inited) {
            // Check for support of multitextures. We use GL13.glActiveTexture
            // to do multitexturing, so we need to support GL13 as well.
            supportsMultiTexture = (GLContext.getCapabilities().GL_ARB_multitexture && GLContext
                    .getCapabilities().OpenGL13);
            
            supportsEnvDot3 = GLContext.getCapabilities().GL_ARB_texture_env_dot3;

            // If we do support multitexturing, find out how many textures we
            // can handler.
            if (supportsMultiTexture) {
                IntBuffer buf = BufferUtils.createIntBuffer(16);
                GL11.glGetInteger(GL13.GL_MAX_TEXTURE_UNITS, buf);
                numFixedTexUnits = buf.get(0);
            } else {
                numFixedTexUnits = 1;
            }

            // Go on to check number of texture units supported for vertex and
            // fragment shaders
            if (GLContext.getCapabilities().GL_ARB_shader_objects
                    && GLContext.getCapabilities().GL_ARB_vertex_shader
                    && GLContext.getCapabilities().GL_ARB_fragment_shader) {
                IntBuffer buf = BufferUtils.createIntBuffer(16);
                GL11.glGetInteger(
                        ARBVertexShader.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS_ARB,
                        buf);
                numVertexTexUnits = buf.get(0);
                GL11.glGetInteger(
                        ARBFragmentShader.GL_MAX_TEXTURE_IMAGE_UNITS_ARB, buf);
                numFragmentTexUnits = buf.get(0);
            } else {
                numVertexTexUnits = 0;
                numFragmentTexUnits = 0;
            }

            // Now determine the maximum number of supported texture units
            numTotalTexUnits = Math.max(numFixedTexUnits, Math.max(
                    numFragmentTexUnits, numVertexTexUnits));

            // Check for S3 texture compression capability.
            supportsS3TCCompression = GLContext.getCapabilities().GL_EXT_texture_compression_s3tc;

            // See if we support anisotropic filtering
            supportsAniso = GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic;

            if (supportsAniso) {
                // Due to LWJGL buffer check, you can't use smaller sized
                // buffers (min_size = 16 for glGetFloat()).
                FloatBuffer max_a = BufferUtils.createFloatBuffer(16);
                max_a.rewind();

                // Grab the maximum anisotropic filter.
                GL11.glGetFloat(
                                EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT,
                                max_a);

                // set max.
                maxAnisotropic = max_a.get(0);
            }

            // See if we support textures that are not power of 2 in size.
            supportsNonPowerTwo = GLContext.getCapabilities().GL_ARB_texture_non_power_of_two;

            // Setup our default texture by adding it to our array and loading
            // it, then clearing our array.
            setTexture(defaultTexture);
            load(0);
            this.texture.clear();

            // We're done initing! Wee! :)
            inited = true;
        }
    }

    /**
     * override MipMap to access helper methods
     */
    protected static class LWJGLMipMap extends MipMap {
        /**
         * @see MipMap#glGetIntegerv(int)
         */
        protected static int glGetIntegerv(int what) {
            return org.lwjgl.opengl.glu.Util.glGetIntegerv(what);
        }

        /**
         * @see MipMap#nearestPower(int)
         */
        protected static int nearestPower(int value) {
            return org.lwjgl.opengl.glu.Util.nearestPower(value);
        }

        /**
         * @see MipMap#bytesPerPixel(int, int)
         */
        protected static int bytesPerPixel(int format, int type) {
            return org.lwjgl.opengl.glu.Util.bytesPerPixel(format, type);
        }
    }

    @Override
    public void load(int unit) {
        Texture texture = getTexture(unit);
        if (texture == null) {
            return;
        }
        
        RenderContext context = DisplaySystem.getDisplaySystem()
                .getCurrentContext();
        TextureStateRecord record = null;
        if (context != null)
            record = (TextureStateRecord) context.getStateRecord(RS_TEXTURE);

        // Check we are in the right unit
        if (record != null)
            checkAndSetUnit(unit, record);
        
        // Create the texture
        if (texture.getTextureKey() != null) {
            Texture cached = TextureManager.findCachedTexture(texture
                    .getTextureKey());
            if (cached == null) {
                TextureManager.addToCache(texture);
            } else if (cached.getTextureId() != 0) {
                texture.setTextureId(cached.getTextureId());
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, cached.getTextureId());
                if (record != null)
                    record.units[unit].boundTexture = texture.getTextureId();
                return;
            }
        }

        IntBuffer id = BufferUtils.createIntBuffer(1);
        id.clear();
        GL11.glGenTextures(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id.get(0));
        if (record != null)
            record.units[unit].boundTexture = id.get(0);

        texture.setTextureId(id.get(0));
        TextureManager.registerForCleanup(texture.getTextureKey(), texture
                .getTextureId());

        // pass image data to OpenGL
        Image image = texture.getImage();
        if (image == null) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Image data for texture is null.");
        }
        // Set up the anisotropic filter.
        if (supportsAniso)
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D,
                    EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
                    Math.max(Math.min(maxAnisotropic, texture.getAnisoLevel()), 1.0f));

        // set alignment to support images with width % 4 != 0, as images are
        // not aligned
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        // Get texture image data. Not all textures have image data.
        // For example, AM_COMBINE modes can use primary colors,
        // texture output, and constants to modify fragments via the
        // texture units.
        if (image != null) {
            if (!supportsNonPowerTwo
                    && (!FastMath.isPowerOfTwo(image.getWidth()) || !FastMath
                            .isPowerOfTwo(image.getHeight()))) {
                LoggingSystem.getLogger().warning(
                        "Attempted to apply texture with size that is not power "
                                + "of 2: " + image.getWidth() + " x "
                                + image.getHeight());

                final int maxSize = LWJGLMipMap
                        .glGetIntegerv(GL11.GL_MAX_TEXTURE_SIZE);

                int actualWidth = image.getWidth();
                int w = LWJGLMipMap.nearestPower(actualWidth);
                if (w > maxSize) {
                    w = maxSize;
                }

                int actualHeight = image.getHeight();
                int h = LWJGLMipMap.nearestPower(actualHeight);
                if (h > maxSize) {
                    h = maxSize;
                }
                LoggingSystem.getLogger().warning(
                        "Rescaling image to " + w + " x " + h + " !!!");

                // must rescale image to get "top" mipmap texture image
                int format = imageFormats[image.getType()];
                int type = GL11.GL_UNSIGNED_BYTE;
                int bpp = LWJGLMipMap.bytesPerPixel(format, type);
                ByteBuffer scaledImage = BufferUtils.createByteBuffer((w + 4)
                        * h * bpp);
                int error = MipMap.gluScaleImage(format, actualWidth,
                        actualHeight, type, image.getData(), w, h, type,
                        scaledImage);
                if (error != 0) {
                    Util.checkGLError();
                }

                image.setWidth(w);
                image.setHeight(h);
                image.setData(scaledImage);
            }

            // For textures which need mipmaps auto-generating and which
            // aren't using compressed images, generate the mipmaps.
            // A new mipmap builder may be needed to build mipmaps for
            // compressed textures.
            if (texture.getMipmap() >= Texture.MM_NEAREST_NEAREST
                    && !image.hasMipmaps() && !image.isCompressedType()) {
                // insure the buffer is ready for reading
                image.getData().rewind();
                GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D, imageComponents[image
                        .getType()], image.getWidth(), image.getHeight(),
                        imageFormats[image.getType()], GL11.GL_UNSIGNED_BYTE,
                        image.getData());
            } else {
                // Get mipmap data sizes and amount of mipmaps to send to
                // opengl. Then loop through all mipmaps and send them.
                int[] mipSizes = image.getMipMapSizes();
                ByteBuffer data = image.getData();
                int max = 1;
                int pos = 0;
                if (mipSizes == null) {
                    mipSizes = new int[] { data.capacity() };
                } else if (texture.getMipmap() != Texture.MM_NONE) {
                    max = mipSizes.length;
                }

                for (int m = 0; m < max; m++) {
                    int width = Math.max(1, image.getWidth() >> m);
                    int height = Math.max(1, image.getHeight() >> m);

                    data.position(pos);

                    if (image.isCompressedType()) {
                        data.limit(pos+mipSizes[m]);
                        ARBTextureCompression.glCompressedTexImage2DARB(
                                GL11.GL_TEXTURE_2D, m, imageComponents[image
                                        .getType()], width, height, 0, data);
                    } else {
                        data.limit(data.position() + mipSizes[m]);
                        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, m,
                                imageComponents[image.getType()], width,
                                height, 0, imageFormats[image.getType()],
                                GL11.GL_UNSIGNED_BYTE, data);
                    }

                    pos += mipSizes[m];
                }
                data.clear();
            }
        }
    }

    /**
     * <code>apply</code> manages the textures being described by the state.
     * If the texture has not been loaded yet, it is generated and loaded using
     * OpenGL11. This means the initial pass to set will be longer than
     * subsequent calls. The multitexture extension is used to define the
     * multiple texture states, with the number of units being determined at
     * construction time.
     * 
     * @see com.jme.scene.state.RenderState#apply()
     */
    public void apply() {
        // ask for the current state record
        RenderContext context = DisplaySystem.getDisplaySystem()
                .getCurrentContext();
        TextureStateRecord record = (TextureStateRecord) context
                .getStateRecord(RS_TEXTURE);
        context.currentStates[RS_TEXTURE] = this;

        if (isEnabled()) {

            Texture texture;
            TextureUnitRecord unitRecord;
            TextureRecord texRecord;

            int glHint = getPerspHint(getCorrection());
            if (record.hint != glHint) {
                // set up correction mode
                GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT,
                        glHint);
                record.hint = glHint;
            }

            // loop through all available texture units...
            for (int i = 0; i < numTotalTexUnits; i++) {
                unitRecord = record.units[i];
                
                // grab a texture for this unit, if available
                texture = getTexture(i);
                
                // check for invalid textures - ones that have no opengl id and
                // no image data
                if (texture != null && texture.getTextureId() == 0
                        && texture.getImage() == null)
                    texture = null;

                // null textures above fixed limit do not need to be disabled. (cant?)
                if (texture == null) {
                    if (i >= numFixedTexUnits)
                        continue;
                    else {
                        // a null texture indicates no texturing at this unit
                        // Disable 2D texturing on this unit if enabled.
                        if (unitRecord.enabled) {
                            // Check we are in the right unit
                            checkAndSetUnit(i, record);
                            GL11.glDisable(GL11.GL_TEXTURE_2D);
                            unitRecord.enabled = false;
                        }
                        if (i < idCache.length)
                            idCache[i] = 0;

                        // next texture!
                        continue;
                    }
                }

                // Time to bind the texture, so see if we need to load in image
                // data for this texture.
                if (texture.getTextureId() == 0) {
                    // texture not yet loaded.
                    // this will load and bind and set the records...
                    load(i);
                    if (texture.getTextureId() == 0) continue;
                } else {
                    // texture already exists in OpenGL, just bind it if needed
                    if (unitRecord.boundTexture != texture.getTextureId()) {
                        // Check we are in the right unit
                        checkAndSetUnit(i, record);
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureId());
                        unitRecord.boundTexture = texture.getTextureId();
                    }
                }
            
                // Grab our record for this texture
                texRecord = record.getTextureRecord(texture.getTextureId());
            
                // Set the idCache value for this unit of this texture state
                // This is done so during state comparison we don't have to
                // spend a lot of time pulling out classes and finding field
                // data.
                idCache[i] = texture.getTextureId();

                // Some texture things only apply to fixed function pipeline
                if (i < numFixedTexUnits) {

                    // Check we are in the right unit
                    
                    // Enable 2D texturing on this unit if not enabled.
                    if (!unitRecord.enabled) {
                        checkAndSetUnit(i, record);
                        GL11.glEnable(GL11.GL_TEXTURE_2D);
                        unitRecord.enabled = true;
                    }

                    // These are texture specific
                    applyFilter(texture, texRecord, i, record);
                    applyWrap(texture, texRecord, i, record);

                    // Now time to play with texture matrices
                    // Determine which transforms to do.
                    applyTextureTransforms(texture, i, record);

                    // Set our blend color, if needed.
                    applyBlendColor(texture, unitRecord, texRecord, i, record);

                    // Now let's look at automatic texture coordinate generation.
                    applyTexCoordGeneration(texture, unitRecord, i, record);

                    // Set the texture environment mode if this unit isn't
                    // already set properly
                    int glEnvMode = getGLEnvMode(texture.getApply());
                    applyEnvMode(glEnvMode, unitRecord, i, record);

                    // If our mode is combine, and we support multitexturing
                    // apply combine settings.
                    if (glEnvMode == GL13.GL_COMBINE && supportsMultiTexture) {
                        applyCombineFactors(texture, unitRecord, i, record);
                    }
                }
            }

        } else {
            // turn off texturing
            TextureUnitRecord unitRecord;

            if (supportsMultiTexture) {
                for (int i = 0; i < numFixedTexUnits; i++) {
                    unitRecord = record.units[i];
                    if (unitRecord.enabled) {
                        checkAndSetUnit(i, record);
                        GL11.glDisable(GL11.GL_TEXTURE_2D);
                        unitRecord.enabled = false;
                    }
                }
            } else {
                unitRecord = record.units[0];
                if (unitRecord.enabled) {
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    unitRecord.enabled = false;
                }
            }
        }
    }

    private void applyCombineFactors(Texture texture, TextureUnitRecord unitRecord, int unit, TextureStateRecord record) {
        // first thing's first... if we are doing dot3 and don't
        // support it, disable this texture.
        boolean checked = false;
        if (!supportsEnvDot3 && (texture.getCombineFuncAlpha() == Texture.ACF_DOT3_RGB
                || texture.getCombineFuncAlpha() == Texture.ACF_DOT3_RGBA 
                || texture.getCombineFuncRGB() == Texture.ACF_DOT3_RGB
                || texture.getCombineFuncRGB() == Texture.ACF_DOT3_RGBA)) {
        
            checkAndSetUnit(unit, record);
            checked = true;
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            unitRecord.enabled = false;
            
            // No need to continue
            return;
        }

        // Okay, now let's set our scales if we need to:
        // First RGB Combine scale
        if (unitRecord.envRGBScale != texture.getCombineScaleRGB()) {
            if (!checked) {
                checkAndSetUnit(unit, record);
                checked = true;
            }
            GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL13.GL_RGB_SCALE, texture
                    .getCombineScaleRGB());
            unitRecord.envRGBScale = texture.getCombineScaleRGB();
        }
        // Then Alpha Combine scale
        if (unitRecord.envAlphaScale != texture.getCombineScaleAlpha()) {
            if (!checked) {
                checkAndSetUnit(unit, record);
                checked = true;
            }
            GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_ALPHA_SCALE, texture
                    .getCombineScaleRGB());
            unitRecord.envAlphaScale = texture.getCombineScaleAlpha();
        }
        
        // Time to set the RGB combines
        int rgbCombineFunc = texture.getCombineFuncRGB();
        if (unitRecord.rgbCombineFunc != rgbCombineFunc) {
            if (!checked) {
                checkAndSetUnit(unit, record);
                checked = true;
            }
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_RGB,
                    getGLCombineFunc(rgbCombineFunc));
            unitRecord.rgbCombineFunc = rgbCombineFunc;
        }
        
        int combSrcRGB = texture.getCombineSrc0RGB();
        if (unitRecord.combSrcRGB0 != combSrcRGB) {
            if (!checked) {
                checkAndSetUnit(unit, record);
                checked = true;
            }
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_RGB, getGLCombineSrc(combSrcRGB));
            unitRecord.combSrcRGB0 = combSrcRGB;
        }
        
        int combOpRGB = texture.getCombineOp0RGB();
        if (unitRecord.combOpRGB0 != combOpRGB) {
            if (!checked) {
                checkAndSetUnit(unit, record);
                checked = true;
            }
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_RGB, getGLCombineOpRGB(combOpRGB));
            unitRecord.combOpRGB0 = combOpRGB;
        }

        if (rgbCombineFunc != Texture.ACF_REPLACE) {
            
            combSrcRGB = texture.getCombineSrc1RGB();
            if (unitRecord.combSrcRGB1 != combSrcRGB) {
                if (!checked) {
                    checkAndSetUnit(unit, record);
                    checked = true;
                }
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE1_RGB, getGLCombineSrc(combSrcRGB));
                unitRecord.combSrcRGB1 = combSrcRGB;
            }

            combOpRGB = texture.getCombineOp1RGB();
            if (unitRecord.combOpRGB1 != combOpRGB) {
                if (!checked) {
                    checkAndSetUnit(unit, record);
                    checked = true;
                }
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_RGB, getGLCombineOpRGB(combOpRGB));
                unitRecord.combOpRGB1 = combOpRGB;
            }

            if (rgbCombineFunc == Texture.ACF_INTERPOLATE) {
                
                combSrcRGB = texture.getCombineSrc2RGB();
                if (unitRecord.combSrcRGB2 != combSrcRGB) {
                    if (!checked) {
                        checkAndSetUnit(unit, record);
                        checked = true;
                    }
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE2_RGB, getGLCombineSrc(combSrcRGB));
                    unitRecord.combSrcRGB2 = combSrcRGB;
                }
                
                combOpRGB = texture.getCombineOp2RGB();
                if (unitRecord.combOpRGB2 != combOpRGB) {
                    if (!checked) {
                        checkAndSetUnit(unit, record);
                        checked = true;
                    }
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND2_RGB, getGLCombineOpRGB(combOpRGB));
                    unitRecord.combOpRGB2 = combOpRGB;
                }

            }
        }

        
        // Now Alpha combines
        int alphaCombineFunc = texture.getCombineFuncAlpha();
        if (unitRecord.alphaCombineFunc != alphaCombineFunc) {
            if (!checked) {
                checkAndSetUnit(unit, record);
                checked = true;
            }
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_ALPHA,
                    getGLCombineFunc(alphaCombineFunc));
            unitRecord.alphaCombineFunc = alphaCombineFunc;
        }
        
        int combSrcAlpha = texture.getCombineSrc0Alpha();
        if (unitRecord.combSrcAlpha0 != combSrcAlpha) {
            if (!checked) {
                checkAndSetUnit(unit, record);
                checked = true;
            }
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_ALPHA, getGLCombineSrc(combSrcAlpha));
            unitRecord.combSrcAlpha0 = combSrcAlpha;
        }
        
        int combOpAlpha = texture.getCombineOp0Alpha();
        if (unitRecord.combOpAlpha0 != combOpAlpha) {
            if (!checked) {
                checkAndSetUnit(unit, record);
                checked = true;
            }
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_ALPHA, getGLCombineOpAlpha(combOpAlpha));
            unitRecord.combOpAlpha0 = combOpAlpha;
        }

        if (alphaCombineFunc != Texture.ACF_REPLACE) {

            combSrcAlpha = texture.getCombineSrc1Alpha();
            if (unitRecord.combSrcAlpha1 != combSrcAlpha) {
                if (!checked) {
                    checkAndSetUnit(unit, record);
                    checked = true;
                }
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE1_ALPHA, getGLCombineSrc(combSrcAlpha));
                unitRecord.combSrcAlpha1 = combSrcAlpha;
            }
            
            combOpAlpha = texture.getCombineOp1Alpha();
            if (unitRecord.combOpAlpha1 != combOpAlpha) {
                if (!checked) {
                    checkAndSetUnit(unit, record);
                    checked = true;
                }
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_ALPHA, getGLCombineOpAlpha(combOpAlpha));
                unitRecord.combOpAlpha1 = combOpAlpha;
            }
            if (alphaCombineFunc == Texture.ACF_INTERPOLATE) {

                combSrcAlpha = texture.getCombineSrc2Alpha();
                if (unitRecord.combSrcAlpha2 != combSrcAlpha) {
                    if (!checked) {
                        checkAndSetUnit(unit, record);
                        checked = true;
                    }
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE2_ALPHA, getGLCombineSrc(combSrcAlpha));
                    unitRecord.combSrcAlpha2 = combSrcAlpha;
                }
                
                combOpAlpha = texture.getCombineOp2Alpha();
                if (unitRecord.combOpAlpha2 != combOpAlpha) {
                    if (!checked) {
                        checkAndSetUnit(unit, record);
                        checked = true;
                    }
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND2_ALPHA, getGLCombineOpAlpha(combOpAlpha));
                    unitRecord.combOpAlpha2 = combOpAlpha;
                }
            }
        }
    }
    
    private static int getGLCombineOpRGB(int combineOpRGB) {
        switch (combineOpRGB) {
            case Texture.ACO_SRC_COLOR:
                return GL11.GL_SRC_COLOR;
            case Texture.ACO_ONE_MINUS_SRC_COLOR:
                return GL11.GL_ONE_MINUS_SRC_COLOR;
            case Texture.ACO_SRC_ALPHA:
                return GL11.GL_SRC_ALPHA;
            case Texture.ACO_ONE_MINUS_SRC_ALPHA:
                return GL11.GL_ONE_MINUS_SRC_ALPHA;
            default:
                return GL11.GL_SRC_COLOR;
        }
    }
    
    private static int getGLCombineOpAlpha(int combineOpAlpha) {
        switch (combineOpAlpha) {
            case Texture.ACO_SRC_ALPHA:
                return GL11.GL_SRC_ALPHA;
            case Texture.ACO_ONE_MINUS_SRC_ALPHA:
                return GL11.GL_ONE_MINUS_SRC_ALPHA;
            case Texture.ACO_SRC_COLOR: // these 2 we just put here to help prevent errors.
                return GL11.GL_SRC_ALPHA;
            case Texture.ACO_ONE_MINUS_SRC_COLOR:
                return GL11.GL_ONE_MINUS_SRC_ALPHA;
            default:
                return GL11.GL_SRC_ALPHA;
        }
    }

    private static int getGLCombineSrc(int combineSrc) {
        switch (combineSrc) {
            case Texture.ACS_TEXTURE:
                return GL11.GL_TEXTURE;
            case Texture.ACS_PRIMARY_COLOR:
                return GL13.GL_PRIMARY_COLOR;
            case Texture.ACS_CONSTANT:
                return GL13.GL_CONSTANT;
            case Texture.ACS_PREVIOUS:
                return GL13.GL_PREVIOUS;
            case Texture.ACS_TEXTURE0:
                return GL13.GL_TEXTURE0;
            case Texture.ACS_TEXTURE1:
                return GL13.GL_TEXTURE1;
            case Texture.ACS_TEXTURE2:
                return GL13.GL_TEXTURE2;
            case Texture.ACS_TEXTURE3:
                return GL13.GL_TEXTURE3;
            case Texture.ACS_TEXTURE4:
                return GL13.GL_TEXTURE4;
            case Texture.ACS_TEXTURE5:
                return GL13.GL_TEXTURE5;
            case Texture.ACS_TEXTURE6:
                return GL13.GL_TEXTURE6;
            case Texture.ACS_TEXTURE7:
                return GL13.GL_TEXTURE7;
            case Texture.ACS_TEXTURE8:
                return GL13.GL_TEXTURE8;
            case Texture.ACS_TEXTURE9:
                return GL13.GL_TEXTURE9;
            case Texture.ACS_TEXTURE10:
                return GL13.GL_TEXTURE10;
            case Texture.ACS_TEXTURE11:
                return GL13.GL_TEXTURE11;
            case Texture.ACS_TEXTURE12:
                return GL13.GL_TEXTURE12;
            case Texture.ACS_TEXTURE13:
                return GL13.GL_TEXTURE13;
            case Texture.ACS_TEXTURE14:
                return GL13.GL_TEXTURE14;
            case Texture.ACS_TEXTURE15:
                return GL13.GL_TEXTURE15;
            case Texture.ACS_TEXTURE16:
                return GL13.GL_TEXTURE16;
            case Texture.ACS_TEXTURE17:
                return GL13.GL_TEXTURE17;
            case Texture.ACS_TEXTURE18:
                return GL13.GL_TEXTURE18;
            case Texture.ACS_TEXTURE19:
                return GL13.GL_TEXTURE19;
            case Texture.ACS_TEXTURE20:
                return GL13.GL_TEXTURE20;
            case Texture.ACS_TEXTURE21:
                return GL13.GL_TEXTURE21;
            case Texture.ACS_TEXTURE22:
                return GL13.GL_TEXTURE22;
            case Texture.ACS_TEXTURE23:
                return GL13.GL_TEXTURE23;
            case Texture.ACS_TEXTURE24:
                return GL13.GL_TEXTURE24;
            case Texture.ACS_TEXTURE25:
                return GL13.GL_TEXTURE25;
            case Texture.ACS_TEXTURE26:
                return GL13.GL_TEXTURE26;
            case Texture.ACS_TEXTURE27:
                return GL13.GL_TEXTURE27;
            case Texture.ACS_TEXTURE28:
                return GL13.GL_TEXTURE28;
            case Texture.ACS_TEXTURE29:
                return GL13.GL_TEXTURE29;
            case Texture.ACS_TEXTURE30:
                return GL13.GL_TEXTURE30;
            case Texture.ACS_TEXTURE31:
                return GL13.GL_TEXTURE31;
            default:
                return GL13.GL_PRIMARY_COLOR;
        }
    }
    
    private static int getGLCombineFunc(int combineFunc) {
        switch (combineFunc) {
            case Texture.ACF_REPLACE:
                return GL11.GL_REPLACE;
            case Texture.ACF_ADD:
                return GL11.GL_ADD;
            case Texture.ACF_ADD_SIGNED:
                return GL13.GL_ADD_SIGNED;
            case Texture.ACF_SUBTRACT:
                return GL13.GL_SUBTRACT;
            case Texture.ACF_INTERPOLATE:
                return GL13.GL_INTERPOLATE;
            case Texture.ACF_DOT3_RGB:
                return GL13.GL_DOT3_RGB;
            case Texture.ACF_DOT3_RGBA:
                return GL13.GL_DOT3_RGBA;
            case Texture.ACF_MODULATE:
            default:
                return GL11.GL_MODULATE;
        }
    }

    private void applyEnvMode(int glEnvMode, TextureUnitRecord unitRecord, int unit, TextureStateRecord record) {
        if (unitRecord.envMode != glEnvMode) {
            checkAndSetUnit(unit, record);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
                    GL11.GL_TEXTURE_ENV_MODE, glEnvMode);
            unitRecord.envMode = glEnvMode;
        }
    }

    private void applyBlendColor(Texture texture, TextureUnitRecord unitRecord, TextureRecord texRecord, int unit, TextureStateRecord record) {
        ColorRGBA texBlend = texture.getBlendColor();
        if (texBlend == null) texBlend = TextureRecord.defaultColor;
        if (unitRecord.blendColor.r != texBlend.r || 
                unitRecord.blendColor.g != texBlend.g || 
                unitRecord.blendColor.b != texBlend.b || 
                unitRecord.blendColor.a != texBlend.a) {
            checkAndSetUnit(unit, record);
            texRecord.colorBuffer.clear();
            texRecord.colorBuffer.put(texBlend.r).put(texBlend.g).put(texBlend.b).put(texBlend.a);
            texRecord.colorBuffer.rewind();
            GL11.glTexEnv(GL11.GL_TEXTURE_ENV,
                    GL11.GL_TEXTURE_ENV_COLOR, texRecord.colorBuffer);
            unitRecord.blendColor.set(texBlend);
        }
    }

    private void applyTextureTransforms(Texture texture, int unit,
            TextureStateRecord record) {
        boolean needsReset = !record.units[unit].identityMatrix;
        
        
        
        // Should we load a base matrix?
        boolean doMatrix = (texture.getMatrix() != null && !texture.getMatrix()
                .isIdentity());

        // Should we apply transforms?
        boolean doTrans = texture.getTranslation() != null
                && (texture.getTranslation().x != 0
                        || texture.getTranslation().y != 0 
                        || texture.getTranslation().z != 0);
        boolean doRot = texture.getRotation() != null
                && !texture.getRotation().isIdentity();
        boolean doScale = texture.getScale() != null
                && (texture.getScale().x != 1 
                        || texture.getScale().y != 1 
                        || texture.getScale().z != 1);

        // Now do them.
        if (doMatrix || doTrans || doRot || doScale) {
            checkAndSetUnit(unit, record);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            if (doMatrix) {
                texture.getMatrix().fillFloatBuffer(record.tmp_matrixBuffer, true);
                GL11.glLoadMatrix(record.tmp_matrixBuffer);
            } else {
                GL11.glLoadIdentity();
            }
            if (doTrans) {
                GL11.glTranslatef(texture.getTranslation().x, texture
                        .getTranslation().y, texture.getTranslation().z);
            }
            if (doRot) {
                Vector3f vRot = record.tmp_rotation1;
                float rot = texture.getRotation().toAngleAxis(vRot)
                        * FastMath.RAD_TO_DEG;
                GL11.glRotatef(rot, vRot.x, vRot.y, vRot.z);
            }
            if (doScale)
                GL11.glScalef(texture.getScale().x, texture.getScale().y,
                        texture.getScale().z);

            // Switch back to the modelview matrix for further operations
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            record.units[unit].identityMatrix = false;
        } else if (needsReset) {
            checkAndSetUnit(unit, record);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            record.units[unit].identityMatrix = true;
        }
    }

    private void applyTexCoordGeneration(Texture texture,
            TextureUnitRecord unitRecord, int unit, TextureStateRecord record) {
        boolean checked = false;
        if (texture.getEnvironmentalMapMode() == Texture.EM_NONE) {
            
            // No coordinate generation
            if (unitRecord.textureGenQ) {
                checkAndSetUnit(unit, record);
                checked = true;
                GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
                unitRecord.textureGenQ = false;
            }
            if (unitRecord.textureGenR) {
                if (!checked) {
                    checkAndSetUnit(unit, record);
                    checked = true;
                }
                GL11.glDisable(GL11.GL_TEXTURE_GEN_R);
                unitRecord.textureGenR = false;
            }
            if (unitRecord.textureGenS) {
                if (!checked) {
                    checkAndSetUnit(unit, record);
                    checked = true;
                }
                GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
                unitRecord.textureGenS = false;
            }
            if (unitRecord.textureGenT) {
                if (!checked) {
                    checkAndSetUnit(unit, record);
                    checked = true;
                }
                GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
                unitRecord.textureGenT = false;
            }
        } else if (texture.getEnvironmentalMapMode() == Texture.EM_SPHERE) {
            // generate spherical texture coordinates
            if (unitRecord.textureGenSMode != GL11.GL_SPHERE_MAP) {
                checkAndSetUnit(unit, record);
                checked = true;
                GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE,
                        GL11.GL_SPHERE_MAP);
                unitRecord.textureGenSMode = GL11.GL_SPHERE_MAP;
            }

            if (unitRecord.textureGenTMode != GL11.GL_SPHERE_MAP) {
                if (!checked) {
                    checkAndSetUnit(unit, record);
                    checked = true;
                }
                GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE,
                        GL11.GL_SPHERE_MAP);
                unitRecord.textureGenTMode = GL11.GL_SPHERE_MAP;
            }

            if (unitRecord.textureGenQ) {
                if (!checked) {
                    checkAndSetUnit(unit, record);
                    checked = true;
                }
                GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
                unitRecord.textureGenQ = false;
            }
            if (unitRecord.textureGenR) {
                if (!checked) {
                    checkAndSetUnit(unit, record);
                    checked = true;
                }
                GL11.glDisable(GL11.GL_TEXTURE_GEN_R);
                unitRecord.textureGenR = false;
            }
            if (!unitRecord.textureGenS) {
                if (!checked) {
                    checkAndSetUnit(unit, record);
                    checked = true;
                }
                GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
                unitRecord.textureGenS = true;
            }
            if (!unitRecord.textureGenT) {
                if (!checked) {
                    checkAndSetUnit(unit, record);
                    checked = true;
                }
                GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
                unitRecord.textureGenT = true;
            }
        } else if (texture.getEnvironmentalMapMode() == Texture.EM_EYE_LINEAR) {
            checkAndSetUnit(unit, record);
            // generate eye linear texture coordinates
            if (unitRecord.textureGenQMode != GL11.GL_EYE_LINEAR) {
                GL11.glTexGeni(GL11.GL_Q, GL11.GL_TEXTURE_GEN_MODE,
                        GL11.GL_EYE_LINEAR);
                unitRecord.textureGenSMode = GL11.GL_EYE_LINEAR;
            }

            if (unitRecord.textureGenRMode != GL11.GL_EYE_LINEAR) {
                GL11.glTexGeni(GL11.GL_R, GL11.GL_TEXTURE_GEN_MODE,
                        GL11.GL_EYE_LINEAR);
                unitRecord.textureGenTMode = GL11.GL_EYE_LINEAR;
            }

            if (unitRecord.textureGenSMode != GL11.GL_EYE_LINEAR) {
                GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE,
                        GL11.GL_EYE_LINEAR);
                unitRecord.textureGenSMode = GL11.GL_EYE_LINEAR;
            }

            if (unitRecord.textureGenTMode != GL11.GL_EYE_LINEAR) {
                GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE,
                        GL11.GL_EYE_LINEAR);
                unitRecord.textureGenTMode = GL11.GL_EYE_LINEAR;
            }

            record.eyePlaneS.rewind();
            GL11.glTexGen(GL11.GL_S, GL11.GL_EYE_PLANE, record.eyePlaneS);
            record.eyePlaneT.rewind();
            GL11.glTexGen(GL11.GL_T, GL11.GL_EYE_PLANE, record.eyePlaneT);
            record.eyePlaneR.rewind();
            GL11.glTexGen(GL11.GL_R, GL11.GL_EYE_PLANE, record.eyePlaneR);
            record.eyePlaneQ.rewind();
            GL11.glTexGen(GL11.GL_Q, GL11.GL_EYE_PLANE, record.eyePlaneQ);

            if (!unitRecord.textureGenQ) {
                GL11.glEnable(GL11.GL_TEXTURE_GEN_Q);
                unitRecord.textureGenQ = true;
            }
            if (!unitRecord.textureGenR) {
                GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
                unitRecord.textureGenR = true;
            }
            if (!unitRecord.textureGenS) {
                GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
                unitRecord.textureGenS = true;
            }
            if (!unitRecord.textureGenT) {
                GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
                unitRecord.textureGenT = true;
            }
        } else if (texture.getEnvironmentalMapMode() == Texture.EM_OBJECT_LINEAR) {
            checkAndSetUnit(unit, record);
            // generate eye linear texture coordinates
            if (unitRecord.textureGenQMode != GL11.GL_OBJECT_LINEAR) {
                GL11.glTexGeni(GL11.GL_Q, GL11.GL_TEXTURE_GEN_MODE,
                        GL11.GL_OBJECT_LINEAR);
                unitRecord.textureGenSMode = GL11.GL_OBJECT_LINEAR;
            }

            if (unitRecord.textureGenRMode != GL11.GL_OBJECT_LINEAR) {
                GL11.glTexGeni(GL11.GL_R, GL11.GL_TEXTURE_GEN_MODE,
                        GL11.GL_OBJECT_LINEAR);
                unitRecord.textureGenTMode = GL11.GL_OBJECT_LINEAR;
            }

            if (unitRecord.textureGenSMode != GL11.GL_OBJECT_LINEAR) {
                GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE,
                        GL11.GL_OBJECT_LINEAR);
                unitRecord.textureGenSMode = GL11.GL_OBJECT_LINEAR;
            }

            if (unitRecord.textureGenTMode != GL11.GL_OBJECT_LINEAR) {
                GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE,
                        GL11.GL_OBJECT_LINEAR);
                unitRecord.textureGenTMode = GL11.GL_OBJECT_LINEAR;
            }

            record.eyePlaneS.rewind();
            GL11.glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE, record.eyePlaneS);
            record.eyePlaneT.rewind();
            GL11.glTexGen(GL11.GL_T, GL11.GL_OBJECT_PLANE, record.eyePlaneT);
            record.eyePlaneR.rewind();
            GL11.glTexGen(GL11.GL_R, GL11.GL_OBJECT_PLANE, record.eyePlaneR);
            record.eyePlaneQ.rewind();
            GL11.glTexGen(GL11.GL_Q, GL11.GL_OBJECT_PLANE, record.eyePlaneQ);

            if (!unitRecord.textureGenQ) {
                GL11.glEnable(GL11.GL_TEXTURE_GEN_Q);
                unitRecord.textureGenQ = true;
            }
            if (!unitRecord.textureGenR) {
                GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
                unitRecord.textureGenR = true;
            }
            if (!unitRecord.textureGenS) {
                GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
                unitRecord.textureGenS = true;
            }
            if (!unitRecord.textureGenT) {
                GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
                unitRecord.textureGenT = true;
            }
        }
    }

    private static int getGLEnvMode(int apply) {
        switch (apply) {
            case Texture.AM_REPLACE:
                return GL11.GL_REPLACE;
            case Texture.AM_BLEND:
                return GL11.GL_BLEND;
            case Texture.AM_COMBINE:
                return GL13.GL_COMBINE;
            case Texture.AM_DECAL:
                return GL11.GL_DECAL;
            case Texture.AM_ADD:
                return GL11.GL_ADD;
            case Texture.AM_MODULATE:
            default:
                return GL11.GL_MODULATE;
        }
    }

    private static int getPerspHint(int correction) {
        switch (correction) {
            case TextureState.CM_AFFINE:
                return GL11.GL_FASTEST;
            case TextureState.CM_PERSPECTIVE:
            default:
                return GL11.GL_NICEST;
        }
    }

    private void checkAndSetUnit(int i, TextureStateRecord record) {
        // If we support multtexturing, specify the unit we are affecting.
        if (supportsMultiTexture && record.currentUnit != i) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
            record.currentUnit = i;
        }
    }

    /**
     * Check if the filter settings of this particular texture have been changed and
     * apply as needed.
     * 
     * @param texture
     *            our texture object
     * @param texRecord
     *            our record of the last state of the texture in gl
     * @param record 
     */
    private void applyFilter(Texture texture, TextureRecord texRecord, int unit, TextureStateRecord record) {
        int magFilter = getGLMagFilter(texture.getFilter());
        // set up magnification filter
        if (texRecord.magFilter != magFilter) {
            checkAndSetUnit(unit, record);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                    GL11.GL_TEXTURE_MAG_FILTER, magFilter);
            texRecord.magFilter = magFilter;
        }

        int minFilter = getGLMinFilter(texture.getMipmap());

        // set up mipmap filter
        if (texRecord.minFilter != minFilter) {
            checkAndSetUnit(unit, record);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                    GL11.GL_TEXTURE_MIN_FILTER, minFilter);
            texRecord.minFilter = minFilter;
        } 
    }

    private static int getGLMagFilter(int magFilter) {
        switch (magFilter) {
            case Texture.FM_LINEAR:
                return GL11.GL_LINEAR;
            case Texture.FM_NEAREST:
            default: 
                return GL11.GL_NEAREST;
                
        }
    }

    private static int getGLMinFilter(int minFilter) {
        switch (minFilter) {
            case Texture.MM_LINEAR:
                return GL11.GL_LINEAR;
            case Texture.MM_LINEAR_LINEAR:
                return GL11.GL_LINEAR_MIPMAP_LINEAR;
            case Texture.MM_LINEAR_NEAREST:
                return GL11.GL_LINEAR_MIPMAP_NEAREST;
            case Texture.MM_NEAREST:
                return GL11.GL_NEAREST;
            case Texture.MM_NEAREST_NEAREST:
                return GL11.GL_NEAREST_MIPMAP_NEAREST;
            case Texture.MM_NONE:
                return GL11.GL_NEAREST;
            case Texture.MM_NEAREST_LINEAR:
            default: 
                return GL11.GL_NEAREST_MIPMAP_LINEAR;
                
        }
    }

    /**
     * Check if the wrap mode of this particular texture has been changed and
     * apply as needed.
     * 
     * @param texture
     *            our texture object
     * @param texRecord
     *            our record of the last state of the unit in gl
     * @param record 
     */
    private void applyWrap(Texture texture, TextureRecord texRecord, int unit, TextureStateRecord record) {
        int wrapS = -1;
        int wrapT = -1;
        switch (texture.getWrap()) {
            case Texture.WM_ECLAMP_S_ECLAMP_T:
                wrapS = GL12.GL_CLAMP_TO_EDGE;
                wrapT = GL12.GL_CLAMP_TO_EDGE;
                break;
            case Texture.WM_BCLAMP_S_BCLAMP_T:
                wrapS = GL13.GL_CLAMP_TO_BORDER;
                wrapT = GL13.GL_CLAMP_TO_BORDER;
                break;
            case Texture.WM_CLAMP_S_CLAMP_T:
                wrapS = GL11.GL_CLAMP;
                wrapT = GL11.GL_CLAMP;
                break;
            case Texture.WM_CLAMP_S_WRAP_T:
                wrapS = GL11.GL_CLAMP;
                wrapT = GL11.GL_REPEAT;
                break;
            case Texture.WM_WRAP_S_CLAMP_T:
                wrapS = GL11.GL_REPEAT;
                wrapT = GL11.GL_CLAMP;
                break;
            case Texture.WM_WRAP_S_WRAP_T:
            default:
                wrapS = GL11.GL_REPEAT;
                wrapT = GL11.GL_REPEAT;
        }
        
        if (texRecord.wrapS != wrapS) {
            checkAndSetUnit(unit, record);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                    GL11.GL_TEXTURE_WRAP_S, wrapS);
            texRecord.wrapS = wrapS;
        }
        if (texRecord.wrapT != wrapT) {
            checkAndSetUnit(unit, record);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                    GL11.GL_TEXTURE_WRAP_T, wrapT);
            texRecord.wrapT = wrapT;
        }
        
    }
    
    public static void checkTexAndUnit(int texID, int unitNo, String label) {
        IntBuffer fetch = BufferUtils.createIntBuffer(16);
        fetch.rewind();
        // are we in the right unit?
        GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE, fetch);
        int realUnit = fetch.get(0) - GL13.GL_TEXTURE0;
        if (realUnit != unitNo) {
            System.err.println(label+": expected unit: "+unitNo+"  actual unit: "+realUnit);
        }
        fetch.rewind();
        // are we in the right texture?
        GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D, fetch);
        int tex = fetch.get(0);
        if (tex != texID) {
            System.err.println(label+": expected tex: "+texID+"  actual tex: "+tex);
        }
    }

    public RenderState extract(Stack stack, SceneElement spat) {
        int mode = spat.getTextureCombineMode();
        if (mode == REPLACE || (mode != OFF && stack.size() == 1)) // todo: use
            // dummy
            // state if
            // off?
            return (LWJGLTextureState) stack.peek();

        // accumulate the textures in the stack into a single LightState object
        LWJGLTextureState newTState = new LWJGLTextureState();
        boolean foundEnabled = false;
        Object states[] = stack.toArray();
        switch (mode) {
            case COMBINE_CLOSEST:
            case COMBINE_RECENT_ENABLED:
                for (int iIndex = states.length - 1; iIndex >= 0; iIndex--) {
                    TextureState pkTState = (TextureState) states[iIndex];
                    if (!pkTState.isEnabled()) {
                        if (mode == COMBINE_RECENT_ENABLED)
                            break;

                        continue;
                    }

                    foundEnabled = true;
                    for (int i = 0, max = pkTState.getNumberOfSetTextures(); i < max; i++) {
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
                    if (!pkTState.isEnabled())
                        continue;

                    foundEnabled = true;
                    for (int i = 0; i < numTotalTexUnits; i++) {
                        Texture pkText = pkTState.getTexture(i);
                        if (newTState.getTexture(i) == null) {
                            newTState.setTexture(pkText, i);
                        }
                    }
                }
                break;
            case OFF:
                break;
        }
        newTState.setEnabled(foundEnabled);
        return newTState;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.scene.state.TextureState#delete(int)
     */
    public void delete(int unit) {
        if (unit < 0 || unit >= texture.size() || texture.get(unit) == null)
            return;
        
        // ask for the current state record
        RenderContext context = DisplaySystem.getDisplaySystem()
                .getCurrentContext();
        TextureStateRecord record = (TextureStateRecord) context
                .getStateRecord(RS_TEXTURE);

        Texture tex = texture.get(unit);
        int texId = tex.getTextureId();

        IntBuffer id = BufferUtils.createIntBuffer(1);
        id.clear();
        id.put(texId);
        id.rewind();
        tex.setTextureId(0);

        GL11.glDeleteTextures(id);
        
        // if the texture was currently bound glDeleteTextures reverts the binding to 0
        // however we still have to clear it from currentTexture.
        record.removeTextureRecord(texId);
        idCache[unit] = 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.scene.state.TextureState#deleteAll()
     */
    public void deleteAll() {
        deleteAll(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.scene.state.TextureState#deleteAll()
     */
    public void deleteAll(boolean removeFromCache) {
        
        // ask for the current state record
        RenderContext context = DisplaySystem.getDisplaySystem()
                .getCurrentContext();
        TextureStateRecord record = (TextureStateRecord) context
                .getStateRecord(RS_TEXTURE);

        IntBuffer id = BufferUtils.createIntBuffer(texture.size());

        for (int i = 0; i < texture.size(); i++) {
            Texture tex = texture.get(i);
            if (removeFromCache) TextureManager.releaseTexture(tex);
            int texId = tex.getTextureId();
            if (tex == null)
                continue;
            id.put(texId);
            tex.setTextureId(0);

            // if the texture was currently bound glDeleteTextures reverts the binding to 0
            // however we still have to clear it from currentTexture.
            record.removeTextureRecord(texId);
            idCache[i] = 0;
        }

        // Now delete them all from GL in one fell swoop.
        id.rewind();
        GL11.glDeleteTextures(id);
    }

    public void deleteTextureId(int textureId) {
        
        // ask for the current state record
        RenderContext context = DisplaySystem.getDisplaySystem()
                .getCurrentContext();
        TextureStateRecord record = (TextureStateRecord) context
                .getStateRecord(RS_TEXTURE);

        IntBuffer id = BufferUtils.createIntBuffer(1);
        id.clear();
        id.put(textureId);
        id.rewind();
        GL11.glDeleteTextures(id);
        record.removeTextureRecord(textureId);
    }

    @Override
    public StateRecord createStateRecord() {
        return new TextureStateRecord(numTotalTexUnits);
    }
}