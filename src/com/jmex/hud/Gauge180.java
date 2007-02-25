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
package com.jmex.hud;

import com.jme.image.*;
import com.jme.math.*;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.scene.state.*;
import com.jme.system.*;

/**
 * Gauge180 represents a HUD gauge that can be displayed and display a circular texture on up to
 * 180 degrees of the screen.
 * 
 * @author Matthew D. Hicks (original concept by shingoki)
 */
public class Gauge180 extends Node {
	private static final long serialVersionUID = 1L;
	
	private Quad quad;
	private Quad clipping;
	
	private Quaternion quat;
	private Vector3f dir;
	
	private int maxValue;
	private float maxRotation;
	private boolean allowPositive;
	private boolean allowNegative;

	public Gauge180(String name, Texture texture, Texture clipTexture, int maxValue, float maxRotation, boolean allowPositive, boolean allowNegative) {
		super(name);
		quad = new Quad(name + "Quad", 400, 400);
		this.maxValue = maxValue;
		this.maxRotation = maxRotation;
		this.allowPositive = allowPositive;
		this.allowNegative = allowNegative;
		TextureState textureState = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		textureState.setEnabled(true);
		textureState.setTexture(texture);
		quad.setRenderState(textureState);
		attachChild(quad);
		
		clipping = new Quad(name + "Clipping", 400, 400);
		TextureState textureStateClip = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		textureStateClip.setEnabled(true);
		textureStateClip.setTexture(clipTexture);
		clipping.setRenderState(textureStateClip);
		attachChild(clipping);
		
		quat = new Quaternion();
		dir = new Vector3f(0.0f, 0.0f, 1.0f);
	}
	
	public void setValue(int value) {
		if ((value > 0) && (!allowPositive)) value = 0;
		if ((value < 0) && (!allowNegative)) value = 0;
		float actual = ((float)value / (float)maxValue) * maxRotation;
		quat.fromAngleAxis(-actual * FastMath.PI, dir);
		clipping.getLocalRotation().set(quat);
	}
}