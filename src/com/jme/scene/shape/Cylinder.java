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
 * <code>Cylinder</code> provides an extension of <code>TriMesh</code>. A
 * <code>Cylinder</code> is defined by a height and radius. The center of the
 * Cylinder is the origin.
 * 
 * @author Mark Powell
 * @version $Id: Cylinder.java,v 1.4 2004-09-14 21:52:21 mojomonkey Exp $
 */
public class Cylinder extends TriMesh {

	private static final long serialVersionUID = 1L;

	private int axisSamples;

	private int radialSamples;

	private float radius;

	private float height;

	/**
	 * Creates a new Cylinder. By default its center is the origin. Usually, a
	 * higher sample number creates a better looking cylinder, but at the cost
	 * of more vertex information.
	 * 
	 * @param name
	 *            The name of this Cylinder.
	 * @param axisSamples
	 *            Number of triangle samples along the axis.
	 * @param radialSamples
	 *            Number of triangle samples along the radial.
	 * @param radius
	 *            The radius of the cylinder.
	 * @param height
	 *            The cylinder's height.
	 */
	public Cylinder(String name, int axisSamples, int radialSamples,
			float radius, float height) {

		super(name);

		this.axisSamples = axisSamples;
		this.radialSamples = radialSamples;
		this.radius = radius;
		this.height = height;

		//      allocate vertices
		int quantity = axisSamples * (radialSamples + 1);
		vertex = new Vector3f[quantity];
		normal = new Vector3f[quantity];
		color = new ColorRGBA[quantity];
		texture[0] = new Vector2f[quantity];
		int triQuantity = 2 * (axisSamples - 1) * radialSamples;
		indices = new int[3 * triQuantity];

		setGeometryData();

		setIndexData();

		setVertices(vertex);
		setNormals(normal);

		setTextures(texture[0]);

		setColorData();

	}

	private void setGeometryData() {
		// generate geometry
		float inverseRadial = 1.0f / (float) radialSamples;
		float inverseAxisLess = 1.0f / (float) (axisSamples - 1);
		float halfHeight = 0.5f * height;

		// Generate points on the unit circle to be used in computing the mesh
		// points on a cylinder slice.
		float[] sin = new float[radialSamples + 1];
		float[] cos = new float[radialSamples + 1];

		for (int radialCount = 0; radialCount < radialSamples; radialCount++) {
			float angle = FastMath.TWO_PI * inverseRadial * radialCount;
			cos[radialCount] = FastMath.cos(angle);
			sin[radialCount] = FastMath.sin(angle);
		}
		sin[radialSamples] = sin[0];
		cos[radialSamples] = cos[0];

		// generate the cylinder itself
		for (int axisCount = 0, i = 0; axisCount < axisSamples; axisCount++) {
			float axisFraction = axisCount * inverseAxisLess; // in [0,1]
			float z = -halfHeight + height * axisFraction;

			// compute center of slice
			Vector3f sliceCenter = new Vector3f(0, 0, z);

			// compute slice vertices with duplication at end point
			int save = i;
			for (int radialCount = 0; radialCount < radialSamples; radialCount++) {
				float radialFraction = radialCount * inverseRadial; // in [0,1)
				Vector3f tempNormal = new Vector3f(cos[radialCount],
						sin[radialCount], 0);
				vertex[i] = sliceCenter.add(tempNormal.mult(radius));
				if (true) {
					normal[i] = tempNormal;
				} else {
					normal[i] = tempNormal.negate();
				}
				if (texture[0][i] == null) {
					texture[0][i] = new Vector2f();
				}
				texture[0][i].x = radialFraction;
				texture[0][i].y = axisFraction;
				i++;
			}

			vertex[i] = vertex[save];
			normal[i] = normal[save];
			if (texture[0][i] == null) {
				texture[0][i] = new Vector2f();
			}
			texture[0][i].x = 1.0f;
			texture[0][i].y = axisFraction;
			i++;
		}
	}

	private void setIndexData() {
		// generate connectivity
		int index = 0;
		for (int axisCount = 0, axisStart = 0; axisCount < axisSamples - 1; axisCount++) {
			int i0 = axisStart;
			int i1 = i0 + 1;
			axisStart += radialSamples + 1;
			int i2 = axisStart;
			int i3 = i2 + 1;
			for (int i = 0; i < radialSamples; i++, index += 6) {
				if (true) {
					indices[index + 0] = i0++;
					indices[index + 1] = i1;
					indices[index + 2] = i2;
					indices[index + 3] = i1++;
					indices[index + 4] = i3++;
					indices[index + 5] = i2++;
				} else {
					indices[index + 0] = i0++;
					indices[index + 1] = i2;
					indices[index + 2] = i1;
					indices[index + 3] = i1++;
					indices[index + 4] = i2++;
					indices[index + 5] = i3++;
				}
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