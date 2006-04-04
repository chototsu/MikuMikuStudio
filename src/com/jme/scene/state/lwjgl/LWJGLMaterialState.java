/*
 * Copyright (c) 2003-2006 jMonkeyEngine
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

import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import com.jme.scene.state.MaterialState;
import com.jme.util.geom.BufferUtils;

/**
 * <code>LWJGLMaterialState</code> subclasses MaterialState using the LWJGL
 * API to access OpenGL to set the material for a given node and it's children.
 * 
 * @author Mark Powell
 * @version $Id: LWJGLMaterialState.java,v 1.11 2006-04-04 17:00:56 nca Exp $
 */
public class LWJGLMaterialState extends MaterialState {
	private static final long serialVersionUID = 1L;

	//buffer for color
	private transient FloatBuffer buffer;

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
        int face = getGLMaterialFace();
        
        if (face != currentMaterialFace || currentColorMaterial != colorMaterial) {
            if (colorMaterial == CM_NONE) {
                GL11.glDisable(GL11.GL_COLOR_MATERIAL);
            } else {
                GL11.glColorMaterial(face, getGLColorMaterial());
                GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            }
            currentColorMaterial = colorMaterial;
        }
        
		if (currentColorMaterial != CM_EMISSIVE
                && (face != currentMaterialFace || !currentEmissive
                        .equals(emissive))) {
            colorArray[0] = emissive.r;
			colorArray[1] = emissive.g;
			colorArray[2] = emissive.b;
			colorArray[3] = emissive.a;

			buffer.clear();
			buffer.put(colorArray);
			buffer.flip();
			GL11.glMaterial(face, GL11.GL_EMISSION, buffer);
            
            currentEmissive.set(emissive);
		}

        if ((currentColorMaterial != CM_AMBIENT && currentColorMaterial != CM_AMBIENT_AND_DIFFUSE)
                && (face != currentMaterialFace || !currentAmbient
                        .equals(ambient))) {
			colorArray[0] = ambient.r;
			colorArray[1] = ambient.g;
			colorArray[2] = ambient.b;
			colorArray[3] = ambient.a;

			buffer.clear();
			buffer.put(colorArray);
			buffer.flip();
			GL11.glMaterial(face, GL11.GL_AMBIENT, buffer);
            
            currentAmbient.set(ambient);
		}

        if ((currentColorMaterial != CM_DIFFUSE && currentColorMaterial != CM_AMBIENT_AND_DIFFUSE)
                && (face != currentMaterialFace || !currentDiffuse
                        .equals(diffuse))) {
			colorArray[0] = diffuse.r;
			colorArray[1] = diffuse.g;
			colorArray[2] = diffuse.b;
			colorArray[3] = diffuse.a;

			buffer.clear();
			buffer.put(colorArray);
			buffer.flip();
			GL11.glMaterial(face, GL11.GL_DIFFUSE, buffer);
            
            currentDiffuse.set(diffuse);
		}

        if (currentColorMaterial != CM_SPECULAR
                && (face != currentMaterialFace || !currentSpecular
                        .equals(specular))) {
			colorArray[0] = specular.r;
			colorArray[1] = specular.g;
			colorArray[2] = specular.b;
			colorArray[3] = specular.a;

			buffer.clear();
			buffer.put(colorArray);
			buffer.flip();
			GL11.glMaterial(face, GL11.GL_SPECULAR, buffer);
            
            currentSpecular.set(specular);
		}

		if (face != currentMaterialFace || currentShininess != shininess) {
			GL11.glMaterialf(face, GL11.GL_SHININESS, shininess);
            
            currentShininess = shininess;
		}
        
        currentMaterialFace = face;
	}
    
    /**
     * Converts the color material setting of this state to a GL constant.
     * 
     * @return the GL constant
     */
    private int getGLColorMaterial() {
        switch (colorMaterial) {
            case CM_AMBIENT:
                return GL11.GL_AMBIENT;
            case CM_DIFFUSE:
                return GL11.GL_DIFFUSE;
            case CM_AMBIENT_AND_DIFFUSE:
                return GL11.GL_AMBIENT_AND_DIFFUSE;
            case CM_EMISSIVE:
                return GL11.GL_EMISSION;
            case CM_SPECULAR:
                return GL11.GL_SPECULAR;
        }
        return -1;
    }
    
    /**
     * Converts the material face setting of this state to a GL constant.
     * 
     * @return the GL constant
     */
    private int getGLMaterialFace() {
        switch (materialFace) {
            case MF_FRONT:
                return GL11.GL_FRONT;
            case MF_BACK:
                return GL11.GL_BACK;
            case MF_FRONT_AND_BACK:
                return GL11.GL_FRONT_AND_BACK;
        }
        return -1;
    }
    
    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        buffer = BufferUtils.createColorBuffer(1);
    }
}