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
package com.jme.scene;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

/**
 * <code>Quad</code>
 * @author Mark Powell
 * @version $Id: Quad.java,v 1.1 2004-03-03 21:58:06 mojomonkey Exp $
 */
public class Quad extends TriMesh {
	public Quad(String name) {
		super(name);
		initialize(1,1);
	}
	
	public void initialize(float width, float height) {
		Vector3f[] verts = new Vector3f[4];
		Vector3f[] norms = new Vector3f[4];
		Vector2f[] texCoords = new Vector2f[4];
		ColorRGBA[] colors = new ColorRGBA[4];
		int[] indices = new int[6];
		
		verts[0] = new Vector3f(-width/2, height/2, 0);
		verts[1] = new Vector3f(-width/2, -height/2, 0);
		verts[2] = new Vector3f(width/2, -height/2, 0);
		verts[3] = new Vector3f(width/2, height/2, 0);
		
		Vector3f n = new Vector3f(0,0,1);
		norms[0] = n;
		norms[1] = n;
		norms[2] = n;
		norms[3] = n;
		
		texCoords[0] = new Vector2f(0,1);
		texCoords[1] = new Vector2f(0,0);
		texCoords[2] = new Vector2f(1,0);
		texCoords[3] = new Vector2f(1,1);
		
		colors[0] = new ColorRGBA();
		colors[1] = new ColorRGBA();
		colors[2] = new ColorRGBA();
		colors[3] = new ColorRGBA();
		
		indices[0] = 0;
		indices[1] = 1;
		indices[2] = 2;
		indices[3] = 0;
		indices[4] = 2;
		indices[5] = 3;
		
		setVertices(verts);
		setNormals(norms);
		setColors(colors);
		setTextures(texCoords);
		setIndices(indices);
	}
}
