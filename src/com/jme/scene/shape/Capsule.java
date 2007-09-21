/*
 * Copyright (c) 2003-2007 jMonkeyEngine
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
import java.nio.FloatBuffer;

import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Capsule</code> provides an extension of <code>TriMesh</code>. A
 * <code>Capsule</code> is defined by a height and a radius. The center of the
 * Cylinder is the origin.
 * 
 * @author Joshua Slack
 * @version $Id: Capsule.java,v 1.2 2007-09-21 15:45:28 nca Exp $
 */
public class Capsule extends TriMesh {

    private static final long serialVersionUID = 1L;

    private int axisSamples, radialSamples, sphereSamples;
    private float radius, height;

    public Capsule() {}
    
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
     */
    public Capsule(String name, int axisSamples, int radialSamples, int sphereSamples,
                    float radius, float height) {

        super(name);

        this.axisSamples = axisSamples;
        this.sphereSamples = sphereSamples;
        this.radialSamples = radialSamples;
        this.radius = radius;
        this.height = height;

        recreateBuffers();
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
        recreateBuffers();
    }

    /**
     * @return Returns the radius.
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Change the radius of this cylinder.
     * @param radius
     *            The radius to set.
     */
    public void setRadius(float radius) {
        this.radius = radius;
        setGeometryData();
    }

    private void recreateBuffers() {
        TriangleBatch batch = getBatch(0);

        // determine vert quantity - first the sphere caps
        int sampleLines = (2 * sphereSamples - 1 + axisSamples);
        int verts = (radialSamples + 1) * sampleLines + 2;
        
        batch.setVertexCount(verts);
        batch.setVertexBuffer(BufferUtils.createVector3Buffer(batch.getVertexBuffer(), batch.getVertexCount()));

        // allocate normals
        batch.setNormalBuffer(BufferUtils.createVector3Buffer(batch.getNormalBuffer(), batch.getVertexCount()));

        // allocate texture coordinates
        batch.getTextureBuffers().set(0, BufferUtils.createVector2Buffer(batch.getVertexCount()));

        // determine tri quantity
        int tris = 2 * radialSamples * sampleLines;

        batch.setTriangleQuantity(tris);
        batch.setIndexBuffer(BufferUtils.createIntBuffer(batch.getIndexBuffer(), 3 * batch.getTriangleCount()));

        setGeometryData();
        setIndexData();
    }

    private void setGeometryData() {
        TriangleBatch batch = getBatch(0);
        FloatBuffer verts = batch.getVertexBuffer();
        FloatBuffer norms = batch.getNormalBuffer();
        FloatBuffer texs = batch.getTextureBuffer(0);
        verts.rewind();
        norms.rewind();
        texs.rewind();

        // generate geometry
        float inverseRadial = 1.0f / radialSamples;
        float inverseSphere = 1.0f / sphereSamples;
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
        
        Vector3f tempA = new Vector3f();
        
        // top point.
        verts.put(0).put(radius+halfHeight).put(0);
        norms.put(0).put(1).put(0);
        texs.put(1).put(1);

        // generating the top dome.
        for (int i = 0; i < sphereSamples; i++) {
            float center = radius * (1 - (i+1)*(inverseSphere));
            float lengthFraction = (center + height + radius) / (height + 2*radius);
            
            // compute radius of slice
            float fSliceRadius = FastMath.sqrt(FastMath.abs(radius * radius - center * center));

            for (int j = 0; j <= radialSamples; j++) {
                Vector3f kRadial = tempA.set(cos[j], 0, sin[j]);
                kRadial.multLocal(fSliceRadius);
                verts.put(kRadial.x).put(center + halfHeight).put(kRadial.z);
                kRadial.y = center;
                kRadial.normalizeLocal();
                norms.put(kRadial.x).put(kRadial.y).put(kRadial.z);
                float radialFraction = 1-(j * inverseRadial); // in [0,1)
                texs.put(radialFraction).put(lengthFraction);
            }
        }

        // generate cylinder... but no need to add points for first and last
        // samples as they are already part of domes.
        for (int i = 1; i < axisSamples; i++) {
            float center = halfHeight - (i * height / axisSamples);
            float lengthFraction = (center + halfHeight + radius) / (height + 2*radius);

            for (int j = 0; j <= radialSamples; j++) {
                Vector3f kRadial = tempA.set(cos[j], 0, sin[j]);
                kRadial.multLocal(radius);
                verts.put(kRadial.x).put(center).put(
                        kRadial.z);
                kRadial.normalizeLocal();
                norms.put(kRadial.x).put(kRadial.y).put(kRadial.z);
                float radialFraction = 1-(j * inverseRadial); // in [0,1)
                texs.put(radialFraction).put(lengthFraction);
            }

        }


        // generating the bottom dome.
        for (int i = 0; i < sphereSamples; i++) {
            float center = i * (radius/sphereSamples);
            float lengthFraction = (radius - center) / (height + 2*radius);
            
            // compute radius of slice
            float fSliceRadius = FastMath.sqrt(FastMath.abs(radius * radius - center * center));
            
            for (int j = 0; j <= radialSamples; j++) {
                Vector3f kRadial = tempA.set(cos[j], 0, sin[j]);
                kRadial.multLocal(fSliceRadius);
                verts.put(kRadial.x).put(-center - halfHeight).put(kRadial.z);
                kRadial.y = -center;
                kRadial.normalizeLocal();
                norms.put(kRadial.x).put(kRadial.y).put(kRadial.z);
                float radialFraction = 1-(j * inverseRadial); // in [0,1)
                texs.put(radialFraction).put(lengthFraction);
            }
        }

        // bottom point.
        verts.put(0).put(-radius-halfHeight).put(0);
        norms.put(0).put(-1).put(0);
        texs.put(0).put(0);

    }

