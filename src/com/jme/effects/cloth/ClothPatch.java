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

import com.jme.math.SpringNodeForce;
import com.jme.math.SpringSystem;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;

/**
 * <code>ClothPatch</code> is a rectangular trimesh representing a
 * piece of Cloth.  It is backed up by and shares verts and normals
 * with a SpringSystem.
 *
 * @author Joshua Slack
 * @version $Id: ClothPatch.java,v 1.2 2005-03-08 01:30:55 renanse Exp $
 */
public class ClothPatch extends TriMesh {
    private static final long serialVersionUID = 1L;

    /** width, number of nodes wide on x axis. */
	protected int clothNodesX;
	/** height, number of nodes high on y axis. */
	protected int clothNodesY;
	/** The initial spring length of structural springs. */
	protected float springLength;
	/** The underlying SpringSystem for this ClothPatch. */
	protected SpringSystem system;
	/** Internal time watch used to track time since last update. */
	protected float sinceLast = 0;
	/** Dilation factor to multiply elapsed time by for use in updating underlying system. */
	protected float timeDilation = 1.0f;

	// Temp vars used to eliminates object creation
	private Vector3f tNorm = new Vector3f();
	private Vector3f tempV1 = new Vector3f(), tempV2 = new Vector3f(), tempV3 = new Vector3f();

	/**
	 * Public constructor.
	 * @param name String
	 * @param nodesX number of nodes wide this cloth will be.
	 * @param nodesY number of nodes high this cloth will be.
	 * @param springLength distance between each node
	 * @param nodeMass mass of an individual node in this Cloth.
	 */
	public ClothPatch(String name, int nodesX, int nodesY,
			float springLength, float nodeMass) {
		super(name);
		clothNodesX = nodesX;
		clothNodesY = nodesY;
		this.springLength = springLength;

		vertex = new Vector3f[clothNodesY * clothNodesX];
		normal = new Vector3f[clothNodesY * clothNodesX];

		texture[0] = new Vector2f[clothNodesY * clothNodesX];
		indices = new int[6 * (clothNodesX - 1) * (clothNodesY - 1)];

		initCloth(nodeMass);
	}

	/**
	 * Add an external force to the underlying SpringSystem.
	 * @param force SpringNodeForce
	 */
	public void addForce(SpringNodeForce force) {
		system.addForce(force);
	}

	/**
	 * Remove a force from the underlying SpringSystem.
	 * @param force SpringNodeForce
	 * @return true if found and removed.
	 */
	public boolean removeForce(SpringNodeForce force) {
		return system.removeForce(force);
	}

	/**
	 * Update the normals in the system.
	 */
	public void updateNormals() {
		// zero out the normals
		for (int i = normal.length; --i >= 0; ) normal[i].zero();
		// go through each triangle and add the tri norm to it's corner's norms
		int i1, i2, i3;
		for (int i = indices.length-3; i >= 0; i-=3) {
			// grab triangle normal
			i1 = indices[i]; i2 = indices[i+1]; i3 = indices[i+2];
			getTriangleNormal(vertex[i1], vertex[i2], vertex[i3], tNorm);
			normal[i1].addLocal(tNorm);
			normal[i2].addLocal(tNorm);
			normal[i3].addLocal(tNorm);
    }
		// normalize
		for (int i = normal.length; --i >= 0; ) normal[i].normalizeLocal();
	}

	/**
	 * Get the normal of the triangle defined by the given vertices.  Please note
	 * that result is not normalized.
	 *
	 * @param vert1 triangle point #1
	 * @param vert2 triangle point #2
	 * @param vert3 triangle point #3
	 * @param store Vector3f to store result in
	 * @return normal of triangle, same as store param.
	 */
	protected Vector3f getTriangleNormal(Vector3f vert1, Vector3f vert2, Vector3f vert3, Vector3f store) {
		tempV1.set(vert1);
		tempV2.set(vert2);
		tempV3.set(vert3);

		//  Translate(v2, v1);
		tempV2.subtractLocal(tempV1);

		//  Translate(v3, v1);
		tempV3.subtractLocal(tempV1);

		//  Result = CrossProduct(v1, v3);
		tempV2.cross(tempV3, store);

		return store;
	}

