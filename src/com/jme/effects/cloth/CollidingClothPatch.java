/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.jme.effects.cloth;

import java.util.ArrayList;

import com.jme.bounding.OrientedBoundingBox;
import com.jme.intersection.CollisionData;
import com.jme.intersection.TriangleCollisionResults;
import com.jme.math.SpringNode;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;

/**
 * <code>CollidingClothPatch</code> is a ClothPatch with the ability to interact
 * with other objects.  Override handleCollision to change collision behavior.
 *
 * @author Joshua Slack
 * @version $Id: CollidingClothPatch.java,v 1.1 2004-11-29 21:24:55 renanse Exp $
 */
public class CollidingClothPatch extends ClothPatch {

	/** Used for storing the results of collisions. */
	protected TriangleCollisionResults results;
	/** Array of TriMesh objects to check against for collision. */
	protected ArrayList colliders;

	// Temp vars used to eliminate object creation
	protected SpringNode[] srcTemps = new SpringNode[3];
	protected Vector3f[] tgtTemps = new Vector3f[3];
	protected Vector3f calcTemp = new Vector3f();

	/**
	 * Public constructor.
	 * @param name String
	 * @param nodesX number of nodes wide this cloth will be.
	 * @param nodesY number of nodes high this cloth will be.
	 * @param springLength distance between each node
	 * @param nodeMass mass of an individual node in this Cloth.
	 */
	public CollidingClothPatch(String name, int nodesX, int nodesY, float springLength,
										float nodeMass) {
		super(name, nodesX, nodesY, springLength, nodeMass);
		setModelBound(new OrientedBoundingBox());
		updateModelBound();
		results = new TriangleCollisionResults();
		colliders = new ArrayList();
	}

	/**
	 * Calls super and then updates model bound and collision info.
	 * @param sinceLast float
	 */
	protected void calcForces(float sinceLast) {
		super.calcForces(sinceLast);
		updateModelBound();
		updateCollisionTree();
		checkForCollisions();
	}

	/**
	 * Check each collider for collision with this Cloth.
	 */
	protected void checkForCollisions() {
		CollisionData data;
		for (int x=colliders.size(); --x>=0; ) {
			results.clear();
			findCollisions((TriMesh)colliders.get(x), results);
			for (int y = results.getNumber(); --y >= 0; ) {
				data = results.getCollisionData(y);
				for (int i = 0; i < data.getSourceTris().size(); i++) {
					int srcTriIndex = ((Integer) data.getSourceTris().get(i)).intValue();
					int tgtTriIndex = ((Integer) data.getTargetTris().get(i)).intValue();
					handleCollision((TriMesh)data.getTargetMesh(), srcTriIndex, tgtTriIndex);
				}
			}
		}
		updateVertexBuffer();
	}

	/**
	 * Given the starting triangle index of the two triangles intersecting,
	 * decide what to do with those triangles.
	 *
	 * @param target TriMesh
	 * @param srcTriIndex int
	 * @param tgtTriIndex int
	 */
	protected void handleCollision(TriMesh target, int srcTriIndex, int tgtTriIndex) {
		srcTemps[0] = system.getNode(indices[srcTriIndex * 3 + 0]);
		srcTemps[1] = system.getNode(indices[srcTriIndex * 3 + 1]);
		srcTemps[2] = system.getNode(indices[srcTriIndex * 3 + 2]);

		tgtTemps[0] = target.getVertices()[target.getIndices()[tgtTriIndex * 3 + 0]];
		tgtTemps[1] = target.getVertices()[target.getIndices()[tgtTriIndex * 3 + 1]];
		tgtTemps[2] = target.getVertices()[target.getIndices()[tgtTriIndex * 3 + 2]];

		if (srcTemps[0].invMass != 0)
			srcTemps[0].position.set(tgtTemps[0]);
		if (srcTemps[1].invMass != 0)
			srcTemps[1].position.set(tgtTemps[1]);
		if (srcTemps[2].invMass != 0)
			srcTemps[2].position.set(tgtTemps[2]);

		srcTemps[0].acceleration.multLocal(.8f); // simple frictional force here.
		srcTemps[1].acceleration.multLocal(.8f);
		srcTemps[2].acceleration.multLocal(.8f);
	}

	/**
	 * Adds a TriMesh to check for collision with.
	 * @param item TriMesh
	 */
	public void addCollider(TriMesh item) {
		colliders.add(item);
	}

	/**
	 * Remove a given TriMesh from collision consideration.
	 * @param item TriMesh
	 * @return true if found and removed
	 */
	public boolean removeCollider(TriMesh item) {
		return colliders.remove(item);
	}

}
