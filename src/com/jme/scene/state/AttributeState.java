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
package com.jme.scene.state;

/**
 * <code>AttributeState</code>
 * @author Mark Powell
 * @version $Id: AttributeState.java,v 1.2 2004-04-16 17:12:49 renanse Exp $
 */
public abstract class AttributeState extends RenderState {
	public static final int ALL_ATTRIB_BIT = 0;
	public static final int ACCUM_BUFFER_BIT = 1;
	public static final int COLOR_BUFFER_BIT = 2;
	public static final int CURRENT_BIT = 3;
	public static final int DEPTH_BUFFER_BIT = 4;
	public static final int ENABLE_BIT = 5;
	public static final int EVAL_BIT = 6;
	public static final int FOG_BIT = 7;
	public static final int HINT_BIT = 8;
	public static final int LIGHTING_BIT = 9;
	public static final int LINE_BIT = 10;
	public static final int LIST_BIT = 11;
	public static final int PIXEL_MODE_BIT = 12;
	public static final int POINT_BIT = 13;
	public static final int POLYGON_BIT = 14;
	public static final int POLYGON_STIPPLE_BIT = 15;
	public static final int SCISSOR_BIT = 16;
	public static final int STENCIL_BUFFER_BIT = 17;
	public static final int TEXTURE_BIT = 18;
	public static final int TRANSFORM_BIT = 19;
	public static final int VIEWPORT_BIT = 20;

	private int mask;
        protected static int level = 0;

	/**
	 * Constructor instantiates a new <code>ShadeState</code> object with the
	 * default mode being smooth.
	 *
	 */
	public AttributeState() {
		mask = ALL_ATTRIB_BIT;
	}

	/** <code>getType</code>
	 * @return
	 * @see com.jme.scene.state.RenderState#getType()
	 */
	public int getType() {
		return RS_ATTRIBUTE;
	}

	public void setMask(int mask) {
		this.mask = mask;
	}

	public int getMask() {
		return mask;
	}
}
