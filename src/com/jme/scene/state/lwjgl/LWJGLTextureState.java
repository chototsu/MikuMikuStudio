/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved.
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

import java.nio.IntBuffer;
import java.util.Stack;
import java.util.logging.Level;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.glu.GLU;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.scene.Spatial;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.util.LoggingSystem;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import java.nio.FloatBuffer;
import java.io.IOException;

/**
 * <code>LWJGLTextureState</code> subclasses the TextureState object using the
 * LWJGL API to access OpenGL for texture processing.
 * 
 * @author Mark Powell
 * @version $Id: LWJGLTextureState.java,v 1.35 2004-09-14 21:52:14 mojomonkey Exp $
 */
public class LWJGLTextureState extends TextureState {

	private static final long serialVersionUID = 1L;

	private static Texture[] currentTexture;

	//OpenGL texture attributes.
	private static int[] textureCorrection = { GL11.GL_FASTEST, GL11.GL_NICEST };

	private static int[] textureApply = { GL11.GL_REPLACE, GL11.GL_DECAL,
			GL11.GL_MODULATE, GL11.GL_BLEND, GL13.GL_COMBINE };

	private static int[] textureFilter = { GL11.GL_NEAREST, GL11.GL_LINEAR };

	private static int[] textureMipmap = { GL11.GL_NEAREST, // MM_NONE (no
															// mipmap)
			GL11.GL_NEAREST, GL11.GL_LINEAR, GL11.GL_NEAREST_MIPMAP_NEAREST,
			GL11.GL_NEAREST_MIPMAP_LINEAR, GL11.GL_LINEAR_MIPMAP_NEAREST,
			GL11.GL_LINEAR_MIPMAP_LINEAR };

	private static int[] textureCombineFunc = { GL11.GL_REPLACE,
			GL11.GL_MODULATE, GL11.GL_ADD, GL13.GL_ADD_SIGNED,
			GL13.GL_SUBTRACT, GL13.GL_INTERPOLATE, GL13.GL_DOT3_RGB,
			GL13.GL_DOT3_RGBA };

	private static int[] textureCombineSrc = { GL11.GL_TEXTURE,
			GL13.GL_PRIMARY_COLOR, GL13.GL_CONSTANT, GL13.GL_PREVIOUS };

	private static int[] textureCombineOpRgb = { GL11.GL_SRC_COLOR,
			GL11.GL_ONE_MINUS_SRC_COLOR, GL11.GL_SRC_ALPHA,
			GL11.GL_ONE_MINUS_SRC_ALPHA };

	// first two entries are mostly dummy (except for def = 0) since our alpha's
	// only use 3 and 4
	private static int[] textureCombineOpAlpha = { GL11.GL_SRC_ALPHA,
			GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_SRC_ALPHA,
			GL11.GL_ONE_MINUS_SRC_ALPHA };

	private static int[] imageComponents = { GL11.GL_RGBA4, GL11.GL_RGB8,
			GL11.GL_RGB5_A1, GL11.GL_RGBA8, GL11.GL_LUMINANCE8_ALPHA8 };

	private static int[] imageFormats = { GL11.GL_RGBA, GL11.GL_RGB,
			GL11.GL_RGBA, GL11.GL_RGBA, GL11.GL_LUMINANCE_ALPHA };

	private transient IntBuffer id = BufferUtils.createIntBuffer(1);

