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

import com.jme.scene.state.RenderState;

/**
 * SharedNode allows the sharing of data
 * @author Mark Powell
 *
 */
public class SharedNode extends Node {

private static final long serialVersionUID = 1L;
	private Node target;

	/**
	 * Constructor creates a new <code>SharedMesh</code> object.
	 * 
	 * @param name
	 *            the name of this shared mesh.
	 * @param target
	 *            the TriMesh to share the data.
	 */
	public SharedNode(String name, Node target) {
		super(name);
		setTarget(target);
	}

	/**
	 * <code>setTarget</code> sets the shared data mesh.
	 * 
	 * @param target
	 *            the TriMesh to share the data.
	 */
	private void setTarget(Node target) {
		this.target = target;
		processTarget(this, target);
		
	}
	
	private void processTarget(Node parent, Spatial target) {
		if((target.getType() & Spatial.NODE) != 0) {
			Node ntarget = (Node)target;
			Node node = new Node(this.getName()+ntarget.getName());
            node.setCullMode(ntarget.getCullMode());
			node.setLightCombineMode(ntarget.getLightCombineMode());
			node.setLocalRotation(ntarget.getLocalRotation());
			node.setLocalScale(ntarget.getLocalScale());
			node.setLocalTranslation(ntarget.getLocalTranslation());
			node.setRenderQueueMode(ntarget.getRenderQueueMode());
			node.setTextureCombineMode(ntarget.getTextureCombineMode());
			node.setZOrder(ntarget.getZOrder());
			
			RenderState[] states = ntarget.getRenderStateList();
			for (int i = 0; i < states.length; i++) {
				if (states[i] != null) {
					node.setRenderState(states[i]);
				}
			}
			
			parent.attachChild(node);
			
			for(int i = 0; i < ntarget.getChildren().size(); i++) {
				processTarget(node, ntarget.getChild(i));
			}
			
		} else if((target.getType() & Spatial.TRIMESH) != 0) {
			SharedMesh copy = new SharedMesh(this.getName()+target.getName(), (TriMesh)target);
			parent.attachChild(copy);
		}
	}
}
