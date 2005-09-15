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
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Octahedron</code> is an eight faced polyhedron. It looks somewhat
 * like two pyramids placed bottom to bottom.
 * 
 * @author Mark Powell
 * @version $Id: Octahedron.java,v 1.5 2005-09-15 17:13:43 renanse Exp $
 */
public class Octahedron extends TriMesh {
	private static final long serialVersionUID = 1L;

	private static final int NUM_POINTS = 6;

	private static final int NUM_TRIS = 8;

	private float sideLength;

	/**
	 * Creates an octahedron with center at the origin. The lenght sides are
	 * given.
	 * 
	 * @param name
	 *            The name of the octahedron.
	 * @param sideLength
	 *            The length of each side of the octahedron.
	 */
	public Octahedron(String name, float sideLength) {
		super(name);
		this.sideLength = sideLength;

		// allocate vertices
		vertQuantity = NUM_POINTS;
		vertBuf = BufferUtils.createVector3Buffer(NUM_POINTS);
		normBuf = BufferUtils.createVector3Buffer(NUM_POINTS);
		texBuf[0] = BufferUtils.createVector2Buffer(NUM_POINTS);

		triangleQuantity = NUM_TRIS;
		indexBuffer = BufferUtils.createIntBuffer(3 * triangleQuantity);

		setVertexData();
		setNormalData();
		setSolidColor(ColorRGBA.white);
		setTextureData();
		setIndexData();

	}

	private void setIndexData() {
	    indexBuffer.rewind();
	    indexBuffer.put(4);
	    indexBuffer.put(0);
	    indexBuffer.put(2);
	    indexBuffer.put(4);
	    indexBuffer.put(2);
	    indexBuffer.put(1);
	    indexBuffer.put(4);
	    indexBuffer.put(1);
	    indexBuffer.put(3);
	    indexBuffer.put(4);
	    indexBuffer.put(3);
	    indexBuffer.put(0);
	    indexBuffer.put(5);
	    indexBuffer.put(2);
	    indexBuffer.put(0);
	    indexBuffer.put(5);
	    indexBuffer.put(1);
	    indexBuffer.put(2);
	    indexBuffer.put(5);
	    indexBuffer.put(3);
	    indexBuffer.put(1);
	    indexBuffer.put(5);
	    indexBuffer.put(0);
	    indexBuffer.put(3);

		if (!true) {
			for (int i = 0; i < triangleQuantity; i++) {
				int iSave = indexBuffer.get(3 * i + 1);
				indexBuffer.put(3 * i + 1, indexBuffer.get(3 * i + 2));
				indexBuffer.put(3 * i + 2, iSave);
			}
		}
	}

	private void setTextureData() {
	    Vector2f tex = new Vector2f();
	    Vector3f vert = new Vector3f();
		for (int i = 0; i < NUM_POINTS; i++) {
		    BufferUtils.populateFromBuffer(vert, vertBuf, i);
			if (FastMath.abs(vert.z) < sideLength) {
			    tex.x = 0.5f * (1.0f + FastMath.atan2(vert.y,
						vert.x)
						* FastMath.INV_PI);
			} else {
			    tex.x = 0.5f;
			}
			tex.y = FastMath.acos(vert.z) * FastMath.INV_PI;
			texBuf[0].put(tex.x).put(tex.y);
		}
	}

	private void setNormalData() {
	    Vector3f norm = new Vector3f();
		for (int i = 0; i < NUM_POINTS; i++) {
		    BufferUtils.populateFromBuffer(norm, vertBuf, i);
		    norm.normalizeLocal();
		    BufferUtils.setInBuffer(norm, normBuf, i);
		}
	}

	private void setVertexData() {
	    vertBuf.put(sideLength).put(0.0f).put(0.0f);
	    vertBuf.put(-sideLength).put(0.0f).put(0.0f);
	    vertBuf.put(0.0f).put(sideLength).put(0.0f);
	    vertBuf.put(0.0f).put(-sideLength).put(0.0f);
	    vertBuf.put(0.0f).put(0.0f).put(sideLength);
	    vertBuf.put(0.0f).put(0.0f).put(-sideLength);
	}
}