	/**
	 * Constructor instantiates a new <code>LWJGLTextureState</code> object.
	 * The number of textures that can be combined is determined during
	 * construction. This equates the number of texture units supported by the
	 * graphics card.
	 *  
	 */
	public LWJGLTextureState() {
		super();
		supportsMultiTexture = (GLContext.GL_ARB_multitexture && GLContext.OpenGL13);
		if (numTexUnits == 0) {
			if (supportsMultiTexture) {
				IntBuffer buf = BufferUtils.createIntBuffer(16); //ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
				GL11.glGetInteger(GL13.GL_MAX_TEXTURE_UNITS, buf);
				numTexUnits = buf.get(0);
			} else {
				numTexUnits = 1;
			}
		}

		if (currentTexture == null)
			currentTexture = new Texture[numTexUnits];

		if (maxAnisotropic == -1.0
				&& GLContext.GL_EXT_texture_filter_anisotropic) {
			// Due to LWJGL buffer check, you can't use smaller sized buffers
			// (min_size = 16 for glGetFloat()).
			FloatBuffer max_a = BufferUtils.createFloatBuffer(16);
			max_a.rewind();

			// Grab the maximum anisotropic filter.
			GL11
					.glGetFloat(
							EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT,
							max_a);

			// set max.
			maxAnisotropic = max_a.get(0);
		}
		texture = new Texture[numTexUnits];
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

		if (isEnabled()) {
			int index;
			Texture texture;
			for (int i = 0; i < numTexUnits; i++) {
				texture = getTexture(i);
				if (texture == currentTexture[i])
					continue;
				currentTexture[i] = texture;

				index = GL13.GL_TEXTURE0 + i;

				if (supportsMultiTexture) {
					GL13.glActiveTexture(index);
				}

				if (texture == null) {
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					continue;
				} else
					GL11.glEnable(GL11.GL_TEXTURE_2D);

				//texture not yet loaded.
				if (texture.getTextureId() == 0) {
					// Create A IntBuffer For Image Address In Memory

					//Create the texture
					id.clear();
					GL11.glGenTextures(id);

					GL11.glBindTexture(GL11.GL_TEXTURE_2D, id.get(0));

					texture.setTextureId(id.get(0));

					// pass image data to OpenGL
					Image image = texture.getImage();
					if (image == null) {
						LoggingSystem.getLogger().log(Level.WARNING,
								"Image data for texture is null.");
					}

					// Set up the anisotropic filter.
					if (GLContext.GL_EXT_texture_filter_anisotropic)
						GL11
								.glTexParameterf(
										GL11.GL_TEXTURE_2D,
										EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
										texture.getAnisoLevel());

					// Get texture image data. Not all textures have image data.
					// For example, AM_COMBINE modes can use primary colors,
					// texture output, and constants to modify fragments via the
					// texture units.
					if (image != null) {
						if (texture.getMipmap() == Texture.MM_NONE) {
							GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0,
									imageComponents[image.getType()], image
											.getWidth(), image.getHeight(), 0,
									imageFormats[image.getType()],
									GL11.GL_UNSIGNED_BYTE, image.getData());
						} else {
							GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D,
									imageComponents[image.getType()], image
											.getWidth(), image.getHeight(),
									imageFormats[image.getType()],
									GL11.GL_UNSIGNED_BYTE, image.getData());
						}
					}
					switch (texture.getWrap()) {
					case Texture.WM_ECLAMP_S_ECLAMP_T:
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
								GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
								GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
						break;
					case Texture.WM_BCLAMP_S_BCLAMP_T:
						GL11
								.glTexParameteri(GL11.GL_TEXTURE_2D,
										GL11.GL_TEXTURE_WRAP_S,
										GL13.GL_CLAMP_TO_BORDER);
						GL11
								.glTexParameteri(GL11.GL_TEXTURE_2D,
										GL11.GL_TEXTURE_WRAP_T,
										GL13.GL_CLAMP_TO_BORDER);
						break;
					case Texture.WM_CLAMP_S_CLAMP_T:
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
								GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
								GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
						break;
					case Texture.WM_CLAMP_S_WRAP_T:
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
								GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
								GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
						break;
					case Texture.WM_WRAP_S_CLAMP_T:
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
								GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
								GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
						break;
					case Texture.WM_WRAP_S_WRAP_T:
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
								GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
								GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
						break;
					}
				} else {
					// texture already exists in OpenGL, just bind it
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture
							.getTextureId());
				}

				// If wrap mode was changed or this texture is new...
				if (texture.needsWrapRefresh()) {
					texture.setNeedsWrapRefresh(false);
					// set up wrap mode
					switch (texture.getWrap()) {
					case Texture.WM_ECLAMP_S_ECLAMP_T:
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
								GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
								GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
						break;
					case Texture.WM_BCLAMP_S_BCLAMP_T:
						GL11
								.glTexParameteri(GL11.GL_TEXTURE_2D,
										GL11.GL_TEXTURE_WRAP_S,
										GL13.GL_CLAMP_TO_BORDER);
						GL11
								.glTexParameteri(GL11.GL_TEXTURE_2D,
										GL11.GL_TEXTURE_WRAP_T,
										GL13.GL_CLAMP_TO_BORDER);
						break;
					case Texture.WM_CLAMP_S_CLAMP_T:
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
								GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
								GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
						break;
					case Texture.WM_CLAMP_S_WRAP_T:
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
								GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
								GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
						break;
					case Texture.WM_WRAP_S_CLAMP_T:
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
								GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
								GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
						break;
					case Texture.WM_WRAP_S_WRAP_T:
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
								GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
								GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
						break;
					}
				}

				// If texture data was changed, (eg. render to texture) the
				// filtering needs to be redone.
				if (texture.needsFilterRefresh()) {
					texture.setNeedsFilterRefresh(false);

					// set up filter mode
					GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
							GL11.GL_TEXTURE_MAG_FILTER, textureFilter[texture
									.getFilter()]);

