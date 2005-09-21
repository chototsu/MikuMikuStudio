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
 * A <code>dome</code> is a half sphere.
 * 
 * @author Peter Andersson
 * @author Joshua Slack (Original sphere code that was adapted)
 * @version $Id:
 */
public class Dome extends TriMesh {
    private static final long serialVersionUID = 1L;

    private int planes;

    private int radialSamples;

    /** The radius of the dome */
    public float radius;

    /** The center of the dome */
    public Vector3f center;

    private static Vector3f tempVa = new Vector3f();

    private static Vector3f tempVb = new Vector3f();

    private static Vector3f tempVc = new Vector3f();

    /**
     * Constructs a dome. By default the dome has not geometry data or center.
     * 
     * @param name
     *            The name of the dome.
     */
    public Dome(String name) {
        super(name);
    }

    /**
     * Constructs a dome with center at the origin. For details, see the other
     * constructor.
     * 
     * @param name
     *            Name of dome.
     * @param planes
     *            The number of planes along the Z-axis.
     * @param radialSamples
     *            The samples along the radial.
     * @param radius
     *            Radius of the dome.
     * @see #dome(java.lang.String, com.jme.math.Vector3f, int, int, float)
     */
    public Dome(String name, int planes, int radialSamples, float radius) {
        this(name, new Vector3f(0, 0, 0), planes, radialSamples, radius);
    }

    /**
     * Constructs a dome. All geometry data buffers are updated automatically.
     * Both planes and radialSamples increase the quality of the generated dome.
     * 
     * @param name
     *            Name of the dome.
     * @param center
     *            Center of the dome.
     * @param planes
     *            The number of planes along the Z-axis.
     * @param radialSamples
     *            The number of samples along the radial.
     * @param radius
     *            The radius of the dome.
     */
    public Dome(String name, Vector3f center, int planes, int radialSamples,
            float radius) {

        super(name);
        setData(center, planes, radialSamples, radius, true, true);
    }

    /**
     * Changes the information of the dome into the given values. The boolean at
     * the end signals if buffer data should be updated as well. If the dome is
     * to be rendered, then that value should be true.
     * 
     * @param center
     *            The new center of the dome.
     * @param planes
     *            The number of planes along the Z-axis.
     * @param radialSamples
     *            The new number of radial samples of the dome.
     * @param radius
     *            The new radius of the dome.
     * @param updateBuffers
     *            If true, buffer information is updated as well.
     * @param outsideView
     *            If true, the triangles will be connected for a view outside of
     *            the dome.
     */
    public void setData(Vector3f center, int planes, int radialSamples,
            float radius, boolean updateBuffers, boolean outsideView) {
        if (center != null)
            this.center = center;
        else
            this.center = new Vector3f(0, 0, 0);
        this.planes = planes;
        this.radialSamples = radialSamples;
        this.radius = radius;

        if (updateBuffers) {
            setGeometryData(outsideView);
            setIndexData(outsideView);
            setDefaultColor(ColorRGBA.white);
        }
    }

