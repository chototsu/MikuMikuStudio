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
 * <code>Sphere</code> is um ... a sphere :)
 * 
 * @author Joshua Slack
 * @version $Id: Sphere.java,v 1.7 2004-09-14 21:52:21 mojomonkey Exp $
 */
public class Sphere extends TriMesh {
	private static final long serialVersionUID = 1L;

	private int zSamples;

	private int radialSamples;

	public float radius;

	public Vector3f center;

	private static Vector3f tempVa = new Vector3f();

	private static Vector3f tempVb = new Vector3f();

	private static Vector3f tempVc = new Vector3f();

	/**
	 * Constructs a sphere. By default the Sphere has not geometry data or
	 * center.
	 * 
	 * @param name
	 *            The name of the sphere.
	 */
	public Sphere(String name) {
		super(name);
	}

	/**
	 * Constructs a sphere with center at the origin. For details, see the other
	 * constructor.
	 * 
	 * @param name
	 *            Name of sphere.
	 * @param zSamples
	 *            The samples along the Z.
	 * @param radialSamples
	 *            The samples along the radial.
	 * @param radius
	 *            Radius of the sphere.
	 * @see #Sphere(java.lang.String, com.jme.math.Vector3f, int, int, float)
	 */
	public Sphere(String name, int zSamples, int radialSamples, float radius) {
		this(name, new Vector3f(0, 0, 0), zSamples, radialSamples, radius);
		//        super(name);
		//        setData(new Vector3f(0f,0f,0f), zSamples, radialSamples, radius,
		// true);
	}

	/**
	 * Constructs a sphere. All geometry data buffers are updated automatically.
	 * Both zSamples and radialSamples increase the quality of the generated
	 * sphere.
	 * 
	 * @param name
	 *            Name of the sphere.
	 * @param center
	 *            Center of the sphere.
	 * @param zSamples
	 *            The number of samples along the Z.
	 * @param radialSamples
	 *            The number of samples along the radial.
	 * @param radius
	 *            The radius of the sphere.
	 */
	public Sphere(String name, Vector3f center, int zSamples,
			int radialSamples, float radius) {

		super(name);
		setData(center, zSamples, radialSamples, radius, true);
	}

	/**
	 * Changes the information of the sphere into the given values. The boolean
	 * at the end signals if buffer data should be updated as well. If the
	 * sphere is to be rendered, then that value should be true.
	 * 
	 * @param center
	 *            The new center of the sphere.
	 * @param zSamples
	 *            The new number of zSamples of the sphere.
	 * @param radialSamples
	 *            The new number of radial samples of the sphere.
	 * @param radius
	 *            The new radius of the sphere.
	 * @param updateBuffers
	 *            If true, buffer information is updated as well.
	 */
	public void setData(Vector3f center, int zSamples, int radialSamples,
			float radius, boolean updateBuffers) {
		if (center != null)
			this.center = center;
		else
			this.center = new Vector3f(0, 0, 0);
		this.zSamples = zSamples;
		this.radialSamples = radialSamples;
		this.radius = radius;

		if (updateBuffers) {
			setGeometryData();
			setIndexData();
			setColorData();
		}

	}