					// set up mipmap mode
					GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
							GL11.GL_TEXTURE_MIN_FILTER, textureMipmap[texture
									.getMipmap()]);
				}

				// set up correction mode
				GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT,
						textureCorrection[texture.getCorrection()]);

				if (texture.getApply() == Texture.AM_COMBINE
						&& supportsMultiTexture) {

					GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
							GL11.GL_TEXTURE_ENV_MODE, textureApply[texture
									.getApply()]);

					if (texture.getCombineFuncAlpha() == Texture.ACF_DOT3_RGB
							|| texture.getCombineFuncAlpha() == Texture.ACF_DOT3_RGBA) {
						GL11.glDisable(GL11.GL_TEXTURE_2D);
						break;
					}

					if (texture.getCombineFuncRGB() == Texture.ACF_DOT3_RGB
							|| texture.getCombineFuncRGB() == Texture.ACF_DOT3_RGBA) {
						// check if supported before proceeding
						if (!GLContext.GL_ARB_texture_env_dot3) {
							GL11.glDisable(GL11.GL_TEXTURE_2D);
							break;
						}
					}
					int cf = texture.getCombineFuncRGB();
					GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_RGB,
							textureCombineFunc[cf]);
					GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_RGB,
							textureCombineSrc[texture.getCombineSrc0RGB()]);
					GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_RGB,
							textureCombineOpRgb[texture.getCombineOp0RGB()]);
					if (cf != Texture.ACF_REPLACE) {
						GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
								GL13.GL_SOURCE1_RGB, textureCombineSrc[texture
										.getCombineSrc1RGB()]);
						GL11
								.glTexEnvi(GL11.GL_TEXTURE_ENV,
										GL13.GL_OPERAND1_RGB,
										textureCombineOpRgb[texture
												.getCombineOp1RGB()]);
						if (cf == Texture.ACF_INTERPOLATE) {
							GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
									GL13.GL_OPERAND2_RGB,
									textureCombineOpRgb[texture
											.getCombineOp2RGB()]);
							GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
									GL13.GL_SOURCE2_RGB,
									textureCombineSrc[texture
											.getCombineSrc2RGB()]);
						}
					}

					cf = texture.getCombineFuncAlpha();
					GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_ALPHA,
							textureCombineFunc[cf]);
					GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_ALPHA,
							textureCombineSrc[texture.getCombineSrc0Alpha()]);
					GL11
							.glTexEnvi(GL11.GL_TEXTURE_ENV,
									GL13.GL_OPERAND0_ALPHA,
									textureCombineOpAlpha[texture
											.getCombineOp0Alpha()]);
					if (cf != Texture.ACF_REPLACE) {
						GL11
								.glTexEnvi(GL11.GL_TEXTURE_ENV,
										GL13.GL_SOURCE1_ALPHA,
										textureCombineSrc[texture
												.getCombineSrc1Alpha()]);
						GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
								GL13.GL_OPERAND1_ALPHA,
								textureCombineOpAlpha[texture
										.getCombineOp1Alpha()]);
						if (cf == Texture.ACF_INTERPOLATE) {
							GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
									GL13.GL_SOURCE2_ALPHA,
									textureCombineSrc[texture
											.getCombineSrc2Alpha()]);
							GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
									GL13.GL_OPERAND2_ALPHA,
									textureCombineOpAlpha[texture
											.getCombineOp2Alpha()]);
						}
					}

					GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL13.GL_RGB_SCALE,
							texture.getCombineScaleRGB());
					GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_ALPHA_SCALE,
							texture.getCombineScaleAlpha());

				} else {
					// set up apply mode
					GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
							GL11.GL_TEXTURE_ENV_MODE, textureApply[texture
									.getApply()]);
				}

				if (texture.getEnvironmentalMapMode() == Texture.EM_IGNORE) {
					// Do not alter the texure generation status. This allows
					// complex texturing outside of texture state to exist
					// peacefully.
				} else if (texture.getEnvironmentalMapMode() == Texture.EM_NONE) {
					// turn off anything that other maps might have turned on
					GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
					GL11.glDisable(GL11.GL_TEXTURE_GEN_R);
					GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
					GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
				} else if (texture.getEnvironmentalMapMode() == Texture.EM_SPHERE) {
					// generate texture coordinates
					GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE,
							GL11.GL_SPHERE_MAP);
					GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE,
							GL11.GL_SPHERE_MAP);
					GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
					GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
				}

				GL11.glTexEnv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR,
						texture.getBlendColor());
			}

			if (supportsMultiTexture) {
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
			}
		} else {
			if (supportsMultiTexture) {
				for (int i = 0; i < numTexUnits; i++) {
					GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					currentTexture[i] = null;
				}
			} else {
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				currentTexture[0] = null;
			}
		}
	}

	public RenderState extract(Stack stack, Spatial spat) {
		int mode = spat.getTextureCombineMode();
		if (mode == REPLACE)
			return (LWJGLTextureState) stack.peek();

		// accumulate the lights in the stack into a single LightState object
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
					else
						continue;
				} else
					foundEnabled = true;
				for (int i = 0; i < numTexUnits; i++) {
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
				else
					foundEnabled = true;
				for (int i = 0; i < numTexUnits; i++) {
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
		if (unit < 0 || unit >= numTexUnits || texture[unit] == null)
			return;
		id.clear();
		id.put(texture[unit].getTextureId());
		id.rewind();
		texture[unit].setTextureId(0);
		GL11.glDeleteTextures(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.scene.state.TextureState#deleteAll()
	 */
	public void deleteAll() {
		for (int i = 0; i < numTexUnits; i++) {
			if (texture[i] == null)
				continue;
			id.clear();
			id.put(texture[i].getTextureId());
			id.rewind();
			GL11.glDeleteTextures(id);
			texture[i].setTextureId(0);
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		id = BufferUtils.createIntBuffer(1);
	}
}