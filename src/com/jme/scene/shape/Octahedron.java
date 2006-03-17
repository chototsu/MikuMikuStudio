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
 * <code>Octahedron</code> is an eight faced polyhedron. It looks somewhat
 * like two pyramids placed bottom to bottom.
 * 
 * @author Mark Powell
 * @version $Id: Octahedron.java,v 1.9 2006-03-17 20:04:17 nca Exp $
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
		batch.setVertQuantity(NUM_POINTS);
		batch.setVertBuf(BufferUtils.createVector3Buffer(NUM_POINTS));
		batch.setNormBuf(BufferUtils.createVector3Buffer(NUM_POINTS));
		batch.getTexBuf().set(0, BufferUtils.createVector2Buffer(NUM_POINTS));

		((TriangleBatch)batch).setTriangleQuantity(NUM_TRIS);
		((TriangleBatch)batch).setIndexBuffer(BufferUtils.createIntBuffer(3 * ((TriangleBatch)batch).getTriangleQuantity()));

		setVertexData();
		setNormalData();
		setDefaultColor(ColorRGBA.white);
		setTextureData();
		setIndexData();

	}

	private void setIndexData() {
		((TriangleBatch)batch).getIndexBuffer().rewind();
		((TriangleBatch)batch).getIndexBuffer().put(4);
		((TriangleBatch)batch).getIndexBuffer().put(0);
		((TriangleBatch)batch).getIndexBuffer().put(2);
		((TriangleBatch)batch).getIndexBuffer().put(4);
		((TriangleBatch)batch).getIndexBuffer().put(2);
		((TriangleBatch)batch).getIndexBuffer().put(1);
		((TriangleBatch)batch).getIndexBuffer().put(4);
		((TriangleBatch)batch).getIndexBuffer().put(1);
		((TriangleBatch)batch).getIndexBuffer().put(3);
		((TriangleBatch)batch).getIndexBuffer().put(4);
		((TriangleBatch)batch).getIndexBuffer().put(3);
		((TriangleBatch)batch).getIndexBuffer().put(0);
		((TriangleBatch)batch).getIndexBuffer().put(5);
		((TriangleBatch)batch).getIndexBuffer().put(2);
		((TriangleBatch)batch).getIndexBuffer().put(0);
		((TriangleBatch)batch).getIndexBuffer().put(5);
		((TriangleBatch)batch).getIndexBuffer().put(1);
		((TriangleBatch)batch).getIndexBuffer().put(2);
		((TriangleBatch)batch).getIndexBuffer().put(5);
		((TriangleBatch)batch).getIndexBuffer().put(3);
		((TriangleBatch)batch).getIndexBuffer().put(1);
		((TriangleBatch)batch).getIndexBuffer().put(5);
		((TriangleBatch)batch).getIndexBuffer().put(0);
		((TriangleBatch)batch).getIndexBuffer().put(3);

		if (!true) {
			for (int i = 0; i < ((TriangleBatch)batch).getTriangleQuantity(); i++) {
				int iSave = ((TriangleBatch)batch).getIndexBuffer().get(3 * i + 1);
				((TriangleBatch)batch).getIndexBuffer().put(3 * i + 1, ((TriangleBatch)batch).getIndexBuffer().get(3 * i + 2));
				((TriangleBatch)batch).getIndexBuffer().put(3 * i + 2, iSave);
			}
		}
	}

	private void setTextureData() {
	    Vector2f tex = new Vector2f();
	    Vector3f vert = new Vector3f();
		for (int i = 0; i < NUM_POINTS; i++) {
		    BufferUtils.populateFromBuffer(vert, batch.getVertBuf(), i);
			if (FastMath.abs(vert.z) < sideLength) {
			    tex.x = 0.5f * (1.0f + FastMath.atan2(vert.y,
						vert.x)
						* FastMath.INV_PI);
			} else {
			    tex.x = 0.5f;
			}
			tex.y = FastMath.acos(vert.z) * FastMath.INV_PI;
            ((FloatBuffer)batch.getTexBuf().get(0)).put(tex.x).put(tex.y);
		}
	}

	private void setNormalData() {
	    Vector3f norm = new Vector3f();
		for (int i = 0; i < NUM_POINTS; i++) {
		    BufferUtils.populateFromBuffer(norm, batch.getVertBuf(), i);
		    norm.normalizeLocal();
		    BufferUtils.setInBuffer(norm, batch.getNormBuf(), i);
		}
	}

	private void setVertexData() {
		batch.getVertBuf().put(sideLength).put(0.0f).put(0.0f);
		batch.getVertBuf().put(-sideLength).put(0.0f).put(0.0f);
		batch.getVertBuf().put(0.0f).put(sideLength).put(0.0f);
		batch.getVertBuf().put(0.0f).put(-sideLength).put(0.0f);
		batch.getVertBuf().put(0.0f).put(0.0f).put(sideLength);
		batch.getVertBuf().put(0.0f).put(0.0f).put(-sideLength);
	}
}