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

package com.jme.scene.model;

import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;

/**
 * <code>JointMesh</code> defines a mesh that defines the geometry of a
 * model (or piece of a model) as well as references to joint indices. These
 * joints are used to alter the orientation and location of the mesh vertices.
 * How these joints change the mesh is dependant on the <code>JointController</code>
 * that has been set to the parent <code>Model</code>. <code>JointMesh</code>
 * maintains a list of the original mesh vertices while the altered vertices
 * are used to render the mesh to the graphics context.
 * @author Mark Powell
 * @version $Id: JointMesh.java,v 1.2 2004-02-20 20:17:50 mojomonkey Exp $
 */
public class JointMesh extends TriMesh {
	//used to define which material and texture to set.
	private int materialsIndex;
	//a copy of the original unaltered vertices.
	private Vector3f[] originalVertices;
	//references into the joint indices.
	private int[] jointIndices;

	/**
	 * Constructor creates a default <code>JointMesh</code>. 
	 * @param name the name of the scene element. This is required for identification and
     * 		comparision purposes.
	 */
	public JointMesh(String name) {
		super(name);
	}

	/**
	 * Constructor creates a <code>JointMesh</code> with the provided
	 * parameters set as it's attributes.
	 * @param name the name of the scene element. This is required for identification and
     * 		comparision purposes.
	 * @param materialsIndex the index into the material used.
	 * @param originalVertices the original vertices that define the 
	 * 		mesh.
	 * @param jointIndices the indices of the joints that alter the mesh.
	 */
	public JointMesh(
		String name,
		int materialsIndex,
		Vector3f[] originalVertices,
		int[] jointIndices) {
		super(name);
		this.materialsIndex = materialsIndex;
		this.originalVertices = originalVertices;
		this.jointIndices = jointIndices;
	}

	/**
	 * <code>getJointIndices</code> returns the indices into the joints
	 * list that alter the vertices of this mesh.
	 * @return the indices into the joint array.
	 */
	public int[] getJointIndices() {
		return jointIndices;
	}

	/**
	 * <code>setJointIndices</code> sets the indices into the joints
	 * list that alter the vertices of this mesh.
	 * @param jointIndices the indices into the joint array.
	 */
	public void setJointIndices(int[] jointIndices) {
		this.jointIndices = jointIndices;
	}

	/**
	 * <code>getOriginalVertices</code> returns the original unaltered
	 * vertices that make up this mesh.
	 * @return the original unaltered vertices that make up this mesh.
	 */
	public Vector3f[] getOriginalVertices() {
		return originalVertices;
	}

	/**
	 * <code>setOriginalVertices</code> sets the original unaltered
	 * vertices that make up this mesh.
	 * @param originalVertices the original unaltered vertices that 
	 * make up this mesh.
	 */
	public void setOriginalVertices(Vector3f[] originalVertices) {
		this.originalVertices = originalVertices;
	}

	/**
	 * <code>getMaterialsIndex</code> returns the index that defines
	 * which material state and texture state to use with this mesh.
	 * @return the index defining the material and texture state of this
	 * 		mesh.
	 */
	public int getMaterialsIndex() {
		return materialsIndex;
	}

	/**
	 * <code>setMaterialsIndex</code> sets the index that defines
	 * which material state and texture state to use with this mesh.
	 * @param materialsIndex the index defining the material and texture 
	 * state of this mesh.
	 */
	public void setMaterialsIndex(int materialsIndex) {
		this.materialsIndex = materialsIndex;
	}

}