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

package jme.locale.external;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;

import javax.swing.ImageIcon;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL;

import jme.exception.MonkeyRuntimeException;
import jme.lighting.AbstractLightMap;
import jme.locale.Locale;
import jme.locale.external.data.AbstractHeightMap;
import jme.texture.TextureManager;
import jme.utility.LoggingSystem;

/**
 * <code>Terrain</code> defines an abstract class for rendering height map 
 * data. The subclass is responsible for setting the height data as well as
 * updating and rendering.
 * 
 * @author Mark Powell
 * @version 0.1.0 
 */
public abstract class Terrain implements Locale {
	
	/**
	 * <code>terrainTexture</code> is the texture id for the terrain map.
	 */
	protected int terrainTexture;
	/**
	 * <code>detailId</code> is the texture id for the detail map.
	 */
	protected int detailId;
	/**
	 * <code>terrainSize</code> contains the size of the terrain where the
	 * area is terrainSize x terrainSize.
	 */
	protected int terrainSize;
	/**
	 * <code>isTextured</code> denotes if a texture map is to be applied 
	 * to the terrain.
	 */
	protected boolean isTextured;
	/**
	 * <code>isLit</code> denotes if a light map is to be used.
	 */
	protected boolean isLit;
	/**
	 * <code>isDetailed</code> denotes if multitextured detail mapping is
	 * to be used.
	 */
	protected boolean isDetailed;
	/**
	 * <code>repeatDetailMap</code> the number of times to repeat the
	 * detail texture across the terrain.
	 */
	protected int repeatDetailMap;
	/**
	 * <code>lightMap</code> the map used to contain shade values.
	 */
	protected AbstractLightMap lightMap;
	/**
	 * <code>heightData</code> the height map used to represent the terrain.
	 */
	protected AbstractHeightMap heightData;
	/**
	 * <code>gl</code> the OpenGL context.
	 */
	protected GL gl;
    /**
     * <code>xScale</code> is the scale of the terrain in the x-axis. Default
     * is 1.0f.
     */
    protected float xScale = 1.0f;
    /**
     * <code>zScale</code> is the scale of the terrain in the z-axis. Default 
     * is 1.0f.
     */
    protected float zScale = 1.0f;
    
    /**
     * <code>useVolumeFog</code> denotes if volumetric fog is being used or not.
     */
    protected boolean useVolumeFog;
    /**
     * <code>fogDepth</code> holds the depth of the volumetric fog.
     */
    private float fogDepth = 0.0f;
    /**
     * <code>useDistanceFog</code> denotes if distance fog is being used or not.
     */
    protected boolean useDistanceFog;
    
    /**
     * <code>setXScale</code> sets the scale of the terrain along the x-axis.
     * @param scale the new x scale.
     */
    public void setXScale(float scale) {
        xScale = scale;
    }
    
    /**
     * <code>setZScale</code> sets the scale of the terrain along the z-axis.
     * @param scale the new z scale.
     */
    public void setZScale(float scale) {
        zScale = scale;
    }
    
	/**
	 * <code>setFogAttributes</code> sets the attributes for how
	 * fog will be used. 
	 * @param mode what GL mode to render the fog as (GL.EXP, GL.EXP2, GL.LINEAR).
	 * @param color the color of the fog.
	 * @param density how thick the fog will be.
	 * @param start what distance to start the fog.
	 * @param end what distance the fog will be at full capacity.
	 */
	public void setFogAttributes(int mode, float[] color, 
			float density, float start, float end) {
		ByteBuffer temp = ByteBuffer.allocateDirect(16);
		temp.order(ByteOrder.nativeOrder());

		gl.fogi(GL.FOG_MODE, mode);
		gl.fogfv(GL.FOG_COLOR, 
			Sys.getDirectBufferAddress(
			temp.asFloatBuffer().put(color)));
		gl.fogf(GL.FOG_DENSITY, density);
		gl.fogf(GL.FOG_START, start);
		gl.fogf(GL.FOG_END, end);
	}
	
	/**
	 * <code>useDistanceFog</code> returns true if distance fog is turned on,
	 * false otherwise.
	 * @return true if distance fog is on, false if it is off.
	 */
	public boolean useDistanceFog() {
		return useDistanceFog;
	}

	/**
	 * <code>useVolumetricFog</code> returns true if volumetric fog is turned on,
	 * false otherwise.
	 * @return true if distance fog is on, false if it is off.
	 */
	public boolean useVolumetricFog() {
		return useVolumeFog;
	}

