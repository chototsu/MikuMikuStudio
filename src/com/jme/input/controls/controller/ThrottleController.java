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
package com.jme.input.controls.controller;

import com.jme.input.controls.*;
import com.jme.scene.*;

/**
 * ThrottleController manages forward and backward thrust on a Spatial
 * 
 * @author Matthew D. Hicks
 */
public class ThrottleController extends Controller {
	private static final long serialVersionUID = 1L;

	private Spatial spatial;
	private GameControl forward;
	private float maxForwardThrottle;
	private GameControl reverse;
	private float maxReverseThrottle;
	private float multiplier;
	private Axis axis;
	
	private float currentThrottle;
	
	// TODO should afterburner be added to this?
	public ThrottleController(Spatial spatial, GameControl forward, float maxForwardThrottle, GameControl reverse, float maxReverseThrottle, float multiplier, Axis axis) {
		this.spatial = spatial;
		this.forward = forward;
		this.maxForwardThrottle = maxForwardThrottle;
		this.reverse = reverse;
		this.maxReverseThrottle = maxReverseThrottle;
		this.multiplier = multiplier;
		this.axis = axis;
	}
	
	public GameControl getForward() {
		return forward;
	}
	
	public GameControl getReverse() {
		return reverse;
	}
	
	public float getCurrentThrottle() {
		return currentThrottle;
	}
	
	public float getThrust() {
		return forward.getValue() - reverse.getValue();
	}
	
	public void update(float time) {
		float value = getThrust();
		float delta = time * multiplier;
		float max = maxForwardThrottle;
		float min = maxReverseThrottle;
		if (value > 0.0f) {
			max = maxForwardThrottle * value;
		} else if (value < 0.0f) {
			min = maxReverseThrottle * -value;
		} else {
			if (currentThrottle > 0.0f) {
				min = 0.0f;
			} else {
				max = 0.0f;
			}
		}
		if (value < 0.0f) {
			delta = -delta;
		} else if (value == 0.0f) {
			if (currentThrottle > 0.0f) {
				if (forward.hasTrueAxis()) {
					delta = -delta;		// Degrade back to zero
				} else {
					delta = 0.0f;
				}
			} else if (currentThrottle < 0.0f) {
				if (reverse.hasTrueAxis()) {
					// Degrade back to zero
				} else {
					delta = 0.0f;
				}
			}
		}
		if ((currentThrottle > 0.0f) && (currentThrottle + delta < 0.0f)) {
			currentThrottle = 0.0f;
		} else if ((currentThrottle < 0.0f) && (currentThrottle + delta > 0.0f)) {
			currentThrottle = 0.0f;
		} else {
			currentThrottle += delta;
		}
		if (currentThrottle > max) {
			currentThrottle = max;
		} else if (currentThrottle < min) {
			currentThrottle = min;
		}
		if (currentThrottle != 0.0f) {
			if (axis == Axis.X) {
				spatial.getLocalTranslation().addLocal(spatial.getLocalRotation().getRotationColumn(0).mult(currentThrottle));
			} else if (axis == Axis.Y) {
				spatial.getLocalTranslation().addLocal(spatial.getLocalRotation().getRotationColumn(1).mult(currentThrottle));
			} else if (axis == Axis.Z) {
				spatial.getLocalTranslation().addLocal(spatial.getLocalRotation().getRotationColumn(2).mult(currentThrottle));
			} else {
				throw new RuntimeException("Unknown axis: " + axis);
			}
		}
	}
}