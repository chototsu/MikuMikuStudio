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
package com.jme.effects.transients;

import com.jme.renderer.TextureRenderer;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.system.DisplaySystem;

/**
 * <code>Transient</code>
 * 
 * @author Ahmed
 * @version $Id: Transient.java,v 1.1 2004-04-04 16:48:10 darkprophet Exp $ provides a base at which furthur effects can be made of
 */
public abstract class Transient extends Node {

	public Transient(
		String name,
		DisplaySystem game,
		Node root,
		Node from,
		Node to,
		TextureRenderer textureR) {
		super(name);
	}

	/**
	 * 
	 * <code>getCurrentStage</code> returns the current stage which the
	 * effect is at.
	 * 
	 * @return currentStage
	 */
	public abstract int getCurrentStage();

	/**
	 * 
	 * <code>setCurrentStage</code> sets the stage at which the effect is at.
	 * 
	 * @param stage
	 */
	public abstract void setCurrentStage(int stage);

	/**
	 * 
	 * <code>getNumOfStages</code> returns the maximum number of stages that
	 * the current effect has
	 * 
	 * @return numOfStages
	 */
	public abstract int getNumOfStages();

	/**
	 * 
	 * <code>setNumOfStages</code> sets the total number of stages the
	 * current effect has
	 * 
	 * @param num
	 */
	public abstract void setNumOfStages(int num);

	/**
	 * <code>getRootNode</code>
	 * 
	 * @return rootNode, where the effect will be attached to
	 */
	public abstract Node getRootNode();

	/**
	 * <code>setRootNode</code>
	 * 
	 * @param rootNode,
	 *            the root where the effect will be attached to
	 */
	public abstract void setRootNode(Node root);

	/**
	 * <code>getRemoveNode</code>
	 * 
	 * @return removeNode, the node that will be removed after effect has taken
	 *         place
	 */
	public abstract Node getRemoveNode();

	/**
	 * <code>setRemoveNode</code>
	 * 
	 * @param out,
	 *            the node that will be removed after effect has taken place
	 */
	public abstract void setRemoveNode(Node out);

	/**
	 * <code>getInsertNode</code>
	 * 
	 * @return InsertNode, this is the node that will be placed when the remove
	 *         node has been removed
	 */
	public abstract Node getInsertNode();

	/**
	 * <code>setInsertNode</code> sets the node that will be attached to the
	 * root after effect has taken place
	 */
	public abstract void setInsertNode(Node attach);

	/**
	 * <code>getEffectController</code> returns the controller that is
	 * controlling the animation's update.
	 * 
	 * @return the controller that defines the effect
	 */
	public abstract Controller getEffectController();

}
