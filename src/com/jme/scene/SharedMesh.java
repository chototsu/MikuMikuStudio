/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
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
package com.jme.scene;

import com.jme.bounding.BoundingVolume;
import com.jme.renderer.Renderer;
import com.jme.scene.state.RenderState;

/**
 * <code>SharedMesh</code> allows the sharing of data between multiple nodes.
 * A provided TriMesh is used as the model for this node. This allows the user
 * to place multiple copies of the same object throughout the scene without
 * having to duplicate data. It should be known that any change to the provided
 * target mesh will affect the appearance of this mesh, including animations.
 * <br>
 * <b>Important:</b> It is highly recommended that the Target mesh is NOT
 * placed into the scenegraph, as it's translation, rotation and scale are
 * replaced by the shared meshes using it before they are rendered. <br>
 * <b>Note:</b> Special thanks to Kevin Glass.
 * 
 * @author Mark Powell
 * @version $id$
 */
public class SharedMesh extends TriMesh {
	private static final long serialVersionUID = 1L;

	private TriMesh target;

	/**
	 * Constructor creates a new <code>SharedMesh</code> object.
	 * 
	 * @param name
	 *            the name of this shared mesh.
	 * @param target
	 *            the TriMesh to share the data.
	 */
	public SharedMesh(String name, TriMesh target) {
		super(name);
		setTarget(target);
	}

	/**
	 * <code>setTarget</code> sets the shared data mesh.
	 * 
	 * @param target
	 *            the TriMesh to share the data.
	 */
	private void setTarget(TriMesh target) {
		this.target = target;

		RenderState[] states = target.getRenderStateList();
		for (int i = 0; i < states.length; i++) {
			if (states[i] != null) {
				setRenderState(states[i]);
			}
		}
	}

	/**
	 * <code>updateWorldBound</code> updates the bounding volume that contains
	 * this geometry. The location of the geometry is based on the location of
	 * all this node's parents.
	 * 
	 * @see com.jme.scene.Spatial#updateWorldBound()
	 */
	public void updateWorldBound() {
		if (target.getModelBound() != null) {
			worldBound = target.getModelBound().transform(worldRotation,
					worldTranslation, worldScale, worldBound);
		}
	}

	/**
	 * <code>updateBound</code> recalculates the bounding object assigned to
	 * the geometry. This resets it parameters to adjust for any changes to the
	 * vertex information.
	 * 
	 */
	public void updateModelBound() {
		if (target.getModelBound() != null) {
			target.getModelBound().computeFromPoints(vertex);
			updateWorldBound();
		}
	}

	/**
	 * returns the model bound of the target object.
	 */
	public BoundingVolume getModelBound() {
		return target.getModelBound();
	}

	/**
	 * draw renders the target mesh, at the translation, rotation and scale of
	 * this shared mesh.
	 * 
	 * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
	 */
	public void draw(Renderer r) {
		applyStates();

		if (!r.isProcessingQueue()) {
			if (r.checkAndAdd(this))
				return;
		}
		target.setLocalTranslation(worldTranslation);
		target.setLocalRotation(worldRotation);
		target.setLocalScale(worldScale);

		r.draw(target);
	}
}
