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
 * <code>Torus</code> is um ... a Torus :) The center is by default the
 * origin.
 * 
 * @author Mark Powell
 * @version $Id: Torus.java,v 1.9 2006-03-17 20:04:16 nca Exp $
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
		setDefaultColor(ColorRGBA.white);

	}

	private void setGeometryData() {

        // allocate vertices
	    batch.setVertQuantity((circleSamples + 1) * (radialSamples + 1));
        batch.setVertBuf(BufferUtils.createVector3Buffer(batch.getVertQuantity()));

        // allocate normals if requested
        batch.setNormBuf(BufferUtils.createVector3Buffer(batch.getVertQuantity()));

        // allocate texture coordinates
        batch.getTexBuf().set(0, BufferUtils.createVector2Buffer(batch.getVertQuantity()));

		// generate geometry
		float inverseCircleSamples = 1.0f / (float) circleSamples;
		float inverseRadialSamples = 1.0f / (float) radialSamples;
		int i = 0;
		// generate the cylinder itself
		Vector3f radialAxis = new Vector3f(), torusMiddle = new Vector3f(), tempNormal = new Vector3f();
		for (int circleCount = 0; circleCount < circleSamples; circleCount++) {
			// compute center point on torus circle at specified angle
			float circleFraction = circleCount * inverseCircleSamples;
			float theta = FastMath.TWO_PI * circleFraction;
			float cosTheta = FastMath.cos(theta);
			float sinTheta = FastMath.sin(theta);
			radialAxis.set(cosTheta, sinTheta, 0);
			radialAxis.mult(outerRadius, torusMiddle);

			// compute slice vertices with duplication at end point
			int iSave = i;
			for (int radialCount = 0; radialCount < radialSamples; radialCount++) {
				float radialFraction = radialCount * inverseRadialSamples;
				// in [0,1)
				float phi = FastMath.TWO_PI * radialFraction;
				float cosPhi = FastMath.cos(phi);
				float sinPhi = FastMath.sin(phi);
				tempNormal.set(radialAxis).multLocal(cosPhi);
				tempNormal.z += sinPhi;
				if (true)
				    batch.getNormBuf().put(tempNormal.x).put(tempNormal.y).put(tempNormal.z);
				else
					batch.getNormBuf().put(-tempNormal.x).put(-tempNormal.y).put(-tempNormal.z);

				tempNormal.multLocal(innerRadius).addLocal(torusMiddle);
				batch.getVertBuf().put(tempNormal.x).put(tempNormal.y).put(tempNormal.z);

                ((FloatBuffer)batch.getTexBuf().get(0)).put(radialFraction).put(circleFraction);
				i++;
			}

            BufferUtils.copyInternalVector3(batch.getVertBuf(), iSave, i);
            BufferUtils.copyInternalVector3(batch.getNormBuf(), iSave, i);

            ((FloatBuffer)batch.getTexBuf().get(0)).put(1.0f).put(circleFraction);

            i++;
		}

		// duplicate the cylinder ends to form a torus
		for (int iR = 0; iR <= radialSamples; iR++, i++) {
            BufferUtils.copyInternalVector3(batch.getVertBuf(), iR, i);
            BufferUtils.copyInternalVector3(batch.getNormBuf(), iR, i);
            BufferUtils.copyInternalVector2(((FloatBuffer)batch.getTexBuf().get(0)), iR, i);
            ((FloatBuffer)batch.getTexBuf().get(0)).put(i*2+1, 1.0f);
		}
	}

	private void setIndexData() {
		//      allocate connectivity
		((TriangleBatch)batch).setTriangleQuantity(2 * circleSamples * radialSamples);
		((TriangleBatch)batch).setIndexBuffer(BufferUtils.createIntBuffer(3 * ((TriangleBatch)batch).getTriangleQuantity()));
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
					((TriangleBatch)batch).getIndexBuffer().put(i0++);
					((TriangleBatch)batch).getIndexBuffer().put(i2);
					((TriangleBatch)batch).getIndexBuffer().put(i1);
					((TriangleBatch)batch).getIndexBuffer().put(i1++);
					((TriangleBatch)batch).getIndexBuffer().put(i2++);
					((TriangleBatch)batch).getIndexBuffer().put(i3++);
				} else {
					((TriangleBatch)batch).getIndexBuffer().put(i0++);
					((TriangleBatch)batch).getIndexBuffer().put(i1);
					((TriangleBatch)batch).getIndexBuffer().put(i2);
					((TriangleBatch)batch).getIndexBuffer().put(i1++);
					((TriangleBatch)batch).getIndexBuffer().put(i3++);
					((TriangleBatch)batch).getIndexBuffer().put(i2++);
				}
			}
		}
	}
}