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
package com.jme.scene.lod;

import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.SwitchModel;
import com.jme.scene.SwitchNode;

/**
 * <code>DiscreteLodNode</code>
 * @author Mark Powell
 * @version $Id: DiscreteLodNode.java,v 1.2 2004-04-22 22:26:49 renanse Exp $
 */
public class DiscreteLodNode extends SwitchNode {
	private Vector3f modelCenter;
	private Vector3f worldCenter;



	private float lastUpdate;
	private SwitchModel model;

	public DiscreteLodNode(String name, SwitchModel model) {
		super(name);
		this.model = model;

		modelCenter = new Vector3f();

	}

	public void selectLevelOfDetail (Camera camera)
	{
		super.updateWorldData(lastUpdate);

		// compute world LOD center
		worldCenter = worldTranslation.add(worldRotation.mult(modelCenter).mult(worldScale));

		// compute world squared distance intervals
		float worldSqrScale = worldScale*worldScale;
		model.set(worldCenter.subtract(camera.getLocation()));
		model.set(worldSqrScale);
		setActiveChild(model.getSwitchChild());

	}



	public void updateWorldData (float time) {
		lastUpdate = time;

		updateWorldBound();
	}

	public void draw (Renderer r) {
		selectLevelOfDetail(r.getCamera());
		super.draw(r);
	}
}
