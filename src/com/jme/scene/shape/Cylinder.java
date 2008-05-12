/*
 * Copyright (c) 2003-2008 jMonkeyEngine
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

import java.io.IOException;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Cylinder</code> provides an extension of <code>TriMesh</code>. A
 * <code>Cylinder</code> is defined by a height and radius. The center of the
 * Cylinder is the origin.
 * 
 * @author Mark Powell
 * @version $Id: Cylinder.java,v 1.16 2007/09/21 15:45:27 nca Exp $
 */
public class Cylinder extends TriMesh {

    private static final long serialVersionUID = 1L;

    private int axisSamples;

    private int radialSamples;

    private float radius;
    private float radius2;

    private float height;
    private boolean closed;
    private boolean inverted;

    public Cylinder() {
    }

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
        this(name, axisSamples, radialSamples, radius, height, false);
    }

    /**
     * Creates a new Cylinder. By default its center is the origin. Usually, a
     * higher sample number creates a better looking cylinder, but at the cost
     * of more vertex information. <br>
     * If the cylinder is closed the texture is split into axisSamples parts:
     * top most and bottom most part is used for top and bottom of the cylinder,
     * rest of the texture for the cylinder wall. The middle of the top is
     * mapped to texture coordinates (0.5, 1), bottom to (0.5, 0). Thus you need
     * a suited distorted texture.
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
     * @param closed
     *            true to create a cylinder with top and bottom surface
     */
    public Cylinder(String name, int axisSamples, int radialSamples,
            float radius, float height, boolean closed) {
        this(name, axisSamples, radialSamples, radius, height, closed, false);
    }

    /**
     * Creates a new Cylinder. By default its center is the origin. Usually, a
     * higher sample number creates a better looking cylinder, but at the cost
     * of more vertex information. <br>
     * If the cylinder is closed the texture is split into axisSamples parts:
     * top most and bottom most part is used for top and bottom of the cylinder,
     * rest of the texture for the cylinder wall. The middle of the top is
     * mapped to texture coordinates (0.5, 1), bottom to (0.5, 0). Thus you need
     * a suited distorted texture.
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
     * @param closed
     *            true to create a cylinder with top and bottom surface
     * @param inverted
     *            true to create a cylinder that is meant to be viewed from the
     *            interior.
     */
    public Cylinder(String name, int axisSamples, int radialSamples,
            float radius, float height, boolean closed, boolean inverted) {

        super(name);

        this.axisSamples = axisSamples + (closed ? 2 : 0);
        this.radialSamples = radialSamples;
        setRadius(radius);
        this.height = height;
        this.closed = closed;
        this.inverted = inverted;

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
     * Change the radius of this cylinder. This resets any second radius.
     * 
     * @param radius
     *            The radius to set.
     */
    public void setRadius(float radius) {
        this.radius = radius;
        this.radius2 = radius;
        allocateVertices();
    }

    /**
     * Set the top radius of the 'cylinder' to differ from the bottom radius.
     * 
     * @param radius
     *            The first radius to set.
     * @see Cone
     */
    public void setRadius1(float radius) {
        this.radius = radius;
        allocateVertices();
    }

    /**
     * Set the bottom radius of the 'cylinder' to differ from the top radius.
     * This makes the Geometry be a frustum of pyramid, or if set to 0, a cone.
     * 
     * @param radius
     *            The second radius to set.
     * @see Cone
     */
    public void setRadius2(float radius) {
        this.radius2 = radius;
        allocateVertices();
    }

    /**
     * @return the number of samples along the cylinder axis
     */
    public int getAxisSamples() {
        return axisSamples;
    }

    /**
     * @return true if end caps are used.
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * @return true if normals and uvs are created for interior use
     */
    public boolean isInverted() {
        return inverted;
    }

    /**
     * @return number of samples around cylinder
     */
    public int getRadialSamples() {
        return radialSamples;
    }

    private void allocateVertices() {
        // allocate vertices
        setVertexCount(axisSamples * (radialSamples + 1) + (closed ? 2 : 0));
        setVertexBuffer(BufferUtils.createVector3Buffer(getVertexBuffer(),
                getVertexCount()));

        // allocate normals if requested
        setNormalBuffer(BufferUtils.createVector3Buffer(getNormalBuffer(),
                getVertexCount()));

        // allocate texture coordinates
        getTextureCoords().set(0,
                new TexCoords(BufferUtils.createVector2Buffer(getVertexCount())));

        setTriangleQuantity(((closed ? 2 : 0) + 2 * (axisSamples - 1))
                * radialSamples);
        setIndexBuffer(BufferUtils.createIntBuffer(getIndexBuffer(),
                3 * getTriangleCount()));

        setGeometryData();
        setIndexData();
    }

    private void setGeometryData() {
        // generate geometry
        float inverseRadial = 1.0f / radialSamples;
        float inverseAxisLess = 1.0f / (closed ? axisSamples - 3
                : axisSamples - 1);
        float inverseAxisLessTexture = 1.0f / (axisSamples - 1);
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
            float axisFraction;
            float axisFractionTexture;
            int topBottom = 0;
            if (!closed) {
                axisFraction = axisCount * inverseAxisLess; // in [0,1]
                axisFractionTexture = axisFraction;
            } else {
                if (axisCount == 0) {
                    topBottom = -1; // bottom
                    axisFraction = 0;
                    axisFractionTexture = inverseAxisLessTexture;
                } else if (axisCount == axisSamples - 1) {
                    topBottom = 1; // top
                    axisFraction = 1;
                    axisFractionTexture = 1 - inverseAxisLessTexture;
                } else {
                    axisFraction = (axisCount - 1) * inverseAxisLess;
                    axisFractionTexture = axisCount * inverseAxisLessTexture;
                }
            }
            float z = -halfHeight + height * axisFraction;

            // compute center of slice
            Vector3f sliceCenter = new Vector3f(0, 0, z);

            // compute slice vertices with duplication at end point
            int save = i;
            for (int radialCount = 0; radialCount < radialSamples; radialCount++) {
                float radialFraction = radialCount * inverseRadial; // in [0,1)
                tempNormal.set(cos[radialCount], sin[radialCount], 0);
                if (topBottom == 0) {
                    if (!inverted)
                        getNormalBuffer().put(tempNormal.x).put(tempNormal.y)
                                .put(tempNormal.z);
                    else
                        getNormalBuffer().put(-tempNormal.x).put(-tempNormal.y)
                                .put(-tempNormal.z);
                } else {
                    getNormalBuffer().put(0).put(0).put(topBottom * (inverted ? -1 : 1));
                }

                tempNormal.multLocal(
                        (radius - radius2) * axisFraction + radius2).addLocal(
                        sliceCenter);
                getVertexBuffer().put(tempNormal.x).put(tempNormal.y).put(
                        tempNormal.z);

                getTextureCoords().get(0).coords.put(
                        (inverted ? 1 - radialFraction : radialFraction)).put(
                        axisFractionTexture);
                i++;
            }

            BufferUtils.copyInternalVector3(getVertexBuffer(), save, i);
            BufferUtils.copyInternalVector3(getNormalBuffer(), save, i);

            getTextureCoords().get(0).coords.put((inverted ? 0.0f : 1.0f)).put(
                    axisFractionTexture);

            i++;
        }

        if (closed) {
            getVertexBuffer().put(0).put(0).put(-halfHeight); // bottom center
            getNormalBuffer().put(0).put(0).put(-1 * (inverted ? -1 : 1));
            getTextureCoords().get(0).coords.put(0.5f).put(0);
            getVertexBuffer().put(0).put(0).put(halfHeight); // top center
            getNormalBuffer().put(0).put(0).put(1 * (inverted ? -1 : 1));
            getTextureCoords().get(0).coords.put(0.5f).put(1);
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
                if (closed && axisCount == 0) {
                    if (!inverted) {
                        getIndexBuffer().put(i0++);
                        getIndexBuffer().put(getVertexCount() - 2);
                        getIndexBuffer().put(i1++);
                    } else {
                        getIndexBuffer().put(i0++);
                        getIndexBuffer().put(i1++);
                        getIndexBuffer().put(getVertexCount() - 2);
                    }
                } else if (closed && axisCount == axisSamples - 2) {
                    if (!inverted) {
                        getIndexBuffer().put(i2++);
                        getIndexBuffer().put(i3++);
                        getIndexBuffer().put(getVertexCount() - 1);
                    } else {
                        getIndexBuffer().put(i2++);
                        getIndexBuffer().put(getVertexCount() - 1);
                        getIndexBuffer().put(i3++);
                    }
                } else {
                    if (!inverted) {
                        getIndexBuffer().put(i0++);
                        getIndexBuffer().put(i1);
                        getIndexBuffer().put(i2);
                        getIndexBuffer().put(i1++);
                        getIndexBuffer().put(i3++);
                        getIndexBuffer().put(i2++);
                    } else {
                        getIndexBuffer().put(i0++);
                        getIndexBuffer().put(i2);
                        getIndexBuffer().put(i1);
                        getIndexBuffer().put(i1++);
                        getIndexBuffer().put(i2++);
                        getIndexBuffer().put(i3++);
                    }
                }
            }
        }
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(axisSamples, "axisSamples", 0);
        capsule.write(radialSamples, "radialSamples", 0);
        capsule.write(radius, "radius", 0);
        capsule.write(radius2, "radius2", 0);
        capsule.write(height, "height", 0);
        capsule.write(closed, "closed", false);
		capsule.write(inverted, "inverted", false);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        axisSamples = capsule.readInt("axisSamples", 0);
        radialSamples = capsule.readInt("radialSamples", 0);
        radius = capsule.readFloat("radius", 0);
        radius2 = capsule.readFloat("radius2", 0);
        height = capsule.readFloat("height", 0);
        closed = capsule.readBoolean("closed", false);
        inverted = capsule.readBoolean("inverted", false);
    }
}
