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
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Disk</code> is a flat circle. It is simply defined with a radius. It
 * starts out flat along the Z, with center at the origin.
 * 
 * @author Mark Powell
 * @version $Id: Disk.java,v 1.9 2006-03-17 20:04:18 nca Exp $
 */
public class Disk extends TriMesh {

	private static final long serialVersionUID = 1L;

	private int shellSamples;

	private int radialSamples;

	private float radius;

	/**
	 * Creates a flat disk (circle) at the origin flat along the Z. Usually, a
	 * higher sample number creates a better looking cylinder, but at the cost
	 * of more vertex information.
	 * 
	 * @param name
	 *            The name of the disk.
	 * @param shellSamples
	 *            The number of shell samples.
	 * @param radialSamples
	 *            The number of radial samples.
	 * @param radius
	 *            The radius of the disk.
	 */
	public Disk(String name, int shellSamples, int radialSamples, float radius) {
		super(name);

		this.shellSamples = shellSamples;
		this.radialSamples = radialSamples;
		this.radius = radius;

		int radialless = radialSamples - 1;
		int shellLess = shellSamples - 1;

		// allocate vertices
		batch.setVertQuantity(1 + radialSamples * shellLess);
		batch.setVertBuf(BufferUtils.createVector3Buffer(batch.getVertQuantity()));
		batch.setNormBuf(BufferUtils.createVector3Buffer(batch.getVertQuantity()));
		batch.getTexBuf().set(0, BufferUtils.createVector3Buffer(batch.getVertQuantity()));

		((TriangleBatch)batch).setTriangleQuantity(radialSamples * (2 * shellLess - 1));
		((TriangleBatch)batch).setIndexBuffer(BufferUtils.createIntBuffer(3 * ((TriangleBatch)batch).getTriangleQuantity()));

		setGeometryData(shellLess);
        setDefaultColor(ColorRGBA.white);
		setIndexData(radialless, shellLess);

	}

	private void setGeometryData(int shellLess) {
		// generate geometry

		// center of disk
	    batch.getVertBuf().put(0).put(0).put(0);
		
		for (int x = 0; x < batch.getVertQuantity(); x++)
		    batch.getNormBuf().put(0).put(0).put(1);
		
        ((FloatBuffer)batch.getTexBuf().get(0)).put(.5f).put(.5f);

		float inverseShellLess = 1.0f / (float) shellLess;
		float inverseRadial = 1.0f / (float) radialSamples;
		Vector3f radialFraction = new Vector3f();
		Vector2f texCoord = new Vector2f();
		for (int radialCount = 0; radialCount < radialSamples; radialCount++) {
			float angle = FastMath.TWO_PI * inverseRadial * radialCount;
			float cos = FastMath.cos(angle);
			float sin = FastMath.sin(angle);
			Vector3f radial = new Vector3f(cos, sin, 0);

			for (int shellCount = 1; shellCount < shellSamples; shellCount++) {
				float fraction = inverseShellLess * shellCount; // in (0,R]
				radialFraction.set(radial).multLocal(fraction);
				int i = shellCount + shellLess * radialCount;
				texCoord.x = 0.5f * (1.0f + radialFraction.x);
				texCoord.y = 0.5f * (1.0f + radialFraction.y);
				BufferUtils.setInBuffer(texCoord, ((FloatBuffer)batch.getTexBuf().get(0)), i);

				radialFraction.multLocal(radius);
				BufferUtils.setInBuffer(radialFraction, batch.getVertBuf(), i);
			}
		}
	}

	private void setIndexData(int radialless, int shellLess) {
		// generate connectivity
		int index = 0;
		for (int radialCount0 = radialless, radialCount1 = 0; radialCount1 < radialSamples; radialCount0 = radialCount1++) {
			((TriangleBatch)batch).getIndexBuffer().put(0);
			((TriangleBatch)batch).getIndexBuffer().put(1 + shellLess * radialCount0);
			((TriangleBatch)batch).getIndexBuffer().put(1 + shellLess * radialCount1);
			index += 3;
			for (int iS = 1; iS < shellLess; iS++, index += 6) {
				int i00 = iS + shellLess * radialCount0;
				int i01 = iS + shellLess * radialCount1;
				int i10 = i00 + 1;
				int i11 = i01 + 1;
				((TriangleBatch)batch).getIndexBuffer().put(i00);
				((TriangleBatch)batch).getIndexBuffer().put(i10);
				((TriangleBatch)batch).getIndexBuffer().put(i11);
				((TriangleBatch)batch).getIndexBuffer().put(i00);
				((TriangleBatch)batch).getIndexBuffer().put(i11);
				((TriangleBatch)batch).getIndexBuffer().put(i01);
			}
		}
	}
}