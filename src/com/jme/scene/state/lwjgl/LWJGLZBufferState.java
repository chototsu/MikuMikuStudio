/*
 * Copyright (c) 2003-2005 jMonkeyEngine
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

package com.jme.scene.state.lwjgl;

import org.lwjgl.opengl.GL11;

import com.jme.scene.state.ZBufferState;

/**
 * <code>LWJGLZBufferState</code> subclasses ZBufferState to use the LWJGL API
 * to access OpenGL.
 * 
 * @author Mark Powell
 * @version $Id: LWJGLZBufferState.java,v 1.7 2005-09-15 17:12:55 renanse Exp $
 */
public class LWJGLZBufferState extends ZBufferState {
	private static final long serialVersionUID = 1L;

	//the open gl depth tests
	private static int[] glBufferCompare = { GL11.GL_NEVER, GL11.GL_LESS,
			GL11.GL_EQUAL, GL11.GL_LEQUAL, GL11.GL_GREATER, GL11.GL_NOTEQUAL,
			GL11.GL_GEQUAL, GL11.GL_ALWAYS };

	/**
	 * <code>set</code> turns on the specified depth test specified by the
	 * state.
	 * 
	 * @see com.jme.scene.state.RenderState#apply()
	 */
	public void apply() {
		if (isEnabled()) {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthFunc(glBufferCompare[function]);
		} else {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthFunc(GL11.GL_ALWAYS);
		}

		if (writable) {
			GL11.glDepthMask(true);
		} else {
			GL11.glDepthMask(false);
		}

	}
}