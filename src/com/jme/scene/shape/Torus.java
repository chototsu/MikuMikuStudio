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
 * <code>Torus</code> is um ... a Torus :) The center is by default the
 * origin.
 * 
 * @author Mark Powell
 * @version $Id: Torus.java,v 1.4 2004-09-14 21:52:21 mojomonkey Exp $
 */
public class Torus extends TriMesh {
	private static final long serialVersionUID = 1L;

	private int circleSamples;

	private int radialSamples;

	private float innerRadius;

	private float outerRadius;

	/**
	 * Constructs a new Torus. Center is the origin, but the Torus may be
	 * transformed.
	 * 
	 * @param name
	 *            The name of the Torus.
	 * @param circleSamples
	 *            The number of samples along the circles.
	 * @param radialSamples
	 *            The number of samples along the radial.
	 * @param innerRadius
	 *            The radius of the inner begining of the Torus.
	 * @param outerRadius
	 *            The radius of the outter end of the Torus.
	 */
	public Torus(String name, int circleSamples, int radialSamples,
			float innerRadius, float outerRadius) {

		super(name);
		this.circleSamples = circleSamples;
		this.radialSamples = radialSamples;
		this.innerRadius = innerRadius;
		this.outerRadius = outerRadius;

		setGeometryData();
		setIndexData();
		setColorData();

	}

	private void setGeometryData() {

		int numVerts = (circleSamples + 1) * (radialSamples + 1);
		//set the geometry's defined data
		vertex = new Vector3f[numVerts];
		normal = new Vector3f[numVerts];
		//initialize the first texture unit (other texture units are set
		//by the user)
		texture[0] = new Vector2f[numVerts];

		// generate geometry
		float inverseCircleSamples = 1.0f / (float) circleSamples;
		float inverseRadialSamples = 1.0f / (float) radialSamples;
		int i = 0;
		// generate the cylinder itself
		for (int circleCount = 0; circleCount < circleSamples; circleCount++) {
			// compute center point on torus circle at specified angle
			float circleFraction = circleCount * inverseCircleSamples;
			float theta = FastMath.TWO_PI * circleFraction;
			float cosTheta = FastMath.cos(theta);
			float sinTheta = FastMath.sin(theta);
			Vector3f radialAxis = new Vector3f(cosTheta, sinTheta, 0);
			Vector3f torusMiddle = radialAxis.mult(outerRadius);

			// compute slice vertices with duplication at end point
			int iSave = i;
			for (int radialCount = 0; radialCount < radialSamples; radialCount++) {
				float radialFraction = radialCount * inverseRadialSamples;
				// in [0,1)
				float phi = FastMath.TWO_PI * radialFraction;
				float cosPhi = FastMath.cos(phi);
				float sinPhi = FastMath.sin(phi);
				Vector3f tempNormal = radialAxis.mult(cosPhi);
				tempNormal.z += sinPhi;
				vertex[i] = torusMiddle.add(tempNormal.mult(innerRadius));
				if (true)
					normal[i] = tempNormal;
				else
					normal[i] = tempNormal.negate();
				if (texture[0][i] == null)
					texture[0][i] = new Vector2f();
				texture[0][i].x = radialFraction;
				texture[0][i].y = circleFraction;
				i++;
			}

			vertex[i] = vertex[iSave];
			normal[i] = normal[iSave];
			if (texture[0][i] == null)
				texture[0][i] = new Vector2f();
			texture[0][i].x = 1.0f;
			texture[0][i].y = circleFraction;
			i++;
		}

		// duplicate the cylinder ends to form a torus
		for (int iR = 0; iR <= radialSamples; iR++, i++) {
			vertex[i] = vertex[iR];
			normal[i] = normal[iR];
			if (texture[0][i] == null)
				texture[0][i] = new Vector2f();
			texture[0][i].x = texture[0][iR].x;
			texture[0][i].y = 1.0f;

		}

		setVertices(vertex);
		setNormals(normal);
		setTextures(texture[0]);
	}

	private void setIndexData() {
		//      allocate connectivity
		int indexQuantity = 2 * circleSamples * radialSamples;
		indices = new int[3 * indexQuantity];
		int i;
		// generate connectivity
		int connectionStart = 0;
		int index = 0;
		for (int circleCount = 0; circleCount < circleSamples; circleCount++) {
			int i0 = connectionStart;
			int i1 = i0 + 1;
			connectionStart += radialSamples + 1;
			int i2 = connectionStart;
			int i3 = i2 + 1;
			for (i = 0; i < radialSamples; i++, index += 6) {
				if (true) {
					indices[index + 0] = i0++;
					indices[index + 1] = i2;
					indices[index + 2] = i1;
					indices[index + 3] = i1++;
					indices[index + 4] = i2++;
					indices[index + 5] = i3++;
				} else {
					indices[index + 0] = i0++;
					indices[index + 1] = i1;
					indices[index + 2] = i2;
					indices[index + 3] = i1++;
					indices[index + 4] = i3++;
					indices[index + 5] = i2++;
				}
			}
		}
		setIndices(indices);
	}

	private void setColorData() {
		color = new ColorRGBA[vertex.length];
		//initialize colors to white
		for (int x = 0; x < vertex.length; x++) {
			color[x] = new ColorRGBA();
		}
		setColors(color);
	}

}