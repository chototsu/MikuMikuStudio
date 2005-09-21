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
 * <code>Cylinder</code> provides an extension of <code>TriMesh</code>. A
 * <code>Cylinder</code> is defined by a height and radius. The center of the
 * Cylinder is the origin.
 * 
 * @author Mark Powell
 * @version $Id: Cylinder.java,v 1.7 2005-09-21 17:52:54 renanse Exp $
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

        allocateVertices();

    }

    /**
     * @return Returns the height.
     */
    public float getHeight() {
        return height;
    }

    /**
     * @param height
     *            The height to set.
     */
    public void setHeight(float height) {
        this.height = height;
        allocateVertices();
    }

    /**
     * @return Returns the radius.
     */
    public float getRadius() {
        return radius;
    }

    /**
     * @param radius
     *            The radius to set.
     */
    public void setRadius(float radius) {
        this.radius = radius;
        allocateVertices();
    }

    private void allocateVertices() {
        // allocate vertices
        vertQuantity = axisSamples * (radialSamples + 1);
        vertBuf = BufferUtils.createVector3Buffer(vertQuantity);

        // allocate normals if requested
        normBuf = BufferUtils.createVector3Buffer(vertQuantity);

        // allocate texture coordinates
        texBuf[0] = BufferUtils.createVector2Buffer(vertQuantity);

        triangleQuantity = 2 * (axisSamples - 1) * radialSamples;
        indexBuffer = BufferUtils.createIntBuffer(3 * triangleQuantity);

        setGeometryData();
        setIndexData();
        
        setDefaultColor(ColorRGBA.white);
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
        Vector3f tempNormal = new Vector3f();
        for (int axisCount = 0, i = 0; axisCount < axisSamples; axisCount++) {
            float axisFraction = axisCount * inverseAxisLess; // in [0,1]
            float z = -halfHeight + height * axisFraction;

            // compute center of slice
            Vector3f sliceCenter = new Vector3f(0, 0, z);

            // compute slice vertices with duplication at end point
            int save = i;
            for (int radialCount = 0; radialCount < radialSamples; radialCount++) {
                float radialFraction = radialCount * inverseRadial; // in [0,1)
                tempNormal.set(cos[radialCount], sin[radialCount], 0);
                if (true) normBuf.put(tempNormal.x).put(tempNormal.y).put(tempNormal.z);
                else normBuf.put(-tempNormal.x).put(-tempNormal.y).put(-tempNormal.z);

				tempNormal.multLocal(radius).addLocal(sliceCenter);
				vertBuf.put(tempNormal.x).put(tempNormal.y).put(tempNormal.z);

                texBuf[0].put(radialFraction).put(axisFraction);
                i++;
            }

            BufferUtils.copyInternalVector3(vertBuf, save, i);
            BufferUtils.copyInternalVector3(normBuf, save, i);

            texBuf[0].put(1.0f).put(axisFraction);

            i++;
        }
    }

    private void setIndexData() {
        // generate connectivity
        for (int axisCount = 0, axisStart = 0; axisCount < axisSamples - 1; axisCount++) {
            int i0 = axisStart;
            int i1 = i0 + 1;
            axisStart += radialSamples + 1;
            int i2 = axisStart;
            int i3 = i2 + 1;
            for (int i = 0; i < radialSamples; i++) {
                if (true) {
                    indexBuffer.put(i0++);
                    indexBuffer.put(i1);
                    indexBuffer.put(i2);
                    indexBuffer.put(i1++);
                    indexBuffer.put(i3++);
                    indexBuffer.put(i2++);
                } else {
                    indexBuffer.put(i0++);
                    indexBuffer.put(i2);
                    indexBuffer.put(i1);
                    indexBuffer.put(i1++);
                    indexBuffer.put(i2++);
                    indexBuffer.put(i3++);
                }
            }
        }
    }
}