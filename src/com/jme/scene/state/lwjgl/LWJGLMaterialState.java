/*
 * Copyright (c) 2003-2005 jMonkeyEngine
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

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import com.jme.scene.state.MaterialState;
import com.jme.util.geom.BufferUtils;

/**
 * <code>LWJGLMaterialState</code> subclasses MaterialState using the LWJGL
 * API to access OpenGL to set the material for a given node and it's children.
 * 
 * @author Mark Powell
 * @version $Id: LWJGLMaterialState.java,v 1.7 2005-09-15 17:12:55 renanse Exp $
 */
public class LWJGLMaterialState extends MaterialState {
	private static final long serialVersionUID = 1L;

	//buffer for color
	private FloatBuffer buffer;

	/**
	 * Constructor instantiates a new <code>LWJGLMaterialState</code> object.
	 *  
	 */
	public LWJGLMaterialState() {
		super();
		buffer = BufferUtils.createColorBuffer(1);
	}

	float[] colorArray = new float[4];

	/**
	 * <code>set</code> calls the OpenGL material function to set the proper
	 * material state.
	 * 
	 * @see com.jme.scene.state.RenderState#apply()
	 */
	public void apply() {
		//        if(isEnabled()) {
		if (!currentEmissive.equals(getEmissive())) {
			colorArray[0] = getEmissive().r;
			colorArray[1] = getEmissive().g;
			colorArray[2] = getEmissive().b;
			colorArray[3] = getEmissive().a;

			buffer.clear();
			buffer.put(colorArray);
			buffer.flip();
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_EMISSION, buffer);
		}

		if (!currentAmbient.equals(getAmbient())) {
			colorArray[0] = getAmbient().r;
			colorArray[1] = getAmbient().g;
			colorArray[2] = getAmbient().b;
			colorArray[3] = getAmbient().a;

			buffer.clear();
			buffer.put(colorArray);
			buffer.flip();
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT, buffer);
		}

		if (!currentDiffuse.equals(getDiffuse())) {
			colorArray[0] = getDiffuse().r;
			colorArray[1] = getDiffuse().g;
			colorArray[2] = getDiffuse().b;
			colorArray[3] = getDiffuse().a;

			buffer.clear();
			buffer.put(colorArray);
			buffer.flip();
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, buffer);
		}

		if (!currentSpecular.equals(getSpecular())) {
			colorArray[0] = getSpecular().r;
			colorArray[1] = getSpecular().g;
			colorArray[2] = getSpecular().b;
			colorArray[3] = getSpecular().a;

			buffer.clear();
			buffer.put(colorArray);
			buffer.flip();
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, buffer);
		}

		if (currentShininess != getShininess()) {
			GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, getShininess());
		}
		//      }
	}
}