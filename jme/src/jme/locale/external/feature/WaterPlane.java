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

import jme.exception.MonkeyRuntimeException;
import jme.math.Vector;
import jme.texture.TextureManager;

import org.lwjgl.opengl.GL;

/**
 * <code>WaterPlane</code> implements the <code>Water</code> interface and
 * defines a water feature to be used with <code>Terrain</code> levels. The
 * water is defined by a single quad with an applied texture and animation
 * values. The supported animation is the water plane rising and falling and
 * the texture sliding across the plane.
 * 
 * @author Mark Powell
 * @version $Id: WaterPlane.java,v 1.1.1.1 2003-10-29 10:58:27 Anakan Exp $
 */
public class WaterPlane implements Water {

	//The texture map.
	private int texId;
	//number of times to repeat the texture across the quad.
	private float repeat = 16;
	//size of the quad (size x size)
	private int size;
	//the mean elevation of the water
	private float baseLevel;
	//the amount the water rises and falls. min = baseLevel - variation.
	//max = baseLevel + variation.
	private float variation;
	//color of the water and transparency of the water.
	private Vector color;
	private float transparency;
	
	//current height of the water.
	private float currentLevel;
	//how fast the water moves up and down
	private float waveSpeed;
	
	//speed to slide the water across the quad.
	private float xSpeed;
	private float zSpeed;
	//current location of the texture on the quad.
	private float changeX = 0.0f;
	private float changeZ = 0.0f;
	
	
	/**
	 * Constructor instantiates a new <code>WaterPlane</code> object. 
	 * The size of the plane is defined, as well as what elevation to
	 * set it at and how much to vary the elevation.
	 * @param size the size of the water plane.
	 * @param baseLevel the starting elevation of the plane.
	 * @param variation the amount to vary to height of the plane (base +- variation)
	 * @throws MonkeyRuntimeException if the size is less than zero.
	 */
	public WaterPlane(int size, float baseLevel, float variation) {
		if(size <= 0) {
			throw new MonkeyRuntimeException("size must be greater than zero.");
		}
		this.size = size;
		this.baseLevel = baseLevel;
		this.variation = variation;
		
	}

	/**
	 * <code>setTexture</code> sets the texture for the water plane.
	 * @param filename the image file to load the texture from.
	 * @throws MonkeyRuntimeException if filename is null.
	 */
	public void setTexture(String filename) {
		if(null == filename) {
			throw new MonkeyRuntimeException("file name cannot be null.");
		}
		texId = TextureManager.getTextureManager().loadTexture(
				filename,
				GL.GL_LINEAR_MIPMAP_LINEAR,
				GL.GL_LINEAR,
				true);
	}

	/**
	 * <code>setTextureAnimation</code> denotes the amount to move
	 * the texture across the water plane. Both positive and negative
	 * values are supported to allow for animation in all directions.
	 * @param x the amount to move the texture on the x axis.
	 * @param z the amount to move the texture on the z axis.
	 */
	public void setTextureAnimation(float x, float z) {
		xSpeed = x;
		zSpeed = z;
	}

	/**
	 * <code>setWaveSpeed</code> sets the speed to move the water 
	 * plane up/down. 
	 * @param speed the speed to move the water plane on the y-axis.
	 */
	public void setWaveSpeed(float speed) {
		waveSpeed = speed;
	}

	/**
	 * <code>setColor</code> sets the color of the water texture.
	 * @param color the color of the water texture.
	 */
	public void setColor(Vector color) {
		this.color = color;
	}

	/**
	 * <code>setTransparency</code> sets the transparency or alpha
	 * of the water plane. Completely opaque is 1 and completely 
	 * transparent is 0.
	 * @param transparency the level of transparency for the water plane.
	 */
	public void setTransparency(float transparency) {
		this.transparency = transparency;
	}
	
	/**
	 * <code>setRepeat</code> sets the number of times to repeat the
	 * water texture across the quad. By default it will be repeated
	 * 16 times.
	 * @param repeat the number of times to repeat the water texture.
	 */
	public void setRepeat(float repeat) {
		this.repeat = repeat;
	}

	/**
	 * <code>update</code> updates the location of the water plane
	 * and the setting for the texture. The rise/fall of the plane's 
	 * height is adjusted as well as the texture coordinates for
	 * animating.
	 */
	public void update(float time) {
		currentLevel += waveSpeed * time;
		if(currentLevel > (baseLevel + variation)) {
			currentLevel = (baseLevel + variation);
			waveSpeed *= -1;
		} else if(currentLevel < (baseLevel - variation)) {
			currentLevel = baseLevel - variation;
			waveSpeed *= -1;
		}
		
		changeX += xSpeed * time / 100;
		changeZ += zSpeed * time / 100;
	
		if(changeX > repeat) {
			changeX = 0;
		}
		if(changeZ > repeat) {
			changeZ = 0;
		}
	}

	/**
	 * <code>render</code> renders a single quad with the set
	 * texture, color and location.
	 */
	public void render() {
		GL.glEnable(GL.GL_BLEND);
		GL.glEnable(GL.GL_TEXTURE_2D);
		GL.glEnable(GL.GL_DEPTH_TEST);
		GL.glDisable(GL.GL_CULL_FACE);
		GL.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
		
		GL.glColor4f(color.x, color.y, color.z, transparency);
		
		TextureManager.getTextureManager().bind(texId);
		
		GL.glBegin(GL.GL_TRIANGLE_STRIP);
		
		
		GL.glTexCoord2f(changeX, changeZ);
		GL.glVertex3f(0.0f, currentLevel, 0.0f);
		GL.glTexCoord2f(changeX + repeat, changeZ);
		GL.glVertex3f(size, currentLevel, 0.0f);
		GL.glTexCoord2f(changeX, changeZ+repeat);
		GL.glVertex3f(0.0f, currentLevel, size);
		GL.glTexCoord2f(changeX + repeat, changeZ + repeat);
		GL.glVertex3f(size, currentLevel, size);
		
		GL.glEnd();
		
		GL.glDisable(GL.GL_BLEND);
		GL.glDisable(GL.GL_TEXTURE_2D);
		GL.glDisable(GL.GL_DEPTH_TEST);
		GL.glEnable(GL.GL_CULL_FACE);
	}

}