    /**
     * Generates the vertices of the dome
     * 
     * @param outsideView
     *            If the dome should be viewed from the outside (if not zbuffer
     *            is used)
     */
    private void setGeometryData(boolean outsideView) {

        // allocate vertices, we need one extra in each radial to get the
        // correct texture coordinates
        vertQuantity = ((planes - 1) * (radialSamples + 1)) + 1;
        vertBuf = BufferUtils.createVector3Buffer(vertQuantity);

        // allocate normals
        normBuf = BufferUtils.createVector3Buffer(vertQuantity);

        // allocate texture coordinates
        texBuf[0] = BufferUtils.createVector2Buffer(vertQuantity);

        // generate geometry
        float fInvRS = 1.0f / (float) radialSamples;
        float fYFactor = 1.0f / (float) (planes - 1);

        // Generate points on the unit circle to be used in computing the mesh
        // points on a cylinder slice.
        float[] afSin = new float[(radialSamples)];
        float[] afCos = new float[(radialSamples)];
        for (int iR = 0; iR < radialSamples; iR++) {
            float fAngle = FastMath.TWO_PI * fInvRS * iR;
            afCos[iR] = FastMath.cos(fAngle);
            afSin[iR] = FastMath.sin(fAngle);
        }

        // generate the cylinder itself
        int i = 0;
        for (int iY = 0; iY < (planes - 1); iY++) {
            float fYFraction = fYFactor * iY; // in (0,1)
            float fY = radius * fYFraction;
            // compute center of slice
            Vector3f kSliceCenter = tempVb.set(center);
            kSliceCenter.y += fY;

            // compute radius of slice
            float fSliceRadius = FastMath.sqrt(FastMath.abs(radius * radius
                    - fY * fY));

            // compute slice vertices
            Vector3f kNormal;
            int iSave = i;
            for (int iR = 0; iR < radialSamples; iR++) {
                float fRadialFraction = iR * fInvRS; // in [0,1)
                Vector3f kRadial = tempVc.set(afCos[iR], 0, afSin[iR]);
                kRadial.mult(fSliceRadius, tempVa);
                vertBuf.put(kSliceCenter.x + tempVa.x).put(kSliceCenter.y + tempVa.y).put(kSliceCenter.z + tempVa.z);

                BufferUtils.populateFromBuffer(tempVa, vertBuf, i);
                kNormal = tempVa.subtractLocal(center);
                kNormal.normalizeLocal();
                if (outsideView)
                    normBuf.put(kNormal.x).put(kNormal.y).put(kNormal.z);
                else 
                    normBuf.put(-kNormal.x).put(-kNormal.y).put(-kNormal.z);

                texBuf[0].put(fRadialFraction).put(fYFraction);

                i++;
            }

            BufferUtils.copyInternalVector3(vertBuf, iSave, i);
            BufferUtils.copyInternalVector3(normBuf, iSave, i);

            texBuf[0].put(1.0f).put(fYFraction);

            i++;
        }

        // pole
        vertBuf.put(center.x).put(center.y+radius).put(center.z);

        if (outsideView)
            normBuf.put(0).put(1).put(0);
        else
            normBuf.put(0).put(-1).put(0);

        texBuf[0].put(0.5f).put(1.0f);
    }

    /**
     * Generates the connections
     * 
     * @param outsideView
     *            True if the dome should be viewed from the outside (if not
     *            using z buffer)
     */
    private void setIndexData(boolean outsideView) {

        // allocate connectivity
        triangleQuantity = (planes - 2) * radialSamples * 2 + radialSamples;
        indexBuffer = BufferUtils.createIntBuffer(3 * triangleQuantity);

        // generate connectivity
        int index = 0;
        // Generate only for middle planes
        for (int plane = 1; plane < (planes - 1); plane++) {
            int bottomPlaneStart = (plane - 1) * (radialSamples + 1);
            int topPlaneStart = plane * (radialSamples + 1);
            for (int sample = 0; sample < radialSamples; sample++, index += 6) {
                if (outsideView) {
                    indexBuffer.put(bottomPlaneStart + sample);
                    indexBuffer.put(bottomPlaneStart + sample + 1);
                    indexBuffer.put(topPlaneStart + sample);
                    indexBuffer.put(bottomPlaneStart + sample + 1);
                    indexBuffer.put(topPlaneStart + sample + 1);
                    indexBuffer.put(topPlaneStart + sample);
                } else // inside view
                {
                    indexBuffer.put(bottomPlaneStart + sample);
                    indexBuffer.put(topPlaneStart + sample);
                    indexBuffer.put(bottomPlaneStart + sample + 1);
                    indexBuffer.put(bottomPlaneStart + sample + 1);
                    indexBuffer.put(topPlaneStart + sample);
                    indexBuffer.put(topPlaneStart + sample + 1);
                }
            }
        }

        // pole triangles
        int bottomPlaneStart = (planes - 2) * (radialSamples + 1);
        for (int samples = 0; samples < radialSamples; samples++, index += 3) {
            if (outsideView) {
                indexBuffer.put(bottomPlaneStart + samples);
                indexBuffer.put(bottomPlaneStart + samples + 1);
                indexBuffer.put(vertQuantity - 1);
            } else // inside view 
            {
                indexBuffer.put(bottomPlaneStart + samples);
                indexBuffer.put(vertQuantity - 1);
                indexBuffer.put(bottomPlaneStart + samples + 1);
            }
        }
    }
}