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

import com.jme.math.FastMath;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

/**
 * <code>Sphere</code>
 * @author Joshua Slack
 * @version $Id: Sphere.java,v 1.5 2004-03-13 05:22:47 renanse Exp $
 */
public class Sphere extends TriMesh {
    private int zSamples;
    private int radialSamples;

    protected float radius;
    protected Vector3f center;

    public Sphere(String name) {
        super(name);
    }

    public Sphere(
        String name,
        int zSamples,
        int radialSamples,
        float radius) {

        super(name);
        setData(new Vector3f(0f,0f,0f), zSamples, radialSamples, radius);
    }

    public Sphere(
        String name,
        Vector3f center,
        int zSamples,
        int radialSamples,
        float radius) {

        super(name);
        setData(center, zSamples, radialSamples, radius);
    }

    public void setData(Vector3f center, int zSamples, int radialSamples, float radius) {
        if (center != null)
            this.center = center;
        this.zSamples = zSamples;
        this.radialSamples = radialSamples;
        this.radius = radius;

        setGeometryData();
        setIndexData();
        setColorData();
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
            Vector3f kSliceCenter = (Vector3f)center.clone();
            kSliceCenter.z+=fZ;

            // compute radius of slice
            float fSliceRadius =
                FastMath.sqrt(FastMath.abs(radius * radius - fZ * fZ));

            // compute slice vertices with duplication at end point
            Vector3f kNormal;
            int iSave = i;
            for (int iR = 0; iR < radialSamples; iR++) {
                float fRadialFraction = iR * fInvRS; // in [0,1)
                Vector3f kRadial = new Vector3f(afCos[iR], afSin[iR], 0);
                vertex[i] = kSliceCenter.add(kRadial.mult(fSliceRadius));

                kNormal = vertex[i].subtract(center);
                kNormal.normalize();
                if (true)
                    normal[i] = kNormal;
                else
                    normal[i] = kNormal.negate();

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
        vertex[i] = (Vector3f)center.clone();
        vertex[i].z-=radius;
        if (true)
            normal[i] = new Vector3f(0,0,-1);
        else
            normal[i] = new Vector3f(0,0,1);

        if (texture[0][i] == null)
            texture[0][i] = new Vector2f();
        texture[0][i].x = 0.5f;
        texture[0][i].y = 0.0f;

        i++;

        // north pole
        vertex[i] = (Vector3f)center.clone();
        vertex[i].z+=radius;
        if (true)
            normal[i] = new Vector3f(0,0,1);
        else
            normal[i] = new Vector3f(0,0,-1);

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


	public Vector3f getCenter(){
		return center;
	}
	public void setCenter(Vector3f aCenter){
		center = aCenter;
	}
}
