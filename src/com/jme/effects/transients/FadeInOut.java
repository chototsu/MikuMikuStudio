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

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.system.DisplaySystem;

/**
 * <code>FadeInOut</code>
 * 
 * @author Ahmed
 * @version $Id: FadeInOut.java,v 1.1 2004-04-04 16:48:11 darkprophet Exp $
 */
public class FadeInOut extends Transient {

	// setup variables
	private int numberOfStages;
	private int currentStage;
	private float speed;
	private ColorRGBA tint;

	// setup the texture renderer stuff
	private TextureRenderer tRenderer;
	private Texture fakeTex;
	private Node root, remove, attach;

	// setup the scene
	private Node whereToPutStuff;
	private Quad quadToFadeInto;
	private ColorRGBA[] quadCornerColors;

	/**
	 * 
	 * Initial Constructor
	 */
	public FadeInOut(
		Node root,
		DisplaySystem game,
		Node from,
		Node to,
		TextureRenderer r) {
		super("FadeInOut Transient", game, root, from, to, r);
		initialise(game, root, from, to, r, new ColorRGBA(0, 0, 0, 0), 0.5f);
	}

	/**
	 * @param name,
	 *            the name of the transient
	 * @param color,
	 *            the color of the fade in out sequence
	 */
	public FadeInOut(
		Node root,
		DisplaySystem game,
		Node from,
		Node to,
		TextureRenderer r,
		ColorRGBA color) {
		super("FadeInOut Transient", game, root, from, to, r);
		initialise(game, root, from, to, r, color, 0.5f);
	}

	/**
	 * @param name,
	 *            the name of the spatial
	 * @param color,
	 *            the color of the fade in out sequence
	 * @param speed_arg,
	 *            the speed of the sequence
	 */
	public FadeInOut(
		DisplaySystem game,
		Node root,
		Node from,
		Node to,
		TextureRenderer r,
		ColorRGBA color,
		float speed_arg) {
		super("FadeInOut Transient", game, root, from, to, r);
		initialise(game, root, from, to, r, color, speed);
	}

	private void initialise(
		DisplaySystem game,
		Node root,
		Node from,
		Node to,
		TextureRenderer r,
		ColorRGBA color,
		float speed_arg) {
		setNumOfStages(2);
		setCurrentStage(0);
		setColor(color);
		setSpeed(speed_arg);

		tRenderer =
			game.createTextureRenderer(
				512,
				512,
				false,
				true,
				false,
				false,
				TextureRenderer.RENDER_TEXTURE_2D,
				0);
		tRenderer.setBackgroundColor(new ColorRGBA(0, 0, 0, 0));
		fakeTex = tRenderer.setupTexture();
		tRenderer.getCamera().setLocation(new Vector3f(0, 0, 75f));
		tRenderer.getCamera().update();

		AlphaState quadAS = game.getRenderer().getAlphaState();
		quadAS.setBlendEnabled(true);
		quadAS.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		quadAS.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
		quadAS.setTestEnabled(true);
		quadAS.setTestFunction(AlphaState.TF_GEQUAL);
		quadAS.setEnabled(true);

		quadCornerColors = new ColorRGBA[4];
		for (int i = 0; i < quadCornerColors.length; i++) {
			quadCornerColors[i] = (ColorRGBA) getColour().clone();
		}
		whereToPutStuff = new Node("FadeInOut Node");
		quadToFadeInto = new Quad("FadeInOut Quad");
		quadToFadeInto.initialize(30, 30);
		quadToFadeInto.setColors(quadCornerColors);
		quadToFadeInto.setRenderState(quadAS);
		whereToPutStuff.attachChild(quadToFadeInto);
	}

	public Quad getQuad() {
		return quadToFadeInto;
	}

	/**
	 * 
	 * <code>getColour</code> returns the color of which the texture will be
	 * rendered.
	 * 
	 * @return tint
	 */
	public ColorRGBA getColour() {
		return tint;
	}

	/**
	 * 
	 * <code>setColor</code> set the color of the texture
	 * 
	 * @param c
	 *            the color to set
	 */
	public void setColor(ColorRGBA c) {
		tint = c;
	}

	/**
	 * sets the speed of the effect <code>getSpeed</code>
	 * 
	 * @return speed, the speed of the effect
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * 
	 * <code>setSpeed</code> sets the speed of the effect
	 * 
	 * @param s
	 *            the speed to set
	 */
	public void setSpeed(float s) {
		speed = s;
	}

	/**
	 * <code>getCurrentStage</code>
	 * 
	 * @return @see com.jme.effects.transients.Transient#getCurrentStage()
	 */
	public int getCurrentStage() {
		return currentStage;
	}

	/**
	 * <code>setCurrentStage</code>
	 * 
	 * @param stage
	 * @see com.jme.effects.transients.Transient#setCurrentStage(int)
	 */
	public void setCurrentStage(int stage) {
		if (stage < getNumOfStages()) {
			currentStage = stage;
		} else {
			stage = getNumOfStages();
		}
	}

	/**
	 * <code>getNumOfStages</code>
	 * 
	 * @return @see com.jme.effects.transients.Transient#getNumOfStages()
	 */
	public int getNumOfStages() {
		return numberOfStages;
	}

	/**
	 * <code>setNumOfStages</code>
	 * 
	 * @param num
	 * @see com.jme.effects.transients.Transient#setNumOfStages(int)
	 */
	public void setNumOfStages(int num) {
		numberOfStages = num;
	}

	/**
	 * <code>getTextureRenderer</code>
	 * 
	 * @return TextureRenderer, the textureRenderer that the scene will be
	 *         renderered to
	 */
	public TextureRenderer getTextureRenderer() {
		return tRenderer;
	}

	/**
	 * <code>setTextureRenderer</code>
	 * 
	 * @param tex,
	 *            the textureRenderer that the scene will be renderered to
	 */
	public void setTextureRenderer(TextureRenderer tex) {
		tRenderer = tex;
	}

	/**
	 * <code>getEffectController</code>
	 * 
	 * @return @see com.jme.effects.transients.Transient#getEffectController()
	 */
	public Controller getEffectController() {
		return (Controller) geometricalControllers.get(0);
	}

	/** <code>getRootNode</code> 
	 * @return
	 * @see com.jme.effects.transients.Transient#getRootNode()
	 */
	public Node getRootNode() {
		return root;
	}

	/** <code>setRootNode</code> 
	 * @param root
	 * @see com.jme.effects.transients.Transient#setRootNode(com.jme.scene.Node)
	 */
	public void setRootNode(Node root) {
		this.root = root;
	}

	/** <code>getRemoveNode</code> 
	 * @return
	 * @see com.jme.effects.transients.Transient#getRemoveNode()
	 */
	public Node getRemoveNode() {
		return remove;
	}

	/** <code>setRemoveNode</code> 
	 * @param out
	 * @see com.jme.effects.transients.Transient#setRemoveNode(com.jme.scene.Node)
	 */
	public void setRemoveNode(Node out) {
		remove = out;
	}

	/** <code>getInsertNode</code> 
	 * @return
	 * @see com.jme.effects.transients.Transient#getInsertNode()
	 */
	public Node getInsertNode() {
		return attach;
	}

	/** <code>setInsertNode</code> 
	 * 
	 * @see com.jme.effects.transients.Transient#setInsertNode()
	 */
	public void setInsertNode(Node attach) {
		this.attach = attach;
	}
}
