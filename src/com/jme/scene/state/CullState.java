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

package com.jme.scene.state;

/**
 * <code>CullState</code> determins which side of a model will be visible when
 * it is rendered. By default, both sides are visible. Define front as the side
 * that traces its vertexes counter clockwise and back as the side that traces
 * its vertexes clockwise, a side (front or back) can be culled, or not shown
 * when the model is rendered. Instead, the side will be transparent.
 * <br>
 * Implementations of this class should take note of the flipped culling mode.
 * @see CullState#setFlippedCulling(boolean)
 * 
 * @author Mark Powell
 * @author Jack Lindamood (javadoc only)
 * @author Tijl Houtbeckers (added flipped culling mode)
 * @version $Id: CullState.java,v 1.8 2006-01-21 15:30:34 llama Exp $
 */
public abstract class CullState extends RenderState {

	/** No sides of the model's triangles are culled. This is default. */
	public static final int CS_NONE = 0;
	/** Cull the front sides. */
	public static final int CS_FRONT = 1;
	/** Cull the back sides. */
	public static final int CS_BACK = 2;

	/**
	 * Set this to enable flipped culling for all culling states.
	 * 
	 * @see CullState#setFlippedCulling(boolean)
	 */
	protected static boolean flippedCulling = false;

	/** The cull mode set for this CullState. */
	protected int cullMode;

	/**
	 * @return whether cull states are flipped when they are applied.
	 * @see CullState#setFlippedCulling(boolean)
	 */
	public static boolean isFlippedCulling() {
		return flippedCulling;
	}

	/**
	 * Use this to set whether all cull states should be flipped when they are
	 * applied. In other words if the cull mode is set to CS_FRONT, then CS_BACK
	 * will be used. For CS_BACK, CS_FRONT will be used. If the cull mode is set
	 * to CS_NONE this setting won't have any effect.
	 * 
	 * @param flippedCulling
	 *            true to flip all cull states when applied. false to use cull
	 *            states as normal.
	 */
	public static void setFlippedCulling(boolean flippedCulling) {
		CullState.flippedCulling = flippedCulling;
	}

	/**
	 * <code>getType</code> returns RenderState.RS_CULL
	 * 
	 * @return RenderState.RS_CULL
	 * @see com.jme.scene.state.RenderState#getType()
	 */
	public int getType() {
		return RS_CULL;
	}

	/**
	 * Sets the cull mode to the integer given. mode most be one of CS_FRONT,
	 * CS_BACK, or CS_NONE
	 * 
	 * @param mode
	 *            The new cull mode.
	 */
	public void setCullMode(int mode) {
		cullMode = mode;
	}

	/**
	 * Returns the current cull mode for this CullState.
	 * 
	 * @return The current cull mode.
	 */
	public int getCullMode() {
		return cullMode;
	}

}
