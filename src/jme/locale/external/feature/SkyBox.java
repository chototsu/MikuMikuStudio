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
 
package jme.locale.external.feature;

import java.util.logging.Level;

import jme.exception.MonkeyRuntimeException;
import jme.system.DisplaySystem;
import jme.texture.TextureManager;
import jme.utility.LoggingSystem;

import org.lwjgl.opengl.GL;
import org.lwjgl.vector.Vector3f;

/**
 * <code>SkyBox</code> defines a implementation of the sky interface
 * where the sky is defined by a cube with a texture on each of
 * the six faces. The cube is centered at the position of the player. The
 * depth buffer for the skybox is turn off so it will always appear behind
 * any object/locale rendered. The skybox moves with the connected entity.
 * @author Mark Powell
 * @version 0.1.0
 */
public class SkyBox implements Sky {
	/**
	 * Defines the index to the top texture of the skybox
	 */
	public static final int TOP = 0;
	/**
	 * Defines the index to the bottom texture of the skybox
	 */
	public static final int BOTTOM = 1;
	/**
	 * Defines the index to the left texture of the skybox
	 */
	public static final int LEFT = 2;
	/**
	 * Defines the index to the right texture of the skybox
	 */
	public static final int RIGHT = 3;
	/**
	 * Defines the index to the front texture of the skybox
	 */
	public static final int FRONT = 4;
	/**
	 * Defines the index to the back texture of the skybox
	 */
	public static final int BACK = 5;

	//area of the box
	private Vector3f center;
	private float size;

	//appearance of box
	private int[] textures;
	private Vector3f color;

	//gl object for rendering.
	private GL gl;

	/**
	 * Constructor creates a new <code>SkyBox</code> object. The size of
	 * the box is given by the size parameter where the box is centered
	 * at (size/2, size/2, size/2). 
	 * @param size the size of each side of the box.
	 */
	public SkyBox(float size) {
		center = new Vector3f();
		textures = new int[6];
		gl = DisplaySystem.getDisplaySystem().getGL();
		this.size = size / 2;
		this.center = new Vector3f(this.size, 32, this.size);
		color = new Vector3f(1.0f, 1.0f, 1.0f);

		LoggingSystem.getLoggingSystem().getLogger().log(
			Level.INFO,
			"Created SkyBox");
	}

	/**
	 * <code>setTexture</code> sets a specified side texture to
	 * a given image texture. The image will be loaded as a
	 * texture and assigned to the proper side.
	 * @param side what side to set teh texture to.
	 * @param filename the image file to use as the texture.
	 * @throws MonkeyRuntimeException if the side is invalid.
	 */
	public void setTexture(int side, String filename) {
		if (side < 0 || side > 5) {
			throw new MonkeyRuntimeException("Invalid side.");
		}

		textures[side] = TextureManager.getTextureManager().loadTexture(
				filename,
				GL.LINEAR_MIPMAP_LINEAR,
				GL.LINEAR,
				true);
	}
	
	/**
	 * <code>setTextures</code> takes an array of image file names. Each
	 * texture is then loaded from the file and set as the corresponding
	 * side's texture.
	 * @param textures array of texture file names.
	 * @throws MonkeyRuntimeException if the textures array is null or 
	 * 		not of size 6.
	 */
	public void setTextures(String[] textures) {
		if (null == textures || textures.length != 6) {
			throw new MonkeyRuntimeException("Texture array cannot be null " +
					"and must be of length 6.");
		}
		
		
		for (int i = 0; i < 6; i++) {
			this.textures[i] = TextureManager.getTextureManager().loadTexture(
					textures[i],
					GL.LINEAR_MIPMAP_LINEAR,
					GL.LINEAR,
					true);
		}
	}

	/**
     * <code>getSize</code> returns the size of the sky box.
     * @return the size of a sky box side.
     */
    public float getSize() {
        return size;
    }

	/**
	 * <code>setColor</code> sets the color tint of the skybox. 
	 * @param color the tint of the box.
	 */
	public void setColor(Vector3f color) {
		if (null == color) {
			throw new MonkeyRuntimeException("Color cannot be null.");
		}
		this.color = color;
	}

