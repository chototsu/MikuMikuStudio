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

package com.jme.scene;

import java.io.Serializable;

import com.jme.intersection.CollisionResults;
import com.jme.renderer.Renderer;
import com.jme.scene.batch.GeomBatch;

/**
 * <code>BatchMesh</code> is a Mesh for using one or several types of
 * different batches such as <code>PointBatch</code>, <code>LineBatch</code>,
 * <code>TriangleBatch</code> and <code>QuadBatch</code>. <br>
 * It does not support OBBTree for triangle accurate collision.
 * 
 * @author Tijl Houtbeckers
 * @version $Id: BatchMesh.java,v 1.1 2006-09-11 23:37:42 llama Exp $
 */
public class BatchMesh extends Geometry implements Serializable {

	private static final long serialVersionUID = 7639644164611314728L;

	/**
	 * Empty Constructor to be used internally only.
	 */
	public BatchMesh() {
		super();
	}

	/**
	 * Constructor instantiates a new <code>BatchMesh</code> object.
	 * 
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparision purposes.
	 */
	public BatchMesh(String name) {
		super(name);
	}

	/**
	 * Constructor instantiates a new <code>BatchMesh</code> object.
	 * 
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparision purposes.
	 * 
	 * @param batches
	 *            The batch(es) to use with this BatchMesh.
	 * 
	 */
	public BatchMesh(String name, GeomBatch... batches) {
		super(name);
		if (batches != null && batches.length > 0)
			setupBatchList(batches);
	}

	protected void setupBatchList(GeomBatch[] batches) {
		removeBatch(0);
		batchList.ensureCapacity(batches.length);
		for (GeomBatch batch : batches)
			addBatch(batch);
	}

	@Override
	public void findCollisions(Spatial scene, CollisionResults results) {
		if (this == scene || !isCollidable || !scene.isCollidable()) {
			return;
		}

		if (getWorldBound().intersects(scene.getWorldBound())) {
			if ((scene.getType() & SceneElement.NODE) != 0) {
				Node parent = (Node) scene;
				for (int i = 0; i < parent.getQuantity(); i++) {
					findCollisions(parent.getChild(i), results);
				}
			} else {
				results.addCollision(this, (Geometry) scene);
			}
		}

	}

	@Override
	public boolean hasCollision(Spatial scene, boolean checkTriangles) {
		if (this == scene || !isCollidable || !scene.isCollidable()) {
			return false;
		}
		if (getWorldBound().intersects(scene.getWorldBound())) {
			if ((scene.getType() & SceneElement.NODE) != 0) {
				Node parent = (Node) scene;
				for (int i = 0; i < parent.getQuantity(); i++) {
					if (hasCollision(parent.getChild(i), checkTriangles)) {
						return true;
					}
				}
				return false;
			}
			// we do not check for triangles yet.
			return true;
		}

		return false;
	}
	
	/**
     * <code>draw</code> calls the onDraw method of the batches in this BatchMesh
     * if they are enabled.
     * 
     * @param r
     *            the renderer to display
     */
    public void draw(Renderer r) {
        GeomBatch batch;
        if (getBatchCount() == 1) {
            batch = getBatch(0);
            if (batch != null && batch.isEnabled()) {
                batch.setLastFrustumIntersection(frustrumIntersects);
                batch.draw(r);
                return;
            }
        }

        for (int i = 0, cSize = getBatchCount(); i < cSize; i++) {
            batch = getBatch(i);
            if (batch != null && batch.isEnabled())
                batch.onDraw(r);
        }
    }

}
