/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding All rights reserved.
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
package com.jme.scene;

import com.jme.renderer.Renderer;

/**
 * <code>SwitchNode</code> defines a node that maintains a single active child at a time. This allows
 * the instantanious switching of children depending on any number of factors. For example, 
 * multiple levels of detail models can be loaded into the switch node and the active model can
 * be set depending on the distance from the camera. 
 * 
 * @author Mark Powell
 * @version $Id: SwitchNode.java,v 1.1 2004-03-03 23:02:49 mojomonkey Exp $
 */
public class SwitchNode extends Node {
	/**
	 * defines an inactive or invalid child.
	 */
	public static final int SN_INVALID_CHILD = -1;

	private int activeChild;
	private Spatial activeChildData;

	/**
	 * Constructor instantiates a new <code>SwitchNode</code> object. The name of the
	 * node is provided during construction.
	 * @param name the name of the node.
	 */
	public SwitchNode(String name) {
		super(name);
	}

	public int getActiveChild() {
		return activeChild;
	}

	public void setActiveChild(int child) {
		if(child < 0 || child > getQuantity()) {
			activeChild = SN_INVALID_CHILD;
		} else {
			this.activeChild = child;
			activeChildData = getChild(activeChild);
		}
	}

	public void disableAllChildren() {
		activeChild = SN_INVALID_CHILD;
	}

	public void draw(Renderer r) {
		if (activeChild != SN_INVALID_CHILD) {
			if (activeChildData != null) {
				activeChildData.onDraw(r);
			}
		}
	}
}