	/**
	 * <code>useDistanceFog</code> determines if distance-based fog 
	 * will be used. If true is passed it is enabled otherwise, it is
	 * disabled.
	 * @param value true if distance fog is to be used, false otherwise.
	 */
	public void setDistanceFog(boolean value) {
		useDistanceFog = value;
		if(value) {
			gl.enable(GL.FOG);
		} else {
			gl.disable(GL.FOG);
		}
	}
	
	/**
	 * <code>setVolumetricFogDepth</code> sets the depth of the volumetric
	 * fog.
	 * @param depth the depth of the volumetric fog.
	 */
	public void setVolumetricFogDepth(float depth) {
		fogDepth = depth;
	}
	
	/**
	 * <code>setVolumetricFog</code> determines if vertex-based fog will
	 * be used. If true is passed it is enabled, otherwise it is disabled.
	 * 
	 * @param value true if volumetric fog is to be used, false otherwise.
	 */
	public void setVolumetricFog(boolean value) {
		useVolumeFog = value;
		
		if(value) {
			gl.fogi( GL.FOG_COORDINATE_SOURCE_EXT, GL.FOG_COORDINATE_EXT );
			gl.enable(GL.FOG);
		} else {
			gl.disable(GL.FOG);
		}
	}
	
	/**
	 * <code>setVolumetricFogCoord</code> sets the level of the fog coordinate.
	 * This is dependant on the fogDepth and the height of the terrain.
	 * @param height the height of the terrain.
	 */
	protected void setVolumetricFogCoord(float height) {
		if(height > fogDepth) {
			gl.fogCoordfEXT(-(height-fogDepth));
		} else {
			gl.fogCoordfEXT(0);
		}
	}

	/**
	 * <code>setDetailTexture</code> sets texture to use for detail texturing.
	 * <code>setDetailTexture</code> takes care of determining if the graphics
	 * card can support multitexturing and will turn if off it it doesn't.
	 * @param detailTexture the image file to use for detail texturing.
	 * @param repeat the number of times to repeat the detail texture.
	 */
	public void setDetailTexture(String detailTexture, int repeat) {

		this.repeatDetailMap = repeat;
		boolean canMulti = gl.ARB_multitexture;

		if (!canMulti) {
			LoggingSystem.getLoggingSystem().getLogger().log(
				Level.WARNING,
				"Graphics does not support multitexturing.");
		}

		if(null != detailTexture && canMulti) {		
			detailId = TextureManager.getTextureManager().loadTexture(
					detailTexture,
					GL.LINEAR_MIPMAP_LINEAR,
					GL.LINEAR,
					true);
			isDetailed = true;
		} else {
			isDetailed = false;
		}
	}

	/**
	 * <code>setTexture</code> sets the texture used to render the terrain.
	 * The texture can be null with will turn off texturing.
	 * @param texture the texture to use for the terrain.
	 */
	public void setTexture(String texture) {
		
		if (texture != null) {
			if((terrainTexture = TextureManager.getTextureManager().loadTexture(
					texture,
					GL.LINEAR_MIPMAP_LINEAR,
					GL.LINEAR,
					true)) != -1) {
				isTextured = true;
			}
		} else {
			isTextured = false;
		}
	}

	/**
	 * <code>setTexture</code> sets the texture used to render the terrain.
	 * The texture can be null with will turn off texturing.
	 * @param texture the texture to use for the terrain.
	 */
	public void setTexture(ImageIcon texture) {
		
		if (texture != null) {
			if((terrainTexture = TextureManager.getTextureManager().loadTexture(
					texture,
					GL.LINEAR_MIPMAP_LINEAR,
					GL.LINEAR,
					true)) != -1) {
				isTextured = true;
			}
		} else {
			isTextured = false;
		}
	}

	/**
	 * <code>setLightMap</code> 
	 * @param lightMap
	 */
	public void setLightMap(AbstractLightMap lightMap) {
		this.lightMap = lightMap;
		isLit = true;
	}
	
	/**
	 * <code>setHeightData</code> takes a <code>AbstractHeightMap</code>
	 * to allow the change of data to render.
	 * @param heightData the new terrain data.
	 * @throws MonkeyRuntimeException if heightData is null.
	 */
	public void setHeightData(AbstractHeightMap heightData) {
		if (null == heightData) {
			throw new MonkeyRuntimeException("heightData cannot be null");
		}

		this.heightData = heightData;
		this.terrainSize = heightData.getSize();
	}
	
	/**
	 * Abstract method to be implemented by the subclass.
	 * @see jme.locale.Locale#update(float)
	 */
	public abstract void update(float time);

	/**
	 * Abstract method to be implemented by the subclass.
	 * @see jme.locale.Locale#render()
	 */
	public abstract void render();

}
