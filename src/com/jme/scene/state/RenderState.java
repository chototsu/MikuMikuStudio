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
package com.jme.scene.state;

import java.util.Stack;
import java.io.Serializable;

import com.jme.scene.Spatial;

/**
 * <code>RenderState</code> is the base class for all states that affect the
 * rendering of a piece of geometry. They aren't created directly, but are
 * created for users from the renderer. The renderstate of a parent affects its
 * children and it is OK to assign to more than one Spatial the same render
 * state.
 * 
 * @author Mark Powell
 * @author Jack Lindamood (javadoc only)
 * @version $Id: RenderState.java,v 1.22 2005-01-03 19:00:16 renanse Exp $
 */
public abstract class RenderState implements Serializable {

	/** The value returned by getType() for AlphaState. */
	public final static int RS_ALPHA = 0;

	/** The value returend by getType() for DitherState. */
	public final static int RS_DITHER = 1;

	/** The value returned by getType() for FogState. */
	public final static int RS_FOG = 2;

	/** The value returned by getType() for LightState. */
	public final static int RS_LIGHT = 3;

	/** The value returend by getType() for MaterialState. */
	public final static int RS_MATERIAL = 4;

	/** The value returned by getType() for ShadeState. */
	public final static int RS_SHADE = 5;

	/** The value returned by getType() for TextureState. */
	public final static int RS_TEXTURE = 6;

	/** The value returned by getType() for WireframeState. */
	public final static int RS_WIREFRAME = 7;

	/** The value returned by getType() for ZBufferState. */
	public final static int RS_ZBUFFER = 8;

	/** The value returned by getType() for CullState. */
	public final static int RS_CULL = 9;

	/** The value returned by getType() for VertexProgramState. */
	public final static int RS_VERTEX_PROGRAM = 10;

	/** The value returned by getType() for FragmentProgramState. */
	public final static int RS_FRAGMENT_PROGRAM = 11;

	/** The value returned by getType() for AttributeState. */
	public final static int RS_ATTRIBUTE = 12;

	/** The value returned by getType() for StencilState. */
	public final static int RS_STENCIL = 13;
	
	/** The value returned by getType() for ShaderObjectsState. */
	public final static int RS_GLSL_SHADER_OBJECTS = 14;

	/** The total number of diffrent types of RenderState. */
	public final static int RS_MAX_STATE = 15; 

	private boolean enabled = true;

	/**
	 * Construts a new RenderState. The state is enabled by default.
	 */
	public RenderState() {
	}

	/**
	 * Defined by the subclass, this returns an int identifying the renderstate.
	 * For example, RS_CULL or RS_TEXTURE.
	 * 
	 * @return An int identifying this render state.
	 */
	public abstract int getType();

	/**
	 * Returns if this render state is enabled during rendering. Disabled states
	 * are ignored.
	 * 
	 * @return True if this state is enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets if this render state is enabled during rendering. Disabled states
	 * are ignored.
	 * 
	 * @param value
	 *            False if the state is to be disabled, true otherwise.
	 */
	public void setEnabled(boolean value) {
		this.enabled = value;
	}

	/**
	 * This function is defined in the RenderState that is actually used by the
	 * Renderer. It contains the code that, when executed, applies the render
	 * state for the given render system. This should only be called internally
	 * and not by users directly.
	 */
	public abstract void apply();

	/**
	 * Extracts from the stack the correct renderstate that should apply to the
	 * given spatial. This is mainly used for RenderStates that can be
	 * cumulitive such as TextureState or LightState. By default, the top of the
	 * static is returned. This function should not be called by users directly.
	 * 
	 * @param stack
	 *            The stack to extract render states from.
	 * @param spat
	 *            The spatial to apply the render states too.
	 * @return The render state to use.
	 */
	public RenderState extract(Stack stack, Spatial spat) {
		// The default behavior is to return the top of the stack, the last item
		// pushed during the recursive traveral.
		return (RenderState) stack.peek();
	}
}