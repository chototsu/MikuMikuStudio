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

package com.jme.scene;

import java.io.Serializable;

import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;

/**
 * <code>VBOInfo</code> provides a single class for dealing with the VBO
 * characteristics of a Geometry object(s)
 * 
 * @author Joshua Slack
 * @version $Id: VBOInfo.java,v 1.1 2005-09-15 17:13:35 renanse Exp $
 */
public class VBOInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean useVBOVertex = false;
	private boolean useVBOTexture = false;
	private boolean useVBOColor = false;
	private boolean useVBONormal = false;
	private int vboVertexID = -1;
	private int vboColorID = -1;
	private int vboNormalID = -1;
	private int[] vboTextureIDs = null;

	public VBOInfo() {
	    this(false);
	}
	
	public VBOInfo(boolean defaultVBO) {
	    useVBOColor = defaultVBO;
	    useVBOTexture = defaultVBO;
	    useVBOVertex = defaultVBO;
	    useVBOColor = defaultVBO;

		int textureUnits = TextureState.getNumberOfUnits();
		if (textureUnits == -1) {
		    DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		    textureUnits = TextureState.getNumberOfUnits();
		}
		vboTextureIDs = new int[textureUnits];
}
	
	public VBOInfo copy() {
	    VBOInfo copy = new VBOInfo();
	    copy.useVBOVertex = useVBOVertex;
	    copy.useVBOTexture = useVBOTexture;
	    copy.useVBOColor = useVBOColor;
	    copy.useVBONormal = useVBONormal;
	    return copy;
	}
	
	/**
	 * Returns true if VBO (Vertex Buffer) is enabled for vertex information.
	 * This is used during rendering.
	 *
	 * @return If VBO is enabled for vertexes.
	 */
	public boolean isVBOVertexEnabled() {
		return useVBOVertex;
	}

	/**
	 * Returns true if VBO (Vertex Buffer) is enabled for texture information.
	 * This is used during rendering.
	 *
	 * @return If VBO is enabled for textures.
	 */
	public boolean isVBOTextureEnabled() {
		return useVBOTexture;
	}

	/**
	 * Returns true if VBO (Vertex Buffer) is enabled for normal information.
	 * This is used during rendering.
	 *
	 * @return If VBO is enabled for normals.
	 */
	public boolean isVBONormalEnabled() {
		return useVBONormal;
	}

	/**
	 * Returns true if VBO (Vertex Buffer) is enabled for color information.
	 * This is used during rendering.
	 *
	 * @return If VBO is enabled for colors.
	 */
	public boolean isVBOColorEnabled() {
		return useVBOColor;
	}

	/**
	 * Enables or disables Vertex Buffer Objects for vertex information.
	 *
	 * @param enabled
	 *            If true, VBO enabled for vertexes.
	 */
	public void setVBOVertexEnabled(boolean enabled) {
		useVBOVertex = enabled;
	}

	/**
	 * Enables or disables Vertex Buffer Objects for texture coordinate
	 * information.
	 *
	 * @param enabled
	 *            If true, VBO enabled for texture coordinates.
	 */
	public void setVBOTextureEnabled(boolean enabled) {
		useVBOTexture = enabled;
	}

	/**
	 * Enables or disables Vertex Buffer Objects for normal information.
	 *
	 * @param enabled
	 *            If true, VBO enabled for normals
	 */
	public void setVBONormalEnabled(boolean enabled) {
		useVBONormal = enabled;
	}

	/**
	 * Enables or disables Vertex Buffer Objects for color information.
	 *
	 * @param enabled
	 *            If true, VBO enabled for colors
	 */
	public void setVBOColorEnabled(boolean enabled) {
		useVBOColor = enabled;
	}

	public int getVBOVertexID() {
		return vboVertexID;
	}

	public int getVBOTextureID(int index) {
		return vboTextureIDs[index];
	}

	public int getVBONormalID() {
		return vboNormalID;
	}

	public int getVBOColorID() {
		return vboColorID;
	}

	public void setVBOVertexID(int id) {
		vboVertexID = id;
	}

	public void setVBOTextureID(int index, int id) {
		vboTextureIDs[index] = id;
	}

	public void setVBONormalID(int id) {
		vboNormalID = id;
	}

	public void setVBOColorID(int id) {
		vboColorID = id;
	}

}
