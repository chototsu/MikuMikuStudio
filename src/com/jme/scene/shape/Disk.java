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
package com.jme.scene.shape;

import com.jme.math.FastMath;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;

/**
 * <code>Disk</code>
 * @author Mark Powell
 * @version $Id: Disk.java,v 1.1 2004-04-02 15:52:06 mojomonkey Exp $
 */
public class Disk extends TriMesh {

	private int shellSamples;
	private int radialSamples;
	private float radius;

	public Disk(
		String name,
		int shellSamples,
		int radialSamples,
		float radius) {
		super(name);

		this.shellSamples = shellSamples;
		this.radialSamples = radialSamples;
		this.radius = radius;

		int radialless = radialSamples - 1;
		int shellLess = shellSamples - 1;

		// allocate vertices
		int quantity = 1 + radialSamples * shellLess;
		vertex = new Vector3f[quantity];
		normal = new Vector3f[quantity];
		color = new ColorRGBA[quantity];
		texture[0] = new Vector2f[quantity];
		int indexQuantity = radialSamples * (2 * shellLess - 1);
		indices = new int[3 * indexQuantity];

		setGeometryData(shellLess);
		setColorData();
		setIndexData(radialless, shellLess);

	}

	private void setGeometryData(int shellLess) {
		// generate geometry

		// center of disk
		vertex[0] = new Vector3f(0f, 0f, 0f);
		normal[0] = new Vector3f(0, 0, 1);
		if (texture[0][0] == null) {
			texture[0][0] = new Vector2f();
		}
		texture[0][0].x = 0.5f;
		texture[0][0].y = 0.5f;

		float inverseShellLess = 1.0f / (float) shellLess;
		float inverseRadial = 1.0f / (float) radialSamples;
		for (int radialCount = 0; radialCount < radialSamples; radialCount++) {
			float angle = FastMath.TWO_PI * inverseRadial * radialCount;
			float cos = FastMath.cos(angle);
			float sin = FastMath.sin(angle);
			Vector3f radial = new Vector3f(cos, sin, 0);

			for (int shellCount = 1; shellCount < shellSamples; shellCount++) {
				float fraction = inverseShellLess * shellCount; // in (0,R]
				Vector3f radialFraction = radial.mult(fraction);
				int i = shellCount + shellLess * radialCount;
				vertex[i] = radialFraction.mult(radius);
				normal[i] = new Vector3f(0, 0, 1);
				if (texture[0][i] == null) {
					texture[0][i] = new Vector2f();
				}
				texture[0][i].x = 0.5f * (1.0f + radialFraction.x);
				texture[0][i].y = 0.5f * (1.0f + radialFraction.y);
			}
		}

		setVertices(vertex);
		setNormals(normal);
		setTextures(texture[0]);
	}

	private void setIndexData(int radialless, int shellLess) {
		// generate connectivity
		int index = 0;
		int triangleCount = 0;
		for (int radialCount0 = radialless, radialCount1 = 0;
			radialCount1 < radialSamples;
			radialCount0 = radialCount1++) {
			indices[index + 0] = 0;
			indices[index + 1] = 1 + shellLess * radialCount0;
			indices[index + 2] = 1 + shellLess * radialCount1;
			index += 3;
			triangleCount++;
			for (int iS = 1; iS < shellLess; iS++, index += 6) {
				int i00 = iS + shellLess * radialCount0;
				int i01 = iS + shellLess * radialCount1;
				int i10 = i00 + 1;
				int i11 = i01 + 1;
				indices[index + 0] = i00;
				indices[index + 1] = i10;
				indices[index + 2] = i11;
				indices[index + 3] = i00;
				indices[index + 4] = i11;
				indices[index + 5] = i01;
				triangleCount += 2;
			}
		}

		setIndices(indices);
	}

	private void setColorData() {
		for (int x = 0; x < color.length; x++) {
			color[x] = new ColorRGBA();
		}
		setColors(color);
	}
}