	private void setGeometryData() {

		// allocate vertices
		int numVerts = (zSamples - 2) * (radialSamples + 1) + 2;
		vertex = new Vector3f[numVerts];

		// allocate normals if requested
		normal = new Vector3f[numVerts];

		// allocate texture coordinates
		texture[0] = new Vector2f[numVerts];

		// generate geometry
		float fInvRS = 1.0f / (float) radialSamples;
		float fZFactor = 2.0f / (float) (zSamples - 1);

		// Generate points on the unit circle to be used in computing the mesh
		// points on a cylinder slice.
		float[] afSin = new float[(radialSamples + 1)];
		float[] afCos = new float[(radialSamples + 1)];
		for (int iR = 0; iR < radialSamples; iR++) {
			float fAngle = FastMath.TWO_PI * fInvRS * iR;
			afCos[iR] = FastMath.cos(fAngle);
			afSin[iR] = FastMath.sin(fAngle);
		}
		afSin[radialSamples] = afSin[0];
		afCos[radialSamples] = afCos[0];

		// generate the cylinder itself
		int i = 0;
		for (int iZ = 1; iZ < (zSamples - 1); iZ++) {
			float fZFraction = -1.0f + fZFactor * iZ; // in (-1,1)
			float fZ = radius * fZFraction;

			// compute center of slice
			Vector3f kSliceCenter = tempVb.set(center);
			kSliceCenter.z += fZ;

			// compute radius of slice
			float fSliceRadius = FastMath.sqrt(FastMath.abs(radius * radius
					- fZ * fZ));

			// compute slice vertices with duplication at end point
			Vector3f kNormal;
			int iSave = i;
			for (int iR = 0; iR < radialSamples; iR++) {
				float fRadialFraction = iR * fInvRS; // in [0,1)
				Vector3f kRadial = tempVc.set(afCos[iR], afSin[iR], 0);
				vertex[i] = kSliceCenter
						.add(kRadial.mult(fSliceRadius, tempVa));

				kNormal = vertex[i].subtract(center);
				kNormal.normalizeLocal();
				if (true) // later we may allow interior texture vs. exterior
					normal[i] = kNormal;
				else
					normal[i] = kNormal.negateLocal();

				if (texture[0][i] == null)
					texture[0][i] = new Vector2f();
				texture[0][i].x = fRadialFraction;
				texture[0][i].y = 0.5f * (fZFraction + 1.0f);

				i++;
			}

			vertex[i] = vertex[iSave];

			normal[i] = normal[iSave];

			if (texture[0][i] == null)
				texture[0][i] = new Vector2f();
			texture[0][i].x = 1.0f;
			texture[0][i].y = 0.5f * (fZFraction + 1.0f);

			i++;
		}

		// south pole
		vertex[i] = (Vector3f) center.clone();
		vertex[i].z -= radius;
		if (true)
			normal[i] = new Vector3f(0, 0, -1);
		else
			normal[i] = new Vector3f(0, 0, 1);

		if (texture[0][i] == null)
			texture[0][i] = new Vector2f();
		texture[0][i].x = 0.5f;
		texture[0][i].y = 0.0f;

		i++;

		// north pole
		vertex[i] = (Vector3f) center.clone();
		vertex[i].z += radius;
		if (true)
			normal[i] = new Vector3f(0, 0, 1);
		else
			normal[i] = new Vector3f(0, 0, -1);

		if (texture[0][i] == null)
			texture[0][i] = new Vector2f();
		texture[0][i].x = 0.5f;
		texture[0][i].y = 1.0f;

		i++;

		setVertices(vertex);
		setNormals(normal);
		setTextures(texture[0]);
	}

	private void setIndexData() {

		// allocate connectivity
		int indexQuantity = 2 * (zSamples - 2) * radialSamples;
		indices = new int[3 * indexQuantity];

		// generate connectivity
		int index = 0;
		for (int iZ = 0, iZStart = 0; iZ < (zSamples - 3); iZ++) {
			int i0 = iZStart;
			int i1 = i0 + 1;
			iZStart += (radialSamples + 1);
			int i2 = iZStart;
			int i3 = i2 + 1;
			for (int i = 0; i < radialSamples; i++, index += 6) {
				if (true) {
					indices[index + 0] = i0++;
					indices[index + 1] = i1;
					indices[index + 2] = i2;
					indices[index + 3] = i1++;
					indices[index + 4] = i3++;
					indices[index + 5] = i2++;
				} else // inside view
				{
					indices[index + 0] = i0++;
					indices[index + 1] = i2;
					indices[index + 2] = i1;
					indices[index + 3] = i1++;
					indices[index + 4] = i2++;
					indices[index + 5] = i3++;
				}
			}
		}

		// south pole triangles
		for (int i = 0; i < radialSamples; i++, index += 3) {
			if (true) {
				indices[index + 0] = i;
				indices[index + 1] = vertex.length - 2;
				indices[index + 2] = i + 1;
			} else // inside view
			{
				indices[index + 0] = i;
				indices[index + 1] = i + 1;
				indices[index + 2] = vertex.length - 2;
			}
		}

		// north pole triangles
		int iOffset = (zSamples - 3) * (radialSamples + 1);
		for (int i = 0; i < radialSamples; i++, index += 3) {
			if (true) {
				indices[index + 0] = i + iOffset;
				indices[index + 1] = i + 1 + iOffset;
				indices[index + 2] = vertex.length - 1;
			} else // inside view
			{
				indices[index + 0] = i + iOffset;
				indices[index + 1] = vertex.length - 1;
				indices[index + 2] = i + 1 + iOffset;
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

	/**
	 * Returns the center of this sphere.
	 * 
	 * @return The sphere's center.
	 */
	public Vector3f getCenter() {
		return center;
	}

	/**
	 * Sets the center of this sphere. Note that other information (such as
	 * geometry buffers and actual vertex information) is not changed. In most
	 * cases, you'll want to use setData()
	 * 
	 * @param aCenter
	 *            The new center.
	 * @see #setData(com.jme.math.Vector3f, int, int, float, boolean)
	 */
	public void setCenter(Vector3f aCenter) {
		center = aCenter;
	}
}