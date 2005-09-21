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

package com.jme.scene.shape;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Sphere</code> is um ... a sphere :)
 * 
 * @author Joshua Slack
 * @version $Id: Sphere.java,v 1.10 2005-09-21 17:52:54 renanse Exp $
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
        setData(center, zSamples, radialSamples, radius);
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
     */
    public void setData(Vector3f center, int zSamples, int radialSamples, float radius) {
        if (center != null)
            this.center = center;
        else
            this.center = new Vector3f(0, 0, 0);
        this.zSamples = zSamples;
        this.radialSamples = radialSamples;
        this.radius = radius;

        setGeometryData();
        setIndexData();
        setDefaultColor(ColorRGBA.white);
    }


    private void setGeometryData() {

        // allocate vertices
        vertQuantity = (zSamples - 2) * (radialSamples + 1) + 2;
        vertBuf = BufferUtils.createVector3Buffer(vertBuf, vertQuantity);

        // allocate normals if requested
        normBuf = BufferUtils.createVector3Buffer(normBuf, vertQuantity);

        // allocate texture coordinates
        texBuf[0] = BufferUtils.createVector2Buffer(texBuf[0], vertQuantity);

        // generate geometry
        float fInvRS = 1.0f / (float) radialSamples;
        float fZFactor = 2.0f / (float) (zSamples - 1);

        // Generate points on the unit circle to be used in computing the mesh
        // points on a sphere slice.
        float[] afSin = new float[(radialSamples + 1)];
        float[] afCos = new float[(radialSamples + 1)];
        for (int iR = 0; iR < radialSamples; iR++) {
            float fAngle = FastMath.TWO_PI * fInvRS * iR;
            afCos[iR] = FastMath.cos(fAngle);
            afSin[iR] = FastMath.sin(fAngle);
        }
        afSin[radialSamples] = afSin[0];
        afCos[radialSamples] = afCos[0];

        // generate the sphere itself
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
                kRadial.mult(fSliceRadius, tempVa);
                vertBuf.put(kSliceCenter.x + tempVa.x).put(kSliceCenter.y + tempVa.y).put(kSliceCenter.z + tempVa.z);
                
                BufferUtils.populateFromBuffer(tempVa, vertBuf, i);
                kNormal = tempVa.subtractLocal(center);
                kNormal.normalizeLocal();
                if (true) // later we may allow interior texture vs. exterior
                    normBuf.put(kNormal.x).put(kNormal.y).put(kNormal.z);
                else 
                    normBuf.put(-kNormal.x).put(-kNormal.y).put(-kNormal.z);

                texBuf[0].put(fRadialFraction).put(0.5f * (fZFraction + 1.0f));

                i++;
            }

            BufferUtils.copyInternalVector3(vertBuf, iSave, i);
            BufferUtils.copyInternalVector3(normBuf, iSave, i);

            texBuf[0].put(1.0f).put(0.5f * (fZFraction + 1.0f));

            i++;
        }

        // south pole
        vertBuf.position(i*3);
        vertBuf.put(center.x).put(center.y).put(center.z-radius);

        normBuf.position(i * 3);        
        if (true) normBuf.put(0).put(0).put(-1); // allow for inner texture orientation later.
        else normBuf.put(0).put(0).put(1);

        texBuf[0].position(i*2);
        texBuf[0].put(0.5f).put(0.0f);

        i++;

        // north pole
        vertBuf.put(center.x).put(center.y).put(center.z+radius);
        
        if (true) normBuf.put(0).put(0).put(1);
        else normBuf.put(0).put(0).put(-1);

        texBuf[0].put(0.5f).put(1.0f);
    }

    private void setIndexData() {

        // allocate connectivity
        triangleQuantity = 2 * (zSamples - 2) * radialSamples;
        indexBuffer = BufferUtils.createIntBuffer(3*triangleQuantity);

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
                    indexBuffer.put(i0++);
                    indexBuffer.put(i1);
                    indexBuffer.put(i2);
                    indexBuffer.put(i1++);
                    indexBuffer.put(i3++);
                    indexBuffer.put(i2++);
                } else // inside view
                {
                    indexBuffer.put(i0++);
                    indexBuffer.put(i2);
                    indexBuffer.put(i1);
                    indexBuffer.put(i1++);
                    indexBuffer.put(i2++);
                    indexBuffer.put(i3++);
                }
            }
        }

        // south pole triangles
        for (int i = 0; i < radialSamples; i++, index += 3) {
            if (true) {
                indexBuffer.put(i);
                indexBuffer.put(vertQuantity - 2);
                indexBuffer.put(i + 1);
            } else // inside view
            {
                indexBuffer.put(i);
                indexBuffer.put(i + 1);
                indexBuffer.put(vertQuantity - 2);
            }
        }

        // north pole triangles
        int iOffset = (zSamples - 3) * (radialSamples + 1);
        for (int i = 0; i < radialSamples; i++, index += 3) {
            if (true) {
                indexBuffer.put(i + iOffset);
                indexBuffer.put(i + 1 + iOffset);
                indexBuffer.put(vertQuantity - 1);
            } else // inside view
            {
                indexBuffer.put(i + iOffset);
                indexBuffer.put(vertQuantity - 1);
                indexBuffer.put(i + 1 + iOffset);
            }
        }
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