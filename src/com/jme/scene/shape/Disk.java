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

import java.io.IOException;
import java.nio.FloatBuffer;

import com.jme.math.FastMath;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Disk</code> is a flat circle. It is simply defined with a radius. It
 * starts out flat along the Z, with center at the origin.
 * 
 * @author Mark Powell
 * @version $Id: Disk.java,v 1.10 2006-05-11 19:39:25 nca Exp $
 */
public class Disk extends TriMesh {

	private static final long serialVersionUID = 1L;

	private int shellSamples;

	private int radialSamples;

	private float radius;
    
    public Disk() {}

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
        TriangleBatch batch = getBatch(0);

		// allocate vertices
		batch.setVertexCount(1 + radialSamples * shellLess);
		batch.setVertexBuffer(BufferUtils.createVector3Buffer(batch.getVertexCount()));
		batch.setNormalBuffer(BufferUtils.createVector3Buffer(batch.getVertexCount()));
		batch.getTextureBuffers().set(0, BufferUtils.createVector3Buffer(batch.getVertexCount()));

		batch.setTriangleQuantity(radialSamples * (2 * shellLess - 1));
		batch.setIndexBuffer(BufferUtils.createIntBuffer(3 * batch.getTriangleCount()));

		setGeometryData(shellLess);
        setDefaultColor(ColorRGBA.white);
		setIndexData(radialless, shellLess);

	}

	private void setGeometryData(int shellLess) {
		// generate geometry
        TriangleBatch batch = getBatch(0);

		// center of disk
	    batch.getVertexBuffer().put(0).put(0).put(0);
		
		for (int x = 0; x < batch.getVertexCount(); x++)
		    batch.getNormalBuffer().put(0).put(0).put(1);
		
        batch.getTextureBuffers().get(0).put(.5f).put(.5f);

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
				BufferUtils.setInBuffer(texCoord, ((FloatBuffer)batch.getTextureBuffers().get(0)), i);

				radialFraction.multLocal(radius);
				BufferUtils.setInBuffer(radialFraction, batch.getVertexBuffer(), i);
			}
		}
	}

	private void setIndexData(int radialless, int shellLess) {
		// generate connectivity
        TriangleBatch batch = getBatch(0);
		int index = 0;
		for (int radialCount0 = radialless, radialCount1 = 0; radialCount1 < radialSamples; radialCount0 = radialCount1++) {
			batch.getIndexBuffer().put(0);
			batch.getIndexBuffer().put(1 + shellLess * radialCount0);
			batch.getIndexBuffer().put(1 + shellLess * radialCount1);
			index += 3;
			for (int iS = 1; iS < shellLess; iS++, index += 6) {
				int i00 = iS + shellLess * radialCount0;
				int i01 = iS + shellLess * radialCount1;
				int i10 = i00 + 1;
				int i11 = i01 + 1;
				batch.getIndexBuffer().put(i00);
				batch.getIndexBuffer().put(i10);
				batch.getIndexBuffer().put(i11);
				batch.getIndexBuffer().put(i00);
				batch.getIndexBuffer().put(i11);
				batch.getIndexBuffer().put(i01);
			}
		}
	}
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(shellSamples, "shellSamples", 0);
        capsule.write(radialSamples, "radialSamples", 0);
        capsule.write(radius, "radius", 0);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        shellSamples = capsule.readInt("shellSamples", 0);
        radialSamples = capsule.readInt("radialSamples", 0);
        radius = capsule.readFloat("raidus", 0);
    }
}