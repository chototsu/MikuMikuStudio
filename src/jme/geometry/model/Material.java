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
package jme.geometry.model;

/**
 * <code>Material</code> maintains all standard lighting coeffecients 
 * for OpenGL based lighting as well as texture information. 
 * @author Mark Powell
 */

public class Material {
    public String name;
    public float transparency;
    public byte mode;
    public String alphaFilename;
	/**
	 * defines the color and intensity of the ambient or natural light.
	 */
	public float[] ambient = new float[4];
	/**
	 * defines the color and intensity of the diffuse or indirect light.
	 */
	public float[] diffuse = new float[4];
	/**
	 * defines the color and intensity of the specular or direct light.
	 */
	public float[] specular = new float[4];
	/**
	 * defines the color and intensity of the emissive or projected light.
	 */
	public float[] emissive = new float[4];
	/**
	 * defines the amount of light is reflected by the material.
	 */
	public float shininess;
	/**
	 * defines the id of the texture as assigned by OpenGL.
	 */
	public int texture;
	/**
	 * defines the name of the image file used to generate the texture.
	 */
	public String textureFilename;
}
