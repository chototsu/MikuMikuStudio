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
package com.jme.scene;

import com.jme.renderer.Camera;

/**
 * <code>CameraNode</code> defines a node that contains a camera object. This
 * allows a camera to be controlled by any other node, and allows the camera to
 * be attached to any node. A call to <code>updateWorldData</code> will adjust
 * the camera's frame by the world translation and the world rotation. The
 * column 0 of the world rotation matrix is used for the camera left vector,
 * column 1 is used for the camera up vector, column 2 is used for the camera
 * direction vector.
 * 
 * @author Mark Powell
 * @version $Id: CameraNode.java,v 1.5 2004-09-14 21:52:13 mojomonkey Exp $
 */
public class CameraNode extends Node {
	private static final long serialVersionUID = 1L;

	private Camera camera;

	/**
	 * Constructor instantiates a new <code>CameraNode</code> object setting
	 * the camera to use for the frame reference.
	 * 
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparision purposes.
	 * @param camera
	 *            the camera this node controls.
	 */
	public CameraNode(String name, Camera camera) {
		super(name);
		this.camera = camera;
	}

	/**
	 * 
	 * <code>setCamera</code> sets the camera that this node controls.
	 * 
	 * @param camera
	 *            the camera that this node controls.
	 */
	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	/**
	 * 
	 * <code>getCamera</code> retrieves the camera object that this node
	 * controls.
	 * 
	 * @return the camera this node controls.
	 */
	public Camera getCamera() {
		return camera;
	}

	/**
	 * <code>updateWorldData</code> updates the rotation and translation of
	 * this node, and sets the camera's frame buffer to reflect the current
	 * view.
	 * 
	 * @param time
	 *            the time between frames.
	 */
	public void updateWorldData(float time) {
		super.updateWorldData(time);
		if (camera != null) {
			camera.setFrame(worldTranslation, worldRotation);
		}
	}
}