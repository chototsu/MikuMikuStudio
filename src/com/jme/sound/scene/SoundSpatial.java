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

/*
 * Created on 25 janv. 2004
 *
 */
package com.jme.sound.scene;

import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.sound.ISoundRenderer;
import com.jme.sound.SoundAPIController;

/**
 * @author Arman Ozcelik
 *
 */
public abstract class SoundSpatial {

	private boolean forceCull;
	private SoundNode parent;

	public static final int CULL_FRONT= 1;
	public static final int CULL_BACK= 2;
	public static final int CULL_DISTANCE= 3;

	/**
	 * <code>setParent</code> sets the parent of this node.
	 * @param parent the parent of this node.
	 */
	public void setParent(SoundNode node) {
		parent= node;
	}

	/**
	 * <code>getParent</code> retrieve's this node's parent. If the parent is
	 * null this is the root node.
	 * @return the parent of this node.
	 */
	public SoundNode getParent() {
		return parent;
	}

	/**
	 * @param time
	 */
	public void updateWorldData(float time) {
		SoundAPIController.getSoundSystem().getListener().setPosition(
			SoundAPIController.getRenderer().getCamera().getLocation());
		float[] orientation=SoundAPIController.getSoundSystem().getListener().getOrientation();
		Vector3f dir = SoundAPIController.getRenderer().getCamera().getDirection();
		orientation[0]=dir.x;
		orientation[1]=dir.y;
		orientation[2]=dir.z;
		SoundAPIController.getSoundSystem().getListener().setOrientation(orientation);
	
	}

	/**
		 *
		 * <code>propagateBoundToRoot</code> passes the new world bound up the
		 * tree to the root.
		 *
		 */
	public void propagateBoundToRoot() {
		if (parent != null) {
			parent.propagateBoundToRoot();
		}
	}

	/**
	 * @param time
	 * @param b
	 */
	public void updateGeometricState(float time, boolean initiator) {
		updateWorldData(time);
		if (initiator) {
			propagateBoundToRoot();
		}

	}

	/**
	 *
	 * <code>onDraw</code> checks the node with the camera to see if it
	 * should be culled, if not, the node's draw method is called.
	 * @param r the renderer used for display.
	 */
	public void onDraw(ISoundRenderer r) {
		if (forceCull) {
			return;
		}
		Camera camera= r.getCamera();
		System.out.println("Camera dir" + camera.getDirection());
		//check to see if we can cull this node
		draw(r);

	}

	public abstract void draw(ISoundRenderer r);

	/**
	 * @param b
	 */
	public void setForceCull(boolean b) {
		forceCull= b;
	}

}