    private void setIndexData() {
        TriangleBatch tb = getBatch(0);
        
        // start with top of top dome.
        for (int samples = 1; samples <= radialSamples; samples++) {
            tb.getIndexBuffer().put(samples + 1);
            tb.getIndexBuffer().put(samples);
            tb.getIndexBuffer().put(0);
        }
        
        for (int plane = 1; plane < (sphereSamples); plane++) {
            int topPlaneStart = plane * (radialSamples+1);
            int bottomPlaneStart = (plane - 1) * (radialSamples+1);
            for (int sample = 1; sample <= radialSamples; sample++) {
              tb.getIndexBuffer().put(bottomPlaneStart + sample);
              tb.getIndexBuffer().put(bottomPlaneStart + sample + 1);
              tb.getIndexBuffer().put(topPlaneStart + sample);
              tb.getIndexBuffer().put(bottomPlaneStart + sample + 1);
              tb.getIndexBuffer().put(topPlaneStart + sample + 1);
              tb.getIndexBuffer().put(topPlaneStart + sample);
            }
        }

        int start = sphereSamples * (radialSamples+1);

        // add cylinder
        for (int plane = 0; plane < (axisSamples); plane++) {
            int topPlaneStart = start + plane * (radialSamples+1);
            int bottomPlaneStart = start + (plane - 1) * (radialSamples+1);
            for (int sample = 1; sample <= radialSamples; sample++) {
              tb.getIndexBuffer().put(bottomPlaneStart + sample);
              tb.getIndexBuffer().put(bottomPlaneStart + sample + 1);
              tb.getIndexBuffer().put(topPlaneStart + sample);
              tb.getIndexBuffer().put(bottomPlaneStart + sample + 1);
              tb.getIndexBuffer().put(topPlaneStart + sample + 1);
              tb.getIndexBuffer().put(topPlaneStart + sample);
            }
        }

        start += ((axisSamples-1) * (radialSamples+1));
        
        // Add most of the bottom dome triangles.
        for (int plane = 1; plane < (sphereSamples); plane++) {
            int topPlaneStart = start + plane * (radialSamples+1);
            int bottomPlaneStart = start + (plane - 1) * (radialSamples+1);
            for (int sample = 1; sample <= radialSamples; sample++) {
              tb.getIndexBuffer().put(bottomPlaneStart + sample);
              tb.getIndexBuffer().put(bottomPlaneStart + sample + 1);
              tb.getIndexBuffer().put(topPlaneStart + sample);
              tb.getIndexBuffer().put(bottomPlaneStart + sample + 1);
              tb.getIndexBuffer().put(topPlaneStart + sample + 1);
              tb.getIndexBuffer().put(topPlaneStart + sample);
            }
        }

        start += ((sphereSamples-1) * (radialSamples+1));
        // Finally the bottom of bottom dome.
        for (int samples = 1; samples <= radialSamples; samples++) {
            tb.getIndexBuffer().put(start+samples);
            tb.getIndexBuffer().put(start+samples + 1);
            tb.getIndexBuffer().put(start+radialSamples+2);
        }
    }
    
    public void reconstruct(Vector3f top, Vector3f bottom, float radius) {
        // first make the capsule the right shape
        this.height = top.distance(bottom);
        this.radius = radius;
        setGeometryData();
        
        // now orient it in space.
        top.add(bottom, localTranslation).multLocal(.5f);  // ok we got translation.
        
        // we need the rotation that takes us from 0,1,0 to the unit vector described by top/center.
        Vector3f capsuleUp = top.subtract(localTranslation).normalizeLocal();
        Matrix3f rotation = new Matrix3f();
        rotation.fromStartEndVectors(Vector3f.UNIT_Y, capsuleUp);
        localRotation.fromRotationMatrix(rotation);
        updateWorldVectors();
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(axisSamples, "axisSamples", 0);
        capsule.write(radialSamples, "radialSamples", 0);
        capsule.write(sphereSamples, "sphereSamples", 0);
        capsule.write(radius, "radius", 0);
        capsule.write(height, "height", 0);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        axisSamples = capsule.readInt("circleSamples", 0);
        radialSamples = capsule.readInt("radialSamples", 0);
        sphereSamples = capsule.readInt("sphereSamples", 0);
        radius = capsule.readFloat("radius", 0);
        height = capsule.readFloat("height", 0);
    }
}