	/**
	 * <code>update</code> does nothing, as the skybox is a static display.
	 */
	public void update(float time) {
		//do nothing
	}

	/**
	 * <code>render</code> displays the skybox to the viewport. Each
	 * side is rendered as a triangle fan with it's normal facing 
	 * inward.
	 */
	public void render() {

		gl.color4f(color.x, color.y, color.z, 1.0f);
		gl.enable(GL.TEXTURE_2D);
		gl.disable(GL.DEPTH_TEST);

		gl.pushMatrix();
		gl.translatef(center.x, center.y, center.z);

		//front face
		TextureManager.getTextureManager().bind(textures[FRONT]);
		gl.begin(GL.TRIANGLE_FAN);
		gl.texCoord2f(1.0f, 1.0f);
		gl.vertex3f(size, size, size);
		gl.texCoord2f(1.0f, 0.0f);
		gl.vertex3f(size, -size, size);
		gl.texCoord2f(0.0f, 0.0f);
		gl.vertex3f(-size, -size, size);
		gl.texCoord2f(0.0f, 1.0f);
		gl.vertex3f(-size, size, size);
		gl.end();

		//back face
		TextureManager.getTextureManager().bind(textures[BACK]);
		gl.begin(GL.TRIANGLE_FAN);
		gl.texCoord2f(1.0f, 1.0f);
		gl.vertex3f(-size, size, -size);
		gl.texCoord2f(1.0f, 0.0f);
		gl.vertex3f(-size, -size, -size);
		gl.texCoord2f(0.0f, 0.0f);
		gl.vertex3f(size, -size, -size);
		gl.texCoord2f(0.0f, 1.0f);
		gl.vertex3f(size, size, -size);
		gl.end();

		//right face
		TextureManager.getTextureManager().bind(textures[RIGHT]);
		gl.begin(GL.TRIANGLE_FAN);
		gl.texCoord2f(1.0f, 1.0f);
		gl.vertex3f(size, size, -size);
		gl.texCoord2f(1.0f, 0.0f);
		gl.vertex3f(size, -size, -size);
		gl.texCoord2f(0.0f, 0.0f);
		gl.vertex3f(size, -size, size);
		gl.texCoord2f(0.0f, 1.0f);
		gl.vertex3f(size, size, size);
		gl.end();

		//left face
		TextureManager.getTextureManager().bind(textures[LEFT]);
		gl.begin(GL.TRIANGLE_FAN);
		gl.texCoord2f(1.0f, 1.0f);
		gl.vertex3f(-size, size, size);
		gl.texCoord2f(1.0f, 0.0f);
		gl.vertex3f(-size, -size, size);
		gl.texCoord2f(0.0f, 0.0f);
		gl.vertex3f(-size, -size, -size);
		gl.texCoord2f(0.0f, 1.0f);
		gl.vertex3f(-size, size, -size);
		gl.end();

		//top face
		TextureManager.getTextureManager().bind(textures[TOP]);
		gl.begin(GL.TRIANGLE_FAN);
		gl.texCoord2f(0.0f, 0.0f);
		gl.vertex3f(-size, size, size);
		gl.texCoord2f(0.0f, 1.0f);
		gl.vertex3f(-size, size, -size);
		gl.texCoord2f(1.0f, 1.0f);
		gl.vertex3f(size, size, -size);
		gl.texCoord2f(1.0f, 0.0f);
		gl.vertex3f(size, size, size);
		gl.end();

		//bottom face
		TextureManager.getTextureManager().bind(textures[BOTTOM]);
		gl.begin(GL.TRIANGLE_FAN);
		gl.texCoord2f(0.0f, 0.0f);
		gl.vertex3f(-size, -size, -size);
		gl.texCoord2f(0.0f, 1.0f);
		gl.vertex3f(-size, -size, size);
		gl.texCoord2f(1.0f, 1.0f);
		gl.vertex3f(size, -size, size);
		gl.texCoord2f(1.0f, 0.0f);
		gl.vertex3f(size, -size, -size);
		gl.end();
		gl.popMatrix();
		
		gl.enable(GL.DEPTH_TEST);
	}

}
