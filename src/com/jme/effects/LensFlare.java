/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved.
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
package com.jme.effects;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.scene.Spatial;
import com.jme.system.JmeException;

/**
 * <code>LensFlare</code>
 *   Lens flare effect for jME.  Notice that currently, it doesn't do occlusion
 *   culling.
 *
 *   The easiest way to use this class is to use the LensFlareFactory to create
 *   your LensFlare and then attach it as a child to a lightnode.
 *   Optionally you can make it a child or a sibling of an object you wish to
 *   have a 'glint' on.  In the case of sibling, use
 *   setLocalTranslation(sibling.getLocalTranslation()) or something similar to
 *   ensure position.
 *
 *   Only FlareQuad objects are acceptable as children.
 *
 * @author Joshua Slack
 * @version $Id: LensFlare.java,v 1.16 2004-11-15 22:04:04 renanse Exp $
 */

public class LensFlare extends Node {
	private static final long serialVersionUID = 1L;
	private Vector2f midPoint;
	private Vector3f flarePoint;

	/**
	 * Creates a new LensFlare node without FlareQuad children.  Use attachChild
	 * to attach FlareQuads.
	 * @param name The name of the node.
	 */
	public LensFlare(String name) {
		super(name);
		init();
	}

	/**
	 * Init basic params of Lensflare...
	 */
	private void init() {
		DisplaySystem display = DisplaySystem.getDisplaySystem();
		midPoint = new Vector2f(display.getWidth() >> 1, display.getHeight() >> 1);

		// Set the renderstates for lensflare to all defaults...
		for (int i = 0; i < RenderState.RS_MAX_STATE; i++) {
			setRenderState(defaultStateList[i]);
		}

		// Set a alpha blending state.
		AlphaState as1 = display.getRenderer().createAlphaState();
		as1.setBlendEnabled(true);
		as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		as1.setDstFunction(AlphaState.DB_ONE);
		as1.setTestEnabled(true);
		as1.setTestFunction(AlphaState.TF_GREATER);
		as1.setEnabled(true);
		setRenderState(as1);

		setRenderQueueMode(Renderer.QUEUE_ORTHO);
		setLightCombineMode(LightState.OFF);
		setTextureCombineMode(TextureState.REPLACE);
	}

	/**
	 * Get the flare's reference midpoint, usually the center of the screen.
	 * @return Vector2f
	 */
	public Vector2f getMidPoint() {
		return midPoint;
	}

	/**
	 * Set the flare's reference midpoint, the center of the screen by default.
	 * It may be useful to change this if the whole screen is not used for a scene
	 * (for example, if part of the screen is taken up by a status bar.)
	 * @param midPoint Vector2f
	 */
	public void setMidPoint(Vector2f midPoint) {
		this.midPoint = midPoint;
	}

	/**
	 * <code>updateWorldData</code> updates all the children maintained by
	 * this node.  It decides where on the screen the flare reference point
	 * should be (by the LensFlares <i>worldTranslation</i>) and updates
	 * the children accordingly.
	 * @param time the frame time.
	 */
	public void updateWorldData(float time) {
		// Update flare:
		super.updateWorldData(time);
		// Locate light src on screen x,y
		flarePoint = DisplaySystem.getDisplaySystem().getScreenCoordinates(
				worldTranslation, flarePoint).subtractLocal(midPoint.x, midPoint.y, 0);
		if (flarePoint.z >= 1.0f) { // if it's behind us
			setForceCull(true);
			return;
		} else setForceCull(false);
		// define a line from light src to one opposite across the center point
		// draw main flare at src point

		for (int x = children.size(); --x >= 0; ) {
			FlareQuad fq = (FlareQuad)getChild(x);
			fq.updatePosition(flarePoint, midPoint);
		}
	}

	/**
	 * Calls Node's attachChild after ensuring child is a FlareQuad.
	 * @see com.jme.scene.Node.attachChild(Spatial)
	 * @param spat Spatial
	 * @return int
	 */
	public int attachChild(Spatial spat) {
		if (!(spat instanceof FlareQuad))
			throw new JmeException("Only children of type FlareQuad may be attached to LensFlare.");
		return super.attachChild(spat);
 }
}
