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

package com.jme.renderer.lwjgl;

import org.lwjgl.opengl.GL11;

/**
 * @author Joshua Slack
 * @version $Id: LWJGLMatrixManager.java,v 1.1 2006-01-09 19:41:09 renanse Exp $
 */
public class LWJGLMatrixManager {

	public static final int UNKNOWN = GL11.GL_NONE;
	public static final int MODELVIEW = GL11.GL_MODELVIEW;
	public static final int PROJECTION = GL11.GL_PROJECTION;
	public static final int TEXTURE = GL11.GL_TEXTURE;
	
	protected static int currentMatrix = UNKNOWN;
	
	public static void switchMatrix(int matrix) {
		if (currentMatrix != matrix) {
			GL11.glMatrixMode(matrix);
			currentMatrix = matrix;
		}
	}
	
	public static void pushMatrix() {
		GL11.glPushMatrix();
	}
	
	public static void setIdentity() {
		GL11.glLoadIdentity();
	}
	
	public static void popMatrix() {
		GL11.glPopMatrix();
	}
}
