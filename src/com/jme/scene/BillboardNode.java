/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding All rights reserved.
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

import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;

/**
 * <code>BillboardNode</code> defines a node that always orients towards the camera. However, it
 * does not tilt up/down as the camera rises. This keep geometry from appearing to fall over if the camera 
 * rises or lowers. <code>BillboardNode</code> is useful to contain a single quad that has a image
 * applied to it for lowest detail models. This quad, with the texture, will appear to be a full 
 * model at great distances, and save on rendering and memory. It is important to note that the
 * billboards orientation will always be up (0,1,0). This means that a standard camera with up (0,1,0)
 * is the only camera setting compatible with <code>BillboardNode</code>. 
 * 
 * @author Mark Powell
 * @version $Id: BillboardNode.java,v 1.1 2004-03-03 21:58:07 mojomonkey Exp $
 */
public class BillboardNode extends Node {
	private float lastTime;
	private Matrix3f orient;
	private Vector3f diff;
	private Vector3f loc;
	private Vector3f up;
	private Vector3f dir;
	private Vector3f left;

	/**
	 * Constructor instantiates a new <code>BillboardNode</code>. The name of the node is supplied
	 * during construction.
	 * @param name the name of the node.
	 */
	public BillboardNode(String name) {
		super(name);
		orient = new Matrix3f();
		loc = new Vector3f();
		diff = new Vector3f();
		up = new Vector3f();
		dir = new Vector3f();
		left = new Vector3f();
	}
	
	/**
	 * 
	 * <code>rotateBillboard</code> rotates the billboards rotation to orient to face the camera.
	 * First, the difference between the billboards position and camera position is determined. This
	 * distance from the projection from the billboard's normal is calculated and if this is 
	 * substantially different, the billboard is rotated until it's normals is facing the 
	 * camera.
	 * @param camera
	 */
	public void rotateBillboard(Camera camera) {
		//get the scale, translation and rotation of the node in world space
		if (parent != null) {
			worldScale = parent.getWorldScale() * localScale;
			parent.getWorldRotation().mult(localRotation, worldRotation);
			worldTranslation = parent.getWorldRotation().mult(localTranslation, worldTranslation)
			.multLocal(parent.getWorldScale())
			.addLocal(parent.getWorldTranslation());
		} else {
			worldScale = localScale;
			worldRotation = localRotation;
			worldTranslation = localTranslation;
		}

		//apply the rotation to match that of the camera's
		diff = camera.getLocation().subtract(worldTranslation);
		float invWorldScale = 1.0f / worldScale;
		worldRotation.mult(diff, loc).mult(invWorldScale, loc);

		// squared length of the camera projection in the xz-plane
		float epsilon = 1e-06f;
		float lengthSquared = loc.x * loc.x + loc.z * loc.z;
		if (lengthSquared < epsilon) {
			// camera on the billboard axis, rotation not defined
			return;
		}

		// unitize the projection
		float invLength = 1.0f / (float)Math.sqrt(lengthSquared);
		loc.x *= invLength;
		loc.y = 0.0f;
		loc.z *= invLength;

		// compute the local orientation matrix for the billboard
		left.x = loc.z;
		left.y = 0;
		left.z = -loc.x;
		
		up.x = 0;
		up.y = 1;
		up.z = 0;
		
		dir.x = loc.x;
		dir.y = 0;
		dir.z = loc.z;
		
		
		
		orient.setColumn(0, left);
		orient.setColumn(1, up);
		orient.setColumn(2, dir);
		
		//orientate the billboard
		worldRotation.apply(orient);
		
		for (int i = 0, cSize = children.size(); i < cSize; i++) {
			Spatial child = (Spatial) children.get(i);
			if (child != null) {
				child.updateGeometricState(lastTime, false);
			}
		}
	}

	/**
	 *  <code>updateWorldData</code> defers the updating of the billboards orientation until
	 * rendering. This keeps the billboard from being needlessly oriented if the player can
	 * not actually see it.
	 * @param time the time between frames.
	 * @see com.jme.scene.Spatial#updateWorldData(float)
	 */
	public void updateWorldData(float time) {
		lastTime = time;
		updateWorldBound();
	}

	/**
	 *  <code>draw</code> updates the billboards orientation then renders the billboard's
	 * children.
	 * @param r the renderer used to draw.
	 * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
	 */
	public void draw(Renderer r) {
		rotateBillboard(r.getCamera());
		super.draw(r);
	}
}
