/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
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
package com.jme.scene.shape;

import com.jme.math.FastMath;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;

/**
 * <code>Octahedron</code>
 * @author Mark Powell
 * @version $Id: Octahedron.java,v 1.2 2004-04-22 22:26:53 renanse Exp $
 */
public class Octahedron extends TriMesh {
	private static final int NUM_POINTS = 6;
	private static final int NUM_TRIS = 8;
	private float sideLength;
	public Octahedron(String name, float sideLength) {
		super(name);
		this.sideLength = sideLength;

		// allocate vertices
		vertex = new Vector3f[NUM_POINTS];
		normal = new Vector3f[NUM_POINTS];
		color = new ColorRGBA[NUM_POINTS];
		texture[0] = new Vector2f[NUM_POINTS];
		indices = new int[3 * NUM_TRIS];

		setVertexData();
		setNormalData();
		setColorData();
		setTextureData();
		setIndexData();

	}

	private void setIndexData() {
		indices[0] = 4;
		indices[1] = 0;
		indices[2] = 2;
		indices[3] = 4;
		indices[4] = 2;
		indices[5] = 1;
		indices[6] = 4;
		indices[7] = 1;
		indices[8] = 3;
		indices[9] = 4;
		indices[10] = 3;
		indices[11] = 0;
		indices[12] = 5;
		indices[13] = 2;
		indices[14] = 0;
		indices[15] = 5;
		indices[16] = 1;
		indices[17] = 2;
		indices[18] = 5;
		indices[19] = 3;
		indices[20] = 1;
		indices[21] = 5;
		indices[22] = 0;
		indices[23] = 3;

		if (!true) {
			for (int i = 0; i < NUM_TRIS; i++) {
				int iSave = indices[3 * i + 1];
				indices[3 * i + 1] = indices[3 * i + 2];
				indices[3 * i + 2] = iSave;
			}
		}

		setIndices(indices);
	}
	private void setTextureData() {
		for (int i = 0; i < NUM_POINTS; i++) {
			texture[0][i] = new Vector2f();
			if (FastMath.abs(vertex[i].z) < sideLength) {
				texture[0][i].x =
					0.5f
						* (1.0f
							+ FastMath.atan2(vertex[i].y, vertex[i].x)
								* FastMath.INV_PI);
			} else {
				texture[0][i].x = 0.5f;
			}
			texture[0][i].y = FastMath.acos(vertex[i].z) * FastMath.INV_PI;
		}

		setTextures(texture[0]);
	}
	private void setColorData() {
		for (int x = 0; x < NUM_POINTS; x++)
			color[x] = new ColorRGBA();
		setColors(color);
	}
	private void setNormalData() {
		for (int i = 0; i < NUM_POINTS; i++)
			normal[i] = vertex[i].normalize();
		setNormals(normal);
	}
	private void setVertexData() {
		vertex[0] = new Vector3f(sideLength, 0.0f, 0.0f);
		vertex[1] = new Vector3f(-sideLength, 0.0f, 0.0f);
		vertex[2] = new Vector3f(0.0f, sideLength, 0.0f);
		vertex[3] = new Vector3f(0.0f, -sideLength, 0.0f);
		vertex[4] = new Vector3f(0.0f, 0.0f, sideLength);
		vertex[5] = new Vector3f(0.0f, 0.0f, -sideLength);

		setVertices(vertex);
	}
}
