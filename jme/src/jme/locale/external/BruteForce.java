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

import org.lwjgl.opengl.GL;

import jme.exception.MonkeyRuntimeException;
import jme.locale.external.data.AbstractHeightMap;
import jme.texture.TextureManager;

/**
 * <code>BruteForce</code> renders height data with no optimizations. It 
 * provides the hightest detail possible at the cost of performance. Each
 * vertex defined by the height data is rendered regardless of visiblity,
 * distance, etc.
 * 
 * @author Mark Powell
 * @version $Id: BruteForce.java,v 1.1.1.1 2003-10-29 10:58:14 Anakan Exp $
 */
public class BruteForce extends Terrain {
	
	
	/**
	 * Constructor sets the heightdata and gets the GL reference. This
	 * creates a new <code>BruteForce</code> object.
	 * 
	 * @param heightData the data that represents the terrain information.
	 * 
	 * @throws MonkeyRuntimeException if heightData is null.
	 */
	public BruteForce(AbstractHeightMap heightData) {
		if (null == heightData) {
			throw new MonkeyRuntimeException("heightData cannot be null");
		}

		this.heightData = heightData;
		this.terrainSize = heightData.getSize();
	}

	
	/**
	 * <code>update</code> does not do anything. It is here for compatibility
	 * sake.
	 */
	public void update(float time) {
		//nothing
	}

	/**
	 * <code>render</code> creates a triangle strip for each row of the 
	 * height map. These strips are then rendered, with the appropriate texture
     * and shadowing.
	 */
	public void render() {
		float shade;
		float texLeft = 0.0f;
		float texUp = 0.0f;
		float texDown = 0.0f;

		float red = 1.0f;
		float green = 1.0f;
		float blue = 1.0f;

		

		//set up the appropriate textures.
		if (isDetailed && isTextured) {
			GL.glActiveTextureARB(GL.GL_TEXTURE0_ARB);
			GL.glEnable(GL.GL_TEXTURE_2D);
			TextureManager.getTextureManager().bind(terrainTexture);
			GL.glActiveTextureARB(GL.GL_TEXTURE1_ARB);
			GL.glEnable(GL.GL_TEXTURE_2D);
			TextureManager.getTextureManager().bind(detailId);
			GL.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_COMBINE_ARB);
			GL.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_RGB_SCALE_ARB, 2);
		} else if (isTextured) {
			GL.glEnable(GL.GL_TEXTURE_2D);
			TextureManager.getTextureManager().bind(terrainTexture);
		}

		//render as triangle strips, one column at a time.
        
        //NOTE:
        //Due to an issue of loading the raw file, the x axis needs to 
        //be swapped. This will need to be resolved in the loaders, before
        //the terrain can be rendered in a normal fashion.
		for (int z = 0; z < terrainSize - 1; z++) {
			GL.glBegin(GL.GL_TRIANGLE_STRIP);
            
            if (isTextured) {
                 texDown = (float) z / terrainSize;
                 texUp = (float) (z + 1) / terrainSize;
             }


			for (int x = 0; x < terrainSize - 1; x++) {

         
				if (isLit) {
					shade = lightMap.getShade(z, x);
					red = shade * lightMap.getColor().x;
					green = shade * lightMap.getColor().y;
					blue = shade * lightMap.getColor().z;
				} else {
					red = 1.0f;
					green = 1.0f;
					blue = 1.0f;
				}
				GL.glColor3f(red, green, blue);

				if (isTextured && isDetailed) {
					texLeft = (float) x / terrainSize;
					GL.glMultiTexCoord2fARB(GL.GL_TEXTURE0_ARB, texDown, texLeft);
					GL.glMultiTexCoord2fARB(
						GL.GL_TEXTURE1_ARB,
						texLeft * repeatDetailMap,
						texDown * repeatDetailMap);
				} else if (isTextured) {
					texLeft = (float) x / terrainSize;
					GL.glTexCoord2f(texLeft, texDown);
				}
				
				GL.glVertex3f(x, heightData.getScaledHeightAtPoint(z, x), z);

				if (isLit) {
					shade = lightMap.getShade(z+1,x);
					red = shade * lightMap.getColor().x;
					green = shade * lightMap.getColor().y;
					blue = shade * lightMap.getColor().z;
				} else {
					red = 1.0f;
					green = 1.0f;
					blue = 1.0f;
				}
				GL.glColor3f(red, green, blue);
				
				if (isTextured && isDetailed) {
					GL.glMultiTexCoord2fARB(GL.GL_TEXTURE0_ARB, texUp, texLeft);
					GL.glMultiTexCoord2fARB(
						GL.GL_TEXTURE1_ARB,
						texLeft * repeatDetailMap,
						texUp * repeatDetailMap);
				} else if (isTextured) {
					GL.glTexCoord2f(texLeft, texUp);
				}

				GL.glVertex3f(
					x,
					heightData.getScaledHeightAtPoint(z+1, x),
					z + 1);
			}

			GL.glEnd();
		}
		
		

		if (isDetailed) {
			GL.glActiveTextureARB(GL.GL_TEXTURE1_ARB);
			GL.glBindTexture(GL.GL_TEXTURE_2D, 0);
			GL.glActiveTextureARB(GL.GL_TEXTURE0_ARB);
			GL.glBindTexture(GL.GL_TEXTURE_2D, 0);
		} 

		if (isTextured) {
			GL.glDisable(GL.GL_TEXTURE_2D);
		}
		
	}

}
