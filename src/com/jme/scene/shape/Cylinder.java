/*
 * Copyright (c) 2003-2006 jMonkeyEngine
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

import java.nio.FloatBuffer;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Cylinder</code> provides an extension of <code>TriMesh</code>. A
 * <code>Cylinder</code> is defined by a height and radius. The center of the
 * Cylinder is the origin.
 * 
 * @author Mark Powell
 * @version $Id: Cylinder.java,v 1.12 2006-03-30 09:47:26 irrisor Exp $
 */
public class Cylinder extends TriMesh {

    private static final long serialVersionUID = 1L;

    private int axisSamples;

    private int radialSamples;

    private float radius;
    private float radius2;

    private float height;
    private boolean closed;

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
        this( name, axisSamples, radialSamples, radius, height, false );
    }

    /**
     * Creates a new Cylinder. By default its center is the origin. Usually, a
     * higher sample number creates a better looking cylinder, but at the cost
     * of more vertex information.
     * <br>
     * If the cylinder is closed the texture is split into axisSamples parts: top most and bottom most part is used for
     * top and bottom of the cylinder, rest of the texture for the cylinder wall. The middle of the top is mapped to
     * texture coordinates (0.5, 1), bottom to (0.5, 0). Thus you need a suited distorted texture.
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
                    float radius, float height, boolean closed ) {

        super(name);

        this.axisSamples = axisSamples + (closed ? 2 : 0);
        this.radialSamples = radialSamples;
        setRadius( radius );
        this.height = height;
        this.closed = closed;

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
     * @param radius
     *            The radius to set.
     */
    public void setRadius(float radius) {
        this.radius = radius;
        this.radius2 = radius;
        allocateVertices();
    }

    /**
     * Set the bottom radius of the 'cylinder' to differ from the top radius. This makes the Geometry be a frustum of
     * pyramid, or if set to 0, a cone.
     * @param radius
     *            The second radius to set.
     */
    public void setRadius2(float radius) {
        this.radius2 = radius;
        allocateVertices();
    }

    private void allocateVertices() {
        // allocate vertices
        batch.setVertQuantity(axisSamples * (radialSamples + 1) + (closed ? 2 : 0));
        batch.setVertBuf(BufferUtils.createVector3Buffer(batch.getVertQuantity()));

        // allocate normals if requested
        batch.setNormBuf(BufferUtils.createVector3Buffer(batch.getVertQuantity()));

        // allocate texture coordinates
        batch.getTexBuf().set(0, BufferUtils.createVector2Buffer(batch.getVertQuantity()));

        ((TriangleBatch)batch).setTriangleQuantity(((closed ? 2 : 0) + 2 * (axisSamples - 1) ) * radialSamples);
        ((TriangleBatch)batch).setIndexBuffer(BufferUtils.createIntBuffer(3 * ((TriangleBatch)batch).getTriangleQuantity()));

        setGeometryData();
        setIndexData();
        
        setDefaultColor(ColorRGBA.white);
    }

    private void setGeometryData() {
        // generate geometry
        float inverseRadial = 1.0f / (float) radialSamples;
        float inverseAxisLess = 1.0f / (float) (closed ? axisSamples - 3 : axisSamples - 1);
        float inverseAxisLessTexture = 1.0f / (float) (axisSamples - 1);
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
            if ( !closed ) {
                axisFraction = axisCount * inverseAxisLess; // in [0,1]
                axisFractionTexture = axisFraction;
            } else {
                if ( axisCount == 0 ) {
                    topBottom = -1; // bottom
                    axisFraction = 0;
                    axisFractionTexture = inverseAxisLessTexture;
                } else if ( axisCount == axisSamples-1 ) {
                    topBottom = 1; // top
                    axisFraction = 1;
                    axisFractionTexture = 1 - inverseAxisLessTexture;
                } else {
                    axisFraction = (axisCount-1)*inverseAxisLess;
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
                if ( topBottom == 0 ) {
                    if (true) batch.getNormBuf().put(tempNormal.x).put(tempNormal.y).put(tempNormal.z);
                    else batch.getNormBuf().put(-tempNormal.x).put(-tempNormal.y).put(-tempNormal.z);
                } else {
                	batch.getNormBuf().put( 0 ).put( 0 ).put( 1 );
                }

                tempNormal.multLocal(radius).addLocal(sliceCenter);
                batch.getVertBuf().put(tempNormal.x).put(tempNormal.y).put(tempNormal.z);

                ((FloatBuffer)batch.getTexBuf().get(0)).put(radialFraction).put(axisFractionTexture);
                i++;
            }

            BufferUtils.copyInternalVector3(batch.getVertBuf(), save, i);
            BufferUtils.copyInternalVector3(batch.getNormBuf(), save, i);

            ((FloatBuffer)batch.getTexBuf().get(0)).put(1.0f).put(axisFractionTexture);

            i++;
        }

        if ( closed ) {
        	batch.getVertBuf().put( 0 ).put( 0 ).put( -halfHeight ); // bottom center
            batch.getNormBuf().put( 0 ).put( 0 ).put( 1 );
            ((FloatBuffer)batch.getTexBuf().get(0)).put(0.5f).put(0);
            batch.getVertBuf().put( 0 ).put( 0 ).put( halfHeight ); // top center
            batch.getNormBuf().put( 0 ).put( 0 ).put( 1 );
            ((FloatBuffer)batch.getTexBuf().get(0)).put(0.5f).put(1);
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
                if ( closed && axisCount == 0 ) {
                	((TriangleBatch)batch).getIndexBuffer().put( i0++ );
                	((TriangleBatch)batch).getIndexBuffer().put( i1++ );
                	((TriangleBatch)batch).getIndexBuffer().put( batch.getVertQuantity() - 2 );
                }
                else if ( closed && axisCount == axisSamples - 2 ) {
                	((TriangleBatch)batch).getIndexBuffer().put( i2++ );
                	((TriangleBatch)batch).getIndexBuffer().put( i3++ );
                	((TriangleBatch)batch).getIndexBuffer().put( batch.getVertQuantity() - 1 );
                } else {
                    if (true) {
                    	((TriangleBatch)batch).getIndexBuffer().put(i0++);
                    	((TriangleBatch)batch).getIndexBuffer().put(i1);
                    	((TriangleBatch)batch).getIndexBuffer().put(i2);
                    	((TriangleBatch)batch).getIndexBuffer().put(i1++);
                    	((TriangleBatch)batch).getIndexBuffer().put(i3++);
                    	((TriangleBatch)batch).getIndexBuffer().put(i2++);
                    } else {
                    	((TriangleBatch)batch).getIndexBuffer().put(i0++);
                    	((TriangleBatch)batch).getIndexBuffer().put(i2);
                    	((TriangleBatch)batch).getIndexBuffer().put(i1);
                    	((TriangleBatch)batch).getIndexBuffer().put(i1++);
                    	((TriangleBatch)batch).getIndexBuffer().put(i2++);
                    	((TriangleBatch)batch).getIndexBuffer().put(i3++);
                    }
                }
            }
        }
    }
}