	/**
	 * Initialize the values of the vertex, normal and texture[0] arrays.
	 * Build a SpringSystem and call setupIndices().  Then update the various
	 * buffers.
	 * @param nodeMass mass of individual node.
	 */
	protected void initCloth(float nodeMass) {
		// Setup our shared vectors...
		for (int j = 0; j < clothNodesY; j++) {
			for (int i = 0; i < clothNodesX; i++) {
				int ind = getIndex(i, j);
				vertex[ind] = new Vector3f(
						springLength * (i - 0.5f * (clothNodesX - 1)),
						springLength * (0.5f * (clothNodesY - 1) - j),
						0);
				normal[ind] = new Vector3f();
				texture[0][ind] = new Vector2f(
						(float) i / (clothNodesX - 1),
						(float) (clothNodesY-(j+1)) / (clothNodesY - 1));
			}
		}

		system = SpringSystem.createRectField(
				clothNodesX, clothNodesY, vertex, normal, nodeMass);
		setupIndices();

		updateVertexBuffer();
		updateNormalBuffer();
		updateTextureBuffer();
		updateIndexBuffer();
	}

	/**
	 * Return the underlying SpringSystem.
	 * @return SpringSystem
	 */
	public SpringSystem getSystem() {
		return system;
	}

	/**
	 * Return how many nodes high this cloth is.
	 * @return int
	 */
	public int getClothNodesY() {
		return clothNodesY;
	}

	/**
	 * Return how many nodes wide this cloth is.
	 * @return int
	 */
	public int getClothNodesX() {
		return clothNodesX;
	}

	/**
	 * Return the preset length the <i>structural</i> springs are set to.
	 * (ie. the springs running along the x and y axis connecting immediate neighbors.)
	 * @return float
	 */
	public float getSpringLength() {
		return springLength;
	}

	/**
	 * Get the time dilation factor.  See <code>setTimeDilation(float)</code> for more.
	 * @return float
	 */
	public float getTimeDilation() {
		return timeDilation;
	}

	/**
	 * Set the timedilation factor used in <code>updateWorldData(float)</code>
	 * Normally this is set to 1.  If set at 2, for example, every 25 ms of real
	 * time, the code will update the SpringSystem and cloth as if 50 ms had passed.
	 *
	 * @param timeDilation float
	 */
	public void setTimeDilation(float timeDilation) {
		this.timeDilation = timeDilation;
	}

	/**
	 * Setup the triangle indices for this cloth.
	 */
	protected void setupIndices() {
		int i = 0;
		for (int Y = 0; Y < clothNodesY - 1; Y++) {
			for (int X = 0; X < clothNodesX - 1; X++) {
				indices[i] = getIndex(X, Y);
				i++;
				indices[i] = getIndex(X, Y + 1);
				i++;
				indices[i] = getIndex(X + 1, Y + 1);
				i++;

				indices[i] = indices[i - 3];
				i++;
				indices[i] = indices[i - 2];
				i++;
				indices[i] = getIndex(X + 1, Y);
				i++;
			}
		}
	}

	/**
	 * Convienence method for calculating the array index of a given node
	 * given it's x and y coordiates.
	 *
	 * @param x int
	 * @param y int
	 * @return index
	 */
	protected int getIndex(int x, int y) {
		return y * clothNodesX + x;
	}

	/**
	 * Update the physics of this cloth.  Updates at 40 FPS (every 25 ms)
	 * @param dt time since last call to this method.  Used to determine
	 * if enough time has passed to require an update of the SpringSystem and
	 * cloth data, normals, etc.
	 */
	public void updateWorldData(float dt) {
		super.updateWorldData(dt);
		sinceLast += dt;
		if (sinceLast >= 0.025f) {  // update physics at 40 FPS
			sinceLast *= timeDilation;
			calcForces(sinceLast);
			doUpdate(sinceLast);
			sinceLast = 0;
		}
	}

	/**
	 * Calculate the forces accting on this cloth.  Called by
	 * <code>updateWorldData(float)</code>
	 * @param sinceLast float
	 */
	protected void calcForces(float sinceLast) {
		system.calcForces(sinceLast);
	}

	/**
	 * Update the spring system underlying this cloth.  Called by
	 * <code>updateWorldData(float)</code>
	 * @param sinceLast float
	 */
	protected void doUpdate(float sinceLast) {
		system.update(sinceLast);
		updateVertexBuffer();
		updateNormals();
		updateNormalBuffer();
	}

}
